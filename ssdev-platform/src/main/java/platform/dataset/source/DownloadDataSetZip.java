package platform.dataset.source;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.dao.SimpleDAO;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.resource.ResourceCenter;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.util.AppContextHolder;
import ctd.util.S;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.util.io.ZipUtil;
import ctd.util.xml.XMLHelper;

public class DownloadDataSetZip {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadDataSetZip.class);

	public void download(Map<String,Object> req, HttpServletResponse response) {
		ZipUtil zipUtil = null;
		Session ss = null;
		try {
			List<Map<String,Object>> list = (List<Map<String, Object>>) req.get("dataSet");
			Map<String,InputStream> end = new HashMap<String, InputStream>();
			DataSetXsd dataSetXsd=new DataSetXsd();
			DataSample dataSample=new DataSample();
			DataToWord dataToWord=new DataToWord();
			OutputStream out = response.getOutputStream();
			Dictionary d = DictionaryController.instance().getDic("platform.dataset.dic.resDataStandard");
			Context ctx = ContextUtils.getContext();
			ss = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class).openSession();
			ctx.put(Context.DB_SESSION, ss);
			for(Iterator<Map<String,Object>> it = list.iterator();it.hasNext();){
				Map<String,Object> map = it.next();
				if("0".equals(map.get("Status")) || d.getItem(S.obj2String(map.get("DataStandardId"))) == null){
					it.remove();
				}
			}
			for(Map<String,Object> map:list){
				Document doc=dataSetXsd.getDocument(
						S.obj2String(map.get("DataSetId")),
						S.obj2String(map.get("CustomIdentify")),
						S.obj2String(map.get("StandardIdentify")), ctx);
				ByteArrayOutputStream o = new ByteArrayOutputStream();
				XMLHelper.putDocument(o, doc);
				ByteArrayInputStream inputStream = new ByteArrayInputStream(o.toByteArray());
				end.put(String.valueOf(map.get("CustomIdentify"))+".xsd", inputStream);
			}
			for (Map<String, Object> map : list) {
				Document doc = dataSample.getDocument(
						S.obj2String(map.get("DataSetId")),
						S.obj2String(map.get("DName")),
						S.obj2String(map.get("CustomIdentify")),
						S.obj2String(map.get("StandardIdentify")),ctx);
				Document doc1=XMLHelper.getDocument(ResourceCenter.load("platform/dataset/source/dataset-sample.xml").getInputStream());
				Element body=(Element) doc1.getRootElement().selectSingleNode("//Body");
				body.add(doc.getRootElement());
				ByteArrayOutputStream o = new ByteArrayOutputStream();
				XMLHelper.putDocument(o, doc1);
				ByteArrayInputStream inputStream = new ByteArrayInputStream(o.toByteArray());
				end.put(String.valueOf(map.get("CustomIdentify"))+".xml", inputStream);
			}
			InputStream is = dataToWord.toWord(list, ctx);
			if(is != null){
				end.put("dataset.doc", is);
			}
			zipUtil = new ZipUtil(end,out);
			zipUtil.zip();
			out.flush();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if(ss != null && ss.isOpen()){
				ss.close();
			}
			if(zipUtil != null){
				zipUtil.close();
			}
		}
	}

	public void batchDownload(Map<String,Object> req, HttpServletResponse response){
		ZipUtil zipUtil = null;
		Session ss = null;
		try {
			Context ctx = ContextUtils.getContext();
			ss = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class).openSession();
			ctx.put(Context.DB_SESSION, ss);
			List<?> queryCnd=(List<?>) req.get("cnd");
			Schema schema = SchemaController.instance().getSchema((String)req.get("schema"));
			SimpleDAO dao = new SimpleDAO(schema, ctx);
			List<Map<String, Object>> list = dao.find(queryCnd).getRecords();
			Map<String,InputStream> end = new HashMap<String, InputStream>();
			DataSetXsd dataSetXsd=new DataSetXsd();
			DataSample dataSample=new DataSample();
			DataToWord dataToWord=new DataToWord();
			OutputStream out = response.getOutputStream();
			Dictionary d = DictionaryController.instance().getDic("platform.dataset.dic.resDataStandard");
			for(Iterator<Map<String,Object>> it = list.iterator();it.hasNext();){
				Map<String,Object> map = it.next();
				if("0".equals(map.get("Status")) || d.getItem(S.obj2String(map.get("DataStandardId"))) == null){
					it.remove();
				}
			}
			for(Map<String,Object> map:list){
				Document doc=dataSetXsd.getDocument(
						S.obj2String(map.get("DataSetId")),
						S.obj2String(map.get("CustomIdentify")),
						S.obj2String(map.get("StandardIdentify")), ctx);
				ByteArrayOutputStream o = new ByteArrayOutputStream();
				XMLHelper.putDocument(o, doc);
				ByteArrayInputStream inputStream = new ByteArrayInputStream(o.toByteArray());
				end.put(String.valueOf(map.get("CustomIdentify"))+".xsd", inputStream);
			}
			for (Map<String, Object> map : list) {
				Document doc = dataSample.getDocument(
						S.obj2String(map.get("DataSetId")),
						S.obj2String(map.get("DName")),
						S.obj2String(map.get("CustomIdentify")),
						S.obj2String(map.get("StandardIdentify")),ctx);
				Document doc1=XMLHelper.getDocument(ResourceCenter.load("platform/dataset/source/dataset-sample.xml").getInputStream());
				Element body=(Element) doc1.getRootElement().selectSingleNode("//Body");
				body.add(doc.getRootElement());
				ByteArrayOutputStream o = new ByteArrayOutputStream();
				XMLHelper.putDocument(o, doc1);
				ByteArrayInputStream inputStream = new ByteArrayInputStream(o.toByteArray());
				end.put(String.valueOf(map.get("CustomIdentify"))+".xml", inputStream);
			}
			InputStream is = dataToWord.toWord(list, ctx);
			if(is != null){
				end.put("dataset.doc", is);
			}
			zipUtil = new ZipUtil(end,out);
			zipUtil.zip();
			out.flush();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if(ss != null && ss.isOpen()){
				ss.close();
			}
			if(zipUtil != null){
				zipUtil.close();
			}
		}
	}
	
}
