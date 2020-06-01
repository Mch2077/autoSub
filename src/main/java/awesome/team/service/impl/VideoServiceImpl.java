package awesome.team.service.impl;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

import awesome.team.api.IfasrAPI;
import awesome.team.service.VideoService;
import awesome.team.util.ConvertUtil;
import awesome.team.util.SrtUtil;

@Service
public class VideoServiceImpl implements VideoService {
	@Override
	public JSONObject videoUpload(MultipartFile file, boolean isCN) {
		JSONObject result = new JSONObject() ;
        try{
        	String storePath = ClassUtils.getDefaultClassLoader().getResource("static/res").getPath()+"/";
            //获取文件后缀
            String fileExt = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1)
                    .toLowerCase();
            // 重构文件名称
            String pikId = UUID.randomUUID().toString().replaceAll("-", "");
            String newVideoName = pikId + "." + fileExt;
            //保存视频
            File fileSave = new File(storePath, newVideoName);
            file.transferTo(fileSave);
            String videoPath = storePath+newVideoName;
            String audioPath = ConvertUtil.transform(videoPath);
            List<Map<String,String>> rawMap = IfasrAPI.getListMap(audioPath,isCN);
            List<Map<String,String>> tempSub = SrtUtil.lm(rawMap);
            if (!tempSub.isEmpty()) {
                result.put("code","0");
            	result.put("data", tempSub);
            	result.put("msg", "upload success");
            	result.put("videoUrl", videoPath);
            	result.put("count",tempSub.size());
                //考虑服务器使用http实现文件暴露下载
                return  result;
			}
            return  result;
        }catch (Exception e){
            e.printStackTrace();
            result.put("code","400");
            result.put("msg", "video upload failed");
            return  result ;
        }
	}
}
