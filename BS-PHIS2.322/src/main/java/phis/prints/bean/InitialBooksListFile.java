package phis.prints.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class InitialBooksListFile implements IHandler {
	
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("jgid", jgid);
		parameters.put("yfsb", yfsb);
		BaseDAO dao = new BaseDAO(ctx);
		try {
			StringBuffer drsjSql = new StringBuffer();
			drsjSql.append("select yk.YPMC as YPMC,yf.YFGG as YFGG,yf.YFDW as YFDW,dz.CDMC as CDMC,kc.LSJG as LSJG,kc.YPSL as YPSL,kc.YPPH as YPPH,kc.YPXQ as YPXQ,kc.JHJG as JHJG,kc.JHJE as JHJE" +
					" from YK_TYPK yk,YF_YPXX yf,YK_CDXX cd,YK_YPXX yp,YK_CDDZ dz,YF_KCMX kc where yk.ZFPB!=1 and yp.YKZF!=1 and yf.YFZF!=1 and cd.ZFPB!=1 and dz.YPCD=cd.YPCD and cd.YPCD = kc.YPCD " +
					"and yp.JGID=yf.JGID and cd.JGID=yf.JGID and yp.JGID = kc.JGID and yp.YPXH=yk.YPXH and cd.YPXH=yf.YPXH  and yk.YPXH=yf.YPXH and yp.YPXH = kc.YPXH " +
					"and yf.JGID=:jgid and yf.YFSB=:yfsb");
			String sbxh = null;
			if (request.get("sbxh") != null) {
				sbxh = request.get("sbxh") + "";
				if(!"0".equals(sbxh)){
					drsjSql.append(" and kc.SBXH in ("+sbxh+")");
				}
			}
			drsjSql.append(" order by yp.YPXH");
			List<Map<String, Object>> kcmxlist = dao.doSqlQuery(
					drsjSql.toString(), parameters);
			records.addAll(kcmxlist);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} 
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnitName();
		response.put("title", jgname + "-初始账簿");
	}
}
