package phis.application.znts.source;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import org.dom4j.DocumentException;

import phis.application.lis.source.ModelOperationException;
import phis.application.znts.source.webservice.impl.OuterEncryServiceImplServiceLocator;
import phis.application.znts.source.webservice.impl.OuterEncryServiceImplServiceSoapBindingStub;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;
import phis.source.BSPHISEntryNames;
import phis.source.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ZntsModel implements BSPHISEntryNames {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ZntsModel.class);

	public ZntsModel(BaseDAO dao) {
		this.dao = dao;
	}

			/**
			 * 获取url页面内容
			 * 
			 * @param body
			 * @param ctx
			 * @return
			 * @throws ModelDataOperationException
			 */
			@SuppressWarnings("unchecked")
			public Map<String, Object> doGetUrlPageContent(Map<String, Object> body,
					Context ctx) throws ModelDataOperationException {
				Map<String, Object> req = new HashMap<String, Object>();
				try {
					//获取东软健康档案浏览器url
					String url=DictionaryController.instance().getDic("phis.dictionary.drwebservice").getText("url"); 
					String pdata = body.get("pdata") + "";
					if(pdata.contains("<yljgdm></yljgdm>")){
						UserRoleToken user = UserRoleToken.getCurrent();
						Map<String,Object> parameters= new HashMap<String, Object>();
						parameters.put("JGID",user.getManageUnitId() + "");
						Map<String,Object> record= dao.doSqlLoad("select WDID as WDID from WD_JGID where JGID=:JGID", parameters);					
						if(record != null){
							pdata = pdata.replace("<yljgdm></yljgdm>", "<yljgdm>"+record.get("WDID")+"</yljgdm>");
						}
					}
					OuterEncryServiceImplServiceLocator service = new OuterEncryServiceImplServiceLocator();
					service.setOuterEncryServiceImplPortEndpointAddress(url);
					OuterEncryServiceImplServiceSoapBindingStub os = (OuterEncryServiceImplServiceSoapBindingStub)service.getOuterEncryServiceImplPort();
					String tagUrl = os.encryXmlUrl(pdata);
					req.put("tagUrl", tagUrl);
					req.put("msg", "");
				} catch (Exception e) {
					logger.error("获取url页面内容.",	e);
					req.put("msg", e);
					//throw new ModelDataOperationException(ServiceCode.CODE_ERROR, "获取url页面内容.");
				}
				return req;
			}

			/**
			 * 获取妇幼保健内嵌页面url
			 * 
			 * @param body
			 * @param ctx
			 * @return
			 * @throws ModelDataOperationException
			 */
			@SuppressWarnings("unchecked")
			public Map<String, Object> doGetFybjUrlPage(Map<String, Object> body,
					Context ctx) throws PersistentDataOperationException,ModelDataOperationException {
				Map<String, Object> req = new HashMap<String, Object>();
				UserRoleToken user = UserRoleToken.getCurrent();
				try {
					List<Map<String,Object>> records=new ArrayList<Map<String,Object>>();
					Map<String,Object> parameters= new HashMap<String, Object>();
					parameters.put("PERSONID",user.getUserId() + "");					
					records= dao.doSqlQuery("select CARDNUM as CARDNUM,MOBILE as MOBILE from SYS_PERSONNEL where PERSONID=:PERSONID", parameters);					
					if(records == null || records.size() == 0){
						req.put("tagUrl", "");
						req.put("msg", "");
						return req;
					}
					for (Map<String, Object> record : records) {
						req.put("idcard", (record.get("CARDNUM")==null?"":record.get("CARDNUM").toString()));
						req.put("phonenum", (record.get("MOBILE")==null?"":record.get("MOBILE").toString()));
						break;
					}
					//获取妇幼保健url
					String url=DictionaryController.instance().getDic("phis.dictionary.drwebservice").getText("fybjurl");
					req.put("tagUrl", url);
					req.put("msg", "");
				} catch (Exception e) {
					logger.error("获取妇幼保健内嵌页面url.",	e);
					req.put("msg", e);
					//throw new ModelDataOperationException(ServiceCode.CODE_ERROR, "获取url页面内容.");
				}
				return req;
			}
			
			/**
			 * 查询需推送到省中医馆平台的草药信息条数
			 * 
			 * @param body
			 * @param ctx
			 * @return
			 * @throws ModelDataOperationException
			 */
			@SuppressWarnings("unchecked")
			public Map<String, Object> doSocketSend(Map<String, Object> body,
					Context ctx) throws ModelDataOperationException {
				Map<String, Object> req = new HashMap<String, Object>();
		    	try{
			    	//客户端
			    	//1、创建客户端Socket，指定服务器地址和端口
			    		//Socket socket =new Socket("127.0.0.1",5026);
			    	Socket socket =new Socket("192.168.20.11",5026);
			    	//2、获取输出流，向服务器端发送信息
			    	OutputStream os = socket.getOutputStream();//字节输出流
			    	PrintWriter pw =new PrintWriter(os);//将输出流包装成打印流
			    	pw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?><YL_ACTIVE_ROOT><CFDDM>3102</CFDDM><KH>9113000102625092</KH><KLX>05</KLX><YLJGDM>111111111111111</YLJGDM><JZLX>13</JZLX><YYKSBM>3.01</YYKSBM><YYYSGH>1001</YYYSGH><YSXM>张三</YSXM><AGENTIP>10.1.7.11</AGENTIP><AGENTMAC>ff-ff-ff-ff-ff</AGENTMAC><ZD><ITEM><ZDBM>E14.408</ZDBM><ZDMC>糖尿病性周围神经病</ZDMC><BMLX>01</BMLX></ITEM></ZD></YL_ACTIVE_ROOT>");
			    	pw.flush();
			    	socket.shutdownOutput();
			    	//3、获取输入流，并读取服务器端的响应信息
			    	InputStream is = socket.getInputStream();
			    	BufferedReader br = new BufferedReader(new InputStreamReader(is));
			    	String info = null;
			    	while((info=br.readLine())!=null){
			    		System.out.println("我是客户端，服务器说："+info);
			    	}
			    	//4、关闭资源
			    	br.close();
			    	is.close();
			    	pw.close();
			    	os.close();
			    	socket.close();
		    	}
		    	catch(UnknownHostException e){
		    		e.printStackTrace();
		    	}catch (IOException e) {
		    		e.printStackTrace();
		    	}
				return req;
			}
}
