/**
 * @(#)HypertensionVisitService.java Created on 2012-3-7 下午2:05:39
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.StyledEditorKit.BoldAction;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.control.ControlRunner;
import chis.source.dic.BusinessType;
import chis.source.dic.CancellationReason;
import chis.source.dic.PlanMode;
import chis.source.dic.PlanStatus;
import chis.source.dic.VisitEffect;
import chis.source.dic.VisitResult;
import chis.source.dic.WorkType;
import chis.source.dic.YesNo;
import chis.source.empi.EmpiModel;
import chis.source.pub.PublicModel;
import chis.source.pub.PublicService;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import chis.source.visitplan.VisitPlanCreator;
import chis.source.visitplan.VisitPlanModel;
import chis.source.worklist.WorkListModel;

import com.alibaba.fastjson.JSONException;

import ctd.account.UserRoleToken;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class HypertensionVisitService extends MDCService {
	private static final Logger logger = LoggerFactory
			.getLogger(HypertensionService.class);
	private VisitPlanCreator visitPlanCreator;

	//获得visitPlanCreator
	public VisitPlanCreator getVisitPlanCreator() {
		return visitPlanCreator;
	}

	//设置visitPlanCreator
	public void setVisitPlanCreator(VisitPlanCreator visitPlanCreator) {
		this.visitPlanCreator = visitPlanCreator;
	}

	//加载随访初始数据
	@SuppressWarnings("unchecked")
	public void doVisitInitialize(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		// 取最小间隔，下一次随访计划编号以及下一次提醒时间。
		doGetMinStep(req, res, dao, ctx);

		Map<String, Object> resBody = new HashMap<String, Object>();
		Map<String, Object> gmsBody = (Map<String, Object>) res.get("body");
		if (gmsBody != null) {
			resBody.put("minStepInfo", gmsBody);
		}
		res.remove("body");

		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String empiId = (String) reqBody.get("empiId");
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		// 取身高以及危险分层，管理组别。
		String groupSchema = (String) reqBody.get("fixGroupSchema");
		HypertensionFixGroupModel hfgm = new HypertensionFixGroupModel(dao);
		List<Map<String, Object>> sBody = null;
		try {
			// 获取管理级别
			sBody = hfgm.getfixGroupByCnd(cnd, "fixId desc", groupSchema);
			sBody = SchemaUtil.setDictionaryMessageForList(sBody, groupSchema);
		} catch (ModelDataOperationException e) {
			logger.error("Get Hypertension supervisory level failed.", e);
			throw new ServiceException(e);
		}
		Map<String, Object> fixGroup = (Map<String, Object>) sBody.get(0);
		if (null != fixGroup.get("riskiness")
				&& ((String) fixGroup.get("riskiness")).length() == 0) {
			fixGroup.put("riskiness", "0");
			fixGroup.put("riskiness_text", "无");
		}
		if (null != fixGroup.get("complication")
				&& ((String) fixGroup.get("complication")).length() == 0) {
			fixGroup.put("complication", "0");
			fixGroup.put("complication_text", "无");
		}
		if (null != fixGroup.get("targetHurt")
				&& ((String) fixGroup.get("targetHurt")).length() == 0) {
			fixGroup.put("targetHurt", "0");
			fixGroup.put("targetHurt_text", "无");
		}
		resBody.put("fixGroup", fixGroup);

		// 获取年龄和性别。
		res.remove("body");
		req.remove("order");
		String occurDate = (String) reqBody.get("occurDate");
		EmpiModel em = new EmpiModel(dao);
		HashMap<String, Object> asBody = null;
		try {
			asBody = (HashMap<String, Object>) em.getAgeAndSex(empiId,
					occurDate);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get age and sex of person. ", e);
			throw new ServiceException(e);
		}
		if (asBody != null) {
			resBody.put("age", asBody.get("age"));
			resBody.put("sex", asBody.get("sexCode"));
		}

		res.remove("body");

		// 获取生活习惯数据。
		String orderBy = StringUtils
				.trimToNull((String) reqBody.get("orderBy"));
		MDCBaseModel md = new MDCBaseModel(dao);
		List<Map<String, Object>> lifeStyleList = null;
		try {
			lifeStyleList = md.getLifeStyle(cnd, orderBy);
		} catch (ModelDataOperationException e) {
			logger.error("Get person life sytle failed.", e);
			throw new ServiceException(e);
		}
		if (lifeStyleList.size() > 0) {
			lifeStyleList = SchemaUtil.setDictionaryMessageForList(
					lifeStyleList, EHR_LifeStyle);
			resBody.put("lifeStyle", lifeStyleList.get(0));
		}

		// 获取前一次随访的信息。
		String planId = (String) reqBody.get("planId");
		VisitPlanModel vpm = new VisitPlanModel(dao);
		Map<String, Object> lvBody = null;
		try {
			String visitId = vpm.getLastVisitId(empiId, BusinessType.GXY,
					planId);
			if (StringUtils.isNotEmpty(visitId)) {
				HypertensionVisitModel hvm = new HypertensionVisitModel(dao);
				lvBody = hvm.getHypertensionVisitByPkey(visitId);
			}
		} catch (ModelDataOperationException e) {
			logger.error("Get last visit info failed.", e);
			throw new ServiceException(e);
		}
		if (lvBody != null) {
			resBody.put("lastVisit", lvBody);
		}
		// 判断是否需要年度评估提醒
		try {
			resBody.put(
					"groupAlarm",
					alarmAndIfLast(
							reqBody,
							BSCHISUtil.toDate((String) reqBody.get("planDate")),
							BusinessType.GXY, dao));

			// 获得planMode
			Application app = ApplicationUtil
					.getApplication(Constants.UTIL_APP_ID);
			String planMode = (String) app.getProperty(BusinessType.GXY
					+ "_planMode");
			String precedeDays = (String) app.getProperty(BusinessType.GXY
					+ "_precedeDays");
			String delayDays = (String) app.getProperty(BusinessType.GXY
					+ "_delayDays");
			resBody.put("planMode", planMode);// 计划生成方式（随访结果或预约时间）
			resBody.put("precedeDays", precedeDays);// 随访提前天数
			resBody.put("delayDays", delayDays);// 随访延迟天数
			res.put("body", resBody);
		} catch (Exception e) {
			logger.error("Check year group alarm failed.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "判断是否需要年度评估提醒失败。");
			throw new ServiceException(e);
		}

	}

	//获取随访信息
	@SuppressWarnings("unchecked")
	public void doGetVisitInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		
		
		// @@ 取随访信息。
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");

		String pkey = (String) reqBody.get("pkey");
		HypertensionVisitModel hvm = new HypertensionVisitModel(dao);
		Map<String, Object> rsMap = null;
		
		try {
			// 加载随访信息
			rsMap = hvm.getHypertensionVisitByPkey(pkey);
			List<Map<String, Object>> list = hvm.getMedicinesByVisitId(pkey);
			if (list != null && list.size() > 0) {
				int n = list.size();
				if (n > 4) {
					n = 4;
				}
				Map<String, Object> medicineIds = new HashMap<String, Object>();
				for (int i = 1; i <= n; i++) {
					Map<String, Object> map = list.get(i - 1);
					rsMap.put("drugNames" + i, map.get("medicineName"));
					rsMap.put("everyDayTime" + i, map.get("medicineFrequency"));
					rsMap.put("oneDosage" + i, map.get("medicineDosage"));
					rsMap.put("medicineUnit" + i, map.get("medicineUnit"));
					medicineIds.put("medicine" + i, map.get("recordId"));
				}
				rsMap.put("medicineIds", medicineIds);
			}
			rsMap = SchemaUtil.setDictionaryMessageForForm(rsMap,
					MDC_HypertensionVisit);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		HashMap<String, Object> resBody = new HashMap<String, Object>();
		res.remove("body");
		resBody.put("visitInfo", rsMap);
		String empiId = (String) reqBody.get("empiId");

		// @@ 从主档取身高
		Double height = 0D;
		HypertensionModel hm = new HypertensionModel(dao);
		try {
			height = hm.getHeight(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Get height of person with empiId =[" + empiId
					+ "] failed.", e);
			throw new ServiceException(e);
		}
		resBody.put("height", height);

		// @@ 取管辖机构
		PublicModel pm = new PublicModel(dao);
		String manaUnitId;
		try {
			manaUnitId = pm.getManaUnit(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Get manaUnitId of EHR_HealthRecord with empiId = ["
					+ empiId + "] and status = ["
					+ Constants.CODE_STATUS_NORMAL + "]", e);
			throw new ServiceException(e);
		}
		resBody.put("manaUnitId", manaUnitId);

		res.put("body", resBody);
	}

	//随访信息保存
	@SuppressWarnings({ "unchecked" })
	public void doSaveHypertensionVisit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException {
		//传日志到大数据接口（慢病管理任务提醒） --wdl
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
			"\"apiCode\":\"MBGLRWTX\",\n"+
			"\"operSystemCode\":\"ehr\",\n"+
			"\"operSystemName\":\"健康档案系统\",\n"+
			"\"fromDomain\":\"ehr_yy\",\n"+
			"\"toDomain\":\"ehr_mb\",\n"+
			"\"clientAddress\":\""+ipc+"\",\n"+
			"\"serviceBean\":\"esb.MBGLRWTX\",\n"+
			"\"methodDesc\":\"void doSaveHypertensionVisit()\",\n"+
			"\"statEnd\":\""+curDate1+"\",\n"+
			"\"stat\":\"1\",\n"+
			"\"avgTimeCost\":\""+num+"\",\n"+
			"\"request\":\"PublicService.httpURLPOSTCase(json)\",\n"+
			"\"response\":\"200\"\n"+
		          "}";	
				
				String json1="{ \n"+
						"\"orgCode\":\""+curUnitId+"\",\n"+
						"\"orgName\":\""+organname+"\",\n"+
						"\"ip\":\""+ipc+"\",\n"+
						"\"opertime\":\""+curDate1+"\",\n"+
						"\"operatorCode\":\""+curUserId+"\",\n"+
						"\"operatorName\":\""+USER_NAME+"\",\n"+
						"\"callType\":\"02\",\n"+
						"\"apiCode\":\"ZQXGWFWTX\",\n"+
						"\"operSystemCode\":\"ehr\",\n"+
						"\"operSystemName\":\"健康档案系统\",\n"+
						"\"fromDomain\":\"ehr_yy\",\n"+
						"\"toDomain\":\"ehr_mb\",\n"+
						"\"clientAddress\":\""+ipc+"\",\n"+
						"\"serviceBean\":\"esb.ZQXGWFWTX\",\n"+
						"\"methodDesc\":\"void doSaveHypertensionVisit()\",\n"+
						"\"statEnd\":\""+curDate1+"\",\n"+
						"\"stat\":\"1\",\n"+
						"\"avgTimeCost\":\""+num+"\",\n"+
						"\"request\":\"PublicService.httpURLPOSTCase(json)\",\n"+
						"\"response\":\"200\"\n"+
					          "}";				
            PublicService.httpURLPOSTCase(json);            
            PublicService.httpURLPOSTCase(json1);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
		
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req.get("body");
		List<Map<String, Object>> medicineList = (List<Map<String, Object>>) reqBody
				.get("medicineList");
		reqBody.remove("medicineList");
		String op = (String) req.get("op");
		String visitEffect = reqBody.get("visitEffect") + "";
		String empiId = reqBody.get("empiId").toString();
		String thisplanDate = reqBody.get("planDate").toString();
		String thisvisitDate = reqBody.get("visitDate").toString();
		String visitMeddle = "0";
		String manageUnit=UserRoleToken.getCurrent().getManageUnitId();
		HypertensionVisitModel VisitModel =new HypertensionVisitModel(dao);
		VisitPlanModel plan=new VisitPlanModel(dao);
		Map<String, Object> thisplan = plan.getPlan(reqBody.get("planId").toString());
		HashMap<String, Object> pa=new HashMap<String, Object>();
		Map<String, Object> birthday=new HashMap<String, Object>();
		try {
			pa.put("empiId", empiId);
			birthday=dao.doSqlLoad("select floor(to_char(sysdate,'yyyy')-to_char(birthday,'yyyy')) as BIRTHDAY from MPI_DemographicInfo" +
					" where empiId=:empiId ", pa);
		} catch (PersistentDataOperationException e3) {
			e3.printStackTrace();
		}
		int old=0;
		if(birthday!=null){
			old=Integer.parseInt(birthday.get("BIRTHDAY")+"");
		}
		reqBody.put("controlBad", false);
		//溧阳判断控制不满意条件更多
		Boolean jyzz=false;//建议转诊
		if(manageUnit.indexOf("320481")==0){//溧阳
			//出现药物不良反应，服药依从性是规律
			if(reqBody.get("medicineBadEffect").toString().equals("y") 
				&&(reqBody.get("medicine").toString().equals("1")) ){
				if(reqBody.get("visitEvaluate").equals("") ||reqBody.get("visitEvaluate").equals("1")){
					reqBody.put("visitEvaluate", '2');
				}
				reqBody.put("controlBad", true);
			}
			//药物不良反应难以控制以及出现并发症
			if(reqBody.get("medicineBadEffect").toString().equals("y") 
				&&!(reqBody.get("complication").toString().equals("")
				|| reqBody.get("complication").toString().equals("16") )
			){
				if(reqBody.get("visitEvaluate").equals("") ||reqBody.get("visitEvaluate").equals("1")){
					reqBody.put("visitEvaluate", '2');
				}
				reqBody.put("controlBad", true);
				jyzz=true;
				}
		}else if (manageUnit.indexOf("320124")==0){//溧水
			if(reqBody.get("medicineBadEffect").toString().equals("y") ){
				reqBody.put("controlBad", true);
			}
			if((thisplan.get("visitMeddle")+"").equals("1"))
			{
				if(!(reqBody.get("complication")+"").equals("")){
						reqBody.put("controlBad", true);
					}
				if((reqBody.get("complicationIncrease")+"").equals("y")){
					reqBody.put("controlBad", true);	
				}
			}
		}
		//收缩压大于139 或 舒张压大于89，随访分类强制控制不满意
		double constriction=0.0;
		double diastolic=0.0;
		if(reqBody.get("constriction")!=null && reqBody.get("constriction").toString().length()>0){
			constriction=Double.parseDouble(reqBody.get("constriction").toString());
		}
		if(reqBody.get("diastolic")!=null && reqBody.get("diastolic").toString().length()>0){
			diastolic=Double.parseDouble(reqBody.get("diastolic").toString());
		}
		if(constriction>139||diastolic>89 ){
			//yx-2017-06-01-新政策65岁及以上的收缩压>=150算控制不满意
			if(old>=65){
				if(constriction>149){
					reqBody.put("controlBad", true);
					reqBody.put("visitEvaluate", '2');
				}
				if(diastolic>89){
					reqBody.put("controlBad", true);
					reqBody.put("visitEvaluate", '2');
				}
			}else{
				reqBody.put("controlBad", true);
				reqBody.put("visitEvaluate", '2');
			}
		   }
		
		if((Boolean)reqBody.get("controlBad")){
			if(VisitModel.checkHasPlanInTwoWeek(empiId, thisvisitDate)==false){
				reqBody.put("needInsertPlan", true);
			}
		}
		boolean needReferral = false;// 是否转诊
		Map<String, Object> lastVisitData = null;
		String visitDate=reqBody.get("visitDate").toString();
		try {
			lastVisitData = VisitModel.getLastVistRecordVisitId(empiId,new SimpleDateFormat("yyyy-MM-dd").parse(visitDate));
		} catch (ParseException e2) {
			throw new ModelDataOperationException(Constants.CODE_DATABASE_ERROR, "获取是否需要定转组参数失败。");
		}
		if (reqBody.get("controlBad")!=null &&  (Boolean)reqBody.get("controlBad")== true 
				&& lastVisitData != null) {
			Map<String, Object> lastplan = plan.getPlanbyVisitId(lastVisitData.get("visitId").toString());
		
			//溧阳有建议转诊随访干预，浦口没有，所以做了区分
			if(lastplan!=null){//系统有时最后一条随访取不到随访计划
			if(manageUnit.indexOf("320481")==0){//溧阳
			//连续两次随访不满意并且前一次随访是正常计划，建议转诊，计划干预是建议转诊干预
			if(thisplan.get("visitMeddle").equals("1") ){
				needReferral = true;
			    visitMeddle="0";
			}
			//连续两次随访不满意并且本次随访是建议转诊干预，不建议转诊
			if(thisplan.get("visitMeddle").equals("2")){
				needReferral = true;
			    visitMeddle="0";
			   
			}//本次随访是计划随访，计划干预是随访干预，不建议转诊
			if(thisplan.get("visitMeddle").equals("0")){
				needReferral = false;
			    visitMeddle="1";
			}
			//建议转诊设置
			if(jyzz){
				if(thisplan.get("visitMeddle").equals("2")){
					needReferral = true;
				    visitMeddle="0";
				}else if (thisplan.get("visitMeddle").equals("1")){
					needReferral = true;
					visitMeddle="0";
				}else{
					needReferral = false;
				    visitMeddle="1";
				}
			}
			//yx溧阳需求变了3次了，这次是半年生成一次随访干预
			if(visitMeddle.equals("1")){
				MDCBaseModel bm=new MDCBaseModel(dao);
				if(bm.checkHasVisithalfyear(empiId,"1",BSCHISUtil.toDate(thisplanDate))){
					visitMeddle="0";
				};
			}
			}else if(manageUnit.indexOf("320111")==0){//浦口
				//本次随访是计划随访，计划干预是随访干预，不建议转诊
				if(thisplan.get("visitMeddle").equals("0")){
					needReferral = false;
				    visitMeddle="1";
				}
				if(lastplan.get("visitMeddle").toString().equals("0")
						&& thisplan.get("visitMeddle").equals("1") ){
						needReferral = true;
					    visitMeddle="0";
				}
				if(constriction >179 || diastolic >109 ){
					needReferral = true;
				}
			}else if (manageUnit.indexOf("320124")==0){//溧水
				//本次随访是计划随访，计划干预是随访干预，不建议转诊
				if(thisplan.get("visitMeddle").equals("0")){
					needReferral = false;
				    visitMeddle="1";
				}
				if(lastplan.get("visitMeddle").toString().equals("0")
						&& thisplan.get("visitMeddle").equals("1") ){
						needReferral = true;
					    visitMeddle="2";
				}
				if(thisplan.get("visitMeddle").equals("1")){
					boolean flag=false;
					if(reqBody.get("medicineBadEffect").toString().equals("y")){
						flag = true;
					}
					String last_complication=lastVisitData.get("complication")==null?"":lastVisitData.get("complication")+"";
					if(!(reqBody.get("complication")+"").equals(last_complication)){
						flag = true;
					}
					if((reqBody.get("complicationIncrease")+"").equals("y")){
						flag = true;
					}
					if(flag){
						needReferral = true;
						visitMeddle="2";
					}
				}
				
				if(thisplan.get("visitMeddle").equals("2")){
					needReferral = true;
				    visitMeddle="0";
				}
				if(constriction >179 || diastolic >109 ){
					needReferral = true;
				}
			}else{//其他
				//本次随访是计划随访，计划干预是随访干预，不建议转诊
				if(thisplan.get("visitMeddle").equals("0")){
					needReferral = false;
				    visitMeddle="1";
				}
				if(lastplan.get("visitMeddle").toString().equals("0")
						&& thisplan.get("visitMeddle").equals("1") ){
						needReferral = true;
					    visitMeddle="2";
				}
				if(thisplan.get("visitMeddle").equals("1")){
					boolean flag=false;
					if(reqBody.get("medicineBadEffect").toString().equals("y")){
						flag = true;
					}
					String last_complication=lastVisitData.get("complication")==null?"":lastVisitData.get("complication")+"";
					if(!(reqBody.get("complication")+"").equals(last_complication)){
						flag = true;
					}
					if((reqBody.get("complicationIncrease")+"").equals("y")){
						flag = true;
					}
					if(flag){
						needReferral = true;
						visitMeddle="2";
					}
				}
				
				if(thisplan.get("visitMeddle").equals("2")){
					needReferral = true;
				    visitMeddle="0";
				}
				if(constriction >179 || diastolic >109 ){
					needReferral = true;
				}
			}
			}else{
				needReferral = false;
			    visitMeddle="1";
			}
		}
		if(manageUnit.indexOf("320481")==0){//溧阳
			if (reqBody.get("controlBad")!=null &&  (Boolean)reqBody.get("controlBad")== true 
			&& lastVisitData == null){
				if(jyzz){
					needReferral = false;
					visitMeddle="1";
				}else{
					needReferral = false;
					visitMeddle="1";
				}
			}
		}else if (manageUnit.indexOf("320111")==0 ||manageUnit.indexOf("320124")==0){//浦口、溧水
			if (reqBody.get("controlBad")!=null &&  (Boolean)reqBody.get("controlBad")== true 
					&& lastVisitData == null){
				if(constriction >179 || diastolic >109){
					needReferral = true;
				}else{
					needReferral = false;
				}
				visitMeddle="1";
			}
		}else{//其他
			if (reqBody.get("controlBad")!=null &&  (Boolean)reqBody.get("controlBad")== true 
					&& lastVisitData == null){
				if(constriction >179 || diastolic >109){
					needReferral = true;
				}else{
					needReferral = false;
				}
				visitMeddle="1";
			}
		}
		reqBody.put("needReferral", needReferral);
		
		// 是否 延后录入
		if (op.equals("create")) {
			String inputDate = reqBody.get("inputDate") + "";
			String planDate = reqBody.get("planDate") + "";
			int period = BSCHISUtil.getPeriod(
					BSCHISUtil.toDate(inputDate.substring(0, 10)),
					BSCHISUtil.toDate(planDate.substring(0, 10)));
			String delayDaysString;
			try {
				delayDaysString = ApplicationUtil.getProperty(
						Constants.UTIL_APP_ID, BusinessType.GXY + "_delayDays");
			} catch (ControllerException e) {
				throw new ServiceException(e);
			}
			int delayDays = Integer.parseInt(delayDaysString);
			if (period > delayDays) {// 1_delayDays
				reqBody.put("lateInput", YesNo.YES);
			} else {
				reqBody.put("lateInput", YesNo.NO);
			}
		}
		// 继续随访*计算分组分级
		HypertensionFixGroupModel hfgm = new HypertensionFixGroupModel(dao);
		if (VisitEffect.CONTINUE.equals(visitEffect)) {
			try {
				Map<String, Object> groupMap = hfgm
						.getHypertensionGroup(reqBody);
				String riskLevel = groupMap.get("riskLevel") + "";
				reqBody.put("riskLevel", riskLevel);
				reqBody.put("groupCode", groupMap.get("group"));
			} catch (ModelDataOperationException e) {
				logger.error("Get Hypertension Group failed", e);
				throw new ServiceException(Constants.CODE_DATABASE_ERROR,
						"获取定转组信息失败！");
			}
		}
		// 保存高血压信息
		reqBody.put("lastModifyUser", UserRoleToken.getCurrent().getUserId());
		reqBody.put("lastModifyDate",
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

		HypertensionVisitModel hvm = new HypertensionVisitModel(dao);
		String entryName = MDC_HypertensionVisit;
		Map<String, Object> resBody = null;
		try {
			//保存高血压随访
			resBody = hvm.saveHypertensionVisitInfo(op, entryName, reqBody,
					false);
			String drugNames = "drugNames";
			String everyDayTime = "everyDayTime";
			String oneDosage = "oneDosage";
			String medicine = "medicine";
			String medicineUnit = "medicineUnit";
			Map<String, Object> medicineIds = null;
			if (reqBody.get("medicineIds") != null) {
				medicineIds = (Map<String, Object>) reqBody.get("medicineIds");
			}
		    
			if(reqBody.containsKey("hypertensionMedicines")){
				List<Map<String, Object>> vmList = (List<Map<String, Object>>) reqBody
						.get("hypertensionMedicines");
				for(int i=0;i<vmList.size();i++){
					Map<String, Object> medicineMap = vmList.get(i);
					String op2 = "create";
					String visitId = (String) reqBody.get("visitId");
					if (visitId == null || "".equals(visitId)) {
						visitId = (String) resBody.get("visitId");
					}
					medicineMap.put("visitId", visitId);	
					medicineMap.put("phrId", reqBody.get("phrId"));
					medicineMap.put("empiId", resBody.get("empiId"));
					medicineMap.put("days", 1);
					hvm.saveMedicineRecord(op2, medicineMap);
				}
			}else{
			for (int i = 1; i < 5; i++) {
				String drugNamesN = reqBody.get(drugNames + i) + "";
				String everyDayTimeN = reqBody.get(everyDayTime + i) + "";
				String oneDosageN = reqBody.get(oneDosage + i) + "";
				String medicineUnitN = reqBody.get(medicineUnit + i) + "";
				String medicineId="";
				if(medicineIds!=null){
					medicineId=medicineIds.get(medicine+i)==null?"":medicineIds.get(medicine+i)+"";
				}
				if (drugNamesN != null && !"".equals(drugNamesN)
						&& !"null".equals(drugNamesN) && everyDayTimeN != null
						&& !"".equals(everyDayTimeN)
						&& !"null".equals(everyDayTimeN) && oneDosageN != null
						&& !"".equals(oneDosageN) && !"null".equals(oneDosageN)) {
					Map<String, Object> medicineMap = new HashMap<String, Object>();
					medicineMap.put("medicineName", drugNamesN);
					medicineMap.put("medicineId", drugNamesN);
					medicineMap.put("medicineFrequency", everyDayTimeN);
					medicineMap.put("medicineDosage",
							Double.parseDouble(oneDosageN));
					medicineMap.put("medicineUnit", medicineUnitN);
					medicineMap.put("days", 1);
					String visitId = (String) reqBody.get("visitId");
					if (visitId == null || "".equals(visitId)) {
						visitId = (String) resBody.get("visitId");
					}
					medicineMap.put("visitId", visitId);
					medicineMap.put("phrId", resBody.get("phrId"));
					medicineMap.put("empiId", resBody.get("empiId"));
					String medicineN = medicine + i;
					String op2 = "create";
					if (medicineIds != null
							&& medicineIds.get(medicineN) != null) {
						medicineMap.put("recordId", medicineIds.get(medicineN));
						op2 = "update";
					} else {
						medicineMap.put("medicineDate", new Date());
					}
					hvm.saveMedicineRecord(op2, medicineMap);
				}else{
					if(medicineId.length()>8){
						try {
							Map<String, Object> re=new HashMap<String, Object>();
							re.put("recordId", medicineId);
							//前台如果点了以前的随访，在新录随访时没录药品，以前随访记录的recordId会带过来，所以加上visitId防止删错数据。
							re.put("visitId", reqBody.get("visitId")+"");
							dao.doUpdate("delete from MDC_HypertensionMedicine where recordId=:recordId and visitId=:visitId", re);
						} catch (PersistentDataOperationException e) {
							e.printStackTrace();
						}
					}
				}
			}
			}
		} catch (ModelDataOperationException e) {
			logger.error("Save hypertension visit record failed.", e);
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"保存随访信息失败", e);
		}
		if (resBody == null) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"保存随访信息失败！");
		}
		resBody.put("needReferral", reqBody.get("needReferral"));
		String visitId = (op.equals("update") ? reqBody.get("visitId")
				: resBody.get("visitId")) + "";
		String phrId = reqBody.get("phrId") + "";
		String medicine = reqBody.get("medicine") + "";
		//标识签约任务完成
		Date visitDate1 = BSCHISUtil.toDate(reqBody.get("visitDate") + "");
		int month = visitDate1.getMonth();
		this.finishSCServiceTask(empiId, GXYSF_GXYFW, month + "", dao);
		// ------------- 高血压用药情况处理 =start= -----------------------------
		HypertensionMedicineModel hmm = new HypertensionMedicineModel(dao);
		// 默认前次服药数据
		boolean addMedicine = true;
		if (op.equals("create")
				&& (medicine.equals("1") || medicine.equals("2"))) {
			String lastVisitId = reqBody.get("lastVisitId") + "";
			boolean hasDefaultMedicine = false;
			try {// 默认前次服药数据
				hasDefaultMedicine = hmm.defaultMedicine(lastVisitId, visitId,
						phrId);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to save default medicine records.", e);
				throw new ServiceException(e);
			}
			// 保存更新药品信息
			if (medicineList != null) {
				for (int i = 0; i < medicineList.size(); i++) {
					Map<String, Object> medicineMap = medicineList.get(i);
					String recordId = medicineMap.get("recordId") + "";
					medicineMap.put("visitId", visitId);
					try {
						if (StringUtils.isEmpty(recordId)) {
							hvm.saveHyperVisitMedicineInfo("create",
									MDC_HypertensionMedicine, medicineMap, true);
						} else {
							continue;
						}
					} catch (ModelDataOperationException e) {
						logger.error("Saving medicine data failed.", e);
						throw new ServiceException(e);
					}
				}
			}
			if (hasDefaultMedicine && medicineList != null
					&& medicineList.size() > 0) {
				addMedicine = false;
			}
		}
		resBody.put("addMedicine", addMedicine);

		// 清除服药数据
		if (op.equals("update")
				&& (reqBody.get("deleteMedicine") != null && (Boolean) reqBody
						.get("deleteMedicine") == true)) {
			try {// 清除服药数据
				hmm.deleteMedicineList(phrId, visitId);
			} catch (ModelDataOperationException e) {
				logger.error("Update medicine data failed.", e);
				throw new ServiceException(e);
			}
		}
		// ------------- 高血压用药情况处理 =end= -----------------------------
		// ----------------- 下面是做一些相关数据同步的更新操作 begin------------
		// @@ 更新主档里的体重。
		Double weight = null;
		if (reqBody.get("weight") instanceof String) {
			weight = null;
		} else if (reqBody.get("weight") instanceof Integer) {
			weight = ((Integer) reqBody.get("weight")).doubleValue();
		} else {
			weight = (Double) reqBody.get("weight");
		}
		HypertensionModel hm = new HypertensionModel(dao);
		if (null != weight && weight > 0) {
			try {
				hm.updateWeihtOfHypertensionRecord(empiId, weight.doubleValue());
			} catch (ModelDataOperationException e) {
				logger.error("Update weigt of MDC_HypertensionRecord failed.",
						e);
				throw new ServiceException(e);
			}
		}
        //更新下次随访日期
		
		String S_nextDate =reqBody.get("nextDate").toString();
		if(S_nextDate!=null && S_nextDate.length() > 0 ){
			Map<String, Object> up = new HashMap<String, Object>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date D_nextDate=new Date();
			try {
				D_nextDate = sdf.parse(S_nextDate);
				up.put("nextVisitDate1", D_nextDate);
				up.put("nextVisitDate2", D_nextDate);
				String upnextvisitdatesql="update MDC_HypertensionRecord a set a.nextVisitDate=:nextVisitDate1" +
						" where a.empiId='"+empiId+"' and (a.nextVisitDate <:nextVisitDate2 or a.nextVisitDate is null )";
				dao.doUpdate(upnextvisitdatesql, up);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// @@ 更新随访计划---更新当前随访计划记录中相关数据
		HashMap<String, Object> upBody = new HashMap<String, Object>();
		String planId = reqBody.get("planId") + "";
		upBody.put("planId", planId);
		if (visitEffect.equals(VisitEffect.LOST)) {
			upBody.put("planStatus", PlanStatus.LOST);
		} else if (visitEffect.equals(VisitEffect.END)) {
			upBody.put("planStatus", PlanStatus.WRITEOFF);
		} else {
			upBody.put("planStatus", PlanStatus.VISITED);
		}
		upBody.put("visitDate", reqBody.get("visitDate"));
		upBody.put("visitId", visitId);
		upBody.put("lastModifyUser", UserRoleToken.getCurrent().getUserId());
		upBody.put("lastModifyDate",
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		try {
			hvm.updateHypertensionVisitPlan(upBody, true);
		} catch (ModelDataOperationException e) {
			logger.error("Update plan status failed.", e);
			throw new ServiceException(e);
		}

		// 更改前面未处理过的随访计划的状态。
		// 将本次随访之前未做的随访计划状态设置为"未访"
		UserRoleToken ur = UserRoleToken.getCurrent();
		String userId = ur.getUserId();
		
		//计划补录时间段内不把以往计划计划更改成未防状态
//		if (op.equals("create")) {
//			String beginDate = reqBody.get("beginDate") + "";
//			try {
//				hvm.updatePastDueVisitPlanStatus(empiId, beginDate, userId);
//			} catch (ModelDataOperationException e) {
//				logger.error("Update status of history visit plan failed.", e);
//				throw new ServiceException(e);
//			}
//		}

		// 更新下一次随访提醒时------为本次的下次预约时间
		String nextPlanId = reqBody.get("nextPlanId") + "";
		Date nextDate = null;
		if (reqBody.get("nextDate") instanceof String) {
			nextDate = BSCHISUtil.toDate(reqBody.get("nextDate") + "");
		} else if (reqBody.get("nextDate") instanceof Date) {
			nextDate = (Date) reqBody.get("nextDate");
		}
		if (StringUtils.isNotEmpty(nextPlanId) && null != nextDate) {
			try {
				hm.setNextRemindDate(BusinessType.GXY, nextPlanId, nextDate);
			} catch (Exception e) {
				logger.error("Update warn date of next visit failed.", e);
				throw new ServiceException(e);
			}
		}
		// ----------------- 上面是做一些相关数据同步的更新操作 end-------------
		// 转归“终止管理”，注销高血压档案。
		if (visitEffect.equals(VisitEffect.END)) {
			try {
				HashMap<String, Object> body = new HashMap<String, Object>();
				body.put("cancellationReason", reqBody.get("noVisitReason"));
				body.put("cancellationDate", new Date());
				body.put("cancellationUser", userId);
				body.put("cancellationUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
				body.put("phrId", phrId);
				body.put("visitEffect", visitEffect);
				body.put("noVisitReason", reqBody.get("noVisitReason"));
				body.put("visitDate", reqBody.get("visitDate"));
				// 注销高血压档案及注销其他随访计划
				hvm.setHypertensionRecordLogout(body);
			} catch (Exception e) {
				logger.error("Write off hypertension record failed.", e);
				throw new ServiceException(e);
			}
			res.put("body", resBody);
			return;
		}

		String planMode;
		try {
			planMode = ApplicationUtil.getProperty(Constants.UTIL_APP_ID,
					BusinessType.GXY + "_planMode");
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}

		if (planMode.equals("1") || planMode.equals("3")) {
			// 判断是否需要分组评估提醒
			try {
				Date planDate = BSCHISUtil.toDate((reqBody.get("planDate") + "").substring(0, 10));
				String businessType = BusinessType.GXY;
				resBody.put("groupAlarm",alarmAndIfLast(reqBody, planDate, businessType, dao));
			} catch (ModelDataOperationException e) {
				logger.error("Check year group alarm failed.", e);
				throw new ServiceException(e);
			}
		}

		// @@ 如果不是“终止管理”和“失访”，需要判断是否需要评估。
		if (!visitEffect.equals(VisitEffect.LOST)) {
			boolean needGroup;
			try {
				// fixDate (评估日期)从这里查出,放到reqBody
				needGroup = hm.checkHyperFactor(reqBody);
			} catch (ModelDataOperationException e) {
				logger.error("Check HyperFactor failed.", e);
				throw new ServiceException(e);
			}
			resBody.put("needGroup", needGroup);
		}
		// 取正常随访记录
		List<Map<String, Object>> normalVisitList;
		try {
			normalVisitList = hvm.getLastNormalVisit(phrId);
		} catch (ModelDataOperationException e) {
			logger.error("Get normal hypertension visit record failed.", e);
			throw new ServiceException(e);
		}
		// 有正常的随访*且本次随访不是终止管理
		if (normalVisitList != null && normalVisitList.size() > 0) {
			// 上次正常随访ID
			Map<String, Object> lastVisitMap = new HashMap<String, Object>();
			if (visitEffect.equals(VisitEffect.CONTINUE)) {// 本次正常
				if (normalVisitList.size() > 2) {
					lastVisitMap = normalVisitList.get(1);
				}
				// 无上次随访，lastVisitId为空
			} else {
				lastVisitMap = normalVisitList.get(0);
			}
			String lastVisitId = lastVisitMap.get("visitId") + "";
			reqBody.put("lastVisitId", lastVisitId);
			// 如果失访-按上次时间周期生成随访
			// *关键是要知道上次未失败随访的血压控制结果和评估分组级别
			if (visitEffect.equals(VisitEffect.LOST)) {
				Map<String, Object> lnvrMap = normalVisitList.get(0);
				// 计算上次正常随访的分组
				try {
					Map<String, Object> groupMap = hfgm
							.getHypertensionGroup(lnvrMap);
					reqBody.put("groupCode", groupMap.get("group"));
				} catch (ModelDataOperationException e) {
					logger.error("Get Hypertension Group failed", e);
					throw new ServiceException(Constants.CODE_DATABASE_ERROR,
							"获取上次正常随访的定转组信息失败！");
				}
				// 计算上次正常随访的血压控制结果*要替换的值--到下面正常程序里去计算
				reqBody.put("constriction", lnvrMap.get("constriction"));
				reqBody.put("diastolic", lnvrMap.get("diastolic"));
				reqBody.put("medicineBadEffect",lnvrMap.get("medicineBadEffect"));
				reqBody.put("complicationIncrease",lnvrMap.get("complicationIncrease"));
				if (normalVisitList.size() > 1) {
					Map<String, Object> snvrMap = normalVisitList.get(1);
					String snvrVisitId = snvrMap.get("visitId") + "";
					reqBody.put("lastVisitId", snvrVisitId);
				} else {
					reqBody.put("lastVisitId", null);
				}
			}

			HashMap<String, Object> paramMap = new HashMap<String, Object>();
			// 血压控制不满意、有药物不良反应、有新并发症、原有并发症加重
			// *计算血压控制结果及判断是否要提示 转诊
			if (PlanMode.BY_PLAN_TYPE.equals(planMode)|| PlanMode.BY_PLAN_MONTH.equals(planMode)) {
				if (reqBody.get("needReferral") != null) {
					needReferral = (Boolean) reqBody.get("needReferral");
				}
			} else {
				if (reqBody.get("nextDate") != null) {
					paramMap.put("reserveDate", reqBody.get("nextDate"));
				}
			}
			paramMap.put("empiId", reqBody.get("empiId"));
			paramMap.put("recordId", reqBody.get("phrId"));
			paramMap.put("phrId", reqBody.get("phrId"));
			paramMap.put("instanceType", BusinessType.GXY);
			paramMap.put("groupCode", reqBody.get("hypertensionGroup"));
			if (needReferral == true) {
				paramMap.put("visitResult", VisitResult.DISSATISFIED);
			} else {
				paramMap.put("visitResult", VisitResult.SATISFIED);
			}
			paramMap.put("visitDate", reqBody.get("visitDate"));
			paramMap.put("lastVisitDate", reqBody.get("visitDate"));
			paramMap.put("lastPlanDate", reqBody.get("planDate"));
			paramMap.put("reserveDate", reqBody.get("nextDate"));

			paramMap.put("hypertensionGroup", reqBody.get("hypertensionGroup"));
			if (reqBody.get("needChangeGroup") != null
					&& (Boolean) reqBody.get("needChangeGroup") == true) {
				paramMap.put("fixGroupDate", new Date());
			} else {
				List<Map<String, Object>> list = hvm.getFixGroupList(empiId);
				if (list != null && list.size() > 0) {
					paramMap.put("fixGroupDate",list.get(list.size() - 1).get("fixDate"));
				} else {
					paramMap.put("fixGroupDate", new Date());
				}
			}
			paramMap.put("constriction", reqBody.get("constriction"));
			paramMap.put("diastolic", reqBody.get("diastolic"));
			paramMap.put("lastComplication", reqBody.get("lastComplication"));
			paramMap.put("complicationIncrease",reqBody.get("complicationIncrease"));
			paramMap.put("nextDate", reqBody.get("nextDate"));
			paramMap.put("sn", reqBody.get("sn"));
			paramMap.put("lastestData", resBody.get("groupAlarm"));
			// 取档案责任医生-设置随访计划执行医生 --begin-------------
			HypertensionModel hModel = new HypertensionModel(dao);
			Map<String, Object> hrMap = null;
			try {
				hrMap = hModel.getHypertensionByPhrid(phrId);
				reqBody.putAll(hrMap);
			} catch (ModelDataOperationException e) {
				logger.error("Get Hypertension record falied.", e);
				throw new ServiceException(e);
			}
			if (hrMap != null) {
				paramMap.put("taskDoctorId", hrMap.get("manaDoctorId"));
			} else {
				paramMap.put("taskDoctorId", UserUtil.get(UserUtil.USER_ID));
			}
			// 取档案责任医生-设置随访计划执行医生 --end-------------

			if (reqBody.get("needChangeGroup") != null
					&& (Boolean) reqBody.get("needChangeGroup") == true) {
				try {
					// 血压随访转组生成
					hvm.insertGroupRecord(reqBody, paramMap, visitPlanCreator,ctx);
				} catch (ModelDataOperationException e) {
					logger.error("Failed to create diabetes visit plan.", e);
					throw new ServiceException(e);
				}
			}
	
			if (reqBody.get("needInsertPlan") != null && (Boolean) reqBody.get("needInsertPlan") == true
					&& !visitMeddle.equals("0")) {
			    paramMap.put("visitMeddle", visitMeddle);
			    if(reqBody.containsKey("needdoublevisit") && "2".equals(reqBody.get("needdoublevisit")+"") )
				{
					//不需要增加二次随访
			    	if(hvm.checkHasMeddlePlanInTwoWeek(empiId,thisplanDate)==true){
						hvm.deleteMeddlePlanInTwoWeek(empiId,thisplanDate);
					}
				}else{
					try {
						// 血压随访计划生成
						hvm.insertVisitPlan(paramMap);
					} catch (ModelDataOperationException e) {
					logger.error("Failed to create diabetes visit plan.", e);
					throw new ServiceException(e);
					}
				}
			}else if(hvm.checkHasMeddlePlanInTwoWeek(empiId,thisplanDate)==true){
				if(reqBody.get("controlBad") != null && !(Boolean) reqBody.get("controlBad") == true)
					hvm.deleteMeddlePlanInTwoWeek(empiId,thisplanDate);
			}

			// planMode=1 并且血压控制结果不满意
			if ((planMode.equals("1") || planMode.equals("3"))
					&& needReferral == true) {
				Map<String, Object> nextVisit = null;
				try {
					nextVisit = hm.getNextVisitPlan(empiId, BusinessType.GXY,
							planId);
				} catch (ModelDataOperationException e) {
					logger.error("Get next visitPlan failed.", e);
					throw new ServiceException(e);
				}
				if (nextVisit != null) {
					try {
						hvm.setNextDate(MDC_HypertensionVisit, visitId,
								(Date) nextVisit.get("beginVisitDate"));
					} catch (ModelDataOperationException e) {
						logger.error(
								"Update nextDate of MDC_HypertensionVisit failed.",
								e);
						throw new ServiceException(e);
					}
				}
			}
			// 如果planMode=1 血压控制结果不满意 并且是最后一条计划
			if ((planMode.equals("1") || planMode.equals("3"))
					&& needReferral == true || planMode.equals("2")) {
				resBody.put("groupAlarm", -1);
			}

		}
		WorkListModel wlm = new WorkListModel(dao);
		if (resBody.get("groupAlarm") != null
				&& "2".equals(resBody.get("groupAlarm") + "")
				&& !wlm.isExistWorkList(phrId, empiId,
						WorkType.MDC_HYPERTENSIONFIXGROUP, dao)) {
			reqBody.putAll(resBody);
			reqBody.put("workType", WorkType.MDC_HYPERTENSIONFIXGROUP);
			reqBody.put("recordId", phrId);
			reqBody.put("doctorId", reqBody.get("manaDoctorId"));
			reqBody.put("manaUnitId", reqBody.get("manaUnitId"));
			reqBody.put("beginDate", new Date());
			reqBody.put("otherId", reqBody.get("visitId"));
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, 1);
			reqBody.put("endDate", cal.getTime());
			hm.saveFixGroupWorkList(reqBody);
		}
		res.put("body", resBody);
	}

	// = 获取上一年度 、 本年度 、下一年度随访计划三个服务
	// =在HypertensionService 和 HypertensionInquireService都有为不改前端,故放在此
	/**
	 * 获取上年度的随访计划
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetPreYearVisitPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = StringUtils.trimToEmpty((String) req.get("empiId"));
		int current = (Integer) req.get("current");
		String instanceType = BusinessType.GXY;
		List<Map<String, Object>> rsList = null;
		try {
			VisitPlanModel vpm = new VisitPlanModel(dao);
			rsList = vpm.getVisitPlan(1, empiId, current, instanceType);
			res.put("body", rsList);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get visit plan of Previous year.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取高血压上年度随访计划失败！");
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取本年度的随访计划
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetCurYearVisitPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = StringUtils.trimToEmpty((String) req.get("empiId"));
		int current = 0;
		String instanceType = BusinessType.GXY;
		List<Map<String, Object>> rsList = null;
		try {
			VisitPlanModel vpm = new VisitPlanModel(dao);
			rsList = vpm.getVisitPlan(2, empiId, current, instanceType);
			res.put("body", rsList);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get visit plan of current.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取高血压本年度随访计划失败！");
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取下年度的随访计划
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetNextYearVisitPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = StringUtils.trimToEmpty((String) req.get("empiId"));
		int current = (Integer) req.get("current");
		String instanceType = BusinessType.GXY;
		List<Map<String, Object>> rsList = null;
		try {
			VisitPlanModel vpm = new VisitPlanModel(dao);
			rsList = vpm.getVisitPlan(3, empiId, current, instanceType);
			res.put("body", rsList);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get visit plan of current.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取高血压下年度随访计划失败！");
			throw new ServiceException(e);
		}
	}

	/**
	 * 判断是否需要重新生成计划。<br/>
	 * return 0 血压控制结果满意 1 血压控制结果不满意 2 血压控制由不满意转满意（计划开始日期为分组评估日期） needReferral
	 * 提示是否要提示 转诊---由reqBody带回调用者
	 * 
	 * @param reqBody
	 * @param resBody
	 * @param session
	 * @param ctx
	 * @return 0 血压控制结果满意 1 血压控制结果不满意 2 血压控制由不满意转满意（计划开始日期为分组评估日期）
	 * @throws JSONException
	 * @throws CreateVisitPlanException
	 */
	protected int whetherNeedCahangeVisit(Map<String, Object> reqBody,
			Map<String, Object> resBody, BaseDAO dao)
			throws ModelDataOperationException {
		// 收缩压(mmHg)
		int constriction = Integer.parseInt(reqBody.get("constriction") + "");
		// 舒张压(mmHg)
		int diastolic = Integer.parseInt(reqBody.get("diastolic") + "");
		// 药物不良反应
		String mb = reqBody.get("medicineBadEffect") + "";

		HypertensionVisitModel hvm = new HypertensionVisitModel(dao);
		Map<String, Object> res;
		res = hvm.getVisitRecordByVisitId((String) reqBody.get("lastVisitId"));
		String lcn = "0", lmb = "", lci = "";
		int lc = 0, ld = 0;
		if (res != null) {
			lcn = (String) res.get("complication"); // 上次并发症
			lc = (Integer) res.get("constriction");
			ld = (Integer) res.get("diastolic");
			lmb = (String) res.get("medicineBadEffect");
			lci = (String) res.get("complicationIncrease");
		}
		String cn = (String) reqBody.get("complication");
		reqBody.put("lastComplication", lcn);
		String ci = (String) reqBody.get("complicationIncrease");
		int needgenerate = 0;// 1 需要重建
		// 连续两次血压超标
		if ((constriction >= 140 || diastolic >= 90)) {
			needgenerate = 1;
			if (lc >= 140 || ld >= 90) {
				resBody.put("needReferral", 1);
			}
		}
		// 连续两次药物不良反应
		if (mb != null && YesNo.YES.equals(mb)) {
			needgenerate = 1;
			if (lmb != null && YesNo.YES.equals(lmb)) {
				resBody.put("needReferral", 1);
			}
		}
		// 原有并发症加重或有新并发症发现
		if (ci != null && YesNo.YES.equals(ci) || !compareStringValues(cn, lcn)) {
			needgenerate = 1;
			resBody.put("needReferral", 1);
		}
		// 由不满意转满意时(缺少新并发症逻辑)
		if ((lc >= 140 || ld >= 90 || YesNo.NO.equals(lmb) || (lci != null && "1"
				.equals(lci)))
				&& (constriction < 140 && diastolic < 90
						&& (mb != null && !YesNo.NO.equals(mb)) && !YesNo.YES
							.equals(ci))) {
			needgenerate = 2;
		}
		return needgenerate;
	}

	/**
	 * 比较两个以“，”分隔的字符串。
	 * 
	 * @param str1
	 * @param str2
	 * @return 如果str2包含所有str1的内容，返回true，否则false
	 */
	protected boolean compareStringValues(String str1, String str2) {
		if (str1 == null || "".equals(str1)) {
			return true;
		}
		if (str2 == null || "".equals(str2)) {
			return false;
		}
		String[] strs1 = str1.split(",");
		String[] strs2 = str2.split(",");
		for (int i = 0; i < strs1.length; i++) {
			for (int j = 0; j < strs2.length; j++) {
				if (strs1[i].equals(strs2[j])) {
					break;
				}
				if (j == strs2.length - 1) {
					return false;
				}
			}
		}
		return true;
	}

	// *** ==================== 高血压随访服药情况 ==================*********\\
	/**
	 * 检查是否有高血压随访药物
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doCheckHasMedicine(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String visitId = (String) reqBody.get("visitId");
		String phrId = (String) reqBody.get("phrId");

		HypertensionModel hm = new HypertensionModel(dao);
		long count = 0;
		try {
			count = hm.countVisitMedicine(phrId, visitId);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get count of hypertension visit medicine.",
					e);
			throw new ServiceException(e);
		}
		HashMap<String, Object> resBody = (HashMap<String, Object>) res
				.get("body");
		if (resBody == null) {
			resBody = new HashMap<String, Object>();
			res.put("body", resBody);
		}
		resBody.put("hasMedicine", count == 0 ? false : true);
	}

	public void doGetHyperVisitMedicine(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String visitId = (String) req.get("visitId");
		int pageSize = (Integer) req.get("pageSize");
		int pageNo = (Integer) req.get("pageNo");
		HypertensionVisitModel hvm = new HypertensionVisitModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = hvm.getHyperVisitMedicineList(visitId, pageSize, pageNo);
			rsMap = SchemaUtil.setDictionaryMessageForList(rsMap,
					MDC_HypertensionMedicine);
		} catch (ModelDataOperationException e) {
			logger.error("Get Hypertension visit Medicine list failed.", e);
			throw new ServiceException(e);
		}
		if (rsMap == null) {
			res.put("pageSize", pageSize);
			res.put("pageNo", pageNo);
			res.put("body", "");
			res.put("body", 0);
			return;
		}
		res.put("pageSize", pageSize);
		res.put("pageNo", pageNo);
		res.put("body", rsMap.get("body"));
		res.put("totalCount", rsMap.get("totalCount"));
	}

	/**
	 * 保存服药情况
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveVisitMedicine(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String op = (String) req.get("op");
		// String entryName = (String) req.get("schema");
		Map<String, Object> record = (Map<String, Object>) req.get("body");
		HypertensionVisitModel hvm = new HypertensionVisitModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = hvm.saveHyperVisitMedicineInfo(op,
					MDC_HypertensionMedicine, record, true);
		} catch (ModelDataOperationException e) {
			logger.error("Saving medicine data failed.", e);
			throw new ServiceException(e);
		}
		String recordId = (String) record.get("recordId");
		if ("create".equals(op)) {
			recordId = (String) rsMap.get("recordId");
		}
		vLogService.saveVindicateLog(MDC_HypertensionMedicine, op, recordId,
				dao);
	}

	/**
	 * 保存服药情况
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doSaveVisitMedicines(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String op = (String) req.get("op");
		// String entryName = (String) req.get("schema");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		HypertensionVisitModel hvm = new HypertensionVisitModel(dao);
		Map<String, Object> rsMap = null;

		List<Map> records = (List<Map>) body.get("records");
		Map resMap = new HashMap();
		try {

			for (int i = 0; i < records.size(); i++) {
				Map record = records.get(i);

				rsMap = hvm.saveHyperVisitMedicineInfo(op,
						MDC_HypertensionMedicine, record, true);
				String recordId = (String) record.get("recordId");
				if ("create".equals(op)) {
					recordId = (String) rsMap.get("recordId");
				}
				vLogService.saveVindicateLog(MDC_HypertensionMedicine, op,
						recordId, dao);

			}
			res.put("body", resMap);
		} catch (ModelDataOperationException e) {
			logger.error("Saving medicine data failed.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 删除随访用药
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doDelVisitMedicine(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String recordId = (String) req.get("pkey");
		HypertensionVisitModel hvm = new HypertensionVisitModel(dao);
		try {
			hvm.delHyperVisitMedicineByRecordId(recordId);
			vLogService.saveVindicateLog(MDC_HypertensionMedicine, "4",
					recordId, dao);
		} catch (ModelDataOperationException e) {
			logger.error("Delete Hypertension visit medicine failed.", e);
			throw new ServiceException(e);
		}
	}

	// ****** ================= 随访中医辨体 =================*************\\
	/**
	 * 根据随访ID取中医辨体信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetHyperVisitDescriptionByVisitId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String visitId = (String) reqBody.get("visitId");
		HypertensionVisitModel hvm = new HypertensionVisitModel(dao);
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		Map<String, Object> rsMap = null;
		try {
			rsMap = hvm.getHyperVisitDescriptionByVisitId(visitId);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Get hypertension visit description by visitId failed.", e);
			throw new ServiceException(e);
		}
		if (null != rsMap && rsMap.size() > 0) {
			rsMap = SchemaUtil.setDictionaryMessageForForm(rsMap,
					MDC_HyperVisitDescription);
			resBodyMap.putAll(rsMap);
		}
		// 获取控制权限
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		try {
			data = ControlRunner.run(MDC_HyperVisitDescription, reqBody, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check MDC_HyperVisitDescription control error.", e);
			throw e;
		}
		resBodyMap.put("_actions", data);
		res.put("body", resBodyMap);
	}

	/**
	 * 保存高血压随访中医辨体
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveHyperVisitDescription(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> resBody = new HashMap<String, Object>();
		String recordId = (String) body.get("recordId");
		HypertensionVisitModel hvm = new HypertensionVisitModel(dao);
		try {
			Map<String, Object> rsMap = hvm.saveHyperVisitDescription(op, body,
					true);
			if ("create".equals(op)) {
				body.putAll(rsMap);
				recordId = (String) rsMap.get("recordId");
			}
			resBody = SchemaUtil.setDictionaryMessageForForm(body,
					MDC_HyperVisitDescription);
			vLogService.saveVindicateLog(MDC_HyperVisitDescription, op,
					recordId, dao);
			res.put("body", resBody);
		} catch (ModelDataOperationException e) {
			logger.error("Save hypertension visit description failed.", e);
			throw new ServiceException(e);
		}
	}

	// ******* ========= 随访健康教育 =================== ********************\\
	/**
	 * 根据随访ID获取随访健康教育信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetHyperHealthTeachByVisitId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String wayId = (String) reqBody.get("wayId");
		HypertensionVisitModel hvm = new HypertensionVisitModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = hvm.getHyperVisitHealthTeachByVisitId(wayId);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Get hypertension visit health teach by visitId failed.", e);
			throw new ServiceException(e);
		}
		// 获取控制权限
		// Map<String, Boolean> data = new HashMap<String, Boolean>();
		// try {
		// data = ControlRunner.run(HER_HealthRecipeRecord_GXYSF, reqBody, ctx,
		// ControlRunner.CREATE, ControlRunner.UPDATE);
		// } catch (ServiceException e) {
		// logger.error("check HER_HealthRecipeRecord_GXYSF control error.", e);
		// throw e;
		// }
		// resBodyMap.put("_actions", data);
		res.put("body", rsMap);
	}

	/**
	 * 保存高血压随访健康教育
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveHyperHealthTeach(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		
		//传日志到大数据接口（健康宣教） --wdl
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
			"\"apiCode\":\"JKXJ\",\n"+
			"\"operSystemCode\":\"ehr\",\n"+
			"\"operSystemName\":\"健康档案系统\",\n"+
			"\"fromDomain\":\"ehr_yy\",\n"+
			"\"toDomain\":\"ehr_mb\",\n"+
			"\"clientAddress\":\""+ipc+"\",\n"+
			"\"serviceBean\":\"esb.JKXJ\",\n"+
			"\"methodDesc\":\"void doSaveHyperHealthTeach()\",\n"+
			"\"statEnd\":\""+curDate1+"\",\n"+
			"\"stat\":\"1\",\n"+
			"\"avgTimeCost\":\""+num+"\",\n"+
			"\"request\":\"PublicService.httpURLPOSTCase(json)\",\n"+
			"\"response\":\"200\"\n"+
		          "}";	
            PublicService.httpURLPOSTCase(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
		
		
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		// String recordId = (String) body.get("id");
		HypertensionVisitModel hvm = new HypertensionVisitModel(dao);
		try {
			Map<String, Object> rsMap = hvm.saveHyperVisitHealthTeach(op, body,
					vLogService);
			res.put("body", rsMap);
		} catch (ModelDataOperationException e) {
			logger.error("Save hypertension visit health teach failed.", e);
			throw new ServiceException(e);
		}
	}

	// ***************** ====== 高血压随访列表 ========****************
	@SuppressWarnings("unchecked")
	public void doGetVisitPlanId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		String visitId = (String) reqBodyMap.get("visitId");
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		HypertensionVisitModel hvmModel = new HypertensionVisitModel(dao);
		String planId = "";
		try {
			planId = hvmModel.getVisitPlanId(empiId, visitId);
		} catch (ModelDataOperationException e) {
			logger.error("Get visit plan id failed.", e);
			throw new ServiceException(e);
		}
		resBodyMap.put("planId", planId);
		res.put("body", resBodyMap);
	}

	/**
	 * 获取随访权限控制
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetHyperVisitControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		try {
			data = ControlRunner.run(MDC_HypertensionVisit, reqBodyMap, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check MDC_HypertensionVisit control error.", e);
			throw e;
		}
		res.put("body", data);
	}

	/**
	 * 获取高血压随访服药列表权限
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetHyperVisitMedicineControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		String phrId = (String) reqBodyMap.get("phrId");
		String visitId = (String) reqBodyMap.get("visitId");
		if (StringUtils.isEmpty(empiId) || StringUtils.isEmpty(phrId)
				|| StringUtils.isEmpty(visitId)) {
			logger.error("check MDC_HypertensionMedicine control error.The empiId or phrId or visitId is Null.");
			return;
		}
		HypertensionVisitModel hvModel = new HypertensionVisitModel(dao);
		long countNum = 0;
		try {
			countNum = hvModel.getHyperVisitMedicineRecordNum(phrId, visitId);
		} catch (ModelDataOperationException e) {
			logger.error("Total MDC_HypertensionMedicine record number error!",
					e);
			throw new ServiceException(e);
		}
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("empiId", empiId);
		paraMap.put("phrId", phrId);
		paraMap.put("recordNum", countNum);
		Map<String, Object> resBody = new HashMap<String, Object>();
		Map<String, Boolean> hrmControlMap = new HashMap<String, Boolean>();
		Map<String, Boolean> cMap = new HashMap<String, Boolean>();
		try {
			cMap = ControlRunner.run(MDC_HypertensionMedicine, paraMap, ctx,
					"visitCreate", "visitUpdate");
			hrmControlMap.put(ControlRunner.CREATE, cMap.get("visitCreate"));
			hrmControlMap.put(ControlRunner.UPDATE, cMap.get("visitUpdate"));
		} catch (ServiceException e) {
			logger.error("check MDC_HypertensionMedicine control error.", e);
			throw e;
		}
		resBody.put("_actions", hrmControlMap);
		res.put("body", resBody);
	}

	public void doCheckNeedChangeGroup(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String hypertensionGroup = (String) body.get("hypertensionGroup");
		String empiId = (String) body.get("empiId");
		if (hypertensionGroup == null || "".equals(hypertensionGroup)
				|| "create".equals(op)) {
			List<Object> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s",
					empiId);
			try {
				List<Map<String, Object>> list = dao.doQuery(cnd,
						"fixDate desc", MDC_HypertensionFixGroup);
				if (list != null && list.size() > 0) {
					hypertensionGroup = (String) list.get(0).get(
							"hypertensionGroup");
				}
			} catch (PersistentDataOperationException e) {
				logger.error("doCheckNeedChangeGroup failed", e);
				throw new ServiceException(e);
			}
		}
		String constriction = body.get("constriction") + "";
		String diastolic = body.get("diastolic") + "";
		String riskiness = (String) body.get("riskiness");
		String targetHurt = (String) body.get("targetHurt");
		String complication = (String) body.get("complication");
		String visitDate = (String) body.get("visitDate");
		String visitEvaluate = (String) body.get("visitEvaluate");
		String planDate = (String) body.get("planDate");
		HypertensionVisitModel dvm = new HypertensionVisitModel(dao);
		try {
			Map<String, Object> reqBody = dvm.getGroupConfig_pk(constriction,
					diastolic, riskiness, targetHurt, complication,
					hypertensionGroup, empiId, visitDate, visitEvaluate,
					planDate);
			res.put("body", reqBody);
		} catch (ModelDataOperationException e) {
			logger.error("doCheckNeedChangeGroup failed", e);
			throw new ServiceException(e);
		}
	}

	public void doGetLastThreeDay(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HypertensionVisitModel dvm = new HypertensionVisitModel(dao);
		try {
			dvm.getLastThreeDay(req, res, ctx);
		} catch (ModelDataOperationException e) {
			logger.error("do Get Last Three Day is  failed.", e);
			throw new ServiceException(e);
		}

	}

	public void doSaveHypertensionYearAssess(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HypertensionVisitModel drm = new HypertensionVisitModel(dao);
		try {
			drm.saveHypertensionYearAssess(visitPlanCreator, ctx);
		} catch (ModelDataOperationException e) {
			logger.error("save Diabetes Year Assess failed.", e);
			throw new ServiceException(e);
		}
	}

	public void doListHypertensionVistPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HypertensionVisitModel drm = new HypertensionVisitModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = drm.listHypertensionVistPlan(req);
		} catch (ModelDataOperationException e) {
			logger.error("list Hypertension Vist Plan failed.", e);
			throw new ServiceException(e);
		}
		res.putAll(rsMap);
	}
	
	public void doListHypertensionVistPlanQC(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HypertensionVisitModel drm = new HypertensionVisitModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = drm.listHypertensionVistPlanQC(req);
		} catch (ModelDataOperationException e) {
			logger.error("list Hypertension Vist Plan failed.", e);
			throw new ServiceException(e);
		}
		res.putAll(rsMap);
	}
	
	public void doDeleteHypertensionVistPlanbyplanId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HypertensionVisitModel drm = new HypertensionVisitModel(dao);
		String planId=req.get("planId")+"";
		try {
			drm.deleteHypertensionVistPlanbyplanId(planId);
		} catch (ModelDataOperationException e) {
			logger.error("delete Hypertension Vist Plan failed.", e);
			throw new ServiceException(e);
		}
	}
	//add-yx-2018-07-02-根据planId删除未随访计划
	public void doDeleteVisitPlan(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		String planId=req.get("planId")==null?"":req.get("planId")+"";
		if(planId.length()>6){
			Map<String, Object> m=new HashMap<String, Object>();
			m.put("planId",planId);
			String sql="delete from PUB_VisitPlan where planId=:planId and visitId is null ";
			try {
				res.put("deletecount",dao.doUpdate(sql,m));
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		}
	}
	public void doGetDiabetesVisitData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body=(Map<String, Object>)req.get("body");
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "phrId", "s", body.get("phrId")+"");
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "to_char(visitDate,'yyyy-MM-dd')","s",body.get("visitDate")+"");
		List<?> cnds = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		Map<String, Object> data=new HashMap<String, Object>();
		try {
			data=dao.doLoad(cnds, MDC_DiabetesVisit);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(data!=null && data.size() > 0){
			res.put("data", data);
		}
	}
}
