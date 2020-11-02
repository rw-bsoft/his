package platform.dataset.source.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;

import platform.dataset.source.DataSample;
import platform.dataset.source.DataSetXsd;
import platform.dataset.source.DataToWord;
import ctd.account.AccountCenter;
import ctd.dao.SimpleDAO;
import ctd.dao.exception.DataAccessException;
import ctd.dictionary.DicToXMLDic;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.resource.ResourceCenter;
import ctd.util.AppContextHolder;
import ctd.util.JSONUtils;
import ctd.util.S;
import ctd.util.annotation.RpcService;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.util.xml.XMLHelper;

@SuppressWarnings({"unchecked","rawtypes"})
public class DataElementRemoter implements DataElementLoader{
	private static String DATA_ELEMENT_SET = "platform.dataset.schemas.RES_DataSetContent";
	private static String GROUP = "GROUP";
	private static String GROUPS = "GROUPS";
	private static final Logger LOGGER = LoggerFactory.getLogger(DataElementRemoter.class);
	
	@RpcService
	@Override
	public Map<String, String> getSetDics(String id) {
		return getSetDics(id, null);
	}
	
	@RpcService
	@Override
	public Map<String, String> getSetRelation(String id) {
		return getSetRelation(id, null);
	}
	
	@RpcService
	@Override
	public String getSetMapping(String id){
		return getSetMapping(id, null);
	}
	
	@RpcService
	@Override
	public Map<String,Object> getDataElement(String id){
		return getDataElement(id, null);
	}	
	
	@RpcService
	@Override
	public Map<String,Object> getDataSet(String id){
		return getDataSet(id, null);
	}
	
	
	private Context createContext(){
		Context ctx = ContextUtils.getContext();
		try {
			ContextUtils.put(Context.USER_ROLE_TOKEN, AccountCenter.getUser("system").getUserRoleTokens().iterator().next());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return ctx;
	}
	
	private List spellCnd(String fieldName, Integer fieldValue) throws DataAccessException{
		StringBuffer q = new StringBuffer("['eq',['$','").append(fieldName).append("'],['i',").append(fieldValue).append("]]");
		try {
			return JSONUtils.parse(q.toString(), List.class);
		} catch (Exception e) {
			throw new DataAccessException(e.getMessage(), e);
		} 
	}
	
	@RpcService
	@Override
	public Document getDataSetXSD(String id) {
		return getDataSetXSD(id, null);
	}

	@RpcService
	@Override
	public Document getDataSetXSD(String id, String version) {
		DataSetXsd dsx = new DataSetXsd();
		Context ctx = createContext();
		ApplicationContext wac = AppContextHolder.get();
		SessionFactory sf = (SessionFactory)wac.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY);
		Session ss = null;
		try {
			ss = sf.openSession();
			ctx.put(Context.DB_SESSION, ss);
			StringBuffer hql = new StringBuffer("select a.DataSetId from RES_DataSet a where a.CustomIdentify = ?");
			if(!S.isEmpty(version)){
				hql.append(" and a.DataStandardId = ?");
			}
			hql.append(" order by a.DataSetId desc");
			Query q = ss.createQuery(hql.toString());
			q.setString(0, id);
			if(!S.isEmpty(version)){
				q.setString(1, version);
			}
			List<Integer> r = q.list();
			if(r.size() > 0){
				String dataSetId = String.valueOf(r.get(0));
				return dsx.getDocument(dataSetId, id, null, ctx);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}finally{
			if(ss != null && ss.isOpen()){
				ss.close();
			}
		}
		return null;
	}
	
	@RpcService
	@Override
	public Document getDataSample(String id) {
		return getDataSample(id, null);
	}

	@RpcService
	@Override
	public Document getDataSample(String id, String version) {
		DataSample dataSample = new DataSample();
		Context ctx = createContext();
		ApplicationContext wac = AppContextHolder.get();
		SessionFactory sf = (SessionFactory) wac
				.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY);
		Session ss = null;
		try {
			ss = sf.openSession();
			ctx.put(Context.DB_SESSION, ss);
			StringBuffer hql = new StringBuffer(
					"from RES_DataSet a where a.CustomIdentify = ?");
			if (!S.isEmpty(version)) {
				hql.append(" and a.DataStandardId = ?");
			}
			hql.append(" order by a.DataSetId desc");
			Query q = ss.createQuery(hql.toString());
			q.setString(0, id);
			if (!S.isEmpty(version)) {
				q.setString(1, version);
			}
			List<Map<String, Object>> r = q.list();
			if (r.size() > 0) {
				String dataSetId = String.valueOf(r.get(0).get("DataSetId"));
				Document result = XMLHelper.getDocument(ResourceCenter.load("platform/dataset/source/dataset-sample.xml").getInputStream());
				Element body = (Element) result.getRootElement()
						.selectSingleNode("//Body");
				body.add(dataSample.getDocument(dataSetId,
						String.valueOf(r.get(0).get("DName")), id, null, ctx)
						.getRootElement());
				return result;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}finally{
			if(ss != null && ss.isOpen()){
				ss.close();
			}
		}
		return null;
	}

	@RpcService
	@Override
	public Object getDataWord(List<Map<String,Object>> dataSets) {
		DataToWord dataToWord = new DataToWord();
		Context ctx = createContext();
		ApplicationContext wac = AppContextHolder.get();
		SessionFactory sf = (SessionFactory) wac.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY);
		Session ss = null;
		try {
			ss = sf.openSession();
			ctx.put(Context.DB_SESSION, ss);
			if (dataSets.size() > 0) {
				InputStream in = dataToWord.toWord(dataSets, ctx);
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				byte[] data = new byte[2048];
				int count = -1;
				while ((count = in.read(data, 0, 2048)) != -1){
					outStream.write(data, 0, count);
				}
				return outStream.toByteArray();
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}finally{
			if(ss != null && ss.isOpen()){
				ss.close();
			}
		}
		return null;
	}

	@RpcService
	@Override
	public String getSetMapping(String id, String version) {
		ApplicationContext wac = AppContextHolder.get();
		SessionFactory sf = (SessionFactory)wac.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY);
		Session ss = null;
		String mapping=null;
		try {
			ss = sf.openSession();
			StringBuffer hql = new StringBuffer("select a.DataSetMapping from RES_DataSet a where a.CustomIdentify = ?");
			if(!S.isEmpty(version)){
				hql.append(" and a.DataStandardId = ?");
			}
			hql.append(" order by a.DataSetId desc");
			Query q = ss.createQuery(hql.toString());
			q.setString(0, id);
			if(!S.isEmpty(version)){
				q.setString(1, version);
			}
			List<String> ls = q.list();
			if(ls.size()<1){
				return mapping;
			}
			mapping=ls.get(0);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if(ss != null && ss.isOpen()){
				ss.close();
			}
		}
		return mapping;
	}

	@RpcService
	@Override
	public Map<String, Object> getDataElement(String id, String version) {
		ApplicationContext wac = AppContextHolder.get();
		try {
			SessionFactory sf = (SessionFactory)wac.getBean("mySessionFactory");
			HibernateTemplate ht = new HibernateTemplate(sf);
			StringBuffer hql = new StringBuffer("from RES_DataElement where CustomIdentify=?");
			List<Map<String,Object>> ls = null;
			if(!S.isEmpty(version)){
				hql.append(" and DataStandardId = ?");
				ls = ht.find(hql.toString(),id,version);
			}else{
				ls = ht.find(hql.toString(),id);
			}
			if(ls!=null && ls.size() >0){
				return ls.get(0);
			}
			return null;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	@RpcService
	@Override
	public Map<String, Object> getDataSet(String id, String version) {
		Dictionary groupDic = DictionaryController.instance().getDic("platform.dataset.dic.resDataGroup");
		Map<String, Object> result = new HashMap<String, Object>();
		ApplicationContext wac = AppContextHolder.get();
		SessionFactory sf = (SessionFactory)wac.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY);
		Session ss = null;
		try {
			ss = sf.openSession();
			StringBuffer hql = new StringBuffer("select a.DataSetId from RES_DataSet a where a.CustomIdentify = ?");
			if(!S.isEmpty(version)){
				hql.append(" and a.DataStandardId = ?");
			}
			hql.append(" order by a.DataSetId desc");
			Query q = ss.createQuery(hql.toString());
			q.setString(0, id);
			if(!S.isEmpty(version)){
				q.setString(1, version);
			}
			List<Integer> ls = q.list();
			if(ls.size() <1){
				return result;
			}
			Integer setId = ls.get(0);
			SimpleDAO dao1 = new SimpleDAO(ss, DATA_ELEMENT_SET, createContext());
			List cnd = spellCnd("DataSetId", setId);
			List<Map<String, Object>> ls1 = dao1.find(cnd).getRecords();
			for(Map<String, Object> m:ls1){
				String groupId=S.obj2String(m.get("DataGroup"));
				if(!StringUtils.isEmpty(groupId)){
					Map<String, Map<String, Object>> gp = (Map<String, Map<String, Object>>) result.get(GROUP);
					Map<String, Map<String, Object>> gps = (Map<String, Map<String, Object>>) result.get(GROUPS);
					if(gp == null){
						gp = new HashMap<String, Map<String,Object>>();
						result.put(GROUP, gp);
					}
					if(gps == null){
						gps = new HashMap<String, Map<String,Object>>();
						result.put(GROUPS, gps);
					}
					DictionaryItem groupItem = groupDic.getItem(groupId);
					String groupIdentify = (String) groupItem.getProperty("GroupIdentify");
					String groupFrequencyNumber = (String) groupItem.getProperty("FrequencyNumber");
					if(S.isEmpty(groupFrequencyNumber) || "1".equals(groupFrequencyNumber)){
						Map<String, Object> g = gp.get(groupIdentify.toUpperCase());
						if(g == null){
							g = new HashMap<String, Object>();
							gp.put(groupIdentify.toUpperCase(), g);
						}
						g.put(((String) m.get("CustomIdentify")).toUpperCase(), m);
					}
					if("2".equals(groupFrequencyNumber)){
						Map<String, Object> g = gps.get(groupIdentify.toUpperCase());
						if(g == null){
							g = new HashMap<String, Object>();
							gps.put(groupIdentify.toUpperCase(), g);
						}
						g.put(((String) m.get("CustomIdentify")).toUpperCase(), m);
					}
				}else{
					result.put(((String) m.get("CustomIdentify")).toUpperCase(), m);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if(ss != null && ss.isOpen()){
				ss.close();
			}
		}
		return result;
	}

	@RpcService
	@Override
	public Map<String, String> getSetRelation(String id, String version) {
		ApplicationContext wac = AppContextHolder.get();
		SessionFactory sf = (SessionFactory)wac.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY);
		Session ss = null;
		Map<String,String> relation=new HashMap<String, String>();
		try {
			ss = sf.openSession();
			StringBuffer hql = new StringBuffer("select a.Parent,a.Foreignkey from RES_DataSet a where a.CustomIdentify = ?");
			if(!S.isEmpty(version)){
				hql.append(" and a.DataStandardId = ?");
			}
			hql.append(" order by a.DataSetId desc");
			Query q = ss.createQuery(hql.toString());
			q.setString(0, id);
			if(!S.isEmpty(version)){
				q.setString(1, version);
			}
			List ls = q.list();
			if(ls.size()<1){
				return relation;
			}
			Object[] r=(Object[]) ls.get(0);
			if(!S.isEmpty(S.obj2String(r[0]))&&!S.isEmpty(S.obj2String(r[1]))){
				relation.put("parent",S.obj2String(r[0]));
				relation.put("foreignkey", S.obj2String(r[1]));
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if(ss != null && ss.isOpen()){
				ss.close();
			}
		}
		return relation;
	}

	@RpcService
	@Override
	public Map<String, String> getSetDics(String id, String version) {
		ApplicationContext wac = AppContextHolder.get();
		SessionFactory sf = (SessionFactory)wac.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY);
		Session ss = null;
		Map<String,String> setDics=new HashMap<String, String>();
		try {
			ss = sf.openSession();
			StringBuffer hql = new StringBuffer("select a.DataSetId from RES_DataSet a where a.CustomIdentify = ?");
			if(!S.isEmpty(version)){
				hql.append(" and a.DataStandardId = ?");
			}
			hql.append(" order by a.DataSetId desc");
			Query q = ss.createQuery(hql.toString());
			q.setString(0, id);
			if(!S.isEmpty(version)){
				q.setString(1, version);
			}
			List<Integer> ls = q.list();
			if(ls.size() <1){
				return setDics;
			}
			Integer setId = ls.get(0);
			SimpleDAO dao = new SimpleDAO(ss, DATA_ELEMENT_SET, createContext());
			List cnd = spellCnd("DataSetId", setId);
			List<Map<String, Object>> ls1 = dao.find(cnd).getRecords();
			for(Map<String, Object> m:ls1){
				String dic=S.obj2String(m.get("bCodeSystem"));
				if(!StringUtils.isEmpty(dic)){
					if(!S.isEmpty(version)){
						dic = "res." + version + "." + dic;
					}
					setDics.put((String) m.get("CustomIdentify"), dic);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if(ss != null && ss.isOpen()){
				ss.close();
			}
		}
		return setDics;
	}
	
	@RpcService
	@Override
	public Dictionary getDataDic(String id) {
		return getDataDic(id,null);
	}

	@RpcService
	@Override
	public Dictionary getDataDic(String id, String version) {
		try{
			if(StringUtils.isEmpty(version)){
				ApplicationContext wac = AppContextHolder.get();
				SessionFactory sf = (SessionFactory)wac.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY);
				Session ss = null;
				try {
					ss = sf.openSession();
					List<String> versions=ss.createQuery("select DataStandardId from RES_DataStandard order by PublishDate desc").list();
					version=versions.get(0);
				}finally {
					if(ss != null && ss.isOpen()){
						ss.close();
					}
				}
			}
			String dicId="platform.dataset.dic.res."+version+"."+id;
			Dictionary dic = DictionaryController.instance().get(dicId);
			return DicToXMLDic.toXMLDictionary(dic);
		}catch (Exception e) {
			LOGGER.error("get data dic[{}] error",id,e);
		} 
		return null;
	}
}
