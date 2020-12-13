package models;

import java.util.ArrayList;

public class WandNode extends BeliefNode{

	ArrayList<Double> weights;
	Double score;
	
	public WandNode(ArrayList<QueryNode> c, ArrayList<Double> weights) 
	{
		super(c);
		this.weights = weights;
	}
	
	public Double score(Integer docId) 
	{
		this.score = 0.0;
		for (int i = 0; i < children.size(); i++)
		{
			QueryNode child = children.get(i);
			this.score += this.weights.get(i) * child.score(docId);
		}
		return this.score;
	}
}
