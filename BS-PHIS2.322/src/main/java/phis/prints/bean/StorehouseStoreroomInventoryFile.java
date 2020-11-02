package phis.prints.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.SchemaUtil;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class StorehouseStoreroomInventoryFile implements IHandler{
	@SuppressWarnings("unchecked")
	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		long yksb=MedicineUtils.parseLong(request.get("xtsb"));
		String pddh=MedicineUtils.parseString(request.get("pddh"));
		BaseDAO dao = new BaseDAO(ctx);
		StringBuffer hql_pdxx=new StringBuffer();
		hql_pdxx.append("select CZGH as CZGH,PDRQ as PDRQ from YK_PD01 where XTSB=:xtsb and PDDH=:pddh");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("xtsb", yksb);
		map_par.put("pddh", pddh);
		StringBuffer hql_count=new StringBuffer();
		hql_count.append("XTSB=:xtsb and PDDH=:pddh");
		try {
			Map<String,Object> map_ykxx=dao.doLoad("phis.application.sto.schemas.YK_YKLB", yksb);
			Map<String,Object> map_pdxx=dao.doLoad(hql_pdxx.toString(), map_par);
			SchemaUtil.setDictionaryMassageForForm(map_pdxx, "phis.application.sto.schemas.YK_PD01");
			response.put("TITLE", map_ykxx.get("YKMC")+"库存盘点");
			response.put("PDDH", pddh);
			response.put("PDR", ((Map<String,Object>)map_pdxx.get("CZGH")).get("text"));
			response.put("PDRQ", map_pdxx.get("PDRQ"));
			response.put("ZBR", UserRoleToken.getCurrent().getUserName());
			response.put("MYHS", 28);//每页行数,int类型
			response.put("YPHJ", dao.doCount("YK_PD02", hql_count.toString(), map_par));
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000,e.getMessage());
		}
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		StringBuffer hql=new StringBuffer();
		hql.append("select b.YPMC as YPMC,b.YPGG as YPGG,b.YPDW as YPDW,c.CDMC as SCCJ,a.ZMSL as ZMSL,a.SPSL as SPSL,a.LSJG as LSJG,a.LSJE as LSJE,a.JHJG as JHJG,a.JHJE as JHJE,a.YPPH as YPPH,to_char(a.YPXQ,'yyyy-MM-dd') as YPXQ from YK_PD02 a,YK_TYPK b,YK_CDDZ c where a.YPXH=b.YPXH and a.YPCD=c.YPCD and a.XTSB=:xtsb and a.PDDH=:pddh");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("xtsb", MedicineUtils.parseLong(request.get("xtsb")));
		map_par.put("pddh", MedicineUtils.parseString(request.get("pddh")));
		BaseDAO dao = new BaseDAO(ctx);
		try {
			List<Map<String,Object>> list_pdxx=dao.doQuery(hql.toString(), map_par);
			records.addAll(list_pdxx);
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000,e.getMessage());
		}
		
	}

}
