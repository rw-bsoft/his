/**
 * @(#)CDHMoveService.java Created on 2012-5-31 下午4:49:33
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.mov;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.conf.BizColumnConfig;
import chis.source.control.ControlRunner;
import chis.source.dic.AffirmType;
import chis.source.dic.ArchiveMoveStatus;
import chis.source.dic.ArchiveType;
import chis.source.dic.WorkType;
import chis.source.dic.YesNo;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import chis.source.worklist.WorkListModel;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class ManaInfoChangeService extends AbstractActionService implements
		DAOSupportable {

	private static final Logger logger = LoggerFactory
			.getLogger(ManaInfoChangeService.class);

	// ** 档案类型
	private static Map<String, String> entryMap = new HashMap<String, String>();
	static {
		entryMap.put("1", EHR_HealthRecord);
		entryMap.put("3", MDC_HypertensionRecord);
		entryMap.put("4", MDC_DiabetesRecord);
		entryMap.put("7", PSY_PsychosisRecord);
		entryMap.put("8", MDC_TumourPatientReportCard);
		entryMap.put("9", MDC_OldPeopleRecord);
		entryMap.put("10", DEF_IntellectDeformityRecord);
		entryMap.put("11", DEF_BrainDeformityRecord);
		entryMap.put("12", DEF_LimbDeformityRecord);
		entryMap.put("13", RVC_RetiredVeteranCadresRecord);
		entryMap.put("14", MDC_TumourHighRisk);

	}

	// ** 个人档案需要修改的表
	private static List<String> healthRecordList = new ArrayList<String>();
	static {
		healthRecordList.add(EHR_HealthRecord);
		healthRecordList.add(PIV_VaccinateRecord);
		healthRecordList.add(CDH_HealthCard);
		healthRecordList.add(MHC_PregnantRecord);
		healthRecordList.add(MHC_WomanRecord);
	}

	// ** 高血压档案需要修改的表
	private static List<String> hypertensionRecordList = new ArrayList<String>();
	static {
		hypertensionRecordList.add(MDC_HypertensionRecord);
		hypertensionRecordList.add(MDC_HypertensionVisit);
	}

	// ** 糖尿病档案需要修改的表
	private static List<String> diabetesRecordList = new ArrayList<String>();
	static {
		diabetesRecordList.add(MDC_DiabetesRecord);
	}

	// ** 老年人档案需要修改的表
	private static List<String> oldPeopleList = new ArrayList<String>();
	static {
		oldPeopleList.add(MDC_OldPeopleRecord);
		oldPeopleList.add(MDC_OldPeopleVisit);
	}

	// ** 精神病档案需要修改的表
	private static List<String> psychosisRecordList = new ArrayList<String>();
	static {
		psychosisRecordList.add(PSY_PsychosisRecord);
	}

	// ** 肢体残疾档案需要修改的表
	private static List<String> intellectDeformityRecordList = new ArrayList<String>();
	static {
		psychosisRecordList.add(DEF_IntellectDeformityRecord);
	}

	// ** 脑瘫残疾档案需要修改的表
	private static List<String> brainDeformityRecordList = new ArrayList<String>();
	static {
		psychosisRecordList.add(DEF_BrainDeformityRecord);
	}

	// ** 智力残疾档案需要修改的表
	private static List<String> limbDeformityRecordList = new ArrayList<String>();
	static {
		psychosisRecordList.add(DEF_LimbDeformityRecord);
	}

	// ** 离休干部档案需要修改的表
	private static List<String> retiredVeteranCadresRecordList = new ArrayList<String>();
	static {
		retiredVeteranCadresRecordList.add(RVC_RetiredVeteranCadresRecord);
	}

	// ** 肿瘤档案需要修改的表
	private static List<String> tumourPatientRecordList = new ArrayList<String>();
	static {
		tumourPatientRecordList.add(MDC_TumourPatientReportCard);
	}

	// ** 肿瘤高危档案需要修改的表
	private static List<String> tumourHighRiskRecordList = new ArrayList<String>();
	static {
		tumourPatientRecordList.add(MDC_TumourHighRisk);
	}

	/**
	 * 获取用户所有档案信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doGetPeopleAllRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = (String) req.get("empiId");
		String manageUnit = null;
		String curManageUnit = UserUtil.get(UserUtil.MANAUNIT_ID);
//		if (curManageUnit.length() > 9) {
//			manageUnit = curManageUnit.substring(0, 6);
//		} else {
//			manageUnit = curManageUnit;
//		}
		manageUnit = curManageUnit.substring(0, 6);
		ManaInfoChangeModule mcm = new ManaInfoChangeModule(dao);
		try {
			List<Map<String, Object>> subRecord = new ArrayList<Map<String, Object>>();
			for (String recordType : entryMap.keySet()) {
				String entryName = entryMap.get(recordType);
				List<Map<String, Object>> recordList = mcm.getPeopleRecords(
						entryName, empiId, manageUnit);
				for (Map<String, Object> record : recordList) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("empiId", record.get("empiId"));
					map.put("manaDoctorId", record.get("manaDoctorId"));
					map.put("manaUnitId", record.get("manaUnitId"));
					map.put("createUnit", record.get("createUnit"));
					map.put("createUser", record.get("createUser"));
					map.put("createDate", record.get("createDate"));
					map.put("status", record.get("status"));
					map.put("recordType", recordType);
					map.put("recordId", record.get("phrId"));
					subRecord.add(SchemaUtil.setDictionaryMessageForList(map,
							MOV_PeopleRecordsQuery));
				}
			}
			res.put("body", subRecord);
		} catch (ModelDataOperationException e) {
			logger.error("get people all records failed.", e);
			throw new ServiceException(e);
		}

	}

	/**
	 * 保存修改管理医生申请记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveApplyRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> data = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String, Object> formData = (Map<String, Object>) data
				.get("formData");
		List<Map<String, Object>> listData = (List<Map<String, Object>>) data
				.get("listData");
		List<String> delDetaiId = (List<String>) data.get("delDetaiId");
		if (formData == null || formData.size() < 1) {
			return;
		}
		formData.put("status", ArchiveMoveStatus.NEEDCONFIRM);
		formData.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		formData.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		formData.put("lastModifyDate", new Date());
		ManaInfoChangeModule mcm = new ManaInfoChangeModule(dao);
		try {
			mcm.saveMoveRecord(formData, op, MOV_ManaInfoChangeApply, res);
			String archiveMoveId = (String) formData.get("archiveMoveId");
			if ("create".equals(archiveMoveId)) {
				Map<String, Object> rsMap = (Map<String, Object>) res
						.get("body");
				archiveMoveId = (String) rsMap.get("archiveMoveId");
			}
			vLogService.saveVindicateLog(MOV_ManaInfoChange, op, archiveMoveId,
					dao);
			if (listData == null || listData.size() < 1) {
				return;
			}
			for (Map<String, Object> map : listData) {
				String detailId = (String) map.get("detailId");
				String detailOp = "update";
				if (detailId == null || "".equals(detailId)) {
					detailOp = "create";
					if (archiveMoveId == null || "".equals(archiveMoveId)) {
						Map<String, Object> resBody = (Map<String, Object>) res
								.get("body");
						archiveMoveId = (String) resBody.get("archiveMoveId");
					}
					map.put("archiveMoveId", archiveMoveId);
				}
				map.put("affirmType", YesNo.NO);
				mcm.saveMoveRecordDetail(map, detailOp);
			}
			if (delDetaiId != null && delDetaiId.size() > 0) {
				for (String detailId : delDetaiId) {
					mcm.removeMoveRecordDetailByPkey(detailId);
				}
			}
		} catch (ModelDataOperationException e) {
			logger.error("save  move apply record failed.", e);
			throw new ServiceException(e);
		}
		// ** 任务提醒相关
		WorkListModel wlm = new WorkListModel(dao);
		try {
			if (op.equals("create")) {
				Map<String, Object> resBody = (Map<String, Object>) res
						.get("body");
				Map<String, Object> workList = new HashMap<String, Object>();
				workList.put("recordId", resBody.get("archiveMoveId"));
				workList.put("workType", WorkType.MOV_MANAGEINFO_CONFIRM);
				workList.put("manaUnitId", getConfirmUnit(formData));
				wlm.insertWorkListRecord(workList, false);
			} else {
				wlm.updateWorkListManageUnit(getConfirmUnit(formData),
						(String) formData.get("archiveMoveId"),
						WorkType.MOV_MANAGEINFO_CONFIRM);
			}
		} catch (ModelDataOperationException e) {
			logger.error("save work list  record failed.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存修改管理医生退回记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveRejectRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> data = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String, Object> formData = (Map<String, Object>) data
				.get("formData");
		formData.put("status", ArchiveMoveStatus.CANCEL);
		formData.put("affirmType", AffirmType.CANCEL);
		formData.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		formData.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		formData.put("lastModifyDate", new Date());
		ManaInfoChangeModule mcm = new ManaInfoChangeModule(dao);
		try {
			mcm.saveMoveRecord(formData, op, MOV_ManaInfoChangeConfirm, res);
		} catch (ModelDataOperationException e) {
			logger.error("save  move reject record failed.", e);
			throw new ServiceException(e);
		}
		String archiveMoveId = (String) formData.get("archiveMoveId");
		vLogService.saveVindicateLog(MOV_ManaInfoChange, "6", archiveMoveId,
				dao);
		// ** 任务提醒相关
		removeWorkList(formData, dao);
	}

	/**
	 * 保存修改管理医生确认记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveConfirmRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> data = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String, Object> formData = (Map<String, Object>) data
				.get("formData");
		List<Map<String, Object>> listData = (List<Map<String, Object>>) data
				.get("listData");
		if (formData == null || formData.size() < 1) {
			return;
		}
		formData.put("status", ArchiveMoveStatus.CONFIRM);
		formData.put("affirmType", AffirmType.CONFIRM);
		formData.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		formData.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		formData.put("lastModifyDate", new Date());
		ManaInfoChangeModule mcm = new ManaInfoChangeModule(dao);
		try {
			mcm.saveMoveRecord(formData, op, MOV_ManaInfoChangeConfirm, res);
			String archiveMoveId = (String) formData.get("archiveMoveId");
			vLogService.saveVindicateLog(MOV_ManaInfoChange, "6",
					archiveMoveId, dao);
			if (listData == null || listData.size() < 1) {
				return;
			}
			for (Map<String, Object> map : listData) {
				String detailOp = "update";
				map.put("affirmType", YesNo.YES);
				mcm.saveMoveRecordDetail(map, detailOp);
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("archiveId", map.get("archiveId"));
				params.put("manaUnitId", map.get("targetUnit"));
				params.put("manaDoctorId", map.get("targetDoctor"));
				params.put("lastModifyUser", formData.get("lastModifyUser"));
				params.put("lastModifyUnit", formData.get("lastModifyUnit"));
				params.put("lastModifyDate", formData.get("lastModifyDate"));
				String archiveType = (String) map.get("archiveType");
				List<String> updateList = null;
				// ** 迁移个人档案 同时迁移儿童孕妇档案
				if (archiveType.equals(ArchiveType.HEALTH_ARCHIVE)) {
					updateList = healthRecordList;
					params.put("empiId", map.get("empiId"));
				}
				// ** 迁移高血压档案
				else if (archiveType.equals(ArchiveType.HYPERTENSION_ARCHIVE)) {
					updateList = hypertensionRecordList;
				}
				// ** 迁移糖尿病档案
				else if (archiveType.equals(ArchiveType.DIABETES_ARCHIVE)) {
					updateList = diabetesRecordList;
				}
				// ** 迁移老年人档案
				else if (archiveType.equals(ArchiveType.OLDPEOPLE_ARCHIVE)) {
					updateList = oldPeopleList;
				}
				// ** 迁移精神病档案
				else if (archiveType.equals(ArchiveType.PSYCHOSIS_ARCHIVE)) {
					updateList = psychosisRecordList;
				}
				// ** 迁移肢体残疾档案
				else if (archiveType.equals(ArchiveType.INTELLECTDEF_ARCHIVE)) {
					updateList = intellectDeformityRecordList;
					params.put("closeFlag", YesNo.NO);
					params.put("status", Constants.CODE_STATUS_NORMAL);
				}
				// ** 迁移脑瘫残疾档案
				else if (archiveType.equals(ArchiveType.BRAINDEF_ARCHIVE)) {
					updateList = brainDeformityRecordList;
					params.put("closeFlag", YesNo.NO);
					params.put("status", Constants.CODE_STATUS_NORMAL);
				}
				// ** 迁移智力残疾档案
				else if (archiveType.equals(ArchiveType.LIMBDEF_ARCHIVE)) {
					updateList = limbDeformityRecordList;
					params.put("closeFlag", YesNo.NO);
					params.put("status", Constants.CODE_STATUS_NORMAL);
				}
				// ** 迁移离休干部档案
				else if (archiveType.equals(ArchiveType.RVC_ARCHIVE)) {
					updateList = retiredVeteranCadresRecordList;
				}
				// ** 迁移肿瘤档案
				else if (archiveType.equals(ArchiveType.TUMOUR_ARCHIVE)) {
					updateList = tumourPatientRecordList;
				}
				// ** 迁移肿瘤高危档案
				else if (archiveType.equals(ArchiveType.TUMOURHIGH_ARCHIVE)) {
					updateList = tumourHighRiskRecordList;
				}
				mcm.moveRecord(BizColumnConfig.MOV_EHR_SUB, params, updateList);
				// 迁移修改过的表-记录日志
				try {
					List<String> tList = BizColumnConfig
							.getUpdateTable(BizColumnConfig.MOV_EHR_SUB);
					for (String tn : tList) {
						vLogService.saveVindicateLog(tn, "m", null, dao);
					}
					if (updateList != null) {
						for (String table : updateList) {
							vLogService.saveVindicateLog(table, "m", null, dao);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (DocumentException e) {
					e.printStackTrace();
				}
			}
		} catch (ModelDataOperationException e) {
			logger.error("save  move confirm record failed.", e);
			throw new ServiceException(e);
		}
		// ** 任务提醒相关
		removeWorkList(formData, dao);
	}

	/**
	 * 获取修改管理医生申请记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetMoveRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String pkey = (String) body.get("pkey");
		ManaInfoChangeModule mcm = new ManaInfoChangeModule(dao);
		Map<String, Object> formData = null;
		try {
			formData = mcm.getMoveRecord(pkey);
		} catch (ModelDataOperationException e) {
			logger.error("failed to get  move record.", e);
			throw new ServiceException(e);
		}
		if (formData == null || formData.size() < 1) {
			return;
		}
		HashMap<String, Object> result = new HashMap<String, Object>();
		for (Iterator<String> keys = formData.keySet().iterator(); keys
				.hasNext();) {
			String key = (String) keys.next();
			Object value = formData.get(key);
			if (value != null) {
				result.put(key, value);
			}
		}
		Map<String, Object> resBody = new HashMap<String, Object>();
		Map<String, Boolean> controls = ControlRunner.run(
				MOV_ManaInfoChangeConfirm, result, ctx, ControlRunner.ALL);
		resBody.put("_actions", controls);
		res.put("body", resBody);
		Map<String, Object> formBody = SchemaUtil.setDictionaryMessageForForm(
				result, MOV_ManaInfoChangeConfirm);
		formBody.remove("affirmUser");
		formBody.remove("affirmUnit");
		resBody.put("formData", formBody);
		try {
			List<Map<String, Object>> detailList = mcm
					.getMoveRecordDetail(pkey);
			if (detailList == null || detailList.size() < 1) {
				return;
			}
			List<Map<String, Object>> listBody = SchemaUtil
					.setDictionaryMessageForList(detailList,
							MOV_ManaInfoBatchChangeDetail);
			resBody.put("listData", listBody);
		} catch (ModelDataOperationException e) {
			logger.error("failed to get  move record detail.", e);
			throw new ServiceException(e);
		}

	}

	/**
	 * 删除修改管理医生申请记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doRemoveMoveRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String pkey = (String) body.get("pkey");
		ManaInfoChangeModule mcm = new ManaInfoChangeModule(dao);
		try {
			mcm.removeMoveRecord(pkey);
			mcm.removeMoveRecordDetail(pkey);
		} catch (ModelDataOperationException e) {
			logger.error("failed to remove  move record .", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MOV_ManaInfoChange, "4", pkey, dao);
	}

	/**
	 * 删除修改管理医生申请明细记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doRemoveMoveRecordDetail(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		ArrayList<String> pkey = (ArrayList<String>) body.get("pkey");
		ManaInfoChangeModule mcm = new ManaInfoChangeModule(dao);
		try {
			for (String key : pkey) {
				mcm.removeMoveRecordDetailByPkey(key);
			}
		} catch (ModelDataOperationException e) {
			logger.error("failed to remove  move record .", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 删除工作任务提醒
	 * 
	 * @param applyData
	 * @param dao
	 * @throws ServiceException
	 */
	private void removeWorkList(Map<String, Object> applyData, BaseDAO dao)
			throws ServiceException {
		WorkListModel wlm = new WorkListModel(dao);
		try {
			wlm.deleteWorkList((String) applyData.get("archiveMoveId"),
					WorkType.MOV_MANAGEINFO_CONFIRM);
		} catch (ModelDataOperationException e) {
			logger.error("delete work list  record failed.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取申请记录的确认机构
	 * 
	 * @param applyData
	 * @return
	 */
	private String getConfirmUnit(Map<String, Object> applyData) {
		String confirmUnit = (String) applyData.get("applyUnit");
		// ** 确认机构为防保科长的管辖机构到中心
		if (confirmUnit.length() > 9) {
			confirmUnit = confirmUnit.substring(0, 9);
		}
		return confirmUnit;
	}
	//
	protected void doSaveConfirmareagridRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException {
		Map<String, Object> data = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		if (data == null || data.size() < 1) {
			return;
		}
		data.put("status", ArchiveMoveStatus.CONFIRM);
		data.put("affirmType", AffirmType.CONFIRM);
		data.put("affirmUser", UserUtil.get(UserUtil.USER_ID));
		data.put("affirmUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		data.put("affirmDate", new Date());
		data.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		data.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		data.put("lastModifyDate", new Date());
		try {
			dao.doSave(op, MOV_Manachangebyareagrid, data, true);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存按网格地址修改责任医生记录失败", e);
		}
		String changeareagrid =(String)data.get("changeareagrid");
		String targetDoctor =(String)data.get("targetDoctor");
		String targetUnit =(String)data.get("targetUnit");
		if(changeareagrid.length()<12){
			return;
		}
		if(changeareagrid!=null && targetDoctor!=null && targetUnit!=null){
		Map<String, Object> parameters= new HashMap<String,Object>();
		parameters.put("changeareagrid", changeareagrid+"%");
		parameters.put("targetDoctor", targetDoctor);
		parameters.put("targetUnit", targetUnit);
		//更新高血压档案
		StringBuffer uphypertensionrecord=new StringBuffer().append(
				"update MDC_HypertensionRecord a  set a.manaDoctorId=:targetDoctor ,a.manaUnitId=:targetUnit ").append(
				" where a.empiId in (select b.empiId from EHR_HealthRecord b ").append(  
				" where  b.regionCode in (select c.regionCode from EHR_AreaGrid c where  c.regionCode like :changeareagrid ))");  
		try {
			dao.doUpdate(uphypertensionrecord.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//更新家庭档案
		StringBuffer upfamilyrecord=new StringBuffer().append(
		"update EHR_FamilyRecord a  set a.manaDoctorId=:targetDoctor ,a.manaUnitId=:targetUnit ")
		.append(" where a.regionCode in (select c.regionCode from EHR_AreaGrid c where  c.regionCode like :changeareagrid ))"
				);
		
		try {
			dao.doUpdate(upfamilyrecord.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		//更新老年人档案
		StringBuffer upoldpeoplerecord=new StringBuffer().append(
				"update MDC_OldPeopleRecord a  set a.manaDoctorId=:targetDoctor ,a.manaUnitId=:targetUnit ").append(
				" where a.empiId in (select b.empiId from EHR_HealthRecord b ").append(
				" where  b.regionCode in (select c.regionCode from EHR_AreaGrid c where  c.regionCode like :changeareagrid ))");  
		try {
			dao.doUpdate(upoldpeoplerecord.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//更新糖尿病档案
		StringBuffer updiabetesrecord=new StringBuffer().append(
				"update MDC_DiabetesRecord a  set a.manaDoctorId=:targetDoctor ,a.manaUnitId=:targetUnit ").append(
				" where a.empiId in (select b.empiId from EHR_HealthRecord b ").append(
				" where  b.regionCode in (select c.regionCode from EHR_AreaGrid c where   c.regionCode like :changeareagrid ))");
		try {
			dao.doUpdate(updiabetesrecord.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//更新健康档案
		StringBuffer uphealthrecord=new StringBuffer().append(
				"update  EHR_HealthRecord a set a.manaDoctorId=:targetDoctor ,a.manaUnitId=:targetUnit ").append(
				" where  a.regionCode in (select c.regionCode from EHR_AreaGrid c where  c.regionCode like :changeareagrid )");
		try {
			dao.doUpdate(uphealthrecord.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
	}
	protected void doSaveRejectareagridRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException {
		Map<String, Object> data = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		if (data == null || data.size() < 1) {
			return;
		}
		data.put("status", ArchiveMoveStatus.CANCEL);
		data.put("affirmType", AffirmType.CANCEL);
		data.put("affirmUser", UserUtil.get(UserUtil.USER_ID));
		data.put("affirmUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		data.put("affirmDate", new Date());
		data.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		data.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		data.put("lastModifyDate", new Date());
		try {
			dao.doSave(op, MOV_Manachangebyareagrid, data, true);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存按网格地址修改责任医生记录失败", e);
		}
	}
	
}
