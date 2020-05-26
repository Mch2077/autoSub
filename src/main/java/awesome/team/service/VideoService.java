package awesome.team.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface VideoService {
	Map<String,String> videoUpload(MultipartFile file);
}
