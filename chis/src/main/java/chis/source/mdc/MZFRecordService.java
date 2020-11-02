/**
 * @(#)MZFService.java Created on 2012-1-18 上午9:57:37
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.conf.SystemCofigManageModel;
import chis.source.control.ControlRunner;
import chis.source.dic.BusinessType;
import chis.source.dic.ControlResult;
import chis.source.dic.FixType;
import chis.source.dic.OperType;
import chis.source.dic.PastHistoryCode;
import chis.source.dic.RecordType;
import chis.source.dic.VisitResult;
import chis.source.phr.HealthRecordModel;
import chis.source.phr.PastHistoryModel;
import chis.source.print.instance.PhysicalExaminationFile;
import chis.source.pub.PublicModel;
import chis.source.pub.PublicService;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import chis.source.visitplan.VisitPlanCreator;
import chis.source.visitplan.VisitPlanModel;
import chis.source.worklist.WorkListModel;
import ctd.dictionary.Dictionary;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class MZFRecordService extends MZFService {
	private static final Logger logger = LoggerFactory
			.getLogger(MZFRecordService.class);

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

	/**
	 * 分布 查询 慢阻肺 档案 记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doListMZFRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MZFRecordModel drm = new MZFRecordModel(dao);
		Map<String, Object> resultMap = null;
		try {
			resultMap = drm.pageQueryList(req);
		} catch (ModelDataOperationException e) {
			logger.error("Get Daibetes record failed.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "查询慢阻肺档案信息失败！");
			throw new ServiceException(e);
		}
		// 取出记录中empiId的值放到empiIdList中
		List<Map<String, Object>> resBody = (List<Map<String, Object>>) resultMap
				.get("body");
		res.put("body", resBody);
		res.put("pageSize", resultMap.get("pageSize"));
		res.put("pageNo", resultMap.get("pageNo"));
		res.put("totalCount", resultMap.get("totalCount"));
	}

	/*
	 * 慢阻肺档案保存
	 * 
	 * @param req
	 * 
	 * @param res
	 * 
	 * @param dao
	 * 
	 * @param ctx
	 * 
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMZFRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException, ModelDataOperationException {
		//传日志到大数据接口 （社区档案管理）--wdl
		String curUserId = UserUtil.get(UserUtil.USER_ID);
		String curUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
		String organname = UserUtil.get(UserUtil.MANAUNIT_NAME);
		String USER_NAME = UserUtil.get(UserUtil.USER_NAME);
		
		Date curDate = new Date();
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String curDate1= sdf.format( new Date());
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
			"\"methodDesc\":\"void doSaveMZFRecord()\",\n"+
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
		if (null == op) {
			op = "create";
		}
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");

		MZFRecordModel drm = new MZFRecordModel(dao);
	
		Map<String, Object> m = null;
		try {
			// 保存慢阻肺档案
			m = drm.saveMZFRecord(op, reqBody, true);
			res.put("body", m);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save diabetes record.", e);
			throw new ServiceException(e);
		}
		res.put(RES_CODE, Constants.CODE_OK);
		res.put(RES_MESSAGE, "慢阻肺档案保存成功");
	}
	
	private void updatePastHistory2(String empiId, Map<String, Object> record,
			BaseDAO dao, Context ctx) throws PersistentDataOperationException,ModelDataOperationException,
			ServiceException, ControllerException {
		Dictionary dic = DictionaryController.instance().get(
				"chis.dictionary.pastHistory");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", record.get("empiId"));
		parameters.put("pastHisTypeCode", PastHistoryCode.SCREEN);
		parameters.put("diseaseCode", PastHistoryCode.PASTHIS_SCREEN_DIABETES);
		parameters.put("diseaseText", "慢阻肺");
		parameters.put("confirmDate", record.get("diagnosisDate"));
		parameters.put("recordUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("recordUser", UserUtil.get(UserUtil.USER_ID));
		parameters.put("recordDate", new Date());
		parameters.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("lastModifyDate", new Date());
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		Map<String, Object> phRecordMap = hrModel.getPastHistory(empiId,
				PastHistoryCode.SCREEN,
				PastHistoryCode.PASTHIS_SCREEN_DIABETES);
		String phOp = "update";
		if (phRecordMap == null) {
			phOp = "create";
		} else {
			parameters.put("pastHistoryId", phRecordMap.get("pastHistoryId"));
		}
		hrModel.savePastHistory(phOp, parameters, vLogService);
	}
	
	public void doRemoveMZFRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		MZFRecordModel drm = new MZFRecordModel(dao);
		drm.removeMZFRecord(phrId);
	}

	/**
	 * 恢复慢阻肺档案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSetMZFRecordNormal(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		// 恢复慢阻肺档案状态
		MZFRecordModel drm = new MZFRecordModel(dao);
		try {
			drm.SetMZFRecordNormal(phrId, ctx);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to updae MZF_DocumentRecord.", e);
			res.put(Service.RES_CODE, e.getCode());
			res.put(Service.RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
		// 记录慢阻肺恢复日志
		HashMap<String, Object> b = new HashMap<String, Object>();
		b.put("empiId", body.get("empiId"));
		b.put("operType", OperType.REVERT);
		b.put("recordType", RecordType.DIABETESRECORD);
		PublicModel pm = new PublicModel(dao);
		try {
			pm.writeLog(b, ctx);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to write log.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "写入恢复慢阻肺档案操作日志失败。");
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
	public void doGetMZFRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MZFRecordModel drm = new MZFRecordModel(dao);
		String phrId = (String) req.get("phrId");
		String empiId = (String) req.get("empiId");
		long count = 0;
		try {
			count = drm.getCountMZFRecord(phrId, empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Get record number of MZF_DocumentRecord failed.", e);
			throw new ServiceException(e);
		}
		res.put("count", count);
	}

	@SuppressWarnings("unchecked")
	public void doInitializeRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException, ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		if (StringUtils.isEmpty(phrId)) {
			logger.info("Failed to initialize MZFRecord.");
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "慢阻肺加载失败");
			return;
		}
		String empiId = (String) body.get("empiId");

		// 判断慢阻肺是否存在
		Map<String, Object> resMap = (HashMap<String, Object>) dao.doLoad(
				MZF_DocumentRecord, phrId);
		resMap = SchemaUtil.setDictionaryMessageForForm(resMap,
				MZF_DocumentRecord);
		if (null != resMap) {
			resMap.put("op", "update");
		} else {
			resMap = new HashMap<String, Object>();
			resMap.put("op", "create");
			resMap.put("empiId", empiId);

			HealthRecordModel hrm = new HealthRecordModel(dao);
			Map<String, Object> healthMap = hrm.getHealthRecordByEmpiId(empiId);
			if (healthMap != null) {
				String key = (String) healthMap.get("manaDoctorId");
				Dictionary dic = DictionaryController.instance().getDic(
						"chis.dictionary.user01");
				String text = dic.getItem(key).getText();
				Map<String, Object> manaDoctorId = new HashMap<String, Object>();
				manaDoctorId.put("key", key);
				manaDoctorId.put("text", text);
				resMap.put("manaDoctorId", manaDoctorId);

				String unitKey = (String) healthMap.get("manaUnitId");
				Dictionary unitDic = DictionaryController.instance().getDic(
						"chis.@manageUnit");
				String unitText = unitDic.getItem(unitKey).getText();
				Map<String, Object> manaUnitId = new HashMap<String, Object>();
				manaUnitId.put("key", unitKey);
				manaUnitId.put("text", unitText);
				resMap.put("manaUnitId", manaUnitId);
			}
			PastHistoryModel phm = new PastHistoryModel(dao);
			boolean hasFamilyMZFPastHistory = phm
					.hasFamilyMZFPastHistory(empiId);
			if (hasFamilyMZFPastHistory) {
				Map<String, Object> familyHistroy = new HashMap<String, Object>();
				familyHistroy.put("key", "1");
				familyHistroy.put("text", "有");
				resMap.put("familyHistroy", familyHistroy);
			}
			String history = phm.getFamilyPastHistoryByEmpiId(empiId);
			resMap.put("history", history);
		}

//		ControlRunner.run(MZF_DocumentRecord, resMap, ctx,
//				(String) resMap.get("op"));
		// 获取个人家族的生活习惯，组装成一个字符串
		res.put("body", resMap);
	}

	@SuppressWarnings("unchecked")
	public void doLogoutMZFRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		String empiId = (String) body.get("empiId");
		String cancellationReason = (String) body.get("cancellationReason");
		String deadReason = (String) body.get("deadReason");

		MZFRecordModel drm = new MZFRecordModel(dao);
		try {
			drm.logoutMZFRecord(phrId, cancellationReason, deadReason);
		} catch (ModelDataOperationException e) {
			logger.error("Logout MZF record failed.", e);
			throw new ServiceException(e);
		}
//		vLogService.saveVindicateLog(MZF_DocumentRecord, "3", phrId, dao,
//				empiId);
//		vLogService.saveRecords("TANG", "logout", dao, empiId);

		res.put("body", body);
	}

	@SuppressWarnings({ "deprecation" })
	public void doGetLastThreeDay(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		Map<String, Object> body = new HashMap<String, Object>();
		try {
			String endMonth = smm.getSystemConfigData("diabetesEndMonth");
			String assessDays = smm.getSystemConfigData("assessDays");
			String assessHour1 = smm.getSystemConfigData("assessHour1");
			String assessHour2 = smm.getSystemConfigData("assessHour2");
			String assessType = smm.getSystemConfigData("assessType");
			String manageType = smm.getSystemConfigData("manageType");
			String planMode = smm.getSystemConfigData(BusinessType.TNB+"_planMode");
			Calendar cDay1 = Calendar.getInstance();
			Date date = new Date();
			date.setMonth(praseMonthInt(endMonth));
			cDay1.setTime(date);
			int lastDay = cDay1.getActualMaximum(Calendar.DAY_OF_MONTH);
			ArrayList<String> list = new ArrayList<String>();
			if (assessDays == null || "".equals(assessDays)) {
				assessDays = "3";
			}
			for (int i = 0; i < BSCHISUtil.parseToInt(assessDays); i++) {
				list.add((lastDay - i) + "");
			}
			body.put("days", list);
			body.put("endMonth", endMonth);
			body.put("assessHour1", assessHour1);
			body.put("assessHour2", assessHour2);
			body.put("assessType", assessType);
			body.put("manageType", manageType);
			body.put("planMode", planMode);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			logger.error("do Get Last Three Day is  failed.", e);
			throw new ServiceException(e);
		}
	}

	private int praseMonthInt(String endMonth) {
		if (endMonth == null || "".equals(endMonth)) {
			return 11;
		}
		int month = 11;
		if (endMonth.startsWith("0")) {
			month = Integer.parseInt(endMonth.substring(1)) - 1;
		} else {
			month = Integer.parseInt(endMonth) - 1;
		}
		return month;
	}
}
