package chis.source.mobilempi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.codehaus.jackson.type.TypeReference;
import org.codehaus.jackson.map.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.conf.SystemCofigManageModel;
import chis.source.dic.RelatedCode;
import chis.source.dic.YesNo;
import chis.source.empi.EmpiInterfaceImpi;
import chis.source.empi.EmpiModel;
import chis.source.empi.EmpiUtil;
import chis.source.fhr.FamilyRecordModule;
import chis.source.mdc.DiabetesRecordModel;
import chis.source.mdc.DiabetesRecordService;
import chis.source.mdc.DiabetesVisitModel;
import chis.source.mdc.DiabetesVisitService;
import chis.source.mdc.HypertensionModel;
import chis.source.mdc.HypertensionService;
import chis.source.mdc.HypertensionVisitModel;
import chis.source.mdc.HypertensionVisitService;
import chis.source.ohr.OldPeopleRecordService;
import chis.source.phr.HealthRecordModel;
import chis.source.phr.LifeStyleModel;
import chis.source.pub.PublicModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.visitplan.VisitPlanCreator;
import chis.source.visitplan.VisitPlanModel;
import com.bsoft.pix.server.EmpiIdGenerator;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.schema.DictionaryIndicator;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.security.Permission;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;
import chis.source.phr.BasicPersonalInformationModel;
public class MobileAppService extends AbstractActionService implements DAOSupportable{
	private VisitPlanCreator visitPlanCreator;
	private static final Logger logger=LoggerFactory.getLogger(MobileAppService.class);
	public static EmpiInterfaceImpi empiInterfaceImpi=null;
	public VisitPlanCreator getVisitPlanCreator(){
		return visitPlanCreator;
	}
	public void setVisitPlanCreator(VisitPlanCreator visitPlanCreator){
		this.visitPlanCreator=visitPlanCreator;
	}
	// 查询一个人近一年随访列表数据
	public void doQueryVisit(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) throws ServiceException{
		Map<String,Object> map=new HashMap<String,Object>();
		Map<String,Object> resBody=new HashMap<String,Object>();
		QueryModel query=new QueryModel(dao);
		String empiId=" ";
		if(req.containsKey("empiId")){
			empiId=(String)req.get("empiId");
		}
		String schemaId="chis.application.mobileApp.schemas.PUB_VisitPlanApp";
		map=query.query(empiId,schemaId,dao);
		resBody.put("body",map);
		res.putAll(resBody);
	}
	// 获取高血压上一次随访用药
	@SuppressWarnings("unchecked")
	public void doQueryLastVisitMedicineHy(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ServiceException{
		List<Map<String,Object>> medicines=new ArrayList<Map<String,Object>>();
		Map<String,Object> resBody=new HashMap<String,Object>();
		String empiId=" ";
		if(req.containsKey("empiId")){
			empiId=(String)req.get("empiId");
		}
		String phrId=" ";
		if(req.containsKey("phrId")){
			phrId=(String)req.get("phrId");
		}
		if(empiId==null||phrId==null){
			return;
		}
		int pageSize=1000;
		if(req.containsKey("pageSize")){
			pageSize=(Integer)req.get("pageSize");
		}
		int pageNo=1;
		if(req.containsKey("pageNo")){
			pageNo=(Integer)req.get("pageNo");
		}
		HypertensionVisitModel hvs=new HypertensionVisitModel(dao);
		HypertensionModel hm=new HypertensionModel(dao);
		try{
			List<Map<String,Object>> visitList=hvs.getLastNormalVisit(phrId);
			if(visitList!=null&&visitList.size()>0){
				Map<String,Object> visit=visitList.get(0);
				medicines=(List<Map<String,Object>>)hvs.getHyperVisitMedicineList((String)visit.get("visitId"),pageSize,
				pageNo).get("body");
			}else{
				medicines=hm.getVisitMedicine(phrId,"");
			}
		}catch(ModelDataOperationException e){
			e.printStackTrace();
		}
		resBody.put("body",medicines);
		if(medicines==null){
			resBody.put("totleCount",0);
		}else{
			resBody.put("totleCount",medicines.size());
		}
		res.putAll(resBody);
	}
	// 获取糖尿病上一次随访用药
	public void doQueryLastVisitMedicineDbs(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ServiceException{
		List<Map<String,Object>> medicines=new ArrayList<Map<String,Object>>();
		Map<String,Object> resBody=new HashMap<String,Object>();
		String empiId=" ";
		if(req.containsKey("empiId")){
			empiId=(String)req.get("empiId");
		}
		String phrId=" ";
		if(req.containsKey("phrId")){
			phrId=(String)req.get("phrId");
		}
		if(empiId==null||phrId==null){
			return;
		}
		DiabetesVisitModel dvm=new DiabetesVisitModel(dao);
		DiabetesRecordModel drm=new DiabetesRecordModel(dao);
		try{
			Map<String,Object> plan=dvm.getLastDiabetesVisitPlan(phrId);
			if(plan!=null&&"1".equals(plan.get("planStatus"))){
				medicines=dvm.getVisitMedicine(phrId,(String)plan.get("visitId"));
			}else{
				medicines=drm.getRecordMedicine(phrId);
			}
		}catch(ModelDataOperationException e){
			e.printStackTrace();
		}catch(ExpException e){
			e.printStackTrace();
		}
		resBody.put("body",medicines);
		if(medicines==null){
			resBody.put("totleCount",0);
		}else{
			resBody.put("totleCount",medicines.size());
		}
		res.putAll(resBody);
	}
	// 查询健康随访列表数据-新版掌上医生已不用
	public void doQueryVisitList(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ServiceException{
		VisitPlanModels drm=new VisitPlanModels(dao);
		Map<String,Object> rsMap=null;
		try{
			rsMap=drm.listVistPlan(req);
		}catch(ModelDataOperationException e){
			logger.error("list VisitPlan failed.",e);
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,"查询随访列表内容失败!",e);
		}
		rsMap.put("body",rsMap.get("data"));
		res.putAll(rsMap);
	}
	// 通过visitId获取随访数据
	public void doGetVisitDataByVisitId(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ServiceException{
		Map<String,Object> map=new HashMap<String,Object>();
		Map<String,Object> resBody=new HashMap<String,Object>();
		QueryModel query=new QueryModel(dao);
		String visitId=" ";
		if(req.containsKey("visitId")){
			visitId=(String)req.get("visitId");
		}
		String schemaId="";
		map=query.queryToVisitId(visitId,schemaId,dao,req);
		resBody.put("body",map);
		res.putAll(resBody);
	}
	// 查询该个人档案数据
	public void doGetPersonalHealthRecord(Map<String,Object> jsonReq,Map<String,Object> jsonRes,BaseDAO dao,Context ctx)
	throws ServiceException{
		Map<String,Object> map=new HashMap<String,Object>();
		QueryModel query=new QueryModel(dao);
		String empiId="";
		String sqlName=" ";
		Map<String,Object> mapDo=null;
		if(jsonReq.containsKey("empiId")){
			empiId=(String)jsonReq.get("empiId");
		}
		String schemaId="chis.application.mobileApp.schemas.EHR_HealthRecordApp";
		map=query.queryInfo(empiId,schemaId,dao);
		Date birthday=(Date)map.get("BIRTHDAY");
		String manaDoctorId=(String)map.get("MANADOCTORID");
		sqlName="select  a.personName from sys_personnel a where a.personId='"+manaDoctorId+"'";
		try{
			mapDo=dao.doSqlLoad(sqlName,null);
		}catch(PersistentDataOperationException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(map.get("BIRTHDAY")!=null){
			int age=MobileAppService.calculateAge(birthday,null);
			map.put("AGE",age);
		}else{
			map.put("AGE","");
		}
		if(map.get("MANADOCTORID")!=null){
			int age=MobileAppService.calculateAge(birthday,null);
			map.put("MANADOCTORID_TEXT",mapDo.get("PERSONNAME"));
		}else{
			map.put("MANADOCTORID_TEXT","");
		}
		Map<String,Object> resBody=new HashMap<String,Object>();
		resBody.put("body",map);
		jsonRes.putAll(resBody);
	}
	// 新增个人档案数据
	public void doSavePersonRecord(Map<String,Object> jsonReq,Map<String,Object> jsonRes,BaseDAO dao,Context ctx)
	throws ServiceException{
		String op="create";
		String entryName="chis.application.mpi.schemas.MPI_DemographicInfo";
		String entryName2="chis.application.hr.schemas.EHR_LifeStyle";
		String entryNameFamily="chis.application.fhr.schemas.EHR_FamilyMiddle";
		String empiId="";
		// Map<String, Object> par = new HashMap<String, Object>();
		Map<String,Object> data=(Map<String,Object>)jsonReq.get("body");
		String idCard=(String)data.get("idCard");
		Map<String,Object> records=new HashMap<String,Object>();
		try{
			if(data.get("empiId")!=null&&!"".equals(data.get("empiId"))){
				empiId=(String)data.get("empiId");
				if(getIfNeedPix(dao,ctx)){
					Map<String,Object> map=empiInterfaceImpi.updatePerson(records);
					empiId=(String)map.get("mpiId");
					String versionNumber=(String)map.get("versionNumber");
					data.put("versionNumber",versionNumber);
				}
			}else{
				if(getIfNeedPix(dao,ctx)){
					Map<String,Object> map=empiInterfaceImpi.submitPerson(records);
					empiId=(String)map.get("mpiId");
					String versionNumber=(String)map.get("versionNumber");
					data.put("versionNumber",versionNumber);
				}else{
					empiId=EmpiIdGenerator.generate();
				}
			}
			if(empiId!=null){
				data.put("empiId",empiId);
				data.put("op","create");
			}else{
				return;
			}
			records.putAll(data);
			jsonRes.put("body",data);
			String sqlMpi=" select * from  MPI_DemographicInfo a where a.idCard='"+idCard+"'";
			Map<String,Object> mapMpi=null;
			try{
				mapMpi=dao.doSqlLoad(sqlMpi,null);
			}catch(PersistentDataOperationException e1){
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(mapMpi==null||mapMpi.isEmpty()){
				dao.doInsert(entryName,data,true);
				// TODO Auto-generated catch block
			}else{
				empiId=(String)mapMpi.get("EMPIID");
				data.put("empiId",empiId);
				dao.doSave("update",MPI_DemographicInfo,data,false);
			}
			String sqlQuery=" select * from  EHR_HealthRecord a where a.empiId='"+empiId+"'";
			Map<String,Object> map=null;
			map=dao.doSqlLoad(sqlQuery,null);
			if(map!=null&&!map.isEmpty()){
				jsonRes.put("msg","改用户已经存在健康档案信息");
				return;
			}
			// 是否存在基本信息
			jsonReq.put("op","create");
			Map<String,Object> body=(HashMap<String,Object>)jsonReq.get("body");
			body.put("empiId",empiId);
			body.remove("signFlag");
			String empiId1=(String)body.get("empiId");
			String op1=(String)jsonReq.get("op");
			String status=(String)body.get("status");
			String masterFlag=(String)body.get("masterFlag");
			String relaCode=(String)body.get("relaCode");
			String regionCode=(String)body.get("regionCode");
			String phrId=(String)body.get("phrId");
			String familyId=(String)body.get("familyId");
			String areaGridType=(String)body.get("areaGridType");
			String manaUnitId=(String)body.get("manaUnitId");
			Context c=new Context();
			c.put("regionCode",regionCode);
			ctx.put("codeCtx",c);
			Map<String,Object> resBody=null;
			String createUser="";
			try{
				HealthRecordModel healthRecordModel=new HealthRecordModel(dao);
				if(YesNo.YES.equals(masterFlag)||RelatedCode.MASTER.equals(relaCode)){
					if(StringUtils.isEmpty(familyId)&&!"part".equals(areaGridType)){
						FamilyRecordModule familyModel=new FamilyRecordModule(dao);
						familyId=familyModel.getFamilyIdByRegionCode(regionCode);
					}
					if(StringUtils.isNotEmpty(familyId)){
						Map<String,Object> masterRecords=healthRecordModel.getMasterRecordByFamilyId(familyId);
						if(masterRecords!=null){
							String masterPhrId=(String)masterRecords.get("phrId");
							if(masterPhrId!=null&&!masterPhrId.equals(phrId)){
								if(!"part".equals(areaGridType)){
									String message="["+(String)masterRecords.get("regionCode_text")
									+"]地址已经存在户主,无法在同一家庭建立两个户主!";
									jsonRes.put(Service.RES_CODE,Constants.CODE_RECORD_EXSIT);
									jsonRes.put(Service.RES_MESSAGE,message);
									return;
								}
							}
						}
					}
				}
				// ****审核档案,设置状态为正常****
				data.put("status",Constants.CODE_STATUS_NORMAL);
				data.put("masterFlag",masterFlag);
				data.put("manaUnitId",manaUnitId);
				// 查找个人档案姓名
				EmpiModel empiModel=new EmpiModel(dao);
				Map<String,Object> empiInfo=empiModel.getEmpiInfoByEmpiid(empiId);
				String preferRelaCode=null;
				if("update".equals(op)){
					Map<String,Object> preferRecord=healthRecordModel.getHealthRecordByPhrId(phrId);
					preferRelaCode=(String)preferRecord.get("relaCode");
				}
				String personName=(String)empiInfo.get("personName");
				String sexCode=(String)empiInfo.get("sexCode");
				body.put("familyId",familyId);
				body.put("personName",personName);
				body.put("sexCode",sexCode);
				body.put("manaUnitId",manaUnitId);
				// 如果有家庭档案，判断是否进行户主更新。
				// 保存基本信息
				resBody=healthRecordModel.saveHealthRecord(op,data);
				phrId=(String)resBody.get("phrId");
				createUser=((Map)resBody.get("createUser")).get("key")+"";
			}catch(Exception e){
				e.printStackTrace();
			}
			data.put("phrId",phrId);
			jsonRes.put("body",data);
			LifeStyleModel lsModel=new LifeStyleModel(dao);
			String lifeStyleId=(String)body.get("lifeStyleId");
			try{
				Map<String,Object> record=lsModel.saveLifeStyle(op,data);
				if(record!=null){
					if("create".equals(op)){
						lifeStyleId=(String)record.get("lifeStyleId");
					}
					resBody.put("lifeStyleId",lifeStyleId);
					if(vLogService!=null){
						vLogService.saveVindicateLog(EHR_LifeStyle,op,lifeStyleId,dao,empiId1);
					}
				}
			}catch(ModelDataOperationException e){
				logger.error("dave lifeStyle is fail");
				throw new ServiceException(e);
			}
			saveFamilyInfo(entryNameFamily,jsonReq,dao,jsonRes);
			// yx 发送信息到app
			String toappflag="2";
			String category="100";// 个人建档
			try{
				Httpclient client=new Httpclient();
				String flag="0";
				// client.sendtofjzlapp(createUser,phrId,category);
				if(flag.equals("1")){
					toappflag="1";
				}
				QueryModel query=new QueryModel(dao);
				query.updatetoappflag(phrId,category,toappflag);
			}catch(Exception e){
				System.out.println("传到app出错！");
			}
		}catch(ModelDataOperationException e){
			throw new ServiceException("保存个人基本信息失败。",e);
		}catch(PersistentDataOperationException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// 生活环境保存
	public void saveFamilyInfo(String entryName,Map<String,Object> data,BaseDAO dao,Map<String,Object> jsonRes)
	throws ServiceException{
		Map<String,Object> body=new HashMap<String,Object>();
		body=(Map<String,Object>)data.get("data");
		try {
			dao.doSave("create", entryName, body, false);
		} catch (ValidateException e) {
			jsonRes.put("data", null);
			jsonRes.put(Service.RES_CODE, 500);
			jsonRes.put(Service.RES_MESSAGE, "fail");
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,"生活环境内容保存失败!", e);
		} catch (PersistentDataOperationException e) {
			logger.error("Save ehr_familyMiddle record failure.", e);
		}
	}
	// 更新健康档案
	public void doUpdatePersonRecord(Map<String,Object> jsonReq,Map<String,Object> jsonRes,BaseDAO dao,Context ctx)
	throws ServiceException,ModelDataOperationException{
		String op="create";
		Map<String,Object> body=(Map<String,Object>)jsonReq.get("body");
		String personName=(String)body.get("personName")==null?"":(String)body.get("personName");
		String empiId=(String)body.get("empiId")==null?"":(String)body.get("empiId");
		String birthday=(String)body.get("birthday")==null?"":(String)body.get("birthday");
		String bloodTypeCode=(String)body.get("bloodTypeCode")==null?"":(String)body.get("bloodTypeCode");
		String contact=(String)body.get("contact")==null?"":(String)body.get("contact");
		String contactPhone=(String)body.get("contactPhone")==null?"":(String)body.get("contactPhone");
		String educationCode=(String)body.get("educationCode")==null?"":(String)body.get("educationCode");
		String idCard=(String)body.get("idCard")==null?"":(String)body.get("idCard");
		String insuranceCode=(String)body.get("insuranceCode")==null?"":(String)body.get("insuranceCode");
		String manaDoctorId=(String)body.get("manaDoctorId")==null?"":(String)body.get("manaDoctorId");
		String maritalStatusCode=(String)body.get("maritalStatusCode")==null?"":(String)body.get("maritalStatusCode");
		String masterFlag=(String)body.get("masterFlag")==null?"":(String)body.get("masterFlag");
		String mobileNumber=(String)body.get("mobileNumber")==null?"":(String)body.get("mobileNumber");
		String nationCode=(String)body.get("nationCode")==null?"":(String)body.get("nationCode");
		String regionCode=(String)body.get("regionCode")==null?"":(String)body.get("regionCode");
		String regionCode_text=(String)body.get("regionCode_text")==null?"":(String)body.get("regionCode_text");
		String registeredPermanent=(String)body.get("registeredPermanent")==null?"":(String)body.get("registeredPermanent");
		String rhBloodCode=(String)body.get("rhBloodCode")==null?"":(String)body.get("rhBloodCode");
		String sexCode=(String)body.get("sexCode")==null?"":(String)body.get("sexCode");
		String smokeFreqCode=(String)body.get("smokeFreqCode")==null?"":(String)body.get("smokeFreqCode");
		String drinkFreqCode=(String)body.get("drinkFreqCode")==null?"":(String)body.get("drinkFreqCode");
		String trainFreqCode=(String)body.get("trainFreqCode")==null?"":(String)body.get("trainFreqCode");
		String eateHabit=(String)body.get("eateHabit")==null?"":(String)body.get("eateHabit");
		String workCode=(String)body.get("workCode")==null?"":(String)body.get("workCode");
		String incomeSource=(String)body.get("incomeSource")==null?"":(String)body.get("incomeSource");
		String isAgrRegister=(String)body.get("isAgrRegister")==null?"":(String)body.get("isAgrRegister");
		// EmpiModel empiModel = new EmpiModel(dao);
		Map<String,Object> par=new HashMap<String,Object>();
		List<?> cnd=CNDHelper.createSimpleCnd("eq","a.empiId","s",empiId);
		List<Map<String,Object>> info;
		try{
			info=dao.doList(cnd,null,EHR_LifeStyle);
		}catch(PersistentDataOperationException e){
			logger.error("failed to get child base message.",e);
			throw new ModelDataOperationException(Constants.CODE_DATABASE_ERROR,"获取基本信息失败。");
		}
		LifeStyleModel lsModel=new LifeStyleModel(dao);
		String lifeStyleId=(String)body.get("lifeStyleId");
		Map<String,Object> resBody=new HashMap<String,Object>();
		if(info.size()==0){
			try{
				Map<String,Object> record=lsModel.saveLifeStyle(op,body);
				if("create".equals(op)){
					lifeStyleId=(String)record.get("lifeStyleId");
				}
				resBody.put("lifeStyleId",lifeStyleId);
				if(vLogService!=null){
					vLogService.saveVindicateLog(EHR_LifeStyle,op,lifeStyleId,dao,empiId);
				}
			}catch(ModelDataOperationException e){
				logger.error("dave lifeStyle is fail");
				throw new ServiceException(e);
			}
		}
		String sql="update  MPI_DemographicInfo set birthday=to_date('"+birthday+"','yyyy-MM-dd'),idCard='"+idCard
		+"',personName='"+personName+"',bloodTypeCode='"+bloodTypeCode+"',"+"contact='"+contact+"',contactPhone='"
		+contactPhone+"',educationCode='"+educationCode+"'"+",insuranceCode='"+insuranceCode+"',maritalStatusCode='"
		+maritalStatusCode+"',mobileNumber='"+mobileNumber+"',nationCode='"+nationCode+"',registeredPermanent='"
		+registeredPermanent+"',"+"rhBloodCode='"+rhBloodCode+"',sexCode='"+sexCode+"',workCode='"+workCode
		+"' where empiId='"+empiId+"'";
		try{
			dao.doUpdate(sql,par);
		}catch(PersistentDataOperationException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sqls="update EHR_HealthRecord set manaDoctorId='"+manaDoctorId+"',masterFlag='"+masterFlag+"',regionCode='"
		+regionCode+"',regionCode_text='"+regionCode_text+"',isAgrRegister='"+isAgrRegister+"',incomeSource='"+incomeSource
		+"'"+" where empiId='"+empiId+"'";
		try{
			dao.doUpdate(sqls,par);
		}catch(PersistentDataOperationException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sqlXg="update EHR_LifeStyle set smokeFreqCode='"+smokeFreqCode+"',drinkFreqCode='"+drinkFreqCode
		+"',trainFreqCode='"+trainFreqCode+"',eateHabit='"+eateHabit+"' where empiId='"+empiId+"'";
		try{
			dao.doUpdate(sqlXg,par);
		}catch(PersistentDataOperationException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Map<String,Object>> faMap=null;
		try{
			faMap=dao.doList(cnd,null,EHR_FamilyMiddle);
		}catch(PersistentDataOperationException e){
			logger.error("failed to get child base message.",e);
			throw new ModelDataOperationException(Constants.CODE_DATABASE_ERROR,"获取生活环境失败。");
		}
		if(faMap!=null){
			String cookAirTool=(String)body.get("cookAirTool");
			String fuelType=(String)body.get("fuelType");
			String livestockColumn=(String)body.get("livestockColumn");
			String washroom=(String)body.get("washroom");
			String waterSourceCode=(String)body.get("waterSourceCode");
			String sqlHJ="update EHR_FamilyMiddle set cookAirTool='"+cookAirTool+"',fuelType='"+fuelType
			+"',livestockColumn='"+livestockColumn+"',washroom='"+washroom+"',waterSourceCode='"+waterSourceCode
			+"' where empiId='"+empiId+"'";
			try{
				dao.doUpdate(sqlHJ,par);
			}catch(PersistentDataOperationException e1){
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else{
			saveFamilyInfo("chis.application.fhr.schemas.EHR_FamilyMiddle",jsonReq,dao,jsonRes);
		}
	}
	// 保存个人健康档案
	public void doSaveHealthRecord(Map<String,Object> req,Map<String,Object> jsonRes,BaseDAO dao,Context ctx)
	throws ServiceException{
	}
	public static boolean getIfNeedPix(BaseDAO dao,Context ctx) throws ModelDataOperationException{
		SystemCofigManageModel scmm=new SystemCofigManageModel(dao);
		String ifNeedPix=scmm.getSystemConfigData("ifNeedPix");
		if("true".equals(ifNeedPix)){
			if(EmpiUtil.empiInterfaceImpi==null){
				EmpiInterfaceImpi empiInterfaceImpi=new EmpiInterfaceImpi(ctx);
				EmpiUtil.empiInterfaceImpi=empiInterfaceImpi;
			}
			return true;
		}
		return false;
	}
	// 保存随访记录（结合高血压，糖尿病，老年人）
	protected void doSaveVisitData(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ServiceException{
		Map<String,Object> body=(Map<String,Object>)req.get("body");
		if(body==null){
			return;
		}
		String planId_LNR=(String)body.get("planId_LNR");
		String planId_GXY=(String)body.get("planId_GXY");
		String planId_TNB=(String)body.get("planId_TNB");
		String[] planIds={planId_LNR,planId_GXY,planId_TNB};
		Map<String,Object> schemaIds=new HashMap<String,Object>();
		schemaIds.put(planId_LNR,BSCHISEntryNames.MDC_OldPeopleVisit);
		schemaIds.put(planId_GXY,BSCHISEntryNames.MDC_HypertensionVisit);
		schemaIds.put(planId_TNB,BSCHISEntryNames.MDC_DiabetesVisit);
		Map<String,Object> ops=new HashMap<String,Object>();
		VisitPlanModel vpm=new VisitPlanModel(dao);
		Map<String,Object> planInfo=null;
		Map<String,Object> allSaveData=new HashMap<String,Object>();
		try{
			for(int j=0;j<planIds.length;j++){
				String planId=planIds[j];
				if(planId==null||planId.length()==0||"null".equals(planId)){
					continue;
				}
				planInfo=vpm.getPlan(planId);
				String schemaId=(String)schemaIds.get(planId);
				ops.put(schemaId,"create");
				if(planInfo.get("visitId")!=null&&!"".equals(planInfo.get("visitId"))){
					ops.put(schemaId,"update");
				}
				Schema sc=SchemaController.instance().get(schemaId);
				List<SchemaItem> items=sc.getItems();
				for(SchemaItem it:items){
					String itemId=it.getId();
					Object value=body.get(itemId);
					if(value!=null){
						planInfo.put(itemId,value);
					}
				}
				allSaveData.put(schemaId,planInfo);
			}
			if(allSaveData.containsKey(BSCHISEntryNames.MDC_OldPeopleVisit)){
				Map<String,Object> saveData=(Map<String,Object>)allSaveData.get(BSCHISEntryNames.MDC_OldPeopleVisit);
				saveData.put("food",(String)body.get("foodLNR"));
				saveData.put("sbp",(Integer)body.get("constriction"));
				saveData.put("dbp",(Integer)body.get("diastolic"));
				saveData.put("visitType",(String)body.get("visitWay"));
				req.put("body",saveData);
				req.put("op",ops.get(BSCHISEntryNames.MDC_OldPeopleVisit));
				OldPeopleRecordService oprs=new OldPeopleRecordService();
				oprs.setVisitPlanCreator(visitPlanCreator);
				Map<String,Object> LNRRes=new HashMap<String,Object>();
				oprs.doSaveOldPeopleVisitRecord(req,LNRRes,dao,ctx);
				res.put("body_LNR",LNRRes);
			}
			if(allSaveData.containsKey(BSCHISEntryNames.MDC_HypertensionVisit)){
				Map<String,Object> saveData=(Map<String,Object>)allSaveData.get(BSCHISEntryNames.MDC_HypertensionVisit);
				if(!body.containsKey("visitEvaluate")){
					saveData.put("visitEvaluate",(String)body.get("visitType"));
				}
				if(body.containsKey("hypertensionMedicines")){
					saveData.put("hypertensionMedicines",(List)body.get("hypertensionMedicines"));
				}
				String empiId=(String)body.get("empiId");
				String heightHy="select height as height from MDC_HypertensionRecord where empiId='"+empiId+"'";
				Map<String,Object> mapHy=null;
				try{
					mapHy=dao.doSqlLoad(heightHy,null);
				}catch(PersistentDataOperationException e1){
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(mapHy!=null){
					BigDecimal height=(BigDecimal)mapHy.get("HEIGHT");
					Double heiNum=height.doubleValue();
					Double weiNum=Double.parseDouble(body.get("weight")+"");
					Double bmi=weiNum/(2*heiNum/100);
					saveData.put("bmi",bmi);
				}
				req.put("body",saveData);
				req.put("op",ops.get(BSCHISEntryNames.MDC_HypertensionVisit));
				HypertensionVisitService hvs=new HypertensionVisitService();
				hvs.setVisitPlanCreator(visitPlanCreator);
				Map<String,Object> GXYRes=new HashMap<String,Object>();
				hvs.doSaveHypertensionVisit(req,GXYRes,dao,ctx);
				// yx 发送信息到app
				String toappflag="2";
				String category="201";// 高血压随访
				Map<String,Object> GXYbody=new HashMap<String,Object>();
				try{
					Httpclient client=new Httpclient();
					GXYbody=(HashMap<String,Object>)GXYRes.get("body");
					if(GXYbody.containsKey("visitId")){
						String flag="0";
						// client.sendtofjzlapp(GXYbody.get("inputUser").toString(),GXYbody.get("visitId").toString(),category);
						if(flag.equals("1")){
							toappflag="1";
						}
					}
				}catch(Exception e){
					System.out.println("传到app出错！");
				}
				if(GXYbody.containsKey("visitId")){
					QueryModel query=new QueryModel(dao);
					query.updatetoappflag(GXYbody.get("visitId").toString(),category,toappflag);
				}
				res.put("body_GXY",GXYRes);
			}
			if(allSaveData.containsKey(BSCHISEntryNames.MDC_DiabetesVisit)){
				Map<String,Object> saveData=(Map<String,Object>)allSaveData.get(BSCHISEntryNames.MDC_DiabetesVisit);
				if(body.get("diabetesMedicines")!=null){
					saveData.put("diabetesMedicines",(List)body.get("diabetesMedicines"));
				}
				String empiId=(String)body.get("empiId");
				String heightDi="select height as height from MDC_DiabetesRecord where empiId='"+empiId+"'";
				Map<String,Object> mapDi=null;
				try{
					mapDi=dao.doSqlLoad(heightDi,null);
				}catch(PersistentDataOperationException e1){
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(mapDi!=null){
					BigDecimal height=(BigDecimal)mapDi.get("HEIGHT");
					Double heiNum=height.doubleValue();
					Double weiNum=Double.parseDouble(body.get("weight")+"");
					Double bmi=weiNum/(2*heiNum/100);
					saveData.put("bmi",bmi);
				}
				req.put("body",saveData);
				req.put("app","1");
				req.put("op",ops.get(BSCHISEntryNames.MDC_DiabetesVisit));
				DiabetesVisitService dvs=new DiabetesVisitService();
				dvs.setVisitPlanCreator(visitPlanCreator);
				Map<String,Object> TNBRes=new HashMap<String,Object>();
				dvs.doSaveDiabetesVisit(req,TNBRes,dao,ctx);
				HashMap<String,Object> returnmsg=(HashMap<String,Object>)res.get("body");
				// yx 发送信息到app
				String toappflag="2";
				String category="202";// 糖尿病随访
				Map<String,Object> TNBbody=new HashMap<String,Object>();
				try{
					Httpclient client=new Httpclient();
					TNBbody=(HashMap<String,Object>)TNBRes.get("body");
					if(TNBbody.containsKey("visitId")){
						String flag="0";
						// client.sendtofjzlapp(TNBbody.get("inputUser").toString(),TNBbody.get("visitId").toString(),category);
						if(flag.equals("1")){
							toappflag="1";
						}
					}
				}catch(Exception e){
					System.out.println("传到app出错！");
				}
				if(TNBbody.containsKey("visitId")){
					QueryModel query=new QueryModel(dao);
					query.updatetoappflag(TNBbody.get("visitId").toString(),category,toappflag);
				}
				res.put("body_TNB",TNBRes);
			}
		}catch(ModelDataOperationException e){
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,"获取计划信息失败!",e);
		}catch(ControllerException e){
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,"获取schema失败!",e);
		}catch(Exception e){
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,"保存随访记录失败!",e);
		}
	}
	//返回所有系统的账号信息列表
	protected void doGetAllUserList(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ServiceException{
		String sql="select a.id as id,a.name as name,a.password as password,"
		+"a.status as status,b.gender as gender,b.birthday as birthday,b.mobile as mobile "
		+"from User a,SYS_Personnel b where a.id=b.personId";
		try{
			List<Map<String,Object>> list=dao.doQuery(sql,null);
			res.put("body",list);
		}catch(PersistentDataOperationException e){
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,"获取所有系统的账号信息失败!",e);
		}
	}
	// 返回个人账号信息
	public static Map<String,Object> doGetUserInfo(String id) throws ServiceException{
		Map<String,Object> m=null;
		Context ctx=ContextUtils.getContext();
		String sql="select a.id as id,a.name as name,a.password as password,"
		+" a.status as status,b.gender as gender,b.birthday as birthday,b.mobile as mobile,b.cardnum as cardnum"
		+" from User a,SYS_Personnel b where a.id=b.personId and a.id=:id";
		try{
			Session ss=(Session)ctx.get(Context.DB_SESSION);
			if(ss==null){
				SessionFactory sf=AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class);
				ss=sf.openSession();
				ctx.put(Context.DB_SESSION,ss);
			}
			Query q=ss.createQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			q.setParameter("id",id);
			m=(Map<String,Object>)q.uniqueResult();
			ss.flush();
		}catch(HibernateException e){
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,"获取个人账号信息失败!",e);
		}
		return m;
	}
	// 查询档案列表数据
	public void doQueryRecordList(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ServiceException{
		String schemaId="chis.application.mobileApp.schemas.EHR_HealthRecordApp";
		List<?> cnd=CNDHelper.createSimpleCnd("in","a.status","0","2");
		List queryCnd=null;
		List list=new ArrayList();
		List list1=new ArrayList();
		List list2=new ArrayList();
		List list3=new ArrayList();
		List list4=new ArrayList();
		List list5=new ArrayList();
		String role=UserRoleToken.getCurrent().getRoleId();
		// yx-2018-05-15-溧水按责任医生过滤
		String manageUnit=UserRoleToken.getCurrent().getManageUnitId();
		if(req.containsKey("cnd")){
			queryCnd=(List)req.get("cnd");
		}
		String region="";
		if(req.containsKey("manaDoctorId")){
			region=req.get("manaDoctorId")+"";
		}
		String regionCode="";
		if(role.equals("chis.100")){
			list.add("$");
			list.add("a.familyDoctorId");
			list2.add("s");
			list2.add(region);
			list3.add("eq");
			list3.add(list);
			list3.add(list2);
		}else if(region!=null&&region!=""){
			list.add("$");
			list.add("a.manaDoctorId");
			list2.add("s");
			list2.add(region);
			list3.add("eq");
			list3.add(list);
			list3.add(list2);
		}
		String queryCndsType=null;
		if(req.containsKey("queryCndsType")){
			queryCndsType=(String)req.get("queryCndsType");
		}
		String sortInfo=null;
		if(req.containsKey("sortInfo")){
			sortInfo=(String)req.get("sortInfo");
		}
		int limit=25;
		if(req.containsKey("limit")){
			limit=(Integer)req.get("limit");
		}
		int start=1;
		if(req.containsKey("start")){
			start=(Integer)req.get("start");
		}
		String year=new Date().getYear()+"";
		if(req.containsKey("year")){
			year=req.get("year")+"";
		}
		String checkType="1";
		if(req.containsKey("checkType")){
			checkType=(String)req.get("checkType");
		}
		// int age = HealthRecordmpi.calculateAge(birthday, null);
		if(queryCnd==null){
			list4.add("and");
			list4.add(list3);
			list4.add(cnd);
			queryCnd=list4;
		}else{
			for(int i=0;i<queryCnd.size();i++){
				if(queryCnd.get(i).toString().indexOf("manaDoctorId")>-1){
					System.out.print("queryCnd:"+queryCnd.get(i).toString());
				}
			}
			list5.add("and");
			list5.add(queryCnd);
			list5.add(list3);
			list5.add(cnd);
			queryCnd=list5;
		}
		String sqlName="";
		PublicModel hrModel=new PublicModel(dao);
		Map<String,Object> resBody=new HashMap<String,Object>();
		resBody=hrModel.queryRecordList(schemaId,queryCnd,queryCndsType,sortInfo,limit,start,year,checkType);
		List<Map<String,Object>> map=(List<Map<String,Object>>)resBody.get("body");
		Map<String,Object> mapDo=null;
		if(map!=null){
			for(int i=0;i<map.size();i++){
				String manaDoctorId=(String)map.get(i).get("manaDoctorId");
				sqlName="select a.personName from sys_personnel a where a.personId='"+manaDoctorId+"'";
				try{
					mapDo=dao.doSqlLoad(sqlName,null);
				}catch(PersistentDataOperationException e){
					e.printStackTrace();
				}
				Date birthday=(Date)map.get(i).get("birthday");
				if(map.get(i).get(" ")!=null){
					map.get(i).put("manaDoctorName",mapDo.get("PERSONNAME"));
				}else{
					map.get(i).put("manaDoctorName","");
				}
				if(map.get(i).get("birthday")!=null){
					int age=MobileAppService.calculateAge(birthday,null);
					map.get(i).put("age",age);
				}else{
					map.get(i).put("age","");
				}
			}
		}
		res.putAll(resBody);
	}
	//计算年龄（周岁）
	public static int calculateAge(Date birthday,Date calculateDate){
		Calendar c=Calendar.getInstance();
		if(calculateDate!=null){
			c.setTime(calculateDate);
		}
		if(birthday==null)
			return 0;
		Calendar birth=Calendar.getInstance();
		birth.setTime(birthday);
		int age=c.get(Calendar.YEAR)-birth.get(Calendar.YEAR);
		c.set(Calendar.YEAR,birth.get(Calendar.YEAR));
		if(dateCompare(c.getTime(),birth.getTime())<0){
			return age-1;
		}
		return age;
	}
	//比较两个日期的年月日，忽略时分秒。
	public static int dateCompare(Date d1,Date d2){
		Calendar c=Calendar.getInstance();
		c.setTime(d1);
		Calendar c2=Calendar.getInstance();
		c2.set(Calendar.YEAR,c.get(Calendar.YEAR));
		c2.set(Calendar.MONTH,c.get(Calendar.MONTH));
		c2.set(Calendar.DAY_OF_YEAR,c.get(Calendar.DAY_OF_YEAR));
		Date date0=c2.getTime();
		c.setTime(d2);
		c2.set(Calendar.YEAR,c.get(Calendar.YEAR));
		c2.set(Calendar.MONTH,c.get(Calendar.MONTH));
		c2.set(Calendar.DAY_OF_YEAR,c.get(Calendar.DAY_OF_YEAR));
		Date date1=c2.getTime();
		return date0.compareTo(date1);
	}
	//验证身份是否被使用
	public void doCheckIdCard(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ServiceException{
		EmpiModel empiModel=new EmpiModel(dao);
		String idCard=(String)req.get("idCard");
		try{
			List<Map<String,Object>> list=empiModel.getEmpiInfoByIdcard(idCard);
			if(list.size()==0){
				res.put("isOk","1");
				return;
			}
		}catch(ModelDataOperationException e){
		}
		res.put("isOk","0");
	}
	//拼音检索数据
	public void doPyQuery(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) throws ServiceException{
		String RegionCode=UserRoleToken.getCurrent().getRegionCode();
		int limit=10;
		if(req.containsKey("limit")){
			limit=(Integer)req.get("limit");
		}
		int start=0;
		if(req.containsKey("start")){
			start=(Integer)req.get("start")-1;
		}
		String query="";
		if(req.containsKey("query")){
			query=(String)req.get("query");
			query=query.toLowerCase();
		}
		Map<String,Object> par=new HashMap<String,Object>();
		par.put("first",start);
		par.put("max",limit);
		Schema sc=null;
		Schema sc2=null;
		try{
			sc=SchemaController.instance().get("chis.application.ag.schemas.EHR_AreaGrid");
			sc2=SchemaController.instance().get("chis.application.sys.schemas.BASE_UserRoles");
		}catch(ControllerException e){
			e.printStackTrace();
		}
		List<SchemaItem> items=sc.getItems();
		String sql="";
		for(SchemaItem it:items){
			if(it.isVirtual()){
				continue;
			}
			String fid=it.getId();
			Permission p=it.lookupPremission();
			if(it.hasProperty("refAlias")){
				fid=(String)it.getProperty("refItemId");
				String refAlias=(String)it.getProperty("refAlias");
				sql=sql+refAlias+"."+fid+" as "+fid+",";
			}else{
				sql=sql+"a."+fid+" as "+fid+",";
			}
		}
		List<SchemaItem> items2=sc2.getItems();
		String sql2="";
		for(SchemaItem it:items2){
			if(it.isVirtual()){
				continue;
			}
			String fid=it.getId();
			Permission p=it.lookupPremission();
			if(it.hasProperty("refAlias")){
				fid=(String)it.getProperty("refItemId");
				String refAlias=(String)it.getProperty("refAlias");
				sql2=sql2+refAlias+"."+fid+" as "+fid+",";
			}else{
				sql2=sql2+"a."+fid+" as "+fid+",";
			}
		}
		sql="a.regionCode as regionCode,a.regionNo as regionNo,a.regionName as regionName,a.pyCode as pyCode";
		String sqls="select "+sql+" from EHR_AreaGrid a where  a.regionCode like '%"+RegionCode+"%' and a.pyCode like '%"
		+query+"%'";
		List<Map<String,Object>> list=null;
		try{
			list=dao.doSqlQuery(sqls,par);
		}catch(PersistentDataOperationException e){
			e.printStackTrace();
		}
		Map<String,Object> map=null;
		Map<String,Object> ListMap=new HashMap<String,Object>();
		for(int i=0;i<list.size();i++){
			String regionCode=(String)list.get(i).get("REGIONCODE");
			sql2=sql2.substring(0,sql2.length()-1)+" ";
			String sqlRole="select "+sql2+" from BASE_UserRoles a where a.roleId = 'chis.01' and  a.regioncode ='"
			+regionCode+"'";
			List<Map<String,Object>> listId=null;
			try{
				listId=dao.doSqlQuery(sqlRole,par);
			}catch(PersistentDataOperationException e){
				e.printStackTrace();
			}
			for(int n=0;n<listId.size();n++){
				String userId=(String)listId.get(n).get("USERID");
				String roleId=(String)listId.get(n).get("ROLEID");
				String manaunitId=(String)listId.get(n).get("MANAUNITID");
				String sqlName="select  a.personName from sys_personnel a where a.personId='"+userId+"'";
				try{
					map=dao.doSqlLoad(sqlName,null);
				}catch(PersistentDataOperationException e){
					e.printStackTrace();
				}
				list.get(i).put("userId",userId);
				list.get(i).put("roleId",roleId);
				list.get(i).put("manaunitId",manaunitId);
				if(map!=null&&!map.isEmpty()){
					list.get(i).put("personName",map.get("PERSONNAME"));
				}else{
					list.get(i).put("personName","");
				}
			}
		}
		res.put("body",list);
	}
	public void doGetManageUnit(Map<String,Object> req,Map<String,Object> res,Context ctx)
	throws ModelDataOperationException,PersistentDataOperationException{
		Map<String,Object> body=(Map<String,Object>)req.get("body");
		String manaUnitId=(String)body.get("manaUnitId");
		Dictionary dic=DictionaryController.instance().getDic("chis.@manageUnit");
		Map<String,Object> m=new HashMap<String,Object>();
		m.put("key",manaUnitId);
		m.put("text",dic.getText(manaUnitId));
		res.put("manageUnit",m);
	}
	//返回特定schema的字典内容
	protected void doGetSchemaDicList(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ServiceException{
		String[] schemaIds={BSCHISEntryNames.EHR_HealthRecord,BSCHISEntryNames.EHR_PastHistory,
		BSCHISEntryNames.EHR_PersonProblem,"platform.reg.schemas.SYS_Personnel","platform.reg.schemas.BASE_User",
		"platform.reg.schemas.BASE_UserRoles",BSCHISEntryNames.MDC_DiabetesMedicine,
		BSCHISEntryNames.MDC_HypertensionMedicine,BSCHISEntryNames.EHR_LifeCycle,BSCHISEntryNames.MDC_OldPeopleVisit,
		BSCHISEntryNames.MDC_HypertensionVisit,BSCHISEntryNames.MDC_DiabetesVisit};
		Map<String,Object> body=new HashMap<String,Object>();
		Map<String,Object> dics=new HashMap<String,Object>();
		Map<String,Object> temp=new HashMap<String,Object>();
		try{
			int flag=1;
			for(int i=0;i<schemaIds.length;i++){
				String schemaId=schemaIds[i];
				Schema sc=SchemaController.instance().get(schemaId);
				List<SchemaItem> items=sc.getItems();
				for(SchemaItem it:items){
					DictionaryIndicator dici=it.getDic();
					if(dici==null){
						continue;
					}
					String dicId=dici.getId();
					if(dicId.indexOf("user")>0||dicId.indexOf("areaGrid")>0||dicId.indexOf("Personnel")>0
					||dicId.indexOf("medicalRoles")>0){
						continue;
					}
					if(dicId.indexOf("manageUnit")>0&&flag==2){
						continue;
					}
					if(dicId.indexOf("manageUnit")>0){
						flag=2;
					}
					if(temp.containsKey(dicId+":"+it.getAlias())){
						continue;
					}
					Dictionary dic=DictionaryController.instance().get(dicId);
					List<DictionaryItem> dicItems=dic.itemsList();
					if(dicItems==null||dicItems.size()==0){
						dicItems=dic.getSlice(null,0,null);
					}
					if(dicItems==null||dicItems.size()==0){
						System.out.println(dicId);
						continue;
					}
					Map<String,Object> dicMap=new HashMap<String,Object>();
					for(DictionaryItem dicIt:dicItems){
						dicMap.put(dicIt.getKey(),dicIt.getText());
					}
					dics.put(dicId+":"+it.getAlias()+":"+sc.getAlias(),dicMap);
					temp.put(dicId+":"+it.getAlias(),dicMap);
				}
			}
			body.put("dics",dics);
			res.put("body",body);
		}catch(ControllerException e){
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,"返回特定schema的字典内容失败!",e);
		}
	}
	//获取个人信息数据
	public void doGetEmpiInfoByEmpiid(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ServiceException,ModelDataOperationException{
		Map<String,Object> map=new HashMap<String,Object>();
		QueryModel query=new QueryModel(dao);
		String empiId="";
		if(req.containsKey("empiId")){
			empiId=(String)req.get("empiId");
		}
		String schemaId="chis.application.mobileApp.schemas.EHR_HealthRecordApp";
		map=query.getQueryInfo(empiId,schemaId,dao);
		Date birthday=(Date)map.get("BIRTHDAY");
		if(map.get("BIRTHDAY")!=null){
			int age=MobileAppService.calculateAge(birthday,null);
			map.put("AGE",age);
		}else{
			map.put("AGE","");
		}
		Map<String,Object> resBody=new HashMap<String,Object>();
		resBody.put("body",map);
		res.putAll(resBody);
	}
	//获取药物信息
	public void doGetMedicine(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx){
		int limit=10;
		if(req.containsKey("limit")){
			limit=(Integer)req.get("limit");
		}
		int start=0;
		if(req.containsKey("start")){
			start=(Integer)req.get("start")-1;
		}
		String query="";
		if(req.containsKey("query")){
			query=(String)req.get("query");
			query=query.toUpperCase();
		}
		Schema sc=null;
		try{
			sc=SchemaController.instance().get("phis.application.cfg.schemas.YK_TYPK");
		}catch(ControllerException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sql="";
		List<SchemaItem> items=sc.getItems();
		Map<String,Object> map=new HashMap<String,Object>();
		for(SchemaItem it:items){
			if(it.isVirtual()){
				continue;
			}
			String fid=it.getId();
			Permission p=it.lookupPremission();
			if(it.hasProperty("refAlias")){
				fid=(String)it.getProperty("refItemId");
				String refAlias=(String)it.getProperty("refAlias");
				sql=sql+refAlias+"."+fid+" as "+fid+",";
			}else{
				sql=sql+"a."+fid+" as "+fid+",";
			}
		}
		map.put("first",start);
		map.put("max",limit);
		sql=sql.substring(0,sql.length()-1);
		sql=sql.replace(",a.MESS as MESS","");
		String sqls="select "+sql+" from yk_typk a where pydm like '%"+query+"%'";
		List<Map<String,Object>> list=null;
		Map<String,Object> resBody=new HashMap<String,Object>();
		try{
			list=dao.doSqlQuery(sqls,map);
		}catch(PersistentDataOperationException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resBody.put("body",list);
		res.putAll(resBody);
	}
	// 新增个人高血压档案
	public void doAddHypertensionArchive(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ServiceException,ModelDataOperationException,PersistentDataOperationException,ParseException,ExpException{
		req.put("op","create");
		req.put("app",true);
		Map<String,Object> reqBody=(Map<String,Object>)req.get("body");
		Map<String,Object> hyMap=null;
		if(reqBody.containsKey("hypertensionArchiveDetail")){
			hyMap=(Map<String,Object>)reqBody.get("hypertensionArchiveDetail");
		}
		for(Entry<String,Object> s:hyMap.entrySet()){
			String key=s.getKey();
			Object value=s.getValue();
			reqBody.put(key,value);
		}
		// 档案默认为正常
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String dateStr=sdf.format(new Date());
		Date date=sdf.parse(dateStr);
		reqBody.put("status","0");
		reqBody.put("fixDate",dateStr);
		reqBody.put("TC",0.0);
		reqBody.put("familyHistoryOfCardiovascular","n");
		reqBody.put("complication","16");
		reqBody.put("targetHurt","10");
		reqBody.put("riskiness","12");
		reqBody.put("waistLine",80);
		int constriction=Integer.parseInt((String)reqBody.get("constriction"));
		int diastolic=Integer.parseInt((String)reqBody.get("diastolic"));
		reqBody.put("constriction",constriction);
		reqBody.put("diastolic",diastolic);
		int grade=decideHypertensionGrade(constriction,diastolic);
		reqBody.put("hypertensionLevel",grade);
		HypertensionService hys=new HypertensionService();
		// 高血压档案保存
		hys.setvLogService(vLogService);
		hys.setVisitPlanCreator(visitPlanCreator);
		hys.doSaveHypertensionRecord(req,res,dao,ctx);
		// 高血压药品保存
		req.put("schema",MDC_HypertensionFixGroup);
		hys.doSaveHypertensionFixGroup(req,res,dao,ctx);
		List<Map<String,Object>> medMap=null;
		if(reqBody.containsKey("hypertensionMedicines")){
			medMap=(List<Map<String,Object>>)reqBody.get("hypertensionMedicines");
		}
		Map<String,Object> hySubMap=null;
		Map<String,Object> reqbody=(Map<String,Object>)req.get("body");
		String createUser=reqbody.get("fixUser").toString();
		if(medMap!=null&&!medMap.isEmpty()){
			for(int i=0;i<medMap.size();i++){
				hySubMap=medMap.get(i);
				Map<String,Object> reqMed=new HashMap<String,Object>();
				for(Entry<String,Object> s:hySubMap.entrySet()){
					String key=s.getKey();
					Object value=s.getValue();
					reqMed.put(key,value);
				}
				Map<String,Object> resMap=(Map<String,Object>)res.get("body");
				reqMed.put("phrId",(String)resMap.get("phrId"));
				dao.doSave("create",MDC_HypertensionMedicine,reqMed,false);
			}
		}
		// yx 发送信息到app
		String toappflag="2";
		String category="101";// 高血压建档
		try{
			Httpclient client=new Httpclient();
			String flag="0";
			// client.sendtofjzlapp(createUser,reqBody.get("phrId").toString(),category);
			if(flag.equals("1")){
				toappflag="1";
			}
			QueryModel query=new QueryModel(dao);
			query.updatetoappflag(reqBody.get("phrId").toString(),category,toappflag);
		}catch(Exception e){
			System.out.println("传到app出错！");
		}
	}
	//新增糖尿病档案
	public void doAddDiabetesArchive(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ServiceException,ParseException, ModelDataOperationException{
		req.put("op","create");
		Map<String,Object> hyMap=null;
		Map<String,Object> reqBody=(Map<String,Object>)req.get("body");
		Map<String,Object> reqDetail=new HashMap<String,Object>();
		Map<String,Object> reqDia=new HashMap<String,Object>();
		Map<String,Object> m=null;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String dateStr=sdf.format(new Date());
		Date date=sdf.parse(dateStr);
		try{
			if(reqBody.containsKey("diabetesArchiveDetail")){
				hyMap=(Map<String,Object>)reqBody.get("diabetesArchiveDetail");
				for(Entry<String,Object> s:hyMap.entrySet()){
					String key=s.getKey();
					Object value=s.getValue();
					reqDetail.put(key,value);
				}
			}
			for(Entry<String,Object> s:reqBody.entrySet()){
				String key=s.getKey();
				Object value=s.getValue();
				reqDetail.put(key,value);
			}
			reqDetail.put("unit","1");
			reqDetail.put("visitDate",date);
			reqDetail.put("createDate",dateStr);
			// 档案默认为正常
			reqBody.put("status","0");
			reqDetail.put("status","0");
			reqDetail.put("planTypeCode","03");
			DiabetesRecordService drs=new DiabetesRecordService();
			drs.setvLogService(vLogService);
			drs.setVisitPlanCreator(visitPlanCreator);
			reqDia.put("body",reqDetail);
			drs.doSaveDiabetesRecord(reqDia,res,dao,ctx);
			// 糖尿病药品保存
			List<Map<String,Object>> diMap=null;
			if(hyMap.containsKey("diabetesMedicines")){
				diMap=(List<Map<String,Object>>)hyMap.get("diabetesMedicines");
			}
			Map<String,Object> diSubMap=null;
			if(diMap!=null&&!diMap.isEmpty()){
				for(int i=0;i<diMap.size();i++){
					diSubMap=diMap.get(i);
					Map<String,Object> resMed=new HashMap<String,Object>();
					for(Entry<String,Object> s:diSubMap.entrySet()){
						String key=s.getKey();
						Object value=s.getValue();
						resMed.put(key,value);
					}
					resMed.put("phrId",(String)reqDetail.get("phrId"));
					dao.doSave("create",MDC_DiabetesMedicine,resMed,false);
				}
			}
			// yx 发送信息到app
			String toappflag="2";
			String category="102";// 糖尿病建档
			Map<String,Object> resbody=(Map<String,Object>)res.get("body");
			Map<String,Object> createUserbody=(Map<String,Object>)resbody.get("createUser");
			String createUser=createUserbody.get("key").toString();
			try{
				Httpclient client=new Httpclient();
				String flag="0";
				// client.sendtofjzlapp(createUser,reqBody.get("phrId").toString(),category);
				if(flag.equals("1")){
					toappflag="1";
				}
				QueryModel query=new QueryModel(dao);
				query.updatetoappflag(reqBody.get("phrId").toString(),category,toappflag);
			}catch(Exception e){
				System.out.println("传到app出错！");
			}
		}catch(PersistentDataOperationException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//统计计划
	public void doGetPlanTotal(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx){
		Calendar c=Calendar.getInstance();
		c.setTime(new Date()); // 设置当前日期
		c.add(Calendar.DATE,14);
		Date date=c.getTime();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		String strDate=format.format(date);
		Calendar c2=Calendar.getInstance();
		c2.setTime(new Date()); // 设置当前日期
		c2.add(Calendar.DATE,-14);
		Date date2=c2.getTime();
		SimpleDateFormat format2=new SimpleDateFormat("yyyy-MM-dd");
		String strDate2=format.format(date2);
		UserRoleToken user=UserRoleToken.getCurrent();
		String userId=user.getUserId();
		Map<String,Object> pars=new HashMap<String,Object>();
		Map<String,Object> resBody=new HashMap<String,Object>();
		String ehrSql="select count(*) as countTotal from EHR_HealthyRecord where manaDoctorId='"+userId+"'";
		Map<String,Object> mapEhr=null;
		try{
			mapEhr=dao.doLoad(ehrSql,pars);
		}catch(PersistentDataOperationException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 健康档案
		String totalCount=(String)mapEhr.get("countTotal");
		resBody.put("ehrRecord",totalCount);
		String regionCode=UserRoleToken.getCurrent().getRegionCode();
		String gxySql="select count(*) as countTotal"
		+" from mpi_demographicinfo a , pub_visitplan b ,EHR_HealthRecord c where  a.empiid =b.empiid and a.empiid = "
		+"c.empiid and c.regionCode like '%"+regionCode+"%'"+" and b.planStatus=:planStatus  and to_date('"+strDate
		+"','yyyy-MM-dd')>=b.plandate "+"and to_date('"+strDate2+"','yyyy-MM-dd')<=b.plandate and b.businessType ='1'";
		String tnbSql="select count(*) as countTotal"
		+" from mpi_demographicinfo a , pub_visitplan b ,EHR_HealthRecord c where  a.empiid =b.empiid and a.empiid = "
		+"c.empiid and c.regionCode like '%"+regionCode+"%'"+" and b.planStatus=:planStatus  and to_date('"+strDate
		+"','yyyy-MM-dd')>=b.plandate "+"and to_date('"+strDate2+"','yyyy-MM-dd')<=b.plandate and b.businessType ='2'";
		String lnrSql="select count(*) as countTotal"
		+" from mpi_demographicinfo a , pub_visitplan b ,EHR_HealthRecord c where  a.empiid =b.empiid and a.empiid = "
		+"c.empiid and c.regionCode like '%"+regionCode+"%'"+" and b.planStatus=:planStatus  and to_date('"+strDate
		+"','yyyy-MM-dd')>=b.plandate "+"and to_date('"+strDate2+"','yyyy-MM-dd')<=b.plandate and b.businessType ='4'";
		try{
			mapEhr=dao.doLoad(gxySql,pars);
		}catch(PersistentDataOperationException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 健康档案
		String totalGxy=(String)mapEhr.get("countTotal");
		resBody.put("gxyRecord",totalGxy);
		try{
			mapEhr=dao.doLoad(tnbSql,pars);
		}catch(PersistentDataOperationException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 健康档案
		String totalTnb=(String)mapEhr.get("countTotal");
		resBody.put("tnbRecord",totalTnb);
		try{
			mapEhr=dao.doLoad(lnrSql,pars);
		}catch(PersistentDataOperationException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 健康档案
		String totalLnr=(String)mapEhr.get("countTotal");
		resBody.put("lnrRecord",totalLnr);
		res.put("body",resBody);
	}
	//高血压档案列表查询
	public void doQueryHypertensionRecord(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ServiceException,ExpException{
		String arg=(String)req.get("cnd");
		HypertensionService hs=new HypertensionService();
		req.put("schema","chis.application.mobileApp.schemas.MDC_HypertensionRecordApp");
		List<?> cnd=null;
		String manaDoctorId=UserRoleToken.getCurrent().getUserId();
		String role=UserRoleToken.getCurrent().getRoleId();
		if(arg!=null&&!arg.equals(" ")){
			cnd=CNDHelper.toListCnd(""+"['and',['in',['$','a.status'],['0','2']],"+"['eq',['$','a.manaDoctorId'],['s','"
			+manaDoctorId+"']],"+"['or', ['like',['$','c.regionCode_text'],['s','%"+arg+"%']]"
			+",['like', ['$', 'b.personName'], ['s', '%"+arg+"%']],['like',"+"['$','b.idCard'], ['s', '%"+arg+"%']]]]");
		}else if(role.equals("chis.100")){
			cnd=CNDHelper.toListCnd("['and',['in',['$','a.status'],['0','2']],['eq',['$','c.familyDoctorId'],['s','"
			+manaDoctorId+"']]]");
		}else{
			cnd=CNDHelper.toListCnd("['and',['in',['$','a.status'],['0','2']],['eq',['$','a.manaDoctorId'],['s','"
			+manaDoctorId+"']]]");
		}
		req.put("cnd",cnd);
		int limit=10;
		if(req.containsKey("limit")){
			limit=(Integer)req.get("limit");
		}
		int start=1;
		if(req.containsKey("start")){
			start=(Integer)req.get("start");
		}
		req.put("pageSize",limit);
		req.put("pageNo",start);
		try{
			hs.doListHypertensionRecord(req,res,dao,ctx);
		}catch(Exception e){
			// TODO: handle exception
			logger.error("查询高血压档案列表失败");
		}
		List<Map<String,Object>> list=(List<Map<String,Object>>)res.get("body");
		if(list==null){
			res.put("body",new ArrayList<Map<String,Object>>());
		}
	}
	//糖尿病档案列表查询
	public void doQueryDiabetesRecord(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx){
		String arg=(String)req.get("cnd");
		DiabetesRecordService drs=new DiabetesRecordService();
		req.put("schema","chis.application.mobileApp.schemas.MDC_DiabetesRecordApp");
		List<?> cnd=null;
		String manaDoctorId=UserRoleToken.getCurrent().getUserId();
		String role=UserRoleToken.getCurrent().getRoleId();
		try{
			if(arg!=null&&!arg.equals(" ")){
				cnd=CNDHelper.toListCnd(""
				+"['and',['in',['$','a.status'],['0','2']],['or', ['like',['$','c.regionCode_text'],"
				+"['eq',['$','a.manaDoctorId'],['s','"+manaDoctorId+"']],"+"['s','%"+arg+"%']]"
				+",['like', ['$', 'b.personName'], ['s', '%"+arg+"%']],['like',"+"['$','b.idCard'], ['s', '%"+arg+"%']]]]");
			}else if(role.equals("chis.100")){
				cnd=CNDHelper.toListCnd("['and',['in',['$','a.status'],['0','2']],['eq',['$','c.familyDoctorId'],['s','"
				+manaDoctorId+"']]]");
			}else{
				cnd=CNDHelper.toListCnd("['and',['in',['$','a.status'],['0','2']],['eq',['$','a.manaDoctorId'],['s','"
				+manaDoctorId+"']]]");
			}
		}catch(ExpException e1){
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		req.put("cnd",cnd);
		try{
			drs.doListDiabetesRecord(req,res,dao,ctx);
		}catch(Exception e){
			// TODO: handle exception
			logger.error("查询糖尿病档案列表失败");
		}
	}
	//慢病档案个人查询
	public void doGetArchiveDetailByRecordType(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ExpException,ModelDataOperationException{
		String phrId=null;
		String recordType=null;
		if(req.containsKey("phrId")&&req.containsKey("recordType")){
			phrId=(String)req.get("phrId");
			recordType=(String)req.get("recordType");
		}else{
			res.put("msg","错误参数");
			return;
		}
		List<?> cnd=CNDHelper.toListCnd("['eq',['$','a.phrId'],['s','"+phrId+"']]");
		Map<String,Object> resBody=new HashMap<String,Object>();
		Map<String,Object> rsHy=null;
		if(recordType.equals("1")){
			try{
				rsHy=dao.doLoad(cnd,"chis.application.mobileApp.schemas.MDC_HypertensionDetailApp");
			}catch(PersistentDataOperationException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Map<String,Object> rsDia=null;
		if(recordType.equals("2")){
			try{
				rsDia=dao.doLoad(cnd,"chis.application.mobileApp.schemas.MDC_DiabetesDetailApp");
			}catch(PersistentDataOperationException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(rsHy!=null&&!rsHy.isEmpty()){
			resBody.putAll(rsHy);
		}
		if(rsDia!=null&&!rsDia.isEmpty()){
			resBody.putAll(rsDia);
		}
		res.remove("body");
		// 获取药品信息
		List<Map<String,Object>> Map=null;
		try{
			// 取高血压药品
			if(recordType.equals("1")){
				Map=getHypertensionRecordMedicine(phrId,dao);
				resBody.put("hypertensionMedicines",Map);
			}
			// 取糖尿病药品
			if(recordType.equals("2")){
				Map=getDiabetesRecordMedicine(phrId,dao);
				resBody.put("diabetesMedicines",Map);
			}
		}catch(Exception e){
			// TODO: handle exception
			res.put("msg","查询药品信息失败信息失败！");
			throw new ModelDataOperationException(Constants.CODE_DATABASE_ERROR,"查询药品信息失败信息失败！");
		}
		res.put("body",resBody);
	}
	//慢病档案个人编辑
	public void doUpdateArchiveByRecordType(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ExpException,ValidateException{
		Map<String,Object> reqBody=(Map<String,Object>)req.get("body");
		String phrId=(String)reqBody.get("phrId");
		List<?> cnd=CNDHelper.toListCnd("['eq',['$','a.phrId'],['s','"+phrId+"']]");
		Map<String,Object> rsBody=null;
		String recordType="1";
		String schemaId="";
		if(req.containsKey("recordType")){
			recordType=(String)req.get("recordType");
		}
		Map<String,Object> hyMap=null;
		if(reqBody.containsKey("hypertensionArchiveDetail")){
			hyMap=(Map<String,Object>)reqBody.get("hypertensionArchiveDetail");
		}
		if(reqBody.containsKey("diabetesArchiveDetail")){
			hyMap=(Map<String,Object>)reqBody.get("diabetesArchiveDetail");
		}
		if(recordType.equals("1")){
			schemaId=MDC_HypertensionRecord;
			for(Entry<String,Object> s:hyMap.entrySet()){
				String key=s.getKey();
				Object value=s.getValue();
				reqBody.put(key,value);
			}
		}
		if(recordType.equals("2")){
			schemaId=MDC_DiabetesRecord;
			for(Entry<String,Object> s:hyMap.entrySet()){
				String key=s.getKey();
				Object value=s.getValue();
				reqBody.put(key,value);
			}
		}
		try{
			rsBody=dao.doSave("update",schemaId,reqBody,false);
			// 药品修改
			String med=null;
			List<Map<String,Object>> medMap=null;
			List<Map<String,Object>> medMapSql=null;
			if(reqBody.containsKey("hypertensionMedicines")){
				med=MDC_HypertensionMedicine;
				medMap=(List<Map<String,Object>>)reqBody.get("hypertensionMedicines");
				medMapSql=dao.doList(cnd,"a.phrId desc",MDC_HypertensionMedicine);
			}
			if(reqBody.containsKey("diabetesMedicines")){
				med=MDC_DiabetesMedicine;
				medMap=(List<Map<String,Object>>)reqBody.get("diabetesMedicines");
				medMapSql=dao.doList(cnd,"a.phrId desc",MDC_DiabetesMedicine);
			}
			Map<String,Object> map=new HashMap<String,Object>();
			Map<String,Object> reqMed=new HashMap<String,Object>();
			if(medMap!=null&&!medMap.isEmpty()){
				// 删除库中数据
				dao.doRemove("phrId",phrId,med);
				for(int i=0;i<medMap.size();i++){
					map=(Map<String,Object>)medMap.get(i);
					for(Entry<String,Object> s:map.entrySet()){
						String key=s.getKey();
						Object value=s.getValue();
						reqMed.put(key,value);
					}
					reqMed.put("phrId",phrId);
					reqMed.remove("recordId");
					dao.doSave("create",med,reqMed,false);
				}
			}else{
				dao.doRemove("phrId",phrId,med);
			}
		}catch(PersistentDataOperationException e){
			e.printStackTrace();
		}
		res.putAll(rsBody);
	}
	//慢病档案查询
	public void doQueryArchivesByContent(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx){
		String arg=(String)req.get("cnd");
		HypertensionService hs=new HypertensionService();
		DiabetesRecordService drs=new DiabetesRecordService();
		req.put("schema","chis.application.mobileApp.schemas.MDC_DiabetesRecordApp");
		String manaDoctorId=UserRoleToken.getCurrent().getUserId();
		List<?> cnd=null;
		try{
			if(arg!=null&&!arg.equals(" ")){
				cnd=CNDHelper.toListCnd(""+"['and',['in',['$','a.status'],['0','2']],['eq',['$','a.manaDoctorId'],['s','"
				+manaDoctorId+"']],"+"['or', ['like',['$','c.regionCode_text'],['s','%"+arg+"%']]"
				+",['like', ['$', 'b.personName'], ['s', '%"+arg+"%']],['like',"+"['$','b.idCard'], ['s', '%"+arg+"%']]]]");
			}else{
				cnd=CNDHelper.toListCnd("['and',['in',['$','a.status'],['0','2']],['eq',['$','a.manaDoctorId'],['s','"
				+manaDoctorId+"']]]");
			}
		}catch(ExpException e1){
			// TODO Auto-generated catch block
			res.put("msg","搜索列表失败");
			e1.printStackTrace();
		}
		req.put("cnd",cnd);
		try{
			drs.doListDiabetesRecord(req,res,dao,ctx);
		}catch(Exception e){
			// TODO: handle exception
			res.put("msg","搜索列表失败");
			logger.error("查询糖尿病档案列表失败");
		}
		List<Map<String,Object>> resBody=(List<Map<String,Object>>)res.get("body");
		Map<String,Object> mapHy=null;
		for(int i=0;i<resBody.size();i++){
			mapHy=resBody.get(i);
			mapHy.put("recordType","2");
		}
		res.remove("body");
		// **高血压的档案列表信息
		req.put("schema","chis.application.mobileApp.schemas.MDC_HypertensionRecordApp");
		try{
			drs.doListDiabetesRecord(req,res,dao,ctx);
		}catch(Exception e){
			// TODO: handle exception
			res.put("msg","搜索列表失败");
			logger.error("查询高血压档案列表失败");
		}
		List<Map<String,Object>> resBody2=(List<Map<String,Object>>)res.get("body");
		Map<String,Object> mapDi=null;
		for(int i=0;i<resBody2.size();i++){
			mapDi=resBody2.get(i);
			mapDi.put("recordType","1");
		}
		for(int i=0;i<resBody2.size();i++){
			resBody.add(resBody2.get(i));
		}
		res.put("body",resBody);
	}
	//身份验证返回内容
	public void doValidateCardIdHypertensionInfo(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ExpException,ModelDataOperationException{
		String regionCode=UserRoleToken.getCurrent().getRegionCode();
		EmpiModel empiModel=new EmpiModel(dao);
		res.remove("body");
		// 查看是否有个人基本信息
		String idCard=(String)req.get("idCard");
		idCard=idCard.toUpperCase();
		String schema=MDC_HypertensionRecord;
		List<?> cnd=CNDHelper.toListCnd("['eq',['$','b.idCard'],['s','"+idCard+"']]");
		List<Map<String,Object>> rsMap=QueryList(dao,schema,cnd);
		// 1有基本信息 0无基本信息
		if(rsMap!=null&&!rsMap.isEmpty()){
			res.put("body",rsMap.get(0));
			res.put("validate","0");
		}else{
			List<Map<String,Object>> list=null;
			try{
				list=empiModel.getEmpiInfoByIdcard(idCard);
			}catch(ModelDataOperationException e){
			}
			// 查看是否有个人健康档案
			if(list!=null&&list.size()!=0){
				String empiId=(String)list.get(0).get("empiId");
				HealthRecordModel hrm=new HealthRecordModel(dao);
				Map<String,Object> mapHeal=null;
				try{
					mapHeal=hrm.getHealthRecordByEmpiId(empiId);
				}catch(ModelDataOperationException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String regionCode2=(String)mapHeal.get("regionCode");
				Map<String,Object> resList=new HashMap<String,Object>();
				if(mapHeal!=null&&mapHeal.size()!=0){
					res.put("validate","2");
					resList=(Map<String,Object>)list.get(0);
					String personName=(String)resList.get("personName");
					mapHeal.put("personName",personName);
					res.put("body",mapHeal);
					if(regionCode2==null||regionCode2.indexOf(regionCode)==-1){
						res.put("validate","3");
					}
				}else{
					res.put("validate","1");
				}
			}else{
				res.put("validate","1");
			}
		}
	}
	//糖尿病身份验证返回内容
	public void doValidateCardIdDiabetesInfo(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ExpException,ModelDataOperationException{
		String regionCode=UserRoleToken.getCurrent().getRegionCode();
		EmpiModel empiModel=new EmpiModel(dao);
		res.remove("body");
		// 查看是否有个人基本信息
		String idCard=(String)req.get("idCard");
		idCard=idCard.toUpperCase();
		String schema=MDC_DiabetesRecord;
		List<?> cnd=CNDHelper.toListCnd("['eq',['$','b.idCard'],['s','"+idCard+"']]");
		List<Map<String,Object>> rsMap=QueryList(dao,schema,cnd);
		if(rsMap!=null&&!rsMap.isEmpty()){
			res.put("body",rsMap.get(0));
			res.put("validate","0");
		}else{
			List<Map<String,Object>> list=null;
			try{
				list=empiModel.getEmpiInfoByIdcard(idCard);
			}catch(ModelDataOperationException e){
			}
			// 查看是否有个人健康档案
			if(list!=null&&list.size()!=0){
				String empiId=(String)list.get(0).get("empiId");
				HealthRecordModel hrm=new HealthRecordModel(dao);
				Map<String,Object> mapHeal=null;
				try{
					mapHeal=hrm.getHealthRecordByEmpiId(empiId);
				}catch(ModelDataOperationException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String regionCode2=(String)mapHeal.get("regionCode");
				Map<String,Object> resList=new HashMap<String,Object>();
				if(mapHeal!=null&&mapHeal.size()!=0){
					res.put("validate","2");
					resList=(Map<String,Object>)list.get(0);
					String personName=(String)resList.get("personName");
					mapHeal.put("personName",personName);
					res.put("body",mapHeal);
					if(regionCode2==null||regionCode2.indexOf(regionCode)==-1){
						res.put("validate","3");
					}
				}else{
					res.put("validate","1");
				}
			}else{
				res.put("validate","1");
			}
		}
	}
	//检查档案是否存在
	public List<Map<String,Object>> QueryList(BaseDAO dao,String schema,List<?> cnd) throws ModelDataOperationException{
		List<Map<String,Object>> rsList=new ArrayList<Map<String,Object>>();
		try{
			rsList=dao.doList(cnd,"b.idCard",schema);
		}catch(PersistentDataOperationException e){
			throw new ModelDataOperationException(Constants.CODE_DATABASE_ERROR,"查询信息失败！");
		}
		return rsList;
	}
	public void getArchiveDetailByRecordType(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ModelDataOperationException{
		String phrId=(String)req.get("phrId");
		String recordType=(String)req.get("recordType");
		req.put("body",phrId);
		// 获取药品信息
		List<Map<String,Object>> Map=null;
		try{
			// 取高血压药品
			if(recordType.equals("1")){
				Map=getHypertensionRecordMedicine(phrId,dao);
			}
			// 取糖尿病药品
			if(recordType.equals("2")){
				Map=getDiabetesRecordMedicine(phrId,dao);
			}
			res.put("body",Map);
		}catch(Exception e){
			// TODO: handle exception
			res.put("msg","查询药品信息失败信息失败！");
			throw new ModelDataOperationException(Constants.CODE_DATABASE_ERROR,"查询药品信息失败信息失败！");
		}
	}
	//糖尿病用药情况 -- 用药列表
	public List<Map<String,Object>> getDiabetesRecordMedicine(String phrId,BaseDAO dao) throws ModelDataOperationException{
		List<Object> cnd1=CNDHelper.createSimpleCnd("eq","phrId","s",phrId);
		List<Object> cnd2=CNDHelper.createSimpleCnd("eq","visitId","s","0000000000000000");
		List<Object> cnd=CNDHelper.createArrayCnd("and",cnd1,cnd2);
		List<Map<String,Object>> rsList=new ArrayList<Map<String,Object>>();
		try{
			List<Map<String,Object>> list=(List<Map<String,Object>>)dao.doQuery(cnd,"recordId",
			BSCHISEntryNames.MDC_DiabetesMedicine);
			rsList=list;
		}catch(PersistentDataOperationException e){
			throw new ModelDataOperationException(Constants.CODE_DATABASE_ERROR,"获取糖尿病档案药品失败。");
		}
		return rsList;
	}
	//高血压用药情况 -- 用药列表
	public List<Map<String,Object>> getHypertensionRecordMedicine(String phrId,BaseDAO dao)
	throws ModelDataOperationException{
		List<Object> cnd1=CNDHelper.createSimpleCnd("eq","phrId","s",phrId);
		List<Object> cnd2=CNDHelper.createSimpleCnd("eq","visitId","s","0000000000000000");
		List<Object> cnd=CNDHelper.createArrayCnd("and",cnd1,cnd2);
		List<Map<String,Object>> rsList=new ArrayList<Map<String,Object>>();
		try{
			List<Map<String,Object>> list=(List<Map<String,Object>>)dao.doQuery(cnd,"recordId",
			BSCHISEntryNames.MDC_HypertensionMedicine);
			rsList=list;
		}catch(PersistentDataOperationException e){
			throw new ModelDataOperationException(Constants.CODE_DATABASE_ERROR,"获取高血压档案药品失败。");
		}
		return rsList;
	}
	//获取责任医生
	public void doGetManaDoctor(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx){
		String ManageUnitId=UserRoleToken.getCurrent().getManageUnitId();
		Map<String,Object> parameters=new HashMap<String,Object>();
		String personName=(String)req.get("query");
		personName=personName.toUpperCase();
		String sql="select b.userId as userId ,b.userName as userName from (select a.userId as userId,a.userName as UserName from sys_users a  where a.pycode like '%"
		+personName+"%' ) b,base_userroles c where b.userId=c.userid  and c.roleId='chis.01'";
		List<Map<String,Object>> map=null;
		try{
			map=dao.doSqlQuery(sql,parameters);
		}catch(PersistentDataOperationException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Map<String,Object>> mapList=new ArrayList<Map<String,Object>>();
		for(int i=0;i<map.size();i++){
			Map<String,Object> mapNew=new HashMap<String,Object>();
			Map<String,Object> mapSub=(Map<String,Object>)map.get(i);
			mapNew.put("personName",(String)mapSub.get("USERNAME"));
			mapNew.put("userId",(String)mapSub.get("USERID"));
			mapList.add(mapNew);
		}
		res.put("body",mapList);
	}
	public static int decideHypertensionGrade(int constriction,int diastolic){
		if(constriction>=180||diastolic>=110){
			return 3; // @@ 3级（重度）
		}
		if((constriction>=160&&constriction<=179)||(diastolic>=100&&diastolic<=109)){
			return 2; // @@ 2级（中度）
		}
		if((constriction>=140&&constriction<=159)||(diastolic>=90&&diastolic<=99)){
			return 1; // @@ 1级（轻度）
		}
		if(constriction<120&&diastolic<80){
			return 4; // @@ 理想血压
		}
		if(constriction<130&&diastolic<85){
			return 5; // @@ 正常血压
		}
		if((constriction>=130&&constriction<=139&&diastolic<90)||(diastolic>=85&&diastolic<=89&&constriction<140)){
			return 6; // @@ 正常高值
		}
		if(constriction>=140&&diastolic<90){
			return 7; // @@ 单纯收缩性高血压
		}
		return 0;
	}
	//获取用户信息
	public static Map<String,Object> doGetUserInfo2(String id) throws ServiceException{
		Map<String,Object> m=null;
		Context ctx=ContextUtils.getContext();
		String sql="select a.id as id,b.personId as username,b.personName as realname,a.password as password,"
		+"a.status as status,b.gender as gender,b.birthday as birthday,b.mobile as mobile "
		+"from User a,SYS_Personnel b where a.id=b.personId and a.id=:id";
		Session ss=(Session)ctx.get(Context.DB_SESSION);
		try{
			if(ss==null||!ss.isOpen()){
				SessionFactory sf=AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class);
				ss=sf.openSession();
				ctx.put(Context.DB_SESSION,ss);
			}
			Query q=ss.createQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			q.setParameter("id",id);
			m=(Map<String,Object>)q.uniqueResult();
			ss.flush();
			ContextUtils.clear();
		}catch(HibernateException e){
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,"查询用户信息失败!",e);
		}finally{
			ss.close();
		}
		return m;
	}
	// 保存体温和血氧
	public void doSaveMonitorInfo(Map<String,Object> jsonReq,Map<String,Object> jsonRes,BaseDAO dao,Context ctx)
	throws ServiceException{
		// 这里做了体温和血氧的保存
		String op="create";
		String entryName="chis.application.hc.schemas.ZJ_RECORD";
		Map<String,Object> data=(Map<String,Object>)jsonReq.get("data");
		if(data.get("empiId")!=null&&!"".equals(data.get("empiId"))){
			HashMap<String,Object> savedata=new HashMap<String,Object>();
			savedata.put("empiId",data.get("empiId")+"");
			savedata.put("zjrq",new Date());
			savedata.put("jcrq",new Date());
			UserRoleToken user=UserRoleToken.getCurrent();
			savedata.put("orgcode",user.getManageUnitId());
			// 体温保存
			if(data.containsKey("temperature")&&data.get("temperature")!=null&&(data.get("temperature")+"").length()>0){
				savedata.put("yqdm","36");// 体温
				savedata.put("yqmc","体温计");
				savedata.put("xmbh","000555");
				savedata.put("xmdw","摄氏度");
				savedata.put("jg",data.get("temperature")+"");
				savedata.put("ckfw","36.1-37.0");
				try{
					dao.doSave("create",entryName,savedata,false);
				}catch(PersistentDataOperationException e){
					e.printStackTrace();
				}
			}
			if(data.containsKey("spo2Vo")&&data.get("spo2Vo")!=null){
				Map<String,Object> spo2Vo=(Map<String,Object>)data.get("spo2Vo");
				if(spo2Vo.containsKey("spo2")&&spo2Vo.get("spo2")!=null&&(spo2Vo.get("spo2")+"").length()>0){
					savedata.put("yqdm","20");// 血氧
					savedata.put("yqmc","血氧");
					savedata.put("xmbh","000201");
					savedata.put("xmdw","%");
					savedata.put("jg",spo2Vo.get("spo2")+"");
					savedata.put("ckfw","动脉血:约98%、静脉血:约75%");
					try{
						dao.doSave("create",entryName,savedata,false);
					}catch(PersistentDataOperationException e){
						e.printStackTrace();
					}
				}
			}
		}
	}
	// 查询签约居民是否建档-亿家接口
	public void doCheckqyinfo(Map<String,Object> jsonReq,Map<String,Object> jsonRes,BaseDAO dao,Context ctx)
	throws ServiceException{
		String IDCARD=jsonReq.get("idcard")+"";
		if(IDCARD==null||IDCARD.length()==0){
			jsonRes.put("code","502");
			jsonRes.put("msg","身份证号不能为空！");
			return;
		}else if(IDCARD.length()<18){
			jsonRes.put("code","502");
			jsonRes.put("msg","身份证号长度为18，谢谢！");
			return;
		}
		String sql="select b.personname as PERSONNAME, case b.sexcode when '1' then '男' when '2' "
		+" then '女' else '未知' end as SEX,"+" floor(months_between(sysdate,b.birthday)/12) as AGE,b.address as ADDRESS"
		+" from ehr_healthrecord a ,mpi_demographicinfo b "+" where b.idcard=:idcard and a.empiid=b.empiid ";
		Map<String,Object> p=new HashMap<String,Object>();
		p.put("idcard",IDCARD);
		try{
			Map<String,Object> record=dao.doSqlLoad(sql,p);
			if(record==null||record.size()==0){
				jsonRes.put("code","502");
				jsonRes.put("msg","该身份证:"+IDCARD+"居民未建档！");
				return;
			}else{
				jsonRes.put("code","200");
				jsonRes.put("msg","该身份证:"+IDCARD+"居民已建档！");
				jsonRes.put("data",record);
				return;
			}
		}catch(PersistentDataOperationException e){
			e.printStackTrace();
		}
	}
	// 保存居民签约期限-亿家接口
	public void doSaveqyinfo(Map<String,Object> jsonReq,Map<String,Object> jsonRes,BaseDAO dao,Context ctx)
	throws ServiceException{
		System.out.println("..............................");
		Map<String,Object> data=(Map<String,Object>)jsonReq.get("data");
		String IDCARD=data.get("idcard")+"";
		String sxsj=data.get("sxsj")+"";
		String zzsj=data.get("zzsj")+"";
		if(IDCARD==null||IDCARD.length()==0){
			jsonRes.put("code","502");
			jsonRes.put("msg","身份证号不能为空！");
			return;
		}else if(IDCARD.length()<18){
			jsonRes.put("code","502");
			jsonRes.put("msg","身份证号长度为18，谢谢！");
			return;
		}
		UserRoleToken user=UserRoleToken.getCurrent();
		String userId=user.getUserId();
		String sql="update EHR_HealthRecord a set a.familyDoctorId=:familyDoctorId ,"
		+" a.sxsj=:sxsj,a.zzsj=:zzsj where a.empiId=("
		+" select b.empiId from MPI_DemographicInfo b where b.idCard=:idCard)";
		Map<String,Object> p=new HashMap<String,Object>();
		p.put("idCard",IDCARD);
		p.put("familyDoctorId",userId);
		p.put("sxsj",BSCHISUtil.toDate(sxsj));
		p.put("zzsj",BSCHISUtil.toDate(zzsj));
		try{
			dao.doUpdate(sql,p);
			jsonRes.put("code","200");
			jsonRes.put("msg","签约成功！");
			return;
		}catch(PersistentDataOperationException e){
			jsonRes.put("code","502");
			jsonRes.put("msg","签约失败！");
			System.out.println(e.getMessage());
			return;
		}
	}
	// 获取用户信息
	public void doGetUserMsg(Map<String,Object> jsonReq,Map<String,Object> jsonRes,BaseDAO dao,Context ctx)
	throws ServiceException{
		UserRoleToken user=UserRoleToken.getCurrent();
		String userId=user.getUserId();
		jsonRes.put("data",doGetUserInfo(userId));
	}
	// 获取未签约信息-app
	public void doGetNeedContractList(Map<String,Object> jsonReq,Map<String,Object> jsonRes,BaseDAO dao,Context ctx)
	throws ServiceException{
		List<Map<String,Object>> rsList=new ArrayList<Map<String,Object>>();
		Map<String,Object> resBody=new HashMap<String,Object>();
		String content=jsonReq.get("content")==null?"":jsonReq.get("content")+"";
		String doctorId=jsonReq.get("doctorId")==null?"":jsonReq.get("doctorId")+"";
		String and="";
		if(content.length()>0&&!content.equals("null")){
			and=" and (b.personName like '"+content+"%' or b.idCard like '"+content+"%' or a.regionCode_text like '"
			+content+"%')";
		}
		String sql="select a.phrId as phrId,a.empiId as empiId ,a.regioncode as regionCode,"
		+"a.regionCode_text as regionCode_text,a.manaDoctorId as manaDoctorId,a.manaUnitId as manaUnitId,"
		+" b.personName as personName,b.sexCode as sexCode,b.birthday as birthday,b.idCard as idCard,"
		+" b.mobileNumber as mobileNumber,floor(months_between (sysdate,b.birthday)/12) as age"
		+" from EHR_HealthRecord a ,MPI_DemographicInfo b where a.empiid not in ("
		+" select  d.fs_empiid from EHR_FamilyContractBase c,EHR_FamilyContractService d "
		+" where c.fc_id=d.fc_id and c.fc_sign_flag='1') and a.empiId=b.empiId "+and
		+" and a.manadoctorid=:userId and rownum <=50 ";
		Map<String,Object> pa=new HashMap<String,Object>();
		pa.put("userId",doctorId);
		try{
			rsList=dao.doSqlQuery(sql,pa);
		}catch(PersistentDataOperationException e){
			jsonRes.put("code","400");
			jsonRes.put("msg","sql语句执行失败！");
			throw new ServiceException(e.getMessage());
		}
		jsonRes.put("code","200");
		jsonRes.put("msg","成功");
		jsonRes.put("data",rsList);
	}
	// 保存签约信息-app
	public void doSaveContractInfo(Map<String,Object> req,Map<String,Object> jsonRes,BaseDAO dao,Context ctx)
	throws ServiceException{
		Map<String,Object> data=(Map<String,Object>)req.get("data");
		Map<String,Object> reqBody=new HashMap<String,Object>();
		jsonRes.put("code","300");
		for(String key:data.keySet()){
			reqBody.put(key.substring(0,1).toUpperCase()+key.substring(1),data.get(key));
		}
		String pkey=(String)reqBody.get("FS_Id");
		String op="create";
		FamilyRecordModule frModule=new FamilyRecordModule(dao);
		if(pkey!=null&&!pkey.equals(" ")){
			op="update";
		}
		String empiId=(String)reqBody.get("FS_EmpiId");
		try{
			boolean pFlag=false;
			HealthRecordModel hrModel=new HealthRecordModel(dao);
			if(reqBody.get("FC_Stop_Date")!=null&&!"".equals(reqBody.get("FC_Stop_Date"))){
				reqBody.put("FC_Sign_Flag","2");
				pFlag=true;
			}else{
				reqBody.put("FC_Sign_Flag","1");
			}
			Map<String,Object> info=hrModel.getHealthRecordByEmpiId(empiId);
			if(info==null){
				return;
			}
			String masterFlag=(String)info.get("masterFlag");
			String familyId="";
			if(masterFlag!=null&&masterFlag.equals("y")){
				familyId=(String)info.get("familyId");
				if(familyId!=null&&!familyId.isEmpty()){
					reqBody.put("F_Id",familyId);
				}
			}
			String userId=UserRoleToken.getCurrent().getUserId();
			String userManageUnitId=UserRoleToken.getCurrent().getManageUnitId();
			reqBody.put("FC_CreateUser",userId);
			reqBody.put("FC_Repre",empiId);
			reqBody.put("FC_CreateUnit",userManageUnitId);
			reqBody.put("FC_CreateDate",new Date());
			reqBody.put("FC_Begin",reqBody.get("FC_Begin"));
			reqBody.put("FC_End",reqBody.get("FC_End"));
			Map<String,Object> contractRecord=frModule.saveFamilyContractBase(reqBody,op);
			if("create".equals(op)){
				pkey=(String)contractRecord.get("FC_Id");
			}
			String FC_Id="";
			if(reqBody.get("FC_Id")!=null){
				FC_Id=(String)reqBody.get("FC_Id");
			}
			String phrId=(String)info.get("phrId");
			Map<String,Object> fcsRecord=frModule.getFamilyContract(empiId,FC_Id);
			String FS_PersonGroup=(String)reqBody.get("FS_PersonGroup");
			String FS_Kind=(String)reqBody.get("FS_Kind");
			String signFlag=(String)reqBody.get("signFlag");
			String oldFlag=(String)info.get("signFlag");
			if((reqBody.get("FC_Sign_Flag")+"").equals("1")){
				signFlag="y";
			}
			if(fcsRecord!=null){
				fcsRecord.put("FS_PersonGroup",FS_PersonGroup);
				fcsRecord.put("FS_Kind",FS_Kind);
				if(info!=null){
					if(pFlag||(signFlag!=null&&!signFlag.equals(oldFlag))){
						info.put("signFlag",signFlag);
						if(pFlag){
							info.put("signFlag","n");
						}
						hrModel.saveHealthRecord("update",info);
						if("y".equals(signFlag)){
							fcsRecord.put("FS_CreateDate",new Date());
						}
					}
				}
				frModule.saveFamilyContract(fcsRecord,"update");
				vLogService.saveVindicateLog(EHR_FamilyContractService,"2",pkey,dao);
			}else{
				reqBody.put("FS_CreateDate",new Date());
				reqBody.put("FC_Id",pkey);
				Map<String,Object> rsMap=frModule.saveFamilyContract(reqBody,"create");
				String pkey2=(String)rsMap.get("FS_Id");
				vLogService.saveVindicateLog(EHR_FamilyContractService,"1",pkey2,dao);
				if(info!=null){
					info.put("signFlag","y");
					if(pFlag){
						info.put("signFlag","n");
					}
					hrModel.saveHealthRecord("update",info);
					vLogService.saveVindicateLog(EHR_HealthRecord,"2",phrId,dao,empiId);
				}
			}
			// 如果签约标志是y则更新个人签约记录
			String grqyempiId=(String)reqBody.get("FS_EmpiId");
			if(reqBody.get("signFlag")!=null){
				String grqybz=(String)reqBody.get("signFlag");
				if(grqybz.equals("y")){
					vLogService.saveRecords("GRQY","create",dao,grqyempiId);
				}else{
					vLogService.saveRecords("GRQY","logout",dao,grqyempiId);
				}
			}else{
				vLogService.saveRecords("GRQY","logout",dao,grqyempiId);
			}
			Map<String,Object> body=new HashMap<String,Object>();
			body.put("pkey",pkey);
			jsonRes.put("code","200");
			jsonRes.put("msg","成功");
			jsonRes.put("data",body);
		}catch(ModelDataOperationException e){
			throw new ServiceException(e);
		}
	}
	// app-获取已签约列表-yx
	public void doGetAlreadyContractList(Map<String,Object> jsonReq,Map<String,Object> jsonRes,BaseDAO dao,Context ctx)
	throws ServiceException{
		MobileAppModule mo=new MobileAppModule(dao);
		mo.getAlreadyContractList(jsonReq,jsonRes);
	}
	// 获取慢病档案管理数
	public void doGetMdcRecordCount(Map<String,Object> jsonReq,Map<String,Object> jsonRes,BaseDAO dao,Context ctx)
	throws ServiceException{
		String manaDoctorId=UserRoleToken.getCurrent().getUserId();
		try{
			String where="where a.status in ('0','2') and a.manaDoctorId=:manaDoctorId ";
			String table="mdc_hypertensionrecord a ";
			String sqlfirst="select count(a.phrid) as COUNT from ";
			Map<String,Object> pa=new HashMap<String,Object>();
			pa.put("manaDoctorId",manaDoctorId);
			Map<String,Object> m=dao.doSqlLoad(sqlfirst+table+where,pa);
			jsonRes.put("gxycount",m.get("COUNT")+"");
			table="mdc_diabetesrecord a ";
			m=dao.doSqlLoad(sqlfirst+table+where,pa);
			jsonRes.put("tnbcount",m.get("COUNT")+"");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//查询档案列表数据-新版app
	public void doQueryArchiveList(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ServiceException{
		String schemaId="chis.application.mobileApp.schemas.EHR_HealthRecordApp";
		List<?> cnd=CNDHelper.createSimpleCnd("in","a.status","0","2");
		List<?> cnd2=null;
		List queryCnd=null;
		List list=new ArrayList();
		List list1=new ArrayList();
		List list2=new ArrayList();
		List list3=new ArrayList();
		List list4=new ArrayList();
		List list5=new ArrayList();
		String userId=UserRoleToken.getCurrent().getUserId();
		UserRoleToken.getCurrent().getManageUnitId();

		String content=null;
		if(req.containsKey("content")){
			if((req.get("content")!=null)&&(req.get("content")!="")){
				content=(String)req.get("content");
			}
		}
		String region="";
		if(req.containsKey("manaDoctorId")){
			region=(String)req.get("manaDoctorId");
		}
		String regionCode="";
		list.add("$");
		list.add("a.manaDoctorId");
		list2.add("s");
		list2.add(userId);
		list3.add("eq");
		list3.add(list);
		list3.add(list2);
		String queryCndsType=null;
		if(req.containsKey("queryCndsType")){
			queryCndsType=(String)req.get("queryCndsType");
		}
		String sortInfo=null;
		if(req.containsKey("sortInfo")){
			sortInfo=(String)req.get("sortInfo");
		}
		int limit=25;
		if(req.containsKey("pageSize")){
			limit=(Integer)req.get("pageSize");
		}
		int start=1;
		if(req.containsKey("pageNo")){
			start=(Integer)req.get("pageNo");
		}
		String year=new Date().getYear()+"";
		if(req.containsKey("year")){
			year=req.get("year")+"";
		}
		String checkType="1";
		if(req.containsKey("checkType")){
			checkType=(String)req.get("checkType");
		}
		
		try {
			cnd2=CNDHelper.toListCnd(""+"['or', ['like',['$','a.regionCode_text'],['s','%"+content+"%']]"
					+",['like', ['$', 'b.personName'], ['s', '%"+content+"%']],['like',"+"['$','b.idCard'], ['s', '%"+content+"%']]]");
		} catch (ExpException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		if(content==null){
			list4.add("and");
			list4.add(list3);
			list4.add(cnd);
			queryCnd=list4;
		}else{
			list5.add("and");
			list5.add(list3);
			list5.add(cnd);
			list5.add(cnd2);
			queryCnd=list5;
		}
		String sqlName="";
		PublicModel hrModel=new PublicModel(dao);
		Map<String,Object> resBody=new HashMap<String,Object>();
		resBody=hrModel.queryRecordList(schemaId,queryCnd,queryCndsType,sortInfo,limit,start,year,checkType);
		List<Map<String,Object>> map=(List<Map<String,Object>>)resBody.get("body");
		Map<String,Object> mapDo=null;
		if(map!=null){
			for(int i=0;i<map.size();i++){
				String manaDoctorId=(String)map.get(i).get("manaDoctorId");
				sqlName="select a.personName from sys_personnel a where a.personId='"+manaDoctorId+"'";
				try{
					mapDo=dao.doSqlLoad(sqlName,null);
				}catch(PersistentDataOperationException e){
					e.printStackTrace();
				}
				Date birthday=(Date)map.get(i).get("birthday");
				if(map.get(i).get(" ")!=null){
					map.get(i).put("manaDoctorName",mapDo.get("PERSONNAME"));
				}else{
					map.get(i).put("manaDoctorName","");
				}
				if(map.get(i).get("birthday")!=null){
					int age=MobileAppService.calculateAge(birthday,null);
					map.get(i).put("age",age);
				}else{
					map.get(i).put("age","");
				}
			}
		}
		List<Map<String,Object>> resBodyList=new ArrayList<Map<String,Object>>();
		resBodyList=(List<Map<String,Object>>)resBody.get("body");
		resBody.remove("body");
		res.put("data",resBodyList);
		res.put(Service.RES_CODE,200);
		res.put(Service.RES_MESSAGE,"success");
	}
	//糖尿病档案列表查询-新版app
	public void doQueryDiabetesArchiveList(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ServiceException{
		String arg=(String)req.get("content");
		DiabetesRecordService drs=new DiabetesRecordService();
		req.put("schema","chis.application.mobileApp.schemas.MDC_DiabetesRecordApp");
		List<?> cnd=null;
		String manaDoctorId=UserRoleToken.getCurrent().getUserId();
		try{
			if(arg!=null&&!arg.equals("")){
				cnd=CNDHelper.toListCnd(""+"['and',['in',['$','a.status'],['0','2']],"+"['eq',['$','a.manaDoctorId'],['s','"
						+manaDoctorId+"']],"+"['or', ['like',['$','c.regionCode_text'],['s','%"+arg+"%']]"
						+",['like', ['$', 'b.personName'], ['s', '%"+arg+"%']],['like',"+"['$','b.idCard'], ['s', '%"+arg+"%']]]]");
			}else{
				cnd=CNDHelper.toListCnd("['and',['in',['$','a.status'],['0','2']],['eq',['$','a.manaDoctorId'],['s','"
				+manaDoctorId+"']]]");
			}
		}catch(ExpException e){
			res.put("data",null);
			res.put(Service.RES_CODE,500);
			res.put(Service.RES_MESSAGE,"fail");
			logger.error("查询糖尿病档案列表失败");
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,"查询糖尿病档案列表失败",e);
		}
		req.put("cnd",cnd);
		try{
			drs.doListDiabetesRecord(req,res,dao,ctx);
				res.put("data",res.get("body"));
				res.remove("body");
		}catch(Exception e){
			res.put("data",null);
			res.put(Service.RES_CODE,500);
			res.put(Service.RES_MESSAGE,"fail");
			logger.error("查询糖尿病档案列表失败");
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,"查询糖尿病档案列表失败",e);
		}
	}
	//高血压档案列表查询-新版app
	public void doQueryHypertensionArchiveList(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ServiceException,ExpException{
		String arg=(String)req.get("content");
		HypertensionService hs=new HypertensionService();
		req.put("schema","chis.application.mobileApp.schemas.MDC_HypertensionRecordApp");
		List<?> cnd=null;
		String manaDoctorId=UserRoleToken.getCurrent().getUserId();
		if(arg!=null&&!arg.equals(" ")){
			cnd=CNDHelper.toListCnd(""+"['and',['in',['$','a.status'],['0','2']],"+"['eq',['$','a.manaDoctorId'],['s','"
			+manaDoctorId+"']],"+"['or', ['like',['$','c.regionCode_text'],['s','%"+arg+"%']]"
			+",['like', ['$', 'b.personName'], ['s', '%"+arg+"%']],['like',"+"['$','b.idCard'], ['s', '%"+arg+"%']]]]");
		}else{
			cnd=CNDHelper.toListCnd("['and',['in',['$','a.status'],['0','2']],['eq',['$','a.manaDoctorId'],['s','"
			+manaDoctorId+"']]]");
		}
		req.put("cnd",cnd);
		int pageNo=10;
		if(req.containsKey("pageNo")){
			pageNo=(Integer)req.get("pageNo");
		}
		int pageSize=1;
		if(req.containsKey("pageSize")){
			pageSize=(Integer)req.get("pageSize");
		}
		req.put("pageSize",pageSize);
		req.put("pageNo",pageNo);
		try{
			hs.doListHypertensionRecord(req,res,dao,ctx);
		}catch(Exception e){
			res.put(Service.RES_CODE,500);
			res.put(Service.RES_MESSAGE,"fail");
			logger.error("查询高血压档案列表失败");
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,"查询高血压档案列表失败",e);
		}
		List<Map<String,Object>> list=(List<Map<String,Object>>)res.get("body");
		if(list==null){
			res.put("data",new ArrayList<Map<String,Object>>());
		}else{
					res.put("data",res.get("body"));
					res.remove("body");
					res.put("code",200);
					res.put("msg","success");
		}
	}
	//查询健康随访列表数据-新版app
	public void doQueryVisitPlans(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
	throws ServiceException{
		String arg=(String)req.get("content");
		if(arg!=null&&!arg.equals(" ")){
			req.put("cnd",arg);
		}
		VisitPlanModels drm=new VisitPlanModels(dao);
		Map<String,Object> rsMap=null;
		try{
			rsMap=drm.listVistPlan(req);
		}catch(Exception e){
			res.put("data",null);
			res.put(Service.RES_MESSAGE,"fail");
			res.put(Service.RES_CODE,500);
			logger.error("list VisitPlan failed.",e);
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,"查询随访列表内容失败!",e);
		}
		res.putAll(rsMap);
	}
	//查询该个人档案数据-新版app
	public void doGetArchiveDetail(Map<String, Object> jsonReq,Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
	throws ServiceException {
		Map<String, Object> map = new HashMap<String, Object>();
		QueryModel query = new QueryModel(dao);
		String empiId = "";
		String sqlName = " ";
		Map<String, Object> mapDo = null;
		if (jsonReq.containsKey("empiId")) {
			empiId = (String) jsonReq.get("empiId");
		}
		String schemaId = "chis.application.mobileApp.schemas.EHR_HealthRecordApp";
		map = query.queryInfo(empiId, schemaId, dao);
		Date birthday = (Date) map.get("BIRTHDAY");
		String manaDoctorId = (String) map.get("MANADOCTORID");
		sqlName = "select a.personName from sys_personnel a where a.personId='"+manaDoctorId+"'";
		try {
			mapDo = dao.doSqlLoad(sqlName, null);
		} catch (PersistentDataOperationException e) {
			jsonRes.put("data", null);
			jsonRes.put(Service.RES_CODE, 500);
			jsonRes.put(Service.RES_MESSAGE, "fail");
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,"查询个人档案内容失败!", e);
		}
		if (map.get("BIRTHDAY") != null) {
			int age = MobileAppService.calculateAge(birthday, null);
			map.put("AGE", age);
		} else {
			map.put("AGE", "");
		}
		if (map.get("MANADOCTORID") != null) {
			int age = MobileAppService.calculateAge(birthday, null);
			map.put("MANADOCTORID_TEXT", mapDo.get("PERSONNAME"));
		} else {
			map.put("MANADOCTORID_TEXT", "");
		}
		Map<String, Object> resBody = new HashMap<String, Object>();
		resBody.put("data", map);
		resBody.put(Service.RES_CODE, 200);
		resBody.put(Service.RES_MESSAGE, "success");
		jsonRes.putAll(resBody);
		}
		
		//查询该个人档案数据-新版app--wangjl  2019-06-20
		public void doGetDetail(Map<String, Object> jsonReq,Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
		throws ServiceException {
			Map<String, Object> map = new HashMap<String, Object>();
			QueryModel query = new QueryModel(dao);
			String empiId = "";
			String sqlName = " ";
			Map<String, Object> mapDo = null;
			if (jsonReq.containsKey("empiId")) {
				empiId = (String) jsonReq.get("empiId");
			}
			String schemaId = "chis.application.mobileApp.schemas.EHR_HealthRecordApp1";
			map = query.querygrInfo(empiId, schemaId, dao);
			String manaDoctorId = (String) map.get("MANADOCTORID");
			sqlName = "select a.personName from ehr_healthrecord b,sys_personnel a where  a.personId=b.manadoctorid and b.empiid='"+empiId+"'";
			try {
				mapDo = dao.doSqlLoad(sqlName, null);
			} catch (PersistentDataOperationException e) {
				jsonRes.put("data", null);
				jsonRes.put(Service.RES_CODE, 500);
				jsonRes.put(Service.RES_MESSAGE, "fail");
				throw new ServiceException(Constants.CODE_DATABASE_ERROR,"查询个人档案内容失败!", e);
			}
			Map<String, Object> resBody = new HashMap<String, Object>();
			resBody.put("data", map);
			resBody.put(Service.RES_CODE, 200);
			resBody.put(Service.RES_MESSAGE, "success");
			jsonRes.putAll(resBody);
			}
		
		
	//新增个人档案数据-新版app
	public void doAddArchive(Map<String, Object> jsonReq,Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
	throws ServiceException {
		String op = "create";
		String entryName = "chis.application.mpi.schemas.MPI_DemographicInfo";
		String entryName2 = "chis.application.hr.schemas.EHR_LifeStyle";
		String entryNameFamily = "chis.application.fhr.schemas.EHR_FamilyMiddle";
		String empiId = "";
		Map<String, Object> data = (Map<String, Object>) jsonReq.get("data");
		String idCard = (String) data.get("idCard");
		Map<String, Object> records = new HashMap<String, Object>();
		try {
			if (data.get("empiId") != null && !"".equals(data.get("empiId"))) {
				empiId = (String) data.get("empiId");
				if (getIfNeedPix(dao, ctx)) {
					Map<String, Object> map = empiInterfaceImpi.updatePerson(records);
					empiId = (String) map.get("mpiId");
					String versionNumber = (String) map.get("versionNumber");
					data.put("versionNumber", versionNumber);
				}
			} else {
				if (getIfNeedPix(dao, ctx)) {
					Map<String, Object> map = empiInterfaceImpi.submitPerson(records);
					empiId = (String) map.get("mpiId");
					String versionNumber = (String) map.get("versionNumber");
					data.put("versionNumber", versionNumber);
				} else {
					empiId = EmpiIdGenerator.generate();
				}
			}
			if (empiId != null) {
				data.put("empiId", empiId);
				data.put("op", "create");
			} else {
				return;
			}
			records.putAll(data);
			jsonRes.put("body", data);
			String sqlMpi = " select * from  MPI_DemographicInfo a where a.idCard='"+ idCard + "'";
			Map<String, Object> mapMpi = null;
			try {
				mapMpi = dao.doSqlLoad(sqlMpi, null);
			} catch (PersistentDataOperationException e1) {
				e1.printStackTrace();
			}
			if (mapMpi == null || mapMpi.isEmpty()) {
				EmpiModel empiModel = new EmpiModel(dao);
				empiModel.saveEmpiRecord(data, "create", false);
			} else {
				empiId = (String) mapMpi.get("EMPIID");
				data.put("empiId", empiId);
				EmpiModel empiModel = new EmpiModel(dao);
				empiModel.saveEmpiRecord(data, "update", false);
			}
			String sqlQuery = " select * from  EHR_HealthRecord a where a.empiId='"+ empiId + "'";
			Map<String, Object> map = null;
			map = dao.doSqlLoad(sqlQuery, null);
			if (map != null && !map.isEmpty()) {
				jsonRes.put("msg", "改用户已经存在健康档案信息");
				return;
			}
			// 是否存在基本信息
			jsonReq.put("op", "create");
			Map<String, Object> body = (HashMap<String, Object>) jsonReq.get("data");
			body.put("empiId", empiId);
			body.remove("signFlag");
			String empiId1 = (String) body.get("empiId");
			String op1 = (String) jsonReq.get("op");
			String status = (String) body.get("status");
			String masterFlag = (String) body.get("masterFlag");
			String relaCode = (String) body.get("relaCode");
			String regionCode = (String) body.get("regionCode");
			String phrId = (String) body.get("phrId");
			String familyId = (String) body.get("familyId");
			String areaGridType = (String) body.get("areaGridType");
			String manaUnitId = (String) body.get("manaUnitId");
			Context c = new Context();
			c.put("regionCode", regionCode);
			ctx.put("codeCtx", c);
			Map<String, Object> resBody = null;
			try {
				HealthRecordModel healthRecordModel = new HealthRecordModel(dao);
				if (YesNo.YES.equals(masterFlag) || RelatedCode.MASTER.equals(relaCode)) {
					if (StringUtils.isEmpty(familyId) && !"part".equals(areaGridType)) {
						FamilyRecordModule familyModel = new FamilyRecordModule(dao);
						familyId = familyModel.getFamilyIdByRegionCode(regionCode);
					}
					if (StringUtils.isNotEmpty(familyId)) {
						Map<String, Object> masterRecords = healthRecordModel.getMasterRecordByFamilyId(familyId);
						if (masterRecords != null) {
							String masterPhrId = (String) masterRecords.get("phrId");
							if (masterPhrId != null && !masterPhrId.equals(phrId)) {
								if (!"part".equals(areaGridType)) {
									String message = "["+ (String) masterRecords.get("regionCode_text")
											+ "]地址已经存在户主,无法在同一家庭建立两个户主!";
									jsonRes.put(Service.RES_CODE,Constants.CODE_RECORD_EXSIT);
									jsonRes.put(Service.RES_MESSAGE, message);
									return;
								}
							}
						}
					}
				}
				// ****审核档案,设置状态为正常****
				data.put("status", Constants.CODE_STATUS_NORMAL);
				data.put("masterFlag", masterFlag);
				data.put("manaUnitId", manaUnitId);
				data.put("signFlag", "n");
				// 查找个人档案姓名
				EmpiModel empiModel = new EmpiModel(dao);
				Map<String, Object> empiInfo = empiModel
						.getEmpiInfoByEmpiid(empiId);
				String preferRelaCode = null;
				if ("update".equals(op)) {
					Map<String, Object> preferRecord = healthRecordModel.getHealthRecordByPhrId(phrId);
					preferRelaCode = (String) preferRecord.get("relaCode");
				}
		
				String personName = (String) empiInfo.get("personName");
				String sexCode = (String) empiInfo.get("sexCode");
				body.put("familyId", familyId);
				body.put("personName", personName);
				body.put("sexCode", sexCode);
				body.put("manaUnitId", manaUnitId);
				// 如果有家庭档案，判断是否进行户主更新。
				// 保存基本信息
				resBody = healthRecordModel.saveHealthRecord(op, data);
				phrId = (String) resBody.get("phrId");
			} catch (Exception e) {
				e.printStackTrace();
			}
			data.put("phrId", phrId);
			jsonRes.put("data", data);
			jsonRes.put(Service.RES_MESSAGE, "success");
			LifeStyleModel lsModel = new LifeStyleModel(dao);
			String lifeStyleId = (String) body.get("lifeStyleId");
			try {
				Map<String, Object> record = lsModel.saveLifeStyle(op, data);
				if (record != null) {
					if ("create".equals(op)) {
						lifeStyleId = (String) record.get("lifeStyleId");
					}
					resBody.put("lifeStyleId", lifeStyleId);
					if (vLogService != null) {
						vLogService.saveVindicateLog(EHR_LifeStyle, op,lifeStyleId, dao, empiId1);
					}
				}
			} catch (ModelDataOperationException e) {
				logger.error("save lifeStyle is fail");
				throw new ServiceException(e);
			}
			saveFamilyInfo(entryNameFamily, jsonReq, dao, jsonRes);
		} catch (ModelDataOperationException e) {
			jsonRes.put("data", null);
			jsonRes.put(Service.RES_CODE, 500);
			jsonRes.put(Service.RES_MESSAGE, "fail");
			throw new ServiceException("保存个人基本信息失败。", e);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		}	
	//高血压档案各档信息查询-新版app
	public void doGetHypertensionArchiveDetail(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ExpException, ModelDataOperationException {
		String phrId = null;
		String recordType = null;
		if (req.containsKey("phrId")) {
			phrId = (String) req.get("phrId");
		} else {
			res.put("msg", "错误参数");
			return;
		}
		List<?> cnd = CNDHelper.toListCnd("['eq',['$','a.phrId'],['s','"+phrId+"']]");
		Map<String, Object> resBody = new HashMap<String, Object>();
		Map<String, Object> rsHy = null;
		try {
			rsHy = dao.doLoad(cnd,"chis.application.mobileApp.schemas.MDC_HypertensionDetailApp");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if (rsHy != null && !rsHy.isEmpty()) {
			resBody.putAll(rsHy);
		}
		res.remove("body");
		// 获取药品信息
		List<Map<String, Object>> Map = null;
		try {
			Map = getHypertensionRecordMedicine(phrId, dao);
			resBody.put("hypertensionMedicines", Map);
		} catch (Exception e) {
			res.put("data", null);
			res.put("code", 500);
			res.put("msg", "查询药品信息失败信息失败!");
			throw new ModelDataOperationException(Constants.CODE_DATABASE_ERROR, "查询药品信息失败信息失败！");
		}
		res.put("data", resBody);
		res.put("code", 200);
		res.put("msg", "success");
	}
	//糖尿病档案各档信息查询-新版app
	public void doGetDiabetesArchiveDetail(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ExpException, ModelDataOperationException {
		String phrId = null;
		if (req.containsKey("phrId")) {
			phrId = (String) req.get("phrId");
		} else {
			res.put("msg", "错误参数");
			return;
		}
		List<?> cnd = CNDHelper.toListCnd("['eq',['$','a.phrId'],['s','"+phrId+"']]");
		Map<String, Object> resBody = new HashMap<String, Object>();
		Map<String, Object> rsDia = null;
		try {
			rsDia = dao.doLoad(cnd,
					"chis.application.mobileApp.schemas.MDC_DiabetesDetailApp");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if (rsDia != null && !rsDia.isEmpty()) {
			resBody.putAll(rsDia);
		}
		res.remove("body");
		// 获取药品信息
		List<Map<String, Object>> Map = null;
		try {
			Map = getDiabetesRecordMedicine(phrId, dao);
			resBody.put("diabetesMedicines", Map);
		} catch (Exception e) {
			res.put("data", null);
			res.put("code", 500);
			res.put("msg", "查询药品信息失败信息失败!");
			throw new ModelDataOperationException(Constants.CODE_DATABASE_ERROR, "查询药品信息失败信息失败！");
		}
		res.put("data", resBody);
		res.put("code", 200);
		res.put("msg", "success");
	}
	//验证身份是否被使用-新版app
	public void doValidateIdCardExisit(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		EmpiModel empiModel = new EmpiModel(dao);
		String idCard = (String) req.get("idCard");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> list = empiModel.getEmpiInfoByIdcard(idCard);
			String sql="select * from ehr_healthrecord a,mpi_demographicinfo b where a.empiid=b.empiid  and b.idcard='"+idCard+"'";
			Map<String, Object> parameters = null;
			try {
				parameters = dao.doSqlLoad(sql, null);
			} catch (PersistentDataOperationException e1) {
				e1.printStackTrace();
			}
			if (list.size() == 0) {
				map.put("isOk", "1");
				res.put("data", map);
				res.put("code", 200);
				res.put("msg", "success");
				return;
			}else if(parameters==null){
				map.put("isOk", "1");
				res.put("data", map);
				res.put("code", 200);
				res.put("msg", "success");
				return;
			}
		} catch (Exception e) {
			res.put("data", null);
			res.put("code", 500);
			res.put("msg", "fail");
		}
		map.put("isOk", "0");
		res.put("data", map);
		res.put("code", 200);
		res.put("msg", "success");
	}
	//身份验证高血压档案返回内容-新版app
	public void doValidateHypertensionArchiveExisit(Map<String, Object> req,
	Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ExpException, ModelDataOperationException {
		String regionCode = UserRoleToken.getCurrent().getRegionCode();
		EmpiModel empiModel = new EmpiModel(dao);
		res.remove("data");
		// 查看是否有个人基本信息
		String idCard = (String) req.get("idCard");
		idCard = idCard.toUpperCase();
		String schema = EHR_HealthRecord;
		List<?> cnd = CNDHelper.toListCnd("['eq',['$','b.idCard'],['s','"+idCard+"']]");
		List<Map<String, Object>> rsMap = QueryList(dao, schema, cnd);
		Map<String, Object> remap=new HashMap<String,Object>();
		String validate="1";
		// 1有基本信息 0无基本信息
		if (rsMap != null && !rsMap.isEmpty()) {
			remap.putAll(rsMap.get(0));
			validate="0";
		} else {
			List<Map<String, Object>> list = null;
			try {
				list = empiModel.getEmpiInfoByIdcard(idCard);
			} catch (ModelDataOperationException e) {
				e.printStackTrace();
			}
			// 查看是否有个人健康档案
			if (list != null && list.size() != 0) {
				String empiId = (String) list.get(0).get("empiId");
				HealthRecordModel hrm = new HealthRecordModel(dao);
				Map<String, Object> mapHeal = null;
				try {
					mapHeal = hrm.getHealthRecordByEmpiId(empiId);
				} catch (ModelDataOperationException e) {
					e.printStackTrace();
				}
				String regionCode2 = (String) mapHeal.get("regionCode");
				Map<String, Object> resList = new HashMap<String, Object>();
				if (mapHeal != null && mapHeal.size() != 0) {
					validate="2";
					resList = (Map<String, Object>) list.get(0);
					String personName = (String) resList.get("personName");
					mapHeal.put("personName", personName);
					remap.putAll(mapHeal);
					if (regionCode2 == null || regionCode2.indexOf(regionCode) == -1) {
						validate="3";
					}
				} else {
					validate="1";
				}
			} else {
				validate="1";
			}
		}
		remap.put("validate",validate);
		res.put("body",remap);
	}
	//糖尿病身份验证返回内容-新版app
	public void doValidateDiabetesArchiveExisit(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ExpException, ModelDataOperationException {
		String regionCode = UserRoleToken.getCurrent().getRegionCode();
		EmpiModel empiModel = new EmpiModel(dao);
		res.remove("data");
		// 查看是否有个人基本信息
		String idCard = (String) req.get("idCard");
		idCard = idCard.toUpperCase();
		String schema = EHR_HealthRecord;
		List<?> cnd = CNDHelper.toListCnd("['eq',['$','b.idCard'],['s','"+idCard+"']]");
		List<Map<String, Object>> rsMap = QueryList(dao, schema, cnd);
		Map<String, Object> remap=new HashMap<String,Object>();
		String validate="1";
		if (rsMap != null && !rsMap.isEmpty()) {
			remap.putAll(rsMap.get(0));
			validate="0";
		} else {
			List<Map<String, Object>> list = null;
			try {
				list = empiModel.getEmpiInfoByIdcard(idCard);
			} catch (ModelDataOperationException e) {
				e.printStackTrace();
			}
			// 查看是否有个人健康档案
			if (list != null && list.size() != 0) {
				String empiId = (String) list.get(0).get("empiId");
				HealthRecordModel hrm = new HealthRecordModel(dao);
				Map<String, Object> mapHeal = null;
				try {
					mapHeal = hrm.getHealthRecordByEmpiId(empiId);
				} catch (ModelDataOperationException e) {
					e.printStackTrace();
				}
				String regionCode2 = (String) mapHeal.get("regionCode");
				Map<String, Object> resList = new HashMap<String, Object>();
				if (mapHeal != null && mapHeal.size() != 0) {
					validate="2";
					resList = (Map<String, Object>) list.get(0);
					String personName = (String) resList.get("personName");
					mapHeal.put("personName", personName);
					remap.putAll(mapHeal);
					if (regionCode2 == null || regionCode2.indexOf(regionCode) == -1) {
						validate="3";
					}
				} else {
					validate="1";
				}
			} else {
				validate="1";
			}
		}
		remap.put("validate",validate);
		res.put("data",remap);
	}
	//保存个人健康档案信息-新版app
	public void doUpdateArchive(Map<String, Object> jsonReq,Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
	throws ServiceException, ModelDataOperationException {
		String op = "create";
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("data");
		Context c = new Context();
		c.put("regionCode", (String) body.get("regionCode"));
		ctx.put("codeCtx", c);
		String personName = (String) body.get("personName") == null ? "":(String) body.get("personName");
		String empiId = (String) body.get("empiId") == null ? "": (String) body.get("empiId");
		String birthday = (String) body.get("birthday") == null ? "": (String) body.get("birthday");
		String bloodTypeCode = (String) body.get("bloodTypeCode") == null ? "": (String) body.get("bloodTypeCode");
		String contact = (String) body.get("contact") == null ? "": (String) body.get("contact");
		String contactPhone = (String) body.get("contactPhone") == null ? "": (String) body.get("contactPhone");
		String educationCode = (String) body.get("educationCode") == null ? "": (String) body.get("educationCode");
		String idCard = (String) body.get("idCard") == null ? "": (String) body.get("idCard");
		String insuranceCode = (String) body.get("insuranceCode") == null ? "": (String) body.get("insuranceCode");
		String manaDoctorId = (String) body.get("manaDoctorId") == null ? "": (String) body.get("manaDoctorId");
		String maritalStatusCode = (String) body.get("maritalStatusCode") == null ? "": (String) body.get("maritalStatusCode");
		String masterFlag = (String) body.get("masterFlag") == null ? "": (String) body.get("masterFlag");
		String mobileNumber = (String) body.get("mobileNumber") == null ? "": (String) body.get("mobileNumber");
		String nationCode = (String) body.get("nationCode") == null ? "": (String) body.get("nationCode");
		String regionCode = (String) body.get("regionCode") == null ? "": (String) body.get("regionCode");
		String regionCode_text = (String) body.get("regionCode_text") == null ? "": (String) body.get("regionCode_text");
		String registeredPermanent = (String) body.get("registeredPermanent") == null ? "": (String) body.get("registeredPermanent");
		String rhBloodCode = (String) body.get("rhBloodCode") == null ? "": (String) body.get("rhBloodCode");
		String sexCode = (String) body.get("sexCode") == null ? "": (String) body.get("sexCode");
		String smokeFreqCode = (String) body.get("smokeFreqCode") == null ? "": (String) body.get("smokeFreqCode");
		String drinkFreqCode = (String) body.get("drinkFreqCode") == null ? "": (String) body.get("drinkFreqCode");
		String trainFreqCode = (String) body.get("trainFreqCode") == null ? "": (String) body.get("trainFreqCode");
		String eateHabit = (String) body.get("eateHabit") == null ? "": (String) body.get("eateHabit");
		String workCode = (String) body.get("workCode") == null ? "": (String) body.get("workCode");
		String incomeSource = (String) body.get("incomeSource") == null ? "": (String) body.get("incomeSource");
		String isAgrRegister = (String) body.get("isAgrRegister") == null ? "": (String) body.get("isAgrRegister");
		Map<String, Object> par = new HashMap<String, Object>();
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s", empiId);
		List<Map<String, Object>> info;
		try {
			info = dao.doList(cnd, null, EHR_LifeStyle);
			LifeStyleModel lsModel = new LifeStyleModel(dao);
			String lifeStyleId = (String) body.get("lifeStyleId");
			Map<String, Object> resBody = new HashMap<String, Object>();
			if (info.size() == 0) {
		
				Map<String, Object> record = lsModel.saveLifeStyle(op, body);
				if ("create".equals(op)) {
					lifeStyleId = (String) record.get("lifeStyleId");
				}
				resBody.put("lifeStyleId", lifeStyleId);
				if (vLogService != null) {
					vLogService.saveVindicateLog(EHR_LifeStyle, op,lifeStyleId, dao, empiId);
				}
			}
			String sql = "update  MPI_DemographicInfo set birthday=to_date('"
					+ birthday + "','yyyy-MM-dd'),idCard='" + idCard
					+ "',personName='" + personName + "',bloodTypeCode='"
					+ bloodTypeCode + "'," + "contact='" + contact
					+ "',contactPhone='" + contactPhone + "',educationCode='"
					+ educationCode + "'" + ",insuranceCode='" + insuranceCode
					+ "',maritalStatusCode='" + maritalStatusCode
					+ "',mobileNumber='" + mobileNumber + "',nationCode='"
					+ nationCode + "',registeredPermanent='"
					+ registeredPermanent + "'," + "rhBloodCode='"
					+ rhBloodCode + "',sexCode='" + sexCode + "',workCode='"
					+ workCode + "' where empiId='" + empiId + "'";
			// 个人信息更新
			dao.doUpdate(sql, par);
//			String sqls = "update EHR_HealthRecord set manaDoctorId='"
//					+ manaDoctorId + "',masterFlag='" + masterFlag
//					+ "',regionCode='" + regionCode + "',regionCode_text='"
//					+ regionCode_text + "',isAgrRegister='" + isAgrRegister
//					+ "',incomeSource='" + incomeSource + "'"
//					+ " where empiId='" + empiId + "'";
			String sqls = "update EHR_HealthRecord set manaDoctorId='"
					+ manaDoctorId + "',masterFlag='" + masterFlag
					+ "',regionCode='" + regionCode + "',isAgrRegister='" + isAgrRegister
					+ "',incomeSource='" + incomeSource + "'"
					+ " where empiId='" + empiId + "'";
			// 健康档案更新
			dao.doUpdate(sqls, par);
		
			// String sqlXg = "update EHR_LifeStyle set smokeFreqCode='"
			// + smokeFreqCode + "',drinkFreqCode='" + drinkFreqCode
			// + "',trainFreqCode='" + trainFreqCode + "',eateHabit='"
			// + eateHabit + "' where empiId='" + empiId + "'";
			// 生活习惯更新
			// dao.doUpdate(sqlXg, par);
		
			List<Map<String, Object>> faMap = null;
			// 生活环境更新
			faMap = dao.doList(cnd, null, EHR_FamilyMiddle);
			if (faMap != null && !faMap.isEmpty()) {
				String cookAirTool = (String) body.get("cookAirTool");
				String fuelType = (String) body.get("fuelType");
				String livestockColumn = (String) body.get("livestockColumn");
				String washroom = (String) body.get("washroom");
				String waterSourceCode = (String) body.get("waterSourceCode");
				String sqlHJ = "update EHR_FamilyMiddle set cookAirTool='"
						+ cookAirTool + "',fuelType='" + fuelType
						+ "',livestockColumn='" + livestockColumn
						+ "',washroom='" + washroom + "',waterSourceCode='"
						+ waterSourceCode + "' where empiId='" + empiId + "'";
				dao.doUpdate(sqlHJ, par);
			} else {
				dao.doSave("create", EHR_FamilyMiddle, body, false);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("failed to get child base message.", e);
			jsonRes.put("data", null);
			jsonRes.put(Service.RES_CODE, 500);
			jsonRes.put(Service.RES_MESSAGE, "fail");
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,"档案信息更新失败", e);
		}
	}
	
	
	//保存个人健康档案信息-新版app--Wangjl 2019.05.22
		public void doUpdatenewArchive(Map<String, Object> jsonReq,Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
		throws ServiceException, ModelDataOperationException, ParseException {
			String op = "create";
			Map<String, Object> bodyjson = (Map<String, Object>) jsonReq.get("data");
			Map<String, Object> body = (Map<String, Object>) bodyjson.get("archiveDetail");
			Context c = new Context();
			c.put("regionCode", (String) body.get("regionCode"));
			ctx.put("codeCtx", c);
			//基本档案--mpi
			String empiId = (String) body.get("empiId") == null ? "": (String) body.get("empiId");
			String personName = (String) body.get("personName") == null ? "":(String) body.get("personName");//姓名
			String sexCode = (String) body.get("sexCode") == null ? "": (String) body.get("sexCode");//性别
			String birthday = (String) body.get("birthday") == null ? "": (String) body.get("birthday");//出生日期
			String workPlace = (String) body.get("workPlace") == null ? "": (String) body.get("workPlace");//工作单位
			String mobileNumber = (String) body.get("mobileNumber") == null ? "": (String) body.get("mobileNumber");//本人电话
			String address = (String) body.get("address") == null ? "": (String) body.get("address");//现地址
			String contact = (String) body.get("contact") == null ? "": (String) body.get("contact");//联系人
			String contactPhone = (String) body.get("contactPhone") == null ? "": (String) body.get("contactPhone");//联系电话
			String bloodTypeCode = (String) body.get("bloodTypeCode") == null ? "": (String) body.get("bloodTypeCode");//血型
			String rhBloodCode = (String) body.get("rhBloodCode") == null ? "": (String) body.get("rhBloodCode");//RH血型
			String educationCode = (String) body.get("educationCode") == null ? "": (String) body.get("educationCode");//文化程度
			String maritalStatusCode = (String) body.get("maritalStatusCode") == null ? "": (String) body.get("maritalStatusCode");//婚姻状况
			String nationCode = (String) body.get("nationCode") == null ? "": (String) body.get("nationCode");//民族代码
			String insuranceCode = (String) body.get("insuranceCode") == null ? "": (String) body.get("insuranceCode");//医疗支付方式
			String workCode = (String) body.get("workCode") == null ? "": (String) body.get("workCode");//职业
			
			//基本档案--mpi
			String phrId = (String) body.get("PHRID") == null ? "": (String) body.get("PHRID");//档案编号
			String createDate = (String) body.get("createDate") == null ? "": (String) body.get("createDate");//建档日期
			String signFlag = (String) body.get("signFlag") == null ? "": (String) body.get("signFlag");//签约标志
			String isAgrRegister = (String) body.get("isAgrRegister") == null ? "": (String) body.get("isAgrRegister");//是否农业户籍
			String regionCode = (String) body.get("regionCode") == null ? "": (String) body.get("regionCode");//网格地址编号 pad上不做修改
			String incomeSource = (String) body.get("incomeSource") == null ? "": (String) body.get("incomeSource");//经济来源
			String masterFlag = (String) body.get("masterFlag") == null ? "": (String) body.get("masterFlag");//是否户主
			String manaDoctorId = (String) body.get("manaDoctorId") == null ? "": (String) body.get("manaDoctorId");//责任医生工号
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			//Date createDate=sdf.parse(createDate1);
			Map<String, Object> par = new HashMap<String, Object>();
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s", empiId);
			List<Map<String, Object>> info;
			try {
				info = dao.doList(cnd, null, EHR_LifeStyle);
				LifeStyleModel lsModel = new LifeStyleModel(dao);
				String lifeStyleId = (String) body.get("lifeStyleId");
				Map<String, Object> resBody = new HashMap<String, Object>();
				if (info.size() == 0) {
			
					Map<String, Object> record = lsModel.saveLifeStyle(op, body);
					if ("create".equals(op)) {
						lifeStyleId = (String) record.get("lifeStyleId");
					}
					resBody.put("lifeStyleId", lifeStyleId);
					if (vLogService != null) {
						vLogService.saveVindicateLog(EHR_LifeStyle, op,lifeStyleId, dao, empiId);
					}
				}

				// mpi更新
				String sql = "update  MPI_DemographicInfo set birthday=to_date('"+ birthday + "','yyyy-MM-dd'),personName='" + personName
						+ "',bloodTypeCode='"+ bloodTypeCode 
						+ "'," + "contact='" + contact
						+ "',contactPhone='" + contactPhone 
						+ "',educationCode='"+ educationCode 
						+ "'" + ",insuranceCode='" + insuranceCode
						+ "',maritalStatusCode='" + maritalStatusCode
						+ "',mobileNumber='" + mobileNumber
						+ "',nationCode='"+ nationCode 
						+ "',rhBloodCode='"+ rhBloodCode 
						+ "',sexCode='" + sexCode 
						+ "',workPlace='" + workPlace
						+ "',workCode='" + workCode
						+ "',address='" + address
						+ "' where empiId='" + empiId + "'";
				dao.doUpdate(sql, par);
				
				// 健康档案更新
				String sqls = "update EHR_HealthRecord set masterFlag='" + masterFlag
						+ "',isAgrRegister='" + isAgrRegister
						+ "',incomeSource='" + incomeSource
						+ "',createDate=to_date(to_char(to_date('"+ createDate  + "','yyyy-MM-dd hh24:mi:ss'),'yyyy-MM-dd'),'yyyy-MM-dd')"
						+ ",signFlag='" + signFlag 
						+ "' where empiId='" + empiId + "'";
				dao.doUpdate(sqls, par);
			
				List<Map<String, Object>> faMap = null;
				// 生活环境更新
				faMap = dao.doList(cnd, null, EHR_FamilyMiddle);
				if (faMap != null && !faMap.isEmpty()) {
					String cookAirTool = (String) body.get("cookAirTool");
					String fuelType = (String) body.get("fuelType");
					String livestockColumn = (String) body.get("livestockColumn");
					String washroom = (String) body.get("washroom");
					String waterSourceCode = (String) body.get("waterSourceCode");
					String sqlHJ = "update EHR_FamilyMiddle set cookAirTool='"
							+ cookAirTool + "',fuelType='" + fuelType
							+ "',livestockColumn='" + livestockColumn
							+ "',washroom='" + washroom + "',waterSourceCode='"
							+ waterSourceCode + "' where empiId='" + empiId + "'";
					dao.doUpdate(sqlHJ, par);
				} else {
					dao.doSave("create", EHR_FamilyMiddle, body, false);
				}
				
				// 既往史更新
				String diseasetext_check_gm = (String) body.get("drugAllergens") == null ? "": (String) body.get("drugAllergens");//药物过敏史
				String a_qt1 = (String) body.get("drugAllergenOther") == null ? "": (String) body.get("drugAllergenOther");//其他药物过敏史
				String diseasetext_check_bl = (String) body.get("riskFactorsTypeCod") == null ? "": (String) body.get("riskFactorsTypeCod");//暴露史
				String diseasetext_check_jb = (String) body.get("pastHiss") == null ? "": (String) body.get("pastHiss");//疾病史
				String confirmdate_gxy = (String) body.get("hypertension") == null ? "": (String) body.get("hypertension");//高血压确诊时间
				String confirmdate_tnb = (String) body.get("diabetes") == null ? "": (String) body.get("diabetes");//糖尿病确诊时间
				String confirmdate_gxb = (String) body.get("ache") == null ? "": (String) body.get("ache");//	冠心病确诊时间
				String confirmdate_mxzsxfjb = (String) body.get("manxingzuse") == null ? "": (String) body.get("manxingzuse");//慢性阻塞性肺疾病确诊时间
				String confirmdate_exzl = (String) body.get("stomacheTime") == null ? "": (String) body.get("stomacheTime");//	恶性肿瘤确诊时间
				String confirmdate_nzz = (String) body.get("nczqzsj") == null ? "": (String) body.get("nczqzsj");//	脑卒中确诊时间
				String confirmdate_zxjsjb = (String) body.get("jinshenbin") == null ? "": (String) body.get("jinshenbin");//重性精神疾病确诊时间
				String confirmdate_jhb = (String) body.get("jiehebing") == null ? "": (String) body.get("jiehebing");//	结核病确诊时间	
				String confirmdate_gzjb = (String) body.get("ganyan") == null ? "": (String) body.get("ganyan");//	肝脏疾病确诊时间
				String confirmdate_xtjx = (String) body.get("xtjxqzsj") == null ? "": (String) body.get("xtjxqzsj");//	先天畸形确诊时间
				String confirmdate_px = (String) body.get("pxqzsj") == null ? "": (String) body.get("pxqzsj");//		贫血确诊时间
				String confirmdate_szjb = (String) body.get("szjbqzsj") == null ? "": (String) body.get("szjbqzsj");//		肾脏疾病确诊时间
				String diseasetext_zyb = (String) body.get("zhiyebin") == null ? "": (String) body.get("zhiyebin");//职业病
				String confirmdate_zyb = (String) body.get("zhiyebinshijian") == null ? "": (String) body.get("zhiyebinshijian");//职业病确诊时间
				String diseasetext_qtfdcrb = (String) body.get("chuanranbin") == null ? "": (String) body.get("chuanranbin");//其他法定传染病
				String confirmdate_qtfdcrb = (String) body.get("qtfdcrbqzsj") == null ? "": (String) body.get("qtfdcrbqzsj");//其他法定传染病确诊时间
				String diseasetext_qt = (String) body.get("qtjbs") == null ? "": (String) body.get("qtjbs");//其他疾病史
				String confirmdate_qt = (String) body.get("qtjbsqzsj") == null ? "": (String) body.get("qtjbsqzsj");//	其他疾病史确诊时间
				String diseasetext_ss = (String) body.get("operationHistorys") == null ? "": (String) body.get("operationHistorys");//手术史
				String diseasetext_ss0 = (String) body.get("operationHistory") == null ? "": (String) body.get("operationHistory");//手术名称1
				String startdate_ss0 = (String) body.get("operationTime") == null ? "": (String) body.get("operationTime");//手术时间1
				String diseasetext_ss1 = (String) body.get("operationHistory1") == null ? "": (String) body.get("operationHistory1");//手术名称2	
				String startdate_ss1 = (String) body.get("operationTime1") == null ? "": (String) body.get("operationTime1");//手术时间2
				String diseasetext_ws = (String) body.get("traumas") == null ? "": (String) body.get("traumas");//外伤史
				String diseasetext_ws0 = (String) body.get("traumaName") == null ? "": (String) body.get("traumaName");//外伤名称1	
				String startdate_ws0 = (String) body.get("traumaTime") == null ? "": (String) body.get("traumaTime");//外伤时间1
				String diseasetext_ws1 = (String) body.get("traumaName1") == null ? "": (String) body.get("traumaName1");//外伤名称2	
				String startdate_ws1 = (String) body.get("traumaTime1") == null ? "": (String) body.get("traumaTime1");//外伤时间2
				String diseasetext_sx = (String) body.get("bloodTransfusions") == null ? "": (String) body.get("bloodTransfusions");//输血史
				String diseasetext_sx0 = (String) body.get("transfusionReason") == null ? "": (String) body.get("transfusionReason");//输血原因1
				String startdate_sx0 = (String) body.get("transfusionsTime") == null ? "": (String) body.get("transfusionsTime");//输血时间1
				String diseasetext_sx1 = (String) body.get("transfusionReason1") == null ? "": (String) body.get("transfusionReason1");//输血原因2
				String startdate_sx1 = (String) body.get("transfusionTime1") == null ? "": (String) body.get("transfusionTime1");//输血时间2
				String diseasetext_check_fq = (String) body.get("fqs") == null ? "": (String) body.get("fqs");//	父亲家族史	
				String qt_fq1 = (String) body.get("fqo") == null ? "": (String) body.get("fqo");//父亲其他家族史
				String diseasetextCheckMQ = (String) body.get("mqs") == null ? "": (String) body.get("mqs");//母亲家族史
				String qt_mq1 = (String) body.get("mqsqt") == null ? "": (String) body.get("mqsqt");//母亲其他家族史
				String diseasetextCheckXDJM = (String) body.get("xds") == null ? "": (String) body.get("xds");//兄弟姐妹家族史
				String qt_xdjm1 = (String) body.get("xdo") == null ? "": (String) body.get("xdo");//兄弟姐妹其他家族史
				String diseasetextCheckZN = (String) body.get("zns") == null ? "": (String) body.get("zns");//子女家族史
				String qt_zn1 = (String) body.get("zno") == null ? "": (String) body.get("zno");//子女其他家族史
				String diseasetextRedioYCBS = (String) body.get("geneticDiseaseHist") == null ? "": (String) body.get("geneticDiseaseHist");//	遗传病史
				String diseasetextYCBS = (String) body.get("ycbsjbmc") == null ? "": (String) body.get("ycbsjbmc");//	遗传病史疾病名称
				String diseasetextCheckCJ = (String) body.get("disabilityCodes") == null ? "": (String) body.get("disabilityCodes");//残疾情况
				String cjqk_qtcj1 = (String) body.get("qtcjqk") == null ? "": (String) body.get("qtcjqk");//其他残疾情况
				BasicPersonalInformationModel model = new BasicPersonalInformationModel(dao, ctx);
				Map<String, Object> bodyMap1 = new HashMap<String, Object>();
				bodyMap1.put("empiId", empiId);
				bodyMap1.put("phrId", phrId);
				bodyMap1.put("manaDoctorId", manaDoctorId);
				bodyMap1.put("regionCode", regionCode);
				bodyMap1.put("cjqk_qtcj1", cjqk_qtcj1);
				bodyMap1.put("diseasetext_check_gm", diseasetext_check_gm);
				bodyMap1.put("a_qt1", a_qt1);
				bodyMap1.put("diseasetext_check_bl", diseasetext_check_bl);
				bodyMap1.put("diseasetext_check_jb", diseasetext_check_jb);
				bodyMap1.put("confirmdate_gxy", confirmdate_gxy);
				bodyMap1.put("confirmdate_tnb",confirmdate_tnb);
				bodyMap1.put("confirmdate_gxb", confirmdate_gxb);
				bodyMap1.put("confirmdate_mxzsxfjb", confirmdate_mxzsxfjb);
				bodyMap1.put("confirmdate_exzl", confirmdate_exzl);
				bodyMap1.put("confirmdate_nzz",confirmdate_nzz);
				bodyMap1.put("confirmdate_zxjsjb", confirmdate_zxjsjb);
				bodyMap1.put("confirmdate_jhb", confirmdate_jhb);
				bodyMap1.put("confirmdate_gzjb", confirmdate_gzjb);
				bodyMap1.put("confirmdate_xtjx",confirmdate_xtjx);
				bodyMap1.put("confirmdate_px", confirmdate_px);
				bodyMap1.put("confirmdate_szjb", confirmdate_szjb);
				bodyMap1.put("diseasetext_zyb", diseasetext_zyb);
				bodyMap1.put("confirmdate_zyb",confirmdate_zyb);
				bodyMap1.put("diseasetext_qtfdcrb", diseasetext_qtfdcrb);
				bodyMap1.put("confirmdate_qtfdcrb",confirmdate_qtfdcrb);
				bodyMap1.put("diseasetext_qt", diseasetext_qt);
				bodyMap1.put("confirmdate_qt", confirmdate_qt);
				bodyMap1.put("diseasetext_ss", diseasetext_ss);
				bodyMap1.put("diseasetext_ss0", diseasetext_ss0);
				bodyMap1.put("startdate_ss0", startdate_ss0);
				bodyMap1.put("diseasetext_ss1", diseasetext_ss1);
				bodyMap1.put("startdate_ss1", startdate_ss1);
				bodyMap1.put("diseasetext_ws", diseasetext_ws);
				bodyMap1.put("diseasetext_ws0", diseasetext_ws0);
				bodyMap1.put("startdate_ws0", startdate_ws0);
				bodyMap1.put("diseasetext_ws1", diseasetext_ws1);
				bodyMap1.put("startdate_ws1", startdate_ws1);
				bodyMap1.put("diseasetext_sx", diseasetext_sx);
				bodyMap1.put("diseasetext_sx0", diseasetext_sx0);
				bodyMap1.put("startdate_sx0", startdate_sx0);
				bodyMap1.put("diseasetext_sx1", diseasetext_sx1);
				bodyMap1.put("startdate_sx1", startdate_sx1);
				bodyMap1.put("diseasetext_check_fq", diseasetext_check_fq);
				bodyMap1.put("qt_fq1", qt_fq1);
				bodyMap1.put("diseasetextCheckMQ", diseasetextCheckMQ);
				bodyMap1.put("qt_mq1", qt_mq1);
				bodyMap1.put("diseasetextCheckXDJM", diseasetextCheckXDJM);
				bodyMap1.put("qt_xdjm1", qt_xdjm1);
				bodyMap1.put("diseasetextCheckZN", diseasetextCheckZN);
				bodyMap1.put("qt_zn1", qt_zn1);
				bodyMap1.put("diseasetextRedioYCBS", diseasetextRedioYCBS);
				bodyMap1.put("diseasetextYCBS", diseasetextYCBS);
				bodyMap1.put("diseasetextCheckCJ", diseasetextCheckCJ);
				bodyMap1.put("cjqk_qtcj1", cjqk_qtcj1);
				if ("0201".equals((String)diseasetext_check_jb)) {
					diseasetext_check_jb=diseasetext_check_jb;
			    }else{
			    	bodyMap1.put("diseasetext_radio_jb", "02");
		      	}
				Map<String, Object> bodyMap = splitInfoBySchema(bodyMap1);
				bodyMap.put("empiId", empiId);
				// 对于个人既往史(家庭健康档案:不能删除)，修改的时候，先删除原来的信息，然后添加现在的信息
				// 删除个人既往史EHR_PASTHISTORY
				dao.doRemove("empiId", empiId, BSCHISEntryNames.EHR_PastHistory);
				
				// 保存药物过敏史
				model.savePastHistoryYWGMS(op, bodyMap);
				// 保存暴露史
				model.savePastHistoryBLS(op, bodyMap);
				// 保存疾病史
				model.savePastHistoryJBS(op, bodyMap);
				// 保存手术、外伤、输血史
				model.savePastHistorySSandWSandSX(op, bodyMap);
				// 保存遗传病、残疾
				model.savePastHistoryYCBandCJ(op, bodyMap);
				// 保存家族史
				model.savePastHistoryJZS(op, bodyMap);		
				vLogService.saveRecords("GRDA","create",dao,empiId);
			} catch (PersistentDataOperationException e) {
				logger.error("failed to get child base message.", e);
				jsonRes.put("data", null);
				jsonRes.put(Service.RES_CODE, 500);
				jsonRes.put(Service.RES_MESSAGE, "fail");
				throw new ServiceException(Constants.CODE_DATABASE_ERROR,"档案信息更新失败", e);
			}
		}
		private Map<String, Object> splitInfoBySchema(Map<String, Object> bodyMap1) {
			Map<String, Object> bodyMap = new HashMap<String, Object>();
			Schema sc = null;
			try {
				sc = SchemaController.instance().get(EHR_HealthRecord_JBXX);
			} catch (ControllerException e) {
				e.printStackTrace();
			}
			Map<String, Object> jbxx = new HashMap<String, Object>();
			Map<String, Object> ywgms = new HashMap<String, Object>();
			Map<String, Object> bls = new HashMap<String, Object>();
			Map<String, Object> jbs = new HashMap<String, Object>();
			Map<String, Object> ss = new HashMap<String, Object>();
			Map<String, Object> ws = new HashMap<String, Object>();
			Map<String, Object> sx = new HashMap<String, Object>();
			Map<String, Object> jzjbs = new HashMap<String, Object>();
			Map<String, Object> ycbs = new HashMap<String, Object>();
			Map<String, Object> cj = new HashMap<String, Object>();
			Map<String, Object> shhj = new HashMap<String, Object>();
		jbxx.put("cards", bodyMap1.get("cards"));
			List<SchemaItem> itemList = sc.getItems();
			for (SchemaItem si : itemList) {
				String type = (String) si.getProperty("lb");
				String key = si.getId();
				if (bodyMap1.get(key) == null||"null".equals(bodyMap1.get(key))) {
					continue;
				}
				Object value = bodyMap1.get(key);
			if ("jbxx".equals(type)) {
				jbxx.put(key, value);
	          } else if ("ywgms".equals(type)) {
					ywgms.put(key, value);
				} else if ("bls".equals(type)) {
					bls.put(key, value);
				} else if ("jbs".equals(type)) {
					jbs.put(key, value);
				} else if ("ss".equals(type)) {
					ss.put(key, value);
				} else if ("ws".equals(type)) {
					ws.put(key, value);
				} else if ("sx".equals(type)) {
					sx.put(key, value);
				} else if ("jzjbs".equals(type)) {
					jzjbs.put(key, value);
				} else if ("ycbs".equals(type)) {
					ycbs.put(key, value);
				} else if ("cj".equals(type)) {
					cj.put(key, value);
				}else if ("shhj".equals(type)) {
					shhj.put(key, value);
				}
			}
		    bodyMap.put("jbxx", jbxx);
			bodyMap.put("ywgms", ywgms);
			bodyMap.put("bls", bls);
			bodyMap.put("jbs", jbs);
			bodyMap.put("ss", ss);
			bodyMap.put("ws", ws);
			bodyMap.put("sx", sx);
			bodyMap.put("jzjbs", jzjbs);
			bodyMap.put("ycbs", ycbs);
			bodyMap.put("cj", cj);
			bodyMap.put("shhj", shhj);
			return bodyMap;
		}
	
	//保存随访记录（结合高血压，糖尿病，老年人-新版app
	protected void doAddVisit(Map<String, Object> req, Map<String, Object> res,BaseDAO dao, Context ctx) throws ServiceException,
	ControllerException {
		Map<String, Object> body = (Map<String, Object>) req.get("data");
		Map<String, Object> bodyGxy = (Map<String, Object>) req.get("data");
		Map<String, Object> bodyTnb = (Map<String, Object>) req.get("data");
		if (body == null) {
			return;
		}
		if (body.containsKey("hypertensionVisit")) {
			Map<String, Object> hypertensionVisit = (Map<String, Object>) body.get("hypertensionVisit");
			for (String s : hypertensionVisit.keySet()) {
				bodyGxy.put(s, hypertensionVisit.get(s));
			}
		}
		if (body.containsKey("diabetesVisit")) {
			Map<String, Object> diabetesVisit = (Map<String, Object>) body.get("diabetesVisit");
			for (String s : diabetesVisit.keySet()) {
				bodyTnb.put(s, diabetesVisit.get(s));
			}
		}
		String weight1 = (String) body.get("weight");
		String visitDate2 = (String) body.get("visitDate");
		String planId_GXY = (String) body.get("planId_GXY");
		String planId_TNB = (String) body.get("planId_TNB");
		String[] planIds = { planId_GXY, planId_TNB };
		Map<String, Object> schemaIds = new HashMap<String, Object>();
		schemaIds.put(planId_GXY, BSCHISEntryNames.MDC_HypertensionVisit);
		schemaIds.put(planId_TNB, BSCHISEntryNames.MDC_DiabetesVisit);
		Map<String, Object> ops = new HashMap<String, Object>();
		VisitPlanModel vpm = new VisitPlanModel(dao);
		Map<String, Object> planInfo = null;
		Map<String, Object> allSaveData = new HashMap<String, Object>();
		try {
			for (int j = 0; j < planIds.length; j++) {
				String planId = planIds[j];
				if (planId == null || planId.length() == 0 || "null".equals(planId)) {
					continue;
				}
				planInfo = vpm.getPlan(planId);
				String schemaId = (String) schemaIds.get(planId);
				ops.put(schemaId, "create");
				if (planInfo.get("visitId") != null && !"".equals(planInfo.get("visitId"))) {
					ops.put(schemaId, "update");
				}
				allSaveData.put(schemaId, planInfo);
			}
			if (allSaveData.containsKey(BSCHISEntryNames.MDC_HypertensionVisit)) {
				String empiId = (String) body.get("empiId");
				if (!bodyGxy.containsKey("visitEvaluate")) {
					bodyGxy.put("visitEvaluate", (String) body.get("visitType"));
				}
				if (bodyGxy.containsKey("hypertensionVisit")) {
					Map<String, Object> hypertensionVisit = (Map<String, Object>) body.get("hypertensionVisit");
					bodyGxy.put("medicine", (String) hypertensionVisit.get("gxymedicine"));
					bodyGxy.put("referralReason", (String) hypertensionVisit.get("gxyreferralReason"));
					bodyGxy.put("needdoublevisit", (String) hypertensionVisit.get("bxyneeddoublevisit"));
					bodyGxy.put("agencyAndDept", (String) hypertensionVisit.get("gxyreferralOffice"));
					bodyGxy.put("visitDoctor", (String) hypertensionVisit.get("uid"));
					String sqlgxy="select manaunitid as manaunitid from mdc_hypertensionrecord where empiId='"+empiId+ "'";
					Map<String, Object> parameters = null;
					try {
						parameters = dao.doSqlLoad(sqlgxy, null);
						bodyGxy.put("visitUnit",parameters.put("MANAUNITID", ""));
					} catch (PersistentDataOperationException e1) {
						e1.printStackTrace();
					}
				}
				
				String heightHy = "select height as height from MDC_HypertensionRecord where empiId='"+empiId+ "'";
				Map<String, Object> mapHy = null;
				try {
					mapHy = dao.doSqlLoad(heightHy, null);
				} catch (PersistentDataOperationException e1) {
					e1.printStackTrace();
				}
				if (mapHy != null) {
					BigDecimal height = (BigDecimal) mapHy.get("HEIGHT");
					Double heiNum = height.doubleValue();
					Double weiNum = (double) Integer.valueOf((String) body.get("weight")).intValue() ;
					Double weiNumDb = weiNum.doubleValue();
					Double bmi = weiNumDb / (2 * heiNum / 100);
					bodyGxy.put("bmi", bmi);
				}
				if (bodyGxy.containsKey("uid")) {
					bodyGxy.put("visitDoctor", bodyGxy.get("uid"));
				}
				if (bodyGxy.containsKey("visitDate")) {
					bodyGxy.put("inputDate", bodyGxy.get("visitDate"));
				}
				if (bodyGxy.containsKey("planId_GXY")) {
					bodyGxy.put("planId", bodyGxy.get("planId_GXY"));
				}
				req.put("body", bodyGxy);
				req.put("op", ops.get(BSCHISEntryNames.MDC_HypertensionVisit));
				HypertensionVisitService hvs = new HypertensionVisitService();
				hvs.setVisitPlanCreator(visitPlanCreator);
				hvs.setvLogService(vLogService);
				Map<String, Object> GXYRes = new HashMap<String, Object>();
				hvs.doSaveHypertensionVisit(req, GXYRes, dao, ctx);
				if (bodyGxy.containsKey("hypertensionVisit")) {
					Map<String, Object> hypertensionVisit = (Map<String, Object>) body.get("hypertensionVisit");
					String medicine=(String) body.get("gxymedicine");
				   if(medicine!="3"){
				    	if (hypertensionVisit.containsKey("gxyypxx")) {
						String records = (String) hypertensionVisit.get("gxyypxx");
						List<Map<String,String>> listObjectFir = (List<Map<String,String>>) JSONArray.parse(records);
						for (int i = 0; i < listObjectFir.size(); i++) {
							Map record1 = listObjectFir.get(i);
							Map<String, Object> record = new HashMap<String, Object>();
							SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
							String dateStr=sdf.format(new Date());
							record.put("medicineName",record1.get("ypmc"));
							record.put("medicineId",record1.get("ypxh"));
							record.put("medicineFrequency",record1.get("yyff"));
							record.put("medicineDosage",record1.get("ypjl"));
							record.put("medicineUnit",record1.get("jldw"));
							record.put("phrId",bodyGxy.get("phrId"));
							record.put("createDate",dateStr);
							record.put("days","1");
							String sqlName = " ";
							sqlName="select a.visitid from pub_visitplan a where a.planid='"+planId_GXY+"'";
							Map<String, Object> parameters = null;
							try {
								parameters = dao.doSqlLoad(sqlName, null);
								record.put("visitId",parameters.put("VISITID", ""));
							} catch (PersistentDataOperationException e1) {
								e1.printStackTrace();
							}
							HypertensionVisitModel hvm = new HypertensionVisitModel(dao);
							Map<String, Object> rsMap = null;
							String op = "create";
							try {
								rsMap = hvm.saveHyperVisitMedicineInfo(op,
										MDC_HypertensionMedicine, record, true);
							} catch (ModelDataOperationException e) {
								logger.error("Saving medicine data failed.", e);
								throw new ServiceException(e);
							}
				            }
				        }
					}		
			 String gxyjkjy=(String) body.get("gxyjkjy");
				   if(gxyjkjy!=null){
						Map<String, Object> record = new HashMap<String, Object>();
					   SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
						String dateStr=sdf.format(new Date());
						record.put("recipeName","高血压");
						record.put("ICD10","I10xx02");
						record.put("diagnoseName","高血压");
						record.put("guideDate",dateStr);
						record.put("guideUser",bodyGxy.get("uid"));
						record.put("phrId",bodyGxy.get("phrId"));
						record.put("guideWay","02");
						record.put("empiId",bodyGxy.get("empiId"));
						String sqlName = " ";
						sqlName="select a.visitid from pub_visitplan a where a.planid='"+planId_GXY+"'";
						Map<String, Object> parameters = null;
						try {
							parameters = dao.doSqlLoad(sqlName, null);
							record.put("wayId",parameters.put("VISITID", ""));
						} catch (PersistentDataOperationException e1) {
							e1.printStackTrace();
						}
						String healthTeach="";
						if(gxyjkjy.indexOf("10")!=-1){
							healthTeach=healthTeach+"注意劳逸结合，保证充足的睡眠。避免过度的脑力和体力负荷，消除紧张情绪， 可适当使用少量安定剂;";	
						}
						if(gxyjkjy.indexOf("11")!=-1){
							healthTeach=healthTeach+"适当的体育锻炼，有助于高血压恢复正常，但对中、重度高血压患者应避免活动 量较大的体育活动;";	
						}
						if(gxyjkjy.indexOf("12")!=-1){
							healthTeach=healthTeach+"不吸烟，饮食宜清淡，进食低盐、低脂肪、低胆固醇的食品;";	
						}
						if(gxyjkjy.indexOf("13")!=-1){
							healthTeach=healthTeach+"控制体重，肥胖的轻度高血压患者通过减轻体重往往可使血压恢复正常;";	
						}
						if(gxyjkjy.indexOf("14")!=-1){
							healthTeach=healthTeach+"对有高血压家族史，而本人血压曾有过增高者，医生的定期随访观察有利于早期 发现和早期治疗;";	
						}
						if(gxyjkjy.indexOf("15")!=-1){
							healthTeach=healthTeach+"避免受凉，清单饮食，适当运动。随诊;";	
						}
						if(gxyjkjy.indexOf("16")!=-1){
							healthTeach=healthTeach+"肥皂水清洗创口1次，时间15-20分钟，保持创口清洁;";	
						}
						if(gxyjkjy.indexOf("17")!=-1){
							healthTeach=healthTeach+"保持乐观情绪，认真对待疾病，学会尿糖测定，了解病情变化;";	
						}
						if(gxyjkjy.indexOf("18")!=-1){
							healthTeach=healthTeach+"控制饮食，限制糖类摄入量，多吃新鲜蔬菜，少食盐油，不宜食果酱、蜜饯、糕点等富含糖食品;";	
						}
						if(gxyjkjy.indexOf("19")!=-1){
							healthTeach=healthTeach+"戒烟酒，注意劳逸结合，适当锻炼，避免激烈运动;";	
						}
						if(gxyjkjy.indexOf("20")!=-1){
							healthTeach=healthTeach+"运动时携带适量食物，有低血糖反应（如头昏、出冷汗、饥饿感）时服用;";	
						}
						if(gxyjkjy.indexOf("21")!=-1){
							healthTeach=healthTeach+"定期复查血糖、尿糖，按医生意见调整治疗方案;";	
						}
						if(gxyjkjy.indexOf("22")!=-1){
							healthTeach=healthTeach+"出现发热、视物不清、心悸、昏迷、恶心、呕吐、胸闷、肢体麻木等立即就医;";	
						}
						if(gxyjkjy.indexOf("23")!=-1){
							healthTeach=healthTeach+"勤洗澡、多换衣、保持皮肤清洁、防感染、穿软底鞋、防局部受压引起糖尿病足;";	
						}
						record.put("healthTeach",healthTeach);
						HypertensionVisitModel hvm = new HypertensionVisitModel(dao);
						Map<String, Object> rsMap = null;
						String op = "create";
						try {
							hvm.saveAppHyperVisitHealthTeach(record);
						} catch (ModelDataOperationException e) {
							logger.error("Saving HealthTeach data failed.", e);
							throw new ServiceException(e);
						}
				   }
			}
				res.put("data", GXYRes);
				res.put("msg", "success");
				res.put("code", 200);
			}
			if (allSaveData.containsKey(BSCHISEntryNames.MDC_DiabetesVisit)) {
				String empiId = (String) body.get("empiId");
				if (bodyTnb.containsKey("diabetesVisit")) {
					Map<String, Object> diabetesVisit = (Map<String, Object>) body.get("diabetesVisit");
					bodyTnb.put("medicine", (String) diabetesVisit.get("tnbmedicine"));	
					bodyTnb.put("referralReason", (String) diabetesVisit.get("tnbreferralReason"));
					bodyTnb.put("needdoublevisit", (String) diabetesVisit.get("tnbneeddoublevisit"));
					bodyTnb.put("referralOffice", (String) diabetesVisit.get("tnbreferralOffice"));
					bodyTnb.put("rbs", (String) diabetesVisit.get("MBS"));
					bodyTnb.put("visitDoctor", (String) diabetesVisit.get("uid"));
					String sqltnb="select manaunitid as manaunitid from mdc_hypertensionrecord where empiId='"+empiId+ "'";
					Map<String, Object> parameters = null;
					try {
						parameters = dao.doSqlLoad(sqltnb, null);
						bodyTnb.put("visitUnit",parameters.put("MANAUNITID", ""));
					} catch (PersistentDataOperationException e1) {
						e1.printStackTrace();
					}
				}
				body.remove("visitDate"); 
				body.put("visitDate", visitDate2);
				String heightDi = "select height as height from MDC_DiabetesRecord where empiId='"+empiId+"'";
				Map<String, Object> mapDi = null;
				try {
					mapDi = dao.doSqlLoad(heightDi, null);
				} catch (PersistentDataOperationException e1) {
					e1.printStackTrace();
				}
				if (mapDi != null) {
					BigDecimal height = (BigDecimal) mapDi.get("HEIGHT");
					Double heiNum = height.doubleValue();
					Double weiNum = (double) Integer.valueOf((String)weight1) ;
					Double weiNumDb = weiNum.doubleValue();
					Double bmi = weiNumDb / (2 * heiNum / 100);
					bodyTnb.put("bmi", bmi);
				}
				if (bodyTnb.containsKey("visitDate")) {
					bodyTnb.put("inputDate", bodyTnb.get("visitDate"));
				}
				if (bodyTnb.containsKey("uid")) {
					bodyTnb.put("visitDoctor", bodyTnb.get("uid"));
				}
				if (bodyTnb.containsKey("planId_TNB")) {
					bodyTnb.put("planId", bodyTnb.get("planId_TNB"));
				}
				if(bodyTnb.containsKey("tnbPlanDate")){
					bodyTnb.put("planDate",bodyTnb.get("tnbPlanDate"));
					bodyTnb.remove("tnbPlanDate");
				}
				req.put("body", bodyTnb);
				req.put("op", ops.get(BSCHISEntryNames.MDC_DiabetesVisit));
				DiabetesVisitService dvs = new DiabetesVisitService();
				dvs.setVisitPlanCreator(visitPlanCreator);
				dvs.setvLogService(vLogService);
				Map<String, Object> TNBRes = new HashMap<String, Object>();
				dvs.doSaveDiabetesVisit(req, TNBRes, dao, ctx);
				
				if (bodyTnb.containsKey("diabetesVisit")) {
					Map<String, Object> diabetesVisit = (Map<String, Object>) body.get("diabetesVisit");
					String medicine=(String) body.get("tnbmedicine");
				
					if(medicine!="3"){
						if (diabetesVisit.containsKey("tnbypxx")) {
							String records = (String) diabetesVisit.get("tnbypxx");
							List<Map<String,String>> listObjectFir = (List<Map<String,String>>) JSONArray.parse(records);
							for (int i = 0; i < listObjectFir.size(); i++) {
								Map record1 = listObjectFir.get(i);
								Map<String, Object> record = new HashMap<String, Object>();
								SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
								String dateStr=sdf.format(new Date());
								record.put("medicineName",record1.get("ypmc"));
								record.put("medicineId",record1.get("ypxh"));
								record.put("medicineFrequency",record1.get("yyff"));
								record.put("medicineDosage",record1.get("ypjl"));
								record.put("medicineUnit",record1.get("jldw"));
								record.put("phrId",bodyTnb.get("phrId"));
								record.put("createDate",dateStr);
								record.put("days","1");
								String sqlName = " ";
								sqlName="select a.visitid from pub_visitplan a where a.planid='"+planId_TNB+"'";
								Map<String, Object> parameters = null;
								try {
									parameters = dao.doSqlLoad(sqlName, null);
									record.put("visitId",parameters.put("VISITID", ""));
								} catch (PersistentDataOperationException e1) {
									e1.printStackTrace();
								}
								Map<String, Object> m = null;
								String op = "create";
								DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
								m = (Map<String, Object>) dvm.saveDiabetesMedicine(op, record,
										true);
					            }
					        }
						}
				 
				}
				String tnbjkjy=(String) body.get("tnbjkjy");
				   if(tnbjkjy!=null){
						Map<String, Object> record = new HashMap<String, Object>();
						record.put("recipeName","糖尿病");
						record.put("ICD10","E11.901");
						record.put("diagnoseName","糖尿病");
						record.put("guideDate",visitDate2);
						record.put("guideUser",bodyGxy.get("uid"));
						record.put("phrId",bodyGxy.get("phrId"));
						record.put("guideWay","03");
						record.put("empiId",bodyGxy.get("empiId"));
						String sqlName = " ";
						sqlName="select a.visitid from pub_visitplan a where a.planid='"+planId_TNB+"'";
						Map<String, Object> parameters = null;
						try {
							parameters = dao.doSqlLoad(sqlName, null);
							record.put("wayId",parameters.put("VISITID", ""));
						} catch (PersistentDataOperationException e1) {
							e1.printStackTrace();
						}
						String healthTeach="";
						if(tnbjkjy.indexOf("10")!=-1){
							healthTeach=healthTeach+"注意劳逸结合，保证充足的睡眠。避免过度的脑力和体力负荷，消除紧张情绪， 可适当使用少量安定剂;";	
						}
						if(tnbjkjy.indexOf("11")!=-1){
							healthTeach=healthTeach+"适当的体育锻炼，有助于高血压恢复正常，但对中、重度高血压患者应避免活动 量较大的体育活动;";	
						}
						if(tnbjkjy.indexOf("12")!=-1){
							healthTeach=healthTeach+"不吸烟，饮食宜清淡，进食低盐、低脂肪、低胆固醇的食品;";	
						}
						if(tnbjkjy.indexOf("13")!=-1){
							healthTeach=healthTeach+"控制体重，肥胖的轻度高血压患者通过减轻体重往往可使血压恢复正常;";	
						}
						if(tnbjkjy.indexOf("14")!=-1){
							healthTeach=healthTeach+"对有高血压家族史，而本人血压曾有过增高者，医生的定期随访观察有利于早期 发现和早期治疗;";	
						}
						if(tnbjkjy.indexOf("15")!=-1){
							healthTeach=healthTeach+"避免受凉，清单饮食，适当运动。随诊;";	
						}
						if(tnbjkjy.indexOf("16")!=-1){
							healthTeach=healthTeach+"肥皂水清洗创口1次，时间15-20分钟，保持创口清洁;";	
						}
						if(tnbjkjy.indexOf("17")!=-1){
							healthTeach=healthTeach+"保持乐观情绪，认真对待疾病，学会尿糖测定，了解病情变化;";	
						}
						if(tnbjkjy.indexOf("18")!=-1){
							healthTeach=healthTeach+"控制饮食，限制糖类摄入量，多吃新鲜蔬菜，少食盐油，不宜食果酱、蜜饯、糕点等富含糖食品;";	
						}
						if(tnbjkjy.indexOf("19")!=-1){
							healthTeach=healthTeach+"戒烟酒，注意劳逸结合，适当锻炼，避免激烈运动;";	
						}
						if(tnbjkjy.indexOf("20")!=-1){
							healthTeach=healthTeach+"运动时携带适量食物，有低血糖反应（如头昏、出冷汗、饥饿感）时服用;";	
						}
						if(tnbjkjy.indexOf("21")!=-1){
							healthTeach=healthTeach+"定期复查血糖、尿糖，按医生意见调整治疗方案;";	
						}
						if(tnbjkjy.indexOf("22")!=-1){
							healthTeach=healthTeach+"出现发热、视物不清、心悸、昏迷、恶心、呕吐、胸闷、肢体麻木等立即就医;";	
						}
						if(tnbjkjy.indexOf("23")!=-1){
							healthTeach=healthTeach+"勤洗澡、多换衣、保持皮肤清洁、防感染、穿软底鞋、防局部受压引起糖尿病足;";	
						}
						record.put("healthTeach",healthTeach);
						DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
						String op = "create";
						try {
							dvm.saveAppDiabetesVisitHealthTeach(record);
						} catch (ModelDataOperationException e) {
							logger.error("Saving HealthTeach data failed.", e);
							throw new ServiceException(e);
						}
				   
			}
				res.put("data", TNBRes);
				res.put("msg", "success");
				res.put("code", 200);	
			}
		} catch (ModelDataOperationException e) {
			res.put("data", null);
			res.put("msg", "fail");
			res.put("code", "200");
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,"获取计划信息失败!", e);
		} catch (Exception e) {
			res.put("data", null);
			res.put("msg", "fail");
			res.put("code", "200");
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,"保存随访记录失败!", e);
		}
	}
	//更新高血压档案
	public void doUpdateHypertensionArchive(Map<String, Object> req,
	Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ExpException, ValidateException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("data");
		String phrId = (String) reqBody.get("phrId");
		List<?> cnd = CNDHelper.toListCnd("['eq',['$','a.phrId'],['s','"+phrId+ "']]");
		Map<String, Object> rsBody = null;
		String schemaId = "";
		Map<String, Object> hyMap = null;
		if (reqBody.containsKey("hypertensionArchiveDetail")) {
			hyMap = (Map<String, Object>) reqBody.get("hypertensionArchiveDetail");
		}
		schemaId = MDC_HypertensionRecord;
		for (Entry<String, Object> s : hyMap.entrySet()) {
			String key = s.getKey();
			Object value = s.getValue();
			reqBody.put(key, value);
		}
		try {
			rsBody = dao.doSave("update", schemaId, reqBody, false);
			// 药品修改
			String med = null;
			List<Map<String, Object>> medMap = null;
			List<Map<String, Object>> medMapSql = null;
			if (reqBody.containsKey("hypertensionMedicines")) {
				med = MDC_HypertensionMedicine;
				medMap = (List<Map<String, Object>>) reqBody.get("hypertensionMedicines");
				medMapSql = dao.doList(cnd, "a.phrId desc",MDC_HypertensionMedicine);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> reqMed = new HashMap<String, Object>();
			if (medMap != null && !medMap.isEmpty()) {
				// 删除库中数据
				dao.doRemove("phrId", phrId, med);
				for (int i = 0; i < medMap.size(); i++) {
					map = (Map<String, Object>) medMap.get(i);
					for (Entry<String, Object> s : map.entrySet()) {
						String key = s.getKey();
						Object value = s.getValue();
						reqMed.put(key, value);
					}
					reqMed.put("phrId", phrId);
					reqMed.remove("recordId");
					dao.doSave("create", med, reqMed, false);
				}
			} else {
				dao.doRemove("phrId", phrId, med);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		res.putAll(rsBody);
		res.put("msg", "success");
		res.put("code", 200);
	}
	//更新糖尿病档案
	public void doUpdateDiabetesArchive(Map<String, Object> req,
	Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ExpException, ValidateException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("data");
		String phrId = (String) reqBody.get("phrId");
		List<?> cnd = CNDHelper.toListCnd("['eq',['$','a.phrId'],['s','"+ phrId + "']]");
		Map<String, Object> rsBody = null;
		String schemaId = "";
		Map<String, Object> hyMap = null;
		if (reqBody.containsKey("diabetesArchiveDetail")) {
			hyMap = (Map<String, Object>) reqBody.get("diabetesArchiveDetail");
		}
		schemaId = MDC_DiabetesRecord;
		for (Entry<String, Object> s : hyMap.entrySet()) {
			String key = s.getKey();
			Object value = s.getValue();
			reqBody.put(key, value);
		}
		try {
			rsBody = dao.doSave("update", schemaId, reqBody, false);
			// 药品修改
			String med = null;
			List<Map<String, Object>> medMap = null;
			List<Map<String, Object>> medMapSql = null;
			if (reqBody.containsKey("diabetesMedicines")) {
				med = MDC_DiabetesMedicine;
				medMap = (List<Map<String, Object>>) reqBody.get("diabetesMedicines");
				medMapSql = dao.doList(cnd, "a.phrId desc",MDC_DiabetesMedicine);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> reqMed = new HashMap<String, Object>();
			if (medMap != null && !medMap.isEmpty()) {
				// 删除库中数据
				dao.doRemove("phrId", phrId, med);
				for (int i = 0; i < medMap.size(); i++) {
					map = (Map<String, Object>) medMap.get(i);
					for (Entry<String, Object> s : map.entrySet()) {
						String key = s.getKey();
						Object value = s.getValue();
						reqMed.put(key, value);
					}
					reqMed.put("phrId", phrId);
					reqMed.remove("recordId");
					dao.doSave("create", med, reqMed, false);
				}
			} else {
				dao.doRemove("phrId", phrId, med);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		res.putAll(rsBody);
		res.put("msg", "success");
		res.put("code", 200);
	}	
			// 查询生活环境数据-zhj
		public void doQueryGrxx(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) throws ServiceException{
			Map<String,Object> map=new HashMap<String,Object>();
			Map<String,Object> resBody=new HashMap<String,Object>();
			QueryModel query=new QueryModel(dao);
			String empiId=" ";
			if(req.containsKey("empiId")){
				empiId=(String)req.get("empiId");
			}
			String schemaId="chis.application.mobileApp.schemas.EHR_ShhjApp";
			map=query.queryShhj(empiId,schemaId,dao);
			resBody.put("body",map);
			res.putAll(resBody);
		}
		// 查询既往史数据-zhj
				public void doQueryPastHistory(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) throws ServiceException{
					Map<String,Object> map=new HashMap<String,Object>();
					Map<String,Object> resBody=new HashMap<String,Object>();
					QueryModel query=new QueryModel(dao);
					String empiId=" ";
					if(req.containsKey("empiId")){
						empiId=(String)req.get("empiId");
					}
					String schemaId="chis.application.mobileApp.schemas.EHR_PastHistoryApp";
					map=query.QueryPastHistory(empiId,schemaId,dao);
					resBody.put("body",map);
					res.putAll(resBody);
				}
				// 查询健康检查-一般情况-zhj
				public void doSearchHealthCheck(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) throws ServiceException{
					Map<String,Object> map=new HashMap<String,Object>();
					Map<String,Object> resBody=new HashMap<String,Object>();
					QueryModel query=new QueryModel(dao);
					String empiId=" ";
					if(req.containsKey("HealthCheck")){
						empiId=(String)req.get("HealthCheck");
					}
					String schemaId="chis.application.mobileApp.schemas.HC_HealthCheckApp";
					map=query.queryHealthCheck(empiId,schemaId,dao);
					resBody.put("body",map);
					res.putAll(resBody);
				}
				// 查询健康检查-生活方式-zhj
				public void doSearchLifeStyle(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) throws ServiceException{
					Map<String,Object> map=new HashMap<String,Object>();
					Map<String,Object> resBody=new HashMap<String,Object>();
					QueryModel query=new QueryModel(dao);
					String healthcheck=" ";
					if(req.containsKey("healthcheck")){
						healthcheck=(String)req.get("healthcheck");
					}
					String schemaId="chis.application.mobileApp.schemas.HC_LifestySituationApp";
					map=query.queryLifeStyle(healthcheck,schemaId,dao);
					resBody.put("body",map);
					res.putAll(resBody);
				}
				// 查询健康检查-脏器-zhj
				public void doSearchAccessoryExamination(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) throws ServiceException{
					Map<String,Object> map=new HashMap<String,Object>();
					Map<String,Object> resBody=new HashMap<String,Object>();
					QueryModel query=new QueryModel(dao);
					String healthcheck=" ";
					if(req.containsKey("healthcheck")){
						healthcheck=(String)req.get("healthcheck");
					}
					String schemaId="chis.application.mobileApp.schemas.HC_AccessoryExaminationApp";
					map=query.queryAccessoryExamination(healthcheck,schemaId,dao);
					resBody.put("body",map);
					res.putAll(resBody);
				}
				// 查询健康检查-查体-zhj
				public void doSearchEXAMINATION(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) throws ServiceException{
					Map<String,Object> map=new HashMap<String,Object>();
					Map<String,Object> resBody=new HashMap<String,Object>();
					QueryModel query=new QueryModel(dao);
					String healthcheck=" ";
					if(req.containsKey("healthcheck")){
						healthcheck=(String)req.get("healthcheck");
					}
					String schemaId="chis.application.mobileApp.schemas.HC_ExaminationApp";
					map=query.queryExamination(healthcheck,schemaId,dao);
					resBody.put("body",map);
					res.putAll(resBody);
				}
				// 查询健康检查-辅助检查-zhj
				public void doSearchHC_AccessoryExamination(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) throws ServiceException{
					Map<String,Object> map=new HashMap<String,Object>();
					Map<String,Object> resBody=new HashMap<String,Object>();
					QueryModel query=new QueryModel(dao);
					String healthcheck=" ";
					if(req.containsKey("healthcheck")){
						healthcheck=(String)req.get("healthcheck");
					}
					String schemaId="chis.application.mobileApp.schemas.HC_AccessoryExaminationApp";
					map=query.queryHC_ACCESSORYEXAMINATION(healthcheck,schemaId,dao);
					resBody.put("body",map);
					res.putAll(resBody);
				}
				// 查询健康评价-zhj
				public void doSearchHealthAssessment(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) throws ServiceException{
					Map<String,Object> map=new HashMap<String,Object>();
					Map<String,Object> resBody=new HashMap<String,Object>();
					QueryModel query=new QueryModel(dao);
					String healthcheck=" ";
					if(req.containsKey("healthcheck")){
						healthcheck=(String)req.get("healthcheck");
					}
					String schemaId="chis.application.mobileApp.schemas.HC_HealthAssessmentApp";
					map=query.queryHealthAssessment(healthcheck,schemaId,dao);
					resBody.put("body",map);
					res.putAll(resBody);
				}
				// 查询住院治疗-zhj
				public void doSearchINHOSPITALSITUATION(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) throws ServiceException{
					Map<String,Object> map=new HashMap<String,Object>();
					Map<String,Object> resBody=new HashMap<String,Object>();
					QueryModel query=new QueryModel(dao);
					String healthcheck=" ";
					if(req.containsKey("healthcheck")){
						healthcheck=(String)req.get("healthcheck");
					}
					String schemaId="chis.application.mobileApp.schemas.HC_InhospitalSituationApp";
					map=query.queryINHOSPITALSITUATION(healthcheck,schemaId,dao);
					resBody.put("body",map);
					res.putAll(resBody);
				}
				// 查询非免疫规划预防接种-zhj
				public void doSearchNonimmuneInoculation(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) throws ServiceException{
					Map<String,Object> map=new HashMap<String,Object>();
					Map<String,Object> resBody=new HashMap<String,Object>();
					QueryModel query=new QueryModel(dao);
					String healthcheck=" ";
					if(req.containsKey("healthcheck")){
						healthcheck=(String)req.get("healthcheck");
					}
					String schemaId="chis.application.mobileApp.schemas.HC_NonimmuneInoculationApp";
					map=query.queryNonimmuneInoculation(healthcheck,schemaId,dao);
					resBody.put("body",map);
					res.putAll(resBody);
				}
				// 查询现存主要问题-zhj
				public void doSearchXczywt(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) throws ServiceException{
					Map<String,Object> map=new HashMap<String,Object>();
					Map<String,Object> resBody=new HashMap<String,Object>();
					QueryModel query=new QueryModel(dao);
					String healthcheck=" ";
					if(req.containsKey("healthcheck")){
						healthcheck=(String)req.get("healthcheck");
					}
					String schemaId="chis.application.mobileApp.schemas.HC_HealthCheck_listApp";
					map=query.queryXczywt(healthcheck,schemaId,dao);
					resBody.put("body",map);
					res.putAll(resBody);
				}
//				//健康检查列表查询-新版app
//				public void doQueryHealthCheckList(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx)
//				throws ServiceException,ExpException{
//					String arg=(String)req.get("cnd");
//					HealthCheckService hs=new HealthCheckService();
//					req.put("schema","chis.application.mobileApp.schemas.HC_HealthCheck");
//					List<?> cnd=null;
//				String manaDoctorId=UserRoleToken.getCurrent().getUserId();
//				//if(arg!=null&&!arg.equals(" ")){
//					//	cnd=CNDHelper.toListCnd(""+"['and',['in',['$','a.status'],['0','2']],"+"['eq',['$','a.manaDoctorId'],['s','"
//					//+manaDoctorId+"']],"+"['or', ['like',['$','c.regionCode_text'],['s','%"+arg+"%']]"
//					//	+",['like', ['$', 'b.personName'], ['s', '%"+arg+"%']],['like',"+"['$','b.idCard'], ['s', '%"+arg+"%']]]]");
//				//	}else{
//					//	cnd=CNDHelper.toListCnd("['and',['in',['$','a.status'],['0','2']],['eq',['$','a.manaDoctorId'],['s','"
//				//	+manaDoctorId+"']]]");
//				//	}
//					req.put("cnd",cnd);
//					int limit=10;
//					if(req.containsKey("limit")){
//						limit=(Integer)req.get("limit");
//					}
//					int start=1;
//					if(req.containsKey("start")){
//						start=(Integer)req.get("start");
//					}
//					req.put("pageSize",limit);
//					req.put("pageNo",start);
//					try{
//						Map<String, Object>p=new HashMap<String, Object>();
//						p.put("pageNo",1);
//						dao.doList(cnd, "", "schema", 1, 25, null);
//						hs.doListHealthCheck(req,res,dao,ctx);
//					}catch(Exception e){
//						res.put(Service.RES_CODE,500);
//						res.put(Service.RES_MESSAGE,"fail");
//						logger.error("查询健康检查列表失败");
//						throw new ServiceException(Constants.CODE_DATABASE_ERROR,"查询健康检查列表失败",e);
//					}
//					List<Map<String,Object>> list=(List<Map<String,Object>>)res.get("body");
//					if(list==null){
//						res.put("data",new ArrayList<Map<String,Object>>());
//					}else{
//						res.put("data",res.get("body"));
//						res.remove("body");
//						res.put("code",200);
//						res.put("msg","success");
//					}
//				}
				// 查询健康体检表首页-zhj
				public void doSearchHealthCheckSY(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) throws ServiceException{
					Map<String,Object> map=new HashMap<String,Object>();
					Map<String,Object> resBody=new HashMap<String,Object>();
					QueryModel query=new QueryModel(dao);
					String healthcheck=" ";
					if(req.containsKey("healthcheck")){
						healthcheck=(String)req.get("healthcheck");
					}
					String schemaId="chis.application.mobileApp.schemas.HC_HealthCheckSYApp";
					map=query.queryHealCheckSY(healthcheck,schemaId,dao);
					resBody.put("body",map);
					res.putAll(resBody);
				}
				// 查询健康体检表列表-zhj
				public void doSearchHealthCheckLb(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) throws ServiceException{
					Map<String,Object> map=new HashMap<String,Object>();
					Map<String,Object> resBody=new HashMap<String,Object>();
					QueryModel query=new QueryModel(dao);
					String healthcheck=" ";
					if(req.containsKey("empiId")){
						healthcheck=(String)req.get("empiId");
					}
					String schemaId="chis.application.mobileApp.schemas.HC_HealthCheckLbApp";
					map=query.queryHealCheckLb(healthcheck,schemaId,dao);
					resBody.put("body",map);
					res.putAll(resBody);
				}
				// 查询高血压随访-zhj
				public void doSearchMDC_HypertensionVisit(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) throws ServiceException{
					Map<String,Object> map=new HashMap<String,Object>();
					Map<String,Object> resBody=new HashMap<String,Object>();
					QueryModel query=new QueryModel(dao);
					String healthcheck=" ";
					if(req.containsKey("visitId")){
						healthcheck=(String)req.get("visitId");
					}
					String schemaId="chis.application.mobileApp.schemas.MDC_HypertensionVisitApp";
					map=query.queryMDC_HypertensionVisit(healthcheck,schemaId,dao);
					resBody.put("body",map);
					res.putAll(resBody);
				}
				// 查询糖尿病随访-zhj
				public void doSearchMDC_DiabetesVisit(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) throws ServiceException{
					Map<String,Object> map=new HashMap<String,Object>();
					Map<String,Object> resBody=new HashMap<String,Object>();
					QueryModel query=new QueryModel(dao);
					String healthcheck=" ";
					if(req.containsKey("visitId")){
						healthcheck=(String)req.get("visitId");
					}
					String schemaId="chis.application.mobileApp.schemas.MDC_DiabetesVisitApp";
					map=query.queryMDC_DiabetesVisit(healthcheck,schemaId,dao);
					resBody.put("body",map);
					res.putAll(resBody);
				}
				// 查询高血压-zhj
				public void doSearchMDC_Hypertension(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) throws ServiceException{
					Map<String,Object> map=new HashMap<String,Object>();
					Map<String,Object> resBody=new HashMap<String,Object>();
					QueryModel query=new QueryModel(dao);
					String healthcheck=" ";
					if(req.containsKey("empiId")){
						healthcheck=(String)req.get("empiId");
					}
					String schemaId="chis.application.mobileApp.schemas.MDC_HypertensionApp";
					map=query.queryMDC_Hypertension(healthcheck,schemaId,dao);
					resBody.put("body",map);
					res.putAll(resBody);
				}
				// 查询糖尿病-zhj
				public void doSearchMDC_DiabetesApp(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) throws ServiceException{
					Map<String,Object> map=new HashMap<String,Object>();
					Map<String,Object> resBody=new HashMap<String,Object>();
					QueryModel query=new QueryModel(dao);
					String healthcheck=" ";
					if(req.containsKey("empiId")){
						healthcheck=(String)req.get("empiId");
					}
					String schemaId="chis.application.mobileApp.schemas.MDC_DiabetesApp";
					map=query.queryMDC_Diabetes(healthcheck,schemaId,dao);
					resBody.put("body",map);
					res.putAll(resBody);
				}
				// 根据idcard查empiid-zhj
				public void doEmpiIdByIdcard(Map<String,Object> req,Map<String,Object>res,BaseDAO dao,Context ctx) throws ServiceException{
					Map<String,Object> map=new HashMap<String,Object>();
					Map<String,Object> resBody=new HashMap<String,Object>();
					QueryModel query=new QueryModel(dao);
					String healthcheck=" ";
					if(req.containsKey("idcard")){
						healthcheck=(String)req.get("idcard");
					}
					String schemaId="chis.application.mobileApp.schemas.MPI_DemographicInfoApp";
					map=query.queryEmpiIdByIdcard(healthcheck,schemaId,dao);
				if(map!=null){
					resBody.put("body",map);
					
				}else{
					resBody.put("msg","未查到此人健康档案信息！");	
				}
					res.putAll(resBody);
				}
				
				// 根据个人基本信息-zhj
				public void doSearchJbxx(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) throws ServiceException{
					Map<String,Object> map=new HashMap<String,Object>();
					Map<String,Object> resBody=new HashMap<String,Object>();
					QueryModel query=new QueryModel(dao);
					String healthcheck=" ";
					if(req.containsKey("empiId")){
						healthcheck=(String)req.get("empiId");
					}
					String schemaId="chis.application.mobileApp.schemas.MPI_DemographicInfoJbApp";
					map=query.queryJbxx(healthcheck,schemaId,dao);
					resBody.put("body",map);
					res.putAll(resBody);
				}

}