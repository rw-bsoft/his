package phis.prints.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class DepartmentBooksFile implements IHandler {
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		int ZBLB = Integer.parseInt(request.get("ZBLB") + "");
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("KFXH", KFXH);
		parameters.put("ZBLB", ZBLB);
		try {
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT t.KSDM as KSDM,t2.OFFICENAME as KSMC,t1.WZMC as WZMC,t1.WZGG as WZGG,t.CJXH as CJXH,t3.CJMC as CJMC,t1.WZDW as WZDW,SUM(t.WZSL) as WZSL,");
			sql_list.append("t.WZJG as WZJG,SUM(t.WZJE)as WZJE,t.KCXH as KCXH,t.ZBLB as ZBLB,t2.PYCODE as PYDM,t.ZRRQ as ZRRQ FROM ");
			sql_list.append(" WL_KSZC t, WL_WZZD t1,SYS_Office  t2,WL_SCCJ t3 ");
			sql_list.append(" WHERE (t.WZXH = t1.WZXH) and (t.CJXH = t3.CJXH) and (t.KSDM = t2.ID) and (t.KFXH =:KFXH) and t.ZBLB =:ZBLB ");
			sql_list.append(" GROUP BY t.KSDM,t2.OFFICENAME,t1.WZMC,t1.WZGG,t.CJXH,t3.CJMC,t1.WZDW,t.WZJG,t.KCXH,t.ZBLB,t2.PYCODE,t.ZRRQ ");
			sql_list.append(" ORDER BY t2.OFFICENAME ASC,t1.WZMC ASC,t1.WZGG ASC ");
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			for (int i = 0; i < inofList.size(); i++) {
				// 数量，价格，金额 格式化
				Double WZSL = 0.00;
				if (inofList.get(i).get("WZSL") != null) {
					WZSL = Double.parseDouble(inofList.get(i).get("WZSL") + "");
				}
				inofList.get(i).put("WZSL",
						String.format("%1$.2f", WZSL).toString());

				Double WZJE = 0.00;
				if (inofList.get(i).get("WZJE") != null) {
					WZJE = Double.parseDouble(inofList.get(i).get("WZJE") + "");
				}
				inofList.get(i).put("WZJE",
						String.format("%1$.4f", WZJE).toString());

				Double WZJG = 0.00;
				if (inofList.get(i).get("WZJG") != null) {
					WZJG = Double.parseDouble(inofList.get(i).get("WZJG") + "");
				}
				inofList.get(i).put("WZJG",
						String.format("%1$.4f", WZJG).toString());
			}

			records.addAll(inofList);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();
		response.put("title", jgname + "科室账册明查询");
	}
}
