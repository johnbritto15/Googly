package models;

import java.util.ArrayList;

public class WsumNode extends BeliefNode {

	ArrayList<Double> weights;
	Double score, sum_of_weights;
	
	public WsumNode(ArrayList<QueryNode> c, ArrayList<Double> weights) 
	{
		super(c);
		this.weights = weights;
	}

	public Double score(Integer docId) 
	{
		this.score = 0.0;
		this.sum_of_weights = 0.0;
		for (int i = 0; i < children.size(); i++)
		{
			this.score += this.weights.get(i) * Math.exp(children.get(i).score(docId));
			this.sum_of_weights += this.weights.get(i);
		}
		return Math.log(this.score/this.sum_of_weights);
	}
}
