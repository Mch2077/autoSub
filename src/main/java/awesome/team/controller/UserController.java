package awesome.team.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import awesome.team.service.UserService;

@RequestMapping("/user")
@RestController
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam(value = "userName",required = true) String userName,
    		@RequestParam(value = "password",required = true)String password) {
    	JSONObject result = userService.userLogin(userName, password);
        return JSON.toJSONString(result);
    }
    
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(
    		@RequestParam(value = "newUserName",required = true)String newUserName,
    		@RequestParam(value = "newPassword",required = true)String newPassword,
    		@RequestParam(value = "oldUserName",required = true)String oldUserName,
    		@RequestParam(value = "oldPassword",required = true)String oldPassword) {
    	JSONObject result = userService.userUpdate(newUserName, newPassword, 
    			oldUserName, oldPassword);
        return JSON.toJSONString(result);
    }
    
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestParam(value = "userName",required = true)String userName,
    		@RequestParam(value = "password",required = true)String password) {
    	String result = JSON.toJSONString(userService.userRegister(userName, password));
        return result;
    }
    

}