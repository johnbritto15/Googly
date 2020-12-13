package driver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import index.Index;
import index.InvertedIndex;
import models.QueryLikelihood;
import models.AndNode;
import models.Dirichlet;
import models.InferenceNetwork;
import models.MaxNode;
import models.OrNode;
import models.OrderedWindow;
import models.ProximityNode;
import models.QueryNode;
import models.QueryLikelihood;
import models.SumNode;
import models.TermNode;
import models.UnorderedWindow;
/*
 * *
Q1: the king queen royalty
Q2: servant guard soldier
Q3: hope dream sleep
Q4: ghost spirit
Q5: fool jester player
Q6: to be or not to be
Q7: alas
Q8: alas poor
Q9: alas poor yorick
Q10: antony strumpet

Please run these queries using the two phrase operators, ordered window and unordered window.
For ordered, use a distance of 1 (exact phrase), for unordered, use a window width 
3*|Q| (three times the length of the query). Please run these queries with each of the 
operators: SUM, AND, OR, and MAX. Use dirichlet smoothing with μ=1500 for all runs.
 */
public class TestInferenceNetwork {

	public static void main(String[] args) 
	{
		int k = Integer.parseInt(args[0]);
		
		List <String> queries = Arrays.asList("the king queen royalty", "servant guard soldier", "hope dream sleep","ghost spirit","fool jester player","to be or not to be","alas","alas poor","alas poor yorick","antony strumpet");
		//List <String> queries = Arrays.asList("alas poor or alas poor");
		Index index = new InvertedIndex();
		index.load(false);

		QueryLikelihood model = new Dirichlet(index, 1500);
		List<Map.Entry<Integer, Double>> results;
		InferenceNetwork network = new InferenceNetwork();
		QueryNode queryNode;
		ArrayList<ProximityNode> children;
		
		String outfile, runId, qId;
		int qNum = 0;
		
		outfile = "od1.trecrun";
		runId = "jnadar-od1-dir-1500";
		for (String query : queries) 
		{
			qNum++;
			// make each of the required query nodes and run the queries
			children = genTermNodes(query, index, model);
			queryNode = new OrderedWindow(1, children, index, model);
			results = network.runQuery(queryNode, k);
			qId = "Q" + qNum;
			boolean append = qNum > 1;
			try 
			{
				PrintWriter writer = new PrintWriter(new FileWriter(outfile, append));
				printResults(results, index, writer, runId, qId);
				writer.close();
			} 
			
			catch (IOException ex) 
			{
				ex.printStackTrace();
			}
		}
		
		outfile = "uw.trecrun";
		runId = "jnadar-uw-dir-1500";
		qNum = 0;
		for (String query : queries) 
		{
			qNum++;
			children = genTermNodes(query, index, model);
			int winSize = 3 * children.size();
			queryNode = new UnorderedWindow(winSize, children, index, model);
			results = network.runQuery(queryNode, k);
			qId = "Q" + qNum;
			boolean append = qNum > 1;
			try 
			{
				PrintWriter writer = new PrintWriter(new FileWriter(outfile, append));
				printResults(results, index, writer, runId, qId);
				writer.close();

			} 
			
			catch (IOException ex) 
			{
				ex.printStackTrace();
			}
		}

		outfile = "sum.trecrun";
		runId = "jnadar-sum-dir-1500";
		qNum = 0;
		for (String query : queries) 
		{
			qNum++;
			// make each of the required query nodes and run the queries
			children = genTermNodes(query, index, model);
			queryNode = new SumNode(children);
			results = network.runQuery(queryNode, k);
			qId = "Q" + qNum;
			boolean append = qNum > 1;
			try 
			{
				PrintWriter writer = new PrintWriter(new FileWriter(outfile, append));
				printResults(results, index, writer, runId, qId);
				writer.close();

			} 
			catch (IOException ex) 
			{
				ex.printStackTrace();
			}
		}

		// and
		outfile = "and.trecrun";
		runId = "jnadar-and-dir-1500";
		qNum = 0;
		for (String query : queries) 
		{
			qNum++;
			children = genTermNodes(query, index, model);
			queryNode = new AndNode(children);
			results = network.runQuery(queryNode, k);
			qId = "Q" + qNum;
			boolean append = qNum > 1;
			try 
			{
				PrintWriter writer = new PrintWriter(new FileWriter(outfile, append));
				printResults(results, index, writer, runId, qId);
				writer.close();

			} 
			catch (IOException ex) 
			{
				ex.printStackTrace();
			}
		}

		// or
		outfile = "or.trecrun";
		runId = "jnadar-or-dir-1500";
		qNum = 0;
		for (String query : queries) 
		{
			qNum++;
			children = genTermNodes(query, index, model);
			queryNode = new OrNode(children);
			results = network.runQuery(queryNode, k);
			qId = "Q" + qNum;
			boolean append = qNum > 1;
			try 
			{
				PrintWriter writer = new PrintWriter(new FileWriter(outfile, append));
				printResults(results, index, writer, runId, qId);
				writer.close();
			} 
			catch (IOException ex) 
			{
				ex.printStackTrace();
			}
		}

		// max
		outfile = "max.trecrun";
		runId = "jnadar-max-dir-1500";
		qNum = 0;
		for (String query : queries) 
		{
			qNum++;
			children = genTermNodes(query, index, model);
			queryNode = new MaxNode(children);
			results = network.runQuery(queryNode, k);
			qId = "Q" + qNum;
			boolean append = qNum > 1;
			try 
			{
				PrintWriter writer = new PrintWriter(new FileWriter(outfile, append));
				printResults(results, index, writer, runId, qId);
				writer.close();

			} 
			catch (IOException ex) 
			{
				ex.printStackTrace();
			}
		}

	}

	private static void printResults(List<Entry<Integer, Double>> results, 
			Index index, PrintWriter writer, String runId, String qId) 
	{
		int rank = 1;
		if (results.size()==0)
			return;
		for (Map.Entry<Integer, Double> entry : results) 
		{
			String sceneId = index.getDocName(entry.getKey());
			writer.println(qId + " skip " + sceneId + " \t \t" + rank + " " + entry.getValue() + " nikita-od1-distance1-"
					+ rank + "-top-documents");
			rank++;
		}
		writer.println();
	}
	
	private static ArrayList<ProximityNode> genTermNodes(String query, Index index, QueryLikelihood model) 
	{
		String [] terms = query.split("\\s+");
		ArrayList<ProximityNode> children = new ArrayList<ProximityNode>();
		for (String term : terms) 
		{
			ProximityNode node = new TermNode(term, index, model);
			children.add(node);
		}
		return children;
	}
	
	public static String spaceWriter(int space, String string) 
	{
		return String.format("%" + (-space) + "s", string);
	}
}
