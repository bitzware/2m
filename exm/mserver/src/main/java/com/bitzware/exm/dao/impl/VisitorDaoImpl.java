package com.bitzware.exm.dao.impl;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bitzware.exm.dao.VisitorDao;
import com.bitzware.exm.visitordb.model.Visitor;


public class VisitorDaoImpl extends HibernateDaoSupport implements VisitorDao {

	private final int strictRfidLimit = 24;
	
	
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public Visitor getVisitorById(final Long id) {
		return (Visitor) getHibernateTemplate().get(Visitor.class, id);
	}

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public Visitor getVisitorByRfid(final String rfid) {
		final String query = "from Visitor where rfid = :rfid and rfidValidFrom >= :limit and" +
				" rfidValidFrom <= :now";
		
		return (Visitor) getHibernateTemplate().execute(new HibernateCallback() {

			@SuppressWarnings("unchecked")
			
			public Visitor doInHibernate(final Session session)
					throws HibernateException, SQLException {
				
				// Check for RFIDs assigned in the last 24 hours.
				Calendar limit = Calendar.getInstance();
				limit.add(Calendar.HOUR, -strictRfidLimit);
				
				List<Visitor> visitors = session.createQuery(query)
					.setParameter("rfid", rfid)
					.setParameter("limit", limit.getTime())
					.setParameter("now", new Date())
					.list();
				
				if (visitors.isEmpty()) {
					return null;
				} else {
					return visitors.get(0);
				}
			}
			
		});
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Visitor getOrCreateVisitorWithRfid(final String rfid) {
		return (Visitor) getHibernateTemplate().execute(new HibernateCallback() {

			
			public Visitor doInHibernate(final Session session)
					throws HibernateException, SQLException {
				
				Visitor visitor = (Visitor) session
					.createQuery("from Visitor where rfid=:rfid")
					.setParameter("rfid", rfid)
					.uniqueResult();
				
				if (visitor == null) {
					visitor = new Visitor();
					visitor.setRfid(rfid);
					
					session.save(visitor);
				}
				
				return visitor;
			}
			
		});
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void deleteVisitors() {
		getHibernateTemplate().execute(new HibernateCallback() {

			
			public Object doInHibernate(final Session session)
					throws HibernateException, SQLException {
				
				session.createQuery("delete from Visitor").executeUpdate();
				return null;
			}
			
		});
	}
	
	/**
	 * Updates the specified visitor profile.
	 * Returns true on success, false if the specified visitor does not exist.
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public boolean updateStationProfile(final Visitor visitor) {
		if (visitor == null || visitor.getId() == null) {
			return false;
		}
		
		return (Boolean) getHibernateTemplate().execute(new HibernateCallback() {

			
			public Boolean doInHibernate(final Session session)
					throws HibernateException, SQLException {
				
				Visitor localVisitor = (Visitor) session.get(Visitor.class, visitor.getId());
				
				if (localVisitor == null) {
					return false;
				}
				
				
				if (!StringUtils.isEmpty(visitor.getAge())) {
					localVisitor.setAge(visitor.getAge());
				}
				
				if (!StringUtils.isEmpty(visitor.getLanguage())) {
					localVisitor.setLanguage(visitor.getLanguage());
				}
				
				if (!StringUtils.isEmpty(visitor.getLevel())) {					
					localVisitor.setLevel(visitor.getLevel());
				}
				
				if (!StringUtils.isEmpty(visitor.getName())) {
					localVisitor.setName(visitor.getName());
				}
				
				if (!StringUtils.isEmpty(visitor.getZoom())) {
					localVisitor.setZoom(visitor.getZoom());
				}
				
				localVisitor.setLastUpdate(new Date());
				
				session.update(localVisitor);
				
				return true;
			}
			
		});
	}

	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void update(final Visitor visitor) {
		getHibernateTemplate().update(visitor);
	}

	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void setRfidTimestamp(final String rfid, final Date timestamp) {
		getHibernateTemplate().execute(new HibernateCallback() {

			
			public Object doInHibernate(final Session session)
					throws HibernateException, SQLException {
				session
					.createQuery("update Visitor set rfidTimestamp = :timestamp where rfid = :rfid")
					.setParameter("rfid", rfid)
					.setParameter("timestamp", timestamp)
					.executeUpdate();
				
				return null;
			}
			
		});
	}

	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void expireRfid(String rfid) {
		Visitor visitor = getVisitorByRfid(rfid);
		if (visitor != null) {
			visitor.setExpired(true);
		}
		getHibernateTemplate().save(visitor);
	}

	private final String checkIfVisitorExistsQuery =
		"from Visitor where rfid = :rfid and rfidValidFrom >= :lower and rfidValidFrom <= :upper";
	
	
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public Long createVisitor(final Visitor visitor) {
		if (visitor.getRfid() == null) {
			return null;
		}
		
		return (Long) getHibernateTemplate().execute(new HibernateCallback() {
			
			@SuppressWarnings("unchecked")
			public Long doInHibernate(Session session) throws HibernateException,
					SQLException {
				visitor.setId(null);
				visitor.setLastUpdate(null);
				if (visitor.getRfidValidFrom() == null) {
					visitor.setRfidValidFrom(visitor.getRfidTimestamp());
				}
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(visitor.getRfidValidFrom());
				cal.add(Calendar.HOUR, -strictRfidLimit);
				Date lower = cal.getTime();
				cal.add(Calendar.HOUR, 2 * strictRfidLimit);
				Date upper = cal.getTime();
				
				List<Visitor> existing = session.createQuery(checkIfVisitorExistsQuery)
					.setParameter("rfid", visitor.getRfid())
					.setParameter("lower", lower)
					.setParameter("upper", upper)
					.list();

				if (!existing.isEmpty()) {
					return null;
				}
				
				getHibernateTemplate().save(visitor);
				
				return visitor.getId();
			}
		});
		
		
	}
	
}
