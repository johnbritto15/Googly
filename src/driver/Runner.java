package driver;

import java.util.*;
import java.util.Map.Entry;
import java.io.*;
import java.lang.*;
import index.*;
import models.*;

public class Runner {

	public static String spaceWriter(int space, String string) 
	{
		return String.format("%" + (-space) + "s", string);
	}
	public static void main(String[] args)
	// TODO Auto-generated method stub
	{
		try {
			Index index = new InvertedIndex();
			int k = Integer.parseInt(args[0]);
			index.load(false);
			//List <String> queries = Arrays.asList("the king queen royalty", "servant guard soldier", "hope dream sleep","ghost spirit","fool jester player","to be or not to be","alas","alas poor","alas poor yorick","antony strumpet");
			List <String> queries = Arrays.asList("i knew him");
			//List <String> queries = Arrays.asList("hope dream sleep");
			List<Entry<String, Double>> results;
			PrintWriter outputWriter;
			
			//VS
			String runid = "jnadar-vs-tf-idf";
			String outputFile = "vs.trecrun";
			VectorSpace vs = new VectorSpace(index);
			outputWriter = new PrintWriter(outputFile);
			int qNum = 1;
			for (String query: queries) {
				results = vs.retrieveQuery(query, k);
				int rank = 1;
				for (Map.Entry<String, Double> result: results) {
					outputWriter.println("Q" + spaceWriter(8,String.valueOf(qNum)) + " skip " + spaceWriter(30,result.getKey()) +
					" " + spaceWriter(8,String.valueOf(rank)) + " " + spaceWriter(20,String.valueOf(result.getValue())) + " " + runid);
					rank++;
				}
				outputWriter.println();
				qNum++;
			}
			outputWriter.close();
			
			//BM25
			runid = "jnadar-bm25-1.5-500-0.75";
			outputFile = "bm25.trecrun";
			BM25 bm25 = new BM25(index, 0.75,1.5,500);
			qNum = 1;
			outputWriter = new PrintWriter(outputFile);
			for (String query: queries) {
				results = bm25.retrieveQuery(query, k);
				int rank = 1;
				for (Map.Entry<String, Double> result: results) {
					outputWriter.println("Q" + spaceWriter(8,String.valueOf(qNum)) + " skip " + spaceWriter(30,result.getKey()) +
					" " + spaceWriter(8,String.valueOf(rank)) + " " + spaceWriter(20,String.valueOf(result.getValue())) + " " + runid);
					rank++;
				}
				outputWriter.println();
				qNum++;
			}
			outputWriter.close();
			
			//JM
			runid = "jnadar-jm-0.2";
			outputFile = "ql-jm.trecrun";
			QueryLikelihood model = new JelinikMercer(index, 0.2);
			qNum = 1;
			outputWriter = new PrintWriter(outputFile);
			for (String query: queries) {
				results = model.retrieveQuery(query, k);
				int rank = 1;
				for (Map.Entry<String, Double> result: results) {
					outputWriter.println("Q" + spaceWriter(8,String.valueOf(qNum)) + " skip " + spaceWriter(30,result.getKey()) +
					" " + spaceWriter(8,String.valueOf(rank)) + " " + spaceWriter(20,String.valueOf(result.getValue())) + " " + runid);
					rank++;
				}
				outputWriter.println();
				qNum++;
			}
			outputWriter.close();
			
			//Dir
			runid = "jnadar-dir-1200";
			outputFile = "ql-dir.trecrun";
			model = new Dirichlet(index, 1200.0);
			qNum = 1;
			outputWriter = new PrintWriter(outputFile);
			for (String query: queries) {
				results = model.retrieveQuery(query, k);
				int rank = 1;
				for (Map.Entry<String, Double> result: results) {
					outputWriter.println("Q" + spaceWriter(8,String.valueOf(qNum)) + " skip " + spaceWriter(30,result.getKey()) +
					" " + spaceWriter(8,String.valueOf(rank)) + " " + spaceWriter(20,String.valueOf(result.getValue())) + " " + runid);
					rank++;
				}
				outputWriter.println();
				qNum++;
			}
			outputWriter.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			// ignore
		}
	}
}



