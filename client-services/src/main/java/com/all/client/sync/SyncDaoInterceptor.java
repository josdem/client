package com.all.client.sync;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import com.all.observ.Observable;
import com.all.observ.ObserverCollection;
import com.all.shared.model.SyncEventEntity.SyncOperation;
import com.all.shared.sync.SyncAble;

public class SyncDaoInterceptor extends EmptyInterceptor {

	Log log = LogFactory.getLog(this.getClass());
	private static final long serialVersionUID = 1L;
	Observable<SyncEvent> syncListener = new Observable<SyncEvent>();
	private boolean enabled = true;

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		sendSyncEvent(entity, SyncOperation.SAVE);
		return false;
	}

	@Override
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		sendSyncEvent(entity, SyncOperation.DELETE);
	}

	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames,
			Type[] types) {
		sendSyncEvent(entity, SyncOperation.UPDATE);
		return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
	}

	private void sendSyncEvent(Object entity, SyncOperation action) {
		if (enabled && entity instanceof SyncAble) {
			SyncAble syncAble = (SyncAble) entity;
			if (syncAble.isSyncAble()) {
				// log.debug("Sync Event : op:" + action + " isSyncAble:" +
				// syncAble.isSyncAble() + " entity:" +
				// ToStringBuilder.reflectionToString(entity));
				syncListener.fire(new SyncEvent(action, syncAble));
			}
		}
	}

	public ObserverCollection<SyncEvent> onSyncEventReceived() {
		return syncListener;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
