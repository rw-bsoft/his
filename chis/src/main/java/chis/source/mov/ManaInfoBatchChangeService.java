/**
 * @(#)ManaInfoBatchChangeService.java Created on 2012-5-23 下午4:49:33
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
import chis.source.conf.BizColumnConfig;
import chis.source.control.ControlRunner;
import chis.source.dic.AffirmType;
import chis.source.dic.ArchiveMoveStatus;
import chis.source.dic.ArchiveType;
import chis.source.dic.WorkType;
import chis.source.dic.YesNo;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
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
public class ManaInfoBatchChangeService extends AbstractActionService implements
		DAOSupportable {

	private static final Logger logger = LoggerFactory
			.getLogger(ManaInfoBatchChangeService.class);

	/**
	 * 保存批量修改管理医生申请记录
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
		ManaInfoBatchChangeModule mbcm = new ManaInfoBatchChangeModule(dao);
		try {
			mbcm.saveMoveRecord(formData, op, MOV_EHRManaInfoBatchChangeApply,
					res);
			String archiveMoveId = (String) formData.get("archiveMoveId");
			if ("create".equals(op)) {
				Map<String, Object> rsMap = (Map<String, Object>) res
						.get("body");
				archiveMoveId = (String) rsMap.get("archiveMoveId");
			}
			vLogService.saveVindicateLog(MOV_ManaInfoBatchChange, op,
					archiveMoveId, dao);
			if (listData == null || listData.size() < 1) {
				return;
			}
			for (Map<String, Object> map : listData) {
				String detailId = (String) map.get("detailId");
				String detailOp = "update";
				if (detailId == null || "".equals(detailId)) {
					detailOp = "create";
					String archiveType = (String) formData.get("archiveType");
					if (archiveMoveId == null || "".equals(archiveMoveId)) {
						Map<String, Object> resBody = (Map<String, Object>) res
								.get("body");
						archiveMoveId = (String) resBody.get("archiveMoveId");
					}
					map.put("archiveMoveId", archiveMoveId);
					map.put("archiveType", archiveType);
				}
				map.put("affirmType", YesNo.NO);
				mbcm.saveMoveRecordDetail(map, detailOp);
			}
			if (delDetaiId != null && delDetaiId.size() > 0) {
				for (String detailId : delDetaiId) {
					mbcm.removeMoveRecordDetailByPkey(detailId);
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
				workList.put("workType", WorkType.MOV_BATCH_CONFIRM);
				workList.put("manaUnitId", getConfirmUnit(formData));
				wlm.insertWorkListRecord(workList, false);
			} else {
				wlm.updateWorkListManageUnit(getConfirmUnit(formData),
						(String) formData.get("archiveMoveId"),
						WorkType.MOV_BATCH_CONFIRM);
			}
		} catch (ModelDataOperationException e) {
			logger.error("save work list  record failed.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存批量修改管理医生退回记录
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
		ManaInfoBatchChangeModule mbcm = new ManaInfoBatchChangeModule(dao);
		try {
			mbcm.saveMoveRecord(formData, op,
					MOV_EHRManaInfoBatchChangeConfirm, res);
		} catch (ModelDataOperationException e) {
			logger.error("save  move reject record failed.", e);
			throw new ServiceException(e);
		}
		String archiveMoveId = (String) formData.get("archiveMoveId");
		vLogService.saveVindicateLog(MOV_ManaInfoBatchChange, "6",
				archiveMoveId, dao);
		// ** 任务提醒相关
		removeWorkList(formData, dao);
	}

	/**
	 * 保存批量修改管理医生确认记录
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
		ManaInfoBatchChangeModule mbcm = new ManaInfoBatchChangeModule(dao);
		try {
			mbcm.saveMoveRecord(formData, op,
					MOV_EHRManaInfoBatchChangeConfirm, res);
			String archiveType = (String) formData.get("archiveType");
			// 日志记录
			String archiveMoveId = (String) formData.get("archiveMoveId");
			vLogService.saveVindicateLog(MOV_ManaInfoBatchChange, "6",
					archiveMoveId, dao);
			// 执行修改
			if (listData == null || listData.size() < 1) {
				return;
			}
			for (Map<String, Object> map : listData) {
				String detailOp = "update";
				map.put("affirmType", YesNo.YES);
				mbcm.saveMoveRecordDetail(map, detailOp);
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("archiveId", map.get("archiveId"));
				params.put("manaUnitId", formData.get("targetUnit"));
				params.put("lastModifyUser", formData.get("lastModifyUser"));
				params.put("lastModifyUnit", formData.get("lastModifyUnit"));
				params.put("lastModifyDate", formData.get("lastModifyDate"));
				String updateFile = "";
				List<String> updateList = null;
				// ** 迁移个人档案
				if (archiveType.equals(ArchiveType.HEALTH_ARCHIVE)) {
					params.put("manaDoctorId", formData.get("targetDoctor"));
					// ** for残疾情况
					params.put("closeFlag", YesNo.NO);
					params.put("status", Constants.CODE_STATUS_NORMAL);
					String movesub = (String) formData.get("movesub");
					updateFile = BizColumnConfig.MOV_EHR_SUB;
					// ** 只迁移个人健康档案
					if (movesub == null || movesub.equals(YesNo.NO)) {
						updateList = new ArrayList<String>();
						updateList.add(EHR_HealthRecord);
						updateList.add(CDH_HealthCard);
						updateList.add(MHC_PregnantRecord);
						updateList.add(MHC_WomanRecord);
						updateList.add(PIV_VaccinateRecord);
						params.put("empiId", map.get("empiId"));
					}
				}
				// ** 迁移儿童档案
				else if (archiveType.equals(ArchiveType.CHILDREN_ARCHIVE)) {
					params.put("cdhDoctorId", formData.get("targetDoctor"));
					updateFile = BizColumnConfig.MOV_CDH_BATCH;
				}
				// ** 迁移孕妇档案
				else if (archiveType.equals(ArchiveType.PREGNANT_ARCHIVE)) {
					params.put("mhcDoctorId", formData.get("targetDoctor"));
					params.put("empiId", map.get("empiId"));
					updateFile = BizColumnConfig.MOV_MHC_BATCH;
				}
				mbcm.moveRecord(updateFile, params, updateList);
				// 迁移修改过的表-记录日志
				try {
					List<String> tList = BizColumnConfig
							.getUpdateTable(updateFile);
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
	 * 获取批量修改管理医生申请记录
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
		ManaInfoBatchChangeModule mbcm = new ManaInfoBatchChangeModule(dao);
		Map<String, Object> formData = null;
		try {
			formData = mbcm.getMoveRecord(pkey);
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
				MOV_ManaInfoBatchChange, result, ctx, ControlRunner.ALL);
		resBody.put("_actions", controls);
		res.put("body", resBody);
		Map<String, Object> formBody = SchemaUtil.setDictionaryMessageForForm(
				result, MOV_ManaInfoBatchChange);
		formBody.remove("affirmUser");
		formBody.remove("affirmUnit");
		resBody.put("formData", formBody);
		try {
			List<Map<String, Object>> detailList = mbcm
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
	 * 删除批量修改管理医生申请记录
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
		ManaInfoBatchChangeModule mbcm = new ManaInfoBatchChangeModule(dao);
		try {
			mbcm.removeMoveRecord(pkey);
			mbcm.removeMoveRecordDetail(pkey);
		} catch (ModelDataOperationException e) {
			logger.error("failed to remove  move record .", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MOV_ManaInfoBatchChange, "4", pkey, dao);
		Map<String, Object> formData = new HashMap<String, Object>();
		formData.put("archiveMoveId", pkey);
		// ** 任务提醒相关
		removeWorkList(formData, dao);
	}

	/**
	 * 删除批量修改管理医生申请明细记录
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
		ManaInfoBatchChangeModule mbcm = new ManaInfoBatchChangeModule(dao);
		try {
			for (String key : pkey) {
				mbcm.removeMoveRecordDetailByPkey(key);
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
					WorkType.MOV_BATCH_CONFIRM);
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

}
