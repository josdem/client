package com.all.client.view.components;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.LocalLibraryPanel;
import com.all.client.view.dnd.DraggedObject;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.client.view.dnd.MyMusicDropListener;
import com.all.core.actions.FileSystemValidatorLight;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.ModelCollection;

@Component
public class MyMusicDnDPanel extends JPanel implements Internationalizable {

	private static final Dimension DROP_LABEL_SIZE = new Dimension(204, 23);
	private static final Dimension DROP_LABEL_SMALL_SIZE = new Dimension(204, 7);
	private static final Dimension ARROW_ICON_SIZE = new Dimension(96, 96);
	private static final Rectangle MY_MUSIC_DND_LABEL_BOUNDS = new Rectangle(0, 0, 184, 22);
	private static final Dimension MY_MUSIC_DND_LABEL_SIZE = new Dimension(204, 22);
	private static final Point LOCATION = new Point(0, 131);
	private static final Dimension SIZE = new Dimension(204, 184);
	private static final String MY_MUSIC_DND_PANEL = "myMusicDnDPanel";
	private static final long serialVersionUID = 1L;
	private JLabel myMusicDndLabel;
	private JLabel dropLabel;
	private JLabel dropSmallLabel;
	private JLabel arrowPanel;
	private LocalLibraryPanel localLibraryPanel;
	private MyMusicDropListener myMusicDropListener;

	@Autowired
	private ViewEngine viewEngine;

	public MyMusicDnDPanel() {
		init();
	}

	@Autowired
	public void setDragAndDrop(MultiLayerDropTargetListener multiLayerDropTargetListener,
			final LocalLibraryPanel localLibraryPanel) {

		this.localLibraryPanel = localLibraryPanel;

		myMusicDropListener = new MyMusicDropListener(viewEngine, getArrowPanel()) {
			@Override
			public void doDrop(FileSystemValidatorLight validator, Point location) {
				MyMusicDnDPanel.this.setVisible(false);
				super.doDrop(validator, location);
			}

			@Override
			public void doDrop(ModelCollection model, Point location) {
				MyMusicDnDPanel.this.setVisible(false);
				super.doDrop(model, location);
			}
		};

		multiLayerDropTargetListener.addDragListener(this, myMusicDropListener);
		multiLayerDropTargetListener.addDropListener(this, myMusicDropListener);
	}

	private void init() {
		this.setLayout(new GridBagLayout());
		this.setName(MY_MUSIC_DND_PANEL);
		this.setSize(SIZE);
		this.setPreferredSize(SIZE);
		this.setMinimumSize(SIZE);
		this.setMaximumSize(SIZE);
		this.setLocation(LOCATION);

		GridBagConstraints myMusicDnDLabelGridBagConstraints = new GridBagConstraints();
		myMusicDnDLabelGridBagConstraints.gridx = 0;
		myMusicDnDLabelGridBagConstraints.gridy = 0;
		myMusicDnDLabelGridBagConstraints.fill = GridBagConstraints.NONE;

		GridBagConstraints dropLabelConstraints = new GridBagConstraints();
		dropLabelConstraints.gridx = 0;
		dropLabelConstraints.gridy = 1;
		dropLabelConstraints.fill = GridBagConstraints.NONE;

		GridBagConstraints dropSmallLabelConstraints = new GridBagConstraints();
		dropSmallLabelConstraints.gridx = 0;
		dropSmallLabelConstraints.gridy = 2;
		dropSmallLabelConstraints.fill = GridBagConstraints.NONE;

		GridBagConstraints arrowConstraints = new GridBagConstraints();
		arrowConstraints.gridx = 0;
		arrowConstraints.gridy = 3;
		arrowConstraints.fill = GridBagConstraints.NONE;

		this.add(getMyMusicDndLabel(), myMusicDnDLabelGridBagConstraints);
		this.add(getDropLabel(), dropLabelConstraints);
		this.add(getDropSmallLabel(), dropSmallLabelConstraints);
		this.add(getArrowPanel(), arrowConstraints);
	}

	private JLabel getMyMusicDndLabel() {
		if (myMusicDndLabel == null) {
			myMusicDndLabel = new JLabel();
			myMusicDndLabel.setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
			myMusicDndLabel.setHorizontalAlignment(JLabel.CENTER);
			myMusicDndLabel.setVerticalAlignment(JLabel.TOP);
			myMusicDndLabel.setBounds(MY_MUSIC_DND_LABEL_BOUNDS);
			myMusicDndLabel.setSize(MY_MUSIC_DND_LABEL_SIZE);
			myMusicDndLabel.setPreferredSize(MY_MUSIC_DND_LABEL_SIZE);
			myMusicDndLabel.setMinimumSize(MY_MUSIC_DND_LABEL_SIZE);
			myMusicDndLabel.setMaximumSize(MY_MUSIC_DND_LABEL_SIZE);
		}
		return myMusicDndLabel;
	}

	private JLabel getDropLabel() {
		if (dropLabel == null) {
			dropLabel = new JLabel();
			dropLabel.setLocation(new Point(0, 23));
			dropLabel.setName(SynthFonts.BOLD_FONT23_PURPLE8130_60_165);
			dropLabel.setHorizontalAlignment(JLabel.CENTER);
			dropLabel.setSize(DROP_LABEL_SIZE);
			dropLabel.setPreferredSize(DROP_LABEL_SIZE);
			dropLabel.setMinimumSize(DROP_LABEL_SIZE);
			dropLabel.setMaximumSize(DROP_LABEL_SIZE);
		}
		return dropLabel;
	}

	private JLabel getDropSmallLabel() {
		if (dropSmallLabel == null) {
			dropSmallLabel = new JLabel();
			dropSmallLabel.setLocation(new Point(0, 45));
			dropSmallLabel.setName(SynthFonts.PLAIN_FONT12_PURPLE8130_60_165);
			dropSmallLabel.setHorizontalAlignment(JLabel.CENTER);
			dropSmallLabel.setSize(DROP_LABEL_SMALL_SIZE);
			dropSmallLabel.setPreferredSize(DROP_LABEL_SIZE);
			dropSmallLabel.setMinimumSize(DROP_LABEL_SIZE);
			dropSmallLabel.setMaximumSize(DROP_LABEL_SIZE);
		}
		return dropSmallLabel;
	}

	private JLabel getArrowPanel() {
		if (arrowPanel == null) {
			arrowPanel = new JLabel();
			arrowPanel.setIcon(UIManager.getDefaults().getIcon("icons.myMusicDndArrow"));
			arrowPanel.setBounds(44, 80, 96, 96);
			arrowPanel.setSize(ARROW_ICON_SIZE);
			arrowPanel.setPreferredSize(ARROW_ICON_SIZE);
			arrowPanel.setMinimumSize(ARROW_ICON_SIZE);
			arrowPanel.setMaximumSize(ARROW_ICON_SIZE);
			arrowPanel.setIconTextGap(0);
			arrowPanel.setAlignmentX(CENTER_ALIGNMENT);
			arrowPanel.setAlignmentY(CENTER_ALIGNMENT);
		}
		return arrowPanel;
	}

	@Override
	public void internationalize(Messages messages) {
		getMyMusicDndLabel().setText(messages.getMessage("previewPanel.dropPanel.mymusic.label"));
		getDropLabel().setText(messages.getMessage("previewPanel.dropPanel.mymusic.droplabel"));
		getDropSmallLabel().setText(messages.getMessage("previewPanel.dropPanel.mymusic.DropSmall"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	@Autowired
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	@Override
	public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);
		localLibraryPanel.setActiveMusicPanel(aFlag);
	}

	public boolean isDropable(DraggedObject dragObject) {
		return myMusicDropListener.isDropable(dragObject);
	}

}
