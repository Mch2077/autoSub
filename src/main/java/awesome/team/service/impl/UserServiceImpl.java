package awesome.team.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import awesome.team.domain.User;
import awesome.team.mapper.UserMapper;
import awesome.team.service.UserService;
import awesome.team.util.Base64EncryptUtils;

@Service
public class UserServiceImpl implements UserService {

	@Resource
	UserMapper userMapper;
	
	@Override
	public JSONObject userLogin(String userName, String password) {
		JSONObject result = new JSONObject();
		if (userName.isEmpty() || password.isEmpty() 
				|| userName.equals(" ") || password.equals(" ")) {
			result.put("code", "400");
			result.put("msg", "paremeter invalid");
		}else if (userMapper.selectByUserName(userName) == null) {
			result.put("code", "400");
			result.put("msg", "user doesn't exist");
		}else{
			User user = userMapper.selectByUserName(userName);
			if (user.getPassword().equals(Base64EncryptUtils.encrypt(password))){
				
				String token = Base64EncryptUtils.encrypt(user.getUserName()+"&"+
								System.currentTimeMillis());
				result.put("code", "200");
	            result.put("token", token);
                userMapper.updateTokenByUserName(token, userName);
			}else {
				result.put("code", "400");
				result.put("msg", "password incorrect");
			}
		}
		return result;
	}
	
	
	@Override
	public JSONObject userUpdate(String newUserName,String newPassword,
			String oldUserName,String oldPassword) {
		JSONObject result = new JSONObject();
		if (newUserName.isEmpty() || newPassword.isEmpty() 
				|| oldUserName.isEmpty() || oldPassword.isEmpty()
				|| newUserName.equals(" ") || newPassword.equals(" ") 
				|| oldUserName.equals(" ") || oldPassword.equals(" ")) {
			result.put("code", "400");
			result.put("msg", "paremeter invalid");
		}else {
			try {
				User user = userMapper.selectByUserName(oldUserName);
				if (user.getPassword().equals(Base64EncryptUtils.encrypt(oldPassword))){
				String token = Base64EncryptUtils.encrypt(newUserName+"&"+
						System.currentTimeMillis());
				newPassword = Base64EncryptUtils.encrypt(newPassword);
					try {
						//userMapper.updateByUP(newUserName, newPassword, token, oldUserName, oldPassword);
						result.put("code", "200");
			            result.put("token", token);
					} catch (Exception e) {
						result.put("code", "400");
						result.put("msg", "update failed");
					}
				}else {
					result.put("code", "400");
					result.put("msg", "paremeter incorrect");
				}
			} catch (Exception e) {
				result.put("code", "400");
				result.put("msg", "user does't exist");
			}
		}		
		return result;
	}
	
	@Override
	public JSONObject userRegister(String userName,String password) {
		JSONObject result = new JSONObject();
		if (userName.isEmpty() || password.isEmpty()
			|| userName.equals(" ") || password.equals(" ")) {
			result.put("code", "400");
			result.put("msg", "paremeter invalid");
		}else if (userMapper.selectByUserName(userName) != null) {
			result.put("code", "400");
			result.put("msg", "user already exist");
		}else{
			password = Base64EncryptUtils.encrypt(password);
			userMapper.insertUser(userName, password);
			result.put("code", "200");
		}
		return result;
	}
}
