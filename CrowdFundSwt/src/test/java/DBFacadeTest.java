import datatypes.PStatus;
import datatypes.Reward;
import dbadapter.DBFacade;
import dbadapter.DBUtil;
import dbadapter.Donation;
import dbadapter.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
 * @author kt
 */
@ExtendWith(MockitoExtension.class)
class DBFacadeTest {
  private Connection conn;

  private String queryAllSql =
      "SELECT id, isTemp, status, title, description, endDate, fundingLimit, psemail, psname, sm, donNum, p.hash, pspayinfo\n"
          + "FROM projects p\n"
          + "LEFT JOIN (SELECT project_id, SUM(amount) sm, COUNT(*) donNum "
          + "     FROM (SELECT * FROM donations WHERE isTemp=0) dons GROUP BY project_id) ds\n"
          + "ON p.id = ds.project_id";

  PreparedStatement ps, psMakeTpj;

  @BeforeEach
  void setUp() {
    conn = mock(Connection.class);

    // mocking static method DBUtil.getConnection() using jmockit
    new mockit.MockUp<DBUtil>() {
      @mockit.Mock
      public Connection getConnection() throws SQLException {
        return conn;
      }
    };
  }

  // Browse -----------------------------------------------
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

  /** Test for get_allProjects with all the parameters. */
  @Test
  void testGet_allProjectsAllParams() {
    try {
      setupAllprojects();
      DBFacade dbf = new DBFacade();
      List<Project> testProjects =
          dbf.get_allProjects("mytitle", LocalDate.of(2020, 1, 29), PStatus.Open);

      String sql = queryAllSql + "\nWHERE endDate >= ? \nAND title LIKE ? \nAND status = ? ";
      verify(conn).prepareStatement(sql);

      Project expectedProject =
          new Project(
              1,
              false,
              PStatus.Open,
              "mytitle",
              "my description",
              LocalDate.of(2019, 2, 15),
              3000.0,
              "abc@abc.com",
              "My Name",
              1000.0,
              2,
              "123",
              "111");

      assertEquals(testProjects.size(), 1);
      assertEquals(testProjects.get(0), expectedProject);

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /** Test for get_allProjects with only valid parameter endDate. */
  @Test
  void testGet_allProjectsOnlyDate() {
    DBFacade dbf;
    try {
      setupAllprojects();
      dbf = new DBFacade();
      dbf.get_allProjects("", LocalDate.of(2020, 1, 29), PStatus.Any);

      String sql = queryAllSql + "\nWHERE endDate >= ? ";
      verify(conn).prepareStatement(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /** Test for get_allProjects with only valid parameters endDate and title. */
  @Test
  void testGet_allProjectsOnlyDate_Title() {
    try {
      setupAllprojects();
      DBFacade dbf = new DBFacade();
      dbf.get_allProjects("mytitle", LocalDate.of(2020, 1, 29), PStatus.Any);

      String sql = queryAllSql + "\nWHERE endDate >= ? \nAND title LIKE ? ";
      verify(conn).prepareStatement(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /** Test for get_allProjects with only valid parameter endDate and status. */
  @Test
  void testGet_allProjectsOnlyDate_Status() {
    try {
      setupAllprojects();
      DBFacade dbf = new DBFacade();
      dbf.get_allProjects("", LocalDate.of(2020, 1, 29), PStatus.Open);

      String sql = queryAllSql + "\nWHERE endDate >= ? \nAND status = ? ";
      verify(conn).prepareStatement(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // Fund -----------------------------------------------

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

  /** Test for makeTempProject without rewards */
  @Test
  void test_makeTempProject() {

    try {
      setUPmakePj();

      DBFacade dbf = new DBFacade();
      int id =
          dbf.makeTempProject(
              "mytitle",
              LocalDate.of(2020, 1, 1),
              "desc",
              100.0,
              "abc@abc.com",
              "abc",
              "1234",
              null);
      verify(psMakeTpj).setBoolean(1, true);
      verify(psMakeTpj).setString(2, PStatus.Open.toString());
      verify(psMakeTpj).setString(3, "mytitle");
      verify(psMakeTpj).setString(4, "desc");
      verify(psMakeTpj).setDate(5, Date.valueOf("2020-01-01"));
      verify(psMakeTpj).setDouble(6, 100.0);
      verify(psMakeTpj).setString(7, "abc@abc.com");
      verify(psMakeTpj).setString(8, "abc");
      verify(psMakeTpj).setString(10, "1234");
      verify(psMakeTpj).executeUpdate();

      assertEquals(id, 25);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /** Test for makeTempProject with a reward */
  @Test
  void test_makeTempProjectWithAReward() {

    try {
      setUPmakePj();
      PreparedStatement rwps = mock(PreparedStatement.class);
      when(conn.prepareStatement("INSERT INTO rewards(reward, amount, project_id) VALUES (?,?,?)"))
          .thenReturn(rwps);

      DBFacade dbf = new DBFacade();
      List<Reward> rewards = new ArrayList<>();
      Reward rwd = new Reward(4, "some reward", 10, 25);
      rewards.add(rwd);

      int id =
          dbf.makeTempProject(
              "mytitle",
              LocalDate.of(2020, 1, 1),
              "desc",
              100.0,
              "abc@abc.com",
              "abc",
              "1234",
              rewards);

      verify(psMakeTpj).setBoolean(1, true);
      verify(psMakeTpj).setString(2, PStatus.Open.toString());
      verify(psMakeTpj).setString(3, "mytitle");
      verify(psMakeTpj).setString(4, "desc");
      verify(psMakeTpj).setDate(5, Date.valueOf("2020-01-01"));
      verify(psMakeTpj).setDouble(6, 100.0);
      verify(psMakeTpj).setString(7, "abc@abc.com");
      verify(psMakeTpj).setString(8, "abc");
      verify(psMakeTpj).setString(10, "1234");
      verify(psMakeTpj).executeUpdate();

      assertEquals(id, 25);

      verify(rwps).setString(1, rwd.getReward());
      verify(rwps).setDouble(2, rwd.getAmount());
      verify(rwps).setInt(3, id);
      verify(rwps).addBatch();
      verify(rwps).executeBatch();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /** Test for makeTempProject with multiple rewards */
  @Test
  void test_makeTempProjectWithMultipleRewards() {

    try {
      setUPmakePj();
      PreparedStatement rwps = mock(PreparedStatement.class);
      when(conn.prepareStatement("INSERT INTO rewards(reward, amount, project_id) VALUES (?,?,?)"))
          .thenReturn(rwps);

      DBFacade dbf = new DBFacade();
      List<Reward> rewards = new ArrayList<>();
      Reward rwd = new Reward("some reward", 10);
      Reward rwd2 = new Reward("some other reward", 20);
      rewards.add(rwd);
      rewards.add(rwd2);

      int id =
          dbf.makeTempProject(
              "mytitle",
              LocalDate.of(2020, 1, 1),
              "desc",
              100.0,
              "abc@abc.com",
              "abc",
              "1234",
              rewards);

      verify(psMakeTpj).setBoolean(1, true);
      verify(psMakeTpj).setString(2, PStatus.Open.toString());
      verify(psMakeTpj).setString(3, "mytitle");
      verify(psMakeTpj).setString(4, "desc");
      verify(psMakeTpj).setDate(5, Date.valueOf("2020-01-01"));
      verify(psMakeTpj).setDouble(6, 100.0);
      verify(psMakeTpj).setString(7, "abc@abc.com");
      verify(psMakeTpj).setString(8, "abc");
      verify(psMakeTpj).setString(10, "1234");
      verify(psMakeTpj).executeUpdate();

      assertEquals(id, 25);

      InOrder iorder = inOrder(rwps);
      iorder.verify(rwps).setString(1, rwd.getReward());
      iorder.verify(rwps).setDouble(2, rwd.getAmount());
      iorder.verify(rwps).setInt(3, id);
      iorder.verify(rwps).addBatch();

      iorder.verify(rwps).setString(1, rwd2.getReward());
      iorder.verify(rwps).setDouble(2, rwd2.getAmount());
      iorder.verify(rwps).setInt(3, id);
      iorder.verify(rwps).addBatch();

      iorder.verify(rwps).executeBatch();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // Donate --------------------------------------------------------

  /** Test for addTempSupporter */
  @Test
  void test_addTempSupporter() {

    try {
      PreparedStatement psUp1 = mock(PreparedStatement.class);
      ResultSet rs1 = mock(ResultSet.class);
      PreparedStatement psUp2 = mock(PreparedStatement.class);

      when(conn.prepareStatement(Mockito.anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
          .thenReturn(psUp1);
      when(conn.prepareStatement("UPDATE donations SET hash = ? WHERE id = ?")).thenReturn(psUp2);

      when(psUp1.executeUpdate()).thenReturn(1);
      when(psUp1.getGeneratedKeys()).thenReturn(rs1);
      when(rs1.next()).thenReturn(true).thenReturn(false);
      when(rs1.getInt("id")).thenReturn(25);
      when(psUp2.executeUpdate()).thenReturn(1);

      DBFacade dbf = new DBFacade();
      int id = dbf.addTempSupporter(1, 450.0, "abc@abc.com", "abc", "123");
      verify(psUp1).setDouble(1, 450.0);
      verify(psUp1).setBoolean(2, true);
      verify(psUp1).setInt(3, 1);
      verify(psUp1).setString(4, "abc@abc.com");
      verify(psUp1).setString(5, "abc");
      verify(psUp1).setString(6, "123");
      verify(psUp1).executeUpdate();

      assertEquals(id, 25);

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // Donate --------------------------------------------------------

  void setUPMark(PreparedStatement ps1, PreparedStatement ps2) throws SQLException {
    ResultSet rs1 = mock(ResultSet.class);

    when(conn.prepareStatement(Mockito.anyString())).thenReturn(ps1).thenReturn(ps2);
    when(ps1.executeQuery()).thenReturn(rs1);
    when(rs1.next()).thenReturn(false);
    when(ps2.executeUpdate()).thenReturn(1);
  }

  /** Test for markStatusSuc */
  @Test
  void test_markStatusSuc() {
    PreparedStatement ps1 = mock(PreparedStatement.class), ps2 = mock(PreparedStatement.class);
    try {
      setUPMark(ps1, ps2);

      DBFacade dbf = new DBFacade();
      dbf.markStatusSuc();

      verify(ps1).setString(1, PStatus.Open.toString());
      verify(ps1).executeQuery();
      verify(ps2).setString(1, PStatus.Successful.toString());
      verify(ps2).executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /** Test for markStatusFail */
  @Test
  void test_markStatusFail() {
    PreparedStatement ps1 = mock(PreparedStatement.class), ps2 = mock(PreparedStatement.class);
    try {
      setUPMark(ps1, ps2);

      DBFacade dbf = new DBFacade();
      dbf.markStatusFail();

      verify(ps1).setString(1, PStatus.Open.toString());
      verify(ps1).executeQuery();
      verify(ps2).setString(1, PStatus.Failed.toString());
      verify(ps2).executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
