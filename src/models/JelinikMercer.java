package models;

import index.Index;

public class JelinikMercer extends QueryLikelihood {
	private double lambda;
	
	public JelinikMercer(Index index, double lambda) 
	{
		this.index = index;
		this.lambda = lambda;
	}

	@Override
	public double scorer(double tf, double ctf, double docLen)
	{
		return Math.log((1 - lambda) * (tf*1.0/docLen) + lambda * ctf/index.getCollectionSize());	
	}	
}	

