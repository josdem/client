package com.all.client.devices;

import java.io.Serializable;
import java.util.List;

import com.all.shared.model.ModelDao;
import com.all.shared.model.Track;

public class ExternalDriveModelDao implements ModelDao {

	@Override
	public <T> T delete(T object) {
		return null;
	}

	@Override
	public <T> List<T> findAll(Class<T> objectClass) {
		return null;
	}

	@Override
	public <T> List<T> findByExample(T clazz) {
		return null;
	}

	@Override
	public <T> T findById(Class<T> objectClass, Serializable id) {
		return null;
	}

	@Override
	public List<Track> findTracks(String query) {
		return null;
	}

	@Override
	public <T> T merge(T object) {
		return null;
	}

	@Override
	public <T> void refresh(T object) {

	}

	@Override
	public <T> T save(T object) {
		return null;
	}

	@Override
	public <T> T saveOrUpdate(T object) {
		return null;
	}

	@Override
	public <T> T update(T object) {
		return null;
	}

	@Override
	public <T> List<T> loadAll(Class<T> clazz) {
		return null;
	}

	@Override
	public void saveAll(List<?> values) {
	}

}
