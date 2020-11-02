package phis.application.pacs.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

public class JckdService extends AbstractActionService implements
DAOSupportable{
	protected Logger logger = LoggerFactory
			.getLogger(JckdService.class);
	/**
	 * 申请单查询服务
	 * @author caijy
	 * @createDate 2017-3-7
	 * @description 
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doExmrequestquery (Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		JckdModel model = new JckdModel(dao);
		String xml=MedicineUtils.parseString(req.get("xml"));
		try {
			 String reXml=model.exmrequestquery(xml,ctx);
			 res.put("data", reXml);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 * 检查执行通知
	 * @author caijy
	 * @createDate 2017-3-11
	 * @description 
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doExmrequestexecuted (Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		JckdModel model = new JckdModel(dao);
		String xml=MedicineUtils.parseString(req.get("xml"));
		try {
			 String reXml=model.exmrequestexecuted(xml,ctx);
			 res.put("data", reXml);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 * 机构服务字典
	 * @author caijy
	 * @createDate 2017-3-13
	 * @description 
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doOrganization (Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		JckdModel model = new JckdModel(dao);
		String xml=MedicineUtils.parseString(req.get("xml"));
		try {
			 String reXml=model.organization(xml,ctx);
			 res.put("data", reXml);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 * 科室服务字典
	 * @author caijy
	 * @createDate 2017-3-13
	 * @description 
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doDepatment (Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		JckdModel model = new JckdModel(dao);
		String xml=MedicineUtils.parseString(req.get("xml"));
		try {
			 String reXml=model.depatment(xml,ctx);
			 res.put("data", reXml);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 * 检查报告内容
	 * @author caijy
	 * @createDate 2017-3-15
	 * @description 
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doExmreport (Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		JckdModel model = new JckdModel(dao);
		String xml=MedicineUtils.parseString(req.get("xml"));
		try {
			 String reXml=model.exmreport(xml,ctx);
			 res.put("data", reXml);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 * 查询报告结果
	 * @author caijy
	 * @createDate 2017-3-17
	 * @description 
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doQueryBgjg (Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		JckdModel model = new JckdModel(dao);
		long sqid=MedicineUtils.parseLong(req.get("SQID"));
		try {
			 Map<String,Object> ret=model.queryBgjg(sqid,ctx);
			 res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
}
