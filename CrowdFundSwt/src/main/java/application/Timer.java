package application;

import dbadapter.DBFacade;
import dbadapter.Donation;
import dbadapter.Project;
import interfaces.ITimer;

import javax.mail.MessagingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** This class contains the part of application that runs periodically. */
@WebListener
public class Timer implements ITimer, ServletContextListener {
  @Override
  public void checkEndDate() {
    try {
      DBFacade dbf = new DBFacade();
      EmailHandler eh = new EmailHandler();
      final List<Project> sucProjects = dbf.markStatusSuc();
      for (Project p : sucProjects) {
        final List<Donation> donations = dbf.get_projectDonations(p.getId());
        eh.sendSuccessfulProjectStatus(p, donations);
      }

      final List<Project> failedProjects = dbf.markStatusFail();
      for (Project p : failedProjects) {
        final List<Donation> donations = dbf.get_projectDonations(p.getId());
        eh.sendFailedProjectStatusProject(p, donations);
      }
    } catch (SQLException | MessagingException e) {
      e.printStackTrace();
    }
  }

  /** Schedule checkEndDate to run every 24 hours. */
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    ses.scheduleAtFixedRate(this::checkEndDate, 1, 24, TimeUnit.HOURS);
  }
}
