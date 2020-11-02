package phis.application.cic.source;

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

public class ClinicStencilNameModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ClinicStencilNameModel.class);

	public ClinicStencilNameModel(BaseDAO dao) {
		this.dao = dao;
	}

	public void clinicStencilNameVerification(Map<String, Object> body,
			Map<String, Object> res, String op, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		if ("create".equals(op)) {
			String sql = "MBMC=:MBMC and YGDM=:YGDM and JGID=:JGID and SSLB=:SSLB";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("MBMC", body.get("MBMC"));
			parameters.put("YGDM", body.get("YGDM"));
			parameters.put("JGID", body.get("JGID"));
			parameters.put("SSLB",
					Integer.parseInt(body.get("SSLB").toString()));
			try {
				Long l = dao.doCount("GY_BLMB", sql, parameters);
				if (l > 0) {
					res.put(Service.RES_CODE, 613);
					res.put(Service.RES_MESSAGE, "模版名称已经存在");
				}
			} catch (PersistentDataOperationException e) {
				logger.error("Save failed.", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "模版名称校验失败");
			}
		} else {
			String sql = "MBMC=:MBMC and YGDM=:YGDM and JGID=:JGID and SSLB=:SSLB and JLXH<>:JLXH";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("MBMC", body.get("MBMC"));
			parameters.put("YGDM", body.get("YGDM"));
			parameters.put("JGID", body.get("JGID"));
			parameters.put("SSLB",
					Integer.parseInt(body.get("SSLB").toString()));
			parameters.put("JLXH", body.get("JLXH"));
			try {
				Long l = dao.doCount("GY_BLMB", sql, parameters);
				if (l > 0) {
					res.put(Service.RES_CODE, 613);
					res.put(Service.RES_MESSAGE, "模版名称已经存在");
				}
			} catch (PersistentDataOperationException e) {
				logger.error("Save failed.", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "模版名称校验失败");
			}
		}
	}
}
