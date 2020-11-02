package phis.prints.bean;

import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.application.pha.source.PharmacyInventoryManageModel;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class PharmacyGDCFile implements IHandler{

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		BaseDAO dao = new BaseDAO(ctx);
		try {
			Map<String,Object> map_yf=dao.doLoad("phis.application.pha.schemas.YF_YFLB", MedicineUtils.parseLong(user.getProperty("pharmacyId")));
			response.put("JGMC", user.getManageUnit().getName()+map_yf.get("YFMC"));
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000,"查询药房失败"+e.getMessage());
		}
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		PharmacyInventoryManageModel model = new PharmacyInventoryManageModel(
				dao);
		try {
			String pydm=(String)request.get("PYDM");
			records.addAll(model.queryYFGDC(pydm,ctx));
		} catch (ModelDataOperationException e) {
			throw new PrintException(9000,"查询药房高低储失败"+e.getMessage());
		}
	}

}
