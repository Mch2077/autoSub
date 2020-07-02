package awesome.team.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import awesome.team.util.HttpUtil;

public class NiutransAPI {

	public static List<Map<String,String>> doGet(List<Map<String,String>> AMap, String to) throws Exception {  
		
		String apikey = "bd87405721e3b00f5652ab028e14734d";
		HttpClient client = new HttpClient();
		
		for (Map<String, String> map : AMap) {
			
			String src = map.get("onebest");
			String result = null;
			String apiurl = "https://free.niutrans.com/NiuTransServer/translation?from=auto&to=" + to + "&apikey=" + apikey +  "&src_text="+URLEncoder.encode(src,"utf-8");
			
			if (null == apiurl || !apiurl.startsWith("http")) {  
				throw new Exception("请求地址格式不对");  
			}
			
			GetMethod method = new GetMethod(apiurl);
			// 设置请求的编码方式  
			method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + "utf-8");  
			
			int statusCode = client.executeMethod(method);
			if (statusCode == HttpStatus.SC_OK) {
				//System.out.println("Method failed: " + method.getStatusLine());// 打印服务器返回的状态  
				// 返回响应消息  
				byte[] responseBody = method.getResponseBodyAsString().getBytes(method.getResponseCharSet());  
				// 在返回响应消息使用编码(utf-8)  
				String response = new String(responseBody, "utf-8");  
				// System.out.println("------------------response:" + response);
				JSONObject json = JSONObject.parseObject(response);
				if (response.indexOf("tgt_text") != -1)
					result = json.get("tgt_text").toString();
			    	map.put("onebest", src + '\n' + result);

			}
			// 释放连接  
			method.releaseConnection();
		}
		return AMap;  
	} 
	

	/**
	 * 翻译函数
	 * @param src_text 待翻译字符串
	 * @return 返回翻译字符串
	 * @throws UnsupportedEncodingException 
	 */
	public static List<Map<String,String>> doPost(List<Map<String,String>> AMap, String to) throws UnsupportedEncodingException{
		String url="https://free.niutrans.com/NiuTransServer/translation";
		
		for (Map<String, String> map : AMap) {
			String src_text = map.get("onebest");
			String tgt_text="输入错误!";
			JSONObject reslut = new JSONObject();
			Map<String, String> param = new HashMap<>();
			param.put("from", "auto");
			param.put("to", to);
			param.put("src_text", src_text);
			param.put("apikey", "bd87405721e3b00f5652ab028e14734d");

			try {
				reslut = JSONObject.parseObject(HttpUtil.post(url, param));
			} catch (Exception e) {
				e.printStackTrace();
			}   
			
			if(reslut.get("tgt_text") != null)
				tgt_text = reslut.get("tgt_text").toString();
			    map.put("onebest", src_text + '\n' + tgt_text);
		}
		System.out.println(AMap.toString());
		return AMap;
	}

	/*
	public static void main(String[] args) throws Exception {
		System.out.println(doGet("你好,今天出去吃饭了么？"));
	}
	*/
}
