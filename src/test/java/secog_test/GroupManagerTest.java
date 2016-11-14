package secog_test;

import java.io.IOException;
import java.util.ArrayList;

import secog.GroupManager;
import secog.ResourceManager;

public class GroupManagerTest {
	public static void main (String args[]){
		ResourceManager resourceManager = new ResourceManager();
		
		String coapLocation = "529";
		String coapResourceType = "light";
		String directoryPath = "D:/Workspace_J2EE/SECoG/Data/Resource/";
		ArrayList<String> coapURLArray = new ArrayList<String>();
		coapURLArray = resourceManager.manageResource(directoryPath, coapLocation, coapResourceType);
		
		GroupManager groupManager = new GroupManager();
		
		//test create a group
		try {
			groupManager.createGroup(coapURLArray, coapLocation, coapResourceType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//test whether the group is existed
		/*System.out.println(groupManager.isGroupExisting(coapLocation, coapResourceType));*/
		
		
		//test to get observation results
		/*ArrayList<Float> observationResultArray = new ArrayList<Float>();
		observationResultArray  = groupManager.getObservationResults(coapLocation, coapResourceType, null, null);
		//groupManager.getObservationResultsParallelly(coapLocation, coapResourceType, null, null);
		
		System.out.println("Observation results are: ");
		for(int i=0; i<observationResultArray.size(); i++){
			System.out.println(observationResultArray.get(i));
		}*/
	}
}
