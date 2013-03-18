package ee.veebimaailm.data;

public class Region {
	private int id_region;
	private String name;
	
	public Region(int id, String name) {
		this.id_region = id;
		this.name = name;
	}
	
	public int getId_region() {
		return id_region;
	}
	public void setId_region(int id_region) {
		this.id_region = id_region;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
