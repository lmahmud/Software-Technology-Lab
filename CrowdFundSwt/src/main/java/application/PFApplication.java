package application;

import datatypes.PStatus;
import datatypes.ProjectAndRewards;
import datatypes.Reward;
import dbadapter.DBFacade;
import dbadapter.Donation;
import dbadapter.Project;
import interfaces.PSCmds;
import interfaces.SCmds;

import javax.mail.MessagingException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class contains the PFApplication which acts as the interface between all components.
 *
 * @author kt
 */
public class PFApplication implements PSCmds, SCmds {
  private DBFacade dbf;

  public PFApplication() throws SQLException {
    this.dbf = new DBFacade();
  }

  /** Calls DBFacace method to retrieve all projects that matches given parameters. */
  @Override
  public List<Project> getAllProjects(String title, String endDate, String status)
      throws SQLException {
    // sanitize arguments before calling DBFacade
    if (title == null) title = "";

    if (status == null || status.isEmpty()) status = "Any";

    LocalDate endd =
        (endDate == null || endDate.isEmpty()) ? LocalDate.MIN : LocalDate.parse(endDate);

    return dbf.get_allProjects(title, endd, PStatus.valueOf(status));
  }

  /** Calls DBFacace method to retrieve the project with its rewards with given id, if it exists. */
  @Override
  public Optional<ProjectAndRewards> getProjectAndRewards(int project_id) throws SQLException {
    return dbf.get_projectAndRewards(project_id);
  }

  /**
   * Forwards the funding request to the database and return the newly created temporary project's
   * id. This method also sends a confirmation email to the project starter before it returns.
   *
   * @return Newly created temporary project's id. -2 if project with same title exists.
   */
  @Override
  public int createFR(
      String title,
      String endDate,
      String description,
      String fundingLimit,
      String psemal,
      String psname,
      String pspayinfo,
      String rewards)
      throws SQLException {
    double dflimit = (fundingLimit == null) ? 0 : Double.parseDouble(fundingLimit);
    LocalDate deDate = (endDate == null) ? LocalDate.now() : LocalDate.parse(endDate);

    List<Reward> rwds = new ArrayList<>();

    // parse rewards string and create Reward objects
    String[] rwdLines = rewards.split("\n");
    for (String rwLine : rwdLines) {
      String[] ln = rwLine.split("-");
      if (ln.length < 2) continue;
      rwds.add(new Reward(ln[0].trim(), Double.parseDouble(ln[1].trim())));
    }

    int id =
        dbf.makeTempProject(title, deDate, description, dflimit, psemal, psname, pspayinfo, rwds);
    // return early if id is invalid.
    if (id < 1) return id;

    // Send confirmation Email
    Optional<Project> project = dbf.get_project(id);
    if (!project.isPresent()) // return if the project is absent
    return id;

    EmailHandler eh = new EmailHandler();
    try {
      eh.sendConfmEmailFR(project.get());
    } catch (MessagingException e) {
      System.err.println(e.getMessage());
    }

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

  /**
   * Forwards the donation request to the database and return the newly created temporary donation's
   * id. This method also sends a confirmation email to the donor before it returns.
   *
   * @return Newly created temporary donations's id. -2 is returned if donation is not possible.
   */
  @Override
  public int makeDonation(
      int project_id, double amount, String semail, String sname, String spayinfo)
      throws SQLException {

    int don_id = dbf.addTempSupporter(project_id, amount, semail, sname, spayinfo);
    // return early if id is invalid.
    if (don_id < 1) return don_id;

    // Send confirmation Email
    // return if the project is absent or closed
    Optional<Project> project = dbf.get_project(project_id);
    if (!project.isPresent() || project.get().getStatus() != PStatus.Open) {
      System.err.println("Project missing or closed");
      return -2;
    }
    ;
    Optional<Donation> donation = dbf.get_donation(don_id);
    if (!donation.isPresent()) {
      System.err.println("Donation missing");
      return don_id;
    }

    try {
      EmailHandler eh = new EmailHandler();
      eh.sendConfmEmailDon(donation.get());
    } catch (MessagingException e) {
      e.printStackTrace();
    }

    return don_id;
  }

  /**
   * Forwards the request to database to mark the donation's confirmation.
   *
   * @param donation_hash The hash string that's unique to each donation.
   */
  @Override
  public void confirmDonation(String donation_hash) throws SQLException {
    dbf.addSupporter(donation_hash);
  }
}
