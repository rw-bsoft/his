package chis.source.dr;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.common.HttpclientUtil;
import com.bsoft.mpi.util.SchemaUtil;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.dictionary.support.XMLDictionary;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class DrApplyModel implements BSCHISEntryNames{
	private BaseDAO dao;

	public DrApplyModel(BaseDAO dao) {
		super();
		this.dao = dao;
	}
	private static final Logger logger = LoggerFactory
			.getLogger(HttpclientUtil.class);
	
	@SuppressWarnings({ "unchecked"})
	public void doSaveSendExchange(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		 Map<String, Object> body = 
			      (Map<String, Object>)req.get("body");
	    Map<String, Object> mpiData = (Map<String, Object>)body.get("mpiData");
	    Map<String, Object> drData = (Map<String, Object>)body.get("drData");
	    Map<String, Object> bodyData = 
	      (Map<String, Object>)body.get("body");
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    if (drData != null) {
	      bodyData.putAll(drData);
	    }
	    String cardNo = (String)mpiData.get("cardNo");
	    String idCard = (String)mpiData.get("idCard");
	    String cardTypeCode = 
	      (String)mpiData.get("cardTypeCode");
	    try {
	      String hql = "select a.recordId as recordId,a.mpiId as mpiId,a.hospitalCode as hospitalCode,a.departmentCode as departmentCode,a.DiseaseDescription as DiseaseDescription,a.announcements as announcements," +
	      		"a.turnReason as turnReason,a.refuseReason as refuseReason,a.status as status,a.operator as operator,a.submitorDoctor as submitorDoctor,a.submitAgency as submitAgency,a.reserveNo as reserveNo," +
	      		"a.businessType as businessType,a.payType as payType,a.brxz as brxz,a.emrNo as emrNo,a.hosNo as hosNo,a.ZZRQ as ZZRQ,a.JZYS as JZYS,a.JYZRKS as JYZRKS,a.JYJCXM as JYJCXM,a.JYJCXMJG as JYJCXMJG,a.KFCSZD as KFCSZD " +
	      		"from DR_Referrals a, DR_DemographicInfo b where a.mpiId = b.mpiId";

	      Map<String,Object> params = new HashMap<String, Object>();
	      String reserveNoStr = (String)bodyData.get("reserveNo");
	      String submitTimeStr = (String)bodyData.get("submitTime");
	      String ZZRQStr = (String)bodyData.get("ZZRQ");
	      String submitTime = submitTimeStr.substring(0, 10);
	      String submitTime2 = submitTimeStr.replace("T", " ").substring(0, 16);
	      String ZZRQStr2 = ZZRQStr.replace("T", " ").substring(0, 16);
	      String operationTimeStr = (String)bodyData.get("operationTime");
	      Date operationTime = sdf.parse(operationTimeStr.substring(0, 10));
	      Date ZZRQ = sdf.parse(ZZRQStr2.substring(0, 10));
	      List<Map<String,Object>> referralList = new ArrayList<Map<String,Object>>();
	      boolean flag = false;
	      if(reserveNoStr!=null && !"".equals(reserveNoStr)){
	    	  hql+=" and a.reserveNo=:reserveNo";
	    	  params.put("reserveNo",reserveNoStr);
	    	  flag=true;
	      }
	      if(cardNo!=null && !"".equals(cardNo)){
		      if(cardTypeCode!=null && !"".equals(cardTypeCode)){
		    	  hql+=" and b.cardTypeCode=:cardTypeCode and b.cardNo=:cardNo";
		    	  params.put("cardTypeCode",cardTypeCode);
		    	  params.put("cardNo",cardNo);
		    	  flag=true;
		      }
	      }
	      if(idCard!=null && !"".equals(idCard)){
	    	  hql+=" and b.idCard=:idCard";
	    	  params.put("idCard",idCard);
	    	  flag=true;
	      }
	      if(submitTime!=null && !"".equals(submitTime) && flag){
	    	  hql+=" and to_char(a.submitTime,'YYYY-MM-DD')=:submitTime";
	    	  params.put("submitTime",submitTime);
	    	  referralList = dao.doQuery(hql, params);
	      }
	      if (referralList.size() > 0){
	    	  bodyData.putAll((Map<String,Object>)referralList.get(0));
	    	  body.put("body", bodyData);
	    	  res.put("msg", "转诊记录已经存在");
	    	  res.put("result", 
	    	          SchemaUtil.setDictionaryMassageForForm(referralList, "chis.application.dr.schemas.DR_Referrals"));
	    	  res.put("code", Integer.valueOf(500));
		      res.put("body", body);
	      }else{
	    	  bodyData.put("submitTime", sdf2.parse(submitTime2));
		      bodyData.put("operationTime", operationTime);
		      bodyData.put("ZZRQ", ZZRQ);
		      String businessType = (String)bodyData.get("businessType");
		      bodyData.put("isNew", "1");
		      bodyData.put("reserveNo", getReserveNo());
		      if (businessType.equals("2")){
		    	  bodyData.put("status", "04");
		      }else{
		    	  bodyData.put("status", "08");
		      }
		      Map<String, Object> card = new HashMap<String, Object>();
		      card.put("cardTypeCode", cardTypeCode);
		      card.put("cardNo", cardNo!=null?cardNo.toUpperCase():"");
		      card.put("idCard", idCard!=null?idCard.toUpperCase():"");
		      String birthdayStr = (String)mpiData.get("birthday");
		      card.put("birthday", sdf.parse(birthdayStr.substring(0, 10)));
		      card.put("personName", (String)mpiData.get("personName"));
		      card.put("sexCode", (String)mpiData.get("sexCode"));
		      card.put("contactNo", (String)mpiData.get("contactNo"));
		      List<Map<String, Object>> cards = new ArrayList<Map<String,Object>>(1);
		      cards.add(card);
		      body.clear();
		      body.put("cards", cards);
		      List<Map<String, Object>> mpi = new ArrayList<Map<String,Object>>();
		      Map<String,Object> parameters = new HashMap<String, Object>();
	    	  for(Map<String, Object> c:cards){
	    			String sql = "select mpiId as mpiId,cardTypeCode as cardTypeCode,cardNo as cardNo,personName as personName,idCard as idCard,sexCode as sexCode," +
	    					"birthday as birthday,contactNo as contactNo from DR_DemographicInfo where idCard=:idCard";
	    			parameters.clear();
	    			parameters.put("idCard", c.get("idCard")+"");
	    			Map<String,Object> demographicInfo = dao.doLoad(sql, parameters);
	    			if(demographicInfo!=null){
	    				mpi.add(demographicInfo);
	    			}
	    	  }
		      if (mpi == null || mpi.size()==0){
		    	  card.put("mpiId", idCard);
		    	  Map<String, Object> mpiId = dao.doSave("create", DR_DemographicInfo, card, false);
		    	  bodyData.put("mpiId", mpiId.get("mpiId"));
			      res.put("code", Integer.valueOf(200));
		      }else if (mpi.size() > 0){
		    	  bodyData.put("mpiId", 
			            ((Map<String, Object>)mpi.get(0)).get("mpiId"));
		    	  Map<String, Object> mpiMap = (Map<String, Object>)mpi.get(0);
		    	  if(mpiData.get("contactNo")!=null){
		    		  mpiMap.put("contactNo", mpiData.get("contactNo")+"");
		    	  }
		    	  dao.doSave("update", DR_DemographicInfo, mpiMap, false);
			  }
		      res.put("JIESHOUSZSQ", bodyData);
		      dao.doSave("create", DR_Referrals, bodyData, false);
	      }
	    } catch (Exception e){
	    	throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取转诊信息失败！", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doUpdate(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		try
	    {
	      Map<String, Object> body = 
	        (Map<String, Object>)req.get("body");
	      Map<String, Object> drData = 
	        (Map<String, Object>)req.get("drData");
	      Map<String, Object> mpiData = 
	  	        (Map<String, Object>)req.get("mpiData");
	      
	      String sql = "select mpiId as mpiId,cardTypeCode as cardTypeCode,cardNo as cardNo,personName as personName,idCard as idCard,sexCode as sexCode," +
					"birthday as birthday,contactNo as contactNo from DR_DemographicInfo where idCard=:idCard";
	      Map<String, Object> parameters = new HashMap<String, Object>();
	      parameters.put("idCard", mpiData.get("idCard")+"");
	      Map<String,Object> demographicInfo = dao.doLoad(sql, parameters);
	      if(mpiData.get("contactNo")!=null){
	    	  demographicInfo.put("contactNo", mpiData.get("contactNo")+"");
	  	  }
	  	  dao.doSave("update", DR_DemographicInfo, demographicInfo, false);
	      
	      body.put("drData", req.get("drData"));
	      body.put("mpiData", req.get("mpiData"));
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	      if (drData != null) {
	        body.putAll(drData);
	      }
	      String businessType = (String)drData.get("businessType");
	      if (businessType!=null && businessType.equals("1")) {
	        body.put("status", "08");
	      } else {
	        body.put("status", "04");
	      }
	      body.put("isverify", "3");
	      body.put("isNew", "1");
	      String submitTimeStr = (String)body.get("submitTime");
	      String operationTimeStr = (String)body.get("operationTime");
	      body.put("submitTime", 
	    		  sdf2.parse(submitTimeStr.replace("T", " ").substring(0, 16)));
	      body.put("operationTime", 
	    		  sdf.parse(operationTimeStr.substring(0, 10)));
	      String idCard = (String)mpiData.get("idCard");
	      body.put("mpiId", idCard.toUpperCase());
	      dao.doSave("update", DR_Referrals, body, false);
	      res.put("code", Integer.valueOf(200));
	      res.put("msg", "更新转诊记录成功");
	    }
	    catch (Exception e)
	    {
	      throw new ModelDataOperationException("更新转诊记录失败", e);
	    }
	}
	
	public void doLoadResourcesHtml(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> body = new HashMap<String, Object>();
	    List<Map<String, Object>> hospitals = getHospitals();
	    body.put("hospitals", hospitals);
	    String hospitalCode = (String)req.get("hospitalCode");
	    String departmentCode = (String)req.get("departmentCode");
	    String businessType = (String)req.get("businesstype");
	    String bedSexCode = (String)req.get("bedSexCode");
//	    String reqInfo = (String)req.get("reqInfo");
	    List<Map<String, Object>> businessTypes = getBusinessTypes();
	    List<Map<String, Object>> departments = new ArrayList<Map<String, Object>>();
	    if (hospitalCode != null)
	    {
	      for (int i = 0; i < hospitals.size(); i++)
	      {
	        Map<String, Object> hospt = (Map<String, Object>)hospitals.get(i);
	        String hosptCode = (String)hospt.get("hospitalCode");
	        if (hosptCode.equals(hospitalCode)) {
	          hospt.put("checked", "true");
	        }
	      }
	      body.put("businessTypes", businessTypes);
	      if (businessType != null)
	      {
	        for (int i = 0; i < businessTypes.size(); i++)
	        {
	          Map<String, Object> typeMap = (Map<String, Object>)businessTypes.get(i);
	          String bstp = (String)typeMap.get("businesstype");
	          if (businessType.equals(bstp)) {
	            typeMap.put("checked", "true");
	          }
	        }
	        try
	        {
//	          departments = addDepartments(ctx, hospitalCode, 
//	  	              businessType);
	          body.put("departmentCounts", Integer.valueOf(departments.size()));
	        }
	        catch (Exception e)
	        {
	          throw new ModelDataOperationException("加载科室信息失败！", e);
	        }
	      }
	    }
	    body.put("departments", departments);
	    
	    if (departmentCode != null) {
	      for (int i = 0; i < departments.size(); i++)
	      {
	        Map<String, Object> department = (Map<String, Object>)departments.get(i);
	        String dptc = (String)department.get("departmentCode");
	        if (dptc.equals(departmentCode)) {
	          if (businessType.equals("1"))
	          {
	            department.put("checked", "true");
	          }
	          else
	          {
	            String dpSexCode = 
	              (String)department.get("bedSexCode");
	            if (dpSexCode.equals(bedSexCode)) {
	              department.put("checked", "true");
	            }
	          }
	        }
	      }
	    }
	    addContent(body);
	    body.put("hospitalCounts", Integer.valueOf(hospitals.size()));
	    body.put("businessTypeCounts", Integer.valueOf(businessTypes.size()));
	    res.put("body", body);
	}
	
	private List<Map<String, Object>> getBusinessTypes()
	{
	    List<Map<String, Object>> businessTypes = new ArrayList<Map<String, Object>>();
	    Map<String, Object> clini = new HashMap<String, Object>();
	    clini.put("businesstype", "1");
	    clini.put("businesstype_text", "门诊");
	    Map<String, Object> inHospital = new HashMap<String, Object>();
	    inHospital.put("businesstype", "2");
	    inHospital.put("businesstype_text", "住院");
	    businessTypes.add(clini);
	    businessTypes.add(inHospital);
	    return businessTypes;
	}

	public List<Map<String, Object>> getHospitals()
	{
	    List<Map<String, Object>> hospitalList = new ArrayList<Map<String, Object>>();
	    try
	    {
	      XMLDictionary dic = (XMLDictionary)DictionaryController.instance().get(
	    		  "chis.dictionary.hospitalCode");
	      List<DictionaryItem> list = dic.itemsList();
	      for (int i = 0; i < list.size(); i++)
	      {
	        Map<String, Object> map = new HashMap<String, Object>();
	        String hospitalCode = ((DictionaryItem)list.get(i)).getKey();
	        String hospital = ((DictionaryItem)list.get(i)).getText();
	        map.put("hospitalCode", hospitalCode);
	        map.put("hospital", hospital);
	        map.put("checked", "false");
	        hospitalList.add(map);
	      }
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    return hospitalList;
	}
	
	private void addContent(Map<String, Object> body)
	{
	    ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
	    resolver.setTemplateMode("XHTML");
	    resolver.setSuffix(".html");
	    resolver.setPrefix("chis/templates/");
	    resolver.setCharacterEncoding("utf-8");
	    resolver.setCacheTTLMs(Long.valueOf(10000L));
	    TemplateEngine templateEngine = new TemplateEngine();
	    templateEngine.setTemplateResolver(resolver);
	    org.thymeleaf.context.Context thy = new org.thymeleaf.context.Context();
	    thy.setVariables(body);
	    String html = templateEngine.process("list", thy);
	    body.put("content", html);
	}
	
//	public static synchronized String getReserveNo(){
//		String uuid = UUID.randomUUID().toString();
//	    String reserveNo = uuid.substring(0, 3) + new Date().getTime();
//	    return reserveNo;
//	}
	
	public String getReserveNo(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int y = calendar.get(Calendar.YEAR);
		String reserveNo = String.valueOf(y)+"001";
		String hql = "select max(reserveNo) as currentNo from Referrals  ";            
		Map<String,Object> params = new HashMap<String, Object>();
		try {
			Map<String,Object> referralList = dao.doLoad(hql, params);
			if(referralList!=null && referralList.get("currentNo")!=null){
				long currentNo = Long.parseLong(String.valueOf(referralList.get("currentNo")).trim());
				reserveNo = String.valueOf(currentNo+1);
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return reserveNo;
	}
	
	
	
	
	@SuppressWarnings({ "unchecked" })
	public void doGetMPI(Map<String, Object> req, Map<String, Object> res, Context ctx)
		    throws ModelDataOperationException {
	    try
	    {
	      String idCard = null;
	      String personName = null;
	      String cardNo = null;
	      String cardTypeCode = null;
	      String reserveNo = null;
	      String mpiId = null;
	      Map<String, Object> body = (Map<String, Object>)req.get("body");
	      idCard = (String)body.get("idCard");
	      personName = (String)body.get("personName");
	      cardNo = (String)body.get("cardNo");
	      cardTypeCode = (String)body.get("cardTypeCode");
	      reserveNo = (String)body.get("reserveNo");
	      if(reserveNo!=null){
	    	  String hql = "select a.recordId as recordId,a.mpiId as mpiId,a.hospitalCode as hospitalCode,a.departmentCode as departmentCode,a.DiseaseDescription as DiseaseDescription,a.announcements as announcements," +
	  	      		"a.turnReason as turnReason,a.refuseReason as refuseReason,a.status as status,a.submitorDoctor as submitorDoctor,a.operator as operator,a.submitAgency as submitAgency,a.reserveNo as reserveNo,a.businessType as businessType " +
	  	      		"from DR_Referrals a where a.reserveNo=:reserveNo";
  		      Map<String,Object> params = new HashMap<String, Object>();
  		      params.put("reserveNo",(String)body.get("reserveNo"));
  		      List<Map<String,Object>> referralList = dao.doQuery(hql, params);
  		      if(referralList.size()>0){
  		    	mpiId = String.valueOf(referralList.get(0).get("mpiId"));
  		      }else{
  		    	throw new ModelDataOperationException(500, 
  		  	        "获取转诊病人信息失败");
  		      }
	      }
	      
	      List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
	      String hql = "select mpiId as mpiId,cardTypeCode as cardTypeCode,cardNo as cardNo,personName as personName,idCard as idCard,sexCode as sexCode," +
					"birthday as birthday,contactNo as contactNo from DR_DemographicInfo ";
	      Map<String,Object> parameters = new HashMap<String, Object>();
		  parameters.clear();
		  boolean flag =false;
		  if(idCard!=null && !"".equals(idCard)){
			  hql+="where idCard=:idCard";
			  parameters.put("idCard", idCard);
			  flag = true;
		  }
		  if(personName!=null && !"".equals(personName)){
			  hql+=flag?" and ":" where ";
			  hql+="personName=:personName";
			  parameters.put("personName", personName);
			  flag = true;
		  }
		  if(cardNo!=null && cardTypeCode!=null && !"".equals(cardNo) && !"".equals(cardTypeCode)){
			  hql+=flag?" and ":" where ";
			  hql+="cardNo=:cardNo and cardTypeCode=:cardTypeCode";
			  parameters.put("cardNo", cardNo);
			  parameters.put("cardTypeCode", cardTypeCode);
			  flag = true;
		  }
		  if(mpiId!=null && !"".equals(mpiId) && !"null".equals(mpiId)){
			  hql+=flag?" and ":" where ";
			  hql+="mpiId=:mpiId";
			  parameters.put("mpiId", mpiId);
		  }
		  List<Map<String,Object>> demographicInfo = dao.doQuery(hql, parameters);
		  data.addAll(demographicInfo);
	      if (data != null && data.size() > 0)
	      {
	        data = SchemaUtil.setDictionaryMassageForList(data, 
	        		"chis.application.dr.schemas.DR_DemographicInfo");
	      }
	      res.put("body", data);
	    }
	    catch (Exception e)
	    {
	      throw new ModelDataOperationException(500, 
	        "获取转诊病人信息失败", e);
	    }
	}
	
	@SuppressWarnings("unchecked")
	public void doCndQuery(Map<String, Object> req, Map<String, Object> res, Context ctx)
		    throws ModelDataOperationException{
	    try {
	      Map<String, Object> body = (Map<String, Object>)req.get("body");
	      if (body != null) {
	    	  String hql = "select a.recordId as recordId,a.mpiId as mpiId,a.hospitalCode as hospitalCode,a.departmentCode as departmentCode,a.DiseaseDescription as DiseaseDescription,a.announcements as announcements," +
	      		"a.turnReason as turnReason,a.refuseReason as refuseReason,a.status as status,a.submitorDoctor as submitorDoctor,a.operator as operator,a.submitAgency as submitAgency,a.reserveNo as reserveNo,a.businessType as businessType " +
	      		"from DR_Referrals a, DR_DemographicInfo b where a.mpiId = b.mpiId and a.mpiId=:mpiId and to_char(a.submitTime,'YYYY-MM-DD')>:submitTime";
		      Map<String,Object> params = new HashMap<String, Object>();
		      String submitTimeStr=(String)body.get("submitTime");
		      params.put("submitTime",submitTimeStr.substring(0, 10));
		      params.put("mpiId",(String)body.get("mpiId"));
		      List<Map<String,Object>> referralList = dao.doQuery(hql, params);
		      res.put("code", Integer.valueOf(200));
		      res.put("msg", "加载转诊记录成功");
		      res.put("result", 
		    		  SchemaUtil.setDictionaryMassageForList(referralList, "chis.application.dr.schemas.DR_Referrals"));
	      } else{
	        res.put("code", Integer.valueOf(4004));
	        res.put("msg", "查询转诊记录失败");
	      }
	    }catch (Exception e){
	      throw new ModelDataOperationException(500, "查询转诊记录失败", e);
	    }
	}
	
	@SuppressWarnings("unchecked")
	public void doSaveSendExchangeReport(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		 Map<String, Object> body = 
			      (Map<String, Object>)req.get("body");
	    Map<String, Object> mpiData = (Map<String, Object>)body.get("mpiData");
	    Map<String, Object> drData = (Map<String, Object>)body.get("drData");
	    Map<String, Object> bodyData = 
	      (Map<String, Object>)body.get("body");
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    if (drData != null) {
	      bodyData.putAll(drData);
	    }
	    String cardNo = (String)mpiData.get("cardNo");
	    String idCard = (String)mpiData.get("idCard");
	    String cardTypeCode = 
	      (String)mpiData.get("cardTypeCode");
	    try {
	      String hql = "select a.recordId as recordId,a.mpiId as mpiId,a.hospitalCode as hospitalCode,a.treatResult as treatResult,a.healthAdvice as healthAdvice," +
	      		"a.leaveConclusion as leaveConclusion,a.submitor as submitor,a.doctorName as doctorName,a.submitAgency as submitAgency,a.reserveNo as reserveNo " +
	      		"from drIt_sendExchangeReport a, DR_DemographicInfo b where a.mpiId = b.mpiId";
	     
	      Map<String,Object> params = new HashMap<String, Object>();
	      String reserveNoStr = (String)bodyData.get("reserveNo");
	      String submitTimeStr = (String)bodyData.get("submitTime");
	      String submitTime = submitTimeStr.substring(0, 10);
	      String submitTime2 = submitTimeStr.replace("T", " ").substring(0, 16);
	      String exchangeTimeStr = (String)bodyData.get("exchangeTime");
	      Date exchangeTime = sdf.parse(exchangeTimeStr.substring(0, 10));
	      List<Map<String,Object>> referralList = new ArrayList<Map<String,Object>>();
	      boolean flag = false;
	      if(reserveNoStr!=null && !"".equals(reserveNoStr)){
	    	  hql+=" and a.reserveNo=:reserveNo";
	    	  params.put("reserveNo",reserveNoStr);
	    	  flag=true;
	      }
	      if(cardNo!=null && !"".equals(cardNo)){
		      if(cardTypeCode!=null && !"".equals(cardTypeCode)){
		    	  hql+=" and b.cardTypeCode=:cardTypeCode and b.cardNo=:cardNo";
		    	  params.put("cardTypeCode",cardTypeCode);
		    	  params.put("cardNo",cardNo);
		    	  flag=true;
		      }
	      }
	      if(idCard!=null && !"".equals(idCard)){
	    	  hql+=" and b.idCard=:idCard";
	    	  params.put("idCard",idCard);
	    	  flag=true;
	      }
	      if(submitTime!=null && !"".equals(submitTime) && flag){
	    	  hql+=" and to_char(a.submitTime,'YYYY-MM-DD')=:submitTime";
	    	  params.put("submitTime",submitTime);
	    	  referralList = dao.doQuery(hql, params);
	      }
	      if (referralList.size() > 0){
	    	  bodyData.putAll((Map<String,Object>)referralList.get(0));
	    	  body.put("body", bodyData);
	    	  res.put("msg", "转诊记录已经存在");
	    	  res.put("result", 
	    	          SchemaUtil.setDictionaryMassageForForm(referralList, "chis.application.dr.schemas.drIt_sendExchangeReport"));
	    	  res.put("code", Integer.valueOf(500));
		      res.put("body", body);
	      }else{
	    	  bodyData.put("submitTime", sdf2.parse(submitTime2));
		      bodyData.put("exchangeTime", exchangeTime);
		      bodyData.put("reserveNo", getReserveNo());
		      Map<String, Object> card = new HashMap<String, Object>();
		      card.put("cardTypeCode", cardTypeCode);
		      card.put("cardNo", cardNo.toUpperCase());
		      card.put("idCard", idCard.toUpperCase());
		      String birthdayStr = (String)mpiData.get("birthday");
		      card.put("birthday", sdf.parse(birthdayStr.substring(0, 10)));
		      card.put("personName", (String)mpiData.get("personName"));
		      card.put("sexCode", (String)mpiData.get("sexCode"));
		      card.put("contactNo", (String)mpiData.get("contactNo"));
		      List<Map<String, Object>> cards = new ArrayList<Map<String,Object>>(1);
		      cards.add(card);
		      body.clear();
		      body.put("cards", cards);
		      List<Map<String, Object>> mpi = new ArrayList<Map<String,Object>>();
		      Map<String,Object> parameters = new HashMap<String, Object>();
	    	  for(Map<String, Object> c:cards){
	    			String sql = "select mpiId as mpiId,cardTypeCode as cardTypeCode,cardNo as cardNo,personName as personName,idCard as idCard,sexCode as sexCode," +
	    					"birthday as birthday,contactNo as contactNo from DR_DemographicInfo where idCard=:idCard";
	    			parameters.clear();
	    			parameters.put("idCard", c.get("idCard")+"");
	    			Map<String,Object> demographicInfo = dao.doLoad(sql, parameters);
	    			if(demographicInfo!=null){
	    				mpi.add(demographicInfo);
	    			}
	    	  }
		      if (mpi == null || mpi.size()==0){
		    	  card.put("mpiId", idCard);
		    	  Map<String, Object> mpiId = dao.doSave("create", DR_DemographicInfo, card, false);
		    	  bodyData.put("mpiId", mpiId.get("mpiId"));
			      res.put("code", Integer.valueOf(200));
		      }else if (mpi.size() > 0){
		    	  bodyData.put("mpiId", 
			            ((Map<String, Object>)mpi.get(0)).get("mpiId"));
		    	  Map<String, Object> mpiMap = (Map<String, Object>)mpi.get(0);
		    	  if(mpiData.get("contactNo")!=null){
		    		  mpiMap.put("contactNo", mpiData.get("contactNo")+"");
		    	  }
		    	  dao.doSave("update", DR_DemographicInfo, mpiMap, false);
			  }
		      res.put("JIESHOUSZSQ", bodyData);
		      dao.doSave("create", drIt_sendExchangeReport, bodyData, false);
	      }
	    } catch (Exception e){
	    	throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取转诊信息失败！", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doUpdateReport(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		try
	    {
	      Map<String, Object> body = 
	        (Map<String, Object>)req.get("body");
	      Map<String, Object> drData = 
	        (Map<String, Object>)req.get("drData");
	      Map<String, Object> mpiData = 
	  	        (Map<String, Object>)req.get("mpiData");
	      
	      String sql = "select mpiId as mpiId,cardTypeCode as cardTypeCode,cardNo as cardNo,personName as personName,idCard as idCard,sexCode as sexCode," +
					"birthday as birthday,contactNo as contactNo from DR_DemographicInfo where idCard=:idCard";
	      Map<String, Object> parameters = new HashMap<String, Object>();
	      parameters.put("idCard", mpiData.get("idCard")+"");
	      Map<String,Object> demographicInfo = dao.doLoad(sql, parameters);
	      if(mpiData.get("contactNo")!=null){
	    	  demographicInfo.put("contactNo", mpiData.get("contactNo")+"");
	  	  }
	  	  dao.doSave("update", DR_DemographicInfo, demographicInfo, false);
	      
	      body.put("drData", req.get("drData"));
	      body.put("mpiData", req.get("mpiData"));
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	      if (drData != null) {
	        body.putAll(drData);
	      }
	      String submitTimeStr = (String)body.get("submitTime");
	      String exchangeTimeStr = (String)body.get("exchangeTime");
	      body.put("submitTime", 
	    		  sdf.parse(submitTimeStr.substring(0, 10)));
	      body.put("exchangeTime", 
	    		  sdf.parse(exchangeTimeStr.substring(0, 10)));
	      String idCard = (String)mpiData.get("idCard");
	      body.put("mpiId", idCard.toUpperCase());
	      dao.doSave("update", drIt_sendExchangeReport, body, false);
	      res.put("code", Integer.valueOf(200));
	      res.put("msg", "更新转诊记录成功");
	    }
	    catch (Exception e)
	    {
	      throw new ModelDataOperationException("更新转诊记录失败", e);
	    }
	}
	
	@SuppressWarnings("unchecked")
	public void doCndQueryReport(Map<String, Object> req, Map<String, Object> res, Context ctx)
		    throws ModelDataOperationException{
	    try {
	      Map<String, Object> body = (Map<String, Object>)req.get("body");
	      if (body != null) {
	    	  String hql = "select a.recordId as recordId,a.mpiId as mpiId,a.hospitalCode as hospitalCode,a.treatResult as treatResult,a.healthAdvice as healthAdvice," +
	      		"a.leaveConclusion as leaveConclusion,a.submitor as submitor,a.doctorName as doctorName,a.submitAgency as submitAgency,a.reserveNo as reserveNo " +
	      		"from drIt_sendExchangeReport a, DR_DemographicInfo b where a.mpiId = b.mpiId and a.mpiId=:mpiId and to_char(a.submitTime,'YYYY-MM-DD')>:submitTime";
		      Map<String,Object> params = new HashMap<String, Object>();
		      String submitTimeStr=(String)body.get("submitTime");
		      params.put("submitTime",submitTimeStr.substring(0, 10));
		      params.put("mpiId",(String)body.get("mpiId"));
		      List<Map<String,Object>> referralList = dao.doQuery(hql, params);
		      res.put("code", Integer.valueOf(200));
		      res.put("msg", "加载转诊记录成功");
		      res.put("result", 
		    		  SchemaUtil.setDictionaryMassageForList(referralList, "chis.application.dr.schemas.drIt_sendExchangeReport"));
	      } else{
	        res.put("code", Integer.valueOf(4004));
	        res.put("msg", "查询转诊记录失败");
	      }
	    }catch (Exception e){
	      throw new ModelDataOperationException(500, "查询转诊记录失败", e);
	    }
	}
	
	@SuppressWarnings("unchecked")
	public void doGetPageUrl_HTTPPOST(Map<String, Object> req, Map<String, Object> res, Context ctx) throws ServiceException{
	    try {
	    	Map<String, Object> body = (Map<String, Object>)req.get("body");
	    	String pdata = body.get("pdata").toString();
	    	String serviceurl = body.get("serviceurl").toString();
			//获取接口url	
			String url=DictionaryController.instance().getDic("chis.dictionary.httppost_drapply").getText("url"); 
			//获取内嵌页面接口url	
			String pageurl=DictionaryController.instance().getDic("chis.dictionary.httppost_drapply").getText("pageurl"); 
			if(pdata.contains("token=")){
				String requestParams = "{\"serviceId\":\"hms.person\",\"method\":\"getAccessToken\",\"body\":[]}";
				//调用接口获取访问权限
				String accessToken = HttpclientUtil.sendHttpPost_JSON(url, requestParams);
				pdata = pdata.replace("token=", "token="+accessToken);
			}
/*			String[] strArray={pdata};
			JSONObject json = new JSONObject(true);
			json.put("serviceId", "hms.person");
			json.put("method", "code");
			json.put("body", strArray);*/
			//调用接口获取加密字符串
			String encryptedStr = HttpclientUtil.sendHttpPost_JSON(url, "{\"serviceId\":\"hms.person\",\"method\":\"code\",\"body\":[\""+pdata+"\"]}");
			encryptedStr = URLEncoder.encode(encryptedStr,"utf-8");
			url = pageurl + serviceurl +"?" + encryptedStr;
			res.put("tagUrl", url);
			System.out.println(url);
	    }catch (Exception e){
        	logger.error("获取双向转诊url参数加密失败！",e);
			throw new ServiceException("获取双向转诊url参数加密失败！",e);
	    }
	}
}
