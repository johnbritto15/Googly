package models;
import java.util.ArrayList;

public class MaxNode extends BeliefNode {
	Double score;
	
	public MaxNode(ArrayList<ProximityNode> c) 
	{
		super(c);
	}
	
	public Double score(Integer docId) 
	{
		this.score = -1.0 *  Double.MAX_VALUE;
		for (QueryNode child : children) 
		{
			this.score = Math.max(this.score, child.score(docId));
		}
		return this.score;
	}
}
