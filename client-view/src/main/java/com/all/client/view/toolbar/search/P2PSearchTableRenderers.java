package com.all.client.view.toolbar.search;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.springframework.beans.factory.annotation.Autowired;

import com.all.client.model.DecoratedSearchData;
import com.all.client.model.Download;
import com.all.client.util.Formatters;
import com.all.client.view.components.CellFilter;
import com.all.client.view.components.SimpleTableRenderer;
import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.SynthIcons;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public final class P2PSearchTableRenderers {

	private P2PSearchTableRenderers() {

	}

	public static TableCellRenderer indexRenderer(P2PSearchTableStyle style) {
		return new P2PSearchTableIndexRenderer();
	}

	public static TableCellRenderer nameRenderer(P2PSearchTableStyle style) {
		return new P2PSearchTableDefaultCellRenderer(style, JLabel.LEFT, new CellFilter<DecoratedSearchData>() {
			@Override
			public Object filter(DecoratedSearchData value, int row, int column) {
				return value.getName();
			}
		});
	}

	public static TableCellRenderer sizeRenderer(P2PSearchTableStyle style) {
		return new P2PSearchTableDefaultCellRenderer(style, JLabel.RIGHT, new CellFilter<DecoratedSearchData>() {
			@Override
			public Object filter(DecoratedSearchData value, int row, int column) {
				return Formatters.formatDataSize(value.getSize(), false);
			}
		});
	}

	public static TableCellRenderer typeRenderer(P2PSearchTableStyle style) {
		return new P2PSearchTableDefaultCellRenderer(style, JLabel.RIGHT, new CellFilter<DecoratedSearchData>() {
			@Override
			public Object filter(DecoratedSearchData value, int row, int column) {
				return value.getFileType();
			}
		});
	}

	public static TableCellRenderer peersRenderer(P2PSearchTableStyle style) {
		return new P2PSearchPeerCellRenderer(style, JLabel.CENTER, new CellFilter<DecoratedSearchData>() {
			@Override
			public Object filter(DecoratedSearchData value, int row, int column) {
				return value.getPeers();
			}
		});
	}
}

class P2PSearchTableIndexRenderer implements TableCellRenderer, Internationalizable {
	private JPanel panel = new JPanel();
	private JLabel iconLabel = new JLabel();
	private JLabel indexLabel = new JLabel();

	public P2PSearchTableIndexRenderer() {
		panel.setLayout(new BorderLayout());
		panel.setOpaque(false);

		Dimension iconSize = new Dimension(14, 20);
		iconLabel.setPreferredSize(iconSize);
		iconLabel.setSize(iconSize);
		iconLabel.setMinimumSize(iconSize);
		iconLabel.setMaximumSize(iconSize);
		iconLabel.setOpaque(false);
		iconLabel.setHorizontalAlignment(JLabel.CENTER);

		indexLabel.setHorizontalAlignment(JLabel.RIGHT);
		indexLabel.setVerticalAlignment(JLabel.CENTER);
		indexLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3));
		indexLabel.setName(SynthFonts.PLAIN_FONT11_GRAY77_77_77);
		indexLabel.setOpaque(false);
		indexLabel.setVisible(false);

		panel.add(iconLabel, BorderLayout.CENTER);
		panel.add(indexLabel, BorderLayout.EAST);
	}

	@Override
	public Component getTableCellRendererComponent(JTable jtable, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		P2PSearchTable table = (P2PSearchTable) jtable;
		DecoratedSearchData search = (DecoratedSearchData) value;
		indexLabel.setText("" + (search.getIndex() + 1));
		indexLabel.setName(isSelected ? SynthFonts.BOLD_FONT11_GRAY77_77_77 : SynthFonts.PLAIN_FONT11_GRAY77_77_77);
		Download download = table.getDownload(search.getFileHash());
		if (!table.isTrackAvailable(search)) {
			indexLabel.setName(isSelected ? SynthFonts.BOLD_FONT11_GRAY170_170_170 : SynthFonts.PLAIN_FONT11_GRAY170_170_170);
		}
		if (download != null) {
			switch (download.getStatus()) {
			case Downloading:
				iconLabel.setIcon(SynthIcons.DOWNLOAD_ICON);
				break;
			case Error:
				iconLabel.setIcon(SynthIcons.DOWNLOAD_ERROR_ICON);
				break;
			case Complete:
				iconLabel.setIcon(SynthIcons.SPEAKER_INVISIBLE_ICON);
				break;
			default:
				iconLabel.setIcon(SynthIcons.DOWNLOAD_QUEUE_ICON);
				break;
			}
		} else {
			iconLabel.setIcon(SynthIcons.SPEAKER_INVISIBLE_ICON);
		}

		return panel;
	}

	@Override
	public void internationalize(Messages messages) {
		iconLabel.setToolTipText(messages.getMessage("tooltip.downloadingST"));
		iconLabel.setToolTipText(messages.getMessage("tooltip.queueST"));
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

class P2PSearchTableDefaultCellRenderer extends SimpleTableRenderer {
	private final CellFilter<DecoratedSearchData> cellFilter;

	public P2PSearchTableDefaultCellRenderer(P2PSearchTableStyle style, int horizontalAlignment,
			CellFilter<DecoratedSearchData> cellFilter) {
		super(horizontalAlignment);
		this.cellFilter = cellFilter;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		DecoratedSearchData res = (DecoratedSearchData) value;
		return super.getTableCellRendererComponent(table, cellFilter.filter(res, row, column), isSelected, hasFocus, row,
				column);
	}
}

class P2PSearchPeerCellRenderer extends P2PSearchTableDefaultCellRenderer {

	public P2PSearchPeerCellRenderer(P2PSearchTableStyle style, int horizontalAlignment,
			CellFilter<DecoratedSearchData> cellFilter) {
		super(style, horizontalAlignment, cellFilter);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		DecoratedSearchData searchData = (DecoratedSearchData) value;
		JLabel peerLabel = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if(searchData.getSource() == DecoratedSearchData.Source.YOUTUBE){
			peerLabel.setIcon(SynthIcons.YOUTUBE_ACTIVE_ICON);
			peerLabel.setText(null);
		}
		return peerLabel;
	}
}