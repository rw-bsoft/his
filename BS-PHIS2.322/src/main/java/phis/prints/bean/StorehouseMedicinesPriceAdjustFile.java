package phis.prints.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.CNDHelper;
import phis.source.utils.SchemaUtil;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class StorehouseMedicinesPriceAdjustFile implements IHandler{
	@SuppressWarnings("unchecked")
	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		StringBuffer hql=new StringBuffer();
		hql.append("select a.TJDH as TJDH,a.TJRQ as TJRQ,a.TJFS as TJFS,b.YKMC as YKMC from YK_TJ01 a,YK_YKLB b where a.XTSB=:xtsb and a.TJDH=:tjdh and a.TJFS=:tjfs and a.XTSB=b.YKSB");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("xtsb", MedicineUtils.parseLong(request.get("XTSB")));
		map_par.put("tjfs", MedicineUtils.parseInt(request.get("TJFS")));
		map_par.put("tjdh", MedicineUtils.parseInt(request.get("TJDH")));
		try {
			Map<String,Object> map_tjxx=dao.doQuery(hql.toString(), map_par).get(0);
			SchemaUtil.setDictionaryMassageForForm(map_tjxx, "phis.application.sto.schemas.YK_TJ01");
			response.putAll(map_tjxx);
			response.put("TJFS", ((Map<String,Object>)map_tjxx.get("TJFS")).get("text"));
			response.put("TITLE", UserRoleToken.getCurrent().getManageUnitName()+map_tjxx.get("YKMC")+"调价单");
			response.put("MYHS", 20);
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000,"打印数据查询失败"+e.getMessage());
		}
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		List<?> cnd=CNDHelper.createArrayCnd("and",CNDHelper.createSimpleCnd("eq", "a.JGID", "i", MedicineUtils.parseString(request.get("JGID"))), CNDHelper.createArrayCnd("and", CNDHelper.createSimpleCnd("eq", "a.TJFS", "i", MedicineUtils.parseInt(request.get("TJFS"))), CNDHelper.createSimpleCnd("eq", "a.TJDH", "i", MedicineUtils.parseInt(request.get("TJDH")))));     
		try {
			records.addAll(dao.doList(cnd, null, BSPHISEntryNames.YF_TJJL_ZX));
			records.addAll(dao.doList(cnd, null, BSPHISEntryNames.YK_TJJL));
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000,"打印数据查询失败"+e.getMessage());
		} 
	}

}
