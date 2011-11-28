package com.all.client.view.toolbar.social;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JLabel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class LinkLabel extends JLabel {

	private static final long serialVersionUID = -954670097219214430L;
	private static final Log LOG = LogFactory.getLog(LinkLabel.class);
	private static final LinkLabelMouseListener LINK_LISTENER = new LinkLabelMouseListener();
	private URI link;

	public LinkLabel(final String text, final String uri, String style) {
		setLink(uri);
		setText(text);
		setName(style);
		if (StringUtils.isNotEmpty(text)) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		addMouseListener(LINK_LISTENER);
	}

	public void setLink(String link) {
		try {
			if (StringUtils.isNotEmpty(link)) {
				this.link = new URI(link);
				this.setToolTipText(link.toString());
			}
		} catch (URISyntaxException e) {
			LOG.error("Error creating URI from link: " + link, e);
		}
	}

	private static final class LinkLabelMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent event) {
			LinkLabel label = (LinkLabel) event.getSource();
			try {
				if (label.link != null) {
					Desktop.getDesktop().browse(label.link);
				}
			} catch (Exception e) {
				LOG.error(e, e);
			}
		}
	}

}
