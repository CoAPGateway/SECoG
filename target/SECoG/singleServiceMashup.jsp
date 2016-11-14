<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import="org.eclipse.californium.core.CoapClient" %>
<%@ page import="org.eclipse.californium.core.CoapResponse" %>

<%@ page import="coap_sg.DataAggregator" %>
<%@ page import="coap_sg.GroupManager" %>
<%@ page import="coap_sg.RequestReceiver" %>
<%@ page import="coap_sg.ResourceManager" %>
<%@ page import="coap_sg.SimilarityCalculator" %>
<%@ page import="coap_sg.UserRequest" %>

<%@ page import="java.util.ArrayList" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
</head>
<body>
	
<%
	long start = System.currentTimeMillis();

	boolean stopFlag = false;
		
	/** Request Receiver Start**/
	request.setCharacterEncoding("euc-kr");
	UserRequest userRequest = new UserRequest();
	RequestReceiver requestReceiver = new RequestReceiver();
	requestReceiver.getUserRequest(request, userRequest);
	
	String coapLocation = userRequest.getCoapLocation();
	String coapResourceType = userRequest.getCoapResourceType();
	String coapOperation = userRequest.getOperation();
	String coapNumberOfReplies = userRequest.getNumberOfReplies();
	String coapTimeout = userRequest.getTimeout();
	String coapReponseFormat = userRequest.getResponseFormat();
	String weightLocation = userRequest.getWeightLocation();
	String weightResouceType = userRequest.getWeightResourceType();
	String coapRetransmission = userRequest.getRetransmission();
	/** Request Receiver End**/
	
	/** Group Manager Start**/
	GroupManager groupManager = new GroupManager();
	ResourceManager resourceManager = new ResourceManager();
	SimilarityCalculator similarityCalculator = new SimilarityCalculator();
	
	//if group does not exist, create one
	if(!groupManager.isGroupExisting(coapLocation, coapResourceType)){
		ArrayList<String> coapURLArray = new ArrayList<String>();
		
		//System.out.println("coapLocation is: " + coapLocation);
		//System.out.println("coapResourceType is: " + coapResourceType);
		
		String directoryPath = "D:/Workspace_J2EE/CoAP-SG/Data/Resource/";
		coapURLArray = resourceManager.manageResource(directoryPath, coapLocation, coapResourceType);
		
		//System.out.println("coapURLArray size is: " + coapURLArray.size());
		
		//if exact matching does not exist, take the most similar one
		ArrayList<String> mostSilimarLocAndResType = new ArrayList<String>();
		if(coapURLArray.size()==0){
			
			//System.out.println("weightLocation and weightResouceType are: " + weightLocation + ", " + weightResouceType);

			//process the weights
			if(weightLocation==""){
				weightLocation = "0.5";
			}
			if(weightResouceType==""){
				weightResouceType = "0.5";
			}
			
			//System.out.println("weightLocation and weightResouceType are: " + weightLocation + ", " + weightResouceType);
			
			mostSilimarLocAndResType = similarityCalculator.calculateSimilarity(coapLocation, coapResourceType, 
					Float.parseFloat(weightLocation), Float.parseFloat(weightResouceType));
			
			//re-search TDB using the most similar loc-rt pair
			coapLocation = mostSilimarLocAndResType.get(0);
			coapResourceType = mostSilimarLocAndResType.get(1);
			
			out.println("Exact matching does not exist! <br>");
			out.println("Most similar location and resource type pair is: " + coapLocation + ", " + coapResourceType + "<br>");
			
			//create group based on the most similar loc-rt pair
			if(!groupManager.isGroupExisting(coapLocation, coapResourceType)){
				coapURLArray = resourceManager.manageResource(directoryPath, coapLocation, coapResourceType);
				
				if(coapURLArray.size()==0){
					stopFlag = true;
					out.println("Most similar loc-rt pair also has no exact matching! Process end... <br>");
				}else{
					groupManager.createGroup(coapURLArray, coapLocation, coapResourceType);
				}
			}
		}else{
			//create group
			groupManager.createGroup(coapURLArray, coapLocation, coapResourceType);
		}
	}
		
	if(stopFlag==false){
		//get observation results
		ArrayList<Float> observationResultArray = new ArrayList<Float>();
		observationResultArray  = groupManager.getObservationResults(coapLocation, coapResourceType, coapNumberOfReplies, coapTimeout, coapRetransmission);
		
		//aggregate observation data according to operation
		DataAggregator dataAggregator = new DataAggregator();
		ArrayList<Float> aggregatedResultArray = new ArrayList<Float>();
		aggregatedResultArray = dataAggregator.aggregateData(observationResultArray, coapOperation);
		
		//print mashup result
		if(aggregatedResultArray.size()==0){
			out.println("No response is arrived!");
		}else{
			out.println("The mashup result is: <br>");
			for(int i=0; i<aggregatedResultArray.size(); i++){
				out.println(aggregatedResultArray.get(i));
			}
		}
	}
	
	long end = System.currentTimeMillis();
	System.out.println("Execution time is: " + (end-start) + " ms");
	
%>

</body>
</html>