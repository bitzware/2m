package com.bitzware.exm.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.bitzware.exm.visitordb.model.Event;
import com.bitzware.exm.visitordb.model.Visitor;


public class TestDao extends HibernateDaoSupport {

	private final int maxResults = 100;
	
	@SuppressWarnings("unchecked")
	public List<Visitor> getVisitors(final Long from, final Long to) {
		return (List<Visitor>) getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public List<Visitor> doInHibernate(final Session session)
					throws HibernateException, SQLException {
				return session.createQuery("from Visitor where id>=:from and id<=:to order by id")
				.setParameter("from", from)
				.setParameter("to", to)
				.setMaxResults(maxResults)
				.list();
			}
			
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<Event> getEvents(final Long from, final Long to) {
		return (List<Event>) getHibernateTemplate().execute(new HibernateCallback() {
			
			@Override
			public List<Event> doInHibernate(final Session session)
			throws HibernateException, SQLException {
				return session.createQuery("from Event where id>=:from and id<=:to order by id")
				.setParameter("from", from)
				.setParameter("to", to)
				.setMaxResults(maxResults)
				.list();
			}
			
		});
	}
}
