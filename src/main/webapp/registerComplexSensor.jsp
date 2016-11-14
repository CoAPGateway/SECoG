<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
    
<%@ page language="java" contentType="text/plain; charset=UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="secog.ResourceManager" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>This is the sensor registration page!</title>
</head>
<body>
	<%
		//get parameters from JSON request
		
		String location = "529";
		String resourceType = "airquality";
		ArrayList<String> composingSensors = new ArrayList<String>();
		composingSensors.add("coap://165.132.120.222:5683/temperature");
		composingSensors.add("coap://165.132.120.222:5683/temperature");
		
		ResourceManager resourceManager = new ResourceManager();
		resourceManager.registerComplexSensor(location, resourceType, composingSensors);
	%>
</body>
</html>