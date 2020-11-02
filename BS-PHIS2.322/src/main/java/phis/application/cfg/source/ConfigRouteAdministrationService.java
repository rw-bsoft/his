/**
 * 
 * @description 给药途径维护
 * @author zhangyq 2012.05.30
 */
package phis.application.cfg.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class ConfigRouteAdministrationService extends AbstractActionService
		implements DAOSupportable {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigRouteAdministrationService.class);

	/**
	 * 给药途径维护保存
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveRouteAdministration(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		int code = 200;
		String msg = "给药途径保存成功";
		try {
			ConfigRouteAdministrationModel cram = new ConfigRouteAdministrationModel(
					dao);
			res.put("body", cram.doSaveRouteAdministration(op, body));
			if ("1".equals(((Map<String, Object>) res.get("body")).get("NUM"))) {
				code = 400;
				msg = "该给药途径名称已存在，数据保存失败";
			}
			DictionaryController.instance().reload("drugMode");
			DictionaryController.instance().reload("drugWay");
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		res.put(RES_CODE, code);
		res.put(RES_MESSAGE, msg);
	}
}
