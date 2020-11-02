/**
 * @(#)ChildrenHealthModel.java Created on 2011-12-27 下午3:55:43
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.cdh;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.YesNo;
import chis.source.empi.EmpiModel;
import chis.source.empi.EmpiUtil;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;
import chis.source.util.UserUtil;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.Dictionary;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */

public class ChildrenBaseModel extends EmpiModel {
	private BaseDAO dao;

	/**
	 * @param dao
	 */
	public ChildrenBaseModel(BaseDAO dao) {
		super(dao);
		this.dao = dao;
	}

	/**
	 * 保存儿童档案数据
	 * 
	 * @param data
	 * @param jsonRes
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void saveChildrenHealthRecord(Map<String, Object> data)
			throws ModelDataOperationException, ValidateException {
		Map<String, Object> healBody = new HashMap<String, Object>();
		String op = null;
		String empiId = (String) data.get("empiId");
		String regionCode = (String) data.get("regionCode");
		Dictionary dic = null;
		try {
			dic = DictionaryController.instance().get(
					"chis.dictionary.areaGrid");
		} catch (ControllerException e1) {
			throw new ModelDataOperationException(e1);
		}
		if (dic != null) {
			healBody.put("regionCode_text", dic.getText(regionCode));
		}
		healBody.put("empiId", empiId);
		healBody.put("regionCode", regionCode);
		healBody.put("isAgrRegister", data.get("isAgrRegister"));
		healBody.put("manaDoctorId", data.get("manaDoctorId"));
		healBody.put("manaUnitId", data.get("manaUnitId"));
		Map<String, Object> healthRecord = getHealthRecordByEmpiId(empiId);
		if (healthRecord != null && healthRecord.size() > 0) {
			op = "update";
			healBody.put("phrId", healthRecord.get("phrId"));
		} else {
			op = "create";
			healBody.put("status", Constants.CODE_STATUS_NORMAL);
			Context ctx = dao.getContext();
			healBody.put("createUser", UserUtil.get(UserUtil.USER_ID));
			healBody.put("createUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
			healBody.put("createDate",
					new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			healBody.put("isDiabetes", YesNo.NO);
			healBody.put("isHypertension", YesNo.NO);
			healBody.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
			healBody.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
			healBody.put("lastModifyDate", new Date());
			Context c = new Context();
			c.put("regionCode", regionCode);
			ctx.put("codeCtx", c);
		}
		if (data.get("familyId") != null) {
			healBody.put("familyId", data.get("familyId"));
		}
		try {
			dao.doSave(op, EHR_HealthRecord, healBody, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存儿童个人健康档案失败。", e);
		}
	}

	/**
	 * 根据empiId获取儿童档案信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getChildBaseInfoByEmpiId(String empiId)
			throws ModelDataOperationException {
		Map<String, Object> info = getEmpiInfoByEmpiid(empiId);
		if (info == null || info.isEmpty()) {
			return null;
		}
		Map<String, Object> otherInfo = getOtherChildBaseInfo(empiId);
		info.putAll(otherInfo);
		return info;
	}

	/**
	 * 根据idCard获取儿童档案信息
	 * 
	 * @param idCard
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getChildBaseInfoByIdCard(String idCard)
			throws ModelDataOperationException {
		List<Map<String, Object>> mpiList = getEmpiInfoByIdcard(idCard);
		if (mpiList == null || mpiList.isEmpty()) {
			return null;
		}
		List<Map<String, Object>> infoList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < mpiList.size(); i++) {
			Map<String, Object> info = mpiList.get(i);
			String empiId = (String) info.get("empiId");
			Map<String, Object> otherInfo = getOtherChildBaseInfo(empiId);
			info.putAll(otherInfo);
			infoList.add(info);
		}
		return infoList;
	}

	/**
	 * 根据cardNo获取儿童档案信息
	 * 
	 * @param cardNo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getChildBaseInfoByCardNo(String cardNo)
			throws ModelDataOperationException {
		List<Map<String, Object>> list = getEmpiInfoByCardNo(cardNo);
		if (list.size() == 0 || list.isEmpty()) {
			return null;
		}
		return list;
	}

	/**
	 * 根据儿童姓名+性别+出生日期获取儿童基本信息
	 * 
	 * @param personName
	 * @param sexCode
	 * @param birthday
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getChildBaseInfoByOther(String personName,
			String sexCode, String birthday) throws ModelDataOperationException {
		List<Map<String, Object>> mpiList = getEmpiInfoByBase(personName,
				sexCode, birthday);
		if (mpiList == null || mpiList.isEmpty()) {
			return null;
		}
		List<Map<String, Object>> infoList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < mpiList.size(); i++) {
			Map<String, Object> info = mpiList.get(i);
			String empiId = (String) info.get("empiId");
			Map<String, Object> otherInfo = getOtherChildBaseInfo(empiId);
			info.putAll(otherInfo);
			infoList.add(info);
		}
		return infoList;
	}

	/**
	 * 获取儿童档案相关信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getOtherChildBaseInfo(String empiId)
			throws ModelDataOperationException {
		Map<String, Object> info = new HashMap<String, Object>();
		Map<String, Object> healthRecord = getHealthRecordByEmpiId(empiId);
		if (healthRecord != null && !healthRecord.isEmpty()) {
			info.put("phrId", healthRecord.get("phrId"));
			info.put("regionCode", healthRecord.get("regionCode"));
			info.put("manaDoctorId", healthRecord.get("manaDoctorId"));
			info.put("manaUnitId", healthRecord.get("manaUnitId"));
			info.put("isAgrRegister", healthRecord.get("isAgrRegister"));
		}
		Map<String, Object> relInfo = null;
		relInfo = getChildInfoByEmpiId(empiId);
		if (relInfo != null && !relInfo.isEmpty()) {
			info.putAll(relInfo);
		}
		BirthCertificateModel bcm = new BirthCertificateModel(dao);
		// 判断出生证明是否有对应记录
		Map<String, Object> chf = null;
		chf = (Map<String, Object>) bcm
				.getBirthCertificateRecordByEmpiId(empiId);
		if (chf != null && !chf.isEmpty()) {
			info.put("existCno", true);
		}
		return info;
	}

	/**
	 * 获取儿童直系亲属信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getChildInfoByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "childEmpiId", "s",
				empiId);

		Map<String, Object> childInfo = null;
		try {
			childInfo = dao.doLoad(cnd, MPI_ChildInfo);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取儿童直系亲属信息失败", e);
		}
		return childInfo;
	}

	/**
	 * 获取儿童直系亲属信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getChildInfoByRelativeIdCard(String idCard)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "relativeIdCard", "s",
				EmpiUtil.idCard15To18(idCard.toUpperCase()));

		List<Map<String, Object>> childInfo = null;
		try {
			childInfo = dao.doList(cnd, null, MPI_ChildInfo);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取儿童直系亲属信息失败", e);
		}
		return childInfo;
	}

	/**
	 * 检查出生证编号是否重复 checkCertificateNo
	 * 
	 * @param empiId
	 * @param certificateNo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean checkCertificateNo(String empiId, String certificateNo)
			throws ModelDataOperationException {
		if (certificateNo == null || "".equals(certificateNo)) {
			return false;
		}
		List<?> cnd1 = CNDHelper.createSimpleCnd("ne", "childEmpiId", "s",
				empiId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "certificateNo", "s",
				certificateNo);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			List<Map<String, Object>> list = dao.doList(cnd, null,
					MPI_ChildInfo);
			if (list != null && list.size() > 0) {
				return true;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "检查出生证编号是否重复失败", e);
		}
		return false;
	}

	/**
	 * 保存儿童直系亲属信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void saveChildInfo(Map<String, Object> data, String op)
			throws ModelDataOperationException, ValidateException {
		String empiId = (String) data.get("empiId");
		String relativeIdCard = (String) data.get("relativeIdCard");
		data.put("childEmpiId", empiId);
		data.put("relativeIdCard",
				EmpiUtil.idCard15To18(relativeIdCard.toUpperCase()));
		Map<String, Object> r = getChildInfoByEmpiId(empiId);
		if ("update".equals(StringUtils.trimToEmpty(op)) && r != null) {
			removeChildRelative(empiId);
		}
		try {
			dao.doSave("create", MPI_ChildInfo, data, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存儿童直系亲属信息失败", e);
		}
	}

	/**
	 * 根据empiId获取健康档案信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	private Map<String, Object> getHealthRecordByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		Map<String, Object> healthRecord;
		try {
			healthRecord = dao.doLoad(cnd, EHR_HealthRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取健康档案信息失败。", e);
		}
		return healthRecord;
	}

	/**
	 * 更新儿童档案既往史数据
	 * 
	 * @param parameters
	 * @throws ModelDataOperationException
	 */
	public void updateHealthCard(Map<String, Object> parameters)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append(CDH_HealthCard)
				.append(" set deformity=:deformityKey,")
				.append("allergicHistory=:allergicHistoryKey,")
				.append("otherDeformity=:otherDeformity,")
				.append("otherAllergyDrug=:otherAllergicHistory ")
				.append("where empiId =:empiId").toString();
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "更新儿童档案既往史信息失败！", e);
		}
	}

	/**
	 * 删除儿童直系亲属信息
	 * 
	 * @param empiId
	 * @throws ModelDataOperationException
	 */
	public void removeChildRelative(String empiId)
			throws ModelDataOperationException {
		try {
			dao.doRemove(empiId, MPI_ChildInfo);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除儿童直系亲属信息失败！", e);
		}
	}

	/**
	 * 查询某条计划的随访时间
	 * 
	 * @param empiId
	 * @param extend1
	 * @param businessType
	 * @param planStatus
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getPlanVisitDate(String empiId, Integer extend1,
			String businessType, String planStatus)
			throws ModelDataOperationException {
		StringBuffer sb = new StringBuffer(
				"select visitDate as visitDate from ");
		sb.append(PUB_VisitPlan).append(
				" where empiId = :empiId and businessType = :businessType ");
		sb.append(" and extend1= :extend1 and  planStatus = :planStatus");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);
		param.put("businessType", businessType);
		param.put("extend1", extend1);
		param.put("planStatus", planStatus);
		try {
			return dao.doLoad(sb.toString(), param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取随访计划失败!", e);
		}
	}

	
}
