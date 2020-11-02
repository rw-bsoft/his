package phis.prints.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class SuppliesStockEjSearchDetailsFile implements IHandler {
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
		String wzxh = null;
		String sql = null;
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("KFXH", KFXH);
		if (request.get("wzxh") != null) {
			wzxh = request.get("wzxh") + "";
		}
		try {
			if (!"".equals(wzxh)) {
				if (!"0".equals(wzxh)) {
					sql = "SELECT DISTINCT t1.WZMC as WZMC,t1.WZGG as WZGG,t.CJXH as CJXH,t2.CJMC as CJMC,t1.WZDW as WZDW,Sum(t.WZSL) as WZSL,Sum(t.WZJE) as WZJE,t1.HSLB as HSLB,t1.ZBLB as ZBLB,t.WZXH as WZXH,t1.PYDM as PYDM,t1.WBDM as WBDM,t1.JXDM as JXDM FROM WL_WZKC t, WL_WZZD t1,WL_SCCJ t2 WHERE (t.WZXH = t1.WZXH) and (t.CJXH = t2.CJXH) and (t.KFXH =:KFXH) and t1.WZXH in("
							+ wzxh
							+ ") GROUP BY t1.WZMC,t1.WZGG,t.CJXH,t2.CJMC,t1.WZDW,t1.HSLB,t1.ZBLB,t.WZXH,t1.PYDM,t1.WBDM,t1.JXDM";
				} else {
					sql = "SELECT DISTINCT t1.WZMC as WZMC,t1.WZGG as WZGG,t.CJXH as CJXH,t2.CJMC as CJMC,t1.WZDW as WZDW,Sum(t.WZSL) as WZSL,Sum(t.WZJE) as WZJE,t1.HSLB as HSLB,t1.ZBLB as ZBLB,t.WZXH as WZXH,t1.PYDM as PYDM,t1.WBDM as WBDM,t1.JXDM as JXDM FROM WL_WZKC t, WL_WZZD t1,WL_SCCJ t2 WHERE (t.WZXH = t1.WZXH) and (t.CJXH = t2.CJXH) and (t.KFXH =:KFXH) GROUP BY t1.WZMC,t1.WZGG,t.CJXH,t2.CJMC,t1.WZDW,t1.HSLB,t1.ZBLB,t.WZXH,t1.PYDM,t1.WBDM,t1.JXDM";
				}
				List<Map<String, Object>> wzkclist = dao.doSqlQuery(sql,
						parameters);
				for (int i = 0; i < wzkclist.size(); i++) {
					wzkclist.get(i).put(
							"ZBLB",
							DictionaryController.instance()
									.get("phis.dictionary.booksCategory")
									.getText(wzkclist.get(i).get("ZBLB") + ""));
					
					Double WZSL = 0.00;
					if (wzkclist.get(i).get("WZSL") != null) {
						WZSL = Double.parseDouble(wzkclist.get(i).get("WZSL") + "");
					}
					wzkclist.get(i).put("WZSL",
							String.format("%1$.2f", WZSL).toString());

					Double WZJE = 0.00;
					if (wzkclist.get(i).get("WZJE") != null) {
						WZJE = Double.parseDouble(wzkclist.get(i).get("WZJE") + "");
					}
					wzkclist.get(i).put("WZJE",
							String.format("%1$.4f", WZJE).toString());

				}
				records.addAll(wzkclist);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnitName();
		response.put("title", jgname);
	}
}
