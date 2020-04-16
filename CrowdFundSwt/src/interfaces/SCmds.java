package interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import dbadapter.Project;

/**
 * Interface that provides all methods for the interaction with a supporter.
 * 
 * @author kt
 *
 */
public interface SCmds {
  public List<Project> getAllProjects(String title, String endDate, String status)
      throws SQLException;

  public Optional<Project> getProject(int project_id) throws SQLException;

  public int makeDonation(int project_id, double amount, String semail, String sname)
      throws SQLException;

  public void confirmDonation(int donation_id);
}
