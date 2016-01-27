package tsauto;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;




public class AlphaNewsDBFetcher extends Thread {
	
	private List<AlphaNews> sharedNewsList;
	
	public AlphaNewsDBFetcher(List<AlphaNews> list) {
		this.sharedNewsList = list;
		//addTest();
	}
	
	private void addTest() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = session.beginTransaction();
        AlphaNews news = new AlphaNews();
        news.setCategoryID(-9999);
        news.setCurrency("TST");
        news.setDate(new DateTime(Calendar.getInstance().getTime()));
        news.setEntryInLong(false);
        news.setValue(-0.9999);
        session.save(news);
        tx.commit();
	}
	
	private List<AlphaNews> getAllTheNews() throws Exception {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = session.beginTransaction();
        List<AlphaNews> lNews = session.createQuery("FROM AlphaNews").list(); 
        for (Iterator iterator = lNews.iterator(); iterator.hasNext();) {
        	iterator.next();
        }
        tx.commit();
        return lNews;
	}
	
	public void run() {
		while(true) {
			try {
				List<AlphaNews> lNews = getAllTheNews();
				System.out.println("assegnazione " + lNews.size());
				sharedNewsList.clear();
				sharedNewsList.addAll(lNews);
			} catch(Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(1*60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
