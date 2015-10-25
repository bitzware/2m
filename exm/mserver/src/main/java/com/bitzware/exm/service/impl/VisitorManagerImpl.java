package com.bitzware.exm.service.impl;

import com.bitzware.exm.dao.VisitorDao;
import com.bitzware.exm.service.VisitorManager;
import com.bitzware.exm.visitordb.model.Visitor;

/**
 * Class that manager visitor data.
 * 
 * @author finagle
 */
public class VisitorManagerImpl implements VisitorManager {
	
	private final int rfidLength = 10;
	
	private VisitorDao visitorDao;

	public VisitorDao getVisitorDao() {
		return visitorDao;
	}

	public void setVisitorDao(final VisitorDao visitorDao) {
		this.visitorDao = visitorDao;
	}

	
	public Long createProfile(Visitor visitor) {
		if (visitor.getRfid() == null || visitor.getRfid().length() != rfidLength) {
			throw new IllegalArgumentException("RFID must consist of 10 characters.");
		}
		
		return visitorDao.createVisitor(visitor);
	}

	public boolean updateProfile(final Visitor visitor) {
		return visitorDao.updateStationProfile(visitor);
	}

	
	public void expireRfid(String rfid) {
		visitorDao.expireRfid(rfid);
	}
	
}
