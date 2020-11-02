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

public class BusinessDocumentDetailforRKFile implements IHandler {
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
		}		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("KFXH", KFXH);
		try {
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT a.DJXH as DJXH, a.LZFS as LZFS ,a.LZDH as LZDH,d.WZMC as WZMC,d.WZGG as WZGG,c.CJXH as CJXH,d.WZDW as WZDW,c.WZSL as WZSL,");
			sql_list.append("c.WZJG as WZJG,c.WZJE as WZJE,c.WZPH as WZPH, a.JZRQ as JZRQ,a.DWXH as DWXH ,c.MJPH as MJPH,a.JBGH as JBGH,d.HSLB as HSLB, ");
			sql_list.append("c.FPHM as FPHM,e.YWLB as YWLB,a.DJZT as DJZT,a.ZBLB as ZBLB,e.DJLX as DJLX,a.THDJ as THDJ,b.DWMC as GHDW,a.RKRQ as RKRQ, ");
			sql_list.append("c.WZXH as WZXH,a.DJJE as DJJE,a.ZDGH as ZDGH, a.JZGH as JZGH,c.FKJE as FKJE,d.BKBZ as BKBZ,d.YCWC as YCWC,d.JLBZ as JLBZ,");
			sql_list.append("a.ZDRQ as ZDRQ,a.SHGH as SHGH FROM ");
			sql_list.append(" WL_RK01 a  LEFT OUTER JOIN WL_GHDW b  ON a.DWXH = b.DWXH,WL_RK02 c,WL_WZZD d,WL_LZFS e ");
			sql_list.append(" WHERE ( a.DJXH = c.DJXH ) and ( c.WZXH = d.WZXH ) and ( a.LZFS = e.FSXH ) and  ( ( a.DJZT = 2 ) ) AND  ");
			sql_list.append(" c.WZSL <> 0 and ( a.THDJ IS NULL OR a.THDJ = 0 ) and a.KFXH =:KFXH  ");
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			for (int i = 0; i < inofList.size(); i++) {
				inofList.get(i).put(
						"LZFS",
						DictionaryController.instance().get("phis.dictionary.transfermodes")
								.getText(inofList.get(i).get("LZFS") + ""));
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
		String jgname = user.getManageUnit().getName();
		response.put("title", jgname + "科室账册汇总查询");
	}
}
