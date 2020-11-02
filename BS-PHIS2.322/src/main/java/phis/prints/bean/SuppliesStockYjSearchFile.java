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

public class SuppliesStockYjSearchFile implements IHandler {
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
					"SELECT DISTINCT t1.WZMC as WZMC,t1.WZGG as WZGG,t.CJXH as CJXH,t2.CJMC as CJMC,t1.WZDW as WZDW,t.WZSL as WZSL,t.WZJG as WZJG,t.WZJE as WZJE,t.SCRQ as SCRQ,");
			sql_list.append("t.SXRQ as SXRQ,t.WZPH as WZPH,t.MJPH as MJPH,t1.HSLB as HSLB,t.JLXH as JLXH,t.KCXH as KCXH,t.KFXH as KFXH,t1.ZBLB as ZBLB,t.WZXH as WZXH,t1.PYDM as PYDM,");
			sql_list.append("t1.WBDM as WBDM,t1.JXDM as JXDM,t1.KWBH as KWBH,t.FSRQ as RKRQ FROM ");
			sql_list.append(" WL_WZKC t, WL_WZZD t1,WL_SCCJ t2 ");
			sql_list.append(" WHERE (t.WZXH = t1.WZXH) and (t.CJXH = t2.CJXH) and (t.KFXH =:KFXH AND t.ZBLB =:ZBLB)");
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			for (int i = 0; i < inofList.size(); i++) {
				inofList.get(i).put(
						"ZBLB",
						DictionaryController.instance()
								.get("phis.dictionary.booksCategory")
								.getText(inofList.get(i).get("ZBLB") + ""));
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
		} catch (ControllerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnitName();
		response.put("title", jgname + "库存明细查询");
	}

}
