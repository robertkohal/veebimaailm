package ee.veebimaailm.servlets;

import java.io.Serializable;

public class UserProfile implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id_person;
	private int SSN;
	private String username;
	
	public UserProfile(int id_person, String username, int SSN) {
		this.id_person = id_person;
		this.username = username;
		this.SSN = SSN;
	}

	public int getId_person() {
		return id_person;
	}

	public String getUsername() {
		return username;
	}

	public int getSSN() {
		return SSN;
	}
	
}
