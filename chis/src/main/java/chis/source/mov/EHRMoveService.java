/**
 * @(#)EHRMoveService.java Created on 2012-5-22 上午9:35:23
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mov;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.admin.AreaGridModel;
import chis.source.admin.SystemUserModel;
import chis.source.conf.BizColumnConfig;
import chis.source.control.ControlRunner;
import chis.source.dic.AffirmType;
import chis.source.dic.ArchiveType;
import chis.source.dic.MoveType;
import chis.source.dic.RolesList;
import chis.source.dic.WorkType;
import chis.source.dic.YesNo;
import chis.source.fhr.FamilyRecordModule;
import chis.source.phr.HealthRecordModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import chis.source.worklist.WorkListModel;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 个档迁移只与个档有关，家庭档案迁移时只迁移其成员的个档，妇保，儿保档案 除外与其他子档无关
 * @@妇保，儿保档案责任医生是随健康档案走的，所在儿保、妇保档案在个档迁移时对应的要修改责任医生和管辖机构
 * @@子档如果要修改责任医生和网格地址里批量修改或各档修改里去做
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class EHRMoveService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(EHRMoveService.class);

	/**
	 * 加载表单初始数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetMoveEHR(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		String entryName = (String) req.get("schema");
		String pkey = (String) req.get("pkey");
		EHRMoveModule ehrModule = new EHRMoveModule(dao);
		Map<String, Object> ehrMap = null;
		try {
			ehrMap = ehrModule.getMoveEHR(entryName, pkey);
		} catch (ModelDataOperationException e) {
			logger.error("Get MOV_EHR record failed.", e);
			throw new ServiceException(e);
		}

		if (ehrMap == null) {
			logger.error("Get MOV_HER Data failed.");
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"加载档案迁移记录失败，请重试！");
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		// 获取-表单按钮权限控制
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("archiveMoveId", ehrMap.get("archiveMoveId"));
		body.put("applyUser", ehrMap.get("applyUser"));
		body.put("status", ehrMap.get("status"));
		body.put("sourceUnit", ehrMap.get("sourceUnit"));
		body.put("targetUnit", ehrMap.get("targetUnit"));
		body.put("moveType", ehrMap.get("moveType"));
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		try {
			data = ControlRunner.run(MOV_EHRConfirm, body, ctx,
					ControlRunner.UPDATE, "confirm");
		} catch (ServiceException e) {
			logger.error("check ehr move record control error .", e);
			throw e;
		}
		// 数据表单化
		resBodyMap = SchemaUtil.setDictionaryMessageForForm(ehrMap, entryName);
		resBodyMap.put("_actions", data);
		res.put("body", resBodyMap);

	}

	/**
	 * 保存家庭/健康档案迁移*申请
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveApplyEHRMove(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		EHRMoveModule ehrMoveModule = new EHRMoveModule(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = ehrMoveModule.saveEHRMoveApply(op, reqBodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save ehr move apply failed.", e);
			throw new ServiceException(e);
		}
		String archiveMoveId = (String) reqBodyMap.get("archiveMoveId");
		if ("create".equals(op)) {
			archiveMoveId = (String) rsMap.get("archiveMoveId");
		}
		vLogService.saveVindicateLog(MOV_EHR, op, archiveMoveId, dao);
		res.put("body", rsMap);
		// ** 任务提醒相关
		WorkListModel wlm = new WorkListModel(dao);
		try {
			if (op.equals("create")) {
				Map<String, Object> resBody = (Map<String, Object>) res
						.get("body");
				Map<String, Object> workList = new HashMap<String, Object>();
				workList.put("recordId", resBody.get("archiveMoveId"));
				workList.put("workType", WorkType.MOV_EHR_CONFIRM);
				workList.put("manaUnitId", getConfirmUnit(reqBodyMap));
				wlm.insertWorkListRecord(workList, false);
			} else {
				wlm.updateWorkListManageUnit(getConfirmUnit(reqBodyMap),
						(String) reqBodyMap.get("archiveMoveId"),
						WorkType.MOV_EHR_CONFIRM);
			}
		} catch (ModelDataOperationException e) {
			logger.error("save work list  record failed.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 档案迁移确认*个人健康档案迁移
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveConfirmEHRMove(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		String affirmType = StringUtils.trimToEmpty((String) reqBodyMap
				.get("affirmType"));
		String archiveType = StringUtils.trimToEmpty((String) reqBodyMap
				.get("archiveType"));
		if (AffirmType.CONFIRM.equals(affirmType)) {
			if (ArchiveType.HEALTH_ARCHIVE.equals(archiveType)) {
				this.moveHealthRecord(req, res, dao, ctx);// 迁移个人健康档案
			} else if (ArchiveType.FAMILY_ARCHIVE.equals(archiveType)) {
				this.moveFamilyRecord(req, res, dao, ctx);// 迁移家庭健康
			}
		}
		EHRMoveModule ehrMoveModule = new EHRMoveModule(dao);
		try {
			resBodyMap = ehrMoveModule.saveEHRMoveConfirm("update", reqBodyMap,
					true);
		} catch (ModelDataOperationException e) {
			logger.error("update mov_ehr rcord failed.", e);
			throw new ServiceException(e);
		}
		String archiveMoveId = (String) reqBodyMap.get("archiveMoveId");
		vLogService.saveVindicateLog(MOV_EHR, "6", archiveMoveId, dao);
		res.put("body", resBodyMap);
		// ** 任务提醒相关
		removeWorkList(reqBodyMap, dao);
	}

	/*
	 * **个人健康档案 迁移
	 */
	@SuppressWarnings("unchecked")
	private void moveHealthRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String targetArea = (String) reqBodyMap.get("targetArea");
		String targetArea_text = (String) reqBodyMap.get("targetArea_text");
		String targetUnit = (String) reqBodyMap.get("targetUnit");
		String targetDoctor = (String) reqBodyMap.get("targetDoctor");
		String archiveID = (String) reqBodyMap.get("archiveId");
		String sourceArea = (String) reqBodyMap.get("sourceArea");

		EHRMoveModule ehrMoveModule = new EHRMoveModule(dao);
		// 判断迁移档案是否已经注销
		boolean isCanelEHR = false;
		try {
			isCanelEHR = ehrMoveModule.isCanelEHR(archiveID);
		} catch (ModelDataOperationException e) {
			logger.error("Judge the ehr is canel failed.", e);
			throw new ServiceException(e);
		}
		if (isCanelEHR) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"该档案已经注销！请先恢复再操作");
		}

		// 如果是户主,清空其他成员的与户主关系。
		boolean isheadOfAHousehold = false;
		try {
			isheadOfAHousehold = ehrMoveModule.isHeadOfAHousehold(archiveID);
		} catch (ModelDataOperationException e) {
			logger.error("Judge the proper of record is head of a household", e);
			throw new ServiceException(e);
		}
		if (isheadOfAHousehold) {
			try {
				// 户主迁出,清楚剩余成员与户主关系.
				ehrMoveModule.clearRelaCode(sourceArea);
				vLogService.saveVindicateLog(EHR_HealthRecord, "m", archiveID,
						dao);
				// 清除原家庭档案户主姓名。
				ehrMoveModule.clearMasterName(sourceArea);
				vLogService.saveVindicateLog(EHR_FamilyRecord, "m", null, dao);
			} catch (ModelDataOperationException e) {
				logger.error("Clear the head of a household relation.", e);
				throw new ServiceException(e);
			}
		}
		// 查目标网格地址上的家庭档案ID
		String familyId = "";
		FamilyRecordModule frModule = new FamilyRecordModule(dao);
		try {
			familyId = frModule.getFamilyIdByRegionCode(targetArea);
		} catch (ModelDataOperationException e) {
			logger.error("Get target regionCode family ID failed.", e);
			throw new ServiceException(e);
		}
		// 处理个人主要问题
		/* 先删除个人问题在原家庭档案中的记录,然后在目标家庭档案（如果有）中增加该人的问题 */
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		Map<String, Object> hrMap = null;
		try {
			hrMap = hrModel.getHealthRecordByPhrId(archiveID);
		} catch (ModelDataOperationException e) {
			logger.error("Get empiId failed.", e);
			throw new ServiceException(e);
		}
		if (hrMap != null && hrMap.size() > 0) {
			String oldFamilyId = (String) hrMap.get("familyId");
			String empiId = (String) hrMap.get("empiId");
			if (StringUtils.isNotEmpty(oldFamilyId)) {
				List<Map<String, Object>> ppList = null;
				try {
					ppList = ehrMoveModule.getPersonProblemByEmpiId(empiId);
				} catch (ModelDataOperationException e) {
					logger.error("Get person problem info failed.", e);
					throw new ServiceException(e);
				}
				for (int i = 0; i < ppList.size(); i++) {
					Map<String, Object> ppMap = ppList.get(i);
					String familyProblemId = (String) ppMap
							.get("familyProblemId");
					if (StringUtils.isNotEmpty(familyId)) {
						// 将家庭问题移到新家庭的家庭问题里
						try {
							ehrMoveModule.moveFamilyProblem(familyProblemId,
									familyId);
							vLogService.saveVindicateLog(EHR_FamilyProblem,
									"m", familyProblemId, dao);
						} catch (ModelDataOperationException e) {
							logger.error("move family problem to " + familyId
									+ " failed.", e);
							throw new ServiceException(e);
						}
					} else {
						// 删除家庭问题中的个人问题
						try {
							frModule.deleteFamilyProblem("familyProblemId",
									familyProblemId);
						} catch (ModelDataOperationException e) {
							logger.error(
									"Delete family problem by familyProblemId = ["
											+ familyProblemId + "] failed.", e);
							throw new ServiceException(e);
						}
						vLogService.saveVindicateLog(EHR_FamilyProblem, "m",
								familyProblemId, dao);
					}
				}
			}
		}

		// 迁移健康档案
		try {
			ehrMoveModule.moveHealthRcord(targetArea, targetArea_text,
					targetUnit, targetDoctor, familyId, archiveID);
			vLogService.saveVindicateLog("EHR_HealthRecord", "m", archiveID,
					dao);
		} catch (ModelDataOperationException e) {
			logger.error("Move health record failed.", e);
			throw new ServiceException(e);
		}

		// 档案迁移同时改变子档的管理机构和责任医生
		try {
			String movesub = (String) reqBodyMap.get("movesub");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("archiveId", archiveID);
			params.put("manaUnitId", targetUnit);
			params.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
			params.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
			params.put("lastModifyDate", new Date());
			params.put("manaDoctorId", targetDoctor);
			params.put("closeFlag", YesNo.NO);
			params.put("status", Constants.CODE_STATUS_NORMAL);
			String updateFile = BizColumnConfig.MOV_EHR_SUB;
			List<String> updateList = null;
			if (movesub != null && movesub.equals(YesNo.NO)) {
				updateList = new ArrayList<String>();
				updateList.add(CDH_HealthCard);
				updateList.add(MHC_PregnantRecord);
				updateList.add(MHC_WomanRecord);
				updateList.add(PIV_VaccinateRecord);
				HealthRecordModel hm = new HealthRecordModel(dao);
				Map<String, Object> map = hm.getHealthRecordByPhrId(archiveID);
				params.put("empiId", map.get("empiId"));
			}
			ManaInfoBatchChangeModule mbcm = new ManaInfoBatchChangeModule(dao);
			mbcm.moveRecord(updateFile, params, updateList);
			// this.saveChangeSubRecord(archiveID, targetUnit, targetDoctor,
			// dao, ctx);
		} catch (ModelDataOperationException e) {
			logger.error("Update manaUnit or manaDoctor of sub record failed.",
					e);
			throw new ServiceException(e);
		}
	}

	/*
	 * **家庭档案迁移
	 */
	@SuppressWarnings("unchecked")
	private void moveFamilyRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String targetArea = (String) reqBodyMap.get("targetArea");
		String targetArea_text = (String) reqBodyMap.get("targetArea_text");
		String targetUnit = (String) reqBodyMap.get("targetUnit");
		String targetDoctor = (String) reqBodyMap.get("targetDoctor");
		String archiveID = (String) reqBodyMap.get("archiveId");
		String targetFamilyId = StringUtils.trimToEmpty((String) reqBodyMap
				.get("existFamily"));

		String userId = UserUtil.get(UserUtil.USER_ID);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("familyId", archiveID);
		parameters.put("regionCode", targetArea);
		parameters.put("regionCode_text", targetArea_text);
		parameters.put("manaUnitId", targetUnit);
		parameters.put("manaDoctorId", targetDoctor);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());

		EHRMoveModule ehrMoveModule = new EHRMoveModule(dao);
		FamilyRecordModule frmModule = new FamilyRecordModule(dao);
		Map<String, Object> frmMap = null;
		try {
			frmMap = frmModule.getFamilyRecordById(archiveID);
		} catch (ModelDataOperationException e) {
			logger.error("Get family reocrd failed.", e);
			throw new ServiceException(e);
		}
		if (frmMap == null || (frmMap != null && frmMap.size() == 0)) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"家庭档案已经不存在！");
		}
		// 查询所有成员的PHRID oh no ...
		List<Map<String, Object>> mbList = null;
		try {
			mbList = ehrMoveModule.getFamilyMember(archiveID);
			vLogService.saveVindicateLog(EHR_HealthRecord, "m", null, dao);
		} catch (ModelDataOperationException e) {
			logger.error("Get phrId of family member failed.", e);
			throw new ServiceException(e);
		}
		if (targetFamilyId != null && targetFamilyId.length() > 0) {
			// 删除原来的家庭
			try {
				frmModule.deleteFamilyRecord(archiveID);
				vLogService.saveVindicateLog(EHR_FamilyRecord, "m", archiveID,
						dao);
			} catch (ModelDataOperationException e) {
				logger.error("Delete old family record failed.", e);
				throw new ServiceException(e);
			}
			// 把原来的家庭问题归到新的家庭中去.
			try {
				ehrMoveModule.mergerFamilyToNew(targetFamilyId, archiveID);
				vLogService.saveVindicateLog(EHR_FamilyProblem, "m", null, dao);
			} catch (ModelDataOperationException e) {
				logger.error("Merger old family to target family failed.", e);
				throw new ServiceException(e);
			}
			// 修改成员的家庭编号、网格地址
			parameters.put("relaCode", null);
			// parameters.put("manaUnitId", null);
			parameters.put("targetFamilyId", targetFamilyId);
			try {
				ehrMoveModule.executeUpdateSQL("EHR_HealthRecord", parameters);
				vLogService.saveVindicateLog(EHR_HealthRecord, "m", null, dao);
			} catch (ModelDataOperationException e) {
				logger.error("Update familyId and regionCode of Family member "
						+ "failed.", e);
				throw new ServiceException(e);
			}
		} else {
			// 修改原家庭网格地址
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("regionCode", targetArea);
			paramMap.put("regionCode_text", targetArea_text);
			paramMap.put("manaUnitId", targetUnit);
			paramMap.put("manaDoctorId", targetDoctor);
			paramMap.put("familyId", archiveID);
			paramMap.put("lastModifyUser", userId);
			paramMap.put("lastModifyDate", new Date());
			try {
				ehrMoveModule.executeUpdateSQL("EHR_FamilyRecord", paramMap);
				vLogService.saveVindicateLog(EHR_FamilyRecord, "m", archiveID,
						dao);
			} catch (ModelDataOperationException e) {
				logger.error("Update old family regiconCode failed.", e);
				throw new ServiceException(e);
			}
			// 修改成员网格地址
			parameters.remove("relaCode");
			// parameters.remove("manaUnitId");
			parameters.put("targetFamilyId", archiveID);
			try {
				ehrMoveModule.executeUpdateSQL("EHR_HealthRecord", parameters);
				vLogService.saveVindicateLog(EHR_HealthRecord, "m", null, dao);
			} catch (ModelDataOperationException e) {
				logger.error("Update regiconCode of family member failed.", e);
				throw new ServiceException(e);
			}
		}

		// 档案迁移同时改变子档的管理机构和责任医生
		String movesub = (String) reqBodyMap.get("movesub");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("manaUnitId", targetUnit);
		params.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		params.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		params.put("lastModifyDate", new Date());
		params.put("manaDoctorId", targetDoctor);
		params.put("closeFlag", YesNo.NO);
		params.put("status", Constants.CODE_STATUS_NORMAL);
		String updateFile = BizColumnConfig.MOV_EHR_SUB;
		List<String> updateList = null;
		if (movesub != null && movesub.equals(YesNo.YES)) {
			updateList = new ArrayList<String>();
			updateList.add(EHR_HealthRecord);
			updateList.add(CDH_HealthCard);
			updateList.add(MHC_PregnantRecord);
			updateList.add(MHC_WomanRecord);
			updateList.add(PIV_VaccinateRecord);
		}
		for (int i = 0; i < mbList.size(); i++) {
			Map<String, Object> mbMap = mbList.get(i);
			String phrId = (String) mbMap.get("phrId");
			params.put("archiveId", phrId);
			try {
				if (movesub == null || movesub.equals(YesNo.NO)) {
					HealthRecordModel hm = new HealthRecordModel(dao);
					Map<String, Object> map = hm.getHealthRecordByPhrId(phrId);
					params.put("empiId", map.get("empiId"));
				}
				ManaInfoBatchChangeModule mbcm = new ManaInfoBatchChangeModule(
						dao);
				mbcm.moveRecord(updateFile, params, updateList);
				// saveChangeSubRecord(phrId, targetUnit, targetDoctor, dao,
				// ctx);
			} catch (ModelDataOperationException e) {
				logger.error(
						"Update manaUnit or manaDoctor of sub record failed.",
						e);
				throw new ServiceException(e);
			}
		}
	}

	/*
	 * 档案迁移同时改变子档的管理机构和责任医生
	 * 
	 * @param phrId
	 * 
	 * @param manaUnitId
	 * 
	 * @param manaDoctorId
	 * 
	 * @throws ModelDataOperationException
	 */
	private void saveChangeSubRecord(String phrId, String manaUnitId,
			String manaDoctorId, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ServiceException {
		String[] schemaArray = new String[] { CDH_HealthCard,
				MHC_PregnantRecord };

		// 责任医生、管辖机构都要修改
		String[] manaArray = new String[] { CDH_HealthCard };

		// 需要修改责任医生的
		String[] manaDoctorArray = new String[] { MHC_PregnantRecord };

		// 需要修改管辖机构的
		String[] manaUnitArray = new String[] {};

		String userId = UserUtil.get(UserUtil.USER_ID);
		for (String schema : schemaArray) {
			String op = "update";
			EHRMoveModule ehrMoveModule = new EHRMoveModule(dao);
			List<Map<String, Object>> records = null;
			records = ehrMoveModule.getSubRecordList(schema, phrId);
			if (records == null || records.size() == 0) {
				continue;
			}
			List<Map<String, Object>> rlist = new ArrayList<Map<String, Object>>();
			// 修改责任医生和管辖机构
			if (ifContain(manaArray, schema)) {
				for (int i = 0; i < records.size(); i++) {
					Map<String, Object> rec = (Map<String, Object>) records
							.get(i);
					rec.put("manaDoctorId", manaDoctorId);
					rec.put("manaUnitId", manaUnitId);
					rec.put("lastModifyUser", userId);
					rec.put("lastModifyDate", BSCHISUtil.toString(new Date()));
					rec.put("lastModifyUnit",
							UserUtil.get(UserUtil.MANAUNIT_ID));
					rlist.add(rec);
				}
			}
			// 修改责任医生 * 加数据
			if (ifContain(manaDoctorArray, schema)) {
				for (int i = 0; i < records.size(); i++) {
					Map<String, Object> rec1 = (Map<String, Object>) records
							.get(i);
					rec1.put("manaDoctorId", manaDoctorId);
					rec1.put("lastModifyUser", userId);
					rec1.put("lastModifyDate", BSCHISUtil.toString(new Date()));
					rec1.put("lastModifyUnit",
							UserUtil.get(UserUtil.MANAUNIT_ID));
					rlist.add(rec1);
				}
			}
			// 修改管辖机构 * 加数据
			if (ifContain(manaUnitArray, schema)) {
				for (int i = 0; i < records.size(); i++) {
					if (schema.equals(SCH_SchistospmaRecord)
							|| schema.equals(DC_RabiesRecord)) {
						if (manaUnitId != null && manaUnitId.length() >= 9) {
							manaUnitId = manaUnitId.substring(0, 9);
						}
					}
					Map<String, Object> rec2 = (Map<String, Object>) records
							.get(i);
					rec2.put("manaUnitId", manaUnitId);
					rec2.put("lastModifyUser", userId);
					rec2.put("lastModifyDate", BSCHISUtil.toString(new Date()));
					rec2.put("lastModifyUnit",
							UserUtil.get(UserUtil.MANAUNIT_ID));
					rlist.add(rec2);
				}
			}

			// 保存子档数据更新*修改*新增
			for (int i = 0; i < rlist.size(); i++) {
				ehrMoveModule.saveSubRecord(op, schema, rlist.get(i), false);
				vLogService.saveVindicateLog(schema, "m", phrId, dao);
			}
		}
	}

	private static boolean ifContain(String[] array, String val) {
		for (String v : array) {
			if (v.equals(val))
				return true;
		}
		return false;
	}

	/**
	 * 删除个人健康档案或家庭档案迁移申请或作废记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveEHRMoveRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String archiveMoveId = (String) reqBody.get("archiveMoveId");
		EHRMoveModule ehrMoveModule = new EHRMoveModule(dao);
		try {
			ehrMoveModule.delEHRMoveRecord(archiveMoveId);
		} catch (ModelDataOperationException e) {
			logger.error("Delete EHR_MOVE recrod failed.", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MOV_EHR, "4", archiveMoveId, dao);
		// 删除任务提示中数据
		// ** 任务提醒相关
		removeWorkList(reqBody, dao);
	}

	/**
	 * 根据网格地址编码获取责任医生、团队长信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 *            ) @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetManaDoctorInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String regionCode = (String) body.get("regionCode");
		res.put("body", body);
		try {
			AreaGridModel agModel = new AreaGridModel(dao);
			Map<String, Object> nodeMap = agModel.getNodeInfo(regionCode);
			body.put("nodeMap", nodeMap);
			if (nodeMap == null) {
				return;
			}
			String manaDoctor = (String) nodeMap.get("manaDoctor");
			body.putAll(SchemaUtil.setDictionaryMessageForForm(nodeMap,
					EHR_AreaGridChild));
			if (manaDoctor == null) {
				return;
			}

			SystemUserModel suModel = new SystemUserModel(dao);
			List<Map<String, Object>> manageUnits = suModel.getUserByLogonName(
					manaDoctor, RolesList.ZRYS, RolesList.TDZ);
			body.put("manageUnits", manageUnits);
		} catch (ModelDataOperationException e) {
			logger.error("Get children base infomation by phrId failed.", e);
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
					WorkType.MOV_EHR_CONFIRM);
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
		String moveType = (String) applyData.get("moveType");
		String confirmUnit = null;
		if (moveType.equals(MoveType.APPLY_IN)) {
			confirmUnit = (String) applyData.get("sourceUnit");
		} else {
			confirmUnit = (String) applyData.get("targetUnit");
		}
		// ** 确认机构为防保科长的管辖机构到中心
		if (confirmUnit.length() > 9) {
			confirmUnit = confirmUnit.substring(0, 9);
		}
		return confirmUnit;
	}
	
	/**
	 * 获取管辖机构
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetManageUnit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String jgid = (String) req.get("body");
		EHRMoveModule ehrMoveModule = new EHRMoveModule(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = ehrMoveModule.getManageUnit(jgid);
		} catch (ModelDataOperationException e) {
			logger.error("Save ehr move apply failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", rsMap);
	}
}
