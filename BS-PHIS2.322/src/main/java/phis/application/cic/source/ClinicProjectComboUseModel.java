package phis.application.cic.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;

public class ClinicProjectComboUseModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ClinicProjectComboUseModel.class);

	public ClinicProjectComboUseModel(BaseDAO dao) {
		this.dao = dao;
	}

	public void ClinicProjectComboUseVerification(Map<String, Object> body,
			Map<String, Object> res, String op, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		if ("create".equals(op)) {
			String sql = "ZDMC=:ZDMC and YGDM=:YGDM and JGID=:JGID and SSLB=:SSLB";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ZDMC", body.get("ZDMC"));
			parameters.put("YGDM", body.get("YGDM"));
			parameters.put("JGID", body.get("JGID"));
			parameters.put("SSLB",
					Integer.parseInt(body.get("SSLB").toString()));
			try {
				Long l = dao.doCount("GY_CYZD", sql, parameters);
				if (l > 0) {
					res.put(Service.RES_CODE, 613);
					res.put(Service.RES_MESSAGE, "诊断名称已经存在");
				}
			} catch (PersistentDataOperationException e) {
				logger.error("Save failed.", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "诊断名称校验失败");
			}
		} else {
			String sql = "ZDMC=:ZDMC and YGDM=:YGDM and JGID=:JGID and SSLB=:SSLB and JLBH<>:JLBH";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ZDMC", body.get("ZDMC"));
			parameters.put("YGDM", body.get("YGDM"));
			parameters.put("JGID", body.get("JGID"));
			parameters.put("SSLB",
					Integer.parseInt(body.get("SSLB").toString()));
			parameters.put("JLBH", body.get("JLBH"));
			try {
				Long l = dao.doCount("GY_CYZD", sql, parameters);
				if (l > 0) {
					res.put(Service.RES_CODE, 613);
					res.put(Service.RES_MESSAGE, "诊断名称已经存在");
				}
			} catch (PersistentDataOperationException e) {
				logger.error("Save failed.", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "诊断名称校验失败");
			}
		}
	}

	public void doQueryProjectComboList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {

		int CFLX = Integer.parseInt(req.get("CFLX") + "");

		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}

		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		try { // 如果是西药 查询GY_JBBM 这个表 如果是中医查询EMR_ZYJB表
			StringBuffer sql_list = new StringBuffer();
			StringBuffer Sql_count = new StringBuffer();
			if (CFLX == 1) {
				sql_list = new StringBuffer(
						"select a.JBXH as JBXH,a.ICD9 as ICD9,a.ICD10 as ICD10,a.DMLB as DMLB,a.JBMC as JBMC,a.JBLB as JBLB,a.PYDM as PYDM,a.WBDM as WBDM,a.JBPB as JBPB from GY_JBBM a");
				Sql_count = new StringBuffer(
						"SELECT COUNT(*) as NUM FROM GY_JBBM a ");
			} else if (CFLX == 2) {
				sql_list = new StringBuffer(
						"select a.JBBS as JBXH,a.JBDM as ICD10,a.JBMC as JBMC,a.PYDM as PYDM,a.WBDM as WBDM from EMR_ZYJB a ");
				Sql_count = new StringBuffer(
						"SELECT COUNT(*) as NUM FROM EMR_ZYJB a ");
			}

			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String parString = "";
				String parName = "";
				if (que[4].indexOf("]") == -1) {
					parString = que[5].substring(0, que[5].indexOf("]")).trim();
				} else {
					parName = que[2].substring(que[2].indexOf(".") + 1,
							que[2].indexOf("]")).trim();
					if (CFLX == 2 && parName.equals("ICD10")) {
						parName = "JBDM";
					}
					parString = que[4].substring(0, que[4].indexOf("]")).trim();
				}
				String qur = " where " + parName + " like '" + parString + "'";

				sql_list.append(qur);
				Sql_count.append(qur);
			}

			List<Map<String, Object>> coun = dao.doSqlQuery(
					Sql_count.toString(), parameters);
			int total = Integer.parseInt(coun.get(0).get("NUM") + "");

			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);

			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			if (CFLX == 1) {
				SchemaUtil.setDictionaryMassageForList(inofList,
						"phis.application.cic.schemas.GY_JBBM_CY");
			} else if (CFLX == 2) {
				SchemaUtil.setDictionaryMassageForList(inofList,
						"phis.application.emr.schemas.EMR_ZYJB_ZD");
			}
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败！");
		}
	}

	public void doQueryInCommonUseInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String ygdm = user.getUserId() + "";// 暂时员工代码全部使用该方法替代
		String jgid = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("YGDM", ygdm);
		parameters.put("JGID", jgid);
		parameters.put("SSLB", 1);
		int cflx = Integer.parseInt(req.get("CFLX") + "");
		parameters.put("CFLX", cflx);
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}

		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}
		try { // 如果是西药 查询GY_JBBM 这个表 如果是中医查询EMR_ZYJB表
			StringBuffer sql_list = new StringBuffer();
			StringBuffer Sql_count = new StringBuffer();
			if (cflx == 1) {
				sql_list = new StringBuffer(
						"select a.JLBH as JLBH,a.SSLB as SSLB,a.YGDM as YGDM,a.KSDM as KSDM,a.ZDMC as ZDMC,a.ZDXH as ZDXH,a.ICD10 as ICD10,a.PYDM as PYDM,b.JBPB as JBPB,a.WBDM as WBDM,a.CFLX as CFLX from GY_CYZD a,GY_JBBM b where a.ZDXH=b.JBXH and a.CFLX=:CFLX and a.SSLB=:SSLB and a.JGID=:JGID and a.YGDM=:YGDM");
				Sql_count = new StringBuffer(
						"SELECT COUNT(*) as NUM FROM GY_JBBM a ");
			} else if (cflx == 2) {
				sql_list = new StringBuffer(
						"select a.JLBH as JLBH,a.SSLB as SSLB,a.YGDM as YGDM,a.KSDM as KSDM,a.ZDMC as ZDMC,a.ZDXH as ZDXH,a.ICD10 as ICD10,a.PYDM as PYDM,'' as JBPB,a.WBDM as WBDM,a.CFLX as CFLX from GY_CYZD a,EMR_ZYJB b where a.ZDXH=b.JBBS and a.CFLX=:CFLX and a.SSLB=:SSLB and a.JGID=:JGID and a.YGDM=:YGDM");
				Sql_count = new StringBuffer(
						"SELECT COUNT(*) as NUM FROM EMR_ZYJB a ");
			}

			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String parString = "";
				String parName = "";
				if (que[4].indexOf("]") == -1) {
					parString = que[5].substring(0, que[5].indexOf("]")).trim();
				} else {
					parName = que[2].substring(que[2].indexOf(".") + 1,
							que[2].indexOf("]")).trim();
					parString = que[4].substring(0, que[4].indexOf("]")).trim();
				}
				sql_list.append(" and a." + parName + " like '" + parString
						+ "'");
				Sql_count.append(" where " + parName + " like '" + parString
						+ "'");
			}
			List<Map<String, Object>> coun = dao.doSqlQuery(
					Sql_count.toString(), null);
			int total = Integer.parseInt(coun.get(0).get("NUM") + "");

			/************modify by zhaojian 2017-05-19 增加判断，解决常用诊断保存时被覆盖问题**********************************/
			if (first > 0 && pageSize > 0) {
				parameters.put("first", first * pageSize);
				parameters.put("max", pageSize);
			}

			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			SchemaUtil.setDictionaryMassageForList(inofList,
					"phis.application.cic.schemas.GY_CYZD_CIC");
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败！");
		}
	}
}
