package at.gv.egiz.pdfas.web.store;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.api.ws.PDFASSignRequest;
import at.gv.egiz.pdfas.web.config.WebConfiguration;
import at.gv.egiz.pdfas.web.store.IRequestStore;
import at.gv.egiz.pdfas.web.store.db.Request;

public class DBRequestStore implements IRequestStore {

	private static final Logger logger = LoggerFactory
			.getLogger(DBRequestStore.class);
	
	private SessionFactory sessions;
	private ServiceRegistry serviceRegistry;
	
	public DBRequestStore() {
		Configuration cfg = new Configuration();
		cfg.addAnnotatedClass(Request.class);
		cfg.setProperties(WebConfiguration.getHibernateProps());
		
		serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
				cfg.getProperties()).build();
		
		sessions = cfg.buildSessionFactory(serviceRegistry);
	}
	
	private void cleanOldRequests() {
		int seconds = WebConfiguration.getDBTimeout();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, (-1)* seconds);
		Date date = calendar.getTime();
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss"); 
		logger.info("Clearing Entries before: " + dt.format(date));
		Session session = null;
		Transaction tx = null;
		try {
			session = sessions.openSession();
			tx = session.beginTransaction();
			Query query = session.createQuery("delete from Request as req" + 
				" where req.created < :date");
			query.setCalendar("date", calendar);
			query.executeUpdate();
		} catch(Throwable e) {
			logger.error("Failed to save Request", e);
			tx.rollback();
		} finally {
			if(session != null) {
				session.close();
			}
		}
	}
	
	public String createNewStoreEntry(PDFASSignRequest request) {
		// Clean Old Requests
		this.cleanOldRequests();
		Session session = null;
		Transaction tx = null;
		try {
			session = sessions.openSession();
			tx = session.beginTransaction();
			Request dbRequest = new Request();
			dbRequest.setSignRequest(request);
			dbRequest.setCreated(Calendar.getInstance().getTime());
			session.save(dbRequest);
		
			tx.commit();
			return dbRequest.getId();
		} catch(Throwable e) {
			logger.error("Failed to save Request", e);
			tx.rollback();
			return null;
		} finally {
			if(session != null) {
				session.close();
			}
		}
	}

	public PDFASSignRequest fetchStoreEntry(String id) {
		// Clean Old Requests
		this.cleanOldRequests();
	
		Session session = null;
		Transaction tx = null;
		try {
			session = sessions.openSession();
			tx = session.beginTransaction();
			Request dbRequest = (Request) session.get(Request.class, id);
			
			PDFASSignRequest request = dbRequest.getSignRequest();
			
			session.delete(dbRequest);
		
			tx.commit();
			return request;
		} catch(Throwable e) {
			logger.error("Failed to fetch Request", e);
			tx.rollback();
			return null;
		} finally {
			if(session != null) {
				session.close();
			}
		}
		
	}

}
