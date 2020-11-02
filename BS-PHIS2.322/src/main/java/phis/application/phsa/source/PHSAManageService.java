package phis.application.phsa.source;

import java.util.Map;

import javax.persistence.MappedSuperclass;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import phis.application.reg.source.RegisteredManagementModel;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

/**
 * 全院查询Service
 * @author Administrator
 *
 */
public class PHSAManageService extends AbstractActionService implements
		DAOSupportable {
	/**
	 * 查询首页信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryHomeInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SessionFactory sf = (SessionFactory) AppContextHolder.get()
				.getBean("phsaSessionFactory");
		Session ss = null;
		try{
			ss = sf.openSession();
			BaseDAO phsaDao = new BaseDAO(ctx, ss);
			PHSAManageModule module = new PHSAManageModule(dao);
			module.doQueryHomeInfo(req, res, phsaDao, ctx);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(ss != null){
				ss.close();
			}
		}
	}
	/**
	 * 总收入数据明细
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doZSRDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SessionFactory sf = (SessionFactory) AppContextHolder.get()
				.getBean("phsaSessionFactory");
		Session ss = null;
		try{
			ss = sf.openSession();
			BaseDAO phsaDao = new BaseDAO(ctx, ss);
			PHSAManageModule module = new PHSAManageModule(dao);
			module.doZSRDetails(req, res, phsaDao, ctx);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(ss != null){
				ss.close();
			}
		}
	}
	/**
	 * 数据明细
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doDataDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SessionFactory sf = (SessionFactory) AppContextHolder.get()
				.getBean("phsaSessionFactory");
		Session ss = null;
		try{
			ss = sf.openSession();
			BaseDAO phsaDao = new BaseDAO(ctx, ss);
			PHSAManageModule module = new PHSAManageModule(dao);
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			//a_JCFY  均次费用   a_DCFS  大处方数  a_MZRC 门诊人次  a_RYRS 入院人数    a_CYRS  出院人数   a_ZYRS  在院人数  a_WZRS  危重人数
			String type = String.valueOf(body.get("TYPE"));
			if("a_JCFY".equals(type)){
				module.doJCFYDetails(req, res, phsaDao, ctx);
			}else if("a_DCFS".equals(type)){
				module.doDCFSDetails(req, res, phsaDao, ctx);
			}else if("a_MZRC".equals(type)){
				module.doMZRCDetails(req, res, phsaDao, ctx);
			}else if("a_RYRS".equals(type)){
				module.doRYRSDetails(req, res, phsaDao, ctx);
			}else if("a_CYRS".equals(type)){
				module.doCYRSDetails(req, res, phsaDao, ctx);
			}else if("a_ZYRS".equals(type)){
				module.doZYRSDetails(req, res, phsaDao, ctx);
			}else if("a_WZRS".equals(type)){
				module.doWZRSDetails(req, res, phsaDao, ctx);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(ss != null){
				ss.close();
			}
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
	public void doQueryPerson(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		PHSAManageModule ccpm = new PHSAManageModule(dao);
		try {
			ccpm.doQueryPerson(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	/**
	 * 查询传染病
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryCRBDetails(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		PHSAManageModule ccpm = new PHSAManageModule(dao);
		try {
			ccpm.queryCRBDetails(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
}
