package phis.prints.bean;

import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.application.pha.source.PharmacyDrugsExpireTipsModel;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class PharmacyDrugsExpireTipsFile implements IHandler{

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		BaseDAO dao = new BaseDAO(ctx);
		try {
			Map<String,Object> map_yf=dao.doLoad("phis.application.pha.schemas.YF_YFLB", MedicineUtils.parseLong(user.getProperty("pharmacyId")));
			response.put("JGYF", user.getManageUnit().getName()+map_yf.get("YFMC"));
			response.put("JZRQ", request.get("JZRQ_PRINT"));
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000,"查询药房失败"+e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		PharmacyDrugsExpireTipsModel model=new PharmacyDrugsExpireTipsModel(dao);
		try {
			request.put("print", true);
			Map<String, Object> ret = model.queryPharmacyDrugsExpireTips(request,request);
			if(ret!=null){
				records.addAll((List<Map<String,Object>>)ret.get("body"));
			}
		} catch (ModelDataOperationException e) {
			throw new PrintException(9000,"药房失效药品打印失败"+e.getMessage());
		}
		
	}

}
