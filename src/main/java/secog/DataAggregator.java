package secog;

import java.util.ArrayList;

import com.fathzer.soft.javaluator.DoubleEvaluator;

public class DataAggregator {
	public ArrayList<Float> aggregateData(ArrayList<Float> observationResultArray, String operator){
		ArrayList<Float> aggregatedResultArray = new ArrayList<Float>();
		float result = 0;
		
		for(int i=0; i<observationResultArray.size(); i++){
			if(operator.equals("list")){
				aggregatedResultArray.add(observationResultArray.get(i));
			}else if(operator.equals("avg")){
				result += observationResultArray.get(i);
				
				if(i==observationResultArray.size()-1){
					aggregatedResultArray.add(result/observationResultArray.size());
				}
			}else if(operator.equals("min")){
				if(i==0){
					result = observationResultArray.get(i);
				}
				
				if(result > observationResultArray.get(i)){
					result = observationResultArray.get(i);
				}
								
				if(i==observationResultArray.size()-1){
					aggregatedResultArray.add(result);
				}
			}else if(operator.equals("max")){
				if(result < observationResultArray.get(i)){
					result = observationResultArray.get(i);
				}
				
				if(i==observationResultArray.size()-1){
					aggregatedResultArray.add(result);
				}
			}
		}
		
		return aggregatedResultArray;
	}
	
	public float aggregateSimpleData(ArrayList<Float> observationResultArray, String operator){
		float aggregatedResult = -1;
		float result = 0;
		
		for(int i=0; i<observationResultArray.size(); i++){
			if(operator.equals("list")){
				aggregatedResult = observationResultArray.get(i);
			}else if(operator.equals("avg")){
				result += observationResultArray.get(i);
				
				if(i==observationResultArray.size()-1){
					aggregatedResult = result/observationResultArray.size();
				}
			}else if(operator.equals("min")){
				if(i==0){
					result = observationResultArray.get(i);
				}
				
				if(result > observationResultArray.get(i)){
					result = observationResultArray.get(i);
				}
								
				if(i==observationResultArray.size()-1){
					aggregatedResult = result;
				}
			}else if(operator.equals("max")){
				if(result < observationResultArray.get(i)){
					result = observationResultArray.get(i);
				}
				
				if(i==observationResultArray.size()-1){
					aggregatedResult = result;
				}
			}
		}
		
		//System.out.println("aggregatedResult is: " + aggregatedResult);
		
		return aggregatedResult;
	}
	
	public float aggregateComplexData(ArrayList<Float> parameterValueArray, String coapFormula){
		float result = 0;
		ExtendedDoubleEvaluator evaluator = new ExtendedDoubleEvaluator();
		
		//replace parameters with values
		for(int i=0; i<parameterValueArray.size(); i++){
			coapFormula = coapFormula.replace("parameter" + (i+1), parameterValueArray.get(i).toString());
		}
		
		double evaluationResult = evaluator.evaluate(coapFormula);
		
		result = (float)evaluationResult;
		System.out.println(result);
		
		return result;
	}
}
