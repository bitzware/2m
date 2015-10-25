package com.bitzware.exm.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bitzware.exm.dao.EventDao;
import com.bitzware.exm.visitordb.model.Event;


/**
 * DAO class for events.
 * 
 * @author finagle
 */
public class EventDaoImpl extends HibernateDaoSupport implements EventDao {
	
	public void saveEvent(final Event event) {
		getHibernateTemplate().save(event);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public List<Event> getEventsWithVisitors() {
		return (List<Event>) getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public List<Event> doInHibernate(final Session session)
					throws HibernateException, SQLException {
				
				return session.createQuery("from Event e left join fetch e.visitor")
					.list();
			}
			
		});
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void deleteEvents() {
		getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(final Session session)
					throws HibernateException, SQLException {
				
				session.createQuery("delete from Event").executeUpdate();
				return null;
			}
			
		});
	}
	
}
