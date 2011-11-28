package com.all.client.view.download;

import javax.swing.JLabel;

import com.all.client.model.Download;
import com.all.client.util.Formatters;
import com.all.client.view.components.FilteredRenderer;
import com.all.client.view.components.SimpleTableRenderer;
import com.all.downloader.bean.DownloadState;

public class AvailabilityRenderer extends FilteredRenderer<Download> {
	
	public AvailabilityRenderer() {
		super(new SimpleTableRenderer(JLabel.RIGHT));
	}

	@Override
	public Object filter(Download value, int row, int column) {
		if (value.getStatus() == DownloadState.Downloading) {			
			return Formatters.formatInteger(value.getFreeNodes()) + " / " + 
				Formatters.formatInteger(value.getBusyNodes());
		}
		return "";
	}

	
}
