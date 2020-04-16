package datatypes;

import dbadapter.Project;

import java.util.List;

/**
 * Aggregator class for a project and its rewards
 * @author kt
 */
public class ProjectAndRewards {
  private Project project;
  private List<Reward> rewards;

  public ProjectAndRewards(Project project, List<Reward> rewards) {
    this.project = project;
    this.rewards = rewards;
  }

  public Project getProject() {
    return project;
  }

  public List<Reward> getRewards() {
    return rewards;
  }
}
