package ee.veebimaailm.data;

public class UserOperationResponse {
	private String result;
	private Long timestamp;
	private String region;
	private String party;
	
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
	public void setRegion(String region) {
		this.region = region;
	}
	public void setParty(String party) {
		this.party = party;
	}
	
}
