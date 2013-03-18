package ee.veebimaailm.listeners;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

public class Databaseinit implements ServletContextListener {
	
	private DataSource datasource;
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		ServletContext servletcontext = arg0.getServletContext();
		try {
			Context initcontext = new InitialContext();
			datasource =(DataSource) initcontext.lookup("java:/comp/env/jdbc/veebimaailm");
		} catch (NamingException e) {
			e.printStackTrace();
		}
		
		servletcontext.setAttribute("datasource", datasource);
	}
}
