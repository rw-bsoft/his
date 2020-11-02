/**

 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.reg.source;

import java.text.ParseException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description 医生排班维护
 * 
 * @author shiwy 2012.08.28
 */
public class RegistrationDoctorPlanService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory.getLogger(RegistrationDoctorPlanService.class);

	/**
	 * 医生排班保存 *@param req
	 * 
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	public void doSaveRegistrationDoctorPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		RegistrationDoctorPlanModule rdpm = new RegistrationDoctorPlanModule(dao);
		rdpm.doSaveRegistrationDoctorPlan(req, res, dao, ctx);
	}

	/**
	 * 医生排班删除
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveRegistrationDoctorPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		RegistrationDoctorPlanModule rdpm = new RegistrationDoctorPlanModule(dao);
		rdpm.doRemoveRegistrationDoctorPlan(body, res, dao, ctx);
	}
	/**
	 * 医生排班复制
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ParseException 
	 * @throws ValidateException 
	 */
	public void doSave_copyYSPB(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ParseException, ValidateException {
		RegistrationDoctorPlanModule rdpm = new RegistrationDoctorPlanModule(dao);
		rdpm.doSave_copyYSPB(req, res, dao, ctx);
	}
	/**
	 * 挂号分类统计
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ParseException
	 * @throws ValidateException
	 */
	public void doRegCount(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ParseException, ValidateException {
		RegistrationDoctorPlanModule rdpm = new RegistrationDoctorPlanModule(dao);
		rdpm.doRegCount(req, res, ctx);
	}
}
