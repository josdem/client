package com.all.client.notifiers;

import com.all.appControl.control.ControlEngine;
import com.all.core.events.Events;
import com.all.core.events.LibrarySyncEvent;
import com.all.core.events.LibrarySyncProgressEvent;
import com.all.core.events.LibrarySyncEventType;
import com.all.event.ValueEvent;
import com.all.messengine.MessEngine;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.Root;
import com.all.shared.sync.MergeLibraryProgressListener;

public class MergeLibraryNotifier implements MergeLibraryProgressListener {

	public enum State {

		LOADING_CONTEXT(0, 10), DOWNLOADING(11, 40), SAVING_SNAPSHOT(41, 70), SAVING_DELTAS(71, 100), CANCELLED(0, 100);

		private int baseProgress;
		private final int maxProgress;

		private State(int baseProgress, int maxProgress) {
			this.baseProgress = baseProgress;
			this.maxProgress = maxProgress;
		}

		public int getRealProgress(int progress) {
			int range = maxProgress - baseProgress;
			return baseProgress + (range * progress) / 100;
		}

	}

	private State state;

	private final ControlEngine controlEngine;

	private int lastProgress;

	private final String name;

	private final String email;

	private final MessEngine messEngine;

	public MergeLibraryNotifier(ContactInfo libraryOwner, ControlEngine controlEngine, MessEngine messEngine) {
		this.messEngine = messEngine;
		this.email = libraryOwner.getEmail();
		this.name = libraryOwner.getNickName();
		this.controlEngine = controlEngine;
		this.setState(State.DOWNLOADING);
	}

	@Override
	public void onProgress(int progress) {
		notifySyncProgress(progress);
	}

	public void notifyMergeLibraryStarted() {
		controlEngine.fireEvent(Events.Library.SYNC_DOWNLOAD_EVENT, new LibrarySyncEvent(email, name, LibrarySyncEventType.SYNC_STARTED));
	}

	public void notifyMergeLibraryDone(Root root) {
		controlEngine.fireEvent(Events.Library.SYNC_DOWNLOAD_EVENT, new LibrarySyncEvent(email, name, LibrarySyncEventType.SYNC_FINISHED));
		controlEngine.fireEvent(Events.Library.TREE_STRUCTURE_CHANGED, new ValueEvent<Root>(root));
		messEngine.send(new AllMessage<String>(MessEngineConstants.LIBRARY_SYNC_DOWNLOAD_COMPLETE, email));
	}

	public void notifyError() {
		controlEngine.fireEvent(Events.Errors.SYNC_DOWNLOAD_FAILED, new LibrarySyncEvent(email, name, LibrarySyncEventType.SYNC_FINISHED));
	}

	public void cancel() {
		setState(State.CANCELLED);
	}

	private void notifySyncProgress(int progress) {
		if (getState() != State.CANCELLED && progress != lastProgress) {
			LibrarySyncProgressEvent syncProgressEvent = new LibrarySyncProgressEvent(email, this.getState().getRealProgress(progress));
			controlEngine.fireEvent(Events.Library.SYNC_DOWNLOAD_PROGRESS_EVENT, syncProgressEvent);
		}
	}

	public void changeState(State state) {
		// TODO: ADD PROGRESS THREAD FOR LOADING_CONTEXT PHASE
		// TODO: ADD ROOT PROVIDER FOR REFRESHING PREVIEW TREE AFTER APPLYING A SNPASHOT
		this.setState(state);
	}

	public void setState(State state) {
		this.state = state;
	}

	public State getState() {
		return state;
	}

}
