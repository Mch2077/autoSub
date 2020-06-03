package awesome.team.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import awesome.team.domain.ApiResultDto;
import awesome.team.util.EncryptUtil;
import awesome.team.util.HttpUtil;
import awesome.team.util.SliceIdGenerator;

/*
  通过IfasrAPI.getListMap(文件路径) 可获得 调用科大讯飞API语音转文字的List<Map<String,String>>
  其中：
  bg : 代表着以视频开始为开始，这句话开始说的时间，单位为 ms
  ed : 代表着以视频开始为开始，这句话结束的时间，单位为 ms
  onebest : 这句话的内容
  speaker ： 说这句话的人的编号，默认值为0
 */

public class IfasrAPI {

    public static final String LFASR_HOST = "http://raasr.xfyun.cn/api";

    /**
     * TODO 设置appid和secret_key
     */
    public static final String APPID = "5ec2cccc";
    public static final String SECRET_KEY = "46c6eaeb2148f6694bbf6a679e149ce3";

    public static final String PREPARE = "/prepare";
    public static final String UPLOAD = "/upload";
    public static final String MERGE = "/merge";
    public static final String GET_RESULT = "/getResult";
    public static final String GET_PROGRESS = "/getProgress";

    /**
     * 文件分片大小,可根据实际情况调整
     */
    public static final int SLICE_SICE = 10485760;// 10M

    public static List<Map<String,String>> getListMap(String filePath ,boolean isCN) {
        File audio = new File(filePath);
        List<Map<String,String>> listObjectFir;
        try (FileInputStream fis = new FileInputStream(audio)) {
            // 预处理
            String taskId = prepare(audio , isCN);
  //          System.out.println(taskId + "???");
            // 分片上传文件
            int len = 0;
            byte[] slice = new byte[SLICE_SICE];
            SliceIdGenerator generator = new SliceIdGenerator();
            while ((len =fis.read(slice)) > 0) {
                // 上传分片
                if (fis.available() == 0) {
                    slice = Arrays.copyOfRange(slice, 0, len);
                }
                uploadSlice(taskId, generator.getNextSliceId(), slice);
            }

            // 合并文件
            merge(taskId);

            // 轮询获取任务结果
            while (true) {
                try {
                    System.out.println("sleep a while Zzz" );
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ApiResultDto taskProgress = getProgress(taskId);
                if (taskProgress.getOk() == 0) {
                    if (taskProgress.getErr_no() != 0) {
                        System.out.println("任务失败：" + JSON.toJSONString(taskProgress));
                    }

                    String taskStatus = taskProgress.getData();
                    if (JSON.parseObject(taskStatus).getInteger("status") == 9) {                    
                        System.out.println("任务完成！");
                        break;
                    }

                    System.out.println("任务处理中：" + taskStatus);
                } else {
                    System.out.println("获取任务进度失败！");
                }
            }
            // 获取结果
            listObjectFir = (List<Map<String,String>>) JSONArray.parse(getResult(taskId));
            return listObjectFir;
     //       System.out.println("利用JSONArray中的parse方法来解析json数组字符串");
  /*          for(Map<String,String> mapList : listObjectFir){
                for (Map.Entry entry : mapList.entrySet()){
                   System.out.println( entry.getKey()  + "  " +entry.getValue());
                }
            }
            System.out.println("\r\n\r\n转写结果: " + getResult(taskId));*/
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
		return null;
    }

    /**
     * 获取每个接口都必须的鉴权参数
     * 
     * @return
     * @throws SignatureException 
     */
    public static Map<String, String> getBaseAuthParam(String taskId) throws SignatureException {
        Map<String, String> baseParam = new HashMap<String, String>();
        String ts = String.valueOf(System.currentTimeMillis() / 1000L);
        baseParam.put("app_id", APPID);
        baseParam.put("ts", ts);
        baseParam.put("signa", EncryptUtil.HmacSHA1Encrypt(EncryptUtil.md5(APPID + ts), SECRET_KEY));
        if (taskId != null) {
            baseParam.put("task_id", taskId);
        }
  //      System.out.println(baseParam);
        return baseParam;
    }

    /**
     * 预处理
     * 
     * @param audio     需要转写的音频
     * @return
     * @throws SignatureException 
     */
    public static String prepare(File audio , boolean isCN) throws SignatureException {
        Map<String, String> prepareParam = getBaseAuthParam(null);
        long fileLenth = audio.length();

        prepareParam.put("file_len", fileLenth + "");
        prepareParam.put("file_name", audio.getName());
        prepareParam.put("slice_num", (fileLenth/SLICE_SICE) + (fileLenth % SLICE_SICE == 0 ? 0 : 1) + "");
        if(!isCN)prepareParam.put("language","en");
        /********************TODO 可配置参数********************/
        // 转写类型
//        prepareParam.put("lfasr_type", "0");
        // 开启分词
//        prepareParam.put("has_participle", "true");
        // 说话人分离
//        prepareParam.put("has_seperate", "true");
        // 设置多候选词个数
//        prepareParam.put("max_alternatives", "2");
        // 是否进行敏感词检出
//        prepareParam.put("has_sensitive", "true");
        // 敏感词类型
//        prepareParam.put("sensitive_type", "1");
        // 关键词
//        prepareParam.put("keywords", "科大讯飞,中国");
        /****************************************************/

        String response = HttpUtil.post(LFASR_HOST + PREPARE, prepareParam);
        System.out.println(response);
        if (response == null) {
            throw new RuntimeException("预处理接口请求失败！");
        }
        ApiResultDto resultDto = JSON.parseObject(response, ApiResultDto.class);
        String taskId = resultDto.getData();
        if (resultDto.getOk() != 0 || taskId == null) {
            throw new RuntimeException("预处理失败！" + response);
        }

        System.out.println("预处理成功, taskid：" + taskId);
        return taskId;
    }

    /**
     * 分片上传
     * 
     * @param taskId        任务id
     * @param slice         分片的byte数组
     * @throws SignatureException 
     */
    public static void uploadSlice(String taskId, String sliceId, byte[] slice) throws SignatureException {
        Map<String, String> uploadParam = getBaseAuthParam(taskId);
        uploadParam.put("slice_id", sliceId);

        String response = HttpUtil.postMulti(LFASR_HOST + UPLOAD, uploadParam, slice);
        if (response == null) {
            throw new RuntimeException("分片上传接口请求失败！");
        }
        if (JSON.parseObject(response).getInteger("ok") == 0) {
            System.out.println("分片上传成功, sliceId: " + sliceId + ", sliceLen: " + slice.length);
            return;
        }

        System.out.println("params: " + JSON.toJSONString(uploadParam));
        throw new RuntimeException("分片上传失败！" + response + "|" + taskId);
    }

    /**
     * 文件合并
     * 
     * @param taskId        任务id
     * @throws SignatureException 
     */
    public static void merge(String taskId) throws SignatureException {
        String response = HttpUtil.post(LFASR_HOST + MERGE, getBaseAuthParam(taskId));
        if (response == null) {
            throw new RuntimeException("文件合并接口请求失败！");
        }
        if (JSON.parseObject(response).getInteger("ok") == 0) {
            System.out.println("文件合并成功, taskId: " + taskId);
            return;
        }

        throw new RuntimeException("文件合并失败！" + response);
    }

    /**
     * 获取任务进度
     * 
     * @param taskId        任务id
     * @throws SignatureException 
     */
    public static ApiResultDto getProgress(String taskId) throws SignatureException {
        String response = HttpUtil.post(LFASR_HOST + GET_PROGRESS, getBaseAuthParam(taskId));
        if (response == null) {
            throw new RuntimeException("获取任务进度接口请求失败！");
        }

        return JSON.parseObject(response, ApiResultDto.class);
    }

    /**
     * 获取转写结果
     * 
     * @param taskId
     * @return
     * @throws SignatureException 
     */
    public static String getResult(String taskId) throws SignatureException {
        String responseStr = HttpUtil.post(LFASR_HOST + GET_RESULT, getBaseAuthParam(taskId));
        if (responseStr == null) {
            throw new RuntimeException("获取结果接口请求失败！");
        }
        ApiResultDto response = JSON.parseObject(responseStr, ApiResultDto.class);
        if (response.getOk() != 0) {
            throw new RuntimeException("获取结果失败！" + responseStr);
        }
        return response.getData();
    }
}