package com.all.client.importx.itunes.xml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.client.importx.itunes.xml.legacy.VisitorImpl;
import com.all.client.model.LocalModelDao;
import com.all.client.model.LocalModelFactory;

@Service
public class VisitorFactory {
	@Autowired
	private LocalModelFactory modelFactory;
	@Autowired
	private LocalModelDao dao;
	
	public Visitor newInstance(){
		return new VisitorImpl(modelFactory, dao);
	}

}
