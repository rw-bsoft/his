package platform.monitor.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.dao.QueryResult;
import ctd.dao.SimpleDAO;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.service.core.ServiceException;
import ctd.service.dao.DBService;
import ctd.util.JSONUtils;
import ctd.util.S;
import ctd.util.context.Context;
import ctd.util.converter.ConversionUtils;

public class QualityDataService extends DBService{
	private static final Logger LOGGER = LoggerFactory.getLogger(QualityDataService.class);
	private String STATDATE = "StatDate";
	private String AUTHORORGANIZATION = "Authororganization";
	private String RECORDCLASSIFYING = "RecordClassifying";
	private String[] stageTypes = new String[]{"Stage1Success","Stage1Fail","Stage2Success","Stage2Fail","Stage3Success","Stage3Fail"};
	private Dictionary setDic;
	private Dictionary orgDic;
	
	private String getDicText(String prop, String DataStandardId){
		List<DictionaryItem> itemsList = setDic.itemsList();
		for(DictionaryItem di:itemsList){
			 if(di.getProperty("CustomIdentify").equals(prop) && di.getProperty("DataStandardId").equals(DataStandardId)){
				 return S.obj2String(di.getText());
			 }
		}
		return "";
	}
	
	private void queryRecordClassifyingWhichHasData(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		Dictionary dic = DictionaryController.instance().getDic("platform.dataset.dic.resDataSet");
		List<DictionaryItem> list = dic.itemsList();
		List<DictionaryItem> items = new ArrayList<DictionaryItem>();
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		Query q = ss.createQuery("select distinct a.RecordClassifying from QualityData_Main a");
		List<String> rs = q.list();
		String DataStandardId = (String) req.get("DataStandardId");
		for(DictionaryItem di:list){
			if(DataStandardId.equals(di.getProperty("DataStandardId"))){
				for(String r:rs){
					if(di.getProperty("CustomIdentify").equals(r)){
						items.add(di);
						break;
					}
				}
			}
		}
		res.put("body", items);
	}
	
	private void queryQuaMainDataWithOga(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException{
		String startTime = S.obj2String(req.get("startTime"));
		String endTime = S.obj2String(req.get("endTime"));
		String Authororganization = S.obj2String(req.get("Authororganization"));
		String DataStandardId = (String) req.get("DataStandardId");Session ss = null;
		try {
			ss = (Session) ctx.get(Context.DB_SESSION);
			StringBuffer hql1 = new StringBuffer("select a.Authororganization as Authororganization,a.RecordClassifying as RecordClassifying,")
				.append("sum(a.Stage1Success) as Stage1Success,sum(a.Stage2Success) as Stage2Success,sum(a.Stage3Success) as Stage3Success,")
				.append("sum(a.Stage1Fail) as Stage1Fail,sum(a.Stage2Fail) as Stage2Fail,sum(a.Stage3Fail) as Stage3Fail,")
				.append("sum(case when str(a.StatDate,'yyyy-MM-dd')>str(current_date()-5,'yyyy-MM-dd') then a.Stage1Fail else a.UnFinishCount end) as UnFinishCount")
				.append(" from QualityData_Main a");
			StringBuffer hql2 = new StringBuffer("select count(*) as numb,a.Authororganization as Authororganization,a.RecordClassifying as RecordClassifying")
				.append(" from QualityData_ErrorNumb a");
			HashMap<String, Object> qm = setQueryCnd(startTime, endTime, Authororganization, null, "a");
			Query q1 = ss.createQuery(hql1.append(" where ").append(qm.get("q"))
					.append(" group by a.Authororganization,a.RecordClassifying").toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			Query q2 = ss.createQuery(hql2.append(" where ").append(qm.get("q"))
					.append(" group by a.Authororganization,a.RecordClassifying").toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			ArrayList<String> p = (ArrayList<String>) qm.get("p");
			for(int i=0;i<p.size();i++){
				q1.setString(i, p.get(i));
				q2.setString(i, p.get(i));
			}
			List<Map<String,Object>> mainData = q1.list();
			List<Map<String,Object>> errorNumbData = q2.list();
			List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
			Map<String,Map<String,Object>> result = new HashMap<String,Map<String,Object>>();
			for(Map<String,Object> md:mainData){
				String org = (String) md.get("RecordClassifying");
				String cls = "Hospital";
				Map<String,Object> map = new HashMap<String, Object>();
				map = result.get(org);
				if(map == null){
					map = new HashMap<String, Object>();
					String orgName = getDicText(org, DataStandardId);
					if(StringUtils.isEmpty(orgName)){
						LOGGER.warn("业务分类:"+org+",没有对应分类");
					}
					map.put("Authororganization", org+"@"+orgName);
				}
				for(String st:stageTypes){
					Long count = md.get(st) == null ? 0 : (Long) md.get(st);
					if(stageTypes[1].equals(st)){
						Long uCount = md.get("UnFinishCount") == null ? 0 : (Long) md.get("UnFinishCount");
						Long c = (count-uCount)<0?0:(count-uCount);
						map.put(cls+st, count);
						map.put(cls+st+"_text", count+" | 0 | <span style='color:green;'>"+c+"</span>");
					}else{
						map.put(cls+st, count);
						map.put(cls+st+"_text", count);
					}
				}
				result.put(org, map);
			}
			for(Map<String,Object> end:errorNumbData){
				String org = (String) end.get("RecordClassifying");
				String cls = "Hospital";
				Long numb = (Long) end.get("numb");
				Map<String,Object> map = result.get(org);
				if(map != null){
					Long count = (Long) map.get(cls+"Stage1Fail");
					String text = (String) map.get(cls+"Stage1Fail_text");
					map.put(cls+"Stage1Fail_text", text.replace("| 0 |", "| "+((count-numb)<0?0:(count-numb))+" |"));
				}
			}
			for(String key:result.keySet()){
				resultList.add(result.get(key));
			}
			res.put("body", resultList);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
	}
	
	private void queryQuaMainData(Map<String, Object> req, Map<String, Object> res, Context ctx) throws ServiceException{
		String startTime = S.obj2String(req.get("startTime"));
		String endTime = S.obj2String(req.get("endTime"));
		String Authororganization = S.obj2String(req.get("Authororganization"));
		String RecordClassifying = S.obj2String(req.get("RecordClassifying"));
		if(!S.isEmpty(RecordClassifying)){
			StringBuffer rs = new StringBuffer();
			for(String s:RecordClassifying.split(",")){
				DictionaryItem it = setDic.getItem(s);
				if(it != null){
					String c = (String) it.getProperty("CustomIdentify");
					if(!S.isEmpty(c)){
						rs.append(",").append(c);
					}
				}
			}
			if(rs.length() > 0){
				RecordClassifying = rs.substring(1);
			}
		}
		Session ss = null;
		try {
			ss = (Session) ctx.get(Context.DB_SESSION);
			StringBuffer hql1 = new StringBuffer("select a.Authororganization as Authororganization,a.RecordClassifying as RecordClassifying,")
				.append("sum(a.Stage1Success) as Stage1Success,sum(a.Stage2Success) as Stage2Success,sum(a.Stage3Success) as Stage3Success,")
				.append("sum(a.Stage1Fail) as Stage1Fail,sum(a.Stage2Fail) as Stage2Fail,sum(a.Stage3Fail) as Stage3Fail,")
				.append("sum(case when str(a.StatDate,'yyyy-MM-dd')>str(current_date()-5,'yyyy-MM-dd') then a.Stage1Fail else a.UnFinishCount end) as UnFinishCount")
				.append(" from QualityData_Main a");
			StringBuffer hql2 = new StringBuffer("select count(*) as numb,a.Authororganization as Authororganization,a.RecordClassifying as RecordClassifying")
				.append(" from QualityData_ErrorNumb a");
			HashMap<String, Object> qm = setQueryCnd(startTime, endTime, Authororganization, RecordClassifying, "a");
			Query q1 = ss.createQuery(hql1.append(" where ").append(qm.get("q"))
					.append(" group by a.Authororganization,a.RecordClassifying").toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			Query q2 = ss.createQuery(hql2.append(" where ").append(qm.get("q"))
					.append(" group by a.Authororganization,a.RecordClassifying").toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			ArrayList<String> p = (ArrayList<String>) qm.get("p");
			for(int i=0;i<p.size();i++){
				q1.setString(i, p.get(i));
				q2.setString(i, p.get(i));
			}
			List<Map<String,Object>> mainData = q1.list();
			List<Map<String,Object>> errorNumbData = q2.list();
			List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
			Map<String,Map<String,Object>> result = new HashMap<String,Map<String,Object>>();
			for(Map<String,Object> md:mainData){
				String org = (String) md.get("Authororganization");
				String cls = (String) md.get("RecordClassifying");
				Map<String,Object> map = new HashMap<String, Object>();
				map = result.get(org);
				if(map == null){
					map = new HashMap<String, Object>();
					String orgName = orgDic.getText(org);
					if(StringUtils.isEmpty(orgName)){
						LOGGER.warn("机构编号:"+org+",没有对应机构");
					}
					map.put("Authororganization", org+"@"+orgName);
				}
				for(String st:stageTypes){
					Long count = md.get(st) == null ? 0 : (Long) md.get(st);
					if(stageTypes[1].equals(st)){
						Long uCount = md.get("UnFinishCount") == null ? 0 : (Long) md.get("UnFinishCount");
						Long c = (count-uCount)<0?0:(count-uCount);
						map.put(cls+st, count);
						map.put(cls+st+"_text", count+" | 0 | <span style='color:green;'>"+c+"</span>");
					}else{
						map.put(cls+st, count);
						map.put(cls+st+"_text", count);
					}
				}
				result.put(org, map);
			}
			for(Map<String,Object> end:errorNumbData){
				String org = (String) end.get("Authororganization");
				String cls = (String) end.get("RecordClassifying");
				Long numb = (Long) end.get("numb");
				Map<String,Object> map = result.get(org);
				if(map != null){
					Long count = (Long) map.get(cls+"Stage1Fail");
					String text = (String) map.get(cls+"Stage1Fail_text");
					map.put(cls+"Stage1Fail_text", text.replace("| 0 |", "| "+((count-numb)<0?0:(count-numb))+" |"));
				}
			}
			for(String key:result.keySet()){
				resultList.add(result.get(key));
			}
			res.put("body", resultList);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
	}
	
	private void queryQuaErorData(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		String startTime=S.obj2String(req.get("startTime"));
		String endTime=S.obj2String(req.get("endTime"));
		String Authororganization=S.obj2String(req.get("Authororganization"));
		String RecordClassifying=S.obj2String(req.get("RecordClassifying"));
		try {
			List<?> cnd = getQueryCnd(startTime, endTime, Authororganization, RecordClassifying);
			List<Map<String,Object>> list=find("platform.monitor.schemas.QualityData_Error",cnd,ctx);
			List<Map<String,Object>> resultList=new ArrayList<Map<String,Object>>();
			res.put("body", resultList);
			String[] stageTypes=new String[]{"Stage1Fail","Stage2Fail","Stage3Fail"};
			if(list.size()>0){
				Map<String,Map<String,Object>> result=new HashMap<String,Map<String,Object>>();
				for(Map<String,Object> m:list){
					String ErrorType=S.obj2String(m.get("ErrorType")).trim();
					Map<String,Object> map=result.get(ErrorType);
					if(map==null){
						map=new HashMap<String, Object>();
						String orgName = DictionaryController.instance().getDic("platform.monitor.dic.quaErrorCode").getText(ErrorType);
						if(StringUtils.isEmpty(orgName)){
							LOGGER.warn("错误编号:"+ErrorType+",没有对应错误类型");
						}
						map.put("Authororganization", orgName);
						result.put(ErrorType,map);
					}
					String key="Hospital";
					for(String stageType:stageTypes){
						if(stageType.contains(S.obj2String(m.get("StageType")))){
							String value=S.obj2String(map.get(key+stageType));
							String newValue=S.obj2String(m.get("ErrorCount"))!=null?S.obj2String(m.get("ErrorCount")):"0";
							if(!StringUtils.isEmpty(value)){
								newValue= S.obj2String(Integer.valueOf(value)+Integer.valueOf(newValue));
							}
							map.put(key+stageType, newValue);
							map.put(key+stageType+"_text", newValue);
						}
					}
				}
				for(String key:result.keySet()){
					resultList.add(result.get(key));
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
	}
	
	private void queryQuaErorDataByNum(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		String startTime=S.obj2String(req.get("startTime"));
		String endTime=S.obj2String(req.get("endTime"));
		String Authororganization=S.obj2String(req.get("Authororganization"));
		String RecordClassifying=S.obj2String(req.get("RecordClassifying"));
		int pageSize = (Integer) req.get("pageSize");
		int pageNo = (Integer) req.get("pageNo");
		try {
			List<?> cnd = getQueryCnd(startTime, endTime, Authororganization, RecordClassifying);
			SimpleDAO dao = new SimpleDAO("platform.monitor.schemas.QualityData_ErrorNumb", ctx);
			QueryResult qr = dao.find(cnd, pageNo, pageSize, null, null);
			res.put("body", qr.getRecords());
			long total = (Long) qr.getTotalCount();
			res.put("totalCount", total);
			Map<String, Object> result = new HashMap<String, Object>();
			queryQuaMainData(req, result, ctx);
			Map<String, Object> m = ((List<Map<String, Object>>)result.get("body")).get(0);
			long failTotal = (Long) m.get(RecordClassifying+"Stage1Fail");
			res.put("totalCountOnHeader", failTotal-total);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
	}
	
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		if(setDic == null){
			setDic = DictionaryController.instance().getDic("platform.dataset.dic.resDataSet");
		}
		if(orgDic == null){
			orgDic = DictionaryController.instance().getDic("platform.monitor.dic.CVX_JGDM");
		}
		Integer qd = (Integer) req.get("queryFlag");
		if(qd != null){
			if(1 == qd){	//查询有数据的业务列
				queryRecordClassifyingWhichHasData(req, res, ctx);
				return;
			}
			if(2 == qd){	//查询某个机构的数据
				queryQuaMainDataWithOga(req, res, ctx);
				return;
			}
			if(3 == qd){	//查询某个机构某个业务的错误明细
				queryQuaErorData(req, res, ctx);
				return;
			}
			if(4 == qd){	//查询某个机构某个业务的错误明细ByNum
				queryQuaErorDataByNum(req, res, ctx);
				return;
			}
		}
		queryQuaMainData(req, res, ctx);
	}
	
	private HashMap<String, Object> setQueryCnd(String startTime,String endTime,String Authororganization,String RecordClassifying,String alias){
		HashMap<String, Object> queryMap = new HashMap<String, Object>();
		alias = S.isEmpty(alias)?"":S.join(alias,".");
		StringBuffer q = new StringBuffer();
		ArrayList<String> p = new ArrayList<String>();
		//Authororganization
		if(!S.isEmpty(Authororganization)){
			q.append(alias).append(AUTHORORGANIZATION).append(" in(");
			String[] ss = Authororganization.split(",");
			for(int i=0;i<ss.length;i++){
				q.append("?");
				if(i < ss.length-1){
					q.append(",");
				}
				p.add(ss[i]);
			}
			q.append(")");
		}
		//RecordClassifying
		if(!S.isEmpty(RecordClassifying)){
			if(q.length() > 0){
				q.append(" and ");
			}
			q.append(alias).append(RECORDCLASSIFYING).append(" in(");
			String[] ss = RecordClassifying.split(",");
			for(int i=0;i<ss.length;i++){
				q.append("?");
				if(i < ss.length-1){
					q.append(",");
				}
				p.add(ss[i]);
			}
			q.append(")");
		}
		//StatDate
		if(!S.isEmpty(startTime)){
			if(q.length() > 0){
				q.append(" and ");
			}
			startTime = startTime.substring(0, 10);
			q.append(alias).append(STATDATE).append(">=to_date(?,'yyyy-MM-dd')");
			p.add(startTime);
		}
		if(!S.isEmpty(endTime)){
			if(q.length() > 0){
				q.append(" and ");
			}
			endTime = endTime.substring(0, 10);
			q.append(alias).append(STATDATE).append("<=to_date(?,'yyyy-MM-dd')");
			p.add(endTime);
		}
		
		queryMap.put("q", q.toString());
		queryMap.put("p", p);
		return queryMap;
	}
	
	private List<?> getQueryCnd(String startTime,String endTime,String Authororganization,String RecordClassifying) throws Exception{
		List<String> exps=new ArrayList<String>();
		if(!StringUtils.isEmpty(startTime)){
			startTime = startTime.substring(0, 10);
			StringBuffer s = new StringBuffer("['ge',['$','StatDate'],['todate',['s','").append(startTime).append("'],['s','yyyy-MM-dd']]]");
			exps.add(s.toString());
		}
		if(!StringUtils.isEmpty(endTime)){
			endTime = endTime.substring(0, 10);
			StringBuffer s = new StringBuffer("['le',['$','StatDate'],['todate',['s','").append(endTime).append("'],['s','yyyy-MM-dd']]]");
			exps.add(s.toString());
		}
		if(!StringUtils.isEmpty(Authororganization)){
			for(String s:Authororganization.split(",")){
				Authororganization=Authororganization.replaceAll(s, "'"+s+"'");
			}
			StringBuffer s = new StringBuffer("['in',['$','Authororganization'],[").append(Authororganization).append("]]");
			exps.add(s.toString());
		}
		if(!StringUtils.isEmpty(RecordClassifying)){
			for(String s:RecordClassifying.split(",")){
				RecordClassifying=RecordClassifying.replaceAll(s, "'"+s+"'");
			}
			StringBuffer s = new StringBuffer("['in',['$','RecordClassifying'],[").append(RecordClassifying).append("]]");
			exps.add(s.toString());
		}
		String resultExps="";
		if(exps.size()>1){
			resultExps="['and',";
			for(int i=0;i<exps.size();i++){
				if(i==(exps.size()-1)){
					resultExps=resultExps+exps.get(i)+"]";
				}else{
					resultExps=resultExps+exps.get(i)+",";
				}
			}
		}else if(exps.size()==1){
			resultExps=exps.get(0);
		}else{
			return null;
		}
		return JSONUtils.parse(resultExps, List.class);
	}
	
	private List<Map<String,Object>> find(String enrtyName,List<?> queryCnd,Context ctx) throws ServiceException{
		Schema schema=SchemaController.instance().getSchema(enrtyName);
		SimpleDAO dao=new SimpleDAO(schema, ctx);
		QueryResult qr=dao.find(queryCnd);
		return qr.getRecords(); 
	}
}
