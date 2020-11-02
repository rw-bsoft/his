package phis.prints.bean;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.utils.BSHISUtil;
import phis.source.utils.EHRUtil;

import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class FsbNursePlanPrintFile implements IHandler {

	@SuppressWarnings("deprecation")
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		Long zyh = 0l;
		if (request.get("zyh") != null) {
			zyh = Long.parseLong(request.get("zyh") + "");
		}

		try {
			parameter.put("ZYH", zyh);
			parameter.put("JGID", jgid);
			List<Map<String, Object>> hljhlist = dao
					.doQuery(
							"select JLBH as JLBH,KSRQ as KSRQ,HLZD as HLZD,HLMB as HLMB,HLCS as HLCS,HLPJ as HLPJ,TZRQ as TZRQ,to_char(KSRQ,'MM-DD') as KSRQ,to_char(KSRQ,'HH24:MI') as KSSJ,to_char(TZRQ,'MM-DD') as JSRQ,to_char(TZRQ,'HH24:MI') as JSSJ,HLHS as HLHS from JC_HLJH WHERE ZYH =:ZYH and JGID=:JGID order by KSRQ",
							parameter);

			// 每页显示多少行
			int culNum = 15;
			// 总页数
			int pagNum = hljhlist.size() / culNum;
			for (int i = 0; i < pagNum * culNum; i++) {
				hljhlist.get(i).put(
						"HLHS",
						DictionaryController
								.instance()
								.getDic("phis.dictionary.doctor")
								.getText(
										String.valueOf(hljhlist.get(i).get(
												"HLHS"))));
				records.add(hljhlist.get(i));
			}
			for (int i = pagNum * culNum; i < hljhlist.size(); i++) {
				hljhlist.get(i).put(
						"HLHS",
						DictionaryController
								.instance()
								.getDic("phis.dictionary.doctor")
								.getText(
										String.valueOf(hljhlist.get(i).get(
												"HLHS"))));
				records.add(hljhlist.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		Long zyh = 0l;
		if (request.get("zyh") != null) {
			zyh = Long.parseLong(request.get("zyh") + "");
		}

		parameter.put("ZYH", zyh);
		String sql = "select ZYHM as ZYHM,BRXM as BRXM,BRXB as BRXB,CSNY as CSNY from JC_BRRY where zyh=:ZYH";
		try {

			Map<String, Object> headermap = dao.doLoad(sql, parameter);
			if (null != headermap.get("ZYHM")) {
				response.put("ZYHM", headermap.get("ZYHM") + "");
			}
			if (null != headermap.get("BRXM")) {
				response.put("BRXM", headermap.get("BRXM") + "");
			}
			if (null != headermap.get("CSNY")) {
				String csny = String.valueOf(headermap.get("CSNY"));
				int nl = EHRUtil.calculateAge(BSHISUtil.toDate(csny),
						new Date());
				response.put("RYNL", nl + "岁");
			}
			if (null != headermap.get("BRXB")) {
				response.put(
						"BRXB",
						DictionaryController.instance()
								.getDic("phis.dictionary.gender")
								.getText(String.valueOf(headermap.get("BRXB"))));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
