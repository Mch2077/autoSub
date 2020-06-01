package awesome.team.util;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.log4j.Logger;

public class Main {
	private static Logger logger = Logger.getLogger(ConvertVideo.class);
    @SuppressWarnings("resource")
	public static void main(String[] args) {
    	logger.info(" 每日凌晨1点清空指定文件下的所有缓存 ");
        new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        logger.info(" 清除完毕 ");
    }

}

