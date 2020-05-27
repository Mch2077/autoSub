package awesome.team.service.impl;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

import awesome.team.api.IfasrAPI;
import awesome.team.service.VideoService;
import awesome.team.util.ConvertUtil;
import awesome.team.util.SrtUtil;

@Service
public class VideoServiceImpl implements VideoService {
	private String storePath = ClassUtils.getDefaultClassLoader().getResource("static/res").getPath()+"/";
	@Override
	public JSONObject videoUpload(MultipartFile file) {
		JSONObject result = new JSONObject() ;
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
            String videoPath = storePath+newVideoName;
            String audioPath = ConvertUtil.transform(videoPath);
            List<Map<String,String>> subMaps = IfasrAPI.getListMap(audioPath);
            String burnedFile = SrtUtil.burnSubtitlesIntoVideo(videoPath, subMaps);
            if (!burnedFile.isEmpty()) {
                result.put("statue","success");
            	result.put("webShowPath", burnedFile); // JUST A EXAMPLE
                //考虑服务器使用http实现文件暴露下载
                return  result;
			}
            //考虑服务器使用http实现文件暴露下载
            return  result;
        }catch (Exception e){
            e.printStackTrace();
            result.put("statue","failed");
           
            return  result ;
        }
	}
}
