package dbadapter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import datatypes.PStatus;
import datatypes.Reward;
import interfaces.IProject;

/**
 * Class which acts as the connector between application and database.
 * 
 * @author kt
 */
public class DBFacade implements IProject {
  /*
   * In a dynamic web app creating database connections for every call is costly
   * and inefficient. Therefore this class uses a connection pool, where
   * connections can be reused without instantiating again and again. (See DBUtil
   * class and META_INF/context.xml). More about connection pools:
   * https://en.wikipedia.org/wiki/Connection_pool
   * https://docs.oracle.com/javase/tutorial/jdbc/basics/sqldatasources.html#
   * pooled_connection
   */

  private Connection conn;

  private String projectQuerySql = "SELECT id, isTemp, status, title, description, endDate, fundingLimit, psemail, psname, sm, donNum, p.hash, pspayinfo\n"
      + "FROM projects p\n"
      + "LEFT JOIN (SELECT project_id, SUM(amount) sm, COUNT(*) donNum FROM donations GROUP BY project_id) AS ds\n"
      + "ON p.id = ds.project_id";

  public DBFacade() throws SQLException {
    conn = DBUtil.getConnection();
  }

  /**
   * Returns a list of projects that matches given parameters.
   * 
   * @throws SQLException
   */
  @Override
  public List<Project> get_allProjects(String title, LocalDate endDate, PStatus status)
      throws SQLException {
    ArrayList<Project> result = new ArrayList<>();
    String whereendDate = "\nWHERE endDate >= ? ";
    String wheretitle = (title.isEmpty()) ? "" : "\nAND title LIKE ? ";
    String wherestatus = (status.equals(PStatus.Any)) ? "" : "\nAND status = ? ";

    String sql = projectQuerySql + whereendDate + wheretitle + wherestatus;

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      int i = 1; // use a variable to set parameterIndex dynamically
      ps.setDate(i++, Date.valueOf(endDate));

      if (!wheretitle.isEmpty())
        ps.setString(i++, "%" + title.trim() + "%");

      if (!wherestatus.isEmpty())
        ps.setString(i++, status.toString());

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          result.add(extractProjectRS(rs));
        }
      }
    }
    return result;
  }

  /**
   * Creates a temporary project with given parameters.
   * 
   * @return the projects id of the created temporary project. -2 is returned if a
   *         project with same title exists.
   */
  @Override
  public int makeTempProject(String title, LocalDate endDate, String description,
      double fundingLimit, String psemail, String psname, String pspayinfo, List<Reward> rewards)
      throws SQLException {
    int result = -1;

    // title check
    if (projectWithSameTitle(title))
      return -2;

    // otherwise continue
    String sql = "INSERT INTO projects(isTemp, status, title, description, endDate, fundingLimit,\n"
        + "psemail, psname, hash, pspayinfo)\n" + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
    try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setBoolean(1, true);
      ps.setString(2, PStatus.Open.toString());
      ps.setString(3, title);
      ps.setString(4, description);
      ps.setDate(5, Date.valueOf(endDate));
      ps.setDouble(6, fundingLimit);
      ps.setString(7, psemail);
      ps.setString(8, psname);
      ps.setString(9, strToHash(title + psemail));
      ps.setString(10, pspayinfo);

      ps.executeUpdate();

      ResultSet genRs = ps.getGeneratedKeys();
      if (genRs.next())
        result = genRs.getInt("id");
    }
    if (rewards == null || rewards.isEmpty())
      return result; // return early if no rewards

    boolean previousAutoCommit = conn.getAutoCommit();
    conn.setAutoCommit(false);

    String rwsql = "INSERT INTO rewards(reward, amount, project_id) VALUES (?,?,?)";
    try (PreparedStatement rwdps = conn.prepareStatement(rwsql)) {
      for (Reward rwd : rewards) {
        rwdps.setString(1, rwd.getReward());
        rwdps.setDouble(2, rwd.getAmount());
        rwdps.setInt(3, result);
        rwdps.addBatch();
      }
      rwdps.executeBatch();
      conn.commit();
      conn.setAutoCommit(previousAutoCommit);
    }

    return result;
  }

  /**
   * Make the project with given id permanent.
   */
  @Override
  public void makeProject(String project_hash) throws SQLException {
    String sql = "UPDATE projects SET isTemp=0 WHERE hash=?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, project_hash);
      ps.executeUpdate();
    }
  }

  /**
   * Creates a temporary supporter(donation) with given parameters.
   * 
   * @return the id of the donation.
   */
  @Override
  public int addTempSupporter(int project_id, double amount, String semail, String sname) {
    // TODO Auto-generated method stub
    return 0;
  }

  /**
   * Make the donation with given id permanent
   */
  @Override
  public void addSupporter(int donation_id) {
    // TODO Auto-generated method stub

  }

  /**
   * Retrieves the project with the given id, if it exists.
   */
  @Override
  public Optional<Project> get_project(int project_id) throws SQLException {
    String sql = projectQuerySql + "\nWHERE id = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, project_id);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return Optional.ofNullable(extractProjectRS(rs));
        }
      }
    }

    return Optional.empty();
  }

  /**
   * Extracts a project from given ResultSet and return it.
   */
  private Project extractProjectRS(ResultSet rs) throws SQLException {
    return new Project(rs.getInt("id"), rs.getBoolean("isTemp"),
        PStatus.valueOf(rs.getString("status")), rs.getString("title"), rs.getString("description"),
        rs.getDate("endDate").toLocalDate(), rs.getDouble("fundingLimit"), rs.getString("psemail"),
        rs.getString("psname"), rs.getDouble("sm"), rs.getInt("donNum"), rs.getString("hash"),
        rs.getString("pspayinfo"));
  }

  /**
   * Calculates and return Sha256 of given input
   * 
   * Returns empty string, if NoSuchAlgorithmException occurs
   */
  private String strToHash(String input) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
      StringBuffer hexString = new StringBuffer();
      for (int i = 0; i < encodedhash.length; i++) {
        String hex = Integer.toHexString(0xff & encodedhash[i]);
        if (hex.length() == 1)
          hexString.append('0');
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      System.err.println(e.getMessage());
      return "";
    }
  }

  /**
   * Returns whether a project with the given title exists.
   */
  private boolean projectWithSameTitle(String title) throws SQLException {
    String sql = projectQuerySql + "\nWHERE title = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, title);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return true;
        }
      }
    }

    return false;
  }

}
