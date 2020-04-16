package dbadapter;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

/**
 * Helper class to initialize database connections and get connections.
 * This Class is not intended to be instantiated by the caller, instead
 * it automatically gets instantiated by Tomcat. (See META_INF/context.xml)
 * @author kt
 */
@WebListener
public class DBUtil implements ServletContextListener {
	private static DataSource ds;
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			InitialContext ic = new InitialContext();
			ds = (DataSource) ic.lookup("java:comp/env/jdbc/crowdfundDB");
		} catch (NamingException e) {
			System.err.println(e.getMessage());
		}
		
	}

	/**
	  * Return a database connection from the connection pool.
	  */
	public static Connection getConnection() throws SQLException {
		if (ds == null) throw new RuntimeException("DB not initialized");
		// no need to synchronize, because driver implementation is thread-safe.
		return ds.getConnection();
	}

}
