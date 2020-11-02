package phis.application.pay.source;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.hibernate.Hibernate;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Hcnservices.HCNWebservices;
import Hcnservices.HCNWebservicesService;
import ctd.account.UserRoleToken;
import ctd.account.organ.OrganController;
import ctd.dictionary.DictionaryController;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.KeyCreator;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

import phis.application.mds.source.MedicineUtils;
import phis.application.tcm.source.TcmModel;
import phis.application.xnh.source.XnhModel;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.SchemaUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.sql.*;
import java.util.Properties; 

public class MobilePaymentModel implements BSPHISEntryNames {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TcmModel.class);

	public MobilePaymentModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 发送xml数据请求到server端
	 * 
	 * @param url
	 *            xml请求数据地址
	 * @param xmlString
	 *            发送的xml数据流
	 * @return null发送失败，否则返回响应内容
	 */
	public String post(String url, String xmlFileName) {
		// 关闭
		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.showdatetime",
				"true");
		System.setProperty(
				"org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient",
				"stdout");

		// 创建httpclient工具对象
		HttpClient client = new HttpClient();
		// 创建post请求方法
		PostMethod myPost = new PostMethod(url);
		// 设置请求超时时间
		client.setConnectionTimeout(300 * 1000);
		String responseString = null;
		try {
			// 设置请求头部类型
			myPost.setRequestHeader("Content-Type", "text/xml");
			myPost.setRequestHeader("charset", "utf-8");

			// 设置请求体，即xml文本内容，注：这里写了两种方式，一种是直接获取xml内容字符串，一种是读取xml文件以流的形式
			// myPost.setRequestBody(xmlString);

			InputStream body = this.getClass().getResourceAsStream(
					"/" + xmlFileName);
			myPost.setRequestBody(body);
			// myPost.setRequestEntity(new
			// StringRequestEntity(xmlString,"text/xml","utf-8"));
			int statusCode = client.executeMethod(myPost);
			if (statusCode == HttpStatus.SC_OK) {
				BufferedInputStream bis = new BufferedInputStream(
						myPost.getResponseBodyAsStream());
				byte[] bytes = new byte[1024];
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				int count = 0;
				while ((count = bis.read(bytes)) != -1) {
					bos.write(bytes, 0, count);
				}
				byte[] strByte = bos.toByteArray();
				responseString = new String(strByte, 0, strByte.length, "utf-8");
				bos.close();
				bis.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		myPost.releaseConnection();
		return responseString;
	}

	/**
	 * 用传统的URL类进行请求
	 * 
	 * @param urlStr
	 */
	public static String sendPost(String urlStr,String paramsXml) {
		try {
			byte[] requestBytes;
			String soapRequestInfo = getXmlInfo(paramsXml);
			requestBytes = soapRequestInfo.getBytes("utf-8");

			HttpClient httpClient = new HttpClient();
			//httpClient.getParams().setContentCharset("UTF-8");
			//httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
			PostMethod postMethod = new PostMethod(urlStr);
			postMethod.setRequestHeader("SOAPAction", "http://tempuri.org/GetMiscInfo");//Soap Action Header!
			//postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"utf-8"); 
			//postMethod.addRequestHeader("Content-type","application/x-www-form-urlencoded; charset=UTF-8");

			InputStream inputStream = new ByteArrayInputStream(requestBytes, 0, requestBytes.length);
			RequestEntity requestEntity = new InputStreamRequestEntity(inputStream, requestBytes.length, "application/soap+xml; charset=utf-8");
			postMethod.setRequestEntity(requestEntity);

			int state = httpClient.executeMethod(postMethod);

			InputStream soapResponseStream = postMethod.getResponseBodyAsStream();
			InputStreamReader inputStreamReader = new InputStreamReader(soapResponseStream, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		
			String responseLine = "";
			String soapResponseInfo = "";
			while((responseLine = bufferedReader.readLine()) != null) {
				soapResponseInfo = soapResponseInfo + responseLine;
			}
			return soapResponseInfo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 用传统的URL类进行请求
	 * 
	 * @param urlStr
	 */
	public static String sendPost2(String urlStr,String paramsXml) {
		try {
			URL url = new URL(urlStr);
			URLConnection con = url.openConnection();
			con.setRequestProperty("Pragma:", "no-cache");
			con.setRequestProperty("Cache-Control", "no-cache");
			con.setRequestProperty("Content-Type", "text/xml"); 
			con.setRequestProperty("ContentType","text/xml;charset=utf-8"); 
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Accept-Language", "en-us,en;q=0.5");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT //5.1)AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.46 Safari/535.11");
			con.setDoOutput(true);
			con.setDoInput(true);

			OutputStreamWriter out = new OutputStreamWriter(
					con.getOutputStream());
			String xmlInfo = getXmlInfo(paramsXml);
			System.out.println("urlStr=" + urlStr);
			// System.out.println("xmlInfo=" + xmlInfo);
			out.write(new String(xmlInfo.getBytes("UTF-8")));
			out.flush();
			out.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String line = "";
			StringBuilder sb = new StringBuilder();
			for (line = br.readLine(); line != null; line = br.readLine()) {
				//System.out.println(line);
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String getXmlInfo(String paramsXml) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<body>");
/*		sb.append("<ip>172.20.16.250</ip>");
		sb.append("<organizationCode>320508466942753</organizationCode>");
		sb.append("<computerName>asus</computerName>");
		sb.append("<hospNo>MZ20180816170854</hospNo>");*/
		sb.append(paramsXml);
		sb.append("</body>");
		return sb.toString();
	}	
	
	public static String doPost(String urlstr,String xmlstr)
	{
		try
		{
			URL url = new URL(urlstr);
			HttpURLConnection connect = (HttpURLConnection) url.openConnection();
			connect.setRequestMethod("POST");
			connect.setDoInput(true);
			connect.setDoOutput(true);
			connect.connect();
			Document doc = null;
			SAXReader reader = new SAXReader();
			
			//xmlstr = "<?xml version='1.0' encoding='gb2312'?><XNHData>	<head>		<sendTime>2009-06-25 19:32:34</sendTime>	</head>	<businessData>		<dataSet size='1'>			<data type='jt401'>				<jt401-01>32062319550706077X</jt401-01>				<yf>1</yf>				<sbsj>2009-06-25</sbsj>				<jgdm>0001</jgdm>			</data>		</dataSet>	</businessData>	<respData time='2099-01-01 09:06:22'>		<code>o</code>		<description>sdfdsfdsfdsf</description>	</respData></XNHData>";
			InputStream in = new ByteArrayInputStream(xmlstr.getBytes("UTF-8"));
			doc = reader.read(in);
			
			/*File f = new File("test","xyjs.xml");
			if(f.exists()){
				doc = reader.read(new FileInputStream(f));
			}*/
			System.out.println(doc.asXML()); 
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connect.getOutputStream(), "UTF-8"));
			out.write(doc.asXML());
			out.flush();
			out.close();			
			InputStream oIns = connect.getInputStream();
			Document d = getDocument(oIns);
			if (d != null)
			{
				System.out.println(d.asXML()); 
				return d.asXML();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private static Document getDocument(InputStream oIns) {
		SAXReader oReader = new SAXReader();
		try {
			return oReader.read(oIns);
		} catch (DocumentException e) {
			System.out.println((new StringBuilder(
					"QueryService Servlet: createXmlDocument: ")).append(
					e.toString()).toString());
		}
		return null;
	}
	public static void main(String[] args)
	{
		String sTmp = "http://223.111.7.25:10001/payRegion/rest/pay/orderquery";
		 String res = sendPost(sTmp,"");
		 System.out.println("获取请求成功："+res); 
		 System.out.println(System.currentTimeMillis());
	}
	
	/**
	 * 支付订单
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */

	@SuppressWarnings("unchecked")
	public Map<String, Object> doPayOrder(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> req = new HashMap<String, Object>();
		Map<String, Object> orderrecord = new HashMap<String, Object>();
		try {
			String APIURL = body.get("APIURL").toString();
			String PAYSERVICE = body.get("PAYSERVICE").toString();
			String IP = body.get("IP").toString();
			String ORGANIZATIONCODE = body.get("ORGANIZATIONCODE").toString();
			String COMPUTERNAME = body.get("COMPUTERNAME").toString();
			String PAYMONEY = body.get("PAYMONEY").toString();
			String VOUCHERNO = body.get("VOUCHERNO").toString();
			String PATIENTYPE = body.get("PATIENTYPE").toString();
			String PATIENTID = body.get("PATIENTID")+"";
			String NAME = body.get("NAME").toString();
			String IDCARD = body.get("IDCARD")==null?"":body.get("IDCARD").toString();
			String BIRTHDAY = "";
			String SEX = "";
			/***根据病人id获取病人身份证号***/
			Map<String, Object> brxx = getBrxx(PATIENTID);
			if(brxx != null && brxx.get("SFZH")!=null){
				IDCARD = brxx.get("SFZH")+"";
				SEX = brxx.get("BRXB")+"";
				BIRTHDAY = brxx.get("CSNY")+"";
			}
/*			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");  
			String time = sdf.format(date);
			String HOSPNO = ((String) body.get("HOSPNO")).replace("YYYYMMDDHHMMSS", time);*/
			String HOSPNO = (String) body.get("HOSPNO");
			String AUTH_CODE = (String) body.get("AUTH_CODE");
			String PAYSOURCE = (String) body.get("PAYSOURCE");
			String TERMINALNO = (String) body.get("TERMINALNO");
			String PAYNO = (String) body.get("PAYNO");
			String COLLECTFEESCODE = (String) body.get("COLLECTFEESCODE");
			String COLLECTFEESNAME = (String) body.get("COLLECTFEESNAME");
			String STATUS = (String) body.get("STATUS");
			String PAYTYPE = (String) body.get("PAYTYPE");
			String paramsxml ="<payService>"+PAYSERVICE+"</payService><ip>"+IP+"</ip><organizationCode>"+ORGANIZATIONCODE+"</organizationCode>" +
					"<computerName>"+COMPUTERNAME+"</computerName><hospNo>"+HOSPNO+"</hospNo><paymoney>"+PAYMONEY+"</paymoney>" +
							"<patientType>"+PATIENTYPE+"</patientType><patientId>"+PATIENTID+"</patientId><name>"+NAME+"</name><sex>"+SEX+"</sex>" +
							"<idcard>"+IDCARD+"</idcard><birthday>"+BIRTHDAY+"</birthday><cardType></cardType>" +
							"<cardNo></cardNo><voucherNO>"+VOUCHERNO+"</voucherNO><payType>"+PAYTYPE+"</payType>" +
							"<auth_code>"+AUTH_CODE+"</auth_code><paySource>"+PAYSOURCE+"</paySource><collectFeesCode>"+COLLECTFEESCODE+"</collectFeesCode>" +
							"<collectFeesName>"+COLLECTFEESNAME+"</collectFeesName>";			
			String res = sendPost(APIURL,paramsxml);
			//String res ="{\"code\":\"200\",\"message\":\"测试\",\"hospNo\":\"GH1536939065582\"}";//测试时使用
			req.put("order", res);//注意此行代码必须放在下面代码之前，以便前台判定
			if(res.contains("\"code\":\"200\"") || res.contains("\"message\":\"等待用户输入密码\"")){
				try{
/*					int  i=2;
					if(i==2){
					 throw new RuntimeException();  //直接手动抛出异常
					}*/					
					orderrecord.put("PAYSERVICE", PAYSERVICE);
					orderrecord.put("IP", IP);
					orderrecord.put("ORGANIZATIONCODE", ORGANIZATIONCODE);
					orderrecord.put("COMPUTERNAME", COMPUTERNAME);
					orderrecord.put("HOSPNO", HOSPNO);
					orderrecord.put("PAYMONEY", Double.parseDouble(PAYMONEY));
					//MS_MZXX.put("QTYS",BSPHISUtil.getDouble(BCTCZFJE+BCDBJZZF+BCMZBZZF+BCDBBXZF, 2));
					orderrecord.put("VOUCHERNO", VOUCHERNO);
					orderrecord.put("PATIENTYPE", PATIENTYPE);
					orderrecord.put("PATIENTID", PATIENTID);
					orderrecord.put("NAME", NAME);
					orderrecord.put("SEX", SEX);
					orderrecord.put("IDCARD", IDCARD);
					orderrecord.put("BIRTHDAY",BSHISUtil.toDate(BIRTHDAY));
					orderrecord.put("PAYTIME",new Date());
					orderrecord.put("VERIFYNO", "");
					orderrecord.put("BANKTYPE", "");
					orderrecord.put("BANKCODE", "");
					orderrecord.put("BANKNO", "");
					orderrecord.put("PAYTYPE", PAYTYPE);
					orderrecord.put("AUTH_CODE", AUTH_CODE);
					orderrecord.put("PAYSOURCE", PAYSOURCE);
					orderrecord.put("TERMINALNO", TERMINALNO);
					orderrecord.put("PAYNO", PAYNO);
					orderrecord.put("COLLECTFEESCODE", COLLECTFEESCODE);
					orderrecord.put("COLLECTFEESNAME", COLLECTFEESNAME);
					orderrecord.put("CARDTYPE", "");
					orderrecord.put("CARDNO", "");
					orderrecord.put("STATUS", STATUS);
					orderrecord.put("SENDXML", Hibernate.createBlob(paramsxml.getBytes("UTF-8")));
					orderrecord.put("RETNRNXML", Hibernate.createBlob("".getBytes("UTF-8")));
					orderrecord.put("return_code", "");
					orderrecord.put("return_msg", "");
					orderrecord.put("TRADENO", "");
					orderrecord.put("TKBZ", "0");
					orderrecord.put("REFUND_FEE", 0);
					orderrecord.put("HOSPNO_ORG", "");
					Map<String, Object> genValue = dao.doSave("create",
							BSPHISEntryNames.PAYRECORD, orderrecord, true);
					long orderid = Long.parseLong(genValue.get("ID") + "");// 获取PAYRECORD的主键
					try{
						doUpdateOrderInfoAfterPaySuccess(orderid,res);
					} catch (Exception e) {
						logger.error("修改HIS库订单记录出错.",	e);
						req.put("hospno", HOSPNO);
						return req;
					}
					req.put("hospno", HOSPNO);
				} catch (Exception e) {
					logger.error("订单支付记录存入HIS库出错.",	e);
					return req;
				}
			}
		} catch (Exception e) {
			logger.error("请求聚合支付平台支付接口.",	e);
			throw new ModelDataOperationException(ServiceCode.CODE_ERROR, "请求聚合支付平台支付接口.");
		}
		return req;
	}
	
	
	/**
	 * 支付成功后更新订单信息，包括回传json、支付宝微信返回的交易号等信息
	 * @param dao
	 * @param FPHM
	 * @param MZXH
	 * @return
	 * @throws Exception
	 */
	public void doUpdateOrderInfoAfterPaySuccess(Long orderid,String res)throws Exception{
		Map<String, Object> orderrecord = new HashMap<String, Object>();
		try {
	        //JSONObject
			Map maps = (Map)JSON.parse(res);
			orderrecord.put("ID", orderid);
			orderrecord.put("RETNRNXML", Hibernate.createBlob(res.getBytes("UTF-8")));
			orderrecord.put("RETURN_CODE", maps.get("code")==null?"":maps.get("code"));
			orderrecord.put("RETURN_MSG", maps.get("message")==null?"":maps.get("message"));
			orderrecord.put("VERIFYNO", maps.get("verifyNo")==null?"":maps.get("verifyNo"));
			Map<String, Object> genValue = dao.doSave("update",
					BSPHISEntryNames.PAYRECORD, orderrecord, true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "支付成功后更新订单信息失败");
		}
	}
	
	/**
	 * 扫码支付退款接口
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doRefund(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> req = new HashMap<String, Object>();
		Map<String, Object> orderrecord = new HashMap<String, Object>();
		try {
			String APIURL = body.get("APIURL")+"";
			String PAYSERVICE = body.get("PAYSERVICE")+"";
			String IP = body.get("IP")+"";
			String ORGANIZATIONCODE = body.get("ORGANIZATIONCODE")+"";
			String COMPUTERNAME = body.get("COMPUTERNAME")+"";
			String PAYMONEY = body.get("PAYMONEY")+"";
			String VOUCHERNO = body.get("VOUCHERNO")+"";
			String PATIENTYPE = body.get("PATIENTYPE")+"";
			String PATIENTID = body.get("PATIENTID")+"";
			String NAME = body.get("NAME")+"";
			String SEX = body.get("SEX")+"";
			String IDCARD =  body.get("IDCARD")+"";
			/***根据病人id获取病人身份证号***/
			Map<String, Object> brxx = getBrxx(PATIENTID);
			if(brxx != null && brxx.get("SFZH")!=null){
				IDCARD = brxx.get("SFZH")+"";
				SEX = brxx.get("BRXB")+"";
			}
			String BIRTHDAY = (String) body.get("BIRTHDAY");
			/*Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");  
			String time = sdf.format(date);
			String HOSPNO = ((String) body.get("HOSPNO")).replace("YYYYMMDDHHMMSS", time);*/
			String HOSPNO = (String) body.get("HOSPNO");
			//String AUTH_CODE = (String) body.get("AUTH_CODE");
			String PAYSOURCE = (String) body.get("PAYSOURCE");
			//String TERMINALNO = (String) body.get("TERMINALNO");
			//String PAYNO = (String) body.get("PAYNO");
			String COLLECTFEESCODE = (String) body.get("COLLECTFEESCODE");
			String COLLECTFEESNAME = (String) body.get("COLLECTFEESNAME");
			String HOSPNO_ORG = (String) body.get("HOSPNO_ORG");
			
			String paramsxml ="<ip>"+IP+"</ip><organizationCode>"+ORGANIZATIONCODE+"</organizationCode>" +
					"<computerName>"+COMPUTERNAME+"</computerName><hospNo>"+HOSPNO_ORG+"</hospNo>" +
							"<voucherNO>"+VOUCHERNO+"</voucherNO><refund_fee>"+PAYMONEY+"</refund_fee>" +
					"<out_request_no>"+HOSPNO+"</out_request_no><collectFeesCode>"+COLLECTFEESCODE+"</collectFeesCode>" +
							"<collectFeesName>"+COLLECTFEESNAME+"</collectFeesName>";
			String res = sendPost(APIURL,paramsxml);
			//String res ="{\"code\":\"200\",\"message\":\"测试\",}";//测试时使用
			req.put("order", res.replace(":\"HOSPNO\"", ":\""+HOSPNO+"\"").replace(",\"verifyNo\":\"VERIFYNO\"", ""));//注意此行代码必须放在下面代码之前，以便前台判定
			if(res.contains("\"code\":\"200\"")){
				try{
/*					int  i=2;
					if(i==2){
					 throw new RuntimeException();  //直接手动抛出异常
					}*/
					orderrecord.put("PAYSERVICE", PAYSERVICE);
					orderrecord.put("IP", IP);
					orderrecord.put("ORGANIZATIONCODE", ORGANIZATIONCODE);
					orderrecord.put("COMPUTERNAME", COMPUTERNAME);
					orderrecord.put("HOSPNO", HOSPNO);
					orderrecord.put("PAYMONEY", Double.parseDouble(PAYMONEY));
					orderrecord.put("VOUCHERNO", VOUCHERNO);
					orderrecord.put("PATIENTYPE", PATIENTYPE);
					orderrecord.put("PATIENTID", PATIENTID);
					orderrecord.put("NAME", NAME);
					orderrecord.put("SEX", SEX);
					orderrecord.put("IDCARD", IDCARD);
					orderrecord.put("BIRTHDAY",BSHISUtil.toDate(BIRTHDAY));
					orderrecord.put("PAYTIME",new Date());
					orderrecord.put("VERIFYNO", "");
					orderrecord.put("BANKTYPE", "");
					orderrecord.put("BANKCODE", "");
					orderrecord.put("BANKNO", "");
					orderrecord.put("PAYSOURCE", PAYSOURCE);
					orderrecord.put("PAYNO", "");
					orderrecord.put("COLLECTFEESCODE", COLLECTFEESCODE);
					orderrecord.put("COLLECTFEESNAME", COLLECTFEESNAME);
					orderrecord.put("CARDTYPE", "");
					orderrecord.put("CARDNO", "");
					orderrecord.put("STATUS", "0");
					orderrecord.put("SENDXML", Hibernate.createBlob(paramsxml.getBytes("UTF-8")));
					orderrecord.put("RETNRNXML", Hibernate.createBlob("".getBytes("UTF-8")));
					orderrecord.put("return_code", "");
					orderrecord.put("return_msg", "");
					orderrecord.put("TRADENO", "");
					orderrecord.put("TKBZ", "1");
					orderrecord.put("REFUND_FEE", 0);
					orderrecord.put("HOSPNO_ORG", HOSPNO_ORG);
					Map<String, Object> genValue = dao.doSave("create",
							BSPHISEntryNames.PAYRECORD, orderrecord, true);
					long orderid = Long.parseLong(genValue.get("ID") + "");// 获取PAYRECORD的主键
					try{
						doUpdateOrderInfoAfterPaySuccess(orderid,res);
					} catch (Exception e) {
						logger.error("修改HIS库退款记录出错.",	e);
						req.put("hospno", HOSPNO);
						return req;
					}
					req.put("hospno", HOSPNO);
				} catch (Exception e) {
					logger.error("退款记录存入HIS库出错.",	e);
					return req;
				}
			}
		} catch (Exception e) {
			logger.error("请求聚合支付平台退款接口.",	e);
			throw new ModelDataOperationException(ServiceCode.CODE_ERROR, "请求聚合支付平台退款接口.");
		}
		return req;
	}
	
	/**
	 * 订单查询接口
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doOrderQuery(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> req = new HashMap<String, Object>();
		try {
			String APIURL = (String) body.get("APIURL");
			String IP = (String) body.get("IP");
			String ORGANIZATIONCODE = (String) body.get("ORGANIZATIONCODE");
			String COMPUTERNAME = (String) body.get("COMPUTERNAME");
			String HOSPNO = (String) body.get("HOSPNO");
			String paramsxml ="<ip>"+IP+"</ip><organizationCode>"+ORGANIZATIONCODE+"</organizationCode>" +
					"<computerName>"+COMPUTERNAME+"</computerName><hospNo>"+HOSPNO+"</hospNo>";
			String res = sendPost(APIURL,paramsxml);
			req.put("order", res);
		} catch (Exception e) {
			logger.error("订单查询.",	e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "订单查询.");
		}
		return req;
	}	

	/**
	 * 支付成功后更新订单信息，包括回传json、支付宝微信返回的交易号等信息
	 * @param 
	 * @return
	 * @throws Exception
	 */
/*	public Map<String, Object> doUpdateOrderInfoAfterPaySuccess(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> req = new HashMap<String, Object>();
		Map<String, Object> orderrecord = new HashMap<String, Object>();
		try {
			String ID = (String) body.get("ID");
			String PAYSERVICE = (String) body.get("RETNRNXML");
			String IP = (String) body.get("RETURN_CODE");
			orderrecord.put("ID", ID);
			orderrecord.put("IP", IP);
			orderrecord.put("ORGANIZATIONCODE", ORGANIZATIONCODE);
			orderrecord.put("COMPUTERNAME", COMPUTERNAME);
			orderrecord.put("HOSPNO", HOSPNO);
			String HOSPNO = (String) body.get("HOSPNO");
			String VERIFYNO = (String) body.get("VERIFYNO");
			String sql="update payrecord set VERIFYNO='"+VERIFYNO+"'  where HOSPNO='"+HOSPNO+"'"  ;
			req.put("data", dao.doSqlUpdate(sql, null));	
			Map<String, Object> genValue = dao.doSave("update",
					BSPHISEntryNames.PAYRECORD, orderrecord, true);
		} catch (Exception e) {
			logger.error("支付成功后更新订单信息.",	e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "支付成功后更新订单信息.");
		}
		return req;
	}	*/

	/**
	 * 支付成功后更新订单状态
	 * @param 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> doUpdateOrderStatus(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> req = new HashMap<String, Object>();
		try {
			String HOSPNO = (String) body.get("HOSPNO");
			String STATUS = (String) body.get("STATUS");
			String sql="update payrecord set STATUS='"+STATUS+"'  where HOSPNO='"+HOSPNO+"'"  ;
			req.put("data", dao.doSqlUpdate(sql, null));
		} catch (Exception e) {
			logger.error("支付成功后更新订单状态.",	e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "支付成功后更新订单状态.");
		}
		return req;
	}	

	/**
	 * 修改支付成功后发票号码
	 * @param dao
	 * @param FPHM
	 * @param MZXH
	 * @return
	 * @throws Exception
	 */
	public int doUpdateFPHM( BaseDAO dao, String FPHM,String MZXH)throws Exception{
		String sql="update  t_p7001 set FPHM='"+FPHM+"'  where mzxh='"+MZXH+"'"  ;
		try {
			return dao.doSqlUpdate(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "修改FPHM失败");
		}
	}
	
	
	/**
	 * 退款查询接口
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */

	@SuppressWarnings("unchecked")
	public Map<String, Object> doRefundQuery(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> req = new HashMap<String, Object>();
		try {
			String APIURL = (String) body.get("APIURL");
			String IP = (String) body.get("IP");
			String ORGANIZATIONCODE = (String) body.get("ORGANIZATIONCODE");
			String COMPUTERNAME = (String) body.get("COMPUTERNAME");
			String HOSPNO = (String) body.get("HOSPNO_ORG");
			String OUT_REQUEST_NO = (String) body.get("HOSPNO");
			String paramsxml ="<ip>"+IP+"</ip><organizationCode>"+ORGANIZATIONCODE+"</organizationCode>" +
					"<computerName>"+COMPUTERNAME+"</computerName><hospNo>"+HOSPNO+"</hospNo>" +
							"<out_request_no>"+OUT_REQUEST_NO+"</out_request_no>";
			String res = sendPost(APIURL,paramsxml);
			req.put("order", res);
		} catch (Exception e) {
			logger.error("退款查询.",	e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "退款查询.");
		}
		return req;
	}
	
	/**
	 * 查询需退款订单信息
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doQueryNeedRefundOrder(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> req = new HashMap<String, Object>();
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		try {
			String APIURL = (String) body.get("APIURL");//获取订单查询接口地址
			String IP =  (String) body.get("IP");
			String COMPUTERNAME = (String) body.get("COMPUTERNAME");			
			String PAYSERVICE = (String) body.get("PAYSERVICE");
			String PAYSERVICE_REFUND = (String) body.get("PAYSERVICE_REFUND");
			String PATIENTID = (String) body.get("PATIENTID");
			String ORGANIZATIONCODE = (String) body.get("ORGANIZATIONCODE");
			String VOUCHERNO = (String) body.get("VOUCHERNO");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("PAYSERVICE",PAYSERVICE);
			parameters.put("PAYSERVICE_REFUND",PAYSERVICE_REFUND);
			parameters.put("PATIENTID",PATIENTID);
			parameters.put("ORGANIZATIONCODE",ORGANIZATIONCODE);
			parameters.put("VOUCHERNO",VOUCHERNO);
			StringBuffer hql = new StringBuffer("select C.HOSPNO,C.PAYMONEY,C.VOUCHERNO,C.ORGANIZATIONCODE," +
					"C.STATUS,C.HOSPNO_REFUND,C.STATUS_REFUND,C.ID_REFUND,C.PATIENTID,C.PATIENTYPE,C.NAME,C.SEX,C.IDCARD,C.BIRTHDAY from (" +
					"select A.HOSPNO,A.PAYMONEY,A.VOUCHERNO,A.ORGANIZATIONCODE,A.STATUS,B.HOSPNO AS HOSPNO_REFUND," +
					"B.STATUS AS STATUS_REFUND,B.ID AS ID_REFUND,A.PATIENTID,A.PATIENTYPE,A.NAME,A.SEX,A.IDCARD,A.BIRTHDAY from (" +
					"select HOSPNO,PAYMONEY,VOUCHERNO,ORGANIZATIONCODE,STATUS,PATIENTID,PATIENTYPE,NAME,SEX,IDCARD,BIRTHDAY from PAYRECORD " +
					"where PATIENTID=:PATIENTID and ORGANIZATIONCODE=:ORGANIZATIONCODE and VOUCHERNO=:VOUCHERNO " +
					"and PAYSERVICE=:PAYSERVICE) A left join (" +
					"select ID,HOSPNO,STATUS,HOSPNO_ORG from (" +
					"select ID,HOSPNO,STATUS,HOSPNO_ORG from PAYRECORD " +
					"where PATIENTID=:PATIENTID and ORGANIZATIONCODE=:ORGANIZATIONCODE and VOUCHERNO=:VOUCHERNO " +
					"and PAYSERVICE=:PAYSERVICE_REFUND order by ID desc) D where rownum=1) B on A.HOSPNO=B.HOSPNO_ORG) C " +
					"where C.HOSPNO_REFUND IS NULL OR C.STATUS_REFUND<>1");
			List<Map<String, Object>> list_order = dao.doSqlQuery(hql.toString(),
					parameters);
			for (Map<String, Object> map_order : list_order) {
				//if(map_order.get("STATUS").equals("0") && (map_order.get("HOSPNO_REFUND") == null || map_order.get("HOSPNO_REFUND").equals(""))){
				if(map_order.get("HOSPNO_REFUND") == null || map_order.get("HOSPNO_REFUND").equals("")){
					map_order.put("APIURL", APIURL);
					map_order.put("IP", IP);
					map_order.put("COMPUTERNAME", COMPUTERNAME);
					//查询订单是否为完成状态
					String returnjson = doQueryOrderStatus(map_order);
/*					if(returnjson.contains("\"50011\"")){//订单已关闭
						continue;
					}*/
					if(returnjson.contains("\"200\"")){
						ret.add(map_order);
						continue;
					}
				}
				else if(map_order.get("STATUS").equals("1") && (map_order.get("HOSPNO_REFUND") == null || map_order.get("HOSPNO_REFUND").equals(""))){
					ret.add(map_order);
					continue;
				}
				else if(map_order.get("HOSPNO_REFUND") != null && !map_order.get("HOSPNO_REFUND").equals("") && (map_order.get("STATUS_REFUND")==null || map_order.get("STATUS_REFUND").equals("0"))){
					ret.add(map_order);
					continue;
				}
			}
			req.put("orders", ret);
		} catch (Exception e) {
			logger.error("查询需退款订单信息.",	e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "查询需退款订单信息.");
		}
		return req;
	}
	
	/**
	 * 查询订单状态（退号或发票作废时使用）
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public String doQueryOrderStatus(Map<String, Object> map_order)throws Exception{
		try {
			String APIURL = (String) map_order.get("APIURL");
			String IP = (String) map_order.get("IP");
			String ORGANIZATIONCODE = (String) map_order.get("ORGANIZATIONCODE");
			String COMPUTERNAME = (String) map_order.get("COMPUTERNAME");
			String HOSPNO = (String) map_order.get("HOSPNO");
			String paramsxml ="<ip>"+IP+"</ip><organizationCode>"+ORGANIZATIONCODE+"</organizationCode>" +
					"<computerName>"+COMPUTERNAME+"</computerName><hospNo>"+HOSPNO+"</hospNo>";
			return sendPost(APIURL,paramsxml);
		} catch (Exception e) {
			e.printStackTrace();
			//throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "查询订单状态失败");
			return "";
		}
	}	
	
	/**
	 * 获取病人信息
	 * @param brid
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getBrxx(String brid)throws Exception{
		try {
			return dao.doLoad(MS_BRDA, brid);
		} catch (PersistentDataOperationException e) {
			logger.error("load failed.", e);
			return null;
		}
	}
	
	public void getDzMx(Map<String, Object> req,
			Map<String, Object> res, Context ctx)throws Exception{	
		Map<String, Object> body = (Map<String, Object>) req.get("body");
/*		String dzrq = (String) req.get("dzrq");
		String zzrq = (String) req.get("zzrq");
		String dzlx = (String) req.get("dzlx");*/
		String dzrq = (String) body.get("dzrq");
		String zzrq = (String) body.get("zzrq");
		String dzlx = (String) body.get("dzlx");
		int pageSize = 300;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		int total = 0;
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId() + "";
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		Connection conn=null;
        Statement stmt = null;
        //驱动，服务器地址，登录用户名，密码
        String DBURL = DictionaryController.instance().getDic("phis.dictionary.JhzfptDbInfo").getText("URL");
        String DBUID = DictionaryController.instance().getDic("phis.dictionary.JhzfptDbInfo").getText("USER");
        String DBPWD = DictionaryController.instance().getDic("phis.dictionary.JhzfptDbInfo").getText("PASSWORD");     
    	try{
    		//1.加载驱动程序
	        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	        //2.获得数据库的连接
	        conn= (Connection)DriverManager.getConnection(DBURL,DBUID,DBPWD);
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }		
		//java.sql.Connection conn = DBUtil.getConnection();
		if(conn==null){
			res.put("errormsg", "支付平台数据库连接异常！");
			return;
		}
		try{
			//Statement stmt = conn.createStatement();
			String hql = "select top "+pageSize+" * from (select row_number() over (order by paytime desc) as rownumber, * from (" +
					"select hospno,case when verifyno is null then '' when verifyno='null' then '' else verifyno end as verifyno," +
					"convert(varchar(50),paytime,120) as paytime,payservice,case when paysource is null then 1 else paysource end as paysource,paytype,paymoney," +
					"patienttype,patientid,name,sex,convert(varchar(100),birthday,23) as birthday,case when idcard is null then '' when idcard='null' then '' else idcard end as idcard," +
					"collectfeescode,collectfeesname,computername,ip,voucherno,refundflag,status from payrecord " +
					"where organizationcode='"+jgid+"' and collectfeescode='"+uid+"' and payservice='"+dzlx+"' and paytime>='"+dzrq+"' " +
					"and paytime<='"+zzrq+" 23:59:59' ) t) t WHERE RowNumber > "+(pageSize*first);

			String countHql = "select count(*)  as num from payrecord " +
					"where organizationcode='"+jgid+"' and collectfeescode='"+uid+"' and payservice='"+dzlx+"' and paytime>='"+dzrq+"' " +
					"and paytime<='"+zzrq+" 23:59:59'";
			//hql = "select top 1 id,paymoney,paytime,name from payrecord";
			//ResultSet rs = stmt.executeQuery(hql);
			//ResultSet rs = DBUtil.executeQuery(hql);
			stmt = conn.createStatement();
			ResultSet rscount = stmt.executeQuery(countHql);
	        while(rscount.next()){//如果对象中有数据，就会循环打印出来
	        	total = rscount.getInt("num");
	        }
			ResultSet rs = stmt.executeQuery(hql);
			List<Map<String,Object>> records=new ArrayList<Map<String,Object>>();
	        while(rs.next()){//如果对象中有数据，就会循环打印出来
				Map<String,Object> temp=new HashMap<String, Object>();
	/*            System.out.println(rs.getInt("examRequestID")+","+rs.getString("SQBH")+","+rs.getInt("his_sqdh"));
				temp.put("EXAMREQUESTID", rs.getInt("examRequestID"));
				temp.put("SQBH", rs.getString("SQBH"));
				temp.put("HIS_SQDH", rs.getInt("his_sqdh"));*/
				temp.put("HOSPNO", rs.getString("hospno"));
				temp.put("VERIFYNO", rs.getString("verifyno"));
				temp.put("PAYTIME", rs.getString("paytime"));
				temp.put("PAYSERVICE", rs.getString("payservice"));
				temp.put("PAYSOURCE", rs.getString("paysource"));
				temp.put("PAYTYPE", rs.getString("paytype"));
				temp.put("PAYMONEY", rs.getString("paymoney"));
				temp.put("PATIENTTYPE", rs.getString("patienttype"));
				temp.put("PATIENTID", rs.getString("patientid"));
				temp.put("NAME", rs.getString("name"));
				temp.put("SEX", rs.getString("sex"));
				temp.put("BIRTHDAY", rs.getString("birthday"));
				temp.put("IDCARD", rs.getString("idcard"));
				temp.put("COLLECTFEESCODE", rs.getString("collectfeescode"));
				temp.put("COLLECTFEESNAME", rs.getString("collectfeesname"));
				temp.put("COMPUTERNAME", rs.getString("computername"));
				temp.put("IP", rs.getString("ip"));
				temp.put("VOUCHERNO", rs.getString("voucherno"));
				temp.put("REFUNDFLAG", rs.getString("refundflag"));
				temp.put("STATUS", rs.getString("status"));
				records.add(temp);
	        }
			records=SchemaUtil.setDictionaryMassageForList(records,
					"phis.application.pay.schemas.MOBILEPAY_MXDZ");
			res.put("body", records);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
		}
		catch(Exception e){
	        e.printStackTrace();
		}finally{
			//DBUtil.close(conn);
			conn.close();
		}
	}
	
	public void mobilepaydz(Map<String, Object> body,Map<String, Object> res)throws Exception{	
		String dzrq = (String) body.get("dzrq");
		String zzrq = (String) body.get("zzrq");
		String dzlx = (String) body.get("dzlx");
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId() + "";
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		List<Map<String,Object>> storedatas=(List<Map<String,Object>>)body.get("data");
		List<Map<String,Object>> dzjg=new ArrayList<Map<String,Object>>();
		try{
			String hql = "SELECT A.HOSPNO,CASE WHEN A.VERIFYNO IS NULL THEN '' WHEN A.VERIFYNO='NULL' THEN '' ELSE A.VERIFYNO END AS VERIFYNO,A.PAYTIME," +
					"A.PAYSERVICE,A.PAYSOURCE,A.PAYTYPE,A.PAYMONEY,A.PATIENTYPE,A.PATIENTID," +
					"A.NAME,CASE WHEN A.IDCARD IS NULL THEN '' WHEN A.IDCARD='NULL' THEN '' ELSE A.IDCARD END AS IDCARD,A.COLLECTFEESCODE," +
					"A.COLLECTFEESNAME,A.VOUCHERNO AS FPHM,B.HOSPNO AS HOSPNO_TF,C.MZXH AS YWID,C.ZFPB " +
					"FROM PAYRECORD A LEFT JOIN PAYRECORD B ON A.HOSPNO=B.HOSPNO_ORG AND A.ORGANIZATIONCODE=B.ORGANIZATIONCODE " +
					"LEFT JOIN MS_MZXX C ON A.VOUCHERNO=C.FPHM AND A.ORGANIZATIONCODE=C.JGID AND A.PATIENTID=C.BRID " +
					"WHERE A.ORGANIZATIONCODE =:JGID AND A.COLLECTFEESCODE =:UID AND A.PAYSERVICE =:DZLX " +
					"AND A.PAYTIME >= TO_DATE('"+dzrq+" 00:00:00','yyyy-mm-dd hh24:mi:ss') " +
					"AND A.PAYTIME <= TO_DATE('"+zzrq+" 23:59:59','yyyy-mm-dd hh24:mi:ss') ";
			if(dzlx.equals("1")){//挂号扫码付
				hql ="SELECT A.HOSPNO,CASE WHEN A.VERIFYNO IS NULL THEN '' WHEN A.VERIFYNO='NULL' THEN '' ELSE A.VERIFYNO END AS VERIFYNO,A.PAYTIME," +
						"A.PAYSERVICE,A.PAYSOURCE,A.PAYTYPE,A.PAYMONEY,A.PATIENTYPE,A.PATIENTID," +
						"A.NAME,CASE WHEN A.IDCARD IS NULL THEN '' WHEN A.IDCARD='NULL' THEN '' ELSE A.IDCARD END AS IDCARD,A.COLLECTFEESCODE," +
						"A.COLLECTFEESNAME,A.VOUCHERNO AS FPHM,B.HOSPNO AS HOSPNO_TF,D.SBXH AS YWID,D.THBZ AS ZFPB " +
						"FROM PAYRECORD A LEFT JOIN PAYRECORD B ON A.HOSPNO=B.HOSPNO_ORG AND A.ORGANIZATIONCODE=B.ORGANIZATIONCODE " +
						"LEFT JOIN MS_GHMX D ON A.VOUCHERNO=D.JZHM AND A.ORGANIZATIONCODE=D.JGID AND A.PATIENTID=D.BRID " +
						"WHERE A.ORGANIZATIONCODE =:JGID AND A.COLLECTFEESCODE =:UID AND A.PAYSERVICE =:DZLX " +
						"AND A.PAYTIME >= TO_DATE('"+dzrq+" 00:00:00','yyyy-mm-dd hh24:mi:ss') " +
						"AND A.PAYTIME <= TO_DATE('"+zzrq+" 23:59:59','yyyy-mm-dd hh24:mi:ss') ";
			}else if(dzlx.equals("3")){//住院预交金扫码付
				hql = "SELECT A.HOSPNO,CASE WHEN A.VERIFYNO IS NULL THEN '' WHEN A.VERIFYNO='NULL' THEN '' ELSE A.VERIFYNO END AS VERIFYNO,A.PAYTIME," +
						"A.PAYSERVICE,A.PAYSOURCE,A.PAYTYPE,A.PAYMONEY,A.PATIENTYPE,A.PATIENTID," +
						"A.NAME,CASE WHEN A.IDCARD IS NULL THEN '' WHEN A.IDCARD='NULL' THEN '' ELSE A.IDCARD END AS IDCARD,A.COLLECTFEESCODE," +
						"A.COLLECTFEESNAME,A.VOUCHERNO AS FPHM,B.HOSPNO AS HOSPNO_TF,D.ZYH AS YWID,D.ZFPB AS ZFPB " +
						"FROM PAYRECORD A LEFT JOIN PAYRECORD B ON A.HOSPNO=B.HOSPNO_ORG AND A.ORGANIZATIONCODE=B.ORGANIZATIONCODE " +
						"LEFT JOIN zy_tbkk D ON A.VOUCHERNO=D.SJHM AND A.ORGANIZATIONCODE=D.JGID " +
						"WHERE A.ORGANIZATIONCODE =:JGID AND A.COLLECTFEESCODE =:UID AND A.PAYSERVICE =:DZLX " +
						"AND A.PAYTIME >= TO_DATE('"+dzrq+" 00:00:00','yyyy-mm-dd hh24:mi:ss')" +
						"AND A.PAYTIME <= TO_DATE('"+zzrq+" 23:59:59','yyyy-mm-dd hh24:mi:ss') ";
			}
			List<Map<String,Object>> records=new ArrayList<Map<String,Object>>();
			Map<String,Object> parameters= new HashMap<String, Object>();
			parameters.put("JGID",jgid);
			parameters.put("UID",uid);
			parameters.put("DZLX",dzlx);
			records=dao.doSqlQuery(hql, parameters);
			for(Map<String,Object> data:storedatas){
				boolean findRecord = false;
				for(Map<String,Object> record:records){
					String status = (data.get("STATUS")==null?"":data.get("STATUS").toString());
					//支付平台订单交易失败
					if(status.equals("3")){
						data.put("DZJG", "8");
						dzjg.add(data);
						findRecord=true;
						break;
					}
					if((record.get("HOSPNO")+"").equals(data.get("HOSPNO")+"")){
						String hospno_tf = (record.get("HOSPNO_TF")==null?"":record.get("HOSPNO_TF").toString());
						String ywid = (record.get("YWID")==null?"":record.get("YWID").toString());
						String zfpb = (record.get("ZFPB")==null?"":record.get("ZFPB").toString());
						//支付平台已支付医院已结算
						if(hospno_tf.equals("") && !ywid.equals("") && zfpb.equals("0")){
							data.put("DZJG", "1");
							dzjg.add(data);
							findRecord=true;
							break;
						}
						//支付平台已支付医院已作废（需要调用退费接口将费用退还给病人）
						if(hospno_tf.equals("") && !ywid.equals("") && zfpb.equals("1")){
							data.put("DZJG", "2");
							dzjg.add(data);
							findRecord=true;
							break;
						}
						//支付平台已支付医院有支付记录无结算记录（需要调用退费接口将费用退还给病人）
						if(hospno_tf.equals("") && ywid.equals("") && zfpb.equals("")){
							data.put("DZJG", "3");
							dzjg.add(data);
							findRecord=true;
							break;
						}
						//支付平台已退款医院未作废（需要将HIS端发票进行作废）
						if(!hospno_tf.equals("") && !ywid.equals("") && zfpb.equals("0")){
							data.put("DZJG", "5");
							dzjg.add(data);
							findRecord=true;
							break;
						}
						//支付平台已退款医院已作废
						if(!hospno_tf.equals("") && !ywid.equals("") && zfpb.equals("1")){
							data.put("DZJG", "6");
							dzjg.add(data);
							findRecord=true;
							break;
						}
					}
				}
				//HIS中未找到任何记录
				if(!findRecord){
					//支付平台已退款医院无记录
					if((data.get("REFUNDFLAG")+"").equals("2")){//refundflag=2 表示已退款
						data.put("DZJG", "7");
						dzjg.add(data);
					}
					//支付平台已支付医院无记录（需要调用退费接口将费用退还给病人）
					else{
						data.put("DZJG", "4");
						dzjg.add(data);
					}
				}
			}
			dzjg=SchemaUtil.setDictionaryMassageForList(dzjg,
					"phis.application.pay.schemas.MOBILEPAY_MXDZ");
			res.put("body", dzjg);
		}
		catch(Exception e){
	        e.printStackTrace();
		}
	}
	
	public void mobilepaycz(Map<String, Object> body,Map<String, Object> res)throws Exception{
/*		String p ="<ip>192.168.9.71</ip><organizationCode>320124003</organizationCode>" +
				"<computerName>DPSF01</computerName><hospNo>SF2018101013563010363362382</hospNo>";
		String res_test = sendPost("http://223.111.7.25:10001/payRegion/rest/pay/orderquery",p);
		return;
		res.put("code", "200");
		res.put("msg","退款成功！");*/
		
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid=user.getUserId();
		String jgid = user.getManageUnitId();
		Integer dzjg =Integer.parseInt(body.get("DZJG").toString());
		int error = 0;
		String msg = "";
		switch(dzjg){	
			case 2://支付平台已支付医院已作废（需要调用退费接口将费用退还给病人）			
			case 3://支付平台已支付医院有支付记录无结算记录（需要调用退费接口将费用退还给病人）			
			case 4://支付平台已支付医院无记录（需要调用退费接口将费用退还给病人）
				try {
					//调用聚合支付平台订单查询接口查询状态
					//String APIURL = "http://192.168.10.155:8081/payRegion/rest/pay/orderquery";
					//String APIURL = "http://223.111.7.25:10001/payRegion/rest/pay/orderquery";
					String APIURL = DictionaryController.instance().getDic("phis.dictionary.JhzfptDbInfo").getText("ORDERQUERY");
					String IP = body.get("IP").toString();
					String COMPUTERNAME = body.get("COMPUTERNAME").toString();
					String HOSPNO = body.get("HOSPNO").toString();
					String paramsxml ="<ip>"+IP+"</ip><organizationCode>"+jgid+"</organizationCode>" +
							"<computerName>"+COMPUTERNAME+"</computerName><hospNo>"+HOSPNO+"</hospNo>";
					String res_orderquery = sendPost(APIURL,paramsxml);
					//String res_orderquery = "\"code\":\"200\",\"成功。\"";
					if(res_orderquery==null){
						error=1;
						msg ="支付平台中未查询到该订单信息，请与管理员联系！";
						throw new ModelDataOperationException(ServiceCode.CODE_ERROR, "支付平台中未查询到该订单信息，请与管理员联系！");
					}
/*					if(res_orderquery.contains("\"code\":\"50017\"")){
						error=1;
						msg ="订单已关闭无法退款！";
						throw new ModelDataOperationException(ServiceCode.CODE_ERROR, "订单已关闭无法退款！");
					}*/
					if(!res_orderquery.contains("\"code\":\"200\"")){
						error=1;
						msg ="支付平台返回"+res_orderquery;
						throw new ModelDataOperationException(ServiceCode.CODE_ERROR, "支付平台返回"+res_orderquery);
					}
					String PAYSERVICE = "-"+body.get("PAYSERVICE").toString();
					String PAYMONEY = (String) body.get("PAYMONEY");
					String VOUCHERNO = (String) body.get("VOUCHERNO");
					String PATIENTYPE = (String) body.get("PATIENTTYPE");
					String PATIENTID = (String) body.get("PATIENTID");
					String NAME = (String) body.get("NAME");
					String SEX = (String) body.get("SEX");
					String IDCARD = (String) body.get("IDCARD");
					String BIRTHDAY = (String) body.get("BIRTHDAY");
					String HOSPNO_ORG = HOSPNO;
					if(HOSPNO.startsWith("GH")){
						HOSPNO = "TH"+HOSPNO.replace("GH", "");
					}else{
						HOSPNO = "TF"+HOSPNO.replace("SF", "");
					}
					String PAYSOURCE = (String) body.get("PAYSOURCE");
					String COLLECTFEESCODE = (String) body.get("COLLECTFEESCODE");
					String COLLECTFEESNAME = (String) body.get("COLLECTFEESNAME");
					
					//APIURL = "http://192.168.10.155:8081/payRegion/rest/pay/refund";
					//APIURL = "http://223.111.7.25:10001/payRegion/rest/pay/refund";
					APIURL = DictionaryController.instance().getDic("phis.dictionary.JhzfptDbInfo").getText("REFUND");
					paramsxml ="<ip>"+IP+"</ip><organizationCode>"+jgid+"</organizationCode>" +
							"<computerName>"+COMPUTERNAME+"</computerName><hospNo>"+HOSPNO_ORG+"</hospNo>" +
									"<voucherNO>"+VOUCHERNO+"</voucherNO><refund_fee>"+PAYMONEY+"</refund_fee>" +
							"<out_request_no>"+HOSPNO+"</out_request_no><collectFeesCode>"+COLLECTFEESCODE+"</collectFeesCode>" +
									"<collectFeesName>"+COLLECTFEESNAME+"</collectFeesName>";
					String res_refund = sendPost(APIURL,paramsxml);
					//String res_refund = "\"code\":\"200\",\"退款成功。\"";
					if(!res_refund.contains("\"code\":\"200\"")){
						error=1;
						msg ="支付平台返回"+res_refund;
						throw new ModelDataOperationException(ServiceCode.CODE_ERROR, "支付平台返回"+res_refund);
					}
					Map<String, Object> orderrecord = new HashMap<String, Object>();
						try{
							orderrecord.put("PAYSERVICE", PAYSERVICE);
							orderrecord.put("IP", IP);
							orderrecord.put("ORGANIZATIONCODE", jgid);
							orderrecord.put("COMPUTERNAME", COMPUTERNAME);
							orderrecord.put("HOSPNO", HOSPNO);
							orderrecord.put("PAYMONEY", Double.parseDouble(PAYMONEY));
							orderrecord.put("VOUCHERNO", VOUCHERNO);
							orderrecord.put("PATIENTYPE", PATIENTYPE);
							orderrecord.put("PATIENTID", PATIENTID);
							orderrecord.put("NAME", NAME);
							orderrecord.put("SEX", SEX);
							orderrecord.put("IDCARD", IDCARD);
							orderrecord.put("BIRTHDAY",BSHISUtil.toDate(BIRTHDAY));
							orderrecord.put("PAYTIME",new Date());
							orderrecord.put("VERIFYNO", "");
							orderrecord.put("BANKTYPE", "");
							orderrecord.put("BANKCODE", "");
							orderrecord.put("BANKNO", "");
							orderrecord.put("PAYSOURCE", PAYSOURCE);
							orderrecord.put("PAYNO", "");
							orderrecord.put("COLLECTFEESCODE", COLLECTFEESCODE);
							orderrecord.put("COLLECTFEESNAME", COLLECTFEESNAME);
							orderrecord.put("CARDTYPE", "");
							orderrecord.put("CARDNO", "");
							orderrecord.put("STATUS", "0");
							orderrecord.put("SENDXML", Hibernate.createBlob(paramsxml.getBytes("UTF-8")));
							orderrecord.put("RETNRNXML", Hibernate.createBlob("".getBytes("UTF-8")));
							orderrecord.put("return_code", "");
							orderrecord.put("return_msg", "");
							orderrecord.put("TRADENO", "");
							orderrecord.put("TKBZ", "1");
							orderrecord.put("REFUND_FEE", 0);
							orderrecord.put("HOSPNO_ORG", HOSPNO_ORG);
							
							Map<String, Object> genValue = dao.doSave("create",
									BSPHISEntryNames.PAYRECORD, orderrecord, true);
							long orderid = Long.parseLong(genValue.get("ID") + "");// 获取PAYRECORD的主键
							try{
								doUpdateOrderInfoAfterPaySuccess(orderid,res_refund);
							} catch (Exception e) {
								error=1;
								msg ="退款成功，但修改HIS库退款记录出错："+e.getMessage();
								logger.error("退款成功，但修改HIS库退款记录出错.",	e);
							}
							res.put("hospno", HOSPNO);
						} catch (Exception e) {
							error=1;
							msg ="退款成功，但退款记录存入HIS库出错："+e.getMessage();
							logger.error("退款成功，但退款记录存入HIS库出错.",	e);
						}
				} catch (Exception e) {
					error=1;
					msg ="退款失败："+e.getMessage();
					logger.error("退款失败.",	e);
					//throw new ModelDataOperationException(ServiceCode.CODE_ERROR, "请求聚合支付平台退款接口.");
				}
				finally{
					if(error==0){
						res.put("code", "200");
						res.put("msg","退款成功！");
					}else{
						res.put("code", "502");
						res.put("msg",msg);
					}
				}
				break;
			default:
		}
	}
}
