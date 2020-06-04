package main.java.awesome.team.util;
import com.alibaba.fastjson.JSONArray;

import java.util.List;
import java.util.Map;
public class BaiduAPI {
    private static final String APP_ID = "20200522000465460";
    private static final String SECURITY_KEY = "3WW9O64uf7yrnmApM9YT";
    /*
     一下是String to的取值：
    语言简写       名称
    auto         自动检测
    zh           中文
    en           英语
    yue          粤语
    wyw          文言文
    jp           日语
    kor          韩语
    fra          法语
    spa          西班牙语
    th           泰语
    ara          阿拉伯语
    ru           俄语
    pt           葡萄牙语
    de           德语
    it           意大利语
    el           希腊语
    nl           荷兰语
    pl           波兰语
    bul          保加利亚语
    est          爱沙尼亚语
    dan          丹麦语
    fin          芬兰语
    cs           捷克语
    rom          罗马尼亚语
    slo          斯洛文尼亚语
    swe          瑞典语
    hu           匈牙利语
    cht          繁体中文
    vie          越南语
    */
	public static List<Map<String,String>> translate(List<Map<String,String>> listObjectFir, String to){
		 TransApi api = new TransApi(APP_ID, SECURITY_KEY);
		 for(Map<String,String> mapList : listObjectFir){ 
			 String Need = mapList.get("onebest");
		     String[] str =Need.split("\n");
//			 System.out.println(str[0]);
			 Map<String,String> m = null;
			 m = (Map)JSONArray.parse(api.getTransResult(str[0], "auto",to));
			 //if(isCN)m = (Map)JSONArray.parse(api.getTransResult(str[0], "zh",to));
			 //else m = (Map)JSONArray.parse(api.getTransResult(str[0], "en",to));			 
             Object ob = m.get("trans_result");
			 String tmp = String.valueOf(ob);;
			 tmp = subNeedString(tmp);
			 Map<String,String> res = (Map)JSONArray.parse(tmp);
			 String result = res.get("dst");
			 /*
			 以下是保留的作为key的tran：
			 mapList.put("tran",result); 
		     */

			 mapList.put("onebest", result);//直接覆盖
			 /*
			  * 仅存一个翻译串跟在原文后：
				 mapList.put("onebest",str[0]+"\n"+result);
			  */
			 
     }
		return listObjectFir;
	}
	private static String subNeedString(String a) {
		String res = "";
		for(int i = 1 ; i < a.length() - 1; ++i)res += a.charAt(i);
		return res;
	}
}