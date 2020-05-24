package awesome.team.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import awesome.team.service.VideoService;
import awesome.team.util.ConvertUtil;

@Service
public class VideoServiceImpl implements VideoService {
	private String storePath = "/home/mig-chen/文档/data/videos/";
	@Override
	public Map<String, String> videoUpload(MultipartFile file) {
		Map<String,String> resultMap = new HashMap<>();
        try{
            //获取文件后缀
            String fileExt = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1)
                    .toLowerCase();
            // 重构文件名称
            String pikId = UUID.randomUUID().toString().replaceAll("-", "");
            String newVideoName = pikId + "." + fileExt;
            //保存视频
            File fileSave = new File(storePath, newVideoName);
            file.transferTo(fileSave);
            if (!videoProcess(storePath+newVideoName).isEmpty()) {
                resultMap.put("statue","success");
            	resultMap.put("webShowPath","http://ip:port/" + newVideoName); // JUST A EXAMPLE
                //考虑服务器使用http实现文件暴露下载
                return  resultMap;
			}
            resultMap.put("statue","failed");
            //考虑服务器使用http实现文件暴露下载
            return  resultMap;
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("statue","failed");
            return  resultMap ;
        }
	}

	private String videoProcess(String filePath) throws Exception {
		return ConvertUtil.transform(filePath);
	}
}
