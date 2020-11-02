/**
 * @(#)DiabetesOGTTModel.java Created on 2015-1-8 上午9:26:47
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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import ctd.account.UserRoleToken;
import ctd.service.dao.SimpleLoad;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.DiagnosisType;
import chis.source.util.CNDHelper;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class DiabetesOGTTModel extends DiabetesModel {

	public DiabetesOGTTModel(BaseDAO dao) {
		super(dao);
	}

	public Map<String, Object> listDiabetesOGTTRecord(Map<String, Object> req)
			throws ModelDataOperationException {
		Map<String, Object> result = new HashMap<String, Object>();
		List<?> queryCnd = null;
		if (req.get("cnd") != null) {
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
		}
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.inputDate", "$",
				"(select max(t.inputDate) from " + MDC_DiabetesOGTTRecord
						+ " t where t.empiId=a.empiId)");
		if (queryCnd != null && queryCnd.size() > 0) {
			queryCnd = CNDHelper.createArrayCnd("and", queryCnd, cnd);
		} else {
			queryCnd = cnd;
		}
		String schemaName = (String) req.get("schema");
		if (StringUtils.isEmpty(schemaName) || queryCnd == null
				|| queryCnd.size() == 0) {
			new ModelDataOperationException(Constants.CODE_INVALID_REQUEST,
					"参数获取失败！");
		}
		String queryCndsType = null;
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String sortInfo = null;
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
		}
		int pageSize = Constants.DEFAULT_PAGESIZE;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = Constants.DEFAULT_PAGENO;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		try {
			Map<String, Object> rsMap = dao.doList(queryCnd, sortInfo,
					schemaName, pageNo, pageSize, queryCndsType);
			result.put("pageSize", pageSize);
			result.put("pageNo", pageNo);
			result.put("totalCount", rsMap.get("totalCount"));
			List<Map<String, Object>> body = (List<Map<String, Object>>) rsMap
					.get("body");
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
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
				resultList.add(map);
			}
			result.put("body", resultList);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "数据加载失败！", e);
		}
		return result;
	}

	public Map<String, Object> insertDiabetesSimilarity(
			Map<String, Object> body, String flag)
			throws ModelDataOperationException {
		Map<String, Object> record = new HashMap<String, Object>();
		UserRoleToken urt = UserRoleToken.getCurrent();
		record.put("registerDate", new Date());
		record.put("registerUser", urt.getUserId());
		record.put("registerUnit", urt.getManageUnitId());
		record.put("empiId", body.get("empiId"));
		record.put("riskiness", body.get("riskFactors"));
		record.put("fbs", body.get("fbs" + flag));
		record.put("pbs", body.get("pbs" + flag));
		record.put("diagnosisType", "2");
		record.put("manaUnitId", body.get("manaUnitId"));
		record.put("inputUnit", urt.getManageUnitId());
		record.put("inputUser", urt.getUserId());
		record.put("inputDate", new Date());
		Map<String, Object> result = null;
		try {
			result = dao.doSave("create", MDC_DiabetesSimilarityCheck, record,
					true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "生成糖尿病疑似记录失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "生成糖尿病疑似记录失败！", e);
		}
		return result;

	}

	public Map<String, Object> saveDiabetesOGTTRecord(String schemaId,
			Map<String, Object> rec, String op)
			throws ModelDataOperationException {
		Map<String, Object> result = null;
		try {
			result = dao.doSave(op, schemaId, rec, true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存数据失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存数据失败！", e);
		}
		if (rec.get("planId") != null) {
			String sql = "update " + PUB_VisitPlan
					+ " set planStatus='1' , visitDate=:visitDate , "
					+ "visitId=:visitId where planId=:planId";
			String visitId = (String) rec.get("OGTTID");
			if (visitId == null) {
				visitId = (String) result.get("OGTTID");
			}
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("visitDate", new Date());
			param.put("visitId", visitId);
			param.put("planId", (String) rec.get("planId"));
			try {
				dao.doUpdate(sql, param);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "更新计划失败！", e);
			}
		}
		String hql = "select max(planId) as planId from " + PUB_VisitPlan
				+ " where empiId=:empiId and businessType='17'";
		Map<String, Object> paramerter = new HashMap<String, Object>();
		paramerter.put("empiId", (String) rec.get("empiId"));
		boolean needUpdatePlan = false;
		boolean needCreatePlan = false;
		try {
			List<Map<String, Object>> list = dao.doQuery(hql, paramerter);
			if (list != null && list.size() > 0
					&& list.get(0).get("planId") != null) {
				if ((rec.get("planId") + "").equals(list.get(0).get("planId"))) {
					needUpdatePlan = true;
				}
			} else {
				needUpdatePlan = true;
			}
		} catch (PersistentDataOperationException e2) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新计划失败！", e2);
		}
		String result1 = (String) rec.get("result1");
		String result2 = (String) rec.get("result2");
		String result3 = (String) rec.get("result3");
		if (result3 != null && !"".equals(result3) && "4".equals(result3)) {
			insertDiabetesSimilarity(rec, "3");
		} else if (result2 != null && !"".equals(result2)
				&& "4".equals(result2)) {
			insertDiabetesSimilarity(rec, "2");
		} else if (result1 != null && !"".equals(result1)
				&& "4".equals(result1)) {
			insertDiabetesSimilarity(rec, "1");
		}
		if (needUpdatePlan) {
			if ("update".equals(op) && rec.get("planId") != null) {
				String sql = "delete from " + PUB_VisitPlan
						+ "  where planId>:planId and visitId is null";
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("planId", (String) rec.get("planId"));
				try {
					dao.doUpdate(sql, param);
				} catch (PersistentDataOperationException e) {
					throw new ModelDataOperationException(
							Constants.CODE_DATABASE_ERROR, "更新计划失败！", e);
				}
			} else {
				needCreatePlan = true;
			}
			Map<String, Object> visitPlan = new HashMap<String, Object>();
			visitPlan.put("recordId", rec.get("phrId"));
			Calendar c = Calendar.getInstance();
			Date endDate = new Date();
			endDate.setTime(Long.MAX_VALUE);
			UserRoleToken ur = UserRoleToken.getCurrent();
			Date nextDate = null;
			try {
				if (rec.get("planDate") != null) {
					nextDate = new SimpleDateFormat("yyyy-mm-dd").parse(rec
							.get("planDate").toString().substring(0, 10));
				} else {
					nextDate = new Date();
				}
			} catch (ParseException e1) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "更新计划失败！", e1);
			}
			int year = Integer.parseInt(nextDate.getYear() + "");
			if (result3 != null && !"".equals(result3)) {
				if ("1".equals(result3)) {
					nextDate.setYear(year + 3);
				} else if ("2".equals(result3) || "3".equals(result3)) {
					nextDate.setYear(year + 1);
				}
			} else if (result2 != null && !"".equals(result2)) {
				if ("1".equals(result2)) {
					nextDate.setYear(year + 3);
				} else if ("2".equals(result2) || "3".equals(result2)) {
					nextDate.setYear(year + 1);
				}
			} else if (result1 != null && !"".equals(result1)) {
				if ("1".equals(result1)) {
					nextDate.setYear(year + 3);
				} else if ("2".equals(result1) || "3".equals(result1)) {
					nextDate.setYear(year + 1);
				}
			} else {
				return result;
			}
			c.setTime(nextDate);
			visitPlan.put("empiId", rec.get("empiId"));
			visitPlan.put("businessType", BusinessType.OGTT);
			visitPlan.put("visitId", null);
			visitPlan.put("visitDate", null);
			visitPlan.put("endDate", endDate);
			visitPlan.put("lastModifyUnit", ur.getManageUnitId());
			visitPlan.put("lastModifyUser", ur.getUserId());
			visitPlan.put("lastModifyDate", new Date());
			try {
				if (needCreatePlan == true) {
					visitPlan.put("planDate", new Date());
					visitPlan.put("beginDate", new Date());
					visitPlan.put("beginVisitDate", new Date());
					String visitId = "";
					if (rec.get("OGTTID") != null) {
						visitId = (String) rec.get("OGTTID");
					} else {
						visitId = (String) result.get("OGTTID");
					}
					visitPlan.put("planStatus", "1");
					visitPlan.put("visitDate", new Date());
					visitPlan.put("visitId", visitId);
					dao.doSave("create", PUB_VisitPlan, visitPlan, true);
				}
				visitPlan.put("planDate", c.getTime());
				visitPlan.put("beginDate", c.getTime());
				visitPlan.put("beginVisitDate", c.getTime());
				visitPlan.put("planStatus", "0");
				if (visitPlan.containsKey("visitDate")) {
					visitPlan.remove("visitDate");
				}
				if (visitPlan.containsKey("visitId")) {
					visitPlan.remove("visitId");
				}
				dao.doSave("create", PUB_VisitPlan, visitPlan, true);
			} catch (ValidateException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "更新计划失败！", e);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "更新计划失败！", e);
			}
		}
		return result;
	}

	/**
	 * 插入糖尿病高危记录
	 * 
	 * @param empiId
	 * @param phrId
	 * @param fbs空腹血糖
	 *            (mmol/L)
	 * @param pbs餐后血糖
	 *            (mmol/L)
	 * @param data数据
	 */
	public void insertDiabetesOGTTRecord(String empiId, String phrId,
			Double fbs, Double pbs, Map<String, Object> data) {
		if (fbs == null && pbs == null) {
			return;
		}
		if (pbs != null && pbs >= 7.8 && pbs <= 11.0) {
		} else if (fbs != null && fbs >= 6.1 && fbs <= 6.9) {
		} else {
			return;
		}
		// if (fbs == null) {
		// fbs = 0.0;
		// }
		// if (pbs == null) {
		// pbs = 0.0;
		// }
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
				empiId);
		try {
			List<Map<String, Object>> list1 = dao.doQuery(cnd, "a.empiId",
					MDC_DiabetesRecord);
			List<Map<String, Object>> list2 = dao.doQuery(cnd,
					"a.inputDate desc", MDC_DiabetesOGTTRecord);
			if (list1 != null && list1.size() > 0) {
				return;
			}
			if (list2 != null && list2.size() > 0) {
				return;
			}
			if (phrId == null || "".equals(phrId)) {
				List<Map<String, Object>> list3 = dao.doQuery(cnd, "a.empiId",
						EHR_HealthRecord);
				if (list3 != null && list3.size() > 0) {
					phrId = (String) list3.get(0).get("phrId");
				}
			}
			if (phrId == null || "".equals(phrId)) {
				return;
			}
			HashMap<String, Object> rec = new HashMap<String, Object>();
			UserRoleToken urt = UserRoleToken.getCurrent();
			rec.put("empiId", empiId);
			rec.put("phrId", phrId);
			rec.put("fbs1", fbs);
			rec.put("pbs1", pbs);
			rec.put("dbsCreate", "n");
			String result1 = getOGTTResultByBs(fbs, pbs, false);
			rec.put("result1", result1);
			rec.put("checkDate1", new Date());
			rec.put("checkUser1", urt.getUserId());
			rec.put("registerDate", new Date());
			rec.put("registerUser", urt.getUserId());
			rec.put("registerUnit", urt.getManageUnitId());
			rec.put("inputDate", new Date());
			rec.put("inputUser", urt.getUserId());
			rec.put("inputUnit", urt.getManageUnitId());
			Map<String, Object> result = dao.doSave("create",
					MDC_DiabetesOGTTRecord, rec, true);
			Map<String, Object> visitPlan = new HashMap<String, Object>();
			Date endDate = new Date();
			endDate.setTime(Long.MAX_VALUE);
			visitPlan.put("recordId", phrId);
			visitPlan.put("planDate", new Date());
			visitPlan.put("beginDate", new Date());
			visitPlan.put("empiId", rec.get("empiId"));
			visitPlan.put("businessType", BusinessType.OGTT);
			visitPlan.put("endDate", endDate);
			visitPlan.put("lastModifyUnit", urt.getManageUnitId());
			visitPlan.put("lastModifyUser", urt.getUserId());
			visitPlan.put("lastModifyDate", new Date());
			visitPlan.put("beginVisitDate", new Date());
			String visitId = (String) result.get("OGTTID");
			visitPlan.put("planStatus", "1");
			visitPlan.put("visitDate", new Date());
			visitPlan.put("visitId", visitId);
			dao.doSave("create", PUB_VisitPlan, visitPlan, true);
			Calendar c = Calendar.getInstance();
			Date nextDate = new Date();
			int year = Integer.parseInt(nextDate.getYear() + "");
			if (result1 != null && !"".equals(result1)) {
				if ("1".equals(result1)) {
					nextDate.setYear(year + 3);
				} else if ("2".equals(result1) || "3".equals(result1)) {
					nextDate.setYear(year + 1);
				}
			}
			c.setTime(nextDate);
			visitPlan.put("planDate", c.getTime());
			visitPlan.put("beginDate", c.getTime());
			visitPlan.put("beginVisitDate", c.getTime());
			visitPlan.put("planStatus", "0");
			if (visitPlan.containsKey("visitDate")) {
				visitPlan.remove("visitDate");
			}
			if (visitPlan.containsKey("visitId")) {
				visitPlan.remove("visitId");
			}
			dao.doSave("create", PUB_VisitPlan, visitPlan, true);
		} catch (PersistentDataOperationException e2) {
			e2.printStackTrace();
		} catch (ValidateException e) {
			e.printStackTrace();
		}

	}

	private String getOGTTResultByBs(Double fbs, Double pbs,
			boolean clinicSymptom) {
		String result = "";
		if (fbs == null) {
			fbs = 0.0;
		}
		if (pbs == null) {
			pbs = 0.0;
		}
		if (fbs < 6.1 && pbs < 7.8) {
			result = "1";
		} else if (fbs >= 6.1 && fbs <= 6.9 && pbs < 7.8) {
			result = "2";
		} else if (pbs >= 7.8 && pbs <= 11.0 && fbs <= 6.9) {
			result = "3";
		} else if (clinicSymptom) {
			result = "5";
		} else {
			result = "4";
		}
		return result;
	}
}
