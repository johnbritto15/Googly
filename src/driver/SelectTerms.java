package driver;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import index.Index;
import index.InvertedIndex;

public class SelectTerms {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			Index index = new InvertedIndex();
			index.load(Boolean.parseBoolean(args[0]));
			int numTerms = Integer.parseInt(args[1]);
			//index.load(false);
			//int numTerms=7;
			Set<String> vocabulary = index.getVocabulary();
			ArrayList<String> words = new ArrayList<String>();
			words.addAll(vocabulary);
			String fileName=null;
			if (numTerms==7)
			{
				fileName = "7queryterms.txt";
			}
			else
			{
				fileName="14queryterms.txt";
			}
			PrintWriter queryWriter = new PrintWriter(fileName,"UTF-8");
			Random rand = new Random(System.currentTimeMillis());
			for (int i=0;i<100;i++)
			{
				Set<Integer> indexes = new HashSet<Integer>();
				while(indexes.size()<numTerms)
				{
					int idx=rand.nextInt(words.size()-1);
					indexes.add(idx);
				}
				String result="";
				for(int idx: indexes)
				{
					result+=words.get(idx);
					result+=" ";
				}
				result=result.trim();
				queryWriter.println(result);
			}
			queryWriter.close();
		}
		catch(Exception ex)
		{
			
		}
	
	}

}
