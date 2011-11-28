package com.all.client.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.all.client.UnitTestCase;

@SuppressWarnings("unchecked")
public class TestLocalModelDao extends UnitTestCase {
	@InjectMocks
	LocalModelDao localModelDao = new LocalModelDao();
	@Mock
	HibernateTemplate hibernateTemplate;
	@Mock
	MusicCacheModel musicCacheModel;
	@Mock
	CacheManager cacheManager;
	@Mock
	Ehcache musicCache;
	@Mock
	Ehcache smartCache;

	String hql = "select hashcode FROM Track";
	List<String> resultList = new ArrayList<String>();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		localModelDao.setHibernateTemplate(hibernateTemplate);

		when(cacheManager.getEhcache("trackCache")).thenReturn(musicCache);
		when(cacheManager.getEhcache("recentlyAddedCache")).thenReturn(smartCache);
		when(hibernateTemplate.find(hql)).thenReturn(resultList);
	}

	@Test
	public void shouldFindById() throws Exception {
		Serializable id = mock(Serializable.class);
		localModelDao.findById(LocalTrack.class, id);
		verify(hibernateTemplate).get(LocalTrack.class, id);
	}

	@Test
	public void shouldFindAll() throws Exception {
		localModelDao.findAll(UltraPeerInfo.class);
		verify(hibernateTemplate).find(anyString());
	}

	@Test
	public void shouldCallSaveOnHibernateTemplate() throws Exception {
		Entity entity = mock(Entity.class);
		localModelDao.save(entity);
		verify(hibernateTemplate).save(entity);
	}

	@Test
	public void shouldFindTrackFileByPath() throws Exception {
		String path = "path";
		TrackFile trackFile = mock(TrackFile.class);

		when(hibernateTemplate.execute(isA((HibernateCallback.class)))).thenReturn(trackFile);
		TrackFile result = localModelDao.findTrackFileByPath(path);

		verify(hibernateTemplate).execute(isA(HibernateCallback.class));
		assertEquals(trackFile, result);
	}

}
