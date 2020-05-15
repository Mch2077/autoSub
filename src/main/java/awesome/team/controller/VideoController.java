package awesome.team.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/video")
@RestController
public class VideoController {
	@RequestMapping(value = "upload", method = RequestMethod.POST)
	    @ResponseBody
	    public Map<String,String> VideoUpload(@RequestParam("file") MultipartFile file)
	            throws IllegalStateException {
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

