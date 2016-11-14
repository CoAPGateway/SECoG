package secog;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

public class SingleServiceMashup {
	public float mashupSingleServices(HttpServletRequest request, UserRequest userRequest){
		boolean stopFlag = false;
		float aggregatedResult = -1;
		String directoryPath = "D:/Workspace_J2EE/SECoG/Data/Resource/";
		
		/** Request Receiver Start**/
		try {
			request.setCharacterEncoding("euc-kr");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String coapLocation = userRequest.getCoapLocation();
		//System.out.println("coapLocation is: " + coapLocation);
		String coapResourceType = userRequest.getCoapResourceType();
		//System.out.println("coapResourceType is: " + coapResourceType);
		String coapOperation = userRequest.getOperation();
		String coapNumberOfReplies = userRequest.getNumberOfReplies();
		String coapTimeout = userRequest.getTimeout();
		//String coapReponseFormat = userRequest.getResponseFormat();
		String weightLocation = userRequest.getWeightLocation();
		String weightResouceType = userRequest.getWeightResourceType();
		String retransimission = userRequest.getRetransmission();
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
				
				System.out.println("Exact matching does not exist!");
				System.out.println("Most similar location and resource type pair is: " + coapLocation + ", " + coapResourceType);
				
				//create group based on the most similar loc-rt pair
				if(!groupManager.isGroupExisting(coapLocation, coapResourceType)){
					coapURLArray = resourceManager.manageResource(directoryPath, coapLocation, coapResourceType);
					
					if(coapURLArray.size()==0){
						stopFlag = true;
						System.out.println("Most similar loc-rt pair also has no exact matching! Process end...");
					}else{
						try {
							groupManager.createGroup(coapURLArray, coapLocation, coapResourceType);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}else{
				//create group
				try {
					groupManager.createGroup(coapURLArray, coapLocation, coapResourceType);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
			
		if(stopFlag==false){
			//get observation results
			ArrayList<Float> observationResultArray = new ArrayList<Float>();
			observationResultArray  = groupManager.getObservationResults(coapLocation, coapResourceType, coapNumberOfReplies, coapTimeout, retransimission);
			
			//aggregate observation data according to operation
			DataAggregator dataAggregator = new DataAggregator();
			aggregatedResult = dataAggregator.aggregateSimpleData(observationResultArray, coapOperation);
		}
		
		return aggregatedResult;
	}
	
	public float mashupSingleServices(UserRequest userRequest){
		boolean stopFlag = false;
		float aggregatedResult = -1;
		String directoryPath = "D:/Workspace_J2EE/SECoG/Data/Resource/";
		
		/** Request Receiver Start**/
		String coapLocation = userRequest.getCoapLocation();
		//System.out.println("coapLocation is: " + coapLocation);
		String coapResourceType = userRequest.getCoapResourceType();
		//System.out.println("coapResourceType is: " + coapResourceType);
		String coapOperation = userRequest.getOperation();
		String coapNumberOfReplies = userRequest.getNumberOfReplies();
		String coapTimeout = userRequest.getTimeout();
		//String coapReponseFormat = userRequest.getResponseFormat();
		String weightLocation = userRequest.getWeightLocation();
		String weightResouceType = userRequest.getWeightResourceType();
		String retransimission = userRequest.getRetransmission();
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
				
				System.out.println("Exact matching does not exist!");
				System.out.println("Most similar location and resource type pair is: " + coapLocation + ", " + coapResourceType);
				
				//create group based on the most similar loc-rt pair
				if(!groupManager.isGroupExisting(coapLocation, coapResourceType)){
					coapURLArray = resourceManager.manageResource(directoryPath, coapLocation, coapResourceType);
					
					if(coapURLArray.size()==0){
						stopFlag = true;
						System.out.println("Most similar loc-rt pair also has no exact matching! Process end...");
					}else{
						try {
							groupManager.createGroup(coapURLArray, coapLocation, coapResourceType);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}else{
				//create group
				try {
					groupManager.createGroup(coapURLArray, coapLocation, coapResourceType);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
			
		if(stopFlag==false){
			//get observation results
			ArrayList<Float> observationResultArray = new ArrayList<Float>();
			observationResultArray  = groupManager.getObservationResults(coapLocation, coapResourceType, coapNumberOfReplies, coapTimeout, retransimission);
			//observationResultArray  = groupManager.getObservationResultsParallelly(coapLocation, coapResourceType, coapNumberOfReplies, coapTimeout, retransimission);
			
			//aggregate observation data according to operation
			DataAggregator dataAggregator = new DataAggregator();
			aggregatedResult = dataAggregator.aggregateSimpleData(observationResultArray, coapOperation);
		}
		
		return aggregatedResult;
	}
}
