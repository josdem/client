package com.all.client.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.geom.Ellipse2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.icon.EmptyIcon;
import org.jdesktop.swingx.painter.BusyPainter;
import org.springframework.beans.factory.annotation.Autowired;

import com.all.core.common.view.SynthFonts;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public abstract class ProgressBottomPanel extends JPanel implements Internationalizable {

	private static final long serialVersionUID = 1L;

	private static final int BUSY_LABEL_ICON_HEIGTH = 20;
	
	private static final int BUSY_LABEL_ICON_WIDTH = 20;

	private static final int FRAME = -1;

	private static final int ICON_TEXT_GAP = 7;

	private static final int POINTS = 8;

	private static final int TRAIL_LENGTH = 7;

	private static final Color BASE_COLOR = new Color(187, 189, 191);

	private static final Color HIGHLIGHT_COLOR = new Color(35, 31, 32);

	private static final Dimension BUSY_LABEL_DEFAULT_SIZE = new Dimension(20, 20);

	private static final Dimension SEPARATOR_PANEL_PREFERRED_SIZE = new Dimension(2, 28);

	private static final Dimension SPACER_DEFAULT_SIZE = new Dimension(7, 26);

	private static final String SEPARATOR_PANEL_NAME = "verticalSeparator";

	private JLabel label;

	private JXBusyLabel busyLabel;

	private final String icon;

	public ProgressBottomPanel(String icon) {
		this.icon = icon;
		JPanel separatorPanel = new JPanel();
		separatorPanel.setPreferredSize(SEPARATOR_PANEL_PREFERRED_SIZE);
		separatorPanel.setName(SEPARATOR_PANEL_NAME);

		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.add(BottomPanel.getSeparatorPanel());
		this.add(getSpacer());
		this.add(getLabel());
		this.add(getSpacer());
		this.add(getBusyLabel());
		this.add(getSpacer());
		this.add(separatorPanel);
		this.setVisible(false);
		this.setName("bottomProgressPanel");
	}

	protected final JPanel getSpacer() {
		JPanel spacer = new JPanel();
		spacer.setSize(SPACER_DEFAULT_SIZE);
		spacer.setMaximumSize(SPACER_DEFAULT_SIZE);
		spacer.setMinimumSize(SPACER_DEFAULT_SIZE);
		spacer.setPreferredSize(SPACER_DEFAULT_SIZE);
		return spacer;
	}

	protected final JLabel getLabel() {
		if (label == null) {
			label = new JLabel();
			label.setName(SynthFonts.BOLD_FONT12_GRAY100_100_100);
			label.setIconTextGap(ICON_TEXT_GAP);
			label.setIcon(UIManager.getIcon(icon));
		}
		return label;
	}

	protected final JXBusyLabel getBusyLabel() {
		if (busyLabel == null) {
			BusyPainter painter = new BusyPainter(new Ellipse2D.Float(0, 0, 4f, 4f), new Ellipse2D.Float(3f, 3f, 14f,
					14f));
			painter.setTrailLength(TRAIL_LENGTH);
			painter.setPoints(POINTS);
			painter.setFrame(FRAME);
			painter.setBaseColor(BASE_COLOR);
			painter.setHighlightColor(HIGHLIGHT_COLOR);
			busyLabel = new JXBusyLabel(BUSY_LABEL_DEFAULT_SIZE);
			busyLabel.setPreferredSize(BUSY_LABEL_DEFAULT_SIZE);
			busyLabel.setIcon(new EmptyIcon(BUSY_LABEL_ICON_WIDTH, BUSY_LABEL_ICON_HEIGTH));
			busyLabel.setBusyPainter(painter);
			busyLabel.setBusy(true);
			busyLabel.setVisible(true);
		}
		return busyLabel;
	}

	

	@Override
	public void internationalize(Messages messages) {
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Autowired
	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}
}
