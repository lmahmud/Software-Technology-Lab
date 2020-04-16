package dbadapter;

import datatypes.PStatus;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Data representation of a Project
 *
 * @author kt
 */
public class Project {
  private int id;
  private boolean temp;
  private PStatus status;
  private String title;
  private String description;
  private LocalDate endDate;
  private double fundingLimit;
  private String psemail;
  private String psname;
  private String pspayinfo;
  private String hash;

  // additional fields to provide more information about the project.
  private double currentlyFunded;
  private int donationsNum;

  public Project(
      int id,
      boolean isTemp,
      PStatus status,
      String title,
      String description,
      LocalDate endDate,
      double fundingLimit,
      String psemail,
      String psname,
      double currentlyFunded,
      int donationsNum,
      String hash,
      String pspayinfo) {
    this.id = id;
    this.temp = isTemp;
    this.status = status;
    this.title = title;
    this.description = description;
    this.endDate = endDate;
    this.fundingLimit = fundingLimit;
    this.psemail = psemail;
    this.psname = psname;
    this.currentlyFunded = currentlyFunded;
    this.donationsNum = donationsNum;
    this.hash = hash;
    this.pspayinfo = pspayinfo;
  }

  public Project(
      boolean isTemp,
      PStatus status,
      String title,
      String description,
      LocalDate endDate,
      double fundingLimit,
      String psemail,
      String psname,
      String pspayinfo) {
    this.temp = isTemp;
    this.status = status;
    this.title = title;
    this.description = description;
    this.endDate = endDate;
    this.fundingLimit = fundingLimit;
    this.psemail = psemail;
    this.psname = psname;
    this.pspayinfo = pspayinfo;
  }

  public int getId() {
    return id;
  }

  public boolean getTemp() {
    return temp;
  }

  public PStatus getStatus() {
    return status;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public double getFundingLimit() {
    return fundingLimit;
  }

  public String getPsemail() {
    return psemail;
  }

  public String getPsname() {
    return psname;
  }

  public double getCurrentlyFunded() {
    return currentlyFunded;
  }

  public int getDonationsNum() {
    return donationsNum;
  }

  public String getPspayinfo() {
    return pspayinfo;
  }

  public String getHash() {
    return hash;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        currentlyFunded,
        description,
        donationsNum,
        endDate,
        fundingLimit,
        id,
        psemail,
        psname,
        status,
        temp,
        title);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Project other = (Project) obj;
    return Double.doubleToLongBits(currentlyFunded)
            == Double.doubleToLongBits(other.currentlyFunded)
        && Objects.equals(description, other.description)
        && donationsNum == other.donationsNum
        && Objects.equals(endDate, other.endDate)
        && Double.doubleToLongBits(fundingLimit) == Double.doubleToLongBits(other.fundingLimit)
        && id == other.id
        && Objects.equals(psemail, other.psemail)
        && Objects.equals(psname, other.psname)
        && status == other.status
        && temp == other.temp
        && Objects.equals(title, other.title);
  }
}
