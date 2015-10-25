package com.bitzware.exm.dao.impl;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bitzware.exm.dao.ReportGeneratorDao;
import com.bitzware.exm.model.report.NamedColumnsResult;
import com.bitzware.exm.util.DateUtil;
import com.bitzware.exm.visitordb.model.Event;
import com.bitzware.exm.visitordb.model.EventType;
import com.bitzware.exm.visitordb.model.Room;
import com.bitzware.exm.visitordb.model.Station;


public class ReportGeneratorDaoImpl extends HibernateDaoSupport implements ReportGeneratorDao {
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public List<Event> getEventsOnStationReportData(final Station station, final Date from,
			final Date to, final EventType eventType) {
		final Calendar startDate = getStartOfDay(from);
		final Calendar endDate = getEndOfDay(to);
		
		return (List<Event>) getHibernateTemplate().execute(new HibernateCallback() {

			public List<Event> doInHibernate(final Session session)
					throws HibernateException, SQLException {
				final Criteria query = session.createCriteria(Event.class, "e");
				
				// TODO: Optimize rooms?
				query.createAlias("station", "s");
				query.setFetchMode("station", FetchMode.JOIN);
				query.setFetchMode("station.room", FetchMode.JOIN);
				
				if (station != null) {
					query.add(Restrictions.eq("station", station));
				}
				
				if (startDate != null) {
					query.add(Restrictions.ge("timestamp", startDate.getTime()));
				}
				
				if (endDate != null) {
					query.add(Restrictions.le("timestamp", endDate.getTime()));
				}
				
				if (eventType != null) {
					query.add(Restrictions.eq("eventType", eventType));
				}

				if (station == null) {
					query.addOrder(Order.asc("s.name"));
					query.addOrder(Order.asc("s.id"));
				}
				query.addOrder(Order.asc("timestamp"));
				
				return query.list();
			}
			
		});
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public List<Event> getEventsInRoomReportData(final Room room, final Date from, final Date to,
			final EventType eventType) {
		final Calendar startDate = getStartOfDay(from);
		final Calendar endDate = getEndOfDay(to);
		
		return (List<Event>) getHibernateTemplate().execute(new HibernateCallback() {
			
			public List<Event> doInHibernate(final Session session)
			throws HibernateException, SQLException {
				final Criteria query = session.createCriteria(Event.class, "e");
				
				// TODO: Optimize rooms?
				query.createAlias("station", "s");
				query.createAlias("station.room", "r");
				query.setFetchMode("station", FetchMode.JOIN);
				query.setFetchMode("station.room", FetchMode.JOIN);
				
				if (room != null) {
					query.add(Restrictions.eq("s.room", room));
				}
				
				if (startDate != null) {
					query.add(Restrictions.ge("timestamp", startDate.getTime()));
				}
				
				if (endDate != null) {
					query.add(Restrictions.le("timestamp", endDate.getTime()));
				}
				
				if (eventType != null) {
					query.add(Restrictions.eq("eventType", eventType));
				}

				if (room == null) {
					query.addOrder(Order.asc("r.name"));
					query.addOrder(Order.asc("r.id"));
				}
				query.addOrder(Order.asc("timestamp"));
				
				return query.list();
			}
			
		});
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public List<Event> getEventsOnFloorReportData(final String floor, final Date from,
			final Date to, final EventType eventType) {
		final Calendar startDate = getStartOfDay(from);
		final Calendar endDate = getEndOfDay(to);
		
		return (List<Event>) getHibernateTemplate().execute(new HibernateCallback() {
			
			public List<Event> doInHibernate(final Session session)
			throws HibernateException, SQLException {
				final Criteria query = session.createCriteria(Event.class, "e");
				
				// TODO: Optimize rooms?
				query.createAlias("station", "s");
				query.createAlias("station.room", "r");
				query.setFetchMode("station", FetchMode.JOIN);
				query.setFetchMode("station.room", FetchMode.JOIN);
				
				if (floor != null) {
					query.add(Restrictions.eq("r.floor", floor));
				}
				
				if (startDate != null) {
					query.add(Restrictions.ge("timestamp", startDate.getTime()));
				}
				
				if (endDate != null) {
					query.add(Restrictions.le("timestamp", endDate.getTime()));
				}
				
				if (eventType != null) {
					query.add(Restrictions.eq("eventType", eventType));
				}

				if (floor == null) {
					query.addOrder(Order.asc("r.floor"));
				}
				query.addOrder(Order.asc("timestamp"));
				
				return query.list();
			}
			
		});
	}

	private final String getVisitorsRoutesQuery =
		"from Event e "
		+ "left join fetch e.station s "
		+ "left join fetch s.room r "
		+ "join fetch e.visitor v "
		+ "where timestamp between :startDate and :endDate "
		+ "order by v, timestamp";
	private final String getVisitorsRoutesWithEventTypeQuery =
		"from Event e "
		+ "left join fetch e.station s "
		+ "left join fetch s.room r "
		+ "join fetch e.visitor v "
		+ "where timestamp between :startDate and :endDate and "
		+ "e.eventType = :eventType "
		+ "order by v, timestamp";
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public List<Event> getVisitorsRoutesReportData(final Date date, final EventType eventType) {
		final Calendar startDate = getStartOfDay(date);
		final Calendar endDate = getEndOfDay(date);
		
		return (List<Event>) getHibernateTemplate().execute(new HibernateCallback() {
			
			public List<Event> doInHibernate(final Session session)
			throws HibernateException, SQLException {
				if (eventType == null) {
					return session.createQuery(getVisitorsRoutesQuery)
					.setParameter("startDate", startDate.getTime())
					.setParameter("endDate", endDate.getTime())
					.list();
				} else {
					return session.createQuery(getVisitorsRoutesWithEventTypeQuery)
					.setParameter("startDate", startDate.getTime())
					.setParameter("endDate", endDate.getTime())
					.setParameter("eventType", eventType)
					.list();					
				}
			}
			
		});
	}
	
	private final String[] popularStationsColumnNames = new String[] {
			"stationName", "roomName", "eventsAmount"
	};
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public NamedColumnsResult getPopularStationsReportData(final Date from, final Date to,
			final EventType eventType) {
		List<Object[]> entries =
			(List<Object[]>) getHibernateTemplate().execute(new HibernateCallback() {
			
			public List<Object[]> doInHibernate(final Session session)
			throws HibernateException, SQLException {
				Calendar fromDate = null;
				if (from != null) {
					fromDate = DateUtil.startOfDay(from);
				}
				
				Calendar toDate = null;
				if (to != null) {
					toDate = DateUtil.endOfDay(to);
				}
				
				final String queryCorePrefix =
					"select s.name, s.room.name, count(*) "
					+ "from Station s, Event e "
					+ "where e.station = s ";
				final String queryCoreSuffix =
					"group by s.name, s.room.name "
					+ "order by col_2_0_ desc";
				// The 'col_2_0_' expression is a workaround for a Hibernate problem with
				// aliases in the 'select' clause.
				
				final int additionalQueryLength = 80;
				
				StringBuilder queryStr = new StringBuilder(queryCorePrefix.length()
						+ queryCoreSuffix.length() + additionalQueryLength);
				
				queryStr.append(queryCorePrefix);
				if (from != null) {
					queryStr.append("and e.timestamp>=:from ");
				}
				if (to != null) {
					queryStr.append("and e.timestamp<:to ");
				}
				if (eventType != null) {
					queryStr.append("and e.eventType=:eventType ");
				}
				queryStr.append(queryCoreSuffix);
				
				Query query = session.createQuery(queryStr.toString());
				if (from != null) {
					query.setParameter("from", fromDate.getTime());
				}
				if (to != null) {
					query.setParameter("to", toDate.getTime());
				}
				if (eventType != null) {
					query.setParameter("eventType", eventType);
				}
				
				return query.list();
			}
			
		});
		
		return new NamedColumnsResult(entries, popularStationsColumnNames);
	}

	private final String[] dailyVisitsColumnNames = new String[] {
			"year", "month", "day", "visitsAmount"
	};
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public NamedColumnsResult getDailyVisitsReportData(final Date from, final Date to) {
		List<Object[]> entries =
			(List<Object[]>) getHibernateTemplate().execute(new HibernateCallback() {
			
			public List<Object[]> doInHibernate(final Session session)
			throws HibernateException, SQLException {
				Calendar fromDate = null;
				if (from != null) {
					fromDate = DateUtil.startOfDay(from);
				}
				
				Calendar toDate = null;
				if (to != null) {
					toDate = DateUtil.endOfDay(to);
				}
				
				final String queryCorePrefix =
					"select extract(year from rfidValidFrom), extract(month from rfidValidFrom), "
					+ " extract(day from rfidValidFrom), count(*) "
					+ "from Visitor ";
				final String queryCoreSuffix =
					"group by extract(year from rfidValidFrom),"
					+ " extract(month from rfidValidFrom), "
					+ " extract(day from rfidValidFrom) "
					+ "order by col_0_0_, col_1_0_, col_2_0_";
				// The 'col_x_0_' expression is a workaround for a Hibernate problem with
				// aliases in the 'select' clause.
				
				final int additionalQueryLength = 50;
				
				StringBuilder queryStr = new StringBuilder(queryCorePrefix.length()
						+ queryCoreSuffix.length() + additionalQueryLength);
				
				queryStr.append(queryCorePrefix);
				boolean addedWhere = false;
				if (from != null) {
					queryStr.append("where rfidValidFrom>=:from ");
					addedWhere = true;
				}
				if (to != null) {
					if (addedWhere) {
						queryStr.append("and ");
					} else {
						queryStr.append("where ");
					}
					queryStr.append("rfidValidFrom<:to ");
					addedWhere = true;
				}
				queryStr.append(queryCoreSuffix);
				
				Query query = session.createQuery(queryStr.toString());
				if (from != null) {
					query.setParameter("from", fromDate.getTime());
				}
				if (to != null) {
					query.setParameter("to", toDate.getTime());
				}
				
				return query.list();
			}
			
		});
		
		return new NamedColumnsResult(entries, dailyVisitsColumnNames);
	}

	private Calendar getStartOfDay(final Date date) {
		if (date == null) {
			return null;
		}
		
		final Calendar startDate = Calendar.getInstance();
		startDate.setTime(date);
		startDate.set(Calendar.HOUR_OF_DAY, 0);
		startDate.set(Calendar.MINUTE, 0);
		
		return startDate;
	}
	
	private Calendar getEndOfDay(final Date date) {
		if (date == null) {
			return null;
		}
		
		final Calendar endDate = Calendar.getInstance();
		endDate.setTime(date);
		endDate.set(Calendar.HOUR_OF_DAY, 0);
		endDate.set(Calendar.MINUTE, 0);
		endDate.add(Calendar.DAY_OF_MONTH, 1);
		
		return endDate;
	}
	
}
