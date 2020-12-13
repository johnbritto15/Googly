package models;

import java.util.ArrayList;

import index.Index;

public class BandNode extends UnorderedWindow 
{
	public BandNode(ArrayList<? extends ProximityNode> termNodes, Index ind, QueryLikelihood mod)
	{
		super(0, termNodes, ind, mod);
	}
}
