/**
 * @(#)RabiesRecordService.java Created on 2012-4-19 上午11:06:07
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.dc;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.control.ControlRunner;
import chis.source.dic.CancellationReason;
import chis.source.phr.HealthRecordModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;

import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class RabiesRecordService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(RabiesRecordService.class);

	protected void doGetRabiesRecordList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String schema = (String) req.get("schema");
		List cnd = (List) req.get("cnd");
		RabiesRecordModel rrm = new RabiesRecordModel(dao);
		List<Map<String, Object>> list = null;
		try {
			list = rrm.getRabiesRecordList(schema, cnd);
			list = SchemaUtil.setDictionaryMessageForList(list, schema);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get RabiesRecord list .", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取狂犬病档案列表信息失败。");
			throw new ServiceException(e);
		}
		ControlRunner.run(DC_RabiesRecord, list, ctx, new String[] {
				ControlRunner.CREATE, ControlRunner.UPDATE });
		res.put("body", list);
	}
	public void doLoadControl(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		RabiesRecordModel rrm = new RabiesRecordModel(dao);
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("empiId", req.get("empiId"));
			Map<String, Boolean> update = ControlRunner.run(DC_RabiesRecord,
					data, ctx, ControlRunner.CREATE, ControlRunner.UPDATE);
			Map<String, Object> map=rrm.getPhrStatus((String) req.get("empiId"));
			if(map.get("create")!=null){
				update.put("create", false);
				update.put("update", false);
			}
			res.put("body", update);
			
		} catch (Exception e) {
			logger.error("get control message error!", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存狂犬病档案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doSaveRabiesRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String schema = (String) req.get("schema");
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String, Object> body;
		RabiesRecordModel rrm = new RabiesRecordModel(dao);
		try {
			body = rrm.saveRabiesRecord(schema, reqBody, op);
			body = SchemaUtil.setDictionaryMessageForForm(body, schema);
			body.put("status_text", getDicText(body, schema, "status"));
			body.put("closeFlag_text", getDicText(body, schema, "closeFlag"));
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save RabiesRecord .", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "保存狂犬病档案信息失败。");
			throw new ServiceException(e);
		}
		res.put("body", body);
	}

	/**
	 * 保存接种记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doSaveVaccination(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String, Object> body;
		RabiesRecordModel rrm = new RabiesRecordModel(dao);
		try {
			body = rrm.saveVaccination(op, reqBody);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save Vaccination .", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "保存接种记录信息失败。");
			throw new ServiceException(e);
		}
		res.put("body", body);
	}

	/**
	 * 拿到字典的text
	 * 
	 * @param body
	 * @param schemaName
	 * @param itemId
	 * @return
	 */
	private Object getDicText(Map<String, Object> body, String schemaName,
			String itemId) {
		Schema schema = SchemaController.instance().getSchema(schemaName);
		SchemaItem item = schema.getItem(itemId);
		String itemKey = item.getId();
		Object keyValue = body.get(itemKey);
		Object displayValue = item.getDisplayValue(keyValue);
		return displayValue;
	}

	/**
	 * 获取狂犬病档案信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doLoadRabiesRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String schema = (String) req.get("schema");
		String pkey = (String) req.get("pkey");
		Map<String, Object> body;
		RabiesRecordModel rrm = new RabiesRecordModel(dao);
		try {
			body = rrm.loadRabiesRecord(schema, pkey);
			body = SchemaUtil.setDictionaryMessageForForm(body, schema);
			body.put("status_text", getDicText(body, schema, "status"));
			body.put("closeFlag_text", getDicText(body, schema, "closeFlag"));
		} catch (ModelDataOperationException e) {
			logger.error("Failed to load RabiesRecord .", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取狂犬病档案信息失败。");
			throw new ServiceException(e);
		}
		res.put("body", body);
	}

	/**
	 * 检查是否有正常的狂犬病档案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doWhetherNeedRabiesRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = new HashMap<String, Object>();
		String empiId = (String) req.get("empiId");
		RabiesRecordModel rrm = new RabiesRecordModel(dao);
		Map<String, Object> map = null;
		try {
			map = rrm.whetherNeedRabiesRecord(empiId);
			SchemaUtil.setDictionaryMessageForForm(map, DC_RabiesRecord);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to check whether RabiesRecord record exists.",
					e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "检查是否有正常的狂犬病档案失败。");
			throw new ServiceException(e);
		}
		if (map != null) {
			body.putAll(map);
			body.put("needCreate", 0);
		} else {
			body.put("needCreate", 1);
		}
		res.put("body", body);
	}
	
	/**
	 * 检查是否有不注销的狂犬病档案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doCheckHasNotLogOut(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = new HashMap<String, Object>();
		String empiId = (String) req.get("empiId");
		RabiesRecordModel rrm = new RabiesRecordModel(dao);
		List<Map<String, Object>> map = null;
		try {
			map = rrm.checkHasNotLogOut(empiId);
			SchemaUtil.setDictionaryMessageForForm(map, DC_RabiesRecord);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to check  RabiesRecord record exists.",
					e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "检查是否有不注销的狂犬病档案失败。");
			throw new ServiceException(e);
		}
		if (map != null&&map.size()>0) {
			body.put("hasNotLogOut", true);
		} else {
			body.put("hasNotLogOut", false);
		}
		res.put("body", body);
	}

	/**
	 * 狂犬病档案结案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doLogoutRabies(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String recordId = (String) reqBody.get("recordId");
		RabiesRecordModel rrm = new RabiesRecordModel(dao);
		try {
			rrm.logoutRabies(recordId);
		} catch (ModelDataOperationException e) {
			logger.error("Logout Rabies record failed.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 注销当前狂犬病档案记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doLogoutRabiesRecordThis(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String cancellationReason = (String) reqBodyMap
				.get("cancellationReason");
		String deadReason = (String) reqBodyMap.get("deadReason");
		String empiId = (String) reqBodyMap.get("empiId");
		String rabiesId = (String) reqBodyMap.get("rabiesId");
		// 修改档案记录状态为--注销
		RabiesRecordModel rrm = new RabiesRecordModel(dao);
		try {
			rrm.logoutRabiesRecordThis(empiId, cancellationReason, rabiesId,
					deadReason);
		} catch (ModelDataOperationException e) {
			logger.error("Logout Rabies record failed.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 注销所有狂犬病档案记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doLogoutRabiesRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String phrId = (String) reqBodyMap.get("phrId");
		String cancellationReason = (String) reqBodyMap
				.get("cancellationReason");
		String deadReason = (String) reqBodyMap.get("deadReason");
		// 修改档案记录状态为--注销
		RabiesRecordModel rrm = new RabiesRecordModel(dao);
		try {
			rrm.logoutRabiesRecord(phrId, cancellationReason, deadReason);
		} catch (ModelDataOperationException e) {
			logger.error("Logout Rabies record failed.", e);
			throw new ServiceException(e);
		}
		// 如果是“死亡”，则在健康档案中打上死亡标识
		if (null != cancellationReason
				&& cancellationReason.equals(CancellationReason.PASS_AWAY)) {
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			String deadFlag = (String) reqBodyMap.get("deadFlag");
			// String deadReason = (String) reqBodyMap.get("deadReason");
			String strDeadDate = (String) reqBodyMap.get("deadDate");
			Date deadDate = BSCHISUtil.toDate(strDeadDate);
			try {
				hrModel.setDeadFlag(deadFlag, deadReason, deadDate, phrId);
			} catch (ModelDataOperationException e) {
				logger.error("Update health record deadFlag failed.", e);
				throw new ServiceException(e);
			}
		}

	}
}
