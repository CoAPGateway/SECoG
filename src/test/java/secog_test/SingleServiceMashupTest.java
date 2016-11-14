package secog_test;

import secog.SingleServiceMashup;
import secog.UserRequest;

public class SingleServiceMashupTest {
	public static void main (String args[]){
		UserRequest userRequest = new UserRequest();
		userRequest.setCoapLocation("529");
		userRequest.setCoapResourceType("dust");
		userRequest.setOperation("avg");
		userRequest.setNumberOfReplies("1");
		userRequest.setRetransmission("no");
		userRequest.setTimeout("");
		userRequest.setWeightLocation("");
		userRequest.setWeightResourceType("");
		
		SingleServiceMashup singleServiceMashup = new SingleServiceMashup();
		
		//int success = 0;
		float result;
		long executionTime = 0;
		
		result = -1;
		long start = System.currentTimeMillis();
		
		result = singleServiceMashup.mashupSingleServices(userRequest);
		
		long end = System.currentTimeMillis();
		
		executionTime += (end-start);
		
		System.out.println("executionTime is: " + executionTime);
		System.out.println("result is: " + result);
		
	}
}
