package application;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;

import datatypes.PStatus;
import datatypes.Reward;
import dbadapter.DBFacade;
import dbadapter.Project;
import interfaces.PSCmds;
import interfaces.SCmds;

/**
 * This class contains the PFApplication which acts as the interface between all
 * components.
 * 
 * @author kt
 *
 */
public class PFApplication implements PSCmds, SCmds {
  private DBFacade dbf;

  public PFApplication() throws SQLException {
    this.dbf = new DBFacade();
  }

  /**
   * Calls DBFacace method to retrieve all projects that matches given parameters.
   */
  @Override
  public List<Project> getAllProjects(String title, String endDate, String status)
      throws SQLException {
    // sanitize arguments before calling DBFacade
    if (title == null)
      title = "";

    if (status == null || status.isEmpty())
      status = "Any";

    LocalDate endd = (endDate == null || endDate.isEmpty()) ? LocalDate.MIN
        : LocalDate.parse(endDate);

    return dbf.get_allProjects(title, endd, PStatus.valueOf(status));
  }

  /**
   * Calls DBFacace method to retrieve the project with given id, if it exists.
   */
  @Override
  public Optional<Project> getProject(int project_id) throws SQLException {
    return dbf.get_project(project_id);
  }

  /**
   * Forwards the funding request to the database and return the newly created
   * temporary project's id. This method also sends a confirmation email to the
   * project starter before it returns.
   * 
   * @return Newly created temporary project's id. -2 if project with same title
   *         exists.
   */
  @Override
  public int createFR(String title, String endDate, String description, String fundingLimit,
      String psemal, String psname, String pspayinfo, String rewards) throws SQLException {
    double dflimit = (fundingLimit == null) ? 0 : Double.parseDouble(fundingLimit);
    LocalDate deDate = (endDate == null) ? LocalDate.now() : LocalDate.parse(endDate);

    List<Reward> rwds = new ArrayList<>();

    // parse rewards string and create Reward objects
    String[] rwdLines = rewards.split("\n");
    for (String rwLine : rwdLines) {
      String[] ln = rwLine.split("-");
      if (ln.length < 2)
        continue;
      rwds.add(new Reward(ln[0].trim(), Double.parseDouble(ln[1].trim())));
    }

    int id = dbf.makeTempProject(title, deDate, description, dflimit, psemal, psname, pspayinfo,
        rwds);
    // return early if id is invalid.
    if (id < 1)
      return id;

    // Send confirmation Email
    Optional<Project> project = dbf.get_project(id);
    if (!project.isPresent()) // return if the project is absent
      return id;

    EmailHandler eh = new EmailHandler();
    eh.sendConfmEmailFR(project.get());
    
    return id;
  }

  /**
   * Forwards the request to database to mark the confirmation.
   * 
   * @param unique_hash The hash string that's unique to each project.
   */
  @Override
  public void confirmFR(String unique_hash) throws SQLException {
    dbf.makeProject(unique_hash);
  }

  @Override
  public int makeDonation(int project_id, double amount, String semail, String sname) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void confirmDonation(int donation_id) {
    // TODO Auto-generated method stub

  }
}
