/**
 * @description 收费项目
 * 
 * @author shiwy 2012.06.29
 */
package phis.application.war.source;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.cic.source.ClinicManageModel;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class WardPatientManageService extends AbstractActionService implements
		DAOSupportable {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(WardPatientManageService.class);

	@SuppressWarnings("unchecked")
	public void doLoadWardRemindInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			WardPatientManageModule wmm = new WardPatientManageModule(dao);
			res.put("body", wmm.doLoadWardRemindInfo(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doLoadZyhByZyhm(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			WardPatientManageModule wmm = new WardPatientManageModule(dao);
			res.put("body", wmm.doLoadZyhByZyhm(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doLoadZyhByCwh(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			WardPatientManageModule wmm = new WardPatientManageModule(dao);
			res.put("body", wmm.doLoadZyhByCwh(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 载入病人及床位信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doLoadBedPatientInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		WardConsultationSqModel wardConsultationSqModel = new WardConsultationSqModel(
				dao);
		UserRoleToken user = UserRoleToken.getCurrent();
		try {
			Object ksdm = req.get("KSDM");
			if (ksdm != null) {

				if (Long.parseLong(ksdm.toString()) == 2) {

					// List<Map<String, Object>> hgList =
					// wardConsultationSqModel
					// .queryYGBH(user.getUserId()+"");
					// if (hgList != null && hgList.size() > 0) {
					// Map<String, Object> hgMap = hgList.get(0);
					req.put("GH", user.getUserId() + "");

					// }

				}
			}
			wmm.doLoadBedPatientInfo(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
			// } catch (PersistentDataOperationException e) {
			// throw new ServiceException("数据库处理发生异常", e);
		}
	}

	/**
	 * 载入病人及床位扩展信息
	 */
	@SuppressWarnings("unchecked")
	public void doLoadPatientExInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			res.put("body", wmm.doLoadPatientExInfo(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存病人在院信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSavePatientInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Long zyh = Long.parseLong(req.get("ZYH").toString());
		String openBy = req.get("openBy").toString();
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			res.put("body", wmm.doSavePatientInfo(zyh, openBy, body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 退床处理
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTccl(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			wmm.doSaveTccl(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 载入医嘱处理参数
	 */
	@SuppressWarnings("unchecked")
	public void doLoadHospitalParams(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			res.put("body", wmm.doLoadHospitalParams(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 查询附加项目信息
	 */
	@SuppressWarnings("unchecked")
	public void doLoadAppendAdvice(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			res.put("body", wmm.doLoadAppendAdvice(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 查询附加项目信息
	 */
	@SuppressWarnings("unchecked")
	public void doLoadAppendAdviceTYSQ(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			res.put("body", wmm.doLoadAppendAdviceTYSQ(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 复核医嘱
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveReview(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			res.put("count", wmm.doSaveReview(body, ctx));
			// 如果条件成立说明需要提示复核时因录入与复核人不能为同人限制没有复核
			if (body != null && body.get("RMESSAGE") != null
					&& "1".equals(body.get("RMESSAGE") + "")) {
				res.put("RMESSAGE", "1");
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 取消复核医嘱
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveUnReview(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			res.put("count", wmm.doSaveUnReview(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 医生站提交
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDocSubmit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			res.put("count", wmm.doSaveDocSubmit(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 病区退回医嘱
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveGoback(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			res.put("count", wmm.doSaveGoback(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 查询住院病人过敏信息
	 */
	@SuppressWarnings("unchecked")
	public void doLoadDetailsInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			res.put("body", wmm.doLoadDetailsInfo(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 载入常用药
	 */
	@SuppressWarnings("unchecked")
	public void doLoadMedicineInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			res.put("body", wmm.doLoadMedicineInfo(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 载入常用项
	 */
	@SuppressWarnings("unchecked")
	public void doLoadClinicInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			res.put("body", wmm.doLoadClinicInfo(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 载入常用文字医嘱
	 */
	@SuppressWarnings("unchecked")
	public void doLoadCharacterInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			res.put("body", wmm.doLoadCharacterInfo(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 载入组套信息
	 */
	@SuppressWarnings("unchecked")
	public void doLoadAdviceSet(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			res.put("body", wmm.doLoadAdviceSet(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 校验库存信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doCheckInventory(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			res.putAll(BSPHISUtil.checkInventory(body, dao, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存住院病区医嘱
	 */
	// @SuppressWarnings("unchecked")
	public void doSaveWardPatientInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		// Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			wmm.doSaveWardPatientInfo(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveAddProjects(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			wmm.doSaveAddProjects(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 出院管理
	 */
	@SuppressWarnings("unchecked")
	public void doSaveLeaveHospitalProve(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			wmm.doSaveLeaveHospitalProve(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 出院条件判别
	 */
	@SuppressWarnings("unchecked")
	public void doCanLeaveHospital(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			res.put("body", wmm.doCanLeaveHospital(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 出院通知保存
	 */
	@SuppressWarnings("unchecked")
	public void doSaveLeaveHospital(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			wmm.doSaveLeaveHospital(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 出院通知保存
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCancelLeaveHospital(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			wmm.doSaveCancelLeaveHospital(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取执行科室 根据药品序号和机构id
	 */
	@SuppressWarnings("unchecked")
	public void doGetZXKSByYPXH(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			res.put("body", wmm.doGetZXKSByYPXH(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取系统参数FHYZHJF
	 */
	public void doGetFHYZHJF(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();// 用户的机构ID
		res.put("body", ParameterUtil.getParameter(JGID,
				BSPHISSystemArgument.FHYZHJF, ctx));
	}

	/**
	 * 查询医嘱复核列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryDoctorReviewList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			wmm.doQueryDoctorReviewList(req, res, ctx);
			res.put(RES_CODE, "200");
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 单病人操作
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveDBR(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			wmm.doSaveBatchReview(req, res, ctx, 1);
			res.put(RES_CODE, "200");
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 全部操作
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveQB(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			wmm.doSaveBatchReview(req, res, ctx, 2);
			res.put(RES_CODE, "200");
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 更新医嘱信息（历史停嘱信息取消停嘱）
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doUpdateAdviceStatus(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			wmm.doUpdateAdviceStatus(req, res, ctx);
			res.put(RES_CODE, "200");
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveHerbInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			wmm.doSaveHerbInfo(req, res);
			res.put(RES_CODE, "200");
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 病区费用列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadClinicsCharge(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			res.putAll(wmm.doLoadClinicsCharge(body));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存病区退费信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveRefundClinic(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		try {
			wmm.doSaveRefundClinic(body);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存住院病区医嘱
	 */
	public void doQueryHerbInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			wmm.doQueryHerbInfo(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取已保存的抗菌药物信息
	 */
	public void doLoadAmqcCount(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			wmm.doLoadAmqcCount(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	/**
	 * 获取已保存的抗菌药物信息
	 */
	public void doQueryJzyfsz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			wmm.doQueryJzyfsz(req,res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	/**
	 * 获取已保存的抗菌药物信息
	 */
	public void doQueryKJYWParams(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			wmm.doQueryKJYWParams(req,res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 获取zy_brry
	 */
	public void doQueryZY_BRRY(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			wmm.doQueryZY_BRRY(req,res, dao,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 添加中医诊断，诊断信息列表需要重写onloadData方法
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryclinicManageList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			wmm.doQueryclinicManageList(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	public void doGetDoctorAdviceStatus(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			wmm.doGetDoctorAdviceStatus((Map<String, Object>)req.get("body"), res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	/**
	 * 合理用药 多科室多处方查找对应的有效处方信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadOtherYZXX(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			res.putAll(wmm.doLoadOtherYZXX(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("合理用药,查找医嘱信息失败！", e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "success");

	}
	/**
	 * 合理用药 多科室多处方查找对应的有效处方信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadOtherBrzd(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardPatientManageModule wmm = new WardPatientManageModule(dao);
		try {
			res.putAll(wmm.doLoadOtherBrzd(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("合理用药,病人诊断查找失败！", e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "success");		
	}	
}
