package chis.source.mobile;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.control.ControlRunner;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;

public class DiabetesMobileService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(DiabetesMobileService.class);

	/**
	 * 获取随访权限控制
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetDiabetesVisitControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		try {
			data = ControlRunner.run(MDC_DiabetesVisit, reqBodyMap,
					ContextUtils.getContext(), ControlRunner.CREATE,
					ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check MDC_DiabetesVisit control error.", e);
			throw e;
		}
		res.put("body", data);
	}

	/**
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doEnabledInputMedicine(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		Map<String, Object> resBody = new HashMap<String, Object>();
		DiabetesMobileModel dbtModel = new DiabetesMobileModel(dao);
		try {
			Map<String, Object> data = dbtModel.enabledInputMedicine(reqBody);
			if (null != data && data.size() > 0) {
				String m = (String) data.get("medicine");
				if ("1".equals(m) || "2".equals(m)) {
					resBody.put("needInputMedicine", true);
				} else {
					resBody.put("needInputMedicine", false);
				}
			} else {
				resBody.put("needInputMedicine", false);
			}
		} catch (ModelDataOperationException e) {
			logger.error("falied to enabled input medicine", e);
			throw new ServiceException(e);
		}
		res.put("body", resBody);
	}

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
		DiabetesMobileModel dbtModel = new DiabetesMobileModel(dao);
		try {
			dbtModel.removeMedicine(visitId);
		} catch (PersistentDataOperationException e) {
			logger.error(
					"failed to delete  MDC_DiabetesMedicine by visitId(mobile offline)",
					e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "删除糖尿病服药失败(上传移动端离线录入的服药)！");
			throw new ServiceException(e);
		}
	}
}
