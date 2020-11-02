package phis.application.ivc.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.util.context.Context;

public class ChargesSummaryService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(ChargesSummaryService.class);

	// 产生前验证
	public void doChargesSummaryBefVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		ChargesSummaryModel csm = new ChargesSummaryModel(dao);
		csm.doChargesSummaryBefVerification(req, res, dao, ctx);
	}

	// 汇总前验证
	@SuppressWarnings("unchecked")
	public void doChargesSummaryCheckOutBefVerification(
			Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
//		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ChargesSummaryModel csm = new ChargesSummaryModel(dao);
		csm.doChargesSummaryCheckOutBefVerification(req, res, dao, ctx);
	}
	
	//取消汇总查询
	public void doQueryCancelCommit(
			Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
//		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ChargesSummaryModel csm = new ChargesSummaryModel(dao);
		csm.doQueryCancelCommit(req, res, dao, ctx);
	}
	
	public void doCancelCommit(Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		ChargesSummaryModel csm = new ChargesSummaryModel(dao);
		csm.doCancelCommit(req, res, dao, ctx);
	}
}
