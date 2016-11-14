package secog_test;

import java.util.ArrayList;

import secog.DataAggregator;
import secog.GroupManager;

public class DataAggregatorTest {
	public static void main (String args[]){
		/*String coapLocation = "529";
		String coapResourceType = "light";
		String operator = "list";
		
		GroupManager groupManager = new GroupManager();
		ArrayList<Float> observationResultArray = new ArrayList<Float>();
		observationResultArray = groupManager.getObservationResults(coapLocation, coapResourceType, "1", null, "yes");
		
		System.out.println("Observation result is: ");
		for(int i=0; i<observationResultArray.size(); i++){
			System.out.println(observationResultArray.get(i));
		}

		DataAggregator dataAggregator = new DataAggregator();
		ArrayList<Float> aggregatedResultArray = new ArrayList<Float>();
		aggregatedResultArray = dataAggregator.aggregateData(observationResultArray, operator);
		
		System.out.println("Aggregated result is: ");
		for(int i=0; i<aggregatedResultArray.size(); i++){
			System.out.println(aggregatedResultArray.get(i));
		}*/
		
		DataAggregator dataAggregator = new DataAggregator();
		ArrayList<Float> parameterValueArray = new ArrayList<Float>();
		parameterValueArray.add((float)2);
		parameterValueArray.add((float)4);
		
		String coapFormula = "parameter1+sqrt(parameter2)";
		
		dataAggregator.aggregateComplexData(parameterValueArray, coapFormula);
	}
}
