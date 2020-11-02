package chis.source.hq;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.util.BSCHISUtil;

import com.bsoft.mpi.util.SchemaUtil;
import com.mongodb.util.Hash;

import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

public class HqQueryModel implements BSCHISEntryNames{
	private BaseDAO dao;

	public HqQueryModel(BaseDAO dao) {
		super();
		this.dao = dao;
	}
	
	public Map<String, Object> doQueryHealthCheckPeople(Map<String, Object> req, Map<String, Object> res, Context ctx)
		    throws ModelDataOperationException{
		String cnd="";
		try {
			if(req.get("cnd")!=null){
				cnd=ExpressionProcessor.instance().toString((List<Object>) req.get("cnd"));
			}else{
				cnd="1=1";
			};
		} catch (ExpException e1) {
			e1.printStackTrace();
		}
		List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
		int pageSize = Constants.DEFAULT_PAGESIZE;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = Constants.DEFAULT_PAGENO;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
	    try {
	    	String zhsql=" select a.healthCheck as HEALTHCHECK,a.empiId as EMPIID,a.phrId as PHRID,a.checkDate as CHECKDATE,a.Manaunitid as MANAUNITID,"+
	    			" a.personName as PERSONNAME,a.sexCode as SEXCODE,a.birthday as BIRTHDAY,a.idCard as IDCARD,a.address as ADDRESS,"+
	    			" a.MOBILENUMBER as MOBILENUMBER,a.CONTACTPHONE as CONTACTPHONE,a.status as STATUS,a.checkWay as CHECKWAY,"+
	    			" a.PERSONALCODE as PERSONALCODE,a.symptom as SYMPTOM,a.symptomOt as SYMPTOMOT,a.MQZT as MQZT,"+
	    			" a.temperature as TEMPERATURE,a.breathe as BREATHE,a.pulse as PULSE,a.constriction_L as CONSTRICTION_L,"+
	    			" a.diastolic_L as DIASTOLIC_L,a.constriction as CONSTRICTION,a.diastolic as DIASTOLIC,a.regioncode as REGIONCODE,"+
	    			" a.fbs as FBS,a.tc as TC,a.waistline as WAISTLINE,a.XYGZ as XYGZ,a.XZXY as XZXY,a.XTGZ as XTGZ," +
	    	  		" a.XZGZ as XZGZ,a.YWCB as YWCB,a.SFJD as SFJD,a.JDSJ as JDSJ,a.SFXY as SFXY,a.CREATEUNIT as CREATEUNIT";
	    	String sql="select a.healthCheck as healthCheck,a.empiId as empiId,a.phrId as phrId,a.checkDate as checkDate,a.Manaunitid as Manaunitid," +
	    	  		" b.personName as personName,b.sexCode as sexCode,b.birthday as birthday,b.idCard as idCard,b.address as address," +
	    	  		" b.mobileNumber as mobileNumber,b.contactPhone as contactPhone,c.status as status,a.checkWay as checkWay," +
	    	  		" a.personalCode as personalCode,a.symptom as symptom,a.symptomOt as symptomOt,a.temperature as temperature," +
	    	  		" a.breathe as breathe,a.pulse as pulse,a.constriction_L as constriction_L, a.diastolic_L as diastolic_L," +
	    	  		" a.constriction as constriction,a.diastolic as diastolic,d.fbs as fbs,d.tc as tc,a.waistline as waistline," +
	    	  		" case when a.CONSTRICTION_L>=130 then 1 when a.CONSTRICTION>=130 then 1 " +
	    	  		" when a.DIASTOLIC_L >=85 then 1 when a.DIASTOLIC >=85 then 1 else 0 end as XYGZ," +
	    	  		" case l.wehtherSmoke when '3' then 1 else 0 end as XZXY," +
	    	  		" case when d.fbs >=6.1 then 1 else 0 end as XTGZ," +
	    	  		" case when d.tc >=5.2 then 1 else 0 end as XZGZ," +
	    	  		" case when (b.sexCode='1' and a.waistline >=90) then 1 when (b.sexCode='2' and a.waistline >=85)" +
	    	  		" then 1 else 0 end as YWCB,1 as SFJD,c.createdate as JDSJ,c.regioncode as regioncode," +
	    	  		" l.wehtherSmoke as SFXY,a.CREATEUNIT as CREATEUNIT,b.mqzt as mqzt,"+
	    	  		"((case when a.CONSTRICTION_L >= 130 then 1 when a.CONSTRICTION >= 130 then 1 when a.DIASTOLIC_L >= 85"+ 
	    	  		 "then 1 when a.DIASTOLIC >= 85 then 1 else 0 end) + (case l.wehtherSmoke when '3' then 1 else  0"+
	    	  		 "end) +(case  when d.fbs >= 6.1 then 1 else  0  end) +( case  when d.tc >= 5.2 then  1 else 0 end) +("+
	    	  		 "case when (b.sexCode = '1' and a.waistline >= 90) then"+
	    	  		  " 1 when (b.sexCode = '2' and a.waistline >= 85) then 1 else  0 end)) as HJX";
	    	String sql1=sql;
	    	String sql2=sql;
	    	String except1=" or a.waistline >=90) and b.sexCode='1'";
	    	String except2=" or a.waistline >=85) and b.sexCode='2'";
	    	/****排除既往史、个人主要问题中包含糖尿病和高血压的人员****/
	    	String pasthistorySql = "select t.empiId as empiId from EHR_PastHistory t where t.diseaseText like '%高血压%' or t.diseaseText like '%糖尿病%'";
	    	String healthCheckSql = "select f.empiId as empiId from HC_HealthCheck f where f.OTHERDISEASESONEDESC like '%高血压%' or f.OTHERDISEASESONEDESC like '%糖尿病%'";
//	    	String personproblemSql = "select f.empiId as empiId from EHR_PersonProblem f where f.problemName like '%高血压%' or f.problemName like '%糖尿病%'";
	    	/****排除既往史、个人主要问题中包含糖尿病和高血压的人员****/
	    	String from=" from HC_HealthCheck a, MPI_DemographicInfo b,ehr_healthrecord c," +
	  				" HC_AccessoryExamination d,HC_LifestySituation l " +
	    	  		" where b.empiId=a.empiId and a.healthCheck=d.healthCheck" +
	    	  		" and a.healthCheck=l.healthCheck and c.empiId=a.empiId " +
	    	  		" and not exists (select 1 from mdc_hypertensionrecord hy where hy.empiid=a.empiId" +
	    	  		" and hy.createdate <a.checkDate and hy.status='0')" +
	    	  		" and not exists (select 1 from mdc_diabetesrecord di where di.empiid=a.empiId"+
	    	  		" and di.createdate <a.checkDate and di.status='0') and a.constriction_L <140 and a.diastolic_L <90" +
	    	  		" and a.constriction <140 and a.diastolic <90 and d.fbs <7 and d.tc <6.2" +
//	    	  		" and a.OTHERDISEASESONEDESC not like '%高血压%' and a.OTHERDISEASESONEDESC not like '%糖尿病%'"+
	    	  		" and a.empiId not in ("+pasthistorySql+")" +
	    	  		" and a.empiId not in ("+healthCheckSql+")" +
//	    	  		" and a.empiId not in ("+personproblemSql+")"+
	    	  		" and (d.tc >=5.2 or d.fbs >=6.1 or a.constriction_L >=130 or a.constriction >=130" +
	    	  		" or a.diastolic_L >=85 or a.diastolic>=85 or l.wehtherSmoke='3'";
	    	String hql = zhsql+" from ("+sql1+from+except1+" union all " +sql2+from+except2+") a where "+cnd;
	    	String entryname="chis.application.hq.schemas.HC_HealthCheck";
	    	if(req.containsKey("needcheck") && "1".equals(""+req.get("needcheck"))){
	    		entryname=entryname+"_NEXT";
	    		zhsql+=",b.phrId as HIGHRISKPHRID,b.EMPIID as HIGHRISKEMPIID," +
	    				" case when b.phrId is null then '0' else '1' end as SFGL,b.createDate as GLSJ ";
	    		hql=zhsql+" from ("+sql1+from+except1+" union all " +sql2+from+except2+") a " +
	    				"left join MDC_HighRiskRecord b on b.phrId=a.phrId where "+cnd;
	    		hql+=" and a.XYGZ+a.XZXY+a.XTGZ+a.XZGZ+a.YWCB >=3 ";
		    }
	    	String countsql="select count(1) as totalCount from ("+hql+")";

	    	StringBuffer countSQL = new StringBuffer(countsql);
		  	Map<String, Object> pMap = new HashMap<String, Object>();
			List<Map<String, Object>> thrvpcList = null;
			try {
				thrvpcList = dao.doSqlQuery(countSQL.toString(), pMap);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "分页查询高危人群时失败！", e);
			}
			long totalCount = 0;
			if (thrvpcList != null && thrvpcList.size() > 0) {
				Map<String, Object> trMap = thrvpcList.get(0);
				totalCount = ((BigDecimal) trMap.get("TOTALCOUNT")).longValue();
			}
			if (totalCount > 0) {
		      Map<String,Object> params = new HashMap<String, Object>();
		      int first = (pageNo - 1) * pageSize;
		      params.put("first", first);
		      params.put("max", pageSize);
		      List<Map<String,Object>> dataList = dao.doSqlQuery(hql, params);
		      if(dataList.size()>0){
		    	  for(Map<String,Object> dta:dataList){
		    			  dta.put("HJX", Integer.parseInt(dta.get("XYGZ")+"")+Integer.parseInt(dta.get("XZXY")+"")+
		    			  Integer.parseInt(dta.get("XTGZ")+"")+Integer.parseInt(dta.get("XZGZ")+"")+
		    			  Integer.parseInt(dta.get("YWCB")+""));
		    			  dta.put("FXTJ","体检");
		    	  }
		      }
		      
		      records = SchemaUtil.setDictionaryMassageForList(dataList,entryname);
		      res.put("code", Integer.valueOf(200));
		      res.put("msg", "加载高危人群成功");
		      res.put("totalCount", totalCount);
		      res.put("body", records);
			}else{
				res.put("totalCount", 0);
				res.put("body", new ArrayList<Map<String, Object>>());
			}
			res.put("pageSize", pageSize);
			res.put("pageNo", pageNo);
			return res;
	    }catch (Exception e){
	    	throw new ModelDataOperationException(500, "查询高危人群失败", e);
	    }
	}
	public void doSaveHighRiskVisit(Map<String, Object> req, Map<String, Object> res, Context ctx)
		    throws ModelDataOperationException{
		Map<String, Object> plandata=(Map<String, Object>)req.get("plandata");
		Map<String, Object> visitdata=(Map<String, Object>)req.get("visitdata");
		String empiId=plandata.get("empiId")+"";
		String phrId=plandata.get("phrId")+"";
		String visitId=plandata.get("visitId")+"";
		if(empiId==null || empiId.length()<10 ||phrId==null||phrId.length()<10){
			res.put("code","400");
			res.put("msg","随访信息传值问题！");
			return;
		}
		String op="create";
		if(visitId!=null && visitId.length()>10){
			op="update";
		}else{
			visitdata.put("empiId", empiId);
			visitdata.put("phrId", phrId);
		}
		Map<String, Object> data=new HashMap<String, Object>();
		try {
			data=dao.doSave(op, MDC_HighRiskVisit, visitdata, true);
			Map<String, Object> p=new HashMap<String, Object>();
			p.put("planId", plandata.get("planId")+"");
			p.put("visitDate", BSCHISUtil.toDate(visitdata.get("visitDate")+""));
			String thisvisitId=data.get("visitId")+"";
			String upsql="update PUB_VisitPlan set planStatus='1',visitDate=:visitDate ";
			if(op.equals("create")){
				p.put("visitId", thisvisitId);
				upsql+=",visitId=:visitId where planId=:planId";
			}else{
				upsql+=" where planId=:planId";
			}
			dao.doUpdate(upsql, p);
			String planDate=visitdata.get("nextDate")==null?"":visitdata.get("nextDate")+"";
			if(planDate.length()==10){
				Map<String, Object> planmap=new HashMap<String, Object>();
				planmap.put("empiId", empiId);
				planmap.put("phrId", phrId);
				planmap.put("planDate", planDate);
				doInsertHighRiskPlan(planmap,res,ctx);
			}
		} catch (ValidateException e) {
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
	public void doInsertHighRiskPlan(Map<String, Object> data, Map<String, Object> res, Context ctx)
		    throws ModelDataOperationException{
		String empiId=data.get("empiId")==null?"":data.get("empiId")+"";
		if(empiId.length()<10){
			res.put("code",400);
			res.put("msg","信息传入有误，不能产生计划！");
		}
		String planDate=data.get("planDate")==null?"":data.get("planDate")+"";
		if(planDate.length()==8){
			planDate=planDate.substring(0,4)+"-"+planDate.substring(4,6)+"-"+planDate.substring(6,8);
		}
		Date date=BSCHISUtil.toDate(planDate);
		Calendar cDay = Calendar.getInstance();
		cDay.setTime(date);
		cDay.add(Calendar.WEEK_OF_MONTH, 2);
		Date datee=new Date();
		Date dateb=new Date();
		datee.setTime(cDay.getTimeInMillis());
		cDay.add(Calendar.WEEK_OF_MONTH, -4);
		dateb.setTime(cDay.getTimeInMillis()); 
		String findsql="select count(1) as SL from PUB_VisitPlan " +
				" where empiId=:empiId and businessType='"+BusinessType.HIGH_RISK+"' " +
				" and planDate >=:dateb and planDate<=:datee";
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("empiId", empiId);
		p.put("dateb", dateb);
		p.put("datee", datee);
		try {
			Map<String, Object> planmap=dao.doSqlLoad(findsql, p);
			Boolean flag=true;
			if(planmap!=null&&planmap.size()>0){
				int sl=planmap.get("SL")==null?0:Integer.parseInt(planmap.get("SL")+"");
				if(sl>0){
					flag=false;
					res.put("code",400);
					res.put("msg","计划日期前后二周内已有随访计划不能新增！");
				}
			}
			if(flag){
				String maxsql="select nvl(max(sn),0)+1 as SN from PUB_VisitPlan where empiId=:empiId and businessType='"+BusinessType.HIGH_RISK+"'";
				p.remove("dateb");
				p.remove("datee");
				Map<String, Object> maxmap=dao.doSqlLoad(maxsql, p);
				if(maxmap!=null && maxmap.size()>0){
					int sn=Integer.parseInt(maxmap.get("SN")+"");
					Map<String, Object> savedata=new HashMap<String, Object>();
					savedata.put("empiId", empiId);
					savedata.put("recordId", data.get("phrId")+"");
					savedata.put("businessType",BusinessType.HIGH_RISK);
					savedata.put("groupCode", "01");
					savedata.put("fixGroupDate", date);
					savedata.put("beginDate", dateb);
					savedata.put("endDate", datee);
					savedata.put("beginVisitDate",dateb);
					savedata.put("planDate", date);
					savedata.put("planStatus", "0");
					savedata.put("sn", sn);
					savedata.put("visitMeddle", "0");
					try {
						dao.doInsert(PUB_VisitPlan, savedata, false);
					} catch (ValidateException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 查询高危人群管理
	 * @param req
	 * @param res
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doQueryHighRiskRecordList(Map<String, Object> req, Map<String, Object> res, Context ctx)
		    throws ModelDataOperationException{
		String cnd="";
		try {
			if(req.get("cnd")!=null){
				cnd=ExpressionProcessor.instance().toString((List<Object>) req.get("cnd"));
//				if(cnd.indexOf("a.DEFINEPHRID")>-1){
//					cnd=cnd.replace("a.DEFINEPHRID", "b.DEFINEPHRID");
//				}
//				if(cnd.indexOf("a.SEXCODE")>-1){
//					cnd=cnd.replace("a.SEXCODE", "b.SEXCODE");
//				}
//				if(cnd.indexOf("a.BIRTHDAY")>-1){
//					cnd=cnd.replace("a.BIRTHDAY", "b.BIRTHDAY");
//				}
//				if(cnd.indexOf("a.IDCARD")>-1){
//					cnd=cnd.replace("a.IDCARD", "b.IDCARD");
//				}
//				if(cnd.indexOf("a.PERSONNAME")>-1){
//					cnd=cnd.replace("a.PERSONNAME", "b.PERSONNAME");
//				}
//				if(cnd.indexOf("a.MOBILENUMBER")>-1){
//					cnd=cnd.replace("a.MOBILENUMBER", "b.MOBILENUMBER");
//				}
//				if(cnd.indexOf("a.REGISTEREDPERMANENT")>-1){
//					cnd=cnd.replace("a.REGISTEREDPERMANENT", "b.REGISTEREDPERMANENT");
//				}
//				if(cnd.indexOf("a.REGIONCODE")>-1){
//					cnd=cnd.replace("a.REGIONCODE", "c.REGIONCODE");
//				}
//				if(cnd.indexOf("a.FAMILYDOCTORID")>-1){
//					cnd=cnd.replace("a.FAMILYDOCTORID", "c.FAMILYDOCTORID");
//				}
			}else{
				cnd="1=1";
			};
		} catch (ExpException e1) {
			e1.printStackTrace();
		}
		List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
		int pageSize = Constants.DEFAULT_PAGESIZE;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = Constants.DEFAULT_PAGENO;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
	    try {
	    	String sql="select * from(select a.phrId as phrId,b.definePhrid as definePhrid,b.personName as personName,b.sexCode as sexCode,b.birthday as birthday,b.idCard as idCard," +
	    			"b.address as address,b.mobileNumber as mobileNumber,b.registeredPermanent as registeredPermanent,c.regionCode as regionCode," +
	    			"c.familyDoctorId as familyDoctorId,a.empiId as empiId,a.manaDoctorId as manaDoctorId,a.manaUnitId as manaUnitId,a.HIGHRISKTYPE as HIGHRISKTYPE,a.findWay as findWay," +
	    			"a.constriction as constriction,a.diastolic as diastolic,a.isSmoke as isSmoke,a.smokeCount as smokeCount,a.fbs as fbs,a.tc as tc,a.waistLine as waistLine," +
	    			"a.createUnit as createUnit,a.createUser as createUser,a.createDate as createDate,a.status as status,a.cancellationDate as cancellationDate," +
	    			"a.cancellationReason as cancellationReason,a.cancellationUser as cancellationUser,a.lastModifyDate as lastModifyDate,a.lastModifyUnit as lastModifyUnit," +
	    			"a.cancellationUnit as cancellationUnit,c.regionCode_text as regionCode_text,b.mqzt as mqzt,length(replace(a.highrisktype,',',''))as hjx";
	    	String from=" from MDC_HighRiskRecord a, MPI_DemographicInfo b,ehr_healthrecord c" +
	    	  		" where b.empiId=a.empiId and c.empiId=a.empiId and c.phrId = a.phrId) a where "+cnd;
	    	String order = " order by a.createDate desc,a.phrId desc";
	    	String hql = sql+from+order;
	    	String entryname="chis.application.hq.schemas.MDC_HighRiskRecord_NEXT";
	    	String countsql="select count(1) as totalCount from ("+hql+")";
	    	
	  	    StringBuffer countSQL = new StringBuffer(countsql);
		  	Map<String, Object> pMap = new HashMap<String, Object>();
			List<Map<String, Object>> thrvpcList = null;
			try {
				thrvpcList = dao.doSqlQuery(countSQL.toString(), pMap);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "分页查询高危人群时失败！", e);
			}
			long totalCount = 0;
			if (thrvpcList != null && thrvpcList.size() > 0) {
				Map<String, Object> trMap = thrvpcList.get(0);
				totalCount = ((BigDecimal) trMap.get("TOTALCOUNT")).longValue();
			}
			if (totalCount > 0) {
		      Map<String,Object> params = new HashMap<String, Object>();
		      int first = (pageNo - 1) * pageSize;
		      params.put("first", first);
		      params.put("max", pageSize);
		      List<Map<String,Object>> dataList = dao.doSqlQuery(hql, params);
		      if(dataList.size()>0){
		    	  for(Map<String,Object> dta:dataList){
		    	  }
		      }
		      
		      records = SchemaUtil.setDictionaryMassageForList(dataList,entryname);
		      res.put("code", Integer.valueOf(200));
		      res.put("msg", "加载高危人群成功");
		      res.put("totalCount", totalCount);
		      res.put("body", records);
			}else{
				res.put("totalCount", 0);
				res.put("body", new ArrayList<Map<String, Object>>());
			}
			res.put("pageSize", pageSize);
			res.put("pageNo", pageNo);
			return res;
	    }catch (Exception e){
	    	throw new ModelDataOperationException(500, "查询高危人群失败", e);
	    }

	}
    
    /**
	 * 修改档案当前状态
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doVerify(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("mqzt", body.get("MQZT")!=null?body.get("MQZT").toString():"");
		parameters.put("empiId", body.get("empiId")+"");
		StringBuffer hql = new StringBuffer();
		hql.append("update MPI_DemographicInfo set mqzt=:mqzt where empiId=:empiId");
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(500, "修改档案当前状态失败", e);
		}
	}
}
