package platform.dataset.source;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import ctd.dictionary.DictionaryController;
import ctd.schema.SchemaController;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleSave;
import ctd.util.AppContextHolder;
import ctd.util.S;
import ctd.util.context.Context;

public class DataSetCatalogSave extends SimpleSave {
	private static final String ELEMENT = "platform.dataset.schemas.RES_DataElement";
	private static final String CATALOG = "platform.dataset.schemas.RES_DataSetCatalog";
	private static final String SET = "platform.dataset.schemas.RES_DataSet";
	private static final String CATEGORY = "platform.dataset.schemas.RES_DataElementCategory";
	private static final String GROUP = "platform.dataset.schemas.RES_DataGroup";
	private static final String SET_CONTENT = "platform.dataset.schemas.RES_DataSetContent";
	
	@Override
	public void doBeforeExec(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		if("create".equals(req.get("op"))){
			Map<String, Object> body = (Map<String, Object>) req.get(BODY);
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			if(SET_CONTENT.equals(req.get("schema"))){
				if(!StringUtils.isEmpty(S.obj2String(body.get("CustomIdentify")))){
					Integer setId = Integer.valueOf(String.valueOf(body.get("DataSetId")));
					String CustomIdentify = String.valueOf(body.get("CustomIdentify"));
					Query q = ss.createQuery("from RES_DataSetContent a where a.DataSetId=? and a.CustomIdentify=?");
					q.setInteger(0, setId);
					q.setString(1, CustomIdentify);
					if(q.list().size()>0){
						throw new ServiceException(420, "自定义表示法重复.");
					}
				}
			}
			else if(ELEMENT.equals(req.get("schema")) || SET.equals(req.get("schema"))
					|| GROUP.equals(req.get("schema")) || CATALOG.equals(req.get("schema"))){
				String StandardIdentify = "StandardIdentify";
				if(GROUP.equals(req.get("schema"))){
					StandardIdentify = "GroupIdentify";
				}else if(CATALOG.equals(req.get("schema"))){
					StandardIdentify = "CatalogKey";
				}
				String p = (String) body.get(StandardIdentify);
				Query q = ss.createQuery("from "+SchemaController.instance().getSchema((String)req.get("schema")).getEntityName()+" a where a."+StandardIdentify+"=?");
				q.setString(0, p);
				if(q.list().size()>0){
					throw new ServiceException(421, "标识符重复.");
				}
			}
		}
	}
	@Override
	public void doAfterExec(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		if((Integer)res.get(Service.RES_CODE) == 200){
			if(CATALOG.equals(req.get("schema"))){
				DictionaryController.instance().reload("platform.dataset.dic.resDataSetCatalog");
				DictionaryController.instance().reload("platform.dataset.dic.resDataSet");
			}
			else if(GROUP.equals(req.get("schema"))){
				DictionaryController.instance().reload("platform.dataset.dic.resDataGroup");
			}
			else if(SET.equals(req.get("schema"))){
				DictionaryController.instance().reload("platform.dataset.dic.resDataSet");
				Map<String,Object> body=(Map<String, Object>) req.get("body");
				DataSetCatalogSave.sendReloadXSDMessage(S.obj2String(body.get("CustomIdentify")), S.obj2String(body.get("DataStandardId")));
			}
			else if(CATEGORY.equals(req.get("schema"))){
				DictionaryController.instance().reload("platform.dataset.dic.resDataElementCata");
			}
			else if(ELEMENT.equals(req.get("schema"))){
				if(req.get("op").equals("update")){
					Map<String,Object> body=(Map<String, Object>) req.get("body");
					Session ss = (Session) ctx.get(Context.DB_SESSION);
					Query q=ss.createQuery("select b.CustomIdentify,b.DataStandardId from RES_DataSetContent a,RES_DataSet b where a.DataElementId=? and a.DataSetId=b.DataSetId");
					q.setString(0, S.obj2String(body.get("DataElementId")));
					List<Object[]> list=q.list();
					for(Object[] m:list){
						DataSetCatalogSave.sendReloadXSDMessage(S.obj2String(m[0]), S.obj2String(m[1]));
					}
				}
			}
			else if(SET_CONTENT.equals(req.get("schema"))){
				Session ss = (Session) ctx.get(Context.DB_SESSION);
				Map<String,Object> body=(Map<String, Object>) req.get("body");
				String sequence=S.obj2String(body.get("DSequence"));
				Query q=null;
				if(!StringUtils.isEmpty(sequence)){
					String DataSetContentId=null;
					if(req.get("op").equals("update")){
						DataSetContentId= S.obj2String(body.get("DataSetContentId"));
					}else if(req.get("op").equals("create")){
						DataSetContentId=String.valueOf(((Map)res.get("body")).get("DataSetContentId"));
					}
					if(!StringUtils.isEmpty(S.obj2String(body.get("DataGroup")))){
						q = ss.createQuery("from RES_DataSetContent where DSequence >= ? and DataSetId=? and DataSetContentId!=? and DataGroup = ? order by DSequence");
						q.setString(3,S.obj2String(body.get("DataGroup")));
					}
					if(StringUtils.isEmpty(S.obj2String(body.get("DataGroup")))){
						q = ss.createQuery("from RES_DataSetContent where DSequence >= ? and DataSetId=? and DataSetContentId!=? and DataGroup is null order by DSequence");
					}
					q.setString(0, sequence);
					q.setString(1, S.obj2String(body.get("DataSetId")));
					q.setString(2,DataSetContentId);
					List<Map<String,Object>> list=q.list();
					for(Map<String,Object> m:list){
						String s=S.obj2String(m.get("DSequence"));
						if(s.equals(sequence)){
							int i=Integer.valueOf(s);
							for(Map<String,Object> m1:list){
								m1.put("DSequence", i+1);
								i++;
							}
							break;
						}
					}
				}
				q = ss.createQuery("from RES_DataSet where DataSetId= ?");
				q.setString(0, S.obj2String(body.get("DataSetId")));
				List<Map<String,Object>> setList=q.list();
				if(setList.size()>0){
					Map<String,Object> setMap=setList.get(0);
					DataSetCatalogSave.sendReloadXSDMessage(S.obj2String(setMap.get("CustomIdentify")), S.obj2String(setMap.get("DataStandardId")));
				}
			}
		}
	}
	
	/**
	 * @param name		数据集名称
	 * @param version	数据集所属标准
	 * 
	 * 以下情况均需重新加载数据集信息
	 * 1.RES_DataSet 表的更新、删除操作
	 * 2.RES_DataSetContent 表的更新、删除操作
	 * 3.RES_DataElement 表的更新操作，且该元已经被数据集中的元所引用
	 * 4.RES_DataGroup 表的更新操作，且该组被数据集中的元引用
	 *
	 */
	@Deprecated
	public static void sendReloadXSDMessage(String name, String version){
//		HashMap<String, Object> msg = new HashMap<String, Object>();
//		msg.put(ConfigWatcher.ID, name);
//		msg.put(ConfigWatcher.CATALOG, "DataSet");
//		msg.put("version", version);
//		msg.put(ConfigWatcher.CMD, WatcherCommands.CMD_RELOAD);
//		ConfigWatcher.sendMessage(msg);
	}
	
	public void setSequence(Map<String,Object> reqData){
		Map<String, String> data=(Map) reqData.get("data");
		Session ss = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class).openSession();
		try{
			Query q=ss.createQuery("update RES_DataSetContent set DSequence =? where DataSetContentId=?");
			for(String key : data.keySet()){
				q.setString(0, key);
				q.setString(1, String.valueOf(data.get(key)));
				q.executeUpdate();
			}
		}finally{
			if(ss != null && ss.isOpen()){
				ss.close();
			}
		}
	}
}
