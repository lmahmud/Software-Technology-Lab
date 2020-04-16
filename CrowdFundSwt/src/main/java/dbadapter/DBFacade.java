package dbadapter;

import datatypes.PStatus;
import datatypes.ProjectAndRewards;
import datatypes.Reward;
import interfaces.IProject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

  private String projectQuerySql =
      "SELECT id, isTemp, status, title, description, endDate, fundingLimit, psemail, psname, sm, donNum, p.hash, pspayinfo\n"
          + "FROM projects p\n"
          + "LEFT JOIN (SELECT project_id, SUM(amount) sm, COUNT(*) donNum "
          + "     FROM (SELECT * FROM donations WHERE isTemp=0) dons GROUP BY project_id) ds\n"
          + "ON p.id = ds.project_id";

  public DBFacade() throws SQLException {
    conn = DBUtil.getConnection();
  }

  /** Returns a list of projects that matches given parameters. */
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

      if (!wheretitle.isEmpty()) ps.setString(i++, "%" + title.trim() + "%");

      if (!wherestatus.isEmpty()) ps.setString(i, status.toString());

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
   * @return the projects id of the created temporary project. -2 is returned if a project with same
   *     title exists.
   */
  @Override
  public int makeTempProject(
      String title,
      LocalDate endDate,
      String description,
      double fundingLimit,
      String psemail,
      String psname,
      String pspayinfo,
      List<Reward> rewards)
      throws SQLException {
    int result = -1;

    // title check
    if (projectWithSameTitle(title)) return -2;

    // otherwise continue
    String sql =
        "INSERT INTO projects(isTemp, status, title, description, endDate, fundingLimit,\n"
            + "psemail, psname, pspayinfo)\n"
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setBoolean(1, true);
      ps.setString(2, PStatus.Open.toString());
      ps.setString(3, title);
      ps.setString(4, description);
      ps.setDate(5, Date.valueOf(endDate));
      ps.setDouble(6, fundingLimit);
      ps.setString(7, psemail);
      ps.setString(8, psname);
      ps.setString(10, pspayinfo);

      ps.executeUpdate();

      ResultSet genRs = ps.getGeneratedKeys();
      if (genRs.next()) result = genRs.getInt("id");
      if (result > 0) {
        String sql2 = "UPDATE projects SET hash = ? WHERE id = ?";
        try (PreparedStatement ps2 = conn.prepareStatement(sql2)) {
          ps2.setString(1, strToHash(result + title));
          ps2.setInt(2, result);
          ps2.executeUpdate();
        }
      }
    }
    if (rewards == null || rewards.isEmpty()) return result; // return early if no rewards

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

  /** Make the project with given hash permanent. */
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
  public int addTempSupporter(
      int project_id, double amount, String semail, String sname, String spayinfo)
      throws SQLException {
    int result = -1;

    String sql =
        "INSERT INTO donations (amount, isTemp, project_id, semail, sname, spayinfo) "
            + "VALUES (?, ?, ?, ?, ?, ?)";
    try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setDouble(1, amount);
      ps.setBoolean(2, true);
      ps.setInt(3, project_id);
      ps.setString(4, semail);
      ps.setString(5, sname);
      ps.setString(6, spayinfo);
      ps.executeUpdate();

      ResultSet genRs = ps.getGeneratedKeys();
      if (genRs.next()) result = genRs.getInt("id");
      if (result > 0) {
        String sql2 = "UPDATE donations SET hash = ? WHERE id = ?";
        try (PreparedStatement ps2 = conn.prepareStatement(sql2)) {
          ps2.setString(1, strToHash(result + semail));
          ps2.setInt(2, result);
          ps2.executeUpdate();
        }
      }
    }
    return result;
  }

  /** Make the donation with given hash permanent */
  @Override
  public void addSupporter(String donation_hash) throws SQLException {
    String sql = "UPDATE donations SET isTemp=0 WHERE hash=?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, donation_hash);
      ps.executeUpdate();
    }
  }

  /** Retrieves the project with the given id, if it exists. */
  @Override
  public Optional<ProjectAndRewards> get_projectAndRewards(int project_id) throws SQLException {
    String sql = projectQuerySql + "\nWHERE id = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, project_id);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          final Project project = extractProjectRS(rs);
          List<Reward> rewards = new ArrayList<>();
          String rSql = "SELECT id,reward,amount,project_id FROM rewards WHERE project_id=?";
          try (PreparedStatement rwdPs = conn.prepareStatement(rSql)) {
            rwdPs.setInt(1, project.getId());
            try (ResultSet rwdRs = rwdPs.executeQuery()) {
              while (rwdRs.next()) {
                rewards.add(extractRewardRS(rwdRs));
              }
            }
          }
          return Optional.of(new ProjectAndRewards(project, rewards));
        }
      }
    }
    return Optional.empty();
  }

  /** Retrieves the project with the given id, if it exists. */
  @Override
  public Optional<Project> get_project(int project_id) throws SQLException {
    String sql = projectQuerySql + "\nWHERE id = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, project_id);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return Optional.of(extractProjectRS(rs));
        }
      }
    }
    return Optional.empty();
  }

  /** Get donation by donation id. */
  public Optional<Donation> get_donation(int don_id) throws SQLException {
    String sql = "SELECT * FROM donations WHERE id = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, don_id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return Optional.of(extractDonationRS(rs));
      }
    }
    return Optional.empty();
  }

  /** Get donation by project id. */
  public List<Donation> get_projectDonations(int project_id) throws SQLException {
    ArrayList<Donation> result = new ArrayList<>();
    String sql = "SELECT * FROM donations WHERE project_id = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, project_id);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          result.add(extractDonationRS(rs));
        }
      }
    }
    return result;
  }

  /** Returns a list of successful projects and mark them as successful */
  @Override
  public List<Project> markStatusSuc() throws SQLException {
    ArrayList<Project> result = new ArrayList<>();
    String sql =
        projectQuerySql
            + " WHERE p.endDate < CURRENT_DATE\n AND p.status = ? \n"
            + "AND sm >= p.fundingLimit";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, PStatus.Open.toString());
      try (final ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          result.add(extractProjectRS(rs));
        }
      }
    }

    String markSql =
        "UPDATE projects AS p\n"
            + "SET status = ? \n"
            + "WHERE p.endDate < CURRENT_DATE\n"
            + "AND (SELECT SUM(amount)\n"
            + "FROM donations AS d WHERE d.project_id = p.id\n"
            + "GROUP BY project_id ) >= p.fundingLimit";

    try (PreparedStatement markPs = conn.prepareStatement(markSql)) {
      markPs.setString(1, PStatus.Successful.toString());
      markPs.executeUpdate();
    }
    return result;
  }

  /** Returns a list of failed projects and mark them as failed */
  @Override
  public List<Project> markStatusFail() throws SQLException {
    ArrayList<Project> result = new ArrayList<>();
    final String sql =
        projectQuerySql
            + " WHERE p.endDate < CURRENT_DATE\n AND p.status = ?"
            + "AND sm < p.fundingLimit";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, PStatus.Open.toString());
      try (final ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          result.add(extractProjectRS(rs));
        }
      }
    }
    final String markSql =
        "UPDATE projects AS p\n"
            + "SET status = ? \n"
            + "WHERE p.endDate < CURRENT_DATE\n"
            + "AND (SELECT SUM(amount)\n"
            + "FROM donations AS d WHERE d.project_id = p.id\n"
            + "GROUP BY project_id ) < p.fundingLimit";

    try (PreparedStatement markPs = conn.prepareStatement(markSql)) {
      markPs.setString(1, PStatus.Failed.toString());
      markPs.executeUpdate();
    }
    return result;
  }

  /** Extracts a project from given ResultSet and return it. */
  private Project extractProjectRS(ResultSet rs) throws SQLException {
    return new Project(
        rs.getInt("id"),
        rs.getBoolean("isTemp"),
        PStatus.valueOf(rs.getString("status")),
        rs.getString("title"),
        rs.getString("description"),
        rs.getDate("endDate").toLocalDate(),
        rs.getDouble("fundingLimit"),
        rs.getString("psemail"),
        rs.getString("psname"),
        rs.getDouble("sm"),
        rs.getInt("donNum"),
        rs.getString("hash"),
        rs.getString("pspayinfo"));
  }

  private Reward extractRewardRS(ResultSet rs) throws SQLException {
    return new Reward(
        rs.getInt("id"), rs.getString("reward"),
        rs.getDouble("amount"), rs.getInt("project_id"));
  }

  private Donation extractDonationRS(ResultSet rs) throws SQLException {
    return new Donation(
        rs.getInt("id"),
        rs.getDouble("amount"),
        rs.getBoolean("isTemp"),
        rs.getInt("project_id"),
        rs.getString("semail"),
        rs.getString("sname"),
        rs.getString("spayinfo"),
        rs.getString("hash"));
  }

  /**
   * Calculates and return Sha256 of given input
   *
   * <p>Returns empty string, if NoSuchAlgorithmException occurs
   */
  private String strToHash(String input) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
      StringBuilder hexString = new StringBuilder();
      for (byte b : encodedhash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) hexString.append('0');
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      System.err.println(e.getMessage());
      return "";
    }
  }

  /** Returns whether a project with the given title exists. */
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
