package com.all.client.view.feeds;

import java.util.HashMap;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.flows.ShowContactFeedInfoFlow;
import com.all.core.common.view.SynthFonts;
import com.all.shared.model.ContactInfo;

public class FeedHeaderPanel extends CommonFeedPanel {

	private static final long serialVersionUID = -2981365525073115718L;
	private final DialogFactory dialogFactory;

	private Log LOG = LogFactory.getLog(this.getClass());
	private HashMap<String, ContactLabel> contactLabels;

	public FeedHeaderPanel(ViewEngine viewEngine, DialogFactory dialogFactory) {
		super(viewEngine);
		this.dialogFactory = dialogFactory;
		this.setLayout(new MigLayout("nogrid, insets 4 0 4 0", "", "[]2[]"));
	}

	public void appendContactInfo(final ContactInfo contactInfo) {
		appendContactInfo(contactInfo, "");
	}

	public void appendContactInfo(final ContactInfo contactInfo, String aditionalText) {

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					new ShowContactFeedInfoFlow(viewEngine, dialogFactory).execute(contactInfo, FeedHeaderPanel.this);
				} catch (Exception e) {
					LOG.error(e, e);
				}
			}
		};

		FeedRunnableLabel feedRunnableLabel = new FeedRunnableLabel(contactInfo.getNickName() + aditionalText, SynthFonts.BOLD_FONT12_PURPLE8F_5B_B1,
				runnable, null);
		getContactLabels().put(contactInfo.getEmail(), new ContactLabel(feedRunnableLabel, aditionalText, contactInfo));
		this.add(feedRunnableLabel, getContraints());
	}
	
	public HashMap<String, ContactLabel> getContactLabels() {
		if(contactLabels == null){
			contactLabels = new HashMap<String, ContactLabel>();
		}
		return contactLabels;
	}
	
	class ContactLabel{
		private final String aditionalText;
		private final FeedRunnableLabel feedRunnableLabel;
		private final ContactInfo contactInfo;
		public ContactLabel(FeedRunnableLabel feedRunnableLabel, String aditionalText, ContactInfo contactInfo) {
			this.feedRunnableLabel = feedRunnableLabel;
			this.aditionalText = aditionalText;
			this.contactInfo = contactInfo;
		}
		
		public String getAditionalText() {
			return aditionalText;
		}
		
		public FeedRunnableLabel getFeedRunnableLabel() {
			return feedRunnableLabel;
		}
		
		public ContactInfo getContactInfo() {
			return contactInfo;
		}
	}

}
