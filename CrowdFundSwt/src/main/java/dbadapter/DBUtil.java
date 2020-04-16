package dbadapter;

import org.apache.ibatis.jdbc.ScriptRunner;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Helper class to initialize database connections and get connections. This Class is not intended
 * to be instantiated by the caller, instead it automatically gets instantiated by Tomcat. (See
 * META_INF/context.xml). Database config data can be set at ** src/main/webapp/META_INF/context.xml
 * **
 *
 * @author kt
 */
@WebListener
public class DBUtil implements ServletContextListener {
  // @Resource(name = "jdbc/crowdfundDB")
  private static DataSource ds;

  /** Return a database connection from the connection pool. */
  public static Connection getConnection() throws SQLException {
    if (ds == null) throw new RuntimeException("DB not initialized");
    // no need to synchronize, because the driver implementation is thread-safe.
    return ds.getConnection();
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try {
      InitialContext ic = new InitialContext();
      ds = (DataSource) ic.lookup("java:comp/env/jdbc/crowdfundDB");

      // create tables if not exists
      Connection conn = ds.getConnection();
      InputStream istream =
          Thread.currentThread().getContextClassLoader().getResourceAsStream("create_tables.sql");
      if (istream == null) return;
      Reader reader = new BufferedReader(new InputStreamReader(istream));
      ScriptRunner sr = new ScriptRunner(conn);
      sr.runScript(reader);
    } catch (SQLException | NamingException e) {
      System.err.println(e.getMessage());
    }
  }
}
