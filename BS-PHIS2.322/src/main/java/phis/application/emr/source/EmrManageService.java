/**
 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.emr.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import phis.application.cfg.source.CaseHistoryControlModel;
import phis.source.utils.CNDHelper;
import phis.source.utils.SchemaUtil;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.ServiceCode;

import ctd.account.UserRoleToken;
import ctd.sequence.exception.KeyManagerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;

//import ctd.util.keyManager.KeyManagerException;

/**
 * @description 电子病历业务
 * 
 * @author yangl</a>
 */
public class EmrManageService extends AbstractActionService implements
		DAOSupportable {

	/**
	 * 获取后台业务表主键
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadKey(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		String table = ((Map<String, Object>) req.get("body")).get("schema")
				.toString();
		EmrManageModel cdm = new EmrManageModel(dao);
		try {
			if (table.equals("EMR_BL01")) {
				res.put("key", cdm.getBl01Key(ctx));
			} else if (table.equals("OMR_BL01")) {
				res.put("key", cdm.getOMR_Bl01Key(ctx));
			} else if (table.equals("EMR_YXBDS_BL")) {
				res.put("key", cdm.getYXBDSKey(ctx));
			}
		} catch (KeyManagerException e) {
			throw new ServiceException("获取病历编号错误!", e);
		}

	}

	@SuppressWarnings("unchecked")
	public void doLoadParaRefItems(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		EmrManageModel cdm = new EmrManageModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			res.put("body", cdm.doLoadParaRefItems(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}

	}

	public void doLoadJkys(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		EmrManageModel cdm = new EmrManageModel(dao);
		try {
			res.put("body", cdm.doLoadJkys());
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}

	}

	@SuppressWarnings("unchecked")
	public void doSavePrintLog(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		EmrManageModel cdm = new EmrManageModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			cdm.doSavePrintLog(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}

	}

	@SuppressWarnings("unchecked")
	public void doLoadYxbds(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cdm = new EmrManageModel(dao);
		try {
			res.put("body", cdm.doLoadYxbds(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}

	}

	/**
	 * 加载病历类别数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadBllb(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cdm = new EmrManageModel(dao);
		try {
			res.put("BLLB", cdm.doLoadBllb(body, ctx));

		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doLoadNavTree(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cdm = new EmrManageModel(dao);
		try {
			Map<String, Object> rsMap = cdm.doLoadNavTree(body, ctx);
			res.put("emrTree", rsMap.get("EMR"));
			res.put("ehrTree", rsMap.get("EHR"));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 门诊emr导航
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadEMRNavTree(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cdm = new EmrManageModel(dao);
		try {
			Map<String, Object> rsMap = cdm.doLoadEMRNavTree(body, ctx);
			res.put("emrTree", rsMap.get("EMR"));
			res.put("ehrTree", rsMap.get("EHR"));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveYxbds(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cdm = new EmrManageModel(dao);
		try {
			cdm.doSaveYxbds(body, ctx);
			req.put(RES_CODE, ServiceCode.CODE_OK);
			req.put(RES_MESSAGE, "Success");
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveEmrLockManage(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cdm = new EmrManageModel(dao);
		try {
			cdm.doSaveEmrLockManage(body, ctx);
			req.put(RES_CODE, ServiceCode.CODE_OK);
			req.put(RES_MESSAGE, "Success");
		} catch (ModelDataOperationException e) {
			if (e.getCode() == 515) {
				res.put("unsign", true);
			}
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveZkks(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cdm = new EmrManageModel(dao);
		try {
			cdm.doSaveZkks(body, ctx);
			req.put(RES_CODE, ServiceCode.CODE_OK);
			req.put(RES_MESSAGE, "Success");
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doRemoveEmrData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cdm = new EmrManageModel(dao);
		try {
			res.putAll(cdm.doRemoveEmrData(body, ctx));
			req.put(RES_CODE, ServiceCode.CODE_OK);
			req.put(RES_MESSAGE, "Success");
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doRemoveOmrData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cdm = new EmrManageModel(dao);
		try {
			res.putAll(cdm.doRemoveOmrData(body, ctx));
			req.put(RES_CODE, ServiceCode.CODE_OK);
			req.put(RES_MESSAGE, "Success");
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 载入病历模版
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("rawtypes")
	public void doLoadBlmb(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		List queryCnd = null;
		String openBy = (String) req.get("openBy"); // 家床
		int zymz = 0;
		if (openBy != null && openBy.equals("FSB")) {
			zymz = 2;
		}

		StringBuffer cnds = new StringBuffer(
				"['and',['and',['and',['eq',['$','TYBZ'],['i',0]],['or',['eq',['$','YMBZ'],['i',0]],['isNull',['$','YMBZ']]]],['eq',['$','ZYMZ'],['i',"
						+ zymz + "]]],['eq',['$','ZKDL'],['i',0]]]");
		if (req.containsKey("cnd")) {
			queryCnd = (List) req.get("cnd");
		}
		String schema = (String) req.get("schema");
		String t_tableName = "chtemplate";
		if (req.get("BLLX") != null && "1".equals(req.get("BLLX").toString())) {
			t_tableName = "chrecordtemplate";
		}
		if (schema.equals("phis.application.emr.schemas.V_EMR_BLMB_PRI")) {
			UserRoleToken user = UserRoleToken.getCurrent();
			cnds.insert(0, "['and',").append(
					",['ne',['$','b.PTSTATE'],['i',9]]]");
			cnds.insert(0, "['and',").append(
					",['or',['and',['eq',['$','b.SPTTYPE'],['i',1]],['eq',['$','b.SPTCODE'],['s','"
							+ user.getUserId() + "']]]");
			cnds.append(",['and',['eq',['$','b.SPTTYPE'],['i',0]],['eq',['$','b.SPTCODE'],['s',"
					+ req.get("KSDM") + "]]]]]");
			cnds.insert(0, "['and',")
					.append(",['or',['eq',['$','b.PTTYPE'],['i',0]],['eq',['$','b.PTTYPE'],['i',1]]]]");
			cnds.insert(0, "['and',").append(
					",['eq',['$','b.JGID'],['s'," + user.getManageUnitId()
							+ "]]]");
			t_tableName = "";
		}
		String queryCndsType = null;
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String sortInfo = null;
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 1;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		try {
			Object Bllb = req.get("BLLB");
			Integer Mblb = 0;
			if (req.containsKey("MBLB")) {
				Mblb = Integer.parseInt(req.get("MBLB").toString());
			}
			if (Bllb != null) {
				cnds.insert(0, "['and',").append(
						",['eq',['$','BLLB'],['d'," + Bllb + "]]]");
			}
			if (Mblb > 0) {
				cnds.insert(0, "['and',").append(
						",['eq',['$','MBLB'],['s'," + Mblb + "]]]");
			}
			if (!schema.equals("phis.application.emr.schemas.V_EMR_BLMB_PRI")
					&& zymz == 0) {
				String ssks = req.get("SSKS") + "";
				if (!"null".equals(ssks)) {
					// List<?> l = getSszk(ssks, dao, ctx);
					if (ssks.length() > 0) {
						cnds.insert(0, "['and',")
								.append(",['eq',['$','SYS_OFFICE_ID'],['d',"
										+ ssks
										+ "]],['eq',['$','T_TABLE_NAME'],['s','"
										+ t_tableName + "']]]");
					}
				} else {
					// schema = "phis.application.emr.schemas.V_EMR_BLMB";
					cnds.insert(0, "['and',").append(
							",['eq',['$','SYS_OFFICE_ID'],['d'," + ssks
									+ "]],['eq',['$','T_TABLE_NAME'],['s','"
									+ t_tableName + "']]]");
				}
			}
			if (queryCnd != null) {
				queryCnd = CNDHelper.createArrayCnd("and", queryCnd,
						CNDHelper.toListCnd(cnds.toString()));
			} else {
				queryCnd = CNDHelper.toListCnd(cnds.toString());
			}
			res.putAll(dao.doList(queryCnd, sortInfo, schema, pageNo, pageSize,
					queryCndsType));
		} catch (HibernateException e) {
			throw new ServiceException("获取病历模版数据异常", e);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException("获取病历模版数据异常", e);
		} catch (ExpException e) {
			throw new ServiceException("获取病历模版数据异常", e);
		}
	}

	/**
	 * 载入门诊病历模版
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("rawtypes")
	public void doLoadMZBlmb(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		List queryCnd = null;
		StringBuffer cnds = new StringBuffer(
				"['and',['and',['and',['eq',['$','TYBZ'],['i',0]],['or',['eq',['$','YMBZ'],['i',0]],['isNull',['$','YMBZ']]]],['eq',['$','ZYMZ'],['i',1]]],['eq',['$','ZKDL'],['i',0]]]");
		if (req.containsKey("cnd")) {
			queryCnd = (List) req.get("cnd");
		}
		String schema = (String) req.get("schema");
		if (schema.equals("phis.application.emr.schemas.V_EMR_BLMB_PRI")) {
			UserRoleToken user = UserRoleToken.getCurrent();
			cnds.insert(0, "['and',").append(
					",['ne',['$','b.PTSTATE'],['i',9]]]");
			cnds.insert(0, "['and',").append(
					",['or',['and',['eq',['$','b.SPTTYPE'],['i',1]],['eq',['$','b.SPTCODE'],['s','"
							+ user.getUserId() + "']]]");
			cnds.append(",['and',['eq',['$','b.SPTTYPE'],['i',0]],['eq',['$','b.SPTCODE'],['s',"
					+ req.get("KSDM") + "]]]]]");
			cnds.insert(0, "['and',")
					.append(",['or',['eq',['$','b.PTTYPE'],['i',0]],['eq',['$','b.PTTYPE'],['i',1]]]]");
			cnds.insert(0, "['and',").append(
					",['eq',['$','b.JGID'],['s'," + user.getManageUnitId()
							+ "]]]");
		} else {
			String ssks = req.get("SSKS") + "";
			if (!"null".equals(ssks)) {
				// List<?> l = getSszk(ssks, dao, ctx);
				if (ssks.length() > 0) {
					cnds.insert(0, "['and',")
							.append(",['eq',['$','SYS_OFFICE_ID'],['d',"
									+ ssks
									+ "]],['eq',['$','T_TABLE_NAME'],['s','chtemplate']]]");
				}
			} else {
				// schema = "phis.application.emr.schemas.V_EMR_BLMB";
				cnds.insert(0, "['and',")
						.append(",['eq',['$','SYS_OFFICE_ID'],['d',"
								+ ssks
								+ "]],['eq',['$','T_TABLE_NAME'],['s','chtemplate']]]");
			}
		}
		String queryCndsType = null;
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String sortInfo = null;
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 1;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		try {
			Object Bllb = req.get("BLLB");
			Integer Mblb = 0;
			if (req.containsKey("MBLB")) {
				Mblb = Integer.parseInt(req.get("MBLB").toString());
			}
			if (Bllb != null) {
				cnds.insert(0, "['and',").append(
						",['eq',['$','BLLB'],['d'," + Bllb + "]]]");
			}
			if (Mblb > 0) {
				cnds.insert(0, "['and',").append(
						",['eq',['$','MBLB'],['d'," + Mblb + "]]]");
			}
			if (queryCnd != null) {
				queryCnd = CNDHelper.createArrayCnd("and", queryCnd,
						CNDHelper.toListCnd(cnds.toString()));
			} else {
				queryCnd = CNDHelper.toListCnd(cnds.toString());
			}
			res.putAll(dao.doList(queryCnd, sortInfo, schema, pageNo, pageSize,
					queryCndsType));
		} catch (HibernateException e) {
			throw new ServiceException("获取病历模版数据异常", e);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException("获取病历模版数据异常", e);
		} catch (ExpException e) {
			throw new ServiceException("获取病历模版数据异常", e);
		}
	}

	public List<?> getSszk(Object ssks, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException {
		// Map<String, Object> params = new HashMap<String, Object>();
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		// params.put("KSDM", Long.parseLong(ssks.toString()));
		String hql = "select ZKFL as ZKFL from EMR_ZKKS where KSDM=:KSDM";
		return ss.createSQLQuery(hql)
				.setLong("KSDM", Long.parseLong(ssks.toString())).list();
		// return dao.doQuery(hql, params);
	}

	/**
	 * 载入个人病历模版
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadPersonalBlmb(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<Object> cnd = (List<Object>) req.get("cnd");
		UserRoleToken user = UserRoleToken.getCurrent();
		StringBuffer hql = new StringBuffer(
				"SELECT a.TEMPLATECODE,a.PTNAME,a.TEMPLATETYPE,a.FRAMEWORKCODE,a.SPTTYPE,a.PTID,a.PTTYPE,a.SPTCODE,a.REGISTRAR,a.PTSTATE,a.DISEASEID,a.DISEASETYPE,a.SJBZ,a.BZMBBH,");
		hql.append("c.JBMC as DISEASENAME,CASE WHEN PTTYPE = 1 THEN 1 WHEN PTTYPE = 3 then 3 ELSE 0 END as RECORDSTYLE,b.MBMC,b.SSKS,0 as TBBZ ");
		hql.append("FROM V_EMR_BLMB b, PRIVATETEMPLATE a left join GY_JBBM c on  a.DISEASEID = c.JBXH WHERE b.MBBH = a.TEMPLATECODE AND b.MBFL = a.PTTYPE + 1  AND a.REGISTRAR = :UID");
		if (cnd != null && cnd.size() > 0) {
			String where = null;
			try {
				where = ExpRunner.toString(cnd, ctx);
			} catch (ExpException e) {
				throw new ServiceException("获取病历模版数据异常", e);
			}
			hql.append(" AND ").append(where);
		}
		hql.append(" union All ");
		hql.append("SELECT a.TEMPLATECODE,a.PTNAME,a.TEMPLATETYPE,a.FRAMEWORKCODE,a.SPTTYPE,a.PTID,a.PTTYPE,a.SPTCODE,a.REGISTRAR, a.PTSTATE,a.DISEASEID,a.DISEASETYPE,a.SJBZ,a.BZMBBH,");
		hql.append("c.JBMC as DISEASENAME,CASE WHEN PTTYPE = 1 THEN 1 WHEN PTTYPE = 3 then 3 ELSE 0 END as RECORDSTYLE,'' as MBMC,'' as SSKS,0 as TBBZ ");
		hql.append("  FROM PRIVATETEMPLATE a left join GY_JBBM c on  a.DISEASEID = c.JBXH  WHERE a.PTTYPE = 3 AND a.REGISTRAR = :UID");
		try {
			if (cnd != null && cnd.size() > 0) {
				String where = null;
				try {
					where = ExpRunner.toString(cnd, ctx);
				} catch (ExpException e) {
					throw new ServiceException("获取病历模版数据异常", e);
				}
				hql.append(" AND ").append(where);
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("UID", (String) user.getUserId());
			List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(),
					parameters);
			for (Map<String, Object> m : list) {
				m = SchemaUtil.setDictionaryMassageForList(m,
						BSPHISEntryNames.PRIVATETEMPLATE_MNG);
			}
			res.put("body", list);
		} catch (HibernateException e) {
			throw new ServiceException("获取病历模版数据异常", e);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException("获取病历模版数据异常", e);
			// } catch (ExpException e) {
			// throw new ServiceException("获取病历模版数据异常", e);
		}
	}

	/**
	 * 注销个人模版
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveCancellationMode(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String pkey = req.get("pkey").toString();
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.saveCancellationMode(pkey);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("注销个人模版失败!", e);
		}
	}

	/**
	 * 恢复个人模版
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveRenewMode(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String pkey = req.get("pkey").toString();
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.saveRenewMode(pkey);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("恢复个人模版失败!", e);
		}
	}

	/**
	 * 修改个人模版名称
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveChangeModeRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<Map<String, Object>> records = (List<Map<String, Object>>) req
				.get("records");
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.saveChangeModeRecords(records);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("修改个人模版名称失败!", e);
		}
	}

	/**
	 * 修改个人模版内容
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSavePTTemplateData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String textData = (String) req.get("textData");
		String xmlData = (String) req.get("xmlData");
		String pkey = req.get("pkey").toString();
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.savePTTemplateData(pkey, xmlData, textData);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("修改个人模版内容失败!", e);
		}
	}

	/**
	 * 获取病历模版
	 */
	@SuppressWarnings("unchecked")
	public void doLoadTemplateData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			res.putAll(cmm.doLoadTemplateData(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取门诊病历模版
	 */
	@SuppressWarnings("unchecked")
	public void doLoadMZTemplateData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			res.putAll(cmm.doLoadMZTemplateData(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取病历模版
	 */
	@SuppressWarnings("unchecked")
	public void doLoadEmrEditorData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			res.putAll(cmm.doLoadEmrEditorData(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取引用元素
	 */
	@SuppressWarnings("unchecked")
	public void doLoadRefItems(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			res.put("retItems", cmm.doLoadRefItems(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 判断病程是否修改
	 */
	@SuppressWarnings("unchecked")
	public void doCheckEditor(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			res.putAll(cmm.doCheckEditor(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 判断是否有权限
	 */
	@SuppressWarnings("unchecked")
	public void doCheckPermission(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			res.putAll(cmm.doCheckPermission(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 更新页眉距离信息
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateYmxx(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.doUpdateYmxx(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存病历模版
	 */
	@SuppressWarnings("unchecked")
	public void doSaveEmrData(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			res.putAll(cmm.doSaveEmrData(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存门诊病历模版
	 */
	@SuppressWarnings("unchecked")
	public void doSaveOmrData(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			res.putAll(cmm.doSaveOmrData(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存个人模版
	 */
	@SuppressWarnings("unchecked")
	public void doSaveAsPrivatePlate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.doSaveAsPrivatePlate(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取模版显示名称
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetMbXsmc(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			res.putAll(cmm.doGetMbXsmc(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取审阅记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadReadRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			res.putAll(cmm.doLoadReadRecord(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 判断是否存在签名
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doCheckSigned(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			res.putAll(cmm.doCheckSigned(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 签名
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSigned(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			res.putAll(cmm.doSigned(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 删除签名
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveSigned(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.doRemoveSigned(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取修改内容
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doLoadEmrHjnr(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Object pkey = req.get("pkey");
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			res.putAll(cmm.doLoadEmrHjnr(pkey, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取打印参数
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadPrtSetup(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			res.putAll(cmm.doLoadPrtSetup(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取字典元素列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("rawtypes")
	public void doListDicInformation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String schema = (String) req.get("schema");
		List queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = (List) req.get("cnd");
		}
		String fieldId = (String) req.get("fieldId");
		String cndValue = (String) req.get("cndValue");
		String queryCndsType = null;
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String sortInfo = null;
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 1;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			res.putAll(cmm.listDicInformation(schema, queryCnd, queryCndsType,
					sortInfo, pageSize, pageNo, fieldId, cndValue));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 强制解锁
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveForcedUnlock(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String BLBH = req.get("BLBH").toString();
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.saveForcedUnlock(BLBH, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 检查个人模版名称
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doCheckHasPersonalName(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String PTNAME = (String) req.get("PTNAME");
		EmrManageModel cmm = new EmrManageModel(dao);
		boolean flag = false;
		try {
			flag = cmm.checkHasPersonalName(PTNAME);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		res.put("flag", flag);
	}

	/**
	 * 页眉页脚设置查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryHeadersFootersSet(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.doQueryHeadersFootersSet(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 页眉页脚设置保存
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveHeadersFootersSet(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.doSaveHeadersFootersSet(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 病历类别保存
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveMedicalRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			// add by Dingxc
			// 为了判断病历类别第一级（根下面一级）是不是重复
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			int MLBZ = (Integer) body.get("MLBZ");// 目录标志
			String YDLBBM = (String) body.get("YDLBBM");// 约定类别
			if (1 == MLBZ) {
				// 是根下面的第一级目录，需要判断重复
				long num = cmm.doGetBLYDLBBMInfo(YDLBBM);
				if (num == 0) {
					// 当前树第一级没有重复的
					cmm.doSaveMedicalRecord(req, res, dao, ctx);
				} else if (num == -1) {
					// 查询数据库时出异常
					res.put("rmsg", 7);
					return;
				} else {
					// 当前树第一级有重复的，提示重复
					res.put("rmsg", 77);
					return;
				}
			} else {
				// 不需要判断是不是重复，直接保存
				cmm.doSaveMedicalRecord(req, res, dao, ctx);
			}
			// add by Dingxc ---end
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 科室模板订阅查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ExpException
	 */
	public void doLoadKsmbdy(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException, ExpException {
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.doLoadKsmbdy(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 病历病程模板类别修改
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateEmrTemplatesType(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.doUpdateEmrTemplatesType(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 病历病程模板类别修改
	 * 
	 * @throws PersistentDataOperationException
	 */
	public void doSaveKsTemplateDy(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException {
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.doSaveKsTemplateDy(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 病历病程模板预览
	 * 
	 * @throws Exception
	 */
	public void doLoadChTemplate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws Exception {
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.doLoadChTemplate(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 更新签名元素
	 * 
	 * @throws Exception
	 */
	public void doLoadQmys(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws Exception {
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.doLoadQmys(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存签名元素
	 * 
	 * @throws Exception
	 */
	public void doSaveQmys(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws Exception {
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.doSaveQmys(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 特殊符号保存
	 * 
	 * @throws Exception
	 */
	public void doSaveTsfh(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws Exception {
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.doSaveTsfh(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 病历类别子类别查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ExpException
	 */
	public void doGetBLLBInfo(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException, ExpException {
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.doGetBLLBInfo(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 公有模板查询
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void doLoadTemplateList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws Exception {
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.doLoadTemplateList(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 保修护理诊断前验证
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ExpException
	 */
	public void doSaveHLZDPre(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException, ExpException {
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.doSaveHLZDPre(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存时限时间设置
	 * 
	 * @throws Exception
	 */
	public void doSaveTimeLimitSet(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws Exception {
		EmrManageModel cmm = new EmrManageModel(dao);
		try {
			cmm.doSaveTimeLimitSet(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doListTimeLimitSet(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ExpException {
		EmrManageModel cmm = new EmrManageModel(dao);
		String schema = (String) req.get("schema");
		List<Map<String, Object>> result = null;
		try {
			result = cmm.listTimeLimitSet(schema);
			SchemaUtil.setDictionaryMassageForList(result, schema);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
			throw new ServiceException("时限时间列表查询失败！", e);
		}
		res.put("body", result);
	}

}
