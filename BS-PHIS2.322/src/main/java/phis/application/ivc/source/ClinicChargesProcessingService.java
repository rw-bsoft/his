package phis.application.ivc.source;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;

import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.print.PrintException;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleQuery;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;

public class ClinicChargesProcessingService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(ClinicChargesProcessingService.class);

	/**
	 * 查询是否有有效的发药窗口
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doLoadOpenPharmacyWin(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doLoadOpenPharmacyWin(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 病人查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryPerson(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doQueryPerson(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 卡号门诊号码切换
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doCheckCardOrMZHM(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doCheckCardOrMZHM(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveGhmx(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doSaveGhmx(body, res, ctx);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 单据查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ControllerException 
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryDJ(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException, ControllerException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		List<Object> cnds = new ArrayList<Object>();
		if (req.containsKey("cnd")) {
			cnds = (List<Object>) req.get("cnd");
		}
		try {
			ccpm.doQueryDJ(body, cnds, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 根据就诊序号查询单据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryDJByJZXH(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");

		try {
			ccpm.doQueryDJByJZXH(body, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 单据明细查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ControllerException 
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryDJDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ControllerException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		String brxz = req.get("brxz") + "";
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		String fpcx = req.get("fpcx") + "";
		try {
			ccpm.doQueryDJDetails(body, brxz, fpcx, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取发票号
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("static-access")
	public void doGetNotesNumber(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId() + "";
		String manageUnit = user.getManageUnitId();
		BSPHISUtil bpu = new BSPHISUtil();
		String fphm = bpu.getNotesNumberNotIncrement(uid, manageUnit, 2, dao,
				ctx);
		if (fphm == null) {
			throw new ServiceException("请先维护发票号码!");
		}
		res.put("body", fphm);
	}

	/**
	 * 获取发票号
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("static-access")
	public void doUpdateNotesNumber(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId() + "";
		String manageUnit = user.getManageUnitId();
		String body = (String) req.get("body");
		BSPHISUtil bpu = new BSPHISUtil();
		try {
			String fphm = bpu.updateNotesNumber(uid, manageUnit, 2, dao, body);
			res.put("body", fphm);
		} catch (ValidateException e) {
			throw new ServiceException("修改发票号出错！", e);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException("修改发票号出错！", e);
		}

	}

	/**
	 * 门诊结算
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveOutpatientSettlement(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			ccpm.doSaveOutpatientSettlement(body, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 复制发票号
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSaveCopyFphm(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doSaveCopyFphm(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}

	}

	/**
	 * 发票作废 发票查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryFphm(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		String fphm = (String) req.get("body");
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doQueryFphm(fphm, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 发票预作废
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doValidateBeforeVoidInvoice(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doValidateBeforeVoidInvoice(body, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 发票作废
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateVoidInvoice(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doUpdateVoidInvoice(body, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	// /**
	// * 省医保发票作废
	// *
	// * @param req
	// * @param res
	// * @param dao
	// * @param ctx
	// * @throws ServiceException
	// */
	// @SuppressWarnings("unchecked")
	// public void doUpdateVoidInvoiceSzyb(Map<String, Object> req,
	// Map<String, Object> res, BaseDAO dao, Context ctx)
	// throws ServiceException {
	// Map<String, Object> body = (Map<String, Object>) req.get("body");
	// ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
	// dao);
	// try {
	// ccpm.doUpdateVoidInvoiceSzyb(body, res, ctx);
	// } catch (ModelDataOperationException e) {
	// throw new ServiceException(e);
	// }
	// }

	/**
	 * 取消发票作废
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doUpdateCanceledVoidInvoice(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String fphm = (String) req.get("body");
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doUpdateCanceledVoidInvoice(fphm, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取当天作废发票
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryVoidInvoice(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId() + "";
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		SimpleQuery sq = new SimpleQuery();
		List<Object> queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = (List<Object>) req.get("cnd");
		}
		List<Object> cnd;
		try {
			cnd = (List<Object>) CNDHelper
					.toListCnd("['and',['and',['isNull',['$','a.JZRQ']],['eq',['$','a.CZGH'],['s','"
							+ uid
							+ "']]],['eq',['$','a.JGID'],['s','"
							+ manaUnitId + "']]]");
			queryCnd = cnd;
			req.put("cnd", queryCnd);
			sq.execute(req, res, ctx);
		} catch (ExpException e) {
			throw new ServiceException("获取当天作废发票出错！", e);
		}
	}

	/**
	 * 默认付款方式查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryPayment(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doQueryPayment(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * @Title: 特定付费方式应收款计算
	 * @Description: TODO(特定付费方式应收款计算)
	 * @param @param req
	 * @param @param res
	 * @param @param dao
	 * @param @param ctx
	 * @param @throws ServiceException 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public void doQueryPayTypes(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String payTypeStr = (String) req.get("payType");
		try {
			HashMap<String, Object> map = (HashMap<String, Object>) JacksonUtil
					.jsonToBean(payTypeStr, HashMap.class);

			// 创建业务对象并设值
			ClinicPayBO bo = new ClinicPayBO();
			Double amount = Double.valueOf(map.get("YSK") + "");
			bo.setAmount(BigDecimal.valueOf(amount));
			bo.setPayType(Integer.valueOf((String) map.get("SRFS")));
			bo.setScale(Integer.valueOf((String) map.get("FKJD")) - 1);
			// 由前台传来的应收款金额,付款精度,舍入方式计算应收款.
			PayContext context = new ClinicPayContext(bo);
			BigDecimal oprAmount = context.convertAmount();
			res.put("YSK", oprAmount.toString());
		} catch (Exception e) {
			throw new ServiceException("付费方式查询失败！", e);
		}
	}

	/**
	 * 收费发票查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryReceivablesInvoice(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doQueryReceivablesInvoice(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 收费病人查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryGhmx(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doQueryGhmx(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 自动插入收费项目
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSaveZDCR(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doSaveZDCR(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("自动插入收费项目失败！", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveMS_YJ02(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> YJXH = null;
		if (req.get("YJXH") != null) {
			YJXH = (Map<String, Object>) req.get("YJXH");
		}
		List<Map<String, Object>> list_FYXH = (List<Map<String, Object>>) req
				.get("list_FYXH");
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doSaveMS_YJ02(null, list_FYXH, YJXH, body, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("自动插入收费项目失败！", e);
		}
	}

	// /**
	// * 判断是否需要审核
	// *
	// * @param req
	// * @param res
	// * @param dao
	// * @param ctx
	// * @throws ServiceException
	// */
	// @SuppressWarnings("unchecked")
	// public void doQueryIsNeedVerify(Map<String, Object> req,
	// Map<String, Object> res, BaseDAO dao, Context ctx)
	// throws ServiceException {
	// ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
	// dao);
	// try {
	// Map<String, Object> body = (Map<String, Object>) req.get("body");
	// ccpm.doQueryIsNeedVerify(body, res, dao, ctx);
	// } catch (ModelDataOperationException e) {
	// throw new ServiceException(e);
	// }
	// }

	/**
	 * 打印真实发票
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveDyzsfp(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doSaveDyzsfp(req, res, dao, ctx);
			res.put(RES_CODE, "200");
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, 2000);
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 查询病人性质 add by liuxy
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doCheckPatient(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {

		Map<String, Object> body = ((Map<String, Object>) req.get("body"));
		// String fphm = body.get("fphm") + "";
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();
		// System.out.println(fphm);
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		// Map<String,String> body = new HashMap<String,String>();
		body.put("jgid", JGID);
		try {
			String a = ccpm.doCheckPatient(body, dao, ctx);
			// System.out.println(a);
			res.put("BRXM", a);
			res.put(RES_CODE, "200");
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, 2000);
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}

	/**
	 * 更新CF02,YJ02的SPBH
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateSPBH(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			ccpm.doUpdateSPBH(body, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 移动打印 成功后设置字段YDCZFPBD为“1”
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateYdczfpbd(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			ccpm.doUpdateYdczfpbd(body, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 退费处理 发票查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryTFFphm(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		String fphm = (String) req.get("body");
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doQueryTFFphm(fphm, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 退费处理 主表查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryTF01(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doQueryTF01(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 退费结算计算
	 * 
	 * @param body
	 * @param res
	 * @param ctx
	 */
	public void doQueryTFPayment(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doQueryTFPayment(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 退费结算
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws PersistentDataOperationException 
	 */
	public void doSaveRefundSettle(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doSaveRefundSettle(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 收费预结算
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveSettle(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doSaveSettle(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 药房处方划价保存
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSavePhamaryClinicInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doSavePhamaryClinicInfo(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 收费删除
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doRemoveSettle(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doRemoveSettle(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 发票打印
	 * 
	 * @throws PrintException
	 */
	public void doPrintMoth(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doPrintMoth(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-9-19
	 * @description 收费查询单据前查询是否挂了多个科室
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryKs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicChargesProcessingModel model = new ClinicChargesProcessingModel(dao);
		Map<String, Object> body=(Map<String, Object>) req.get("body");
		try {
			long ret=model.queryKs(body,ctx);
			res.put("num", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-9-19
	 * @description 收费病人科室列表
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryGHKS(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicChargesProcessingModel model = new ClinicChargesProcessingModel(dao);
		Map<String, Object> body=(Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryGHKS(body,ctx);
			res.put("body", ret.get("body"));
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
	 * @createDate 2014-9-19
	 * @description 查询收费是否要选择科室
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doCheckSFSFXZKS(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
			String SFSFXZKS = ParameterUtil.getParameter(UserRoleToken.getCurrent().getManageUnitId(),
					BSPHISSystemArgument.SFSFXZKS, ctx);
			res.put("SFSFXZKS", SFSFXZKS);
	}
	/**
	 * 健康证 默认信息参数查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryPhysicalMr(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doQueryPhysicalMr(req, res,  ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	/**
	 * 健康证 默认信息参数
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSavePhysicalMr(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doSavePhysicalMr(req, res,  ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	/**
	 * 健康证保存
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSavePhysical(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(dao);
		try {
			ccpm.doSavePhysical(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 卡号查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryCardNo(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doQueryCardNo(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doChangeSCMStatus(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(dao);
		ccpm.doChangeSCMStatus(req, res, ctx);
	}
}
