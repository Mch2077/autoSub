package awesome.team.service;

import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

public interface VideoService {
	JSONObject videoUpload(MultipartFile file, boolean isCN);
}
