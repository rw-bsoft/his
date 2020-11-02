/**
 * @(#)DiabetesService.java Created on 2012-1-18 上午9:57:37
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
public class DiabetesRecordService extends DiabetesService {
	private static final Logger logger = LoggerFactory
			.getLogger(DiabetesRecordService.class);

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
	 * 分布 查询 糖尿病 档案 记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doListDiabetesRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		DiabetesRecordModel drm = new DiabetesRecordModel(dao);
		Map<String, Object> resultMap = null;
		try {
			resultMap = drm.pageQueryList(req);
		} catch (ModelDataOperationException e) {
			logger.error("Get Daibetes record failed.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "查询糖尿病档案信息失败！");
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
	 * 糖尿病档案保存
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
	public void doSaveDiabetesRecord(Map<String, Object> req,
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
			"\"methodDesc\":\"void doSaveDiabetesRecord()\",\n"+
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

		DiabetesRecordModel drm = new DiabetesRecordModel(dao);
		// 产生定转组是需要的血糖信息
		if (op.equals("create")) {
			reqBody.put("planTypeCode", "-1");
			try {
				drm.updatePastHistory(reqBody);
			} catch (PersistentDataOperationException e) {
				logger.error("Failed to updatePastHistory.", e);
				throw new ServiceException(e);
			} catch (ControllerException e) {
				logger.error("Failed to updatePastHistory.", e);
				throw new ServiceException(e);
			}

			DiabetesRiskModel riskModel = new DiabetesRiskModel(dao);
			riskModel.removeDiabetesRecordWorkList((String) reqBody
					.get("empiId"));
		}else if(op.equals("update")){
			String empiId = (String) reqBody.get("empiId");
			try {
				updatePastHistory2(empiId, reqBody, dao, ctx);
			} catch (PersistentDataOperationException e) {
				logger.error("Failed to updatePastHistory.", e);
				throw new ServiceException(e);
			} catch (ControllerException e) {
				logger.error("Failed to updatePastHistory.", e);
				throw new ServiceException(e);
			}
		}	
		Map<String, Object> m = null;
		try {
			// 保存糖尿病档案
			m = drm.saveDiabetesRecord(op, reqBody, true);
			res.put("body", m);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save diabetes record.", e);
			throw new ServiceException(e);
		}
		res.put(RES_CODE, Constants.CODE_OK);
		res.put(RES_MESSAGE, "糖尿病档案保存成功");
		String empiId = (String) reqBody.get("empiId");
		String phrId = (String) reqBody.get("phrId");
		vLogService
				.saveVindicateLog(MDC_DiabetesRecord, op, phrId, dao, empiId);
		vLogService.saveRecords("TANG", op, dao, empiId);

		// 判断是否有(初次定级)定转组记录
		boolean hasFixGroup = false;
		Map<String, Object> info = null;
		try {
			info = drm.isHasDiabetesFixGroup(phrId, FixType.CREATE);
			hasFixGroup = (Boolean) info.get("hasFixGroup");
		} catch (ModelDataOperationException e) {
			logger.error("Select record number of MDC_DiabetesFixGroup.", e);
			throw new ServiceException(e);
		}

		double fbs = BSCHISUtil.parseToDouble(reqBody.get("fbs") + "");
		double pbs = BSCHISUtil.parseToDouble(reqBody.get("pbs") + "");
		String diabetesType = "";
		if (reqBody.get("diabetesType") != null) {
			diabetesType = (String) reqBody.get("diabetesType");
		}
		String diabetesGroup = "";
		try {
			diabetesGroup = drm
					.getDiabetesGroupByConfig(diabetesType, fbs, pbs);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get DiabetesGroup By Config ", e);
			throw new ServiceException(e);
		}
		if (hasFixGroup) {
			// 更新定转组中的血糖值
			try {
				drm.updateDiatebetesFixGroupGLYX(phrId, fbs, pbs, diabetesGroup);
			} catch (ModelDataOperationException e) {
				logger.error("Update fbs and pbs of MDC_DiabetesFixGroup.", e);
				throw new ServiceException(e);
			}
			if (!diabetesGroup.equals(info.get("diabetesGroup"))) {
				HashMap<String, Object> fixJsonReq = new HashMap<String, Object>();
				fixJsonReq.put("phrId", phrId);
				fixJsonReq.put("fixType", "1");
				fixJsonReq.put("instanceType", BusinessType.TNB);
				fixJsonReq.put("empiId", reqBody.get("empiId"));
				fixJsonReq.put("groupCode", diabetesGroup);
				fixJsonReq.put("oldGroup", info.get("diabetesGroup"));
				fixJsonReq.put("fixGroupDate", new Date());
				fixJsonReq.put("fbs", fbs);
				fixJsonReq.put("nextDate",
						BSCHISUtil.toString(new Date(), null));
				fixJsonReq.put("adverseReactions",
						reqBody.get("adverseReactions"));
				fixJsonReq.put("visitResult", VisitResult.SATISFIED);
				fixJsonReq.put("taskDoctorId", reqBody.get("manaDoctorId"));
				fixJsonReq.put("taskDoctorId", reqBody.get("manaDoctorId"));
				fixJsonReq.put("visitMeddle", "0");
//				try {
////					 生成糖尿病随访计划
//					if (drm.needCreatePlanNoAsses(empiId, new Date())) {
//						drm.createVisitPlan(fixJsonReq, visitPlanCreator, ctx);
//					}
//				} catch (ModelDataOperationException e) {
//					logger.error("Failed to create diabetes visit plan.", e);
//					throw new ServiceException(e);
//				}
			}
		} else {
			// $$ 自动生成定转组信息，并生成随访计划
			HashMap<String, Object> fixJsonReq = new HashMap<String, Object>();
			HashMap<String, Object> fixreqBody = new HashMap<String, Object>();
			fixreqBody.put("phrId", reqBody.get("phrId"));
			fixreqBody.put("empiId", reqBody.get("empiId"));
			fixreqBody.put("fixDate", reqBody.get("createDate"));
			fixreqBody.put("fixType", "1");
			fixreqBody.put("fbs", fbs);
			fixreqBody.put("pbs", pbs);
			fixJsonReq.put("oldGroup", diabetesGroup);
			fixreqBody.put("diabetesGroup", diabetesGroup);
			fixreqBody.put("taskDoctorId", reqBody.get("manaDoctorId"));
			fixreqBody.put("controlResult", ControlResult.NEW_DOC);
			fixJsonReq.put("body", fixreqBody);
			fixJsonReq.put("schema", MDC_DiabetesFixGroup);
			fixJsonReq.put("op", "create");
			//
			this.doSaveDiabetesFixGroup(fixJsonReq, res, dao, ctx);
		}

		if (op.equals("create")) {
			// 更新健康档案是否糖尿病字段
			try {
				drm.setHealthRecordIsDiabetes(phrId);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to update EHR_HealthRecord isDiateses "
						+ "column.", e);
				throw new ServiceException(e);
			}
		}
		if (op.equals("create")) {
			// 更新工作列表
			WorkListModel wlm = new WorkListModel(dao);
			m = new HashMap<String, Object>();
			m.put("empiId", empiId);
			m.put("recordId", phrId);
			m.put("workType", "18");
			try {
				wlm.deleteWorkList(m);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to update PUB_WorkList " + "column.", e);
				throw new ServiceException(e);
			}
		}

		// 判断是否存在已随访的随访计划
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
				empiId);
		List<Map<String, Object>> visitList = dao.doList(cnd, "",
				MDC_DiabetesVisit);
		if (visitList != null && visitList.size() > 0) {
			res.put("hasVisit", true);
		} else {
			res.put("hasVisit", false);
		}
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
		parameters.put("diseaseText", "糖尿病");
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

	/**
	 * 恢复糖尿病档案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSetDiabetesRecordNormal(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		// 恢复糖尿病档案状态
		DiabetesRecordModel drm = new DiabetesRecordModel(dao);
		try {
			drm.SetDiabetesRecordNormal(phrId, ctx);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to updae MDC_DiabetesRecord.", e);
			res.put(Service.RES_CODE, e.getCode());
			res.put(Service.RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
		// @@ 查询档案的定转组信息,如有定转组信息则跟新随访计划
		boolean isHasFixGroup = false;
		try {
			isHasFixGroup = (Boolean) drm.isHasDiabetesFixGroup(phrId, null)
					.get("hasFixGroup");
		} catch (ModelDataOperationException e) {
			logger.error("Select record number of MDC_DiabetesFixGroup.", e);
			res.put(Service.RES_CODE, e.getCode());
			res.put(Service.RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
		if (isHasFixGroup) {
			// 判断是否有未完成的随访计划
			List<Map<String, Object>> rsList = null;
			try {
				rsList = drm.getUnfinishedVisitPlan(phrId);
			} catch (ModelDataOperationException e) {
				logger.error("Judge diabetes record is has visit plan.", e);
				res.put(Service.RES_CODE, e.getCode());
				res.put(Service.RES_MESSAGE, e.getMessage());
				throw new ServiceException(e);
			}
			if (rsList.size() > 0) {
				// 更新随访计划
				try {
					drm.setVisitPlanNormal(phrId);
				} catch (ModelDataOperationException e) {
					logger.error("Update diabetes visit plan with recordId="
							+ phrId, e);
					res.put(Service.RES_CODE, e.getCode());
					res.put(Service.RES_MESSAGE, e.getMessage());
					throw new ServiceException(e);
				}
			} else {
				// 生成随访计划
				HashMap<String, Object> reqBody = new HashMap<String, Object>();
				Map<String, Object> m = (Map<String, Object>) rsList.get(0);
				reqBody.put("phrId", phrId);
				reqBody.put("fixType", m.get("FIXTYPE"));
				reqBody.put("instanceType", BusinessType.TNB);
				reqBody.put("empiId", body.get("empiId"));
				reqBody.put("groupCode", body.get("diabetesGroup"));
				reqBody.put("fixGroupDate",
						new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
				reqBody.put("visitMeddle", "0");
				try {
					if (drm.needCreatePlanNoAsses((String) body.get("empiId"),
							new Date())) {
						drm.createVisitPlan(reqBody, visitPlanCreator, ctx);
					}
				} catch (ModelDataOperationException e) {
					logger.error("Failed to create diabetes visit plan.", e);
					res.put(RES_CODE, e.getCode());
					res.put(RES_MESSAGE, e.getMessage());
					throw new ServiceException(e);
				}
				logger.debug("生成随访计划");
			}
		}
		// 记录糖尿病恢复日志
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
			res.put(RES_MESSAGE, "写入恢复糖尿病档案操作日志失败。");
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存糖尿病服药情况
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMedicine(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		DiabetesRecordModel drm = new DiabetesRecordModel(dao);
		String recordId = (String) body.get("recordId");
		Map<String, Object> m = null;
		try {
			// 保存糖尿病服药情况
			m = (Map<String, Object>) drm.saveDiabetesMedicine(op, body, true);
			m.putAll(SchemaUtil.setDictionaryMessageForList(body,
					MDC_DiabetesMedicine));
			if ("create".equals(op)) {
				recordId = (String) m.get("recordId");
			}
			vLogService.saveVindicateLog(MDC_DiabetesMedicine, op, recordId,
					dao);
			res.put("body", m);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to insert medicine for diabertes.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 删除药品
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveMedine(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException, ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String recordId = (String) body.get("pkey");
		DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
		dvm.removeMedicine(recordId);
		vLogService.saveVindicateLog(MDC_DiabetesMedicine, "4", recordId, dao);


		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBody.get("empiId");
		//标识签约任务完成
		this.finishSCServiceTask(empiId,TNBDAGL_TNBFW,null,dao);
	}

	/**
	 * 糖尿病定转组信息保存
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected synchronized void doSaveDiabetesFixGroup(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		// 页面取的组别
		String diabetesGroup = (String) body.get("diabetesGroup");
		// 定转组类型 1初次定组 2 维持原组不变 3 定期转组 4 不定期转组
		String phrId = (String) body.get("phrId");
		// body.put("phrId", phrId);
		String op = (String) req.get("op");
		// 根据血糖控制目标，确定糖尿病分组。血糖水平为“较差”者参照一组管理；血糖水平为“理想”和“一般”者参照二组管理；糖耐量减低IGT和和空腹血糖损害IFG的患者纳入三组管理。
		// 字典表没有 暂时直接用标准代替组别

		// 保存定转组信息
		DiabetesRecordModel drm = new DiabetesRecordModel(dao);
		try {
			// 保存糖尿病定级信息
			drm.saveDiabetesFixGroup(op, body, true);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save diabetes fix group record.", e);
			throw new ServiceException(e);
		}
		// 现在定转组已经没有保存功能 所以这个方法不需要再执行 代码保留
		// // 更新糖尿病档案 糖尿病组别字段
		// HashMap<String, Object> rsres = new HashMap<String, Object>();
		// try {
		// drm.updateDiabetesRecordDiabetesGroup(req, rsres);
		// } catch (ModelDataOperationException e) {
		// logger.error("Failed to update fix group of diabetes record.", e);
		// res.put(Service.RES_CODE, e.getCode());
		// res.put(Service.RES_MESSAGE, e.getMessage());
		// throw new ServiceException(e);
		// }

		// 新生成的组别与页面获得的组别不一样的时候生成新随访计划
//		if ("create".equals(op)) {
			HashMap<String, Object> reqBody = new HashMap<String, Object>();
			reqBody.put("phrId", phrId);
			reqBody.put("fixType", body.get("fixType"));
			reqBody.put("instanceType", BusinessType.TNB);
			reqBody.put("empiId", body.get("empiId"));
			reqBody.put("groupCode", diabetesGroup);
			reqBody.put("fixGroupDate",
					BSCHISUtil.toDate((String) (body.get("fixDate"))));
			reqBody.put("fbs", Double.parseDouble(body.get("fbs").toString()));
			reqBody.put("nextDate", BSCHISUtil.toString(new Date(), null));
			reqBody.put("adverseReactions", body.get("adverseReactions"));
			reqBody.put("visitResult", VisitResult.SATISFIED);
			reqBody.put("taskDoctorId", body.get("taskDoctorId"));
			reqBody.put("visitMeddle", "0");
			try {
				// 生成糖尿病随访计划
				String tempempiId=(String) body.get("empiId");
				Date tempfixdate=BSCHISUtil.toDate((String) (body.get("fixDate")));
				if (drm.needCreatePlanNoAsses(tempempiId, tempfixdate)) {
					//先删除计划
					drm.deleteNoVisitPlan(tempempiId,tempfixdate);
					//重置定组日期年份之前的计划为失防
					drm.updatediaPastVisitPlanStatus(tempempiId,tempfixdate);
					//生成随访
					drm.createVisitPlan(reqBody, visitPlanCreator, ctx);
					
				}
			} catch (ModelDataOperationException e) {
				logger.error("Failed to create diabetes visit plan.", e);
				throw new ServiceException(e);
			}
			//更新糖尿病档案评估时间
			String uplastFixGroupDatesql="update MDC_DiabetesRecord a set a.lastFixGroupDate=:lastFixGroupDate1 " +
					" where a.empiId='"+body.get("empiId").toString()+"' and (a.lastFixGroupDate<:lastFixGroupDate2 or a.lastFixGroupDate is null ) ";
			Map<String, Object> up = new HashMap<String, Object>();
			String S_fixDate =body.get("fixDate").toString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date D_fixDate=new Date();
			try {
				D_fixDate = sdf.parse(S_fixDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			up.put("lastFixGroupDate1", D_fixDate);
			up.put("lastFixGroupDate2", D_fixDate);
			
			try {
				dao.doUpdate(uplastFixGroupDatesql, up);
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
//	}

	/**
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetDiabetesRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		DiabetesRecordModel drm = new DiabetesRecordModel(dao);
		String phrId = (String) req.get("phrId");
		String empiId = (String) req.get("empiId");
		long count = 0;
		try {
			count = drm.getCountDiabetesRecord(phrId, empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Get record number of MDC_DiabetesRecord failed.", e);
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
			logger.info("Failed to initialize DiabetesRecord.");
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "糖尿病加载失败");
			return;
		}
		String empiId = (String) body.get("empiId");

		// 判断糖尿病是否存在
		Map<String, Object> resMap = (HashMap<String, Object>) dao.doLoad(
				MDC_DiabetesRecord, phrId);
		resMap = SchemaUtil.setDictionaryMessageForForm(resMap,
				MDC_DiabetesRecord);
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
			boolean hasFamilyDiabetesPastHistory = phm
					.hasFamilyDiabetesPastHistory(empiId);
			if (hasFamilyDiabetesPastHistory) {
				Map<String, Object> familyHistroy = new HashMap<String, Object>();
				familyHistroy.put("key", "1");
				familyHistroy.put("text", "有");
				resMap.put("familyHistroy", familyHistroy);
			}
		}

		ControlRunner.run(MDC_DiabetesRecord, resMap, ctx,
				(String) resMap.get("op"));
		// 获取个人家族的生活习惯，组装成一个字符串
		PastHistoryModel phm = new PastHistoryModel(dao);
		String history = phm.getFamilyPastHistoryByEmpiId(empiId);
		resMap.put("history", history);
		res.put("body", resMap);
	}

	@SuppressWarnings("unchecked")
	public void doGetRecordMedicine(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		//
		DiabetesRecordModel drm = new DiabetesRecordModel(dao);
		try {
			List<Map<String, Object>> list = drm.getRecordMedicine(phrId);
			res.put("body", list);
		} catch (ModelDataOperationException e) {
			logger.error("doGetRecordMedicine is [n] failed.", e);
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doInitializeFixGroup(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> resBody = new HashMap<String, Object>();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> resMap = null;

		String empiId = (String) body.get("empiId");

		DiabetesRecordModel drm = new DiabetesRecordModel(dao);
		List<Map<String, Object>> list = drm.getFixGroupList(empiId);
		resBody.put(MDC_DiabetesFixGroup + Constants.DATAFORMAT4LIST, list);
		if (list.size() > 0) {
			resMap = list.get(0);
		}
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "fixId", "s", resMap.get("fixId"));
		Map<String, Object> diafix =dao.doLoad(cnd, MDC_DiabetesFixGroup);
		diafix.putAll(SchemaUtil.setDictionaryMessageForForm(diafix,
				MDC_DiabetesFixGroup));
		resBody.put(MDC_DiabetesFixGroup + Constants.DATAFORMAT4FORM, diafix);
		res.put("body", resBody);
	}

	@SuppressWarnings("unchecked")
	public void doLogoutDiabetesRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		String empiId = (String) body.get("empiId");
		String cancellationReason = (String) body.get("cancellationReason");
		String deadReason = (String) body.get("deadReason");

		DiabetesRecordModel drm = new DiabetesRecordModel(dao);
		try {
			drm.logoutDiabetesRecord(phrId, cancellationReason, deadReason);
		} catch (ModelDataOperationException e) {
			logger.error("Logout hypertension record failed.", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MDC_DiabetesRecord, "3", phrId, dao,
				empiId);
		vLogService.saveRecords("TANG", "logout", dao, empiId);

		// **注销未执行过的随访计划
		VisitPlanModel vpModel = new VisitPlanModel(dao);
		try {
			String[] businessTypes = { BusinessType.TNB };
			vpModel.logOutVisitPlan(vpModel.RECORDID, phrId, businessTypes);
		} catch (ModelDataOperationException e) {
			logger.error("Logout diabetses visit plan failed.", e);
			throw new ServiceException(e);
		}
		// 修改健康档案中是否为糖尿病病人字段值为 “否”
		HealthRecordModel hrm = new HealthRecordModel(dao);
		try {
			hrm.clearChronicDiseaseFlag(phrId, "isDiabetes");
		} catch (ModelDataOperationException e) {
			logger.error("Update health record isDiabetes is [n] failed.", e);
			throw new ServiceException(e);
		}
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

	public void doSaveDiabetesYearAssess(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		DiabetesRecordModel drm = new DiabetesRecordModel(dao);
		try {
			drm.saveDiabetesYearAssess(visitPlanCreator, ctx);
		} catch (ModelDataOperationException e) {
			logger.error("save Diabetes Year Assess failed.", e);
			throw new ServiceException(e);
		}
	}
}
