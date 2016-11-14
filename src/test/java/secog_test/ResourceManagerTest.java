package secog_test;

import secog.ResourceManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ResourceManagerTest {
	public static void main (String args[]) throws FileNotFoundException {	
		ResourceManager resourceManager = new ResourceManager();
		
		String alias = "529temperature";
		String URL = "http://165.132.120.225/temperature";
		String featureOfInterest = "temperature";
		String geoLocation = "529";
		String sensingPeriod = "5";
		String coverage = "2";
		
		resourceManager.registerSensor(alias, URL, featureOfInterest, geoLocation, sensingPeriod, coverage);
		
		/*String directoryPath = "D:/Workspace_J2EE/SECoG/Data/Sensor/";
		ArrayList<String> searchResultArray = new ArrayList<String>();
		searchResultArray = resourceManager.manageSensor(directoryPath, geoLocation, featureOfInterest);
		
		for(int i=0; i<searchResultArray.size(); i++){
			System.out.println(searchResultArray.get(i));
		}*/
		
	}
}
