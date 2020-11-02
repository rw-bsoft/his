/**
 * @(#)MedicalRecordsQueryService.java Created on 2013-5-16 下午1:41:40
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.war.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.CNDHelper;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class MedicalRecordsQueryService extends AbstractActionService implements
		DAOSupportable {

	public void doGetRecordHtmlData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String pkey = req.get("pkey").toString();
		String JZXH = req.get("JZXH").toString();
		String BLLX = req.get("BLLX").toString();
		MedicalRecordsQueryModel mrqm = new MedicalRecordsQueryModel(dao);
		String html = null;
		try {
			html = mrqm.getRecordHtmlData(pkey, JZXH, BLLX);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询文档html内容失败！", e);
		}
		res.put("body", html);
	}

	public void doCheckHasTJMC(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String schema = (String) req.get("schema");
		String TJMC = (String) req.get("TJMC");
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId();
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "YHID", "s", uid);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "TJMC", "s", TJMC);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		MedicalRecordsQueryModel mrqm = new MedicalRecordsQueryModel(dao);
		boolean flag = true;
		try {
			flag = mrqm.checkHasTJMC(cnd, schema);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询是否存在该条件名称失败！", e);
		}
		res.put("body", flag);
	}

	public void doListAllConditions(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String schema = (String) req.get("schema");
		MedicalRecordsQueryModel mrqm = new MedicalRecordsQueryModel(dao);
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId();
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "YHID", "s", uid);
		List<Map<String, Object>> result = null;
		try {
			result = mrqm.listAllConditions(cnd, schema);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询所有条件失败！", e);
		}
		res.put("body", result);
		res.put("totalCount", result.size());
	}

	public void doRemoveMedicalCondition(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String pkey = req.get("pkey").toString();
		MedicalRecordsQueryModel mrqm = new MedicalRecordsQueryModel(dao);
		try {
			mrqm.removeMedicalCondition(pkey);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("删除查询条件失败！", e);
		}
	}

	public void doSaveMedicalCondition(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String op = (String) req.get("op");
		MedicalRecordsQueryModel mrqm = new MedicalRecordsQueryModel(dao);
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId();
		if (body != null) {
			body.put("YHID", uid);
		}
		Map<String, Object> result = null;
		try {
			result = mrqm.saveMedicalCondition(body, schema, op);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("保存病历查询条件失败！", e);
		}
		res.put("body", result);
	}

	public void doGetReviewRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String pkey = req.get("pkey").toString();
		MedicalRecordsQueryModel mrqm = new MedicalRecordsQueryModel(dao);
		List<Map<String, Object>> list = null;
		try {
			list = mrqm.getReviewRecord(pkey);
			SchemaUtil.setDictionaryMassageForList(list, "phis.application.emr.schemas.EMR_BLSY_WSYCX");
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询审阅记录失败！", e);
		}
		res.put("body", list);
		res.put("totalCount", list.size());
	}

	public void doGetMedicalRecordData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String pkey = req.get("pkey").toString();
		String schema = (String) req.get("schema");
		MedicalRecordsQueryModel mrqm = new MedicalRecordsQueryModel(dao);
		Map<String, Object> map = null;
		try {
			map = mrqm.getMedicalRecordData(schema, pkey);
			if (map != null) {
				String MBLB = map.get("MBLB") == null ? null : map.get("MBLB")
						.toString();
				SchemaUtil.setDictionaryMassageForForm(map, schema);
				if ("2000001".equals(MBLB)) {
					Map<String, Object> m = new HashMap<String, Object>();
					m.put("key", "2000001");
					m.put("text", "住院病案首页");
					map.put("MBLB", m);
					map.put("BLLB", m);
				}
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询病历失败！", e);
		}
		res.put("body", map);
	}

	public void doListMedicalRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = (List) req.get("cnd");
		}
		String schema = (String) req.get("schema");
		String type = (String) req.get("type");
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
		MedicalRecordsQueryModel mrqm = new MedicalRecordsQueryModel(dao);
		Map<String, Object> map = null;
		try {
			map = mrqm.listMedicalRecords(queryCnd, schema, queryCndsType,
					sortInfo, pageSize, pageNo, type);
			res.putAll(map);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询所有病历失败！", e);
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
		StringBuffer cnds = new StringBuffer(
				"['and',['and',['and',['eq',['$','TYBZ'],['i',0]],['eq',['$','YMBZ'],['i',0]]],['eq',['$','ZYMZ'],['i',0]]],['eq',['$','ZKDL'],['i',0]]]");
		if (req.containsKey("cnd")) {
			queryCnd = (List) req.get("cnd");
		}
		String schema = (String) req.get("schema");
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
			Integer Mblb = Integer.parseInt(req.get("MBLB").toString());
			Integer bllb = Integer.parseInt(req.get("BLLB").toString());
			if (Mblb > 0) {
				String field = "";
				if (bllb > 0) {
					field = "BLLB";
				} else {
					field = "MBLB";
				}
				cnds.insert(0, "['and',").append(
						",['eq',['$','" + field + "'],['d'," + Mblb + "]]]");
			}
			Object ssks = req.get("SSKS");
			if (ssks != null) {
				List<?> l = getSszk(ssks, dao, ctx);
				if (l.size() > 0) {
					cnds.insert(0, "['and',").append(
							",['in',['$','SSZK']," + l + ",'d']]");
				} else {
					cnds.insert(0, "['and',").append(
							",['eq',['$','SSZK'],['d',-1]]]");
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

	public List<?> getSszk(Object ssks, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException {
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		String hql = "select ZKFL as ZKFL from " + BSPHISEntryNames.EMR_ZKKS
				+ " where KSDM=:KSDM";
		return ss.createSQLQuery(hql)
				.setLong("KSDM", Long.parseLong(ssks.toString())).list();
	}
}
