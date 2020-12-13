package models;

import index.Index;
import index.Posting;
import index.PostingList;

public abstract class ProximityNode implements QueryNode {
	protected int ctf=0;
	protected PostingList postingList = null;
	protected Index index;
	protected QueryLikelihood model;
	
	public ProximityNode(Index ind, QueryLikelihood mod)
	{
		index = ind;
		this.model = mod;
	}
	
	protected abstract void generatePostings();
	protected Posting getCurrentPosting()
	{
		return postingList.getCurrentPosting();
	}
	
	public Integer nextCandidate()
	{
		if(postingList.hasMore())
		{
			return postingList.getCurrentPosting().getDocId();
		}
		return index.getDocCount()+1;
	}
	
	public Double score(Integer docId)
	{
		int tf=0;
		if(postingList.hasMore() && postingList.getCurrentPosting().getDocId().equals(docId))
		{
			tf=postingList.getCurrentPosting().getTermFreq();
		}
		return model.scorer(tf, ctf, index.getDocLength(docId));
	}
	public void skipTo(int docId)
	{
		postingList.skipTo(docId);
	}
	public boolean hasMore()
	{
		//System.out.println(postingList.hasMore()+"1");
		return postingList.hasMore();
	}
	protected PostingList getPostings() {
		return postingList;
	}

}
