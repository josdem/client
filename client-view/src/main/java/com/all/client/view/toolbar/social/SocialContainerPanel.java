package com.all.client.view.toolbar.social;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.apache.commons.lang.StringUtils;

import com.all.appControl.control.ViewEngine;
import com.all.chat.ChatType;
import com.all.client.view.MiddleCloseablePanel;
import com.all.client.view.View;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.client.view.feeds.FeedViewFactory;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.core.model.Profile;
import com.all.event.EventListener;
import com.all.event.ValueEvent;
import com.all.i18n.Messages;
import com.all.observ.Observable;
import com.all.observ.ObserveObject;
import com.all.observ.Observer;
import com.all.observ.ObserverCollection;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.User;

public final class SocialContainerPanel extends MiddleCloseablePanel implements View {

	private static final long serialVersionUID = 1L;

	private Messages messages;

	private MultiLayerDropTargetListener dropListener;

	private DialogFactory dialogFactory;

	private ProfilePanel profilePanel;

	private SocialCardPanel socialCardPanel;

	private ProfileDropListener profileDropListener;

	private ViewEngine viewEngine;

	private Observable<ObserveObject> onCloseEvent = new Observable<ObserveObject>();

	private EventListener<ValueEvent<Profile>> currentProfileListener;

	private EventListener<ValueEvent<ContactInfo>> contactUpdateListener;

	private EventListener<ValueEvent<User>> profileUpdatedListener;

	private final FeedViewFactory feedViewFactory;
	
	public SocialContainerPanel(SocialCardPanel socialCardPanel, ProfileDropListener profileDropListener,
			ViewEngine viewEngine, Messages messages, MultiLayerDropTargetListener multiLayerDropTargetListener,
			DialogFactory dialogFactory, FeedViewFactory feedViewFactory) {
		super();

		this.socialCardPanel = socialCardPanel;
		this.profileDropListener = profileDropListener;
		this.viewEngine = viewEngine;
		this.messages = messages;
		this.dropListener = multiLayerDropTargetListener;
		this.dialogFactory = dialogFactory;
		this.feedViewFactory = feedViewFactory;
		this.currentProfileListener = new CurrentProfileListener();
		this.contactUpdateListener = new ContactUpdatedListener();
		this.profileUpdatedListener = new ProfileUpdatedListener();
		initialize();
	}
	
	private void initialize() {
		getMiddlePanel().add(getMainPanel(), BorderLayout.CENTER);
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() {
  		        setCurrentProfile();
				return null;
			}

		}.execute();
		socialCardPanel.onClose().add(new Observer<ObserveObject>() {
			@Override
			public void observe(ObserveObject eventArgs) {
				onCloseEvent.fire(ObserveObject.EMPTY);
			}
		});
	}
	
	@Override
	protected JPanel getMainPanel() {
		return socialCardPanel;
	}

	private void setCurrentProfile() {
		if (profilePanel != null) {
			profilePanel.destroy(viewEngine);
		}
		profilePanel = new ProfilePanel(messages, dropListener, dialogFactory, profileDropListener, viewEngine, feedViewFactory);
		profilePanel.initialize(viewEngine);
		// TODO:Review this code it works perfectly bue if we are using a card panel
		// we have a defect here
		// profilePanel.setViewState(viewState);
		// this.removeAll();
		// this.add(profilePanel, BorderLayout.CENTER);
		// this.invalidate();
		// SwingUtilities.getWindowAncestor(this).validate();
		socialCardPanel.getProfileContainer().removeAll();
		socialCardPanel.getProfileContainer().add(profilePanel, BorderLayout.CENTER);
		Profile profile = viewEngine.get(Model.CURRENT_PROFILE);
		ContactInfo contact = profile.getContact();
		String text = messages.getMessage("profile.name", StringUtils.isNotEmpty(contact.getNickName()) ? contact
				.getNickName().toUpperCase() : contact.getNickName());
		this.getTitleLabel().setText(text);
		socialCardPanel.revalidate();
	}
	
	public ObserverCollection<ObserveObject> onClose() {
		return onCloseEvent;
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		super.initialize(viewEngine);
		viewEngine.addListener(Events.UserProfile.USER_PROFILE_UPDATED, profileUpdatedListener);
		viewEngine.addListener(Events.Social.CURRENT_PROFILE, currentProfileListener);
		viewEngine.addListener(Events.Social.CONTACT_UPDATED, contactUpdateListener);
		socialCardPanel.initialize(viewEngine);
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		super.destroy(viewEngine);
		viewEngine.removeListener(Events.UserProfile.USER_PROFILE_UPDATED, profileUpdatedListener);
		viewEngine.removeListener(Events.Social.CURRENT_PROFILE, currentProfileListener);
		viewEngine.removeListener(Events.Social.CONTACT_UPDATED, contactUpdateListener);
		socialCardPanel.destroy(viewEngine);
		profilePanel.destroy(viewEngine);
	}

	private final class ContactUpdatedListener extends EventListener<ValueEvent<ContactInfo>> {
		@Override
		public void handleEvent(ValueEvent<ContactInfo> event) {
			ContactInfo contact = event.getValue();
			if (contact.getChatType() == ChatType.ALL) {
				profilePanel.onContactUpdated(contact);
			}
		}
	}

	private final class CurrentProfileListener extends EventListener<ValueEvent<Profile>> {
		@Override
		public void handleEvent(ValueEvent<Profile> eventArgs) {
			setCurrentProfile();
		}
	}
	
	private final class ProfileUpdatedListener extends EventListener<ValueEvent<User>>{
		@Override
		public void handleEvent(ValueEvent<User> eventArgs) {
			if( viewEngine.get(Model.CURRENT_PROFILE).getContact().getEmail().equals(eventArgs.getValue().getEmail()) ){
				User user = eventArgs.getValue();
				String text = messages.getMessage("profile.name", StringUtils.isNotEmpty(user.getNickName()) ? user
								.getNickName().toUpperCase() : user.getNickName());
				getTitleLabel().setText(text);
			}
			
		}
	}
}
