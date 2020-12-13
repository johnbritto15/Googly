package clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import index.Index;

public class Cluster {
	Map<Integer, Map<String, Double>> docVectors = new HashMap<Integer, Map<String, Double>>();
	private List<Integer> docIds;
	int id;
	private Index index;
	private Linkage linkage;
	private SimilarityMethod sim;
	
	public Cluster(int id, Index index, Linkage linkage, SimilarityMethod sim)
	{
		this.id=id;
		this.index = index;
		this.linkage=linkage;
		this.sim = sim;
	}
	
	
	public void add(int docId) {
		docVectors.put(docId, index.getDocumentVector(docId));
	}
	
	public Integer getSize() {
		return this.docVectors.keySet().size();
	}
	
	public int getId() {
		return this.id;
	}
	
	public Set<Integer> getDocumentIds(){
		return this.docVectors.keySet();
	}


	
	public double score(Map<String, Double> other)
	{
		ArrayList<Double> scores  = new ArrayList();
		Map<String, Double> center = new HashMap<>();
		
		for(Map<String, Double> docVec : docVectors.values()) {
			scores.add(this.sim.similarity(docVec, other));
			
			for(String term : docVec.keySet()) {
				center.put(term, center.getOrDefault(term, 0.0)+ docVec.get(term));
			}
		}
        switch(linkage)
        {
	        case SINGLE:
	        {
	        	return scoreSingle(scores);
	        }
	        case COMPLETE:
	        {
	        	return scoreComplete(scores);
	        }
	        case AVERAGE:
	        {
	        	return scoreAverage(scores);
	        }
	        case MEAN:
	        default:
	        {
	        	return scoreMean(center, other);
	        }
        }
	}
	
	private double scoreAverage(ArrayList<Double> other)
	{
		double sum = other.stream().mapToDouble(a -> a).sum();
		return sum/docVectors.keySet().size();
	}
	
	private double scoreComplete(ArrayList <Double> other)
	{
		return Collections.min(other);
	}
	
	private double scoreMean(Map<String, Double> center, Map<String, Double> other)
	{
		int numberDocs = docVectors.size();
		center.replaceAll((k,v) -> v = (center.get(k)/numberDocs));
		return sim.similarity(center, other);

	}
	
	private double scoreSingle(ArrayList<Double> other)
	{
		return Collections.max(other);
	}

}

