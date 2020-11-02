/**
 * @(#)PublicInfoService.java Created on 2012-5-8 上午10:26:29
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.admin;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.SchemaUtil;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class PublicInfoService extends AbstractActionService implements
		DAOSupportable {
	private static final Log logger = LogFactory
			.getLog(PublicInfoService.class);

	protected void doRemovePublicInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String pkey = (String) req.get("pkey");
		String schema = (String) req.get("schema");
		PublicInfoModel pim = new PublicInfoModel(dao);
		try {
			pim.removePublicInfo(pkey, schema);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to remove PublicInfo .", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "删除公告维护信息失败。");
			throw new ServiceException(e);
		}
	}
	
	protected void doSavePublicInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String schema = (String) req.get("schema");
		String op = (String) req.get("op");
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		PublicInfoModel pim = new PublicInfoModel(dao);
		Map<String, Object> body = null;
		try {
			body = pim.savePublicInfo(schema, op, reqBody);
			body = SchemaUtil.setDictionaryMessageForForm(body, schema);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save PublicInfo .", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "保存公告维护信息失败。");
			throw new ServiceException(e);
		}
		res.put("body", body);
	}

}
