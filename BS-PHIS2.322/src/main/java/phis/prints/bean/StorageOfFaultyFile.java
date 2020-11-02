package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;

import ctd.schema.SchemaController;
import ctd.util.context.Context;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.print.IHandler;
import ctd.print.PrintException;

//DynamicPrint
public class StorageOfFaultyFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Long djxh = 0l;
		if (request.get("djxh") != null) {
			djxh = Long.parseLong(request.get("djxh") + "");
		}
		try {
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "DJXH", "d", djxh);
			list = dao.doList(cnd, null, BSPHISEntryNames.WL_BS02);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> m = list.get(i);
			records.add(m);
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> zk = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		Long djxh = 0l;
		if (request.get("djxh") != null) {
			djxh = Long.parseLong(request.get("djxh") + "");
		}
		try {
			List<?> cnd = CNDHelper.createSimpleCnd("eq", SchemaController.instance().get(BSPHISEntryNames.WL_BS01_FORM_CON).getKeyItem().getId(), "d", djxh);
			list = dao.doList(cnd, null, BSPHISEntryNames.WL_BS01_FORM_CON);
			zk = list.get(0);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();
		response.put("TITLE", jgname);
		response.put("TITLE2", "（固定资产）报损单");

		response.put("ZBLB", zk.get("ZBLB_text")); //帐薄类别
		response.put("BSFS", zk.get("BSFS_text")); //报损方式
		response.put("BSKS", zk.get("BSKS_text")); //报损科室
		if (null != zk.get("BSRQ")) {
			response.put("BSRQ", sdf.format(BSHISUtil.toDate(zk.get("BSRQ")+""))); //报损日期
		}
		response.put("LZDH", zk.get("LZDH") == null ? "":zk.get("LZDH") + ""); //No
		response.put("DJJE", "￥"+Double.parseDouble(zk.get("DJJE")+ "")); //总计
		response.put("DJJE_DX", BSPHISUtil.changeMoneyUpper(Double.parseDouble(zk.get("DJJE")+ ""))); //总计大写
		response.put("ZDGH", zk.get("ZDGH_text")); //制单

	}
}
