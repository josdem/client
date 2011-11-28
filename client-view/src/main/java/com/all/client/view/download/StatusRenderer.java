package com.all.client.view.download;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;

import com.all.client.model.Download;
import com.all.client.view.components.FilteredRenderer;
import com.all.client.view.components.SimpleTableRenderer;
import com.all.client.view.toolbar.downloads.DownloadTableStyle;
import com.all.core.common.view.SynthIcons;
import com.all.downloader.bean.DownloadState;
import com.all.i18n.Messages;

public class StatusRenderer extends FilteredRenderer<Download> {

	private static final int LABEL_STATUS_ICON_TEXT_GAP = 6;

	private static final Border LABEL_STATUS_BORDER = BorderFactory.createEmptyBorder(0, 5, 0, 0);
	
	private final DownloadTableStyle style;

	public StatusRenderer(DownloadTableStyle style) {
		super(new SimpleTableRenderer(JLabel.LEFT));
		this.style = style;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		JLabel labelStatus = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		labelStatus.setIconTextGap(LABEL_STATUS_ICON_TEXT_GAP);
    labelStatus.setBorder(LABEL_STATUS_BORDER);
		DownloadState status = ((Download) value).getStatus();
		switch (status) {
		case YoutubeTranscoding:
		case YoutubeDownloading:
		case YoutubeComplete:
			labelStatus.setIcon(SynthIcons.YOUTUBE_ACTIVE_ICON);
			break;
		case YoutubeWaiting:
		case YoutubePaused:
		case YoutubeError:
		case YoutubeFNF:
			labelStatus.setIcon(SynthIcons.YOUTUBE_INACTIVE_ICON);
			break;
		default:
			labelStatus.setIcon(null);
			break;
		}
		return labelStatus;
	}

	@Override
	public Object filter(Download download, int row, int column) {
		Messages messages = style.getMessages();
		DownloadState status = download.getStatus();
		return messages.getMessage(status.getKey());
	}
}
