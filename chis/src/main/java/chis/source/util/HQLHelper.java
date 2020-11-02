/*
 * @(#)DBHelper.java Created on 2011-12-28 下午4:46:20
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.PersistentDataOperationException;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class HQLHelper {

	/**
	 * 构建查询语句，schema中引用及标志为“virtual”不被查询。
	 * 不包括引用字段，单表
	 * @param cnd
	 * @param schema
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public static String buildQueryHql(List<?> cnd, String orderBy,
			String schema, Context ctx) throws PersistentDataOperationException {
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(schema);
		} catch (ControllerException e1) {
			e1.printStackTrace();
		}
		String where;
		try {
			where = " where " + ExpressionProcessor.instance().toString(cnd);
		} catch (ExpException e) {
			throw new PersistentDataOperationException(e);
		}
		StringBuffer hql = new StringBuffer();
		for (SchemaItem si : sc.getItems()) {
			if (si.hasProperty("refAlias") || si.isVirtual()) {
				continue;
			}
			String f = si.getId();
			hql.append(",").append(f).append(" as ").append(f);
		}
		String entryName = sc.getId();
		String tableName = sc.getTableName();
		if (tableName != null && !"".equals(tableName)) {
			entryName = tableName;
		}
		hql.append(" from ").append(entryName).append(" a ").append(where);
		return "select "
				+ hql.substring(1)
				+ (orderBy == null || orderBy.trim().length() == 0 ? ""
						: (" order by " + orderBy));
	}
	
	

	/**
	 * 根据sql中出现的=:param从collection中挑选出有用的参数返回
	 * 
	 * @param sql
	 * @param collection
	 * @return
	 */
	public static Map<String, Object> selectParameters(String sql,
			Map<String, Object> collection) {
		Map<String, Object> realParams = new HashMap<String, Object>();
		String[] split = sql.split(":");
		for (int i = 1; i < split.length; i++) {
			String paramStr = split[i];
			paramStr = paramStr.replace(":", "");
			int idx = paramStr.indexOf(" ");
			if (idx < 0) {
				idx = paramStr.indexOf(",");
			}
			if (idx < 0) {
				idx = paramStr.length();
			}
			paramStr = paramStr.substring(0, idx);
			realParams.put(paramStr, collection.get(paramStr));
		}
		return realParams;
	}
}
