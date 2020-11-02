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

public class InitialTransferFile implements IHandler {
	
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("jgid", jgid);
		BaseDAO dao = new BaseDAO(ctx);
		try {
			StringBuffer drsjSql = new StringBuffer();
			drsjSql.append("select yk_cdxx0_.JGID as JGID, yk_cdxx0_.YPXH as YPXH, yk_cdxx0_.YPCD as YPCD, yk_typk1_.YPMC as YPMC, yk_typk1_.YPGG as YPGG, " +
					"yk_typk1_.YPDW as YPDW, yk_cddz2_.CDMC as CDMC, yk_cdxx0_.KCSL as KCSL, yk_cdxx0_.JHJG as JHJG, yk_cdxx0_.PFJG as PFJG, yk_cdxx0_.LSJG as LSJG, " +
					"yk_cdxx0_.JHJE as JHJE, yk_cdxx0_.PFJE as PFJE, yk_cdxx0_.LSJE as LSJE, yk_cdxx0_.PZWH as PZWH, yk_cdxx0_.GMP as GMP, " +
					"yk_cdxx0_.DJFS as DJFS, yk_cdxx0_.DJGS as DJGS, yk_ypcd3_.ZFPB as ZFPB, yk_ypxx4_.YKSB as YKSB, yk_typk1_.PYDM as PYDM, " +
					"yk_cdxx0_.ZFPB as ZFPB, yk_cdxx0_.GYJJ as GYJJ, yk_cdxx0_.GYLJ as GYLJ from YK_CDXX yk_cdxx0_, YK_TYPK yk_typk1_, YK_CDDZ yk_cddz2_, " +
					"YK_YPCD yk_ypcd3_, YK_YPXX yk_ypxx4_ where yk_cdxx0_.YPXH=yk_typk1_.YPXH and yk_cdxx0_.YPCD=yk_cddz2_.YPCD and yk_cdxx0_.YPXH=yk_ypcd3_.YPXH " +
					"and yk_cdxx0_.YPCD=yk_ypcd3_.YPCD and yk_cdxx0_.JGID=yk_ypxx4_.JGID and yk_cdxx0_.YPXH=yk_ypxx4_.YPXH and yk_ypxx4_.YKSB='1' " +
					"and yk_cdxx0_.JGID=:jgid");
			String sbxh = null;
			if (request.get("sbxh") != null) {
				sbxh = request.get("sbxh") + "";
				if(!"0".equals(sbxh)){
					drsjSql.append(" and (yk_cdxx0_.JGID || '_' || yk_cdxx0_.YPXH || '_' || yk_cdxx0_.YPCD) in ("+sbxh+")");
				}
			}
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
		response.put("title", jgname + "-初始转帐");
	}
}
