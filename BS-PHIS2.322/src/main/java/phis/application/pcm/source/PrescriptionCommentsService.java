package phis.application.pcm.source;

import java.util.List;
import java.util.Map;

import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import phis.application.mds.source.MedicineUtils;
import phis.application.pcm.source.PrescriptionCommentsModel;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.ParameterUtil;

public class PrescriptionCommentsService extends AbstractActionService implements DAOSupportable{
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-8-20
	 * @description 处方点评抽取保存
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCfCQ(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		PrescriptionCommentsModel model = new PrescriptionCommentsModel(dao);
			Map<String,Object> body=(Map<String,Object>)req.get("body");
		try {
			Map<String,Object> ret = model.saveCfCQ(body);
			res.put(RES_CODE, ret.get("code"));
			res.put(RES_MESSAGE, ret.get("msg"));
			res.put("body", ret.get("body"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-8-21
	 * @description 处方点评-左边list查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryCQRQ(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		PrescriptionCommentsModel model = new PrescriptionCommentsModel(dao);
			Map<String,Object> body=(Map<String,Object>)req.get("body");
		try {
			List<Map<String,Object>> ret = model.queryCQRQ(body);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-8-25
	 * @description 问题维护-删除,注销,取消注销
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateCfWT(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		PrescriptionCommentsModel model = new PrescriptionCommentsModel(dao);
			Map<String,Object> body=(Map<String,Object>)req.get("body");
		try {
			Map<String,Object> ret = model.updateCfWT(body);
			res.put(RES_CODE, ret.get("code"));
			res.put(RES_MESSAGE, ret.get("msg"));
			res.put("body", ret.get("body"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-8-25
	 * @description 问题维护-保存
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCfWT(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		PrescriptionCommentsModel model = new PrescriptionCommentsModel(dao);
			Map<String,Object> body=(Map<String,Object>)req.get("body");
		try {
			Map<String,Object> ret = model.saveCfWT(body);
			res.put(RES_CODE, ret.get("code"));
			res.put(RES_MESSAGE, ret.get("msg"));
			res.put("body", ret.get("body"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-8-25
	 * @description 保存处方点评里面的合理和不合理
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCfdpWt(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		PrescriptionCommentsModel model = new PrescriptionCommentsModel(dao);
			Map<String,Object> body=(Map<String,Object>)req.get("body");
		try {
			model.saveCfdpWt(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-8-26
	 * @description 处方点评-删除,注销,取消注销
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateCfdp(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		PrescriptionCommentsModel model = new PrescriptionCommentsModel(dao);
			Map<String,Object> body=(Map<String,Object>)req.get("body");
		try {
			model.updateCfdp(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-8-27
	 * @description 查询处方点评结果记录
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryCfdpjg(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		PrescriptionCommentsModel model = new PrescriptionCommentsModel(dao);
			Map<String,Object> body=(Map<String,Object>)req.get("body");
		try {
			Map<String,Object> ret = model.queryCfdpjg(body);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-9-2
	 * @description 处方点评报表 list数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryTJBBSJ(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		PrescriptionCommentsModel model = new PrescriptionCommentsModel(dao);
			Map<String,Object> body=(Map<String,Object>)req.get("body");
		try {
			List<Map<String,Object>> ret = model.queryTJBBSJ(body);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-9-2
	 * @description 点评报表明细数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryTJBBSJMX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		PrescriptionCommentsModel model = new PrescriptionCommentsModel(dao);
			List<?> cnd=(List<?>)req.get("cnd");
			int pageSize=0;
			int pageNo=1;
			if(req.containsKey("pageSize")){
				pageSize = (Integer)req.get("pageSize");
			}
			if(req.containsKey("pageNo")){
				pageNo = (Integer)req.get("pageNo");
			}
		try {
			Map<String,Object> ret = model.queryTJBBSJMX(cnd,pageSize,pageNo);
			res.put("body", ret.get("body"));
			res.put("totalCount", ret.get("totalCount"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-9-22
	 * @description 查询处方点评抽取数量参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryCqsl(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		res.put("CFDPCQSL",ParameterUtil
				.getParameter(UserRoleToken.getCurrent().getManageUnit().getId(), BSPHISSystemArgument.CFDPCQSL, ctx));
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-9-17
	 * @description 自动点评接口数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryZddp(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		PrescriptionCommentsModel model = new PrescriptionCommentsModel(dao);
		long cyxh=MedicineUtils.parseLong(req.get("CYXH"));
		try {
			List<Map<String,Object>> ret = model.queryZddp(cyxh,ctx);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
}
