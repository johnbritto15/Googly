package driver;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map.Entry;

import index.Index;
import index.InvertedIndex;
import models.AndNode;
import models.Dirichlet;
import models.InferenceNetwork;
import models.PriorNode;
import models.ProximityNode;
import models.QueryLikelihood;
import models.QueryNode;
import models.TermNode;

public class TestPrior {

	public static void main(String[] args) {
		try
		{
			Index index = new InvertedIndex();
			index.load(false);
			InferenceNetwork network = new InferenceNetwork();
			QueryLikelihood model = new Dirichlet(index, 1500);
	
	        //ArrayList<String> id = index.getBackingDocumentIDs();
	
	        // there is only one query to run
	        String query = "the king queen royalty";
	        String priorFileName = "uniform.prior";
	        PriorNode priorNode = new PriorNode(index, priorFileName);
	
	        ArrayList<QueryNode> children = new ArrayList<QueryNode>();
	
	        // first add the Prior-node to the list of children
	        children.add(priorNode);
	
	        // then add the TermProximityNodes for each term in the query
	        children.addAll(
	                getTermNodesFromQuery(query, model, index));
	
	        // "AND" is the combine operator
	        AndNode andNode = new AndNode(children);
	        Integer rank = 1;
	        String runTag = "jnadar-infnet-and-ql-dir-mu=1500-" + priorFileName;
	
	        PrintWriter pWriter = new PrintWriter(new File("uniform.trecrun"));
	
	        // run the query and fetch the top-10 results
	        for (Entry<Integer, Double> entry : network.runQuery(andNode, 10)) {
	            String sceneId = index.getDocName(entry.getKey());
	            String toWriteString = ("Q1  "
	                    + "  skip  " + spaceWriter(40, sceneId)
	                    + spaceWriter(10,rank.toString())
	                    + spaceWriter(25, entry.getValue().toString())
	                    + spaceWriter(20,runTag) + "\n");
	            pWriter.write(toWriteString);
	            rank++;
	        }
	
	        pWriter.close();
	        
	        priorFileName = "random.prior";
	        priorNode = new PriorNode(index, priorFileName);
	
	        children = new ArrayList<QueryNode>();
	
	        // first add the Prior-node to the list of children
	        children.add(priorNode);
	
	        // then add the TermProximityNodes for each term in the query
	        children.addAll(
	                getTermNodesFromQuery(query, model, index));
	
	        // "AND" is the combine operator
	        andNode = new AndNode(children);
	        rank = 1;
	        runTag = "jnadar-infnet-and-ql-dir-mu=1500-" + priorFileName;
	
	        pWriter = new PrintWriter(new File("random.trecrun"));
	
	        // run the query and fetch the top-10 results
	        for (Entry<Integer, Double> entry : network.runQuery(andNode, 10)) {
	            String sceneId = index.getDocName(entry.getKey());
	            String toWriteString = ("Q1  "
	                    + "  skip  " + spaceWriter(40, sceneId)
	                    + spaceWriter(10,rank.toString())
	                    + spaceWriter(25, entry.getValue().toString())
	                    + spaceWriter(20,runTag) + "\n");
	            pWriter.write(toWriteString);
	            rank++;
	        }
	
	        pWriter.close();
		}
		catch (Exception e)
		{
			
		}
	}
	
	public static String spaceWriter(int space, String string) 
	{
		return String.format("%" + (-space) + "s", string);
	}
	public static ArrayList<TermNode> getTermNodesFromQuery(String query,
            QueryLikelihood model, Index index) {

        ArrayList<TermNode> list = new ArrayList<TermNode>();
        String[] terms = query.split("\\s+");

        for (String term : terms) {
            TermNode node = new TermNode(term, index, model);
            list.add(node);
        }

        return list;
    }
}

