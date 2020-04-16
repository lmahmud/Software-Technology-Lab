package datatypes;

/**
 * Contains informations about a reward of a project.
 * @author kt
 *
 */
public class Reward {
  private int id;
  private String reward;
  private double amount;
  private int project_id;
  
  public Reward(String reward, double amount) {
    this.reward = reward;
    this.amount = amount;
    this.project_id = -1;
    this.id = -1;
  }

  public Reward(int id, String reward, double amount, int project_id) {
    this.id = id;
    this.reward = reward;
    this.amount = amount;
    this.project_id = project_id;
  }

  public int getId() {
    return id;
  }

  public String getReward() {
    return reward;
  }

  public double getAmount() {
    return amount;
  }

  public int getProject_id() {
    return project_id;
  }
  
}
