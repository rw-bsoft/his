/**
 * @(#)HypertensionModel.java Created on 2011-12-28 下午2:35:02
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.HyperRecordStatus;
import chis.source.dic.PlanStatus;
import chis.source.dic.VisitEffect;
import chis.source.dic.WorkType;
import chis.source.pub.PublicModel;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;

import com.alibaba.fastjson.JSONException;

import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;

/**
 * @description 高血压档案业务模型类
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HypertensionModel extends MDCBaseModel {

	/**
	 * @param dao
	 */
	public HypertensionModel(BaseDAO dao) {
		super(dao);
	}

	public void addHypertensionRecordWorkList(Map<String, Object> m)
			throws ValidateException, PersistentDataOperationException {
		m.put("workType", WorkType.MDC_HYPERTENSIONRECORD);
		dao.doInsert(PUB_WorkList, m, false);
	}

	/**
	 * 依据empiId获取高血压档案记录
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> findHypertensionRecordByEmpiId(
			String empiId) throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao
					.doQuery(cnd, "createDate desc", MDC_HypertensionRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取高血压档案记录失败！", e);
		}
		return rsList;
	}

	/**
	 * 依据cnd表达式查高血压记录
	 * 
	 * @param cnd
	 * @param order
	 *            排序方式
	 * @param schema
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> findHypertensionRecord(List<?> cnd,
			String order, String schema) throws ModelDataOperationException {
		List<Map<String, Object>> rsList = new ArrayList<Map<String, Object>>();
		try {
			rsList = dao.doList(cnd, order, schema);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询高血压信息失败！");
		}
		return rsList;
	}

	/**
	 * 根据phrid查高血压信息<br>
	 * 当参数ifSetDicInfo设置为true时做字典转换处理
	 * 
	 * @param phrid
	 * @param ifSetDicInfo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getHypertensionByPhrid(String phrid)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "phrid", "s", phrid);
		Map<String, Object> rsInfo = null;
		try {
			rsInfo = dao.doLoad(cnd, MDC_HypertensionRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "加载高血压信息失败！");
		}
		return rsInfo;
	}

	/**
	 * 根据empiId查询未被注销过的高血压档案
	 * 
	 * @param empiId
	 * @param ifSetDicInfo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getHypertensionByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("ne", "status", "s",
				Constants.CODE_STATUS_WRITE_OFF);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);

		Map<String, Object> rsInfo = null;
		try {
			rsInfo = dao.doLoad(cnd, MDC_HypertensionRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "加载高血压信息失败！");
		}
		return rsInfo;
	}

	/**
	 * 注销高血压档案
	 * 
	 * @param phrId
	 * @param cancellationReason
	 * @param deadReason
	 * @throws ModelDataOperationException
	 */
	public void logoutHypertensionRecord(String phrId,
			String cancellationReason, String deadReason)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		StringBuffer hql = new StringBuffer("update ")
				.append("MDC_HypertensionRecord")
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ")
				.append(" where phrId = :phrId and (cancellationReason<>'6' ")
				.append(" or cancellationReason is null) ")
				.append("  and  status = :normal");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("normal", Constants.CODE_STATUS_NORMAL);
		parameters.put("status", Constants.CODE_STATUS_WRITE_OFF);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationUser", userId);
		parameters.put("cancellationDate", new Date());
		parameters.put("cancellationUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationReason", cancellationReason);
		parameters.put("deadReason", deadReason);
		parameters.put("phrId", phrId);
		//注销随访
		String zxsfsql="update PUB_VisitPlan set planStatus='9' where recordId=:phrId " +
				" and planStatus in ('0','3') and businessType='1' ";
		try {
			dao.doUpdate(hql.toString(), parameters);
			Map<String, Object> p = new HashMap<String, Object>();
			p.put("phrId", phrId);
			dao.doUpdate(zxsfsql, p);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销高血压档案失败！", e);
		}
	}

	/**
	 * 恢复高血压档案 ---取消注销
	 * 
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void revertHypertensionRecord(String phrId)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ")
				.append("MDC_HypertensionRecord")
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationCheckUser = :cancellationCheckUser, ")
				.append(" cancellationCheckDate = :cancellationCheckDate, ")
				.append(" cancellationCheckUnit = :cancellationCheckUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ")
				.append(" where phrId = :phrId ")
				.append(" and status = :status1").toString();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status", Constants.CODE_STATUS_NORMAL);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationCheckUser", "");
		parameters.put("cancellationCheckDate", BSCHISUtil.toDate(""));
		parameters.put("cancellationCheckUnit", "");
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
					ServiceCode.CODE_DATABASE_ERROR, "恢复高血压档案失败！", e);
		}
	}

	/**
	 * 注销核实
	 * 
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void checkHyperSubRecordLogout(String phrId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ")
				.append("MDC_HypertensionRecord")
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationCheckUser = :cancellationCheckUser, ")
				.append(" cancellationCheckDate = :cancellationCheckDate, ")
				.append(" cancellationCheckUnit = :cancellationCheckUnit ")
				.append(" where phrId = :phrId ")
				.append(" and status = :status1");

		UserRoleToken ur = UserRoleToken.getCurrent();
		String userId = ur.getUserId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status", "" + HyperRecordStatus.WRITE_OFF);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationCheckUser", userId);
		parameters.put("cancellationCheckDate", new Date());
		parameters.put("cancellationCheckUnit",
				UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("phrId", phrId);
		parameters.put("status1", "" + HyperRecordStatus.NOT_AUDIT);

		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "档案核实注销失败！", e);
		}
	}

	/**
	 * 更新主档的体重数据
	 * 
	 * @param empiId
	 * @param weight
	 * @throws ModelDataOperationException
	 */
	public void updateWeihtOfHypertensionRecord(String empiId, double weight)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ")
				.append("MDC_HypertensionRecord")
				.append(" set weight=:weight,lastModifyUser=:lastModifyUser")
				.append(",lastModifyDate=:lastModifyDate where empiId = :empiId")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("weight", weight);
		parameters.put("empiId", empiId);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新主档的体重数据失败！");
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public List getTestList(String empiId) throws ModelDataOperationException {
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "empiId", "s",
				empiId);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "businessType",
				"s", "1");

		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List list = null;
		try {
			list = (List) dao.doQuery(cnd, "planId",
					BSCHISEntryNames.PUB_VisitPlan);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取计划失败。");
		}
		return list;
	}

	/**
	 * 检查影响高血压评估的因素有没有变更。
	 * 
	 * @param rec
	 * @param session
	 * @return 如果高血压因素有修改返回true，否则返回false。
	 * @throws JSONException
	 * @throws PersistentDataOperationException
	 */
	public boolean checkHyperFactor(Map<String, Object> rec)
			throws ModelDataOperationException {
		String empiId = (String) rec.get("ecmpiId");
		PublicModel pm = new PublicModel(this.dao);
		String hyperManaDoc = pm.getManaDoctor(empiId, MDC_HypertensionRecord);

		String userId = UserRoleToken.getCurrent().getUserId();
		if (!userId.equals("system")) {
			if (false == hyperManaDoc.equals(userId)) {
				return false;
			}
		}
		String hql = new StringBuffer("select targetHurt as targetHurt")
				.append(",riskiness as riskiness,fixDate as fixDate")
				.append(",complication as complication from ")
				.append(MDC_HypertensionFixGroup)
				.append(" where empiId=:empiId order by fixId desc").toString();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("empiId", empiId);
		List<Map<String, Object>> list;
		try {
			list = dao.doQuery(hql, paramMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
		if (list.size() == 0) {
			return true;
		}
		Map<String, Object> map = list.get(0);
		// 高血压随访时用
		rec.put("fixDate", map.get("fixDate"));
		map.remove("fixDate");

		for (String key : map.keySet()) {
			String value = (String) map.get(key);
			String value0 = (String) rec.get(key);
			value0 = value0 == null || value0.trim().length() == 0 ? "0"
					: value0;
			if (value.length() != value0.length()) {
				return true;
			}
			String[] values = value.split(",");
			String[] value0s = value0.split(",");
			for (int i = 0; i < values.length; i++) {
				int j = 0;
				for (; j < value0s.length; j++) {
					if (value0s[j].equals(values[i])) {
						break;
					}
				}
				if (j == value0s.length) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 更新高血压档案
	 * 
	 * @param parameters
	 * @throws ModelDataOperationException
	 */
	public int updateHypertensionRecord(Map<String, Object> parameters)
			throws ModelDataOperationException {
		int rs = 0;
		try {
			String sql = new StringBuffer("update ")
					.append("MDC_HypertensionRecord")
					.append(" set familyhistroy =:familyhistroy where empiId=:empiId")
					.toString();
			rs = dao.doUpdate(sql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新高血压档案失败！");
		}
		return rs;
	}

	/**
	 * 根据empiId查高血压档案中身高
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Double getHeight(String empiId) throws ModelDataOperationException {
		String hql = new StringBuffer("select height as height from ").append(
				"MDC_HypertensionRecord where empiId = :empiId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doSqlQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询身高失败！");
		}
		if (list == null || list.size() == 0) {
			throw new ModelDataOperationException(
					"Get height of person with empiid =[" + empiId
							+ "] failed.");
		}
		return Double.parseDouble(list.get(0).get("HEIGHT") + "");
	}

	/**
	 * 获取高血压随访药物总数
	 * 
	 * @param phrId
	 * @param visitId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public long countVisitMedicine(String phrId, String visitId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(*) as count from ")
				.append("MDC_HypertensionMedicine")
				.append(" where (visitId='0000000000000000' or visitId=:visitId) and phrId=:phrId")
				.toString();
		HashMap<String, Object> para = new HashMap<String, Object>();
		para.put("visitId", visitId);
		para.put("phrId", phrId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, para);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取高血压随访药物总数失败！");
		}
		Long count = (Long) rsMap.get("count");

		return count;
	}

	public List<Map<String, Object>> getVisitMedicine(String phrId,
			String visitId) throws ModelDataOperationException {
		List<Object> cnd1 = CNDHelper
				.createSimpleCnd("eq", "phrId", "s", phrId);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "visitId", "s",
				visitId);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> list = null;
		try {
			list = (List<Map<String, Object>>) dao.doQuery(cnd, "recordId",
					BSCHISEntryNames.MDC_HypertensionMedicine);
			SchemaUtil.setDictionaryMessageForList(list,
					BSCHISEntryNames.MDC_HypertensionMedicine);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取高血压随访药品失败。");
		}
		return list;
	}

	protected void initLifeStyleDataForHypertensionRecord(
			Map<String, Object> resMap, Map<String, Object> lifeMap)
			throws ServiceException {
		try {
			if (null == lifeMap) {
				return;
			}
			// 吸烟频率
			resMap.put("smoke", lifeMap.get("smokeFreqCode"));
			// 日吸烟量(支)
			resMap.put("smokeCount", lifeMap.get("smokeCount"));
			// 饮酒频率
			resMap.put("drink", lifeMap.get("drinkFreqCode"));
			// 饮酒种类
			resMap.put("drinkTypeCode", lifeMap.get("drinkTypeCode"));
			// 日饮酒量(两)
			resMap.put("drinkCount", lifeMap.get("drinkCount"));
			// 锻炼频率
			resMap.put("train", lifeMap.get("trainFreqCode"));
			// 饮食习惯
			resMap.put("eateHabit", lifeMap.get("eateHabit"));

		} catch (Exception e) {
			throw new ServiceException(
					"failed to initLifeStyleDataForHypertensionRecord method.",
					e);
		}

	}

	/**
	 * 保存高血压档案
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveHypertensionRecord(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = new HashMap<String, Object>();
		try {
			rsMap = dao.doSave(op, MDC_HypertensionRecord, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存高血压档案数据验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存高血压档案失败！");
		}
		return rsMap;
	}

	/**
	 * 保存高血压档案
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveHypertensionRecordEnd(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = new HashMap<String, Object>();
		try {
			rsMap = dao.doSave(op, MDC_HypertensionRecordEnd, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存高血压档案数据验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存高血压档案失败！");
		}
		return rsMap;
	}

	/**
	 * 生成初次评估信息
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveHypertensionFixGroupInfo(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = new HashMap<String, Object>();
		try {
			rsMap = dao.doSave(op, MDC_HypertensionFixGroup, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "生成初次评估信息数据验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "生成初次评估信息失败！");
		}
		return rsMap;
	}

	/**
	 * 保存年度健康检查
	 * 
	 * @param op
	 * @param entryName
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveAnnualHealthCheckInfo(String op,
			String entryName, Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = new HashMap<String, Object>();
		try {
			rsMap = dao.doSave(op, entryName, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存年度健康检查数据验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存年度健康检查失败！");
		}
		return rsMap;
	}

	public List<Map<String, Object>> getGroupRecordByEmpiId(String empiId)
			throws ModelDataOperationException {
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
					empiId);
			List<?> cnd2 = CNDHelper
					.toListCnd("['notNull', ['$', 'a.hypertensionGroup']]");
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			return dao.doList(cnd, null, MDC_HypertensionFixGroup);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询分组评估数据失败！");
		} catch (ExpException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询分组评估数据失败！");
		}
	}

	public void setLastVisitEffect(String phrId, String visitEffect,
			String noVisitReason) throws ModelDataOperationException {
		String sql = "update MDC_HypertensionVisit set visitEffect=:visitEffect,"
				+ "noVisitReason=:noVisitReason where phrId=:phrId "
				+ "and visitDate=(select max(visitDate) from MDC_HypertensionVisit"
				+ " where phrId=:phrId)";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phrId", phrId);
		parameters.put("visitEffect", visitEffect);
		parameters.put("noVisitReason", noVisitReason);
		try {
			dao.doUpdate(sql, parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新转归失败！");
		}
		if (VisitEffect.LOST.equals(visitEffect)) {
			sql = "update PUB_VisitPlan set planStatus=:planStatus "
					+ " where recordId=:recordId and businessType=:businessType "
					+ "and visitId=(select max(visitId) from MDC_DiabetesVisit"
					+ " where phrId=:phrId) and visitId is not null";
			parameters = new HashMap<String, Object>();
			parameters.put("recordId", phrId);
			parameters.put("phrId", phrId);
			parameters.put("planStatus", PlanStatus.LOST);
			parameters.put("businessType", BusinessType.TNB);
			try {
				dao.doUpdate(sql, parameters);
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "更新转归失败！");
			}
		}

	}

	public void saveFixGroupWorkList(Map<String, Object> m)
			throws ModelDataOperationException {
		Map<String, Object> record = new HashMap<String, Object>();
		if (m == null) {
			throw new ModelDataOperationException("没有对应的参数列表");
		}
		if (m.get("workType") == null) {
			throw new ModelDataOperationException("没有对应的工作计划");
		}
		if (m.get("recordId") == null) {
			throw new ModelDataOperationException("档案号获取失败");
		}
		if (m.get("empiId") == null) {
			throw new ModelDataOperationException("empiId获取失败");
		}
		record.put("recordId",
				StringUtils.trimToEmpty((String) m.get("recordId")));
		record.put("empiId", StringUtils.trimToEmpty((String) m.get("empiId")));
		record.put("workType", m.get("workType"));
		record.put("count", m.get("count") == null ? "1" : m.get("count"));
		if (m.get("manaUnitId") != null) {
			record.put("manaUnitId", m.get("manaUnitId"));
		}
		if (m.get("doctorId") != null) {
			record.put("doctorId", m.get("doctorId"));
		}
		if (m.get("beginDate") != null) {
			record.put("beginDate", m.get("beginDate"));
		}
		if (m.get("endDate") != null) {
			record.put("endDate", m.get("endDate"));
		}
		if (m.get("otherId") != null) {
			record.put("otherId", m.get("otherId"));
		}
		try {
			dao.doInsert(PUB_WorkList, record, false);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存高血压分组评估任务失败！");
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存高血压分组评估任务失败！");
		}
	}

}
