/**
 * @(#)Schis.sourcetospmaBaseInfoService.java Created on 2012-5-15 上午10:12:22
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.sch;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.util.SchemaUtil;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class SnailBaseInfoService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(SnailBaseInfoService.class);

	protected void doSaveSnailBaseInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String op = (String) req.get("op");
		SnailBaseInfoModel smm = new SnailBaseInfoModel(dao);
		Map<String, Object> body = null;
		try {
			body = smm.saveSnailBaseInfo(schema, op, reqBody);
			body = SchemaUtil.setDictionaryMessageForForm(body, schema);
		} catch (ModelDataOperationException e) {
			logger.error("save " + schema + " is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "保存查螺灭螺基本情况失败");
			throw new ServiceException(e);
		}
		res.put("body", body);
	}

	protected void doSaveSnailFindInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String op = (String) req.get("op");
		SnailBaseInfoModel smm = new SnailBaseInfoModel(dao);
		Map<String, Object> body = null;
		try {
			body = smm.saveSnailFindInfo(schema, op, reqBody);
			body.put("snailBaseInfoId", reqBody.get("snailBaseInfoId"));
			body = SchemaUtil.setDictionaryMessageForForm(body, schema);
		} catch (ModelDataOperationException e) {
			logger.error("save " + schema + " is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "保存查螺记录失败");
			throw new ServiceException(e);
		}
		res.put("body", body);
	}

	protected void doSaveSnailKillInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String op = (String) req.get("op");
		SnailBaseInfoModel smm = new SnailBaseInfoModel(dao);
		Map<String, Object> body = null;
		try {
			body = smm.saveSnailKillInfo(schema, op, reqBody);
			body.put("snailBaseInfoId", reqBody.get("snailBaseInfoId"));
			body = SchemaUtil.setDictionaryMessageForForm(body, schema);
		} catch (ModelDataOperationException e) {
			logger.error("save " + schema + " is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "保存灭螺记录失败");
			throw new ServiceException(e);
		}
		res.put("body", body);
	}

	protected void doRemoveSnailBaseInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String snailBaseInfoId = (String) req.get("pkey");
		String schema = (String) req.get("schema");
		SnailBaseInfoModel smm = new SnailBaseInfoModel(dao);
		try {
			smm.removeSnailBaseInfo(snailBaseInfoId, schema);
		} catch (ModelDataOperationException e) {
			logger.error("remove " + schema + " is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "删除查螺灭螺基本情况失败");
			throw new ServiceException(e);
		}
	}

	protected void doRemoveSnailFindInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String snailFindInfoId = (String) req.get("pkey");
		String schema = (String) req.get("schema");
		SnailBaseInfoModel smm = new SnailBaseInfoModel(dao);
		try {
			smm.removeSnailFindInfo(snailFindInfoId, schema);
		} catch (ModelDataOperationException e) {
			logger.error("remove " + schema + " is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "删除查螺记录失败");
			throw new ServiceException(e);
		}
	}

	protected void doRemoveSnailKillInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String snailKillInfoId = (String) req.get("pkey");
		String schema = (String) req.get("schema");
		SnailBaseInfoModel smm = new SnailBaseInfoModel(dao);
		try {
			smm.removeSnailKillInfo(snailKillInfoId, schema);
		} catch (ModelDataOperationException e) {
			logger.error("remove " + schema + " is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "删除灭螺记录失败");
			throw new ServiceException(e);
		}
	}
}
