/**
 * @(#)StatService.java Created on 2012-8-2 上午09:07:16
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.pub;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.PersistentDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class StatService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(StatService.class);

	/**
	 * 查询该机构下健康档案数量
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetRecordNum(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String manaUnitId = (String) body.get("deptId");
		Date now = new Date();
		StatModel statModel = new StatModel(dao, now);
		try {
			List<Map<String, Object>> data = statModel.getUnitData(manaUnitId);
			Map<String, Object> maxValue = statModel.getMaxValue(manaUnitId);
			int max = 0 ;
			if(maxValue.get("maxValue") != null ){
				max= (Integer) maxValue.get("maxValue");
			}
			if (max < 4) {
				res.put("autoHigh", false);
			} else {
				res.put("autoHigh", true);
			}
			data = SchemaUtil.setDictionaryMessageForList(data, PUB_Stat);
			res.put("body", data);
		} catch (PersistentDataOperationException e) {
			logger.error("get all healthRecord num fail.");
			throw new ServiceException("查询该机构下健康档案数量失败。", e);
		}
	}

	/**
	 * 查询各档案数量
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetAllRecordCount(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String manaUnitId = (String) body.get("deptId");
		Date now = new Date();
		StatModel statModel = new StatModel(dao, now);
		try {
			Map<String, Object> data = statModel
					.getSummaryInfoByUnitId(manaUnitId);
			int addCount = statModel.getNewHealthRecordCount(manaUnitId);
			data.put("addCount", addCount);
			res.put("body", data);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			logger.error("get record num fail.");
			throw new ServiceException("查询各档案数量失败。", e);
		}
	}
}
