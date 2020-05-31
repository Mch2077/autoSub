package awesome.team.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import awesome.team.domain.User;
import awesome.team.mapper.UserMapper;
import awesome.team.service.SrtService;
import awesome.team.util.Base64EncryptUtils;


@RequestMapping("/srt")
@RestController
public class SrtController {
	
	@Resource
    private SrtService ss;
	
	@Resource
	UserMapper userMapper;

	@RequestMapping(value = "process", method = RequestMethod.POST)
    public JSONObject videoUpload(@RequestParam("videoUrl") String videoUrl,
    		@RequestParam("srt") String srt,
    		@RequestHeader("Authorization")String token)
            throws IllegalStateException {
		
		JSONObject result = new JSONObject();
		
		if (!token.isEmpty() && !token.equals("") && token != null && !token.isBlank()) {
			
            String tempString = Base64EncryptUtils.decrypt(token);
            String[] baseInfo = tempString.split("&");
            String userName = baseInfo[0];
    		User user = userMapper.selectByUserName(userName);
    		
	        if (token.equals(user.getToken())){  
	            //request.setAttribute(REQUEST_CURRENT_KEY, userName);
	            //token = Base64EncryptUtils.encrypt(user.getUserName()+"&"+System.currentTimeMillis());
	            //userMapper.updateTokenByUserName(token, user.getUserName());
	            result = ss.burnSrt(videoUrl, srt);
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

