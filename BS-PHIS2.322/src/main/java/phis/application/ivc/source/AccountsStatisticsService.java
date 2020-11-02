package phis.application.ivc.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class AccountsStatisticsService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(AccountsStatisticsService.class);

	// 统计前验证
	@SuppressWarnings("unchecked")
	public void doAccountsStatisticsBefVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		AccountsStatisticsModel asm = new AccountsStatisticsModel(dao);
		asm.doAccountsStatisticsBefVerification(body, res, dao, ctx);
	}

	@SuppressWarnings("unchecked")
	public void doSaveAccountsStatistics(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		AccountsStatisticsModel asm = new AccountsStatisticsModel(dao);
		try {
			asm.doSaveAccountsStatistics(body, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage());
		}
	}

}
