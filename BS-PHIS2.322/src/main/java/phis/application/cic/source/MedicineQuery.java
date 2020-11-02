package phis.application.cic.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.service.dao.SimpleQuery;
import ctd.util.context.Context;
import ctd.util.exp.ExpressionProcessor;

public class MedicineQuery extends SimpleQuery {

	/**
	 * 查询药品信息
	 */
	@SuppressWarnings("rawtypes")
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		BaseDAO dao = new BaseDAO();
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		Object wardId = req.get("wardId");
		int pageNo = Integer.parseInt(req.get("pageNo") + "");
		int pageSize = Integer.parseInt(req.get("pageSize") + "");
		int first = (pageNo - 1) * pageSize;
		try {
			List queryCnd = null;
			if (req.containsKey("cnd")) {
				queryCnd = (List) req.get("cnd");
			}
			String whereCnd = ExpressionProcessor.instance().toString(queryCnd)
					+ " and a.JGID=" + manageUnit;

			if (wardId != null && wardId.toString().trim().length() > 0) {
				String bzlx = ParameterUtil.getParameter(manageUnit,
						BSPHISSystemArgument.YZLR_BZLX, ctx);
				parameters.put("JGID", manageUnit);
				parameters.put("BQDM", Long.parseLong(wardId.toString()));
				String bzlxFields = "";
				if ("2".equals(bzlx)) {
					bzlxFields = "b.YPGG as YFGG,b.YPDW as YFDW";
				} else {
					bzlxFields = "b.YPGG as YFGG,b.YPDW as YFDW";
				}
				StringBuffer sqlCount = new StringBuffer(
						"select count(distinct a.YPXH) as count from ");
				sqlCount.append(" YF_YPXX a,");
				sqlCount.append(" YK_TYPK b,");
				sqlCount.append(" YK_YPBM c, ");
				sqlCount.append(" YK_YPXX d ");
				sqlCount.append(" where a.YPXH=b.YPXH and a.YPXH=c.YPXH and a.YPXH=d.YPXH and a.YFSB in (select distinct YFSB from BQ_FYYF where BQDM=:BQDM and JGID =:JGID) and "
						+ whereCnd + " order by a.YPXH");
				long count = (Long) dao.doLoad(sqlCount.toString(), parameters)
						.get("count");
				parameters.put("first", first);
				parameters.put("max", pageSize);
				StringBuffer sql = new StringBuffer(
						" select distinct a.YPXH as YPXH,b.ZBLB as ZBLB," + bzlxFields
								+ ", b.YPMC,d.CFLX as CFLX,b.TYPE as TYPE");
				sql.append(" from ");
				sql.append(" YF_YPXX a,");
				sql.append(" YK_TYPK b,");
				sql.append(" YK_YPBM c, ");
				sql.append(" YK_YPXX d ");
				sql.append(" where a.YPXH=b.YPXH and a.YPXH=c.YPXH and a.YPXH=d.YPXH  and a.YFSB in (select distinct YFSB from BQ_FYYF where BQDM=:BQDM and JGID =:JGID) and "
						+ whereCnd + " order by a.YPXH");
				List<Map<String, Object>> data = dao.doSqlQuery(sql.toString(),
						parameters);

				res.put("body", data);
				res.put("totalCount", count);
			} else {
				parameters.put("first", first);
				parameters.put("max", pageSize);
				StringBuffer sql = new StringBuffer(
						" select distinct a.YPXH,a.YFGG,a.YFDW as YFDW, b.YPMC,b.ZBLB as ZBLB");
				sql.append(" from ");
				sql.append(" YF_YPXX a,");
				sql.append(" YK_TYPK b,");
				sql.append(" YK_YPBM c, ");
				sql.append(" YK_YPXX d ");
				sql.append(" where a.YPXH=b.YPXH and a.YPXH=c.YPXH and a.YPXH=d.YPXH and "
						+ whereCnd + " order by a.YPXH");
				List<Map<String, Object>> data = dao.doSqlQuery(sql.toString(),
						parameters);
				StringBuffer sqlCount = new StringBuffer(
						"select count(distinct a.YPXH) as count from ");
				sqlCount.append(" YF_YPXX a,");
				sqlCount.append(" YK_TYPK b,");
				sqlCount.append(" YK_YPBM c, ");
				sqlCount.append(" YK_YPXX d ");
				sqlCount.append(" where a.YPXH=b.YPXH and a.YPXH=c.YPXH and a.YPXH=d.YPXH and "
						+ whereCnd + " order by a.YPXH");
				long count = (Long) dao.doLoad(sqlCount.toString(),
						new HashMap<String, Object>()).get("count");
				res.put("body", data);
				res.put("totalCount", count);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
