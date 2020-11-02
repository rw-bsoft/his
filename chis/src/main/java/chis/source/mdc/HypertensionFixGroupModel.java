/**
 * @(#)HypertensionFixGroupModel.java Created on 2012-1-5 上午11:29:28
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.UserUtil;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.schema.Schema;
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;

/**
 * @description 高血压分组评估业务模型类
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HypertensionFixGroupModel extends MDCBaseModel {
	/**
	 * @param dao
	 */
	public HypertensionFixGroupModel(BaseDAO dao) {
		super(dao);
	}

	/**
	 * 依据empiId检索 分组评级信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getFixGroupByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s",
				empiId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cnd, "fixId", MDC_HypertensionFixGroup);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取分组评级信息失败！");
		}
		return rsList;
	}

	/**
	 * 获取分组评级信息
	 * 
	 * @param cnd
	 * @param orderBy
	 * @param entryName
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getfixGroupByCnd(List<?> cnd,
			String orderBy, String entryName)
			throws ModelDataOperationException {
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cnd, orderBy, entryName);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取分组评级信息失败！");
		}
		return rsList;
	}

	/**
	 * 获取高血压定转组信息
	 * 
	 * @param empiId
	 * @param schema
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getLastFixGroup(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(" from ")
				.append(MDC_HypertensionFixGroup)
				.append(" where empiId=:empiId order by fixId desc").toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取高血压定转组信息失败！");
		}

		return rsList.size() > 0 ? rsList.get(0) : null;
	}

	/**
	 * 获取定转组信息， 返回值包含一个高血压分级（grade），一个危险分层（riskLevel）以及管理级别（group）。
	 * 
	 * @param hRecord
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getHypertensionGroup(Map<String, Object> hRecord)
			throws ModelDataOperationException {
		int constriction = 0;// 收缩压
		if (hRecord.get("constriction") instanceof String) {
			constriction = 0;
		} else {
			constriction = (Integer) hRecord.get("constriction");
		}
		int diastolic = 0;// 舒张压
		if (hRecord.get("diastolic") instanceof String) {
			diastolic = 0;
		} else {
			diastolic = (Integer) hRecord.get("diastolic");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		String empiId = (String) hRecord.get("empiId");
		String riskiness = (String) hRecord.get("riskiness"); // 危险因素
		String targetHurt = (String) hRecord.get("targetHurt"); // 靶器官损害
		String complication = (String) hRecord.get("complication"); // 并发症
		List<Map<String, Object>> dbsList = null;
		List<?> cnd = null;
		try {
			cnd = CNDHelper.toListCnd("['and',['eq',['$','a.empiId'],['s','" + empiId
					+ "']],['eq',['$','a.status'],['s','0']]]");
			dbsList = dao.doList(cnd, "", MDC_DiabetesRecord);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取是否需要定转组参数失败。");
		}
		int riskLength = 0;
		String riskinessInEstimate = "1";
		if (riskiness != null && !riskiness.equals("")
				&& !riskiness.equals("12")) {
			String[] riskinessList = riskiness.split(",");
			//过滤riskiness是12(无)的危险因素
			riskLength=riskinessList.length;
		}
		
		if (riskLength == 1 || riskLength == 2) {
			riskinessInEstimate = "2";
		}
		if (riskLength >= 3) {
			riskinessInEstimate = "3";
		}
		if (dbsList != null && dbsList.size() > 0) {
			riskinessInEstimate = "3";
		}
		if (targetHurt != null && targetHurt.length() > 0
				&& !"10".equals(targetHurt)) {
			riskinessInEstimate = "3";
		}
		if (complication != null && complication.length() > 0
				&& !"16".equals(complication)) {
			riskinessInEstimate = "4";
		}
		// 查询根据评估影响因素和血压级别得出危险分层的数据
		List<Map<String, Object>> Estimate = null;
		try {
			cnd = CNDHelper.toListCnd("['eq',['$','riskiness'],['s','"
					+ riskinessInEstimate + "']]");
			Estimate = dao.doList(cnd, "", MDC_EstimateDictionary);
		} catch (ExpException e1) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取是否需要定转组参数失败。");
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取是否需要定转组参数失败。");
		}
		int riskLevelGrade = MDCBaseModel.decideHypertensionGrade(constriction,
				diastolic);
		if (riskLevelGrade > 3 || riskLevelGrade == 0) {
			riskLevelGrade = 1;
		}
		String riskLevel = "1";// 危险分层
		if (Estimate == null || Estimate.size() == 0) {
			map.put("errorCode", "588");
			map.put("errorMsg", "请先维护高血压模块参数中的危险分层列表！");
			return map;
		} else {
			riskLevel = Estimate.get(0).get("HL" + riskLevelGrade) + "";
		}
		if (riskLevel == null || "".equals(riskLevel)) {
			map.put("errorCode", "588");
			map.put("errorMsg", "请先维护高血压模块参数中的危险分层列表！");
			return map;
		}
		cnd = CNDHelper.createSimpleCnd("eq", "controlCondition", "s", "1");
		// 查询根据控制情况和危险分层得出组别的数据
		List<Map<String, Object>> Control = null;
		try {
			Control = dao.doList(cnd, "", MDC_HypertensionControl);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取是否需要定转组参数失败。");
		}
		String newGroup = "";// 组别
		if (Control == null || Control.size() == 0) {
			map.put("errorCode", "588");
			map.put("errorMsg", "请先维护高血压模块参数中的分组列表！");
			return map;
		} else {
			Map<String, Object> m = Control.get(0);
			if (riskLevel.equals("1")) {
				newGroup = (String) m.get("lowRisk");
			} else if (riskLevel.equals("2")) {
				newGroup = (String) m.get("middleRisk");
			} else if (riskLevel.equals("3")) {
				newGroup = (String) m.get("highRisk");
			} else if (riskLevel.equals("4")) {
				newGroup = (String) m.get("veryHighRisk");
			}
		}
		if (newGroup == null || "".equals(newGroup)) {
			map.put("errorCode", "588");
			map.put("errorMsg", "请先维护高血压模块参数中的分组列表！");
			return map;
		}
		map.put("grade", riskLevelGrade);
		map.put("riskLevel", riskLevel);
		map.put("group", newGroup);
		return map;
	}

	/**
	 * 保存一条转组记录， 并更新高血压档案里的分组信息
	 * 
	 * @param rec
	 * @param res
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveSetGroupRecord(Map<String, Object> rec,
			Schema sc) throws ModelDataOperationException {
		// 获取定转组信息 
		Map<String, Object> groupMap = getHypertensionGroup(rec);
		if ("588".equals(groupMap.get("errorCode"))) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, (String)groupMap.get("errorMsg"));
		}
		String level = String.valueOf(groupMap.get("grade"));
		String riskLevel = (String) groupMap.get("riskLevel");
		String group = (String) groupMap.get("group");
		rec.put("hypertensionLevel", level);
		rec.put("riskLevel", riskLevel);
		rec.put("hypertensionGroup", group);
		// User user = (User) dao.getContext().get("user.instance");
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId();
		String unit = user.getManageUnitId();
		rec.put("fixUser", uid);
		rec.put("fixUnit", unit);
		rec.put("manaUnitId", unit);
		rec.put("lastModifyUser", uid);
		rec.put("lastModifyUnit", unit);
		rec.put("lastModifyDate", new Date());

		String op = (String) rec.get("op");
		//不知道是哪个写的，真想骂他
//		if (op.equals("create")) {
//			rec.put("fixDate", BSCHISUtil.toString(new Date(), "yyyy-MM-dd"));
//		}
		Map<String, Object> genValues = new HashMap<String, Object>();
		try {
			genValues = dao.doSave(op, MDC_HypertensionFixGroup, rec, true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存转组记录时数据验证失败！");
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存转组记录信息失败！");
		}

		// 更新档案里的分组及分层等信息
		StringBuffer sb = new StringBuffer("update ")
				.append(MDC_HypertensionRecord)
				.append(" set hypertensionGroup=:hypertensionGroup")
				.append(",riskLevel=:riskLevel,lastModifyUser=:lastModifyUser")
				.append(",lastModifyDate=:lastModifyDate")
				.append(" where phrId=:phrId");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("hypertensionGroup", group);
		parameters.put("riskLevel", riskLevel);
		parameters.put("phrId", rec.get("phrId"));
		parameters.put("lastModifyUser", uid);
		parameters.put("lastModifyDate", new Date());

		try {
			dao.doUpdate(sb.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存转组记录后更新档案里的分组及分层等信息失败！");
		}

		// 返回分组，分层等信息，供档案保存后显示相应信息。
		genValues.put("fixDate", rec.get("fixDate"));
		genValues.put("hypertensionGroup", group);
		genValues.put("hypertensionGroup_text", sc.getItem("hypertensionGroup")
				.getRefDic().getText(group));
		genValues.put("riskLevel", riskLevel);
		genValues.put("riskLevel_text", sc.getItem("riskLevel").getRefDic()
				.getText(riskLevel));
		String grade = groupMap.get("grade").toString();
		genValues.put("hypertensionLevel", grade);
		genValues.put("hypertensionLevel_text", sc.getItem("hypertensionLevel")
				.getRefDic().getText(grade));

		return genValues;
	}

	/**
	 * 获取初次定组记录
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getFirstFixGroup(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(" from ")
				.append(MDC_HypertensionFixGroup)
				.append(" where fixType = '1' and empiId = :empiId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取初次定组记录失败!", e);
		}
		if (rsList != null && rsList.size() > 0) {
			return rsList.get(0);
		}
		return null;
	}
}
