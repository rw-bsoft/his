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

public class StorehouseProcurementPlanFile implements IHandler{

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgmc=user.getManageUnit().getName();
		response.put("JGMC", jgmc);
		BaseDAO dao = new BaseDAO(ctx);	
		StringBuffer hql=new StringBuffer();
		hql.append("select a.JHDH as JHDH,b.DWMC as GHDW,a.BZRQ as BZRQ,a.JHBZ as JHBZ from YK_JH01 a,YK_JHDW b where a.DWXH=b.DWXH and a.XTSB=:xtsb and a.JHDH=:jhdh");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("xtsb", MedicineUtils.parseLong(request.get("XTSB")));
		map_par.put("jhdh", MedicineUtils.parseInt(request.get("JHDH")));
		try {
			Map<String,Object> m=dao.doLoad(hql.toString(), map_par);
			response.putAll(m);
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000,"查询失败"+e.getMessage());
		}
		
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);	
		StringBuffer hql=new StringBuffer();
		hql.append("select b.YPMC as YPMC,b.YPGG as YPGG,b.YPDW as YPDW,c.CDMC as SCCJ,a.GJJG as JG,a.JHSL as JHSL,a.SPSL as SPSL,a.GJJE as JE from YK_JH02 a,YK_TYPK b,YK_CDDZ c where a.YPXH=b.YPXH and a.YPCD=c.YPCD and a.XTSB=:xtsb and a.JHDH=:jhdh");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("xtsb", MedicineUtils.parseLong(request.get("XTSB")));
		map_par.put("jhdh", MedicineUtils.parseInt(request.get("JHDH")));
		try {
			List<Map<String,Object>> l=dao.doQuery(hql.toString(), map_par);
			records.addAll(l);
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000,"查询失败"+e.getMessage());
		}
	}

}
