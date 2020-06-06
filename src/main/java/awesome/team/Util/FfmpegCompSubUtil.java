package awesome.team.Util;

import java.io.BufferedWriter;  
import java.io.File;  
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
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
    
    //转化字符串
    public static List<Map<String,String>> lm (List<Map<String,String>> AMap){
    	List<Map<String,String>> AM0 = new ArrayList<Map<String,String>>();
    	try {
            for(int i=0; i<AMap.size(); i++ ) {
            	Map m = AMap.get(i);
            	String s1 = time(Integer.valueOf(m.get("bg").toString())); 
            	String s2 = time(Integer.valueOf(m.get("ed").toString()));
            	String s3 = m.get("onebest").toString();
	    		Map<String,String> m1 = new HashMap<String,String>();
	    		m1.put("sn",String.valueOf(i+1));
	    		m1.put("bg", s1);
	    		m1.put("ed", s2);
	    		m1.put("onebest", s3);
	    		AM0.add(m1);
            }
    	}catch(Exception e){  
			System.out.println(e.toString());  
        } 
    	return AM0;
    }
    
    //将字符串写入srt文件
	private static void Srt(List<Map<String,String>> AMap, File file) {
		try {
		int EngORCh;
		char[] fh1= new char[10];
		fh1[0]='，';fh1[1]='。';fh1[2]='、';fh1[3]='？';fh1[4]='！';fh1[5]='：';fh1[6]='；';fh1[7]='）';fh1[8]='”';fh1[9]='》';
		String sm = "'";
		char[] fh2= new char[8];
		fh2[0]=',';fh2[1]='.';fh2[2]='?';fh2[3]='!';fh2[4]=':';fh2[5]=';';fh2[6]=')';fh2[7]='"';
        FileOutputStream fw = new FileOutputStream(file); 
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fw,"UTF-8")); 
        for(int i=0; i<AMap.size(); i++ ) {
        	Map m = AMap.get(i);
        	int a = 30;
        	int b = 60;
        	int c = 0;
        	String s = "-->";
        	String s1 = m.get("bg").toString(); 
        	String s2 = m.get("ed").toString();
        	String s3 = "";
        	String s4 = m.get("sn").toString();
        	bw.write(s4);
        	bw.newLine();//换行  
        	bw.write(s1); 
        	bw.write(" ");
        	bw.write(s);
        	bw.write(" ");
        	bw.write(s2);
        	bw.newLine();//换行 
        	for(int j=0;j<m.get("onebest").toString().length()-1;j=j+c) {
        		int n = 1;
        		if(a!=30) a=30;
        		if(b!=60) b=60;
        		char cha = m.get("onebest").toString().charAt(j),sh;
        		if(cha==' ') {
        			sh=cha;
        		    while (sh==' ') { 
        			    sh=m.get("onebest").toString().charAt(j+n);
        			    n++;
        		      }
        		    if((sh>='a'&&sh<='z')||(sh>='A'&&sh<='Z')) EngORCh=1;
        		    else EngORCh=0;
        		}
        		else if((cha>='a'&&cha<='z')||(cha>='A'&&cha<='Z')) EngORCh=1;
    		    else EngORCh=0;
        		if(EngORCh==0) {
        		if(j+a+1<m.get("onebest").toString().length()) {
        			System.out.println(j);
        			char x=m.get("onebest").toString().charAt(j+a);
        			while((x>='a'&&x<='z')||(x>='A'&&x<='Z')||x==fh2[0]||x==fh2[1]||x==fh2[2]||x==fh2[3]||x==fh2[4]||x==fh2[5]||x==fh2[6]||x==fh2[7]||x==' '||x==sm.charAt(0)) {
        				a--;
        				x=m.get("onebest").toString().charAt(j+a);
        			}
        			for(int k=0;k<10;k++){
        			if(m.get("onebest").toString().charAt(j+a) == fh1[k]) a=a+1;
        			}
        			s3=m.get("onebest").toString().substring(j,j+a);
            	    bw.write(s3);
            	    bw.newLine();//换行 
        		}
        		else {
        			int end=0;
        			char x=m.get("onebest").toString().charAt(m.get("onebest").toString().length()-1);
        			a=m.get("onebest").toString().length()-1;
        			while((x>='a'&&x<='z')||(x>='A'&&x<='Z')||x==fh2[0]||x==fh2[1]||x==fh2[2]||x==fh2[3]||x==fh2[4]||x==fh2[5]||x==fh2[6]||x==fh2[7]||x==' '||x==sm.charAt(0)) {
        				a--;
        				x=m.get("onebest").toString().charAt(j+a);
        				end=1;
        			}
        			if(end==0) {
        			s3=m.get("onebest").toString().substring(j,m.get("onebest").toString().length());
        			bw.write(s3);
        			}
        			else if(end==1) {
        				a++;
        				s3=m.get("onebest").toString().substring(j,j+a);
        			bw.write(s3);
        			bw.newLine();//换行 
        			}
        		}
        		c=a;
        		}
        		else {
            		if(j+b+1<m.get("onebest").toString().length()) {
            			char x=m.get("onebest").toString().charAt(j+b);
            			while((x<'a'||x>'z'||x==' ')&&(x<'A'||x>'Z'||x==' ')&&x!=fh2[0]&&x!=fh2[1]&&x!=fh2[2]&&x!=fh2[3]&&x!=fh2[4]&&x!=fh2[5]&&x!=fh2[6]&&x!=fh2[7]&&x!=sm.charAt(0)) {
            				b--;
            				x=m.get("onebest").toString().charAt(j+b);
            			}
            			char c1 = m.get("onebest").toString().charAt(j+b-1);
            			char c2 = m.get("onebest").toString().charAt(j+b);
            			if((c2>='a'&&c2<='z')||(c2>='A'&&c2<='Z')) {
            				if(c1!=sm.charAt(0)&&(c1>='a'&&c1<='z')||(c1>='A'&&c1<='Z')){
            					s3=m.get("onebest").toString().substring(j,j+a)+'-';
                     	        bw.write(s3);
                     	        bw.newLine();//换行
            				}
            				else {
            					b++;
                			    s3=m.get("onebest").toString().substring(j,j+b);
                    	        bw.write(s3);
                    	        bw.newLine();//换行 
            				}
            			}
            			else if(c2==sm.charAt(0)){
            				b = b+2;
            			    s3=m.get("onebest").toString().substring(j,j+b);
                	        bw.write(s3);
                	        bw.newLine();//换行 
            					}		
            			else {
            			    for(int k=0;k<8;k++){
            			       if(m.get("onebest").toString().charAt(j+b) == fh2[k]) b=b+1;  
            				}
            			    s3=m.get("onebest").toString().substring(j,j+b);
                	        bw.write(s3);
                	        bw.newLine();//换行 
            			}
            		}
            		else {
            			int end=0;
            			char x=m.get("onebest").toString().charAt(m.get("onebest").toString().length()-1);
            			b=m.get("onebest").toString().length()-1;
            			while((x<'a'||x>'z'||x==' ')&&(x<'A'||x>'Z'||x==' ')&&x!=fh2[0]&&x!=fh2[1]&&x!=fh2[2]&&x!=fh2[3]&&x!=fh2[4]&&x!=fh2[5]&&x!=fh2[6]&&x!=fh2[7]&&x!=sm.charAt(0)) {
            				b--;
            				x=m.get("onebest").toString().charAt(j+b);
            				end=1;
            			}
            			if(end==0) {
            			s3=m.get("onebest").toString().substring(j,m.get("onebest").toString().length());
            			bw.write(s3);
            			}
            			else if(end==1) {
            				b++;
            				s3=m.get("onebest").toString().substring(j,j+b);
            			bw.write(s3);
            			bw.newLine();//换行 
            			}
            		}
            		c=b;
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
    	
    	File subtitleFile = new File(subtitleUrl);
    	
        //字幕写入
        Srt(AMap,subtitleFile);
        
        String burnedFile = "";
        String burnedFile1 = "";
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