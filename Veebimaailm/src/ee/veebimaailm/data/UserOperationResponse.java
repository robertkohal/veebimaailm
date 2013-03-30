package ee.veebimaailm.data;

public class UserOperationResponse {
	private String result;
	private Long timestamp;
	
	public UserOperationResponse() {
		
	}
	public UserOperationResponse(String result) {
		this.result = result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
}
