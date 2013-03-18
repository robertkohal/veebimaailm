package ee.veebimaailm.data;



public class Party {
	
	private int id_party;
	private String name;
	
	public Party() {
		
	}
	
	public Party(int id, String name) {
		this.id_party = id;
		this.name = name;
	}
	
	public int getId_party() {
		return id_party;
	}
	public void setId_party(int id_party) {
		this.id_party = id_party;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
