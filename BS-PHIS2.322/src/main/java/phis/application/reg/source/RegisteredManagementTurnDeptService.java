package phis.application.reg.source;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.BSHISUtil;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class RegisteredManagementTurnDeptService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(RegisteredManagementTurnDeptService.class);

	/**
	 * 获取系统参数
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetParameter(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx)throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
//		String dqghrq = ParameterUtil.getParameter(jgid, BSPHISSystemArgument.DQGHRQ, ctx);
		String dqzblb = ParameterUtil.getParameter(jgid, BSPHISSystemArgument.DQZBLB, ctx);
		//与杨力确认dqghrq为当前日期的星期几
		res.put("dqghrq", BSHISUtil.getWeekOfDate(new Date()));
		res.put("dqzblb", dqzblb);
		res.put(Service.RES_CODE, 200);
	}
	/**
	 * 根据卡号查询可以转科的挂号单
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryTurnDept(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx)throws ServiceException {
		RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
		try {
			rmm.doQueryTurnDept(req,res,ctx);
			res.put(Service.RES_CODE, 200);
		} catch (ModelDataOperationException e) {
			res.put(Service.RES_CODE, 9001);
			res.put(Service.RES_MESSAGE, "根据卡号查询可以转科的挂号单失败！");
			throw new ServiceException("根据卡号查询可以转科的挂号单失败！",e);
		}
	}
	/**
	 * 查询科室收费
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doCheckKSFY(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx)throws ServiceException {
		RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
		try {
			rmm.doCheckKSFY(req,res,ctx);
			res.put(Service.RES_CODE, 200);
		} catch (ModelDataOperationException e) {
			res.put(Service.RES_CODE, 9001);
			res.put(Service.RES_MESSAGE, "查询科室收费失败！");
			throw new ServiceException("查询科室收费失败！",e);
		}
	}
	/**
	 * 转科确认
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doCommitTurnDept(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx)throws ServiceException {
		RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
		try {
			rmm.doCommitTurnDept(req,res,ctx);
			res.put(Service.RES_CODE, 200);
		} catch (ModelDataOperationException e) {
			res.put(Service.RES_CODE, 9001);
			res.put(Service.RES_MESSAGE, "转科确认失败！");
			throw new ServiceException("转科确认失败！",e);
		}
	}
	/**
	 * 添加需要事务控制的方法
	 * @return
	 */
	public List<String> getTransactedActions() {
		 List<String> list = new ArrayList<String>();
		 list.add("commitTurnDept");
		 return list ;
	}
}
