package secog;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import secog.UserRequest;

public class RequestReceiver {
	public RequestReceiver(){}
	
	//get user request parameters
	public void getUserRequest(HttpServletRequest request, UserRequest userRequest){
		String coapLocation = request.getParameter("coapLocation");
		String coapResourceType = request.getParameter("coapResourceType");
		String operation = request.getParameter("operation");
		String numberOfReplies = request.getParameter("numberOfReplies");
		String timeout = request.getParameter("timeout");
		String responseFormat = request.getParameter("responseFormat");
		String weightLocation = request.getParameter("weightLocation");
		String weightResourceType = request.getParameter("weightResourceType");
		String retransmission = request.getParameter("retransmission");
		
		userRequest.coapLocation = coapLocation;
		userRequest.coapResourceType = coapResourceType;
		userRequest.operation = operation;
		userRequest.numberOfReplies = numberOfReplies;
		userRequest.timeout = timeout;
		userRequest.responseFormat = responseFormat;
		userRequest.weightLocation = weightLocation;
		userRequest.weightResourceType = weightResourceType;
		userRequest.retransmission = retransmission;
	}
	
	//get user request parameters
	public ArrayList<UserRequest> getComplexUserRequest(HttpServletRequest request){
		ArrayList<UserRequest> userRequestArray = new ArrayList<UserRequest>();
		UserRequest userRequest;
		
		//String coapLocation, coapResourceType, operation;
		
		String numberOfReplies = request.getParameter("numberOfReplies");
		String timeout = request.getParameter("timeout");
		String responseFormat = request.getParameter("responseFormat");
		String weightLocation = request.getParameter("weightLocation");
		String weightResourceType = request.getParameter("weightResourceType");
		String retransmission = request.getParameter("retransmission");
		
		String numberOfParameters = request.getParameter("coapNumberOfParameters");
		
		for(int i=0; i<Integer.parseInt(numberOfParameters); i++){
			userRequest = new UserRequest();
					
			userRequest.coapLocation = request.getParameter("coapLocation"+i);
			//System.out.println(userRequest.coapLocation);
			userRequest.coapResourceType = request.getParameter("coapResourceType"+i);
			//System.out.println(userRequest.coapResourceType);
			userRequest.operation = request.getParameter("operation"+i);
			//System.out.println(userRequest.operation);
			userRequest.numberOfReplies = numberOfReplies;
			userRequest.timeout = timeout;
			userRequest.responseFormat = responseFormat;
			userRequest.weightLocation = weightLocation;
			userRequest.weightResourceType = weightResourceType;
			userRequest.retransmission = retransmission;
			
			userRequestArray.add(userRequest);
		}
		
		return userRequestArray;
	}
}
