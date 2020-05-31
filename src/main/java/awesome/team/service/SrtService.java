package awesome.team.service;


import com.alibaba.fastjson.JSONObject;

public interface SrtService {
	JSONObject burnSrt(String videoUrl ,String srt);
}
