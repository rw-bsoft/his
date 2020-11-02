/*
 * @(#)DBHelper.java Created on 2011-12-28 下午4:46:20
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.source.utils;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import phis.source.PersistentDataOperationException;
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
	 * 
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
			throw new PersistentDataOperationException(e1);
		}
		String where;
		String sortInfo = sc.getSortInfo();
		// add by lily 2012-07-17 增加按 schema排序条件
		if (StringUtils.isEmpty(orderBy)) {
			orderBy = sortInfo;
		}
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
		// modify by yangl: sc.getId() To sc.getTableName()
		hql.append(" from ").append(sc.getEntityName()).append(" a ")
				.append(where);
		return "select "
				+ hql.substring(1)
				+ (orderBy == null || orderBy.trim().length() == 0 ? ""
						: (" order by " + orderBy));
	}
}
