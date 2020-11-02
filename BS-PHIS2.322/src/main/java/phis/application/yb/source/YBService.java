package phis.application.yb.source;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class YBService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(YBService.class);
	
	/**
	 * 查询医保门诊挂号
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryMzghXmlByPath(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ParseException {
		YBModel yb = new YBModel(dao);
		try {
			yb.doQueryMzghXmlByPath(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询医保门诊挂号失败！", e);
		}
	}
	
	/**
	 * 查询医保挂号信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ParseException
	 */
	public void doQueryYbghxx(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ParseException {
		YBModel yb = new YBModel(dao);
		try {
			yb.doQueryYbghxx(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询医保门诊挂号信息失败！", e);
		}
	}
	
	/**
	 * 保存门诊就诊文件和药费、项目收费明细数据文件
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ParseException
	 * @throws ControllerException 
	 */
	public void doSaveMzjzxxAndCfsh(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ParseException, ControllerException{
		YBModel yb = new YBModel(dao);
		try {
			yb.doSaveMzjzxxAndCfsh(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("文件保存失败,请核对门诊挂号信息和医保自编码！", e);
		}
	}
	
	/**
	 * 查询医保系统门诊结算信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ParseException
	 */
	public void doQueryMzjsByPath(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ParseException {
		YBModel yb = new YBModel(dao);
		try {
			yb.doQueryMzjsByPath(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询医保住院登记失败！", e);
		}
	}

	/**
	 * 查询医保住院登记
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryXmlByPath(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ParseException {
		YBModel yb = new YBModel(dao);
		try {
			yb.doQueryXmlByPath(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询医保住院登记失败！", e);
		}
	}
	
	
	/**
	 * 查询住院病人列表
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ParseException
	 */
	public void doQueryZyBrry(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ParseException {
			YBModel yb = new YBModel(dao);
		try {
			yb.doQueryZyBrry(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询住院病人列表失败！", e);
		}
	}
	
	/**
	 * 查询住院费用明细
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ParseException
	 */
	public void doGetZyfymxQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ParseException {
			YBModel yb = new YBModel(dao);
		try {
			yb.doGetZyfymxQuery(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询住院病人列表失败！", e);
		}
	}
	
	/**
	 * 医保住院费用明细上载
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ParseException
	 * @throws ControllerException 
	 */
	public void doSaveZyfymx(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ParseException, UnknownHostException, ControllerException {
		YBModel yb = new YBModel(dao);
		try {
			yb.doSaveZyfymx(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("文件失败，请核对住院登记信息和医保自编码！", e);
		}
	}
	
	/**
	 * 查询医保出院结算
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryCyjsdXmlByPath(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ParseException {
		YBModel yb = new YBModel(dao);
		try {
			yb.doQueryCyjsdXmlByPath(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询医保出院结算失败！", e);
		}
	}
	
	/**
	 * 保存医保住院结算信息
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSaveZyjs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException {
		YBModel yb = new YBModel(dao);
		try {
			yb.doSaveZyjs(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("保存医保住院结算信息失败！", e);
		}
	}
	
	/**
	 *  医保人员对照,左边list数据查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryYbrydz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		YBModel model=new YBModel(dao);
		List<?> cnd=req.get("cnd")==null?null:(List<?>)req.get("cnd");
		try {
			model.queryYbrydz(cnd,req,res);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
		
	}
	
	/**
	 * 保存医保人员对照
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveYbrydz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		YBModel model=new YBModel(dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req.get("bodys");
		try {
			model.doSaveYbrydz(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 *  医保药品对照,左边list数据查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryYbypdz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		YBModel model=new YBModel(dao);
		List<?> cnd=req.get("cnd")==null?null:(List<?>)req.get("cnd");
		try {
			model.queryYbypdz(cnd,req,res);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
		
	}
	
	/**
	 * 保存医保药品对照
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveYbypdz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		YBModel model=new YBModel(dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req.get("bodys");
		try {
			model.doSaveYbypdz(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 *  医保项目对照,左边list数据查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryYbxmdz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		YBModel model=new YBModel(dao);
		List<?> cnd=req.get("cnd")==null?null:(List<?>)req.get("cnd");
		try {
			model.queryYbxmdz(cnd,req,res);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
		
	}
	
	/**
	 * 保存医保药品对照
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveYbxmdz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		YBModel model=new YBModel(dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req.get("bodys");
		try {
			model.doSaveYbxmdz(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 保存医保信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	public void doSavegrzhxx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx) {
		YBModel model=new YBModel(dao);
		try {
			model.doSavegrzhxx(req,res,dao,ctx);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询卡号是否存在
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doCheckHasCardInfo(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx) 
			throws ServiceException {
		YBModel model=new YBModel(dao);
		try {
			model.doCheckHasCardInfo(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 保存医保错误日志
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSaveYbErrorLogs(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx) 
			throws ModelDataOperationException {
		YBModel model=new YBModel(dao);
		String errMsg = req.get("errorMsg")==null?null:(String)req.get("errorMsg");
		model.doSaveYbErrorLogs(errMsg);
	}
	/**
	 * 药品通用品库编码对照 wy 2019年11月18日10:06:18
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSavetypk(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException {
		YBModel model = new YBModel(dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req.get("body");
		try {
			model.doSavetypk(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}

	/**
	 * 检查部位对照 wy  2019年11月18日15:46:00
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveJCBW(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException {
		YBModel model = new YBModel(dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req.get("body");
		try {
			model.doSaveJCBW(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

}
