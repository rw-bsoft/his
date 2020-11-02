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

public class FsbNurseRecordPrintFile implements IHandler {

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
			List<Map<String, Object>> hljllist = dao
					.doQuery(
							"select HLRQ as HLRQ,str(RHSJ,'YYYY-MM-DD HH24:MI:SS') as RHSJ,str(CHSJ,'YYYY-MM-DD HH24:MI:SS') as CHSJ,HLCS as HLCS,HLHS as HLHS from JC_HLJL WHERE ZYH =:ZYH and JGID=:JGID order by HLRQ",
							parameter);
			// 每页显示多少行
			int culNum = 15;
			// 总页数
			int pagNum = hljllist.size() / culNum;
			for (int i = 0; i < pagNum * culNum; i++) {
				hljllist.get(i).put(
						"HLHS",
						DictionaryController
								.instance()
								.getDic("phis.dictionary.doctor")
								.getText(
										String.valueOf(hljllist.get(i).get(
												"HLHS"))));
				records.add(hljllist.get(i));
			}
			for (int i = pagNum * culNum; i < hljllist.size(); i++) {
				hljllist.get(i).put(
						"HLHS",
						DictionaryController
								.instance()
								.getDic("phis.dictionary.doctor")
								.getText(
										String.valueOf(hljllist.get(i).get(
												"HLHS"))));
				records.add(hljllist.get(i));
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
