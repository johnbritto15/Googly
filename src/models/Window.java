package models;

import java.util.ArrayList;

import index.Index;
import index.Posting;
import index.PostingList;

public abstract class Window extends ProximityNode{
	ArrayList<? extends ProximityNode> children;
	public Window (Index ind, QueryLikelihood mod)
	{
		super(ind, mod);
	}
	
	private Boolean allHaveMore()
	{
		return children.stream().allMatch(c -> c.hasMore());
	}
	
	private int candidate()
	{
		return children.stream().mapToInt(c -> c.nextCandidate()).max().getAsInt();
	}
	
	protected void generatePostings()
	{
		postingList = new PostingList();
		ArrayList<Posting> matchingPostings = new ArrayList<Posting>();
		//get inverted lists for all query terms
		
		while(allHaveMore())
		{
			Integer next=candidate();
			children.forEach(c -> c.skipTo(next));
			if(children.stream().allMatch(c -> next.equals(c.nextCandidate())))
			{
				for (ProximityNode child:children)
					matchingPostings.add(child.getCurrentPosting());
				Posting p = calculateWindows(matchingPostings);
				if(p!=null)
				{
					postingList.add(p);
					ctf+=p.getTermFreq();
				}
			}
			matchingPostings.clear();
			children.forEach(c -> c.skipTo(next+1));
		}
		postingList.startIteration();
	}
	
	abstract protected Posting calculateWindows(ArrayList<Posting> matchingPostings);
}
