package phis.application.hph.source;

import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
public class HospitalPharmacyService extends AbstractActionService implements DAOSupportable{
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-10
	 * @description 发药
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveHospitalPharmacyDispensing(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HospitalPharmacyDispensingModel model = new HospitalPharmacyDispensingModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.saveHospitalPharmacyDispensing(
					body, ctx);
			res.put(RES_CODE, ret.get("code"));
			res.put(RES_MESSAGE, ret.get("msg"));
			res.put("otherRet", ret.get("otherRet"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-11
	 * @description 医嘱发药全退
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMedicineFullRefund(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HospitalPharmacyDispensingModel model = new HospitalPharmacyDispensingModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			model.saveMedicineFullRefund(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-11
	 * @description 医嘱退回
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMedicineRefund(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HospitalPharmacyDispensingModel model = new HospitalPharmacyDispensingModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			model.saveMedicineRefund(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-11
	 * @description 查询药房是否已经维护领药科室
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryMedicineDepartment(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		HospitalPharmacyDispensingModel model = new HospitalPharmacyDispensingModel(dao);
		try {
			Map<String,Object> ret=model.queryMedicineDepartment(ctx);
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
	 * @createDate 2013-12-11
	 * @description 病区待发药记录查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryDispensingWard(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HospitalPharmacyDispensingModel model = new HospitalPharmacyDispensingModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			List<Map<String, Object>> ret = model.queryDispensingWard(body,ctx);
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
	 * @createDate 2013-12-11
	 * @description 待发药记录按病人查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryDispensing_br(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		HospitalPharmacyDispensingModel model = new HospitalPharmacyDispensingModel(dao);
		List<?> cnd = (List<?>) req.get("cnd");
		try {
			List<Map<String, Object>> ret=model.queryDispensing_br(cnd,ctx);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
//	/**
//	 * 
//	 * @author caijy
//	 * @createDate 2014-9-1
//	 * @description 待发药记录按病区查询
//	 * @updateInfo
//	 * @param req
//	 * @param res
//	 * @param dao
//	 * @param ctx
//	 * @throws ServiceException
//	 */
//	public void doQueryDispensing_bq(Map<String, Object> req,
//			Map<String, Object> res, BaseDAO dao, Context ctx)
//	throws ServiceException {
//		HospitalPharmacyDispensingModel model = new HospitalPharmacyDispensingModel(dao);
//		List<?> cnd = (List<?>) req.get("cnd");
//		try {
//			List<Map<String, Object>> ret=model.queryDispensing_bq(cnd,ctx);
//			res.put("body", ret);
//		} catch (ModelDataOperationException e) {
//			res.put(RES_CODE, e.getCode());
//			res.put(RES_MESSAGE, e.getMessage());
//			throw new ServiceException(e);
//		}
//	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-11
	 * @description 发药药品明细查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryDispensing(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		HospitalPharmacyDispensingModel model = new HospitalPharmacyDispensingModel(dao);
		List<?> cnd = (List<?>) req.get("cnd");
		try {
			List<Map<String,Object>> body=model.queryDispensing(cnd,ctx);
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
	 * @createDate 2013-12-11
	 * @description 病区退药
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveHospitalPharmacyBackMedicine(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		HospitalPharmacyBackMedicineModel model = new HospitalPharmacyBackMedicineModel(dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req.get("body");
		try {
			Map<String,Object> ret=model.saveHospitalPharmacyBackMedicine(body, ctx);
			res.put("otherRet", ret.get("otherRet"));
			if(ret.get("msg")!=null&&ret.get("msg")!=""&&(ret.get("msg")+"").length()>0){
				res.put(RES_CODE, 9000);
				res.put(RES_MESSAGE, ret.get("msg"));
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
	 * @createDate 2013-12-11
	 * @description 病区退药全部退回病区
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveBackMedicineFullRefund(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		HospitalPharmacyBackMedicineModel model = new HospitalPharmacyBackMedicineModel(dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req.get("body");
		try {
			model.saveBackMedicineFullRefund(body,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-11
	 * @description 病区退药退回病区
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveBackMedicineRefund(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		HospitalPharmacyBackMedicineModel model = new HospitalPharmacyBackMedicineModel(dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req.get("body");
		try {
			model.saveBackMedicineRefund(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-11
	 * @description 病区待退药病区记录查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryBackMedicineWard(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HospitalPharmacyBackMedicineModel model = new HospitalPharmacyBackMedicineModel(dao);
		try {
			List<Map<String, Object>> ret = model.queryBackMedicineWard(ctx);
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
	 * @createDate 2013-12-11
	 * @description 病区待退药记录查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryBackMedicine(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HospitalPharmacyBackMedicineModel model = new HospitalPharmacyBackMedicineModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			List<Map<String, Object>> ret = model.queryBackMedicine(body, ctx);
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
	 * @createDate 2013-12-11
	 * @description 发药查询上面病区下拉框数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryWard(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		HospitalPharmacyHistoryDispensingModel model = new HospitalPharmacyHistoryDispensingModel(dao);
		try {
			List<List<Object>> ret=model.queryWard(ctx);
			res.put("bqs", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-11
	 * @description 发药汇总查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryCollectRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		HospitalPharmacyHistoryDispensingCollectModel model = new HospitalPharmacyHistoryDispensingCollectModel(dao);
		Map<String,Object> body = (Map<String,Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryCollectRecords(body,req,ctx);
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
	 * @createDate 2014-4-22
	 * @description 住院记账-入院病人信息查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadZyxx(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		HospitalPharmacyAccountingModel model = new HospitalPharmacyAccountingModel(dao);
		Map<String,Object> body = (Map<String,Object>) req.get("body");
		try {
			Map<String,Object> ret=model.loadZyxx(body);
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
	 * @createDate 2014-4-22
	 * @description 住院记账保存
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveHospitalPharmacyAccounting(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		HospitalPharmacyAccountingModel model = new HospitalPharmacyAccountingModel(dao);
		Map<String,Object> body = (Map<String,Object>) req.get("body");
		try {
			model.saveHospitalPharmacyAccounting(body,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-5-13
	 * @description 住院记账数量输入负数时查询已经计费的费用明细的价格放到前台显示
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryJzmx(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		HospitalPharmacyAccountingModel model = new HospitalPharmacyAccountingModel(dao);
		Map<String,Object> body = (Map<String,Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryJzmx(body,ctx);
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
	 * 获取药品组套明细
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doLoadMedcineSet(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		HospitalPharmacyAccountingModel model = new HospitalPharmacyAccountingModel(dao);
		try {
			model.doLoadMedcineSet(req,res,dao,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取药品信息失败!", e);
		}
	}
	
	/**
	 * 病区发药按提交单查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
//	public void doQueryFyjltjd(Map<String, Object> req,
//			Map<String, Object> res, BaseDAO dao, Context ctx)
//	throws ServiceException {
//		HospitalPharmacyDispensingModel model = new HospitalPharmacyDispensingModel(dao);
//		List<?> cnd = (List<?>) req.get("cnd");
//		try {
//			List<Map<String, Object>> ret=model.queryFyjltjd(cnd,ctx);
//			res.put("body", ret);
//		} catch (ModelDataOperationException e) {
//			res.put(RES_CODE, e.getCode());
//			res.put(RES_MESSAGE, e.getMessage());
//			throw new ServiceException(e);
//		}
//	}
	public void doQueryFyjltjd(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HospitalPharmacyDispensingModel model = new HospitalPharmacyDispensingModel(dao);
		try {
			model.queryFyjltjd(req, res, dao,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 病区发药按病人查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
//	public void doQueryFyjlbr(Map<String, Object> req,
//							  Map<String, Object> res, BaseDAO dao, Context ctx)
//			throws ServiceException {
//		HospitalPharmacyDispensingModel model = new HospitalPharmacyDispensingModel(dao);
//		List<?> cnd = (List<?>) req.get("cnd");
//		try {
//			List<Map<String, Object>> ret=model.queryFyjlbr(cnd,ctx);
//			res.put("body", ret);
//		} catch (ModelDataOperationException e) {
//			res.put(RES_CODE, e.getCode());
//			res.put(RES_MESSAGE, e.getMessage());
//			throw new ServiceException(e);
//		}
//	}

	/**
	 * 病区发药按病人查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryFyjlbr(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HospitalPharmacyDispensingModel model = new HospitalPharmacyDispensingModel(dao);
		try {
			model.queryFyjlbr(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 查询病人发药明细
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryBRFYMX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		HospitalPharmacyDispensingModel model = new HospitalPharmacyDispensingModel(dao);
		try {
			model.queryBRFYXM(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
}
