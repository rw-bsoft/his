package chis.source.gis;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.web.context.WebApplicationContext;
import chis.source.PersistentDataOperationException;
import ctd.util.context.Context;

public class ServiceUtil {

	@SuppressWarnings("rawtypes")
	public static List executeSql(String hql, Context ctx)
			throws PersistentDataOperationException {
		Session ss = null;
		List list = null;
		try {
			WebApplicationContext wac = (WebApplicationContext) ctx
					.get("_applicationContext");
			SessionFactory sf = (SessionFactory) wac
					.getBean("mySessionFactory");
			ss = sf.openSession();
			Query q = ss.createQuery(hql);
			list = q.list();
		} catch (Exception e) {
			throw new PersistentDataOperationException(e);
		} finally {
			if (ss != null && ss.isOpen())
				ss.close();
		}
		return list;
	}

	public static HashMap<String,Object> executeSqlObj(String hql, Context ctx)
			throws PersistentDataOperationException {
		Session ss = null;
		HashMap<String,Object> listObj = new HashMap<String,Object>();
		try {
			WebApplicationContext wac = (WebApplicationContext) ctx
					.get("_applicationContext");
			SessionFactory sf = (SessionFactory) wac
					.getBean("mySessionFactory");
			ss = sf.openSession();
			ScrollableResults q = ss.createQuery(hql).scroll();
			while (q.next()) {
				listObj.put(q.getString(0), q.getString(1));
			}
		} catch (Exception e) {
			throw new PersistentDataOperationException(e);
		} finally {
			if (ss != null && ss.isOpen())
				ss.close();
		}
		return listObj;
	}

	public static HashMap<String,Object> executeSqlAy(String hql, Context ctx,
			String KPICode, String kpiCodeText) throws PersistentDataOperationException {
		Session ss = null;
		HashMap<String,Object> listObj = new HashMap<String,Object>();
		try {
			WebApplicationContext wac = (WebApplicationContext) ctx
					.get("_applicationContext");
			SessionFactory sf = (SessionFactory) wac
					.getBean("mySessionFactory");
			ss = sf.openSession();
			ScrollableResults q = ss.createQuery(hql).scroll();
			while (q.next()) {
				listObj.put(q.getString(0), q.getString(1));
				listObj.put("type", KPICode);
				listObj.put("type_text", kpiCodeText);
			}

		} catch (Exception e) {
			throw new PersistentDataOperationException(e);
		} finally {
			if (ss != null && ss.isOpen())
				ss.close();
		}
		return listObj;
	}

	public static HashMap<String,Object> executeReportSql(String hql, Context ctx)
			throws PersistentDataOperationException {
		Session ss = null;
		HashMap<String,Object> listObj = new HashMap<String,Object>();
		try {
			WebApplicationContext wac = (WebApplicationContext) ctx
					.get("_applicationContext");
			SessionFactory sf = (SessionFactory) wac
					.getBean("mySessionFactory");
			ss = sf.openSession();
			ScrollableResults q = ss.createQuery(hql).scroll();
			while (q.next()) {
				listObj.put((String) q.get(0), q.get(1));
			}

		} catch (Exception e) {
			throw new PersistentDataOperationException(e);
		} finally {
			if (ss != null && ss.isOpen())
				ss.close();
		}
		return listObj;
	}

	public static String readXMLToString(String fileName)
			throws MalformedURLException, DocumentException {
		String xmlString = "";
		ServiceUtil service = new ServiceUtil();
		String appHome = service.getClass().getClassLoader().getResource("/")
				.getPath();
		if (appHome != null) {
			try {
				appHome = URLDecoder.decode(appHome, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		String Path = appHome + "config/gis/" + fileName;

		Document xmlDoc = read(Path);
		if (xmlDoc != null) {
			xmlString = xmlDoc.asXML();
		} else {
			xmlString = "";
		}
		return xmlString;
	}

	public static HashMap<String,Object> readXML(String fileName)
			throws MalformedURLException, DocumentException{
		HashMap<String,Object> returnObj = new HashMap<String,Object>();
		ServiceUtil service = new ServiceUtil();
		String appHome = service.getClass().getClassLoader().getResource("/")
				.getPath();
		String Path = appHome + "config/gis/" + fileName;
		Document xmlDoc = read(Path);
		Element xmlList = getRootElement(xmlDoc);
		returnObj.put(xmlList.getName(), treeWalk(xmlList));
		returnObj.put("attribute", getAttribute(xmlList));
		return returnObj;
	}

	@SuppressWarnings("rawtypes")
	public static HashMap<String,Object> getAttribute(Element element){
		HashMap<String,Object> attributeObj = new HashMap<String,Object>();
		for (Iterator i = element.attributeIterator(); i.hasNext();) {
			Attribute attribute = (Attribute) i.next();
			attributeObj.put(attribute.getName(), attribute.getValue());
		}
		return attributeObj;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List treeWalk(Element root){
		ArrayList treeWalkArray = new ArrayList();
		HashMap<String,Object> attributeObj = null;
		for (Iterator i = root.elementIterator(); i.hasNext();) {
			HashMap<String,Object> treeWalkObj = new HashMap<String,Object>();
			Element subRoot = (Element) i.next();
			String nodeName = subRoot.getName();
			attributeObj = getAttribute(subRoot);
			if (subRoot.elementIterator().hasNext()) {
				treeWalkObj.put(nodeName, treeWalk(subRoot));
			}
			treeWalkObj.put("attribute", attributeObj);
			treeWalkArray.add(treeWalkObj);
		}
		return treeWalkArray;
	}

	public static String getToString(Object obj) {
		String returnString = "";
		if (obj == null) {
			returnString = "";
		} else {
			returnString = obj.toString();
		}
		return returnString;
	}

	public static Document read(String fileName) throws MalformedURLException,
			DocumentException {
		SAXReader reader = new SAXReader();
		File file = new File(fileName);
		if (!file.exists()) {
			return null;
		}
		Document document = reader.read(file);
		return document;
	}

	public static Element getRootElement(Document doc) {
		return doc.getRootElement();
	}

}
