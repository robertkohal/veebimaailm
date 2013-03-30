package ee.veebimaailm.servlets;

import java.io.Serializable;

public class UserProfile implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id_person;
	private String username;
	
	public UserProfile(int id_person, String username) {
		this.id_person = id_person;
		this.username = username;
	}

	public int getId_person() {
		return id_person;
	}

	public String getUsername() {
		return username;
	}
}
