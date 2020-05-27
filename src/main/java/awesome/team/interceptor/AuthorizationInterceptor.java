package awesome.team.interceptor;

import java.io.PrintWriter;
import java.lang.reflect.Method;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;

import awesome.team.anno.AuthToken;
import awesome.team.domain.User;
import awesome.team.mapper.UserMapper;
import awesome.team.util.Base64EncryptUtils;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    //存放鉴权信息的Header名称，默认是Authorization
    private String httpHeaderName = "Authorization";

    //鉴权失败后返回的错误信息，默认为401 unauthorized
    private String unauthorizedErrorMessage = "401 unauthorized";

    //鉴权失败后返回的HTTP错误码，默认为401
    private int unauthorizedErrorCode = HttpServletResponse.SC_UNAUTHORIZED;

	//存放登录用户模型Key的Request Key
    public static final String REQUEST_CURRENT_KEY = "REQUEST_CURRENT_KEY";
    
    @Resource
	UserMapper userMapper;
   
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 如果打上了AuthToken注解则需要验证token
        if (method.getAnnotation(AuthToken.class) != null || handlerMethod.getBeanType().getAnnotation(AuthToken.class) != null) {
        	JSONObject result = new JSONObject();
            String token = request.getHeader(httpHeaderName);
            if (!token.isEmpty() && !token.equals("") && token != null && !token.isBlank()) {
	            String tempString = Base64EncryptUtils.decrypt(token);
	            String[] baseInfo = tempString.split("&");
	            String userName = baseInfo[0];
	    		User user = userMapper.selectByUserName(userName);
	    		long tokenTime = Long.parseLong(baseInfo[1]);
	    		long currentTime = System.currentTimeMillis();
            if (token.equals(user.getToken()) && ((currentTime - tokenTime) <= 3600)){  
                //request.setAttribute(REQUEST_CURRENT_KEY, userName);
                token = Base64EncryptUtils.encrypt(user.getUserName()+"&"+System.currentTimeMillis());
                userMapper.updateByUP(user.getUserName(), user.getPassword(), token, user.getUserName(), user.getPassword());
                request.setAttribute("token", token);
                return true;
			}else {
                PrintWriter out = null;
                try {
                    response.setStatus(unauthorizedErrorCode);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    
                    result.put("status", ((HttpServletResponse) response).getStatus());
                    result.put("errMsg", HttpStatus.UNAUTHORIZED);
                    out = response.getWriter();
                    out.println(result);
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (null != out) {
                        out.flush();
                        out.close();
                    }
                }
            }
        }else {
                PrintWriter out = null;
                try {
                    response.setStatus(unauthorizedErrorCode);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                    result.put("status", ((HttpServletResponse) response).getStatus());
                    result.put("errMsg", HttpStatus.UNAUTHORIZED);
                    out = response.getWriter();
                    out.println(result);
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (null != out) {
                        out.flush();
                        out.close();
                    }
                }
            }
        }
        request.setAttribute(REQUEST_CURRENT_KEY, null);
        return true;    
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}