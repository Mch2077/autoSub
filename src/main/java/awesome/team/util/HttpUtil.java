package awesome.team.util;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.util.ArrayList;
import java.util.List;

public class HttpUtil {

	public static List<NameValuePair> convertMapToPair(Map<String, String> params) {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return pairs;
	}

	public static String post(String url, Map<String, String> param) {
	    String result = null;
		CloseableHttpResponse httpResponse = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(convertMapToPair(param), "utf-8"));
			httpResponse = httpClient.execute(httpPost);
			result = EntityUtils.toString(httpResponse.getEntity());
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
			try {
				if (httpResponse != null) {
					httpResponse.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

	public static String postMulti(String url, Map<String, String> param, byte[] body) {
		String result = null;
		CloseableHttpResponse httpResponse = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);

		MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
		reqEntity.addPart("content", new ByteArrayBody(body, ContentType.DEFAULT_BINARY, param.get("slice_id")));

		for (Map.Entry<String, String> entry : param.entrySet()) {
			StringBody value = new StringBody(entry.getValue(), ContentType.create("text/plain", Consts.UTF_8));
			reqEntity.addPart(entry.getKey(), value);
		}
		HttpEntity httpEntiy = reqEntity.build();

		try {
			httpPost.setEntity(httpEntiy);
			httpResponse = httpClient.execute(httpPost);
			result = EntityUtils.toString(httpResponse.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
			    e.printStackTrace();
			}
		}
		return result;
	}
	
	
	 protected static final int SOCKET_TIMEOUT = 10000; // 10S
	    protected static final String GET = "GET";

	    public static String get(String host, Map<String, String> params) {
	        try {
	            // 设置SSLContext
	            SSLContext sslcontext = SSLContext.getInstance("TLS");
	            sslcontext.init(null, new TrustManager[] { myX509TrustManager }, null);

	            String sendUrl = getUrlWithQueryString(host, params);

	            // System.out.println("URL:" + sendUrl);

	            URL uri = new URL(sendUrl); // 创建URL对象
	            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
	            if (conn instanceof HttpsURLConnection) {
	                ((HttpsURLConnection) conn).setSSLSocketFactory(sslcontext.getSocketFactory());
	            }

	            conn.setConnectTimeout(SOCKET_TIMEOUT); // 设置相应超时
	            conn.setRequestMethod(GET);
	            int statusCode = conn.getResponseCode();
	            if (statusCode != HttpURLConnection.HTTP_OK) {
	                System.out.println("Http错误码：" + statusCode);
	            }

	            // 读取服务器的数据
	            InputStream is = conn.getInputStream();
	            BufferedReader br = new BufferedReader(new InputStreamReader(is));
	            StringBuilder builder = new StringBuilder();
	            String line = null;
	            while ((line = br.readLine()) != null) {
	                builder.append(line);
	            }

	            String text = builder.toString();

	            close(br); // 关闭数据流
	            close(is); // 关闭数据流
	            conn.disconnect(); // 断开连接

	            return text;
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (KeyManagementException e) {
	            e.printStackTrace();
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        }

	        return null;
	    }

	    public static String getUrlWithQueryString(String url, Map<String, String> params) {
	        if (params == null) {
	            return url;
	        }

	        StringBuilder builder = new StringBuilder(url);
	        if (url.contains("?")) {
	            builder.append("&");
	        } else {
	            builder.append("?");
	        }

	        int i = 0;
	        for (String key : params.keySet()) {
	            String value = params.get(key);
	            if (value == null) { // 过滤空的key
	                continue;
	            }

	            if (i != 0) {
	                builder.append('&');
	            }

	            builder.append(key);
	            builder.append('=');
	            builder.append(encode(value));

	            i++;
	        }

	        return builder.toString();
	    }

	    protected static void close(Closeable closeable) {
	        if (closeable != null) {
	            try {
	                closeable.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }

	    /**
	     * 对输入的字符串进行URL编码, 即转换为%20这种形式
	     * 
	     * @param input 原文
	     * @return URL编码. 如果编码失败, 则返回原文
	     */
	    public static String encode(String input) {
	        if (input == null) {
	            return "";
	        }

	        try {
	            return URLEncoder.encode(input, "utf-8");
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }

	        return input;
	    }

	    private static TrustManager myX509TrustManager = new X509TrustManager() {

	        @Override
	        public X509Certificate[] getAcceptedIssuers() {
	            return null;
	        }

	        @Override
	        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	        }

	        @Override
	        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	        }
	    };
}