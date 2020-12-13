package models;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import index.Index;
import index.Posting;
import index.PostingList;

public abstract class QueryLikelihood {
	Index index;

	public abstract double scorer(double tf, double ctf, double docLen);

	public ArrayList<Entry<String, Double>> retrieveQuery(String query, int k) 
	{
		PriorityQueue<Map.Entry<String, Double>> result = 
				new PriorityQueue<>(Map.Entry.<String, Double>comparingByValue());
		String [] queryTerms = query.split("\\s+");
		
		int doc_count = index.getDocCount();
		double scores[] = new double[doc_count+1];
		double lengths[] = new double[doc_count+1];
		for(int i=1;i<=doc_count;i++)
		{
			scores[i]=0;
			lengths[i]=index.getDocLength(i);
		}
		try
		{
			//term-at-a-time
			for(String term:queryTerms)
			{
				PostingList list = index.getPostings(term);
				int ctf = index.getTermFreq(term);
				for(int i=1;i<=doc_count;i++)
				{
					double tf=0;
					list.skipTo(i);
					Posting p = list.getCurrentPosting();
					if(p!=null && i==p.getDocId())
					{
						tf=p.getTermFreq();
					}
					double doc_len = lengths[i];
					scores[i]+= scorer(tf, ctf, doc_len);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	
		for(int j=1;j<=doc_count;j++)
		{
			result.add(new AbstractMap.SimpleEntry<String,Double>(index.getDocName(j),scores[j]));
			if (result.size()>k)
			{
				result.poll();
			}
		}
		ArrayList<Map.Entry<String, Double>> result_scores = new ArrayList<Map.Entry<String,Double>>();
		result_scores.addAll(result);
		result_scores.sort(Map.Entry.<String,Double>comparingByValue(Comparator.reverseOrder()));
		return result_scores;
	}
}
