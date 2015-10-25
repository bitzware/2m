package com.bitzware.exm.service;

import com.bitzware.exm.dao.VisitorDao;
import com.bitzware.exm.visitordb.model.Visitor;

public class TestManager {

	private VisitorDao visitorDao;

	public VisitorDao getVisitorDao() {
		return visitorDao;
	}

	public void setVisitorDao(final VisitorDao visitorDao) {
		this.visitorDao = visitorDao;
	}
	
	public void createVisitor(final Visitor visitor) {
		visitorDao.createVisitor(visitor);
	}
	
}
