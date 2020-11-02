/**
 * @(#)SchistospmaService.java Created on 2012-5-15 下午03:09:37
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.sch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.phr.HealthRecordModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.UserUtil;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class SchistospmaService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(SchistospmaManageService.class);

	protected void doRecordIfHereAndStatus(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = (String) req.get("empiId");
		String manaUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
		HealthRecordModel healthRecordModel = new HealthRecordModel(dao);
		Map<String, Object> record = null;
		try {
			record = healthRecordModel.getHealthRecordByEmpiId(empiId);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		String manaUnitId_n = (String) record.get("manaUnitId");
		String status = (String) record.get("status");
		Map<String, Object> resBody = new HashMap<String, Object>();
		if (manaUnitId_n.length() < 9
				|| !manaUnitId.equals(manaUnitId_n.substring(0, 9))
				|| !"0".equals(status)) {
			resBody.put("doFlag", false);
		} else {
			resBody.put("doFlag", true);
		}
		res.put("body", resBody);
	}

	/**
	 * 保存血吸虫病档案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveSchistospmaRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		SchistospmaModel sm = new SchistospmaModel(dao);
		try {
			Map<String, Object> key = sm.saveSchistospmaRecord(op, body);
			if ("create".equals(op)) {
				res.put("body", key);
			}
		} catch (ModelDataOperationException e) {
			logger.error("save SchistospmaRecord is fail!", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 结案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doCloseSchistospmaRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String schisRecordId = (String) body.get("schisRecordId");
		SchistospmaModel sm = new SchistospmaModel(dao);
		try {
			sm.closeSchistospmaRecord(schisRecordId);
		} catch (ModelDataOperationException e) {
			logger.error("close SchistospmaRecord is fail!", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 验证是否可以新建档案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doAddSchistospmaRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = (String) req.get("empiId");
		SchistospmaModel sm = new SchistospmaModel(dao);
		Map<String, Object> resBody = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> record = sm.notCloseRecord(empiId);
			if (record.size() > 0) {
				resBody.put("code", 201);
				resBody.put("msg", "有未结案的档案,请先结案后再新建档案!");
				res.put("body", resBody);
				return;
			}
			record = sm.wirteOffrecord(empiId);
			if (record.size() > 0) {
				resBody.put("code", 202);
				resBody.put("msg", "档案已存在,请联系系统管理员走档案恢复流程!");
				res.put("body", resBody);
				return;
			}
			resBody.put("code", Constants.CODE_OK);
			resBody.put("msg", "ok!");
			res.put("body", resBody);
		} catch (ModelDataOperationException e) {
			logger.error("add SchistospmaRecord is fail!", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "判断血吸虫是否有未结案档案失败。");
			throw new ServiceException(e);
		}
	}

	/**
	 * 注销单个用户的所有血吸虫档案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLogoutSchistospmaRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		String cancellationReason = (String) body.get("cancellationReason");
		String deadReason = (String) body.get("deadReason");
		SchistospmaModel sm = new SchistospmaModel(dao);
		try {
			sm.logoutSchistospmaRecord(phrId, cancellationReason, deadReason,
					true);
		} catch (ModelDataOperationException e) {
			logger.error("logout all SchistospmaRecord is fail!", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 作废单条血吸虫档案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSingleRecordLogout(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String schisRecordId = (String) body.get("schisRecordId");
		String cancellationReason = (String) body.get("cancellationReason");
		String deadReason = (String) body.get("deadReason");
		SchistospmaModel sm = new SchistospmaModel(dao);
		try {
			sm.logoutSchistospmaRecord(schisRecordId, cancellationReason,
					deadReason, false);
		} catch (ModelDataOperationException e) {
			logger.error("single RecordLogout is fail!", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存血吸虫病随访记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveSchistospmaVisitInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		SchistospmaModel sm = new SchistospmaModel(dao);
		try {
			sm.saveSchistospmaVisitInfo(op, body);
		} catch (ModelDataOperationException e) {
			logger.error("save SchistospmaVisit info is fail!", e);
			throw new ServiceException(e);
		}
	}

}
