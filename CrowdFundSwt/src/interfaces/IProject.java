package interfaces;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import datatypes.PStatus;
import datatypes.Reward;
import dbadapter.Project;

/**
 * Interface for DBFacade to provide necessary database functions.
 * 
 * @author kt
 */
public interface IProject {

  public List<Project> get_allProjects(String title, LocalDate endDate, PStatus status)
      throws SQLException;

  public Optional<Project> get_project(int project_id) throws SQLException;

  public int makeTempProject(String title, LocalDate endDate, String description,
      double fundingLimit, String psemal, String psname, String pspayinfo, List<Reward> rewards)
      throws SQLException;

  public void makeProject(String project_hash) throws SQLException;

  public int addTempSupporter(int project_id, double amount, String semail, String sname)
      throws SQLException;

  public void addSupporter(int donation_id) throws SQLException;

}
