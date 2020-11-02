/**
 * @(#)AbstractTaskListService.java Created on 2010-6-23 下午02:53:29
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.schema.SchemaRelation;
import ctd.security.Condition;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public abstract class AbstractTaskListService extends AbstractActionService
		implements DAOSupportable {

	private static final Log logger = LogFactory
			.getLog(AbstractTaskListService.class);

	/**
	 * 以三天为分界线，剩余三天及以下的工作归类为紧急任务。
	 */
	public static final int HURRY_UP_DAY = 3;

	/**
	 * @see com.bsoft.hzehr.biz.AbstractService#getTransactedActions()
	 */
	@Override
	public List<String> getTransactedActions() {
		// @@ No transacted action.
		return null;
	}

	/**
	 * @param jsonReq
	 * @param jsonRes
	 * @param session
	 * @param ctx
	 * @throws ServiceException
	 * @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void doGetTaskList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String condition = null;
		String entryName = (String) req.get("schema");
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(entryName);
			condition = initCondition(req, sc, ctx);
		} catch (Exception e) {
			logger.error("error cnd ", e);
			throw new ServiceException("查询条件错误.", e);
		}
		int pageSize = (Integer) req.get("pageSize") == null ? 50
				: ((Integer) req.get("pageSize")).intValue();
		int pageNo = (Integer) req.get("pageNo") == null ? 1 : ((Integer) req
				.get("pageNo")).intValue();
		res.put("pageSize", pageSize);
		res.put("pageNo", pageNo);
		AbstractTaskListModel atlm = AbstractTaskListModelFactory.getInstance()
				.createTaskLiskModel(dao, this.getType());
		// @@ 取总记录数。
		int totalCount = 0;
		try {
			totalCount = atlm.getTotalCount(condition, sc);
		} catch (ModelDataOperationException e) {
			logger.error("Get Total record number task failed.", e);
			throw new ServiceException(e);
		}
		res.put("totalCount", totalCount);
		// @@ 获取任务列表。
		List<Map<String, Object>> list;
		try {
			list = atlm.getTaskList(condition, sc, pageSize, pageNo);
		} catch (ModelDataOperationException e) {
			logger.error("Get " + entryName + " list failed. ", e);
			throw new ServiceException(e);
		}
		ArrayList resBody = new ArrayList();
		res.put("body", resBody);
		for (Map<String, Object> map : list) {
			Map<?, ?> r = (Map<?, ?>) map;
			HashMap<String, Object> rec = new HashMap<String, Object>();
			for (SchemaItem si : sc.getItems()) {
				String name = si.getId();
				Object o = map.get(name);
				if (o == null) {
					continue;
				}
				Object v = r.get(name);
				rec.put(si.getId(), o);
				if (si != null && si.isCodedValue()) {
					Object dv = null;
					boolean isCacheDic = false;
					for (int k = 0; k < sc.getItems().size(); k++) {
						String vtext = sc.getItems().get(k).getId();
						if (vtext.equals(name + "_text")) {
							dv = r.get(si.getId());
							isCacheDic = true;
							break;
						}
					}
					if (dv != null) {
						rec.put(name + "_text", dv);
					} else {
						if (!isCacheDic) {
							rec.put(name + "_text", si.toDisplayValue(v));
						}
					}
				}
				// if (si.isCodedValue()) {
				// rec.put(si.getId() + "_text", si.getDisplayValue(o));
				// }
			}
			resBody.add(rec);
		}
	}

	/**
	 * @param sc
	 * @return
	 */
	protected String buildQueryFields(Schema sc) {
		StringBuffer fields = new StringBuffer();
		for (SchemaItem si : sc.getItems()) {
			if (si.isVirtual()) {
				continue;
			}
			fields.append(",")
					.append(si.hasProperty("refAlias") ? si
							.getProperty("refAlias") : "a").append(".")
					.append(si.getId()).append(" as ").append(si.getId());
		}
		return fields.substring(1);
	}

	/**
	 * @param jsonReq
	 * @param sc
	 * @param ctx
	 * @return
	 * @throws ExpException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected String initCondition(Map<String, Object> req, Schema sc,
			Context ctx) throws ExpException {
		String queryCndsType = StringUtils.isEmpty((String) req
				.get("queryCndsType")) ? "query" : StringUtils
				.trimToEmpty((String) req.get("queryCndsType"));
		ArrayList queryCnd = (ArrayList) req.get("cnd");
		List<ArrayList> cnds = new ArrayList<ArrayList>();
		if (queryCnd != null) {
			cnds.add(queryCnd);
		}
		for (SchemaRelation sr : sc.getParentRelations()) {
			if (sr.getJoinCondition() != null) {
				cnds.add((ArrayList) sr.getJoinCondition());
			}
		}
		for (SchemaRelation sr : sc.getChildRelations()) {
			if (sr.getJoinCondition() != null) {
				cnds.add((ArrayList) sr.getJoinCondition());
			}
		}

		StringBuffer condition = new StringBuffer();
		if (sc.lookupPremission().getMode().isAccessible() == false) {
			condition.append(" and 1=2");
		} else {
			// add role query condition
			Condition c = sc.lookupCondition(queryCndsType);
			if (c != null) {
				ArrayList exp = (ArrayList)c.getDefine();
				if (exp != null) {
					cnds.add(exp);
				}
			}
			// start
			ArrayList whereCnd = null;
			int cndCount = cnds.size();
			if (cndCount == 0) {
				whereCnd = queryCnd;
			} else {
				if (cndCount == 1) {
					whereCnd = cnds.get(0);
				} else {
					whereCnd = new ArrayList();
					whereCnd.add("and");
					for (ArrayList cd : cnds) {
						whereCnd.add(cd);
					}
				}
			}
			if (whereCnd != null) {
				condition.append(" and ").append(
						ExpressionProcessor.instance().toString(whereCnd));
			}
		}
		return condition.toString();
	}

	protected String getType() {
		return "";
	}
}
