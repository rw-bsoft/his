/**
 * 	@(#)GroupDinnerRecordService.java Created on 2012-02-17 上午11:27:20
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.gdr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.util.SchemaUtil;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * * @description 群宴管理服务
 * 
 * @author <a href="mailto:zhoux@bsoft.com.cn">zhoux</a>
 * @author
 * 
 */
public class GroupDinnerRecordService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
	.getLogger(GroupDinnerRecordService.class);
	/**
	 * 保存群宴登记信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doSaveGroupDinnerRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		GroupDinnerModel gdm = new GroupDinnerModel(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String op = (String) req.get("op");
		Map<String, Object> body = new HashMap<String, Object>();
		try {
			body = gdm.saveGroupDinnerRecord(reqBody, schema, op);
			body=SchemaUtil.setDictionaryMessageForForm(body, schema);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			logger.error("save "+schema+" is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "保存群宴登记信息失败");
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存群宴首次指导信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doSaveFirstGuide(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		GroupDinnerModel gdm = new GroupDinnerModel(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String op = (String) req.get("op");
		Map<String, Object> body = new HashMap<String, Object>();
		try {
			body = gdm.saveFirstGuide(reqBody, schema, op);
			body=SchemaUtil.setDictionaryMessageForForm(body, schema);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			logger.error("save "+schema+" is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "保存群宴首次指导信息失败");
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存群宴第二次指导信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doSaveSecondGuide(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		GroupDinnerModel gdm = new GroupDinnerModel(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String op = (String) req.get("op");
		Map<String, Object> body = new HashMap<String, Object>();
		try {
			body = gdm.saveSecondGuide(reqBody, schema, op);
			body=SchemaUtil.setDictionaryMessageForForm(body, schema);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			logger.error("save "+schema+" is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "保存群宴第二次指导信息失败");
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存群宴回访信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doSaveVisit(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		GroupDinnerModel gdm = new GroupDinnerModel(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String op = (String) req.get("op");
		Map<String, Object> body = new HashMap<String, Object>();
		try {
			body = gdm.saveVisit(reqBody, schema, op);
			body=SchemaUtil.setDictionaryMessageForForm(body, schema);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			logger.error("save "+schema+" is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "保存群宴回访信息失败");
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取群宴登记信息，查看是否有群宴首次执导信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 * @throws ModelDataOperationException
	 */
	protected void doGetGroupDinnerInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		GroupDinnerModel gdm = new GroupDinnerModel(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String gdrId = (String) reqBody.get("pkey");
		Map<String, Object> body = new HashMap<String, Object>();
		try {
			body = gdm.getGroupDinnerRecord(gdrId);
			body = SchemaUtil.setDictionaryMessageForForm(body,
					GDR_GroupDinnerRecord);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			logger.error("get GroupDinnerInfo is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "获取群宴管理信息失败");
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取群宴首次指导信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doGetFirstGuideInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		GroupDinnerModel gdm = new GroupDinnerModel(dao);
		@SuppressWarnings("rawtypes")
		List cnd = (List) req.get("cnd");
		String schema = (String) req.get("schema");
		Map<String, Object> body = new HashMap<String, Object>();
		try {
			body = gdm.getFirstGuideInfo(cnd, schema);
			body = SchemaUtil.setDictionaryMessageForForm(body, schema);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			logger.error("get FirstGuideInfo is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "获取群宴首次指导信息失败");
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取群宴第二次指导信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doGetSecondGuideInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		GroupDinnerModel gdm = new GroupDinnerModel(dao);
		@SuppressWarnings("rawtypes")
		List cnd = (List) req.get("cnd");
		String schema = (String) req.get("schema");
		Map<String, Object> body = new HashMap<String, Object>();
		try {
			body = gdm.getSecondGuideInfo(cnd, schema);
			body = SchemaUtil.setDictionaryMessageForForm(body, schema);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			logger.error("get SecondGuideInfo is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "获取群宴第二次指导信息失败");
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取群宴回访信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doGetVisitInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		GroupDinnerModel gdm = new GroupDinnerModel(dao);
		@SuppressWarnings("rawtypes")
		List cnd = (List) req.get("cnd");
		String schema = (String) req.get("schema");
		Map<String, Object> body = new HashMap<String, Object>();
		try {
			body = gdm.getVisitInfo(cnd, schema);
			body = SchemaUtil.setDictionaryMessageForForm(body, schema);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			logger.error("get VisitInfo is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "获取群宴回访信息失败");
			throw new ServiceException(e);
		}
	}

	/**
	 * 删除群宴信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doRemoveGroupDinnerRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		GroupDinnerModel gdm = new GroupDinnerModel(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String gdrId = (String) reqBody.get("pkey");
		String schema = (String) req.get("schema");
		try {
			gdm.removeGroupDinnerRecord(gdrId, schema);
		} catch (ModelDataOperationException e) {
			logger.error("remove GroupDinnerRecord is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "删除群宴信息失败");
			throw new ServiceException(e);
		}
	}
}
