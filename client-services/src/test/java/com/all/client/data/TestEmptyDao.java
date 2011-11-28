package com.all.client.data;

import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import org.junit.Test;

import com.all.shared.model.ModelDao;

public class TestEmptyDao {

	@Test
	public void shouldBeDomainDao() throws Exception {
		EmptyDao emptyDao = EmptyDao.getInstance();
		assertTrue(emptyDao instanceof ModelDao);
	}

	@Test
	public void shouldBeSerializable() throws Exception {
		EmptyDao emptyDao = EmptyDao.getInstance();
		assertTrue(emptyDao instanceof Serializable);
	}
}
