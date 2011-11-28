package com.all.client.view.dialog;

import java.awt.CardLayout;
import java.awt.Frame;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.all.action.ResponseCallback;
import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.dnd.DraggedObject;
import com.all.client.view.dnd.MainFrameDragOverListener;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.core.actions.Actions;
import com.all.core.actions.SendContentAction;
import com.all.core.model.ContactCollection;
import com.all.core.model.ContainerView;
import com.all.core.model.Views;
import com.all.i18n.Messages;
import com.all.shared.model.ModelCollection;

public class SendContentDialog extends AllDialog {
	private static final String SUCCESS_PANEL = "SUCCESS_PANEL";
	private static final String SEND_CONTENT_PANEL = "SEND_CONTENT_PANEL";
	private static final String ERROR_PANEL = "ERROR_PANEL";
	private SendContentPanel sendContentPanel;
	private SendContentConfirmationPanel successPanel;
	private SendContentErrorPanel errorPanel;
	private JPanel mainPanel;
	private ViewEngine viewEngine;
	private final DialogFactory dialogFactory;

	public SendContentDialog(Frame frame, ModelCollection model, ContactCollection contacts, Messages messages,
			ViewEngine viewEngine, DialogFactory dialogFactory) {
		super(frame, messages);
		this.viewEngine = viewEngine;
		this.dialogFactory = dialogFactory;
		initialize(model, contacts, messages);
	}

	private void initialize(ModelCollection model, ContactCollection contacts, Messages messages) {
		successPanel = new SendContentConfirmationPanel(contacts);
		successPanel.setMessages(messages);
		successPanel.addActionListenerToOkButton(new CloseListener());

		errorPanel = new SendContentErrorPanel();
		errorPanel.setMessages(messages);
		errorPanel.getOkButton().addActionListener(new CloseListener());

		MultiLayerDropTargetListener dndListener = new MultiLayerDropTargetListener();
		this.setDropTarget(new DropTarget(this, dndListener));

		sendContentPanel = new SendContentPanel(messages, model, contacts, viewEngine);
		sendContentPanel.setup(this, dndListener);
		sendContentPanel.invalidate();
		sendContentPanel.updateDataUpload();
		sendContentPanel.getSendButton().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.sendValueAction(Actions.Twitter.POST, sendContentPanel.isTwitterSelected());
				sendContent();
			}
		});
		sendContentPanel.getCancelButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// we close the dialog through this method to throw events that will
				// managed the toolbar's button state
				closeDialog();
			}
		});
		dndListener.addDragListener(this, new MainFrameDragOverListener(this, messages) {

			@Override
			public void dragEnter(DraggedObject dragObject) {
				super.dragEnter(dragObject);
				sendContentPanel.setContainerPanelName("blueRoundedBorderPanel");
			}

			@Override
			public void dragExit(boolean dropped) {
				super.dragExit(dropped);
				sendContentPanel.setContainerPanelName("grayRoundedBorderPanel");
			}
		});

		this.setModal(false);
		this.setAlwaysOnTop(true);
		this.initializeContentPane();
	}

	private void sendContent() {
		final ModelCollection model = sendContentPanel.getModel();
		viewEngine.request(Actions.Social.REQUEST_UPLOADABLE_CONTENT, model, new ResponseCallback<ModelCollection>() {
			@Override
			public void onResponse(ModelCollection uploadableModel) {
				if (uploadableModel.isEmpty()) {
					closeDialog();
					dialogFactory.showReferencesOnlyErrorDialog();
					return;
				}
				if (uploadableModel.trackCount() < model.trackCount()) {
					if (!dialogFactory.showSomeReferencesErrorDialog(model.trackCount() - uploadableModel.trackCount())) {
						return;
					}
				}
				viewEngine.send(Actions.Social.SEND_CONTENT_ALERT, new SendContentAction(sendContentPanel.getModel(),
						sendContentPanel.getContacts(), sendContentPanel.getUserMessage()));
				closeDialog();
			}
		});
	}

	private static final long serialVersionUID = 1L;

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("sendContent.title");
	}

	@Override
	JComponent getContentComponent() {
		return getMainPanel();
	}

	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel(new CardLayout());
			mainPanel.add(sendContentPanel, SEND_CONTENT_PANEL);
			mainPanel.add(successPanel, SUCCESS_PANEL);
			mainPanel.add(errorPanel, ERROR_PANEL);
		}
		return mainPanel;
	}

	@Override
	void internationalizeDialog(Messages messages) {
		sendContentPanel.internationalize(messages);
		successPanel.internationalize(messages);
	}

	public void showSuccessPanel() {
		successPanel.updateContacts(sendContentPanel.getContacts());
		((CardLayout) mainPanel.getLayout()).show(mainPanel, SUCCESS_PANEL);
	}

	@Override
	protected void closeDialog() {
		super.closeDialog();
		viewEngine.send(Actions.View.setCurrentView, new ValueAction<ContainerView>(new ContainerView(Views.LOCAL_MUSIC)));
	}

}
