package platform.monitor.source;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.util.AppContextHolder;
import ctd.util.JSONUtils;
import ctd.util.S;
import ctd.util.annotation.RpcService;
import ctd.util.xml.XMLHelper;

public class LogInfoUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogInfoUtils.class);
	private final String entityName="SYS_MessageLog";
	@RpcService
	public void onSubscribeMessage(Object message){
		HashMap<String,Object> a=(HashMap<String, Object>) message;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class);
		Session ss=null;
		try {
			ss=sf.openSession();
			String send=S.obj2String(a.get("SendDate"));
			a.put("SendDate", df.parse(S.obj2String(a.get("SendDate"))));
			if(!StringUtils.isEmpty(S.obj2String(a.get("ReceiveDate")))){
				a.put("ReceiveDate", a.get("ReceiveDate"));
			}
			a.put("IsSuccess",S.obj2String(a.get("IsSuccess")));
			ss.save(entityName,a);
			ss.flush();
		} catch (Exception e) {
			LOGGER.error("Save message log error!",e);
		}finally{
			if(ss.isOpen()){
				ss.close();
			}
		}
	}
	
	public Document dic(){
		Document doc = XMLHelper.createDocument();
		Element root = doc.addElement("dic");
		SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class);
		Session ss=null;
		try {
			ss=sf.openSession();
			Query q=ss.createQuery("select distinct Sender,Receiver from SYS_MessageLog ");
			List<Object[]> list=q.list();
			HashMap<String,Set<String>> nodes=new HashMap<String,Set<String>>();
			for(Object[] objects:list){
				for(Object o:objects){
					String node=S.obj2String(o);
					if(!StringUtils.isEmpty(node)){
						String[] s=node.split("@");
						if(nodes.containsKey(s[0])){
							nodes.get(s[0]).add(s[1]);
						}else{
							Set<String> ips=new HashSet<String>();
							ips.add(s[1]);
							nodes.put(s[0],ips);
						}
					};
				}
			}
			Dictionary domain=DictionaryController.instance().get("platform.reg.dictionary.domain");
			for(String key:nodes.keySet()){
				String domainName=domain.getText(key);
				Element item=root.addElement("item").addAttribute("key",key).addAttribute("text",domainName.equals("")?key:domainName);
				Set<String> ips=nodes.get(key);
				for(String ip:ips){
					item.addElement("item").addAttribute("key",ip).addAttribute("text", ip);
				}
			}
		} catch (Exception e) {
			LOGGER.error("loading dic[messageLogDic] error!",e);
		}finally{
			if(ss.isOpen()){
				ss.close();
			}
		}
		return doc;
	}

}
