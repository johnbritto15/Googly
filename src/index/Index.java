package index;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public interface Index {

	void load(boolean parseBoolean);

	Set<String> getVocabulary();

	int getTermFreq(String term);

	int getDocFreq(String term);

	PostingList getPostings(String term);
	
	double getAverageDocLength();
	
	long getCollectionSize();
	
	int getDocCount();
	
	int getDocLength(int docId);
	
	public String getDocName(int docId);
	
	public String getPlay(int docId);
 
    public String getScene(int docId);

	List<Entry<Integer, Double>> retrieveQuery(String query, int k);
	
	public Entry<Integer, Integer> getShortestScene();
	
	public Map<String, Double> getDocumentVector(int docId);

	Double getPriorForDocument(Integer docId, String priorLookupFile);

}
