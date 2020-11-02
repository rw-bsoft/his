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
public class StorageOfTransferFile implements IHandler {

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
			list = dao.doList(cnd, null, BSPHISEntryNames.WL_ZK02);
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
			List<?> cnd = CNDHelper.createSimpleCnd("eq", SchemaController
					.instance().get(BSPHISEntryNames.WL_ZK01).getKeyItem()
					.getId(), "d", djxh);
			list = dao.doList(cnd, null, BSPHISEntryNames.WL_ZK01);
			zk = list.get(0);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();
		response.put("TITLE", jgname);
		Map<String, Object> zblbpar = new HashMap<String, Object>();
		zblbpar.put("ZBLB", Long.parseLong(request.get("zblb") + ""));
		try {
			String zbmc = dao
					.doLoad("select ZBMC as ZBMC from WL_ZBLB where ZBLB=:ZBLB",
							zblbpar).get("ZBMC")
					+ "";
			response.put("TITLE2", "(" + zbmc + ")转科单");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		response.put("LZFS", zk.get("LZFS_text")); // 转科方式
		response.put("ZCKS", zk.get("ZCKS_text")); // 转出科室
		response.put("ZRKS", zk.get("ZRKS_text")); // 转入科室
		if (null != zk.get("JZRQ")) {
			response.put("JZRQ",
					sdf.format(BSHISUtil.toDate(zk.get("JZRQ") + ""))); // 转科日期
		}
		response.put("LZDH", zk.get("LZDH") == null ? "" : zk.get("LZDH") + ""); // No
		response.put("DJJE", "￥" + Double.parseDouble(zk.get("DJJE") + "")); // 总计
		response.put(
				"DJJE_DX",
				BSPHISUtil.changeMoneyUpper(Double.parseDouble(zk.get("DJJE")
						+ ""))); // 总计大写
		response.put("SHGH", zk.get("SHGH_text")); // 复核人
		response.put("JZGH", zk.get("JZGH_text")); // 记账人
		response.put("ZRGH", zk.get("ZRGH_text")); // 转入人
		response.put("ZDGH", zk.get("ZDGH_text")); // 制单

	}
}
