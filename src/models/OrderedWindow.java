package models;

import java.util.ArrayList;
import java.util.Arrays;

import index.Index;
import index.Posting;

public class OrderedWindow extends Window{
	private Integer distance;

	public OrderedWindow(int distance, ArrayList<? extends ProximityNode> children, Index ind, QueryLikelihood mod) 
	{
		// TODO Auto-generated constructor stub
		super(ind,mod);
		this.children = children;
		this.distance = distance;
		generatePostings();
	}

	protected Posting calculateWindows(ArrayList<Posting> postings) 
	{
		int prev_pos, cur_pos;
		Posting post = null; 
		Integer[] pn;
		boolean flag = false;
		ArrayList<Integer> positions;
		if (postings.size() > 1) 
		{
			pn = postings.get(0).getPositionsArray();
			for(int i = 0; i < pn.length; i++)
			{
				prev_pos = pn[i];
				for (int j = 1; j < postings.size(); j++)
				{
					positions = new ArrayList<>(Arrays.asList(postings.get(j).getPositionsArray()));
					flag = false;
					for (int k = 0; k < positions.size(); k++) 
					{
						cur_pos = positions.get(k);
						if (prev_pos < cur_pos && (prev_pos + distance) >= cur_pos) 
						{
							prev_pos = cur_pos;
							flag = true;
							break;
						}
					}
					
					if (!flag) 
						break;
				}
				
				if (flag) 
				{
					if(post != null)
						post.add(pn[i]);
					else
						post = new Posting(postings.get(0).getDocId(), pn[i]);
				}
			}
			return post;
		}
		else 						//single posting only
			return postings.get(0);
		
	}

}
