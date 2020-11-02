/**
 * @description 收费项目
 * 
 * @author shiwy 2012.06.29
 */
package phis.application.cfg.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class ConfigChargingProjectsDetailDelService extends AbstractActionService
		implements DAOSupportable {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigChargingProjectsDetailDelService.class);

	/**
	 * 收费项目明细删除时。同时要删除费用别名和费用限制两张表
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveChargingProjectsDetail(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		int fyxh = Integer.parseInt(((Map<String, Object>) req.get("body"))
				.get("fyxh") + "");
		try {
			ConfigChargingProjectsDetailDelModel ccpddm = new ConfigChargingProjectsDetailDelModel(
					dao);
			ccpddm.removeChargingProjectsDetail(fyxh, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
}
