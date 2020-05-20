package awesome.team.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import awesome.team.service.VideoService;

@Service
public class VideoServiceImpl implements VideoService {

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
            File fileSave = new File("/home/mig-chen/文档/data/videos", newVideoName);
            file.transferTo(fileSave);
            resultMap.put("resCode","1");
            resultMap.put("webShowPath","/home/mig-chen/文档/data/videos" + newVideoName);

            return  resultMap;

        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("resCode","0");
            return  resultMap ;
        }
	}

}
