package models;

public class FilterReject extends FilterOperator
{
	public FilterReject(ProximityNode proximityExp, QueryNode q)
	{
		super(proximityExp, q);
	}
	
	public Integer nextCandidate()
	{
		return query.nextCandidate();
	}
	
	public Double score(Integer docId)
	{
		if(!docId.equals(filter.nextCandidate()))
			return query.score(docId);
		else
			return null;
	}
}
