/**
 * @(#)DebilityChildrenModel.java Created on 2012-1-12 下午4:08:59
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */

package chis.source.cdh;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.Appraise;
import chis.source.dic.BusinessType;
import chis.source.dic.PlanStatus;
import chis.source.dic.YesNo;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.ManageYearUtil;
import chis.source.util.UserUtil;
import com.alibaba.fastjson.JSONException;
import ctd.account.UserRoleToken;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */

public class DebilityChildrenModel implements BSCHISEntryNames {
	private BaseDAO dao;

	public DebilityChildrenModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 获取未结案的体弱儿档案数
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int getNoCloseRecordCount(String empiId)
			throws ModelDataOperationException {
		List<Map<String, Object>> records = getNoCloseRecord(empiId);
		if (records == null || records.size() < 1) {
			return 0;
		} else {
			return records.size();
		}
	}

	/**
	 * 获取体弱儿指导意见数据
	 * 
	 * @param diseaseType
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getChildCorrection(String diseaseType)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "diseaseType", "s",
				diseaseType);
		try {
			return dao.doLoad(cnd, CDH_DebilityCorrectionDic);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取体弱儿指导意见数据失败", e);
		}
	}

	/**
	 * 获取未结案的体弱儿档案
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getNoCloseRecord(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select empiId as empiId,recordId as recordId,debilityReason as debilityReason  from ")
				.append(CDH_DebilityChildren)
				.append(" where empiId =:empiId and status != :status and (closeFlag=:closeFlag or closeFlag is null)")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("status", Constants.CODE_STATUS_WRITE_OFF);
		parameters.put("closeFlag", YesNo.NO);
		try {
			return dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取未结案的体弱儿档案数失败", e);
		}
	}

	/**
	 * 获取体弱儿童疾病指导意见
	 * 
	 * @param recordId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getCorrection(String recordId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select debilityReason as reason from ")
				.append(CDH_DebilityChildren)
				.append(" where recordId = :recordId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("recordId", recordId);
		String debilityReason;
		try {
			Map<String, Object> reason = dao.doLoad(hql, parameters);
			if (reason == null || reason.isEmpty()) {
				return "";
			}
			debilityReason = (String) reason.get("reason");
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取儿童体弱原因失败", e);
		}
		StringTokenizer st = new StringTokenizer(debilityReason, ",");
		String hql2 = new StringBuffer("select suggestion as suggestion  from ")
				.append(CDH_DebilityCorrectionDic)
				.append(" where diseaseType = :diseaseType").toString();
		StringBuffer sb = new StringBuffer();
		List<String> l = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			if (l.contains(str)) {
				continue;
			}
			Map<String, Object> dType = new HashMap<String, Object>();
			dType.put("diseaseType", str);
			String s;
			try {
				Map<String, Object> reason = dao.doLoad(hql2, dType);
				if (reason == null || reason.isEmpty()) {
					continue;
				}
				s = (String) reason.get("suggestion");
				if (s != null && s.length() > 0) {
					Dictionary dic = DictionaryController.instance().get(
							"chis.dictionary.debilityDiseaseType");
					if (dic != null) {
						DictionaryItem di = dic.getItem(str);
						if (di != null) {
							String disease = di.getText();
							sb.append(disease).append(":\n").append(s.trim())
									.append("\n\n");
						}
					}
				}
			} catch (Exception e) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "获取体弱儿童疾病指导意见失败", e);
			}
			l.add(str);
		}
		return sb.length() == 0 ? sb.toString().trim() : sb.substring(0,
				sb.length() - 1).trim();
	}

	/**
	 * 保存儿童体弱儿档案信息
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void saveDebilityChildrenRecord(Map<String, Object> data, String op,
			Map<String, Object> res) throws ModelDataOperationException,
			ValidateException {
		try {
			Map<String, Object> genValues = dao.doSave(op,
					CDH_DebilityChildren, data, true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存儿童体弱儿档案失败。", e);
		}
	}

	/**
	 * 更新体弱儿档案转归信息
	 * 
	 * @param data
	 * @throws ModelDataOperationException
	 */
	public void updateVestingMassage(Map<String, Object> data)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append("CDH_DebilityChildren")
				.append(" set vestingCode =:vestingCode , ")
				.append("otherVesting =:otherVesting ")
				.append("where phrId=:phrId and closeFlag='").append(YesNo.NO)
				.append("'").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("vestingCode", data.get("vestingCode"));
		parameters.put("otherVesting", data.get("otherVesting"));
		parameters.put("phrId", data.get("phrId"));
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "跟新儿童体弱儿档案转归信息失败", e);
		}
	}

	/**
	 * 体弱儿档案结案
	 * 
	 * @param rec
	 * @throws ModelDataOperationException
	 */
	public void endDebilityRecord(Map<String, Object> rec)
			throws ModelDataOperationException {
		Date sd = BSCHISUtil.toDate((String) rec.get("summaryDate"));
		Date emDate = null;
		if (sd != null) {
			emDate = sd;
		} else {
			emDate = new Date();
		}
		String emDoctor = StringUtils.trimToEmpty((String) rec
				.get("summaryDoctor"));
		String emUnit = StringUtils
				.trimToEmpty((String) rec.get("summaryUnit"));
		String userId =  UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ")
				.append("CDH_DebilityChildren")
				.append(" set closedDate=cast(:closedDate as date),")
				.append(" closedDoctor=:closedDoctor, ")
				.append("closedUnit=:closedUnit, closeFlag=:closeFlag,lastModifyUser=:lastModifyUser,lastModifyDate=:lastModifyDate ")
				.append(" where phrId=:phrId and closeFlag='").append(YesNo.NO)
				.append("'").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("closedDate", emDate);
		parameters.put("closedDoctor", emDoctor);
		parameters.put("closedUnit", emUnit);
		parameters.put("closeFlag", YesNo.YES);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("phrId", (String) rec.get("phrId"));
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "儿童体弱儿档案结案失败", e);
		}
	}

	/**
	 * 获取下次随访计划是否已经做过
	 * 
	 * @param planId
	 * @param recordId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean getNextPlanVisited(String planId, String recordId)
			throws ModelDataOperationException {
		String hql = new StringBuilder("select visitDate as visitDate from ")
				.append(PUB_VisitPlan)
				.append(" where planId > :planId and businessType = :businessType and recordId = :recordId  order by planId asc")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("planId", planId);
		parameters.put("recordId", recordId);
		parameters.put("businessType", BusinessType.CD_DC);
		try {
			List<Map<String, Object>> list = dao.doQuery(hql, parameters);
			if (list == null || list.size() < 1) {
				return false;
			} else {
				Map<String, Object> map = list.get(0);
				Date visitDate = (Date) map.get("visitDate");
				if (visitDate != null) {
					return true;
				} else {
					return false;
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取下一条随访计划失败", e);
		}
	}

	/**
	 * 体弱儿档案结案并处理计划
	 * 
	 * @param recordId
	 * @throws ModelDataOperationException
	 */
	public void closeDebilityRecord(String recordId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append("CDH_DebilityChildren")
				.append(" set closeFlag= :closeFlag,closedDoctor=:closedDoctor,closedUnit=:closedUnit,closedDate=:closedDate,lastModifyUser=:lastModifyUser,lastModifyDate=:lastModifyDate where recordId =:recordId")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("closeFlag", YesNo.YES);
		parameters.put("closedDoctor",
				UserUtil.get(UserUtil.USER_ID));
		parameters.put("closedDate", new Date());
		parameters.put("closedUnit",
				UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUser",
				UserUtil.get(UserUtil.USER_ID));
		parameters.put("recordId", recordId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "儿童体弱儿档案结案失败", e);
		}

		hql = new StringBuffer("update ")
				.append("PUB_VisitPlan")
				.append(" set planstatus=:planStatus0 where recordId =:recordId and businessType=:businessType and planstatus =:planStatus ")
				.toString();
		parameters = new HashMap<String, Object>();
		parameters.put("planStatus0", PlanStatus.CLOSE);
		parameters.put("recordId", recordId);
		parameters.put("businessType", BusinessType.CD_DC);
		parameters.put("planStatus",
				String.valueOf(Constants.CODE_STATUS_NORMAL));
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "儿童体弱儿档案结案失败", e);
		}
	}

	/**
	 * @param type
	 *            表示前一年度，本年度，还是下一年度。
	 * @param rec
	 * @throws JSONException
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getVisitPlan(Map<String, Object> rec,
			int type) throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat(
				Constants.DEFAULT_SHORT_DATE_FORMAT);
		Date startDate = null;
		Date endDate = null;
		int current = ((Integer) rec.get("current")).intValue();
		Calendar c = Calendar.getInstance();
		if (type == 1) {
			c.add(Calendar.YEAR, current + 1);
			ManageYearUtil util = new ManageYearUtil(c.getTime());
			startDate = util.getDebilityChildrenPreYearStartDate();
			endDate = util.getDebilityChildrenPreYearEndDate();
		} else if (type == 2) {
			ManageYearUtil util = new ManageYearUtil(c.getTime());
			startDate = util.getDebilityChildrenCurYearStartDate();
			endDate = util.getDebilityChildrenCurYearEndDate();
		} else if (type == 3) {
			c.add(Calendar.YEAR, current - 1);
			ManageYearUtil util = new ManageYearUtil(c.getTime());
			startDate = util.getDebilityChildrenNextYearStartDate();
			endDate = util.getDebilityChildrenNextYearEndDate();
		}

		String strStartDate = sdf.format(startDate);
		String strEndDate = sdf.format(endDate);
		String recordId = (String) rec.get("recordId");
		ArrayList<Object> idCol = (ArrayList<Object>) CNDHelper.createArrayCnd(
				"$", "recordId");
		ArrayList<Object> idVal = (ArrayList<Object>) CNDHelper.createArrayCnd(
				"s", recordId);
		ArrayList<Object> idExp = (ArrayList<Object>) CNDHelper.createArrayCnd(
				"eq", idCol, idVal);

		ArrayList<Object> typeCol = (ArrayList<Object>) CNDHelper
				.createArrayCnd("$", "businessType");
		ArrayList<Object> typeValue = (ArrayList<Object>) CNDHelper
				.createArrayCnd("s", BusinessType.CD_DC);
		ArrayList<Object> typeExp = (ArrayList<Object>) CNDHelper
				.createArrayCnd("eq", typeCol, typeValue);

		ArrayList<Object> cnd0 = (ArrayList<Object>) CNDHelper.createArrayCnd(
				"and", idExp, typeExp);

		ArrayList<Object> strtCol = (ArrayList<Object>) CNDHelper
				.createArrayCnd("$", "str(planDate,'yyyy-MM-dd')");
		ArrayList<Object> strtVal = (ArrayList<Object>) CNDHelper
				.createArrayCnd("s", strStartDate);
		ArrayList<Object> strtExp = (ArrayList<Object>) CNDHelper
				.createArrayCnd("ge", strtCol, strtVal);

		ArrayList<Object> cnd1 = (ArrayList<Object>) CNDHelper.createArrayCnd(
				"and", cnd0, strtExp);

		ArrayList<Object> endCol = (ArrayList<Object>) CNDHelper
				.createArrayCnd("$", "str(planDate,'yyyy-MM-dd')");
		ArrayList<Object> endVal = (ArrayList<Object>) CNDHelper
				.createArrayCnd("s", strEndDate);
		ArrayList<Object> endExp = (ArrayList<Object>) CNDHelper
				.createArrayCnd("le", endCol, endVal);

		ArrayList<Object> cnd = (ArrayList<Object>) CNDHelper.createArrayCnd(
				"and", cnd1, endExp);
		try {
			return dao.doQuery(cnd, " planDate asc ", PUB_VisitPlan);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取随访计划列表失败", e);
		}
	}

	/**
	 * 保存体弱儿随访信息
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void saveDebilityChildrenVisit(Map<String, Object> data, String op,
			Map<String, Object> res) throws ModelDataOperationException,
			ValidateException {
		try {
			Map<String, Object> genValues = dao.doSave(op,
					CDH_DebilityChildrenVisit, data, true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存儿童体弱儿随访信息失败。", e);
		}
	}

	/**
	 * 根据身高体重获取身高别体重信息
	 * 
	 * @param height
	 * @param weight
	 * @param sex
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getWH(Double height, Double weight, String sex)
			throws ModelDataOperationException {
		if (height == null || height < 45) {
			return null;
		}
		String hql = new StringBuffer("select max(height) as maxHeight from ")
				.append(CDH_WHOHeight)
				.append(" where sexCode=:sexCode and height<=:height")
				.toString();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("sexCode", sex);
		param.put("height", height);
		Map<String, Object> mh;
		try {
			mh = dao.doLoad(hql, param);
		} catch (PersistentDataOperationException e1) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取身高别体重信息失败", e1);
		}
		Double h;
		if (mh == null || mh.get("maxHeight") == null) {
			return null;
		} else {
			h = (Double) mh.get("maxHeight");
		}
		List<Object> cnd1 = CNDHelper
				.createSimpleCnd("eq", "sexCode", "s", sex);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "height", "d", h);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		Map<String, Object> record;
		try {
			record = dao.doLoad(cnd, CDH_WHOHeight);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取身高别体重信息失败", e);
		}
		if (record == null || record.size() < 1) {
			return null;
		}
		String appraiseWH = this.getAppraiseWH(record, weight);
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("appraiseWH", appraiseWH);
		return body;
	}

	/**
	 * 获取年龄别身高体重信息
	 * 
	 * @param height
	 * @param weight
	 * @param sex
	 * @param age
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getWHY(Double height, Double weight, String sex,
			int age) throws ModelDataOperationException {
		String hql = new StringBuffer("select max(age) as maxAge from ")
				.append(CDH_WHOAge)
				.append(" where sexCode=:sexCode and age<=:age").toString();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("sexCode", sex);
		param.put("age", age);
		Map<String, Object> m;
		try {
			m = dao.doLoad(hql, param);
		} catch (PersistentDataOperationException e1) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取年龄别身高体重信息失败", e1);
		}
		int a = 0;
		if (m == null || m.get("maxAge") == null) {
			return null;
		} else {
			a = (Integer) m.get("maxAge");
		}
		List<Object> cnd1 = CNDHelper
				.createSimpleCnd("eq", "sexCode", "s", sex);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "age", "i", a);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		Map<String, Object> record;
		try {
			record = dao.doLoad(cnd, CDH_WHOAge);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取年龄别身高体重信息失败", e);
		}
		Map<String, Object> body = new HashMap<String, Object>();
		String appraiseWY = this.getAppraiseWY(record, weight);
		body.put("appraiseWY", appraiseWY);
		String appraiseHY = this.getAppraiseHY(record, height);
		body.put("appraiseHY", appraiseHY);
		return body;
	}

	/**
	 * 
	 * 
	 * @param record
	 * @param v
	 * @return
	 */
	private String getAppraiseWH(Map<String, Object> record, Double v) {
		if (v == null) {
			return null;
		}
		double sD2 = (Double) record.get("sD2") != null ? ((Double) record
				.get("sD2")).doubleValue() : 0;
		double sD2neg = (Double) record.get("sD2neg") != null ? ((Double) record
				.get("sD2neg")).doubleValue() : 0;
		if (v > sD2) {
			return Appraise.SHANG;
		} else if (v <= sD2 && v >= sD2neg) {
			return Appraise.ZHONG;
		} else if (v < sD2neg) {
			return Appraise.XIA;
		} else {
			return null;
		}
	}

	/**
	 * 
	 * 
	 * @param record
	 * @param v
	 * @return
	 */
	private String getAppraiseWY(Map<String, Object> record, Double v) {
		if (v == null) {
			return null;
		}
		double wSD2 = (Double) record.get("wSD2") != null ? ((Double) record
				.get("wSD2")).doubleValue() : 0;
		double wSD2neg = (Double) record.get("wSD2neg") != null ? ((Double) record
				.get("wSD2neg")).doubleValue() : 0;
		if (v > wSD2) {
			return Appraise.SHANG;
		} else if (v <= wSD2 && v >= wSD2neg) {
			return Appraise.ZHONG;
		} else if (v < wSD2neg) {
			return Appraise.XIA;
		} else {
			return null;
		}
	}

	/**
	 * 
	 * 
	 * @param record
	 * @param v
	 * @return
	 */
	private String getAppraiseHY(Map<String, Object> record, Double v) {
		if (v == null) {
			return null;
		}
		double hSD2 = (Double) record.get("hSD2") != null ? ((Double) record
				.get("hSD2")).doubleValue() : 0;
		double hSD2neg = (Double) record.get("hSD2neg") != null ? ((Double) record
				.get("hSD2neg")).doubleValue() : 0;
		if (v > hSD2) {
			return Appraise.SHANG;
		} else if (v <= hSD2 && v >= hSD2neg) {
			return Appraise.ZHONG;
		} else if (v < hSD2neg) {
			return Appraise.XIA;
		} else {
			return null;
		}
	}

	/**
	 * 删除体弱儿随访化验信息
	 * 
	 * @param visitId
	 * @throws HibernateException
	 * @throws ModelDataOperationException
	 */
	public void deleteCheckList(String visitId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ")
				.append(CDH_DebilityChildrenCheck)
				.append(" where visitId = :visitId").toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("visitId", visitId);
		try {
			dao.doUpdate(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除体弱儿随访化验信息失败", e);
		}
	}

	/**
	 * 保存体弱儿随访化验信息
	 * 
	 * @param data
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void saveCheck(Map<String, Object> data)
			throws ModelDataOperationException, ValidateException {
		try {
			dao.doSave("create", CDH_DebilityChildrenCheck, data, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存体弱儿随访化验信息失败", e);
		}
	}

	/**
	 * 修改体弱儿随访计划编号
	 * 
	 * @param data
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void updateDebilityPlanType(String planTypeCode, String empiId)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ")
				.append("CDH_DebilityChildren")
				.append(" set planTypeCode = :planTypeCode,lastModifyUser=:lastModifyUser,lastModifyDate=:lastModifyDate where empiId = :empiId")
				.toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("planTypeCode", planTypeCode);
		param.put("empiId", empiId);
		param.put("lastModifyUser", userId);
		param.put("lastModifyDate", new Date());
		try {
			dao.doUpdate(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "更新体弱儿童档案中的计划类型失败，数据库错误。",
					e);
		}
	}

	/**
	 * 注销档案信息
	 * 
	 * @param whereField
	 * @param whereValue
	 * @param cancellationReason
	 * @param deadReason
	 * @param logOutAll
	 * @throws ModelDataOperationException
	 */
	public void logOutDebilityChildrenRecord(String whereField,
			String whereValue, String cancellationReason, String deadReason)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ")
				.append("CDH_DebilityChildren").append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ").append(" where ")
				.append(whereField).append(" = :whereValue")
				.append("  and  status != :logout");

		String userId =  UserRoleToken.getCurrent().getUserId();
		Map<String, Object> parameters = new HashMap<String, Object>();

		parameters.put("logout", Constants.CODE_STATUS_WRITE_OFF);
		parameters.put("status", Constants.CODE_STATUS_WRITE_OFF);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("cancellationUser", userId);
		parameters.put("cancellationDate", new Date());
		parameters.put("cancellationUnit",
				UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationReason", cancellationReason);
		parameters.put("deadReason", deadReason);
		parameters.put("whereValue", whereValue);
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销档案信息失败!", e);
		}
	}

	/**
	 * 恢复体弱儿童档案
	 * 
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void revertDebilityChildrenRecord(String phrId)
			throws ModelDataOperationException {
		String userId =  UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ")
				.append("CDH_DebilityChildren")
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ")
				.append(" where phrId = :phrId and (cancellationReason<>'6' or cancellationReason is null)")
				.append(" and status = :status1").toString();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status", Constants.CODE_STATUS_NORMAL);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit",
				UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationUser", "");
		parameters.put("cancellationDate", BSCHISUtil.toDate(""));
		parameters.put("cancellationUnit", "");
		parameters.put("cancellationReason", "");
		parameters.put("deadReason", "");
		parameters.put("phrId", phrId);
		parameters.put("status1", "" + Constants.CODE_STATUS_WRITE_OFF);

		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "恢复体弱儿童档案失败！", e);
		}
	}

	/**
	 * 根据体弱儿编号获取体弱儿记录
	 * 
	 * @param recordId
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> getDebilityChildrenRecordByRecordId(
			String recordId) throws PersistentDataOperationException {
		Map<String, Object> record = this.dao.doLoad(CDH_DebilityChildren,
				recordId);
		return record;
	}

	/**
	 * 获取最后一条体弱儿档案
	 * 
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ExpException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getLastDebilityChildrenRecord(String phrId)
			throws ModelDataOperationException, ExpException {
		String exp = "['eq',['$','phrId'],['s','" + phrId + "']]";
		List<Object> cnd = (List<Object>) CNDHelper.toListCnd(exp);
		List<Map<String, Object>> list = null;
		try {
			list = (List<Map<String, Object>>) dao.doQuery(cnd,
					"recordId desc", CDH_DebilityChildren);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取随访记录记录失败。");
		}
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 获取最后一条体弱儿档案的随访计划
	 * 
	 * @param recordId
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ExpException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getLastDebilityChildrenVisitPlan(String recordId)
			throws ModelDataOperationException, ExpException {
		String exp = "['and',['eq',['$','recordId'],['s','" + recordId
				+ "']],['eq',['$','businessType'],['s','7']]]";
		List<Object> cnd = (List<Object>) CNDHelper.toListCnd(exp);
		List<Map<String, Object>> list = null;
		try {
			list = (List<Map<String, Object>>) dao.doQuery(cnd,
					"recordId desc", PUB_VisitPlan);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取随访记录记录失败。");
		}
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

}
