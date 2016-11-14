package secog_test;

import java.util.ArrayList;

import secog.SimilarityCalculator;

public class SimilarityCalculatorTest {
	public static void main (String args[]){
		SimilarityCalculator similarityCalculator = new SimilarityCalculator();
		
		//resource type similarity calculation test
		//System.out.println(similarityCalculator.calculateResourceTypeSimilarity("Temperature"));
		
		//location similarity calculation test
		//System.out.println(similarityCalculator.calculateLocationSimilarity("529"));
		
		//similarity calculation test
		ArrayList<String> similarityCalculationResult = new ArrayList<String>();
		similarityCalculationResult = similarityCalculator.calculateSimilarity("527", "light", (float)0.5, (float)0.5);
		
		System.out.println("similarityCalculationResult is: ");
		for(int i=0; i<similarityCalculationResult.size(); i++){
			System.out.println(similarityCalculationResult.get(i));
		}
	}
}
