package interfaces;

import datatypes.ProjectAndRewards;
import dbadapter.Project;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interface that provides all methods for the interaction with a supporter.
 *
 * @author kt
 */
public interface SCmds {
  public List<Project> getAllProjects(String title, String endDate, String status)
      throws SQLException;

  public Optional<ProjectAndRewards> getProjectAndRewards(int project_id) throws SQLException;

  public int makeDonation(
      int project_id, double amount, String semail, String sname, String spayinfo)
      throws SQLException;

  public void confirmDonation(String donation_hash) throws SQLException;
}
