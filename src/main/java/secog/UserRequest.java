package secog;

public class UserRequest {
	String coapLocation;
	String coapResourceType;
	String operation;
	String numberOfReplies;
	String timeout;
	String responseFormat;
	String weightLocation;
	String weightResourceType;
	String retransmission;
	
	public UserRequest(){}
	
	public UserRequest(String coapLocation, String coapResourceType, String operation,
			String numberOfReplies, String timeout, String responseFormat, 
			String weightLocation, String weightResourceType, String retransmission){
		this.coapLocation = coapLocation;
		this.coapResourceType = coapResourceType;
		this.operation = operation;
		this.numberOfReplies = numberOfReplies;
		this.timeout = timeout;
		this.responseFormat = responseFormat;
		this.weightLocation = weightLocation;
		this.weightResourceType = weightResourceType;
		this.retransmission = retransmission;
	}
	
	public String getRetransmission() {
		return retransmission;
	}

	public void setRetransmission(String retransmission) {
		this.retransmission = retransmission;
	}

	public String getWeightLocation() {
		return weightLocation;
	}

	public void setWeightLocation(String weightLocation) {
		this.weightLocation = weightLocation;
	}

	public String getWeightResourceType() {
		return weightResourceType;
	}

	public void setWeightResourceType(String weightResourceType) {
		this.weightResourceType = weightResourceType;
	}

	//get,set method definition
	public String getCoapLocation() {
		return coapLocation;
	}
	public void setCoapLocation(String coapLocation) {
		this.coapLocation = coapLocation;
	}
	public String getCoapResourceType() {
		return coapResourceType;
	}
	public void setCoapResourceType(String coapResourceType) {
		this.coapResourceType = coapResourceType;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getNumberOfReplies() {
		return numberOfReplies;
	}
	public void setNumberOfReplies(String numberOfReplies) {
		this.numberOfReplies = numberOfReplies;
	}
	public String getTimeout() {
		return timeout;
	}
	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}
	public String getResponseFormat() {
		return responseFormat;
	}
	public void setResponseFormat(String responseFormat) {
		this.responseFormat = responseFormat;
	}
}
