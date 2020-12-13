package models;

import index.Index;

public class TermNode extends ProximityNode{
	private String term;
	public TermNode(String t, Index ind, QueryLikelihood mod)
	{
		super(ind, mod);
		term = t;
		generatePostings();
	}
	
	protected void generatePostings()
	{
		postingList = index.getPostings(term);
		ctf = index.getTermFreq(term);
	}
}
