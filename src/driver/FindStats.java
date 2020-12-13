package driver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import index.Index;
import index.InvertedIndex;
import java.util.Map.Entry;
import java.util.Objects;
public class FindStats {

	public static void main(String[] args) {
		try
		{
			// TODO Auto-generated method stub
			Index index = new InvertedIndex();
			HashMap<String, Integer> plays = new HashMap<String, Integer>();
			index.load(Boolean.parseBoolean(args[0]));
			//index.load(false);
			System.out.println("The average scene length is: "+index.getAverageDocLength());
			Entry <Integer, Integer> minScene=index.getShortestScene();
			String minSceneName = index.getScene(minScene.getKey());
			System.out.println("The shortest scene is :" +minSceneName+" with "+ minScene.getValue()+" words");
			String playId;
			int length;
			for (int docId=1; docId<=index.getDocCount(); docId++) 
			{
				playId=index.getPlay(docId);
				//System.out.println(playId);
				if(!plays.containsKey(playId)) 
				{
					plays.put(playId, index.getDocLength(docId));
				}
				else
				{
					length= plays.get(playId) + index.getDocLength(docId);
					plays.put(playId, length);
				}
			}
			List playLengths = new ArrayList<>(plays.values());
			int max = (int) Collections.max(playLengths);
			int min = (int) Collections.min(playLengths);
			System.out.println("The longest play is: " + getKeyByValue(plays, max) + " of "+ max+" words");
			System.out.println("The shortest play is: " + getKeyByValue(plays, min) + " of "+ min+" words");
		}
		catch(Exception e)
		{
			
		}
		//System.out.print(Collections.min(index.docLengths.values()));
	}
	public static <String, Integer> String getKeyByValue(HashMap<String, Integer> map, Integer value) {
	    for (Entry<String, Integer> entry : map.entrySet()) 
	    {
	        if (Objects.equals(value, entry.getValue())) 
	        {
	            return entry.getKey();
	        }
	    }
	    return null;
	}

}
