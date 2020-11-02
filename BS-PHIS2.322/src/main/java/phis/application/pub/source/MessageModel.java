/**
 * @(#)PublicModel.java Created on 2012-1-12 上午9:43:35
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.pub.source;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;   
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;   
import org.dom4j.Element; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ctd.util.context.Context;
import phis.source.ModelDataOperationException;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.validator.ValidateException;

/**
 * @description 短信服务
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class MessageModel {

	private static final Logger logger = LoggerFactory
			.getLogger(MessageModel.class);

	protected BaseDAO dao = null;

	/**
	 * 
	 * @param dao
	 */
	public MessageModel(BaseDAO dao) {
		this.dao = dao;
	}

	public MessageModel() {
	}
	
	/**
	 * 发送短信
	 * 
	 * @param
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void doSendMessage(Map<String, Object> res,Context ctx)
			throws ModelDataOperationException, ValidateException {
		//HashMap<String, Object> req = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		if(jgid.length()<9){
			return;
		}
        //短信驱动，服务器地址，登录用户名，密码
        String url = DictionaryController.instance().getDic("phis.dictionary.MessageService").getText("url");
        String username = DictionaryController.instance().getDic("phis.dictionary.MessageService").getText("username");
        String pwd = DictionaryController.instance().getDic("phis.dictionary.MessageService").getText("pwd");
        String phones = DictionaryController.instance().getDic("phis.dictionary.MessageService").getText(jgid.substring(0,9));
        String[] arr_phone = phones.split(",");
        if(arr_phone.length==0){
        	return;
        }

/*		DictionaryItem dicJg = DictionaryController.instance().getDic("phis.dictionary.MessageService").getItem(jgid);
		if(dicJg!=null){
			String name = dicJg.getProperty("name").toString();
		}*/
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
		String curDate = dateFormat.format(new Date());
	    String content = new String("您于" + curDate + "，收到一条法定传染病报告卡待审核通知，请尽快处理！");
	    
		HttpClient client = new HttpClient(); 
		//设置代理服务器的ip地址和端口  
        String ip = DictionaryController.instance().getDic("phis.dictionary.MessageService").getText("ip");
        String port = DictionaryController.instance().getDic("phis.dictionary.MessageService").getText("port");
		client.getHostConfiguration().setProxy(ip, Integer.parseInt(port));
		//使用抢先认证  
		client.getParams().setAuthenticationPreemptive(true);  
		PostMethod method = new PostMethod(url);
		client.getParams().setContentCharset("GBK");
		method.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=GBK");

		String successSend = "", failSend = "";
	    for(String phonenum : arr_phone){		    	
			NameValuePair[] data = {//提交短信
				    new NameValuePair("account", username), //查看用户名 登录用户中心->验证码通知短信>产品总览->API接口信息->APIID
				    new NameValuePair("password", pwd), //查看密码 登录用户中心->验证码通知短信>产品总览->API接口信息->APIKEY
				    //new NameValuePair("password", util.StringUtil.MD5Encode("密码")),
				    new NameValuePair("mobile", phonenum), 
				    new NameValuePair("content", content),
			};
			method.setRequestBody(data);
			try {
				client.executeMethod(method);			
				String SubmitResult =method.getResponseBodyAsString();
				//System.out.println(SubmitResult);
				Document doc = DocumentHelper.parseText(SubmitResult);
				Element root = doc.getRootElement();
				String code = root.elementText("code");
				String msg = root.elementText("msg");
				String smsid = root.elementText("smsid");
	
				System.out.println(code);
				System.out.println(msg);
				System.out.println(smsid);
	
				 if("2".equals(code)){
					System.out.println("短信提交成功");
					successSend += phonenum + ",";
				}
				 else{
					 failSend += msg+"("+phonenum+"),";
				 }
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				 failSend += "调用短信接口异常("+phonenum+","+e.getMessage()+"),";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				 failSend += "调用短信接口异常("+phonenum+","+e.getMessage()+"),";
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				 failSend += "调用短信接口异常("+phonenum+","+e.getMessage()+"),";
			}
	    }
		Map<String, Object> rebody =  new HashMap<String, Object>();
		rebody.put("successSend", (successSend.equals("")?successSend:successSend.substring(0,successSend.length()-1)));
		rebody.put("failSend", (failSend.equals("")?failSend:failSend.substring(0,failSend.length()-1)));
		res.put("body", rebody);
	}
	
	/**
	 * 发送短信
	 * 
	 * @param
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void doSendJYLYMessage(Map<String, Object> req, Map<String, Object> res,Context ctx)
			throws ModelDataOperationException, ValidateException {
		//HashMap<String, Object> req = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		if(jgid.length()<9){
			return;
		}
        //短信驱动，服务器地址，登录用户名，密码
        String url = DictionaryController.instance().getDic("phis.dictionary.MessageService").getText("url");
        String username = DictionaryController.instance().getDic("phis.dictionary.MessageService").getText("username");
        String pwd = DictionaryController.instance().getDic("phis.dictionary.MessageService").getText("pwd");
        String phones = (String) body.get("phone");
        String operatorUnit_text = "("+(String) body.get("operatorUnit_text")+")";
        String serviceItems = (String)body.get("serviceItems");     
        String serviceDesc = (String)body.get("serviceDesc");
        String contentDesc = serviceDesc + operatorUnit_text;
        String[] arr_phone = phones.split(",");
		String successSend = "", failSend = "";
        if(arr_phone.length==0){
        	return;
        }
		if(serviceDesc.trim().length()==0){
			failSend ="服务内容不能为空";
		}else {

	/*		DictionaryItem dicJg = DictionaryController.instance().getDic("phis.dictionary.MessageService").getItem(jgid);
			if(dicJg!=null){
				String name = dicJg.getProperty("name").toString();
			}*/
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
				String curDate = dateFormat.format(new Date());
				String content = new String("日期：" + curDate + "，内容：" + contentDesc + "。");

				HttpClient client = new HttpClient();
				//设置代理服务器的ip地址和端口
				String ip = DictionaryController.instance().getDic("phis.dictionary.MessageService").getText("ip");
				String port = DictionaryController.instance().getDic("phis.dictionary.MessageService").getText("port");
				client.getHostConfiguration().setProxy(ip, Integer.parseInt(port));
				//使用抢先认证
				client.getParams().setAuthenticationPreemptive(true);
				PostMethod method = new PostMethod(url);
				client.getParams().setContentCharset("GBK");
				method.setRequestHeader("ContentType", "application/x-www-form-urlencoded;charset=GBK");


			for(String phonenum : arr_phone){
				NameValuePair[] data = {//提交短信
						new NameValuePair("account", username), //查看用户名 登录用户中心->验证码通知短信>产品总览->API接口信息->APIID
						new NameValuePair("password", pwd), //查看密码 登录用户中心->验证码通知短信>产品总览->API接口信息->APIKEY
						//new NameValuePair("password", util.StringUtil.MD5Encode("密码")),
						new NameValuePair("mobile", phonenum),
						new NameValuePair("content", content),
				};
				method.setRequestBody(data);
				try {
					client.executeMethod(method);
					String SubmitResult =method.getResponseBodyAsString();
					//System.out.println(SubmitResult);
					Document doc = DocumentHelper.parseText(SubmitResult);
					Element root = doc.getRootElement();
					String code = root.elementText("code");
					String msg = root.elementText("msg");
					String smsid = root.elementText("smsid");

					System.out.println(code);
					System.out.println(msg);
					System.out.println(smsid);

					 if("2".equals(code)){
						System.out.println("短信提交成功");
						successSend += phonenum + ",";
					}
					 else{
						 failSend += msg+"("+phonenum+"),";
					 }
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					 failSend += "调用短信接口异常("+phonenum+","+e.getMessage()+"),";
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					 failSend += "调用短信接口异常("+phonenum+","+e.getMessage()+"),";
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					 failSend += "调用短信接口异常("+phonenum+","+e.getMessage()+"),";
				}
			}
		}
		Map<String, Object> rebody =  new HashMap<String, Object>();
		rebody.put("successSend", (successSend.equals("")?successSend:successSend.substring(0,successSend.length()-1)));
		rebody.put("failSend", (failSend.equals("")?failSend:failSend.substring(0,failSend.length()-1)));
		res.put("body", rebody);
	}
}
