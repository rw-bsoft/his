/**
 * @(#)SchistospmaManageService.java Created on 2012-5-14 下午04:08:24
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.sch;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.util.SchemaUtil;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class SchistospmaManageService  extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
	.getLogger(SchistospmaManageService.class);
	
	@SuppressWarnings("unchecked")
	protected void doSaveSchistospmaManage(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String op = (String) req.get("op");
		SchistospmaManageModel smm=new SchistospmaManageModel(dao);
		Map<String, Object> body=null;
		try {
		body=smm.saveSchistospmaManage(schema,op,reqBody);
		body=SchemaUtil.setDictionaryMessageForForm(body, schema);
			} catch (ModelDataOperationException e) {
				logger.error("save "+schema+" is fail");
				res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
				res.put(Service.RES_MESSAGE, "保存血吸虫综合治理失败");
				throw new ServiceException(e);
			}
			res.put("body", body);
		
	}
	
	
	protected void doRemoveSchistospmaManage(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String schisManageId = (String) req.get("pkey");
		String schema = (String) req.get("schema");
		SchistospmaManageModel smm=new SchistospmaManageModel(dao);
		try {
			smm.removeSchistospmaManage(schisManageId,schema);
		} catch (ModelDataOperationException e) {
			logger.error("remove "+schema+" is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "删除血吸虫综合治理失败");
			throw new ServiceException(e);
		}
	}
}
