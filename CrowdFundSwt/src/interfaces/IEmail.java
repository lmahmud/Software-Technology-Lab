package interfaces;

import dbadapter.Donation;
import dbadapter.Project;

/**
 * Interface for EmailHander to provide all necessary email function.
 * 
 * @author kt
 *
 */
public interface IEmail {
  public void sendConfmEmailFR(Project pj);

  public void sendConfmEmailDon(Donation don);
}
