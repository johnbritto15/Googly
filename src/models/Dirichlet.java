package models;

import index.Index;

public class Dirichlet extends QueryLikelihood {
	private double mu;
	
	public Dirichlet(Index index, double mu) 
	{
		this.index = index;
		this.mu = mu;
	}

	@Override
	public double scorer(double tf, double ctf, double docLen) 
	{
		return Math.log((tf + mu * ctf/index.getCollectionSize())/(mu + docLen));	
	}	
}	
