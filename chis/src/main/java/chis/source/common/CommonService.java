/**
 * @(#)CommonService.java Created on 2013-5-8 下午3:13:10
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.common;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.common.entity.AbstractSearchModule;
import chis.source.dic.BusinessType;
import chis.source.mdc.DiabetesVisitModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.DiabetesVisitPlanModel;
import chis.source.visitplan.VisitPlanModel;
import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class CommonService extends AbstractActionService implements
		DAOSupportable {

	private static Logger logger = LoggerFactory
			.getLogger(CommonService.class);
	
	/**
	 * 获取基本医疗个人健康档案参数
	 * 
	 * @Description:
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-5-8 下午3:28:41
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doGetPhisShowEhrView(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> bodyMap = (Map<String, Object>) req.get("body");
		try {
			CommonModel commonModule = new CommonModel(dao);
			commonModule.getPhisShowEhrView(req,res,dao);
		} catch (Exception e) {
			logger.error("get phisShowEhrView failed.", e);
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 导入药品
	 * 
	 * @Description:
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-5-8 下午3:28:41
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDrugInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> bodyMap = (Map<String, Object>) req.get("body");
		try {
			CommonModel commonModule = new CommonModel(dao);
			commonModule.saveDrugInfo(req,res,dao);
		} catch (Exception e) {
			logger.error("geg clinc  drugInfo failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", null);
	}

	/**
	 * 保存个人既往史
	 * 
	 * @Description:
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-5-8 下午3:28:41
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSavePastHistory(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> bodyMap = (Map<String, Object>) req.get("body");
		try {
			CommonModel commonModule = new CommonModel(dao);
			commonModule.doSavePastHistory(req,res,dao,ctx);
		} catch (Exception e) {
			logger.error("save pastHistory failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", null);
	}
	
	/**
	 * 删除当天本人操作的个人既往史
	 * 
	 * @Description:
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-5-8 下午3:28:41
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doDelPastHistory(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> bodyMap = (Map<String, Object>) req.get("body");
		try {
			CommonModel commonModule = new CommonModel(dao);
			commonModule.doDelPastHistory(req,res,dao,ctx);
		} catch (Exception e) {
			logger.error("delete pastHistory failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", null);
	}
	
	/**
	 * 加载药品信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doLoadDicData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String packageName = "chis.source.common.entity.";
		try {
			HttpServletRequest request = (HttpServletRequest) ctx.get(Context.HTTP_REQUEST);
			String clz = (String) req.get("className");
			AbstractSearchModule asm = (AbstractSearchModule) Class.forName(
					packageName + clz + "SearchModule").newInstance();
			Cookie cookies[] = request.getCookies();
			if (cookies != null) {
				for (int i = 0; i < cookies.length; i++) {
					Cookie c = cookies[i];
					if (c.getName().equals(user.getUserId() + "_searchType")) {
						asm.SEARCH_TYPE = c.getValue();
					}
					if (c.getName().equals(user.getUserId() + "_matchType")) {
						String matchType = c.getValue();
						if ("ALL".equals(matchType)) {
							asm.MATCH_TYPE = "%";
						}else if("LEFT".equals(matchType)) {
							asm.MATCH_TYPE = "";
						}
					}
				}

			}
			asm.open(ctx);
			//response.setContentType("text/plain");
			asm.execute(req, res, ctx);
		} catch (Exception e) {
			logger.error("search service failed.", e);
		} finally {
			//close(ctx);
		}
	}
	
	
	
	/**
	 * 加载网格地址信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doLoadAreaGridData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String packageName = "chis.source.common.entity.";
		try {
			HttpServletRequest request = (HttpServletRequest) ctx.get(Context.HTTP_REQUEST);
			String clz = (String) req.get("className");
			AbstractSearchModule asm = (AbstractSearchModule) Class.forName(
					packageName + clz + "SearchModule").newInstance();
			Cookie cookies[] = request.getCookies();
			if (cookies != null) {
				for (int i = 0; i < cookies.length; i++) {
					Cookie c = cookies[i];
					if (c.getName().equals(user.getUserId() + "_searchType")) {
						asm.SEARCH_TYPE = c.getValue();
					}
					if (c.getName().equals(user.getUserId() + "_matchType")) {
						String matchType = c.getValue();
						if ("ALL".equals(matchType)) {
							asm.MATCH_TYPE = "%";
						}else if("LEFT".equals(matchType)) {
							asm.MATCH_TYPE = "";
						}
					}
				}

			}
			asm.open(ctx);
			//response.setContentType("text/plain");
			asm.execute(req, res, ctx);
		} catch (Exception e) {
			logger.error("search service failed.", e);
		} finally {
			//close(ctx);
		}
	}
	
	
	/**
	 * 加载档案信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doLoadRecordInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			Map body = (Map) req.get("body");
			String empiId=(String)body.get("empiId");
			
			String recordType="chis";
			if(body.get("recordType")!=null)
			{
				 recordType=(String)body.get("recordType");
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			List resList=new ArrayList();
			//根据empiId查询所有档案
			StringBuilder sb=new StringBuilder(" select GRDA as GRDA, ");
			sb.append(" GRQY as GRQY,GAO as GAO,LI as LI,LAO as LAO,TANG as TANG,YI as YI,XIAN as XIAN,BAO as BAO,FU as FU, ");
			sb.append(" ER as ER,RUO as RUO,JING as JING,CANZT as CANZT,CANN as CANN,CANZL as CANZL,LOGOUT as LOGOUT, ");
			sb.append(" YI_DC as YI_DC,XIAN_DC as XIAN_DC,YI_WEI as YI_WEI,XIAN_WEI as XIAN_WEI,YI_GAN as YI_GAN,XIAN_GAN as XIAN_GAN,YI_FEI as YI_FEI, ");
			sb.append(" XIAN_FEI as XIAN_FEI,YI_RX as YI_RX,XIAN_RX as XIAN_RX,YI_GJ as YI_GJ,XIAN_GJ as XIAN_GJ ");
			sb.append(" from EHR_RECORDINFO where EMPIID=:EMPIID ");
			Map param=new HashMap();
			param.put("EMPIID",empiId);
			
			List l=dao.doSqlQuery(sb.toString(), param);
			
			List zlList=new ArrayList();
		
			LinkedHashMap<String,String> rexMap=BSCHISEntryNames.RECORDSMAP_CHIS;
			if(recordType.equals("phis"))
			{
				rexMap=BSCHISEntryNames.RECORDSMAP_PHIS;	
			}
			if(l.size()>0)
			{
				Map<String,BigDecimal> resMap=(Map)l.get(0);
				
				if(resMap.get("LOGOUT")==null||resMap.get("LOGOUT").intValue()!=1)
				//循环
				{
					for(String key:rexMap.keySet())
					{
						if(resMap.get(key)!=null&&resMap.get(key).intValue()==1)
						{
							resList.add(rexMap.get(key));
						}
						
					}
					
					
					//判断是否需要显示肿瘤，只要有一个类型易患，现患，或者报告卡为1，则显示
					
					String[] zlss={"DC","WEI","GAN","FEI","RX","GJ"};
					String[] zlzs={"大肠","胃","肝","肺","乳腺","宫颈"};
					boolean bflag=false;
					BigDecimal BAO=resMap.get("BAO");
					if(BAO!=null&&BAO.intValue()==1)
					{
						bflag=true;
					}
					boolean flag=false;
					for (int i = 0; i < zlss.length; i++) {
						Map tempMap=new HashMap();
						flag=false;
						tempMap.put("typeValue", (i+1)+"");						
						tempMap.put("typeName",zlzs[i]);						
						BigDecimal YI=resMap.get("YI_"+zlss[i]);
						BigDecimal XIAN=resMap.get("XIAN_"+zlss[i]);
						if(YI!=null&&YI.intValue()==1)
						{
							tempMap.put("YI","1");
							flag=true;
						}else
						{
							tempMap.put("YI","0");
						}
						if(XIAN!=null&&XIAN.intValue()==1)
						{
							tempMap.put("XIAN","1");
							flag=true;
						}else
						{
							tempMap.put("XIAN","0");
						}
						if(flag)
						{
							bflag=true;
							zlList.add(tempMap);
						}
						
					}
					if(bflag)
					{
							resList.add(BSCHISEntryNames.RECORDSMAP_CHIS.get("YI"));
						if(recordType.equals("phis"))
						{
							resList.add(BSCHISEntryNames.RECORDSMAP_PHIS.get("YI"));
						}
					}
					//存在肿瘤报告卡
					res.put("zlBao",'0');
					if(resMap.get("BAO")!=null&&resMap.get("BAO").intValue()==1)
					{
						res.put("zlBao",'1');
					}
					//add by Wangjl
					String sql=" select a.crowdtype from mpi_demographicinfo a where a.empiid='"+empiId+"'";
					Map<String, Object> sql1 = null;
					sql1 = dao.doSqlLoad(sql, null);
					if(((String) sql1.get("CROWDTYPE"))!=null){
					if(((String) sql1.get("CROWDTYPE")).indexOf("04")!=-1){
						resList.add("RQ_01");
					}
					if(((String) sql1.get("CROWDTYPE")).indexOf("05")!=-1){
						resList.add("RQ_02");
					}
					if(((String) sql1.get("CROWDTYPE")).indexOf("06")!=-1){
						resList.add("RQ_03");
					}
					if(((String) sql1.get("CROWDTYPE")).indexOf("07")!=-1){
						resList.add("RQ_04");
					}
					if(((String) sql1.get("CROWDTYPE")).indexOf("08")!=-1){
						resList.add("RQ_05");
					}
					if(((String) sql1.get("CROWDTYPE")).indexOf("09")!=-1){
						resList.add("RQ_06");
					}
					if(((String) sql1.get("CROWDTYPE")).indexOf("10")!=-1){
						resList.add("RQ_07");
					}
					if(((String) sql1.get("CROWDTYPE")).indexOf("11")!=-1){
						resList.add("RQ_08");
					}
					if(((String) sql1.get("CROWDTYPE")).indexOf("12")!=-1){
						resList.add("RQ_09");
					}
					if(((String) sql1.get("CROWDTYPE")).indexOf("13")!=-1){
						resList.add("RQ_10");
					}
					if(((String) sql1.get("CROWDTYPE")).indexOf("14")!=-1){
						resList.add("RQ_11");
					}
					if(((String) sql1.get("CROWDTYPE")).indexOf("15")!=-1){
						resList.add("RQ_12");
					}
					if(((String) sql1.get("CROWDTYPE")).indexOf("16")!=-1){
						resList.add("RQ_13");
					}
					if(((String) sql1.get("CROWDTYPE")).indexOf("17")!=-1){
						resList.add("RQ_14");
					}
					if(((String) sql1.get("CROWDTYPE")).indexOf("18")!=-1){
						resList.add("RQ_15");
					}
					if(((String) sql1.get("CROWDTYPE")).indexOf("19")!=-1){
						resList.add("RQ_16");
					}
					if(((String) sql1.get("CROWDTYPE")).indexOf("20")!=-1){
						resList.add("RQ_17");
					}
					}
				}

			}
			String sql2=" select count(1)+10 as SIGNFLAG  from SCM_SignContractRecord a where to_char(a.beginDate,'yyyy-mm-dd')<=to_char(sysdate,'yyyy-mm-dd') and to_char(a.endDate,'yyyy-mm-dd')>=to_char(sysdate,'yyyy-mm-dd') and  a.favoreeEmpiId='"+empiId+"'";
			Map<String,Object> sql21=dao.doSqlLoad(sql2, null);
			Integer temp=0;
			if(sql21!=null && sql21.size() >0 ){
				temp=Integer.parseInt(sql21.get("SIGNFLAG")+"");
				if(temp!=10){
					resList.add("B_91");
				}
			}
			
			res.put("icos", resList);
			res.put("zlList", zlList);

		} catch (Exception e) {
			logger.error("loadRecordInfo failed.", e);
		} finally {
			//close(ctx);
		}
	}
	
	
	/**
	 * 查询肿瘤档案id
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doLoadTrRecordId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			Map body = (Map) req.get("body");
			String empiId=(String)body.get("empiId");
			String highRiskType=(String)body.get("highRiskType");

			Map param=new HashMap();
			param.put("empiId", empiId);
			param.put("highRiskType", highRiskType);
			
			//报告卡
			List l1=dao.doSqlQuery("select TPRCID as TPRCID  from MDC_TumourPatientReportCard where empiId=:empiId and highRiskType=:highRiskType", param);
			if(l1.size()>0)
			{
				res.put("TPRCID",((Map)l1.get(0)).get("TPRCID"));
			}
			//高危易患
			List l2=dao.doSqlQuery("select THRID as THRID  from Mdc_Tumourhighrisk where empiId=:empiId and highRiskType=:highRiskType", param);
			if(l2.size()>0)
			{
				res.put("THRID",((Map)l2.get(0)).get("THRID"));
			}
			//确诊现患
			List l3=dao.doSqlQuery("select TCID as TCID  from Mdc_Tumourconfirmed where empiId=:empiId and highRiskType=:highRiskType", param);
			if(l3.size()>0)
			{
				res.put("TCID",((Map)l3.get(0)).get("TCID"));
				Map map4=dao.doLoad(MDC_TumourConfirmed, (String)((Map)l3.get(0)).get("TCID"));
				Map mapData=SchemaUtil.setDictionaryMessageForList(map4, MDC_TumourConfirmed);
				res.put("tcrData",mapData);
			}
		} catch (Exception e) {
			logger.error("loadRecordInfo failed.", e);
		} finally {
		}
	}
	
	
	
	/**
	 * 更新个人档案信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			Map body = (Map) req.get("body");
			String empiId=(String)body.get("empiId");
			String businessType=(String)body.get("businessType");
			int businessValue=(Integer)body.get("businessValue");
			// 如果存在档案则更新,不存在则插入
			Map param=new HashMap();
			param.put("empiId", empiId);
			List l=dao.doQuery("select empiId as empiId  from EHR_RecordInfo where empiId=:empiId", param);
			if(l.size()>0)//存在则更新
			{
				param.put(businessType,businessValue);
				dao.doUpdate("update EHR_RecordInfo set "+businessType+"=:"+businessType+" where empiId=:empiId", param);
				
			}else//不存在插入
			{
				param.put(businessType,businessValue);
				dao.doInsert(BSCHISEntryNames.EHR_RecordInfo, param, false);
				//dao.doSq("insert into EHR_RecordInfo(empiId,"+businessType+") values(:empiId,:"+businessType+")", param);
			}


		} catch (Exception e) {
			logger.error("loadRecordInfo failed.", e);
		} finally {
		}
	}
	
	
	/**
	 * 保存心脑血管报告卡
	 * 
	 * @Description:
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-5-8 下午3:28:41
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDiseaseManagement(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> bodyMap = (Map<String, Object>) req.get("body");
		try {
			CommonModel commonModule = new CommonModel(dao);
			commonModule.saveDiseaseManagement(req,res,dao,ctx);
		} catch (Exception e) {
			logger.error("save pastHistory failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", null);
	}
	
	/**
	 * 根据下次随访日期生成随访计划
	 * 
	 * @Description:
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-5-8 下午3:28:41
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveVisitPlan(Map<String, Object> req,BaseDAO dao)
			throws ServiceException {
		try {
			
			//删除下次随访时间以前的所有未随访的记录
			Date visitDate=(Date)req.get("visitDate");
			String delSql="delete from PUB_VisitPlan where empiId=:empiId and businessType=:businessType and planDate>=:planDate and visitId is null";
			Session session=dao.getSession();
			Query q=session.createQuery(delSql);
			q.setParameter("empiId",req.get("empiId"));
			q.setParameter("businessType",req.get("businessType"));
			q.setParameter("planDate",req.get("planDate"));
			q.executeUpdate();
			dao.doSave("create",PUB_VisitPlan, req, true);
		} catch (Exception e) {
			logger.error("save pastHistory failed.", e);
			throw new ServiceException(e);
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
		String instanceType =(String)req.get("SFLB");
		List<Map<String, Object>> rsList = null;
		try {
			CommonModel vpm = new CommonModel(dao);
			rsList = vpm.getVisitPlan(1, empiId, current, instanceType);
			res.put("body", rsList);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get visit plan of current.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取上年度随访计划失败！");
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
		String instanceType =(String)req.get("SFLB");
		List<Map<String, Object>> rsList = null;
		try {
			CommonModel vpm = new CommonModel(dao);
			rsList = vpm.getVisitPlan(2, empiId, current, instanceType);
			res.put("body", rsList);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get visit plan of current.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取本年度随访计划失败！");
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
		String instanceType =(String)req.get("SFLB");
		List<Map<String, Object>> rsList = null;
		try {
			CommonModel vpm = new CommonModel(dao);
			rsList = vpm.getVisitPlan(3, empiId, current, instanceType);
			res.put("body", rsList);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get visit plan of current.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取下年度随访计划失败！");
			throw new ServiceException(e);
		}
	}
	
	
	/**
	 * 获取检验明细
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void doGetVisitRecordDetial(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> r = (Map<String, Object>) req.get("r");
		DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
		List<Map<String, Object>> mList = dvm.getVisitComplication(
				(String) r.get("recordId"), (String) r.get("visitId"));
		res.put("body", mList);
	}

}
