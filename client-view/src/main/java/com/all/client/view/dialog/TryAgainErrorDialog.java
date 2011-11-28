package com.all.client.view.dialog;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.all.i18n.Messages;

public class TryAgainErrorDialog extends AbstractErrorDialog {
	private static final int VGAP = 7;

	private static final int HGAP = 10;

	private static final Dimension DEFAULT_BUTTON_SIZE = new Dimension(80, 22);

	private static final long serialVersionUID = 1L;
	
	private Response result = Response.CANCEL;

	private JPanel buttonPanel;
	
	public enum Response {
		CANCEL, TRY_AGAIN 
	}
	
	public Response getResult() {
		return result;
	}
	

	public TryAgainErrorDialog(Frame frame, Messages messages, String errorKey) {
		super(frame, messages, errorKey);
		setAlwaysOnTop(true);
	}
	
	@Override
	protected JPanel getButtonPanel() {
		if(buttonPanel==null){
			buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, HGAP, VGAP));
			JButton cancelButton = new JButton();
			cancelButton.setName("buttonCancel");
			cancelButton.setText(getMessages().getMessage("cancel"));
			cancelButton.setPreferredSize(DEFAULT_BUTTON_SIZE);
			cancelButton.addActionListener(new CloseListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					result = Response.CANCEL;
					super.actionPerformed(e);
				}
			});
			JButton tryAgainButton = new JButton();
			tryAgainButton.setName("buttonTry");
			tryAgainButton.setText(getMessages().getMessage("tryAgain"));
			tryAgainButton.setPreferredSize(DEFAULT_BUTTON_SIZE);
			tryAgainButton.addActionListener(new CloseListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					result = Response.TRY_AGAIN;
					super.actionPerformed(e);
				}
			});
			
			buttonPanel.add(cancelButton);
			buttonPanel.add(tryAgainButton);
		}
		return buttonPanel;
	}

}
