package driver;
import java.util.SortedSet;
import java.util.TreeSet;

import index.Index;
import index.InvertedIndex;

public class DumpVocabulary {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Index index = new InvertedIndex();
		index.load(Boolean.parseBoolean(args[0]));
		//index.load(false);
		SortedSet<String> vocabulary = new TreeSet<String>(index.getVocabulary());
		for (String term:vocabulary)
		{
			
			int freq = index.getTermFreq(term);
			int docFreq = index.getDocFreq(term);
			System.out.println(term+" "+freq+" "+docFreq);
		}
	}

}
