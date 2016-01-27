package tsauto;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;




public class HibernateUtil {

    private static SessionFactory sessionFactory;
    private static StandardServiceRegistryBuilder serviceRegistryBuilder;

    static {
        try{
            Configuration configuration = new Configuration();
           // configuration.configure(new File("C:\\Users\\flavio\\Desktop\\docs\\workspace3\\DBLoggers\\src\\hibernate.cfg.xml"));
            configuration.configure("hibernate.cfg.xml");
            serviceRegistryBuilder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).configure();
            sessionFactory = configuration.buildSessionFactory(serviceRegistryBuilder.build());
        }catch (Throwable ex) { 
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }

    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}