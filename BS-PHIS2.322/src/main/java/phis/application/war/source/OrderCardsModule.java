package phis.application.war.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.SchemaUtil;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;

public class OrderCardsModule {
	protected Logger logger = LoggerFactory.getLogger(OrderCardsModule.class);
	protected BaseDAO dao;

	public OrderCardsModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 医嘱病人List左边显示
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryPatientList(Map<String, Object> req,
			Map<String, Object> res,  Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();
		String schema = (String) req.get("schema");
		
		List<Map<String, Object>> result = null;
		int pageNo = Integer.parseInt(req.get("pageNo").toString()) - 1;
		int pageSize = Integer.parseInt(req.get("pageSize").toString());
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder
				.append("SELECT DISTINCT a.BRCH as BRCH,a.BRXM as BRXM,a.ZYH as ZYH,a.ZYHM as ZYHM FROM ZY_BRRY a,ZY_CWSZ b WHERE ( a.ZYH = b.ZYH) and a.JGID = b.JGID and b.KSDM = :KSDM and a.JGID = :JGID ORDER BY BRCH");

		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("SELECT COUNT(*) as NUM FROM (").append(
				sqlBuilder.toString());
		sBuilder.append(") t");
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("KSDM", (Integer)req.get("wardId"));
			parameters.put("JGID", JGID);

			List<Map<String, Object>> coun = dao.doSqlQuery(
					sBuilder.toString(), parameters);
			int total = Integer.parseInt(coun.get(0).get("NUM").toString());
			res.put("totalCount", total);
			res.put("pageSize", pageSize);
			res.put("pageNo", pageNo);
			parameters.put("first", pageNo * pageSize);
			parameters.put("max", pageSize);
			result = dao.doSqlQuery(sqlBuilder.toString(), parameters);
			result = SchemaUtil.setDictionaryMassageForList(result, schema);
		} catch (PersistentDataOperationException e) {
			logger.error("获取医嘱病人列表数据失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取医嘱病人列表数据失败");
		}
		return result;
	}
	
	/**
	 * 医嘱病人List左边显示
	 * 按ZYHM搜索
	 * @param req
	 * @param res
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryPatientListByZYHM(Map<String, Object> req,
			Map<String, Object> res,  Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();
		String schema = (String) req.get("schema");
		String ZYHM = (String)req.get("ZYHM");
		
		List<Map<String, Object>> result = null;
		int pageNo = Integer.parseInt(req.get("pageNo").toString()) - 1;
		int pageSize = Integer.parseInt(req.get("pageSize").toString());
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder
				.append("SELECT DISTINCT a.BRCH as BRCH,a.BRXM as BRXM,a.ZYH as ZYH,a.ZYHM as ZYHM FROM ZY_BRRY a,ZY_CWSZ b WHERE ( a.ZYH = b.ZYH) and a.JGID = b.JGID and b.KSDM = :KSDM and a.JGID = :JGID and a.ZYHM like :ZYHM ORDER BY BRCH");

		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("SELECT COUNT(*) as NUM FROM (").append(
				sqlBuilder.toString());
		sBuilder.append(") t");
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("KSDM", (Integer)req.get("wardId"));
			parameters.put("JGID", JGID);
			parameters.put("ZYHM", "%"+ZYHM+"%");

			List<Map<String, Object>> coun = dao.doSqlQuery(
					sBuilder.toString(), parameters);
			int total = Integer.parseInt(coun.get(0).get("NUM").toString());
			res.put("totalCount", total);
			res.put("pageSize", pageSize);
			res.put("pageNo", pageNo);
			parameters.put("first", pageNo * pageSize);
			parameters.put("max", pageSize);
			result = dao.doSqlQuery(sqlBuilder.toString(), parameters);
			result = SchemaUtil.setDictionaryMassageForList(result, schema);
		} catch (PersistentDataOperationException e) {
			logger.error("获取医嘱病人列表数据失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取医嘱病人列表数据失败");
		}
		return result;
	}
	
	/**
	 * 医嘱病人List左边显示
	 * 按brxm搜索
	 * @param req
	 * @param res
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryPatientListByBRXM(Map<String, Object> req,
			Map<String, Object> res,  Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();
		String schema = (String) req.get("schema");
		String BRXM = (String)req.get("BRXM");
		
		List<Map<String, Object>> result = null;
		int pageNo = Integer.parseInt(req.get("pageNo").toString()) - 1;
		int pageSize = Integer.parseInt(req.get("pageSize").toString());
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder
				.append("SELECT DISTINCT a.BRCH as BRCH,a.BRXM as BRXM,a.ZYH as ZYH,a.ZYHM as ZYHM FROM ZY_BRRY a,ZY_CWSZ b WHERE ( a.ZYH = b.ZYH) and a.JGID = b.JGID and b.KSDM = :KSDM and a.JGID = :JGID and a.BRXM like :BRXM ORDER BY BRCH");

		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("SELECT COUNT(*) as NUM FROM (").append(
				sqlBuilder.toString());
		sBuilder.append(") t");
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("KSDM", (Integer)req.get("wardId"));
			parameters.put("JGID", JGID);
			parameters.put("BRXM", "%"+BRXM+"%");

			List<Map<String, Object>> coun = dao.doSqlQuery(
					sBuilder.toString(), parameters);
			int total = Integer.parseInt(coun.get(0).get("NUM").toString());
			res.put("totalCount", total);
			res.put("pageSize", pageSize);
			res.put("pageNo", pageNo);
			parameters.put("first", pageNo * pageSize);
			parameters.put("max", pageSize);
			result = dao.doSqlQuery(sqlBuilder.toString(), parameters);
			result = SchemaUtil.setDictionaryMassageForList(result, schema);
		} catch (PersistentDataOperationException e) {
			logger.error("获取医嘱病人列表数据失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取医嘱病人列表数据失败");
		}
		return result;
	}
	
	/**
	 * 医嘱病人List左边显示
	 * 按brch搜索
	 * @param req
	 * @param res
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryPatientListByBRCH(Map<String, Object> req,
			Map<String, Object> res,  Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();
		String schema = (String) req.get("schema");
		String BRCH = (String)req.get("BRCH");
		
		List<Map<String, Object>> result = null;
		int pageNo = Integer.parseInt(req.get("pageNo").toString()) - 1;
		int pageSize = Integer.parseInt(req.get("pageSize").toString());
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder
				.append("SELECT DISTINCT a.BRCH as BRCH,a.BRXM as BRXM,a.ZYH as ZYH,a.ZYHM as ZYHM FROM ZY_BRRY a,ZY_CWSZ b WHERE ( a.ZYH = b.ZYH) and a.JGID = b.JGID and b.KSDM = :KSDM and a.JGID = :JGID and a.BRCH like :BRCH ORDER BY BRCH");

		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("SELECT COUNT(*) as NUM FROM (").append(
				sqlBuilder.toString());
		sBuilder.append(") t");
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("KSDM", (Integer)req.get("wardId"));
			parameters.put("JGID", JGID);
			parameters.put("BRCH", "%"+BRCH+"%");

			List<Map<String, Object>> coun = dao.doSqlQuery(
					sBuilder.toString(), parameters);
			int total = Integer.parseInt(coun.get(0).get("NUM").toString());
			res.put("totalCount", total);
			res.put("pageSize", pageSize);
			res.put("pageNo", pageNo);
			parameters.put("first", pageNo * pageSize);
			parameters.put("max", pageSize);
			result = dao.doSqlQuery(sqlBuilder.toString(), parameters);
			result = SchemaUtil.setDictionaryMassageForList(result, schema);
		} catch (PersistentDataOperationException e) {
			logger.error("获取医嘱病人列表数据失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取医嘱病人列表数据失败");
		}
		return result;
	}
}
