package platform.dataset.source;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import ctd.dao.exception.InsertDataAccessException;
import ctd.dictionary.DictionaryController;
import ctd.resource.ResourceCenter;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleSave;
import ctd.util.S;
import ctd.util.context.Context;

public class DataStandardSave extends SimpleSave {
	private static final String attachmentDataType = "$a";
	private static Map<String, String> attachments;
	static{
		attachments = new HashMap<String, String>();
		attachments.put("html", "网页");
		attachments.put("pdf", "pdf文档");
		attachments.put("word", "word文档");
		attachments.put("image", "图片");
	}
	
	public DataStandardSave() {
		transactionSupport = true;
	}
	
	@Override
	public void doBeforeExec(Map<String, Object> req, Map<String, Object> res, Context ctx) throws ServiceException {
		if("create".equals(req.get("op"))){
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			if(body != null){
				String did = (String) body.get("DataStandardId");
				Session ss = (Session) ctx.get(Context.DB_SESSION);
				Query q = ss.createQuery("select count(*) from RES_DataStandard where DataStandardId = ?");
				q.setString(0, did);
				if((Long)q.list().get(0) > 0){
					throw new ServiceException(600,"标识符已存在.");
				}
			}
		}
	}

	@Override
	public void doAfterExec(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws InsertDataAccessException {
		if((Integer)res.get(Service.RES_CODE) == 200){
			if("create".equals(req.get("op"))){
				Session ss = null;
				try {
					Map<String, Object> body = (Map<String, Object>) res.get("body");
					if(body != null){
						String did = (String) body.get("DataStandardId");
						ss = (Session) ctx.get(Context.DB_SESSION);
						for(String k:attachments.keySet()){
							HashMap<String, Object> r = new HashMap<String, Object>();
							r.put("DataStandardId", did);
							r.put("CustomIdentify", k);
							r.put("DName", attachments.get(k));
							r.put("DataType", attachmentDataType);
							ss.save("RES_DataElement", r);
						}
						String folder = S.join("platform/dataset/dic/res/",did);
						new File(ResourceCenter.getAbstractClassPath(folder)).mkdirs();
						folder = S.join("platform/dataset/dic/verify/",did);
						new File(ResourceCenter.getAbstractClassPath(folder)).mkdir();
					}
				} catch (Exception e) {
					throw new InsertDataAccessException("insert element standard with attachments error.", e);
				}
			}
			DictionaryController.instance().reload("platform.dataset.dic.resDataStandard");
		}
	}
	
}
