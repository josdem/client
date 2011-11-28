package com.all.client.services;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.action.ValueAction;
import com.all.appControl.ActionMethod;
import com.all.appControl.control.ControlEngine;
import com.all.client.services.reporting.ClientReporter;
import com.all.core.actions.Actions;
import com.all.core.actions.ComposeView;
import com.all.core.common.model.ApplicationModel;
import com.all.core.events.Events;
import com.all.core.events.NetworkActionErrorEvent;
import com.all.core.events.NetworkActions;
import com.all.core.model.ContainerView;
import com.all.core.model.Model;
import com.all.core.model.ModelContainerView;
import com.all.core.model.SubViews;
import com.all.core.model.Views;
import com.all.event.ValueEvent;
import com.all.messengine.MessageMethod;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.Playlist;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;
import com.all.shared.stats.usage.UserActions;

@Service
public class ViewService {
	@Autowired
	private ClientReporter reporter;

	@Autowired
	private ControlEngine controlEngine;
	
	@PostConstruct
	public void initialize() {
		controlEngine.set(Model.CURRENT_VIEW, Views.HOME, null);
		controlEngine.set(Model.DRAWER_DISPLAYED, false, Events.View.DRAWER_DISPLAYED_CHANGED);
	}
	
	@MessageMethod(MessEngineConstants.USER_SESSION_STARTED_TYPE)
	public void initView(){
		 controlEngine.fireEvent(Events.View.CURRENT_VIEW_CHANGED, new ValueEvent<ContainerView>( new
		 ContainerView(Views.HOME)));
		
	}

	@ActionMethod(Actions.View.SET_CURRENT_COMPOSE_VIEW_ID)
	public void changeCurrentComposeView(ValueAction<ComposeView> action) {
		Views view = action.getValue().getView();
		changeCurrentView(new ValueAction<ContainerView>(new ContainerView(view)));
		if (!action.getValue().getSubView().equals(SubViews.NONE)) {
			changeCurrentSubView(new ValueAction<SubViews>(action.getValue().getSubView()));
		}
	}

	@ActionMethod(Model.CURRENT_SUBVIEW_ID)
	public void changeCurrentSubView(ValueAction<SubViews> action) {
		controlEngine.set(Model.CURRENT_SUBVIEW, action.getValue(), null);
		controlEngine.fireEvent(Events.View.CURRENT_SUBVIEW_CHANGED, new ValueEvent<SubViews>(action.getValue()));
	}

	@ActionMethod(Actions.View.SELECT_CATEGORY_ID)
	public void selectTopHundredCategory(String categoryId) {
		controlEngine.set(Model.CURRENT_VIEW, Views.HUNDRED, null);

	}

	@ActionMethod(Model.CURRENT_VIEW_ID)
	public void changeCurrentView(ValueAction<ContainerView> containerView) {
		if (containerView == null || containerView.getValue()==null || containerView.getValue().getViews() == null) {
			return;
		}
		Views views = containerView.getValue().getViews();
		if (views == Views.SEND_MUSIC) {
			controlEngine.fireEvent(Events.View.CURRENT_VIEW_CHANGED, new ValueEvent<ContainerView>(new ContainerView(
					Views.LOCAL_MUSIC)));
		}
		controlEngine.set(Model.CURRENT_VIEW, views, null);
		controlEngine.fireEvent(Events.View.CURRENT_VIEW_CHANGED, new ValueEvent<ContainerView>(
				containerView.getValue()));
		if (views != null) {
			switch (views) {
			case HOME:
				reporter.logUserAction(UserActions.Toolbar.ACCESS_HOME);
				break;
			case SEARCH:
				reporter.logUserAction(UserActions.Downloads.BROWSE_SEARCH_TABLE);
				break;
			case DOWNLOAD:
				reporter.logUserAction(UserActions.Downloads.BROWSE_DOWNLOAD_TABLE);
				break;
			case PROFILE:
				reporter.logUserAction(UserActions.Toolbar.ACCESS_PROFILE);
				break;
			case HUNDRED:
				reporter.logUserAction(UserActions.Toolbar.ACCESS_HUNDRED);
				break;
			case CRAWLER:
				reporter.logUserAction(UserActions.Crawler.ACCESS_CRAWLER);
				break;
			case BROWSE_MEMBERS:
				reporter.logUserAction(UserActions.AllNetwork.BROWSE_ONLINE_USERS);
				break;
			case SEND_MUSIC:
				reporter.logUserAction(UserActions.Toolbar.ACCESS_SEND_MUSIC);
				break;
			case FIND_FRIENDS:
				reporter.logUserAction(UserActions.Toolbar.ACCESS_FIND_FRIENDS);
				break;
			default:
			}
		}
	}
	
	@ActionMethod(Actions.View.SET_TOP_HUNDRED_CATEGORY_VIEW_ID)
	public void setTopHundredCategoryModel(ValueAction<ModelContainerView> action){
		controlEngine.fireValueEvent(Events.View.TOP_HUNDRED_CATEGORY_MODEL_SELECTION, action.getValue());
	}
	@ActionMethod(Actions.View.SET_TOP_HUNDRED_PLAYLIST_VIEW_ID)
	public void setTopHundredPlaylistModel(ValueAction<ModelContainerView> action){
		controlEngine.fireValueEvent(Events.View.TOP_HUNDRED_PLAYLIST_MODEL_SELECTION, action.getValue());
	}
	

	@ActionMethod(Actions.View.HIDE_DRAWER_ID)
	public void hideDrawer() {
		setDrawer(false);
	}

	@ActionMethod(Actions.View.TOGGLE_DRAWER_ID)
	public void toggleDrawer() {
		boolean value = getDrawerDisplayed();
		setDrawer(!value);
	}

	@ActionMethod(Model.DISPLAYED_ITEM_COUNT_ID)
	public void setDisplayedItemCount(ValueAction<Integer> action) {
		controlEngine.set(Model.DISPLAYED_ITEM_COUNT, action.getValue(),
				Events.Application.DISPLAYED_ITEM_COUNT_CHANGED);
	}

	@ActionMethod(Model.DISPLAYED_TRACK_LIST_ID)
	public void setDisplayedTrackList(ValueAction<List<Track>> action) {
		controlEngine.set(Model.DISPLAYED_TRACK_LIST, action.getValue(), null);
		controlEngine.set(Model.DISPLAYED_ITEM_COUNT, action.getValue().size(),
				Events.Application.DISPLAYED_ITEM_COUNT_CHANGED);
	}

	@MessageMethod(MessEngineConstants.USER_SESSION_CLOSED_TYPE)
	public void onUserSessionClosed() {
		setDrawer(false);
	}

	public void setDrawer(boolean value) {
		boolean old = getDrawerDisplayed();
		if (value != old) {
			if (value) {
				if (!controlEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)) {
					controlEngine.fireEvent(Events.Errors.NETWORK_REQUIRED_FOR_ACTION, new NetworkActionErrorEvent(
							NetworkActions.DISPLAY_DRAWER));
					return;
				}
				reporter.logUserAction(UserActions.AllNetwork.ACCESS_ALERTS_PANEL);
			}
			controlEngine.set(Model.DRAWER_DISPLAYED, value, Events.View.DRAWER_DISPLAYED_CHANGED);
		}
	}

	private boolean getDrawerDisplayed() {
		Boolean old = controlEngine.get(Model.DRAWER_DISPLAYED);
		if (old == null) {
			old = false;
		}
		return old;
	}

	@ActionMethod(Actions.View.SELECT_PLAYING_TRACKCONTAINER_ID)
	public void selectPlayingTrackContainer(){
	    TrackContainer trackContainer = controlEngine.get(Model.PLAYING_TRACKCONTAINER);
	    if(trackContainer instanceof Playlist && ((Playlist)trackContainer).getName().equals("downloadTrackContainer")){
		   changeCurrentView(new ValueAction<ContainerView>(new ContainerView(Views.DOWNLOAD)));
	    }
	    else{
	    	controlEngine.fireValueEvent(Events.View.SELECTED_PLAYING_TRACKCONTAINER, trackContainer);
	    }
	}
	
}
