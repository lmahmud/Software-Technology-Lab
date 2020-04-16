package datatypes;

/**
 * Contains the possible statuses for a project.
 * 
 * @author kt
 *
 */
public enum PStatus {
	Open,
	Successful,
	Failed,
	Any // any one of these states. (only intended to use with search)
}
