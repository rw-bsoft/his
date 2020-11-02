package phis.application.cic.source;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.application.sup.source.StorageOfMaterialsModel;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.SkinTestUnConfigException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.annotation.RpcService;
import ctd.util.context.Context;
/**
 * @description 处方信息维护
 * 
 * @author yangl</a>
 */
public class ClinicManageService extends AbstractActionService implements
		DAOSupportable {

	/**
	 * 校验是否在皮试中
	 */
	@SuppressWarnings("unchecked")
	public void doQuerySkinTestStatus(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.put("skintestRunning", cmm.doQuerySkinTestStatus(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 验证上级医生权限
	 */
	@SuppressWarnings("unchecked")
	public void doVerifyDocInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.putAll(cmm.doVerifyDocInfo(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}


	//根据药品序号取生编码 wy

	public void doGetSbmByYpxh(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel clinicManageModel = new ClinicManageModel(dao);
		try {
			clinicManageModel.doGetSbmByYpxh(body,res,ctx);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存处方信息
	 */
	@SuppressWarnings("unchecked")
	public void doSaveClinicInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.putAll(cmm.doSaveClinicInfo(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 载入附加项目
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadAddition(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.put("body", cmm.doLoadAddition(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 载入皮试收费项目
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws SkinTestUnConfigException 
	 */
	@SuppressWarnings("unchecked")
	public void doLoadPssfxm(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, SkinTestUnConfigException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.put("body", cmm.doLoadPssfxm(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 判断该药是否已经开始皮试
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws SkinTestUnConfigException 
	 */
	@SuppressWarnings("unchecked")
	public void doLoadSfps(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, SkinTestUnConfigException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.put("body", cmm.doLoadSfps(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	
	
	/**
	 * 判断
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSfpsyp(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.put("body", cmm.doSfpsyp(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	

	/**
	 * 根据empiId查找病人基本信息
	 */
	@SuppressWarnings("unchecked")
	public void doLoadEmpi(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		String empiId = (String) ((Map<String, Object>) req.get("body"))
				.get("empiId");
		if (empiId.isEmpty()) {
			res.put(RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(RES_MESSAGE, "empiId is null");
		}
		try {
			Map<String, Object> empiInfo = dao.doLoad(BSPHISEntryNames.MS_BRDA,
					empiId);
			res.put("empiData", empiInfo);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException("用户信息查找失败");
		}
	}

	/**
	 * 根据传入的处方号码查找对应的CF01处方信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadCF01(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Integer jzxh = (Integer) ((Map<String, Object>) req.get("body"))
				.get("clinicId");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.putAll(cmm.loadCF01(Long.parseLong(jzxh.toString()), ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查找处方信息失败！", e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "success");

	}

	/**
	 * 保存皮试结果信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveSkinTestResult(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doSaveSkinTestResult(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("皮试结果信息保存失败!", e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "success");

	}

	/**
	 * 根据组套载入组套明细信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadPersonalSet(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			res.put("body", cmm.doLoadPersonalSet(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询组套信息失败，请联系管理员！", e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "success");
	}

	/**
	 * 根据药品序号获取药品信息（助手方式）
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadMedcineInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.put("body", cmm.doLoadMedicineInfo(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取药品信息失败!", e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "success");
	}
	
	/**
	 * 获取药品组套明细
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ControllerException 
	 */
	public void doLoadMedcineSet(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException, ControllerException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doLoadMedcineSet(req,res,dao,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取药品信息失败!", e);
		}
	}
	
	/**
	 * 获取项目组套明细
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ControllerException 
	 */
	public void doLoadProjectSet(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException, ControllerException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doLoadProjectSet(req,res,dao,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取药品信息失败!", e);
		}
	}

	/**
	 * 选择病人后，初始化病人就诊信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveInitClinicInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.putAll(cmm.initClinicInfo(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 载入病历首页信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadClinicInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.putAll(cmm.loadClinicInfo(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 载入结算明细
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadSettlementInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.putAll(cmm.loadSettlementInfo(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doLoadOutClinicInitParams(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.putAll(cmm.doLoadOutClinicInitParams(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取系统参数
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	@RpcService
	public void doLoadSystemParams(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.put("body", (cmm.loadSystemParams(body, ctx)));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 查询病程记录中血压值
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryMsBcjl(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doQueryMsBcjl(body, ctx, res);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存病历信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMsBcjl(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doSaveMsBcjl(body, ctx, res);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 暂挂或者结束就诊
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveClinicFinish(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doSaveClinicFinish(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 验证科室排班和医生排班信息
	 */
	@SuppressWarnings("unchecked")
	public void doVerificationReservationInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doVerificationReservationInfo(body, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存复诊预约信息
	 */
	@SuppressWarnings("unchecked")
	public void doSaveReservationInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doSaveReservationInfo(body, res, ctx);
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
	 * 获取药品或者项目的自负比例
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetPayProportion(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			res.put("body", BSPHISUtil.getPayProportion(body, ctx, dao));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取药品皮试历史信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetSkinTest(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.put("body", cmm.getGetSkinTest(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取药品皮试历史信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveSkinTest(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.saveGetSkinTest(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 根据处方主键删除处方信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doRemoveClinicInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doRemoveClinicInfo(req, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveClinicTherapeutic(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, NumberFormatException, ControllerException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.put("body", cmm.doSaveClinicTherapeutic(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doQueryList(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doQueryList(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doQueryHistoryList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doQueryHistoryList(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	
	public void doQueryWorkLogs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doQueryWorkLogs(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 排队系统 跳过功能
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveSkipInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			cmm.doSaveSkipInfo(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存排队就诊状态
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	// @SuppressWarnings("unchecked")
	// public void doSavePdJzzt(Map<String, Object> req, Map<String, Object>
	// res,
	// BaseDAO dao, Context ctx) throws ServiceException {
	// ClinicManageModel cmm = new ClinicManageModel(dao);
	// try {
	// Map<String, Object> body = (Map<String, Object>) req.get("body");
	// cmm.doSavePdJzzt(body, ctx);
	// } catch (ModelDataOperationException e) {
	// throw new ServiceException(e);
	// }
	// }

	

	/**
	 * 根据系统参数QYCFCZQZTJ 未录入诊断，不允许录入处方处置
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryIsAllowed(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			cmm.doQueryIsAllowed(body, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 查询诊断录入
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryZDLR(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			cmm.doQueryZDLR(body, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	/**
	 * 根据系统参数限制每张处方明细条数
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQuerySystemArgumentCFMXSL(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doQuerySystemArgumentCFMXSL(res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取一次限量
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryYCXL(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			cmm.doQueryYCXL(body, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 市医保病人草药超额提示，总价不能超过30块、规定病种50块
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doIsAllowedSave(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			cmm.doIsAllowedSave(body, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 根据ypxh查询药品的zssf（判断是否为输液药品）
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	// public void doQueryZSSF(Map<String, Object> req, Map<String, Object> res,
	// BaseDAO dao, Context ctx) throws ServiceException {
	// ClinicManageModel cmm = new ClinicManageModel(dao);
	// try {
	// String ypxh = req.get("ypxh")+"";
	// cmm.doQueryZSSF(ypxh, res, dao, ctx);
	// } catch (ModelDataOperationException e) {
	// throw new ServiceException(e);
	// }
	// }
	/**
	 * 根据jzxh查询表ms_bcjl 的 BRQX,JKJY
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryBrqxAndJkjy(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			cmm.doQueryBrqxAndJkjy(body, res, dao, ctx);
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
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doQueryclinicManageList(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取药房划价处方信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadCfxx(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.putAll(cmm.doLoadCfxx(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 划价收费获取物资价格
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	// @SuppressWarnings("unchecked")
	public void doQueryMaterialsPrice(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		// Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doQueryMaterialsPrice(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doQueryOMRHistoryList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doQueryOMRHistoryList(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	/**
	 * 特殊yaop
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	// @SuppressWarnings("unchecked")
	public void doGetTsypPb(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		// Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doGetTsypPb(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 查询本年度是否存在就诊记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	// @SuppressWarnings("unchecked")
	public void doGetHasClinicRecord(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.getHasClinicRecord(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	public void doSelectdybz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doSelectdybz(body, ctx, res);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	public void doSavedybz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doSavedybz(body, ctx, res);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 签约查询信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryqybrxx(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doQueryqybrxx(body, ctx, res);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	/**
	 * 病人相关信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetBrxx(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			Map map = cmm.doGetBrxx(body, ctx);
			res.put("body", map);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-6-10
	 * @description 病历书写向导XDBH查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryBlsxxd(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
			try {
				res.put("XDBH", cmm.queryBlsxxd(body));
			} catch (ModelDataOperationException e) {
				res.put(RES_CODE, e.getCode());
				res.put(RES_MESSAGE, e.getMessage());
				throw new ServiceException(e);
			}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-6-10
	 * @description 病历复制
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doBlfzQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		long brid = MedicineUtils.parseLong(req.get("brid")) ;
		long jzxh = MedicineUtils.parseLong(req.get("jzxh")) ;
		ClinicManageModel cmm = new ClinicManageModel(dao);
			try {
				res.put("bcjl", cmm.blfzQuery(brid,jzxh));
			} catch (ModelDataOperationException e) {
				res.put(RES_CODE, e.getCode());
				res.put(RES_MESSAGE, e.getMessage());
				throw new ServiceException(e);
			}
		
	}
	
	@SuppressWarnings("unchecked")
	public void doRemoveBcjl(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			cmm.doRemoveBcjl(body, ctx);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	//
	public void doQueryyuyuexx(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			cmm.doQueryyuyuexx(body, ctx,res);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 开处方前判断是否有诊断
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doCheckHasDiagnose(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			cmm.doCheckHasDiagnose(body, ctx,res);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 查询处方笺模板
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryCfjmb(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doQueryCfjmb(body, ctx,res);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 检查库存数量是否足够
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doCheckKcslEnough(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req.get("body");
		try {
			cmm.doCheckKcslEnough(body, ctx,res);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 查询是否中医科室
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadChineDept(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doQueryKsxx(body,res,dao,ctx);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 检查药品使用情况：在指定时间段内已开过哪些相同的药品或检查
	 * zhaojian 2017-11-02
	 */
	@SuppressWarnings("unchecked")
	public void doCheckYpsy(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			 cmm.doCheckYpsy(body, ctx,res);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 根据省编码获取药品信息
	 * zhaojian 2017-11-21
	 */
	@SuppressWarnings("unchecked")
	public void doGetYpFromSbm(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			 cmm.doGetYpFromSbm(body, ctx,res);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 保存传染病报告卡
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveIdrReport(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doSaveIdrReport(req, ctx,res);
		
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	
	
	
	/**
	 * 根据门诊诊断记录编号查询是否已保存传染病报告卡
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryIdrReport(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.put("result", cmm.doQueryIdrReport(req, ctx));
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
	public void doLoadOtherKSCF01(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.putAll(cmm.loadOtherKSCF01(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("合理用药,查找处方信息失败！", e);
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
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.putAll(cmm.loadOtherBrzd(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("合理用药,查找病人信息失败！", e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "success");		
	}
	//yx-2017-12-25-获取捷士达签约信息
	@SuppressWarnings("unchecked")
	public void doGetjsdqyjcxx(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			res.putAll(cmm.getjsdqyjcxx(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("合理用药,查找病人信息失败！", e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "success");		
	}
	
		/**
	 * 
	 * @description 传染病报告卡审核
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doVerify(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String curUserId = user.getUserId();
		String curUnitId = user.getManageUnit().getId();// 用户的机构ID
		String organname = user.getManageUnit().getName();
		String USER_NAME = user.getUserName();		
		Date curDate = new Date();
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String curDate1= sdf1.format( new Date());
		int num =(int) (Math.random( )*50+50) ;
		try {
		String ip = ClinicManageService.getIpByEthNum();	
		String ipc = InetAddress.getLocalHost().getHostAddress();
				String json="{ \n"+
			"\"orgCode\":\""+curUnitId+"\",\n"+
			"\"orgName\":\""+organname+"\",\n"+
			"\"ip\":\""+ip+"\",\n"+
			"\"opertime\":\""+curDate1+"\",\n"+
			"\"operatorCode\":\""+curUserId+"\",\n"+
			"\"operatorName\":\""+USER_NAME+"\",\n"+
			"\"callType\":\"02\",\n"+
			"\"apiCode\":\"CRBGXRWTX\",\n"+
			"\"fromDomain\":\"ehr_yy\",\n"+
			"\"toDomain\":\"ehr_mb\",\n"+
			"\"clientAddress\":\""+ipc+"\",\n"+
			"\"serviceBean\":\"ClinicManageService\",\n"+
			"\"methodDesc\":\"doVerify\",\n"+
			"\"statEnd\":\""+curDate1+"\",\n"+
			"\"stat\":\"1\",\n"+
			"\"avgTimeCost\":\""+num+"\",\n"+
			"\"request\":\"ClinicManageService.httpURLPOSTCase(json)\",\n"+
			"\"response\":\"200\"\n"+
		          "}";	
				ClinicManageService.httpURLPOSTCase(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doVerify(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	public static String getIpByEthNum() {
	     try {
		
	           Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
	           InetAddress ip;
	           while (allNetInterfaces.hasMoreElements()) {
	               NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();      
	                   Enumeration addresses = netInterface.getInetAddresses();
	                   while (addresses.hasMoreElements()) {
	                       ip = (InetAddress) addresses.nextElement();
	                       if (ip !=null  && ip instanceof Inet4Address) {
	                           return ip.getHostAddress();
	                       }
	                   }               
	           }
	     } catch (Exception e) {
				e.printStackTrace();
			}
	      
	       return "获取服务器IP错误";
	   }

	
	public  static void httpURLPOSTCase( String body) {
	   String methodUrl = "http://192.168.10.178:8881/apiCallLog";
	   HttpURLConnection connection = null;
	   OutputStream dataout = null;
	   BufferedReader reader = null;
	   StringBuilder result = null;
	   String line = null;
	   try {
	       URL url = new URL(methodUrl);
	       connection = (HttpURLConnection) url.openConnection();// 根据URL生成HttpURLConnection
	       connection.setDoOutput(true);// 设置是否向connection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true,默认情况下是false
	       connection.setDoInput(true); // 设置是否从connection读入，默认情况下是true;
	       connection.setRequestMethod("POST");// 设置请求方式为post,默认GET请求
	       connection.setRequestProperty("charset", "utf-8");
	       connection.setRequestProperty("Content-Type", "application/json");
	       dataout = new DataOutputStream(connection.getOutputStream());// 创建输入输出流,用于往连接里面输出携带的参数
	      ((DataOutputStream) dataout).writeChars(body);
	      if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	          reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));// 发送http请求
	           result = new StringBuilder();
	          // 循环读取流
	          while ((line = reader.readLine()) != null) {
	             result.append(line).append(System.getProperty("line.separator"));//
	          }
	      }
	     } catch (IOException e) {
	      e.printStackTrace();
	     } 
	}
	
	/**
	 * 
	 * @description 传染病报告卡弃审
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doCancelVerify(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doCancelVerify(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @description 传染病报告卡退回
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doCancel(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doCancel(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 
	 * @description 传染病报告卡上报
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doReportIdr(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doReportIdr(body, ctx, res);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	//发送调用合理用药接口给大数据
	public void doSendMsgToBigData(Map<String, Object> req, Map<String, Object> res,
		BaseDAO dao, Context ctx) throws ServiceException {
		String brid=req.get("brid")+"";
		String sfzh="";
		String brxm="";
		try{
			Map<String,Object> d=dao.doSqlLoad("select sfzh as SFZH,brxm as BRXM from ms_brda where brid="+brid,null);
			sfzh=d.get("SFZH")==null?"":d.get("SFZH")+"";
			brxm=d.get("BRXM")==null?"":d.get("BRXM")+"";
		}catch(PersistentDataOperationException ee){
			ee.printStackTrace();
		}
		UserRoleToken user = UserRoleToken.getCurrent();
		String curUserId = user.getUserId();
		String curUnitId = user.getManageUnit().getId();// 用户的机构ID
		String organname = user.getManageUnit().getName();
		String USER_NAME = user.getUserName();		
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String curDate1= sdf1.format( new Date());
		int num =(int) (Math.random( )*50+50) ;
		try {
			String ip = getRealIp();
			String ipc = InetAddress.getLocalHost().getHostAddress();
			String json="{ \n"+
				"\"orgCode\":\""+curUnitId+"\",\n"+
				"\"orgName\":\""+organname+"\",\n"+
				"\"ip\":\""+ip+"\",\n"+
				"\"opertime\":\""+curDate1+"\",\n"+
				"\"operatorCode\":\""+curUserId+"\",\n"+
				"\"operatorName\":\""+USER_NAME+"\",\n"+
				"\"patientCardNo\":\""+sfzh+"\",\n"+
				"\"patientName\":\""+brxm+"\",\n"+
				"\"callType\":\"01\",\n"+
				"\"apiCode\":\"HLYYTX\",\n"+
				"\"operSystemCode\":\"HIS\",\n"+
				"\"operSystemName\":\"HIS系统\",\n"+
				"\"fromDomain\":\"cic_cf\",\n"+
				"\"toDomain\":\"cic_hlyy\",\n"+
				"\"clientAddress\":\""+ipc+"\",\n"+
				"\"serviceBean\":\"esb.HLYYTX\",\n"+
				"\"methodDesc\":\"void doSendMsgToBigData()\",\n"+
				"\"statEnd\":\""+curDate1+"\",\n"+
				"\"stat\":\"1\",\n"+
				"\"avgTimeCost\":\""+num+"\",\n"+
				"\"request\":\"ClinicManageService.httpURLPOSTCase(json)\",\n"+
				"\"response\":\"200\"\n"+"}";
				ClinicManageService.httpURLPOSTCase(json);
			} catch (Exception e) {
				if(e.getMessage().contains("timed out")){
					System.out.println("将成功调用合理用药接口的结果发送到大数据平台失败:http://192.168.10.178:8881/apiCallLog");
					return;
				}
				e.printStackTrace();
		}
	}
	//yx
	public static String getRealIp(){
		 try{
	           Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
	          while (allNetInterfaces.hasMoreElements()){
	                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
	               Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
	              while (addresses.hasMoreElements()){
	                   InetAddress ip = (InetAddress) addresses.nextElement();
	                  if (ip != null && ip instanceof Inet4Address
	                      && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":")==-1){
	                     return ip.getHostAddress();
	                   } 
	              }
	           }
	       }catch(Exception e){
	          e.printStackTrace();
	        }
		 return "127.0.0.1";
	}
	/**
	 * 
	 * @author zhaohj
	 * @createDate 2019-08-27
	 * @description 获取医保病种
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */

	public void doQueryYBMC(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doQueryYBMC(body, ctx, res);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 
	 * @author xh
	 * @createDate 2020-06-30
	 * @description 查询是否有处方
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doCheckCF (Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doCheckCF(body, ctx, res);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
//	/**
//	 * 
//	 * @description 保存传染病报告卡-手足口病附卡
//	 * @param req
//	 * @param res
//	 * @param dao
//	 * @param ctx
//	 * @throws ServiceException
//	 */
//	@SuppressWarnings("unchecked")
//	public void doVerify_HFMD(Map<String, Object> req, Map<String, Object> res,
//			BaseDAO dao, Context ctx) throws ServiceException {
//		Map<String, Object> body = (Map<String, Object>) req.get("body");
//		ClinicManageModel cmm = new ClinicManageModel(dao);
//		try {
//			cmm.doVerify_HFMD(body, ctx);
//		} catch (ModelDataOperationException e) {
//			res.put(RES_CODE, e.getCode());
//			res.put(RES_MESSAGE, e.getMessage());
//			throw new ServiceException(e);
//		}
//	}
//	/**
//	 * 
//	 * @description 保存传染病报告卡-艾滋病病附卡
//	 * @param req
//	 * @param res
//	 * @param dao
//	 * @param ctx
//	 * @throws ServiceException
//	 */
//	@SuppressWarnings("unchecked")
//	public void doVerify_HIV(Map<String, Object> req, Map<String, Object> res,
//			BaseDAO dao, Context ctx) throws ServiceException {
//		Map<String, Object> body = (Map<String, Object>) req.get("body");
//		ClinicManageModel cmm = new ClinicManageModel(dao);
//		try {
//			cmm.doVerify_HIV(body, ctx);
//		} catch (ModelDataOperationException e) {
//			res.put(RES_CODE, e.getCode());
//			res.put(RES_MESSAGE, e.getMessage());
//			throw new ServiceException(e);
//		}
//	}
//	/**
//	 * 
//	 * @description 保存传染病报告卡-AFP病附卡
//	 * @param req
//	 * @param res
//	 * @param dao
//	 * @param ctx
//	 * @throws ServiceException
//	 */
//	@SuppressWarnings("unchecked")
//	public void doVerify_AFP(Map<String, Object> req, Map<String, Object> res,
//			BaseDAO dao, Context ctx) throws ServiceException {
//		Map<String, Object> body = (Map<String, Object>) req.get("body");
//		ClinicManageModel cmm = new ClinicManageModel(dao);
//		try {
//			cmm.doVerify_AFP(body, ctx);
//		} catch (ModelDataOperationException e) {
//			res.put(RES_CODE, e.getCode());
//			res.put(RES_MESSAGE, e.getMessage());
//			throw new ServiceException(e);
//		}
//	}
//	/**
//	 * 
//	 * @description 保存传染病报告卡-梅毒病附卡
//	 * @param req
//	 * @param res
//	 * @param dao
//	 * @param ctx
//	 * @throws ServiceException
//	 */
//	@SuppressWarnings("unchecked")
//	public void doVerify_MD(Map<String, Object> req, Map<String, Object> res,
//			BaseDAO dao, Context ctx) throws ServiceException {
//		Map<String, Object> body = (Map<String, Object>) req.get("body");
//		ClinicManageModel cmm = new ClinicManageModel(dao);
//		try {
//			cmm.doVerify_MD(body, ctx);
//		} catch (ModelDataOperationException e) {
//			res.put(RES_CODE, e.getCode());
//			res.put(RES_MESSAGE, e.getMessage());
//			throw new ServiceException(e);
//		}
//	}
//	/**
//	 * 
//	 * @description 保存传染病报告卡-乙肝病附卡
//	 * @param req
//	 * @param res
//	 * @param dao
//	 * @param ctx
//	 * @throws ServiceException
//	 */
//	@SuppressWarnings("unchecked")
//	public void doVerify_HBV(Map<String, Object> req, Map<String, Object> res,
//			BaseDAO dao, Context ctx) throws ServiceException {
//		Map<String, Object> body = (Map<String, Object>) req.get("body");
//		ClinicManageModel cmm = new ClinicManageModel(dao);
//		try {
//			cmm.doVerify_HBV(body, ctx);
//		} catch (ModelDataOperationException e) {
//			res.put(RES_CODE, e.getCode());
//			res.put(RES_MESSAGE, e.getMessage());
//			throw new ServiceException(e);
//		}
//	}


	/**
	 * 获取过敏药物
	 *
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	@RpcService
	public void doLoadGmyw(Map<String, Object> req,
						   Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doLoadGmyw(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	/**
	 * 获取过敏原
	 *
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	@RpcService
	public void doLoadGmy(Map<String, Object> req,
						  Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			List<Map<String, Object>> list =cmm.doLoadGmy(body, ctx,res);

		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	/**
	 * 保存过敏原
	 *
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveGmy(Map<String, Object> req,
						  Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicManageModel ccpm = new ClinicManageModel(
				dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			ccpm.doSaveGmy(body, res, ctx);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 根据病人ID查询病人信息，用于新增发热病人module
	 *
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryBRXX(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doQueryBRXX(req,res,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 根据JZXH查询MS_MZFR的SBXH
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQuerySBXH(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException {
		ClinicManageModel cmm = new ClinicManageModel(dao);
		try {
			cmm.doQuerySBXH(req,res,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

}

