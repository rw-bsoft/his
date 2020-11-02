package phis.prints.bean;

import java.util.List;
import java.util.Map;

import java.util.HashMap;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class InventoryDetailsFile implements IHandler {
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb = Long.parseLong((String) user.getProperty("pharmacyId"));
		String ypxh = null;
		String sql = null;
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("YFSB", yfsb);
		parameters.put("JGID", jgid);
		if (request.get("ypxh") != null) {
			ypxh = request.get("ypxh") + "";
		}
		if (!"0".equals(ypxh)) {
			sql = "select yk_typk.YPMC as YPMC,yf_ypxx.YFGG as YFGG,yf_ypxx.YFDW as YFDW,yk_cddz.CDMC as YPCD,yf_kcmx.YPPH as YPPH,yf_kcmx.YPXQ as YPXQ,yf_kcmx.LSJG as LSJG,yf_kcmx.YPSL as KCSL from YF_KCMX yf_kcmx,YK_TYPK yk_typk,YF_YPXX yf_ypxx,YK_CDDZ yk_cddz where yk_typk.YPXH = yf_kcmx.YPXH and yf_kcmx.YPXH = yf_ypxx.YPXH and yf_kcmx.YFSB = yf_ypxx.YFSB and yf_kcmx.YPCD = yk_cddz.YPCD and yf_kcmx.YPXH in ("
					+ ypxh
					+ ") and yf_kcmx.YFSB=:YFSB and yf_kcmx.JGID=:JGID order by yf_kcmx.YPXH, yf_kcmx.YPSL";
		} else {
			sql = "select yk_typk.YPMC as YPMC,yf_ypxx.YFGG as YFGG,yf_ypxx.YFDW as YFDW,yk_cddz.CDMC as YPCD,yf_kcmx.YPPH as YPPH,yf_kcmx.YPXQ as YPXQ,yf_kcmx.LSJG as LSJG,yf_kcmx.YPSL as KCSL from YF_KCMX yf_kcmx,YK_TYPK yk_typk,YF_YPXX yf_ypxx,YK_CDDZ yk_cddz where yk_typk.YPXH = yf_kcmx.YPXH and yf_kcmx.YPXH = yf_ypxx.YPXH and yf_kcmx.YFSB = yf_ypxx.YFSB and yf_kcmx.YPCD = yk_cddz.YPCD and yf_kcmx.YFSB=:YFSB and yf_kcmx.JGID=:JGID order by yf_kcmx.YPXH, yf_kcmx.YPSL";
		}
		try {
			List<Map<String, Object>> kcmxlist = dao.doQuery(sql, parameters);
			records.addAll(kcmxlist);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		long yfsb = Long.parseLong((String) user.getProperty("pharmacyId"));
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("YFSB", yfsb);
		parameters.put("JGID", jgid);		
		String jgname = user.getManageUnit().getName();
		response.put("title", jgname + "库存明细表");
		response.put("RQ", BSHISUtil.getDate());
		StringBuffer hql = new StringBuffer("select sum(a.LSJG*a.YPSL) as KCLSJE,sum(a.JHJG*a.YPSL) as KCJHJE");
		hql.append(" from YF_KCMX a where a.JGID=:JGID and a.YFSB=:YFSB");
		try {
			Map<String, Object> sumMap = new HashMap<String, Object>();
			sumMap = dao.doLoad(hql.toString(), parameters);
			if(sumMap!=null){
				response.put("KCLSJE", String.format("%1$.4f", parseDouble(sumMap.get("KCLSJE"))));
				response.put("KCJHJE", String.format("%1$.4f", parseDouble(sumMap.get("KCJHJE"))));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}
}
