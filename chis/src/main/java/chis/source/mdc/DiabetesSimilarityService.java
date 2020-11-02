/**
 * @(#)DiabetesService.java Created on 2012-1-18 上午9:57:37
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.lang.StringUtils;
import org.mvel2.ast.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.ctd.exp.DATEDEC;
import chis.source.dic.BusinessType;
import chis.source.dic.DiagnosisType;
import chis.source.dic.WorkType;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.VisitPlanCreator;
import ctd.account.UserRoleToken;
import ctd.dao.SimpleDAO;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleQuery;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class DiabetesSimilarityService extends DiabetesService {
	private static final Logger logger = LoggerFactory
			.getLogger(DiabetesSimilarityService.class);

	private VisitPlanCreator visitPlanCreator;

	/**
	 * 获得visitPlanCreator
	 * 
	 * @return the visitPlanCreator
	 */
	public VisitPlanCreator getVisitPlanCreator() {
		return visitPlanCreator;
	}

	/**
	 * 设置visitPlanCreator
	 * 
	 * @param visitPlanCreator
	 *            the visitPlanCreator to set
	 */
	public void setVisitPlanCreator(VisitPlanCreator visitPlanCreator) {
		this.visitPlanCreator = visitPlanCreator;
	}

	public void doInitializeSimilarity(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, ValidateException,
			DateParseException, ExpException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		StringBuffer cnd = new StringBuffer();
		cnd.append("['eq',['$','a.empiId'],['s','").append(empiId)
				.append("']]");
		List<Map<String, Object>> m = dao.doList(
				CNDHelper.toListCnd(cnd.toString()), "a.registerDate desc",
				MDC_DiabetesSimilarity);
		if (m != null && m.size() > 0) {
			res.put("body", m.get(0));
		}
	}

	@SuppressWarnings("unchecked")
	public void doListDiabetesSimilarity(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, DateParseException,
			ServiceException {
		SimpleQuery query = new SimpleQuery();
		query.execute(req, res, ctx);
		List<Map<String, Object>> body = (List<Map<String, Object>>) res
				.get("body");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> para = new HashMap<String, Object>();
		String sql = "select empiId from MDC_DiabetesRecord where status='0' and empiId=:empiId";
		for (Map<String, Object> map : body) {
			String empiId = (String) map.get("empiId");
			para.put("empiId", empiId);
			List<Map<String, Object>> l = dao.doSqlQuery(sql, para);
			if (l != null && l.size() > 0) {
				map.put("dbsCreate", "y");
				map.put("dbsCreate_text", "是");
			} else {
				map.put("dbsCreate", "n");
				map.put("dbsCreate_text", "否");
			}
			result.add(map);
		}
		res.put("body", result);
	}

	@SuppressWarnings("unchecked")
	public void doSaveDiabetesSimilarity(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, DateParseException,
			ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		body.put("diagnosisType", DiagnosisType.YS);
		Map<String, Object> m = dao.doSave(op,
				BSCHISEntryNames.MDC_DiabetesSimilarity, body, false);
		res.put("body", m);
		String similarityId = (String) body.get("similarityId");
		String empiId = (String) body.get("empiId");
		if ("create".equals(op)) {
			similarityId = (String) m.get("similarityId");
		}
		vLogService.saveVindicateLog(MDC_DiabetesSimilarity, op, similarityId,
				dao, empiId);
	}

	@SuppressWarnings("unchecked")
	public void doSaveDiabetesSimilarityCheck(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, DateParseException,
			ModelDataOperationException, ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
				body.get("empiId"));
		List<Map<String, Object>> recordList = dao.doList(cnd, "",
				MDC_DiabetesRecord);
		if (recordList != null && recordList.size() > 0) {
			if (body.get("result2") != null && "5".equals(body.get("result2"))) {
				body.put("diagnosisType", "1");
			} else if (body.get("result1") != null
					&& "5".equals(body.get("result1"))) {
				body.put("diagnosisType", "1");
			}
		} else {
			body.put("diabetesRecord", true);
		}
		Map<String, Object> m = dao.doSave(op,
				BSCHISEntryNames.MDC_DiabetesSimilarity, body, false);
		m.putAll(body);
		res.put("body", SchemaUtil.setDictionaryMessageForForm(m,
				MDC_DiabetesSimilarityCheck));
		String similarityId = (String) body.get("similarityId");
		String empiId = (String) body.get("empiId");
		vLogService.saveVindicateLog(MDC_DiabetesSimilarity, "6", similarityId,
				dao, empiId);

		if (body.get("diagnosisType").equals(DiagnosisType.QZ)) {
			DiabetesRecordModel recordModel = new DiabetesRecordModel(dao);
			List<Map<String, Object>> l = recordModel
					.findDiabetesRecordByEmpiId((String) body.get("empiId"));

			if (l == null || l.size() == 0) {
				Map<String, Object> workList = new HashMap<String, Object>();
				Calendar c = Calendar.getInstance();
				workList.put("recordId", body.get("phrId"));
				workList.put("empiId", body.get("empiId"));
				workList.put("beginDate", c.getTime());
				c.add(Calendar.YEAR, 1);
				workList.put("endDate", c.getTime());
				workList.put("count", 1);
				UserRoleToken ur = UserRoleToken.getCurrent();
				workList.put("doctorId", ur.getUserId());
				workList.put("manaUnitId", ur.getManageUnitId());

				recordModel.addDiabetesRecordWorkList(workList);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void doUpdateSimilarityType(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String similarityId = (String) req.get("similarityId");
		String diagnosisType = (String) req.get("diagnosisType");
		String hql = "update "
				+ MDC_DiabetesSimilarityCheck
				+ " set diagnosisType=:diagnosisType where similarityId=:similarityId";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("similarityId", similarityId);
		parameters.put("diagnosisType", diagnosisType);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveConfirmSimilarity(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, ValidateException,
			DateParseException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> workList = new HashMap<String, Object>();
		Calendar c = Calendar.getInstance();
		workList.put("recordId", body.get("phrId"));
		workList.put("empiId", body.get("empiId"));
		workList.put("beginDate", c.getTime());
		c.add(Calendar.YEAR, 1);
		workList.put("endDate", c.getTime());
		workList.put("count", 1);
		UserRoleToken ur = UserRoleToken.getCurrent();
		workList.put("doctorId", ur.getUserId());
		workList.put("manaUnitId", ur.getManageUnitId());

		DiabetesRecordModel recordModel = new DiabetesRecordModel(dao);
		recordModel.addDiabetesRecordWorkList(workList);
		// Map<String, Object> risk = new HashMap<String, Object>();

	}

	@SuppressWarnings("unchecked")
	public void doGetHasDiabetesRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, ValidateException,
			DateParseException {
		String empiId = (String) req.get("empiId");
		String op = (String) req.get("op");
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
				empiId);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "a.status", "s",
				"0");
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> recordList = dao.doList(cnd, "",
				MDC_DiabetesRecord);
		if (recordList != null && recordList.size() > 0) {
			res.put("diagnosisType", "1");
			res.put("diabetesRecord", true);
		} else {
			res.put("diabetesRecord", false);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveTurnHighRisk(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, ValidateException,
			DateParseException, ParseException, ModelDataOperationException {
		String similarityId = (String) req.get("similarityId");
		String empiId = (String) req.get("empiId");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
				empiId);
		List<Map<String, Object>> recordList = dao.doList(cnd,
				"a.registerDate desc", MDC_DiabetesOGTTRecord);
		if (recordList != null && recordList.size() > 0) {
			Map<String, Object> record = recordList.get(0);
			for (Iterator it = body.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				Object value = body.get(key);
				if (record.get("result3") != null) {
					key = key + "3";
				} else if (record.get("result2") != null) {
					key = key + "3";
				} else if (record.get("result1") != null) {
					key = key + "2";
				} else {
					key = key + "1";
				}
				record.put(key, value);
				if (key.startsWith("checkDate")) {
					if (value != null && !"".equals(value)) {
						record.put(key, new SimpleDateFormat("yyyy-mm-dd")
								.parse(value.toString().substring(0, 10)));
					} else {
						record.put(key, null);
					}
				}
				if (key.startsWith("riskiness")) {
					record.put("riskFactors", value);
				}
			}
			record.put("dbsCreate", "y");
			dao.doSave("update", MDC_DiabetesOGTTRecord, record, true);
		} else {
			Map<String, Object> record = new HashMap<String, Object>();
			Map<String, Object> visitPlan = new HashMap<String, Object>();
			record.put("empiId", empiId);
			List<Map<String, Object>> list = dao.doList(cnd, "",
					EHR_HealthRecord);
			if (list != null && list.size() > 0) {
				record.put("phrId", list.get(0).get("phrId"));
				visitPlan.put("recordId", list.get(0).get("phrId"));
			} else {
				res.put("hasHealthRecord", true);
				throw new ModelDataOperationException(
						Constants.CODE_RECORD_NOT_FOUND, "没有健康档案不能建高危档案");
			}
			Date nextDate = new Date();
			String result = (String) body.get("result");
			int year = Integer.parseInt(nextDate.getYear() + "");
			if ("1".equals(result)) {
				nextDate.setYear(year + 3);
				record.put("nextScreenDate", nextDate);
			} else if ("2".equals(result) || "3".equals(result)) {
				nextDate.setYear(year + 1);
				record.put("nextScreenDate", nextDate);
			}
			for (Iterator it = body.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				Object value = body.get(key);
				key = key + "1";
				record.put(key, value);
			}
			UserRoleToken urt = UserRoleToken.getCurrent();
			record.put("registerDate", new Date());
			record.put("registerUser", urt.getUserId());
			record.put("registerUnit", urt.getManageUnitId());
			record.put("superDiagnose", "n");
			record.put("dbsCreate", "n");
			if (record.get("checkDate1") != null
					&& !"".equals(record.get("checkDate1"))) {
				record.put(
						"checkDate1",
						new SimpleDateFormat("yyyy-mm-dd").parse(record
								.get("checkDate1").toString().substring(0, 10)));
			}
			record.put("inputUnit", urt.getManageUnitId());
			record.put("inputUser", urt.getUserId());
			record.put("inputDate", new Date());
			record.put("lastModifyUser", urt.getUserId());
			record.put("lastModifyDate", new Date());
			record.put("lastModifyUnit", urt.getManageUnitId());
			Map<String, Object> bodyOGTT = dao.doSave("create",
					MDC_DiabetesOGTTRecord, record, true);

			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			visitPlan.put("empiId", empiId);
			visitPlan.put("planStatus", "1");
			visitPlan.put("businessType", BusinessType.OGTT);
			visitPlan.put("planDate", c.getTime());
			visitPlan.put("beginDate", c.getTime());
			visitPlan.put("beginVisitDate", c.getTime());
			visitPlan.put("visitDate", c.getTime());
			visitPlan.put("visitId", bodyOGTT.get("OGTTID"));
			Date endDate = new Date();
			endDate.setTime(Long.MAX_VALUE);
			visitPlan.put("endDate", endDate);
			UserRoleToken ur = UserRoleToken.getCurrent();
			visitPlan.put("lastModifyUnit", ur.getManageUnitId());
			visitPlan.put("lastModifyUser", ur.getUserId());
			visitPlan.put("lastModifyDate", new Date());
			dao.doSave("create", PUB_VisitPlan, visitPlan, true);

			c.setTime(nextDate);
			visitPlan.put("empiId", empiId);
			visitPlan.put("planStatus", "0");
			visitPlan.put("businessType", BusinessType.OGTT);
			visitPlan.put("planDate", c.getTime());
			visitPlan.put("beginDate", c.getTime());
			visitPlan.put("beginVisitDate", c.getTime());
			visitPlan.put("visitId", null);
			visitPlan.put("visitDate", null);
			visitPlan.put("endDate", endDate);
			visitPlan.put("lastModifyUnit", ur.getManageUnitId());
			visitPlan.put("lastModifyUser", ur.getUserId());
			visitPlan.put("lastModifyDate", new Date());
			dao.doSave("create", PUB_VisitPlan, visitPlan, true);
		}
	}

	public void doLoadDiabetesSimilarityCheck(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException {
		String pkey = (String) req.get("pkey");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		if (pkey != null) {
			res.put("body", SchemaUtil.setDictionaryMessageForForm(
					dao.doLoad(MDC_DiabetesSimilarityCheck, pkey),
					MDC_DiabetesSimilarityCheck));
		} else if (body != null) {
			String empiId = (String) body.get("empiId");
			List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "a.empiId",
					"s", empiId);
			List<Object> cnd2 = CNDHelper.createSimpleCnd("eq",
					"a.diagnosisType", "s", "2");
			List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<Map<String, Object>> list = dao.doList(cnd,
					"a.registerDate desc", MDC_DiabetesSimilarityCheck);
			if (list != null && list.size() > 0) {
				res.put("body", SchemaUtil.setDictionaryMessageForForm(
						list.get(0), MDC_DiabetesSimilarityCheck));
			}
		}
	}

}
