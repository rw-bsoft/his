package phis.application.cfg.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.ivc.source.TreatmentNumberService;
import phis.source.BaseDAO;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.util.context.Context;

public class AntimicrobialDrugUseCausesService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(TreatmentNumberService.class);

	/**
	 * 删除抗菌药物使用原因
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doSaveAntimicrobialDrug(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		AntimicrobialDrugUseCausesModule ciim = new AntimicrobialDrugUseCausesModule(
				dao);
		ciim.doSaveAntimicrobialDrug(req, res, dao, ctx);
	}

	/**
	 * 判断该抗菌药物使用原因有没有被使用
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	public void doQueryIfUsed(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		AntimicrobialDrugUseCausesModule ciim = new AntimicrobialDrugUseCausesModule(
				dao);
		ciim.doQueryIfUsed(req, res, dao, ctx);
	}
	/**
	 * 重新加载抗菌药物使用原因字典
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	public void doReloadYY(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		AntimicrobialDrugUseCausesModule ciim = new AntimicrobialDrugUseCausesModule(
				dao);
		ciim.reloadYY(req, res, dao, ctx);
	}

}
