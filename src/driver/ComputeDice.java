package driver;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import index.Index;
import index.InvertedIndex;
import index.Posting;
import index.PostingList;

public class ComputeDice {
	Index index;
	public static void main(String[] args)
	{
		try
		{
			ComputeDice me = new ComputeDice();
			String inputFile = args[0];
			me.index = new InvertedIndex();
			me.index.load(Boolean.parseBoolean(args[1]));
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			Set <String> vocabulary = me.index.getVocabulary();
			PrintWriter diceWriter = new PrintWriter("diced-queries.txt","UTF-8");
			String query;
			int count=0;
			while ((query = br.readLine())!=null)
			{
				String queryTerms[] = query.split("\\s+");
				List<String> addedTerms = new ArrayList<String>();
				String bestTerm="";
				for (int i=0;i<queryTerms.length;i++)
				{
					double best=0;
					for (String term:vocabulary)
					{
						double dice = me.computeDice(queryTerms[i],term);
						if (dice>best)
						{
							best = dice;
							bestTerm = term;
						}
					}
					//addedTerms.add(bestTerm);
					diceWriter.print(queryTerms[i]+" "+bestTerm+" ");
					System.out.println(bestTerm+":"+ ++count);
				}
				diceWriter.println();
			}
			br.close();
			diceWriter.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	double computeDice(String termA, String termB)
	{
		PostingList listA=index.getPostings(termA);
		PostingList listB=index.getPostings(termB);
		int nA = index.getTermFreq(termA);
		int nB = index.getTermFreq(termB);
		double nAB = 0.0;
		while(listA.hasMore()) //intersect docs
		{
			Posting a = listA.getCurrentPosting();
			listB.skipTo(a.getDocId());
			//breaks if listB.hasMore() is false
			Posting b = listB.getCurrentPosting();
			if(b!=null&&b.getDocId().equals(a.getDocId()))
			{
				//count matches
				Integer [] aPos = a.getPositionsArray();
				Integer [] bPos = b.getPositionsArray();
				for (int aIdx=0;aIdx<aPos.length;aIdx++)
				{
					for (int bIdx=0;bIdx<bPos.length;bIdx++)
					{
						if(bPos[bIdx].equals((aPos[aIdx]+1)))
						{
							nAB++;
						}
					}
				}
			}
			listA.skipTo(a.getDocId()+1);
		}
		return nAB/(nA+nB);
	}
}
