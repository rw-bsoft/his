package chis.source.gis;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.web.context.WebApplicationContext;

import ctd.service.core.Service;
import ctd.util.context.Context;

public class RoleService implements Service {

	private static Log logger = LogFactory.getLog(RoleService.class);

	private static Context myCtx = null;

	@SuppressWarnings("unused")
	public void execute(Map<String,Object> jsonReq, Map<String,Object> jsonRes, Context ctx){
		myCtx = ctx;
		WebApplicationContext wac = (WebApplicationContext) ctx
				.get("_applicationContext");
		String user=(String) jsonReq.get("user");
		Map<String,Object> returnObj= getUser(user,jsonRes);
		if(returnObj==null)
		{
			jsonRes.put(RES_CODE, 500);
		}
		jsonRes.put("body",returnObj);
	}

	@SuppressWarnings("rawtypes")
	public Map<String,Object> getUser(String userName,Map<String,Object> jsonRes) {
		String Path = this.getClass().getClassLoader().getResource("/").getPath() + "config/gis/";
		Map<String,Object> userObj = new HashMap<String,Object>();
		try {
			Document roleDoc = read(Path+"RoleList.xml");
			Document userDoc = read(Path+"UserList.xml");
			Element roleList = getRootElement(roleDoc);
			Element userList = getRootElement(userDoc);
			for (Iterator i = userList.elementIterator(); i.hasNext();) {
				Element users = (Element) i.next();
				String userID = users.attributeValue("id");
				for (Iterator j = users.elementIterator(); j.hasNext();) {
					Element user = (Element) j.next();
					if (user.attributeValue("username").equals(userName)) {
						for (Iterator it = roleList.elementIterator(); it.hasNext();) {
							Element role = (Element) it.next();
							if (role.attributeValue("id").equals(userID)) {
								userObj.put("layerID", role.attributeValue("layerID"));
							}
						}
						userObj.put("RegionCode", user.attributeValue("id"));
						userObj.put("username", userName);
						return userObj;
					}
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Document read(String fileName) throws MalformedURLException,
			DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(fileName));
		return document;
	}

	public Element getRootElement(Document doc) {
		return doc.getRootElement();
	}

	public void init() throws ServletException {
		// Put your code here
	}

}
