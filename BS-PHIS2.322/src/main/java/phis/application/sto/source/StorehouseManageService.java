package phis.application.sto.source;

import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
public class StorehouseManageService extends AbstractActionService implements
		DAOSupportable {
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-5
	 * @description 药品出库汇总查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadStorehouseOutSummary(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseInOutSummaryModel model = new StorehouseInOutSummaryModel(dao);
		try {
			Map<String, Object> ret =model.loadStorehouseOutSummary(body, req, ctx);
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
	 * @createDate 2013-11-6
	 * @description 药品入库汇总查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadStorehouseInSummary(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseInOutSummaryModel model = new StorehouseInOutSummaryModel(dao);
		try {
			Map<String, Object> ret = model.loadStorehouseInSummary(body,req, ctx);
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
	 * @createDate 2013-11-6
	 * @description 药库出库汇总明细信息查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadStorehouseOutSummaryDetailList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseInOutSummaryModel model = new StorehouseInOutSummaryModel(dao);
		try {
			Map<String, Object> ret = model.loadStorehouseInOutSummaryDetailList(
					body,req, 1, ctx);
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
	 * @createDate 2013-11-6
	 * @description 药库入库汇总明细信息查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadStorehouseInSummaryDetailList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseInOutSummaryModel model = new StorehouseInOutSummaryModel(dao);
		try {
			Map<String, Object> ret = model.loadStorehouseInOutSummaryDetailList(
					body,req, 2, ctx);
			res.put("body", ret.get("body"));
			res.put("totalCount", ret.get("totalCount"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e.getCode(), e.getMessage());
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-6
	 * @description 获取药品过期预警 截至日期 系统参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQuerySX_PREALARM(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseDrugsExpireTipsModel model = new StorehouseDrugsExpireTipsModel(dao);
		try {
			int ret = model.querySX_PREALARM(body, ctx);
			res.put("SX_PREALARM", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e.getCode(), e.getMessage());
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-7
	 * @description 药品过期提示查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadStorehouseDrugsExpireTipsList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseDrugsExpireTipsModel model = new StorehouseDrugsExpireTipsModel(dao);
		try {
			Map<String, Object> ret = model.loadStorehouseDrugsExpireTipsList(
					body,req, ctx);
			res.put("body", ret.get("body"));
			res.put("totalCount", ret.get("totalCount"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e.getCode(), e.getMessage());
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-21
	 * @description 药品私用信息作废和取消作废
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doInvalidPrivateMedicines(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseMedicineManageModel model = new StorehouseMedicineManageModel(dao);
		try {
			Map<String,Object> ret = model.invalidPrivateMedicines(body);
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
	 * @createDate 2013-11-21
	 * @description 药品私用信息保存(包括价格保存)
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMedicinesPrivateInformation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseMedicineManageModel model = new StorehouseMedicineManageModel(dao);
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			model.saveMedicinesPrivateInformation(op, body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-21
	 * @description 药品私用信息修改界面数据 查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadPirvateMedicinesInformation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseMedicineManageModel model = new StorehouseMedicineManageModel(dao);
		try {
			Map<String, Object> retMap = model
					.loadPirvateMedicinesInformation(body);
			res.put("body", retMap);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-21
	 * @description 价格注销和恢复
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doReomovePriceInformation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseMedicineManageModel model = new StorehouseMedicineManageModel(dao);
		Map<String, Object> record = (Map<String, Object>) req.get("body");
		int op = (Integer) req.get("op");
		try {
			Map<String, Object> ret = model.reomovePriceInformation(record, op,
					ctx);
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
	 * @createDate 2013-11-21
	 * @description 查询是否中心控制价格
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryControlPrices(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseMedicineManageModel model = new StorehouseMedicineManageModel(dao);
		try {
			int i = model.queryControlPrices(ctx);
			res.put("sfkz", i);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-21
	 * @description 私用药品修改价格时检查下是否有库存,出入库单,调价单
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryMedicinesStock(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseMedicineManageModel model = new StorehouseMedicineManageModel(dao);
		try {
			Map<String, Object> ret = model.queryMedicinesStock(body, ctx);
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
	 * @createDate 2013-11-21
	 * @description 查询批零加成
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryPljc(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		StorehouseMedicineManageModel model = new StorehouseMedicineManageModel(dao);
		try {
			double pljc = model.queryPljc(ctx);
			res.put("pljc", pljc);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-21
	 * @description 药品私用信息调入
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMedicinesPrivateImportInformation(
			Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ServiceException {
		StorehouseMedicineManageModel model = new StorehouseMedicineManageModel(dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		try {
			model.saveMedicinesPrivateImportInformation(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-21
	 * @description 待导入药品信息查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doMedicinesPrivateInformationList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseMedicineManageModel model = new StorehouseMedicineManageModel(dao);
		List<Object> cnds = (List<Object>) req.get("cnd");
		try {
			Map<String,Object> ret = model.medicinesPrivateInformationList(cnds,req, ctx);
			res.put("totalCount", ret.get("totalCount"));
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
	 * @createDate 2013-11-21
	 * @description 药库删除前验证
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doVerifiedUsing_yklb(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseBasicInfomationModel model = new StorehouseBasicInfomationModel(dao);
		try {
			Map<String, Object> ret = model.verifiedUsing_yklb(body);
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
	 * @createDate 2013-11-21
	 * @description 保存前判断名称是否重复
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRepeatInspection(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseBasicInfomationModel model = new StorehouseBasicInfomationModel(dao);
		try {
			Map<String, Object> ret = model.repeatInspection(body);
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
	 * @createDate 2013-11-22
	 * @description 查询药库是否已经初始化
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQuerySystemInit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseBasicInfomationModel model = new StorehouseBasicInfomationModel(dao);
		try {
			Map<String, Object> ret = model.querySystemInit(ctx);
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
	 * @createDate 2013-11-22
	 * @description 查询药库是否已经转账
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doInitialQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseBasicInfomationModel model = new StorehouseBasicInfomationModel(dao);
		try {
			Map<String, Object> ret = model.initialQuery(ctx);
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
	 * @createDate 2013-11-22
	 * @description 保存初始信息
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMedicinesStorehouseInitialData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseInitialBooksModel model = new StorehouseInitialBooksModel(dao);
		try {
			model.saveMedicinesStorehouseInitialData(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-22
	 * @description 初始账册form数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doInitialDataQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseInitialBooksModel model = new StorehouseInitialBooksModel(dao);
		try {
			Map<String, Object> ret = model.initialDataQuery(body, ctx);
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
	 * @createDate 2013-11-22
	 * @description 药库初始转账
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveInitialTransfer(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseInitialBooksModel model = new StorehouseInitialBooksModel(dao);
		try {
			Map<String, Object> ret = model.saveInitialTransfer(ctx);
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
	 * @createDate 2013-12-2
	 * @description 验证出入库方式是否已经被使用
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
		StorehouseCheckInOutWayModel model = new StorehouseCheckInOutWayModel(dao);
		try {
			Map<String,Object> ret= model.verifiedUsing(body);
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
	 * @createDate 2013-12-2
	 * @description 出库方式保存前判断
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRepeatInspection_ckfs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseCheckInOutWayModel model = new StorehouseCheckInOutWayModel(dao);
		try {
			Map<String, Object> ret = model.repeatInspection_ckfs(body, ctx);
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
	 * @createDate 2013-12-5
	 * @description 打开入库单提交页面前校验数据是否已经删除
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doVerificationCheckInDelete(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseCheckInOutModel model = new StorehouseCheckInOutModel(dao);
		try {
			Map<String, Object> map = model.verificationCheckInDelete(body);
			res.put(RES_CODE, map.get("code"));
			res.put(RES_MESSAGE, map.get("msg"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-5
	 * @description 删除入库
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveCheckInData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseCheckInOutModel model = new StorehouseCheckInOutModel(dao);
		try {
			Map<String, Object> ret = model.removeCheckInData(body);
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
	 * @createDate 2013-12-5
	 * @description 采购入库时间条件查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doDateQuery(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseCheckInOutModel model = new StorehouseCheckInOutModel(dao);
		try {
			List<String> l = model.dateQuery(body, ctx);
			res.put("body", l);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-5
	 * @description 页面查询条件里面的默认财务月份
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doInitDateQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseCheckInOutModel model = new StorehouseCheckInOutModel(dao);
		try {
			String date = model.initDateQuery(ctx);
			res.put("date", date);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-5
	 * @description 保存入库单
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCheckIn(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		StorehouseCheckInOutModel model = new StorehouseCheckInOutModel(dao);
		try {
			model.saveCheckIn(body, op);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-5
	 * @description 提交入库单
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCheckInToInventory(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseCheckInOutModel model = new StorehouseCheckInOutModel(dao);
		try {
			Map<String,Object> ret = model.saveCheckInToInventory(body, ctx);
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
	 * @createDate 2013-12-5
	 * @description
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadCheckInData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseCheckInOutModel model = new StorehouseCheckInOutModel(dao);
		try {
			Map<String, Object> ret = model.loadCheckInData(body);
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
	 * @createDate 2013-12-5
	 * @description 入库方式下拉框数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryCheckInWay(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseCheckInOutModel model = new StorehouseCheckInOutModel(dao);
		try {
			List<List<Object>> ret = model.queryCheckInWay(body, ctx);
			res.put("rkfs", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-11-12
	 * @description 根据定价公式计算零售价格
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doJsLsjg(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseCheckInOutModel model = new StorehouseCheckInOutModel(dao);
		double ret = model.jsLsjg(body);
		res.put("lsjg", ret);
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-5
	 * @description 财务验收审核前判断下有没未审核的明细
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doVerificationFinancialAcceptanceNum(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseFinancialAcceptanceModel model = new StorehouseFinancialAcceptanceModel(dao);
		try {
			Map<String, Object> ret = model
					.verificationFinancialAcceptanceNum(body);
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
	 * @createDate 2013-12-5
	 * @description 已验收入库单查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doFinancialAcceptanceDataQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		List<Object> cnd = (List<Object>) req.get("cnd");
		StorehouseFinancialAcceptanceModel model = new StorehouseFinancialAcceptanceModel(dao);
		try {
			Map<String, Object> ret = model.financialAcceptanceDataQuery(body,
					cnd, req, ctx);
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
	 * @createDate 2013-12-5
	 * @description 财务验收
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveFinancialAcceptance(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseFinancialAcceptanceModel model = new StorehouseFinancialAcceptanceModel(dao);
		try {
			model.saveFinancialAcceptance(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-5
	 * @description 查询药库财务验收药品扣率
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryYPKL(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		StorehouseFinancialAcceptanceModel model = new StorehouseFinancialAcceptanceModel(dao);
		try {
			double ret = model.queryYPKL(ctx);
			res.put("ypkl", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-5
	 * @description 入库单验收前判断是否已经验收和当月是否已经月结
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doVerificationFinancialAcceptance(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseFinancialAcceptanceModel model = new StorehouseFinancialAcceptanceModel(dao);
		try {
			Map<String, Object> ret = model.verificationFinancialAcceptance(body,
					ctx);
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
	 * @createDate 2013-12-6
	 * @description 修改确认前判断出库单是否已经删除或者确认
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doVerificationCheckOutDelete(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseCheckInOutModel model = new StorehouseCheckInOutModel(dao);
		try {
			Map<String, Object> ret = model.verificationCheckOutDelete(body);
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
	 * @createDate 2013-12-6
	 * @description 删除出库单
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveCheckOutData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseCheckInOutModel model = new StorehouseCheckInOutModel(dao);
		try {
			Map<String, Object> ret = model.removeCheckOutData(body);
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
	 * @createDate 2013-12-6
	 * @description 出库单退回
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCheckOutBack(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseCheckInOutModel model = new StorehouseCheckInOutModel(dao);
		try {
			Map<String, Object> ret = model.saveCheckOutBack(body);
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
	 * @createDate 2013-12-6
	 * @description 查询出库方式的对应方式和科室判别
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryDyfs(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseCheckInOutModel model = new StorehouseCheckInOutModel(dao);
		try {
			Map<String, Object> ret = model.queryDyfs(body, ctx);
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
	 * @createDate 2013-12-6
	 * @description 出库记录保存
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveStorehouseCheckOut(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseCheckInOutModel model = new StorehouseCheckInOutModel(dao);
		try {
			Map<String, Object> ret = model.saveStorehouseCheckOut(body, ctx);
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
	 * @createDate 2013-12-6
	 * @description 药库出库form记录查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryStorehouseCheckOut(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseCheckInOutModel model = new StorehouseCheckInOutModel(dao);
		try {
			Map<String, Object> ret = model.queryStorehouseCheckOut(body);
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
	 * @createDate 2013-12-6
	 * @description 查询药品的库存数量
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryKcsl(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseCheckInOutModel model = new StorehouseCheckInOutModel(dao);
		try {
			Map<String,Object> ret= model.queryKcsl(body, ctx);
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
	 * @createDate 2013-12-6
	 * @description 确认出库单
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveStorehouseCheckOutCommit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseCheckInOutModel model = new StorehouseCheckInOutModel(dao);
		try {
			Map<String, Object> ret = model.saveStorehouseCheckOutCommit(body,
					ctx);
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
	 * @createDate 2013-12-6
	 * @description 药库出库确认明细查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryCheckOutDetail(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseCheckInOutModel model = new StorehouseCheckInOutModel(dao);
		try {
			List<Map<String, Object>> ret = model.queryCheckOutDetail(body, ctx);
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
	 * @createDate 2013-12-9
	 * @description 账册库存查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doStockSearch(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		StorehouseStockManageModel model = new StorehouseStockManageModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		List<Object> cnd = (List<Object>) req.get("cnd");
		try {
			Map<String,Object> ret=model.stockSearch(body,cnd,req,ctx);
			res.put("totalCount", ret.get("totalCount"));
			res.put("totalJhje", ret.get("totalJhje"));
			res.put("totalLsje", ret.get("totalLsje"));
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
	 * @createDate 2013-12-9
	 * @description 查询实物库存
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doPhysicalDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseStockManageModel model = new StorehouseStockManageModel(dao);
		List<Object> cnd = (List<Object>) req.get("cnd");
		try {
			Map<String,Object> ret=model.physicalDetails(cnd,req,ctx);
			res.put("totalCount", ret.get("totalCount"));
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
	 * @createDate 2013-12-9
	 * @description 药房库存明细查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doPharmacyStockSearch(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseStockManageModel model = new StorehouseStockManageModel(dao);
		List<Object> cnd = (List<Object>) req.get("cnd");
		try {
			Map<String,Object> ret=model.pharmacyStockSearch(cnd,req,ctx);
			res.put("totalCount", ret.get("totalCount"));
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
	 * @createDate 2013-12-9
	 * @description 月终过账
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveStorehouseMonthly(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseMonthlyModel model = new StorehouseMonthlyModel(dao);
		try {
			Map<String, Object> ret = model.saveStorehouseMonthly(body, ctx);
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
	 * @createDate 2013-12-9
	 * @description 调价单删除
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemovePriceAdjustData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseMedicinesPriceAdjustModel model = new StorehouseMedicinesPriceAdjustModel(dao);
		try {
			Map<String, Object> ret = model.removePriceAdjustData(body);
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
	 * @createDate 2013-12-9
	 * @description 提交前判断调价单是否已经删除或提交
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doVerificationPriceAdjustDelete(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseMedicinesPriceAdjustModel model = new StorehouseMedicinesPriceAdjustModel(dao);
		try {
			Map<String, Object> map = model.verificationPriceAdjustDelete(body);
			res.put(RES_CODE, map.get("code"));
			res.put(RES_MESSAGE, map.get("msg"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-9
	 * @description 调价的条件中的调价日期范围查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doPriceAdjustDateQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseMedicinesPriceAdjustModel model = new StorehouseMedicinesPriceAdjustModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			List<String> l = model.priceAdjustDateQuery(body, ctx);
			res.put("body", l);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-9
	 * @description 药库调价单保存
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSavePriceAdjust(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		StorehouseMedicinesPriceAdjustModel model = new StorehouseMedicinesPriceAdjustModel(dao);
		try {
			model.savePriceAdjust(body, op, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-9
	 * @description 调价单执行数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryPriceAdjustExecutionData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<Object> cnd = (List<Object>) req.get("cnd");
		StorehouseMedicinesPriceAdjustModel model = new StorehouseMedicinesPriceAdjustModel(dao);
		try {
			List<Map<String, Object>> list = model
					.queryPriceAdjustExecutionData(cnd);
			res.put("body", list);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-9
	 * @description 药品调价查看数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryPriceAdjustExecutionedData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<Object> cnd = (List<Object>) req.get("cnd");
		StorehouseMedicinesPriceAdjustModel model = new StorehouseMedicinesPriceAdjustModel(dao);
		try {
			List<Map<String, Object>> list = model
					.queryPriceAdjustExecutionedData(cnd);
			res.put("body", list);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-9
	 * @description 执行调价单
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSavePriceAdjustToInventory(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<Object> cnd = (List<Object>) req.get("cnd");
		List<Map<String,Object>> body = (List<Map<String,Object>>) req.get("body");
		StorehouseMedicinesPriceAdjustModel model = new StorehouseMedicinesPriceAdjustModel(dao);
		try {
			Map<String, Object> ret = model.savePriceAdjustToInventory(body,cnd, ctx);
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
	 * @createDate 2013-12-10
	 * @description 删除盘点单
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveInventory(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseStoreroomInventoryModel model = new StorehouseStoreroomInventoryModel(dao);
		try {
			 Map<String,Object> ret=model.removeInventory(body);
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
	 * @createDate 2013-12-10
	 * @description 查询库存盘点是否按批次盘参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryKCPD_PC(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		StorehouseStoreroomInventoryModel model = new StorehouseStoreroomInventoryModel(dao);
		try {
			boolean ret = model.queryKCPD_PC(ctx);
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
	 * @createDate 2013-12-10
	 * @description 查询kcsb和实盘数量,用于保存的时候更新数据
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQuertyInventoryData_PC_KCSB(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = req.get("op")+"";
		StorehouseStoreroomInventoryModel model = new StorehouseStoreroomInventoryModel(dao);
		try {
			List<Map<String,Object>> ret = model.quertyInventoryData_PC_KCSB(body,op,ctx);
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
	 * @createDate 2013-12-10
	 * @description 盘点单保存
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveInventory(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = req.get("op")+"";
		StorehouseStoreroomInventoryModel model = new StorehouseStoreroomInventoryModel(dao);
		try {
			model.saveInventory(body,op,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-10
	 * @description 确认盘点单
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCommitInventory(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehouseStoreroomInventoryModel model = new StorehouseStoreroomInventoryModel(dao);
		try {
			Map<String,Object> ret= model.saveCommitInventory(body,ctx);
			res.put(RES_CODE, ret.get("code"));
			res.put(RES_MESSAGE,ret.get("msg"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-10
	 * @description 按批次盘存数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQuertyInventoryData_PC(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = req.get("op")+"";
		StorehouseStoreroomInventoryModel model = new StorehouseStoreroomInventoryModel(dao);
		try {
			Map<String,Object> ret= model.quertyInventoryData_PC(body,req,op,ctx);
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
	 * @createDate 2013-12-10
	 * @description 盘存明细数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQuertyInventoryDataDetail(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = req.get("op")+"";
		StorehouseStoreroomInventoryModel model = new StorehouseStoreroomInventoryModel(dao);
		try {
			List<Map<String,Object>> ret = model.quertyInventoryDataDetail(body,op,ctx);
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
	 * @createDate 2013-12-10
	 * @description 查询库存盘点明细 不按批次
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQuertyInventoryData(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = req.get("op")+"";
		StorehouseStoreroomInventoryModel model = new StorehouseStoreroomInventoryModel(dao);
		try {
			Map<String,Object> ret = model.quertyInventoryData(body,req,op,ctx);
			res.put("body", ret.get("body"));
			res.put("totalCount", ret.get("totalCount"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	// ===========>分割线
	/**
	 * 1111111111111111
	 * 
	 * @author
	 * @createDate 2013-11-29
	 * @description 付款处理数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryPayment(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehousePaymentModel model = new StorehousePaymentModel(dao);
		try {
			Map<String,Object> ret = model.queryPayment(req, ctx);
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
	 * @author
	 * @createDate 2013-11-29
	 * @description 付款处理明细查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryPaymentDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehousePaymentModel model = new StorehousePaymentModel(dao);
		try {
			Map<String,Object> ret =model.queryPaymentDetails(req,ctx);
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
	 * @author
	 * @createDate 2013-11-29
	 * @description 付款处理窗口
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryPaymentProcessing(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehousePaymentModel model = new StorehousePaymentModel(dao);
		try {
			List<Map<String,Object>> ret=model.queryPaymentProcessing(req, ctx);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author
	 * @createDate 2013-11-29
	 * @description 保存付款信息
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdatePaymentProcessing(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StorehousePaymentModel model = new StorehousePaymentModel(dao);
		try {
			model.updatePaymentProcessing(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	// 111111111
	/**
	 * 
	 * @author gaof
	 * @createDate 2013-12-5
	 * @description 保管员账簿列表查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetStoreManBook(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseStoreManBookModel model = new StorehouseStoreManBookModel(dao);
		try {
			Map<String,Object> ret=model.getStoreManBook(req);
			res.put("totalCount", ret.get("totalCount"));
			res.put("body", ret.get("body"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author gaof
	 * @createDate 2013-12-5
	 * @description 调价历史查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryPriceHistory(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehousePriceHistoryModel model = new StorehousePriceHistoryModel(dao);
		try {
			Map<String,Object> ret=model.queryPriceHistory(req);
			res.put("totalCount", ret.get("totalCount"));
			res.put("body", ret.get("body"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 
	 * @author lizhi
	 * @createDate 2017-11-16
	 * @description 调价历史查询(药房查询)
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryPriceHistory2(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehousePriceHistoryModel model = new StorehousePriceHistoryModel(dao);
		try {
			Map<String,Object> ret=model.queryPriceHistory2(req);
			res.put("totalCount", ret.get("totalCount"));
			res.put("body", ret.get("body"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author gaof
	 * @createDate 2013-12-5
	 * @description 调价历史明细查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryPriceHistoryDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehousePriceHistoryModel model = new StorehousePriceHistoryModel(dao);
		Map<String, Object> body=(Map<String, Object>) req.get("body");
		try {
			List<Map<String,Object>> ret=model.queryPriceHistoryDetails(body);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 
	 * @author lizhi
	 * @createDate 2017-11-16
	 * @description 调价历史明细查询(药房)
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryPriceHistoryDetails2(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehousePriceHistoryModel model = new StorehousePriceHistoryModel(dao);
		Map<String, Object> body=(Map<String, Object>) req.get("body");
		try {
			List<Map<String,Object>> ret=model.queryPriceHistoryDetails2(body);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author gaof
	 * @createDate 2013-12-5
	 * @description 查询药库采购历史模块中的药品记录
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQuerySPHDrugRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehousePurchaseHistoryModel model = new StorehousePurchaseHistoryModel(dao);
		Map<String, Object> body=(Map<String, Object>) req.get("body");
		try {
			List<Map<String,Object>> ret=model.querySPHDrugRecord(body);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author gaof
	 * @createDate 2013-12-5
	 * @description 查询药库采购历史模块中的药品明细
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQuerySPHDrugDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehousePurchaseHistoryModel model = new StorehousePurchaseHistoryModel(dao);
		Map<String, Object> body=(Map<String, Object>) req.get("body");
		try {
			List<Map<String,Object>> ret=model.querySPHDrugDetails(body);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author gaof
	 * @createDate 2013-12-5
	 * @description 查询药库采购历史模块中的供应商记录
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQuerySPHSupplierRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehousePurchaseHistoryModel model = new StorehousePurchaseHistoryModel(dao);
		Map<String, Object> body=(Map<String, Object>) req.get("body");
		try {
			List<Map<String,Object>> ret=model.querySPHSupplierRecord(body);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author gaof
	 * @createDate 2013-12-5
	 * @description 查询药库采购历史模块中的供应商明细
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQuerySPHSupplierDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehousePurchaseHistoryModel model = new StorehousePurchaseHistoryModel(dao);
		Map<String, Object> body=(Map<String, Object>) req.get("body");
		try {
			List<Map<String,Object>> ret=model.querySPHSupplierDetails(body);
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
	 * @createDate 2014-3-21
	 * @description 查询养护单明细
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryConservationDetail(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseMedicinesConservationModel model = new StorehouseMedicinesConservationModel(dao);
		Map<String, Object> body=(Map<String, Object>) req.get("body");
		String op=req.get("op")+"";
		try {
			Map<String,Object> ret=model.queryConservationDetail(body,op,req);
			res.put("body", ret.get("body"));
			//res.put("total", ret.get("total"));
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
	 * @createDate 2014-3-24
	 * @description 养护单保存
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveConservation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseMedicinesConservationModel model = new StorehouseMedicinesConservationModel(dao);
		Map<String, Object> body=(Map<String, Object>) req.get("body");
		String op=req.get("op")+"";
		try {
			Map<String,Object> ret=model.saveConservation(body,op);
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
	 * @createDate 2014-3-24
	 * @description 养护单确认
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveConservationCommit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseMedicinesConservationModel model = new StorehouseMedicinesConservationModel(dao);
		Map<String, Object> body=(Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.saveConservationCommit(body,ctx);
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
	 * @createDate 2014-3-24
	 * @description 删除养护单
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveConservation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseMedicinesConservationModel model = new StorehouseMedicinesConservationModel(dao);
		Map<String, Object> body=(Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.removeConservation(body);
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
	 * @createDate 2014-12-26
	 * @description 药库计划单自动计划
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryZdjh(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseProcurementPlanModel model = new StorehouseProcurementPlanModel(dao);
		try {
			List<Map<String, Object>> ret=model.queryZdjh();
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
	 * @createDate 2014-12-29
	 * @description 药库采购计划单保存
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCgjh(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseProcurementPlanModel model = new StorehouseProcurementPlanModel(dao);
		Map<String,Object> body=(Map<String,Object>)req.get("body");
		try {
			model.saveCgjh(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-12-30
	 * @description 查询计划单
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryJHDS(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseProcurementPlanModel model = new StorehouseProcurementPlanModel(dao);
		try {
			Map<String, Object> ret=model.queryJHDS(req);
			res.put("totalCount", ret.get("totalCount"));
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
	 * @createDate 2014-12-30
	 * @description 计划单明细form表单数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryJhdForm(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseProcurementPlanModel model = new StorehouseProcurementPlanModel(dao);
		long sbxh=Long.parseLong(req.get("pkey")+"");
		try {
			Map<String, Object> ret=model.queryJhdForm(sbxh);
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
	 * @createDate 2014-12-30
	 * @description 计划单明细list数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryJhdList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseProcurementPlanModel model = new StorehouseProcurementPlanModel(dao);
		List<?> cnd=(List<?> )req.get("cnd");
		String op=(String)req.get("op");
		try {
			List<Map<String, Object>> ret=model.queryJhdList(cnd,op);
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
	 * @createDate 2014-12-31
	 * @description 计划单删除
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveStorehouseProcurementPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseProcurementPlanModel model = new StorehouseProcurementPlanModel(dao);
		Map<String,Object> body=(Map<String,Object>)req.get("body");
		try {
			model.removeStorehouseProcurementPlan(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-1-4
	 * @description 采购入库-计划单引入
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryStorehouseProcurementPlanSelectRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseProcurementPlanModel model = new StorehouseProcurementPlanModel(dao);
		try {
			List<Map<String, Object>> ret=model.queryStorehouseProcurementPlanSelectRecord();
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
	 * @createDate 2015-1-4
	 * @description 采购入库-计划单明细查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryStorehouseProcurementPlanDetailRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseProcurementPlanModel model = new StorehouseProcurementPlanModel(dao);
		List<String> body=(List<String>)req.get("body");
		try {
			List<Map<String, Object>> ret=model.queryStorehouseProcurementPlanDetailRecord(body);
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
	 * @createDate 2015-1-9
	 * @description 药库会计账簿查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryStorehouseAccountingBooks(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseAccountingBooksModel model = new StorehouseAccountingBooksModel(dao);
		try {
			Map<String,Object> ret=model.queryStorehouseAccountingBooks(req,ctx);
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
	 * @createDate 2015-1-13
	 * @description 根据年月判断当月是否月结
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQuerySfyj(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		StorehouseAccountingBooksModel model = new StorehouseAccountingBooksModel(dao);
		Map<String,Object> body=(Map<String,Object>)req.get("body");
		try {
			Map<String,Object> ret=model.querySfyj(body);
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
	 * @createDate 2015-9-9
	 * @description 出库分析查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadOutBoundAnalysicList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
					throws ServiceException {
		//获取页面的参数
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		TjfxSTOModel model= new TjfxSTOModel(dao);
//		String ds = (String) body.get("ds");
//		String de = (String) body.get("de");
//		String sorttype = (String) body.get("sorttype");
//		String topnum = (String) body.get("topnum");
//		//模拟查询结果
//		List<PurchaseAnalysis> pa = new ArrayList<PurchaseAnalysis>();
//		PurchaseAnalysis pa1 = new PurchaseAnalysis("1", "抗震药物", "100", "200", "300", "100");
//		PurchaseAnalysis pa2 = new PurchaseAnalysis("2", "飞行药物", "100", "200", "300", "100");
//		pa.add(pa1);
//		pa.add(pa2);
//		res.put("body",pa);
//		res.put("totalCount",100);
		try {
			List<Map<String,Object>> ret =model.loadOutBoundAnalysicList(body);
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
	 * @createDate 2015-9-9
	 * @description 采购分析查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadPurchaseAnalysicList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
					throws ServiceException {
		//获取页面的参数
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		TjfxSTOModel model= new TjfxSTOModel(dao);
//		String ds = (String) body.get("ds");
//		String de = (String) body.get("de");
//		String sorttype = (String) body.get("sorttype");
//		String topnum = (String) body.get("topnum");
		//模拟查询结果
//		List<PurchaseAnalysis> pa = new ArrayList<PurchaseAnalysis>();
//		PurchaseAnalysis pa1 = new PurchaseAnalysis("1", "抗震药物", "100", "200", "300", "100");
//		PurchaseAnalysis pa2 = new PurchaseAnalysis("2", "飞行药物", "100", "200", "300", "100");
//		pa.add(pa1);
//		pa.add(pa2);
//		res.put("body",pa);
//		res.put("totalCount",100);
		try {
			List<Map<String,Object>> ret =model.loadPurchaseAnalysicList(body);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 抗菌药采购分析查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadAntiMicrobialPurchaseAnalysicList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
					throws ServiceException {
		//获取页面的参数
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		TjfxSTOModel model= new TjfxSTOModel(dao);
		try {
			List<Map<String,Object>> ret =model.loadAntiMicrobialPurchaseAnalysicList(body);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 抗菌药采购分析明细查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadAntiMicrobialPurchaseDetailList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
					throws ServiceException {
		//获取页面的参数
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		TjfxSTOModel model= new TjfxSTOModel(dao);
		try {
			List<Map<String,Object>> ret =model.loadAntiMicrobialPurchaseDetailList(body);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 抗菌药出库分析查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadAntiMicrobialOutBoundAnalysicList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
					throws ServiceException {
		//获取页面的参数
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		TjfxSTOModel model= new TjfxSTOModel(dao);
		try {
			List<Map<String,Object>> ret =model.loadAntiMicrobialOutBoundAnalysicList(body);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 抗菌药出库分析明细查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadAntiMicrobialOutBoundDetailList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
					throws ServiceException {
		//获取页面的参数
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		TjfxSTOModel model= new TjfxSTOModel(dao);
		try {
			List<Map<String,Object>> ret =model.loadAntiMicrobialOutBoundDetailList(body);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 查询库存数量
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doCountKcsl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
				throws ServiceException {
		StorehouseAccountingBooksModel model = new StorehouseAccountingBooksModel(dao);
		Map<String, Object> body=(Map<String, Object>) req.get("body");
		try {
			List<Map<String, Object>> ret =model.countKcsl(body, res, ctx);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 药库转移
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doChangeYKSB(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
				throws ServiceException {
		StorehouseAccountingBooksModel model = new StorehouseAccountingBooksModel(dao);
		Map<String, Object> body=(Map<String, Object>) req.get("body");
		try {
			model.changYKSBInfo(body, res, ctx);
		} catch (ModelDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 基本药物出库分析查询
	 * @author renwei  2020-08-28
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadBasicDrugOutAnalysicList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
					throws ServiceException {
		//获取页面的参数
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		TjfxSTOModel model= new TjfxSTOModel(dao);
		try {
			List<Map<String,Object>> ret =model.loadBasicDrugOutAnalysicList(body);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	
}
