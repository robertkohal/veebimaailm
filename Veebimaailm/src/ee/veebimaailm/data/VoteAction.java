package ee.veebimaailm.data;

public class VoteAction {
	private String action;
	private Integer person_id;
	private Integer candidate_id;
	
	public VoteAction() {}

	public String getAction() {
		return action;
	}

	public Integer getPerson_id() {
		return person_id;
	}

	public Integer getCandidate_id() {
		return candidate_id;
	}

}
