package com.bitzware.exm.dao.impl;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bitzware.exm.dao.ServerPropertyDao;
import com.bitzware.exm.service.ConfigManager;
import com.bitzware.exm.visitordb.model.ServerProperty;


public class ServerPropertyDaoImpl extends HibernateDaoSupport implements ServerPropertyDao {

	private ConfigManager configManager;
	
	public ConfigManager getConfigManager() {
		return configManager;
	}

	public void setConfigManager(final ConfigManager configManager) {
		this.configManager = configManager;
	}

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public String getStringProperty(final String name) {
		String value = (String) getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public String doInHibernate(final Session session)
					throws HibernateException, SQLException {
				return (String) session.createQuery(
						"select strValue from ServerProperty where name=:name")
					.setParameter("name", name)
					.uniqueResult();
			}
			
		});
		
		if (value != null) {
			return value;
		} else {
			return configManager.getStringValue(name);
		}
	}

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public Long getLongProperty(final String name) {
		Long property = (Long) getHibernateTemplate().execute(new HibernateCallback() {
			
			@Override
			public Long doInHibernate(final Session session)
			throws HibernateException, SQLException {
				return (Long) session.createQuery(
						"select longValue from ServerProperty where name=:name")
					.setParameter("name", name)
					.uniqueResult();
			}
			
		});
		
		if (property != null) {
			return property;
		} else {
			return configManager.getLongValue(name);
		}
	}
	
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public Integer getIntegerProperty(final String name) {
		Long property = getLongProperty(name);
		
		if (property == null) {
			return null;
		} else {
			return (int) ((long) property);
		}
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void setStringProperty(final String name, final String value) {
		getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(final Session session)
					throws HibernateException, SQLException {
				ServerProperty property =
					(ServerProperty) session.createQuery("from ServerProperty where name=:name")
					.setParameter("name", name)
					.uniqueResult();
				
				if (property != null) {
					property.setStrValue(value);
					session.update(property);
				} else {
					property = new ServerProperty();
					property.setName(name);
					property.setStrValue(value);
					session.save(property);
				}
				
				return null;
			}
			
		});
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void setLongProperty(final String name, final Long value) {
		getHibernateTemplate().execute(new HibernateCallback() {
			
			@Override
			public Object doInHibernate(final Session session)
			throws HibernateException, SQLException {
				ServerProperty property =
					(ServerProperty) session.createQuery("from ServerProperty where name=:name")
					.setParameter("name", name)
					.uniqueResult();
				
				if (property != null) {
					property.setLongValue(value);
					session.update(property);
				} else {
					property = new ServerProperty();
					property.setName(name);
					property.setLongValue(value);
					session.save(property);
				}
				
				return null;
			}
			
		});
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void setIntegerProperty(final String name, final Integer value) {
		if (value == null) {
			setLongProperty(name, null);
		} else {
			setLongProperty(name, (long) ((int) value));
		}
	}
	
}
