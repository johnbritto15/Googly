package index;

import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import utilities.Compression;
import utilities.CompressionFactory;
import utilities.Compressors;

public class InvertedIndex implements Index {
	private Map<Integer, String> docIdtosceneIdMap = new HashMap<Integer, String>();
	private Map<Integer, String> playIdMap = new HashMap<Integer, String>();
	//private Map<String, PostingList> invertedLists;
	private Map<Integer, Integer>docLengths = new HashMap<Integer, Integer>();
	private Map<String, RetrievedIndex> retrievedIndex= new HashMap<String, RetrievedIndex>(); 
	private Map<Integer, Map<String, Double>> documentVectors = new HashMap<Integer, Map<String, Double>>();
	private Compressors compression;
	private String invFile;
	private String priorFile = null;
	private DocStats docStats = new DocStats(0,0,0);
	@Override
	public void load(boolean compress) {
		// TODO Auto-generated method stub
		this.compression = compress ? Compressors.VBYTE : Compressors.EMPTY;
    	invFile = compress ? "invListCompressed" : "invList";
		loadStringMap("sceneId.txt", docIdtosceneIdMap);
        loadStringMap("playIds.txt", playIdMap);
        loadDocLengths("docLength.txt");
        loadInvertedLists("lookup.txt");
        loadDocumentVectors("documentVectors.json");
	}

	private void loadInvertedLists(String string) 
	{
		// TODO Auto-generated method stub
		 BufferedReader br = null;
	        
	        try
	        {
	            
	            //create file object
	            File file = new File(string);
	            
	            //create BufferedReader object from the File
	            br = new BufferedReader( new FileReader(file) );
	            
	            String line = null;
	            
	            //read file line by line
	            while ( (line = br.readLine()) != null )
	            {
	                
	                //split the line by :
	                String[] entry = line.split("\\s+");
	                String term = entry[0];
	                long offset = Long.parseLong(entry[1]);
	                long bytesWritten = Long.parseLong(entry[2]);
	                int docTermFreq = Integer.parseInt(entry[3]);
	                int collectionTermFreq = Integer.parseInt(entry[4]);
	                retrievedIndex.put(term, new RetrievedIndex(offset, bytesWritten, docTermFreq, collectionTermFreq));

	            }
	            br.close();
	        }   

	        catch(IOException e) 
	        {
	            e.printStackTrace();
	        }

	}
	

    public void loadDocumentVectors(String fileName){
        JSONParser jsonParser = new JSONParser();
        try{
            FileReader reader = new FileReader(fileName);
            Object obj = jsonParser.parse(reader);
            JSONArray docVecs = (JSONArray) obj;
            for(Object docVec: docVecs){
                JSONObject vec = (JSONObject) docVec;
                int docId = (int)(long) vec.get("docId");
                String term = (String) vec.get("term");
                Double count = (double) vec.get("count");
                if(documentVectors.containsKey(docId))
                {
                    documentVectors.get(docId).put(term, count);
                }
                else
                {
                	Map<String, Double> dv = new HashMap();
                    dv.put(term, count);
                    documentVectors.put(docId, dv);
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }


	private void loadDocLengths(String string) {
		// TODO Auto-generated method stub
		BufferedReader br = null;
		long totalCollection = 0; //size of collection
        try
        {
            //create file object
            File file = new File(string);
            //create BufferedReader object from the File
            br = new BufferedReader( new FileReader(file) );
            String line = null;
            
            //read file line by line
            while ( (line = br.readLine()) != null )
            {
            	String[] entry = line.split("\\s+");
            	int docLength = Integer.parseInt(entry[1]);
            	totalCollection += docLength;
                docLengths.put(Integer.parseInt(entry[0]), docLength);
            }
            
            br.close();
        } 
        
        catch(IOException e) 
        {
            e.printStackTrace();
        }
        //docStats = new DocStats(totalCollection, 1.0 * totalCollection / docStats.numDocs , docLengths.keySet().size());
        docStats.collectionSize = totalCollection;
        docStats.numDocs = docLengths.keySet().size();
        docStats.averageDocLength = 1.0 * totalCollection / docStats.numDocs;

	}

	private void loadStringMap(String string, Map map) 
		// TODO Auto-generated method stub
		{
	        BufferedReader br = null;
	        
	        try
	        {
	            
	            //create file object
	            File file = new File(string);
	            
	            //create BufferedReader object from the File
	            br = new BufferedReader( new FileReader(file) );
	            String line = null;
	            
	            //read file line by line
	            while ( (line = br.readLine()) != null )
	            {
	            	String[] entry = line.split("\\s+");
	            	map.put(Integer.parseInt(entry[0]), entry[1]);
	            }
	            
	            br.close();
	        } 
	        catch(IOException e) 
	        {
	            e.printStackTrace();
	        }

	}

	@Override
	public Set<String> getVocabulary() 
	{
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>(retrievedIndex.keySet()); 
		Collections.sort(list);
		 Set<String> set = new HashSet<>();
	     for (String t : list) 
	         set.add(t); 
	     return set;
	}

	@Override
	public int getTermFreq(String term) 
	{
		// TODO Auto-generated method stub
		if (retrievedIndex.containsKey(term)) 
    	{
    		return retrievedIndex.get(term).collectionTermFreq;
    	}
    	return 0;
	}
	
	@Override
	public String getPlay(int docId) 
	{
		// TODO Auto-generated method stub
		return playIdMap.get(docId);
	}

	@Override
	public String getScene(int docId) 
	{
		// TODO Auto-generated method stub
		return docIdtosceneIdMap.get(docId);
	}
	
	@Override
	public int getDocFreq(String term) 
	{
		// TODO Auto-generated method stub
    	if (retrievedIndex.containsKey(term)) 
    	{
    		return retrievedIndex.get(term).docTermFreq;
    	}
    	return 0;
	}
	
	public Entry<Integer, Integer> getShortestScene()
	{
		Entry <Integer, Integer> min=Collections.min(docLengths.entrySet(), new Comparator<Entry<Integer, Integer>>() {
		    public int compare(Entry<Integer, Integer> entry1, Entry<Integer, Integer> entry2) 
		    {
		        return entry1.getValue().compareTo(entry2.getValue());
		    }
		});
		return min;
		
	}

	private int fromByteArray(byte[] bytes) 
	{ 
		return ByteBuffer.wrap(bytes).getInt(); 
	}
	@Override
	public PostingList getPostings(String term) 
	{
		PostingList invertedList = new PostingList();
        try {
            RandomAccessFile reader = new RandomAccessFile(invFile, "rw");
            RetrievedIndex retrieved = retrievedIndex.get(term);
            reader.seek(retrieved.offset);
            int buffLength =(int)(retrieved.bytesWritten);
            byte[] buffer = new byte[buffLength];
            int numRead = reader.read(buffer, 0, buffLength);
            assert numRead == retrieved.bytesWritten;
            Compression comp = CompressionFactory.getCompressor(compression);
            IntBuffer intBuffer = IntBuffer.allocate(buffer.length);
            comp.decode(buffer, intBuffer);   
            int[] data = new int[intBuffer.position()];
            //System.out.print(data);
            intBuffer.rewind();
            intBuffer.get(data);
            invertedList.fromIntegerArray(data);
            reader.close();
        } 
        catch(IOException e) 
        {
            e.printStackTrace();
        }
        return invertedList;

	}
	
	public String getDocName(int docId)
	{
		// TODO Auto-generated method stub
		return getScene(docId);
	}
	
	@Override
	public double getAverageDocLength() 
	{
		// TODO Auto-generated method stub
		return docStats.averageDocLength;
	}

	@Override
	public long getCollectionSize() 
	{
		// TODO Auto-generated method stub
		return docStats.collectionSize;
	}

	@Override
	public int getDocCount() 
	{
		// TODO Auto-generated method stub
		return docStats.numDocs;
	}

	@Override
	public int getDocLength(int docId) 
	{
		// TODO Auto-generated method stub
		return docLengths.get(docId);
	}

	@Override

    /* @return a list of the top k documents in descending order with respect to scores.
    * key = sceneId, value = score
    * Does document at a time retrieval using raw counts for the model */

	public List<Entry<Integer, Double>> retrieveQuery(String query, int k) {
		// TODO Auto-generated method stub
		PriorityQueue<Map.Entry<Integer,Double>> result = new PriorityQueue<>(Map.Entry.<Integer,Double>comparingByValue());
		String[] queryTerms = query.split("\\s+");
		PostingList[] lists = new PostingList[queryTerms.length];
		for(int i=0;i<queryTerms.length;i++)
		{
			lists[i]=getPostings(queryTerms[i]);
		}
		for (int doc = 1;doc<=getDocCount();doc++)
		{
			Double curScore = 0.0;
			for(PostingList p:lists)
			{
				p.skipTo(doc);
				Posting post = p.getCurrentPosting();
				if(post!=null && post.getDocId()==doc)
				{
					curScore+=post.getTermFreq();
				}
			}
			result.add(new AbstractMap.SimpleEntry<Integer,Double>(doc,curScore));
			if (result.size()>k)
			{
				result.poll();
			}
		}
		ArrayList<Map.Entry<Integer, Double>> scores = new ArrayList<Map.Entry<Integer,Double>>();
		scores.addAll(result);
		scores.sort(Map.Entry.<Integer,Double>comparingByValue(Comparator.reverseOrder()));
		return scores;
	}

	@Override
	public Map<String, Double> getDocumentVector(int docId) {
		// TODO Auto-generated method stub
		return documentVectors.get(docId);
	}

	@Override
	public Double getPriorForDocument(Integer docId, String priorFile) 
	{

	        Double priorValue = null;
	        try 
	        {
	            RandomAccessFile priorReader = new RandomAccessFile(priorFile, "r");

	            // each document's prior value is in a line ordered by the document-id
	            // seek to offset in the file and just read 8 bytes
	            // i.e. the size of a double value
	            priorReader.seek(docId * 8);
	            priorValue = priorReader.readDouble();
	            priorReader.close();
	        } 
	        
	        catch (Exception e) 
	        {
	            e.printStackTrace();
	        }

	        return priorValue;

	}


}
