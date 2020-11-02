package phis.application.pha.source;


import java.util.Map;    
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;

import java.util.List;       
import phis.source.service.DAOSupportable;

public class PharmacyManageService extends AbstractActionService implements DAOSupportable {
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-25
	 * @description 删除前验证是否被使用(入库方式和出库方式)
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
		PharmacyCheckInOutWayModel model = new PharmacyCheckInOutWayModel(dao);
		try {
			Map<String,Object> ret = model.verifiedUsing(body);
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
	 * @createDate 2013-11-25
	 * @description 保存前验证名称是否存在
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
		PharmacyCheckInOutWayModel model = new PharmacyCheckInOutWayModel(dao);
		try {
			Map<String,Object> ret = model.repeatInspection(body);
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
	 * @createDate 2013-11-25
	 * @description 药品私用信息导入
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSavePharmacyMedicinesInformations(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyMedicinesManageModel model = new PharmacyMedicinesManageModel(dao);
		try {
			int i = model.savePharmacyMedicinesInformations(ctx);
			if (i == -1) {
				res.put(RES_MESSAGE, "没有权限");
			} else {
				res.put(RES_MESSAGE, "已成功导入" + i + "条");
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
	 * @createDate 2013-11-25
	 * @description 药房药品作废或取消作废
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doValidationPharmacyMedicinesInvalid(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyMedicinesManageModel model = new PharmacyMedicinesManageModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model
					.validationPharmacyMedicinesInvalid(body);
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
	 * @createDate 2013-11-25
	 * @description 系统药房是否初始化查询
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
		PharmacyBasicInfomationModel model = new PharmacyBasicInfomationModel(dao);
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
	 * @createDate 2013-11-25
	 * @description 获取当前修改药房药品信息
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryPharmacyMedicinesInformation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyMedicinesManageModel model = new PharmacyMedicinesManageModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model
					.queryPharmacyMedicinesInformation(body);
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
	 * @createDate 2013-11-25
	 * @description 保存药房药品信息(修改包装)
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSavePharmacyMedicinesInformation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyMedicinesManageModel model = new PharmacyMedicinesManageModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			model.savePharmacyMedicinesInformation(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-25
	 * @description 验证药房药品是否有发生业务(发生业务后药房包装不能修改)
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doValidationPharmacyMedicinesPackage(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyMedicinesManageModel model = new PharmacyMedicinesManageModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret = model.validationPharmacyMedicinesPackage(body);
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
	 * @createDate 2013-11-25
	 * @description 药房账簿初始化跳转判断
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doInitialSignsQueries(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInitialBooksModel model = new PharmacyInitialBooksModel(dao);
		try {
			Map<String,Object> ret = model.initialSignsQueries(ctx);
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
	 * @createDate 2013-11-25
	 * @description 保存账簿初始化数据
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveInventory(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInitialBooksModel model = new PharmacyInitialBooksModel(dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		try {
			model.saveInventory(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-25
	 * @description 药房账簿初始化转账
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSavePharmacyTransfer(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInitialBooksModel model = new PharmacyInitialBooksModel(dao);
		try {
			model.pharmacyTransfer(ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-26
	 * @description 检查药房账簿是否已经初始化
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doInitialization(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInitialBooksModel model = new PharmacyInitialBooksModel(dao);
		try {
			Map<String,Object> map_ret = model.initialization(ctx);
			res.put(RES_CODE, map_ret.get("code"));
			res.put(RES_MESSAGE, map_ret.get("msg"));
			res.put("QYFYCK", map_ret.get("qyfyck"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-26
	 * @description  查询出入库是否删除
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
		PharmacyCheckInOutManageModel model = new PharmacyCheckInOutManageModel(dao);
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
	 * @createDate 2013-11-26
	 * @description 入库单删除
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
		PharmacyCheckInOutManageModel model = new PharmacyCheckInOutManageModel(dao);
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
	 * @createDate 2013-11-26
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
		PharmacyCheckInOutManageModel model = new PharmacyCheckInOutManageModel(dao);
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
	 * @createDate 2013-11-26
	 * @description 入库和出库的条件中的出入库日期范围查询
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
		PharmacyCheckInOutManageModel model = new PharmacyCheckInOutManageModel(dao);
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
	 * @createDate 2013-11-26
	 * @description 药品入库单记录保存
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
		PharmacyCheckInOutManageModel model = new PharmacyCheckInOutManageModel(dao);
		try {
			model.saveCheckIn(body, op, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-26
	 * @description 入库单提交
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
		PharmacyCheckInOutManageModel model = new PharmacyCheckInOutManageModel(dao);
		try {
			Map<String,Object> map_ret = model.saveCheckInToInventory(body);
			res.put(RES_CODE, map_ret.get("code"));
			res.put(RES_MESSAGE,map_ret.get("msg"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-26
	 * @description 入库保存和提交时判断有没超过中心控制最大价格
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryPriceChanges(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyCheckInOutManageModel model = new PharmacyCheckInOutManageModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> map_ret = model.queryPriceChanges(body, ctx);
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
	 * @createDate 2013-11-26
	 * @description 入库主表回填数据查询(由于入库方式是双主键的数据字典,故只能自己查)
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadCheckIn(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		PharmacyCheckInOutManageModel model = new PharmacyCheckInOutManageModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> map_ret = model.loadCheckIn(body, ctx);
			res.put("body", map_ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-26
	 * @description 打开出库单提交页面前校验数据是否已经删除
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
		PharmacyCheckInOutManageModel model = new PharmacyCheckInOutManageModel(dao);
		try {
			Map<String, Object> map = model.verificationCheckOutDelete(body);
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
	 * @createDate 2013-11-26
	 * @description 出库单删除
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
		PharmacyCheckInOutManageModel model = new PharmacyCheckInOutManageModel(dao);
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
	 * @createDate 2013-11-26
	 * @description 保存出库记录
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCheckOut(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		PharmacyCheckInOutManageModel model = new PharmacyCheckInOutManageModel(dao);
		try {
			model.saveCheckOut(body, op);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-26
	 * @description 出库单提交
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCheckOutToInventory(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyCheckInOutManageModel model = new PharmacyCheckInOutManageModel(dao);
		try {
			Map<String, Object> map = model.saveCheckOutToInventory(body, ctx);
			res.put(RES_CODE, map.get(RES_CODE));
			res.put(RES_MESSAGE, map.get(RES_MESSAGE));
			if (map.size() > 3) {
				res.put("ypxh", map.get("ypxh"));
				res.put("ypmc", map.get("ypmc"));
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
	 * @createDate 2013-11-26
	 * @description 药品出库明细数据查询(无库存也显示)
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryCheckOutToInventory(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyCheckInOutManageModel model = new PharmacyCheckInOutManageModel(dao);
		List<Object> cnds = (List<Object>) req.get("cnd");
		try {
			List<Map<String, Object>> ret = model.queryCheckOutToInventory(cnds,
					ctx);
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
	 * @createDate 2013-11-27
	 * @description 发药
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDispensing(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyDispensingModel model = new PharmacyDispensingModel(dao);
		try {
			Map<String, Object> map = model.saveDispensing(body, ctx);
			res.put(RES_CODE, map.get(RES_CODE));
			res.put(RES_MESSAGE, map.get(RES_MESSAGE));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-27
	 * @description 待发药记录查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryDispensing(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<Object> cnds = (List<Object>) req.get("cnd");
		PharmacyDispensingModel model = new PharmacyDispensingModel(dao);
		try {
			Map<String,Object> map_ret= model.queryDispensing(cnds, ctx);
			res.put("totalCount", map_ret.get("totalCount"));
			res.put("body", map_ret.get("body"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-27
	 * @description 处方信息查询(发药)
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryPrescribingInformation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyDispensingModel model = new PharmacyDispensingModel(dao);
		try {
			Map<String, Object> re = model.queryPrescribingInformation(body);
			res.put("body", re);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 * 
	 * @author zhaojian
	 * @createDate 2017-05-31
	 * @description 处方明细信息查询(发药)
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryPrescribingDetailInformation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyDispensingModel model = new PharmacyDispensingModel(dao);
		try {
			List<Map<String,Object>> list_ret = model.queryPrescribingDetailInformation(body);
			res.put("body", list_ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-27
	 * @description 药房自动刷新参数查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryPharmacyAutoRefresh(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyDispensingModel model = new PharmacyDispensingModel(dao);
		try {
			Map<String,Object> map_ret=model.queryPharmacyAutoRefresh(ctx);
			res.put("body", map_ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-27
	 * @description 获取当前窗口信息
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doLoadPharmacyWindowInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyDispensingModel model = new PharmacyDispensingModel(dao);
		try {
			Map<String, Object> body = model.doLoadPharmacyWindowInfo(ctx);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-27
	 * @description 更新发药窗口状态
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSavePharmacyWindowStatus(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyDispensingModel model = new PharmacyDispensingModel(dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			model.doSavePharmacyWindowStatus(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-28
	 * @description 取消发药保存
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveBackMedicine(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyDispensingModel model = new PharmacyDispensingModel(dao);
		try {
			model.saveBackMedicine(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-4-15
	 * @description 部分退药保存
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveBackPartMedicine(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyDispensingModel model = new PharmacyDispensingModel(dao);
		try {
			model.saveBacPartkMedicine(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-28
	 * @description 处方信息查询(取消发药)
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryBackMedicinePrescribingInformation(
			Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyDispensingModel model = new PharmacyDispensingModel(dao);
		try {
			Map<String, Object> re = model
					.queryBackMedicinePrescribingInformation(body);
			res.put("body", re);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-28
	 * @description 处方信息list查询(取消发药)
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
//	@SuppressWarnings("unchecked")
//	public void doQueryPharmacyDispensingDetail(Map<String, Object> req,
//			Map<String, Object> res, BaseDAO dao, Context ctx)
//			throws ServiceException {
//		Map<String, Object> body = (Map<String, Object>) req.get("body");
//		PharmacyDispensingModel model = new PharmacyDispensingModel(dao);
//		try {
//			List<Map<String,Object>> list_ret=model.doQueryPharmacyDispensingDetail(body, ctx);
//			res.put("body", list_ret);
//		} catch (ModelDataOperationException e) {
//			res.put(RES_CODE, e.getCode());
//			res.put(RES_MESSAGE, e.getMessage());
//			throw new ServiceException(e);
//		}
//	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-29
	 * @description 查询库存记录
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryInventory(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInventoryManageModel model = new PharmacyInventoryManageModel(dao);
		try {
			List<Object> cnd=null;
			if(req.get("cnd") != null){
				cnd = (List<Object>) req.get("cnd");
			}
			Map<String,Object> map_ret=model.queryInventory(cnd,req,ctx);
			res.put("totalCount", map_ret.get("totalCount"));
			res.put("totalJhje", map_ret.get("totalJhje"));
			res.put("totalLsje", map_ret.get("totalLsje"));
			res.put("body", map_ret.get("body"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-29
	 * @description 禁用或者取消禁用库存
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDisableInventory(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInventoryManageModel model = new PharmacyInventoryManageModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			model.saveDisableInventory(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-29
	 * @description 药房已申领和未申领数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryMedicinesApply(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyApplyAndRefundModel model = new PharmacyApplyAndRefundModel(dao);
		try {
			Map<String, Object> ret = model.queryMedicinesApply(body, req,
					ctx);
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
	 * @createDate 2013-11-29
	 * @description 修改确认前判断领药单是否被删除和确认
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doVerificationApplyDelete(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyApplyAndRefundModel model = new PharmacyApplyAndRefundModel(dao);
		try {
			Map<String, Object> ret = model.verificationApplyDelete(body);
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
	 * @createDate 2013-11-29
	 * @description 删除领药单
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveApplyData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyApplyAndRefundModel model = new PharmacyApplyAndRefundModel(dao);
		try {
			Map<String,Object> ret = model.removeApplyData(body);
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
	 * @createDate 2013-11-29
	 * @description 领药单提交
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateApplyData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyApplyAndRefundModel model = new PharmacyApplyAndRefundModel(dao);
		try {
			model.updateApplyData(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-29
	 * @description 查询领药库房
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryStorehouse(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyApplyAndRefundModel model = new PharmacyApplyAndRefundModel(dao);
		try {
			List<List<Object>> ret = model.queryStorehouse(ctx);
			res.put("yklb", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-29
	 * @description 查询领药方式
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryCkfs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyApplyAndRefundModel model = new PharmacyApplyAndRefundModel(dao);
		try {
			int ckfs = model.queryCkfs(body,ctx);
			res.put("ckfs", ckfs);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-29
	 * @description 药品领药单保存
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMedicinesApply(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyApplyAndRefundModel model = new PharmacyApplyAndRefundModel(dao);
		try {
			Map<String,Object> ret = model.saveMedicinesApply(body,ctx);
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
	 * @createDate 2013-11-29
	 * @description 领药单确认
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMedicinesApplyCommit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyApplyAndRefundModel model = new PharmacyApplyAndRefundModel(dao);
		try {
			Map<String, Object> ret = model.saveMedicinesApplyCommit(body, ctx);
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
	 * @createDate 2013-11-29
	 * @description 药品申领查询药库库存
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryKcsl_yfyk(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyApplyAndRefundModel model = new PharmacyApplyAndRefundModel(dao);
		try {
			double kcsl = model.queryKcsl_yfyk(body, ctx);
			res.put("kcsl", kcsl);
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
	 * @description 领药方式查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryReceiveWay(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyBasicInfomationModel model = new PharmacyBasicInfomationModel(dao);
		try {
			Map<String, Object> ret =model.queryReceiveWay(body, ctx);
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
	 * @description 查询领药方式
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doListReceiveWay(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyBasicInfomationModel model = new PharmacyBasicInfomationModel(dao);
		try {
			List<Map<String, Object>> ret=model.listReceiveWay(body, ctx);
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
	 * @createDate 2013-12-2
	 * @description 保存领药方式
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveReceiveWay(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		PharmacyBasicInfomationModel model = new PharmacyBasicInfomationModel(dao);
		try {
			model.saveReceiveWay(body, ctx);
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
	 * @description 重置领药方式
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateReceiveWay(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		PharmacyBasicInfomationModel model = new PharmacyBasicInfomationModel(dao);
		try {
			Map<String,Object> map_ret=model.updateReceiveWay(body, ctx);
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
	 * @createDate 2013-12-2
	 * @description 领药方式维护查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryReceiveWayMaintain(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyBasicInfomationModel model = new PharmacyBasicInfomationModel(dao);
		try {
			List<Map<String, Object>> reList=model.queryReceiveWayMaintain(body, ctx);
			res.put("body", reList);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-3
	 * @description 药房确认退药和未确认退药数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryMedicinesApplyRefund(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyApplyAndRefundModel model = new PharmacyApplyAndRefundModel(dao);
		try {
			Map<String, Object> ret = model.queryMedicinesApplyRefund(body, req,
					ctx);
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
	 * @createDate 2013-12-3
	 * @description 申领退药方式查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryTyCkfs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyApplyAndRefundModel model = new PharmacyApplyAndRefundModel(dao);
		try {
			Map<String,Object> ret = model.queryTyCkfs(body,ctx);
			if(ret==null){
				res.put(RES_CODE, 9000);
				res.put(RES_MESSAGE, "请先维护药库出库[申领退药]的对应方式!");
			}else{
			res.put("ckfs", ret.get("CKFS"));
			res.put("fsmc", ret.get("FSMC"));}
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-3
	 * @description 提交药房退药单
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMedicinesApplyRefundCommit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyApplyAndRefundModel model = new PharmacyApplyAndRefundModel(dao);
		try {
			Map<String, Object> ret = model.saveMedicinesApplyRefundCommit(body, ctx);
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
	 * @createDate 2013-12-3
	 * @description 申领退药明细查询(用于解决库存数量为0不能关联出记录的问题)
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryMedicinesApplyRefundDetail(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		List<Object> cnd = (List<Object>) req.get("cnd");
		PharmacyApplyAndRefundModel model = new PharmacyApplyAndRefundModel(dao);
		try {
			List<Map<String,Object>> ret = model.queryMedicinesApplyRefundDetail(cnd);
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
	 * @createDate 2013-12-3
	 * @description 调拨申请数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryMedicinesRequisition(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<Object> cnds = (List<Object>) req.get("cnd");
		PharmacyAllocationModel model = new PharmacyAllocationModel(dao);
		try {
			Map<String,Object> ret = model.queryMedicinesRequisition(cnds, req,
					ctx);
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
	 * @createDate 2013-12-3
	 * @description 调拨申请单修改提交前判断是否已经被删除
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doVerificationMedicinesRequisitionDelete(
			Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ServiceException {
		PharmacyAllocationModel model = new PharmacyAllocationModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model
					.verificationMedicinesRequisitionDelete(body);
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
	 * @createDate 2013-12-3
	 * @description 调拨申请单删除
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveMedicinesRequisition(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyAllocationModel model = new PharmacyAllocationModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.removeMedicinesRequisition(body);
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
	 * @createDate 2013-12-3
	 * @description 调拨申请单提交
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMedicinesRequisitionSubmit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyAllocationModel model = new PharmacyAllocationModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.saveMedicinesRequisitionSubmit(body);
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
	 * @createDate 2013-12-3
	 * @description 调拨出库 明细查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryMedicinesRequisitionDetailData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		List<Object> cnds = (List<Object>) req.get("cnd");
		PharmacyAllocationModel model = new PharmacyAllocationModel(dao);
		try {
			Map<String,Object> ret= model.queryMedicinesRequisitionDetailData(body,
					cnds, ctx);
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
	 * @createDate 2013-12-3
	 * @description 调拨申请单保存
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMedicinesRequisition(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyAllocationModel model = new PharmacyAllocationModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		try {
			Map<String, Object> ret = model.saveMedicinesRequisition(body, op,
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
	 * @createDate 2013-12-3
	 * @description 调拨申请确认
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveRequisitionCommit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyAllocationModel model = new PharmacyAllocationModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.saveRequisitionCommit(body, ctx);
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
	 * @createDate 2013-12-3
	 * @description 调拨申请查询当前药房药品库存
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryKcsl_yfdbsq(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyAllocationModel model = new PharmacyAllocationModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.queryKcsl_yfdbsq(body, ctx);
			res.put("body", ret);
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
	 * @createDate 2013-12-3
	 * @description 查询目标药房药品数量
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryTarHouseStore(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyAllocationModel model = new PharmacyAllocationModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.queryTarHouseStore(body, ctx);
			res.put("body", ret);
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
	 * @createDate 2013-12-3
	 * @description 调拨确认界面打开前判断是否已经确认
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doVerificationDeployInventorySubmit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyAllocationModel model = new PharmacyAllocationModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.verificationDeployInventorySubmit(body);
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
	 * @createDate 2013-12-3
	 * @description 调拨出库退回
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDeployInventoryBack(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyAllocationModel model = new PharmacyAllocationModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.saveDeployInventoryBack(body);
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
	 * @createDate 2013-12-3
	 * @description 调拨出库确认 设计上是写 出库的时候 当前KCSB的数量不够
	 *              要去同种价格的药减库存,由于当前程序写的是同一产地的药品只有一个库存的(特殊情况,药房发完药,再申领退药
	 *              不考虑),所以暂时kcsb对应 的没数量就终止
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDeployInventoryCommit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyAllocationModel model = new PharmacyAllocationModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.saveDeployInventoryCommit(body, ctx);
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
	 * @createDate 2013-12-3
	 * @description 调拨退药确认
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveInventoryBackCommit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyAllocationModel model = new PharmacyAllocationModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.saveInventoryBackCommit(body, ctx);
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
	 * @createDate 2013-12-4
	 * @description 盘点处理-开始
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSavePharmacyInventoryProcessing(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInventoryProcessingModel model = new PharmacyInventoryProcessingModel(dao);
		try {
			Map<String, Object> ret = model.savePharmacyInventoryProcessing(ctx);
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
	 * @createDate 2013-12-4
	 * @description 药房盘点录入汇总
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSavePharmacyInventoryProcessingHz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInventoryProcessingModel model = new PharmacyInventoryProcessingModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.savePharmacyInventoryProcessingHz(
					body, ctx);
			res.put(RES_CODE, ret.get("code"));
			res.put(RES_MESSAGE, ret.get("msg"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 药房盘点前判断下是否有变动
	 * @author caijy
	 * @createDate 2017-5-22
	 * @description 
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doCheckPharmacyInventoryProcessingWc(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInventoryProcessingModel model = new PharmacyInventoryProcessingModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.checkPharmacyInventoryProcessingWc(
					body, ctx);
			res.put(RES_CODE, ret.get("code"));
			res.put(RES_MESSAGE, ret.get("msg"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 盘点输入自动校准-修改实盘数量,盘前数量=当前库存数量
	 * @author caijy
	 * @createDate 2017-5-23
	 * @description 
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSavePdZdjz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInventoryProcessingModel model = new PharmacyInventoryProcessingModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.savePdZdjz(
					body, ctx);
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
	 * @createDate 2013-12-4
	 * @description 盘点处理完成
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSavePharmacyInventoryProcessingWc(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInventoryProcessingModel model = new PharmacyInventoryProcessingModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.savePharmacyInventoryProcessingWc(
					body, ctx);
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
	 * @createDate 2013-12-4
	 * @description 删除盘点单
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemovePharmacyInventoryProcessing(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInventoryProcessingModel model = new PharmacyInventoryProcessingModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model
					.removePharmacyInventoryProcessing(body);
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
	 * @createDate 2013-12-4
	 * @description 查询药房盘点参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryKCPD_PC(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInventoryProcessingModel model = new PharmacyInventoryProcessingModel(dao);
		try {
			int ret = model.queryKCPD_PC(ctx);
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
	 * @createDate 2013-12-4
	 * @description 获取盘点状态(是否完成 是否汇总,谁完成)
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryState_pc(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInventoryProcessingModel model = new PharmacyInventoryProcessingModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			List<Map<String, Object>> ret = model.queryState_pc(body, ctx);
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
	 * @createDate 2013-12-4
	 * @description 药房盘点录修改数量
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSavePharmacyInventoryProcessingXgsl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInventoryProcessingModel model = new PharmacyInventoryProcessingModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			model.savePharmacyInventoryProcessingXgsl(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-4
	 * @description 盘点处理完成盈亏金额查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryPharmacyInventoryProcessingWc(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInventoryProcessingModel model = new PharmacyInventoryProcessingModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.queryPharmacyInventoryProcessingWc(
					body, ctx);
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
	 * @createDate 2013-12-4
	 * @description 盘点录入初始数据增加
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSavePharmacyInventoryInitData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInventoryProcessingModel model = new PharmacyInventoryProcessingModel(dao);
		try {
			Map<String, Object> ret = model.savePharmacyInventoryInitData(ctx);
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
	 * @createDate 2013-12-4
	 * @description 删除盘点录入
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemovePharmacyInventoryEntry(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInventoryProcessingModel model = new PharmacyInventoryProcessingModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.removePharmacyInventoryEntry(body,
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
	 * @createDate 2013-12-4
	 * @description 保存盘点单
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSavePharmacyInventoryEntry(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInventoryProcessingModel model = new PharmacyInventoryProcessingModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			model.savePharmacyInventoryEntry(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-4
	 * @description 完成/取消完成 盘点单
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSavePharmacyInventoryEntryWc(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInventoryProcessingModel model = new PharmacyInventoryProcessingModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			model.savePharmacyInventoryEntryWc(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-19
	 * @description 重置,为了将实盘数量变成0 用于删除盘点单
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSavePharmacyInventoryEntryCz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInventoryProcessingModel model = new PharmacyInventoryProcessingModel(dao);
		try {
			model.savePharmacyInventoryEntryCz(ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-3-10
	 * @description 盘点录入新增
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSavePharmacyInventoryEntryAdd(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyInventoryProcessingModel model = new PharmacyInventoryProcessingModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.savePharmacyInventoryEntryAdd(body);
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
	 * @createDate 2013-12-4
	 * @description 药品月终过账时间查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryMedicinesAccountingStatementDate(
			Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ServiceException {
		PharmacyStatementModel model = new PharmacyStatementModel(dao);
		try {
			Map<String, Object> ret = model
					.queryMedicinesAccountingStatementDate(ctx);
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
	 * @createDate 2013-12-4
	 * @description 月结
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMedicinesAccountingStatementDate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyStatementModel model = new PharmacyStatementModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.saveMedicinesAccountingStatementDate(
					body, ctx);
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
	 * @createDate 2013-12-12
	 * @description 检验新增时药房窗口编号是否重复
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doWindowNumberRepeatInspection(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyBasicInfomationModel model = new PharmacyBasicInfomationModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret= model.windowNumberRepeatInspection(body);
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
	 * @createDate 2013-12-12
	 * @description 更新窗口信息(双主键并且主键值可以修改故需要特别写方法处理)
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateWindowInformation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyBasicInfomationModel model = new PharmacyBasicInfomationModel(dao);
		try {
			Map<String,Object> ret = model.updateWindowInformation(body);
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
	 * @createDate 2013-12-12
	 * @description 药房注销
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doPharmacyCancellation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyBasicInfomationModel model = new PharmacyBasicInfomationModel(dao);
		try {
			Map<String, Object> ret = model.pharmacyCancellation(body, ctx);
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
	 * @createDate 2012-12-28
	 * @description 药房界面 查询是否有领药科室
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doDepartmentSearch(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyBasicInfomationModel model = new PharmacyBasicInfomationModel(dao);
		try {
			Map<String,Object> ret = model.departmentSearch(ctx);
			res.put(RES_CODE, ret.get("code"));
			res.put(RES_MESSAGE, ret.get("msg"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	//分割开始===================================================>
	
	
	/**
	 * 查询病人处方信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryPrescription(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		PharmacyPrescriptionAuditModel model = new PharmacyPrescriptionAuditModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryPrescription(body, req, ctx);
			res.put("totalCount", ret.get("totalCount"));
			res.put("body", ret.get("body"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}


	/**
	 * 检查系统启用门诊审方标志
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryPharmacyAuditEnable(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyPrescriptionAuditModel model = new PharmacyPrescriptionAuditModel(dao);
		try {
			String ret = model.queryAuditPharmacyEnable(ctx);
			res.put("enable", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 查询处方审核明细
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryAuditPrescriptionDetail(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyPrescriptionAuditModel model = new PharmacyPrescriptionAuditModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			List<Map<String, Object>> ret=model.queryAuditPrescriptionDetail(body,ctx);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 全部审核未审核的处方信息为审核状态
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateToAdoptAll(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		PharmacyPrescriptionAuditModel model = new PharmacyPrescriptionAuditModel(dao);
		try {
			model.updateToAdoptAll(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 * 处方明细审核明细保存
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveAuditPrescription(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		PharmacyPrescriptionAuditModel model = new PharmacyPrescriptionAuditModel(dao);
		try {
			model.saveAuditPrescription(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	//1111111111111111111        12/2
	
	/**
	 * 查询病人处方明细
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryPrescriptionDetail(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyPrescriptionAuditModel model = new PharmacyPrescriptionAuditModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			res.put("body", model.queryPrescriptionDetail(body, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 获取药房药品对账明细信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryBalanceDetailInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyBalanceModel model = new PharmacyBalanceModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryBalanceDetailInfo(body, req, ctx);
			res.put("totalCount", ret.get("totalCount"));
			res.put("body", ret.get("body"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 对账明细初始化
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doBalanceDetailInit(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		PharmacyBalanceModel model = new PharmacyBalanceModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.queryBalanceDetailInit(body, ctx);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 获取药房药品对账汇总信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryBalanceSummaryInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyBalanceModel model = new PharmacyBalanceModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryBalanceSummaryInfo(body, req, ctx);
			res.put("totalCount", ret.get("totalCount"));
			res.put("body", ret.get("body"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 对账初始化
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	
	public void doBalanceInitialization(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyBalanceModel model = new PharmacyBalanceModel(dao);
		try {
			List<String> dates = model.queryBalanceInitialization(ctx);
			res.put("body", dates);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * @author zhangh
	 * @createDate 2013-5-30
	 * @description 对账日期范围查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doDateBalanceQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PharmacyBalanceModel model = new PharmacyBalanceModel(dao);
		try {
			List<String> l = model.dateBalanceQuery(body, ctx);
			res.put("body", l);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 药房发药统计查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doYffytjQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PharmacyStatisticalModel model = new PharmacyStatisticalModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try{
			List<Map<String, Object>> list = model.yffytjQuery(body, ctx);
			res.put("body", list);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 药房发药统计明细查询    11/29   yun
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doYffyDetailsQuery(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException{
		PharmacyStatisticalModel model = new PharmacyStatisticalModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try{
			Map<String,Object> ret=model.yffyDetailsQuery(body,req, ctx);
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
	 * @createDate 2014-6-24
	 * @description 发药统计,发药明细查询(按药品)
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doYffyDetailsDetailsQuery(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException{
		PharmacyStatisticalModel model = new PharmacyStatisticalModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try{
			List<Map<String,Object>> list_ret=model.yffyDetailsDetailsQuery(body, ctx);
			res.put(Service.RES_BODY, list_ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
//	/**
//	 * 
//	 * @author zhouyl
//	 * @createDate 2013-10-09
//	 * @description 查询药房是否初始建账
//	 * @updateInfo
//	 * @param req
//	 * @param res
//	 * @param dao
//	 * @param ctx
//	 * @throws ServiceException
//	 */
//	public void doDrugstoreInitialQuery(Map<String, Object> req,
//			Map<String, Object> res, BaseDAO dao, Context ctx)
//			throws ServiceException {
//		PharmacyManageModel mmd = new PharmacyManageModel(
//				dao);
//		try {
//			int code = mmd.drugstoreInitialQuery(ctx);
//			res.put(RES_CODE, code);
//		} catch (ModelDataOperationException e) {
//			res.put(RES_CODE, e.getCode());
//			res.put(RES_MESSAGE, e.getMessage());
//			throw new ServiceException(e);
//		}
//	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-4-29
	 * @description 药房高低储查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryYFGDC(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		PharmacyInventoryManageModel model = new PharmacyInventoryManageModel(
				dao);
		String pydm=(String)req.get("PYDM");
		try {
			List<Map<String,Object>> ret = model.queryYFGDC(pydm,ctx);
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
	 * @description 药房保管员账簿明细List查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryPharmacyCustodianBooksDetail(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx)throws ServiceException {
		PharmacyCustodianBooksModel model=new PharmacyCustodianBooksModel(dao);
		Map<String,Object> body=(Map<String,Object>)req.get("body");
		try {
			List<Map<String,Object>> ret = model.queryPharmacyCustodianBooksDetail(body);
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
	 * @createDate 2015-1-16
	 * @description 药房失效药品查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryPharmacyDrugsExpireTips(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx)throws ServiceException {
		PharmacyDrugsExpireTipsModel model=new PharmacyDrugsExpireTipsModel(dao);
		Map<String,Object> body=(Map<String,Object>)req.get("body");
		try {
			Map<String, Object> ret = model.queryPharmacyDrugsExpireTips(body,req);
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
		PharmacyMaintainModel model = new PharmacyMaintainModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = req.get("op") + "";
		try {
			Map<String, Object> ret = model.queryConservationDetail(body, op,
					req);
			res.put("body", ret.get("body"));
			// res.put("total", ret.get("total"));
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
		PharmacyMaintainModel model = new PharmacyMaintainModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = req.get("op") + "";
		try {
			Map<String, Object> ret = model.saveConservation(body, op);
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
		PharmacyMaintainModel model = new PharmacyMaintainModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.saveConservationCommit(body, ctx);
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
		PharmacyMaintainModel model = new PharmacyMaintainModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.removeConservation(body);
			res.put(RES_CODE, ret.get("code"));
			res.put(RES_MESSAGE, ret.get("msg"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
}
