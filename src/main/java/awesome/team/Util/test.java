package awesome.team.Util;

import java.io.File;  
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class test {
	    public static void main(String args[]){
	    	try {
	    		List<Map<String,String>> AM = new ArrayList<Map<String,String>>();
	    		Map<String,String> m1 = new HashMap<String,String>();
	    		Map<String,String> m2 = new HashMap<String,String>();
	    		m1.put("beginTime", "00:00:00.000");
	    		m1.put("endTime", "00:00:10.000");
	    		m1.put("srtBody", "字幕测试1");
	    		m2.put("beginTime", "00:00:12.000");
	    		m2.put("endTime", "00:00:15.000");
	    		m2.put("srtBody", "字幕测试2");
	    		AM.add(m1);
	    		AM.add(m2);
	    	    //File file = new File("/tmp/a.srt");
	    	    //WriteSubtitle ws = new WriteSubtitle();
	    	    //ws.Srt(AM, file);
	            String s = "/tmp/a.mp4";
	            //FFMpegCompressionSubtitle F = new FFMpegCompressionSubtitle();
	            
	            String q = FfmpegCompSubUtil.burnSubtitlesIntoVideo(s,AM);
	            System.out.print(q); 
	    	}catch(Exception e){  
	            System.out.println(e.toString());  
	        }  
	    }
}
