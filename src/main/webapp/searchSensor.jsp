<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
    
<%@ page import="secog.ResourceManager" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.JSONArray" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Search sensors from the directory</title>
</head>
<body>
	<%
		String featureOfInterest = request.getParameter("foi");	
		String geoLocation = request.getParameter("geoloc");
		
		String directoryPath = "D:/Workspace_J2EE/SECoG/Data/Sensor/";
		
		/* System.out.println("location: " + location + ", resource type: " + resourceType); */
		
		ResourceManager resourceManager = new ResourceManager();
		
		ArrayList<String> searchResultArray = new ArrayList<String>();
		searchResultArray = resourceManager.manageSensor(directoryPath, geoLocation, featureOfInterest);
		
		JSONArray jsonList = new JSONArray();
		
		for(int i=0; i<searchResultArray.size(); i+=3){
			JSONObject jsonOb = new JSONObject();

			jsonOb.put("URL", searchResultArray.get(i));
			jsonOb.put("FeatureOfInterest", featureOfInterest);
			jsonOb.put("GeoLocation", geoLocation);
			jsonOb.put("SensingPeriod", searchResultArray.get(i+1));
			jsonOb.put("Coverage", searchResultArray.get(i+2));
		    jsonList.add(jsonOb);
		}
	    
	    out.print(jsonList);
	%>
</body>
</html>