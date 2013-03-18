package ee.veebimaailm.data;

import java.util.ArrayList;


public class PartyList {
	private ArrayList<Party> partys;
	
	public PartyList(ArrayList<Party> partys) {
		super();
		this.partys = partys;
	}

	public ArrayList<Party> getPartys() {
		return partys;
	}

	public void setPartys(ArrayList<Party> partys) {
		this.partys = partys;
	}
	
}
