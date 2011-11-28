package com.all.client.sync;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.Serializable;

import org.hibernate.type.Type;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.all.client.UnitTestCase;
import com.all.client.model.LocalTrack;
import com.all.observ.Observable;
import com.all.shared.sync.SyncAble;

public class TestSyncDaoInterceptor extends UnitTestCase {

	SyncDaoInterceptor daoInterceptor;
	@Mock
	Observable<SyncEvent> syncListener;

	@Before
	public void setup() {
		daoInterceptor = new SyncDaoInterceptor();
		daoInterceptor.syncListener = syncListener;
	}

	@Test
	public void shouldSyncEventOnSave() throws Exception {
		SyncAble syncAble = new LocalTrack("testTrack", "0123456789abcdef0123456789abcdef01234567");
		syncAble.setSyncAble(true);
		Serializable id = null;
		Object[] state = null;
		String[] propertyNames = null;
		Type[] types = null;
		daoInterceptor.onSave(syncAble, id, state, propertyNames, types);
		verify(syncListener).fire(isA(SyncEvent.class));
		syncAble.setSyncAble(false);
		daoInterceptor.onSave(syncAble, id, state, propertyNames, types);
		verify(syncListener, times(1)).fire(isA(SyncEvent.class));
	}

	@Test
	public void shouldSyncEventOnDelete() throws Exception {
		SyncAble syncAble = new LocalTrack("testTrack", "0123456789abcdef0123456789abcdef01234567");
		syncAble.setSyncAble(true);
		Serializable id = null;
		Object[] state = null;
		String[] propertyNames = null;
		Type[] types = null;
		daoInterceptor.onDelete(syncAble, id, state, propertyNames, types);
		verify(syncListener).fire(isA(SyncEvent.class));
		syncAble.setSyncAble(false);
		daoInterceptor.onDelete(syncAble, id, state, propertyNames, types);
		verify(syncListener, times(1)).fire(isA(SyncEvent.class));
	}

	@Test
	public void shouldSyncEventOnUpdate() throws Exception {
		SyncAble syncAble = new LocalTrack("testTrack", "0123456789abcdef0123456789abcdef01234567");
		syncAble.setSyncAble(true);
		Serializable id = null;
		Object[] currentState = null;
		Object[] previousState = null;
		String[] propertyNames = null;
		Type[] types = null;
		daoInterceptor.onFlushDirty(syncAble, id, currentState, previousState, propertyNames, types);
		verify(syncListener).fire(isA(SyncEvent.class));
		syncAble.setSyncAble(false);
		daoInterceptor.onFlushDirty(syncAble, id, currentState, previousState, propertyNames, types);
		verify(syncListener, times(1)).fire(isA(SyncEvent.class));
	}

	@Test
	public void shouldNotSyncEventBecauseIsNotSyncAble() throws Exception {
		Object entity = new Object();
		Serializable id = null;
		Object[] currentState = null;
		Object[] previousState = null;
		String[] propertyNames = null;
		Type[] types = null;
		daoInterceptor.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
		verify(syncListener, never()).fire(isA(SyncEvent.class));
	}
}
