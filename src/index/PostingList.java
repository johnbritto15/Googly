package index;

import java.util.ArrayList;
import java.util.List;

public class PostingList {
	
	List<Posting> postings;
	private int postingsIndex;
	
	public PostingList () 
	{
		postings = new ArrayList<Posting>();
		postingsIndex = -1;
	}

	public void startIteration () 
	{
		postingsIndex = 0;
	}
	
	public boolean hasMore() 
	
	{
		return (postingsIndex >= 0 && postingsIndex < postings.size());
	}
	
	public void skipTo (int docid)
	{
		while (postingsIndex < postings.size() &&
				getCurrentPosting().getDocId() < docid) 
		{
			postingsIndex++;
		}
	}
	
	public Posting getCurrentPosting() 
	{
		Posting retval = null;
		try 
		{
			retval = postings.get(postingsIndex);
		} 
		catch (IndexOutOfBoundsException ex) 
		{
			
		}
		return retval;
	}
	
	public Posting get(int index) 
	{
		return postings.get(index);
	}
	
	public int documentCount() 
	{
		return postings.size();
	}
	
	public void add(Posting posting) 
	{
		postings.add(posting);
		postingsIndex++;
	}
	
	public void add(Integer docid, Integer position) 
	{
		Posting current = getCurrentPosting();
		if (current != null && current.getDocId().equals(docid) ) 
		{
			current.add(position);
		} 
		else 
		{ 
			Posting posting = new Posting(docid, position);
			add(posting);
		}
	}
	
	public Integer[] toIntegerArray () 
	{
		ArrayList <Integer> retval = new ArrayList<Integer>();
		for (Posting p : postings) 
		{
			retval.addAll(p.toIntegerArray());
		}
		return retval.toArray(new Integer[retval.size()]);
		
	}
	
	public void fromIntegerArray(int[] input) 
	{
		int idx = 0;
		while (idx < input.length) 
		{
			int docid = input[idx++];
			int count = input[idx++];
			for (int j = 0; j < count; j++) 
			{
				int position = input[idx++];
				add(docid, position);
			}
		}
		postingsIndex = 0; // reset the list pointer
	}
	
	public int termFrequency() 
	{
		int freq = 0;
		for (Posting p : postings) 
		{
			freq += p.getTermFreq();
		}
		return freq;
		//return postings.stream().maptoInt(p -> p.getTermFreq()).sum();
	}
	
	public String toString() 
	{
		StringBuffer buf = new StringBuffer();
		int savedIdx = postingsIndex;
        startIteration();
        
        while (hasMore()) 
        {
        	Posting p = getCurrentPosting();
        	int doc = p.getDocId();
        	Integer [] positions = p.getPositionsArray();
        	buf.append("{").append(doc).append(", ");
        	buf.append(positions.length).append(" [");
        	
        	for (int i : positions) 
        	{
        		buf.append(i).append(" ");
        	}
        	buf.append(" ]} ");
        	skipTo(doc  + 1);
        }
        
        postingsIndex = savedIdx;
        return buf.toString();
	}
}
