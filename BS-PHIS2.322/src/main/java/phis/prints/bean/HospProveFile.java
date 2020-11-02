package phis.prints.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.SchemaUtil;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class HospProveFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
	  }

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> obj = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		String TITLE = user.getManageUnitName();// 用户的机构ID
		Long zyh = 0l;
		if (request.get("ZYH") != null) {
			zyh = Long.parseLong(request.get("ZYH") + "");
		}
		parameters.put("ZYH", zyh);
		parameters.put("JGID", manageUnit);
		try {
			StringBuffer sql = new StringBuffer(
					" select a.ZYH as ZYH,a.ZYHM,a.BRXM,a.BRCH,a.BRXB,a.GZDW,a.RYQK,a.BRKS,a.CYPB,"
							+ BSPHISUtil.toChar("a.RYRQ",
									"yyyy-mm-dd HH24:mi:ss")
							+ " as RYRQ,"
							+ BSPHISUtil.toChar("b.LCRQ",
									"yyyy-mm-dd HH24:mi:ss")
							+ " as CYRQ,b.CYFS,b.BZXX,");
			sql.append(BSPHISUtil.toChar("a.CYRQ","yyyy-mm-dd HH24:mi:ss") + "as CYRQ");
			sql.append(" from ZY_BRRY a");
			sql.append(" left join ZY_RCJL b on (a.ZYH=b.ZYH and a.JGID=b.JGID and b.BQPB=0 and b.CZLX=-1 ) ");
			sql.append(" where a.JGID=:JGID and a.ZYH=:ZYH");
			list = dao.doSqlQuery(sql.toString(),
					parameters);
			list = SchemaUtil.setDictionaryMassageForList(list,
					"phis.application.war.schemas.ZY_BRRY_CY2");
			if (list.size() > 0) {
				obj = list.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.put("TITLE", TITLE);
			/*if (request.get("ZYH") != null) {
				response.put("ZYH", request.get("ZYH"));
			}*/
		if (obj.get("ZYHM") != null) {
			response.put("ZYH", obj.get("ZYHM"));
		}
		if (obj.get("BRXM") != null) {
			response.put("BRXM", obj.get("BRXM"));
		}
		if (obj.get("BRCH") != null) {
			response.put("BRCH", obj.get("BRCH"));
		}
		if (obj.get("BRXB") != null) {
			response.put("BRXB", obj.get("BRXB_text"));
		}
		if (obj.get("GZDW") != null) {
			response.put("GZDW", obj.get("GZDW"));
		}
		if (obj.get("BRKS") != null) {
			response.put("BRKS", obj.get("BRKS_text"));
		}
		if (obj.get("RYRQ") != null) {
			response.put("RYRQ", obj.get("RYRQ"));
		}
		if (obj.get("CYRQ") != null) {
			response.put("XCYRQ", obj.get("CYRQ"));
		}
		if (obj.get("RYQK") != null) {
			response.put("RYQK", obj.get("RYQK_text"));
		}
		if (obj.get("CYFS") != null) {
			response.put("CYFS", obj.get("CYFS_text"));
		}
		if (obj.get("BZXX") != null) {
			response.put("BZXX", obj.get("BZXX"));
		}
		if (request.get("DAYS") != null && !request.get("DAYS").equals("null")) {
			response.put("DAYS", request.get("DAYS"));
		}
	}
}
