package chis.source.mobilempi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.util.JsonUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jfree.util.ObjectList;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.common.entity.JSONUtil;

import ctd.service.core.ServiceException;

public class Httpclient {
	private static final Logger logger = LoggerFactory
			.getLogger(Httpclient.class);
	public  String sendtofjzlapp(String job_number,String service_id,String category)
			throws ServiceException, IOException {
		
		String url="http://32.24.200.187:8080/rest";
		Map<String, String> params = new HashMap<String, String>();
//		long timestamp=System.currentTimeMillis();
		 params.put("method","lifesea.cmy.service.record.add");
		 params.put("job_number",job_number);
		 params.put("service_id",service_id);
		 params.put("category",category);
		 CloseableHttpClient httpclient = HttpClients.createDefault();
		 String body = null;
		 HttpPost post = postForm(url, params);
	     RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();//设置请求和传输超时时间
	     post.setConfig(requestConfig);
		 body = invoke(httpclient, post);
	     httpclient.close();
	     JSONObject msg;
		try {
			msg = new JSONObject(body);
			System.out.println(msg);
			if(msg.get("code").toString().equals("0")){
				return "1";
			}else{
				return "2";
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		return "2";
		}
	public  String send()
			throws ServiceException, IOException {
		
		String url="http://localhost:8080/myproject/jkda_services";
		Map<String, String> params = new HashMap<String, String>();
//		long timestamp=System.currentTimeMillis();
//		 params.put("method","post");
		 params.put("idcard","");
		 params.put("createUnit","");
		 params.put("personname","杨洋");
		 CloseableHttpClient httpclient = HttpClients.createDefault();
		 String body = null;
		 HttpPost post = postForm(url, params);
	     RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();//设置请求和传输超时时间
	     post.setConfig(requestConfig);
//	     post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		 body = invoke(httpclient, post);
	     httpclient.close();
	     JSONObject msg;
		try {
			String ss=new String(body.getBytes("ISO-8859-1"),"utf-8");
			msg = new JSONObject(ss);
			System.out.println(msg);
			if(msg.get("code").toString().equals("0")){
				return "1";
			}else{
				return "2";
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		return "2";
		}
	private static HttpPost postForm(String url, Map<String, String> params){
		
		HttpPost httpost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList <NameValuePair>();
		
		Set<String> keySet = params.keySet();
		for(String key : keySet) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}
		
		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return httpost;
	}
	private static String invoke(CloseableHttpClient httpclient,
			HttpUriRequest httpost) {
		
		HttpResponse response = sendRequest(httpclient, httpost);
		String body=paseResponse(response);
		return body;
	}
	private static HttpResponse sendRequest(CloseableHttpClient httpclient,
			HttpUriRequest httpost) {
		HttpResponse response = null;
		
		try {
			response = httpclient.execute(httpost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
	private static String paseResponse(HttpResponse response) {
		HttpEntity entity = response.getEntity();
		String charset = EntityUtils.getContentCharSet(entity);
		String body = null;
		try {
			body = EntityUtils.toString(entity);
			//log.info(body);
		} catch (org.apache.http.ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	//	String message=new String(body.getBytes("ISO-8859-1"),"utf-8");
		return body;
	}
	 public static void main(String[] args) throws ServiceException, IOException {
		 Httpclient a =new Httpclient();
	 a.send();
//	 String b=a.sendtofjzlapp("4040", "0000000000075327", "202","0");
//	 System.out.println(b);
//	 JSONObject j;
//	try {
//		j = new JSONObject(b);
//		System.out.println(j.get("code"));
//	} catch (JSONException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	 
	 
	 
    }
}
