package phis.application.hos.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.ivc.source.TreatmentNumberModule;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.utils.BSPHISUtil;
import ctd.util.context.Context;

public class HospitalRetreatBedModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TreatmentNumberModule.class);

	public HospitalRetreatBedModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 退床
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSaveRetreatBedVerification(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZYH", body.get("Ll_zyh"));
		parameters.put("CWHM", body.get("Ls_cwhm"));
		BSPHISUtil.cwgl_tccl(parameters, dao, ctx);
	}
}
