package awesome.team.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import awesome.team.service.SrtService;
import awesome.team.util.SrtUtil;

@Service
public class SrtServiceImpl implements SrtService {
	
	@Override
	public JSONObject burnSrt(String videoUrl, String srt) {
		JSONObject result = new JSONObject() ;
		System.out.println(srt);
		List<Map<String,String>> AMap = (List<Map<String,String>>) JSONArray.parse(srt);
		
        try{
           String webShowPath = SrtUtil.burnSubtitlesIntoVideo(videoUrl, AMap);
            if (!webShowPath.isEmpty()) {
                result.put("code","200");
            	result.put("msg", "upload success");
            	result.put("webShowPath", webShowPath);
                //考虑服务器使用http实现文件暴露下载
                return  result;
			}
            return  result;
        }catch (Exception e){
            e.printStackTrace();
            result.put("code","400");
            result.put("msg", "srt process failed");
            return  result ;
        }
	}
}
