/**
 * @(#)TumourCriterionModel.java Created on 2014-3-23 上午10:43:06
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

import com.bsoft.mpi.util.CndHelper;

import ctd.validator.ValidateException;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.TumourNature;
import chis.source.util.CNDHelper;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourCriterionModel implements BSCHISEntryNames {

	private BaseDAO dao;

	public TumourCriterionModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @Description:判断该年分该类别该高危因素的初筛转高危标准是否存在
	 * @param year
	 * @param highRiskType
	 * @param highRiskFactor
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-27 下午4:18:33
	 * @Modify:
	 */
	public boolean isExistCriterion(int year, String highRiskType,
			String highRiskFactor, String highRiskSource)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(*) as recNum from ")
				.append(MDC_TumourHighRiskCriterion)
				.append(" where year=:year and highRiskType=:highRiskType")
				.append(" and highRiskFactor=:highRiskFactor")
				.append(" and highRiskSource=:highRiskSource").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("year", year);
		pMap.put("highRiskType", highRiskType);
		pMap.put("highRiskFactor", highRiskFactor);
		pMap.put("highRiskSource", highRiskSource);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "判断该标准是否存在失败！", e);
		}
		boolean isExist = false;
		if (rsMap != null) {
			long recNum = (Long) rsMap.get("recNum");
			if (recNum > 0) {
				isExist = true;
			}
		}
		return isExist;
	}

	/**
	 * 
	 * @Description:保存肿瘤问卷标准
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-3-23 上午11:10:00
	 * @Modify:
	 */
	public Map<String, Object> saveTQCriterion(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao
					.doSave(op, MDC_QuestionnaireCriterion, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤问卷标准时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤问卷标准失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:保存肿瘤问卷标准明细
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-3-23 上午11:24:23
	 * @Modify:
	 */
	public Map<String, Object> saveQuestCriterionDetail(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_QuestCriterionDetail, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤问卷标准明细时数据验证失败！");
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤问卷标准明细失败！");
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:删除肿瘤问卷标准明细
	 * @param QCId
	 * @param QMId
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-3-23 上午11:29:38
	 * @Modify:
	 */
	public void deleteQuestCriterionDetail(String QCId, String QMId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(" delete ")
				.append(MDC_QuestCriterionDetail)
				.append(" where QCId=:QCId and QMId=:QMId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("QCId", QCId);
		parameters.put("QMId", QMId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除肿瘤问卷标准明细失败！");
		}
	}

	/**
	 * 
	 * @Description:删除肿瘤问卷标准
	 * @param pkey
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-3-23 下午4:57:26
	 * @Modify:
	 */
	public void deleteQuestCriterion(String pkey)
			throws ModelDataOperationException {
		try {
			dao.doRemove(pkey, MDC_QuestionnaireCriterion);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除肿瘤问卷标准失败！");
		}
	}

	/**
	 * 
	 * @Description:保存筛转高危标准
	 * @param op
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-3-27 上午9:21:36
	 * @Modify:
	 */
	public Map<String, Object> saveTHRCriterion(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_TumourHighRiskCriterion, record,
					validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存筛转高危标准时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存筛转高危标准失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description: 依据模版ID取最近的问卷标准
	 * @param QMId
	 *            问卷模版编号
	 * @param source
	 *            问卷业源
	 * @param hrcId
	 *            高危标准ID
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-6 上午9:22:17
	 * @Modify:
	 */
	public List<Map<String, Object>> getLastTQCriterionList(String QMId,String source,
			String hrcId) throws ModelDataOperationException {
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "QMId", "s", QMId);
		List<Object> cnd = null;
		if(StringUtils.isNotEmpty(source)){
			List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "highRiskSource", "s",
					source);
			cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		}
		if (StringUtils.isNotEmpty(hrcId)) {
			List<Object> cnd3 = CNDHelper.createSimpleCnd("eq", "hrcId", "s",
					hrcId);
			cnd = CNDHelper.createArrayCnd("and", cnd1, cnd3);
		} else {
			cnd = cnd1;
		}
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doList(cnd, "year desc,createDate desc",
					MDC_QuestionnaireCriterion);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "依据模板ID获取问卷标准失败！", e);
		}
		return rsList;
	}

	/**
	 * 
	 * @Description:获取问卷标准明细个数
	 * @param qcId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-19 下午3:49:15
	 * @Modify:
	 */
	public long getTQCriterionDetailNum(String qcId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(*) as qcdNum from ")
				.append(MDC_QuestCriterionDetail).append(" where QCId=:qcId")
				.toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("qcId", qcId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "统计问卷标准明细个数失败!", e);
		}
		long qcdNum = 0;
		if (rsMap != null && rsMap.size() > 0) {
			qcdNum = (Long) rsMap.get("qcdNum");
		}
		return qcdNum;
	}

	/**
	 * 
	 * @Description:依据肿瘤类型确定高危标准年份
	 * @param highRiskType
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-28 下午5:14:01
	 * @Modify:
	 */
	public int getTHRCriterionYear(String highRiskType)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select year as year from ")
				.append(MDC_TumourHighRiskCriterion)
				.append(" where highRiskType=:highRiskType")
				.append(" group by year order by year desc").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("highRiskType", highRiskType);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "依据肿瘤类型确定高危标准年份失败！", e);
		}
		int year = Calendar.getInstance().get(Calendar.YEAR);
		if (rsList != null && rsList.size() > 0) {
			Map<String, Object> rsMap = rsList.get(0);
			year = (Integer) rsMap.get("year");
		}
		return year;
	}

	/**
	 * 
	 * @Description:依据肿瘤类型,高危来源 确定高危标准年份
	 * @param highRiskType
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-28 下午5:14:01
	 * @Modify:
	 */
	public int getTHRCriterionYear(String highRiskType, String highRiskSource)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select year as year from ")
				.append(MDC_TumourHighRiskCriterion)
				.append(" where highRiskType=:highRiskType")
				.append(" and highRiskSource=:highRiskSource")
				.append(" group by year order by year desc").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("highRiskType", highRiskType);
		pMap.put("highRiskSource", highRiskSource);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "依据肿瘤类型确定高危标准年份失败！", e);
		}
		int year = 0;
		if (rsList != null && rsList.size() > 0) {
			Map<String, Object> rsMap = rsList.get(0);
			year = (Integer) rsMap.get("year");
		}
		return year;
	}

	/**
	 * 
	 * @Description:依据肿瘤类型获取高危标准
	 * @param highRiskType
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-28 下午5:11:51
	 * @Modify:
	 */
	public List<Map<String, Object>> getTHRCriterion(String highRiskType,
			String highRiskSource) throws ModelDataOperationException {
		// 确定标准年份
		int year = this.getTHRCriterionYear(highRiskType, highRiskSource);
		boolean hrsHave = true;
		if (year == 0) {// 如果按类型和高危来源取不到年份则用类型再取一次
			year = this.getTHRCriterionYear(highRiskType);
			hrsHave = false;
		}
		// 取可用标准列表
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "highRiskType",
				"s", highRiskType);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "year", "i", year);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		if (hrsHave) {
			List<Object> cnd3 = CNDHelper.createSimpleCnd("eq",
					"highRiskSource", "s", highRiskSource);
			List<Object> cnd4 = CndHelper.createArrayCnd("and", cnd, cnd3);
			cnd = cnd4;
		}
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doList(cnd, "highRiskFactor",
					MDC_TumourHighRiskCriterion);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "依据肿瘤类型获取高危标准失败！", e);
		}
		return rsList;
	}

	/**
	 * 
	 * @Description:判断是否有问卷症状
	 * @param empiId
	 *            个人主索引
	 * @param highRiskType
	 *            高危类型
	 * @param hrcId
	 *            高危标准ID
	 * @return true 有 false 无
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-29 上午9:39:42
	 * @Modify:
	 */
	public boolean isExistTumourQuestionnaireSymptom(String empiId,
			String highRiskType, String hrcId)
			throws ModelDataOperationException {
		// 高危类型转问卷模板类型
		String masterplateType = "0" + (Integer.parseInt(highRiskType) + 2);
		TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
		// 取问卷列表
		List<Map<String, Object>> tqList = tqModel.getTumourQuestionnaire(
				empiId, masterplateType);
		boolean hasTQS = false;
		if (tqList != null && tqList.size() > 0) {
			// 取出最近一次问卷
			Map<String, Object> tqMap = tqList.get(0);
			String gcId = (String) tqMap.get("gcId");
			String source = (String) tqMap.get("source");
			String QMId = (String) tqMap.get("masterplateId");
			// 判断问卷结果与标准是否符合，符合则说明有问卷症状
			hasTQS = this.isAccordWithCriterion(empiId, QMId, gcId, source,hrcId);
		}
		return hasTQS;
	}

	/**
	 * 
	 * @Description:判断某人某次问卷是否符合标准(有一个对上就通过)
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
			String gcId,String source, String hrcId) throws ModelDataOperationException {
		boolean passport = false;
		List<Map<String, Object>> rsList = this.getLastTQCriterionList(QMId,source,
				hrcId);
		if (rsList != null && rsList.size() > 0) {
			Map<String, Object> qcMap = rsList.get(0);
			String criterionExplain = (String) qcMap.get("criterionExplain");
			if ("1".equals(criterionExplain)) {// 所有，则直接过
				passport = true;
			} else {// 可疑心 ，则判断标准明细
				String qcId = (String) qcMap.get("recordId");
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
	 * @Description:判断是否存在某种既往史
	 * @param empiId
	 *            个人主索引
	 * @param diseaseCodes
	 *            既往史（疾病）代码
	 * @param pastHisTypeCode
	 *            既往史类别代码
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-29 下午2:10:39
	 * @Modify:
	 */
	public boolean isExitCancer(String empiId, String[] diseaseCodes,
			String... pastHisTypeCode) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("select count(*) as recNum from ")
				.append(EHR_PastHistory).append(" where empiId=:empiId ");
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		int dcLen = diseaseCodes.length;
		if (dcLen == 1) {
			hql.append(" and diseaseCode=:diseaseCode");
			pMap.put("diseaseCode", diseaseCodes[0]);
		}
		if (dcLen > 1) {
			String[] dcs = new String[dcLen];
			for (int i = 0; i < dcLen; i++) {
				dcs[i] = "'" + diseaseCodes[i] + "'";
			}
			hql.append(" and diseaseCode in(" + StringUtils.join(dcs, ',')
					+ ")");
		}
		int phtcLen = pastHisTypeCode.length;
		if (phtcLen == 1) {
			hql.append(" and pastHisTypeCode=:pastHisTypeCode");
			pMap.put("pastHisTypeCode", pastHisTypeCode[0]);
		}
		if (phtcLen > 1) {
			String[] phtCodes = new String[phtcLen];
			for (int j = 0; j < phtcLen; j++) {
				phtCodes[j] = "'" + pastHisTypeCode[j] + "'";
			}
			hql.append(" and pastHisTypeCode in("
					+ StringUtils.join(phtCodes, ',') + ")");
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql.toString(), pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "判断是否存在既往史失败！", e);
		}
		boolean isExist = false;
		if (rsMap != null && rsMap.size() > 0) {
			long recNum = (Long) rsMap.get("recNum");
			if (recNum > 0) {
				isExist = true;
			}
		}
		return isExist;
	}

	/**
	 * 
	 * @Description:肿瘤标准与初筛检查结果对比
	 * @param screeningId
	 *            初筛记录号
	 * @param criterionSerialNumber
	 *            标准序号
	 * @param highRiskType
	 *            高危类别
	 * @param PSItemRelation
	 *            初筛检查项目关系
	 * @param traceItemRelation
	 *            追踪检查项目关系
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-29 下午3:10:42
	 * @Modify: ChenXianRui 2015-04-13 14:42
	 */
	public boolean isAccordWithCheckResult(String screeningId,
			String criterionSerialNumber, String highRiskType,
			String PSItemRelation, String traceItemRelation)
			throws ModelDataOperationException {
		// 判断标准配置中是否 有 追踪检查项目
		String hql = new StringBuffer("select count(a.hrcdId) as tiNum from ")
				.append(MDC_TumourHRCDetail).append(" a ")
				.append(" where a.criterionType='2' ")
				.append(" and a.criterionSerialNumber=:criterionSerialNumber")
				.toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("criterionSerialNumber", criterionSerialNumber);
		Map<String, Object> tiMap = null;
		try {
			tiMap = dao.doLoad(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "判断标准配置中是否 有 追踪检查项目失败！", e);
		}
		boolean isExist = false;
		long tiNum = 0;
		if (tiMap != null && tiMap.size() > 0) {
			tiNum = (Long) tiMap.get("tiNum");
		}
		if (tiNum > 0) {
			// 有追踪项目以追踪项目标准为准
			String sql = new StringBuffer(
					"select count(a.recordId) as recNum from ")
					.append("MDC_TumourScreeningCheckResult a, MDC_TumourHRCDetail b")
					.append(" where a.itemid = b.inspectionitem ")
					.append(" and a.checkresult = b.criterionresult ")
					.append(" and a.screeningid=:screeningId")
					.append(" and a.highrisktype=:highRiskType")
					.append(" and b.criterionSerialNumber=:criterionSerialNumber")
					.append(" and b.criterionType='2'").toString();
			pMap.put("screeningId", screeningId);
			pMap.put("highRiskType", highRiskType);
			List<Map<String, Object>> rsList = null;
			try {
				rsList = dao.doSqlQuery(sql, pMap);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "肿瘤标准与初筛检查结果对比失败！", e);
			}
			long recNum = 0;
			if (rsList != null && rsList.size() > 0) {
				recNum = ((BigDecimal) rsList.get(0).get("RECNUM")).longValue();
			}
			if ("2".equals(traceItemRelation)) {// 关系 且
				if(recNum > 0 && recNum == tiNum){
					isExist = true;
				}
			} else {// 关系 或
				if (recNum > 0) {
					isExist = true;
				}
			}
		} else {
			// 无追踪项目以初筛项目标准为准
			String sql = new StringBuffer(
					"select count(a.recordId) as recNum from ")
					.append("MDC_TumourScreeningCheckResult a, MDC_TumourHRCDetail b")
					.append(" where a.itemid = b.inspectionitem ")
					.append(" and a.checkresult = b.criterionresult ")
					.append(" and a.screeningid=:screeningId")
					.append(" and a.highrisktype=:highRiskType")
					.append(" and b.criterionSerialNumber=:criterionSerialNumber")
					.append(" and b.criterionType='1'").toString();
			pMap.put("screeningId", screeningId);
			pMap.put("highRiskType", highRiskType);
			List<Map<String, Object>> rsList = null;
			try {
				rsList = dao.doSqlQuery(sql, pMap);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "肿瘤标准与初筛检查结果对比失败！", e);
			}
			long recNum = 0;
			if (rsList != null && rsList.size() > 0) {
				recNum = ((BigDecimal) rsList.get(0).get("RECNUM")).longValue();
			}
			if ("2".equals(PSItemRelation)) {// 关系 且
				String hrcHQL = new StringBuffer(
						"select count(a.hrcdId) as hrcNum from ")
						.append(MDC_TumourHRCDetail)
						.append(" a ")
						.append(" where a.criterionType='1'")
						.append(" and a.criterionSerialNumber=:criterionSerialNumber")
						.toString();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("criterionSerialNumber", criterionSerialNumber);
				Map<String, Object> hrcMap = null;
				try {
					hrcMap = dao.doLoad(hrcHQL, parameters);
				} catch (PersistentDataOperationException e) {
					throw new ModelDataOperationException(
							Constants.CODE_DATABASE_ERROR, "获取标准中初筛查询项目数失败！", e);
				}
				long hrcNum = 0;
				if(hrcMap != null && hrcMap.size() > 0){
					hrcNum = (Long) hrcMap.get("hrcNum");
				}
				if(recNum > 0 && recNum == hrcNum){
					isExist = true;
				}
			} else {// 关系 或
				if (recNum > 0) {
					isExist = true;
				}
			}
		}
		return isExist;
	}

	/**
	 * 
	 * @Description:判断肿瘤高危检查项标准是否存在
	 * @param inspectionItem
	 * @param hrcdId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-11-6 上午10:51:08
	 * @Modify:
	 */
	public boolean existTHRCIC(String criterionSerialNumber,
			String inspectionItem, String hrcdId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("select count(*) as recNum from ")
				.append(MDC_TumourHRCDetail)
				.append(" where inspectionItem=:inspectionItem")
				.append(" and criterionSerialNumber=:criterionSerialNumber");
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("inspectionItem", inspectionItem);
		pMap.put("criterionSerialNumber", criterionSerialNumber);
		if (StringUtils.isNotEmpty(hrcdId)) {
			hql.append(" and hrcdId != :hrcdId");
			pMap.put("hrcdId", hrcdId);
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql.toString(), pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询肿瘤高危标准检查项标准是否存在失败！", e);
		}
		boolean exist = false;
		if (rsMap != null && rsMap.size() > 0) {
			long recNum = (Long) rsMap.get("recNum");
			if (recNum > 0) {
				exist = true;
			}
		}
		return exist;
	}

	/**
	 * 
	 * @Description:保存肿瘤高危标准检查项标准
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-11-6 上午11:03:33
	 * @Modify:
	 */
	public Map<String, Object> saveTHRCIC(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_TumourHRCDetail, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, "保存肿瘤高危标准检查项标准时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, "保存肿瘤高危标准检查项标准失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description: 删除肿瘤高危确诊标准检查项目
	 * @param criterionSerialNumber
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-11-18 下午3:37:51
	 * @Modify:
	 */
	public void removeTumourHRCDetail(String criterionSerialNumber)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ")
				.append(MDC_TumourHRCDetail)
				.append(" where criterionSerialNumber=:criterionSerialNumber")
				.toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("criterionSerialNumber", criterionSerialNumber);
		try {
			dao.doUpdate(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除肿瘤高危确诊标准检查项目失败！", e);
		}
	}

	/**
	 * 
	 * @Description:删除肿瘤高危确诊标准
	 * @param pkey
	 * @param highRiskFactor
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-11-18 下午4:09:53
	 * @Modify:
	 */
	public void removeTHRCriterion(String pkey, String highRiskFactor)
			throws ModelDataOperationException {
		// 删除检查项目
		this.removeTumourHRCDetail(pkey);
		// 删除高危问卷
		if ("1".equals(highRiskFactor)) {// 高危因素 问卷症状--有
			List<Map<String, Object>> thqList = this.getTHQCriterion(pkey);
			if (thqList != null && thqList.size() > 0) {
				for (Map<String, Object> thqMap : thqList) {
					String recordId = (String) thqMap.get("recordId");
					String QMId = (String) thqMap.get("QMId");
					// 删除问卷标准明细
					this.deleteQuestCriterionDetail(pkey, QMId);
					// 删除问卷标准记录
					this.deleteQuestCriterion(recordId);
				}
			}
		}
		// 删除确诊标准记录
		try {
			dao.doRemove(pkey, MDC_TumourHighRiskCriterion);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除肿瘤高危确诊标准记录失败！", e);
		}
	}

	/**
	 * 
	 * @Description:获取肿瘤高危确诊问卷标准
	 * @param hrcId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-11-18 下午4:00:32
	 * @Modify:
	 */
	public List<Map<String, Object>> getTHQCriterion(String hrcId)
			throws ModelDataOperationException {
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "criterionType",
				"s", TumourNature.T_HighRisk);
		List<Object> cnd2 = CNDHelper
				.createSimpleCnd("eq", "hrcId", "s", hrcId);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doList(cnd, null, MDC_QuestionnaireCriterion);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取肿瘤高危确诊问卷标准失败！", e);
		}
		return rsList;
	}

}
