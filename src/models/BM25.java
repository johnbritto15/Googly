package models;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Map.Entry;

import index.Index;
import index.Posting;
import index.PostingList;

public class BM25 {
	private Index index;
	private double b,k1,k2;
	
	public BM25(Index index, double b, double k1, double k2) 
	{
		this.index = index;
		this.b = b;
		this.k1 = k1;
		this.k2 = k2;
	}
	
    public ArrayList<Entry<String, Double>> retrieveQuery(String query, int k) 
    {	
    	// TODO Auto-generated method stub
		PriorityQueue<Map.Entry<String, Double>> result = new PriorityQueue<>(Map.Entry.<String, Double>comparingByValue());
		String [] queryTerms = query.split("\\s+");
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
				double qtf = query_counts.get(term);
				PostingList list = index.getPostings(term);
				double tdf = index.getDocFreq(term);
				//only those docs that have the term
				int count=0;
				while(list.hasMore())
				{
					Posting a = list.getCurrentPosting();
					if(a!=null)
					{
						double tf=a.getTermFreq();
						double doc_len = lengths[a.getDocId()];
						double K = k1 * ((1-b) + b*(doc_len/index.getAverageDocLength()));
						double idf = Math.log((index.getDocCount() -tdf + 0.5)/(tdf + 0.5)); 
						double doc = ((k1 + 1) * tf)/(K + tf);
						double q = (1.0*(k2 + 1) * qtf)/(k2 + qtf);
						count+=tf;
						//System.out.println(term+"\t"+a.getDocId()+"\t"+K+"\t"+idf+"\t"+doc+"\t"+q);
						System.out.println(term+"\t"+idf+"\t"+K+"\t"+doc_len+"\t"+q+"\t"+tdf+"\t");
						scores[a.getDocId()]+= idf * doc * q;
						list.skipTo(a.getDocId()+ 1);
					}			
				}
				System.out.println(count);	
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
		//System.out.println(Arrays.toString(result_scores.toArray()));
		return result_scores;
    }
	
}
