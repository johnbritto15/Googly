package models;
public abstract class FilterOperator implements QueryNode {
	protected QueryNode query = null;
	protected ProximityNode filter;
	
	public FilterOperator(ProximityNode proximityExp, QueryNode q)
	{
		filter = proximityExp;
		query = q;
	}
	
	public boolean hasMore()
	{
		return query.hasMore();
	}
	
	public void skipTo(int docId)
	{
		filter.skipTo(docId);
		query.skipTo(docId);
	}
}
