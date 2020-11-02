package phis.application.reg.source;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mongodb.util.Hash;

import phis.application.ivc.source.ClinicChargesProcessingModel;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.account.user.UserController;
import ctd.print.PrintException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;

public class RegisteredManagementService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(RegisteredManagementService.class);

	/**
	 * 查询排版科室
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQuerySchedulingDepartment(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
		try {
			rmm.doQuerySchedulingDepartment(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("科室排班查询失败！", e);
		}
	}

	/**
	 * 查询排班医生
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQuerySchedulingDoctor(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
		try {
			rmm.doQuerySchedulingDoctor(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("医生排班查询失败！", e);
		}
	}

	/**
	 * 获取就诊号码
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doUpdateDoctorNumbers(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
		try {
			rmm.doUpdateDoctorNumbers(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("挂号初始化失败！", e);
		}
	}

	/**
	 * 挂号结算查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryRegisteredSettlement(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
		try {
			rmm.doQueryRegisteredSettlement(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("挂号结算查询失败！", e);
		}
	}

	/**
	 * 挂号信息保存
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveRegisteredManagement(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
		try {
			rmm.doSaveRegisteredManagement(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 挂号单据查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryGhdj(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
		// String JZKH = (String) req.get("JZKH");
		try {
			rmm.doQueryGhdj(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("单据查询失败！", e);
		}
	}

	/**
	 * 挂号单据查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryGhdjs(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
		String BRID = req.get("body") + "";
		try {
			rmm.doQueryGhdjs(BRID, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("单据查询失败！", e);
		}
	}

	/**
	 * 退号
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveRetireRegistered(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
		Map<String,Object> body = (Map<String,Object>)req.get("body");
		try {
			rmm.doSaveRetireRegistered(body, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("退号失败！", e);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	public void doSaveRetireRegisteredSzyb(Map<String, Object> req,
//			Map<String, Object> res, BaseDAO dao, Context ctx)
//			throws ServiceException {
//		Map<String, Object> body = (Map<String, Object>) req.get("body");
//		RegisteredManagementModel rmm = new RegisteredManagementModel(
//				dao);
//		try {
//			rmm.doSaveRetireRegisteredSzyb(body, res, ctx);
//		} catch (ModelDataOperationException e) {
//			throw new ServiceException("发票作废失败！", e);
//		}
//	}

	/**
	 * 挂号查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doRegisteredQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
		try {
			rmm.doRegisteredQuery(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("挂号查询失败！", e);
		}
	}

//	public void doUpdateQueue(Map<String, Object> req, Map<String, Object> res,
//			BaseDAO dao, Context ctx) throws ServiceException {
//		RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
//		try {
//			rmm.doUpdateQueue(ctx);
//		} catch (ModelDataOperationException e) {
//			throw new ServiceException(e);
//		}
//	}
	/**
	 * 发票打印
	 * @throws PrintException 
	 */
	public void doPrintMoth(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
		try {
			rmm.doPrintMoth(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 挂号预检保存
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveGHYJ(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
		try {
			rmm.doSaveGHYJ(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("挂号预检保存失败！", e);
		}
	}
	
	/**
	 * 发票打印(增加科室排列序号)
	 * 
	 * @throws PrintException
	 */
	public void doPrintMoth2(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
		try {
			rmm.doPrintMoth2(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 查询（紫金数云）预约挂号记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryAppointmentRecord(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
		try {
			rmm.doQueryAppointmentRecord(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 预约挂号取号
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveAppointmentRecord(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
		try {
			rmm.doSaveAppointmentRecord(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	//add-yx-2018-09-25-获取分时预约列表
	public void doGetFyssList(Map<String, Object> req, Map<String, Object> res,
	BaseDAO dao, Context ctx) throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		List queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = (List) req.get("cnd");
		}
		int pageSize =25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 1;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal=Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE,10);
		
		try{
			String where=ExpressionProcessor.instance().toString(queryCnd);
			where=where.replaceAll("str","to_char");
			StringBuffer sql=new StringBuffer();
			sql.append("select a.HYXH as HYXH,a.ZBLB as ZBLB,a.PATIENTNAME as PATIENTNAME,d.PERSONNAME as YSMC,")
			.append("b.KSDM as KSDM,to_char(a.HYSJ,'yyyy-MM-dd HH24:mi:ss') as HYSJ,a.HYZT as HYZT,a.CARDID as CARDID,")
			.append("b.KSMC as KSMC,c.XJJE as XJJE,c.BRXZ as BRXZ")
			.append(" from MS_YYGHHY a join MS_GHKS b on a.HYKS=b.KSDM")
			.append(" left join MS_GHMX c on a.GHXH=c.SBXH")
			.append(" left join SYS_PERSONNEL d on a.HYYS=d.PERSONID")
			.append(" where "+where+" and a.HYJG='"+manaUnitId+"'")
			.append(" and a.HYSJ>=to_date('"+df.format(cal.getTime())+"','yyyy-MM-dd') order by a.HYSJ");
			Map<String,Object>p=new HashMap<String,Object>();
			p.put("first",(pageNo-1)*pageSize);
			p.put("max",pageNo*pageSize);
			try{
				List<Map<String,Object>>re=dao.doSqlQuery(sql.toString(),p);
				re=SchemaUtil.setDictionaryMassageForList(re,BSPHISEntryNames.MS_YYGHHY_LIST);
				res.put("pageSize",pageSize);
				res.put("pageNo",pageNo);
				res.put("totalCount",re.size());
				res.put("body",re);
			}catch(PersistentDataOperationException e){
				e.printStackTrace();
			}
		}catch(ExpException e){
			e.printStackTrace();
		}
	}
	//add-yx-2018-09-25-保存预约
	public void doSaveYyxx(Map<String, Object> req, Map<String, Object> res,
	BaseDAO dao, Context ctx) throws ServiceException {
		RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
		try {
			rmm.doSaveYyxx(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	//add-yx-2018-09-25-取消预约
	public void doSaveRetreatYyxx(Map<String, Object> req, Map<String, Object> res,
	BaseDAO dao, Context ctx) throws ServiceException {
		RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
		try {
			rmm.doSaveRetreatYyxx(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	//add-yx-2018-11-12-转农合
	public void doSaveTurnToNh(Map<String, Object> req, Map<String, Object> res,
	BaseDAO dao, Context ctx) throws PersistentDataOperationException {
		res.put("turnMsg","");
		String HYXH=req.get("HYXH")+"";
		StringBuffer sql=new StringBuffer();
		sql.append("select b.NHKH as NHKH,a.BRXZ as BRXZ,a.SBXH as SBXH,a.BRID as BRID from MS_GHMX a,MS_BRDA b ")
		.append(" where a.BRID=b.BRID and a.THBZ=0 and a.HYXH="+HYXH);
		Map<String, Object> re=dao.doSqlLoad(sql.toString(),null);
		if((re.get("BRXZ")+"").equals("6000")){
			res.put("turnMsg","此病人已转换成农合");
		}
		dao.doSqlUpdate("update MS_BRDA set BRXZ=6000 where BRID="+re.get("BRID"),null);
		dao.doSqlUpdate("update MS_GHMX set BRXZ=6000 where SBXH="+re.get("SBXH"),null);
		if(re.get("NHKH")==null || (re.get("NHKH")+"").length()==0){
			res.put("turnMsg","由于农合信息缺失，请在农合读卡功能中，读出该病人信息后双击该病人信息，但不需要挂号。");
		}
	}
	//add-yx-2018-11-15-医保验证病人信息
	public void doCheckPerson(Map<String,Object> req,Map<String,Object> res,BaseDAO dao,Context ctx) 
	throws PersistentDataOperationException {
		String HYXH=req.get("HYXH")+"";
		String SFZH=req.get("SFZH")+"";
		StringBuffer sql=new StringBuffer();
		Map<String,Object> checkre=new HashMap<String,Object>();
		Integer CHECK=0;//正常
		String KSDM="";
		String LXDH="";
		sql.append("select b.SHBZKH as SHBZKH,a.HYKS as KSDM,b.LXDH as LXDH from MS_YYGHHY a,MS_BRDA b")
		.append(" where a.BRID=b.BRID and a.HYXH="+HYXH+" and a.SFZH='"+SFZH+"'");
		Map<String,Object> cm=dao.doSqlLoad(sql.toString(),null);
		if(cm!=null && cm.size()>0){
			if(cm.get("SHBZKH")==null || (cm.get("SHBZKH")+"").isEmpty()){
				CHECK=1;//以前没用医保看过病
			}
			KSDM=cm.get("KSDM")+"";
			LXDH=cm.get("LXDH")==null?"":cm.get("LXDH")+"";
		}else{
			CHECK=2;//病人信息不匹配
		}
		checkre.put("CHECK",CHECK);
		checkre.put("KSDM",KSDM);
		checkre.put("LXDH",LXDH);
		res.put("checkre",checkre);
	}
	//add-yx-2018-11-15-转医保
	public void doSaveTurnToYb(Map<String, Object> req, Map<String, Object> res,BaseDAO dao, Context ctx)
	throws PersistentDataOperationException {
		Map<String, Object> body=(Map<String, Object>)req.get("body");
		String HYXH=body.get("HYXH")+"";
		StringBuffer sql=new StringBuffer();
		sql.append("select b.NHKH as NHKH,a.BRXZ as BRXZ,a.SBXH as SBXH,a.BRID as BRID from MS_GHMX a,MS_BRDA b ")
		.append(" where a.BRID=b.BRID and a.THBZ=0 and a.HYXH="+HYXH);
		Map<String, Object> re=dao.doSqlLoad(sql.toString(),null);
		if((re.get("BRXZ")+"").equals("2000")){
			res.put("turnMsg","此病人已转换成医保");
		}
		dao.doSqlUpdate("update MS_BRDA set BRXZ=2000 where BRID="+re.get("BRID"),null);
		dao.doSqlUpdate("update MS_GHMX set BRXZ=2000,NJJBLSH='"+body.get("NJJBLSH")+"'," +
				"NJJBYLLB='"+body.get("NJJBYLLB")+"',YBMC='"+body.get("YBMC")+"' where SBXH="+re.get("SBXH"),null);
	}
	
	//获取义诊标志
	public void doGetYZBZ(Map<String, Object> req, Map<String, Object> res,BaseDAO dao, Context ctx){
		Map<String, Object> body=(Map<String, Object>)req.get("body");
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("SBXH", body.get("SBXH"));
		StringBuffer sql = new StringBuffer();
		sql.append("select YZBZ from MS_GHMX where SBXH = :SBXH");
		try {
			Map<String, Object> result = new HashMap<String, Object>();
			result = dao.doSqlLoad(sql.toString(), params);
			res.put("body", result);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
