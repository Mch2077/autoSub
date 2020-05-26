package awesome.team.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.web.multipart.MultipartFile;

import awesome.team.api.IfasrAPI;
import awesome.team.service.VideoService;
import awesome.team.util.ConvertUtil;
import awesome.team.util.SrtUtil;

@Service
public class VideoServiceImpl implements VideoService {
	private String storePath = ClassUtils.getDefaultClassLoader().getResource("static/res").getPath()+"/";
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
            String videoPath = storePath+newVideoName;
            String audioPath = ConvertUtil.transform(videoPath);
            List<Map<String,String>> subMaps = IfasrAPI.getListMap(audioPath);
            String burnedFile = SrtUtil.burnSubtitlesIntoVideo(videoPath, subMaps);
            if (!burnedFile.isEmpty()) {
                resultMap.put("statue","success");
            	resultMap.put("webShowPath", burnedFile); // JUST A EXAMPLE
                //考虑服务器使用http实现文件暴露下载
                return  resultMap;
			}
            //考虑服务器使用http实现文件暴露下载
            return  resultMap;
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("statue","failed");
            return  resultMap ;
        }
	}
}
