/**
 * @(#)TumourQuestionnaire.java Created on 2014-6-18 上午10:18:13
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourQuestionnaireModel implements BSCHISEntryNames {
	public static final String MPM_Dic = "dic";
	public static final String MPM_Text = "text";
	public static final String MPM_Keys = "keys";
	public static final String MPM_IsAnswer = "isAnswer";
	public static final String MPM_Option = "option";
	public static final String MPM_Score = "score";

	private BaseDAO dao;

	public TumourQuestionnaireModel(BaseDAO dao) {
		this.dao = dao;
	}

	public static String hrt2mt(String highRiskType) {
		Map<String, String> hrt2mt = new HashMap<String, String>();
		hrt2mt.put("1", "03");
		hrt2mt.put("2", "04");
		hrt2mt.put("3", "05");
		hrt2mt.put("4", "06");
		hrt2mt.put("5", "07");
		hrt2mt.put("6", "08");
		return hrt2mt.get(highRiskType);
	}

	public static String mt2hrt(String masterplateType) {
		Map<String, String> mt2hrt = new HashMap<String, String>();
		mt2hrt.put("03", "1");
		mt2hrt.put("04", "2");
		mt2hrt.put("05", "3");
		mt2hrt.put("06", "4");
		mt2hrt.put("07", "5");
		mt2hrt.put("08", "6");
		return mt2hrt.get(masterplateType);
	}

	/**
	 * 重组某一个字典(服务记录)
	 * 
	 * @param filelds
	 * @param fieldAttribute
	 * @return
	 */
	public static List<Map<String, Object>> resetDicItems(
			List<Map<String, Object>> filelds, String dicRender) {
		List<Map<String, Object>> dicItems = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> data : filelds) {
			Map<String, Object> item = new HashMap<String, Object>();
			Object text = data.get(MPM_Text);
			Object keys = data.get(MPM_Keys);
			if (text != null && keys != null) {
				item.put("key", keys);
				item.put("text", text);
				dicItems.add(item);
			}
		}
		return dicItems;
	}

	/**
	 * 
	 * @Description:获取试题
	 * @param masterplateId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-5-4 上午11:21:42
	 * @Modify:
	 */
	public List<Map<String, Object>> getQuestionnaireByMasterplateId(
			String masterplateId) throws ModelDataOperationException {
		List<Map<String, Object>> rsList = null;
		String sql = "select b.fieldId as fieldId,b.alias as alias,b.id as id,"
				+ " b.type as type,b.dicRender as dicRender,b.notNull as notNull,"
				+ " b.defaultValue as defaultValue,b.length as length,b.pyCode as pyCode,b.remark as remark"
				+ " from MPM_FieldMasterRelation a, MPM_FieldMaintain b where a.masterplateId=:masterplateId and a.fieldId=b.fieldId ";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("masterplateId", masterplateId);
		try {
			rsList = dao.doQuery(sql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询获取试题失败", e);
		}
		return rsList;
	}

	/**
	 * 
	 * @Description:获取问卷类型
	 * @param masterplateId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-25 下午2:33:40
	 * @Modify:
	 */
	public String getQuestionnaireType(String masterplateId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select whmb as whmb from ")
				.append(MPM_MasterplateMaintain)
				.append(" where masterplateId=:masterplateId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("masterplateId", masterplateId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取问卷类型失败！", e);
		}
		String type = "";
		if (rsMap != null && rsMap.size() > 0) {
			String whmb = (String) rsMap.get("whmb");
			type = (Integer.parseInt(whmb) - 2) + "";
		}
		return type;
	}

	/**
	 * 获取试题答案选择项
	 * 
	 * @Description:
	 * @param fieldAttribute
	 * @param dao
	 * @return
	 * @throws ServiceException
	 * @author ChenXianRui 2014-6-18 上午10:21:44
	 * @Modify:
	 */
	public List<Map<String, Object>> getListFilelds(
			Map<String, Object> fieldAttribute, BaseDAO dao)
			throws ServiceException {
		String fieldId = (String) fieldAttribute.get("fieldId");
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "fieldId", "s", fieldId);
		List<Map<String, Object>> dicFields = null;
		try {
			dicFields = dao.doList(cnd1, null,
					BSCHISEntryNames.MPM_DictionaryMaintain);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException("获取试题答案选择项失败", e);
		}
		return dicFields;
	}

	/**
	 * 
	 * @Description:获取问卷一般情况信息
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-6-19 上午11:29:26
	 * @Modify:
	 */
	public Map<String, Object> getGeneralCasebyPkey(String pkey)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(PHQ_GeneralCase, pkey);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取问卷一般情况信息失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:保存肿瘤问卷个人基本信息
	 * @param op
	 * @param recordMap
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-1 下午4:25:00
	 * @Modify:
	 */
	public Map<String, Object> saveGeneralCaseRecord(String op,
			Map<String, Object> recordMap, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, PHQ_GeneralCase, recordMap, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤问卷个人基本信息时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤问卷个人基本信息失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:保存问卷答题信息
	 * @param op
	 * @param recordMap
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-1 下午4:35:53
	 * @Modify:
	 */
	public Map<String, Object> saveAnswerRecord(String op,
			Map<String, Object> recordMap, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, PHQ_AnswerRecord, recordMap, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存问卷答题信息时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存问卷答题信息失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:删除问卷答题记录
	 * @param gcId
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-4 上午9:42:20
	 * @Modify:
	 */
	public void deleteAnswerRecord(String gcId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ").append(PHQ_AnswerRecord)
				.append(" where gcId= :gcId ").toString();
		Map<String, Object> dMap = new HashMap<String, Object>();
		dMap.put("gcId", gcId);
		try {
			dao.doUpdate(hql, dMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除问卷答题记录失败！", e);
		}
	}

	/**
	 * 
	 * @Description:依据问卷基本信息ID获取问卷答案信息
	 * @param gcId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-4 下午3:31:15
	 * @Modify:
	 */
	public List<Map<String, Object>> getAnswerRecordByGCID(String gcId)
			throws ModelDataOperationException {
		String sql = new StringBuffer(
				"select a.fieldId as fieldId,a.fieldname as fieldName,a.fieldvalue as fieldValue,b.text as text ")
				.append(" from PHQ_AnswerRecord a left join mpm_dictionarymaintain b ")
				.append(" on a.fieldid=b.fieldid and a.fieldvalue = b.keys ")
				.append(" where a.gcId=:gcId").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("gcId", gcId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doSqlQuery(sql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "依据问卷基本信息ID获取问卷答案信息失败！", e);
		}
		return rsList;
	}

	/**
	 * 
	 * @Description:删除问卷基本信息
	 * @param pkey
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-5 下午2:01:01
	 * @Modify:
	 */
	public void deleteGeneralCaseByPkey(String pkey)
			throws ModelDataOperationException {
		try {
			dao.doRemove(pkey, PHQ_GeneralCase);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除问卷基本信息失败！", e);
		}
	}

	/**
	 * 
	 * @Description:判断某人某次问卷是否符合标准(全部对上)
	 * @param empiId
	 *            个人主索引
	 * @param QMId
	 *            问卷模版编号
	 * @param gcId
	 *            问卷编号
	 * @param source
	 *            问卷来源
	 * @param hrcId
	 *            高危标准ID
	 * @return boolean true 符合标准 false 不符合标准
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-19 下午2:44:51
	 * @Modify:
	 */
	public boolean isAccordWithCriterion(String empiId, String QMId,
			String gcId, String source,String hrcId) throws ModelDataOperationException {
		boolean passport = false;
		TumourCriterionModel tcm = new TumourCriterionModel(dao);
		List<Map<String, Object>> rsList = tcm.getLastTQCriterionList(QMId,source,
				hrcId);
		if (rsList != null && rsList.size() > 0) {
			Map<String, Object> qcMap = rsList.get(0);
			String criterionExplain = (String) qcMap.get("criterionExplain");
			if ("1".equals(criterionExplain)) {// 所有，则直接过
				passport = true;
			} else {// 可疑 ，则判断标准明细 有一项符合就行
				String qcId = (String) qcMap.get("recordId");
				// long qcdNum = tcm.getTQCriterionDetailNum(qcId);
				long awcNum = this
						.getAccordWithCriterionNum(empiId, gcId, qcId);
				if (awcNum > 0) {
					passport = true;
				}
			}
		}
		return passport;
	}

	/**
	 * 
	 * @Description:统计问卷答案与标准符合项数
	 * @param gcId
	 *            问卷Id
	 * @param qcId
	 *            问卷标准Id
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-19 下午4:15:44
	 * @Modify:
	 */
	public long getAccordWithCriterionNum(String empiId, String gcId,
			String qcId) throws ModelDataOperationException {
		String sql = new StringBuffer("select count(*) as awcNum from ")
				.append(" PHQ_AnswerRecord a ")
				.append(" join ")
				.append(" (select * from MDC_QuestCriterionDetail t where t.qcId = :qcId) b ")
				.append(" on a.fieldId=b.QFId and a.fieldValue=b.keys where a.empiId=:empiId and a.gcId = :gcId")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("gcId", gcId);
		parameters.put("qcId", qcId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doSqlQuery(sql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "统计问卷答案与标准符合项数失败！", e);
		}
		long awcNum = 0;
		if (rsList != null && rsList.size() > 0) {
			awcNum = ((BigDecimal) rsList.get(0).get("AWCNUM")).longValue();
		}
		return awcNum;
	}

	/**
	 * 
	 * @Description:获取肿瘤检查项目
	 * @param itemType
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-21 上午9:04:53
	 * @Modify:
	 */
	public long getTumourInspectionItems(String itemType)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(*) as tiiNum from ")
				.append(MDC_TumourInspectionItem)
				.append(" where startUsing='y' and itemType=:itemType")
				.toString();
		String inHql = new StringBuffer("select count(*) as tiiNum from ")
				.append(MDC_TumourInspectionItem)
				.append(" where startUsing='y' and itemType in(:itemType)")
				.toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("itemType", itemType);
		Map<String, Object> rsMap = null;
		try {
			if (itemType.length() > 1) {
				rsMap = dao.doLoad(inHql, pMap);
			} else {
				rsMap = dao.doLoad(hql, pMap);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取肿瘤检查项目失败！", e);
		}
		long tiiNum = 0;
		if (rsMap != null && rsMap.size() > 0) {
			tiiNum = (Long) rsMap.get("tiiNum");
		}
		return tiiNum;
	}

	/**
	 * 
	 * @Description:保存肿瘤初筛检查项目
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-22 上午9:34:14
	 * @Modify:
	 */
	public Map<String, Object> saveCheckItem(String op,
			Map<String, Object> record, Boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_TumourScreeningCheckResult, record,
					validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤初筛检查项目时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤初筛检查项目失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:取该人某类型的问卷
	 * @param empiId
	 * @param masterplateType
	 *            问卷模版类型
	 * @return List<Map<String, Object>> 按创建时间倒序排列结果集
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-29 上午9:25:51
	 * @Modify:
	 */
	public List<Map<String, Object>> getTumourQuestionnaire(String empiId,
			String masterplateType) throws ModelDataOperationException {
		String hqlString = new StringBuffer(" from ")
				.append(PHQ_GeneralCase)
				.append(" where empiId=:empiId and masterplateType=:masterplateType")
				.append(" order by createDate desc").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		pMap.put("masterplateType", masterplateType);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hqlString, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "取该人某类型的问卷列表失败！", e);
		}
		return rsList;
	}

	/**
	 * 
	 * @Description:获取高危因素
	 * @param empiId
	 * @param highRiskType
	 * @param highRiskSource
	 * @param hrcId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-4 上午9:29:09
	 * @Modify:
	 */
	public String getHighRiskFactor(String empiId, String highRiskType,
			String highRiskSource) throws ModelDataOperationException {
		TumourCriterionModel tcModel = new TumourCriterionModel(dao);
		StringBuffer hrfBuffer = new StringBuffer();
		// 是否 有 家族肿瘤史
		boolean existFTH = tcModel.isExitCancer(empiId, new String[] { "0706",
				"0806", "0906", "1006" });
		if (existFTH) {
			hrfBuffer.append("有--家族肿瘤史");
		}
		// 是否 有 肿瘤疾病史
		boolean existTDH = tcModel.isExitCancer(empiId,
				new String[] { "0206" }, new String[] { "02" });
		if (existTDH) {
			if (hrfBuffer.length() > 0) {
				hrfBuffer.append(";");
			}
			hrfBuffer.append("有--肿瘤疾病史");
		}
		// 是否 有 问卷症状
		String hrcId = "";
		List<Map<String, Object>> thrcList = tcModel.getTHRCriterion(
				highRiskType, highRiskSource);
		if (thrcList != null && thrcList.size() > 0) {
			for (int i = 0, len = thrcList.size(); i < len; i++) {
				Map<String, Object> thrcMap = thrcList.get(i);
				String highRiskFactor = (String) thrcMap.get("highRiskFactor");
				if ("1".equals(highRiskFactor)) {
					hrcId = (String) thrcMap.get("hrcId");
					break;
				}
			}
		}
		if (StringUtils.isNotEmpty(hrcId)) {
			boolean existTQS = tcModel.isExistTumourQuestionnaireSymptom(
					empiId, highRiskType, hrcId);
			if (existTQS) {
				if (hrfBuffer.length() > 0) {
					hrfBuffer.append(";");
				}
				hrfBuffer.append("有--问卷症状");
			}
		}
		return hrfBuffer.toString();
	}

	public void queryMasterplateKey(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		String masterplateId = req.get("masterplateId") + "";
		String JZXH = req.get("JZXH") + "";
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "masterplateId",
				"s", masterplateId);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "JZXH", "s", JZXH);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			List<Map<String, Object>> list = dao.doQuery(cnd, "a.gcId",
					PHQ_GeneralCase);
			if (list != null && list.size() > 0) {
				res.put("body", list.get(0));
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "根据就诊序号和模版序号获取问卷数据失败！", e);
		}
	}

	/**
	 * 
	 * @Description:注销肿瘤健康问卷
	 * @param gcId
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-10-17 上午9:52:03
	 * @Modify:
	 */
	public void logoutTumourQuestionnaire(String gcId, String cancellationReason)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append(PHQ_GeneralCase)
				.append(" set status = :status")
				.append(" , cancellationReason = :cancellationReason")
				.append(" , cancellationUser = :cancellationUser")
				.append(" , cancellationDate = :cancellationDate")
				.append(" , cancellationUnit = :cancellationUnit")
				.append(" , lastModifyUnit = :lastModifyUnit")
				.append(" , lastModifyUser = :lastModifyUser")
				.append(" , lastModifyDate = :lastModifyDate")
				.append(" where gcId=:gcId").toString();
		Date curDate = Calendar.getInstance().getTime();
		String curUser = UserUtil.get(UserUtil.USER_ID);
		String curUnit = UserUtil.get(UserUtil.MANAUNIT_ID);
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("status", Constants.CODE_STATUS_WRITE_OFF);
		pMap.put("cancellationReason", cancellationReason);
		pMap.put("cancellationUser", curUser);
		pMap.put("cancellationDate", curDate);
		pMap.put("cancellationUnit", curUnit);
		pMap.put("lastModifyUnit", curUnit);
		pMap.put("lastModifyUser", curUser);
		pMap.put("lastModifyDate", curDate);
		pMap.put("gcId", gcId);
		try {
			dao.doUpdate(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "注销肿瘤健康问卷失败！", e);
		}
	}

	/**
	 * 
	 * @Description:注销该人员的全部肿瘤健康问卷
	 * @param empiId
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-10-17 上午9:54:46
	 * @Modify:
	 */
	public void logoutMyAllTHQ(String empiId, String cancellationReason)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append(PHQ_GeneralCase)
				.append(" set status = :status")
				.append(" , cancellationReason = :cancellationReason")
				.append(" , cancellationUser = :cancellationUser")
				.append(" , cancellationDate = :cancellationDate")
				.append(" , cancellationUnit = :cancellationUnit")
				.append(" , lastModifyUnit=:lastModifyUnit")
				.append(" , lastModifyUser=:lastModifyUser")
				.append(" , lastModifyDate=:lastModifyDate")
				.append(" where empiId=:empiId").toString();
		Date curDate = Calendar.getInstance().getTime();
		String curUser = UserUtil.get(UserUtil.USER_ID);
		String curUnit = UserUtil.get(UserUtil.MANAUNIT_ID);
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("status", Constants.CODE_STATUS_WRITE_OFF);
		pMap.put("cancellationReason", cancellationReason);
		pMap.put("cancellationUser", curUser);
		pMap.put("cancellationDate", curDate);
		pMap.put("cancellationUnit", curUnit);
		pMap.put("lastModifyUnit", curUnit);
		pMap.put("lastModifyUser", curUser);
		pMap.put("lastModifyDate", curDate);
		pMap.put("empiId", empiId);
		try {
			dao.doUpdate(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "注销该人员的全部肿瘤健康问卷失败！", e);
		}
	}

	/**
	 * 
	 * @Description:恢复肿瘤健康问卷
	 * @param gcId
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-10-17 上午9:52:03
	 * @Modify:
	 */
	public void revertTumourQuestionnaire(String gcId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append(PHQ_GeneralCase)
				.append(" set status=:status")
				.append(" , cancellationReason = :cancellationReason")
				.append(" , cancellationUser = :cancellationUser")
				.append(" , cancellationDate = :cancellationDate")
				.append(" , cancellationUnit = :cancellationUnit")
				.append(" , lastModifyUnit = :lastModifyUnit")
				.append(" , lastModifyUser = :lastModifyUser")
				.append(" , lastModifyDate = :lastModifyDate")
				.append(" where gcId=:gcId").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("status", Constants.CODE_STATUS_NORMAL);
		pMap.put("cancellationReason", "");
		pMap.put("cancellationUser", "");
		pMap.put("cancellationDate", null);
		pMap.put("cancellationUnit", "");
		pMap.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		pMap.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		pMap.put("lastModifyDate", Calendar.getInstance().getTime());
		pMap.put("gcId", gcId);
		try {
			dao.doUpdate(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "恢复肿瘤健康问卷失败！", e);
		}
	}

	/**
	 * 
	 * @Description:恢复该人员的全部肿瘤健康问卷
	 * @param empiId
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-10-17 上午9:54:46
	 * @Modify:
	 */
	public void revertMyAllTHQ(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append(PHQ_GeneralCase)
				.append(" set status = :status")
				.append(" , cancellationReason = :cancellationReason")
				.append(" , cancellationUser = :cancellationUser")
				.append(" , cancellationDate = :cancellationDate")
				.append(" , cancellationUnit = :cancellationUnit")
				.append(" , lastModifyUnit = :lastModifyUnit")
				.append(" , lastModifyUser = :lastModifyUser")
				.append(" , lastModifyDate = :lastModifyDate")
				.append(" where empiId = :empiId").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("status", Constants.CODE_STATUS_NORMAL);
		pMap.put("cancellationReason", "");
		pMap.put("cancellationUser", "");
		pMap.put("cancellationDate", null);
		pMap.put("cancellationUnit", "");
		pMap.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		pMap.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		pMap.put("lastModifyDate", Calendar.getInstance().getTime());
		pMap.put("empiId", empiId);
		try {
			dao.doUpdate(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "恢复该人员的全部肿瘤健康问卷失败！", e);
		}
	}

	/**
	 * 
	 * @Description:依据empiId判断是否存在 健康档案
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-11-4 上午11:03:34
	 * @Modify:
	 */
	public boolean existHealthRecord(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(*) as recNum from ")
				.append(EHR_HealthRecord)
				.append(" where status='0' and empiId=:empiId").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "依据empiId判断是否存在 健康档案失败！", e);
		}
		boolean existHR = false;
		if (rsMap != null && rsMap.size() > 0) {
			long recNum = (Long) rsMap.get("recNum");
			if (recNum > 0) {
				existHR = true;
			}
		}
		return existHR;
	}

	/**
	 * 
	 * @Description:颁布加载（查询）问卷列表
	 * @param req
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-11-19 下午2:33:17
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> loadTHQRecordsPage(Map<String, Object> req)
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
		boolean isFQ = false;
		if (req.containsKey("isFQ")) {
			isFQ = (Boolean) req.get("isFQ");
		}
		Map<String, Object> res = new HashMap<String, Object>();
		if (isFQ) {// 分页 查 阴性问卷
			StringBuffer psql = new StringBuffer(" from PHQ_GeneralCase a,")
					.append("(select fmr.masterplateid as masterplateid, count(f.fieldid) as fNum from MPM_FieldMaintain f, MPM_FieldMasterRelation fmr where f.fieldid = fmr.fieldid and f.dicRender = '1' group by fmr.masterplateid) b,")
					.append("(select ar.gcid as gcid, ar.masterplateId as masterplateid,count(ar.answerRecordId) as fNum from PHQ_AnswerRecord ar where ar.fieldValue = '2' group by ar.gcid, ar.masterplateId) c ")
					.append(" where a.gcid = c.gcid and b.masterplateid = c.masterplateid and b.fNum = c.fNum");
			if (queryCnd.size() > 0) {
				String cndSQL = "";
				try {
					cndSQL = ExpressionProcessor.instance().toString(queryCnd);
				} catch (ExpException e) {
					throw new ModelDataOperationException(
							Constants.CODE_EXP_ERROR, "查询表达式错误！", e);
				}
				if (StringUtils.isNotEmpty(cndSQL)) {
					psql.append(" and ").append(
							cndSQL.replaceAll("str", "to_char"));
				}
			}
			String countSQL = new StringBuffer("select count(*) as totalCount ")
					.append(psql.toString()).toString();
			Map<String, Object> pMap = new HashMap<String, Object>();
			List<Map<String, Object>> trList = null;
			try {
				trList = dao.doSqlQuery(countSQL, pMap);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "分页查询阴性问卷统计总记录数时失败！", e);
			}
			long totalCount = 0;
			if (trList != null && trList.size() > 0) {
				Map<String, Object> trMap = trList.get(0);
				totalCount = ((BigDecimal) trMap.get("TOTALCOUNT")).longValue();
			}
			if (totalCount > 0) {
				StringBuffer hsql = new StringBuffer();
				Schema sc = null;
				try {
					sc = SchemaController.instance().get(PHQ_GeneralCase);
				} catch (ControllerException e1) {
					throw new ModelDataOperationException(
							Constants.CODE_EXP_ERROR, "schema解析失败！", e1);
				}
				for (SchemaItem si : sc.getItems()) {
					if (si.hasProperty("refAlias") || si.isVirtual()) {
						continue;
					}
					String f = si.getId();
					hsql.append(",a.").append(f).append(" as ").append(f);
				}
				String fsql = new StringBuffer("select ")
						.append(hsql.toString().substring(1))
						.append(psql.toString())
						.append(" order by a.idCard asc ").toString();
				int first = (pageNo - 1) * pageSize;
				pMap.put("first", first);
				pMap.put("max", pageSize);
				List<Map<String, Object>> rsList = null;
				try {
					rsList = dao.doSqlQuery(fsql.toString(), pMap);
				} catch (PersistentDataOperationException e) {
					throw new ModelDataOperationException(
							Constants.CODE_DATABASE_ERROR, "分页查询阴性问卷时失败！", e);
				}
				if (rsList != null && rsList.size() > 0) {
					List<Map<String, Object>> fqList = new ArrayList<Map<String, Object>>();
					for (Map<String, Object> rMap : rsList) {
						Map<String, Object> fqMap = new HashMap<String, Object>();
						for (SchemaItem si : sc.getItems()) {
							if (si.hasProperty("refAlias") || si.isVirtual()) {
								continue;
							}
							String f = si.getId();
							// 计算年龄
							if ("birthday".equals(f)) {
								Date birthday = (Date) rMap.get("BIRTHDAY");
								fqMap.put("age",
										BSCHISUtil.calculateAge(birthday, null));
							}
							fqMap.put(f, rMap.get(f.toUpperCase()));
						}
						fqList.add(fqMap);
					}
					res.put("totalCount", totalCount);
					res.put("body", SchemaUtil.setDictionaryMessageForList(
							fqList, PHQ_GeneralCaseListView));
				} else {
					res.put("totalCount", totalCount);
					res.put("body", new ArrayList<Map<String, Object>>());
				}
			} else {
				res.put("totalCount", 0);
				res.put("body", new ArrayList<Map<String, Object>>());
			}
		} else {
			try {
				Map<String, Object> rsMap = dao.doList(queryCnd, sortInfo,
						PHQ_GeneralCase, pageNo, pageSize, queryCndsType);
				List<Map<String, Object>> rsList = (List<Map<String, Object>>) rsMap
						.get("body");
				for (Map<String, Object> map : rsList) {
					Date birthday = (Date) map.get("birthday");
					if (birthday != null) {
						map.put("age", BSCHISUtil.calculateAge(birthday, null));
					}
				}
				res.put("totalCount", rsMap.get("totalCount"));
				res.put("body", rsList);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "数据加载失败！", e);
			}
		}
		res.put("pageSize", pageSize);
		res.put("pageNo", pageNo);
		return res;
	}

	public Map<String, Object> doCkTHQTemplate(Map<String, Object> req)
			throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		List<?> queryCnd = null;
		StringBuffer hql = new StringBuffer(
				"select count(masterplateId) as num from ")
				.append(MPM_MasterplateMaintain);
		Map<String, Object> parameters = new HashMap<String, Object>();
		queryCnd = (List<?>) req.get("body");
		if (queryCnd.size() > 0) {
			String cndSQL = "";
			try {
				cndSQL = ExpressionProcessor.instance().toString(queryCnd);
			} catch (ExpException e) {
				throw new ModelDataOperationException(Constants.CODE_EXP_ERROR,
						"查询表达式错误！", e);
			}
			if (StringUtils.isNotEmpty(cndSQL)) {
				hql.append(" where ").append(
						cndSQL.replaceAll("str", "to_char"));
			}
		}
		try {
			res = dao.doLoad(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
		return res;
	}

	/**
	 * 
	 * @Description:获取肿瘤健康问卷判定结果
	 * @param gcId
	 * @return 阴性/阳性
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-1-8 下午2:20:45
	 * @Modify:
	 */
	public String getTHQJudgementResult(String gcId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select count(a.answerRecordId) as recNum from ")
				.append(PHQ_AnswerRecord)
				.append(" a, ")
				.append(MPM_DictionaryMaintain)
				.append(" b ")
				.append(" where a.fieldId=b.fieldId and b.text='是' and a.gcId=:gcId")
				.toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("gcId", gcId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "肿瘤健康问卷结果判定失败！", e);
		}
		String jr = "阴性";
		if (rsMap != null) {
			long recNum = 0;
			recNum = (Long) rsMap.get("recNum");
			if (recNum > 0) {
				jr = "阳性";
			}
		}
		return jr;
	}
	
	/**
	 * 
	 * @Description:获取问阳性的值--该方法与getTHQJudgementResult()同功，返回值不同
	 * @param gcId
	 * @return  0(阴性) 1(阳性)
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-5-13 下午2:54:55
	 * @Modify:
	 */
	public String getQuestionnairePositive(String gcId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select count(a.answerRecordId) as recNum from ")
				.append(PHQ_AnswerRecord)
				.append(" a, ")
				.append(MPM_DictionaryMaintain)
				.append(" b ")
				.append(" where a.fieldId=b.fieldId and b.text='是' and a.gcId=:gcId")
				.toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("gcId", gcId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "肿瘤健康问卷结果判定失败！", e);
		}
		String qpv = "0";
		if (rsMap != null) {
			long recNum = 0;
			recNum = (Long) rsMap.get("recNum");
			if (recNum > 0) {
				qpv = "1";
			}
		}
		return qpv;
	}

	/**
	 * 获取最新的全部肿瘤问题模板
	 * 
	 * @Description:
	 * @return
	 * @author ChenXianRui 2015-1-9 下午4:36:56
	 * @Modify:
	 */
	public List<Map<String, Object>> getLatestTHQMasterplate()
			throws ModelDataOperationException {
		String sql = new StringBuffer("select  *   from ")
				.append("( select  row_number() over(partition  by  t.whmb  order by inputdate desc ) as lev ")
				.append(",  t.inputdate as inputdate,t.masterplateid as masterplateid,t.whmb as whmb,t.masterplatename as masterplatename ")
				.append(" from  mpm_masterplatemaintain t ) ")
				.append(" where  lev = 1  and whmb in('03','04','05','06','07','08')")
				.toString();
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doSqlQuery(sql, new HashMap<String, Object>());
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取最新的全部肿瘤问题模板失败！", e);
		}
		return rsList;
	}

	/**
	 * 
	 * @Description:组装问卷试题表单schema的items
	 * @param masterplateId
	 * @param masterplateName
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-1-9 下午5:10:45
	 * @Modify:
	 */
	public List<Map<String, Object>> packageItems(String masterplateId,
			String masterplateName, String group) throws ServiceException {
		List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
		// Map<String, Object> titleMap = new HashMap<String, Object>();
		// titleMap.put("alias", masterplateName);
		// titleMap.put("xtype", "title");
		// titleMap.put("display", 2);
		// titleMap.put("acValue", 1);
		// titleMap.put("group", group);
		// itemList.add(titleMap);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = this.getQuestionnaireByMasterplateId(masterplateId);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		if (rsList != null && rsList.size() > 0) {
			List<Map<String, Object>> filelds = null;
			for (Map<String, Object> fieldAttribute : rsList) {
				String dicRender = (String) fieldAttribute.get("dicRender");
				fieldAttribute.put("display", 2);
				fieldAttribute.put("acValue", 1);
				fieldAttribute.put("group", group);
				if ("0".equals(dicRender)) {
					itemList.add(fieldAttribute);
				} else if ("1".equals(dicRender)) {// 单选
					filelds = this.getListFilelds(fieldAttribute, dao);
					List<Map<String, Object>> items = TumourQuestionnaireModel
							.resetDicItems(filelds, dicRender);
					Map<String, Object> dic = new HashMap<String, Object>();
					dic.put("id",
							"chis.application.tr.schemas.PHQ_TumourQuestionnaire."
									+ fieldAttribute.get("id"));
					dic.put("items", items);
					dic.put("render", "Radio");
					dic.put("colWidth", 70);
					dic.put("columns", 3);
					fieldAttribute.put("dic", dic);
					itemList.add(fieldAttribute);
				} else if ("2".equals(dicRender)) {// 多选
					filelds = this.getListFilelds(fieldAttribute, dao);
					List<Map<String, Object>> items = TumourQuestionnaireModel
							.resetDicItems(filelds, dicRender);
					Map<String, Object> dic = new HashMap<String, Object>();
					dic.put("id",
							"chis.application.tr.schemas.PHQ_TumourQuestionnaire."
									+ fieldAttribute.get("id"));
					dic.put("items", items);
					dic.put("render", "Checkbox");
					dic.put("colWidth", 70);
					dic.put("columns", 3);
					fieldAttribute.put("dic", dic);
					itemList.add(fieldAttribute);
				}else if("3".equals(dicRender)){
					fieldAttribute.put("xtype", "label");
					itemList.add(fieldAttribute);
				}
			}
		}
		return itemList;
	}

	/**
	 * 
	 * @Description:依据问卷模板类型对某人进行问卷模板过虑，看那些类型是要做问卷的 <br/>
	 *                                              条件: ①已做过问卷就不再做问卷
	 *                                              ②已是本年度初筛人群就不再做问卷
	 *                                              ③已是高危、确诊人群就不再做问卷
	 * @param empiId
	 * @param masterplateTypes
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-1-28 下午5:39:39
	 * @Modify:
	 */
	public List<String> getNeedQuestionnaireOfTemplate(String empiId,
			List<String> masterplateTypes) throws ModelDataOperationException {
		TumourScreeningModel tsModel = new TumourScreeningModel(dao);
		TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
		TumourConfirmedModel tcModel = new TumourConfirmedModel(dao);
		List<Map<String, Object>> lthqmList = getLatestTHQMasterplate();
		StringBuffer latestHT = new StringBuffer();
		if (lthqmList != null && lthqmList.size() > 0) {
			for (int i = 0, len = lthqmList.size(); i < len; i++) {
				Map<String, Object> lmMap = lthqmList.get(i);
				String whmb = (String) lmMap.get("WHMB");
				latestHT.append(mt2hrt(whmb));
				if (i < len - 1) {
					latestHT.append(",");
				}
			}
		}
		List<String> mTypes = new ArrayList<String>();
		for (int i = 0, len = masterplateTypes.size(); i < len; i++) {
			String ht = mt2hrt(masterplateTypes.get(i));
			boolean isExist = false;
			isExist = this.existTHQatThisYear(empiId, ht);
			if(!isExist){//没有问卷
				isExist = tsModel.isTumourScreening(empiId, ht);
				if (!isExist) {// 没有初筛
					isExist = thrModel.isExistTHR(empiId, ht);
					if (!isExist) {// 不是高危
						isExist = tcModel.isTumourConfirmed(empiId, ht);
						if (!isExist) {// 不是确诊
							if (latestHT.indexOf(ht) != -1) {// 有模板才行
								mTypes.add(hrt2mt(ht));
							}
						}
					}
				}
			}
		}
		return mTypes;
	}

	/**
	 * 
	 * @Description:判断想当年里是否有某高危类型的肿瘤健康问卷
	 * @param empiId
	 * @param highRiskType
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-4-22 下午12:59:34
	 * @Modify:
	 */
	public boolean existTHQatThisYear(String empiId, String highRiskType)
			throws ModelDataOperationException {
		boolean existTHQ = false;
		String hql = new StringBuffer("select count(a.gcId) as recNum from ")
				.append(PHQ_GeneralCase)
				.append(" a ")
				.append(" where a.empiId=:empiId")
				.append(" and a.highRiskType=:highRiskType")
				.append(" and a.surveyDate>=:startDate and a.surveyDate<=:endDate")
				.toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		pMap.put("highRiskType", highRiskType);
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		Date startDate = BSCHISUtil.toDate(year + "-01-01 00:00:00");
		Date endDate = BSCHISUtil.toDate(year + "-12-31 23:59:59");
		pMap.put("startDate", startDate);
		pMap.put("endDate", endDate);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "判断当年是否有某类型问卷失败！", e);
		}
		if(rsMap != null){
			long recNum = 0;
			recNum = (Long) rsMap.get("recNum");
			if(recNum > 0){
				existTHQ = true;
			}
		}
		return existTHQ;
	}
}
