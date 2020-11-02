package chis.source.pub;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ctd.dictionary.DictionaryController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.UnitType;
import chis.source.util.CNDHelper;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryItem;
import ctd.service.core.ServiceException;
import ctd.validator.ValidateException;

public class StatModel implements BSCHISEntryNames {
	private static final Logger logger = LoggerFactory
			.getLogger(StatModel.class);

	BaseDAO dao = null;
	Date d = null;// 被统计的日期
	String dateStr = "";

	/**
	 * 
	 * @param dao
	 * @param d
	 *            被统计的日期
	 */
	public StatModel(BaseDAO dao, Date d) {
		this.dao = dao;
		this.d = d;
		this.dateStr = toStringDate(d);
	}

	/**
	 * 查询下去健康分析数据
	 * 
	 * @param manaUnitId
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public List<Map<String, Object>> getUnitData(String manaUnitId)
			throws PersistentDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("like", "manaUnitId", "s",
				getUnitLikeCnd(manaUnitId));
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "computeDate", "s",
				dateStr);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> list = dao.doList(cnd, "", PUB_Stat);
		return list;
	}

	public Map<String, Object> getMaxValue(String manaUnitId)
			throws PersistentDataOperationException {
		// List<?> cnd1 = CNDHelper.createSimpleCnd("like", "manaUnitId", "s",
		// getUnitLikeCnd(manaUnitId));
		// List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "computeDate", "s",
		// dateStr);
		// List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		// List<Map<String, Object>> list = dao.doList(cnd, "", PUB_Stat);
		StringBuffer hql = new StringBuffer(
				"select max(healthRecord) as maxValue from ").append("PUB_Stat")
				.append(" where manaUnitId like '")
				.append(getUnitLikeCnd(manaUnitId)).append("'");
		Map<String, Object> map = dao.doLoad(hql.toString(),
				new HashMap<String, Object>());
		return map;
	}

	/**
	 * 查询管理对象分析数据
	 * 
	 * @param manaUnitId
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> getSummaryInfoByUnitId(String manaUnitId)
			throws PersistentDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "manaUnitId", "s",
				manaUnitId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "computeDate", "s",
				dateStr);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> list = dao.doList(cnd, "", PUB_Stat);
		if (list.size() == 0) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("healthRecord", 0);
			data.put("familyRecord", 0);
			data.put("oldPeopleRecord", 0);
			data.put("childRecord", 0);
			data.put("hypertensionRecord", 0);
			data.put("pregnantRecord", 0);
			data.put("diabetesRecord", 0);
			data.put("psychosisRecord", 0);
			list.add(data);
		}
		return list.get(0);
	}

	/**
	 * 查询当月新增健康档案数。
	 * 
	 * @param manaUnitId
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public int getNewHealthRecordCount(String manaUnitId)
			throws PersistentDataOperationException {
		int lastMonth = 0;
		int thisMonth = 0;
		String lastMonthDateStr = toLastMonthStringDate(d);
		String hql = new StringBuffer(
				"select healthRecord as healthRecord from  ")
				.append(PUB_Stat)
				.append(" where computeDate=:computeDate and manaUnitId=:manaUnitId ")
				.toString();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("manaUnitId", manaUnitId);
		params.put("computeDate", lastMonthDateStr);
		List<Map<String, Object>> res = dao.doQuery(hql, params);
		if (res.size() > 0 && res.get(0) != null) {
			if (res.get(0).get("healthRecord") == null) {
				lastMonth = 0;
			} else {
				lastMonth = (Integer) res.get(0).get("healthRecord");
			}

		}

		params.put("computeDate", dateStr);
		res = dao.doQuery(hql, params);
		if (res.size() > 0 && res.get(0) != null) {
			if (res.get(0).get("healthRecord") == null) {
				thisMonth = 0;
			} else {
				thisMonth = (Integer) res.get(0).get("healthRecord");
			}
		}
		return thisMonth - lastMonth;
	}

	/**
	 * 执行统计
	 * 
	 * @param d
	 * @throws ServiceException
	 */
	public void caculate() throws ModelDataOperationException {
		try {
			// 删除d当月所有数据。
			deleteDataByDate(dateStr);
			// 统计所有团队的数据。
			List<String> teamList = getUnitsByType("P121");
			for (int i = 0; i < teamList.size(); i++) {
				caculateTeamData(teamList.get(i));
			}
			// 统计所有中心数据
			List<String> centerList = getUnitsByType("B");
			for (int i = 0; i < centerList.size(); i++) {
				caculateUnitData(centerList.get(i));
				caculateCenterData(centerList.get(i));// 计算业务在中心的数据
			}
			// 统计所有区数据
			List<String> areaList = getUnitsByType("S");
			for (int i = 0; i < areaList.size(); i++) {
				caculateUnitData(areaList.get(i));
			}
			// 统计市数据
			List<String> cityList = getUnitsByType("R");
			for (int i = 0; i < cityList.size(); i++) {
				caculateUnitData(cityList.get(i));
			}
		} catch (Exception e) {
			throw new ModelDataOperationException(e);
		}
	}

	/**
	 * 计算高层机构档案数
	 * 
	 * @param unitId
	 * @throws PersistentDataOperationException
	 * @throws ValidateException
	 */
	private void caculateUnitData(String manaUnitId)
			throws PersistentDataOperationException, ValidateException {
		Map<String, Object> data = sumDoc(manaUnitId);
		data.put("manaUnitId", manaUnitId);
		data.put("computeDate", this.dateStr);
		dao.doInsert(PUB_Stat, data, false);
	}

	/**
	 * 计算团队档案数
	 * 
	 * @param teamUnitId
	 * @throws PersistentDataOperationException
	 * @throws ValidateException
	 */
	private void caculateTeamData(String teamUnitId)
			throws PersistentDataOperationException, ValidateException {
		Map<String, Object> teamData = new HashMap<String, Object>();
		teamData.put("healthRecord", countDoc(EHR_HealthRecord, teamUnitId));
		teamData.put("familyRecord", countDoc(EHR_FamilyRecord, teamUnitId));
		teamData.put("oldPeopleRecord",
				countDoc(MDC_OldPeopleRecord, teamUnitId));
		teamData.put("childRecord", 0);
		teamData.put("hypertensionRecord",
				countDoc(MDC_HypertensionRecord, teamUnitId));
		teamData.put("pregnantRecord", 0);
		teamData.put("diabetesRecord", countDoc(MDC_DiabetesRecord, teamUnitId));
		teamData.put("psychosisRecord",
				countDoc(PSY_PsychosisRecord, teamUnitId));
		teamData.put("computeDate", dateStr);
		teamData.put("manaUnitId", teamUnitId);
		dao.doInsert(PUB_Stat, teamData, false);
	}

	/**
	 * 计算中心档案数
	 * 
	 * @param teamUnitId
	 * @throws PersistentDataOperationException
	 * @throws ValidateException
	 */
	private void caculateCenterData(String teamUnitId)
			throws PersistentDataOperationException, ValidateException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "computeDate", "s",
				dateStr);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "manaUnitId", "s",
				teamUnitId);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> recs = dao.doList(cnd, "", PUB_Stat);
		Map<String, Object> rec = new HashMap<String, Object>();
		String op = "create";
		if (recs.size() > 0) {
			rec = recs.get(0);
			op = "update";
		}
		rec.put("childRecord", countDoc(CDH_HealthCard, teamUnitId));
		rec.put("pregnantRecord", countDoc(MHC_PregnantRecord, teamUnitId));
		dao.doSave(op, PUB_Stat, rec, false);
	}

	/**
	 * 计算高层机构各种档案数
	 * 
	 * @param manaUnitId
	 * @return
	 * @throws PersistentDataOperationException
	 */
	private Map<String, Object> sumDoc(String manaUnitId)
			throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer(
				"select sum(healthRecord) as healthRecord ,")
				.append("sum(familyRecord) as familyRecord,")
				.append("sum(oldPeopleRecord) as oldPeopleRecord,")
				.append("sum(hypertensionRecord) as hypertensionRecord,");
		if (manaUnitId.length() != UnitType.LEN_ZHONGXIN) {// 以下两种业务在中心，已统计完成
			hql.append("sum(childRecord) as childRecord,").append(
					"sum(pregnantRecord) as pregnantRecord,");
		}
		hql.append("sum(diabetesRecord)as diabetesRecord,")
				.append("sum(psychosisRecord)as psychosisRecord ")
				.append(" from ")
				.append(PUB_Stat)
				.append(" where computeDate=:computeDate and manaUnitId like :manaUnitId ")
				.toString();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("computeDate", dateStr);
		params.put("manaUnitId", getUnitLikeCnd(manaUnitId));
		List<Map<String, Object>> res = dao.doQuery(hql.toString(), params);
		for (int i = 0; i < res.size(); i++) {
			Map<String, Object> rec = res.get(i);
			for (String key : rec.keySet()) {
				if (rec.get(key) == null) {
					rec.put(key, 0);
				}
			}
		}

		return res.get(0);
	}

	/**
	 * 根据当前机构编号获取下层机构查询like 条件
	 * 
	 * @param manaUnitId
	 * @return
	 */
	private static String getUnitLikeCnd(String manaUnitId) {
		StringBuffer likeCnd = new StringBuffer(manaUnitId);
		int nextLength = UnitType.getNextLevelLength(manaUnitId);
		for (int i = 0; i < nextLength - manaUnitId.length(); i++) {
			likeCnd.append("_");
		}
		return likeCnd.toString();
	}

	/**
	 * 根据档案类型计算档案总数
	 * 
	 * @param entryName
	 * @return
	 * @throws PersistentDataOperationException
	 */
	private Long countDoc(String entryName, String manaUnitId)
			throws PersistentDataOperationException {
		String hql = new StringBuffer("select count(*) as count from ")
				.append(entryName)
				.append(" where status='0' and manaUnitId =:manaUnitId")
				.toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("manaUnitId", manaUnitId);
		List<Map<String, Object>> count = dao.doQuery(hql, param);
		return (Long) count.get(0).get("count");
	}

	/**
	 * 删除数据
	 * 
	 * @param date
	 * @throws PersistentDataOperationException
	 */
	public void deleteDataByDate(String date)
			throws PersistentDataOperationException {
		String hql = new StringBuffer("delete from ").append(PUB_Stat)
				.append(" where computeDate=:computeDate").toString();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("computeDate", date);
		dao.doUpdate(hql, params);
	}

	/**
	 * 根据机构类型获取所有的管辖机构
	 * 
	 * @param type
	 * @return
	 */
	public static List<String> getUnitsByType(String type) {
		List<String> res = new ArrayList<String>();
		Dictionary manaUnitDic = DictionaryController.instance().getDic("chis.@manageUnit");
		List<DictionaryItem> items = manaUnitDic.itemsList();
		for (int i = 0; i < items.size(); i++) {
			DictionaryItem it = items.get(i);
			if (type.equals(it.getProperty("type"))) {
				res.add(it.getKey());
			}
		}
		return res;
	}

	/**
	 * 
	 * @param d
	 * @return
	 */
	private static String toStringDate(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		StringBuffer sb = new StringBuffer();
		sb.append(c.get(Calendar.YEAR));
		sb.append("-");
		sb.append(c.get(Calendar.MONTH) + 1);
		return sb.toString();
	}

	/**
	 * 
	 * @param d
	 * @return
	 */
	private static Date toLastMonth(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.MONTH, -1);
		return c.getTime();
	}

	/**
	 * 
	 * @param d
	 * @return
	 */
	private String toLastMonthStringDate(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.MONTH, -1);
		return toStringDate(c.getTime());
	}

}
