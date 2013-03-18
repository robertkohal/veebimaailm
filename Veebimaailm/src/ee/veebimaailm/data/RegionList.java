package ee.veebimaailm.data;

import java.util.ArrayList;


public class RegionList {
	private ArrayList<Region> regions;

	public RegionList(ArrayList<Region> regions) {
		super();
		this.regions = regions;
	}

	public ArrayList<Region> getRegionList() {
		return regions;
	}

	public void setRegionList(ArrayList<Region> regionList) {
		this.regions = regionList;
	}
}
