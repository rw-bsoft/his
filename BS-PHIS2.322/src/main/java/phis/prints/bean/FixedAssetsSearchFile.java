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

public class FixedAssetsSearchFile implements IHandler {
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
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("KFXH", KFXH);
		try {
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT a.WZBH as WZBH, d.WZMC as WZMC,d.WZGG as WZGG,e.CJMC as CJMC,d.WZDW as WZDW,");
			sql_list.append("a.ZCYZ as ZCYZ,a.CZYZ as CZYZ,a.TZRQ as TZRQ,a.QYRQ as QYRQ,a.ZRRQ as ZRRQ,a.BSRQ as BSRQ,b.OFFICENAME as KSMC,a.WZZT as WZZT, ");
			sql_list.append("d.HSLB as HSLB,a.ZBXH as ZBXH,c.DWMC as DWMC,a.XHGG as XHGG,a.XHMC as XHMC,f.HSMC as HSMC FROM ");
			sql_list.append(" WL_ZCZB a LEFT OUTER JOIN SYS_Office b ON a.ZYKS = b.ID LEFT OUTER JOIN WL_GHDW c ON a.GHDW = c.DWXH,WL_WZZD d,WL_SCCJ e,WL_HSLB f ");
			sql_list.append(" WHERE ( d.WZXH = a.WZXH ) AND (d.HSLB = f.HSLB) AND ( a.CJXH = e.CJXH ) and (a.KFXH =:KFXH) order by  a.WZBH ");

			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			for (int i = 0; i < inofList.size(); i++) {
				inofList.get(i).put(
						"WZZT",
						DictionaryController.instance()
								.get("phis.dictionary.SuppliesStatus")
								.getText(inofList.get(i).get("WZZT") + ""));
				// 数量，价格，金额 格式化
				Double ZCYZ = 0.00;
				if (inofList.get(i).get("ZCYZ") != null) {
					ZCYZ = Double.parseDouble(inofList.get(i).get("ZCYZ") + "");
				}
				inofList.get(i).put("ZCYZ",
						String.format("%1$.2f", ZCYZ).toString());

				Double CZYZ = 0.00;
				if (inofList.get(i).get("CZYZ") != null) {
					CZYZ = Double.parseDouble(inofList.get(i).get("CZYZ") + "");
				}
				inofList.get(i).put("CZYZ",
						String.format("%1$.4f", CZYZ).toString());
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
		String jgname = user.getManageUnit().getName();
		response.put("title", jgname + "固定资产查询");
	}
}
