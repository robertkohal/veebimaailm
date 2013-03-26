package ee.veebimaailm.data;

public class VoteResponse {
	private String result;
	private Long timestamp;
	
	public VoteResponse() {
		
	}
	public VoteResponse(String result) {
		this.result = result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
}
