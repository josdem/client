package com.all.client;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.all.client.model.LocalModelDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/testDataLayer.xml" })
@Transactional
@TransactionConfiguration
public class SimpleDBTest extends UnitTestCase {

	@Autowired
	private HibernateTemplate hibernateTemplate;
	protected LocalModelDao dao;
	
	@Before
	public void initDao(){
		dao = new LocalModelDao();
		dao.setHibernateTemplate(hibernateTemplate);
	}
		
	@Test
	public void shouldInitialize() throws Exception {
		assertTrue(true);
	}

}
