/**
 * @(#)VisitMZFModel.java Created on 2012-1-17 上午9:58:07
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.common.HttpclientUtil;
import chis.source.dic.BusinessType;
import chis.source.dic.PlanStatus;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.VisitPlanCreator;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HypertensionBaselineModel extends MDCBaseModel {

	public HypertensionBaselineModel(BaseDAO dao) {
		super(dao);
	}

	/**
	 * 依据慢阻肺随访主键值取取询问记录
	 * 
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getHyBaselineByPkey(String pkey)
			throws ModelDataOperationException {
		try {
			Map<String, Object> data = dao
					.doLoad(MDC_HyBaseline, pkey);
			data = SchemaUtil.setDictionaryMessageForForm(data,
					MDC_HyBaseline);
			return data;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取慢阻肺询问信息失败！", e);
		}
	}

	/**
	 * 查询当天记录
	 * 
	 * @param phrId
	 * @param inquireDate
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ParseException 
	 */
	public long CheckHasCurHyBaselineRecord(String empiId, String createDate)
			throws ModelDataOperationException, ParseException {
		String hql = new StringBuffer("select count(*) as ct from ")
				.append(MDC_HyBaseline)
				.append(" where createDate=:createDate and empiId=:empiId ")
				.toString();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(createDate);
		paramMap.put("createDate", date);
		paramMap.put("empiId", empiId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, paramMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询当天调查记录失败！", e);
		}
		return rsMap == null ? 0 : (Long) rsMap.get("ct");
	}

	/**
	 * 保存慢阻肺询问记录
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveHyBaselineInfo(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_HyBaseline, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存慢阻肺询问记录数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存慢阻肺询问记录失败！", e);
		}
		return rsMap;
	}
	
	public Map<String, Object> listHyBaselinePlanQC(Map<String, Object> req)
			throws ModelDataOperationException {
		List<?> queryCnd = null;
		if (req.get("cnd") instanceof List) {
			queryCnd = (List<?>) req.get("cnd");
		} else if (req.get("cnd") instanceof String) {
			try {
				queryCnd = CNDHelper.toListCnd((String) req.get("cnd"));
			} catch (ExpException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "数据加载失败！", e);
			}
		}
		int pageSize = Constants.DEFAULT_PAGESIZE;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = Constants.DEFAULT_PAGENO;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		Map<String, Object> res = new HashMap<String, Object>();
		// 组装SQL
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(
					"chis.application.hy.schemas.MDC_HyBaseline");
		} catch (ControllerException e1) {
			e1.printStackTrace();
		}
		StringBuffer sfBuffer = new StringBuffer();
		for (SchemaItem si : sc.getItems()) {
			if (si.isVirtual()) {
				continue;
			}
			if (si.hasProperty("refAlias")) {
				String ref = (String) si.getProperty("ref");
				sfBuffer.append(",").append(ref).append(" as ")
						.append(si.getProperty("refItemId"));
			} else {
				String f = si.getId();
				sfBuffer.append(",a.").append(f).append(" as ").append(f);
			}
		}
		String where = "";
		try {
			where = ExpressionProcessor.instance().toString(queryCnd);
		} catch (ExpException e) {
			throw new ModelDataOperationException(Constants.CODE_EXP_ERROR,
					"查询表达式转SQL失败", e);
		}
		StringBuffer countSQL = new StringBuffer("select count(distinct(empiid)) as totalCount from MZF_VISITRECORD a");
		if (S.isNotEmpty(where)) {
			countSQL.append(" where ").append(where)
			.append(" and SFFS in ('1', '4', '5')");
		}
		Map<String, Object> pMap = new HashMap<String, Object>();
		List<Map<String, Object>> thrvpcList = null;
		try {
			thrvpcList = dao.doSqlQuery(countSQL.toString(), pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "分页查询慢阻肺随访统计总记录数时失败！", e);
		}
		long totalCount = 0;
		if (thrvpcList != null && thrvpcList.size() > 0) {
			Map<String, Object> trMap = thrvpcList.get(0);
			totalCount = ((BigDecimal) trMap.get("TOTALCOUNT")).longValue();
		}
		if (totalCount > 0) {
			StringBuffer sql = new StringBuffer();
			sql.append("select id as id, a.EMPIID as EMPIID,"+
						"a.PHRID as PHRID from MZF_VISITRECORD a LEFT JOIN MPI_DemographicInfo b on a.empiid = b.empiid "+ 
						"left join EHR_HealthRecord c on c.empiid = a.empiid");
			if (S.isNotEmpty(where)) {
				sql.append(" where ").append(where)
				.append(" and visitId in (select max(visitid) from MZF_VISITRECORD a group by empiid)")
				.append(" and SFFS in ('1', '4', '5')");
			}else{
				sql.append(" where visitId in (select max(visitid) from MZF_VISITRECORD a group by empiid)");
			}
			int first = (pageNo - 1) * pageSize;
			pMap.put("first", first);
			pMap.put("max", pageSize);
			List<Map<String, Object>> rsList = null;
			try {
				rsList = dao.doSqlQuery(sql.toString(), pMap);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "分页查询糖尿病随访计划时失败！", e);
			}
			res.put("totalCount", totalCount);
			if (rsList != null && rsList.size() > 0) {
				List<Map<String, Object>> tpList = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> rMap : rsList) {
					Map<String, Object> tpMap = new HashMap<String, Object>();
					for (SchemaItem si : sc.getItems()) {
						if (si.isVirtual()) {
							continue;
						}
						if (si.hasProperty("refAlias")) {
							String refItemId = (String) si
									.getProperty("refItemId");
							tpMap.put(refItemId,
									rMap.get(refItemId.toUpperCase()));
						} else {
							String f = si.getId();
							tpMap.put(f, rMap.get(f.toUpperCase()));
						}
					}
					if (tpMap.get("birthday") != null) {
						tpMap.put("age", BSCHISUtil.calculateAge(
								(Date) tpMap.get("birthday"), new Date()));
					}
					tpList.add(tpMap);
				}
				res.put("body", SchemaUtil.setDictionaryMessageForList(tpList,
						"chis.application.hy.schemas.MDC_HyBaseline"));
			} else {
				res.put("body", new ArrayList<Map<String, Object>>());
			}
		} else {
			res.put("totalCount", 0);
			res.put("body", new ArrayList<Map<String, Object>>());
		}
		res.put("pageSize", pageSize);
		res.put("pageNo", pageNo);
		return res;
	}
	
	public Map<String, Object> getHealthCheckInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ModelDataOperationException{
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> parameters = new HashMap<String,Object>();
		String empiId = (String) body.get("empiId");
		parameters.put("empiId", empiId);
		try {
			List<Map<String,Object>> healthCheckInfo = dao.doSqlQuery("select * from (select a.YEAR,a.HEIGHT as SG,a.WEIGHT as TZ,a.BMI as TZZS,b.WEHTHERSMOKE as XYZK,"+
						"b.BEGINSMOKETIME as XYZKJYKSNL,b.smokes as XYZKJYMTJZ,b.stopSmokeTime as XYZKJYNL,b.drinkingFrequency as YJQK,"+
						"b.alcoholConsumption as YJQKORMTJL,b.whetherDrink as SFJJ,b.geginToDrinkTime as KSYJNL,"+
						"b.stopDrinkingTime as SFJJYJJJJNL,b.isDrink as JYNSFJJ,b.mainDrinkingVvarieties as YJZL,b.drinkOther as YJZLQT,"+
						"c.HEARTRATE as XL,c.kalemia as XJ,c.natremia as XN,c.alt as BASAJZYM,c.ast as TMDASAJZYM,c.BUN as XNSD,"+
						"c.cr as XJG,c.tc as ZDGC,c.tg as GYSZ,c.ldl as DMDZDBDGC,c.hdl as GMDZDBDGC,c.fbs as KFXT,c.ecg as XDT,c.ecgText as XDTYC "+
						"from HC_HealthCheck a left join HC_LifestySituation b on a.HEALTHCHECK = b.HEALTHCHECK left join HC_HEALTHEXAMINATION c "+
						"on a.HEALTHCHECK = c.HEALTHCHECK where a.EMPIID = :empiId order by a.CHECKDATE desc) where rownum = 1", parameters);
			
			List<Map<String,Object>> medicineInfo = dao.doSqlQuery("select MEDICINE as YWMC,USE as YWYF,eachDose as YWJL,USEDATE as YWSJ,medicineYield as YWYCX from HC_MEDICINESITUATION "+
						"where HEALTHCHECK = (select HEALTHCHECK from (select HEALTHCHECK from HC_HEALTHCHECK where EMPIID = :empiId order by CHECKDATE desc) where rownum = 1) ", parameters);
			res.put("healthCheckInfo", healthCheckInfo);
			res.put("medicineInfo", medicineInfo);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	@SuppressWarnings("unchecked")
	public void doGetJxdcUrl_HTTPPOST(Map<String, Object> req, Map<String, Object> res, Context ctx) throws ServiceException{
	    try {
	    	Map<String, Object> body = (Map<String, Object>)req.get("body");
	    	Map<String, Object> parameters = new HashMap<String,Object>();
	    	String empiId = body.get("empiId").toString();
	    	String pdata = body.get("pdata").toString();
	    	parameters.put("empiId", empiId);
			//获取接口url	
			String url=DictionaryController.instance().getDic("chis.dictionary.httppost_HyBaseline").getText("url"); 
			Map<String, Object> bodyMap = new HashMap<String, Object>();
			bodyMap.put("body", pdata);
			String jsonStr = JSONUtils.toJSONString(bodyMap);
			String encryptedStr = HttpclientUtil.sendHttpPost_JSON(url, jsonStr);
			encryptedStr = URLEncoder.encode(encryptedStr,"utf-8"); 	
			if(encryptedStr != null && encryptedStr.equals("SUCCESS")){
				dao.doSqlUpdate("update MDC_HyBaseline set SFSC='1' where empiId = :empiId", parameters);
			}
			res.put("tagUrl", url);
			System.out.println(encryptedStr);
	    }catch (Exception e){
			throw new ServiceException("调取高血压基线表接口失败！",e);
	    }
	}
}
