package ee.veebimaailm.data;

public class Region {
	private int id_region;
	private String name;
	private double longitude;
	private double latitude;
	
	public Region() {
		
	}
	public Region(int id, String name, double longitude, double latitude) {
		super();
		this.id_region = id;
		this.name = name;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
}
