package com.all.client.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.core.actions.Actions;
import com.all.core.common.view.SynthFonts;
import com.all.core.model.ContainerView;
import com.all.core.model.Views;
import com.all.observ.Observable;
import com.all.observ.ObserveObject;
import com.all.observ.ObserverCollection;

public abstract class MiddleCloseablePanel extends JPanel implements View {

	private static final long serialVersionUID = 1L;

	private static final Insets TOP_PANEL_INSETS = new Insets(0, 0, 2, 0);

	private static final String TOP_PANEL_NAME = "toolbarPanel";

	private static final String CLOSE_BUTTON_NAME = "closeLibraryButton";

	private static final Dimension CLOSE_BUTTON_DEFAULT_SIZE = new Dimension(14, 14);

	private static final Insets CLOSE_BUTTON_INSETS = new Insets(0, 0, 0, 10);

	private static final Insets TITLE_LABEL_INSETS = new Insets(3, 10, 5, 0);

	private static final Dimension TOP_PANEL_DEFAULT_SIZE = new Dimension(100, 24);

	private JButton closeButton;

	private JLabel titleLabel;

	private JPanel topPanel;

	private JPanel middlePanel;

	private Observable<ObserveObject> onCloseEvent = new Observable<ObserveObject>();

	private ViewEngine viewEngine;

	public MiddleCloseablePanel() {
		initialize();
	}

	protected abstract JPanel getMainPanel();

	private void initialize() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints topPanelConstraints = new GridBagConstraints();
		topPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		topPanelConstraints.weightx = 1;
		topPanelConstraints.insets = TOP_PANEL_INSETS;
		GridBagConstraints midlePanelConstraints = new GridBagConstraints();
		midlePanelConstraints.fill = GridBagConstraints.BOTH;
		midlePanelConstraints.gridx = 0;
		midlePanelConstraints.gridy = 1;
		midlePanelConstraints.weightx = 1;
		midlePanelConstraints.weighty = 1;
		this.add(getTopPanel(), topPanelConstraints);
		this.add(getMiddlePanel(), midlePanelConstraints);
	}

	public JPanel getTopPanel() {
		if (topPanel == null) {
			topPanel = new JPanel();
			topPanel.setName(TOP_PANEL_NAME);
			topPanel.setLayout(new GridBagLayout());
			topPanel.setPreferredSize(TOP_PANEL_DEFAULT_SIZE);
			topPanel.setSize(TOP_PANEL_DEFAULT_SIZE);
			topPanel.setMinimumSize(TOP_PANEL_DEFAULT_SIZE);
			topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
			GridBagConstraints closeConstraints = new GridBagConstraints();
			closeConstraints.gridx = 2;
			closeConstraints.gridy = 0;
			closeConstraints.insets = CLOSE_BUTTON_INSETS;
			GridBagConstraints titleConstraints = new GridBagConstraints();
			titleConstraints.gridx = 0;
			titleConstraints.gridy = 0;
			titleConstraints.weightx = 1;
			titleConstraints.weighty = 1;
			titleConstraints.fill = GridBagConstraints.BOTH;
			titleConstraints.insets = TITLE_LABEL_INSETS;
			topPanel.add(getCloseButton(), closeConstraints);
			topPanel.add(getTitleLabel(), titleConstraints);
		}
		return topPanel;
	}

	protected JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setName(CLOSE_BUTTON_NAME);
			closeButton.setSize(CLOSE_BUTTON_DEFAULT_SIZE);
			closeButton.setPreferredSize(CLOSE_BUTTON_DEFAULT_SIZE);
			closeButton.setMaximumSize(CLOSE_BUTTON_DEFAULT_SIZE);
			closeButton.setMinimumSize(CLOSE_BUTTON_DEFAULT_SIZE);
		}
		return closeButton;
	}

	protected JLabel getTitleLabel() {
		if (titleLabel == null) {
			titleLabel = new JLabel();
			titleLabel.setName(SynthFonts.BOLD_FONT12_GRAY64_64_64);
		}
		return titleLabel;
	}

	public JPanel getMiddlePanel() {
		if (middlePanel == null) {
			middlePanel = new JPanel();
			middlePanel.setLayout(new BorderLayout());
		}
		return middlePanel;
	}

	public ObserverCollection<ObserveObject> onClose() {
		return onCloseEvent;
	}

	@Override
	public void initialize(final ViewEngine viewEngine) {
		this.setViewEngine(viewEngine);
		getCloseButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.View.setCurrentView, new ValueAction<ContainerView>(new ContainerView(Views.LOCAL_MUSIC)));
				onCloseEvent.fire(ObserveObject.EMPTY);
			}
		});
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
	}

	public void setViewEngine(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;
	}

	public ViewEngine getViewEngine() {
		return viewEngine;
	}

}
