package clustering;

import java.util.Map;

public interface SimilarityMethod {
	public double similarity(Map<String, Double> docVec, Map<String, Double> other);
}
