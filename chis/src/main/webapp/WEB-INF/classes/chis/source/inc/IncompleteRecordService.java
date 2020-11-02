/**
 * @(#)IncompleteRecordService.java Created on 2014-8-14 下午1:50:12
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.inc;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.empi.EmpiUtil;
import chis.source.phr.HealthRecordModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.SchemaUtil;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">YuBo</a>
 */
public class IncompleteRecordService extends AbstractActionService implements
		DAOSupportable {

	private static final Logger logger = LoggerFactory
			.getLogger(IncompleteRecordService.class);

	@SuppressWarnings("unchecked")
	public void doQueryPersonInfoByHealthNo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String healthNo = (String) body.get("healthNo");
		IncompleteRecordModel irm = new IncompleteRecordModel(dao);
		try {
			Map<String, Object> personInfo = irm
					.queryPersonInfoByHealthNo(healthNo);
			if (personInfo != null) {
				res.putAll(SchemaUtil.setDictionaryMessageForForm(personInfo,
						INC_IncompleteRecord));
			}
		} catch (ModelDataOperationException e) {
			logger.error("Failed to queryPersonInfoByHealthNo.", e);
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doQueryPersonInfoByIdCard(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String idCard = (String) body.get("idCard");
		IncompleteRecordModel irm = new IncompleteRecordModel(dao);
		try {
			Map<String, Object> personInfo = irm
					.queryPersonInfoByIdCard(idCard);
			if (personInfo == null) {
				return;
			}
			String empiId = (String) personInfo.get("empiId");
			String healthNo = irm.getHealthNoByEmpiId(empiId);
			if (healthNo != null) {
				personInfo.put("healthNo", healthNo);
			}
			if (personInfo != null) {
				res.putAll(SchemaUtil.setDictionaryMessageForForm(personInfo,
						INC_IncompleteRecord));
			}
		} catch (ModelDataOperationException e) {
			logger.error("Failed to queryPersonInfoByIdCard.", e);
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doAdvancedSearch(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		// IncompleteRecordModel irm = new IncompleteRecordModel(dao);
		Map<String, Object> map = EmpiUtil.queryByPersonInfo(dao, ctx, reqBody);
		// String empiId = (String) map.get("empiId");
		// try {
		// String healthNo = irm.getHealthNoByEmpiId(empiId);
		// if (healthNo != null) {
		// map.put("healthNo", healthNo);
		// }
		res.putAll(map);
		int pageSize = (Integer) req.get("pageSize") == null ? 50
				: ((Integer) req.get("pageSize")).intValue();
		int pageNo = (Integer) req.get("pageNo") == null ? 1 : ((Integer) req
				.get("pageNo")).intValue();
		res.put("pageSize", pageSize);
		res.put("pageNo", pageNo);
		res.putAll(map);
		// } catch (ModelDataOperationException e) {
		// logger.error("Failed to advancedSearch.", e);
		// throw new ServiceException(e);
		// }
	}

	@SuppressWarnings("unchecked")
	public void doCheckupHealthRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		HealthRecordModel hm = new HealthRecordModel(dao);
		try {
			Map<String, Object> record = hm.getHealthRecordByEmpiId(empiId);
			if (record != null) {
				res.put("phrId", record.get("phrId"));
				res.put("manaDoctorId", record.get("manaDoctorId"));
				res.put("haveRecord", true);
			} else {
				res.put("haveRecord", false);
			}
		} catch (ModelDataOperationException e) {
			logger.error("Failed to checkupHealthRecord.", e);
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doGetYLData(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		IncompleteRecordModel irm = new IncompleteRecordModel(dao);
		try {
			List<Map<String, Object>> YLData = irm.getYLData(empiId);
			if (YLData != null && YLData.size() > 0) {
				res.put("body", YLData);
			}
		} catch (ModelDataOperationException e) {
			logger.error("Failed to doGetYLData.", e);
			throw new ServiceException(e);
		}
	}

}
