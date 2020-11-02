/**

 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.fsb.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.cic.source.ClinicManageModel;
import phis.application.war.source.WardPatientManageModule;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.BSPHISUtil;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 家床病人管理
 * 
 * @author yangl
 */
public class FamilySickBedManageService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(FamilySickBedManageService.class);

	/**
	 * 载入组套信息
	 */
	@SuppressWarnings("unchecked")
	public void doLoadAdviceSet(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String ztlb = String.valueOf(body.get("ZTLB"));
		try {
			if (ztlb.equals("4")) {// 项目
				WardPatientManageModule wpm = new WardPatientManageModule(dao);
				res.put("body", wpm.doLoadAdviceSet(body, ctx));
			} else {
				ClinicManageModel cmm = new ClinicManageModel(dao);
				res.put("body", cmm.doLoadPersonalSet(body, ctx));
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 家床停嘱病人
	 * 
	 * @Description:
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author yangl
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doLoadStopPatients(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FamilySickBedManageModule fsb = new FamilySickBedManageModule(dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			res.put("body", fsb.doLoadStopPatients(body));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 查询本次家床可以使用的审批数量
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doCheckApplyInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FamilySickBedManageModule fsb = new FamilySickBedManageModule(dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			res.put("body", fsb.doCheckApplyInfo(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 删除查床记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveFsbCheck(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		FamilySickBedManageModule fsb = new FamilySickBedManageModule(dao);

		try {
			fsb.doRemoveFsbCheck(body, ctx);
		} catch (ModelDataOperationException e) {
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
		FamilySickBedManageModule fsb = new FamilySickBedManageModule(dao);
		try {
			fsb.doUpdateAdviceStatus(req, res, ctx);
			res.put(RES_CODE, "200");
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存诊断信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDiagnossis(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		FamilySickBedManageModule fsb = new FamilySickBedManageModule(dao);
		try {
			fsb.doSaveClinicDiagnossis(body);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("保存诊断信息出错！", e);
		}
	}

	/**
	 * 诊疗计划调入
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doLoadPlanSet(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedManageModule wmm = new FamilySickBedManageModule(dao);
		try {
			res.put("body", wmm.doLoadPlanSet(req, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 家床护士站退回医嘱
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveGoback(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		FamilySickBedManageModule wmm = new FamilySickBedManageModule(dao);
		try {
			res.put("count", wmm.doSaveGoback(body, ctx));
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
		FamilySickBedManageModule wmm = new FamilySickBedManageModule(dao);
		try {
			res.put("count", wmm.doSaveDocSubmit(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 家床医嘱历史医嘱
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCaclLsbz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FamilySickBedManageModule ccm = new FamilySickBedManageModule(dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			ccm.doSaveCaclLsbz(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 家床撤床证
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCancelSickBed(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FamilySickBedManageModule ccm = new FamilySickBedManageModule(dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			ccm.doSaveCancelSickBed(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取家床诊疗计划组套
	 */
	@SuppressWarnings("unchecked")
	public void doLoadPlanDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FamilySickBedManageModule ccm = new FamilySickBedManageModule(dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			res.put("body", ccm.doLoadPlanDetails(body));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存家床病人在院信息
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
		FamilySickBedManageModule wmm = new FamilySickBedManageModule(dao);
		try {
			res.put("body", wmm.doSavePatientInfo(zyh, body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSavePlanDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		FamilySickBedManageModule wmm = new FamilySickBedManageModule(dao);
		try {
			wmm.doSavePlanDetails(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取家床发药药房
	 */
	public void doLoadPharmacy(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FamilySickBedManageModule ccm = new FamilySickBedManageModule(dao);
		try {
			res.put("body", ccm.doLoadPharmacy());
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doLoadNavTree(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			// 获取家床查床记录及电子病历信息
			FamilySickBedManageModule ccm = new FamilySickBedManageModule(dao);
			res.putAll(ccm.doLoadNavTree(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存家床医嘱
	 */
	// @SuppressWarnings("unchecked")
	public void doSaveWardPatientInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		// Map<String, Object> body = (Map<String, Object>) req.get("body");
		FamilySickBedManageModule ccm = new FamilySickBedManageModule(dao);
		try {
			ccm.doSaveWardPatientInfo(req, res, ctx);
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
	 * 获取已保存的抗菌药物信息
	 */
	public void doLoadAmqcCount(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FamilySickBedManageModule fsb = new FamilySickBedManageModule(dao);
		try {
			fsb.doLoadAmqcCount(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 查询家床病人过敏信息
	 */
	@SuppressWarnings("unchecked")
	public void doLoadDetailsInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		FamilySickBedManageModule ccm = new FamilySickBedManageModule(dao);
		try {
			res.put("body", ccm.doLoadDetailsInfo(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 家床诊疗计划组套明细保存 *@param req
	 * 
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	public void doSavePrescriptionDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		FamilySickBedManageModule ccm = new FamilySickBedManageModule(dao);
		ccm.doSavePrescriptionDetails(req, res, dao, ctx);
	}

	/**
	 * 家床诊疗计划组套明细删除
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doRemovePrescriptionDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		long pkey = Long.parseLong(req.get("pkey").toString());
		String schemaList = req.get("schemaList") + "";
		String schemaDetailsList = req.get("schemaDetailsList") + "";
		FamilySickBedManageModule ccm = new FamilySickBedManageModule(dao);
		ccm.doRemovePrescriptionDetails(body, pkey, schemaList,
				schemaDetailsList, res, dao, ctx);
	}

	/**
	 * 家床诊疗计划组套启用
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doUpdatePrescriptionStack(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String schemaList = req.get("schemaList") + "";
		String schemaDetailsList = req.get("schemaDetailsList") + "";
		FamilySickBedManageModule ccm = new FamilySickBedManageModule(dao);
		ccm.savePersonalComboExecute(body, schemaList, schemaDetailsList, res,
				dao, ctx);
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-1-27
	 * @description 药品提交病人查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryJcDrugSubmissionPatient(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FamilySickBedDoctorExecutionModel model = new FamilySickBedDoctorExecutionModel(
				dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			List<Map<String, Object>> ret = model
					.queryJcDrugSubmissionPatient(body);
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
	 * @createDate 2015-1-30
	 * @description 家床药品提交
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveJcDrugSubmission(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FamilySickBedDoctorExecutionModel model = new FamilySickBedDoctorExecutionModel(
				dao);
		FamilySickBedManageModule fsb = new FamilySickBedManageModule(dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		try {
			// 抗菌药物判断
			Map<String, Object> params = new HashMap<String, Object>();
			StringBuffer errMsg = new StringBuffer();
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> mds = body.get(i);
				if ("1".equals(mds.get("KSBZ") + "")) {
					params.put("ZYH", mds.get("ZYH"));
					params.put("LSYZ", mds.get("LSYZ"));
					params.put("YPXH", mds.get("YPXH"));
					String msg = fsb.doCheckAntibacterial(params, ctx);
					if (msg != null) {
						if (errMsg.length() > 0) {
							errMsg.append("<br />");
						}
						errMsg.append(msg);
						body.remove(mds);
						i--;
					}
				}
			}
			// 判断欠费病人
						List<Map<String, Object>> tmpList = new ArrayList<Map<String, Object>>();
						tmpList.addAll(body);
						boolean flag = BSPHISUtil.FSBArrearsPatientsQuery(tmpList, ctx, dao,
								res);
						if (flag) {// 重新过滤将不需要执行的病人项目去除！(根据住院号判断)
							boolean f = true;
							int index = 0;
							Map<String, Object> tmpMap = null;
							for (int i = 0; i < body.size(); i++) {
								tmpMap = body.get(i);
								f = true;
								for (Map<String, Object> map : tmpList) {
									if (tmpMap.get("ZYH").equals(map.get("ZYH"))) {
										f = false;
										continue;
									}
								}
								if (f) {
									body.remove(i);
									i--;
									index++;
									continue;
								}
							}
							if(index!=0){
								errMsg.append(res.get("RES_MESSAGE")+"");
							}
							// 主项和附加项都没有时直接返回
							if ((body == null || body.size() == 0)) {
								res.put(RES_CODE, 509);
								res.put(RES_MESSAGE, errMsg.toString());
								return;
							}
						}
			Map<String, Object> ret = null;
			if (body.size() > 0) {
				ret = model.saveJcDrugSubmission(body, ctx);
			}
			if (errMsg.length() > 0) {
				res.put(RES_CODE, 509);
				res.put(RES_MESSAGE, errMsg.toString());
			} else {
				res.put(RES_CODE, ret.get("code"));
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
	 * @createDate 2015-3-9
	 * @description 项目提交病人列表查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryProjectSubmissionPatient(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FamilySickBedProjectSubmissionModel model = new FamilySickBedProjectSubmissionModel(
				dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			List<Map<String, Object>> ret = model
					.queryProjectSubmissionPatient(body, ctx);
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
	 * @createDate 2015-3-9
	 * @description 项目提交明细列表查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryProjectSubmissionDetail(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FamilySickBedProjectSubmissionModel model = new FamilySickBedProjectSubmissionModel(
				dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			List<Map<String, Object>> ret = model.queryProjectSubmissionDetail(
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
	 * @createDate 2015-3-9
	 * @description 项目提交保存
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveProject(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedProjectSubmissionModel model = new FamilySickBedProjectSubmissionModel(
				dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		try {
			model.saveProject(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-3-9
	 * @description 项目提交
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveProjectSubmission(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FamilySickBedProjectSubmissionModel model = new FamilySickBedProjectSubmissionModel(
				dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		try {
			Map<String, Object> ret = model.saveProjectSubmission(body, ctx);
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
	 * @createDate 2015-3-10
	 * @description 项目执行病人列表查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryProjectExecutionPatient(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FamilySickBedProjectExecutionModel model = new FamilySickBedProjectExecutionModel(
				dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			List<Map<String, Object>> ret = model.queryProjectExecutionPatient(
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
	 * @createDate 2015-3-11
	 * @description 项目执行明细查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doDetailChargeQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FamilySickBedProjectExecutionModel model = new FamilySickBedProjectExecutionModel(
				dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			model.doDetailChargeQuery(body, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-3-11
	 * @description 项目执行
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveProjectExecution(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FamilySickBedProjectExecutionModel model = new FamilySickBedProjectExecutionModel(
				dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		try {
			model.saveProjectExecution(body, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-5
	 * @description 家床发药按方式查询发药数量
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryDispensingFs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FamilySickBedDispensingModel model = new FamilySickBedDispensingModel(
				dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			List<Map<String, Object>> ret = model.queryDispensingFs(body, ctx);
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
	 * @createDate 2015-2-5
	 * @description 家床发药病人列表
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
		FamilySickBedDispensingModel model = new FamilySickBedDispensingModel(
				dao);
		List<?> cnd = (List<?>) req.get("cnd");
		try {
			List<Map<String, Object>> ret = model.queryDispensing_br(cnd, ctx);
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
	 * @createDate 2015-2-5
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
		FamilySickBedDispensingModel model = new FamilySickBedDispensingModel(
				dao);
		List<?> cnd = (List<?>) req.get("cnd");
		try {
			List<Map<String, Object>> body = model.queryDispensing(cnd, ctx);
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
	 * @createDate 2015-2-5
	 * @description 发药
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSavefamilySickBedDispensing(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FamilySickBedDispensingModel model = new FamilySickBedDispensingModel(
				dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.savefamilySickBedDispensing(body,
					ctx);
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
	 * @createDate 2015-2-5
	 * @description 全退
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
		FamilySickBedDispensingModel model = new FamilySickBedDispensingModel(
				dao);
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
	 * @createDate 2015-2-5
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
		FamilySickBedDispensingModel model = new FamilySickBedDispensingModel(
				dao);
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
	 * @createDate 2015-2-26
	 * @description 保存退药申请记录
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveBackApplication(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		FamilySickBedMedicalBackApplicationModel dsm = new FamilySickBedMedicalBackApplicationModel(
				dao);
		try {
			dsm.saveBackApplication(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-26
	 * @description 提交退药记录
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCommitBackApplication(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		FamilySickBedMedicalBackApplicationModel dsm = new FamilySickBedMedicalBackApplicationModel(
				dao);
		try {
			Map<String, Object> map_ret = dsm.saveCommitBackApplication(body,
					ctx);
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
	 * @createDate 2015-2-26
	 * @description 退药申请查询已发药记录
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryDispensingRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		FamilySickBedMedicalBackApplicationModel dsm = new FamilySickBedMedicalBackApplicationModel(
				dao);
		try {
			List<Map<String, Object>> ret = dsm.queryDispensingRecords(body,
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
	 * @createDate 2015-2-26
	 * @description 查询可退数量
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryTurningBackNumber(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		FamilySickBedMedicalBackApplicationModel dsm = new FamilySickBedMedicalBackApplicationModel(
				dao);
		try {
			double ret = dsm.queryTurningBackNumber(body, ctx);
			res.put("ktsl", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-26
	 * @description 退药申请查询退药记录
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQuerytyRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FamilySickBedMedicalBackApplicationModel dsm = new FamilySickBedMedicalBackApplicationModel(
				dao);
		try {
			dsm.querytyRecords(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-3-3
	 * @description 病区待退药记录查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryBackMedicine(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FamilySickBedBackMedicineModel model = new FamilySickBedBackMedicineModel(
				dao);
		try {
			List<Map<String, Object>> ret = model.queryBackMedicine();
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
	 * @createDate 2015-3-3
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
		FamilySickBedBackMedicineModel model = new FamilySickBedBackMedicineModel(
				dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		try {
			Map<String, Object> ret = model.saveHospitalPharmacyBackMedicine(
					body, ctx);
			res.put("otherRet", ret.get("otherRet"));
			if (ret.get("msg") != null && ret.get("msg") != ""
					&& (ret.get("msg") + "").length() > 0) {
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
	 * @createDate 2015-3-3
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
		FamilySickBedBackMedicineModel model = new FamilySickBedBackMedicineModel(
				dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		try {
			model.saveBackMedicineFullRefund(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-3-3
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
		FamilySickBedBackMedicineModel model = new FamilySickBedBackMedicineModel(
				dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
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
	 * @createDate 2015-5-13
	 * @description 家床记账-病人信息查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadJcxx(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		FamilySickBedPharmacyAccountingModel model = new FamilySickBedPharmacyAccountingModel(dao);
		Map<String,Object> body = (Map<String,Object>) req.get("body");
		try {
			Map<String,Object> ret=model.loadJcxx(body);
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
	 * @createDate 2015-5-14
	 * @description  家床记账保存
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveFamilySickBedPharmacyAccounting(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		FamilySickBedPharmacyAccountingModel model = new FamilySickBedPharmacyAccountingModel(dao);
		Map<String,Object> body = (Map<String,Object>) req.get("body");
		try {
			model.saveFamilySickBedPharmacyAccounting(body,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-5-14
	 * @description 家床记账数量输入负数时查询已经计费的费用明细的价格放到前台显示
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
		FamilySickBedPharmacyAccountingModel model = new FamilySickBedPharmacyAccountingModel(dao);
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
}
