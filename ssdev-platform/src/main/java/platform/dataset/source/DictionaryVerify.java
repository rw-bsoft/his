package platform.dataset.source;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.dao.SimpleDAO;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.resource.ResourceCenter;
import ctd.util.AppContextHolder;
import ctd.util.S;
import ctd.util.annotation.RpcService;
import ctd.util.context.ContextUtils;
import ctd.util.xml.XMLHelper;

public class DictionaryVerify implements DictionaryVerifyUploader{
	private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryVerify.class);
	private static String verifyFolder = "platform.dataset.dic.verify.";
	private static String dicFolder = "platform.dataset.dic.res.";
	
	public Boolean verify(String resDataStandard, String dicId){
		String shortDicId = S.substringAfter(dicId, S.join(verifyFolder,resDataStandard,"."));
		try {
			String fileName = ResourceCenter.getAbstractClassPath(dicId.replace(".", "/")+".dic");
			String newFileName = ResourceCenter.getAbstractClassPath(S.join(dicFolder,resDataStandard,".",shortDicId).replace(".", "/")+".dic");
			File file = new File(fileName);
			if(file.exists()){
				file.renameTo(new File(newFileName));
				save(resDataStandard, dicId, 1);	//1 pass
				return true;
			}
		} catch (Exception e) {
			LOGGER.error("verify dic {} failed.",dicId,e);
		}
		return false;
	}
	
	public Boolean reject(String resDataStandard, String dicId){
		try {
			String fileName = ResourceCenter.getAbstractClassPath(dicId.replace(".", "/")+".dic");
			File file = new File(fileName);
			if(file.exists()){
				file.delete();
				save(resDataStandard, dicId, 2);	//2 reject
				return true;
			}
		} catch (Exception e) {
			LOGGER.error("verify dic {} failed.",dicId,e);
		}
		return false;
	}
	
	private void save(String resDataStandard, String dicId, Integer flag) throws Exception{
		Session ss = null;
		SimpleDAO dao = null;
		try {
			SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class);
			ss = sf.openSession();
			dao = new SimpleDAO(ss, "platform.dataset.schemas.RES_DictionaryVerifyLog", ContextUtils.getContext());
			dao.beginTransaction();
			Map<String, Object> rec = new HashMap<String, Object>();
			rec.put("VERSION", resDataStandard);
			rec.put("DICID", dicId);
			rec.put("USERID", UserRoleToken.getCurrent().getUserId());
			rec.put("VFLAG", flag);
			rec.put("VDATE", new Date());
			dao.create(rec);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			throw e;
		} finally {
			if(ss != null && ss.isOpen()){
				ss.close();
			}
		}
	}
	
	@Override
	@RpcService
	public Boolean uploadVerifyDictionary(String id,String dataStandard,Document doc){
		try {
			String dicId = S.join(dicFolder, dataStandard, ".", id);
			Dictionary d = DictionaryController.instance().getDic(dicId);
			if(d != null){	//该字典已经被审核
				return false;
			}
			String fileName = ResourceCenter.getAbstractClassPath(S.join(verifyFolder,dataStandard,".",id).replace(".", "/")+".dic");
			XMLHelper.putDocument(new File(fileName), doc);
			return true;
		} catch (Exception e) {
			LOGGER.error("dic {} upload failed.",id,e);
		}
		return false;
	}

//	@RpcService
//	@Override
//	public Map<String, Boolean> checkUploadStatus(String dataStandard) {
//		SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class);
//		Session ss = null;
//		Map<String, Boolean> m = new HashMap<String, Boolean>();
//		try {
//			ss = sf.openSession();
//			Query q = ss.createQuery("select a.DICID,a.VFLAG from RES_DictionaryVerifyLog a where a.VERSION = ?");
//			q.setString(0, dataStandard);
//			List<Object[]> rs = q.list();
//			for(Object[] r:rs){
//				m.put((String)r[0], r[1]==null?false:(1==(Integer)r[1]?true:false));
//			}
//		} catch (Exception e) {
//			
//		} finally {
//			if(ss != null && ss.isOpen()){
//				ss.close();
//			}
//		}
//		return m;
//	}

//	@RpcService
//	@Override
//	public Map<String, Boolean> checkUploadStatus(String dataStandard, Boolean isSuccess) {
//		SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class);
//		Session ss = null;
//		Map<String, Boolean> m = new HashMap<String, Boolean>();
//		try {
//			ss = sf.openSession();
//			Query q = ss.createQuery("select a.DICID,a.VFLAG from RES_DictionaryVerifyLog a where a.VERSION = ? and a.VFLAG = ?");
//			q.setString(0, dataStandard);
//			q.setInteger(1, isSuccess?1:2);
//			List<Object[]> rs = q.list();
//			for(Object[] r:rs){
//				m.put((String)r[0], r[1]==null?false:(1==(Integer)r[1]?true:false));
//			}
//		} catch (Exception e) {
//			
//		} finally {
//			if(ss != null && ss.isOpen()){
//				ss.close();
//			}
//		}
//		return m;
//	}
	
}
