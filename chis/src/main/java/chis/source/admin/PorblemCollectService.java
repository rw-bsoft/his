/**
 * @(#)PorblemCollectService.java Created on 2012-5-21 下午04:25:23
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.admin;

import java.util.HashMap;
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
public class PorblemCollectService extends AbstractActionService implements
		DAOSupportable {
	private static final Log logger = LogFactory
			.getLog(PorblemCollectService.class);
	
	protected void doRemovePorblemCollect(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String pkey = (String) req.get("pkey");
		String schema = (String) req.get("schema");
		PorblemCollectModel pcm=new PorblemCollectModel(dao);
		try {
			pcm.removePublicInfo(pkey, schema);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to remove PorblemCollect .", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "删除问题收集信息失败。");
			throw new ServiceException(e);
		}	
	}
	
	@SuppressWarnings("unchecked")
	protected void doSavePorblemCollect(Map<String, Object> req,
			Map<String, Object> res,BaseDAO dao,Context ctx) throws ServiceException{
		String op = (String) req.get("op");
		String schema = (String) req.get("schema");
		Map<String, Object> reqBody=(Map<String, Object>) req.get("body");
		PorblemCollectModel pcm=new PorblemCollectModel(dao);
		Map<String, Object> body=new HashMap<String, Object>();
		try {
			body=pcm.savePorblemCollect(op,schema,reqBody);
			body=SchemaUtil.setDictionaryMessageForForm(body, schema);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save PorblemCollect .", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "保存问题收集信息失败。");
			throw new ServiceException(e);
		}
		res.put("body", body);
	}
}
