/**
 * 
 * @description 付款方式维护
 * 名称重复验证公用方法
 * @author zhangyq 2012.05.30
 */
package phis.application.cfg.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

import ctd.service.core.ServiceException;
import ctd.validator.ValidateException;

public class ConfigPaymentTypeModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigPaymentTypeModel.class);

	public ConfigPaymentTypeModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 付款方式作废
	 * 
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public int doPaymentWayInvalidate(Map<String, Object> body)
			throws ModelDataOperationException {
		try {
			body.put("FKFS", Long.parseLong(body.get("FKFS") + ""));
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("FKFS", Long.parseLong(body.get("FKFS") + ""));
			if ("1".equals(dao.doLoad(
					"select MRBZ as MRBZ from GY_FKFS where FKFS=:FKFS", map)
					.get("MRBZ")
					+ "")) {
				return 301;
			}
			if ("1".equals(dao.doLoad(
					"select HBWC as HBWC from GY_FKFS where FKFS=:FKFS", map)
					.get("HBWC")
					+ "")) {
				return 302;
			}
			dao.doUpdate("update GY_FKFS set ZFBZ=:ZFBZ WHERE FKFS=:FKFS", body);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("作废付款方式失败！", e);
		}
		return 200;
	}

	/**
	 * 付款方式默认
	 * 
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public int doUpdatePaymentDefault(Map<String, Object> body)
			throws ModelDataOperationException {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("FKFS", Long.parseLong(body.get("FKFS") + ""));
			if ("1".equals(dao.doLoad(
					"select ZFBZ as ZFBZ from GY_FKFS where FKFS=:FKFS",
					parameters).get("ZFBZ")
					+ "")) {
				return 301;
			}
			if ("1".equals(dao.doLoad(
					"select HBWC as HBWC from GY_FKFS where FKFS=:FKFS",
					parameters).get("HBWC")
					+ "")) {
				return 302;
			}
			parameters.remove("FKFS");
			parameters.put("SYLX", body.get("SYLX") + "");
			dao.doUpdate("update GY_FKFS  set MRBZ=0 where SYLX=:SYLX",
					parameters);
			parameters.put("FKFS", Long.parseLong(body.get("FKFS") + ""));
			dao.doUpdate(
					"update GY_FKFS set MRBZ=1 WHERE FKFS=:FKFS and SYLX=:SYLX",
					parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("修改默认付款方式失败！", e);
		}
		return 200;
	}

	/**
	 * 付款方式货币误差
	 * 
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public int doUpdateCurrencyErrors(Map<String, Object> body)
			throws ModelDataOperationException {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("FKFS", Long.parseLong(body.get("FKFS") + ""));
			if ("1".equals(dao.doLoad(
					"select ZFBZ as ZFBZ from GY_FKFS where FKFS=:FKFS",
					parameters).get("ZFBZ")
					+ "")) {
				return 301;
			}
			if ("1".equals(dao.doLoad(
					"select MRBZ as MRBZ from GY_FKFS where FKFS=:FKFS",
					parameters).get("MRBZ")
					+ "")) {
				return 302;
			}
			parameters.remove("FKFS");
			parameters.put("SYLX", body.get("SYLX") + "");
			dao.doUpdate("update GY_FKFS  set HBWC=0 where SYLX=:SYLX",
					parameters);
			parameters.put("FKFS", Long.parseLong(body.get("FKFS") + ""));
			dao.doUpdate(
					"update GY_FKFS set HBWC=1 WHERE FKFS=:FKFS and SYLX=:SYLX",
					parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Payment method fails to modify the monetary error.",
					e);
			throw new ModelDataOperationException("修改货币误差付款方式失败！", e);
		}
		return 200;
	}

	/**
	 * 付款方式维护保存
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected Map<String, Object> doSavePayment(String op,
			Map<String, Object> body) throws ModelDataOperationException {
		Map<String, Object> remap = new HashMap<String, Object>();
		remap.put("NUM", "0");
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("FKMC", body.get("FKMC"));
			map.put("SYLX", body.get("SYLX"));
			map.put("FKFS", 0l);
			if (body.get("FKFS") != null) {
				map.put("FKFS", Long.parseLong(body.get("FKFS") + ""));
			}
			if (dao.doCount("GY_FKFS",
					"FKMC=:FKMC and SYLX=:SYLX and FKFS<>:FKFS", map) > 0) {
				remap.put("NUM", "1");
				return remap;
			}
			remap = dao.doSave(op, BSPHISEntryNames.GY_FKFS + "_MZ", body,
					false);
		} catch (ValidateException e) {
			logger.error("fail to validate medicine information.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		} catch (PersistentDataOperationException e) {
			logger.error("fail to save medicine information.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		}
		return remap;
	}
}
