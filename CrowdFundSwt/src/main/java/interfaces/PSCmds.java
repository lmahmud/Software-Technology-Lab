package interfaces;

import java.sql.SQLException;

/**
 * Interface that provides all methods for the interaction with a project
 * starter.
 * 
 * @author kt
 *
 */
public interface PSCmds {
  public int createFR(String title, String endDate, String description, String fundingLimit,
      String psemal, String psname, String pspayinfo, String rewards) throws SQLException;

  public void confirmFR(String unique_hash) throws SQLException;
}
