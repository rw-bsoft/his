package chis.source.mobile;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;

public class HypertensionMobileService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(HypertensionMobileService.class);

	/**
	 * 移动端上传糖尿病服药前,首先根据visitId删除服药,然后上传移动端录入的服药
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveMedine(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String visitId = (String) body.get("visitId");
		HypertensionMobileModel hypModel = new HypertensionMobileModel(dao);
		try {
			hypModel.removeMedicine(visitId);
		} catch (PersistentDataOperationException e) {
			logger.error(
					"failed to delete  MDC_HypertensionMedicine by visitId(mobile offline)",
					e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "删除高血压服药失败(上传移动端离线录入的服药)！");
			throw new ServiceException(e);
		}
	}

}
