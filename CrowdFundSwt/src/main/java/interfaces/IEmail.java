package interfaces;

import dbadapter.Donation;
import dbadapter.Project;

import javax.mail.MessagingException;
import java.util.List;

/**
 * Interface for EmailHander to provide all necessary email function.
 *
 * @author kt
 */
public interface IEmail {
  public void sendConfmEmailFR(Project pj) throws MessagingException;

  public void sendConfmEmailDon(Donation don) throws MessagingException;

  public void sendSuccessfulProjectStatus(Project pj, List<Donation> dons) throws MessagingException;

  public void sendFailedProjectStatusProject(Project pj, List<Donation> dons) throws MessagingException;
}
