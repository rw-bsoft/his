package platform.dataset.source;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import ctd.dictionary.DictionaryController;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleRemove;
import ctd.util.S;
import ctd.util.context.Context;

public class DataStandardRemove extends SimpleRemove {
	private static final String CATALOG = "platform.dataset.schemas.RES_DataSetCatalog";
	private static final String ELEMENT = "platform.dataset.schemas.RES_DataElement";
	private static final String SET = "platform.dataset.schemas.RES_DataSet";
	private static final String STANDARD = "platform.dataset.schemas.RES_DataStandard";
	private static final String CATEGORY = "platform.dataset.schemas.RES_DataElementCategory";
	private static final String GROUP = "platform.dataset.schemas.RES_DataGroup";
	private static final String SET_CONTENT = "platform.dataset.schemas.RES_DataSetContent";
	@Override
	public void doAfterExec(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		if((Integer)res.get(Service.RES_CODE) == 200){
			if(STANDARD.equals(req.get("schema"))){
				DictionaryController.instance().reload("platform.dataset.dic.resDataStandard");
				Session ss = null;
				try {
					String pkey = (String) req.get("pkey");
					if(pkey != null){
						ss = (Session) ctx.get(Context.DB_SESSION);
						Query q = ss.createSQLQuery("delete from RES_DataElement where DataStandardId=? and StandardIdentify='$attachment'");
						q.setString(0, pkey);
						q.executeUpdate();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(CATALOG.equals(req.get("schema"))){
				DictionaryController.instance().reload("platform.dataset.dic.resDataSetCatalog");
			}
			if(SET.equals(req.get("schema"))){
				DictionaryController.instance().reload("platform.dataset.dic.resDataSet");
				if(!StringUtils.isEmpty(S.obj2String(req.get("CustomIdentify")))){
					DataSetCatalogSave.sendReloadXSDMessage(S.obj2String(req.get("CustomIdentify")),S.obj2String(req.get("Version")));
				}
			}
			if(CATEGORY.equals(req.get("schema"))){
				DictionaryController.instance().reload("platform.dataset.dic.resDataElementCata");
			}
			if(GROUP.equals(req.get("schema"))){
				DictionaryController.instance().reload("platform.dataset.dic.resDataGroup");
			}
			if(SET_CONTENT.equals(req.get("schema"))){
				if(!StringUtils.isEmpty(S.obj2String(req.get("CustomIdentify")))){
					DataSetCatalogSave.sendReloadXSDMessage(S.obj2String(req.get("CustomIdentify")),S.obj2String(req.get("Version")));
				}
			}
		}
	}
	@Override
	public void doBeforeExec(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		String pkey=S.obj2String(req.get("pkey"));
		if(!StringUtils.isEmpty(pkey)){
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			if(STANDARD.equals(req.get("schema"))){
				Query q = ss.createQuery("select count(*) from RES_DataElement where DataStandardId = ?");
				q.setString(0, pkey);
				if((Long)q.list().get(0) > 0){
					throw new ServiceException(502,"该标准下还存在有数据元，删除失败");
				}
				q = ss.createQuery("select count(*) from RES_DataSet where DataStandardId = ?");
				q.setString(0, pkey);
				if((Long)q.list().get(0) > 0){
					throw new ServiceException(502,"该标准下还存在有数据集，删除失败");
				}
			}
			if(ELEMENT.equals(req.get("schema"))){
				Query q = ss.createQuery("from RES_DataSetContent where DataElementId =?");
				q.setString(0, pkey);
				if(q.list().size()>0){
					throw new ServiceException(502,"有数据集引用到此数据元,删除失败");
				}
			}
			if(CATEGORY.equals(req.get("schema"))){
				Query q = ss.createQuery("from RES_DataElement where Category =?");
				q.setString(0, pkey);
				if(q.list().size()>0){
					throw new ServiceException(502,"该分类下还存在有数据元，删除失败");
				}
			}
			if(SET.equals(req.get("schema"))){
				Query q = ss.createSQLQuery("delete from RES_DataSetContent where dataSetId=?");
				q.setString(0, pkey);
				q.executeUpdate();
				q = ss.createQuery("from RES_DataSet where DataSetId= ?");
				q.setString(0, pkey);
				List<Map<String,Object>> set=q.list();
				if(set.size()>0){
					req.put("CustomIdentify", set.get(0).get("CustomIdentify"));
					req.put("Version", set.get(0).get("DataStandardId"));
				}
			}
			if(GROUP.equals(req.get("schema"))){
				Query q = ss.createQuery("from RES_DataSetContent where DataGroup =?");
				q.setString(0, pkey);
				if(q.list().size()>0){
					throw new ServiceException(502,"有数据集引用到此数据组(段落),删除失败");
				}
			}
			if(SET_CONTENT.equals(req.get("schema"))){
				Query q=ss.createQuery("select b.CustomIdentify,b.DataStandardId from RES_DataSetContent a,RES_DataSet b where a.DataSetContentId=? and a.DataSetId=b.DataSetId");
				q.setString(0, pkey);
				List<Object[]> list=q.list();
				if(list.size()>0){
					req.put("CustomIdentify", list.get(0)[0]);
					req.put("Version", list.get(0)[1]);
				}
			}
		}
	}
	
}
