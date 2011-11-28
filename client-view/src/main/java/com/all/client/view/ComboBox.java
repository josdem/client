package com.all.client.view;

import java.awt.Dimension;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.core.actions.Actions;
import com.all.core.actions.LoadContactLibraryAction;
import com.all.core.common.view.SynthFonts;
import com.all.shared.model.Root;

public class ComboBox extends JPanel {
	private static final int LIBRARY_LABEL_X_INSET = 4;

	private static final int LIBRARY_LABEL_HEIGHT_INSET = 2;

	private static final int LIBRARY_LABEL_WIDTH_INSET = 20;

	private static final int LIBRARY_LABEL_Y_COORDINATE = 0;

	private static final long serialVersionUID = 1L;

	private static final int MENU_HEIGHT = 20;

	private static final int MENU_POSITION_X = 0;

	private static final int MENU_POSITION_Y = 16;

	private static final int MENU_WIDTH = 150;

	private static final Dimension MENU_MAXIMUM_SIZE = new Dimension(150, Integer.MAX_VALUE);

	private static final Dimension MENU_MINIMUM_SIZE = new Dimension(150, 0);

	private static final String DROP_DOWN_PANEL_NAME = "libraryDropDown";

	private static final String MENU_NO_RECENT_LIBRARIES = "No recent libraries.";

	private JLabel libraryLabel = null;

	private final ViewEngine viewEngine;
	
	private boolean isShowingMenu = false;

	public ComboBox(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;
		initialize();
	}

	private void initialize() {
		this.setLayout(null);
		this.setName(DROP_DOWN_PANEL_NAME);
		this.add(getLibraryLabel());
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(isShowingMenu){
					isShowingMenu = false;
				}
				else{
					showMenu();
				}
			}
		});
	}

	private JLabel getLibraryLabel() {
		if (libraryLabel == null) {
			libraryLabel = new JLabel();
			libraryLabel.setOpaque(false);
			libraryLabel.setVerticalAlignment(Label.CENTER);
			libraryLabel.setName(SynthFonts.BOLD_FONT12_GRAY64_64_64);
		}
		return libraryLabel;
	}

	@Override
	public void setBounds(Rectangle r) {
		super.setBounds(r);
		libraryLabel.setBounds(calculateBounds());
	}

	private Rectangle calculateBounds() {
		int x = this.getX() + LIBRARY_LABEL_X_INSET;
		int width = this.getWidth() - LIBRARY_LABEL_WIDTH_INSET;
		int height = this.getHeight() - LIBRARY_LABEL_HEIGHT_INSET;
		return new Rectangle(x, LIBRARY_LABEL_Y_COORDINATE, width, height);
	}

	private void showMenu() {
		viewEngine.request(Actions.Library.GET_LAST_LIBRARIES, new ResponseCallback<List<Root>>() {
			@Override
			public void onResponse(List<Root> recentLibraries) {
				JPopupMenu menu = new JPopupMenu();
				int i = 0;
				for (Root library : recentLibraries) {
					JMenuItem menuItem = new JMenuItem(library.getName());
					menuItem.addActionListener(new MenuItemListener(library));
					menu.add(menuItem);
					i++;
				}
				if (i == 0) {
					menu.add(MENU_NO_RECENT_LIBRARIES);
					i = 1;
				}
				menu.setMinimumSize(MENU_MINIMUM_SIZE);
				menu.setMaximumSize(MENU_MAXIMUM_SIZE);
				menu.setPreferredSize(new Dimension(MENU_WIDTH, i * MENU_HEIGHT));
				menu.setSize(new Dimension(MENU_WIDTH, i * MENU_HEIGHT));
				menu.show(ComboBox.this, MENU_POSITION_X, MENU_POSITION_Y);
				isShowingMenu = true;
			}
		});
	}

	private final class MenuItemListener implements ActionListener {
		private final Root library;

		public MenuItemListener(Root library) {
			this.library = library;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			viewEngine.send(Actions.Library.LOAD_CONTACT_LIBRARY, LoadContactLibraryAction.load(library.getOwnerMail()));
		}
	}

	public void setText(String text) {
		getLibraryLabel().setText(text);
	}

}
