/**
 * @(#)SignContractRecordService.java Created on 2017-11-9 下午1:32:05
 * <p>
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.scm;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.net.rpc.util.ServiceAdapter;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.S;
import ctd.util.annotation.RpcService;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 * @description
 */
public class SignContractRecordService extends AbstractActionService implements
        DAOSupportable {
    private static final Logger logger = LoggerFactory
            .getLogger(SignContractRecordService.class);

    /**
     * 加载某签约记录所有的包信息
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    public void doLoadTheSignContractPackage(Map<String, Object> req,
                                             Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
        String SCID = (String) reqBodyMap.get("SCID");
        if (S.isNotEmpty(SCID)) {
            List<Object> cnd = CNDHelper.createSimpleCnd("eq", "a.SCID", "s",
                    SCID);
            List<Map<String, Object>> rsList = null;
            try {
                rsList = dao.doQuery(cnd, null, SCM_SignContractPackage);
            } catch (PersistentDataOperationException e) {
                e.printStackTrace();
            }
            if (rsList != null && rsList.size() > 0) {
                res.put("body", rsList);
            }
        }
    }

    /**
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     * @Description:保存签约，生成签约计划 和 工作任务
     * @author ChenXianRui 2017-11-9 下午1:54:34
     * @Modify:
     */
    @SuppressWarnings("unchecked")
    public void doSaveSignContract(Map<String, Object> req,
                                   Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
        String empiId = (String) reqBodyMap.get("favoreeEmpiId");
        if (S.isEmpty(empiId)) {
            throw new ServiceException(Constants.CODE_INVALID_REQUEST,
                    "个人主索引不能为空！");
        }
        // 签约人所属人群
        String scip = (String) reqBodyMap.get("intendedPopulation");
        // 签约生产日期
        Date bDate = null;
        if (reqBodyMap.get("beginDate") instanceof String) {
            bDate = BSCHISUtil.toDate((String) reqBodyMap.get("beginDate"));
        } else {
            bDate = (Date) reqBodyMap.get("beginDate");
        }
        Date eDate = null;
        if (reqBodyMap.get("endDate") instanceof String) {
            eDate = BSCHISUtil.toDate((String) reqBodyMap.get("endDate"));
        } else {
            eDate = (Date) reqBodyMap.get("endDate");
        }
        String op = (String) req.get("op");
        // 保存签约记录
        Map<String, Object> rMap = null;
        try {
            String stopReason = (String) reqBodyMap.get("stopReason");
            if (S.isNotEmpty(stopReason)) {
                reqBodyMap.put("signFlag", "2");// 解约
            } else {
                reqBodyMap.put("signFlag", "1");// 签约
            }
            rMap = dao.doSave(op, SCM_SignContractRecord, reqBodyMap, true);
        } catch (PersistentDataOperationException e) {
            logger.error("保存签约记录失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "保存签约记录失败！", e);
        }
        String SCID = (String) reqBodyMap.get("SCID");
        if ("create".equals(op)) {
            SCID = (String) rMap.get("SCID");
        }
        res.put("body", rMap);
        String signFlag = (String) reqBodyMap.get("signFlag");
        //更新签约图标标识 EHR_RecordInfo.GRQY
        String qyop = "2".equals(signFlag) ? "delete" : "create";
        vLogService.saveRecords("GRQY", qyop, dao, empiId);
        // 修改个人基本中的是否签约标识
        String upHQL = new StringBuffer("update ")
                .append(MPI_DemographicInfo)
                .append(" set signFlag=:singFlag,sceBeginDate=:beginDate,sceEndDate=:endDate")
                .append(" where empiId=:empiId").toString();
        Map<String, Object> pMap = new HashMap<String, Object>();
        if ("1".equals(signFlag)) {// 签约时
            pMap.put("singFlag", "1");
            pMap.put("beginDate", bDate);
            pMap.put("endDate", eDate);
        } else {
            pMap.put("singFlag", "2");
            pMap.put("beginDate", null);
            pMap.put("endDate", null);
        }
        pMap.put("empiId", empiId);
        try {
            dao.doUpdate(upHQL, pMap);
        } catch (PersistentDataOperationException e) {
            logger.error("更新居民基本信息中签约标识失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "更新居民基本信息中签约标失败！", e);
        }
        // 更新所属家庭
        String familyId = (String) reqBodyMap.get("familyId");
        String ownerName = (String) reqBodyMap.get("ownerName");
        if (S.isNotEmpty(familyId)) {
            String upfHQL = new StringBuffer("update ")
                    .append(MPI_DemographicInfo)
                    .append(" set familyId=:familyId,ownerName=:ownerName ")
                    .append(" where empiId=:empiId").toString();
            Map<String, Object> fMap = new HashMap<String, Object>();
            fMap.put("familyId", familyId);
            fMap.put("ownerName", ownerName);
            fMap.put("empiId", empiId);
            try {
                dao.doUpdate(upfHQL, fMap);
            } catch (PersistentDataOperationException e) {
                logger.error("更新居民所属家庭失败！", e);
                throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                        "更新居民所属家庭标失败！", e);
            }
        }
        // 生计签约计划
        SignContractRecordModule scrm = new SignContractRecordModule(dao);
        scrm.genSCPlan(SCID, empiId, bDate, eDate, scip);
    }

    /**
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     * @Description:解除签约，更新签约计划 和 工作任务
     * @author guoliang
     * @throws PersistentDataOperationException 
     * @Modify:
     */
    @SuppressWarnings("unchecked")
    public void doStopSignStatus(Map<String, Object> req,
                                 Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException, PersistentDataOperationException {
    	ctx = ContextUtils.getContext();
        Session ss = (Session) ctx.get(Context.DB_SESSION);
        if (ss == null) {
            SessionFactory sf = AppContextHolder.getBean(
                    AppContextHolder.DEFAULT_SESSION_FACTORY,
                    SessionFactory.class);
            ss = sf.openSession();
            ctx.put(Context.DB_SESSION, ss);
        }
        dao = new BaseDAO(ctx,ss);
		try {
            ss.beginTransaction();
            Map<String, Object> reqBodyMap = (Map<String, Object>) req
                    .get("formData");
            String empiId = (String) reqBodyMap.get("favoreeEmpiId");
            String scid = (String) reqBodyMap.get("SCID");
            Map<String, Object> rMap = null;
            long count = dao.doSqlCount("ms_yj01",
    				" fphm is not null and zfpb=0 and yjxh in(select yjxh from scm_increaseserver where scid="+scid+" and yjxh is not null)", null);
			if (count > 0) {
				throw new ServiceException(Constants.CODE_RECORD_EXSIT,
						"本次签约已完成收费，无法解约！");
			}
            String stopReason = (String) reqBodyMap.get("stopReason");
            if (S.isNotEmpty(stopReason)) {
                reqBodyMap.put("signFlag", "2");// 解约
            }
            reqBodyMap.put("stopDate", reqBodyMap.get("stopDate"));
            reqBodyMap.remove("fphm");
            reqBodyMap.remove("payOrNot");
            rMap = dao.doSave("update", SCM_SignContractRecord, reqBodyMap,
                    false);
            res.put("body", rMap);
            // 修改个人基本中的是否签约标识
            String upHQL = new StringBuffer("update ")
                    .append("MPI_DemographicInfo")
                    .append(" set signFlag=2,sceBeginDate=null,sceEndDate=null")
                    .append(" where empiId='"+empiId+"'")
                    .append(" and not exists(select SCID from SCM_SIGNCONTRACTRECORD where SCID<>"+scid+" and signFlag=1 and favoreeEmpiId='"+empiId+"' and endDate>=to_date(to_char(sysdate,'yyyy-mm-dd'),'yyyy-mm-dd'))").toString();
            dao.doSqlUpdate(upHQL, null);            
            upHQL ="delete from ms_yj02 where yjxh in(select yjxh from scm_increaseserver where scid="+scid+" and yjxh is not null) " +
            		"and exists(select yjxh from ms_yj01 where ms_yj02.yjxh=yjxh and fphm is null and zfpb=0)";
            //删除签约包收费单据ms_yj01、ms_yj02
            upHQL ="delete from ms_yj02 where yjxh in(select yjxh from scm_increaseserver where scid="+scid+" and yjxh is not null) " +
            		"and exists(select yjxh from ms_yj01 where ms_yj02.yjxh=yjxh and fphm is null and zfpb=0)";
            dao.doSqlUpdate(upHQL, null);
            upHQL ="delete from ms_yj01 where fphm is null and zfpb=0 and yjxh in(select yjxh from scm_increaseserver where scid="+scid+" and yjxh is not null)";
            dao.doSqlUpdate(upHQL, null);
            // 更新签约计划为解约
            SignContractRecordModule scrm = new SignContractRecordModule(dao);
            scrm.stopContractPlanTaskStatus(scid);
            ss.beginTransaction().commit();
            System.out.println("解约成功");
        } catch(ServiceException e){
            ss.getTransaction().rollback();
			logger.error("解约失败！", e.getMessage());
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
            		"解约失败！"+e.getMessage(), e);
		}finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }

    /**
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     * @Description:保存签约所选择的服务包
     * @author ChenXianRui 2017-11-10 下午2:30:03
     * @Modify:
     */
    @SuppressWarnings("unchecked")
    public void doSaveSignContractPackage(Map<String, Object> req,
                                          Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        List<Map<String, Object>> reqList = (List<Map<String, Object>>) req
                .get("body");
        if (reqList != null && reqList.size() > 0) {
            for (Map<String, Object> map : reqList) {
                try {
                    dao.doSave("create", SCM_SignContractPackage, map, false);
                } catch (PersistentDataOperationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     * @Description:保存签约，生成签约计划 和 工作任务
     * @author guol 2018-2-6 下午1:54:34
     * @Modify:
     */
    @SuppressWarnings("unchecked")
    public void doSaveSignContractPlan(Map<String, Object> req,
                                       Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException {
    	ctx = ContextUtils.getContext();
        Session ss = (Session) ctx.get(Context.DB_SESSION);
        if (ss == null) {
            SessionFactory sf = AppContextHolder.getBean(
                    AppContextHolder.DEFAULT_SESSION_FACTORY,
                    SessionFactory.class);
            ss = sf.openSession();
            ctx.put(Context.DB_SESSION, ss);
        }
        dao = new BaseDAO(ctx,ss);
		try {
            ss.beginTransaction();          
			Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("formData");
			//如果是工卫签约则发票号码为ysf，默认为已收费
            if(reqBodyMap.get("clinicId") == null){
            	reqBodyMap.put("fphm","ysf"); 
            	reqBodyMap.put("payOrNot", "1");
            }else{
            	reqBodyMap.put("payOrNot", "2");
            }
			String empiId = (String) reqBodyMap.get("favoreeEmpiId");
			SignContractRecordModule scrm = new SignContractRecordModule(dao);
			int times = Integer.parseInt(reqBodyMap.get("year") + "");
			if (S.isEmpty(empiId)) {
				throw new ServiceException(Constants.CODE_INVALID_REQUEST,
						"个人主索引不能为空！");
			}
			// 签约人所属人群
			String scip = (String) reqBodyMap.get("intendedPopulation");
			// 签约生产日期
			Calendar a = Calendar.getInstance();
			// 是否已经存在有效的签约记录
			boolean flag = SignContractRecordService.doHavePersonnalContractRecord(empiId, dao);
			Date bDate;
			Date eDate;
			String signFlag = (String) reqBodyMap.get("signFlag");
			if (flag && signFlag.equals("3")) { // edit by zhj 解决签约保存时间问题
				Date now = new Date();
				// 1.取出签约记录最大的beginDate endDate
				Map<String, Object> map = new HashMap<String, Object>();
				map = SignContractRecordService.doGetMaxBeginDateandEndDate(empiId, dao);
				// Date beginDate=(Date) map.get("beginDate");
				// 若当前日期在上次签约结束日期之前，取上次结束日期的下一天为基准日期 否则取当前日期为基准日期
				Date endDate = (Date) map.get("endDate");
				if (now.before(endDate)) {
					a.setTime(endDate);
					a.add(Calendar.DAY_OF_YEAR, 1);
				} else {
					a.setTime(now);
				}
				// 2.生成下一条计划的beginDate endDate
				// a.setTime(endDate);

				bDate = a.getTime();
				// eDate = BSCHISUtil.toDate(a.get(Calendar.YEAR) + times - 1
				// + "-12-31");
				a.add(Calendar.YEAR, times);
				a.add(Calendar.DAY_OF_YEAR, -1);
				eDate = a.getTime();

			} else {
				// bDate = BSCHISUtil.toDate(a.get(Calendar.YEAR) + "-01-01");
				// eDate = BSCHISUtil.toDate(a.get(Calendar.YEAR) + times - 1
				// + "-12-31");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date scdate = sdf.parse(reqBodyMap.get("scDate").toString());
				a.setTime(scdate);
				bDate = BSCHISUtil.toDate(reqBodyMap.get("scDate").toString());
				a.add(Calendar.YEAR, times);
				a.add(Calendar.DAY_OF_YEAR, -1);
				eDate = a.getTime();
			}
			reqBodyMap.put("beginDate", bDate);
			reqBodyMap.put("endDate", eDate);
			// String createUnit1 = (String) reqBodyMap.get("createUnit");
			// String createUnit =createUnit1.substring(0, 9);
			// reqBodyMap.put("createUnit", createUnit);
			String op = "";
			Long scid = (Long) reqBodyMap.get("SCID");
			if (scid != null && !scid.equals("")) {
				op = "update";
			} else {
				op = "create";
			}
			// 保存签约记录
			Map<String, Object> rMap = null;
	        try {
				String stopReason = (String) reqBodyMap.get("stopReason");
				// String signFlag = (String) reqBodyMap.get("signFlag");
				// if (S.isNotEmpty(stopReason)) {
				// reqBodyMap.put("signFlag", "2");// 解约
				// } else {
				// reqBodyMap.put("signFlag", "1");// 签约
				// }
				rMap = dao.doSave(op, SCM_SignContractRecord, reqBodyMap, false);
				((Map<String, Object>) req.get("formData")).put("scid", rMap.get("SCID"));
	        } catch (PersistentDataOperationException e) {
	            logger.error("保存签约记录失败！", e);
	            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
	                    "保存签约记录失败！", e);
	        }
			if (scid == null || scid.equals("")) {
				scid = (Long) rMap.get("SCID");
			}
			res.put("body", rMap);
			// 保存签约相关服务包
			scrm.saveSignContractPackage(empiId, scid, req);
			// 修改个人基本中的是否签约标识
			// String signFlag = (String) reqBodyMap.get("signFlag");
			String upHQL = new StringBuffer("update ")
					.append(MPI_DemographicInfo)
					.append(" set signFlag=:singFlag,sceBeginDate=:beginDate,sceEndDate=:endDate")
					.append(" where empiId=:empiId").toString();
			Map<String, Object> pMap = new HashMap<String, Object>();
			if ("1".equals(signFlag)) {// 签约时
				pMap.put("singFlag", "1");
				pMap.put("beginDate", bDate);
				pMap.put("endDate", eDate);
			} else {
				pMap.put("singFlag", "2");
				pMap.put("beginDate", null);
				pMap.put("endDate", null);
			}
			pMap.put("empiId", empiId);
	        try {
				dao.doUpdate(upHQL, pMap);
	        } catch (PersistentDataOperationException e) {
	            logger.error("更新居民基本信息中签约标识失败！", e);
	            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
	                    "更新居民基本信息中签约标失败！", e);
	        }
			// 更新所属家庭
			String familyId = (String) reqBodyMap.get("familyId");
			String ownerName = (String) reqBodyMap.get("ownerName");
			if (S.isNotEmpty(familyId)) {
				String upfHQL = new StringBuffer("update ")
						.append(MPI_DemographicInfo)
						.append(" set familyId=:familyId,ownerName=:ownerName ")
						.append(" where empiId=:empiId").toString();
				Map<String, Object> fMap = new HashMap<String, Object>();
				fMap.put("familyId", familyId);
				fMap.put("ownerName", ownerName);
				fMap.put("empiId", empiId);
				try {
					dao.doUpdate(upfHQL, fMap);            
				} catch (PersistentDataOperationException e) {
					logger.error("更新居民所属家庭失败！", e);
	                throw new ServiceException(Constants.CODE_DATABASE_ERROR,
	                        "更新居民所属家庭标失败！", e);
				}
			}
			// 生计签约计划
			List<Map<String, Object>> records = scrm.genSignContractPlan(req,
					empiId, scid);
			req.put("planList", records);
            try{
    			this.doSaveSignContractToHis(req, res, dao, ctx);
            }catch (Exception e) {
                throw new ServiceException(Constants.CODE_SERVICE_ERROR, e.getMessage());
            }
            ss.beginTransaction().commit();
			res.put("body", records);
            System.out.println("签约成功");
		}catch (ParseException e) {
            ss.getTransaction().rollback();
			logger.error("签约失败！", e.getMessage());
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "签约失败！"+e.getMessage(), e);
		}catch(ServiceException e){
            ss.getTransaction().rollback();
			logger.error("签约失败！", e.getMessage());
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
            		"签约失败！"+e.getMessage(), e);
		}finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }

    /**
     * 更新工作任务状态
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    public void doUpdateTaskStatus(Map<String, Object> req,
                                   Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        Map<String, Object> par = new HashMap<String, Object>();
        par.put("taskId", req.get("taskId"));
        String sql = "update SCM_ServiceContractPlanTask set status='1' where taskId=:taskId";
        try {
            dao.doUpdate(sql, par);
        } catch (PersistentDataOperationException e) {
            // TODO Auto-generated catch block
            logger.error("更新签约任务状态失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "更新签约任务状态失败！", e);
        }
    }

    /**
     * 更新工作任务状态为未完成
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    public void doUpdateTaskStatus2(Map<String, Object> req,
                                    Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        Map<String, Object> par = new HashMap<String, Object>();
        par.put("taskId", req.get("taskId"));
        String sql = "update SCM_ServiceContractPlanTask set status='0' where taskId=:taskId";
        try {
            dao.doUpdate(sql, par);
        } catch (PersistentDataOperationException e) {
            // TODO Auto-generated catch block
            logger.error("更新签约任务状态失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "更新签约任务状态失败！", e);
        }
    }

    /**
     * 批量更新工作任务状态
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    @SuppressWarnings("unchecked")
    public void doUpdateAllTaskStatus(Map<String, Object> req,
                                      Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        Map<String, Object> par = new HashMap<String, Object>();
        String sql = "update SCM_ServiceContractPlanTask set status='1',visitId=:visitId,planId=:planId where taskId=:taskId";
        try {
            List<Map<String, Object>> taskIdArray = (List<Map<String, Object>>) req
                    .get("taskIdArray");
            List<String> visitIdArray = (List<String>) req.get("visitIdArray");
            List<String> planIdArray = (List<String>) req.get("planIdArray");
            for (int i = 0; i < taskIdArray.size(); i++) {
                String taskId = taskIdArray.get(i).get("taskId") + "";
                String planId = "";
                String visitId = "";
                if (!planIdArray.isEmpty()) {
                    planId = planIdArray.get(i);
                }
                if (!visitIdArray.isEmpty()) {
                    visitId = visitIdArray.get(i);
                }
                par.put("taskId", taskId);
                par.put("planId", planId);
                par.put("visitId", visitId);
                dao.doUpdate(sql, par);
            }
        } catch (PersistentDataOperationException e) {
            // TODO Auto-generated catch block
            logger.error("更新签约任务状态失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "更新签约任务状态失败！", e);
        }
    }

    @SuppressWarnings("unchecked")
    public void doRemoveSCR(Map<String, Object> req, Map<String, Object> res,
                            BaseDAO dao, Context ctx) throws ServiceException {
        Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
        String SCID = (String) reqBodyMap.get("SCID");
        String empiId = (String) reqBodyMap.get("empiId");
        String signFlag = (String) reqBodyMap.get("signFlag");
        try {
            dao.doRemove("SCID", SCID, SCM_SignContractPackage);
            dao.doRemove("SCID", SCID, SCM_ServicePlanTask);
            dao.doRemove("SCID", SCID, SCM_ServicePlan);
            dao.doRemove("SCID", SCID, SCM_ServiceRecords);
            dao.doRemove(SCID, SCM_SignContractRecord);
        } catch (PersistentDataOperationException e) {
            logger.error("删除签约信息失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "删除签约信息失败！", e);
        }
        // 删除签约记录时更新签约状态
        if ("1".equals(signFlag)) {
            String upHQL = new StringBuffer("update ")
                    .append(MPI_DemographicInfo)
                    .append(" set signFlag=:singFlag,sceBeginDate=:beginDate,sceEndDate=:endDate")
                    .append(" where empiId=:empiId").toString();
            Map<String, Object> pMap = new HashMap<String, Object>();
            pMap.put("singFlag", "2");
            pMap.put("beginDate", null);
            pMap.put("endDate", null);
            pMap.put("empiId", empiId);
            try {
                dao.doUpdate(upHQL, pMap);
            } catch (PersistentDataOperationException e) {
                logger.error("更新居民基本信息中签约标识失败！", e);
                throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                        "更新居民基本信息中签约标失败！", e);
            }
        }
    }

    /**
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     * @Description:保存维护服务包所属项目
     * @author ChenXianRui 2017-11-10 下午2:30:03
     * @Modify:
     */
    @SuppressWarnings("unchecked")
    public void doSavePackagetoServiceItem(Map<String, Object> req,
                                           Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        List<Map<String, Object>> reqList = (List<Map<String, Object>>) req
                .get("body");
        if (reqList != null && reqList.size() > 0) {
            for (Map<String, Object> map : reqList) {
                try {
                    if ("1".equals(map.get("isNew") + "")) {
                        map.remove("isNew");
                        dao.doSave("create", SCM_ServicePackageItems, map,
                                false);
                    } else {
                        map.remove("isNew");
                        dao.doSave("update", SCM_ServicePackageItems, map,
                                false);
                    }
                } catch (PersistentDataOperationException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     * @Description:保存维护服务包所属项目
     * @author ChenXianRui 2017-11-10 下午2:30:03
     * @Modify:
     */
    @SuppressWarnings("unchecked")
    public void doSaveHisServiceItem(Map<String, Object> req,
                                     Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        List<Map<String, Object>> reqList = (List<Map<String, Object>>) req
                .get("body");
        if (reqList != null && reqList.size() > 0) {
            for (Map<String, Object> map : reqList) {
                try {
                    if ("1".equals(map.get("isNew") + "")) {
                        map.remove("isNew");
                        dao.doSave("create", SCM_ServiceItems, map, false);
                    } else {
                        map.remove("isNew");
                        dao.doSave("update", SCM_ServiceItems, map,
                                false);

                    }
                } catch (PersistentDataOperationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void doRemovePackagetoServiceItem(Map<String, Object> req,
                                             Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        // List<Map<String, Object>> reqList = (List<Map<String, Object>>)
        // req.get("body");
        Map<String, Object> body = (Map<String, Object>) req.get("body");
        if (body != null && body.containsKey("SPIID"))
            try {
                dao.doRemove(body.get("SPIID").toString(),
                        SCM_ServicePackageItems);
            } catch (PersistentDataOperationException e) {
                e.printStackTrace();
            }
    }

    public void doUpdateServiceTimes(Map<String, Object> req,
                                     Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        List<Map<String, Object>> reqList = (List<Map<String, Object>>) req
                .get("body");
        if (reqList != null && reqList.size() > 0) {
            for (Map<String, Object> map : reqList) {
                try {
                    dao.doSave("update", SCM_ServicePackageItems, map, false);
                } catch (PersistentDataOperationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void doLogOffPackage(Map<String, Object> req,
                                Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        Map<String, Object> body = (Map<String, Object>) req.get("body");
        String SPID = body.get("SPID").toString();
        int LOGOFF = Integer.parseInt("0".equals(body.get("LOGOFF").toString()) ? "1" : "0");
        String hql = new StringBuffer("update ").append(SCM_ServicePackage)
                .append(" set LOGOFF =:LOGOFF ").append("where SPID =:SPID")
                .toString();
        Map<String, Object> par = new HashMap<String, Object>();
        par.put("LOGOFF", LOGOFF);
        par.put("SPID", SPID);

        try {
            dao.doUpdate(hql, par);
        } catch (PersistentDataOperationException e) {
            logger.error("签约包注销操作失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "签约包注销操作失败！", e);
        }

    }

    /**
     * 注销选中的项目，防止删除引起的显示错误
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    public void doLogOffPackagetoServiceItem(Map<String, Object> req,
                                             Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        Map<String, Object> body = (Map<String, Object>) req.get("body");
        String SPIID = body.get("SPIID").toString();
        String hql = new StringBuffer("update ")
                .append(SCM_ServicePackageItems)
                .append(" set LOGOFF =:LOGOFF ").append("where SPIID =:SPIID")
                .toString();
        Map<String, Object> par = new HashMap<String, Object>();
        par.put("LOGOFF", 1);
        par.put("SPIID", SPIID);

        try {
            dao.doUpdate(hql, par);
        } catch (PersistentDataOperationException e) {
            logger.error("签约项目注销操作失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "签约项目注销操作失败！", e);
        }
    }

    /**
     * 启用选中的注销项目，防止删除引起的显示错误
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    public void doLogOnPackagetoServiceItem(Map<String, Object> req,
                                            Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        Map<String, Object> body = (Map<String, Object>) req.get("body");
        String SPIID = body.get("SPIID").toString();
        String hql = new StringBuffer("update ")
                .append(SCM_ServicePackageItems)
                .append(" set LOGOFF =:LOGOFF ").append("where SPIID =:SPIID")
                .toString();
        Map<String, Object> par = new HashMap<String, Object>();
        par.put("LOGOFF", 0);
        par.put("SPIID", SPIID);

        try {
            dao.doUpdate(hql, par);
        } catch (PersistentDataOperationException e) {
            logger.error("签约项目启用操作失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "签约项目启用操作失败！", e);
        }
    }

    /**
     * 查询该病人是否存在有效签约记录
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    public void doCheckPersonnalContractRecord(Map<String, Object> req,
                                               Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        String empiId = (String) req.get("empiId");
        Date date = new Date();
        String hql = new StringBuffer("select count(favoreeEmpiId) from ")
                .append(SCM_SignContractRecord)
                .append(" where endDate>=:curDate and favoreeEmpiId=:empiId and signFlag='1' ")
                .toString();
        Map<String, Object> par = new HashMap<String, Object>();
        par.put("empiId", empiId);
        par.put("curDate", Calendar.getInstance().getTime());
        try {
            List<Map<String, Object>> list = dao.doQuery(hql, par);
            res.put("body", list);
        } catch (PersistentDataOperationException e) {
            logger.error("签约项目启用操作失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "签约项目启用操作失败！", e);
        }
    }

    /**
     * 查询家庭成员列表
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    public void doQueryfamilyPeopleList(Map<String, Object> req,
                                        Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException {
        String empiId = (String) req.get("empiId");
        Map<String, Object> map = new HashMap<String, Object>();
        List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
        try {
            map = dao.doLoad(cnd, EHR_HealthRecord);
        } catch (PersistentDataOperationException e) {
            logger.error("查询基本信息失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "查询基本信息失败！", e);
        }
        String familyId = "";
        if (map != null && !map.isEmpty()) {
            familyId = (String) map.get("familyId");
            res.put("familyId", familyId);
        }
        String hql = new StringBuffer("select a.empiId ,a.personName ,a.idCard , ")
                .append(" a.sexCode , a.birthday ,a.mobileNumber  from ")
                .append("MPI_DemographicInfo  a, ")
                .append("EHR_HealthRecord   b ")
                .append(" where a.empiid = b.empiid and b.status  ='0' and b.familyId=:familyId")
                .toString();
        Map<String, Object> par = new HashMap<String, Object>();
        par.put("familyId", familyId);
        List<Map<String, Object>> list = null;
        try {
            list = dao.doSqlQuery(hql, par);
        } catch (PersistentDataOperationException e) {
            logger.error("获取家庭成员失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "获取家庭成员失败！", e);
        }
        if (list != null && !list.isEmpty()) {
            res.put("body", list);
        }
    }

    /**
     * 查看签约记录是否已经完成主表业务记录
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    public void doCheckSignContractService(Map<String, Object> req,
                                           Map<String, Object> res, BaseDAO dao, Context ctx) {

        String entryNames = req.get("entryNames") + "";
        String empiId = req.get("empiId") + "";
        String[] entryArray = entryNames.split(",");
        String entryName, hql;
        Map<String, Object> par = new HashMap<String, Object>();
        for (int i = 0; i < entryArray.length; i++) {
            entryName = entryArray[i];
            try {
                hql = new StringBuffer("select count(*) as totalCount from " + entryName + " where empiId=:empiId").toString();
                par.put("empiId", empiId);
                Map<String, Object> total = dao.doLoad(hql, par);
                if (Integer.parseInt(total.get("totalCount") + "") == 0) {
                    Schema sc = SchemaController.instance().get(entryName);
                    String tableAlias = sc.getAlias();
                    res.put("tableAlias", tableAlias);
                    break;
                }
            } catch (PersistentDataOperationException e) {
                e.printStackTrace();
            } catch (ControllerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询已签约的包
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    public void doQueryTask(Map<String, Object> req,
                            Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        Map<String, Object> par = new HashMap<String, Object>();
        List<Map<String, Object>> list = null;
        // par.put("taskId", req.get("taskId"));
        String sql = "SELECT distinct SPID AS SPID from SCM_ServiceContractPlanTask";
        try {
            list = dao.doQuery(sql, par);
        } catch (PersistentDataOperationException e) {
            logger.error("获取签约包spid失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "获取签约包spid失败！", e);
        }
        if (list != null && !list.isEmpty()) {
            res.put("body", list);
        }
    }

    /**
     * 查询该病人是否存在有效签约记录
     *
     * @param empiId
     * @param dao
     * @return
     * @throws ServiceException
     */
    public static boolean doHavePersonnalContractRecord(String empiId, BaseDAO dao) throws ServiceException {
        Date date = new Date();
        String hqlWhere = " endDate>=:curDate and favoreeEmpiId=:empiId and signFlag='1'";
        Map<String, Object> par = new HashMap<String, Object>();
        par.put("empiId", empiId);
        par.put("curDate", Calendar.getInstance().getTime());
        long num;
        try {
            num = dao.doCount(SCM_SignContractRecord, hqlWhere, par);
        } catch (PersistentDataOperationException e) {
            logger.error("签约项目启用操作失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "签约项目启用操作失败！", e);
        }
        if (num > 0) {
            return true;
        }
        return false;
    }

    /**
     * 查询该病人存在有效签约的最大beginDate  endDate
     *
     * @param empiId
     * @param dao
     * @return
     */
    public static Map<String, Object> doGetMaxBeginDateandEndDate(String empiId, BaseDAO dao) {
        Date date = new Date();
        String hql = new StringBuffer("select MAX(beginDate) as beginDate,MAX(endDate)as endDate  from ")
                .append(SCM_SignContractRecord)
                .append(" where endDate>=:curDate and favoreeEmpiId=:empiId and signFlag='1' ")
                .toString();
        Map<String, Object> par = new HashMap<String, Object>();
        par.put("empiId", empiId);
        par.put("curDate", Calendar.getInstance().getTime());
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            map = dao.doLoad(hql, par);
        } catch (PersistentDataOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 查询该病人是否存在（用来判断是否可以更新）
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    public void doCheckContractRecord(Map<String, Object> req,
                                      Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        String SPIID = req.get("SPIID").toString();
        Date date = new Date();
        String hql = new StringBuffer("select count(SPIID) from ")
                .append(SCM_ServicePackageItems)
                .append(" where  SPIID=:SPIID")
                .toString();
        Map<String, Object> par = new HashMap<String, Object>();
        par.put("SPIID", SPIID);
        try {
            List<Map<String, Object>> list = dao.doQuery(hql, par);
            res.put("body", list);
        } catch (PersistentDataOperationException e) {
            logger.error("查询签约项目失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "查询签约项目失败！", e);
        }
    }

    /**
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    public void doCheackHealthReaordStatus(Map<String, Object> req,
                                           Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        String empiId = (String) req.get("empiId");
        String taskCode = (String) req.get("taskCode");
        String year = String.valueOf(req.get("year"));
        String hql = "select status as status from " + SCM_ServiceContractPlanTask + " where empiId='" + empiId + "' and taskCode = '" + taskCode + "' and year = '" + year + "'";
        Map<String, Object> par = new HashMap<String, Object>();
        try {
            Map<String, Object> map = dao.doLoad(hql, par);
            res.put("body", map);
        } catch (PersistentDataOperationException e) {
            logger.error("查询个人健康档案状态失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "查询个人健康档案状态失败！", e);
        }
    }

    /**
     * 获取医疗的收费项目
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     */
    public void doGetHisItems(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) throws PersistentDataOperationException {

        SignContractRecordModule signContractRecordModule = new SignContractRecordModule(dao);
        signContractRecordModule.doGetHisItems(req, res, dao, ctx);

    }

    public void doSaveSignContractToHis(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException {
        Map<String, Object> saveData = new HashMap<String, Object>();
        List<Map<String, Object>> gpList = (List<Map<String, Object>>) req.get("listData");
        List<Map<String, Object>> planList = (List<Map<String, Object>>) req.get("planList");
        Map<String , Object> formData = (Map<String, Object>) req.get("formData");
        Double totalCost = 0d;
        /*        for (Map<String, Object> gpItem : gpList) {
        if (gpItem.containsKey("realPrice") && gpItem.containsKey("serviceTimes") && null != gpItem
                .get("realPrice") && null != gpItem.get("serviceTimes")) {
            totalCost += Double.parseDouble(String.valueOf(gpItem.get("realPrice"))) * Integer.parseInt(String.valueOf(gpItem.get("serviceTimes")));
        }
    }*/
    List<Map<String, Object>> gpYjList = new ArrayList<Map<String, Object>>();
    //按签约包分组合计
    for (Map<String, Object> gpItem : gpList) {
    	//签约入口为门诊医生站时需要判定服务包对应收费项目是否已维护
        if(formData.get("loginSystem").toString().equals("phis") && gpItem.get("SFXM")==null){
        	throw new ServiceException(Constants.CODE_SERVICE_ERROR, "门诊医生站签约时，服务包【"+gpItem.get("packageName")+"】的收费项目必须维护！");
        }
        if(!(gpItem.get("isUsePrice")+"").equals("1")){
            if(gpItem.get("realPrice")==null){
            	throw new ServiceException(Constants.CODE_SERVICE_ERROR, "签约非固定价格的服务包【"+gpItem.get("packageName")+"】时，服务项【"+gpItem.get("itemName")+"】的服务项单价未维护，请排查本服务包所有服务项是否均已维护！");
            }
/*            else if(gpItem.get("serviceTimes")==null){
            	throw new ServiceException(Constants.CODE_SERVICE_ERROR, "签约非固定价格的服务包【"+gpItem.get("packageName")+"】时，服务项【"+gpItem.get("itemName")+"】的服务次数未维护，请排查本服务包所有服务项是否均已维护！");
            }*/
        }
        if(gpYjList.size()==0){
            Map<String, Object> tempItem = new HashMap<String, Object>();
            tempItem.put("SPID",gpItem.get("SPID"));
            tempItem.put("SFXM",gpItem.get("SFXM"));
            if((gpItem.get("isUsePrice")+"").equals("1")){
                if(gpItem.get("spRealPrice")==null){
                	throw new ServiceException(Constants.CODE_SERVICE_ERROR, "签约固定价格的服务包【"+gpItem.get("packageName")+"】时，服务包的实际价格未维护！");
                }
                tempItem.put("totalCost",gpItem.get("spRealPrice"));
            }
            else{
                Double tCost = Double.parseDouble(String.valueOf(gpItem.get("realPrice"))) * Integer.parseInt(String.valueOf(gpItem.get("SERVICETIMES")==null?0:gpItem.get("SERVICETIMES")));
                tempItem.put("totalCost",tCost);
            }
            gpYjList.add(tempItem);
            continue;
        }
        boolean isexist = false;
        for (Map<String, Object> gpYjItem : gpYjList) {
            if(gpYjItem.get("SPID").equals(gpItem.get("SPID"))){
                //使用签约包固定价格
                if((gpItem.get("isUsePrice")+"").equals("1")){
                    gpYjItem.put("totalCost",gpItem.get("spRealPrice"));
                }
                else{
                    Double tCost = Double.parseDouble(String.valueOf(gpYjItem.get("totalCost"))) + Double.parseDouble(String.valueOf(gpItem.get("realPrice"))) * Integer.parseInt(String.valueOf(gpItem.get("SERVICETIMES")==null?0:gpItem.get("SERVICETIMES")));
                    gpYjItem.put("totalCost",tCost);
                }
                isexist=true;
            }
        }
        if(!isexist){
            Map<String, Object> tempItem = new HashMap<String, Object>();
            tempItem.put("SPID",gpItem.get("SPID"));
            tempItem.put("SFXM",gpItem.get("SFXM"));
            if((gpItem.get("isUsePrice")+"").equals("1")){
                tempItem.put("totalCost",gpItem.get("spRealPrice"));
            }
            else{
                Double tCost = Double.parseDouble(String.valueOf(gpItem.get("realPrice"))) * Integer.parseInt(String.valueOf(gpItem.get("SERVICETIMES")==null?0:gpItem.get("SERVICETIMES")));
                tempItem.put("totalCost",tCost);
            }
            gpYjList.add(tempItem);
        }
/*            if (gpItem.containsKey("realPrice") && gpItem.containsKey("serviceTimes") && null != gpItem
                .get("realPrice") && null != gpItem.get("serviceTimes")) {
            totalCost += Double.parseDouble(String.valueOf(gpItem.get("realPrice"))) * Integer.parseInt(String.valueOf(gpItem.get("serviceTimes")));
        }*/
    }

        try {
/*            Map<String , Object> parms = new HashMap<String, Object>();
            parms.put("empiid", formData.get("favoreeEmpiId"));
            parms.put("now" , new Date());
            String sql1 = "select SCID as SCID from SCM_SIGNCONTRACTRECORD where BEGINDATE <= :now and ENDDATE >= :now and FAVOREEEMPIID =:empiid";
            List<Map<String , Object>> scids = dao.doSqlQuery(sql1,parms);
            String scid = scids.get(0).get("SCID").toString();
            formData.put("scid",scid);*/

            saveData.put("totalCost", totalCost);
            saveData.put("manaUnit", UserRoleToken.getCurrent().getManageUnit().getId());
            saveData.put("listData", gpList);
            saveData.put("formData", formData);
            saveData.put("result",req.get("result"));
            saveData.put("yjlistData", gpYjList);
            saveData.put("planList", planList);
            try{
            	ServiceAdapter.invoke("phis.gpService", "doSaveGpService", saveData);
            }catch (Exception e) {
                throw new ServiceException(Constants.CODE_SERVICE_ERROR, e.getMessage());
            }
        } catch (Exception e) {
            throw new ServiceException(Constants.CODE_SERVICE_ERROR, e.getMessage());
        }
    }

    public void doCheckEhrAGh(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) {
        Map<String , Object> body = (Map<String, Object>) req.get("body");
        int result = 9;
        SignContractRecordModule signContractRecordModule = new SignContractRecordModule(dao);
        try {
            result = signContractRecordModule.doCheckEhrAGh(body);
        } catch (PersistentDataOperationException e) {
            e.printStackTrace();
        }
        res.put("body" , result);
    }


    /**
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     * @Description:解除签约，更新签约计划 和 工作任务
     * @author guoliang
     * @Modify:
     */
    @SuppressWarnings("unchecked")
    @RpcService
    public void doStopSignStatus(String scid)
            throws ServiceException {

        Context ctx = ContextUtils.getContext();
        Session ss = (Session) ctx.get(Context.DB_SESSION);
        if (ss == null) {
            SessionFactory sf = AppContextHolder.getBean(
                    AppContextHolder.DEFAULT_SESSION_FACTORY,
                    SessionFactory.class);
            ss = sf.openSession();
            ctx.put(Context.DB_SESSION, ss);
        }
        BaseDAO dao = new BaseDAO(ctx , ss);


        Map<String, Object> rMap = null;
        try {
            ss.beginTransaction();
            Map<String, Object> reqBodyMap = dao.doLoad(SCM_SignContractRecord , scid);
            String empiId = (String) reqBodyMap.get("favoreeEmpiId");
            String stopReason = (String) reqBodyMap.get("stopReason");
            reqBodyMap.put("signFlag", "2");// 解约
            reqBodyMap.put("stopDate", new Date());
            rMap = dao.doSave("update", SCM_SignContractRecord, reqBodyMap,
                    false);
            // 修改个人基本中的是否签约标识
            String upHQL = new StringBuffer("update ")
                    .append(MPI_DemographicInfo)
//                    .append(" set signFlag=:singFlag,sceBeginDate=:beginDate,sceEndDate=:endDate")
                    .append(" set signFlag=:singFlag")
                    .append(" where empiId=:empiId").toString();
            Map<String, Object> pMap = new HashMap<String, Object>();
            pMap.put("singFlag", "2");
//            pMap.put("beginDate", null);
//            pMap.put("endDate", null);
            pMap.put("empiId", empiId);
            dao.doUpdate(upHQL, pMap);


            String hql_u = "update MS_BRDA set ISGP = '0' where empiId =:empiId";
            Map<String,Object> parm_u = new HashMap<String, Object>();
            parm_u.put("empiId",empiId);
            dao.doUpdate(hql_u , parm_u);



            // 更新签约计划为解约
            SignContractRecordModule scrm = new SignContractRecordModule(dao);
            scrm.stopContractPlanTaskStatus(empiId);
            ss.beginTransaction().commit();
            System.out.println("rpc 调用成功");
        } catch (ServiceException e) {
            ss.getTransaction().rollback();
            logger.error("解约失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR, "解约失败！",
                    e);
        } catch (PersistentDataOperationException e) {
            e.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }

    /**
     * 加载签约履约记录
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    public void doLoadServiceRecord(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
        Map<String, Object> par = new HashMap<String, Object>();
        List<Map<String, Object>> list = null;
        par.put("taskid", reqBodyMap.get("taskId"));
        String sql = "SELECT serviceId as serviceId,taskId as taskId,empiId as empiId,serviceObj as serviceObj,serviceOrg as serviceOrg," +
        		"b.organizname as serviceOrgName,serviceTeam as serviceTeam,c.organizname as serviceTeamName,servicer as servicer," +
        		"d.name as servicerName,servicePackId as servicePackId,servicePack as servicePack,serviceItemsId as serviceItemsId," +
        		"serviceItems as serviceItems,decode(serviceMode,1,'电话服务',2,'家庭服务','门诊服务') as serviceModeName,serviceMode as serviceMode," +
        		"serviceDate as serviceDate,e.regionname as gridAddressName,gridAddress as gridAddress,detailedAddress as detailedAddress," +
        		"case when deleted=1 then '(已删除)'||serviceDesc else serviceDesc end as serviceDesc,a.SCID as SCID,a.SPIID as SPIID," +
        		"to_char(a.createTime,'yyyy-mm-dd hh24:mi:ss') as createTime,f.name as createUserName,a.dataSource as dataSource,f.PRICE as price, f.REALPRICE as realPrice " + 
        		"from SCM_NEWSERVICE a left join SCM_SERVICEITEMS f on f.ITEMCODE = a.serviceitemsid left join EHR_AREAGRID e on a.gridaddress = e.regioncode,SYS_ORGANIZATION b,SYS_ORGANIZATION c,BASE_USER d,BASE_USER f " +
        		"where taskid=:taskid and a.serviceorg=b.organizcode and a.serviceteam=c.organizcode and a.servicer=d.id and a.createUser=f.id " +
        		"order by a.serviceDate desc,a.createTime desc";
        try {
            list = dao.doSqlQuery(sql, par);
            //SchemaUtil.setDictionaryMassageForList(list,"chis.application.scm.schemas.SCM_NewService");
			for (Map<String, Object> item : list) {
				Map<String, Object> rec = new HashMap<String, Object>();
				rec.put("serviceId", Long.parseLong(item.get("SERVICEID")+""));
				rec.put("taskId", item.get("TASKID"));
				rec.put("empiId",item.get("EMPIID"));
				rec.put("serviceObj",item.get("SERVICEOBJ"));
				rec.put("serviceOrg",item.get("SERVICEORG"));
				rec.put("serviceOrgName",item.get("SERVICEORGNAME"));
				rec.put("serviceTeam",item.get("SERVICETEAM"));
				rec.put("serviceTeamName",item.get("SERVICETEAMNAME"));
				rec.put("servicer",item.get("SERVICER"));
				rec.put("servicerName",item.get("SERVICERNAME"));
				rec.put("servicePackId",item.get("SERVICEPACKID"));
				rec.put("servicePack",item.get("SERVICEPACK"));
				rec.put("serviceItemsId",item.get("SERVICEITEMSID"));
				rec.put("serviceItems",item.get("SERVICEITEMS"));
				rec.put("serviceMode",item.get("SERVICEMODE"));
				rec.put("serviceModeName",item.get("SERVICEMODENAME"));
				rec.put("serviceDate",item.get("SERVICEDATE"));
				rec.put("gridAddress",item.get("GRIDADDRESS"));
				rec.put("gridAddressName",item.get("GRIDADDRESSNAME"));
				rec.put("detailedAddress",item.get("DETAILEDADDRESS"));							
				//签约用户减免1元，暂时写死 xiaheng 2020-05-06
				rec.put("diffPrice", "1.00");						
				rec.put("serviceDesc",item.get("SERVICEDESC"));
				rec.put("dataSource",item.get("DATASOURCE"));
				rec.put("SCID",item.get("SCID")==null?item.get("SCID"):Long.parseLong(item.get("SCID")+""));
				rec.put("SPIID",item.get("SPIID")==null?item.get("SPIID"):Long.parseLong(item.get("SPIID")+""));
				rec.put("createTime",item.get("CREATETIME"));
				rec.put("createUserName",item.get("CREATEUSERNAME"));
	            records.add(rec);
	        }
        } catch (PersistentDataOperationException e) {
            logger.error("获取签约包spid失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "获取签约包spid失败！", e);
        }
        if (records != null && !records.isEmpty()) {
            res.put("body", records);
        }
    }

    /**
     * 获取签约医生所属团队 2019-08-08 zhaojian 
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    public void doGetCreateUnit(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        Map<String, Object> par = new HashMap<String, Object>();
        par.put("USERID", UserRoleToken.getCurrent().getUserId());
        String sql = "select b.ORGANIZCODE as ORGANIZCODE,b.ORGANIZNAME as ORGANIZNAME " +
        		"from BASE_USERROLES a,SYS_Organization b " +
        		"where a.MANAGEUNITID=b.ORGANIZCODE and b.CLASSIFYCODE=9 and a.LOGOFF=0 and a.USERID=:USERID " +
        		"order by b.ORGANIZCODE";
        try {
            List<Map<String, Object>> list = dao.doSqlQuery(sql, par);
            res.put("body", list);
        } catch (PersistentDataOperationException e) {
            logger.error("获取签约医生所属团队失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "获取签约医生所属团队失败！", e);
        }
    }

    /**
     * 获取已签约记录
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    public void doLoadSignedRecord(Map<String, Object> req,
                                             Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
        Map<String, Object> par = new HashMap<String, Object>();
        List<Map<String, Object>> list = null;
        par.put("empiid", reqBodyMap.get("empiid"));
        String sql = "select b.organizname as organizname,a.createdate as createdate,c.name as name,a.enddate-30 as enddate,sysdate as  sdate from scm_signcontractrecord a,sys_organization b,base_user c  " +
        		"where a.createunit=b.organizcode and a.createuser=c.id and a.signflag=1 and to_char(a.enddate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') " +
        		"and a.favoreeempiid=:empiid group by a.createunit,b.organizname,a.createdate,c.name,a.enddate-30 order by a.createdate desc";
        logger.info("调试："+sql);
        try {
            list = dao.doSqlQuery(sql, par);
        } catch (PersistentDataOperationException e) {
            logger.error("获取已签约记录失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "获取已签约记录失败！", e);
        }
        if (list != null && !list.isEmpty()) {
            res.put("body", list);
        }
    }

    /**
     * 解约所有签约记录
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    public void doCancelAllSignRecord(Map<String, Object> req,
                                             Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
        Map<String, Object> par = new HashMap<String, Object>();
        par.put("empiid", reqBodyMap.get("empiid"));
        String sql = "update scm_signcontractrecord set signflag=2,stopDate=sysdate,stopReason='03' where signflag=1 and to_char(enddate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') and favoreeempiid=:empiid";
        try {
            dao.doSqlUpdate(sql, par);
        } catch (PersistentDataOperationException e) {
            logger.error("解约所有签约记录失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "解约所有签约记录失败，请联系管理员！"+e.getMessage(), e);
        }
    }
	
	  /**
     * 获取居民信息
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
  	@SuppressWarnings("unchecked")
  	public void doGetjmxx(Map<String, Object> req,
  			Map<String, Object> res, BaseDAO dao, Context ctx)
  			throws ServiceException {
  		Map<String, Object> body = (Map<String, Object>) req.get("body");
  		String empiid=(String) body.get("empiid");
  		String sql="select to_char(a.brid) as BRID,a.sfzh as idCard,a.MZHM as MZHM from ms_brda a where a.empiid='"+empiid+"'";
		Map<String, Object> result = new LinkedHashMap<String, Object>();
  		try {
			result = dao.doSqlLoad(sql, null);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		res.put("jmxx", result);
  	}
  	
    /**
     * 获取已签约的服务包
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    public void doGetSigPackage(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException {
        Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
        Map<String, Object> par = new HashMap<String, Object>();
        Map<String, Object> packagename = new HashMap<String, Object>();
        List<Map<String, Object>> list = null;
//        List<Map<String, Object>> packages = null;
        par.put("empiid", reqBodyMap.get("empiid"));
        String sql = "select signservicepackages as SIGNSERVICEPACKAGES from (select signservicepackages,enddate from scm_signcontractrecord  " +
        		"where favoreeempiid=:empiid and (signflag='1' OR signflag='3') " +
        		"order by enddate desc ) where rownum=1  and   enddate-30>=sysdate ";
        try {
            list = dao.doSqlQuery(sql, par);
            if(list.size()>0){
            String spids = (String) list.get(0).get("SIGNSERVICEPACKAGES");
            if(spids!=null){
            String [] strArr= spids.split(",");
            for(String spid : strArr){
            	Object remark = doGetRemark(spid,dao,ctx);
            	if(remark.equals("基本服务包")||remark.equals("基本服务包（不含基本医疗服务）")||remark.equals("基本服务包(减免)")){
            		packagename.put("SIGNSERVICEPACKAGES", remark);
            		break;
            	}else{
            		packagename.put("SIGNSERVICEPACKAGES", "个性包");
            	}
            }
            }
           }
        } catch (PersistentDataOperationException e) {
            logger.error("获取已签约服务包记录失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "获取已签约服务包记录失败！", e);
        }
        if (packagename != null && !packagename.isEmpty()) {
            res.put("body", packagename);
        }else{
        	res.put("body", packagename);
        }
    }
    
    /**
     * 获取已签约的服务包(保存时判断)
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    public void doGetSigPack(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException {
        Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
        Map<String, Object> par = new HashMap<String, Object>();
        Map<String, Object> packagename = new HashMap<String, Object>();
        List<Map<String, Object>> list = null;
//        List<Map<String, Object>> packages = null;
        par.put("empiid", reqBodyMap.get("empiid"));
        String sql = "select signservicepackages as SIGNSERVICEPACKAGES from (select signservicepackages,enddate from scm_signcontractrecord  " +
        		"where favoreeempiid=:empiid and (signflag='1' OR signflag='3') and enddate-30>=sysdate " +
        		"order by enddate desc ) ";
        try {
            list = dao.doSqlQuery(sql, par);
            if(list.size()>0){
            	a:for(int i = 0;i < list.size();i++){
            	String spids = (String) list.get(i).get("SIGNSERVICEPACKAGES");
                if(spids!=null){
                String [] strArr= spids.split(",");
                b:for(String spid : strArr){
                	Object remark = doGetRemark(spid,dao,ctx);
                	if(remark.equals("基本服务包")||remark.equals("基本服务包（不含基本医疗服务）")||remark.equals("基本服务包(减免)")){
                		packagename.put("SIGNSERVICEPACKAGES", remark);
                		break a;
                	}else{
                		packagename.put("SIGNSERVICEPACKAGES", "个性包");
                	}
                }
                }
            }
            
           }
        } catch (PersistentDataOperationException e) {
            logger.error("获取已签约服务包记录失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "获取已签约服务包记录失败！", e);
        }
        if (packagename != null && !packagename.isEmpty()) {
            res.put("body", packagename);
        }else{
        	res.put("body", packagename);
        }
    }    
    
    public Object doGetRemark(String spid,BaseDAO dao, Context ctx) throws ServiceException {
    	Object packagename = null;
        List<Map<String, Object>> list = null;
        Map<String, Object> par = new HashMap<String, Object>();
        par.put("spid", spid);
        String sql = "select packagename as PACKAGENAME,remark as REMARK from scm_servicepackage where spid=:spid";
        try {
            list = dao.doSqlQuery(sql, par);
            packagename = list.get(0).get("PACKAGENAME");
        } catch (PersistentDataOperationException e) {
            logger.error("获取已签约服务包记录失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "获取已签约服务包记录失败！", e);
        }
		return packagename; 
    }
}
