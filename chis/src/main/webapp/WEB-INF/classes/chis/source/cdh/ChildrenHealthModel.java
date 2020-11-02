/**
 * @(#)ChildrenBaseModel.java Created on 2011-12-31 下午5:05:30
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */

package chis.source.cdh;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.PastHistoryCode;
import chis.source.dic.RecordStatus;
import chis.source.dic.YesNo;
import chis.source.service.ServiceCode;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */

public class ChildrenHealthModel implements BSCHISEntryNames {

	private BaseDAO dao;

	public ChildrenHealthModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 根据 婴儿出生证号 获取新生儿访视基本信息
	 * 
	 * @param certificateNo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getBabyVisitInfoByCertificateNo(
			String certificateNo) throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "certificateNo", "s",
				certificateNo);
		Map<String, Object> re = null;
		try {
			List<Map<String, Object>> list = dao.doList(cnd, null,
					MHC_BabyVisitInfo);
			if (list.size() > 0) {
				re = list.get(0);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取新生儿访视基本信息失败", e);
		}
		return re;
	}

	/**
	 * <该方法为体格检查提供> 根据 婴儿出生证号(certificateNo) 获取新生儿访视记录
	 * 
	 * @param certificateNo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getBabyVisitRecordByCertificateNoForCheckUp(
			String certificateNo) throws ModelDataOperationException {
		String hql = new StringBuilder(
				"select  weight as  weight,skin as skin,")
				.append("skinAbnormal as skinText,eye as pupil,ear as ear,")
				.append("earAbnormal as earText,mouse as mouse,mouseAbnormal as mouseText,")
				.append("abdominal as abdomen,abdominalabnormal as abdomenText,umbilical as navel,")
				.append("umbilicalOther as navelText,bregmaTransverse as bregmaTransverse,")
				.append("bregmaLongitudinal as bregmaLongitudinal from ")
				.append(MHC_BabyVisitRecord)
				.append(" where babyId = ( select babyId from ")
				.append(MHC_BabyVisitInfo)
				.append(" where certificateNo = :certificateNo)").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("certificateNo", certificateNo);
		List<Map<String, Object>> list;
		try {
			list = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取新生儿随访记录失败", e);
		}
		return list;
	}

	/**
	 * <该方法为儿童询问提供> 根据 婴儿出生证号(certificateNo) 获取新生儿访视记录
	 * 
	 * @param certificateNo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getBabyVisitRecordByCertificateNoForInquire(
			String certificateNo) throws ModelDataOperationException {
		String hql = new StringBuilder(
				"select stoolTimes as defecateTimes,stoolColor as fecesColor,")
				.append("stoolDates as defecateDates,stoolStatus as fecesState")
				.append(" from ").append(MHC_BabyVisitRecord)
				.append(" where babyId = ( select babyId from ")
				.append(MHC_BabyVisitInfo)
				.append(" where certificateNo = :certificateNo)").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("certificateNo", certificateNo);
		List<Map<String, Object>> list;
		try {
			list = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取新生儿随访记录失败", e);
		}
		return list;
	}

	/**
	 * 根据empiId 和 既往史类别代码（pastHisTypeCode）获取既往史信息
	 * 
	 * @param empiId
	 * @param pastHisTypeCode
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getPastHistoryByEmpiIdAndTypeCode(
			String empiId, String pastHisTypeCode)
			throws ModelDataOperationException {
		ArrayList<Object> cnd1 = (ArrayList<Object>) CNDHelper.createSimpleCnd(
				"eq", "empiId", "s", empiId);
		ArrayList<Object> cnd2 = (ArrayList<Object>) CNDHelper.createSimpleCnd(
				"eq", "pastHisTypeCode", "s", pastHisTypeCode);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> list;
		try {
			list = dao.doList(cnd, null, EHR_PastHistory);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取既往史信息失败", e);
		}
		return list;
	}

	/**
	 * 根据个人健康档案的网格地址获取其儿保医生
	 * 
	 * @param empiId
	 * @throws ModelDataOperationException
	 */
	public String getCDHDoctor(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select cdhDoctor as cdhDoctor from ")
				.append("EHR_AreaGridChild").append(" where  regionCode = ")
				.append("(select regionCode from ").append(EHR_HealthRecord)
				.append(" where empiId = :empiId)").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		try {
			List<Map<String, Object>> list = dao.doQuery(hql, parameters);
			if (list != null && list.size() > 0) {
				Map<String, Object> map = list.get(0);
				String cdhDoctor = (String) map.get("cdhDoctor");
				return cdhDoctor;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取儿童儿保医生失败", e);
		}
		return null;
	}

	/**
	 * 根据empiId获取儿童个人档案信息
	 * 
	 * @param empiId
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getManageChildHealthCardByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("ne", "status", "s",
				Constants.CODE_STATUS_WRITE_OFF);
		List<?> cnd3 = CNDHelper.createSimpleCnd("eq", "endManageFlag", "s",
				YesNo.NO);
		List<?> cnd4 = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd3, cnd4);

		Map<String, Object> healthcard;
		try {
			healthcard = dao.doLoad(cnd, CDH_HealthCard);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取儿童档案信息失败", e);
		}
		return healthcard;
	}

	/**
	 * 根据empiId获取儿童个人档案信息(单表查询)
	 * 
	 * @param empiId
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getChildHealthCardByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		Map<String, Object> healthcard;
		try {
			healthcard = dao.doLoad(cnd, CDH_HealthCard);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取儿童档案信息失败", e);
		}
		return healthcard;
	}

	/**
	 * 根据empiId获取儿童个人档案信息(连接查询)
	 * 
	 * @param empiId
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getChildHealthCardByEmpiIdJoin(String empiId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s", empiId);
		try {
			List<?> list = dao.doList(cnd, null, CDH_HealthCard);
			if (list == null || list.size() < 1) {
				return null;
			} else {
				return (Map<String, Object>) list.get(0);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取儿童档案信息失败", e);
		}
	}

	/**
	 * 根据empiId查找一份正常状态的儿童档案
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getNormalHealthCardByEmpiId(String empiId)
			throws ModelDataOperationException {
		Map<String, Object> rec = getChildHealthCardByEmpiId(empiId);
		if (rec == null) {
			return null;
		}
		String status = (String) rec.get("status");
		if (RecordStatus.NOMAL.equals(status)) {
			return rec;
		}
		return null;
	}

	/**
	 * 根据phrId获取儿童个人档案信息
	 * 
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getChildHealthCardByPhrId(String phrId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "phrId", "s", phrId);
		Map<String, Object> healthcard;
		try {
			healthcard = dao.doLoad(cnd, CDH_HealthCard);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取儿童档案信息失败", e);
		}
		return healthcard;
	}

	/**
	 * 根据phrId查找一份正常状态的儿童档案
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getNormalHealthCardByPhrId(String phrId)
			throws ModelDataOperationException {
		Map<String, Object> rec = getChildHealthCardByPhrId(phrId);
		String status = rec == null ? "" : (String) rec.get("status");
		if (RecordStatus.NOMAL.equals(status)) {
			return rec;
		}
		return null;
	}

	/**
	 * 获取儿童健康档案状态
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getChildHealthCardStatus(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select status as status from ")
				.append(CDH_HealthCard).append(" where empiId=:empiId")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		try {
			Map<String, Object> map = dao.doLoad(hql, parameters);
			if (map == null || map.size() < 1) {
				return null;
			} else {
				return (String) map.get("status");
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取儿童档案状态失败", e);
		}
	}

	/**
	 * 获取儿童出生缺陷记录
	 * 
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getChildDefectRecord(String phrId)
			throws ModelDataOperationException {
		try {
			return dao.doLoad(CDH_DefectRegister, phrId);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取儿童出生缺陷记录失败", e);
		}
	}

	/**
	 * 根据母亲empiId获取儿童出生缺陷记录
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getDefectByMotherEmpiId(String empiId)
			throws ModelDataOperationException {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "motherEmpiId", "s",
				empiId);
		try {
			List<Map<String, Object>> defects = dao.doQuery(cnd, null,
					CDH_DeadRegister);
			if (defects == null || defects.size() < 1) {
				return null;
			} else {
				return defects;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取儿童出生缺陷记录失败", e);
		}
	}

	/**
	 * 保存儿童健康档案信息
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveChildHealthCard(Map<String, Object> data, String op,
			Map<String, Object> res) throws ValidateException,
			ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op, CDH_HealthCard,
					data, true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存儿童档案信息失败", e);
		}
	}

	/**
	 * 更新儿童档案中的残疾情况到儿童疑似残疾报告卡中
	 * 
	 * @param phrId
	 * @param deformity
	 * @return Integer 0为没有残疾报告卡，1为与残疾报告卡中的数据冲突，2为不需更新与报告卡内容一致，3为更新成功
	 * @throws ModelDataOperationException
	 */
	public Integer updateDisabilityMonitor(String phrId, String deformity)
			throws ModelDataOperationException {
		try {
			Map<String, Object> monitor = dao.doLoad(CDH_DisabilityMonitor,
					phrId);
			if (monitor == null || monitor.size() < 1) { // ** 没有残疾报告卡
				return 0;
			}
			String disabilityType = (String) monitor.get("disabilityType");
			if (deformity.equals(disabilityType)) { // ** 不需更新，内容一致
				return 2;
			} else {
				if (deformity.equals(PastHistoryCode.PASTHIS_NOTDEFORMITY_CODE)) { // **
																					// 数据冲突
					return 1;
				} else {
					String hql = new StringBuffer("update ")
							.append("CDH_DisabilityMonitor")
							.append(" set disabilityType =:disabilityType ")
							.append("where phrId =:phrId ").toString();
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("disabilityType", deformity);
					parameters.put("phrId", phrId);
					dao.doUpdate(hql, parameters);
					return 3; // ** 更新成功
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "跟新疑似残疾儿童报告卡信息失败", e);
		}
	}

	/**
	 * 通过empiId更新个人既往史残疾情况到儿童疑似残疾报告卡中
	 * 
	 * @param parameters
	 * @throws ModelDataOperationException
	 */
	public void updateDisabilityMonitorByEmpiId(Map<String, Object> parameters)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append("CDH_DisabilityMonitor")
				.append(" set disabilityType=:deformityKey")
				.append(" where empiId =:empiId").toString();
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "跟新疑似残疾儿童报告卡信息失败！", e);
		}
	}

	/**
	 * 保存儿童死亡登记信息(有档案)
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveChildrenDeadRecord(Map<String, Object> data, String op,
			Map<String, Object> res) throws ValidateException,
			ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op, CDH_DeadRegister,
					data, true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存儿童死亡登记信息失败", e);
		}
	}

	/**
	 * 保存儿童死亡登记信息(无档案)
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveChildrenDeadRecordNH(Map<String, Object> data, String op,
			Map<String, Object> res) throws ValidateException,
			ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op, CDH_DeadRegisterNh,
					data, true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存儿童死亡登记信息失败", e);
		}
	}
	/**
	 * 删除某条儿童死亡记录
	 * 
	 * @param deadRegisterId
	 * @throws ModelDataOperationException
	 */
	public void removeChildrenDeadRecord(String deadRegisterId)
			throws ModelDataOperationException {
		String hql = new StringBuilder("delete from ").append(CDH_DeadRegister)
				.append(" where deadRegisterId = :deadRegisterId ").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("deadRegisterId", deadRegisterId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除儿童死亡记录失败", e);
		}
	}

	/**
	 * 删除某个儿童的死亡记录
	 * 
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void deleteChildrenDeadRecord(String phrId)
			throws ModelDataOperationException {
		String hql = new StringBuilder("delete from ").append(CDH_DeadRegister)
				.append(" where phrId = :phrId ").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phrId", phrId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除儿童死亡记录失败", e);
		}
	}

	/**
	 * 检测儿童是否死亡
	 * 
	 * @param empiId
	 * @param session
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean isDeadRecordExistsByIdCard(String idCard)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(*) as num from ")
				.append(CDH_DeadRegister).append(" where idCard = :idCard")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idCard", idCard);
		boolean recordExists = false;
		try {
			Map<String, Object> map = dao.doLoad(hql, parameters);
			long c = (Long) map.get("num");
			if (c > 0) {
				recordExists = true;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询儿童否存死亡失败", e);
		}
		return recordExists;
	}

	/**
	 * 检测儿童是否存在死亡记录
	 * 
	 * @param empiId
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> isDeadRecordExistsByEmpiId(String empiId)
			throws ModelDataOperationException {
		Map<String, Object> resBody = new HashMap<String, Object>();
		boolean hasDeadRecord = false;
		try {
			ArrayList<Object> cnd = (ArrayList<Object>) CNDHelper
					.createSimpleCnd("eq", "empiId", "s", empiId);
			List<Map<String, Object>> data = dao.doQuery(cnd, null,
					CDH_DeadRegister);
			if (data == null || data.size() < 1) {
				hasDeadRecord = false;
				ArrayList<Object> cnd1 = (ArrayList<Object>) CNDHelper
						.createSimpleCnd("eq", "empiId", "s", empiId);
				ArrayList<Object> cnd2 = (ArrayList<Object>) CNDHelper
						.createSimpleCnd("eq", "status", "s", "0");
				ArrayList<Object> cnds = (ArrayList<Object>) CNDHelper
						.createArrayCnd("and", cnd1, cnd2);
				try {
					List<Map<String, Object>> result = dao.doQuery(cnds, null,
							CDH_HealthCard);
					if (result == null || result.size() < 1) {
						resBody.put("hasHealthCard", false);
					} else {
						resBody.put("hasHealthCard", true);
					}
				} catch (PersistentDataOperationException e) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "获取儿童档案失败", e);
				}
			} else {
				Map<String, Object> map = data.get(0);
				resBody.put("deadRegisterId", map.get("deadRegisterId"));
				hasDeadRecord = true;
			}
			resBody.put("hasDeadRecord", hasDeadRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "监测儿童是否存在死亡记录失败", e);
		}
		return resBody;
	}

	/**
	 * 检查死亡编号是否重复
	 * 
	 * @param empiId
	 * @param deadNo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean checkDeadNo(String empiId, String deadNo)
			throws ModelDataOperationException {
		StringBuffer sb = new StringBuffer(
				"select  deadRegisterId as deadRegisterId from ")
				.append(CDH_DeadRegister);
		sb.append(" where (empiId != :empiId or empiId is null) and deadNo = :deadNo");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);
		param.put("deadNo", deadNo);
		try {
			List<Map<String, Object>> list = dao.doQuery(sb.toString(), param);
			if (list != null && list.size() > 0) {
				return true;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "检查死亡编号是否重复失败", e);
		}
		return false;
	}
	/**
	 * 根据询问记录号（inquireId）获取儿童询问记录
	 * 
	 * @param inquireId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getChildInquireRecordByInquirId(String inquireId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "inquireId", "s",
				inquireId);
		Map<String, Object> inquireReocrd;
		try {
			inquireReocrd = dao.doLoad(cnd, CDH_Inquire);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取儿童询问记录失败", e);
		}
		return inquireReocrd;
	}
	/**
	 * 保存儿童询问信息
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveChildInquireRecord(Map<String, Object> data, String op,
			Map<String, Object> res) throws ValidateException,
			ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op, CDH_Inquire, data,
					false);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存儿童询问信息失败", e);
		}
	}

	/**
	 * 保存儿童意外情况
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveChildAccidentRecord(Map<String, Object> data, String op,
			Map<String, Object> res) throws ValidateException,
			ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op, CDH_Accident, data,
					true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存儿童意外情况失败", e);
		}
	}

	/**
	 * 保存儿童缺陷监测记录
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveDefectChildrenRecord(Map<String, Object> data, String op,
			Map<String, Object> res) throws ValidateException,
			ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op, CDH_DefectRegister,
					data, true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存儿童缺陷监测记录失败", e);
		}
	}

	/**
	 * 保存儿童缺陷监测记录
	 * 
	 * @param data
	 * @param op
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveDefectChildrenRecord(Map<String, Object> data, String op)
			throws ValidateException, ModelDataOperationException {
		try {
			dao.doSave(op, CDH_DefectRegister, data, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存儿童缺陷监测记录失败", e);
		}
	}

	/**
	 * 保存疑似残疾儿童报告卡
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveDisabilityMonitorRecord(Map<String, Object> data,
			String op, Map<String, Object> res) throws ValidateException,
			ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op,
					CDH_DisabilityMonitor, data, true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存疑似残疾儿童报告卡失败", e);
		}
	}

	/**
	 * 删除儿童缺陷记录
	 * 
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void deleteDefectChildrenRecord(String phrId)
			throws ModelDataOperationException {
		String delhql = new StringBuilder("delete from ")
				.append(CDH_DefectRegister).append(" where phrId = :phrId")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phrId", phrId);
		try {
			dao.doUpdate(delhql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "儿童缺陷记录删除失败", e);
		}
	}

	/**
	 * 获取儿童母亲基本档案信息。
	 * 
	 * @param motherEmpiId
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public Map<String, Object> getChildMotherMess(String motherEmpiId)
			throws ModelDataOperationException, ValidateException {
		String mpiHql = new StringBuffer(
				"select personName as motherName , zipCode as postCode, educationCode as literacy, empiId as motherEmpiId from ")
				.append(MPI_DemographicInfo).append(" where empiId = :empiId")
				.toString();
		String healthHql = new StringBuffer(
				"select regionCode_text as regionCode_text from ")
				.append(EHR_HealthRecord).append(" where empiId = :empiId")
				.toString();
		String womenHql = new StringBuffer("select   gravidity as gravidity, ")
				.append("vaginalDelivery as vaginalDelivery,  abdominalDelivery as abdominalDelivery from ")
				.append(MHC_PregnantRecord).append(" where empiId = :empiId")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", motherEmpiId);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Map<String, Object> mpiMap = dao.doLoad(mpiHql, parameters);
			if (mpiMap == null || mpiMap.size() < 1) {
				return null;
			}
			map.putAll(mpiMap);
			Map<String, Object> healthMap = dao.doLoad(healthHql, parameters);
			if (healthMap != null && healthMap.size() > 0) {
				map.put("postalAddress", healthMap.get("regionCode_text"));
			}
			Map<String, Object> womenMap = dao.doLoad(womenHql, parameters);
			Map<String, Object> women = SchemaUtil.setDictionaryMessageForList(
					womenMap, MHC_PregnantRecord);
			if (women != null && women.size() > 0) {
				map.put("pregnancyTimes", women.get("gravidity"));
				Integer vaginalDelivery = (Integer) SchemaUtil.getValue(
						MHC_PregnantRecord, "vaginalDelivery",
						women.get("vaginalDelivery"));
				Integer abdominalDelivery = (Integer) SchemaUtil.getValue(
						MHC_PregnantRecord, "abdominalDelivery",
						women.get("abdominalDelivery"));
				int birthTimes = vaginalDelivery + abdominalDelivery;
				map.put("birthTimes", birthTimes);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取母亲基本信息失败", e);
		}
		return map;
	}

	/**
	 * 保存儿童三周岁小结信息
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveChildrenOneYearSummary(Map<String, Object> data, String op,
			Map<String, Object> res) throws ValidateException,
			ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op, CDH_OneYearSummary,
					data, true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存儿童三周岁小结信息失败", e);
		}
	}

	/**
	 * 儿童档案结案
	 * 
	 * @param data
	 * @throws ModelDataOperationException
	 */
	public void endHealthCard(Map<String, Object> data)
			throws ModelDataOperationException {
		Date sd = BSCHISUtil.toDate((String) data.get("summaryDate"));
		Date emDate = null;
		if (sd != null) {
			emDate = sd;
		} else {
			emDate = new Date();
		}
		String emDoctor = StringUtils.trimToEmpty((String) data
				.get("summaryDoctor"));
		String emUnit = StringUtils.trimToEmpty((String) data
				.get("summaryUnit"));
		String userId = UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ")
				.append("CDH_HealthCard")
				.append(" set endManageDate=cast(:endManageDate as date),")
				.append(" endManageDoctor=:endManageDoctor, ")
				.append("endManageUnit=:endManageUnit,")
				.append(" endManageFlag=:endManageFlag,lastModifyUser=:lastModifyUser,lastModifyDate=:lastModifyDate")
				.append(" where phrId=:phrId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("endManageDate", emDate);
		parameters.put("endManageDoctor", emDoctor);
		parameters.put("endManageUnit", emUnit);
		parameters.put("endManageFlag", YesNo.YES);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("phrId", (String) data.get("phrId"));

		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "儿童档案结案失败", e);
		}
	}

	/**
	 * 修改儿童随访计划
	 * 
	 * @param birthday
	 * @param precedeDays
	 * @param delayDays
	 * @param childrenFirstVistDays
	 * @param empiId
	 * @throws ModelDataOperationException
	 * @throws ControllerException
	 * @throws NumberFormatException
	 */
	public void changeChildPlan(String birthday, String empiId)
			throws ModelDataOperationException, NumberFormatException,
			ControllerException {
		int childrenFirstVistDays = Integer.valueOf(ApplicationUtil
				.getProperty(Constants.UTIL_APP_ID, "childrenFirstVistDays"));
		int precedeDays = Integer.valueOf(ApplicationUtil.getProperty(
				Constants.UTIL_APP_ID, "_precedeDays"));
		int delayDays = Integer.valueOf(ApplicationUtil.getProperty(
				Constants.UTIL_APP_ID, "_delayDays"));
		// User user = (User) dao.getContext().get("user.instance");
		UserRoleToken ur = UserRoleToken.getCurrent();
		Map<String, Object> baseParam = new HashMap<String, Object>();
		baseParam.put("birthday", BSCHISUtil.toDate(birthday));
		baseParam.put("lastModifyUser", ur.getUserId());
		baseParam.put("lastModifyUnit", ur.getManageUnitId());
		baseParam.put("lastModifyDate", new Date());
		baseParam.put("businessType1", BusinessType.CD_IQ);
		baseParam.put("businessType2", BusinessType.CD_CU);
		baseParam.put("empiId", empiId);
		try {
			StringBuilder sb1 = new StringBuilder(" update ")
					.append("PUB_VisitPlan")
					.append(" set beginDate = sum_day2(:birthday,")
					.append(childrenFirstVistDays - precedeDays)
					.append(") , ")
					.append("endDate = sum_day2(:birthday, ")
					.append(childrenFirstVistDays + delayDays)
					.append(") , ")
					.append("planDate = sum_day2(:birthday, ")
					.append(childrenFirstVistDays)
					.append(") ,lastModifyUser = :lastModifyUser,")
					.append("lastModifyDate = :lastModifyDate ,lastModifyUnit =:lastModifyUnit  where empiId = :empiId ")
					.append(" and  extend1 < :extend1 and businessType in (:businessType1,:businessType2)");
			Map<String, Object> param1 = new HashMap<String, Object>(baseParam);
			param1.put("extend1", 2);
			dao.doUpdate(sb1.toString(), param1);
			StringBuilder sb2 = new StringBuilder(" update ")
					.append("PUB_VisitPlan")
					.append(" set beginDate =sum_day( sum_month(:birthday,extend1)+")
					.append(-precedeDays)
					.append(") ,")
					.append("endDate = sum_day(sum_month(:birthday,extend1)+")
					.append(delayDays)
					.append("), ")
					.append("planDate = sum_month( :birthday,extend1) ,lastModifyUser = :lastModifyUser ,")
					.append(" lastModifyDate = :lastModifyDate,lastModifyUnit = :lastModifyUnit where empiId = :empiId ")
					.append(" and  extend1 > :extend1  and businessType in (:businessType1,:businessType2)");
			Map<String, Object> param2 = new HashMap<String, Object>(baseParam);
			param2.put("extend1", 1);
			dao.doUpdate(sb2.toString(), param2);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "修改儿童随访计划信息失败!", e);
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
	public void logOutHealthCardRecord(String whereField, String whereValue,
			String cancellationReason, String deadReason)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ").append("CDH_HealthCard")
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ").append(" where ")
				.append(whereField).append(" = :whereValue")
				.append("  and  status = :normal");

		String userId = UserRoleToken.getCurrent().getUserId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("normal", Constants.CODE_STATUS_NORMAL);
		parameters.put("status", Constants.CODE_STATUS_WRITE_OFF);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("cancellationUser", userId);
		parameters.put("cancellationDate", new Date());
		parameters.put("cancellationUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
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
	 * 恢复儿童健康档案
	 * 
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void revertHealthCardRecord(String phrId)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ").append("CDH_HealthCard")
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
					ServiceCode.CODE_DATABASE_ERROR, "恢复儿童档案失败！", e);
		}
	}

	/**
	 * 
	 * @Description:儿童档案结案
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-5-22 下午1:21:13
	 * @Modify:
	 */
	public int endManageCHR(String pkey) throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append(CDH_HealthCard)
				.append(" set endManageFlag = 'y'")
				.append(" where phrId = :phrId").toString();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("phrId", pkey);
		int upr = 0;
		try {
			upr = dao.doUpdate(hql, paraMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "儿童档案结案操作失败！", e);
		}
		return upr;
	}

	/**
	 * 查看是否创建儿童档案
	 * 
	 * @param empiId
	 * @param extend1
	 * @param businessType
	 * @param planStatus
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean isCreateHealthCard(Map<String, Object> param)
			throws ModelDataOperationException {
		StringBuffer sb = new StringBuffer();
		sb.append(" select empiId as empiId from " + CDH_HealthCard).append(
				" where phrId=:phrId and empiId=:empiId ");
		String empiId = null;
		try {
			Map<String, Object> mapHealthCard = dao
					.doLoad(sb.toString(), param);
			if (mapHealthCard != null && !mapHealthCard.isEmpty()) {
				empiId = (String) mapHealthCard.get("empiId");
				if (empiId != null && empiId.length() > 0) {
					return true;
				}
				return false;
			}
			return false;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取儿童档案失败失败!", e);
		}
	}

	/**
	 * 保存新生儿视访的基本信息
	 * 
	 * @param param
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public Map<String, Object> saveNewChildrenVistInfo(Map<String, Object> param)
			throws ModelDataOperationException, ValidateException {
		Map<String, Object> data = null;
		String babyId = (String) param.get("babyId");
		try {
			if (babyId != null && babyId.length() > 0) {
				data = dao.doSave("update", MHC_BabyVisitInfo, param, false);
			} else {
				data = dao.doSave("create", MHC_BabyVisitInfo, param, false);
			}
			return data;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存新生儿视访的基本信息失败", e);
		}

	}

	/**
	 * 保存新生儿视访的记录
	 * 
	 * @param param
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public Map<String, Object> saveNewChildrenVistRecord(
			Map<String, Object> param) throws ModelDataOperationException,
			ValidateException {
		Map<String, Object> data = null;
		String visitId = (String) param.get("visitId");
		try {
			if (visitId != null && visitId.length() > 0) {
				data = dao.doSave("update", MHC_BabyVisitRecord, param, false);
			} else {
				data = dao.doSave("create", MHC_BabyVisitRecord, param, false);
			}
			return data;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存新生儿视访的记录失败", e);
		}

	}

	/**
	 * 根据母亲的身份证查询新生儿视访的基本信息
	 * 
	 * @param param
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public List<Map<String, Object>> getNewChildrenVistRecord(
			Map<String, Object> param) throws ModelDataOperationException,
			ValidateException {
		List<Map<String, Object>> data = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append(
					" select babyId as babyId,certificateNo as certificateNo , babyAddress as babyAddress"
							+ ",fatherName as fatherName,fatherJob as fatherJob,fatherPhone as fatherPhone,fatherBirth as fatherBirth,"
							+ "motherName as motherName,motherJob as motherJob ,motherPhone as motherPhone,motherBirth as motherBirth , gestation as gestation,"
							+ " pregnancyDisease as pregnancyDisease ,otherDisease as otherDisease,deliveryUnit as deliveryUnit,"
							+ "birthStatus as birthStatus,otherStatus as otherStatus ,asphyxia as asphyxia,malforMation as malforMation,hearingTest as hearingTest,"
							+ "illnessScreening as illnessScreening ,otherIllness as otherIllness ,weight as weight,length as length"
							+ " from " + MHC_BabyVisitInfo).append(
					" where motherCardNo=:motherCardNo ");
			// data = dao.doLoad(sb.toString(), param);
			data = dao.doQuery(sb.toString(), param);
			if (data != null && data.size() > 0) {
				return data;
			}
			return null;

		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取新生儿访视基本信息失败", e);
		}

	}

	/**
	 * 
	 * @Description:获取新生儿的信息（特定的方法）
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author
	 * @throws PersistentDataOperationException
	 * @throws ModelDataOperationException
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> getHealthCard(Map<String, Object> m)
			throws ServiceException, PersistentDataOperationException,
			ModelDataOperationException {

		ChildrenHealthModel chModel = new ChildrenHealthModel(dao);
		List<String> parameters = new ArrayList<String>();
		Map<String, Object> fData = null;
		parameters.add("gestation");// 出生孕周期
		parameters.add("apgar1");// 评分标准
		parameters.add("apgar5");
		parameters.add("birthWeight");// 出生体重
		parameters.add("birthHeight");// 出生身长
		parameters.add("boneCondition");// 出生情况
		parameters.add("otherBone");// 其他出生情况
		parameters.add("fatherEmpiId");
		parameters.add("motherEmpiId");
		

		try {
			fData = chModel
					.getValuesByParameters(CDH_HealthCard, parameters, m);
			if (fData != null && !fData.isEmpty()) {
				// 将返回的数据改成 MHC_BabyVisitInfo表的字段
				fData.put("weight", fData.get("birthWeight"));
				fData.put("length", fData.get("birthHeight"));
//				String apgar1 = (String) fData.get("apgar1");
//				String apgar5 = (String) fData.get("apgar5");
				fData.remove("birthWeight");
				fData.remove("birthHeight");
				fData.put("birthStatus", fData.get("boneCondition"));
				fData.put("otherStatus", fData.get("otherBone"));
			}
			if (fData != null && !fData.isEmpty()) {
				return fData;
			}
			return null;
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}

	}

	/**
	 * 根据传入的字段(单个)和schemas，where后面就一个条件获取该值，
	 * 
	 * @param
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public String getValueByParameters(String entryName, String parameters,
			Map<String, Object> data) throws ModelDataOperationException,
			PersistentDataOperationException {
		Set<String> set = data.keySet();
		Iterator<String> iterator = set.iterator();
		String s = null;
		String V = null;
		while (iterator.hasNext()) {
			s = iterator.next();
		}
		StringBuffer sb = new StringBuffer();
		sb.append("select ").append(parameters).append(" as " + parameters)
				.append(" from " + entryName).append(" where " + s + "=:" + s);
		Map<String, Object> mapInfo = dao.doLoad(sb.toString(), data);
		if (mapInfo != null && mapInfo.size() > 0) {
			V = (String) mapInfo.get(parameters);
			if (V != null && V.length() > 0) {
				return V; // null+""返回 null
			}
			return "";
		}
		return "";

	}

	/**
	 * 根据多个(一个)条件以及多个(一个)查询的字段对单表查数据
	 * 
	 * @param
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> getValuesByParameters(String entryName,
			List<String> parameters, Map<String, Object> data)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		StringBuffer sb = new StringBuffer(" select ");
		String s = null;
		// for (String s : parameters) {
		// sb.append(s + " as " + s + ",");
		// }
		int len = parameters.size();
		for (int i = 0; i < len; i++) {
			s = parameters.get(i);
			if (i != len - 1) {
				sb.append(s + " as " + s + ",");
			} else {
				sb.append(s + " as " + s + " ");
			}
		}
		sb.append(" from " + entryName);
		Set<String> set = data.keySet();
		Iterator<String> iterator = set.iterator();

		while (iterator.hasNext()) {
			s = iterator.next();
			sb.append(" where " + s + " =:" + s + " and ");

		}
		try {
			Map<String, Object> mapInfo = dao.doLoad(
					sb.substring(0, sb.length() - 4), data);
			if (mapInfo != null && mapInfo.size() > 0) {
				return mapInfo;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取" + entryName + "信息失败",
					e);
		}
		return null;
	}

	/**
	 * 查询新生儿访视的基本信息
	 * 
	 * @param param
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public Map<String, Object> getChildrenVisitInfo(Map<String, Object> param)
			throws ModelDataOperationException, ValidateException {
		Map<String, Object> data = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append(
					" select babyId as babyId,certificateNo as certificateNo , babyAddress as babyAddress"
							+ ",fatherName as fatherName,fatherJob as fatherJob,fatherPhone as fatherPhone,fatherBirth as fatherBirth,"
							+ "motherName as motherName,motherJob as motherJob ,motherPhone as motherPhone,motherBirth as motherBirth , gestation as gestation,"
							+ " pregnancyDisease as pregnancyDisease ,malforMationDescription as malforMationDescription,otherDisease as otherDisease,deliveryUnit as deliveryUnit,apgar1 as apgar1,apgar5 as apgar5 ,"
							+ "birthStatus as birthStatus,otherStatus as otherStatus ,asphyxia as asphyxia,malforMation as malforMation,hearingTest as hearingTest,"
							+ "illnessScreening as illnessScreening ,otherIllness as otherIllness ,weight as weight,length as length"
							+ " from " + CDH_ChildVisitInfo).append(
					" where empiId=:empiId ");

			data = dao.doLoad(sb.toString(), param);
			if (data != null && data.size() > 0) {
				return data;
			}
			return null;

		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取新生儿访视基本信息失败", e);
		}

	}

	// 获取新生儿的 出生证、 身份证 、母亲身份证
	@SuppressWarnings("unchecked")
	protected Map<String, Object> getInfoNumber(Map<String, Object> req)
			throws ModelDataOperationException {
		Map<String, Object> data = null;
		Map<String, Object> mData = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		try {
			sb.append(" select idCard as idCard  from ")
					.append(MPI_DemographicInfo)
					.append(" where empiId=:empiId ");
			data = dao.doLoad(sb.toString(), req);
			if (data != null && data.size() > 0) {
				mData.put("idCard", data.get("idCard"));
			}

		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取新生儿身份证信息失败!", e);
		}
		try {
			// MPI_ChildInfo 母亲身份证号 出生证号
			sb.setLength(0);
			sb.append(
					" select relativeIdCard as relativeIdCard ,certificateNo as certificateNo from ")
					.append(MPI_ChildInfo)
					.append(" where childEmpiId=:empiId ");

			data = dao.doLoad(sb.toString(), req);
			if (data != null && data.size() > 0) {
				mData.put("relativeIdCard", data.get("relativeIdCard"));
				mData.put("certificateNo", data.get("certificateNo"));
			}

		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取新生儿出生证和母亲身份证信息失败!", e);
		}
		
		return mData;
	}//

	/**
	 * 查询新生儿访视的基本信息
	 * 
	 * @param param
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public Map<String, Object> getGetFullRecord(Map<String, Object> param)
			throws ModelDataOperationException, ValidateException {
		StringBuffer sbRecord = new StringBuffer();
		StringBuffer sbInfo = new StringBuffer();
		Map<String, Object> dataRercord = new HashMap<String, Object>();
		Map<String, Object> dataInfo = new HashMap<String, Object>();
		String babyId = null;
		try {
			sbRecord.append("select  visitId as visitId , weightNow as weightNow,analAbnormal as analAbnormal,babyId as babyId,visitDate as visitDate,feedWay as feedWay ,eatNum as eatNum ,eatCount as eatCount,vomit as vomit,"
					+ " stoolStatus as stoolStatus,stoolTimes as stoolTimes,temperature as temperature,pulse as pulse,"
					+ " respiratoryFrequency as respiratoryFrequency,face as face,faceOther as faceOther,otherStatus1 as otherStatus1,jaundice as jaundice,bregmaTransverse"
					+ " as bregmaTransverse,bregmaLongitudinal as bregmaLongitudinal,bregmaStatus as bregmaStatus,"
					+ " eye as eye,eyeAbnormal as eyeAbnormal,ear as ear,earAbnormal as earAbnormal,"
					+ " nose as nose,noseAbnormal as noseAbnormal ,mouse as mouse,mouseAbnormal as mouseAbnormal,anal as anal,"
					+ " heartlung as heartlung,heartLungAbnormal as heartLungAbnormal,genitalia as genitalia,genitaliaAbnormal as genitaliaAbnormal,"
					+ " spine as spine,spineAbnormal as spineAbnormal,umbilical as umbilical,umbilicalOther as umbilicalOther,"
					+ " abdominal as abdominal,abdominalabnormal as abdominalabnormal ,referral as referral,"
					+ " referralReason as referralReason,referralUnit as referralUnit ,guide as guide,visitDate as visitDate,"
					+ " nextVisitDate as nextVisitDate,nextVisitAddress as nextVisitAddress,visitDoctor as visitDoctor ,"
					+ " limbs as limbs,limbsAbnormal as limbsAbnormal,neck as neck,neck1 as neck1,skin as skin ,skinAbnormal as skinAbnormal from CDH_ChildVisitRecord"
					+ "  where visitId=:visitId and visitDate=to_date(:visitDate,'YYYY-MM-DD') ");
			dataRercord = dao.doLoad(sbRecord.toString(), param);
			if (dataRercord != null && dataRercord.size() > 0) {
				babyId = (String) dataRercord.get("babyId");
				param.clear();
				param.put("babyId", babyId);

			}
			//
			sbInfo.append(
					" select motherEmpiId as motherEmpiId,fatherEmpiId as fatherEmpiId,babyId as babyId,certificateNo as certificateNo , babyAddress as babyAddress"
							+ ",fatherName as fatherName,fatherJob as fatherJob,fatherPhone as fatherPhone,fatherBirth as fatherBirth,"
							+ "motherName as motherName,motherJob as motherJob ,motherPhone as motherPhone,motherBirth as motherBirth , gestation as gestation,"
							+ " pregnancyDisease as pregnancyDisease ,malforMationDescription as malforMationDescription,otherDisease as otherDisease,deliveryUnit as deliveryUnit,apgar1 as apgar1,apgar5 as apgar5 ,"
							+ "birthStatus as birthStatus,otherStatus as otherStatus ,asphyxia as asphyxia,malforMation as malforMation,hearingTest as hearingTest,"
							+ "illnessScreening as illnessScreening ,otherIllness as otherIllness ,weight as weight,length as length"
							+ " from " + CDH_ChildVisitInfo).append(
					" where babyId=:babyId ");

			dataInfo = dao.doLoad(sbInfo.toString(), param);
			if (dataInfo != null && dataInfo.size() > 0) {
				dataRercord.putAll(dataInfo);
			}
			return dataRercord;

		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取新生儿访视基本信息以及记录失败", e);
		}

	}

	/**
	 * 根据empiId 查询babyId
	 * 
	 * @param
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> getBabyIdByEmpiId(String empiId)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> data=new HashMap<String,Object>();
		Map<String, Object> resBoby = null;
		StringBuffer sb = new StringBuffer();
		data.put("empiId", empiId);
		sb.append(" select babyId as babyId from " + CDH_ChildVisitInfo
				+ "  where empiId=:empiId ");
		resBoby = dao.doLoad(sb.toString(), data);
		if (resBoby != null && resBoby.size() > 0) {
			return resBoby;
		}
		return null;

	}//
	/**
	 * 根据babyId and visitDate 查询visitId
	 * 
	 * @param
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> getVisitIdByIdAndDate(String visitDate,String babyId)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> data=new HashMap<String,Object>();
		Map<String, Object> resBoby = null;
		StringBuffer sb = new StringBuffer();
		data.put("babyId", babyId);
		data.put("visitDate", visitDate);
		sb.append(" select visitId as visitId from " + CDH_ChildVisitRecord
				+ "  where babyId=:babyId and visitDate=to_date(:visitDate,'YYYY-MM-DD') ");
		resBoby = dao.doLoad(sb.toString(), data);
		if (resBoby != null && resBoby.size() > 0) {
			return resBoby;
		}
		return null;

	}
}
