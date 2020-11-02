package phis.application.sup.source;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class ApplyRegisterModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(ApplyRegisterModel.class);

	public ApplyRegisterModel(BaseDAO dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	public void doSaveform(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> treeMap = (Map<String, Object>) req.get("body");
		String op = req.get("op") + "";
		UserRoleToken user = UserRoleToken.getCurrent();
		String KFXH = user.getProperty("treasuryId").toString();
		String userId = user.getUserId();
		try {
			if ("create".equals(op)) {
				String sql = "CKKF =:CKKF and WZXH =:WZXH and SLZT in(-1,-9) and SLKS=:SLKS";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("CKKF", Integer.parseInt(KFXH));
				parameters
						.put("WZXH", Long.parseLong(treeMap.get("WZXH") + ""));
				parameters
						.put("SLKS", Long.parseLong(treeMap.get("SLKS") + ""));

				Long l = dao.doCount("WL_SLXX", sql, parameters);
				if (l > 0) {
					res.put(Service.RES_CODE, 613);
					res.put(Service.RES_MESSAGE, "物资已存在");
				} else {
					parameters.put("WZGG", treeMap.get("WZGG"));
					parameters.put("WZDW", treeMap.get("WZDW"));
					parameters.put("WZMC", treeMap.get("WZMC"));
					parameters.put("WZSL", treeMap.get("WZSL"));

					parameters.put("ZBLB", treeMap.get("ZBLB"));
					parameters.put("GLFS", treeMap.get("GLFS"));
					parameters.put("KFXH", treeMap.get("KFXH"));
					parameters.put("CKKF", KFXH);

					parameters.put("SLSJ", new Date());
					parameters.put("SLGH", userId);
					parameters.put("SLKS", treeMap.get("SLKS"));
					parameters.put("JGID", treeMap.get("JGID"));

					if (treeMap.get("BZXX") != null
							|| treeMap.get("BZXX") != "") {
						parameters.put("BZXX", treeMap.get("BZXX"));
					}

					parameters.put("DJXH", 0);
					parameters.put("SLZT", -1);
					parameters.put("JHBZ", 0);
					dao.doSave(op, BSPHISEntryNames.WL_SLXX, parameters, false);
				}
			} else {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("KFXH",
						Integer.parseInt(treeMap.get("KFXH") + ""));
				parameters
						.put("WZXH", Long.parseLong(treeMap.get("WZXH") + ""));
				parameters
						.put("SLKS", Long.parseLong(treeMap.get("SLKS") + ""));
				StringBuffer sql_list = new StringBuffer(
						"SELECT DISTINCT JLXH as JLXH from WL_SLXX  where KFXH =:KFXH and WZXH =:WZXH and SLZT in(-1,-2) and SLKS=:SLKS");
				List<Map<String, Object>> queryList = dao.doQuery(
						sql_list.toString(), parameters);
				int num = 0;
				if (queryList.size() > 0) {
					for (int i = 0; i < queryList.size(); i++) {
						long JLXH = Long.parseLong(queryList.get(i).get("JLXH")
								+ "");
						if (JLXH == Long.parseLong(treeMap.get("JLXH") + "")) {
							num = num + 1;
						}
					}
				}
				if ((num == 1 && queryList.size() > 1)
						|| (num == 0 && queryList.size() > 0)) {
					res.put(Service.RES_CODE, 613);
					res.put(Service.RES_MESSAGE, "物资已存在");
				} else {
					Map<String, Object> perMap = new HashMap<String, Object>();

					int KFXH1 = Integer.parseInt(treeMap.get("KFXH") + "");
					if (KFXH1 != 0) {
						perMap.put("KFXH", KFXH1);
					}

					perMap.put("WZGG", treeMap.get("WZGG"));
					perMap.put("WZDW", treeMap.get("WZDW"));
					perMap.put("JLXH", treeMap.get("JLXH"));
					perMap.put("WZMC", treeMap.get("WZMC"));
					perMap.put("WZSL", treeMap.get("WZSL"));
					perMap.put("BZXX", treeMap.get("BZXX"));
					perMap.put("SLKS", treeMap.get("SLKS"));
					dao.doSave(op, BSPHISEntryNames.WL_SLXX, perMap, false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败！");
		}
	}

	/**
	 * 删除申领登记信息
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doRemove(Map<String, Object> req, Map<String, Object> res)
			throws ModelDataOperationException {
		long JLXH = Long.parseLong(req.get("body") + "");
		try {
			dao.doRemove(JLXH, BSPHISEntryNames.WL_SLXX);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doQueryYksl(Map<String, Object> req, Map<String, Object> res)
			throws ModelDataOperationException {
		Map<String, Object> reqMap = (Map<String, Object>) req.get("body");

		StringBuffer sql_list = new StringBuffer(
				"SELECT DISTINCT sum(t.WZSL - t.YKSL ) as YKSL  from WL_WZKC t where t.KFXH =:KFXH and t.WZXH =:WZXH");
		List<Map<String, Object>> inofList = new ArrayList<Map<String, Object>>();
		try {
			inofList = dao.doSqlQuery(sql_list.toString(), reqMap);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败");
		}
		if (inofList.size() <= 0) {
			res.put("ret", 0);
		} else {
			res.put("ret", inofList.get(0).get("YKSL"));
		}
	}

	public String loadSystemParams(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String kfxh = user.getProperty("treasuryId").toString();
		String jgid = user.getManageUnit().getId();

		String ejslkz = ParameterUtil.getParameter(jgid, "EJSLKZ" + kfxh, "1",
				"二级库房向一级库房发起申领时，申领数量是否可以超过一级库房的库存数量。1控制，0不控制", ctx);

		return ejslkz;
	}

	public String doQueryWzkf(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		String WZXH = body.get("WZXH") + "";
		String resKFXH = "0";

		Map<String, Object> reqMap = new HashMap<String, Object>();

		reqMap.put("WZXH", Long.parseLong(WZXH));
		reqMap.put("JGID", jgid);
		StringBuffer sql_list = new StringBuffer(
				"SELECT DISTINCT t1.KFMC as KFMC from WL_WZGS t,WL_KFXX t1 where t.KFXH = t1.KFXH and t.WZXH =:WZXH and t.JGID =:JGID");
		List<Map<String, Object>> inofList = new ArrayList<Map<String, Object>>();
		try {
			inofList = dao.doSqlQuery(sql_list.toString(), reqMap);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败");
		}
		if (inofList.get(0).get("KFMC") != null
				|| inofList.get(0).get("KFMC") != "") {
			resKFXH = inofList.get(0).get("KFMC") + "";
		}

		return resKFXH;
	}

	public void doQueryKs(Context ctx, Map<String, Object> res)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String YGID = (String) user.getUserId();
		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put("YGID", YGID);
		StringBuffer sql_list = new StringBuffer(
				"SELECT DISTINCT a.KSDM as KSDM,b.OFFICENAME as KSMC from WL_HSQX a,SYS_Office b WHERE a.KSDM = b.ID and a.YGID =:YGID and MRZ = 1");
		List<Map<String, Object>> inofList = new ArrayList<Map<String, Object>>();
		try {
			inofList = dao.doSqlQuery(sql_list.toString(), reqMap);
			if (inofList.size() > 0) {
				res.put("ret", inofList.get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败");
		}

	}

	public void doUpdateSlZT(List<Map<String, Object>> bodys, Context ctx)
			throws ModelDataOperationException {
		for (int i = 0; i < bodys.size(); i++) {
			Map<String, Object> parMap = new HashMap<String, Object>();
			long JLXH = Long.parseLong(bodys.get(i).get("JLXH") + "");
			parMap.put("JLXH", JLXH);
			parMap.put("SLZT", 0);
			try {
				dao.doUpdate("update WL_SLXX set SLZT=:SLZT where JLXH=:JLXH",
						parMap);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "提交失败");
			}
		}
	}
}
