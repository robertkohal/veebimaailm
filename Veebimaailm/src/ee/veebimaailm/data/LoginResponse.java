package ee.veebimaailm.data;

public class LoginResponse {
	private String result;
	private String username;
	private String error_code;
	
	public LoginResponse() {
		super();
	}
	public LoginResponse(String result, String username) {
		this.result = result;
		this.username = username;
	}
	public LoginResponse(String result, String error_code,int someNumber) {
		this.result = result;
		this.error_code = error_code;
	}
	
}
