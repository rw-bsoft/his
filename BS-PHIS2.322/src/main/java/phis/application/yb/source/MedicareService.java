package phis.application.yb.source;

import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class MedicareService extends AbstractActionService implements DAOSupportable{


	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-16
	 * @description 查询数据库有没该医保卡信息
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryYBKXX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		MedicareModel model=new MedicareModel(dao);
		try {
			Map<String,Object> ret=model.queryYBKXX(body);
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
	 * @createDate 2015-7-16
	 * @description 保存医保卡信息
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveYBKXX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		
		//TODO	这里根据不同医保类型产生不同的model类
		String yblx = body.get("yblx").toString();		
		MedicareModel model = createModelProfile(yblx);
		
		//MedicareModel model=new MedicareModel(dao);
		model.setDao(dao);
		try {
			Map<String,Object> ret=model.saveYBKXX(body);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	private MedicareModel createModelProfile(String type) {
        return ModelManager.createVpnProfile(Enum.valueOf(YbModelType.class, type));
    }
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-16
	 * @description 查询病人性质是否是医保,并返回具体是什么类型的医保
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryYbbrxz(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		MedicareModel model = new MedicareModel(dao);
		try {
			long brxz=MedicineUtils.parseLong(req.get("body"));
			int ret=model.queryYbbrxz(brxz);
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
	 * @createDate 2015-7-16
	 * @description  获取挂号登记,上传,预结算,结算参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryYbGhjscs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel model = new MedicareModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryYbGhjscs(body,ctx);
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
	 * @createDate 2015-7-21
	 * @description 医保挂号退号参数查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryYbThcs(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		MedicareModel model = new MedicareModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryYbThcs(body,ctx);
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
	 * @createDate 2015-7-27
	 * @description 根据读卡信息查询病人的MZHM
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryOutpatientAssociation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel model = new MedicareModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryOutpatientAssociation(body);
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
	 * @createDate 2015-7-27
	 * @description 获取门诊收费登记,上传,预结算,结算参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryYbMzjscs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel model=new MedicareModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryYbMzjscs(body);
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
	 * @createDate 2015-7-27
	 * @description 医保结算成功,本地结算失败 用于查询取消结算的参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryMzqxjscs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel model=new MedicareModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryMzqxjscs(body);
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
	 * @createDate 2015-7-29
	 * @description 获取医保发票作废参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryYbFpzfcs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel model=new MedicareModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryYbFpzfcs(body);
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
	 * @createDate 2015-8-13
	 * @description 获取医保入院登记参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryYbRydjcs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel model=new MedicareModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryYbRydjcs(body,ctx);
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
		 * @createDate 2015-8-14
		 * @description 查询医保住院病人性质转换参数
		 * @updateInfo
		 * @param req
		 * @param res
		 * @param dao
		 * @param ctx
		 * @throws ServiceException
		 */
	@SuppressWarnings("unchecked")
	public void doQueryYbZyxzzhcs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel model=new MedicareModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryYbZyxzzhcs(body,ctx);
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
	 * @createDate 2015-8-14
	 * @description 医保性质转成成功,更新入院表
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateRydj(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel model=new MedicareModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			model.updateRydj(body,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
		
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-8-17
	 * @description 根据医保卡信息查询住院号码
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryZyhmByYbkxx(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel model=new MedicareModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			String ret=model.queryZyhmByYbkxx(body);
			res.put("ZYHM", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
		
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-8-17
	 * @description 获取医保费用上传,住院预结算,住院结算参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryYbZyjscs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel model=new MedicareModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryYbZyjscs(body);
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
	 * @createDate 2015-8-17
	 * @description 更新费用表的费用上传标志
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateFyScbz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel model=new MedicareModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			model.updateFyScbz(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
		
	}

	

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-8-17
	 * @description  查询住院取消结算参数(结算失败)
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryYbZyqxjscs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel model=new MedicareModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryYbZyqxjscs(body);
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
	 * @createDate 2015-8-17
	 * @description 查询住院结算作废参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryYbzyzfcs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel model=new MedicareModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryYbzyzfcs(body);
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
	 * @createDate 2015-8-19
	 * @description 保存医保药品对照
	 * @updateInfo
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
		MedicareModel model=new MedicareModel(dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req.get("body");
		try {
			model.saveYbypdz(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
		
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-8-19
	 * @description 医保药品对照,左边list数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryYbypdz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel model=new MedicareModel(dao);
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
	 * 
	 * @author caijy
	 * @createDate 2015-8-19
	 * @description 保存医保费用对照
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveYbfydz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel model=new MedicareModel(dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req.get("body");
		try {
			model.saveYbfydz(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
		
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-8-19
	 * @description 医保费用对照,左边list数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryYbfydz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel model=new MedicareModel(dao);
		List<?> cnd=req.get("cnd")==null?null:(List<?>)req.get("cnd");
		try {
			model.queryYbfydz(cnd,req,res);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
		
	}
	
	/**
	 * 查询医保信息在病人档案里是否已经存在
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryBrxx(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		MedicareModel mm = new MedicareModel(dao);
		try {
			Map<String, Object> mapbody = (Map<String, Object>) req.get("body");
			mm.doQueryBrxx(mapbody, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, 2000);
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 保存参保病人基本信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveNJJBBRXX(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel mm = new MedicareModel(dao);
		try {
			Map<String, Object> mapbody = (Map<String, Object>) req.get("body");
			mm.doSaveNJJBBRXX(mapbody, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, 2000);
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * @description 读社保卡 回调收费界面(入院登记)的卡号
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryOutpatientAssociationNew(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		MedicareModel mm2 = new MedicareModel(dao);
		try {
			String sfzh=req.get("SFZH")+"";
			String mzgl = mm2.queryOutpatientAssociationforSFZH(sfzh,ctx);
			res.put("MZGL", mzgl);
			res.put("SMKQDJL", mm2.doQueryADelSMK_QDJL(ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, 2000);
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 保存产保信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCBRYJBXX(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel mm = new MedicareModel(dao);
		try {
			Map<String, Object> mapbody = (Map<String, Object>) req.get("body");
			mm.doSaveCBRYJBXX(mapbody, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, 2000);
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * @description 入院登记
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRydj(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel mm = new MedicareModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = mm.doRydj(body,ctx);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 
	 * @description 住院病人费用上传
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doHopitalFeeUp(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		MedicareModel mm = new MedicareModel(dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			Map<String,Object> ret = mm.doHopitalFeeUp(body,ctx);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, 2000);
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 
	 * @description 住院处方上传成功，更新本地信息
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMedicarePrescriptions(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		MedicareModel mm = new MedicareModel(dao);
		try {
			List<Map<String, Object>> map_body = (List<Map<String, Object>>)req.get("body");
			mm.doSaveMedicarePrescriptions(map_body,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, 2000);
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 查询是否有结算记录
	 */
	public void doQueryNJJBjsjl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel mm = new MedicareModel(dao);
		try {
			long count = mm.queryNJJBjsjl(req, ctx);
			res.put(RES_CODE, 200);
			res.put("jsjl", count);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, 2000);
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 修改ZY_FYMX表里面的SCBZ为0 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveScbz(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		MedicareModel mm = new MedicareModel(dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			mm.doRemoveScbz(body,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, 2000);
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
}
