<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import="org.eclipse.californium.core.CoapClient" %>
<%@ page import="org.eclipse.californium.core.CoapResponse" %>

<%@ page import="secog.DataAggregator" %>
<%@ page import="secog.GroupManager" %>
<%@ page import="secog.RequestReceiver" %>
<%@ page import="secog.ResourceManager" %>
<%@ page import="secog.SimilarityCalculator" %>
<%@ page import="secog.SingleServiceMashup" %>
<%@ page import="secog.UserRequest" %>

<%@ page import="java.util.ArrayList" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
</head>
<body>
	
<%
	long start = System.currentTimeMillis();

	SingleServiceMashup singleServiceMashup = new SingleServiceMashup();
	ArrayList<Float> parameterValueArray = new ArrayList<Float>();
	
	ArrayList<UserRequest> userRequestArray = new ArrayList<UserRequest>();
	RequestReceiver requestReceiver = new RequestReceiver();
	userRequestArray = requestReceiver.getComplexUserRequest(request);
	
	for(int i=0; i<userRequestArray.size(); i++){
		System.out.println("Start processing parameter " + i + "...");
		float singleServiceMashupResult = 0;
		singleServiceMashupResult = singleServiceMashup.mashupSingleServices(request, userRequestArray.get(i));
		parameterValueArray.add(singleServiceMashupResult);
	}
	
	for(int i=0; i<parameterValueArray.size(); i++){
		out.println(parameterValueArray.get(i) + "<br>");
	}
	
	DataAggregator dataAggregator = new DataAggregator();
	String coapFormula = request.getParameter("coapFormula");
	float aggregatedResult = -1;
	aggregatedResult = dataAggregator.aggregateComplexData(parameterValueArray, coapFormula);
	
	if(aggregatedResult == -1){
		out.println("Failed to get mashup result!");
	}else{
		out.println("The mashup result is: " + aggregatedResult);
	}
	
	long end = System.currentTimeMillis();
	System.out.println("Execution time is: " + (end-start) + " ms");
%>

</body>
</html>