/**
 * 
 */
package chis.source.demo;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.control.ControlRunner;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.SchemaUtil;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;


/**
 * @author 86159
 *
 */
public class PersonInfoService extends AbstractActionService implements
DAOSupportable {
	private static final Logger logger = LoggerFactory.getLogger(PersonInfoService.class);
	
	/**
	 * 
	 * @Description:保存演示记录服务
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2015-7-8 下午10:00:53
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDemo(Map<String, Object> req, Map<String, Object> res,BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		PersonInfoModel piModel = new PersonInfoModel(dao);
		Map<String, Object> resMap = new HashMap<String, Object>();
		try {
			resMap = piModel.saveDemo(op, reqBodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save demo record failed", e);
			throw new ServiceException(e);
		}
		res.put("body", resMap);
	}
	
	/**
	 * 
	 * @Description:获取人员信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2015-7-8 下午10:01:06
	 * @Modify:
	 */
//	public void doGetPersonInfo(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException {
//		String pkey = (String) req.get("pkey");
//		Map<String, Object> rsMap = null;
//		PersonInfoModel piModel = new PersonInfoModel(dao);
//		try {
//			rsMap = piModel.getPersonInfoByPkey(pkey);
//		} catch (ModelDataOperationException e) {
//			logger.error("Get Person Info by pkey failed.", e);
//			throw new ServiceException(e);
//		}
//		Map<String, Object> resBodyMap = new HashMap<String, Object>();
//		if (rsMap != null && rsMap.size() > 0) {
//			resBodyMap = SchemaUtil.setDictionaryMessageForForm(rsMap,PersonInfo);
//		}
//		res.put("body", resBodyMap);
//	}
	
	/**
	 * 获取人员信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
//	public void doGetPersonInfo(Map<String, Object> req,
//			Map<String, Object> res, BaseDAO dao, Context ctx)
//			throws ServiceException {
//		String pkey = (String) req.get("pkey");
//		Map<String, Object> rsMap = null;
//		PersonInfoModel piModel = new PersonInfoModel(dao);
//		try {
//			rsMap = piModel.getPersonInfoByPkey(pkey);
//		} catch (ModelDataOperationException e) {
//			logger.error("Get Person Info by pkey failed.", e);
//			throw new ServiceException(e);
//		}
//		Map<String, Object> resBodyMap = new HashMap<String, Object>();
//		if (rsMap != null && rsMap.size() > 0) {
//			resBodyMap = SchemaUtil.setDictionaryMessageForForm(rsMap,PersonInfo);
//			// 加载权限控制值
//			Map<String, Object> paraMap = new HashMap<String, Object>();
//			paraMap.put("id", rsMap.get("id"));
//			paraMap.put("sex", rsMap.get("sex"));
//			paraMap.put("age", rsMap.get("age"));
//			Map<String, Boolean> controlMap = ControlRunner.run(PersonInfo,paraMap, ctx, ControlRunner.UPDATE);
//			resBodyMap.put("_actions", controlMap);
//		}
//		res.put("body", resBodyMap);
//	}
	
	
	public void doGetPersonInfo(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException {
		String pkey = (String) req.get("pkey");
		Map<String, Object> rsMap = null;
		PersonInfoModel piModel = new PersonInfoModel(dao);
		try {
			rsMap = piModel.getPersonInfoByPkey(pkey);
		} catch (ModelDataOperationException e) {
			logger.error("Get Person Info by pkey failed.", e);
			throw new ServiceException(e);
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		Map<String, Boolean> controlMap = new HashMap<String, Boolean>();
		if (rsMap != null && rsMap.size() > 0) {
			resBodyMap = SchemaUtil.setDictionaryMessageForForm(rsMap,
					PersonInfo);
			// 加载权限控制值
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("id", rsMap.get("id"));
			paraMap.put("sex", rsMap.get("sex"));
			paraMap.put("age", rsMap.get("age"));
			controlMap = ControlRunner.run(PersonInfo,paraMap, ctx, ControlRunner.CREATE,ControlRunner.UPDATE);
		}else{
			controlMap.put("update", true);
		}
		controlMap.put("create", true);
		resBodyMap.put("_actions", controlMap);
		res.put("body", resBodyMap);
	}
	
}
