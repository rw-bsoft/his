/**
 * @(#)CommonModel.java Created on 2013-5-8 下午4:02:01
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.conf.SystemCofigManageModel;
import chis.source.phr.HealthRecordService;
import chis.source.util.CNDHelper;
import chis.source.util.ManageYearUtil;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class CommonModel implements BSCHISEntryNames {
	protected Logger logger = LoggerFactory
			.getLogger(CommonModel.class);
	protected BaseDAO dao;
	public CommonModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 获取基本医疗个人健康档案参数
	 * 
	 * @Description:
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-5-8 下午4:10:26
	 * @Modify:
	 */
	public void getPhisShowEhrView(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao)throws ModelDataOperationException {
		try {
			SystemCofigManageModel scmm = new SystemCofigManageModel(dao);
			String s=scmm.getSystemConfigData("PhisShowEhrViewType");
			res.put("PhisShowEhrViewType", s);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, "获取最近处方信息失败！", e);
		}
	}
	
	
	
	/**
	 * 导入药品
	 * 
	 * @Description:
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-5-8 下午4:10:26
	 * @Modify:
	 */
	public Map<String, Object> saveDrugInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao)throws ModelDataOperationException {
		
		Map<String, Object> rsMap = null;
		Map<String, Object> paramMap =(Map)req.get("param");
		
		String brid=paramMap.get("brid").toString();
		int pageSize = Integer.parseInt(req.get("pageSize") + "");
		int pageNo = Integer.parseInt(req.get("pageNo") + "");
		
		
		StringBuilder totalSql = new StringBuilder("select a.cfsb,a.kfrq,b.ypxh,c.ypmc,c.ypsx,d.sxmc,c.jldw,b.mrcs,c.ycjl,b.mrcs*c.ycjl zjl");
		//totalSql.append();
		
		
		
		try {
			
			
			
			

		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, "获取最近处方信息失败！", e);
		} 

		return rsMap;
	}
	
	/**
	 * 查询是否存在过敏已经存在过敏或疾病史
	 * 
	 * @Description:
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-5-8 下午4:10:26
	 * @Modify:
	 */
	public void doSavePastHistory(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao,Context ctx)throws ModelDataOperationException {
		Map<String, Object> body =(Map)req.get("body");
		Map<String, Object> param =new HashMap<String, Object>();
		
		List<Map> records=(List<Map>)body.get("record");
		Map record=records.get(0);
		//根据empiid和过敏类别查询改类是否已有记录，有则不保存，没有则添加
		String hql="select pasthistoryid as PASTHISTORYID from EHR_PastHistory where empiId=:empiId and diseaseCode=:diseaseCode and diseaseText=:diseaseText";
		param.put("empiId",record.get("empiId"));
		param.put("diseaseCode",record.get("diseaseCode"));
		param.put("diseaseText",record.get("diseaseText"));
		
		try {
			List cl=dao.doSqlQuery(hql, param);
			if(cl.size()<1)
			{
				res.put("flag",true);
			}else
			{
				res.put("flag",false);				
			}
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, "保存既往史信息失败！", e);
		} 
	}
	/**
	 * 删除当天本人操作的个人既往史
	 * 
	 * @Description:
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-5-8 下午4:10:26
	 * @Modify:
	 */
	public void doDelPastHistory(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao,Context ctx)throws ModelDataOperationException {
		Map<String, Object> body =(Map)req.get("body");
		Map<String, Object> param =new HashMap<String, Object>();
		
		List<Map> records=(List<Map>)body.get("record");
		try {
		for(int i=0;i<records.size();i++)
		{
		Map record=records.get(i);
		Object diseaseCodeObj=record.get("diseaseCode");
		Object diseaseTextObj=record.get("diseaseText");
		String diseaseCode=null;
		String diseaseText=null;
		if(diseaseCodeObj!=null)
		{
			diseaseCode=(String)diseaseCodeObj;
		}
		if(diseaseTextObj!=null)
		{
			diseaseText=(String)diseaseTextObj;
		}
		//根据empiid和过敏类别查询改类是否已有记录，有则不保存，没有则添加
		Date startDate=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Calendar c=Calendar.getInstance();
		c.setTime(startDate);
		c.add(Calendar.DATE, 1);
		Date endDate=c.getTime();

		String startDateStr=sdf.format(startDate);
		String endDateStr=sdf.format(endDate);
		
		String hql="delete from EHR_PastHistory where empiId=:empiId and pastHisTypeCode=:pastHisTypeCode and recordUser=:ysid and recordDate>=to_date(:startDate,'yyyy-mm-dd hh24:mi:ss') and recordDate<to_date(:endDate,'yyyy-mm-dd hh24:mi:ss')";
		Session session=dao.getSession();

			if(diseaseCode!=null)
			{
				hql+=" and diseaseCode=:diseaseCode";
			}
			if(diseaseText!=null)
			{
				hql+=" and diseaseText=:diseaseText";
			}
			Query q=session.createQuery(hql);
			q.setParameter("empiId",record.get("empiId"));
			q.setParameter("pastHisTypeCode",record.get("pastHisTypeCode"));
			q.setParameter("ysid",record.get("ysid"));
			
			q.setParameter("startDate",startDateStr);
			q.setParameter("endDate",endDateStr);


			if(diseaseCode!=null)
			{
				q.setParameter("diseaseCode", diseaseCode);
			}	
			if(diseaseText!=null)
			{
				q.setParameter("diseaseText", diseaseText);
			}
			q.executeUpdate();

		}
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, "删除既往史信息失败！", e);
		} 
	}
	/**
	 * 保存心脑血管报告卡
	 * 
	 * @Description:
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-5-8 下午4:10:26
	 * @Modify:
	 */
	public void saveDiseaseManagement(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao,Context ctx)throws ModelDataOperationException {
		Map<String, Object> body =(Map)req.get("body");
		Map<String, Object> param =new HashMap<String, Object>();
		
		String empiId=(String)body.get("empiId");
		//根据empiid查询28天内是存在心脑血管报卡（根据本次发病时间）
		String hql="select bcfbrqsj as bcfbrqsj from cvd_diseasemanagement where empiId=:empiId order by bcfbrqsj desc";
		param.put("empiId", empiId);
		try {
		List<Map<String,Object>> list=dao.doSqlQuery(hql, param);
		if(list.size()>=1)
		{
			Map<String,Object> resultMap=list.get(0);
			String bcfbrqsj=(String)resultMap.get("BCFBRQSJ");
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date d1 = new Date();
		    Date d2 = df.parse(bcfbrqsj);
		    long diff = d1.getTime() - d2.getTime();
		    long days = diff / (1000 * 60 * 60 * 24);
		    if(days>=28)//超过28天则添加漏报
		    {
		    
		    	
		    }

		}else
		{
			//不存在则添加漏报
			
			
		}
		
		
		
		
//		param.put("empiId",record.get("empiId"));
//		param.put("diseaseCode",record.get("diseaseCode"));
//		param.put("diseaseText",record.get("diseaseText"));
	
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, "保存既往史信息失败！", e);
		} 
	}
	
	
	/**
	 * 获取年度随访计划列表<br>
	 * 取年度随访/询问记录 共用方法
	 * 
	 * @param yearType
	 *            [1:上一年 2：本年 3 ：下一年]
	 * @param empiId
	 * @param current
	 *            向上或向下 的年数,本年度该参数无效
	 * @param businessType
	 *            随访/询问 随访计划类型 <br>
	 *            eg. 高血压随访 BusinessType.GXY
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	public List<Map<String, Object>> getVisitPlan(int yearType, String empiId,
			int current, String businessType)
			throws ModelDataOperationException, ServiceException {
		if (StringUtils.isEmpty(empiId)) {
			throw new ModelDataOperationException(
					Constants.CODE_BUSINESS_DATA_NULL, "The empiId is null!");
		}
		String strStartDate ="";
		String strEndDate = "";
		Calendar c = Calendar.getInstance();
		int nowYear=c.get(c.YEAR);
		if (yearType == 1) {
			strStartDate =(nowYear+current)+"-01-01";
			strEndDate =(nowYear+current)+"-12-30";
		} else if (yearType == 2) {
			strStartDate =nowYear+"-01-01";
			strEndDate =nowYear+"-12-30";
		} else if (yearType == 3) {
			strStartDate =(nowYear+current)+"-01-01";
			strEndDate =(nowYear+current)+"-12-30";
		}
		
		String cnd1="[\"and\",[\"eq\", [\"$\", \"EMPIID\"], [\"s\", \""+ empiId+"\"]]," +
				   			 "[\"eq\", [\"$\", \"SFLB\"], [\"i\", \""+ businessType + "\"]]]";
		String cnd2="[\"and\",[\"ge\", [\"$\", \"str(KSRQ,'yyyy-MM-dd')\"], [\"s\", \""+ strStartDate+"\"]]," +
							 "[\"le\", [\"$\", \"str(JSRQ,'yyyy-MM-dd')\"], [\"s\", \""+ strEndDate + "\"]]]";
		String cnd = "[\"and\", " + cnd1 + ", " + cnd2 + "]]";
		List list=new ArrayList();
		try {
			list = dao.doQuery(CNDHelper.toListCnd(cnd),"JHSFRQ","chis.application.mh.schemas.SQ_ZKSFJH");
		} catch (ExpException e) {
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return list;
	}
}
