package com.all.client.services;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Matchers.*;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.all.client.UnitTestCase;
import com.all.client.model.LocalModelDao;
import com.all.client.model.LocalPlaylist;

@SuppressWarnings("unused")
public class TestLocalModelService extends UnitTestCase {
	@InjectMocks
	private LocalModelService service = new LocalModelService();
	
	@Mock
	HibernateTemplate hibernateTemplate;
	
	@Mock
	LocalModelDao dao;
	
	@Test
	public void shouldUpdateUserObject() throws Exception {
		service.updateUserObject(new LocalPlaylist());
		verify(dao).update(anyObject());
	}
	
	
	
}
