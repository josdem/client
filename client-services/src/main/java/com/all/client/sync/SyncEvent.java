package com.all.client.sync;

import com.all.observ.ObserveObject;
import com.all.shared.model.SyncEventEntity.SyncOperation;
import com.all.shared.sync.SyncAble;

public class SyncEvent extends ObserveObject {

	private static final long serialVersionUID = 1L;

	private final SyncOperation syncOperation;
	private final SyncAble entity;

	public SyncEvent(SyncOperation action, SyncAble entity) {
		this.syncOperation = action;
		this.entity = entity;
	}

	public SyncOperation getSyncOperation() {
		return syncOperation;
	}

	public SyncAble getEntity() {
		return entity;
	}
}
