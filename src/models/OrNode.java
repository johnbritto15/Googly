package models;

import java.util.ArrayList;

public class OrNode extends BeliefNode {
	Double score;
	
	public OrNode(ArrayList<ProximityNode> c) 
	{
		super(c);
	}

	public Double score(Integer docId) 
	{
		this.score = 0.0;
		for (QueryNode child : children) 
		{
			this.score += Math.log(1 - Math.exp(child.score(docId)));
		}
		return Math.log(1 - Math.exp(this.score));
	}
}
