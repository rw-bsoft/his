/**
 * @(#)SystemManageTypeService.java Created on 2014-7-30 上午9:25:18
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.conf;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONException;

import ctd.app.ApplicationController;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.util.SchemaUtil;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class SystemManageTypeService extends AbstractActionService implements
		DAOSupportable {
	private static Log logger = LogFactory
			.getLog(SystemManageTypeService.class);

	/**
	 * 保存公共设置
	 * 
	 * @param req
	 * @param res
	 * @param session
	 * @param ctx
	 * @throws URISyntaxException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveConfig(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		if (body == null) {
			logger.error("body  is missed!");
			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "请求数据丢失！");
			return;
		}
		SystemCofigManageModel scmm = new SystemCofigManageModel(dao);
		try {
			Schema sc = null;
			try {
				sc = SchemaController.instance().get(SYS_ManageTypeConfig);
			} catch (ControllerException e1) {
				throw new ModelDataOperationException(
						Constants.CODE_EXP_ERROR, "schema[SYS_ManageTypeConfig]解析失败！", e1);
			}
			for (SchemaItem si : sc.getItems()) {
				if (si.hasProperty("refAlias") || si.isVirtual()) {
					continue;
				}
				String f = si.getId();
				String fv = (String) body.get(f);
				scmm.saveSystemConfigData(f, fv);
			}
			
			ApplicationController.instance().reload(Constants.UTIL_APP_ID);
			List<Map<String, Object>> count = dao.doSqlQuery("select count(*) as totalcount from EHR_HealthRecord", null);
			String healthCount=count.get(0).get("TOTALCOUNT")+"";
			count = dao.doSqlQuery("select count(*) as totalcount from EHR_FamilyRecord", null);
			String familyCount=count.get(0).get("TOTALCOUNT")+"";
			body = SchemaUtil.setDictionaryMessageForForm(body,
					SYS_ManageTypeConfig);
//			if("0".endsWith(familyCount)&&"0".endsWith(healthCount)){
//				body.put("canUpdate", true);
//			}else{
//				body.put("canUpdate", false);
//			}
			res.put("body", body);
		} catch (Exception e) {
			res.put(RES_CODE, ServiceCode.CODE_IO_EXCEPTION);
			res.put(RES_MESSAGE, "配置文件保存失败。");
			e.printStackTrace();
			return;
		}
	}

	/**
	 * 获得配置信息
	 * 
	 * @param req
	 * @param res
	 * @param session
	 * @param sc
	 * @param ctx
	 * @throws ServiceException
	 * @throws JSONException
	 */
	public void doGetConfig(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			Map<String, Object> body = new HashMap<String, Object>();
			SystemCofigManageModel scmm = new SystemCofigManageModel(dao);
			
			Schema sc = null;
			try {
				sc = SchemaController.instance().get(SYS_ManageTypeConfig);
			} catch (ControllerException e1) {
				throw new ModelDataOperationException(
						Constants.CODE_EXP_ERROR, "schema[SYS_ManageTypeConfig]解析失败！", e1);
			}
			for (SchemaItem si : sc.getItems()) {
				if (si.hasProperty("refAlias") || si.isVirtual()) {
					continue;
				}
				String f = si.getId();
				String fv = scmm.getSystemConfigData(f);
				body.put(f, fv);
			}
			
			List<Map<String, Object>> count = dao.doSqlQuery("select count(*) as totalcount from EHR_HealthRecord", null);
			String healthCount=count.get(0).get("TOTALCOUNT")+"";
			count = dao.doSqlQuery("select count(*) as totalcount from EHR_FamilyRecord", null);
			String familyCount=count.get(0).get("TOTALCOUNT")+"";
			body = SchemaUtil.setDictionaryMessageForForm(body,
					SYS_ManageTypeConfig);
//			if("0".endsWith(familyCount)&&"0".endsWith(healthCount)){
//				body.put("canUpdate", true);
//			}else{
//				body.put("canUpdate", false);
//			}
			res.put("body", body);
		} catch (Exception e) {
			e.printStackTrace();
			res.put(RES_CODE, ServiceCode.CODE_IO_EXCEPTION);
			res.put(RES_MESSAGE, "配置文件解析失败。");
			return;
		}
	}
}
