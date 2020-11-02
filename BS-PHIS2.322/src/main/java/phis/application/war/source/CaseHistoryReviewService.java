/**
 * @(#)CaseHistoryReviewService.java Created on 2013-4-26 上午9:56:34
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.war.source;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.SchemaUtil;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class CaseHistoryReviewService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(CaseHistoryReviewService.class);

	public void doListAllCaseRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		CaseHistoryReviewModel chcm = new CaseHistoryReviewModel(dao);
		String YWID1 = req.get("YWID1") == null ? null : req.get("YWID1")
				.toString();
		List<Map<String, Object>> result = null;
		try {
			result = chcm.listAllCaseRecord(YWID1);
			SchemaUtil.setDictionaryMassageForList(result,
					BSPHISEntryNames.EMR_BL01_SJRZ);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询病人所有病历失败！", e);
		}
		res.put("body", result);
	}

	@SuppressWarnings("unchecked")
	public void doListRecordByTabId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ExpException {
		CaseHistoryReviewModel chcm = new CaseHistoryReviewModel(dao);
		String tabId = (String) req.get("tabId");
		String YWID1 = req.get("YWID1") == null ? null : req.get("YWID1")
				.toString();
		String YWID2 = req.get("YWID2") == null ? null : req.get("YWID2")
				.toString();
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 1;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		String queryCndsType = null;
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String records = (String) req.get("records");
		String[] r = null;
		if (records != null&&records.length()>0) {
			records = records.substring(0, records.length() - 1);
			r = records.split(",");
		}
		List<Map<String, Object>> body = null;
		Map<String, Object> result = null;
		try {
			result = chcm.listRecordByTabId(tabId, YWID1, r, YWID2, pageNo,
					pageSize, queryCndsType);
			body = (List<Map<String, Object>>) result.get("body");
			if (body != null) {
				SchemaUtil.setDictionaryMassageForList(body,
						BSPHISEntryNames.EMR_BLSJRZ);
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询病人所有病历失败！", e);
		}
		res.put("body", body);
		res.put("totalCount", result.get("totalCount"));
	}

	public void doLoadCountByTabID(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		CaseHistoryReviewModel chcm = new CaseHistoryReviewModel(dao);
		Map<String, Integer> result = null;
		String YWID1 = req.get("YWID1") == null ? null : req.get("YWID1")
				.toString();
		String YWID2 = req.get("YWID2") == null ? null : req.get("YWID2")
				.toString();
		String records = (String) req.get("records");
		String[] r = null;
		if (records != null&&records.length()>0) {
			records = records.substring(0, records.length() - 1);
			r = records.split(",");
		}
		try {
			result = chcm.loadCountByTabID(YWID1, YWID2, r);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("按标签查询日志失败！", e);
		}
		res.put("body", result);
	}

	public void doGetFileContent(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, IOException {
		CaseHistoryReviewModel chcm = new CaseHistoryReviewModel(dao);
		String BLBH = req.get("BLBH") == null ? null : req.get("BLBH")
				.toString();
		String type = (String) req.get("type");
		Map<String, Object> body = null;
		try {
			body = chcm.getFileContent(type, BLBH);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询文档内容失败！", e);
		}
		res.put("body", body);
	}

	public void doGetFormDataFromDB(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, IOException {
		CaseHistoryReviewModel chcm = new CaseHistoryReviewModel(dao);
		String BLBH = req.get("BLBH") == null ? null : req.get("BLBH")
				.toString();
		Map<String, Object> body = null;
		try {
			body = chcm.getFormDataFromDB(BLBH);
			SchemaUtil.setDictionaryMassageForForm(body,
					BSPHISEntryNames.EMR_BL01_SJRZ);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("根据主键查询病历失败！", e);
		}
		res.put("body", body);

	}
	
	public void doGetMZFormDataFromDB(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, IOException {
		CaseHistoryReviewModel chcm = new CaseHistoryReviewModel(dao);
		String BLBH = req.get("BLBH") == null ? null : req.get("BLBH")
				.toString();
		Map<String, Object> body = null;
		try {
			body = chcm.getMZFormDataFromDB(BLBH);
			SchemaUtil.setDictionaryMassageForForm(body,
					BSPHISEntryNames.EMR_BL01_SJRZ);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("根据主键查询病历失败！", e);
		}
		res.put("body", body);

	}
}
