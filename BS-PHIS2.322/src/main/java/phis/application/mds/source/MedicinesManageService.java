package phis.application.mds.source;

import java.util.List;
import java.util.Map;

import ctd.util.exp.ExpException;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.ServiceCode;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 药品信息Service
 *
 * @author
 */
public class MedicinesManageService extends AbstractActionService implements DAOSupportable {


	/**
	 *
	 * @author caijy
	 * @createDate 2013-12-23
	 * @description 药品公共信息保存
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveMedicinesInfomation(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		MedicineManageModel mmm = new MedicineManageModel(dao);
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> map_ret = mmm.saveMedicinesInfomation(op, body, ctx);
			res.put(RES_CODE, map_ret.get("code"));
			res.put(RES_MESSAGE, map_ret.get("msg"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 *
	 * @author caijy
	 * @createDate 2012-6-4
	 * @description 药品公共信息作废和取消作废
	 * @updateInfo
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doInvalidMedicines(Map<String, Object> req,
								   Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		MedicineManageModel mmm = new MedicineManageModel(dao);
		try {
			Map<String,Object> map_ret = mmm.invalidMedicines(body);
			res.put(RES_CODE, map_ret.get("code"));
			res.put(RES_MESSAGE, map_ret.get("msg"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 *
	 * @author caijy
	 * @createDate 2012-8-30
	 * @description 药品信息导入
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMedicinesPrivateImportInformation(Map<String, Object> req,
														Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicineManageModel mmm = new MedicineManageModel(dao);
		Map<String,Object> body=(Map<String,Object>)req.get("body");
		try {
			mmm.saveMedicinesPrivateImportInformation(body,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 *
	 * @author caijy
	 * @createDate 2013-1-22
	 * @description 进货单位保存前判断
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doInspectionPurchaseUnits(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		MedicinesPurchaseUnitsModel mpm = new MedicinesPurchaseUnitsModel(dao);
		try {
			Map<String, Object> ret = mpm.inspectionPurchaseUnits(body);
			res.put(RES_CODE, ret.get("code"));
			res.put(RES_MESSAGE, ret.get("msg"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 *
	 * @author caijy
	 * @createDate 2012-7-30
	 * @description 验证是否有库存和入库(如果有最小包装不能修改)
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doVerifiedUsing(Map<String, Object> req,
								Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		MedicineManageModel mmm = new MedicineManageModel(dao);
		try {
			Map<String, Object> ret = mmm.verifiedUsing(body, ctx);
			res.put(RES_CODE, ret.get("code"));
			res.put(RES_MESSAGE, ret.get("msg"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 *
	 * @author caijy
	 * @createDate 2012-5-24
	 * @description 产地保存时验证产地名称有没重复
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doValidationManufacturerModeName(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		MedicinesManufacturerModel mmf = new MedicinesManufacturerModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = mmf.validationManufacturerModeName(body);
			res.put(RES_CODE, ret.get("code"));
			res.put(RES_MESSAGE, ret.get("msg"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 *
	 * @author caijy
	 * @createDate 2012-5-29
	 * @description 剂型保存前验证有没重复
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doPropertyAdd(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			MedicineCommonModel model=new MedicineCommonModel(dao);
			if(model.repeatVerification(body)){
				res.put(RES_CODE, ServiceCode.CODE_RECORD_REPEAT);
				res.put(RES_MESSAGE, "剂型名称已存在");
			}else{
				res.put(RES_CODE, ServiceCode.CODE_OK);
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
	 * @createDate 2012-5-29
	 * @description 发药方式保存前验证有没重复
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doDispensingAdd(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			MedicineCommonModel model=new MedicineCommonModel(dao);
			if(model.repeatVerification(body)){
				res.put(RES_CODE, ServiceCode.CODE_RECORD_REPEAT);
				res.put(RES_MESSAGE, "发药方式已存在");
			}else{
				res.put(RES_CODE, ServiceCode.CODE_OK);
			}
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 用药限制查询
	 *
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doLimitInformationList(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		MedicineManageModel mmm = new MedicineManageModel(dao);
		int ypxh = (Integer) req.get("ypxh");
		try {
			List<Map<String, Object>> ret = mmm.medicinesLimitList(ypxh,ctx);
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
	 * @createDate 2012-7-31
	 * @description 药品公共信息价格注销
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doReomovePriceInformation(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		MedicineManageModel mmm = new MedicineManageModel(dao);
		Map<String, Object> record = (Map<String, Object>) req.get("body");
		int op = (Integer) req.get("op");
		try {
			mmm.reomovePriceInformation(record, op);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 *
	 * @author caijy
	 * @createDate 2012-8-29
	 * @description 查询批零加成
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryPljc(Map<String, Object> req,
							Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			res.put("pljc", MedicineUtils.queryPljc(ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 *
	 * @author caijy
	 * @createDate 2013-12-6
	 * @description 用于删除时验证该数据是否被使用
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doVerifiedUsing_base(Map<String, Object> req,
									 Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			MedicineCommonModel model=new MedicineCommonModel(dao);
			if (model.repeatVerification(body)) {
				res.put(RES_CODE, ServiceCode.CODE_RECORD_USING);
			} else {
				res.put(RES_CODE, ServiceCode.CODE_OK);
			}
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 * @author yanghe
	 * @createDate 2020/9/15
	 * @description 药品数据导入功能
	 * @updateInfo
	 *
	 */
	public void doSaveZslypsjdr(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException, ExpException {
		List<Map<String,Object>> body = (List<Map<String,Object>>) req.get("body");
		try {
			MedicineManageModel model=new MedicineManageModel(dao);
			model.saveZslypsjdr(body, ctx);
//			res.put("YPXH", ypxh);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
}
