package ee.veebimaailm.data;

public class Party {
	
	private Integer id_party;
	private String name;
	private String color;
	private Long votes;
	
	public Party() {
		
	}
	
	public Party(int id, String name, String color) {
		super();
		this.id_party = id;
		this.name = name;
		this.color = color;
	}
	
	public Party(String name, long vote) {
		super();
		this.name = name;
		this.votes = vote;
	}
}
