/**
 * @(#)DiabetesOGTTService.java Created on 2015-1-8 上午9:26:25
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.visitplan.VisitPlanCreator;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleLoad;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class DiabetesOGTTService extends DiabetesService {
	private static final Logger logger = LoggerFactory
			.getLogger(DiabetesOGTTService.class);
	private VisitPlanCreator visitPlanCreator;

	/**
	 * 获得visitPlanCreator
	 * 
	 * @return the visitPlanCreator
	 */
	public VisitPlanCreator getVisitPlanCreator() {
		return visitPlanCreator;
	}

	/**
	 * 设置visitPlanCreator
	 * 
	 * @param visitPlanCreator
	 *            the visitPlanCreator to set
	 */
	public void setVisitPlanCreator(VisitPlanCreator visitPlanCreator) {
		this.visitPlanCreator = visitPlanCreator;
	}

	/**
	 * 查询糖尿病高危管理
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 */
	public void doCheckDiabetesRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException {
		Map<String, Object> body = new HashMap<String, Object>();
		Map<String, Object> reqBody =(Map<String, Object>) req.get("body");
		if (reqBody != null && reqBody.size() > 0) {
			Map<String, Object> para = new HashMap<String, Object>();
			String sql = "select empiId from MDC_DiabetesRecord where status='0' and empiId=:empiId";
			String empiId = (String) reqBody.get("empiId");
			para.put("empiId", empiId);
			List<Map<String, Object>> l = dao.doSqlQuery(sql, para);
			if (l != null && l.size() > 0) {
				body.put("dbsCreate", "y");
			} else {
				body.put("dbsCreate", "n");
			}
			sql = "select status as status from EHR_HealthRecord where empiId=:empiId";
			l = dao.doSqlQuery(sql, para);
			body.put("status", l.get(0).get("STATUS"));
			res.put("body", body);
		}
	}

	/**
	 * 查询糖尿病高危管理列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doListDiabetesOGTTRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		DiabetesOGTTModel dom = new DiabetesOGTTModel(dao);
		Map<String, Object> resultMap = null;
		try {
			resultMap = dom.listDiabetesOGTTRecord(req);
		} catch (ModelDataOperationException e) {
			logger.error("Get Daibetes record failed.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "查询糖尿病档案信息失败！");
			throw new ServiceException(e);
		}
		res.putAll(resultMap);
	}

	public void doSaveDiabetesOGTTRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String schemaId = (String) req.get("schema");
		Map<String, Object> rec = (HashMap<String, Object>) req.get("body");
		String op = (String) req.get("op");
		DiabetesOGTTModel dom = new DiabetesOGTTModel(dao);
		Map<String, Object> body;
		try {
			body = dom.saveDiabetesOGTTRecord(schemaId, rec, op);
		} catch (ModelDataOperationException e) {
			logger.error("do Save Diabetes OGTT Record failed.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "保存糖尿病高危信息失败！");
			throw new ServiceException(e);
		}
		res.put("body", body);
	}
}
