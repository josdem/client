package com.all.client.notifiers;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.all.client.model.LocalModelDao;
import com.all.client.sync.SyncDaoInterceptor;
import com.all.client.sync.SyncEvent;
import com.all.observ.Observer;
import com.all.shared.model.SyncEventEntity;
import com.all.shared.sync.SyncGenericConverter;

@Controller
public class SyncEventInterceptor {

	@Autowired
	private LocalModelDao modelDao;

	@Autowired
	public void setDaoNotifier(SyncDaoInterceptor daoInterceptor) {
		daoInterceptor.onSyncEventReceived().add(new Observer<SyncEvent>() {
			public void observe(SyncEvent syncEvent) {
				modelDao.save(new SyncEventEntity(syncEvent.getSyncOperation(), getMap(syncEvent)));
			}
		});
	}

	private HashMap<String, Object> getMap(SyncEvent syncEvent) {
		return SyncGenericConverter.toMap(syncEvent.getEntity(), syncEvent.getSyncOperation());
	}
}