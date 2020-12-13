package models;
public interface QueryNode {

	Integer nextCandidate();

	Double score(Integer docId);

	boolean hasMore();

	void skipTo(int docId);

}
