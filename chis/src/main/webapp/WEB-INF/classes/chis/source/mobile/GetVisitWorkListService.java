package chis.source.mobile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.Medicine;
import chis.source.mdc.HypertensionVisitModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class GetVisitWorkListService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(GetVisitWorkListService.class);

	/**
	 * 获取老年人随访列表数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doLoadOldPeopleVisit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		queryDealWith(req, res, dao, ctx, MDC_OldPeopleVisitSearch,
				BusinessType.LNR);
	}

	/**
	 * 获取高血压随访列表数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doLoadHypertensionVisit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		queryDealWith(req, res, dao, ctx, MDC_HypertensionVisitSearch,
				BusinessType.GXY);
	}

	/**
	 * 获取糖尿病随访列表数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doLoadDiabetesVisit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		queryDealWith(req, res, dao, ctx, MDC_DiabetesVisitSearch,
				BusinessType.TNB);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void queryDealWith(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx, String schema,
			String type) {
		int pageSize = (Integer) req.get("pageSize");
		int pageNo = (Integer) req.get("pageNo");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String manaDoctorId = (String) body.get("uid");
		try {
			String currentDate = BSCHISUtil.toString(new Date(), null);
			String serverDate = "date('" + currentDate + "')";

			List<?> manaCnd = CNDHelper.createSimpleCnd("eq", "a.manaDoctorId",
					"s", manaDoctorId);
			List<?> beginDate = CNDHelper.createSimpleCnd("le", "d.beginDate",
					"$", serverDate);
			List<?> endDate = CNDHelper.createSimpleCnd("ge", "d.endDate", "$",
					serverDate);
			List<?> typeCnd = CNDHelper.createSimpleCnd("eq", "d.businessType",
					"s", type);
			List<?> statusCnd = CNDHelper.createSimpleCnd("eq", "d.planStatus",
					"s", '0');

			List<?> dateCnd = CNDHelper.createArrayCnd("and", beginDate,
					endDate);
			List<?> cnd1 = CNDHelper.createArrayCnd("and", manaCnd, dateCnd);
			List<?> cnd2 = CNDHelper.createArrayCnd("and", typeCnd, cnd1);
			List<?> cnd = CNDHelper.createArrayCnd("and", statusCnd, cnd2);

			if (body.containsKey("personName")) {
				String personName = (String) body.get("personName");
				List<?> personCnd = CNDHelper.createSimpleCnd("like",
						"b.personName", "s", "%"+personName+"%");
				cnd = CNDHelper.createArrayCnd("and", cnd, personCnd);
			}
			if (body.containsKey("idCard")) {
				String idCard = (String) body.get("idCard");
				List<?> idCardCnd = CNDHelper.createSimpleCnd("like", "b.idCard",
						"s", "%"+idCard+"%");
				cnd = CNDHelper.createArrayCnd("and", cnd, idCardCnd);
			}
			Map map = dao.doList(cnd, "b.personName,d.planDate", schema,
					pageNo, pageSize, null);
			if (map != null) {
				res.put("totalCount", map.get("totalCount"));
				res.put("body", map.get("body"));
			}
		} catch (PersistentDataOperationException e) {
			logger.error("loading user list is failure");
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取老年人随访列表数据失败。");
			e.printStackTrace();
		}
	}

	/**
	 * 获取随访（记录）是否服药
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetMedicineCase(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String visitId = (String) reqBodyMap.get("visitId");
		boolean isMedicine = false;
		if (StringUtils.isNotEmpty(visitId)) {
			HypertensionVisitModel hvm = new HypertensionVisitModel(dao);
			Map<String, Object> rsMap = null;
			try {
				rsMap = hvm.getHypertensionVisitByPkey(visitId);
			} catch (ModelDataOperationException e) {
				logger.error("Get Hypertension visit record by visitId["
						+ visitId + "] failed.", e);
				throw new ServiceException(e);
			}
			if (rsMap != null) {
				String medicine = (String) rsMap.get("medicine");
				if (StringUtils.isNotBlank(medicine)) {
					if (Medicine.LAW_TAKE_MEDICINE.equals(medicine)
							|| Medicine.INTERRUPTION_TAKE_MEDICINE
									.equals(medicine)) {
						isMedicine = true;
					}
				}
			}
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put("isMedicine", isMedicine);
		res.put("body", resBodyMap);
	}

}
