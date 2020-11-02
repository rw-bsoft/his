package phis.application.cfg.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

import ctd.service.core.Service;
import ctd.util.context.Context;

public class ConfigPaymentCategoryModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigPaymentCategoryModel.class);

	public ConfigPaymentCategoryModel(BaseDAO dao) {
		this.dao = dao;
	}

	public void paymentCategoryVerification(Map<String, Object> body,
			Map<String, Object> res, String schemaDetailsList, String op,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		if ("create".equals(op)) {
			String sql = "LBMC=:LBMC";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("LBMC", body.get("LBMC"));
			try {
				Long l = dao.doCount(schemaDetailsList, sql, parameters);
				if (l > 0) {
					res.put(Service.RES_CODE, 613);
					res.put(Service.RES_MESSAGE, "付款类别已经存在");
				}
			} catch (PersistentDataOperationException e) {
				logger.error("Save failed.", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "付款类别校验失败");
			}
		} else {
			String sql = "LBMC=:LBMC and FKLB<>:FKLB";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("LBMC", body.get("LBMC"));
			parameters.put("FKLB", Long.parseLong(body.get("FKLB")+""));
			try {
				Long l = dao.doCount(schemaDetailsList, sql, parameters);
				if (l > 0) {
					res.put(Service.RES_CODE, 613);
					res.put(Service.RES_MESSAGE, "付款类别已经存在");
				}
			} catch (PersistentDataOperationException e) {
				logger.error("Save failed.", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "付款类别校验失败");
			}
		}
	}

	public void paymentCategoryDelVerification(Map<String, Object> body,
			Map<String, Object> res, String schemaDetailsList, String op,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		String sql = "FKLB=:FKLB";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FKLB",body.get("FKLB")+"");
		try {
			Long l = dao.doCount(schemaDetailsList, sql, parameters);
			if (l > 0) {
				res.put(Service.RES_CODE, 613);
				res.put(Service.RES_MESSAGE, "付款方式已选此类别，无法删除");
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "付款方式已选此类别校验失败");
		}
	}
}
