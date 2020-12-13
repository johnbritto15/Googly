package models;

import java.util.*;
import java.util.Map.Entry;

import index.Index;
import index.Posting;
import index.PostingList;

public class VectorSpace {
	private Index index;
	public VectorSpace (Index index)
	{
		this.index = index;
	}
	public ArrayList<Entry<String, Double>> retrieveQuery(String query, int k) 
	{
		// TODO Auto-generated method stub
		PriorityQueue<Map.Entry<String,Double>> result = new PriorityQueue<>(Map.Entry.<String,Double>comparingByValue());
		String[] queryTerms = query.split("\\s+");
		Map<String, Integer> query_counts = new HashMap<String, Integer>(); //tracks no. of times a query term occurs
		List<String> terms = new ArrayList<String>();
		for(String term:queryTerms)
		{
			if(!query_counts.containsKey(term)) 
			{
				query_counts.put(term, 1);
				terms.add(term);
			}
			else
			{
				query_counts.put(term, query_counts.get(term)+1);
			}
		}
		
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
			for(String term:terms)
			{
				double qtf = 1+ Math.log(query_counts.get(term));
				//System.out.println(term);
				PostingList list = index.getPostings(term);
				double t_idf = Math.log(1.0*doc_count/list.documentCount());
				double qt_weight = qtf*t_idf;
				//only those docs that have the term
				while(list.hasMore())
				{
					Posting a = list.getCurrentPosting();
					if(a!=null)
					{
						double dtf=1 + Math.log(a.getTermFreq());
						//System.out.println(a.getTermFreq());
						double dt_weight  = dtf*t_idf;
						scores[a.getDocId()]+= qt_weight*dt_weight;
						//System.out.println(scores[a.getDocId()]);
						list.skipTo(a.getDocId()+ 1);
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		for(int j=1;j<=doc_count;j++)
		{
			scores[j]=scores[j]/lengths[j];
			result.add(new AbstractMap.SimpleEntry<String,Double>(index.getDocName(j),scores[j]));
			if (result.size()>k)
			{
				result.poll();
			}
		}
		ArrayList<Map.Entry<String, Double>> result_scores = new ArrayList<Map.Entry<String,Double>>();
		result_scores.addAll(result);
		result_scores.sort(Map.Entry.<String,Double>comparingByValue(Comparator.reverseOrder()));
		//System.out.println(Arrays.toString(result_scores.toArray()));
		return result_scores;
	}

}
