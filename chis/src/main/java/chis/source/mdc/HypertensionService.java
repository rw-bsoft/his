/**
 * @(#)HypertensionService.java Created on 2011-12-27 下午5:23:37
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.control.ControlRunner;
import chis.source.dic.BusinessType;
import chis.source.dic.ControlResult;
import chis.source.dic.DiabetesRiskDataSource;
import chis.source.dic.DiagnosisType;
import chis.source.dic.FixType;
import chis.source.dic.PastHistoryCode;
import chis.source.dic.PlanStatus;
import chis.source.dic.VisitEffect;
import chis.source.dic.VisitResult;
import chis.source.dic.WorkType;
import chis.source.empi.EmpiModel;
import chis.source.log.VindicateLogService;
import chis.source.phr.HealthRecordModel;
import chis.source.phr.HealthRecordService;
import chis.source.phr.LifeStyleModel;
import chis.source.phr.PastHistoryModel;
import chis.source.pub.PublicModel;
import chis.source.pub.PublicService;
import chis.source.service.ServiceCode;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import chis.source.visitplan.VisitPlanCreator;
import chis.source.visitplan.VisitPlanModel;
import chis.source.worklist.WorkListModel;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

/**
 * @description 高血压 档案业务服务类
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HypertensionService extends MDCService {
	private static final Logger logger = LoggerFactory
			.getLogger(HypertensionService.class);
	private VisitPlanCreator visitPlanCreator;
	/**
	 * 获得visitPlanCreator
	 * 
	 * @return the visitPlanCreator
	 */
	public VisitPlanCreator getVisitPlanCreator() {
		return visitPlanCreator;
	}

	/**
	 * 设置visitPlanCreator
	 * 
	 * @param visitPlanCreator
	 *            the visitPlanCreator to set
	 */
	public void setVisitPlanCreator(VisitPlanCreator visitPlanCreator) {
		this.visitPlanCreator = visitPlanCreator;
	}
	
	public VindicateLogService getvLogService() {
		return vLogService;
	}

	public void setVisitPlanCreator(VindicateLogService vLogService) {
		this.vLogService = vLogService;
	}
	/**
	 * 查询高血压记录 --高血压档案管理主页list
	 * 
	 * @param req
	 *            ['schema':'','cnd':'','pageNo':'','pageSize':'']
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doListHypertensionRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HypertensionModel hm = new HypertensionModel(dao);
		Map<String, Object> resultMap = null;
		String schemaId = (String) req.get("schema");
		List queryCnd = null;
		//yx列表导出时判断要不要查随访计划，原先随访计划条数多了会报错，而且不用去查什么随访计划,值从PrintLoaderCHIS传过来
		Boolean print=false;
		if(req.containsKey("print")){
			if(req.get("print").toString().equals("true")){
				print=true;
			}
		}
		if (req.containsKey("cnd")) {
			queryCnd = (List) req.get("cnd");
		}
		String queryCndsType = null;
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String sortInfo = null;
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 1;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		String year = new Date().getYear() + "";
		if (req.containsKey("year")) {
			year = req.get("year") + "";
		}
		String checkType = "1";
		if (req.containsKey("checkType")) {
			checkType = (String) req.get("checkType");
		}
		PublicModel hrModel = new PublicModel(dao);
		resultMap = hrModel.queryRecordList(schemaId, queryCnd, queryCndsType,
				sortInfo, pageSize, pageNo, year, checkType);
		List<Map<String, Object>> resBody = (List<Map<String, Object>>) resultMap
				.get("body");
		
		if(print){
			res.put("body", resBody);
			return;
		}
		// 取出记录中empiId的值放到empiIdList中

		List<String> empiIdList = new ArrayList<String>();
		if (resBody != null) {
			for (int i = 0; i < resBody.size(); i++) {
				HashMap<String, Object> rec = (HashMap<String, Object>) resBody
						.get(i);
				String empiId = (String) rec.get("empiId");
				empiIdList.add(empiId);
			}
		}
		if (empiIdList.size() == 0) {
			return;
		}
		// 取出高血压随访未做的随访记录
		List<Map<String, Object>> list = null;
		try {
			list = hm.checkHasVisitUndo(empiIdList,BusinessType.GXY,"高血压");
		} catch (ModelDataOperationException e) {
			logger.error(
					"Failed to find record of hypertension visit plan. message:",
					e);
			throw new ServiceException(e);
		}
		// 将未做随访的高血压记录标识为true,(标识属性名为needDoVisit)
		if (list != null) {
			for (int i = 0; i < resBody.size(); i++) {
				HashMap<String, Object> rec = (HashMap<String, Object>) resBody
						.get(i);
				String empiId = (String) rec.get("empiId");
				for (Iterator<Map<String, Object>> it = list.iterator(); it
						.hasNext();) {
					Map<String, Object> map = it.next();
					if (empiId.equals(map.get("empiId"))) {
						if ((Long) map.get("count") > 0) {
							rec.put("needDoVisit", true);
						}
						it.remove();
						break;
					}
				}
			}
		}
		res.put("body", resBody);
		res.put("pageSize", resultMap.get("pageSize"));
		res.put("pageNo", resultMap.get("pageNo"));
		res.put("totalCount", resultMap.get("totalCount"));
	}

	/**
	 * 高血压加载 --高血压查看
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doInitializeRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException,
			ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		if (StringUtils.isEmpty(phrId)) {
			logger.info("Failed to initialize HypertensionRecord.");
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "高血压加载失败");
			return;
		}
		String empiId = (String) body.get("empiId");

		HypertensionModel hm = new HypertensionModel(dao);
		// 判断高血压是否存在
		String manaDoctorId = null;
		Map<String, Object> resMap = null;
		resMap = hm.getHypertensionByPhrid(phrId);
		if (null != resMap) {
			manaDoctorId = (String) resMap.get("manaDoctorId");
			resMap = SchemaUtil.setDictionaryMessageForForm(resMap,
					MDC_HypertensionRecord);
			resMap.put("op", "update");
		} else {
			resMap = new HashMap<String, Object>();
			// resMap.put("phrId", phrId);
			resMap.put("empiId", empiId);
			resMap.put("op", "create");
		}
		Map<String, Object> resBody = new HashMap<String, Object>();
		resBody.put(MDC_HypertensionRecord + Constants.DATAFORMAT4FORM, resMap);
		Map<String, Boolean> hrControlMap = this.getHRControl(resMap, ctx);
		resBody.put("MDC_HypertensionRecord" + "_actions", hrControlMap);
		String hrStatus = "";
		if ("create".equals(resMap.get("op"))) {
			// 获取个人档案，取出其他责任医生相关信息初始化责任医生及管辖机构
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			Map<String, Object> hrMap = hrModel.getHealthRecordByEmpiId(empiId);
			Map<String, Object> hrFormMap = SchemaUtil
					.setDictionaryMessageForForm(hrMap, EHR_HealthRecord);
			resMap.put("manaDoctorId", hrFormMap.get("manaDoctorId"));
			resMap.put("manaUnitId", hrFormMap.get("manaUnitId"));
			hrStatus = (String) hrMap.get("status");
			// 取得个人生活习惯相关字段用于表单用
			LifeStyleModel lsm = new LifeStyleModel(dao);
			Map<String, Object> lifeMap = lsm.getLifeStyleByEmpiId(empiId);
			hm.initLifeStyleDataForHypertensionRecord(resMap, lifeMap);

			// 获取个人家族的生活习惯，组装成一个字符串
			PastHistoryModel phm = new PastHistoryModel(dao);
			String familyHistory = phm.getFamilyPastHistoryByEmpiId(empiId);
			resMap.put("familyHistroy", familyHistory);
		}
		// 获取档案相关的药品
		HypertensionMedicineModel hmm = new HypertensionMedicineModel(dao);
		List<Map<String, Object>> list = hmm.getRecordMedicine(phrId);
		resBody.put(MDC_HypertensionMedicine + Constants.DATAFORMAT4LIST, list);
		// 获取药品列表权限控制
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("empiId", empiId);
		paraMap.put("phrId", phrId);
		paraMap.put("chis.application.hr.schemas.EHR_HealthRecord.status",
				hrStatus);
		paraMap.put(
				"chis.application.hy.schemas.MDC_HypertensionRecord.status",
				resMap.get("status"));
		paraMap.put(
				"chis.application.hy.schemas.MDC_HypertensionRecord.manaDoctorId",
				manaDoctorId);
		paraMap.put("recordNum", list.size());
		Map<String, Boolean> hrmControlMap = this.getHRMControl(paraMap, ctx);
		resBody.put("MDC_HypertensionMedicine" + "_actions", hrmControlMap);
		resBody.put("manaDoctorId", manaDoctorId);
		res.put("body", resBody);
	}

	/**
	 * 根据pkey获高血压信息
	 * 
	 * @param req
	 *            ['pkey':'']
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetHypertensionInfoByPhrid(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String phrid = (String) req.get("pkey");
		if (StringUtils.isEmpty(phrid)) {
			logger.error("Failed to get pkey. message:");
			res.put(Service.RES_CODE, ServiceCode.CODE_UNSUPPORTED_ENCODING);
			res.put(Service.RES_MESSAGE, "无效的请求，获取参数失败！");
			throw new ServiceException(ServiceCode.CODE_UNSUPPORTED_ENCODING,
					"无效的请求，获取参数失败！");
		}
		HypertensionModel hm = new HypertensionModel(dao);
		Map<String, Object> hInfo = null;
		try {
			hInfo = hm.getHypertensionByPhrid(phrid);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get record of hypertension info. message:",
					e);
			throw new ServiceException(e);
		}

		// 获取个人基本信息的性别和年龄
		Map<String, Object> sexBirthdayMap = null;
		String empiId = (String) hInfo.get("empiId");
		EmpiModel em = new EmpiModel(dao);
		try {
			sexBirthdayMap = em.getSexAndBirthday(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Get sex and birthday failed. message:", e);
			throw new ServiceException(e);
		}
		Date createDate = (Date) hInfo.get("createDate");
		Date birthday = (Date) sexBirthdayMap.get("birthday");
		if (createDate == null || birthday == null) {
			logger.error("Get createDate or birthday failed. ");
			throw new ServiceException(ServiceCode.CODE_BUSINESS_DATA_NULL,
					"计算周岁失败！");
		}
		int age = BSCHISUtil.calculateAge(birthday, createDate);

		hInfo.put("sex", sexBirthdayMap.get("sexCode"));
		hInfo.put("age", age);

		res.put("body", hInfo);
	}

	/**
	 * 检查是否已经建档
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doIfHypertensionRecordExist(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String schema = (String) req.get("schema");
		List<?> cnd = (List<?>) req.get("cnd");
		String order = (String) req.get("order");
		if (cnd == null || cnd.size() == 0 || StringUtils.isEmpty(schema)) {
			logger.error("Failed to get schema,cnd. message:");
			res.put(Service.RES_CODE, ServiceCode.CODE_UNSUPPORTED_ENCODING);
			res.put(Service.RES_MESSAGE, "无效的请求，获取参数失败！");
			throw new ServiceException(ServiceCode.CODE_UNSUPPORTED_ENCODING,
					"无效的请求，获取参数失败！");
		}

		HypertensionModel hm = new HypertensionModel(dao);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = hm.findHypertensionRecord(cnd, order, schema);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get record of hypertension info. message:",
					e);
			res.put(Service.RES_CODE, e.getCode());
			res.put(Service.RES_MESSAGE, "检查是否已建档失败。");
			throw new ServiceException(e);
		}

		if (rsList != null && rsList.size() > 0) {
			res.put(RES_CODE, ServiceCode.CODE_TARGET_EXISTS);
			res.put(RES_MESSAGE, "已经注册过高血压档案.");
			return;
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "未注册高血压档案.");
	}

	/**
	 * 保存高血压档案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveHypertensionRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException,
			PersistentDataOperationException {
		
		//传日志到大数据接口 （社区档案管理）--wdl
		String curUserId = UserUtil.get(UserUtil.USER_ID);
		String curUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
		String organname = UserUtil.get(UserUtil.MANAUNIT_NAME);
		String USER_NAME = UserUtil.get(UserUtil.USER_NAME);
		
		Date curDate = new Date();
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String curDate1= sdf1.format( new Date());
		int num =(int) (Math.random( )*50+50) ;
		try {
		String ip = PublicService.getIpByEthNum();	
		String ipc = InetAddress.getLocalHost().getHostAddress();
				String json="{ \n"+
			"\"orgCode\":\""+curUnitId+"\",\n"+
			"\"orgName\":\""+organname+"\",\n"+
			"\"ip\":\""+ipc+"\",\n"+
			"\"opertime\":\""+curDate1+"\",\n"+
			"\"operatorCode\":\""+curUserId+"\",\n"+
			"\"operatorName\":\""+USER_NAME+"\",\n"+
			"\"callType\":\"02\",\n"+
			"\"apiCode\":\"SQDAGL\",\n"+
			"\"operSystemCode\":\"ehr\",\n"+
			"\"operSystemName\":\"健康档案系统\",\n"+
			"\"fromDomain\":\"ehr_yy\",\n"+
			"\"toDomain\":\"ehr_mb\",\n"+
			"\"clientAddress\":\""+ipc+"\",\n"+
			"\"serviceBean\":\"esb.SQDAGL\",\n"+
			"\"methodDesc\":\"void doSaveHypertensionRecord()\",\n"+
			"\"statEnd\":\""+curDate1+"\",\n"+
			"\"stat\":\"1\",\n"+
			"\"avgTimeCost\":\""+num+"\",\n"+
			"\"request\":\"PublicService.httpURLPOSTCase(json)\",\n"+
			"\"response\":\"200\"\n"+
		          "}";	
				System.out.println(json);
            PublicService.httpURLPOSTCase(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
		
		String op = (String) req.get("op");
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		String phrId = (String) body.get("phrId");
		String empiId = (String) body.get("empiId");
		String status = (String) body.get("status");
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put("phrId", phrId);
		res.put("body", resBodyMap);
		if (op.equals("create")) {
			body.put("planTypeCode", "-1");
		}
		// 为防止ehrView里打开高血压创建页面后再去增加“既往史”后再回来没有家族史记录
		String familyHistroy = (String) body.get("familyHistroy");
		if (StringUtils.isEmpty(familyHistroy)) {
			// 获取个人家族的生活习惯，组装成一个字符串
			PastHistoryModel phm = new PastHistoryModel(dao);
			familyHistroy = phm.getFamilyPastHistoryByEmpiId(empiId);
			body.put("familyHistroy", familyHistroy);
			resBodyMap.put("familyHistroy", familyHistroy);
		}
		HypertensionModel hm = new HypertensionModel(dao);
		// 保存高血压档案信息
		Map<String, Object> rsMap = null;
		try {
			rsMap = hm.saveHypertensionRecord(op, body, false);
			HypertensionRiskModel riskModel = new HypertensionRiskModel(dao);
			riskModel.removeHypertensionRecordWorkList(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Save hypertensionRecord failed.", e);
			throw new ServiceException(e);
		}
		if(vLogService!=null){
		vLogService.saveVindicateLog(MDC_HypertensionRecord, op, phrId, dao,
				empiId);
		vLogService.saveRecords("GAO", op, dao, empiId);
		}
		//标识签约任务完成
		this.finishSCServiceTask(empiId,GXYDAGL_GXYFW,null,dao);
		if (rsMap != null) {
			resBodyMap.putAll(rsMap);
		}

		// 保存个人既往史(疾病史)记录
		savePastHistory(empiId, body, dao, ctx);

		// 更新生活习惯到个人健康档案去。
		try {
			hm.updateLifeStyle(body);
		} catch (ModelDataOperationException e) {
			logger.error("Update life style failed.", e);
			throw new ServiceException(e);
		}
		if (op.equals("create")) {
			body.put("fixType", FixType.CREATE);
			HashMap<String, Object> jsonRec = (HashMap<String, Object>) body
					.clone();
			String controlResult = ControlResult.NEW_DOC;
			// 生成初次评估信息
			jsonRec.put("controlResult", controlResult);

			try {
				// 组装 riskiness [吸烟、血压、年龄] 数据
				hm.makeGroup(jsonRec);
				// 生成初次评估信息
				jsonRec.put("fixUser", UserUtil.get(UserUtil.USER_ID));
				jsonRec.put("fixUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
				jsonRec.put("manaUnitId", UserUtil.get(UserUtil.MANAUNIT_ID));
				if(req.get("app")==null){
				hm.saveHypertensionFixGroupInfo(op, jsonRec, false);
				}
				WorkListModel wlm = new WorkListModel(dao);
				if (!wlm.isExistWorkList(phrId, empiId,
						WorkType.MDC_HYPERTENSIONFIXGROUP, dao)) {
					jsonRec.put("workType", WorkType.MDC_HYPERTENSIONFIXGROUP);
					jsonRec.put("recordId", phrId);
					jsonRec.put("doctorId", jsonRec.get("manaDoctorId"));
					jsonRec.put("beginDate", new Date());
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.YEAR, 1);
					jsonRec.put("endDate", cal.getTime());
					hm.saveFixGroupWorkList(jsonRec);
				}
			} catch (ModelDataOperationException e) {
				logger.error("Save hypertension set group data failed.", e);
				throw new ServiceException(e);
			}

			// 更新健康档案是否高血压字段
			try {
				hm.updateIsHypertensionOfHealthRecord(phrId);
			} catch (ModelDataOperationException e) {
				logger.error(
						"update isHypertension of EHR_HealthRecord failed.", e);
				res.put(RES_CODE, e.getCode());
				res.put(RES_MESSAGE, e.getMessage());
				throw new ServiceException(e);
			}

			// 取药品情况操作权限
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("empiId", empiId);
			paraMap.put("phrId", phrId);
			paraMap.put("chis.application.hr.schemas.EHR_HealthRecord.status",
					"0");
			paraMap.put(
					"chis.application.hy.schemas.MDC_HypertensionRecord.status",
					"0");
			paraMap.put(
					"chis.application.hy.schemas.MDC_HypertensionRecord.manaDoctorId",
					body.get("manaDoctorId"));
			paraMap.put("recordNum", 0);
			Map<String, Boolean> hrmControlMap = this.getHRMControl(paraMap,
					ctx);
			resBodyMap.put("MDC_HypertensionMedicine" + "_actions",
					hrmControlMap);
		} else {
			// 更新未评估前初次评估初始数据（身高，体重，BMI,血压值）
			HypertensionFixGroupModel hfgModel = new HypertensionFixGroupModel(
					dao);
			Map<String, Object> fgMap = hfgModel.getFirstFixGroup(empiId);
			if (fgMap != null && fgMap.size() > 0) {
				String hypertensionGroup = (String) fgMap
						.get("hypertensionGroup");
				if (StringUtils.isEmpty(hypertensionGroup)) {
					Map<String, Object> fguMap = new HashMap<String, Object>();
					fguMap.put("fixId", fgMap.get("fixId"));
					fguMap.put("empiId", fgMap.get("empiId"));
					fguMap.put("height", body.get("height"));
					fguMap.put("weight", body.get("weight"));
					fguMap.put("bmi", body.get("bmi"));
					fguMap.put("constriction", body.get("constriction"));
					fguMap.put("diastolic", body.get("diastolic"));
					hm.saveHypertensionFixGroupInfo("update", fguMap, false);
				}
			}

		}
		if (op.equals("create")) {
			// 更新工作列表
			WorkListModel wlm = new WorkListModel(dao);
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("empiId", empiId);
			m.put("recordId", phrId);
			m.put("workType", "21");
			try {
				wlm.deleteWorkList(m);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to update PUB_WorkList " + "column.", e);
				throw new ServiceException(e);
			}
		}

		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("empiId", empiId);
		paraMap.put("phrId", phrId);
		paraMap.put("status", status);
		paraMap.put("manaDoctorId", body.get("manaDoctorId"));
		paraMap.put("manaUnitId", body.get("manaUnitId"));
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		try {
			data = ControlRunner.run(MDC_HypertensionRecord, paraMap, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check checkup ICD record control error .", e);
			throw e;
		}
		resBodyMap.put("MDC_HypertensionRecord" + "_actions", data);
	}

	/**
	 * 保存个人既往史
	 * 
	 * @param empiId
	 * @param body
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 * @throws PersistentDataOperationException 
	 */
	private void savePastHistory(String empiId, Map<String, Object> body,
			BaseDAO dao, Context ctx) throws ModelDataOperationException,
			ServiceException , PersistentDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		 parameters.put("empiId", body.get("empiId"));
		 String hql = "";
		 hql = "select count(*) as cnt from EHR_PastHistory where empiId=:empiId and diseaseCode=:diseaseCode and pastHisTypeCode=:pastHisTypeCode";
		 parameters.put("pastHisTypeCode", PastHistoryCode.SCREEN);
		 parameters.put("diseaseCode", PastHistoryCode.PASTHIS_SCREEN_NOT_HAVE);
		 List<?> l = dao.doQuery(hql, parameters);
		 if ((Long) ((Map<?, ?>) l.get(0)).get("cnt") == 1) {
			hql = "delete from EHR_PastHistory where empiId=:empiId and diseaseCode=:diseaseCode and pastHisTypeCode=:pastHisTypeCode";
			parameters = new HashMap<String, Object>();
			parameters.put("empiId", body.get("empiId"));
			parameters.put("pastHisTypeCode", PastHistoryCode.SCREEN);
			parameters.put("diseaseCode",
					PastHistoryCode.PASTHIS_SCREEN_NOT_HAVE);
			dao.doUpdate(hql, parameters);
		}	
		Map<String, Object> phMap = new HashMap<String, Object>();
		phMap.put("empiId", empiId);
		phMap.put("pastHisTypeCode", PastHistoryCode.SCREEN);
		phMap.put("methodsCode", body.get("methodsCode"));
		//phMap.put("methodsCode", "3");
		phMap.put("diseaseCode", PastHistoryCode.PASTHIS_SCREEN_HYPERTENSION);
		phMap.put("diseaseText", "高血压");
		phMap.put("confirmDate", body.get("confirmDate"));
		phMap.put("recordUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		phMap.put("recordUser", UserUtil.get(UserUtil.USER_ID));
		phMap.put("recordDate", new Date());
		phMap.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		phMap.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		phMap.put("lastModifyDate", new Date());
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		Map<String, Object> phRecordMap = hrModel.getPastHistory(empiId,
				PastHistoryCode.SCREEN,
				PastHistoryCode.PASTHIS_SCREEN_HYPERTENSION);
		String phOp = "update";
		if (phRecordMap == null) {
			phOp = "create";
		} else {
			phMap.put("pastHistoryId", phRecordMap.get("pastHistoryId"));
		}
		hrModel.savePastHistory(phOp, phMap, vLogService);
	}

	/**
	 * 获取高血压用药记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetHyperMedicine(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		// 获取档案相关的药品
		HypertensionMedicineModel hmm = new HypertensionMedicineModel(dao);
		List<Map<String, Object>> list = null;
		try {
			list = hmm.getRecordMedicine(phrId);
		} catch (ModelDataOperationException e) {
			logger.error("Get hypertension medicine failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", list);
	}

	/**
	 * 保存高血压用药记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveHyperMedicine(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String op = (String) req.get("op");
		Map<String, Object> record = (Map<String, Object>) req.get("body");
		Map<String, Object> resBody = new HashMap<String, Object>();
		resBody.putAll(record);
		HypertensionMedicineModel hmm = new HypertensionMedicineModel(dao);
		Map<String, Object> rsMap = null;
		boolean validate = true;
		if(req.get("app")!=null){
			validate = false;
		}
		try {
			rsMap = hmm.saveHyperMedicine(op, record, validate);
		} catch (ModelDataOperationException e) {
			logger.error("Save hypertension medicine fialed.", e);
			throw new ServiceException(e);
		}
		if (rsMap != null) {
			resBody.putAll(rsMap);
			// 当增加时重新取权限
			if ("create".equals(op)) {
				Map<String, Object> cpMap = (Map<String, Object>) record
						.get("cp");
				cpMap.put("recordNum", (Integer) cpMap.get("recordNum") + 1);
				Map<String, Boolean> hrmControlMap = this.getHRMControl(cpMap,
						ctx);
				resBody.put("_actions", hrmControlMap);
			}

		}
		String recordId = (String) resBody.get("recordId");
		vLogService.saveVindicateLog(MDC_HypertensionMedicine, op, recordId,
				dao);
		res.put("body", resBody);
	}

	/**
	 * 删除高血压用药记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveHyperMedicine(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String recordId = (String) req.get("pkey");
		HypertensionMedicineModel hmm = new HypertensionMedicineModel(dao);
		try {
			hmm.deleteHyperMedicineByPkey(recordId);
		} catch (ModelDataOperationException e) {
			logger.error("Delete hypertension medicine by pkey=[" + recordId
					+ "] failed.", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MDC_HypertensionMedicine, "4", recordId,
				dao);
		Map<String, Object> resBody = new HashMap<String, Object>();
		Map<String, Object> cpMap = (Map<String, Object>) req.get("body");
		cpMap.put("recordNum", (Integer) cpMap.get("recordNum") - 1);
		Map<String, Boolean> hrmControlMap = this.getHRMControl(cpMap, ctx);
		resBody.put("_actions", hrmControlMap);
		res.put("body", resBody);
	}

	/**
	 * 注销高血压档案 (专档注销 即只注销本子档)<br>
	 * 注销原因为 死亡或迁出 会去调用 健康档案服务[HealthRcordService]中的全档注销服务
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLogoutHypertensionRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		String empiId = (String) body.get("empiId");
		String cancellationReason = (String) body.get("cancellationReason");
		String deadReason = (String) body.get("deadReason");

		HypertensionModel hypModel = new HypertensionModel(dao);
		try {
			hypModel.logoutHypertensionRecord(phrId, cancellationReason,
					deadReason);
		} catch (ModelDataOperationException e) {
			logger.error("Logout hypertension record failed.", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MDC_HypertensionRecord, "3", phrId, dao,
				empiId);
		// 注销
		vLogService.saveRecords("GAO", "logout", dao, empiId);
		// ------------------

		// **注销未执行过的随访计划
		VisitPlanModel vpModel = new VisitPlanModel(dao);
		try {
			String[] businessTypes = { BusinessType.GXY,
					BusinessType.HYPERINQUIRE };
			vpModel.logOutVisitPlan(vpModel.RECORDID, phrId, businessTypes);
		} catch (ModelDataOperationException e) {
			logger.error("Logout hypertension visit plan failed.", e);
			throw new ServiceException(e);
		}
		// 修改健康档案中是否为高血压病人字段值为 “否”
		HealthRecordModel hrm = new HealthRecordModel(dao);
		try {
			hrm.clearChronicDiseaseFlag(phrId, "isHypertension");
		} catch (ModelDataOperationException e) {
			logger.error("Update health record isHypertension is [n] failed.",
					e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 取消注销/ 恢复
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRevertHypertensionRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		HypertensionModel hsModel = new HypertensionModel(dao);
		HypertensionVisitModel hvModel = new HypertensionVisitModel(dao);
		try {
			hsModel.revertHypertensionRecord(phrId);
		} catch (ModelDataOperationException e) {
			logger.error("Revert hypertension record failed.", e);
			throw new ServiceException(e);
		}
		VisitPlanModel vpModel = new VisitPlanModel(dao);
		try {
			vpModel.revertVisitPlan(vpModel.RECORDID, phrId, BusinessType.GXY,
					BusinessType.HYPERINQUIRE);
		} catch (ModelDataOperationException e) {
			logger.error("Revert hypertension visit plan failed.", e);
			throw new ServiceException(e);
		}
		Map<String, Object> visitMap = null;
		try {
			visitMap = hvModel.searchOverHypertensionVisit(phrId);
		} catch (ModelDataOperationException e) {
			logger.error("search over hypertension visit after today failed.",
					e);
			throw new ServiceException(e);
		}
		if (visitMap != null && !visitMap.isEmpty()) {
			String visitId = (String) visitMap.get("visitId");
			try {
				vpModel.revertOverVisitPlan(phrId, visitId, BusinessType.GXY);
			} catch (ModelDataOperationException e) {
				logger.error("revert over hypertension visit failed.", e);
				throw new ServiceException(e);
			}
			try {
				hvModel.deleteOverHypertensionVisit(phrId, visitId);
			} catch (ModelDataOperationException e) {
				logger.error("delete over hypertension visit failed.", e);
				throw new ServiceException(e);
			}
		}
	}

	/**
	 * 注销核实
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doCheckHypertensionRecordLogout(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		HypertensionModel hm = new HypertensionModel(dao);
		try {
			hm.checkHyperSubRecordLogout(phrId);
		} catch (ModelDataOperationException e) {
			logger.error("Check hypertension record logout failed.", e);
			throw new ServiceException(e);
		}
	}

	// =======================-- 高血压用药信息 --===============================
	/**
	 * 获取处方药品列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetRecipeMedicineList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = (String) req.get("empiId");
		if (StringUtils.isEmpty(empiId)) {
			logger.error("Failed to get empiId. message:");
			res.put(Service.RES_CODE, ServiceCode.CODE_UNSUPPORTED_ENCODING);
			res.put(Service.RES_MESSAGE, "无效的请求，获取参数失败！");
			throw new ServiceException(ServiceCode.CODE_UNSUPPORTED_ENCODING,
					"无效的请求，获取参数失败！");
		}
		HypertensionMedicineModel hmm = new HypertensionMedicineModel(dao);
		List<Map<String, Object>> rsList = null;
		try {
			// 获取处方药品列表
			rsList = hmm.getRecipeMedicineList(empiId);
		} catch (Exception e) {
			logger.error("update isHypertension of EHR_HealthRecord failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", rsList);
	}

	private Map<String, Boolean> getHRControl(Map<String, Object> paraMap,
			Context ctx) throws ServiceException {
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		try {
			data = ControlRunner.run(MDC_HypertensionRecord, paraMap, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check MDC_HypertensionRecord control error.", e);
			throw e;
		}
		return data;
	}

	private Map<String, Boolean> getHRMControl(Map<String, Object> paraMap,
			Context ctx) throws ServiceException {
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		try {
			data = ControlRunner.run(MDC_HypertensionMedicine, paraMap, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check MDC_HypertensionMedicine control error.", e);
			throw e;
		}
		return data;
	}

	// ===========================--分组评估--=================================
	/**
	 * 加载分组评估
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doInitializeFixGroup(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		HypertensionFixGroupModel hfgm = new HypertensionFixGroupModel(dao);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = hfgm.getFixGroupByEmpiId(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Get hypertension fixGroup failed.", e);
			e.printStackTrace();
			throw new ServiceException(e);
		}
		res.put(MDC_HypertensionFixGroup + Constants.DATAFORMAT4LIST, rsList);
		Map<String, Object> resMap = null;
		if (rsList.size() > 0) {
			resMap = SchemaUtil.setDictionaryMessageForForm(rsList.get(0),
					MDC_HypertensionFixGroup);
		}
		res.put(MDC_HypertensionFixGroup + Constants.DATAFORMAT4FORM, resMap);

	}

	/**
	 * 新建分组评估*数据初始化<br>
	 * 返回左边list格式数据，由左边选中增加的最后一条数据转为form格式填到右边表单中
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ControllerException
	 */
	@SuppressWarnings("unchecked")
	public void doInitializeCreateGroup(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ControllerException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String empiId = (String) reqBody.get("empiId");
		HashMap<String, Object> resBody = (HashMap<String, Object>) res
				.get("body");
		if (resBody == null) {
			resBody = new HashMap<String, Object>();
			res.put("body", resBody);
		}

		try {
			// 获取随访计划最大序号
			logger.info(
					"get sn of PUB_VisitPlan by empiId={} and businessType={}",
					empiId, BusinessType.GXY);
			HypertensionVisitModel hvm = new HypertensionVisitModel(dao);
			int sn = hvm.getVisitPlanMaxSN(empiId, BusinessType.GXY);
			resBody.put("sn", sn);

			// 获取fixid最大的一条 高血压定转组 信息 -by empiId
			logger.info("Load MDC_HypertensionFixGroup by empiId={}", empiId);
			HypertensionFixGroupModel hfgm = new HypertensionFixGroupModel(dao);
			Map<String, Object> lastFixGroup = hfgm.getLastFixGroup(empiId);
			if (lastFixGroup != null) {
				resBody.putAll(SchemaUtil.setDictionaryMessageForList(
						lastFixGroup, MDC_HypertensionFixGroup));
			}

			// 定转组类型
			resBody.put("fixType", FixType.NON_FIX_DATE);
			String fixTypeText = DictionaryController.instance()
					.get(MDC_HypertensionFixGroup + "_fixType")
					.getText(FixType.NON_FIX_DATE);
			resBody.put("fixType_text", fixTypeText);

			//
			if (reqBody.get("autoCreate") != null
					&& (Boolean) reqBody.get("autoCreate") == false) {
				return;
			}
			// 查询高血压随访的 危险因素(riskiness)、靶器官损害(targetHurt)、并发症(complication)
			String selectFields = "riskiness as riskiness,targetHurt as targetHurt, complication as complication";
			List<Map<String, Object>> rsList = null;
			rsList = hvm.getPartOfHypertensionVisitByEmpiId(selectFields,
					empiId);
			// 没有随访数据直接返回
			if (rsList.isEmpty()) {
				return;
			}
			//
			Map<String, Object> fixGroupFieldsMap = rsList.get(0);
			fixGroupFieldsMap.put("empiId", empiId);
			// @@ 新档案，初次定组。生成定组信息以及随访计划都需要。
			// @@ 默认为新病人，从哪里去取这个值
			String controlResult = ControlResult.NEW_DOC;
			fixGroupFieldsMap.put("controlResult", controlResult);
			UserRoleToken ur = UserRoleToken.getCurrent();
			String uid = ur.getUserId();
			String unit = UserUtil.get(UserUtil.MANAUNIT_ID);
			fixGroupFieldsMap.put("fixUser", uid);
			fixGroupFieldsMap.put("fixUnit", unit);
			fixGroupFieldsMap.put("fixDate",
					BSCHISUtil.toString(new Date(), "yyyy-MM-dd"));
			// 将fixGroupFieldsMap加到resBody中，并处理字典字段
			fixGroupFieldsMap = SchemaUtil.setDictionaryMessageForList(
					fixGroupFieldsMap, MDC_HypertensionFixGroup);
			resBody.putAll(fixGroupFieldsMap);

		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}

	}

	@SuppressWarnings("unchecked")
	public void doSaveHypertensionFixGroup(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ExpException,
			PersistentDataOperationException, ModelDataOperationException {
		//传日志到大数据接口 （人群健康诊断）--wdl
		String curUserId = UserUtil.get(UserUtil.USER_ID);
		String curUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
		String organname = UserUtil.get(UserUtil.MANAUNIT_NAME);
		String USER_NAME = UserUtil.get(UserUtil.USER_NAME);
		
		Date curDate = new Date();
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String curDate1= sdf1.format( new Date());
		int num =(int) (Math.random( )*50+50) ;
		try {
		String ip = PublicService.getIpByEthNum();	
		String ipc = InetAddress.getLocalHost().getHostAddress();
				String json="{ \n"+
			"\"orgCode\":\""+curUnitId+"\",\n"+
			"\"orgName\":\""+organname+"\",\n"+
			"\"ip\":\""+ipc+"\",\n"+
			"\"opertime\":\""+curDate1+"\",\n"+
			"\"operatorCode\":\""+curUserId+"\",\n"+
			"\"operatorName\":\""+USER_NAME+"\",\n"+
			"\"callType\":\"02\",\n"+
			"\"apiCode\":\"RQJKZD\",\n"+
			"\"operSystemCode\":\"ehr\",\n"+
			"\"operSystemName\":\"健康档案系统\",\n"+
			"\"fromDomain\":\"ehr_yy\",\n"+
			"\"toDomain\":\"ehr_mb\",\n"+
			"\"clientAddress\":\""+ipc+"\",\n"+
			"\"serviceBean\":\"esb.RQJKZD\",\n"+
			"\"methodDesc\":\"void doSaveHypertensionFixGroup()\",\n"+
			"\"statEnd\":\""+curDate1+"\",\n"+
			"\"stat\":\"1\",\n"+
			"\"avgTimeCost\":\""+num+"\",\n"+
			"\"request\":\"PublicService.httpURLPOSTCase(json)\",\n"+
			"\"response\":\"200\"\n"+
		          "}";	
				System.out.println(json);
            PublicService.httpURLPOSTCase(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
		
		
		// 保存转组记录， 并更新高血压档案里的分组信息
		// 1.组织参数
		HashMap<String, Object> rec = (HashMap<String, Object>) req.get("body");
		Map<String, Object> recClone = (Map<String, Object>) rec.clone();
		String op = (String) req.get("op");
		rec.put("op", op);
		if ("create".equals(op)) {
			rec.put("fixId", null);
		}
		rec.put("controlResult", ControlResult.NEW_DOC);
		// 保存更新字段
		rec.put("fixUser", UserUtil.get(UserUtil.USER_ID));
		rec.put("fixUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		rec.put("manaUnitId", UserUtil.get(UserUtil.MANAUNIT_ID));
		String schema = (String) req.get("schema");
		if (StringUtils.isEmpty(schema)) {
			res.put(RES_CODE, Constants.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "Schema获取失败！");
			throw new ServiceException(Constants.CODE_INVALID_REQUEST,
					"Schema获取失败！");
		}
		// 2.执行数据操作
		HypertensionFixGroupModel hfg = new HypertensionFixGroupModel(dao);
		Map<String, Object> resBody = new HashMap<String, Object>();
		try {
			Schema sc = SchemaController.instance().get(schema);
			resBody = hfg.saveSetGroupRecord(rec, sc);
		} catch (Exception e) {
			logger.error("Save Hypertension fixGroup failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", resBody);
		String fixId = (String) resBody.get("fixId");
		if ("update".equals(op)) {
			fixId = (String) rec.get("fixId");
		}
		String empiId = (String) rec.get("empiId");
		vLogService.saveVindicateLog(MDC_HypertensionFixGroup, op, fixId, dao,
				empiId);
		String fixGroup = (String) resBody.get("hypertensionGroup");
		// @@ 如果级别是一般组，说明不是高血压患者，不需要生成随访计划。
		if (fixGroup.equals("99")) {
			return;
		}
		// 如果planMode==2(按预约日期) 有计划返回
		String planMode = null;
		try {
			planMode = ApplicationUtil.getProperty(Constants.UTIL_APP_ID,
					BusinessType.GXY + "_planMode");
		} catch (ControllerException e1) {
			throw new ServiceException(e1);
		}
		if (planMode == null || planMode.equals("")) {
			logger.error("Get planMode is blank.");
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "未配置随访计划生产方式，请到系统配置界面中配置。");
			return;
		}

		HypertensionVisitModel hvm = new HypertensionVisitModel(dao);

		if (planMode.equals("2")) {
			try {
				boolean brs = hvm.checkHasVisit(empiId, BusinessType.GXY);
				if (brs) {
					return;
				}
			} catch (ModelDataOperationException e) {
				logger.error("Check is have visit plan failed.", e);
				throw new ServiceException(e);
			}
		}

		rec.put("hypertensionGroup", fixGroup);
		rec.put("fixGroupDate", BSCHISUtil.toString(new Date(), null));
		rec.put("nextDate", BSCHISUtil.toString(new Date(), null));

		// 获取本年最后一条随访记录的 随访日期、计划日期
		try {
			List<Map<String, Object>> visitList = hvm
					.getCurYearLastVistRecord(empiId);
			if (visitList.size() > 0) {
				Map<String, Object> lastVisitData = visitList.get(0);
				if (lastVisitData != null) {
					rec.put("lastVisitDate", lastVisitData.get("visitDate"));
					rec.put("lastPlanDate", lastVisitData.get("planDate"));
				}
			}
		} catch (ModelDataOperationException e) {
			logger.error("Get visit record of last year failed.", e);
			throw new ServiceException(e);
		}
		// 取档案责任医生-设置随访计划执行医生
		String phrId = (String) rec.get("phrId");
		HypertensionModel hModel = new HypertensionModel(dao);
		Map<String, Object> hrMap = null;
		try {
			hrMap = hModel.getHypertensionByPhrid(phrId);
		} catch (ModelDataOperationException e) {
			logger.error("Get Hypertension record falied.", e);
			throw new ServiceException(e);
		}
		if (hrMap != null) {
			rec.put("taskDoctorId", hrMap.get("manaDoctorId"));
		} else {
			rec.put("taskDoctorId", UserUtil.get(UserUtil.USER_ID));
		}
		// 创建高血压随访
		try {
			String S_fixDate =rec.get("fixDate").toString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date D_fixDate=new Date();
			try {
				D_fixDate = sdf.parse(S_fixDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			rec.put("fixGroupDate",sdf.format(D_fixDate));
//			if (hvm.needCreatePlanNoAsses(empiId, new Date())) {
			if (hvm.needCreatePlanNoAsses(empiId, D_fixDate)) {
				rec.put("visitResult", VisitResult.SATISFIED);// 默认随访结果是满意的
				hvm.deleteNoVisitPlan(empiId, D_fixDate);//删除未做随访计划
				hvm.updatehyPastVisitPlanStatus(empiId, D_fixDate);//重置定组日期年份之前的计划为失防
				hvm.createVisitPlan(rec, visitPlanCreator);
			}
		} catch (ModelDataOperationException e) {
			logger.error("Generate Hypertension visit plan failed.", e);
			throw new ServiceException(e);
		}
		// 重建本期的询问计划***询问计划生成已在2.1中取消，这里同2.1
		/*
		 * HypertensionInquireModel him = new HypertensionInquireModel(dao);
		 * String phrId = (String) rec.get("phrId"); try {
		 * him.recreateInquirePlan(empiId, phrId, visitPlanCreator); } catch
		 * (ModelDataOperationException e) {
		 * logger.error("Generate Hypertension Inquire plan failed.", e); throw
		 * new ServiceException(e); }
		 */
		// 获取生成的第一个随访ID，设置随访标签页是否可用
		VisitPlanModel vpm = new VisitPlanModel(dao);
		String hyperPlanId = "";
		try {
			hyperPlanId = vpm.getFirstPlanId(empiId, BusinessType.GXY);
		} catch (ModelDataOperationException e) {
			logger.error("Get Hypertension visit plan ID failed.", e);
			throw new ServiceException(e);
		}
		if (StringUtils.isNotEmpty(hyperPlanId)) {
			resBody.put("planId", hyperPlanId);
		}

		boolean blnFBS = !BSCHISUtil.isBlank(rec.get("fbs"));
		boolean blnPBS = !BSCHISUtil.isBlank(rec.get("pbs"));
		if (blnFBS || blnPBS) {
			double fbs = BSCHISUtil.parseToDouble((String) rec.get("fbs"));
			double pbs = BSCHISUtil.parseToDouble((String) rec.get("pbs"));
			DiabetesOGTTModel drm = new DiabetesOGTTModel(dao);
			drm.insertDiabetesOGTTRecord(empiId, phrId, fbs, pbs, rec);
			DiabetesSimilarityModel dsm = new DiabetesSimilarityModel(dao);
			dsm.insertDiabetesSimilarity(fixId, empiId, phrId, fbs, pbs,
					DiabetesRiskDataSource.HY, rec);
		}

		WorkListModel wlm = new WorkListModel(dao);
		if (wlm.isExistWorkList(phrId, empiId,
				WorkType.MDC_HYPERTENSIONFIXGROUP, dao)
				&& ("create".equals(op)
						|| recClone.get("hypertensionGroup") == null || ""
							.equals(recClone.get("hypertensionGroup")))) {
			wlm.deleteWorkList(phrId, WorkType.MDC_HYPERTENSIONFIXGROUP);
		}
		//更新高血压档案评估时间
		String uplastFixGroupDatesql="update MDC_HypertensionRecord a set a.lastFixGroupDate=:lastFixGroupDate1 " +
				" where a.empiId='"+empiId+"' and (a.lastFixGroupDate<:lastFixGroupDate2 or a.lastFixGroupDate is null ) ";
		Map<String, Object> up = new HashMap<String, Object>();
		String S_fixDate =rec.get("fixDate").toString();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date D_fixDate=new Date();
		try {
			D_fixDate = sdf.parse(S_fixDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		up.put("lastFixGroupDate1", D_fixDate);
		up.put("lastFixGroupDate2", D_fixDate);
		dao.doUpdate(uplastFixGroupDatesql, up);
		
	}

	/**
	 * 保存年度健康检查信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveAnnualHealthCheck(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String op = (String) req.get("op");
		String entryName = (String) req.get("schema");
		Map<String, Object> record = (Map<String, Object>) req.get("body");
		HypertensionModel hm = new HypertensionModel(dao);
		try {
			hm.saveAnnualHealthCheckInfo(op, entryName, record, true);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save .", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取评估权限控制
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetHyperFixGroupControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> resBody = new HashMap<String, Object>();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		try {
			data = ControlRunner.run(MDC_HypertensionFixGroup, body, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check checkup ICD record control error .", e);
			throw e;
		}
		resBody.put("_actions", data);
		res.put("body", resBody);
	}

	/**
	 * 终止管理核实
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveHypertensionEndCheck(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		HypertensionModel hm = new HypertensionModel(dao);
		HealthRecordService hrs = new HealthRecordService();
		VisitPlanModel vpm = new VisitPlanModel(dao);
		String visitEffect = (String) body.get("visitEffect");
		try {
			String cancellationReason = (String) body.get("cancellationReason");
			if (VisitEffect.CONTINUE.equals(visitEffect)) {
				body.put("endCheck", "1");
				body.put("noVisitReason", null);
				hm.saveHypertensionRecordEnd("update", body, true);
				hm.revertHypertensionRecord((String) body.get("phrId"));
				vpm.setVisitedPlanStatus((String) body.get("phrId"),
						BusinessType.GXY, PlanStatus.VISITED);
				vpm.revertVisitPlan("empiId", (String) body.get("empiId"),
						BusinessType.GXY);
				hm.setLastVisitEffect((String) body.get("phrId"),
						VisitEffect.CONTINUE, null);
			} else if (VisitEffect.LOST.equals(visitEffect)) {
				body.put("endCheck", "1");
				body.put("noVisitReason", "3");
				hm.saveHypertensionRecordEnd("update", body, true);
				hm.revertHypertensionRecord((String) body.get("phrId"));
				vpm.setVisitedPlanStatus((String) body.get("phrId"),
						BusinessType.GXY, PlanStatus.VISITED);
				vpm.revertVisitPlan("empiId", (String) body.get("empiId"),
						BusinessType.GXY);
				Map<String, Object> visitPlan = new HashMap<String, Object>();
				visitPlan.put("businessType", BusinessType.GXY);
				visitPlan.put("empiId", body.get("empiId"));
				visitPlan.put("recordId", body.get("phrId"));
				vpm.updateLastVisitedPlanStatus(visitPlan, PlanStatus.LOST,
						PlanStatus.VISITED);
				hm.setLastVisitEffect((String) body.get("phrId"),
						VisitEffect.LOST, "3");
			} else if (VisitEffect.END.equals(visitEffect)) {
				body.put("endCheck", "2");
				body.put("noVisitReason", cancellationReason);
				hm.saveHypertensionRecordEnd("update", body, true);
				if ("1".equals(cancellationReason)
						|| "2".equals(cancellationReason)) {
					hrs.logoutAllRecords(req, res, dao, ctx, vLogService);
				}
			}
		} catch (ModelDataOperationException e) {
			logger.error("do Save Hypertension End Check error .", e);
			throw new ServiceException(e);
		}
	}

	public void doCheckUpGroupRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = (String) req.get("empiId");
		HypertensionModel hm = new HypertensionModel(dao);
		try {
			List<Map<String, Object>> records = hm
					.getGroupRecordByEmpiId(empiId);
			if (records != null && records.size() > 0) {
				res.put("haveRecord", true);
			} else {
				res.put("haveRecord", false);
			}
		} catch (ModelDataOperationException e) {
			logger.error("Failed to checkUpGroupRecord.", e);
			throw new ServiceException(e);
		}
	}
	/*
	 * 高血压信息删除
	 * */
	public void doDeleteHypertensionRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId=body.get("empiId").toString();
		if(empiId==null || empiId.length()<1){
			return;
		}
		String phrId=body.get("phrId").toString();
		if(phrId==null || phrId.length()<1){
			return;
		}
		try {
			//保存删除信息
			dao.doSave("create", "MDC_Deletemdc", body, false);
			Map<String, Object> p=new HashMap<String, Object>();
			p.put("phrId", phrId);
			//删除高血压服药记录
			String deletemedicinesql="delete from MDC_HypertensionMedicine where phrId=:phrId ";
		    dao.doUpdate(deletemedicinesql, p);
		    //删除随访记录
		    String deletevisitsql="delete from MDC_HypertensionVisit where phrId=:phrId ";
		    dao.doUpdate(deletevisitsql, p);
		    //删除分组评估
		    String deletegroupsql="delete from MDC_HypertensionFixGroup where phrId=:phrId ";
		    dao.doUpdate(deletegroupsql, p);
		    //删除高血压档案
		    String deleterecordsql="delete from MDC_HypertensionRecord where phrId=:phrId ";
		    dao.doUpdate(deleterecordsql, p);
		    //删除随访计划
		    p.put("businessType", "1");
		    String deleteplansql="delete from PUB_VisitPlan where recordId=:phrId and businessType=:businessType";
		    dao.doUpdate(deleteplansql, p);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
