/**
 * @(#)SystemConfigManage.java Created on 2011-2-20 上午09:49:59
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.conf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import chis.source.BaseDAO;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import com.alibaba.fastjson.JSONException;
import com.bsoft.esb.ws.WebServiceDispatcher;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * 
 * @description：his接口设置服务
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */
public class InterfaceManageService extends AbstractActionService implements
		DAOSupportable {
	private static Log logger = LogFactory.getLog(InterfaceManageService.class);

	/**
	 * 保存HIS服务接口设置
	 * 
	 * @param req
	 * @param res
	 * @param session
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveConfig(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		if (body == null) {
			logger.error("body  is missed!");
			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "请求数据丢失！");
			return;
		}
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		try {
			smm.saveSystemConfigData(body);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 测试HIS服务接口设置
	 * 
	 * @param req
	 * @param res
	 * @param session
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doTestConfig(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		String url = new StringBuffer("http://")
				.append(StringUtils.trimToEmpty((String) body
						.get("webBindAddr"))).append(":")
				.append(body.get("webBindPort")).append("/")
				.append(StringUtils.trimToEmpty((String) body.get("appName")))
				.append("/TestWsService?wsdl").toString();
		try {
			WebServiceDispatcher ws = new WebServiceDispatcher();
			ws.wsInvoke(url, "execute", new Object[]{""});
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("HIS interface Test webService error!", e);
			res.put(Service.RES_CODE, 500);
			res.put(Service.RES_MESSAGE, "接口测试失败");
			throw new ServiceException("his 接口配置测试失败！");
		}
	}

	/**
	 * 获得his接口配置信息
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
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		Schema sc;
		try {
			sc = SchemaController.instance().get(SYS_InterfaceConfig);
			List<SchemaItem> list = sc.getItems();
			Map<String, Object> body = new HashMap<String, Object>();
			for (SchemaItem item : list) {
				String id = item.getId();
				body.put(id, smm.getSystemConfigData(id));
			}
			res.put("body", body);
		} catch (Exception e) {
			logger.error("get HIS interface  webService error!", e);
			res.put(Service.RES_CODE, 500);
			res.put(Service.RES_MESSAGE, "获取HIS接口配置信息失败!");
			throw new ServiceException("获取HIS接口配置信息失败！");
		}
		
	}

}
