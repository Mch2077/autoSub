package awesome.team.service;

import com.alibaba.fastjson.JSONObject;

public interface UserService {

	JSONObject userLogin(String userName, String password);
	JSONObject userUpdate(String newUserName,String newPassword,
			String oldUserName,String oldPassword);
	JSONObject userRegister(String userName, String password);

}
