package phis.source.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.CNDHelper;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;

public class TreeLoadSearchModule extends AbstractSearchModule {
	protected Logger logger = LoggerFactory
			.getLogger(TreeLoadSearchModule.class);

	/**
	 * 自定义树实现
	 */
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		try {
			String queryFields = req.get("queryFields") + "";
			String entryName = req.get("entryName") + "";
			String condition = req.get("condition") + "";
			String leaf = req.get("leaf") + "";
			String orderBy = req.get("orderBy") + "";
			res.put("body",
					getJsonData(queryFields, entryName, condition, orderBy,
							leaf, ctx));
		} catch (ExpException e) {
			logger.error("load tree failed by expException.", e);
		}

	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getJsonData(String queryFields,
			String entryName, String condition, String orderBy, String leaf,
			Context ctx) throws ExpException {
		String[] fields = queryFields.split(",");
		String strCnd = "";
		if (condition != null) {
			strCnd = ExpressionProcessor.instance().toString(
					(List<Object>) CNDHelper.toListCnd(condition));
			// System.out.println(strCnd);
		}
		String sql = "select " + queryFields + " from " + entryName
				+ (condition == null ? "" : " where ") + strCnd + " ";
		if (orderBy != null) {
			sql += "order by " + orderBy;
		}
		Session session = (Session) ctx.get(Context.DB_SESSION);
		SQLQuery query = session.createSQLQuery(sql);
		List<Object[]> treeItems = (List<Object[]>) query.list();
		// dao.doSqlQuery(sql, null);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (Object[] obj : treeItems) {
			int length = obj.length;
			Map<String, Object> m = new HashMap<String, Object>();
			Map<String, Object> attributes = new HashMap<String, Object>();
			for (int i = 2; i < length; i++) {
				attributes.put(fields[i], obj[i]);
			}
			if (leaf != null && leaf.equals("1")) {
				m.put("leaf", true);
			}
			m.put("id", obj[0]);
			m.put("text", obj[1]);
			m.put("attributes", attributes);
			result.add(m);
		}
		session.close();
		return result;
		// System.out.println(retStr);
	}
}
