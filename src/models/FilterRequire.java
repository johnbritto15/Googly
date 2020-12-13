package models;

public class FilterRequire extends FilterOperator{
	public FilterRequire(ProximityNode proximityExp, QueryNode q)
	{
		super(proximityExp, q);
	}
	
	public Integer nextCandidate()
	{
		return Math.max(filter.nextCandidate(), query.nextCandidate());
	}
	
	public Double score(Integer docId)
	{
		if(docId.equals(filter.nextCandidate()))
			return query.score(docId);
		else
			return null;
	}
}
