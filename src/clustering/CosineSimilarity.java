package clustering;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import index.Index;

public class CosineSimilarity implements SimilarityMethod{
	private Map<String, Double> idfValues = new HashMap<String,Double>();
	public CosineSimilarity(Index index)
	{
		Set<String> vocab = index.getVocabulary();
        int noOfDocs = index.getDocCount();
        for (String word: vocab) 
        {
            int dtf = index.getDocFreq(word);
            Double val = Math.log((noOfDocs + 1)/(dtf+0.5));
            idfValues.put(word, val);
        }
	}
	
	public double similarity(Map<String, Double> repA, Map<String, Double> repB) 
	{
		Set<String>Union = new HashSet<>();
		
		double num = 0, dA= 0, dB = 0.0;
		
		Union.addAll(repA.keySet());
	    Union.addAll(repB.keySet());
		
		
		for(String term : Union) 
		{
			double termIdf = idfValues.get(term);
			double tfA, tfB;
			
			tfA = repA.getOrDefault(term, 0.0);
			tfB = repB.getOrDefault(term, 0.0);
			
			num += tfA * tfB * termIdf * termIdf;
			dA  += tfA * tfA * termIdf * termIdf;
			dB  += tfB * tfB * termIdf * termIdf;
		}
		
		return num/(Math.sqrt(dA*dB));

    }
}
