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
		if (userName.isEmpty() || password.isEmpty()) {
			result.put("status", "4005");
			result.put("errMsg", "username or password invalid");
			return result;
		}
		User user =(User) userMapper.selectByUserName(userName);
		if (user.getPassword().equals(Base64EncryptUtils.encrypt(password))){
			
			String token = Base64EncryptUtils.encrypt(user.getUserName()+"&"+
							System.currentTimeMillis());
			result.put("status", "200");
            result.put("token", token);
		}else {
			result.put("status", "4007");
			result.put("errMsg", "username or password is incorrect");
		}
		return result;
	}
	
	
	@Override
	public JSONObject userUpdate(String newUserName,String newPassword,
			String oldUserName,String oldPassword) {
		JSONObject result = new JSONObject();
		if (newUserName.isEmpty() || newPassword.isEmpty() 
				|| oldUserName.isEmpty() || oldPassword.isEmpty()) {
			result.put("status", "4005");
			result.put("errMsg", "paremeter invalid");
			return result;
		}
		User user = userMapper.selectByUserName(oldUserName);
		if (user.getPassword().equals(Base64EncryptUtils.encrypt(oldPassword))){
			
			String token = Base64EncryptUtils.encrypt(newUserName+"&"+
							System.currentTimeMillis());
			newPassword = Base64EncryptUtils.encrypt(newPassword);
			try {
				userMapper.updateByUP(newUserName, newPassword, token, oldUserName, oldPassword);
				result.put("status", "200");
	            result.put("token", token);
			} catch (Exception e) {
				result.put("status", "4002");
				result.put("errMsg", "update failed");
			}
		}else {
			result.put("status", "4007");
			result.put("errMsg", "username or password is incorrect");
		}
			
		return result;
	}
	
	@Override
	public JSONObject userRegister(String userName,String password) {
		JSONObject result = new JSONObject();
		if (userName.isEmpty() || password.isEmpty()) {
			result.put("status", "4005");
			result.put("errMsg", "paremeter invalid");
			return result;
		}
		password = Base64EncryptUtils.encrypt(password);
		try {
			userMapper.insertUser(userName, password);
			result.put("status", "200");
		} catch (Exception e) {
			result.put("status", "4003");
			result.put("errMsg", "insert failed");
		}	
		return result;
	}

}
