package phis.application.reg.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.application.ivc.source.TreatmentNumberModule;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class RegistrationDepartmentModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TreatmentNumberModule.class);

	public RegistrationDepartmentModel(BaseDAO dao) {
		this.dao = dao;
	}

	public void registrationDepartmentVerification(Map<String, Object> body,
			Map<String, Object> res, String schemaDetailsList, String op,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		if ("create".equals(op)) {
			String sql = "KSMC=:KSMC and JGID=:JGID";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("KSMC", body.get("KSMC"));
			parameters.put("JGID", jgid);
			try {
				Long l = dao.doCount(schemaDetailsList, sql, parameters);
				if (l > 0) {
					res.put(Service.RES_CODE, 613);
					res.put(Service.RES_MESSAGE, "科室名称已经存在");
				}
			} catch (PersistentDataOperationException e) {
				logger.error("Save failed.", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "科室名称校验失败");
			}
		} else {
			String sql = "KSMC=:KSMC and JGID=:JGID and KSDM<>:KSDM";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("KSMC", body.get("KSMC"));
			parameters.put("KSDM", Long.parseLong(body.get("KSDM") + ""));
			parameters.put("JGID", jgid);
			try {
				Long l = dao.doCount(schemaDetailsList, sql, parameters);
				if (l > 0) {
					res.put(Service.RES_CODE, 613);
					res.put(Service.RES_MESSAGE, "科室名称已经存在");
				}
			} catch (PersistentDataOperationException e) {
				logger.error("Save failed.", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "科室名称校验失败");
			}
		}
	}

	/**
	 * 挂号科室保存
	 * 
	 * @param op
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doSaveRegistrationDepartment(String op,
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String manaUnitId = user.getManageUnitId();// 用户的机构ID
			body.put("MZLB", BSPHISUtil.getMZLB(manaUnitId, dao));
			return dao.doSave(op, BSPHISEntryNames.MS_GHKS, body, false);
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "挂号科室保存失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "挂号科室保存失败");
		}
	}

	public void doRemoveRegistrationDepartment(Object pkey)
			throws ModelDataOperationException {
		// 判断挂号科室是否被分配权限
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("KSDM", Long.parseLong(pkey.toString()));
		try {
			Long l = dao.doCount("GY_QXKZ",
					"KSDM=:KSDM and YWLB=2", parameters);
			if (l > 0) {
				throw new ModelDataOperationException("当前挂号科室已被分配业务权限，不能删除!");
			}
			dao.doRemove(Long.parseLong(pkey.toString()), BSPHISEntryNames.MS_GHKS);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "数据库异常,请联系管理员!");
		}

	}
}
