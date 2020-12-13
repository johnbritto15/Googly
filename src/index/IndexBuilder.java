package index;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import utilities.Compression;
import utilities.CompressionFactory;
import utilities.Compressors;

public class IndexBuilder {
	private Map<Integer, String> docIdtosceneIdMap;
	private Map<Integer, String> playIdMap;
	private Map<String, PostingList> invertedLists;
	private Map<Integer, Integer>docLengths;
	private Compressors compression;
	private Map<Integer, Map<String, Double>> documentVectors; 
	public IndexBuilder()
	{
		docIdtosceneIdMap = new HashMap<Integer, String>();
		playIdMap = new HashMap<Integer, String>();
		invertedLists = new HashMap<String, PostingList>();
		docLengths = new HashMap<Integer, Integer>();
		documentVectors = new HashMap<>();
	}
	public void parseFile(String filename) 
	{
			// TODO Auto-generated method stub
			JSONParser jsonParser = new JSONParser();
			//System.out.print("Yolo");
			try 
			{
	
	            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("shakespeare-scenes.json"));
	            JSONArray scenes = (JSONArray) jsonObject.get("corpus");
	            for (int idx = 0; idx < scenes.size(); idx++) 
	            {
	                JSONObject scene = (JSONObject) scenes.get(idx); 
	                String sceneId = (String) scene.get("sceneId");
	                int docId = idx + 1; //start doc ids from 0
	                docIdtosceneIdMap.put(docId, sceneId);
	                String playId = (String) scene.get("playId");
	                playIdMap.put(docId, playId);
	                
	                String text = (String) scene.get("text");
	                String[] words = text.split("\\s+");
	                docLengths.put(docId, words.length);//doc length
	                Map<String, Double> vector = new HashMap<>(); 
	                for (int pos=0;pos<words.length;pos++)
	                {
	                	String word = words[pos];
	                	invertedLists.putIfAbsent(word, new PostingList());
	                	invertedLists.get(word).add(docId,pos+1);
	                	vector.put(words[pos], vector.getOrDefault(words[pos], 0.0) + 1);
	                }
		        	 documentVectors.put(docId, vector);
		        	 docId++;
	            }
			}
			
			catch (FileNotFoundException e) 
			{
	        e.printStackTrace();
			} 
			catch (IOException e) 
			{
	        e.printStackTrace();
			} 
			catch (ParseException e) 
			{
	        e.printStackTrace();
			}
	}
	
	private void saveStringMap(String fileName, Map<Integer, String> map) 
	{
        List<String> lines = new ArrayList<>();
        map.forEach((k,v) -> lines.add(k + " " + v));
        try 
        {
            Path file = Paths.get(fileName);
            Files.write(file, lines, Charset.forName("UTF-8"));
        }  
        
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    private void saveDocLengths(String fileName) 
    {
        List<String> lines = new ArrayList<>();
        docLengths.forEach((k,v) -> lines.add(k + " " + v));
        try 
        {
            Path file = Paths.get(fileName);
            Files.write(file, lines, Charset.forName("UTF-8"));
        }  
        
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    private void saveInvertedLists(String lookupName, String invListName) 
    {
        long offset = 0;
        
        try 
        {
            PrintWriter lookupWriter = new PrintWriter(lookupName, "UTF-8");
            RandomAccessFile invListWriter = new RandomAccessFile(invListName, "rw");
            Compression comp = CompressionFactory.getCompressor(compression);
          
            for (Map.Entry<String, PostingList> entry : invertedLists.entrySet()) 
            {
                String term = entry.getKey();
                PostingList postings = entry.getValue();
                int docTermFreq = postings.documentCount();
                int collectionTermFreq = postings.termFrequency();
                Integer [] posts = postings.toIntegerArray();
                ByteBuffer byteBuffer = ByteBuffer.allocate(posts.length * 8);
                comp.encode(posts, byteBuffer);
                byte [] array = byteBuffer.array();
                invListWriter.write(array, 0, byteBuffer.position());
                long bytesWritten = invListWriter.getFilePointer() - offset;
                lookupWriter.println(term + " " + offset + " " + bytesWritten + " " + docTermFreq + " " + collectionTermFreq);
                offset = invListWriter.getFilePointer();
            }
            
            invListWriter.close();
            lookupWriter.close();
        } 
        
        catch (IOException e) 
        {
            e.printStackTrace();
        }

    }
    
    public void saveDocumentVectors(String fileName)
    {
        try
        {
            FileWriter fileWriter = new FileWriter(fileName);
            JSONArray docList = new JSONArray();
            for(Map.Entry<Integer, Map<String, Double>>  entry: documentVectors.entrySet())
            {
                for(Map.Entry<String, Double> tf: entry.getValue().entrySet())
                {
                    JSONObject vec = new JSONObject();
                    vec.put("docId", entry.getKey());
                    vec.put("term", tf.getKey());
                    vec.put("count", tf.getValue());
                    docList.add(vec);
                }
            }
            fileWriter.write(docList.toJSONString());
            fileWriter.flush();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }


    public void buildIndex(String sourcefile, boolean compress) 
    {
    	this.compression = compress ? Compressors.VBYTE : Compressors.EMPTY;
    	String invFile = compress ? "invListCompressed" : "invList";
        parseFile(sourcefile);
        saveStringMap("sceneId.txt", docIdtosceneIdMap);
        saveStringMap("playIds.txt", playIdMap);
        saveDocLengths("docLength.txt");
        saveInvertedLists("lookup.txt", invFile);
        saveDocumentVectors("documentVectors.json");
    }
 }
