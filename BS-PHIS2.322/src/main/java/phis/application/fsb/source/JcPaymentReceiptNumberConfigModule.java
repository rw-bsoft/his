package phis.application.fsb.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.service.core.Service;
import ctd.util.context.Context;

public class JcPaymentReceiptNumberConfigModule {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(JcPaymentReceiptNumberConfigModule.class);

	public JcPaymentReceiptNumberConfigModule(BaseDAO dao) {
		this.dao = dao;
	}

	// 判断所有号码是否重复
	public void doPaymentReceiptNumberConfigVerification(
			Map<String, Object> body, Map<String, Object> res,
			String schemaDetailsList, String op, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		if ("create".equals(op)) {
			if (body.get("QSHM") != null) {
				String sql = "QSHM=:QSHM and PJLX=2 and JGID=:JGID";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("QSHM", body.get("QSHM"));
				parameters.put("JGID", jgid);
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
				String sql = "ZZHM=:ZZHM and PJLX=2 and JGID=:JGID";
				parameters.put("ZZHM", body.get("ZZHM"));
				parameters.put("JGID", jgid);
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
			if (body.get("DQHM") != null) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				String sql = "DQHM=:DQHM and PJLX=2 and JGID=:JGID";
				parameters.put("DQHM", body.get("DQHM"));
				parameters.put("JGID", jgid);
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
				String sql = "QSHM=:QSHM and JLXH<>:JLXH and PJLX=2 and JGID=:JGID";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("QSHM", body.get("QSHM"));
				parameters.put("JLXH", Long.parseLong(body.get("JLXH") + ""));
				parameters.put("JGID", jgid);
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
				String sql = "ZZHM=:ZZHM and JLXH<>:JLXH and PJLX=2 and JGID=:JGID";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("ZZHM", body.get("ZZHM"));
				parameters.put("JLXH", Long.parseLong(body.get("JLXH") + ""));
				parameters.put("JGID", jgid);
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
			if (body.get("DQHM") != null) {
				String sql = "DQHM=:DQHM and JLXH<>:JLXH and PJLX=2 and JGID=:JGID";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("DQHM", body.get("DQHM"));
				parameters.put("JLXH", Long.parseLong(body.get("JLXH") + ""));
				parameters.put("JGID", jgid);
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
	public void doPaymentReceiptNumberConflictVerification(
			Map<String, Object> body, Map<String, Object> res,
			String schemaDetailsList, String op, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		if ("create".equals(op)) {
			if (body.get("QSHM") != null && body.get("ZZHM") != null) {
				int l = (body.get("QSHM") + "").length();
				String sql1 = "QSHM<=:QSHM and ZZHM>=:QSHM and PJLX=2 and length(QSHM)=:QSHMLENGTH and JGID=:JGID";
				Map<String, Object> parameters1 = new HashMap<String, Object>();
				parameters1.put("QSHM", body.get("QSHM") + "");
				parameters1.put("QSHMLENGTH", l);
				parameters1.put("JGID", jgid);
				String sql2 = "((QSHM>=:QSHM and QSHM<=:ZZHM) or (ZZHM>=:QSHM and ZZHM<=:ZZHM)) and length(QSHM)=:QSHMLENGTH and PJLX=2 and JGID=:JGID";
				Map<String, Object> parameters2 = new HashMap<String, Object>();
				parameters2.put("QSHM", body.get("QSHM") + "");
				parameters2.put("ZZHM", body.get("ZZHM") + "");
				parameters2.put("QSHMLENGTH", l);
				parameters2.put("JGID", jgid);
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
				String sql1 = "QSHM<=:QSHM and ZZHM>=:QSHM and JLXH<>:JLXH and PJLX=2 and length(QSHM)=:QSHMLENGTH and JGID=:JGID";
				Map<String, Object> parameters1 = new HashMap<String, Object>();
				parameters1.put("QSHM", body.get("QSHM") + "");
				parameters1.put("JLXH", Long.parseLong(body.get("JLXH") + ""));
				parameters1.put("QSHMLENGTH", l);
				parameters1.put("JGID", jgid);
				String sql2 = "((QSHM>=:QSHM and QSHM<=:ZZHM) or (ZZHM>=:QSHM and ZZHM<=:ZZHM)) and length(QSHM)=:QSHMLENGTH and JLXH<>:JLXH and PJLX=2 and JGID=:JGID";
				Map<String, Object> parameters2 = new HashMap<String, Object>();
				parameters2.put("QSHM", body.get("QSHM") + "");
				parameters2.put("ZZHM", body.get("ZZHM") + "");
				parameters2.put("JLXH", Long.parseLong(body.get("JLXH") + ""));
				parameters2.put("QSHMLENGTH", l);
				parameters2.put("JGID", jgid);
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

	public void doQueryJksjCount(Map<String, Object> body,
			Map<String, Object> res, String schemaDetailsList, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		String zyfpsfzcgy = ParameterUtil.getParameter(jgid, "JKSJSFZCGY", ctx);
		parameters.put("JGID", jgid);
		parameters.put("PJLX", 2);
		if ("1".equals(zyfpsfzcgy)) {
			try {
				long l = dao.doCount(schemaDetailsList,
						"JGID=:JGID and PJLX=:PJLX", parameters);
				if (l > 0) {
					res.put(Service.RES_CODE, 607);
					res.put(Service.RES_MESSAGE, "支持公有票据参数已开启,票据号码只能维护一个");
				}
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		}
	}
}
