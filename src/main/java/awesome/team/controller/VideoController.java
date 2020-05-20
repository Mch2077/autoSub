package awesome.team.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import awesome.team.service.VideoService;


@RequestMapping("/video")
@RestController
public class VideoController {
	
	@Resource
    private VideoService vs;
	
	@RequestMapping(value = "upload", method = RequestMethod.POST)
	    @ResponseBody
	    public Map<String,String> videoUpload(@RequestParam("file") MultipartFile file)
	            throws IllegalStateException {
			Map<String,String> resultMap = vs.videoUpload(file);
			return resultMap;
	    }
	
}

