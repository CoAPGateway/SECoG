package secog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class SimilarityCalculator {
	class ResourceType{
		String name;
		String hierarchy;
		float informationContent;
		
		public ResourceType(){};
		
		public ResourceType(String name, String hierarchy, float informationContent){
			this.name = name;
			this.hierarchy = hierarchy;
			this.informationContent = informationContent;
		}
	}
	
	class Location{
		String name;
		String hierarchy;
		float informationContent;
		
		public Location(){};
		
		public Location(String name, String hierarchy, float informationContent){
			this.name = name;
			this.hierarchy = hierarchy;
			this.informationContent = informationContent;
		}
	}
	
	//Initiate resoruce type information contents
	public void initiateResourceTypeIC(){
		try {
			OutputStream out = new FileOutputStream("D:/Workspace_J2EE/SECoG/Data/ResourceType/ResourceTypeInformationContents.txt");
			PrintStream printStream = new PrintStream(out);
			
			printStream.println("sensor");
			printStream.println("0");
			printStream.println(-Math.log(1));
			
			printStream.println("temphumidity");
			printStream.println("00");
			printStream.println(-Math.log(0.3));
			
			printStream.println("temperature");
			printStream.println("000");
			printStream.println(-Math.log(0.1));
			
			printStream.println("humidity");
			printStream.println("001");
			printStream.println(-Math.log(0.1));
			
			printStream.println("sound");
			printStream.println("01");
			printStream.println(-Math.log(0.1));
			
			printStream.println("light");
			printStream.println("02");
			printStream.println(-Math.log(0.1));
			
			printStream.println("dust");
			printStream.println("03");
			printStream.println(-Math.log(0.1));
			
			printStream.println("motion");
			printStream.println("04");
			printStream.println(-Math.log(0.1));
			
			printStream.println("distance");
			printStream.println("05");
			printStream.println(-Math.log(0.1));
			
			printStream.println("wind");
			printStream.println("06");
			printStream.println(-Math.log(0.1));
			
			printStream.close();
			out.close();
			
			System.out.println("Initiate resource type information contents done!");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Initiate location information contents
	public void initiateLocationIC(){
		try {
			OutputStream out = new FileOutputStream("D:/Workspace_J2EE/SECoG/Data/Location/LocationInformationContents.txt");
			PrintStream printStream = new PrintStream(out);
			
			printStream.println("campus");
			printStream.println("0");
			printStream.println(-Math.log(1));
			
			printStream.println("engineeringbuilding");
			printStream.println("00");
			printStream.println(-Math.log((float)6/(float)7));
			
			printStream.println("floor5");
			printStream.println("000");
			printStream.println(-Math.log((float)3/(float)7));
			
			printStream.println("floor6");
			printStream.println("001");
			printStream.println(-Math.log((float)2/(float)7));
			
			printStream.println("529");
			printStream.println("0000");
			printStream.println(-Math.log((float)1/(float)7));
			
			printStream.println("527");
			printStream.println("0001");
			printStream.println(-Math.log((float)1/(float)7));
			
			printStream.println("cdma");
			printStream.println("0010");
			printStream.println(-Math.log((float)1/(float)7));
			
			printStream.close();
			out.close();
			
			System.out.println("Initiate location information contents done!");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Calculate resource type similarity
	public ArrayList<ResourceType> calculateResourceTypeSimilarity(String requestedResourceTypeName){
		ArrayList<ResourceType> resourceTypeArray = new ArrayList<ResourceType>();
		ResourceType resourceType;
		ArrayList<ResourceType> resourceTypeSimilarityArray = new ArrayList<ResourceType>();

		//get resource type information contents
		String line1;
	    String line2;
	    String line3;
		File file = new File("D:/Workspace_J2EE/SECoG/Data/ResourceType/ResourceTypeInformationContents.txt");
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    while ((line1 = br.readLine()) != null) {
		    	line2 = br.readLine();
		    	line3 = br.readLine();
		    	resourceType = new ResourceType(line1, line2, Float.parseFloat(line3));
		    	//System.out.println("line1,2,3 is: " + line1 + ", " + line2 + ", " + line3);
		    	
		    	resourceTypeArray.add(resourceType);
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//get requested resource type index
		int requestedResourceTypeIndex = 0;
		for(int i=1; i<resourceTypeArray.size(); i++){
			//System.out.println(resourceTypeArray.get(i).name);
			if(requestedResourceTypeName.equals(resourceTypeArray.get(i).name)){
				requestedResourceTypeIndex = i;
				//System.out.println("The requestedResourceTypeIndex is: " + requestedResourceTypeIndex);
				break;
			}
		}

		//calculate resource type similarity
		for(int i=1; i<resourceTypeArray.size(); i++){
			//System.out.println("Comparison object is: " + resourceTypeArray.get(i).name);
			/*if(i==requestedResourceTypeIndex){
				continue;
			}*/
			//System.out.println("Comparison object hierarchy is: " + resourceTypeArray.get(i).hierarchy);
			
			//get nearest common parent node
			String requestedHierarchy = resourceTypeArray.get(requestedResourceTypeIndex).hierarchy;
			String currentHierarchy = resourceTypeArray.get(i).hierarchy;
			String commonParentHierchary = "";
			
			for(int j=1; j<=Math.min(requestedHierarchy.length(), currentHierarchy.length()); j++){
				String subRequest = requestedHierarchy.substring(0, j);
				String subCurrent = currentHierarchy.substring(0, j);
				
				if(!subRequest.equals(subCurrent)){
					commonParentHierchary = requestedHierarchy.substring(0, j-1);
					break;
				}else if(j == Math.min(requestedHierarchy.length(),currentHierarchy.length())){
					commonParentHierchary = requestedHierarchy.substring(0, j);
					break;
				}
			}
			
			//System.out.println("commonParentHierchary is: " + commonParentHierchary);
			
			//get the similarity
			ResourceType resourceTypeSimilarity;
			for(int m=0; m<resourceTypeArray.size(); m++){
				currentHierarchy = resourceTypeArray.get(m).hierarchy;
				//System.out.println("current hierarchy is: " + currentHierarchy);
				
				if(currentHierarchy.equals(commonParentHierchary)){
					resourceTypeSimilarity = new ResourceType(resourceTypeArray.get(i).name, resourceTypeArray.get(i).hierarchy, resourceTypeArray.get(m).informationContent);
					//System.out.println("currentSimilarity is: " + currentSimilarity);
					
					resourceTypeSimilarityArray.add(resourceTypeSimilarity);
				}
			}
		}
		
		return resourceTypeSimilarityArray;		
	}
	
	//Calculate location similarity
	public ArrayList<Location> calculateLocationSimilarity(String requestedLocationName){
		ArrayList<Location> locationArray = new ArrayList<Location>();
		Location location;
		ArrayList<Location> locationSimilarityArray = new ArrayList<Location>();

		//get location information contents
		String line1;
	    String line2;
	    String line3;
		File file = new File("D:/Workspace_J2EE/SECoG/Data/Location/LocationInformationContents.txt");
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    while ((line1 = br.readLine()) != null) {
		    	line2 = br.readLine();
		    	line3 = br.readLine();
		    	location = new Location(line1, line2, Float.parseFloat(line3));
		    	
		    	locationArray.add(location);
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//get requested resource type index
		int requestedLocationIndex = 0;
		for(int i=1; i<locationArray.size(); i++){
			if(requestedLocationName.equals(locationArray.get(i).name)){
				requestedLocationIndex = i;
				break;
			}
		}

		//calculate resource type similarity
		for(int i=1; i<locationArray.size(); i++){
			/*if(i==requestedLocationIndex){
				continue;
			}*/
			
			//get nearest common parent node
			String requestedHierarchy = locationArray.get(requestedLocationIndex).hierarchy;
			String currentHierarchy = locationArray.get(i).hierarchy;
			String commonParentHierchary = "";
			
			for(int j=1; j<=Math.min(requestedHierarchy.length(), currentHierarchy.length()); j++){
				String subRequest = requestedHierarchy.substring(0, j);
				String subCurrent = currentHierarchy.substring(0, j);
				
				if(!subRequest.equals(subCurrent)){
					commonParentHierchary = requestedHierarchy.substring(0, j-1);
					break;
				}else if(j == Math.min(requestedHierarchy.length(),currentHierarchy.length())){
					commonParentHierchary = requestedHierarchy.substring(0, j);
					break;
				}
			}
			
			//System.out.println("commonParentHierchary is: " + commonParentHierchary);
			
			//get the similarity
			Location locationSimilarity;
			for(int m=0; m<locationArray.size(); m++){
				currentHierarchy = locationArray.get(m).hierarchy;
				
				if(currentHierarchy.equals(commonParentHierchary)){
					locationSimilarity = new Location(locationArray.get(i).name, locationArray.get(i).hierarchy, locationArray.get(m).informationContent);
					
					locationSimilarityArray.add(locationSimilarity);
				}
			}
		}
		
		return locationSimilarityArray;		
	}
	
	public ArrayList<String> calculateSimilarity(String requestedLocationName, String requestedResourceTypeName, 
			float locationWeight, float resourceTypeWeight){
		ArrayList<Location> locationSimilarityArray = new ArrayList<Location>();
		ArrayList<ResourceType> resourceTypeSimilarityArray = new ArrayList<ResourceType>();
		
		locationSimilarityArray = calculateLocationSimilarity(requestedLocationName);
		resourceTypeSimilarityArray = calculateResourceTypeSimilarity(requestedResourceTypeName);
		
		//Calculate aggregated similarity
		float maxSimilarity = 0;
		float currentSimilarity = 0;
		int locationIndex = 0;
		int resourceTypeIndex = 0;
		for(int i=0; i<locationSimilarityArray.size(); i++){
			for(int j=0; j<resourceTypeSimilarityArray.size(); j++){
				//skip requested location and resource type itself
				if(locationSimilarityArray.get(i).name.equals(requestedLocationName) && resourceTypeSimilarityArray.get(j).name.equals(requestedResourceTypeName)){
					continue;
				}
				
				currentSimilarity = locationSimilarityArray.get(i).informationContent*locationWeight + resourceTypeSimilarityArray.get(j).informationContent*resourceTypeWeight;
				
				if(currentSimilarity>=maxSimilarity){
					maxSimilarity = currentSimilarity;
					locationIndex = i;
					resourceTypeIndex = j;
				}
			}
		}
		
		ArrayList<String> result = new ArrayList<String>();
		result.add(locationSimilarityArray.get(locationIndex).name);
		result.add(resourceTypeSimilarityArray.get(resourceTypeIndex).name);
		
		return result;
	}
}
