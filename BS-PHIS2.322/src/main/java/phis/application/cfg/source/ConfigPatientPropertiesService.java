/**
 * @description 病人性质
 * 
 * @author zhangyq 2012.05.30
 */
package phis.application.cfg.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.ServiceCode;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class ConfigPatientPropertiesService extends AbstractActionService
		implements DAOSupportable {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigPatientPropertiesService.class);

	/**
	 * 自付比例保存
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveOwnExpenseProportion(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		int BRXZ = (Integer)req.get("brxz");
		try {
			ConfigPatientPropertiesModel cpm = new ConfigPatientPropertiesModel(
					dao);
			cpm.doSaveOwnExpenseProportion(body,BRXZ);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "自负比例修改成功");
	}

	/**
	 * 自付比例查询
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doQueryOwnExpenseProportion(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		int BRXZ = Integer.parseInt((String)((List<?>) ((List<?>) req.get("cnd")).get(2)).get(1));
		List<Map<String,Object>> body = new ArrayList<Map<String,Object>>();
		try {
			ConfigPatientPropertiesModel cpm = new ConfigPatientPropertiesModel(
					dao);
			body = cpm.doQueryOwnExpenseProportion(BRXZ);
			for(int i = 0; i < body.size(); i++){
				body.get(i).put("SFXM_text", body.get(i).get("SFXM_TEXT"));
			}
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		// res.put(RES_CODE, ServiceCode.CODE_OK);
		// res.put(RES_MESSAGE, "自负比例查询成功");
	}

	/**
	 * 药品限制和费用限制保存
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveLimit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String tableName = (String) req.get("schema");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String fName = (String) body.get("fName");
		String fValue = body.get("fValue") + "";
		try {
			ConfigPatientPropertiesModel pm = new ConfigPatientPropertiesModel(dao);
			pm.doSaveLimit(tableName, body, fName, fValue);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "保存成功");
	}
	
	
	/**
	 * 药品限制和费用限制查询
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doListLimit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
		ConfigPatientPropertiesModel cppm = new ConfigPatientPropertiesModel(dao);
		int pageSize= 25;
		if(req.containsKey("pageSize")){
			pageSize = (Integer)req.get("pageSize");
		}
		int first =0;
		if(req.containsKey("pageNo")){
			first = (Integer)req.get("pageNo")-1;
		}
		Map<String,Object> body = (Map<String,Object>)req.get("body");
		List<Object> cnd = (List<Object>)req.get("cnd");
		String tableName = (String)req.get("schema");
		Map<String, Object> parameters=new HashMap<String, Object>();
		parameters.put("first", first*pageSize);
		parameters.put("max", pageSize);
		try {
			List<Object> l=cppm.doListLimit(tableName,body,cnd,parameters);
			int count=(Integer)l.get(0);
			List<Map<String, Object>> rs=	(List<Map<String, Object>>)l.get(1);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount",count);
			res.put("body",rs);
		}catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	
	/**
	 * 药品限制和费用限制删除
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doRemoveLimit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String tableName = (String) req.get("schema");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			ConfigPatientPropertiesModel pm = new ConfigPatientPropertiesModel(dao);
			pm.doRemoveLimit(tableName, body);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	/**
	 * 病人性质保存。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSavePatientNature(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String op = (String) req.get("op");
		if (StringUtils.isEmpty(op)) {
			op = "create";
		}
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			ConfigPatientPropertiesModel cpm = new ConfigPatientPropertiesModel(
					dao);
			res.put("body", cpm.doSavePatientNature(op, body));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "保存成功");
	}

	/**
	 * 病人性质删除。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemovePatientNature(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		int pkey = Integer.parseInt(((Map<String, Object>) req.get("body"))
				.get("pkey") + "");
		try {
			ConfigPatientPropertiesModel pm = new ConfigPatientPropertiesModel(
					dao);
			pm.doRemovePatientNature(pkey, res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
}
