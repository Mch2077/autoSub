package awesome.team.Util;

import java.io.BufferedWriter;  
import java.io.File;  
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

public class FfmpegCompSubUtil {
    private static String FFMPEG_PATH = "ffmpeg";
    
    //时间转化
    private static String time(int t) {
    	int a = t;
    	int hour = 0,mintue = 0, second = 0,msecond = 0;
    	if(a >=3600000) {
    		hour = a/3600000;
    		a = a%3600000;
    		mintue = a/60000;
    		a = a%60000;
    		second = a/1000;
    		msecond = a%1000;
    	}
    	else if(a < 3600000 && a >= 60000) {
    		hour = 0;
    		mintue = a/60000;
    		a = a%60000;
    		second = a/1000;
    		msecond = a%1000;	
    	}
    	else if(a < 60000 && a>=1000) {
    		hour = 0;
    		mintue = 0;
    		second = a/1000;
    		msecond = a%1000;	
    	}
    	else if(a<1000) {
    		hour = 0;
    		mintue = 0;
    		second = 0;
    		msecond = a;
    	}
    	String h = "00"+String.valueOf(hour);
    	String m = "00"+String.valueOf(mintue);
    	String s = "00"+String.valueOf(second);
    	String ms = "000"+String.valueOf(msecond);
    	String time = h.substring(h.length()-2,h.length())+":"+m.substring(m.length()-2,m.length())+":"+s.substring(s.length()-2,s.length())+"."+ms.substring(ms.length()-3,ms.length());
    	return time;
    }
    
    //将字符串写入srt文件
	private static void Srt(List<Map<String,String>> AMap, File file) {
		try {
		char[] fh= new char[10];
				fh[0]='，';fh[1]='。';fh[2]='、';fh[3]='？';fh[4]='！';fh[5]='：';fh[6]='；';fh[7]='）';fh[8]='”';fh[9]='》';
        FileOutputStream fw = new FileOutputStream(file); 
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fw,"UTF-8")); 
        for(int i=0; i<AMap.size(); i++ ) {
        	Map m = AMap.get(i);
        	int a = 30;
        	String s = "-->";
        	String s1 = time(Integer.valueOf(m.get("bg").toString())); 
        	String s2 = time(Integer.valueOf(m.get("ed").toString()));
        	String s3 = "";
        	bw.write(String.valueOf(i+1));
        	bw.newLine();//换行  
        	bw.write(s1); 
        	bw.write(" ");
        	bw.write(s);
        	bw.write(" ");
        	bw.write(s2);
        	bw.newLine();//换行 
        	for(int j=0;j<m.get("onebest").toString().length()-1;j=j+a) {
        		if(a!=30) a=30;
        		if(j+a+1<m.get("onebest").toString().length()) {
        			for(int k=0;k<10;k++){
        			if(m.get("onebest").toString().charAt(j+a) == fh[k]) a=a+1;
        			}
        			s3=m.get("onebest").toString().substring(j,j+a);
            	    bw.write(s3);
            	    bw.newLine();//换行 
        		}
        		else {
        			s3=m.get("onebest").toString().substring(j,m.get("onebest").toString().length());
        			bw.write(s3);
        		}
        	}
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
    	//获取无路径视频名
    	String Wfilename = videoUrl.substring(videoUrl.lastIndexOf("/") + 1, videoUrl.lastIndexOf("."));
    	//获取视频后缀
        String fileSuffix = videoUrl.substring(videoUrl.lastIndexOf(".") + 1).toLowerCase();
        //设置字幕地址
        String subtitleUrl = filename + ".srt";
    	//字幕写入
    	File subtitleFile = new File(subtitleUrl);
    	Srt(AMap,subtitleFile);
        
        String burnedFile = "";
        String burnedFile1= "";
        burnedFile1 = Wfilename+"(字幕版)."+fileSuffix;
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
        return burnedFile1;
    }
}
