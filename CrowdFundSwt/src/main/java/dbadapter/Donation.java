package dbadapter;

import datatypes.Reward;

/**
 * Data representation of a Donation
 *
 * @author kt
 */
public class Donation {
  private int id;
  private double amount;
  private boolean temp;
  private int project_id;
  private String semail;
  private String sname;
  private String spayinfo;
  private String hash;
  private Reward reward;

  public Donation(
      int id,
      double amount,
      boolean temp,
      int project_id,
      String semail,
      String sname,
      String spayinfo,
      String hash) {
    this.id = id;
    this.amount = amount;
    this.temp = temp;
    this.project_id = project_id;
    this.semail = semail;
    this.sname = sname;
    this.spayinfo = spayinfo;
    this.reward = null;
    this.hash = hash;
  }

  public Donation(
      double amount, boolean temp, int project_id, String semail, String sname, String spayinfo) {
    this.amount = amount;
    this.temp = temp;
    this.project_id = project_id;
    this.semail = semail;
    this.sname = sname;
    this.spayinfo = spayinfo;
    this.reward = null;
  }

  public int getId() {
    return id;
  }

  public double getAmount() {
    return amount;
  }

  public boolean isTemp() {
    return temp;
  }

  public int getProject_id() {
    return project_id;
  }

  public String getSemail() {
    return semail;
  }

  public String getSname() {
    return sname;
  }

  public String getSpayinfo() {
    return spayinfo;
  }

  public Reward getReward() {
    return reward;
  }

  public void setReward(Reward reward) {
    this.reward = reward;
  }

  public String getHash() {
    return hash;
  }
}
