/**

 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.cic.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.util.context.Context;

/**
 * @description 处方组套维护
 * 
 * @author zhangyq 2012.05.28
 */
public class ClinicComboService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory.getLogger(ClinicComboService.class);

	/**
	 * 处方组套明细保存 *@param req
	 * 
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	public void doSavePrescriptionDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		ClinicComboModule ccm = new ClinicComboModule(dao);
		ccm.doSavePrescriptionDetails(req, res, dao, ctx);
	}

	/**
	 * 处方组套明细删除
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doRemovePrescriptionDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		int pkey = (Integer)req.get("pkey");
		String schemaList = req.get("schemaList") + "";
		String schemaDetailsList = req.get("schemaDetailsList") + "";
		ClinicComboModule ccm = new ClinicComboModule(dao);
		ccm.doRemovePrescriptionDetails(body, pkey,schemaList,schemaDetailsList, res, dao, ctx);
	}

	/**
	 * 处方组套启用
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doUpdatePrescriptionStack(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String schemaList = req.get("schemaList") + "";
		String schemaDetailsList = req.get("schemaDetailsList") + "";
		ClinicComboModule ccm = new ClinicComboModule(dao);
		ccm.savePersonalComboExecute(body,schemaList, schemaDetailsList, res,
				dao, ctx);
	}
	
	/**
	 * 病历模板启用
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doMedicalTemplateExecute(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String schemaList = req.get("schemaList") + "";
		String schemaDetailsList = req.get("schemaDetailsList") + "";
		ClinicComboModule ccm = new ClinicComboModule(dao);
		ccm.saveMedicalTemplateExecute(body,schemaList, schemaDetailsList, res,
				dao, ctx);
	}
	
	/**
	 * 诊疗方案模板启用
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doTherapeuticRegimenExecute(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String schemaList = req.get("schemaList") + "";
		String schemaDetailsList = req.get("schemaDetailsList") + "";
		ClinicComboModule ccm = new ClinicComboModule(dao);
		ccm.saveTherapeuticRegimenExecute(body,schemaList, schemaDetailsList, res,
				dao, ctx);
	}
	@SuppressWarnings("unchecked")
	public void doRemovePrescriptionDel(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ModelDataOperationException{
		ClinicComboModule ccm = new ClinicComboModule(dao);
		String op = (String)req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ccm.removePrescriptionDel(body, res, op, dao, ctx);
	}
	@SuppressWarnings("unchecked")
	public void doSaveCommonlyUsedDrugs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ModelDataOperationException{
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicComboModule ccm = new ClinicComboModule(dao);
		ccm.doSaveCommonlyUsedDrugs(body, res, dao, ctx);
	}
	
	@SuppressWarnings("unchecked")
	public void doSavePersonalCommonly(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ModelDataOperationException{
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicComboModule ccm = new ClinicComboModule(dao);
		ccm.doSavePersonalCommonly(body, res, dao, ctx);
	}
	@SuppressWarnings("unchecked")
	public void doSaveClinicMedicalDetail(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ModelDataOperationException{
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicComboModule ccm = new ClinicComboModule(dao);
		ccm.saveClinicMedicalDetail(body, res, dao, ctx);
	}
	@SuppressWarnings("unchecked")
	public void doLoadClinicMedicalDetail(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ModelDataOperationException{
		String pkey =  req.get("pkey")+"";
		ClinicComboModule ccm = new ClinicComboModule(dao);
		ccm.loadClinicMedicalDetail(pkey, res, dao, ctx);
	}
}
