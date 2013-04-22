package ee.veebimaailm.data;


public class Candidate {
	private Integer id;
	private String person_name;
	private String party_name;
	private String region_name;
	private Long vote;
	
	public Candidate() {
		super();
	}
	
	public Candidate(int id, String name, String party_name, String region_name) {
		super();
		this.id = id;
		this.person_name = name;
		this.party_name = party_name;
		this.region_name = region_name;
	}
	public Candidate(String name, long vote) {
		this.person_name = name;
		this.vote = vote;
	}
	public Candidate(String name, int party_name, int region_name, long vote) {
		super();
		this.person_name = name;
		this.party_name = Integer.toString(party_name);
		this.region_name = Integer.toString(region_name);
		this.vote = vote;
	}
}
