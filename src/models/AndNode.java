package models;

import java.util.ArrayList;

public class AndNode extends BeliefNode
{
	public AndNode(ArrayList<? extends QueryNode> c)
	{
		super(c);
	}
	
	public Double score(Integer docId)
	{
		//return children.stream().mapToDouble(c -> c.score(docId)).sum();
		double score = 0.0;

        for (QueryNode child : children) {
            // add up the score in log-space
            score += child.score(docId);
        }
        // return the score which is already in log-space
        return score;
	}
}
