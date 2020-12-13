package models;
import java.util.ArrayList;

public class SumNode extends BeliefNode {
	Double score;
	
	public SumNode(ArrayList<ProximityNode> c) 
	{
		super(c);
	}
	
	public Double score(Integer docId) 
	{
		this.score=0.0;
		for (QueryNode child : children) 
		{
			this.score += Math.exp(child.score(docId));
		}
		return Math.log(this.score/children.size());
	}
}
