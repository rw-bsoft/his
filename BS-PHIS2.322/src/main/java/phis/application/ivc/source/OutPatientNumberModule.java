package phis.application.ivc.source;

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

public class OutPatientNumberModule {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(OutPatientNumberModule.class);

	public OutPatientNumberModule(BaseDAO dao) {
		this.dao = dao;
	}

	// 判断所有号码是否重复
	public void outPatientNumberVerification(Map<String, Object> body,
			Map<String, Object> res, String schemaDetailsList, String op,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		if ("create".equals(op)) {
			if (body.get("QSHM") != null) {
				String sql = "QSHM=:QSHM and PJLX=3";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("QSHM", body.get("QSHM"));
				try {
					Long l = dao.doCount(schemaDetailsList, sql, parameters);
					if (l > 0) {
						res.put(Service.RES_CODE, 608);
						res.put(Service.RES_MESSAGE, "起始号码已经存在");
					}
				} catch (PersistentDataOperationException e) {
					logger.error("Save failed.", e);
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "起始号码校验失败");
				}
			}
			if (body.get("ZZHM") != null) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				String sql = "ZZHM=:ZZHM and PJLX=3";
				parameters.put("ZZHM", body.get("ZZHM"));
				try {
					Long l = dao.doCount(schemaDetailsList, sql, parameters);
					if (l > 0) {
						res.put(Service.RES_CODE, 609);
						res.put(Service.RES_MESSAGE, "终止号码已经存在");
					}
				} catch (PersistentDataOperationException e) {
					logger.error("Save failed.", e);
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "终止号码校验失败");
				}
			}
			if (body.get("SYHM") != null) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				String sql = "SYHM=:SYHM and PJLX=3";
				parameters.put("SYHM", body.get("SYHM"));
				try {
					Long l = dao.doCount(schemaDetailsList, sql, parameters);
					if (l > 0) {
						res.put(Service.RES_CODE, 610);
						res.put(Service.RES_MESSAGE, "使用号码已经存在");
					}
				} catch (PersistentDataOperationException e) {
					logger.error("Save failed.", e);
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "使用号码校验失败");
				}
			}
		} else {
			if (body.get("QSHM") != null) {
				String sql = "QSHM=:QSHM and JLXH<>:JLXH and PJLX=3";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("QSHM", body.get("QSHM"));
				parameters.put("JLXH", body.get("JLXH"));
				try {
					Long l = dao.doCount(schemaDetailsList, sql, parameters);
					if (l > 0) {
						res.put(Service.RES_CODE, 608);
						res.put(Service.RES_MESSAGE, "起始号码已经存在");
					}
				} catch (PersistentDataOperationException e) {
					logger.error("Save failed.", e);
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "起始号码校验失败");
				}
			}
			if (body.get("ZZHM") != null) {
				String sql = "ZZHM=:ZZHM and JLXH<>:JLXH and PJLX=3";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("ZZHM", body.get("ZZHM"));
				parameters.put("JLXH", body.get("JLXH"));
				try {

					Long l = dao.doCount(schemaDetailsList, sql, parameters);
					if (l > 0) {
						res.put(Service.RES_CODE, 609);
						res.put(Service.RES_MESSAGE, "终止号码已经存在");
					}
				} catch (PersistentDataOperationException e) {
					logger.error("Save failed.", e);
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "终止号码校验失败");
				}
			}
			if (body.get("SYHM") != null) {
				String sql = "SYHM=:SYHM and JLXH<>:JLXH and PJLX=3";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("SYHM", body.get("SYHM"));
				parameters.put("JLXH", body.get("JLXH"));
				try {
					Long l = dao.doCount(schemaDetailsList, sql, parameters);
					if (l > 0) {
						res.put(Service.RES_CODE, 610);
						res.put(Service.RES_MESSAGE, "使用号码已经存在");
					}
				} catch (PersistentDataOperationException e) {
					logger.error("Save failed.", e);
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "使用号码校验失败");
				}
			}
		}
	}

	// 判断号码段是否冲突
	public void outPatientNumberRangeVerification(Map<String, Object> body,
			Map<String, Object> res, String schemaDetailsList, String op,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		if ("create".equals(op)) {
			if (body.get("QSHM") != null && body.get("ZZHM") != null) {
				int l = (body.get("QSHM") + "").length();
				String sql1 = "QSHM<=:QSHM and ZZHM>=:QSHM and PJLX=3 and length(QSHM)=:QSHMLENGTH";
				Map<String, Object> parameters1 = new HashMap<String, Object>();
				parameters1.put("QSHM", body.get("QSHM") + "");
				parameters1.put("QSHMLENGTH", l);
				String sql2 = "((QSHM>=:QSHM and QSHM<=:ZZHM) or (ZZHM>=:QSHM and ZZHM<=:ZZHM)) and length(QSHM)=:QSHMLENGTH and PJLX=3";
				Map<String, Object> parameters2 = new HashMap<String, Object>();
				parameters2.put("QSHM", body.get("QSHM") + "");
				parameters2.put("ZZHM", body.get("ZZHM") + "");
				parameters2.put("QSHMLENGTH", l);
				try {
					Long l1 = dao.doCount(schemaDetailsList, sql1, parameters1);
					Long l2 = dao.doCount(schemaDetailsList, sql2, parameters2);
					if (l1 > 0 || l2 > 0) {
						res.put(Service.RES_CODE, 611);
						res.put(Service.RES_MESSAGE, "起始号码到终止号码段不能跟别的号码段发生冲突");
					}
				} catch (PersistentDataOperationException e) {
					logger.error("Save failed.", e);
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "号码段冲突校验失败");
				}
			}
		} else {
			if (body.get("QSHM") != null && body.get("ZZHM") != null) {
				int l = (body.get("QSHM") + "").length();
				String sql1 = "QSHM<=:QSHM and ZZHM>=:QSHM and JLXH<>:JLXH and PJLX=3 and length(QSHM)=:QSHMLENGTH";
				Map<String, Object> parameters1 = new HashMap<String, Object>();
				parameters1.put("QSHM", body.get("QSHM") + "");
				parameters1.put("JLXH", body.get("JLXH"));
				parameters1.put("QSHMLENGTH", l);
				String sql2 = "((QSHM>=:QSHM and QSHM<=:ZZHM) or (ZZHM>=:QSHM and ZZHM<=:ZZHM)) and length(QSHM)=:QSHMLENGTH and JLXH<>:JLXH and PJLX=3";
				Map<String, Object> parameters2 = new HashMap<String, Object>();
				parameters2.put("QSHM", body.get("QSHM") + "");
				parameters2.put("ZZHM", body.get("ZZHM") + "");
				parameters2.put("JLXH", body.get("JLXH"));
				parameters2.put("QSHMLENGTH", l);
				try {
					Long l1 = dao.doCount(schemaDetailsList, sql1, parameters1);
					Long l2 = dao.doCount(schemaDetailsList, sql2, parameters2);
					if (l1 > 0 || l2 > 0) {
						res.put(Service.RES_CODE, 611);
						res.put(Service.RES_MESSAGE, "起始号码到终止号码段不能跟别的号码段发生冲突");
					}
				} catch (PersistentDataOperationException e) {
					logger.error("Save failed.", e);
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "号码段冲突校验失败");
				}
			}
		}
	}
}
