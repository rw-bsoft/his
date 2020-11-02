/**
 * @(#)CDHMoveService.java Created on 2012-5-20 下午4:49:33
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
import chis.source.cdh.ChildrenHealthModel;
import chis.source.conf.BizColumnConfig;
import chis.source.control.ControlRunner;
import chis.source.dic.AffirmType;
import chis.source.dic.ArchiveMoveStatus;
import chis.source.dic.MoveType;
import chis.source.dic.WorkType;
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
public class CDHMoveService extends AbstractActionService implements
		DAOSupportable {

	private static final Logger logger = LoggerFactory
			.getLogger(CDHMoveService.class);

	/**
	 * 保存儿童户籍地址迁移记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveCDHMoveApplyRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> data = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		data.put("status", ArchiveMoveStatus.NEEDCONFIRM);
		data.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		data.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		data.put("lastModifyDate", new Date());
		CDHMoveModule cmd = new CDHMoveModule(dao);
		try {
			cmd.saveCDHMoveRecord(data, op, MOV_CDHApply, res);
		} catch (ModelDataOperationException e) {
			logger.error("save children move apply record failed.", e);
			throw new ServiceException(e);
		}
		String archiveMoveId = (String) data.get("archiveMoveId");
		if("create".equals(op)){
			Map<String, Object> rsMap = (Map<String, Object>) res.get("body");
			archiveMoveId = (String) rsMap.get("archiveMoveId");
		}
		vLogService.saveVindicateLog(MOV_CDH, op, archiveMoveId, dao);
		// ** 任务提醒相关
		WorkListModel wlm = new WorkListModel(dao);
		try {
			if (op.equals("create")) {
				Map<String, Object> resBody = (Map<String, Object>) res
						.get("body");
				Map<String, Object> workList = new HashMap<String, Object>();
				workList.put("recordId", resBody.get("archiveMoveId"));
				workList.put("workType", WorkType.MOV_CDH_CONFIRM);
				workList.put("manaUnitId", getConfirmUnit(data));
				wlm.insertWorkListRecord(workList, false);
			} else {
				wlm.updateWorkListManageUnit(getConfirmUnit(data),
						(String) data.get("archiveMoveId"),
						WorkType.MOV_CDH_CONFIRM);
			}
		} catch (ModelDataOperationException e) {
			logger.error("save work list  record failed.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存儿童户籍地址申请退回记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveCDHMoveRejectRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> data = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		data.put("status", ArchiveMoveStatus.CANCEL);
		data.put("affirmType", AffirmType.CANCEL);
		data.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		data.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		data.put("lastModifyDate", new Date());
		CDHMoveModule cmd = new CDHMoveModule(dao);
		try {
			cmd.saveCDHMoveRecord(data, op, MOV_CDHConfirm, res);
		} catch (ModelDataOperationException e) {
			logger.error("save children move reject record failed.", e);
			throw new ServiceException(e);
		}
		String archiveMoveId = (String) data.get("archiveMoveId");
		vLogService.saveVindicateLog(MOV_CDH, "6", archiveMoveId, dao);
		// ** 任务提醒相关
		removeWorkList(data, dao);
	}

	/**
	 * 保存儿童户籍地址申请确认记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveCDHMoveConfirmRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> data = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		data.put("status", ArchiveMoveStatus.CONFIRM);
		data.put("affirmType", AffirmType.CONFIRM);
		data.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		data.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		data.put("lastModifyDate", new Date());
		CDHMoveModule cmd = new CDHMoveModule(dao);
		try {
			cmd.saveCDHMoveRecord(data, op, MOV_CDHConfirm, res);
		} catch (ModelDataOperationException e) {
			logger.error("save children move confirm record failed.", e);
			throw new ServiceException(e);
		}
		String archiveMoveId = (String) data.get("archiveMoveId");
		vLogService.saveVindicateLog(MOV_CDH, "6", archiveMoveId, dao);
		//执行迁移
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("phrId", data.get("archiveId"));
		params.put("ownerArea", data.get("targetOwnerArea"));
		params.put("homeAddress", data.get("targetHomeAddress"));
		params.put("homeAddress_text", data.get("targetHomeAddress_text"));
		params.put("manaUnitId", data.get("targetManaUnitId"));
		params.put("cdhDoctorId", data.get("targetCdhDoctorId"));
		params.put("lastModifyUser", data.get("lastModifyUser"));
		params.put("lastModifyUnit", data.get("lastModifyUnit"));
		params.put("lastModifyDate", data.get("lastModifyDate"));
		try {
			cmd.moveChildMessage(params);
		} catch (ModelDataOperationException e) {
			logger.error("failed to move child message.", e);
			throw new ServiceException(e);
		}
		//迁移修改过的表-记录日志
		try {
			List<String> tList = BizColumnConfig.getUpdateTable(BizColumnConfig.MOV_CDH);
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
	 * 获取儿童户籍地址申请记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetCDHMoveRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String pkey = (String) body.get("pkey");
		CDHMoveModule cmd = new CDHMoveModule(dao);
		Map<String, Object> data = null;
		try {
			data = cmd.getCDHMoveRecord(pkey);
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
		Map<String, Boolean> controls = ControlRunner.run(MOV_CDH,
				result, ctx, ControlRunner.ALL);
		Map<String, Object> resBody = new HashMap<String, Object>();
		resBody = SchemaUtil
				.setDictionaryMessageForForm(result, MOV_CDH);
		resBody.put("_actions", controls);
		res.put("body", resBody);
	}

	/**
	 * 获取儿童档案信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetChildrenMessage(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> data = (Map<String, Object>) req.get("body");
		String empiId = (String) data.get("empiId");
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		try {
			Map<String, Object> result = chm
					.getChildHealthCardByEmpiIdJoin(empiId);
			if (result != null) {
				result = SchemaUtil.setDictionaryMessageForForm(result,
						CDH_HealthCard);
				Map<String, Object> resBody = new HashMap<String, Object>();
				resBody.put("archiveId", result.get("phrId"));
				resBody.put("personName", result.get("personName"));
				resBody.put("sourceOwnerArea", result.get("ownerArea"));
				resBody.put("sourceHomeAddress", result.get("homeAddress"));
				resBody.put("sourceHomeAddress_text",
						result.get("sourceHomeAddress_text"));
				resBody.put("sourceManaUnitId", result.get("manaUnitId"));
				resBody.put("sourceCdhDoctorId", result.get("cdhDoctorId"));
				resBody.put("status", result.get("status"));
				res.put("body", resBody);
			}
		} catch (ModelDataOperationException e) {
			logger.error("get  children record failed.", e);
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
					WorkType.MOV_CDH_CONFIRM);
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
	 * 删除儿童户籍地址申请记录*任务列表记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doRemoveCDHMoveRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String pkey = (String) req.get("pkey");
		CDHMoveModule cdhmModule = new CDHMoveModule(dao);
		try {
			cdhmModule.deleteCDHMoveRecord(pkey);
		} catch (ModelDataOperationException e) {
			logger.error("Delete mov_cdh apply record failed.",e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MOV_CDH, "4", pkey, dao);
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("archiveMoveId", pkey);
		// ** 任务提醒相关
		removeWorkList(paraMap, dao);
	}
	
}
