package awesome.team.job;
import java.io.File;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.ClassUtils;

public class ClearCatch extends QuartzJobBean {
	private String path = ClassUtils.getDefaultClassLoader().getResource("static/res").getPath()+"/";
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    	delFile(path);
    }
    
    private void delFile(String path){
		File file = new File(path);
		if (file.exists()) {
			String[] tempList = file.list();
			File temp = null;
			for (int i = 0; i < tempList.length; i++) {
				if (path.endsWith(File.separator)) {
					temp = new File(path + tempList[i]);
				} else {
					temp = new File(path + File.separator + tempList[i]);
				}
				if (temp.isFile()) {
					temp.delete();
				}
			}
		}
	}
}