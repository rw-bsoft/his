/**
 * @(#)DiabetesService.java Created on 2012-1-18 上午9:57:37
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
import chis.source.dic.DiagnosisType;
import chis.source.dic.Gender;
import chis.source.dic.HypertensionRiskDataSource;
import chis.source.dic.PlanMode;
import chis.source.dic.VisitEffect;
import chis.source.dic.VisitResult;
import chis.source.dic.WorkType;
import chis.source.phr.LifeStyleModel;
import chis.source.pub.PublicService;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import chis.source.visitplan.DiabetesVisitPlanModel;
import chis.source.visitplan.VisitPlanCreator;
import chis.source.visitplan.VisitPlanModel;
import ctd.account.UserRoleToken;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class DiabetesVisitService extends DiabetesService {
	private static final Logger logger = LoggerFactory
			.getLogger(DiabetesVisitService.class);

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
	 * 保存糖尿病随访记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws Exception 
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDiabetesVisit(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)
			throws Exception {
		//传日志到大数据接口 （慢病管理任务提醒）--wdl
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
			"\"methodDesc\":\"void doSaveDiabetesVisit()\",\n"+
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
						"\"methodDesc\":\"void  doSaveDiabetesVisit()\",\n"+
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
		
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
		Map<String, Object> m = null;
		Map<String, Object> record = null;
		String empiId = body.get("empiId").toString();
		String manageUnit=UserRoleToken.getCurrent().getManageUnitId();
		String thisplanDate = body.get("planDate").toString();
		String visitMeddle = "0";
		DiabetesVisitModel diaVisitModel =new DiabetesVisitModel(dao);
		VisitPlanModel plan=new VisitPlanModel(dao);
		Map<String, Object> thisplan = plan.getPlan(body.get("planId").toString());
		try {
			body.put("controlBad", false);
			//溧阳判断控制不满意条件更多
			Boolean jyzz=false;//建议转诊
			if(manageUnit.indexOf("320481")==0){
				//出现药物不良反应，服药依从性是规律
				if(body.get("adverseReactions").toString().equals("2") 
						&&(body.get("medicine").toString().equals("1")) ){
						if(body.get("visitType").equals("") ||body.get("visitType").equals("1")){
							body.put("visitType", '2');
						}
						body.put("controlBad", true);
					}
				//药物不良反应难以控制以及出现并发症
				if(body.get("adverseReactions").toString().equals("2") 
						&&(body.get("complicationChange").toString().equals("1")
								|| body.get("complicationChange").toString().equals("2") )
						){
					if(body.get("visitType").equals("") ||body.get("visitType").equals("1")){
						body.put("visitType", '2');
					}
					body.put("controlBad", true);
					jyzz=true;
					}
			}else if (manageUnit.indexOf("320124")==0){//溧水
				if(body.containsKey("adverseReactions") && body.get("adverseReactions").toString().equals("2")){
					body.put("visitType", '2');
					body.put("controlBad", true);
				}
				if((thisplan.get("visitMeddle")+"").equals("1") &&((body.get("complicationChange")+"").equals("1")
						||(body.get("complicationChange")+"").equals("2")))
				{
					body.put("visitType", '2');
					body.put("controlBad", true);	
				}
			}
			//空腹血糖>6.9 或 餐后血糖 >9.9，随访分类强制控制不满意
			double fbs=0.0;
			double pbs=0.0;
			double constriction=0.0;
			double diastolic=0.0;
			if(body.get("fbs")!=null && body.get("fbs").toString().length()>0){
				fbs=Double.parseDouble(body.get("fbs").toString());
			}
			if(body.get("pbs")!=null && body.get("pbs").toString().length()>0){
				pbs=Double.parseDouble(body.get("pbs").toString());
			}
			if(body.get("constriction")!=null && body.get("constriction").toString().length()>0){
				constriction=Double.parseDouble(body.get("constriction").toString());
			}
			if(body.get("diastolic")!=null && body.get("diastolic").toString().length()>0){
				diastolic=Double.parseDouble(body.get("diastolic").toString());
			}
			if(fbs>6.9 || (fbs<4.0 && fbs>0)){
				if(body.get("visitType").equals("") ||body.get("visitType").equals("1")){
					body.put("visitType", '2');
				}
				body.put("controlBad", true);
			}
			if(pbs>9.9){
				body.put("controlBad", true);
			}
			if(constriction>179){//收缩压大于等于180随访干预
				if(body.get("visitType").equals("") ||body.get("visitType").equals("1")){
					body.put("visitType", '2');
				}
				body.put("controlBad", true);
			}
			if(diastolic>109){//舒张压大于等于110随访干预
				if(body.get("visitType").equals("") ||body.get("visitType").equals("1")){
					body.put("visitType", '2');
				}
				body.put("controlBad", true);
			}
			if((Boolean)body.get("controlBad")){
				if(diaVisitModel.checkHasPlanInTwoWeek(empiId,thisplanDate)==false){
					body.put("needInsertPlan", true);
				}
			}
			String visitDate=body.get("visitDate").toString();
			boolean needReferral = false;// 是否转诊
			Map<String, Object> lastVisitData = null;
			try {
				lastVisitData = diaVisitModel.getLastVistRecordVisitId(empiId,new SimpleDateFormat("yyyy-MM-dd").parse(visitDate));
			} catch (ParseException e2) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "获取最后一次随访记录失败。");
			}
			if (body.get("controlBad")!=null && (Boolean)body.get("controlBad")== true ) {
				if(lastVisitData != null){
				Map<String, Object> lastplan = plan.getPlanbyVisitId(lastVisitData.get("visitId").toString());
				//溧阳有建议转诊随访干预，浦口没有，所以做了区分
				if(manageUnit.indexOf("320481")==0){
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
						}else if(thisplan.get("visitMeddle").equals("1")) {
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
						if(bm.checkHasVisithalfyear(empiId,"2",BSCHISUtil.toDate(thisplanDate))){
							visitMeddle="0";
						};
					}				
				}
				else if(manageUnit.indexOf("320111")==0){//浦口
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
				}
				else if (manageUnit.indexOf("320124")==0){//溧水
					//本次随访是计划随访，计划干预是随访干预，不建议转诊
					if(thisplan.get("visitMeddle").equals("0")){
						needReferral = false;
					    visitMeddle="1";
					}
					if(lastplan.get("visitMeddle").toString().equals("0")&& thisplan.get("visitMeddle").equals("1") ){
						needReferral = true;
						visitMeddle="2";
					}
					if(thisplan.get("visitMeddle").equals("1") && ((body.get("complicationChange")+"").equals("1")
							||(body.get("complicationChange")+"").equals("2"))){
						needReferral = true;
						visitMeddle="2";
					}
					if(thisplan.get("visitMeddle").equals("2")){
						needReferral = true;
					    visitMeddle="0";
					}
			}else{//其他
				//本次随访是计划随访，计划干预是随访干预，不建议转诊
				if(thisplan.get("visitMeddle").equals("0")){
					needReferral = false;
				    visitMeddle="1";
				}
				if(lastplan.get("visitMeddle").toString().equals("0")&& thisplan.get("visitMeddle").equals("1") ){
					needReferral = true;
					visitMeddle="2";
				}
				if(thisplan.get("visitMeddle").equals("1") && ((body.get("complicationChange")+"").equals("1")
						||(body.get("complicationChange")+"").equals("2"))){
					needReferral = true;
					visitMeddle="2";
				}
				if(thisplan.get("visitMeddle").equals("2")){
					needReferral = true;
				    visitMeddle="0";
				}
			}
			if(constriction >179 || diastolic >109||fbs>16.6||(fbs<4.0 &&fbs>0) ){
				needReferral = true;
			}	
			}else{
				if(manageUnit.indexOf("320481")==0){
					if(jyzz){
						needReferral = true;
						visitMeddle="1";
					}else{
						needReferral = false;
						visitMeddle="1";
					}
				}else{
					if(constriction >179 || diastolic >109||fbs>16.6||(fbs<4.0 &&fbs>0) ){
						needReferral = true;
					}else{
						needReferral = false;
					}
					visitMeddle="1";
				}
			}
			}
			body.put("needReferral", needReferral);
			
			// 保存糖尿病随访
			record = dvm.saveDiabetesVisit(op, body, true);
			body.putAll(record);
			m = SchemaUtil.setDictionaryMessageForForm(body, MDC_DiabetesVisit);
			res.put("body", m);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save diabetes visit record.", e);
			throw new ServiceException(e);
		}
		String visitId = (String) body.get("visitId");
		vLogService.saveVindicateLog(MDC_DiabetesVisit, op, visitId, dao, empiId);

		//标识签约任务完成
		Date visitDate = BSCHISUtil.toDate((String) body.get("visitDate"));
		int month = visitDate.getMonth();
		this.finishSCServiceTask(empiId,TNBSF_TNBFW,month+"",dao);
		// 当服务情况有改变或是修改操作时，删除糖尿病服药情况
		if (body.get("medicineChange") != null && req.get("op").equals("update")) {
			if ((Boolean) body.get("medicineChange")) {
				try {
					dvm.deleteDiabetesMedicine(visitId);
				} catch (ModelDataOperationException e) {
					logger.error("Delete MDC_DiabetesMedicine records "+ "with visitId = " + visitId, e);
					throw new ServiceException(e);
				}
			}
		}
		Date visitDate2 = null;
		// 获取本年度最后一条随访记录/ 	的 ID
		if(body.get("visitDate") instanceof Date){
		 visitDate2 = (Date)body.get("visitDate");
		}else{
		 visitDate2 = BSCHISUtil.toDate((String) body.get("visitDate"));
		}
		Map<String, Object> lastVisitData = null;
		try {
			lastVisitData = dvm.getLastVistRecordVisitId(empiId, visitDate2);
		} catch (ModelDataOperationException e) {
			logger.error("Get last visitId of MDC_DiabetesVisit with empiId = "
					+ empiId + " and visitDate = " + visitDate, e);
			throw new ServiceException(e);
		}

		if ("create".equals(req.get("op"))) {
			if (null != lastVisitData) {
				String preVisitId = (String) lastVisitData.get("visitId");
				String exp = "['eq',['$','visitId'],['s','" + preVisitId+ "']]";
				try {
					dvm.saveComplicationList(exp, visitId);
				} catch (ModelDataOperationException e) {
					logger.error("Save MDC_DiabetesComplication failed.", e);
					throw new ServiceException(e);
				}
			}
			if (null != body.get("medicine")&& (body.get("medicine").equals("3")
				|| body.get("medicine").equals("4") || body.get("medicine").equals(""))) {
				((HashMap<String, Object>) res.get("body")).put(
						"needInputMedicine", false);
			} else {
				List<Map<String, Object>> vsList = (List<Map<String, Object>>) body.get("diabetesMedicines");
				String name="";
				if(vsList!=null){
					name=vsList.get(0).get("medicineName")==null?"":vsList.get(0).get("medicineName")+"";
				}
				if(name.length()>1){
					//自己录入了药品不默认带入前次随访的药品
				}else{
					String exp = "";
					if (null != lastVisitData) {
						// 找前一次随访的用药
						String preVisitId = (String) lastVisitData.get("visitId");
						exp = "['eq',['$','visitId'],['s','" + preVisitId + "']]";
					} else {
						// 如果找不到前一次随访说明是第一次随访 取档案录入的药品信息
						exp = "['and',['eq',['$','phrId'],['s','"+ body.get("phrId")
								+ "']],['eq',['$','visitId'],['s','0000000000000000']]]";
					}
					try {
						dvm.saveMedicineList(exp, res);
					} catch (ModelDataOperationException e) {
						logger.error("Save MDC_DiabetesMedicine failed.", e);
						throw new ServiceException(e);
					}
					String phrId = (String) body.get("phrId");
					try {
						int rsCount = dvm.getCountDiabetesMedicine(phrId, visitId);
						if (rsCount == 0) {
							((HashMap<String, Object>) res.get("body")).put("needInputMedicine", true);
						} else {
							((HashMap<String, Object>) res.get("body")).put("needInputMedicine", false);
						}
					} catch (ModelDataOperationException e) {
						logger.error("Chceked MDC_DiabetesMedicine failed.", e);
						throw new ServiceException(e);
					}
				}
			}
		} else {
			// @@ 更新的时候也要判断是否有服药。
			if (null != body.get("medicine")&& (body.get("medicine").equals("3")
				|| body.get("medicine").equals("4") || body.get("medicine").equals(""))) {
				((HashMap<String, Object>) res.get("body")).put("needInputMedicine", false);
			} else {
				String phrId = (String) body.get("phrId");
				try {
					int rsCount = dvm.getCountDiabetesMedicine(phrId, visitId);
					if (rsCount == 0) {
						((HashMap<String, Object>) res.get("body")).put("needInputMedicine", true);
					} else {
						((HashMap<String, Object>) res.get("body")).put("needInputMedicine", false);
					}
				} catch (ModelDataOperationException e) {
					logger.error("Chceked MDC_DiabetesMedicine failed.", e);
					throw new ServiceException(e);
				}
			}
		}

		String diabetesType = (String) body.get("diabetesFormType");
		String medicine = (String) body.get("medicine");
		if(body.containsKey("diabetesMedicines")){
			diabetesType="1";
		}
//		if ("1".equals(medicine) || "2".equals(medicine)) {
//			if (S.isNotEmpty(diabetesType)) {
//				List<Map<String, Object>> vmList = (List<Map<String, Object>>) body.get("vmList");
//				List<Map<String, Object>> vsList = (List<Map<String, Object>>) body.get("diabetesMedicines");
//				if((vmList==null)&&(vsList!=null)){
//					vmList = vsList;
//				}
//				String recordIds="";
//				for (int i = 0; i < vmList.size(); i++) {
//					Map<String, Object> vmMap = vmList.get(i);
//					vmMap.put("visitId",(String)body.get("visitId"));
//					vmMap.put("phrId",(String)body.get("phrId"));
//					String medicineName = (String) vmMap.get("medicineName");
//					String recordId = vmMap.get("recordId")==null?"":vmMap.get("recordId")+"";
//					if(recordId.length() >8 ){
//						recordIds+=",'"+recordId+"'";
//					}
//					if (S.isEmpty(medicineName)) {
//						continue;
//					}
//					String vmOp = "create";
//					
//					if (S.isNotEmpty(recordId)) {
//						vmOp = "update";
//					}
//					dvm.saveDiabetesMedicine(vmOp, vmMap, true);
//				}
//				Map<String, Object> pa=new HashMap<String, Object>();
//				pa.put("visitId", visitId);
//				Map<String, Object> countmap=dao.doSqlLoad(" select count(a.recordid) as COUNT from MDC_DiabetesMedicine a " +
//						" where a.visitId=:visitId", pa);
//				if(Integer.parseInt(countmap.get("COUNT")+"")>vmList.size()){
//					if(recordIds.length()>0){
//						dao.doUpdate("delete from MDC_DiabetesMedicine a where a.visitId=:visitId " +
//								" and a.recordId not in ("+recordIds.substring(1)+")", pa);
//						recordIds=null;
//					}
//				}
//				if(Integer.parseInt(countmap.get("COUNT")+"") >0 && (vmList==null ||vmList.size()==0)
//						&& !"create".equals(req.get("op"))){
//					dao.doUpdate("delete from MDC_DiabetesMedicine a where a.visitId=:visitId", pa);
//				}
//			}
//		}

		// 更新糖尿病档案中的身高体重
		double weight;
		if (body.get("weight") != null && !body.get("weight").equals("")) {
			weight = Double.valueOf(String.valueOf(body.get("weight")));
			String phrId = (String) body.get("phrId");
			try {
				dvm.updateDiabetesRecordWeight(weight, phrId);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to update weight of diabetes record.", e);
				throw new ServiceException(e);
			}
		}
		
		//更新下次随访日期
		String S_nextDate =body.get("nextDate").toString();
		if(S_nextDate!=null && S_nextDate.length() > 0 ){
			Map<String, Object> up = new HashMap<String, Object>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date D_nextDate=new Date();
			try {
				D_nextDate = sdf.parse(S_nextDate);
				up.put("nextVisitDate1", D_nextDate);
				up.put("nextVisitDate2", D_nextDate);
				String upnextvisitdatesql="update MDC_DiabetesRecord a set a.nextVisitDate=:nextVisitDate1" +
						" where a.empiId='"+empiId+"' and (a.nextVisitDate <:nextVisitDate2 or a.nextVisitDate is null )";
				dao.doUpdate(upnextvisitdatesql, up);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 更新随访计划信息
		String visitEffect = body.get("visitEffect")==null?"1":body.get("visitEffect")+"";// 界面转归
		String planId = (String) body.get("planId");// 随访计划主键
		String userId = UserRoleToken.getCurrent().getUserId();
		Map<String, Object> updateFields = new HashMap<String, Object>();
		updateFields.put("visitDate",BSCHISUtil.toDate((String) body.get("visitDate")));
		if (op.equals("create")) {
			updateFields.put("visitId", (String) m.get("visitId"));
		} else {
			updateFields.put("visitId", (String) body.get("visitId"));
		}
		updateFields.put("lastModifyUser", userId);

		// 更改前面未处理过的随访计划的状态。
		String planDate = null;
		if(body.get("planDate") instanceof Date){
		     planDate =  BSCHISUtil.toString((Date)body.get("planDate"));
		}else{
			 planDate = (String)body.get("planDate");
		}
		try {
			dvm.updateUntreatedVisitPlan(empiId, userId, planDate);
		} catch (Exception e) {
			logger.error("Update data of PUB_VisitPan failed.", e);
			throw new ServiceException(e);
		}
		if (visitEffect.equals(VisitEffect.END)|| visitEffect.equals(VisitEffect.LOST)) {
			((HashMap<String, Object>) res.get("body")).put("needInputMedicine", false);
		}

		HashMap<String, Object> reqBody = new HashMap<String, Object>();
		// 血糖控制不满意、有药物不良反应、有新并发症、原有并发症加重
		String planMode = null;
		try {
			planMode = ApplicationUtil.getProperty(Constants.UTIL_APP_ID,BusinessType.TNB + "_planMode");
		} catch (ControllerException e2) {
			throw new ServiceException(e2);
		}
		boolean needReferral = false;
		if (PlanMode.BY_PLAN_TYPE.equals(planMode)) {
			if (body.get("needReferral") != null) {
				needReferral = (Boolean) body.get("needReferral");
			}
		} else {
			if (body.get("nextDate") != null) {
				reqBody.put("reserveDate", body.get("nextDate"));
			}
		}

		reqBody.put("phrId", body.get("phrId"));
		reqBody.put("businessType", BusinessType.TNB);
		reqBody.put("empiId", body.get("empiId"));
		reqBody.put("groupCode", body.get("diabetesGroup"));
		reqBody.put("lastestData", body.get("lastestData"));
		DiabetesRecordModel drm = new DiabetesRecordModel(dao);
		if (body.get("needChangeGroup") != null&& (Boolean) body.get("needChangeGroup") == true) {
			reqBody.put("fixGroupDate", new Date());
		} else {
			List<Map<String, Object>> list = drm.getFixGroupList(empiId);
			if (list != null && list.size() > 0) {
				reqBody.put("fixGroupDate",list.get(list.size() - 1).get("fixDate"));
			} else {
				reqBody.put("fixGroupDate", new Date());
			}
		}
		reqBody.put("adverseReactions", body.get("adverseReactions"));
		reqBody.put("fbs", body.get("fbs"));
		reqBody.put("complicationChange", body.get("complicationChange"));
		reqBody.put("nextDate", body.get("nextDate"));
		reqBody.put("visitDate", body.get("visitDate"));
		reqBody.put("lastVisitDate", body.get("visitDate"));
		reqBody.put("lastPlanDate", body.get("planDate"));

		Map<String, Object> diabetesRecord = null;
		try {
			diabetesRecord = drm.getDiabetesRecordByPkey((String) body.get("phrId"));
		} catch (ModelDataOperationException e1) {
			throw new ServiceException("糖尿病记录获取失败。");
		}
		if (diabetesRecord != null) {
			reqBody.put("taskDoctorId", diabetesRecord.get("manaDoctorId"));
		}
		if (needReferral == true) {
			reqBody.put("visitResult", VisitResult.DISSATISFIED);
		} else {
			reqBody.put("visitResult", VisitResult.SATISFIED);
		}
		updateFields.put("visitResult", reqBody.get("visitResult"));
		try {
			//更新随访计划
			dvm.updateVisitPlanPartInfo(planId, visitEffect, updateFields);
		} catch (ModelDataOperationException e) {
			logger.error("Update data of PUB_VisitPlan with planId = " + planId+ " failed.", e);
			throw new ServiceException(e);
		}
		if (body.get("needChangeGroup") != null
				&& (Boolean) body.get("needChangeGroup") == true) {
			try {
				// 糖尿病随访转组生成
				drm.deleteNoVisitPlan((String)reqBody.get("empiId"),(Date)reqBody.get("fixGroupDate"));
				dvm.insertGroupRecord(body, reqBody, visitPlanCreator, ctx);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to create diabetes visit plan.", e);
				throw new ServiceException(e);
			}
		}
		if (visitEffect.equals(VisitEffect.END)) {
			// 注销糖尿病档案
			//yx-客户要求不注销档案
//			try {
//				drm.logoutDiabetesRecord((String) body.get("phrId"),(String) body.get("noVisitReason"), "");
//			} catch (ModelDataOperationException e) {
//				logger.error("Logout hypertension record failed.", e);
//				throw new ServiceException(e);
//			}
			// 更新糖尿病档案转归
			try {
				HashMap<String, Object> endBody = new HashMap<String, Object>();
				endBody.put("phrId", body.get("phrId"));
				endBody.put("visitEffect", visitEffect);
				endBody.put("noVisitReason", body.get("noVisitReason"));
				endBody.put("visitDate", visitDate);
				drm.setDiabetesRecordEnd(endBody);
			} catch (ModelDataOperationException e) {
				logger.error("Logout hypertension record failed.", e);
				throw new ServiceException(e);
			}
			// **注销未执行过的随访计划
			VisitPlanModel vpModel = new VisitPlanModel(dao);
			try {
				String[] businessTypes = { BusinessType.TNB };
				vpModel.logOutVisitPlan(vpModel.RECORDID,(String) body.get("phrId"), businessTypes);
			} catch (ModelDataOperationException e) {
				logger.error("Logout diabetses visit plan failed.", e);
				throw new ServiceException(e);
			}
			return;
		}
		if (body.get("needInsertPlan") != null && (Boolean) body.get("needInsertPlan") == true
				&& !visitMeddle.equals("0"))
		{
			if(body.containsKey("needdoublevisit") && "2".equals(body.get("needdoublevisit")+"") )
			{
				//不需要增加二次随访
				if(diaVisitModel.checkHasMeddlePlanInTwoWeek(empiId,thisplanDate)==true){
					diaVisitModel.deleteMeddlePlanInTwoWeek(empiId,thisplanDate);
				}
			}else{
				reqBody.put("visitMeddle", visitMeddle);
				try {
					// 糖尿病随访计划生成
					dvm.insertVisitPlan(reqBody);
				} catch (ModelDataOperationException e) {
					logger.error("Failed to create diabetes visit plan.", e);
					throw new ServiceException(e);
				}
			}
		}else if(diaVisitModel.checkHasMeddlePlanInTwoWeek(empiId,thisplanDate)==true){
			if(body.get("controlBad") != null && !(Boolean) body.get("controlBad") == true)
			diaVisitModel.deleteMeddlePlanInTwoWeek(empiId,thisplanDate);
		}

		// 高血压高危需求
		boolean blnConstriction = !BSCHISUtil.isBlank(body.get("constriction"));
		boolean blnDiastolic = !BSCHISUtil.isBlank(body.get("diastolic"));
		if (blnConstriction || blnDiastolic) {
			if ((blnConstriction
					&& Integer.parseInt(body.get("constriction") + "") >= 120 && Integer
					.parseInt(body.get("constriction") + "") <= 139)
					|| (blnDiastolic
							&& Integer.parseInt(body.get("diastolic") + "") >= 80 && Integer
							.parseInt(body.get("diastolic") + "") <= 89)) {
				HypertensionRiskModel hrm = new HypertensionRiskModel(dao);
				hrm.insertHypertensionRisk(empiId, (String) body.get("phrId"),
						Integer.parseInt(body.get("constriction") + ""),
						Integer.parseInt(body.get("diastolic") + ""), "2");
			}
		}
		// 生成疑似记录
		if (blnConstriction || blnDiastolic) {
			if ((blnConstriction && Integer.parseInt(body.get("constriction")+ "") >= 140)
				|| (blnConstriction && Integer.parseInt(body.get("diastolic") + "") >= 90)) {
				HypertensionSimilarityModel hsm = new HypertensionSimilarityModel(dao);
				System.out.println("-------------empiId:"+empiId+"phrId:"+body.get("phrId"));
				hsm.insertHypertensionSimilarity(empiId,(String) body.get("phrId"),
						Integer.parseInt(body.get("constriction") + ""),
						Integer.parseInt(body.get("diastolic") + ""), body);
			}
		}
		//判断最后一次随访生成随访计划 
		//yx 这种判断最后一条随访来生成随访计划不适用用户漏防的情况故屏蔽
//		if(req.containsKey("app")&& req.get("app").equals("1")){
//			
//		}else{
//		if(dvm.checklastvisitplan(planId, empiId)){
//			HashMap<String, Object> fixJsonReq = new HashMap<String, Object>();
//			fixJsonReq.put("phrId", body.get("phrId"));
//			fixJsonReq.put("fixType", "1");
//			fixJsonReq.put("instanceType", BusinessType.TNB);
//			fixJsonReq.put("empiId", reqBody.get("empiId"));
//			fixJsonReq.put("groupCode", body.get("oldGroup"));
//			fixJsonReq.put("oldGroup", body.get("oldGroup"));
//			String planDateyear=planDate.substring(0,4);
//			Date day=new Date();
//			String day_s=BSCHISUtil.toString(day,null);
//            Calendar nextplanday = Calendar.getInstance();//时间变量
//            nextplanday.setTime(day);
//			if(planDateyear.equals(day_s.substring(0,4))){
//				nextplanday.setTime(BSCHISUtil.toDate(planDate));
//				nextplanday.add(nextplanday.MONTH, 3);
//			}else{
//				nextplanday.setTime(day);
//			}
//			fixJsonReq.put("fixGroupDate", nextplanday.getTime());
//			fixJsonReq.put("fbs", body.get("fbs"));
//			fixJsonReq.put("nextDate",
//					BSCHISUtil.toString(nextplanday.getTime(), null));
//			fixJsonReq.put("adverseReactions",
//					reqBody.get("adverseReactions"));
//			fixJsonReq.put("visitResult", VisitResult.SATISFIED);
//			fixJsonReq.put("taskDoctorId", reqBody.get("manaDoctorId"));
//			fixJsonReq.put("visitMeddle", "0");
//			drm.createVisitPlan(fixJsonReq, visitPlanCreator, ctx);
//		}
//		}
	}

	public void doCheckNeedChangeGroup(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String diabetesGroup = (String) body.get("diabetesGroup");
		String diabetesType = (String) body.get("diabetesType");
		String empiId = (String) body.get("empiId");
		if (diabetesGroup == null || "".equals(diabetesGroup)
				|| "create".equals(op)) {
			List<Object> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s",
					empiId);
			try {
				List<Map<String, Object>> list = dao.doQuery(cnd,
						"fixDate desc", MDC_DiabetesFixGroup);
				if (list != null && list.size() > 0) {
					diabetesGroup = (String) list.get(0).get("diabetesGroup");
				}
			} catch (PersistentDataOperationException e) {
				logger.error("doCheckNeedChangeGroup failed", e);
				throw new ServiceException(e);
			}
		}
		String fbs = (String) body.get("fbs");
		String pbs = (String) body.get("pbs");
		String visitDate = (String) body.get("visitDate");
		String planDate = (String) body.get("planDate");
		String visitId = (String) body.get("visitId");
		DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
		try {
			Map<String, Object> reqBody = dvm.getGroupConfigPK(fbs, pbs,
					diabetesGroup, empiId, visitDate, visitId, diabetesType,planDate);
			res.put("body", reqBody);
		} catch (ModelDataOperationException e) {
			logger.error("doCheckNeedChangeGroup failed", e);
			throw new ServiceException(e);
		}
	}

	// /**
	// * 糖尿病档案注销
	// *
	// * @param req
	// * @param res
	// * @param dao
	// * @param ctx
	// * @throws ServiceException
	// */
	// @SuppressWarnings("unchecked")
	// public void doSetDiabetesRecordLogout(Map<String, Object> req,
	// Map<String, Object> res, BaseDAO dao, Context ctx)
	// throws ServiceException {
	// // 1. 更新糖尿病档案表中的“档案状态”，改为2（注销，等核实）和注销原因、注销日期
	// // 2. 跟新糖尿病档案随访计划中"未随访"的计划的“计划状态”，改为9(档案注销）
	// // 3. 保存成功，返回成功标志
	// Map<String, Object> body = (Map<String, Object>) req.get("body");
	// DiabetesRecordModel drm = new DiabetesRecordModel(dao);
	// try {
	// drm.saveDiabetesRecord("update", body, false);// 跟新糖尿病档案表
	// } catch (ModelDataOperationException e) {
	// logger.error("Failed to save MDC_DiabetesRecord.", e);
	// throw new ServiceException(e);
	// }
	// // 跟新糖尿病档案随访计划表
	// String phrId = (String) body.get("phrId");
	// try {
	// drm.SetDiabetesVisitPlanLogout(phrId);
	// } catch (ModelDataOperationException e) {
	// logger.error(
	// "Failed to update planStatus of PUB_VisitPlan with phrId ="
	// + phrId, e);
	// res.put(Service.RES_CODE, e.getCode());
	// res.put(Service.RES_MESSAGE, e.getMessage());
	// throw new ServiceException(e);
	// }
	// res.put(RES_CODE, Constants.CODE_OK);
	// res.put(RES_MESSAGE, "已经核实并注销");
	//
	// // @@ 注销后更改档案中“是否糖尿病”这个字段的值。
	// try {
	// drm.setHealthRecordIsDiabetes(phrId);
	// } catch (ModelDataOperationException e) {
	// logger.error(
	// "Failed to update EHR_HealthRecord isDiateses column.", e);
	// res.put(Service.RES_CODE, e.getCode());
	// res.put(Service.RES_MESSAGE, e.getMessage());
	// throw new ServiceException(e);
	// }
	//
	// }

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
		DiabetesVisitModel dvm = new DiabetesVisitModel(dao);

		Map<String, Object> m = null;
		try {
			// 保存糖尿病服药情况
			m = (Map<String, Object>) dvm.saveDiabetesMedicine(op, body, true);
			m.putAll(SchemaUtil.setDictionaryMessageForList(body,
					MDC_DiabetesMedicine));
			String recordId = (String) body.get("recordId");
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
	 * 保存多个糖尿病服药情况
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMedicines(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		DiabetesVisitModel dvm = new DiabetesVisitModel(dao);

		List<Map> records = (List<Map>) body.get("records");
		Map resMap = new HashMap();
		try {

			for (int i = 0; i < records.size(); i++) {
				Map record = records.get(i);

				Map<String, Object> m = null;
				// 保存糖尿病服药情况
				m = (Map<String, Object>) dvm.saveDiabetesMedicine(op, record,
						true);
				m.putAll(SchemaUtil.setDictionaryMessageForList(record,
						MDC_DiabetesMedicine));
				String recordId = (String) record.get("recordId");
				if ("create".equals(op)) {
					recordId = (String) m.get("recordId");
				}
				vLogService.saveVindicateLog(MDC_DiabetesMedicine, op,
						recordId, dao);

			}
			res.put("body", resMap);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to insert medicine for diabertes.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取糖尿病随访药品
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void doGetVisitMedine(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> r = (Map<String, Object>) req.get("r");
		DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
		List<Map<String, Object>> mList = dvm.getVisitMedicine(
				(String) r.get("recordId"), (String) r.get("visitId"));
		res.put("body", mList);
	}

	/**
	 * 获取糖尿病随访并发症
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void doGetVisitComplication(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> r = (Map<String, Object>) req.get("r");
		DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
		List<Map<String, Object>> mList = dvm.getVisitComplication(
				(String) r.get("recordId"), (String) r.get("visitId"));
		res.put("body", mList);
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
	}

	/**
	 * 获取糖尿病随访描述
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void doGetDiabetesDescription(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
		String visitId = (String) body.get("visitId");
		List<Map<String, Object>> l = dvm.getDiabetesDescription(visitId);
		if (null != l && l.size() > 0) {
			Map<String, Object> m = SchemaUtil.setDictionaryMessageForForm(
					l.get(0), MDC_DiabetesComplication);
			res.put("body", m);
		}
	}

	/**
	 * 保存糖尿病描述
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDiabetesDescription(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException, ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String recordId = (String) body.get("recordId");
		DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
		Map<String, Object> map = dvm.saveDiabetesDescription(op, body, true);
		if ("create".equals(op)) {
			recordId = (String) map.get("recordId");
		}
		vLogService.saveVindicateLog(MDC_DiabetesVisitDescription, op,
				recordId, dao);
		res.put("body", res.put("body", SchemaUtil.setDictionaryMessageForForm(
				map, MDC_DiabetesVisitDescription)));
	}

	/**
	 * 获取糖尿病随访健康教育
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void doGetDiabetesHealthTeach(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
		String wayId = (String) body.get("wayId");
		Map<String, Object> l = dvm.getDiabetesHealthTeach(wayId);
		res.put("body", l);

	}

	/**
	 * 保存糖尿病健康教育
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDiabetesHealthTeach(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException, ServiceException {
		
		//传日志到大数据接口（健康宣教） --wdl
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
			"\"apiCode\":\"JKXJ\",\n"+
			"\"operSystemCode\":\"ehr\",\n"+
			"\"operSystemName\":\"健康档案系统\",\n"+
			"\"fromDomain\":\"ehr_yy\",\n"+
			"\"toDomain\":\"ehr_mb\",\n"+
			"\"clientAddress\":\""+ipc+"\",\n"+
			"\"serviceBean\":\"esb.JKXJ\",\n"+
			"\"methodDesc\":\"void doSaveDiabetesVisit()\",\n"+
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
				
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String recordId = (String) body.get("id");
		DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
		Map<String, Object> map = dvm.saveDiabetesHealthTeach(op, body,
				vLogService);
		res.put("body", map);
	}

	// /**
	// * 加载糖尿病随访
	// *
	// * @param req
	// * @param res
	// * @param dao
	// * @param ctx
	// * @throws ServiceException
	// * @throws ParseException
	// */
	// @SuppressWarnings("unchecked")
	// public void doLoadVisitData(Map<String, Object> req,
	// Map<String, Object> res, BaseDAO dao, Context ctx) throws
	// ModelDataOperationException, PersistentDataOperationException,
	// ServiceException{
	// Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
	// Map<String, Object> r = (Map<String, Object>) reqBody.get("r");
	//
	// Map<String, Object> mReq = new HashMap<String, Object>();
	// Map<String, Object> mBody = new HashMap<String, Object>();
	// mReq.put("body", mBody);
	// mBody.put("planId", r.get("planId"));
	// mBody.put("empiId", r.get("empiId"));
	// mBody.put("businessType", BusinessType.TNB);
	// this.doGetMinStep(mReq, res, dao, ctx);
	//
	// DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
	// Map<String, Object> visitMap =
	// SchemaUtil.setDictionaryMessageForForm(dvm.getDiabetesVisitByPkey((String)r.get("visitId")),
	// MDC_DiabetesVisit);
	//
	// DiabetesRecordModel drm = new DiabetesRecordModel(dao);
	// Map<String, Object> record =
	// drm.getDiabetesRecordByPkey((String)r.get("recordId"));
	// visitMap.put("height", record.get("height"));
	//
	// Map<String, Object> map = dvm.getNextVisitPlan((String)r.get("empiId"),
	// BusinessType.TNB, (String)r.get("planId"));
	// visitMap.put("nextPlan", map);
	// ControlRunner.run(MDC_DiabetesVisit, visitMap, ctx,
	// ControlRunner.UPDATE);
	// res.put("body", visitMap);
	//
	// }
	//
	// @SuppressWarnings("unchecked")
	// public void doInitializeVisit(Map<String, Object> req,
	// Map<String, Object> res, BaseDAO dao, Context ctx) throws
	// ModelDataOperationException, PersistentDataOperationException,
	// ServiceException{
	// Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
	// Map<String, Object> r = (Map<String, Object>) reqBody.get("r");
	//
	// Map<String, Object> mReq = new HashMap<String, Object>();
	// Map<String, Object> mBody = new HashMap<String, Object>();
	// mReq.put("body", mBody);
	// mBody.put("planId", r.get("planId"));
	// mBody.put("empiId", r.get("empiId"));
	// mBody.put("businessType", BusinessType.TNB);
	// this.doGetMinStep(mReq, res, dao, ctx);
	//
	// DiabetesRecordModel drm = new DiabetesRecordModel(dao);
	// DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
	// Map<String, Object> resBody = (Map<String, Object>) res.get("body");
	// //获取档案
	// Map<String, Object> record =
	// SchemaUtil.setDictionaryMessageForForm(drm.getDiabetesRecordByPkey((String)r.get("recordId")),
	// MDC_DiabetesRecord);
	// //前一次随访
	// Map<String, Object> preVisit =
	// dvm.getPerRecord((String)r.get("empiId"),(String)r.get("beginDate"));
	// //个人生活习惯
	// LifeStyleModel lsm = new LifeStyleModel(dao);
	// Map<String, Object> lifeMap =
	// lsm.getLifeStyleByEmpiId((String)r.get("empiId"));
	//
	// Map<String, Object> map = dvm.getNextVisitPlan((String)r.get("empiId"),
	// BusinessType.TNB, (String)r.get("planId"));
	//
	// if(null != preVisit){
	// resBody.put("targetWeight", preVisit.get("targetWeight"));
	// resBody.put("targetSmokeCount", preVisit.get("targetSmokeCount"));
	// resBody.put("targetDrinkCount", preVisit.get("targetDrinkCount"));
	// resBody.put("targetTrainTimesWeek",
	// preVisit.get("targetTrainTimesWeek"));
	// resBody.put("targetTrainMinute", preVisit.get("targetTrainMinute"));
	// resBody.put("targetFood", preVisit.get("targetFood"));
	// }
	// if(null != lifeMap){
	// resBody.put("smokeCount", lifeMap.get("smokeCount"));
	// resBody.put("drinkCount", lifeMap.get("drinkCount"));
	// resBody.put("drinkTypeCode", lifeMap.get("drinkTypeCode"));
	// }
	//
	// resBody.put("height", record.get("height"));
	// resBody.put("diabetesGroup", record.get("diabetesGroup"));
	// resBody.put("nextPlan", map);
	// ControlRunner.run(MDC_DiabetesVisit, map, ctx, ControlRunner.CREATE);
	// res.put("body", resBody);
	// }

	/**
	 * 获取 糖尿病 随访信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetPerRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String phrId = (String) req.get("phrId");
		String startDate = (String) req.get("visitDate");

		List<Object> strtCol = (List<Object>) CNDHelper.createArrayCnd("$",
				"str(visitDate,'yyyy-MM-dd')");
		List<Object> strtVal = (List<Object>) CNDHelper.createArrayCnd("s",
				startDate);
		List<Object> strtExp = (List<Object>) CNDHelper.createArrayCnd("lt",
				strtCol, strtVal);

		List<Object> typeCol = (List<Object>) CNDHelper.createArrayCnd("$",
				"a.phrId");
		List<Object> typeValue = (List<Object>) CNDHelper.createArrayCnd("s",
				phrId);
		List<Object> typeExp = (List<Object>) CNDHelper.createArrayCnd("eq",
				typeCol, typeValue);

		List<Object> cnd = (List<Object>) CNDHelper.createArrayCnd("and",
				typeExp, strtExp);

		req.put("sortInfo", " visitDate asc ");
		req.put("cnd", cnd);

		DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
		try {
			// 查询糖尿病随访信息
			res = dvm.pageQueryList(req);
		} catch (ModelDataOperationException e) {
			logger.error("Select MDC_DiabetesVisit failed.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存糖尿病并发症信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDiabetesComplications(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		String visitId = (String) body.get("visitId");

		DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
		try {
			dvm.deleteDiabetesComplication(phrId, visitId);
			vLogService.saveVindicateLog(MDC_DiabetesComplication, "4",
					visitId, dao);
		} catch (ModelDataOperationException e) {
			logger.error("Delete MDC_DiabetesComplication with phrId = "
					+ phrId + " and visitId = " + visitId, e);
			throw new ServiceException(e);
		}

		List<Map<String, Object>> array = (List<Map<String, Object>>) req
				.get("array");
		Map<String, Object> bodyMap = new HashMap<String, Object>();
		bodyMap.put("op", "create");
		if (!"".equals(array) && array != null && array.size() > 0) {
			for (int i = 0; i < array.size(); i++) {
				bodyMap = (Map<String, Object>) array.get(i);
				Map<String, Object> rsMap = null;
				try {
					// 保存糖尿病并发症信息
					rsMap = dvm.saveDiabetesComplication("create", bodyMap,
							false);
				} catch (ModelDataOperationException e) {
					logger.error("Save MDC_DiabetesComplication failed.", e);
					throw new ServiceException(e);
				}
				String complicationId = (String) rsMap.get("complicationId");
				vLogService.saveVindicateLog(MDC_DiabetesComplication, "1",
						complicationId, dao);
				bodyMap = null;
			}
		}

	}

	/**
	 * 获取上年度的随访计划
	 */
	public void doGetPreYearVisitPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = StringUtils.trimToEmpty((String) req.get("empiId"));
		int current = (Integer) req.get("current");
		String instanceType = BusinessType.TNB;
		List<Map<String, Object>> rsList = null;
		try {
			VisitPlanModel vpm = new DiabetesVisitPlanModel(dao);
			rsList = vpm.getVisitPlan(1, empiId, current, instanceType);
			res.put("body", rsList);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get visit plan of current.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取糖尿病上年度随访计划失败！");
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取本年度的随访计划
	 */
	public void doGetCurYearVisitPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = StringUtils.trimToEmpty((String) req.get("empiId"));
		int current = 0;
		String instanceType = BusinessType.TNB;
		List<Map<String, Object>> rsList = null;
		try {
			VisitPlanModel vpm = new DiabetesVisitPlanModel(dao);
			rsList = vpm.getVisitPlan(2, empiId, current, instanceType);
			res.put("body", rsList);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get visit plan of current.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取糖尿病本年度随访计划失败！");
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取下年度的随访计划
	 */
	public void doGetNextYearVisitPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = StringUtils.trimToEmpty((String) req.get("empiId"));
		int current = (Integer) req.get("current");
		String instanceType = BusinessType.TNB;
		List<Map<String, Object>> rsList = null;
		try {
			VisitPlanModel vpm = new DiabetesVisitPlanModel(dao);
			rsList = vpm.getVisitPlan(3, empiId, current, instanceType);
			res.put("body", rsList);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get visit plan of current.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取糖尿病下年度随访计划失败！");
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doInitialize(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException,
			PersistentDataOperationException, ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		int index = (Integer) body.get("selectedIndex");
		Map<String, Object> ids = (Map<String, Object>) body.get("ids");
		Map<String, Object> resBody = new HashMap<String, Object>();

		String empiId = (String) ids.get("empiId");
		String instanceType = BusinessType.TNB;
		int current = 0;
		int yearType = 2;
		if (body.get("requestData") != null) {
			current = (Integer) ((Map<String, Object>) body.get("requestData"))
					.get("current");
			yearType = (Integer) ((Map<String, Object>) body.get("requestData"))
					.get("yearType");
		}
		VisitPlanModel vpm = new VisitPlanModel(dao);
		List<Map<String, Object>> rsList = vpm.getVisitPlan(yearType, empiId,
				current, instanceType);
		resBody.put(PUB_VisitPlan + Constants.DATAFORMAT4LIST, rsList);
		Map<String, Object> plan = null;
		if (rsList.size() > 0 && rsList.size() >= index) {
			plan = rsList.get(index);
			Map<String, Object> mReq = new HashMap<String, Object>();
			mReq.put("body", plan);
			mReq.put("empiData", body.get("empiData"));
			Map<String, Object> visitMap = this
					.getFormData(mReq, res, dao, ctx);
			resBody.put(MDC_DiabetesVisit + Constants.DATAFORMAT4FORM, visitMap);
		}
		res.put("body", resBody);
	}

	@SuppressWarnings("unchecked")
	public void doInitializeForm(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> resBody = new HashMap<String, Object>();
		DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
		Map<String, Object> visitMap = this.getFormData(req, res, dao, ctx);
		resBody.put(MDC_DiabetesVisit + Constants.DATAFORMAT4FORM, visitMap);

		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String planId = (String) body.get("planId");
		VisitPlanModel vpm = new VisitPlanModel(dao);
		String lastPlanId = (String) vpm.getLastVisitedPlanId(
				(String) body.get("recordId"), BusinessType.TNB);
		if (lastPlanId != null && lastPlanId.equals(planId)) {
			resBody.put("isLastVisitedPlan", true);
		} else {
			resBody.put("isLastVisitedPlan", false);
		}
		// 取药品信息
		String visitId = (String) body.get("visitId");
		List<Map<String, Object>> vmList = dvm.getHtmlVisitMedicine(visitId);
		resBody.put(MDC_DiabetesMedicine + Constants.DATAFORMAT4LIST, vmList);
		res.put("body", resBody);
		// 取档案病例种类
		DiabetesRecordModel drm = new DiabetesRecordModel(dao);
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		List<Map<String, Object>> record = drm
				.findDiabetesRecordByEmpiId((String) reqBody.get("empiId"));
		if (record != null && record.size() > 0) {
			res.put("diabetesType", record.get(0).get("diabetesType"));
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getFormData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> plan = (Map<String, Object>) req.get("body");

		if (!plan.get("visitId").equals("")) {

			DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
			Map<String, Object> visitMap = SchemaUtil
					.setDictionaryMessageForForm(dvm
							.getDiabetesVisitByPkey((String) plan
									.get("visitId")), MDC_DiabetesVisit);

			DiabetesRecordModel drm = new DiabetesRecordModel(dao);
			Map<String, Object> record = drm
					.getDiabetesRecordByPkey((String) plan.get("recordId"));
			if (record.get("height") != null) {
				visitMap.put("height", record.get("height"));
			}

			// 下一次随访计划
			ControlRunner.run(MDC_DiabetesVisit, visitMap, ctx,
					ControlRunner.UPDATE);
			return visitMap;
		} else {

			DiabetesRecordModel drm = new DiabetesRecordModel(dao);
			DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
			Map<String, Object> initMap = new HashMap<String, Object>();

			initMap.put("empiId", plan.get("empiId"));
			// 获取档案
			Map<String, Object> record = SchemaUtil
					.setDictionaryMessageForForm(drm
							.getDiabetesRecordByPkey((String) plan
									.get("recordId")), MDC_DiabetesRecord);
			// 前一次随访
			Map<String, Object> preVisit = dvm.getPerRecord(
					(String) plan.get("empiId"), plan.get("beginDate"));
			// 个人生活习惯
			LifeStyleModel lsm = new LifeStyleModel(dao);
			Map<String, Object> lifeMap = lsm
					.getLifeStyleByEmpiId((String) plan.get("empiId"));

			if (null != preVisit) {
				initMap.put("targetWeight", preVisit.get("targetWeight"));
				initMap.put("targetSmokeCount",
						preVisit.get("targetSmokeCount"));
				initMap.put("targetDrinkCount",
						preVisit.get("targetDrinkCount"));
				initMap.put("targetTrainTimesWeek",
						preVisit.get("targetTrainTimesWeek"));
				initMap.put("targetTrainMinute",
						preVisit.get("targetTrainMinute"));
				initMap.put("targetFood", preVisit.get("targetFood"));
			} else {
				if (req.get("empiData") != null) {
					if (((Map<String, Object>) req.get("empiData")).get(
							"sexCode").equals(Gender.MAN)) {
						initMap.put("targetWeight",
								(Double) record.get("height") - 105);
					} else if (((Map<String, Object>) req.get("empiData")).get(
							"sexCode").equals(Gender.WOMEN)) {
						initMap.put("targetWeight",
								(Double) record.get("height") - 110);
					}
				}
			}
			if (null != lifeMap) {
				initMap.put("smokeCount", lifeMap.get("smokeCount"));
				initMap.put("drinkCount", lifeMap.get("drinkCount"));
				initMap.put("drinkTypeCode", lifeMap.get("drinkTypeCode"));
			}

			initMap.put("height", record.get("height"));
			initMap.put("diabetesGroup", record.get("diabetesGroup"));
			initMap.put("diabetesType", record.get("diabetesType"));
			ControlRunner.run(MDC_DiabetesVisit, initMap, ctx,
					ControlRunner.CREATE);
			return initMap;
		}
	}

	/**
	 * 糖尿病随访纸质页面 服药信息获取
	 */
	public void doGetMedineName(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		Map<String, Object> pam = new HashMap<String, Object>();
		String visitId = req.get("body") + "";
		pam.put("visitId", visitId);
		String sb = " select * from (select b.mdcuse as MDCUSE,a.recordid as RECORDID,b.drugname as DRUGNAME, b.drugcode as DRUGCODE,a.visitId AS VISITID, a.medicinefrequency MEDICINEFREQUENCY,a.medicinedosage MEDICINEDOSAGE from  MDC_DiabetesMedicine a  "
				+ " ,PUB_DrugDirectory b "
				+ " where   a.medicinename=b.drugcode and b.mdcUse!='4' and a.visitId=:visitId  order by  a.RECORDID desc) where ROWNUM <= 3";
		String yds = " select * from (select b.mdcuse as MDCUSE,a.recordid as RECORDID,b.drugname as DRUGNAME, b.drugcode as DRUGCODE,a.visitId AS VISITID, a.medicinefrequency MEDICINEFREQUENCY,a.medicinedosage MEDICINEDOSAGE from  MDC_DiabetesMedicine a  "
				+ " ,PUB_DrugDirectory b "
				+ " where   a.medicinename=b.drugcode and b.mdcUse='4' and a.visitId=:visitId  order by  a.RECORDID desc) where ROWNUM <=1";
		List<Map<String, Object>> rsMap = null;
		List<Map<String, Object>> ydsMap = null;
		List<Map<String, Object>> backMap = null;
		try {
			Map<String, Object> pam2 = new HashMap<String, Object>();
			rsMap = dao.doSqlQuery(sb.toString(), pam);
			if (!"".equals(rsMap) && rsMap != null && rsMap.size() > 0) {
				backMap = rsMap;
				int k = 3 - rsMap.size();
				for (int j = 0; j < k; j++) {
					backMap.add(0, null);
				}
			}
			ydsMap = dao.doSqlQuery(yds.toString(), pam);
			if (!"".equals(ydsMap) && ydsMap != null && ydsMap.size() > 0) {
				if (!"".equals(backMap) && backMap != null
						&& backMap.size() > 0) {
					backMap.add(3, ydsMap.get(0));
				} else {
					backMap = ydsMap;
					for (int i = 0; i < 3; i++) {
						backMap.add(0, null);
					}
				}
			}
			if (!"".equals(backMap) && backMap != null && backMap.size() > 0) {
				Map<String, Object> k = new HashMap<String, Object>();
				Map<String, Object> k1 = new HashMap<String, Object>();
				Map<String, Object> k2 = new HashMap<String, Object>();
				Map<String, Object> k3 = new HashMap<String, Object>();
				k1 = backMap.get(0);
				k2 = backMap.get(1);
				k3 = backMap.get(2);
				if (k1 == null && k2 != null && k3 != null) {
					k = k1;
					backMap.add(1, k3);
					backMap.remove(0);
					backMap.remove(2);
					backMap.add(2, k);
				}
				if (k1 == null && k2 == null && k3 != null) {
					k = k1;
					backMap.add(0, k3);
					backMap.remove(3);
				}
			}
		} catch (Exception e) {
		}
		res.put("body", backMap);
	}

	//糖尿病随访纸质页面 服药情况保存
	@SuppressWarnings("unchecked")
	public void doUpdateMedineName(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		Map<String, Object> pamCreat = new HashMap<String, Object>();
		Map<String, Object> pamUpdate = new HashMap<String, Object>();
		List<Map<String, Object>> array = (List<Map<String, Object>>) req
				.get("body");

		// initMap=l.get(0);
		// String count=initMap.get("COUNT")+"";
		// String creat =
		// "INSERT INTO  MDC_DiabetesMedicine (MEDICINEFREQUENCY,MEDICINEDOSAGE,RECORDID,DAYS) "
		// +
		// " VALUES (:MEDICINEFREQUENCY,:MEDICINEDOSAGE,:RECORDID,:DAYS) ";
		String update = "update  MDC_DiabetesMedicine set MEDICINEFREQUENCY=:MEDICINEFREQUENCY,MEDICINEDOSAGE=:MEDICINEDOSAGE "
				+ " where   RECORDID=:RECORDID ";
		List<?> rsMap = null;
		try {
			for (int i = 0; i < array.size(); i++) {
				rsMap = (List<?>) array.get(i);
				String phrId = rsMap.get(0) + "";
				String visitId = rsMap.get(1) + "";
				String RECORDID = rsMap.get(2) + "";
				String DRUGCODE = rsMap.get(3) + "";
				String MEDICINEFREQUENCY = rsMap.get(4) + "";
				int MEDICINEDOSAGE = Integer.parseInt(rsMap.get(5) + "");
				String key = rsMap.get(6) + "";
				if (key == "update" || "update".equals(key)) {
					pamUpdate.put("MEDICINEFREQUENCY", MEDICINEFREQUENCY);
					pamUpdate.put("MEDICINEDOSAGE", MEDICINEDOSAGE);
					pamUpdate.put("RECORDID", RECORDID);
					int back = dao.doUpdate(update.toString(), pamUpdate);
					req.put("back", back);
				} else {
					String pd = "select a.DRUGNAME as DRUGNAME,a.DRUGUNITS as DRUGUNITS from PUB_DrugDirectory a where a.DRUGCODE = :RECORDID";// PUB_DrugDirectory
					Map<String, Object> pamPd = new HashMap<String, Object>();
					pamPd.put("RECORDID", DRUGCODE);
					List<Map<String, Object>> ypxsList = dao.doSqlQuery(
							pd.toString(), pamPd);
					Map<String, Object> ypxs = new HashMap<String, Object>();
					if (ypxsList.size() > 0) {
						ypxs = ypxsList.get(0);
						String DRUGNAME = ypxs.get("DRUGNAME") + "";
						pamCreat.put("phrId", phrId);
						pamCreat.put("visitId", visitId);
						pamCreat.put("medicineId", DRUGCODE);
						pamCreat.put("medicineName", DRUGCODE);
						pamCreat.put("medicineFrequency", MEDICINEFREQUENCY);
						pamCreat.put("medicineDosage", MEDICINEDOSAGE);
						pamCreat.put("medicineUnit", "01");
						pamCreat.put("days", 1);
						Map<String, Object> back = dao
								.doInsert(MDC_DiabetesMedicine.toString(),
										pamCreat, true);
						req.put("back", back);
					}
				}
			}
		} catch (Exception e) {
		}
	}

	//糖尿病随访纸质页面 档案判别
	public void doGetStatus(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) {
		Map<String, Object> pam = new HashMap<String, Object>();
		String phrId = req.get("body") + "";
		pam.put("phrId", phrId);
		String sb = " select status from MDC_DiabetesRecord where phrId=:phrId";
		List<Map<String, Object>> rsMap = null;
		try {
			rsMap = dao.doSqlQuery(sb.toString(), pam);
		} catch (Exception e) {
		}
		res.put("body", rsMap);
	}

	public void doListDiabetesVistPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dvm.listDiabetesVistPlan(req);
		} catch (ModelDataOperationException e) {
			logger.error("list Hypertension Vist Plan failed.", e);
			throw new ServiceException(e);
		}
		res.putAll(rsMap);
	}
	public void doListDiabetesVistPlanQC(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dvm.listDiabetesVistPlanQC(req);
		} catch (ModelDataOperationException e) {
			logger.error("list Hypertension Vist Plan failed.", e);
			throw new ServiceException(e);
		}
		res.putAll(rsMap);
	}
	public void doDeleteDiabetesVistPlanbyplanId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
		String planId=req.get("planId")+"";
		try {
			dvm.deleteDiabetesVistPlanbyplanId(planId);
		} catch (ModelDataOperationException e) {
			logger.error("delete Hypertension Vist Plan failed.", e);
			throw new ServiceException(e);
		}
	}
	public void doGetHypertensionVisitData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body=(Map<String, Object>)req.get("body");
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "phrId", "s", body.get("phrId")+"");
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "to_char(visitDate,'yyyy-MM-dd')","s",body.get("visitDate")+"");
		List<?> cnds = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		Map<String, Object> data=new HashMap<String, Object>();
		try {
			data=dao.doLoad(cnds, MDC_HypertensionVisit);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(data!=null && data.size() > 0){
			res.put("data", data);
		}
	}
	//add-yx-根据planId删除未随访的计划
	public void doDeleteVisitPlan(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ServiceException{
		String planId=req.get("planId")==null?"":req.get("planId")+"";
		if(planId.length()>6){
			Map<String,Object> m=new HashMap<String,Object>();
			m.put("planId",planId);
			String sql="delete from PUB_VisitPlan where planId=:planId and visitId is null ";
			try{
				res.put("deletecount",dao.doUpdate(sql,m));
			}catch(PersistentDataOperationException e){
				e.printStackTrace();
			}
		}
	}
}
