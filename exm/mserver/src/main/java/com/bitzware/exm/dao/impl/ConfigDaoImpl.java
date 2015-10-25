package com.bitzware.exm.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bitzware.exm.dao.ConfigDao;
import com.bitzware.exm.visitordb.model.config.Device;
import com.bitzware.exm.visitordb.model.config.Interaction;


public class ConfigDaoImpl extends HibernateDaoSupport implements ConfigDao {

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	@SuppressWarnings("unchecked")
	public List<Device> getDevices() {
		return getHibernateTemplate().loadAll(Device.class);
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void saveDevices(final List<Device> devices) {
		getHibernateTemplate().execute(new HibernateCallback() {

			
			public Object doInHibernate(final Session session)
					throws HibernateException, SQLException {
				
				session.createQuery("delete from Device").executeUpdate();
				
				for (Device device : devices) {
					session.save(device);
				}
				
				return null;
			}
			
		});
	}

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	@SuppressWarnings("unchecked")
	public List<Interaction> getInteractions() {
		return getHibernateTemplate().loadAll(Interaction.class);
	}
	
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void saveInteractions(final List<Interaction> interactions) {
		getHibernateTemplate().execute(new HibernateCallback() {
			
			
			public Object doInHibernate(final Session session)
			throws HibernateException, SQLException {
				
				session.createQuery("delete from Interaction").executeUpdate();
				for (Interaction interaction : interactions) {
					session.save(interaction);
				}
				
				return null;
			}
			
		});
	}
}
