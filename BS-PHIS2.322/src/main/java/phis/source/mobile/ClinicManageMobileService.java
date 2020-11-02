/**
 * @(#)ClinicManageMobileService.java 创建于 2013年6月13日10:24:15
 * 
 * 版权：版本所有 bsoft.com.cn 保留所有权力。
 * 
 */
package phis.source.mobile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import phis.application.cic.source.ClinicDisposalEntryModule;
import phis.application.cic.source.ClinicManageModel;
import phis.application.ivc.source.ClinicChargesProcessingModel;
import phis.application.ivc.source.ClinicPayBO;
import phis.application.ivc.source.ClinicPayContext;
import phis.application.ivc.source.PayContext;
import phis.application.pix.source.EmpiModel;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 家医诊疗服务
 * 
 * @author <a href="mailto:suny@bsoft.com.cn">suny</a>
 */
public class ClinicManageMobileService extends AbstractActionService implements
		DAOSupportable {
	/**
	 * 获取病人性质 和默认性质
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryNature(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		EmpiModel empiModel = new EmpiModel(dao);
		ClinicManageMobileModel mobileModel = new ClinicManageMobileModel(dao);
		Map<String, Object> body = new HashMap<String, Object>();
		try {
			Map<String, Object> defaultBrxz = empiModel.queryNature(ctx);
			List<Map<String, Object>> allBrxz = mobileModel.queryNature(ctx);
			body.put("defaultBrxz", defaultBrxz);
			body.put("allBrxz", allBrxz);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取常用药
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */

	@SuppressWarnings("unchecked")
	public void doLoadCommonDrugs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body1 = (Map<String, Object>) req.get("body");
		ClinicManageMobileModel mobileModel = new ClinicManageMobileModel(dao);
		List<Map<String, Object>> commonDrugs = new ArrayList<Map<String, Object>>();
		try {
			commonDrugs = mobileModel.LoadCommonDrugs(ctx, body1);
			res.put("body", commonDrugs);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取处方组套
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadPrescriptionGroup(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		ClinicManageMobileModel mobileModel = new ClinicManageMobileModel(dao);
		List<Map<String, Object>> prescriptionGroup = new ArrayList<Map<String, Object>>();
		try {
			prescriptionGroup = mobileModel.LoadPrescriptionGroup(ctx, reqBody);
			res.put("body", prescriptionGroup);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 1、根据empiid获取基本信息 2、病人性质和默认性质
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadUpdateData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		EmpiModel empiModel = new EmpiModel(dao);
		ClinicManageMobileModel mobileModel = new ClinicManageMobileModel(dao);
		Map<String, Object> body = new HashMap<String, Object>();
		try {
			Map<String, Object> defaultBrxz = empiModel.queryNature(ctx);
			List<Map<String, Object>> allBrxz = mobileModel.queryNature(ctx);
			Map<String, Object> baseInfo = mobileModel.loadUpdateData(ctx,
					reqBody);
			body.put("defaultBrxz", defaultBrxz);
			body.put("allBrxz", allBrxz);
			body.put("baseInfo", baseInfo);
			res.put("body", body);

		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 病历首页
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doInitClinicInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> ghmxBody = (Map<String, Object>) reqBody
				.get("ghmx");
		Map<String, Object> xtcsBody = (Map<String, Object>) reqBody
				.get("xtcs");
		ClinicManageModel cmm = new ClinicManageModel(dao);
		ClinicManageMobileModel cmmMobile = new ClinicManageMobileModel(dao);
		try {
			Map<String, Object> ghmx = cmmMobile.initClinicInfo(ghmxBody, ctx);
			Map<String, Object> xtcs = cmm.loadSystemParams(xtcsBody, ctx);
			body.put("ghmx", ghmx);
			body.put("xtcs", xtcs);
			String clinicId = ghmx.get("JZXH").toString();
			String brid = ghmx.get("BRID").toString();
			Map<String, Object> bcjlBody = new HashMap<String, Object>();
			bcjlBody.put("clinicId", Integer.parseInt(clinicId));
			bcjlBody.put("BRID", brid);
			bcjlBody.put("type", "5");
			Map<String, Object> bcjl = cmm.loadClinicInfo(bcjlBody, ctx);
			body.put("bcjl", bcjl);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}

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
		Map<String, Object> medcineInfo = (Map<String, Object>) body
				.get("medcineInfo");
		Map<String, Object> payProportion = (Map<String, Object>) body
				.get("payProportion");
		ClinicManageMobileModel cmm = new ClinicManageMobileModel(dao);
		try {
			Map<String, Object> item = cmm.doLoadMedicineInfo(medcineInfo, ctx);
			String YPXH = item.get("YPXH").toString();
			payProportion.put("FYXH", YPXH);
			item.putAll(BSPHISUtil.getPayProportion(payProportion, ctx, dao));
			res.put("body", item);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取药品信息失败!", e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "success");
	}

	/**
	 * 调入诊疗方案前，根据JZXH查询是否存在已收费的处方和存在已收费的处置
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doCostCount(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		ClinicManageMobileModel mobileModel = new ClinicManageMobileModel(dao);
		try {
			Map<String, Object> body = mobileModel.costCount(ctx, reqBody);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}

	}

	/**
	 * 调入诊疗方案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ControllerException 
	 * @throws NumberFormatException 
	 */
	@SuppressWarnings("unchecked")
	public void doAddZLFA(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException, NumberFormatException, ControllerException {
		Map<String, Object> bodyReq = (Map<String, Object>) req.get("body");
		int clinicId = Integer.parseInt(bodyReq.get("JZXH").toString());
		String brid = bodyReq.get("BRID").toString();
		// 诊断
		Map<String, Object> bodyZdReq = new HashMap<String, Object>();
		bodyZdReq.put("type", "2");
		bodyZdReq.put("clinicId", clinicId);
		bodyZdReq.put("brid", brid);
		// 处方和处置
		Map<String, Object> bodyCfCzReq = new HashMap<String, Object>();
		bodyCfCzReq.put("type", "3");
		bodyCfCzReq.put("clinicId", clinicId);
		bodyCfCzReq.put("brid", brid);
		ClinicManageModel cmm = new ClinicManageModel(dao);
		Map<String, Object> body = new HashMap<String, Object>();
		try {
			Map<String, Object> bodyZlfa = cmm.doSaveClinicTherapeutic(bodyReq,
					ctx);
			Map<String, Object> bodyCfCz = cmm.loadClinicInfo(bodyCfCzReq, ctx);
			Map<String, Object> bodyZd = cmm.loadClinicInfo(bodyZdReq, ctx);
			body.put("blxx", bodyZlfa.get("blxx"));
			body.put("ms_brzd", bodyZd.get("ms_brzd"));
			body.put("disposal", bodyCfCz.get("disposal"));
			body.put("measures", bodyCfCz.get("measures"));
			body.put("errorMsg", bodyZlfa.get("errorMsg"));
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取诊断部位字典
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("deprecation")
	public void doGetPosition(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = new HashMap<String, Object>();
		Map<String, Object> dicMap = new HashMap<String, Object>();
		List<Map<String, Object>> dicList = new ArrayList<Map<String, Object>>();
		Dictionary dic = DictionaryController.instance().getDic("phis.dictionary.position");
		Map<String, DictionaryItem> map = dic.getItems();
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			DictionaryItem dicItem = map.get(key);
			String text = dicItem.getText();
			dicMap.put(text, key);
		}
		dicList.add(dicMap);
		body.put("position", dicList);
		res.put("body", body);
	}

	/**
	 * 诊断初始化信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void doInitDiagnoseInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = new HashMap<String, Object>();
		// 常用诊断
		Map<String, Object> cyzd = (Map<String, Object>) req.get("cyzd");
		List<?> cnd1 = (List<?>) cyzd.get("cnd");
		int pageNo = (Integer) cyzd.get("pageNo");
		int pageSize = (Integer) cyzd.get("pageSize");
		// 病人诊断
		Map<String, Object> brzd = (Map<String, Object>) req.get("brzd");
		List<?> cnd2 = (List<?>) brzd.get("cnd");
		// 诊断部位字典
		Map<String, Object> dicMap = new HashMap<String, Object>();
		List<Map<String, Object>> dicList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> zdbwList = new ArrayList<Map<String, Object>>();
		Dictionary dic = DictionaryController.instance().getDic(
				"phis.dictionary.position");
		Map<String, DictionaryItem> map = dic.getItems();
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			Map<String, Object> zdbwMap = new HashMap<String, Object>();
			String key = iterator.next();
			DictionaryItem dicItem = map.get(key);
			String text = dicItem.getText();
			dicMap.put(text, key);
			zdbwMap.put("ZDBW_text", text);
			zdbwMap.put("ZDBW", key);
			zdbwList.add(zdbwMap);
		}
		dicList.add(dicMap);
		body.put("position", dicList);
		try {
			Map<String, Object> cyzdMap = dao.doList(cnd1, null,
					"phis.application.cic.schemas.GY_CYZD_CIC", pageNo,
					pageSize, null);
			List<Map<String, Object>> brzdList = dao.doList(cnd2, "PLXH",
					"phis.application.cic.schemas.MS_BRZD_CIC");
			body.put("cyzd", cyzdMap.get("body"));
			body.put("brzd", brzdList);
			body.put("zdbw", zdbwList);
			body.put("totalCount", cyzdMap.get("totalCount"));
		} catch (PersistentDataOperationException e) {
			throw new ServiceException(e);
		}
		res.put("body", body);
	}

	/**
	 * 划价收费 1、查询基本信息；2、单据；3、单据明细；4、单据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ControllerException 
	 */
	@SuppressWarnings("unchecked")
	public void doQueryPersonAndDJAndDJDetailsAndDJ(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ControllerException {
		Map<String, Object> body = new HashMap<String, Object>();
		Map<String, Object> personMap = new HashMap<String, Object>();
		List<Map<String, Object>> djList = new ArrayList<Map<String, Object>>();
		ClinicChargesProcessingModel ccpm = new ClinicChargesProcessingModel(
				dao);
		try {
			ccpm.doQueryPerson(req, res, ctx);
			personMap = (Map<String, Object>) res.get("body");
			ccpm.doQueryDJ(personMap, null, res, ctx);
			djList = (List<Map<String, Object>>) res.get("body");
			body.put("person", personMap);
			body.put("DJ", djList);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("划价收费查询失败！", e);
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
		try {
			Map<String, Object> map = (HashMap<String, Object>) req.get("body");
			Map<String, Object> body = new HashMap<String, Object>();
			// 创建业务对象并设值
			ClinicPayBO bo = new ClinicPayBO();
			Double amount = Double.valueOf((String) map.get("YSK"));
			bo.setAmount(BigDecimal.valueOf(amount));
			bo.setPayType(Integer.valueOf((String) map.get("SRFS")));
			bo.setScale(Integer.valueOf((String) map.get("FKJD")));
			// 由前台传来的应收款金额,付款精度,舍入方式计算应收款.
			PayContext context = new ClinicPayContext(bo);
			BigDecimal oprAmount = context.convertAmount();
			body.put("YSK", oprAmount.toString());
			res.put("body", body);
		} catch (Exception e) {
			throw new ServiceException("付费方式查询失败！", e);
		}
	}

	/**
	 * 获取组套信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */

	@SuppressWarnings("unchecked")
	public void doLoadGroup(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageMobileModel mobileModel = new ClinicManageMobileModel(dao);
		List<Map<String, Object>> commonDrugs = null;
		try {
			commonDrugs = mobileModel.LoadGroup(ctx, body);
			res.put("body", commonDrugs);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取常用项
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */

	@SuppressWarnings("unchecked")
	public void doLoadCommonTerm(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicManageMobileModel mobileModel = new ClinicManageMobileModel(dao);
		List<Map<String, Object>> commonDrugs = null;
		try {
			commonDrugs = mobileModel.LoadCommonTerm(ctx, body);
			res.put("body", commonDrugs);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 根据项目序号获取药品信息（助手方式）
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadCostInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> CostInfo = (Map<String, Object>) req
				.get("CostInfo");
		Map<String, Object> ZFBLMap = (Map<String, Object>) req.get("ZFBLMap");
		ClinicManageMobileModel cmmm = new ClinicManageMobileModel(dao);
		ClinicDisposalEntryModule cdem = new ClinicDisposalEntryModule(dao);
		try {
			Map<String, Object> response = cmmm.doLoadCostInfo(CostInfo, ctx);
			Object FYXH = response.get("YLXH");
			Object FYGB = response.get("FYGB");
			ZFBLMap.put("FYXH", Long.parseLong(FYXH.toString()));
			ZFBLMap.put("FYGB", Long.parseLong(FYGB.toString()));
			Map<String, Object> ZFBLres = cdem
					.doGetZFBL(ZFBLMap, res, dao, ctx);
			response.putAll((Map<String, Object>) ZFBLres.get("ZFBL"));
			res.put("body", response);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取药品信息失败!", e);
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
		Map<String, Object> groupMap = (Map<String, Object>) req
				.get("groupMap");
		Map<String, Object> ZFBLMap = (Map<String, Object>) req.get("ZFBLMap");
		ClinicManageMobileModel cmmm = new ClinicManageMobileModel(dao);
		ClinicDisposalEntryModule cdem = new ClinicDisposalEntryModule(dao);
		try {
			List<Map<String, Object>> response = cmmm.doLoadPersonalSet(
					groupMap, ctx);
			Object FYXH = response.get(0).get("YLXH");
			Object FYGB = response.get(0).get("FYGB");
			ZFBLMap.put("FYXH", Long.parseLong(FYXH.toString()));
			ZFBLMap.put("FYGB", Long.parseLong(FYGB.toString()));
			Map<String, Object> ZFBLres = (Map<String, Object>) cdem.doGetZFBL(
					ZFBLMap, res, dao, ctx).get("ZFBL");
			for (Map<String, Object> map : response) {
				map.putAll(ZFBLres);
			}
			res.put("body", response);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询组套信息失败，请联系管理员！", e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "success");
	}

	/**
	 * 根据机构id获取执行科室的字典
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadKSDMDic(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		ClinicManageMobileModel mobileModel = new ClinicManageMobileModel(dao);
		List<Map<String, Object>> prescriptionGroup = new ArrayList<Map<String, Object>>();
		try {
			prescriptionGroup = mobileModel.LoadKSDM(ctx, reqBody);
			res.put("body", prescriptionGroup);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSimpleLoad(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String CFSBSTR = (String) reqBody.get("CFSB");
		Long CFSB = Long.parseLong(CFSBSTR);
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "CFSB", "l", CFSB);
		try {
			List<Map<String, Object>> data = dao.doList(cnd, null,
					BSPHISEntryNames.MS_CF01);
			if (null == data || data.isEmpty()) {
				res.put("body", new HashMap<String, Object>());
			} else {
				res.put("body", data.get(0));
			}
		} catch (PersistentDataOperationException e) {
			throw new ServiceException("默认加载第一条处方失败！", e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "success");
	}

	/**
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadMSBRDA(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBody.get("empiId");
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		try {
			List<Map<String, Object>> data = dao.doList(cnd, null,
					BSPHISEntryNames.MS_BRDA);
			if (null == data || data.isEmpty()) {
				res.put("body", new HashMap<String, Object>());
			} else {
				res.put("body", data.get(0));
			}
		} catch (PersistentDataOperationException e) {
			throw new ServiceException("查询病人档案表失败！", e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "success");
	}
}
