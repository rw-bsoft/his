package phis.application.wjz.source;

import java.util.List;
import java.util.Map;

import javax.jws.soap.SOAPBinding;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
/**
 * 危机值处理service
 * @author caijy
 *
 */
public class WjzManageService  extends AbstractActionService implements DAOSupportable{
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-5-7
	 * @description 危机值提醒数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doQueryWjztx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		WjzManageModel model = new WjzManageModel(dao);
		try {
			Map<String,Object> m = model.queryWjztx(req,ctx);
			if(!req.containsKey("type")){
				res.put("body", m);
			}else{
				res.put("data", m.get("data"));
			}
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-5-8
	 * @description 保存危机值处理
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveWjzcz(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		WjzManageModel model = new WjzManageModel(dao);
		Map<String,Object> body=(Map<String,Object>)req.get("body");
		try {
			 model.saveWjzcz(body,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-5-15
	 * @description 危机值常用语查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doLoadWjzcyy(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		WjzManageModel model = new WjzManageModel(dao);
		try {
			 model.loadWjzcyy();
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-12-17
	 * @description 保存危机值数据
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SOAPBinding(style = SOAPBinding.Style.RPC)
	@SuppressWarnings("unchecked")
	protected void doSaveWjz(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		WjzManageModel model = new WjzManageModel(dao);
		List<Map<String,Object>>  body=(List<Map<String,Object>>)req.get("data");
		try {
			Map<String,Object> map_ret= model.saveWjz(body,ctx);
			res.putAll(map_ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
}
