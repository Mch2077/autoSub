package awesome.team.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

import awesome.team.anno.AuthToken;
import awesome.team.service.VideoService;


@RequestMapping("/video")
@RestController
public class VideoController {
	
	@Resource
    private VideoService vs;
    
    @Autowired
    private HttpServletRequest request;

	
	@AuthToken
	@RequestMapping(value = "upload", method = RequestMethod.POST)
    public JSONObject videoUpload(@RequestParam("file") MultipartFile file)
            throws IllegalStateException {
		JSONObject result = vs.videoUpload(file);
		String token = (String) request.getAttribute("token");
		result.put("token", token);
		return result;
    }
	
}

