package driver;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import index.Index;
import index.InvertedIndex;

public class TimeQueries {
	public static void main(String [] args)
	{
		try
		{
			Index index=new InvertedIndex();
			boolean compressed = Boolean.parseBoolean(args[1]);
			index.load(compressed);
			int k = Integer.parseInt(args[0]);
			List<Map.Entry<Integer,Double>> results;
			Instant start, end;
			String inputFile = args[2];
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			String query;
			while ((query = br.readLine())!=null)
			{
				results = index.retrieveQuery(query,k);
			}
			br.close();
			br = new BufferedReader(new FileReader(inputFile));
			start = Instant.now();
			while ((query = br.readLine())!=null)
			{
				results = index.retrieveQuery(query,k);
			}
			end = Instant.now();
			br.close();
			System.out.println(" Seven word queries "+ compressed + " took " + Duration.between(start, end));
			inputFile = args[3];
			br = new BufferedReader(new FileReader(inputFile));
			while ((query = br.readLine())!=null)
			{
				results = index.retrieveQuery(query,k);
			}
			br.close();
			br = new BufferedReader(new FileReader(inputFile));
			start = Instant.now();
			while ((query = br.readLine())!=null)
			{
				results = index.retrieveQuery(query,k);
			}
			end = Instant.now();
			br.close();
			System.out.println(" Fourteen word queries "+ compressed + " took " + Duration.between(start, end));
		}
		catch (Exception e)
		{
			
		}
	}
}
