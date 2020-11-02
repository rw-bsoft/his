/**
 * @(#)MHCMoveService.java Created on 2012-5-21 下午4:49:33
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.mov;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.conf.BizColumnConfig;
import chis.source.control.ControlRunner;
import chis.source.dic.AffirmType;
import chis.source.dic.ArchiveMoveStatus;
import chis.source.dic.MoveType;
import chis.source.dic.WorkType;
import chis.source.mhc.PregnantRecordModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import chis.source.worklist.WorkListModel;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 孕妇户籍地址修改
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class MHCMoveService extends AbstractActionService implements
		DAOSupportable {

	private static final Logger logger = LoggerFactory
			.getLogger(MHCMoveService.class);

	/**
	 * 保存孕妇户籍地址迁移记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveMHCMoveApplyRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> data = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		data.put("status", ArchiveMoveStatus.NEEDCONFIRM);
		data.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		data.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		data.put("lastModifyDate", new Date());
		MHCMoveModule cmd = new MHCMoveModule(dao);
		try {
			cmd.saveMHCMoveRecord(data, op, MOV_MHCApply, res);
		} catch (ModelDataOperationException e) {
			logger.error("save pregnant move apply record failed.", e);
			throw new ServiceException(e);
		}
		String archiveMoveId = (String) data.get("archiveMoveId");
		if("create".equals(op)){
			Map<String, Object> rsMap = (Map<String, Object>)res.get("body");
			archiveMoveId = (String) rsMap.get("archiveMoveId");
		}
		vLogService.saveVindicateLog(MOV_MHC, op, archiveMoveId, dao);
		// ** 任务提醒相关
		WorkListModel wlm = new WorkListModel(dao);
		try {
			if (op.equals("create")) {
				Map<String, Object> resBody = (Map<String, Object>) res
						.get("body");
				Map<String, Object> workList = new HashMap<String, Object>();
				workList.put("recordId", resBody.get("archiveMoveId"));
				workList.put("workType", WorkType.MOV_MHC_CONFIRM);
				workList.put("manaUnitId", getConfirmUnit(data));
				wlm.insertWorkListRecord(workList, false);
			} else {
				wlm.updateWorkListManageUnit(getConfirmUnit(data),
						(String) data.get("archiveMoveId"),
						WorkType.MOV_MHC_CONFIRM);
			}
		} catch (ModelDataOperationException e) {
			logger.error("save work list  record failed.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存孕妇户籍地址申请退回记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveMHCMoveRejectRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> data = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		data.put("status", ArchiveMoveStatus.CANCEL);
		data.put("affirmType", AffirmType.CANCEL);
		data.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		data.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		data.put("lastModifyDate", new Date());
		MHCMoveModule cmd = new MHCMoveModule(dao);
		try {
			cmd.saveMHCMoveRecord(data, op, MOV_MHCConfirm, res);
		} catch (ModelDataOperationException e) {
			logger.error("save pregnant move reject record failed.", e);
			throw new ServiceException(e);
		}
		String archiveMoveId = (String) data.get("archiveMoveId");
		vLogService.saveVindicateLog(MOV_MHC, "6", archiveMoveId, dao);
		// ** 任务提醒相关
		removeWorkList(data, dao);
	}

	/**
	 * 保存孕妇户籍地址申请确认记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveMHCMoveConfirmRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> data = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		data.put("status", ArchiveMoveStatus.CONFIRM);
		data.put("affirmType", AffirmType.CONFIRM);
		data.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		data.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		data.put("lastModifyDate", new Date());
		MHCMoveModule cmd = new MHCMoveModule(dao);
		try {
			cmd.saveMHCMoveRecord(data, op, MOV_MHCConfirm, res);
		} catch (ModelDataOperationException e) {
			logger.error("save pregnant move confirm record failed.", e);
			throw new ServiceException(e);
		}
		String archiveMoveId = (String) data.get("archiveMoveId");
		vLogService.saveVindicateLog(MOV_MHC, "6", archiveMoveId, dao);
		//执行迁移
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pregnantId", data.get("archiveId"));
		params.put("phrId", data.get("phrId"));
		params.put("ownerArea", data.get("targetOwnerArea"));
		params.put("residenceCode", data.get("targetResidenceCode"));
		params.put("homeAddress", data.get("targetHomeAddress"));
		params.put("homeAddress_text", data.get("targetHomeAddress_text"));
		params.put("restRegionCode", data.get("targetRestRegion"));
		params.put("restRegionCode_text", data.get("targetRestRegion_text"));
		params.put("manaUnitId", data.get("targetManaUnitId"));
		params.put("mhcDoctorId", data.get("targetMhcDoctorId"));
		params.put("lastModifyUser", data.get("lastModifyUser"));
		params.put("lastModifyUnit", data.get("lastModifyUnit"));
		params.put("lastModifyDate", data.get("lastModifyDate"));
		try {
			cmd.movePregnantMessage(params);
		} catch (ModelDataOperationException e) {
			logger.error("failed to move child message.", e);
			throw new ServiceException(e);
		}
		//迁移修改过的表-记录日志
		try {
			List<String> tList = BizColumnConfig.getUpdateTable(BizColumnConfig.MOV_MHC);
			for(String tn : tList){
				vLogService.saveVindicateLog(tn, "m", null, dao);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		// ** 任务提醒相关
		removeWorkList(data, dao);
	}

	/**
	 * 获取孕妇户籍地址申请记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetMHCMoveRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String pkey = (String) body.get("pkey");
		MHCMoveModule cmd = new MHCMoveModule(dao);
		Map<String, Object> data = null;
		try {
			data = cmd.getMHCMoveRecord(pkey);
		} catch (ModelDataOperationException e) {
			logger.error("failed to get cdh move record.", e);
			throw new ServiceException(e);
		}
		if (data == null || data.size() < 1) {
			return;
		}
		HashMap<String, Object> result = new HashMap<String, Object>();
		for (Iterator<String> keys = data.keySet().iterator(); keys.hasNext();) {
			String key = (String) keys.next();
			Object value = data.get(key);
			if (value != null) {
				result.put(key, value);
			}
		}
		Map<String, Boolean> controls = ControlRunner.run(MOV_MHC,
				result, ctx, ControlRunner.ALL);
		Map<String, Object> resBody = new HashMap<String, Object>();
		resBody = SchemaUtil
				.setDictionaryMessageForForm(result, MOV_MHC);
		resBody.put("_actions", controls);
		res.put("body", resBody);
	}

	/**
	 * 获取孕妇档案信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetPregnantMessage(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> data = (Map<String, Object>) req.get("body");
		String empiId = (String) data.get("empiId");
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		try {
			List<Map<String, Object>> result = prm
					.getPregnantByEmpiIdJoin(empiId);
			if (result == null || result.size() < 1) {
				res.put("noRecord", true);
				return;
			}
			Map<String, Object> resBody = SchemaUtil.setDictionaryMessageForForm(result.get(0),
					MHC_PregnantRecord);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("archiveId", resBody.get("pregnantId"));
			map.put("phrId", resBody.get("phrId"));
			map.put("personName", resBody.get("personName"));
			map.put("sourceOwnerArea", resBody.get("ownerArea"));
			map.put("sourceResidenceCode", resBody.get("residenceCode"));
			map.put("sourceHomeAddress", resBody.get("homeAddress"));
			map.put("sourceHomeAddress_text", resBody.get("homeAddress_text"));
			map.put("sourceRestRegion", resBody.get("restRegionCode"));
			map.put("sourceRestRegion_text", resBody.get("restRegionCode_text"));
			map.put("sourceManaUnitId", resBody.get("manaUnitId"));
			map.put("sourceMhcDoctorId", resBody.get("mhcDoctorId"));
			res.put("body", map);
		} catch (ModelDataOperationException e) {
			logger.error("get  pregnant record failed.", e);
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
					WorkType.MOV_MHC_CONFIRM);
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
			confirmUnit = (String) applyData.get("sourceManaUnitId");
		} else {
			confirmUnit = (String) applyData.get("targetManaUnitId");
		}
		// ** 确认机构为防保科长的管辖机构到中心
		if (confirmUnit.length() > 9) {
			confirmUnit = confirmUnit.substring(0, 9);
		}
		return confirmUnit;
	}
	
	/**
	 * 删除孕妇户籍地址迁移记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doRemoveMHCMoveRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String pkey = (String) req.get("pkey");
		MHCMoveModule cdhmModule = new MHCMoveModule(dao);
		try {
			cdhmModule.deleteMHCMoveRecord(pkey);
		} catch (ModelDataOperationException e) {
			logger.error("Delete mov_mhc apply record failed.",e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MOV_MHC, "4", pkey, dao);
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("archiveMoveId", pkey);
		// ** 任务提醒相关
		removeWorkList(paraMap, dao);
	}
}
