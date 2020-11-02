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

public class MedicinesPricePhysicalDetailsFile implements IHandler {
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		String ypxh = null;
		if (request.get("ypxh") != null) {
			ypxh = request.get("ypxh") + "";
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yksb = Long.parseLong(user.getProperty("storehouseId")+"");// 用户的药库识别
		parameters.put("YKSB", yksb);
		parameters.put("JGID", jgid);
		BaseDAO dao = new BaseDAO(ctx);
		try {
			StringBuffer hql = new StringBuffer();
			if (!"".equals(ypxh)) {
				if (!"0".equals(ypxh)) {
					hql.append("SELECT a.YPMC as YPMC,a.YPGG as YPGG,a.YPDW as YPDW,b.CDMC as CDMC,sum(c.KCSL) as KCSL,c.YPPH as YPPH,c.YPXQ as YPXQ,c.TYPE as TYPE,a.PYDM as PYDM,a.WBDM as WBDM,a.JXDM as JXDM,a.QTDM as QTDM,c.YPXH as YPXH,c.YPCD as YPCD,a.YPDM as YPDM,d.KWBM as KWBM,d.YKZF as YKZF,a.ZBLB as ZBLB FROM ");
					hql.append( " YK_CDDZ b,");
					hql.append( " YK_KCMX c,");
					hql.append( " YK_TYPK a,");
					hql.append( " YK_YPXX d WHERE ( b.YPCD = c.YPCD ) and  c.JGID = d.JGID and ( c.YPXH = a.YPXH ) and ( a.YPXH = d.YPXH ) and  ( c.JGID = d.JGID ) and ( d.YKSB = :YKSB ) and ( d.JGID = :JGID ) and( a.YPXH in ("
							+ ypxh + "))");
					hql.append(" GROUP BY a.YPMC,a.YPGG,a.YPDW,b.CDMC,c.YPPH,c.YPXQ,c.TYPE,a.PYDM,a.WBDM,a.JXDM,   a.QTDM,c.YPXH,c.YPCD,a.YPDM,d.KWBM,d.YKZF,a.ZBLB");
				} else {
					hql.append("SELECT a.YPMC as YPMC,a.YPGG as YPGG,a.YPDW as YPDW,b.CDMC as CDMC,sum(c.KCSL) as KCSL,c.YPPH as YPPH,c.YPXQ as YPXQ,c.TYPE as TYPE,a.PYDM as PYDM,a.WBDM as WBDM,a.JXDM as JXDM,a.QTDM as QTDM,c.YPXH as YPXH,c.YPCD as YPCD,a.YPDM as YPDM,d.KWBM as KWBM,d.YKZF as YKZF,a.ZBLB as ZBLB FROM ");
					hql.append(" YK_CDDZ b,");
					hql.append(" YK_KCMX c,");
					hql.append(" YK_TYPK a,");
					hql.append( " YK_YPXX d WHERE ( b.YPCD = c.YPCD ) and  c.JGID = d.JGID and ( c.YPXH = a.YPXH ) and ( a.YPXH = d.YPXH ) and  ( c.JGID = d.JGID ) and ( d.YKSB = :YKSB ) and ( d.JGID = :JGID ) ");
					hql.append(" GROUP BY a.YPMC,a.YPGG,a.YPDW,b.CDMC,c.YPPH,c.YPXQ,c.TYPE,a.PYDM,a.WBDM,a.JXDM,a.QTDM,c.YPXH,c.YPCD,a.YPDM,d.KWBM,d.YKZF,a.ZBLB");
				}
				hql.append(" order by c.YPXH");
				List<Map<String, Object>> kccx = dao.doSqlQuery(hql.toString(),
						parameters);
				for (int i = 0; i < kccx.size(); i++) {
					if ("1".equals(kccx.get(i).get("TYPE") + "")) {
						kccx.get(i).put("TYPE", "合格");
					}
					if ("2".equals(kccx.get(i).get("TYPE") + "")) {
						kccx.get(i).put("TYPE", "次品");
					}
					if ("3".equals(kccx.get(i).get("TYPE") + "")) {
						kccx.get(i).put("TYPE", "伪劣");
					}
					if ("4".equals(kccx.get(i).get("TYPE") + "")) {
						kccx.get(i).put("TYPE", "破损");
					}
					if ("5".equals(kccx.get(i).get("TYPE") + "")) {
						kccx.get(i).put("TYPE", "霉变");
					}
				}
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
		String jgname = user.getManageUnitName();
		response.put("title", jgname);
	}
}
