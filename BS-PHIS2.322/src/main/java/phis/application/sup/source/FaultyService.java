package phis.application.sup.source;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.dao.exception.DataAccessException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;

/**
* @ClassName: FaultyService
* @Description: TODO(报损管理)
* @author zhoufeng
* @date 2013-5-18 下午02:40:01
* 
*/
public class FaultyService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory.getLogger(FaultyService.class);
	/**
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws PersistentDataOperationException 
	* @Title: doSaveOrUpdaterFaulty
	* @Description: TODO(保存报损单)
	* @param @param req
	* @param @param res
	* @param @param dao
	* @param @param ctx
	* @param @throws ServiceException    设定文件
	* @return void    返回类型
	* @throws
	*/
	public void doSaveOrUpdaterFaulty(Map<String, Object> req, Map<String, Object> res,
	BaseDAO dao, Context ctx) throws ServiceException, PersistentDataOperationException, JsonParseException, JsonMappingException, IOException {
		FaultyModel model = new FaultyModel(dao);
			model.doSaveOrUpdaterFaulty(req, res, ctx);
	}
	/**
	 * 
	 * @description 单据状态获取
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetDjztByDjxh(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		FaultyModel model = new FaultyModel(dao);
		try {
			model.doGetDjztByDjxh(body, res);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws PersistentDataOperationException 
	 * @throws ServiceException 
	* @Title: doVerify
	* @Description: TODO(审核)
	* @param @param req
	* @param @param res
	* @param @param dao
	* @param @param ctx    设定文件
	* @return void    返回类型
	* @throws
	*/
	public void doVerify(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException, PersistentDataOperationException, JsonParseException, JsonMappingException, IOException{
		FaultyModel model = new FaultyModel(dao);
		try {
			model.verify(req, res,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * @throws ExpException 
	 * @throws NumberFormatException 
	* @Title: doUnVerify
	* @Description: TODO(弃审)
	* @param @param req
	* @param @param res
	* @param @param dao
	* @param @param ctx
	* @param @throws ServiceException
	* @param @throws PersistentDataOperationException    设定文件
	* @return void    返回类型
	* @throws
	*/
	public void doCancelVerify(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException, PersistentDataOperationException, NumberFormatException, ExpException{
		FaultyModel model = new FaultyModel(dao);
		try {
			model.unVerify(req, res,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * @throws ServiceException 
	 * @throws PersistentDataOperationException 
	* @Title: doGetTjslByWzzd
	* @Description: TODO(由WZXH获得推荐数量)
	* @param @param req
	* @param @param res
	* @param @param dao
	* @param @param ctx    设定文件
	* @return void    返回类型
	* @throws
	*/
	public void doGetTjslByWzzd(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws PersistentDataOperationException, ServiceException{
		FaultyModel model = new FaultyModel(dao);
		
		
		try {
			model.doGetTjslByWzzd(req, res,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	* @Title: doGetTjslByWzzd
	* @Description: TODO(回显资产编号)
	* @param @param req
	* @param @param res
	* @param @param dao
	* @param @param ctx
	* @param @throws PersistentDataOperationException
	* @param @throws ServiceException    设定文件
	* @return void    返回类型
	* @throws
	*/
	public void doGetZCBH(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws PersistentDataOperationException, ServiceException{
		FaultyModel model = new FaultyModel(dao);
		
		try {
			model.doGetZCBH(req, res,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * @throws ModelDataOperationException 
	 * @throws PersistentDataOperationException 
	 * @Title: doGetTjslByWzzd
	 * @Description: TODO(由WZXH获得推荐数量)
	 * @param @param req
	 * @param @param res
	 * @param @param dao
	 * @param @param ctx    设定文件
	 * @return void    返回类型
	 * @throws
	 */
	public void doButtonbarStaus(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws PersistentDataOperationException, ModelDataOperationException{
		FaultyModel model = new FaultyModel(dao);
		model.findButtonbarStaus(req, res,ctx);
	}
	/**
	 * @throws PersistentDataOperationException 
	 * @throws ValidateException 
	 * @throws NumberFormatException 
	* @Title: doCommit
	* @Description: TODO(记账)
	* @param @param req
	* @param @param res
	* @param @param dao
	* @param @param ctx    设定文件
	* @return void    返回类型
	* @throws
	*/
	public void doSaveCommit(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx) throws NumberFormatException, ValidateException, PersistentDataOperationException{
		FaultyModel model = new FaultyModel(dao);
		model.doSaveCommit(req, res,ctx);
	}
	
	public void doGetKSZCByks(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
		try {
			FaultyModel model = new FaultyModel(dao);
			model.doGetKSZCByks(req, res,ctx);
		} catch (DataAccessException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	public void doDelFaultyById(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
		FaultyModel model = new FaultyModel(dao);
		try {
			model.doDelFaultyById(req, res,ctx);
		} catch (PersistentDataOperationException e) {
			res.put(RES_CODE, 500);
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
}

