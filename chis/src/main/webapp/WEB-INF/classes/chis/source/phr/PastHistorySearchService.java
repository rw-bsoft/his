/**
 * @(#)PastHistorySearchService1.java Created on 2012-4-8 下午10:01:07
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.phr;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:tianj@bsoft.com.cn">田军</a>
 */
public class PastHistorySearchService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(PastHistorySearchService.class);

	/**
	 * 载入个人既往史数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void doQueryPastHistoryRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			PastHistoryModel phModel = new PastHistoryModel(dao);
			Map<String, Object> reValue = phModel.loadPastHistoryRecords(req);
			if (reValue != null) {
				List records = (List) reValue.get("records");
				int pageSize = (Integer) reValue.get("pageSize");
				int pageNo = (Integer) reValue.get("pageNo");
				Long totalCount = (Long) reValue.get("totalCount");
				res.put("body", records);
				res.put("pageSize", pageSize);
				res.put("pageNo", pageNo);
				res.put("totalCount", totalCount);
			}
		} catch (ModelDataOperationException e) {
			logger.error("searching pashHistoryRecords unsuccessfully");
			throw new ServiceException(e);
		}
	}
}
