package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import datatypes.PStatus;

import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import dbadapter.DBFacade;
import dbadapter.DBUtil;
import dbadapter.Project;

/*
 * Tests use JUnit5 and Jmockit. (Powermock is not compatible with JUnit5)
 */

/* IMPORTANT
 * '-javaagent:lib/jmockit-1.49.jar' should be configured as 
 * a vm argument in the run configuration.
 */

/**
 * Tests for DBFacade.
 * 
 * @author chamatht
 *
 */
@ExtendWith(MockitoExtension.class)
class DBFacadeTest {
  private Connection conn;

  private String queryAllSql = "SELECT id, isTemp, status, title, description, endDate, fundingLimit, psemail, psname, sm, donNum, p.hash, pspayinfo\n"
      + "FROM projects p\n"
      + "LEFT JOIN (SELECT project_id, SUM(amount) sm, COUNT(*) donNum FROM donations GROUP BY project_id) AS ds\n"
      + "ON p.id = ds.project_id";

  PreparedStatement ps, psMakeTpj;

  @BeforeEach
  void setUp() throws Exception {
    conn = mock(Connection.class);

    // mocking static method DBUtil.getConnection() using jmockit
    new mockit.MockUp<DBUtil>() {
      @mockit.Mock
      public Connection getConnection() throws SQLException {
        return conn;
      }
    };
  }

  // get_allProjects -----------------------------------------------
  void setupAllprojects() throws SQLException {
    ps = mock(PreparedStatement.class);
    ResultSet rs = mock(ResultSet.class);

    // Set up return values for Connection and PrepareStatement
    when(conn.prepareStatement(Mockito.anyString())).thenReturn(ps);
    when(ps.executeQuery()).thenReturn(rs);

    // Set up return values for ResultSet
    when(rs.next()).thenReturn(true).thenReturn(false);
    when(rs.getInt("id")).thenReturn(1);
    when(rs.getBoolean("isTemp")).thenReturn(false);
    when(rs.getString("status")).thenReturn("Open");
    when(rs.getString("title")).thenReturn("mytitle");
    when(rs.getString("description")).thenReturn("my description");
    when(rs.getDate("endDate")).thenReturn(Date.valueOf("2019-02-15"));
    when(rs.getDouble("fundingLimit")).thenReturn(3000.0);
    when(rs.getString("psemail")).thenReturn("abc@abc.com");
    when(rs.getString("psname")).thenReturn("My Name");
    when(rs.getDouble("sm")).thenReturn(1000.0);
    when(rs.getInt("donNum")).thenReturn(2);
    when(rs.getString("hash")).thenReturn("123");
    when(rs.getString("pspayinfo")).thenReturn("111");

  }

  /**
   * Test for get_allProjects with all the parameters.
   */
  @Test
  void testGet_allProjectsAllParams() {
    try {
      setupAllprojects();
      DBFacade dbf = new DBFacade();
      List<Project> testProjects = dbf.get_allProjects("mytitle", LocalDate.of(2020, 01, 29),
          PStatus.Open);

      String sql = queryAllSql + "\nWHERE endDate >= ? \nAND title LIKE ? \nAND status = ? ";
      verify(conn).prepareStatement(sql);

      Project expectedProject = new Project(1, false, PStatus.Open, "mytitle", "my description",
          LocalDate.of(2019, 02, 15), 3000.0, "abc@abc.com", "My Name", 1000.0, 2, "123", "111");

      assertEquals(testProjects.size(), 1);
      assertEquals(testProjects.get(0), expectedProject);

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Test for get_allProjects with only valid parameter endDate.
   */
  @Test
  void testGet_allProjectsOnlyDate() {
    DBFacade dbf;
    try {
      setupAllprojects();
      dbf = new DBFacade();
      dbf.get_allProjects("", LocalDate.of(2020, 01, 29), PStatus.Any);

      String sql = queryAllSql + "\nWHERE endDate >= ? ";
      verify(conn).prepareStatement(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Test for get_allProjects with only valid parameters endDate and title.
   */
  @Test
  void testGet_allProjectsOnlyDate_Title() {
    try {
      setupAllprojects();
      DBFacade dbf = new DBFacade();
      dbf.get_allProjects("mytitle", LocalDate.of(2020, 01, 29), PStatus.Any);

      String sql = queryAllSql + "\nWHERE endDate >= ? \nAND title LIKE ? ";
      verify(conn).prepareStatement(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Test for get_allProjects with only valid parameter endDate and status.
   */
  @Test
  void testGet_allProjectsOnlyDate_Status() {
    try {
      setupAllprojects();
      DBFacade dbf = new DBFacade();
      dbf.get_allProjects("", LocalDate.of(2020, 01, 29), PStatus.Open);

      String sql = queryAllSql + "\nWHERE endDate >= ? \nAND status = ? ";
      verify(conn).prepareStatement(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // makeTempProject -----------------------------------------------

  void setUPmakePj() throws SQLException {
    psMakeTpj = mock(PreparedStatement.class);
    ResultSet rstm = mock(ResultSet.class);
    ResultSet rs = mock(ResultSet.class);
    PreparedStatement ps = mock(PreparedStatement.class);

    when(conn.prepareStatement(Mockito.anyString())).thenReturn(ps);
    when(conn.prepareStatement(Mockito.anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
        .thenReturn(psMakeTpj);

    when(ps.executeQuery()).thenReturn(rs);
    when(psMakeTpj.getGeneratedKeys()).thenReturn(rstm);
    when(rs.next()).thenReturn(false);
    when(rstm.next()).thenReturn(true).thenReturn(false);
    when(rstm.getInt("id")).thenReturn(25);
  }

  /**
   * Test for makeTempProject
   */
  @Test
  void test_makeTempProject() {

    try {
      setUPmakePj();

      DBFacade dbf = new DBFacade();
      int id = dbf.makeTempProject("mytitle", LocalDate.of(2020, 1, 1), "desc", 100.0,
          "abc@abc.com", "abc", "1234", null);
      verify(psMakeTpj).setBoolean(1, true);
      verify(psMakeTpj).setString(2, PStatus.Open.toString());
      verify(psMakeTpj).setString(3, "mytitle");
      verify(psMakeTpj).setString(4, "desc");
      verify(psMakeTpj).setDate(5, Date.valueOf("2020-01-01"));
      verify(psMakeTpj).setDouble(6, 100.0);
      verify(psMakeTpj).setString(7, "abc@abc.com");
      verify(psMakeTpj).setString(8, "abc");
      verify(psMakeTpj).setString(10, "1234");

      assertEquals(id, 25);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
