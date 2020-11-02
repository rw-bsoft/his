package phis.application.sup.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import phis.source.utils.SchemaUtil;

import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class RepairRequestrModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(RepairRequestrModel.class);

	public RepairRequestrModel(BaseDAO dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	public void doSaveform(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> treeMap = (Map<String, Object>) req.get("body");
		String op = req.get("op") + "";
		UserRoleToken user = UserRoleToken.getCurrent();
		try {
			treeMap.put("SYKS", Long.parseLong(treeMap.get("SYKS") + ""));
			treeMap.put("JJCD", Integer.parseInt(treeMap.get("JJCD") + ""));
			treeMap.put("KFXH", Integer.parseInt(treeMap.get("KFXH") + ""));
			treeMap.put("WXZT", -1);
			treeMap.put("JGID",
					Long.parseLong(user.getManageUnit().getId() + ""));
			dao.doSave(op, BSPHISEntryNames.WL_WXBG, treeMap, false);
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败！");
		}
	}

	/**
	 * @description 维修申请验收
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doAcceptance(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> treeMap = (Map<String, Object>) req.get("body");
		UserRoleToken user = UserRoleToken.getCurrent();
		String YGID = user.getUserId() + "";
		try {
			treeMap.put("YSGH", YGID);
			treeMap.put("YSRQ", new Date());
			treeMap.put("WXZT", 3);
			dao.doSave("update", BSPHISEntryNames.WL_WXBG, treeMap, false);
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
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
		long WXXH = Long.parseLong(req.get("body") + "");
		try {
			dao.doRemove(WXXH, BSPHISEntryNames.WL_WXBG);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除失败");
		}
	}

	/**
	 * 删除申领登记信息
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doUpdateWZBGWZXH(Map<String, Object> req,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> parameterswxxh = new HashMap<String, Object>();
		long WXXH = Long.parseLong(req.get("pkey") + "");
		parameterswxxh.put("WXXH", WXXH);
		try {
			dao.doUpdate(
					"update WL_WXBG set WZXH='',CJXH='',ZBXH='',SBXZ='',WXDW='',GZXZ='',GZXX='',GZYY='' where WXXH=:WXXH",
					parameterswxxh);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除失败");
		}
	}

	public void doQueryKs(Context ctx, Map<String, Object> res)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String YGID = user.getUserId() + "";
		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put("YGID", YGID);
		StringBuffer sql_list = new StringBuffer(
				"SELECT DISTINCT a.KSDM as KSDM,b.KSMC as KSMC from WL_HSQX a,GY_KSDM b WHERE a.KSDM = b.KSDM and a.YGID =:YGID and MRZ = 1");
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

	@SuppressWarnings("unchecked")
	public void doSaveWXGLform(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		UserRoleToken user = UserRoleToken.getCurrent();
		long kfxh = Integer
				.parseInt(user.getProperty("treasuryId") == null ? "0" : user
						.getProperty("treasuryId") + "");
		List<Map<String, Object>> wxpjlist = (List<Map<String, Object>>) req
				.get("WL_WXPJ");
		Map<String, Object> parameterswxxh = new HashMap<String, Object>();
		try {
			Double ZJFY = 0.0;
			if (req.get("WXFY") != null) {
				ZJFY = ZJFY + Double.parseDouble(req.get("WXFY") + "");
			}
			if (req.get("CLFY") != null) {
				ZJFY = ZJFY + Double.parseDouble(req.get("CLFY") + "");
			}
			req.put("WXZT", 1);
			req.put("ZJFY", ZJFY);
//			if (req.containsKey("WZXH")) {
//				if (req.get("WZXH") != null) {
					String today = sdf.format(new Date());
					req.put("WXBH", today + req.get("WXXH"));
//				}
//			}
			dao.doSave("update", BSPHISEntryNames.WL_WXBG, req, false);
			parameterswxxh.put("WXXH", Long.parseLong(req.get("WXXH") + ""));
			dao.doUpdate("delete from WL_WXPJ where WXXH=:WXXH", parameterswxxh);
			for (int i = 0; i < wxpjlist.size(); i++) {
				wxpjlist.get(i).put("WXXH",
						Long.parseLong(req.get("WXXH") + ""));
				wxpjlist.get(i).put("KFXH", kfxh);
				dao.doSave("create", BSPHISEntryNames.WL_WXPJ, wxpjlist.get(i),
						false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败！");
		}
	}

	@SuppressWarnings("unchecked")
	public void doQueryWXDJ(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("cnd");
		UserRoleToken user = UserRoleToken.getCurrent();
		int wxzt = Integer.parseInt(body.get("WXZT") + "");
		Map<String, Object> parametersksdm = new HashMap<String, Object>();
		Map<String, Object> parameterswxdj = new HashMap<String, Object>();
		try {
			String formDate = body.get("formDate") + "";
			String endDate = body.get("endDate") + "";
			parametersksdm.put("YGID", user.getUserId());
			List<Map<String, Object>> listksdm = dao
					.doSqlQuery(
							"select a.KSDM as KSDM from WL_HSQX a, SYS_Office b where a.KSDM = b.ID and b.LOGOFF<>1 and a.YGID=:YGID",
							parametersksdm);
			StringBuffer ksdmbuf = new StringBuffer();
			for (int i = 0; i < listksdm.size(); i++) {
				ksdmbuf.append(listksdm.get(i).get("KSDM").toString());
				ksdmbuf.append(",");
			}
			int pageSize = 25;
			if (req.containsKey("pageSize")) {
				pageSize = (Integer) req.get("pageSize");
			}
			int first = 0;
			if (req.containsKey("pageNo")) {
				first = (Integer) req.get("pageNo") - 1;
			}
			StringBuffer sqlwxdj = new StringBuffer(
					"select wl_wxbg0_.WXXH as WXXH, wl_wxbg0_.JGID as JGID, wl_wxbg0_.WXDW as WXDW, wl_wxbg0_.ZBXH as ZBXH, wl_wxbg0_.WXBH as WXBH, wl_wxbg0_.WZXH as WZXH, wl_wxbg0_.CJXH as CJXH, wl_wxbg0_.SYKS as SYKS, wl_wxbg0_.FZGH as FZGH, wl_wxbg0_.SQGH as SQGH, wl_wxbg0_.LXDH as LXDH, wl_wxbg0_.QYRQ as QYRQ, wl_wxbg0_.SXRQ as SXRQ, wl_wxbg0_.GZMS as GZMS, wl_wxbg0_.JJCD as JJCD, wl_wxbg0_.GZXZ as GZXZ, wl_wxbg0_.GZXX as GZXX, wl_wxbg0_.GZYY as GZYY, wl_wxbg0_.GZNR as GZNR, wl_wxbg0_.SBXZ as SBXZ, wl_wxbg0_.WXLB as WXLB, wl_wxbg0_.KFXH as KFXH, wl_wxbg0_.WXRC as WXRC, wl_wxbg0_.KSRQ as KSRQ, wl_wxbg0_.JSRQ as JSRQ, wl_wxbg0_.WXSJ as WXSJ, wl_wxbg0_.WXFY as WXFY, wl_wxbg0_.CLFY as CLFY, wl_wxbg0_.ZJFY as ZJFY, wl_wxbg0_.FPHM as FPHM, wl_wxbg0_.TJRQ as TJRQ, wl_wxbg0_.SHGH as SHGH, wl_wxbg0_.SHRQ as SHRQ, wl_wxbg0_.YSGH as YSGH, wl_wxbg0_.YSRQ as YSRQ, wl_wxbg0_.ZFGH as ZFGH, wl_wxbg0_.ZFRQ as ZFRQ, wl_wxbg0_.WXZT as WXZT, wl_wxbg0_.GLXH as GLXH, wl_wxbg0_.GLLB as GLLB, wl_wxbg0_.BZXX as BZXX, wl_wxbg0_.JSCD as JSCD, wl_wxbg0_.MYCD as MYCD from WL_WXBG wl_wxbg0_");
			sqlwxdj.append(" where wl_wxbg0_.SYKS in (");
			if (ksdmbuf.length() > 0) {
				sqlwxdj.append(ksdmbuf.toString().subSequence(0,
						ksdmbuf.length() - 1));
			} else {
				sqlwxdj.append("");
			}
			sqlwxdj.append(")");
			if (formDate != null && formDate != "") {
				sqlwxdj.append(" and to_char(wl_wxbg0_.SXRQ,'yyyy-mm-dd')>='");
				sqlwxdj.append(formDate);
				sqlwxdj.append("'");
			}
			if (endDate != null && endDate != "") {
				sqlwxdj.append(" and to_char(wl_wxbg0_.SXRQ,'yyyy-mm-dd')<='");
				sqlwxdj.append(endDate);
				sqlwxdj.append("'");
			}
			if (wxzt == -1) {
				sqlwxdj.append(" and wl_wxbg0_.WXZT in (-1, -9)");
			} else {
				sqlwxdj.append(" and wl_wxbg0_.WXZT=");
				sqlwxdj.append(wxzt);
			}
			List<Map<String, Object>> listwxdjsize = dao.doSqlQuery(
					sqlwxdj.toString(), parameterswxdj);
			parameterswxdj.put("first", first * pageSize);
			parameterswxdj.put("max", pageSize);
			List<Map<String, Object>> listwxdj = dao.doSqlQuery(
					sqlwxdj.toString(), parameterswxdj);
			SchemaUtil.setDictionaryMassageForList(listwxdj,
					BSPHISEntryNames.WL_WXBG);
			res.put("totalCount", listwxdjsize.size());
			res.put("body", listwxdj);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void doQueryWXDJINFO(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("cnd");
		UserRoleToken user = UserRoleToken.getCurrent();
		int wxzt = Integer.parseInt(body.get("WXZT") + "");

		String wxbh = null;
		if (body.containsKey("WXBH")) {
			if (body.get("WXBH") != null && body.get("WXBH") != "") {
				wxbh = body.get("WXBH") + "";
			}
		}
		Map<String, Object> parametersksdm = new HashMap<String, Object>();
		Map<String, Object> parameterswxdj = new HashMap<String, Object>();
		try {
			String formDate = body.get("formDate") + "";
			String endDate = body.get("endDate") + "";
			parametersksdm.put("YGID", user.getUserId());
			List<Map<String, Object>> listksdm = dao
					.doSqlQuery(
							"select a.KSDM as KSDM from WL_HSQX a, SYS_Office b where a.KSDM = b.ID and b.LOGOFF<>1 and a.YGID=:YGID",
							parametersksdm);
			StringBuffer ksdmbuf = new StringBuffer();
			for (int i = 0; i < listksdm.size(); i++) {
				ksdmbuf.append(listksdm.get(i).get("KSDM").toString());
				ksdmbuf.append(",");
			}
			int pageSize = 25;
			if (req.containsKey("pageSize")) {
				pageSize = (Integer) req.get("pageSize");
			}
			int first = 0;
			if (req.containsKey("pageNo")) {
				first = (Integer) req.get("pageNo") - 1;
			}
			StringBuffer sqlwxdj = new StringBuffer(
					"select wl_wxbg0_.WXXH as WXXH, wl_wxbg0_.JGID as JGID, wl_wxbg0_.WXDW as WXDW, wl_wxbg0_.ZBXH as ZBXH, wl_wxbg0_.WXBH as WXBH, wl_wxbg0_.WZXH as WZXH, wl_wxbg0_.CJXH as CJXH, wl_wxbg0_.SYKS as SYKS, wl_wxbg0_.FZGH as FZGH, wl_wxbg0_.SQGH as SQGH, wl_wxbg0_.LXDH as LXDH, wl_wxbg0_.QYRQ as QYRQ, wl_wxbg0_.SXRQ as SXRQ, wl_wxbg0_.GZMS as GZMS, wl_wxbg0_.JJCD as JJCD, wl_wxbg0_.GZXZ as GZXZ, wl_wxbg0_.GZXX as GZXX, wl_wxbg0_.GZYY as GZYY, wl_wxbg0_.GZNR as GZNR, wl_wxbg0_.SBXZ as SBXZ, wl_wxbg0_.WXLB as WXLB, wl_wxbg0_.KFXH as KFXH, wl_wxbg0_.WXRC as WXRC, wl_wxbg0_.KSRQ as KSRQ, wl_wxbg0_.JSRQ as JSRQ, wl_wxbg0_.WXSJ as WXSJ, wl_wxbg0_.WXFY as WXFY, wl_wxbg0_.CLFY as CLFY, wl_wxbg0_.ZJFY as ZJFY, wl_wxbg0_.FPHM as FPHM, wl_wxbg0_.TJRQ as TJRQ, wl_wxbg0_.SHGH as SHGH, wl_wxbg0_.SHRQ as SHRQ, wl_wxbg0_.YSGH as YSGH, wl_wxbg0_.YSRQ as YSRQ, wl_wxbg0_.ZFGH as ZFGH, wl_wxbg0_.ZFRQ as ZFRQ, wl_wxbg0_.WXZT as WXZT, wl_wxbg0_.GLXH as GLXH, wl_wxbg0_.GLLB as GLLB, wl_wxbg0_.BZXX as BZXX, wl_wxbg0_.JSCD as JSCD, wl_wxbg0_.MYCD as MYCD from WL_WXBG wl_wxbg0_");
			sqlwxdj.append(" where wl_wxbg0_.SYKS in (");
			if (ksdmbuf.length() > 0) {
				sqlwxdj.append(ksdmbuf.toString().subSequence(0,
						ksdmbuf.length() - 1));
			} else {
				sqlwxdj.append("");
			}
			sqlwxdj.append(")");
			if (formDate != null && formDate != "") {
				sqlwxdj.append(" and to_char(wl_wxbg0_.SXRQ,'yyyy-mm-dd')>='");
				sqlwxdj.append(formDate);
				sqlwxdj.append("'");
			}
			if (endDate != null && endDate != "") {
				sqlwxdj.append(" and to_char(wl_wxbg0_.SXRQ,'yyyy-mm-dd')<='");
				sqlwxdj.append(endDate);
				sqlwxdj.append("'");
			}
			sqlwxdj.append(" and wl_wxbg0_.WXZT=");
			sqlwxdj.append(wxzt);
			if (wxbh != null) {
				sqlwxdj.append(" and wl_wxbg0_.WXBH=");
				sqlwxdj.append(wxbh);
			}
			List<Map<String, Object>> listwxdjsize = dao.doSqlQuery(
					sqlwxdj.toString(), parameterswxdj);
			parameterswxdj.put("first", first * pageSize);
			parameterswxdj.put("max", pageSize);
			List<Map<String, Object>> listwxdj = dao.doSqlQuery(
					sqlwxdj.toString(), parameterswxdj);
			SchemaUtil.setDictionaryMassageForList(listwxdj,
					BSPHISEntryNames.WL_WXBG);
			res.put("totalCount", listwxdjsize.size());
			res.put("body", listwxdj);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设备维修状况 form
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQuerySbForm(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Long pkey = Long.parseLong(req.get("pkey") + "");
		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			String sql = "select b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,c.GHDW as GHDW,e.DWMC as DWMC,d.CJMC as CJMC,c.WZXH as WZXH,"
					+ " c.CJXH as CJXH,a.WXDW as WXDW,a.SBXZ as SBXZ,a.GZXZ as GZXZ,a.GZXX as GZXX,a.GZYY as GZYY,c.WZBH as WZBH "
					+ " from WL_GHDW e , WL_SCCJ d, WL_ZCZB c,WL_WZZD b left outer join WL_WXBG a on a.wzxh= b.wzxh"
					+ " where e.DWXH = c.GHDW and b.WZXH = c.WZXH and c.CJXH = d.CJXH and c.WZZT = 1 and a.WXXH =:WXXH";
			map_par.put("WXXH", pkey);
			List<Map<String, Object>> sbList = dao.doSqlQuery(sql, map_par);
			Map<String, Object> SbForm = new HashMap<String, Object>();
			if (sbList.size() > 0) {
				SbForm = sbList.get(0);
			}
			SchemaUtil.setDictionaryMassageForForm(SbForm,
					BSPHISEntryNames.WL_WXBG_SBWX_FORM);
			res.put("body", SbForm);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "设备信息查询失败");

		}
	}

	public void doQueryWXDW(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Long wxxh = Long.parseLong(req.get("WXXH") + "");
		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			String sql = "select WZXH as WZXH,WXDW as WXDW from WL_WXBG where WXXH =:WXXH";
			map_par.put("WXXH", wxxh);
			Map<String, Object> wxdwMap = dao.doLoad(sql, map_par);
			long wxdw = 0;
			long wzxh = 0;
			if (wxdwMap != null) {
				if (wxdwMap.containsKey("WXDW")) {
					if (wxdwMap.get("WXDW") != null) {
						wxdw = Long.parseLong(wxdwMap.get("WXDW") + "");
					}
				}
			}
			if (wxdwMap != null) {
				if (wxdwMap.containsKey("WZXH")) {
					if (wxdwMap.get("WZXH") != null) {
						wzxh = Long.parseLong(wxdwMap.get("WZXH") + "");
					}
				}
			}
			res.put("wzxh", wzxh);
			res.put("wxdw", wxdw);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "设备信息查询失败");

		}
	}
}
