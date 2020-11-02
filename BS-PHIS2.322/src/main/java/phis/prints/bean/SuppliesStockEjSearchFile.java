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

public class SuppliesStockEjSearchFile implements IHandler {
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
		String jlxh = null;
		String sql = null;
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("KFXH", KFXH);
		if (request.get("jlxh") != null) {
			jlxh = request.get("jlxh") + "";
		}
		try {
			if (!"".equals(jlxh)) {
				if (!"0".equals(jlxh)) {
					sql = "SELECT DISTINCT t1.WZMC as WZMC,t1.WZGG as WZGG,t.CJXH as CJXH,t2.CJMC as CJMC,t1.WZDW as WZDW,t.WZSL as WZSL,t.WZJG as WZJG,t.WZJE as WZJE,to_char(t.SCRQ,'yyyy-mm-dd') as SCRQ,to_char(t.SXRQ,'yyyy-mm-dd') as SXRQ,t.WZPH as WZPH,t.MJPH as MJPH,t1.HSLB as HSLB,t.JLXH as JLXH,t.KCXH as KCXH,t.KFXH as KFXH,t1.ZBLB as ZBLB,t.WZXH as WZXH,t1.PYDM as PYDM, t1.WBDM as WBDM,t1.JXDM as JXDM,t1.KWBH as KWBH,to_char(t.FSRQ,'yyyy-mm-dd') as RKRQ FROM WL_WZKC t, WL_WZZD t1,WL_SCCJ t2 WHERE (t.WZXH = t1.WZXH) and (t.CJXH = t2.CJXH) and (t.KFXH =:KFXH)  and t.JLXH in ("
							+ jlxh + ")";
				} else {
					sql = "SELECT DISTINCT t1.WZMC as WZMC,t1.WZGG as WZGG,t.CJXH as CJXH,t2.CJMC as CJMC,t1.WZDW as WZDW,t.WZSL as WZSL,t.WZJG as WZJG,t.WZJE as WZJE,to_char(t.SCRQ,'yyyy-mm-dd') as SCRQ,to_char(t.SXRQ,'yyyy-mm-dd') as SXRQ,t.WZPH as WZPH,t.MJPH as MJPH,t1.HSLB as HSLB,t.JLXH as JLXH,t.KCXH as KCXH,t.KFXH as KFXH,t1.ZBLB as ZBLB,t.WZXH as WZXH,t1.PYDM as PYDM, t1.WBDM as WBDM,t1.JXDM as JXDM,t1.KWBH as KWBH,to_char(t.FSRQ,'yyyy-mm-dd') as RKRQ FROM WL_WZKC t, WL_WZZD t1,WL_SCCJ t2  WHERE (t.WZXH = t1.WZXH) and (t.CJXH = t2.CJXH) and (t.KFXH =:KFXH)";
				}
				List<Map<String, Object>> wzkclist = dao.doSqlQuery(sql,
						parameters);
				for (int i = 0; i < wzkclist.size(); i++) {
					wzkclist.get(i).put("ZBLB",DictionaryController.instance().get("phis.dictionary.booksCategory").getText(wzkclist.get(i).get("ZBLB") + ""));
					// 数量，价格，金额 格式化
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

					Double WZJG = 0.00;
					if (wzkclist.get(i).get("WZJG") != null) {
						WZJG = Double.parseDouble(wzkclist.get(i).get("WZJG") + "");
					}
					wzkclist.get(i).put("WZJG",
							String.format("%1$.4f", WZJG).toString());
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
