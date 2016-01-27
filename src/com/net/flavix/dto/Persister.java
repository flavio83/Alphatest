package com.net.flavix.dto;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;




public class Persister<T> {
	
	public void persist(T... t) throws Exception {
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Transaction transaction = session.getTransaction();
			transaction.begin();
			for(T p : t)
				session.save(p);
			transaction.commit();
		} catch (Exception e) {
			if (session!=null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			throw e;
		}
	}
	
	public T loadPerID(int id) throws Exception {
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(this.getClass());
			criteria.add(Restrictions.eq("id", Long.valueOf(id)));
			return (T)criteria.uniqueResult();
		} catch (Exception e) {
			if (session!=null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			throw e;
		}
	}
	
	public T loadPerCategory(int category) throws Exception {
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(this.getClass());
			criteria.add(Restrictions.eq("category", category));
			return (T)criteria.uniqueResult();
		} catch (Exception e) {
			if (session!=null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			throw e;
		}
	}
	
	public List<T> loadAll() throws Exception {
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(this.getClass());
			List list = criteria.list();
			List<T> listt = new ArrayList<T>(list.size());
			for (Object o : list) {
				listt.add((T) o);
			}
			return listt;
		} catch (Exception e) {
			if (session!=null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			throw e;
		}
	}

}
