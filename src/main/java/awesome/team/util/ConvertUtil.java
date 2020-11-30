package awesome.team.util;

import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

//import org.apache.log4j.Logger;
  
public class ConvertUtil {
	//private static Logger logger = Logger.getLogger(ConvertUtil.class);
	private static String FFMPEG_PATH = "ffmpeg";
	//private static String filePath = "/home/soalive/git/autoSub/src/main/resources/videos/test.mp4";
	/**
	 * 提取视频中的音频文件
	 * @param filePath
	 * @return 音频文件名
	 * @throws Exception
	 */
	public static String transform(String filePath) throws Exception {
		//logger.info(" 可更改 fileName 的目标路径以选择原视频文件 ");
		File file = new File(filePath);
		if(!file.exists()) {
			//logger.error(" 文件不存在："+filePath);
			throw new RuntimeException(" 文件不存在："+filePath);
		}
		
		String name = filePath.substring(0, filePath.lastIndexOf(".")) + ".wav";
		//logger.info(" 获取到wav格式的音频文件："+name);
		
		// 提取视频中的音频文件。 根据讯飞要求设置采样率， 位数
		String cmd = FFMPEG_PATH + " -i "+ filePath +" -vn -ar 16000 -ac 1 -acodec pcm_s16le -y "+ name;//编辑命令
		Process process = Runtime.getRuntime().exec(cmd);//使用exec（）调用该命令
        
        InputStreamReader ir = new InputStreamReader(process.getInputStream());
        LineNumberReader input = new LineNumberReader(ir);
        String line;
        //输出结果，需要有这部分代码， 否则不能生产抽取的音频文件
        while ((line = input.readLine()) != null) {
            System.out.println(line);
        }
        process.destroy();
		return name;
	}

/*
	public static void main(String[] args) {
		try {
			transform();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
*/
}