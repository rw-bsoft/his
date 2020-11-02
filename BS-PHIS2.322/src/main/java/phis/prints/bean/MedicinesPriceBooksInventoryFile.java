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
public class MedicinesPriceBooksInventoryFile implements IHandler {
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		String ypxh = null;
		Boolean showZero = false;
		if (request.get("ypxh") != null) {
			ypxh = request.get("ypxh") + "";
		}
		if (request.get("showZero") != null) {
			showZero = Boolean.parseBoolean(request.get("showZero") + "");
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid  =user.getManageUnit().getId();
		long yksb = Long.parseLong(user.getProperty("storehouseId")+"");// 用户的药库识别
		parameters.put("YKSB", yksb);
		parameters.put("JGID", jgid);
		BaseDAO dao = new BaseDAO(ctx);
		try {
			StringBuffer hql = new StringBuffer();
			if (!"".equals(ypxh)) {
				if (!"0".equals(ypxh)) {
					hql.append("SELECT a.YPMC as YPMC,a.YPGG as YPGG,a.YPDW as YPDW,b.CDMC as CDMC,sum(d.KCSL) as SWKC,c.KCSL as KCSL,c.JHJE as JHJE,c.PFJE as PFJE,c.LSJE as LSJE,a.PYDM as PYDM,a.WBDM as WBDM,a.JXDM as JXDM,a.QTDM as QTDM,a.YPDM as YPDM,e.KWBM as KWBM,c.YPXH as YPXH,c.YPCD as YPCD,e.YKZF as YKZF,a.ZBLB as ZBLB,c.ZFPB as ZFPB FROM ");
					hql.append( " YK_CDDZ b,");
					hql.append(" YK_TYPK a,");
					hql.append( " YK_CDXX c left join ");
					hql.append(" YK_KCMX d on (c.JGID = d.JGID and c.YPXH = d.YPXH and c.YPCD = d.YPCD),");
					hql.append(" YK_YPXX e WHERE e.YKSB = :YKSB and c.YPCD = b.YPCD and a.YPXH = c.YPXH and a.YPXH = e.YPXH and e.JGID = c.JGID and e.JGID = :JGID and a.YPXH in ("
							+ ypxh + ")");
					if (showZero == false) {
						hql.append(" and c.KCSL > 0 ");
					}
					hql.append(" GROUP BY a.YPMC,a.YPGG,a.YPDW,b.CDMC,c.KCSL,c.JHJE,c.PFJE,c.LSJE,a.PYDM,a.WBDM,a.JXDM,a.QTDM,a.YPDM,e.KWBM,c.YPXH,c.YPCD,e.YKZF,a.ZBLB,c.ZFPB");
					if (showZero == false) {
						hql.append(" having sum(d.KCSL) > 0");
					}
				} else {
					hql.append("SELECT a.YPMC as YPMC,a.YPGG as YPGG,a.YPDW as YPDW,b.CDMC as CDMC,sum(d.KCSL) as SWKC,c.KCSL as KCSL,c.JHJE as JHJE,c.PFJE as PFJE,c.LSJE as LSJE,a.PYDM as PYDM,a.WBDM as WBDM,a.JXDM as JXDM,a.QTDM as QTDM,a.YPDM as YPDM,e.KWBM as KWBM,c.YPXH as YPXH,c.YPCD as YPCD,e.YKZF as YKZF,a.ZBLB as ZBLB,c.ZFPB as ZFPB FROM ");
					hql.append(" YK_CDDZ b,");
					hql.append(" YK_TYPK a,");
					hql.append(" YK_CDXX c left join ");
					hql.append(" YK_KCMX d on (c.JGID = d.JGID and c.YPXH = d.YPXH and c.YPCD = d.YPCD),");
					hql.append(" YK_YPXX e WHERE e.YKSB = :YKSB and c.YPCD = b.YPCD and a.YPXH = c.YPXH and a.YPXH = e.YPXH and e.JGID = c.JGID and e.JGID = :JGID");
					if (showZero == false) {
						hql.append(" and c.KCSL > 0 ");
					}
					hql.append(" GROUP BY a.YPMC,a.YPGG,a.YPDW,b.CDMC,c.KCSL,c.JHJE,c.PFJE,c.LSJE,a.PYDM,a.WBDM,a.JXDM,a.QTDM,a.YPDM,e.KWBM,c.YPXH,c.YPCD,e.YKZF,a.ZBLB,c.ZFPB");
					if (showZero == false) {
						hql.append(" having sum(d.KCSL) > 0");
					}
				}
				hql.append(" order by c.YPXH");
				List<Map<String, Object>> kccx = dao.doSqlQuery(hql.toString(),
						parameters);
				records.addAll(kccx);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();
		response.put("title", jgname);
		String ypxh = null;
		Boolean showZero = false;
		if (request.get("ypxh") != null) {
			ypxh = request.get("ypxh") + "";
		}
		if (request.get("showZero") != null) {
			showZero = Boolean.parseBoolean(request.get("showZero") + "");
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		String jgid  =user.getManageUnit().getId();
		long yksb = Long.parseLong(user.getProperty("storehouseId")+"");// 用户的药库识别
		parameters.put("YKSB", yksb);
		parameters.put("JGID", jgid);
		BaseDAO dao = new BaseDAO(ctx);
		try {
			StringBuffer hql = new StringBuffer();
			if (!"".equals(ypxh)) {
				if (!"0".equals(ypxh)) {
					hql.append("SELECT a.YPMC as YPMC,a.YPGG as YPGG,a.YPDW as YPDW,b.CDMC as CDMC,sum(d.KCSL) as SWKC,c.KCSL as KCSL,c.JHJE as JHJE,c.PFJE as PFJE,c.LSJE as LSJE,a.PYDM as PYDM,a.WBDM as WBDM,a.JXDM as JXDM,a.QTDM as QTDM,a.YPDM as YPDM,e.KWBM as KWBM,c.YPXH as YPXH,c.YPCD as YPCD,e.YKZF as YKZF,a.ZBLB as ZBLB,c.ZFPB as ZFPB FROM ");
					hql.append( " YK_CDDZ b,");
					hql.append( " YK_TYPK a,");
					hql.append( "YK_CDXX c left join ");
					hql.append(" YK_KCMX d on (c.JGID = d.JGID and c.YPXH = d.YPXH and c.YPCD = d.YPCD),");
					hql.append(" YK_YPXX e WHERE e.YKSB = :YKSB and c.YPCD = b.YPCD and a.YPXH = c.YPXH and a.YPXH = e.YPXH and e.JGID = c.JGID and e.JGID = :JGID and c.YPXH in ("
							+ ypxh + ")");
					if (showZero == false) {
						hql.append(" and c.KCSL > 0 ");
					}
					hql.append(" GROUP BY a.YPMC,a.YPGG,a.YPDW,b.CDMC,c.KCSL,c.JHJE,c.PFJE,c.LSJE,a.PYDM,a.WBDM,a.JXDM,a.QTDM,a.YPDM,e.KWBM,c.YPXH,c.YPCD,e.YKZF,a.ZBLB,c.ZFPB");
					if (showZero == false) {
						hql.append(" having sum(d.KCSL) > 0");
					}
				} else {
					hql.append("SELECT a.YPMC as YPMC,a.YPGG as YPGG,a.YPDW as YPDW,b.CDMC as CDMC,sum(d.KCSL) as SWKC,c.KCSL as KCSL,c.JHJE as JHJE,c.PFJE as PFJE,c.LSJE as LSJE,a.PYDM as PYDM,a.WBDM as WBDM,a.JXDM as JXDM,a.QTDM as QTDM,a.YPDM as YPDM,e.KWBM as KWBM,c.YPXH as YPXH,c.YPCD as YPCD,e.YKZF as YKZF,a.ZBLB as ZBLB,c.ZFPB as ZFPB FROM ");
					hql.append( " YK_CDDZ b,");
					hql.append( " YK_TYPK a,");
					hql.append( " YK_CDXX c left join ");
					hql.append(" YK_KCMX d on (c.JGID = d.JGID and c.YPXH = d.YPXH and c.YPCD = d.YPCD),");
					hql.append( " YK_YPXX e WHERE e.YKSB = :YKSB and c.YPCD = b.YPCD and a.YPXH = c.YPXH and a.YPXH = e.YPXH and e.JGID = c.JGID and e.JGID = :JGID");
					if (showZero == false) {
						hql.append(" and c.KCSL > 0 ");
					}
					hql.append(" GROUP BY a.YPMC,a.YPGG,a.YPDW,b.CDMC,c.KCSL,c.JHJE,c.PFJE,c.LSJE,a.PYDM,a.WBDM,a.JXDM,a.QTDM,a.YPDM,e.KWBM,c.YPXH,c.YPCD,e.YKZF,a.ZBLB,c.ZFPB");
					if (showZero == false) {
						hql.append(" having sum(d.KCSL) > 0");
					}
				}
				hql.append(" order by c.YPXH");
				List<Map<String, Object>> kccx = dao.doSqlQuery(hql.toString(),
						parameters);
				double swkc = 0.00;
				double kcsl = 0.00;
				double jhje = 0.00;
				double lsje = 0.00;
				for (int i = 0; i < kccx.size(); i++) {
					if (kccx.get(i).get("SWKC") != null) {
						swkc = swkc
								+ Double.parseDouble(kccx.get(i).get("SWKC")
										+ "");
					}
					if (kccx.get(i).get("KCSL") != null) {
						kcsl = kcsl
								+ Double.parseDouble(kccx.get(i).get("KCSL")
										+ "");
					}
					if (kccx.get(i).get("JHJE") != null) {
						jhje = jhje
								+ Double.parseDouble(kccx.get(i).get("JHJE")
										+ "");
					}
					if (kccx.get(i).get("LSJE") != null) {
						lsje = lsje
								+ Double.parseDouble(kccx.get(i).get("LSJE")
										+ "");
					}
				}
				response.put("SUMSWKC", String.format("%1$.2f", swkc));
				response.put("SUMKCSL", String.format("%1$.2f", kcsl));
				response.put("SUMJHJE", String.format("%1$.2f", jhje));
				response.put("SUMLSJE", String.format("%1$.2f", lsje));
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
