/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver;

import clustering.Cluster;
import clustering.CosineSimilarity;
import clustering.Linkage;
import clustering.SimilarityMethod;
import index.Index;
import index.InvertedIndex;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class OnlineCluster {
    
    public static void main(String[] args)
    {
        Index index = new InvertedIndex();
        index.load(false);
        Linkage linkage = Linkage.valueOf(args[0].toUpperCase());
        boolean append = false;
        for (int t = 5; t< 100; t+= 5)
        {
        	double threshold = (double) t / 100.0;
            Map <Integer, Cluster> clusters = new HashMap<Integer, Cluster>();
            int clusterId = 0;
            SimilarityMethod sim = new CosineSimilarity(index);
            int limit = index.getDocCount();
            for( int docId = 1; docId <= limit; docId++)
            {
                double score = 0.0;
                int best = -1;
                for( Cluster c: clusters.values())
                {
                    int cId = c.getId();
                    double s = c.score(index.getDocumentVector(docId));
                    if(s > score)
                    {
                        score = s;
                        best = cId;
                    }
                }
                if( score > threshold)
                {
                    clusters.get(best).add(docId);
                }
                else
                {
                    clusterId++;
                    Cluster clust = new Cluster(clusterId, index, linkage, sim);
                    clust.add(docId);
                    clusters.put(clusterId, clust);
                }
            }
            try
            {
                String outfile1 = "cluster-"+threshold+".out";
                String outfile2 = "sizes.txt";
                PrintWriter writer1 = new PrintWriter(new FileWriter(outfile1));
                PrintWriter writer2 = new PrintWriter(new FileWriter(outfile2, append));
                append = true;
                writer2.println("Threshold = "+threshold+";");
                clusters.keySet().stream().sorted().forEach((cId) ->{
                    Cluster c = clusters.get(cId);
                    c.getDocumentIds().forEach((dId) -> 
                        writer1.println(c.getId() + "\t\t\t"+ index.getDocName(dId)));
                    writer2.println("Cluster ID = "+c.getId()+"; Size = "+c.getSize()+";");
                });

                writer1.close();
                writer2.close();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
