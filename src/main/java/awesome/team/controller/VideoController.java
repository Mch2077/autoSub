package awesome.team.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

import awesome.team.domain.User;
import awesome.team.mapper.UserMapper;
import awesome.team.service.VideoService;
import awesome.team.util.Base64EncryptUtils;


@RequestMapping("/video")
@RestController
public class VideoController {
	
	@Resource
    private VideoService vs;
	
	@Resource
	UserMapper userMapper;

	@RequestMapping(value = "upload", method = RequestMethod.POST)
    public JSONObject videoUpload(@RequestParam("file") MultipartFile file, 
    		@RequestParam("language") boolean isCN,
    		@RequestHeader("Authorization")String token)
            throws IllegalStateException {
		
		JSONObject result = new JSONObject();
		//System.out.println(language);
		if (!token.isEmpty() && !token.equals("") && token != null && !token.isBlank()) {
			
            String tempString = Base64EncryptUtils.decrypt(token);
            String[] baseInfo = tempString.split("&");
            String userName = baseInfo[0];
    		User user = userMapper.selectByUserName(userName);
    		
	        if (token.equals(user.getToken())){  
	            //request.setAttribute(REQUEST_CURRENT_KEY, userName);
	            //token = Base64EncryptUtils.encrypt(user.getUserName()+"&"+System.currentTimeMillis());
	            //userMapper.updateTokenByUserName(token, user.getUserName());
		        result = vs.videoUpload(file,isCN);
	            result.put("token",token);
	        }else {
				result.put("code","401");
				result.put("msg", "unauthorized");
			}
	        
        }else{
        	result.put("code","401");
			result.put("msg", "unauthorized");
		}
		return result;
    }
	
}

