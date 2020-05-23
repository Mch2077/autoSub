package awesome.team.Util;

import java.io.BufferedWriter;  
import java.io.File;  
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FfmpegCompSubUtil {
    private static String FFMPEG_PATH = "ffmpeg";
    
	private static void Srt(List<Map<String,String>> AMap, File file) {
		try {
        FileOutputStream fw = new FileOutputStream(file); 
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fw,"UTF-8")); 
        for(int i=0; i<AMap.size(); i++ ) {
        	Map m = AMap.get(i);
        	String s = "-->";
        	String s1 = m.get("beginTime").toString(); 
        	String s2 = m.get("endTime").toString();
        	String s3 = m.get("srtBody").toString();
        	bw.write(String.valueOf(i+1));
        	bw.newLine();//换行  
        	bw.write(s1); 
        	bw.write(" ");
        	bw.write(s);
        	bw.write(" ");
        	bw.write(s2);
        	bw.newLine();//换行 
        	bw.write(s3);
        	bw.newLine();//换行 
        	if(i != AMap.size()) bw.newLine();//换行 
        	}
        bw.close();
		}catch(Exception e){  
			System.out.println(e.toString());  
        } 
	}
	
    public static String burnSubtitlesIntoVideo(String videoUrl ,List<Map<String,String>> AMap){
    	//获取视频名
    	String filename = videoUrl.substring(0, videoUrl.lastIndexOf("."));
    	//获取视频后缀
        String fileSuffix = videoUrl.substring(videoUrl.lastIndexOf(".") + 1).toLowerCase();
        //设置字幕地址
        String subtitleUrl = filename + ".srt";
    	//字幕写入
    	File subtitleFile = new File(subtitleUrl);
    	Srt(AMap,subtitleFile);
        
        String burnedFile = "";
        try {
            //将字幕压缩至视频中
            burnedFile = filename+"(字幕版)."+fileSuffix;
            String command = FFMPEG_PATH + " -i "+ videoUrl + " -vf subtitles="+ subtitleFile +" "+ burnedFile;
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
        } catch (Exception e) {
        	System.out.println(e.toString());
            System.out.println("视频压缩字幕失败，视频地址："+videoUrl+",字幕地址："+subtitleUrl );
        }
        return burnedFile;
    }
}
