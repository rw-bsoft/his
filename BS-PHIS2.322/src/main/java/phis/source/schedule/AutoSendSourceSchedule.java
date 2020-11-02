package phis.source.schedule;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.SimpleFormatter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.service.configure.DicConfig;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class AutoSendSourceSchedule extends AbstractJobSchedule {
	private static final Logger logger = LoggerFactory.getLogger(AutoSendSourceSchedule.class);
	@Override
	public void doJob(BaseDAO dao,Context ctx) throws ModelDataOperationException {
		if(1==1)
			return;
		StringBuffer sql=new StringBuffer();
		sql.append("select a.HYXH as HYXH,a.SBXH as SBXH from MS_GHMX a")
		.append(" where a.JZZT in (1,2,9) and a.HYXH is not null ")
		.append(" and (a.TOAPP is null or a.TOAPP<>'1') and a.THBZ=0 and ROWNUM<100");
		try{
			StringBuffer sb=new StringBuffer();
			sb.append("<updateOrderInfo>");
			List<Map<String,Object>> jzl=dao.doSqlQuery(sql.toString(),null);
			if(jzl.size()>0){
				for(Map<String,Object> o:jzl){
					dao.doSqlUpdate("update MS_GHMX set TOAPP='1' where SBXH="+o.get("SBXH"),null);
					sb.append("<request><yyid>"+o.get("HYXH")+"</yyid>")
					.append("<workid>"+o.get("SBXH")+"</workid><state>1</state></request>");
				}
			}
			sb.append("</updateOrderInfo>");
			String re=postTowebService("hcnInterface.registrationService","confirm",sb.toString());
			try{
				Document document = DocumentHelper.parseText(re);
				Element element0 = document.getRootElement();
				Element code = element0.element("code");
				if(code.getTextTrim().equals("200")){
					logger.info(new Date()+"同步已就诊预约挂号信息成功！");
				}else{
					logger.info(new Date()+"同步已就诊预约挂号信息失败！");
				}
			}catch(DocumentException e){
				e.printStackTrace();
			}
		}catch(PersistentDataOperationException e){
			e.printStackTrace();
		}
	}
	public static String postTowebService(String service,String method,String params){
		DictionaryController d=DictionaryController.instance();
		try{
			Dictionary fs=d.get("phis.dictionary.fsyypzxx");
			String wsUrl=fs.getItem("wsUrl").getText();
			String appId=fs.getItem("appId").getText();
			String pwd=fs.getItem("pwd").getText();
			try{
				URL wurl=new URL(wsUrl);
				try{
					HttpURLConnection conn=(HttpURLConnection)wurl.openConnection();
					conn.setDoInput(true);
			        conn.setDoOutput(true);
			        conn.setRequestMethod("POST");
			        conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
			        OutputStream os = conn.getOutputStream();
			        String soap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v2=\"http://www.bsoft.com/bs-hcn/api/v2.0\">"+
			        	"   <soapenv:Header/>"+
			        	"   <soapenv:Body>"+
			        	"      <v2:invoke>"+
			        	"         <appId>"+appId+"</appId>"+
			        	"         <pwd>"+pwd+"</pwd>"+
			        	"         <service>"+service+"</service>"+
			        	"         <method>"+method+"</method>"+
			        	"         <params>"+
			        	"            <item><![CDATA["+params+"]]></item>"+
			        	"         </params>"+
			        	"      </v2:invoke>"+
			        	"   </soapenv:Body>"+
			        	"</soapenv:Envelope>";
			        os.write(soap.getBytes("UTF-8"));
			        InputStream is = conn.getInputStream();
			        byte[] b = new byte[1024];
			        int len = 0;
			        String s = "";
			        while((len = is.read(b)) != -1){
			            String ss = new String(b,0,len,"UTF-8");
			            s += ss;
			        }
			        s=s.replaceAll("&lt;","<");
			        s=s.replaceAll("&gt;",">");
			        String re=s.substring(s.indexOf("<return>")+8,s.indexOf("</return>"));
			        is.close();
			        os.close();
			        conn.disconnect();
			        return re;
				}catch(IOException e){
					e.printStackTrace();
				}
			}catch(MalformedURLException e){
				e.printStackTrace();
			}
		}catch(ControllerException e){
			e.printStackTrace();
		}
		return "";
	}
}
