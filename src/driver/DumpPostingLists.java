package driver;
import java.util.SortedSet;
import java.util.TreeSet;

import index.Index;
import index.InvertedIndex;
import index.PostingList;

public class DumpPostingLists {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Index index = new InvertedIndex();
		index.load(Boolean.parseBoolean(args[0]));
		SortedSet<String> vocabulary = new TreeSet<String>(index.getVocabulary());
		for (String term:vocabulary)
		{
			System.out.print(term);
			PostingList list = index.getPostings(term);
			//System.out.println(term +" -> " +list.toString());
		}
	}

}
