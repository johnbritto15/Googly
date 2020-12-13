package models;

import java.util.ArrayList;

public class NotNode extends BeliefNode{
	double score;
	public NotNode(ArrayList<QueryNode> c) 
	{
		super(c);
	}
	
	public Double score(Integer docId) 
	{
		this.score = Math.log(1 - Math.exp(children.get(0).score(docId)));
		return this.score;
	}
}
