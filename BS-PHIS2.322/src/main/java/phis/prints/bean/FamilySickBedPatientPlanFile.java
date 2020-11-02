package phis.prints.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;

import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class FamilySickBedPatientPlanFile implements IHandler {

	@SuppressWarnings("deprecation")
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		Long zyh = 0l;
		if (request.get("ZYH") != null) {
			zyh = Long.parseLong(request.get("ZYH") + "");
		}

		try {
			parameter.put("ZYH", zyh);
			parameter.put("JGID", jgid);
			List<Map<String, Object>> zljhlist = dao
					.doSqlQuery(
							"select a.XMMC as XMMC,a.YCJL as YCJL,b.JLDW as JLDW,a.SYPC as SYPC,a.GYTJ as GYTJ,a.KSSJ as KSSJ,a.JSSJ as JSSJ,a.YSDM as YSDM from JC_ZLJH a left join YK_TYPK b on a.XMBH=b.YPXH and a.XMLX=1 WHERE ZYH =:ZYH and JGID=:JGID order by a.YPZH,a.KSSJ",
							parameter);

			// 每页显示多少行
			int culNum = 15;
			// 总页数
			int pagNum = zljhlist.size() / culNum;
			for (int i = 0; i < pagNum * culNum; i++) {
				zljhlist.get(i).put(
						"SYPC",
						DictionaryController
								.instance()
								.getDic("phis.dictionary.useRate")
								.getText(
										String.valueOf(zljhlist.get(i).get(
												"SYPC"))));
				zljhlist.get(i).put(
						"GYTJ",
						DictionaryController
								.instance()
								.getDic("phis.dictionary.drugMode")
								.getText(
										String.valueOf(zljhlist.get(i).get(
												"GYTJ"))));
				zljhlist.get(i).put(
						"YSDM",
						DictionaryController
								.instance()
								.getDic("phis.dictionary.doctor")
								.getText(
										String.valueOf(zljhlist.get(i).get(
												"YSDM"))));
				records.add(zljhlist.get(i));
			}
			for (int i = pagNum * culNum; i < zljhlist.size(); i++) {
				zljhlist.get(i).put(
						"SYPC",
						DictionaryController
								.instance()
								.getDic("phis.dictionary.useRate")
								.getText(
										String.valueOf(zljhlist.get(i).get(
												"SYPC"))));
				zljhlist.get(i).put(
						"GYTJ",
						DictionaryController
								.instance()
								.getDic("phis.dictionary.drugMode")
								.getText(
										String.valueOf(zljhlist.get(i).get(
												"GYTJ"))));
				zljhlist.get(i).put(
						"YSDM",
						DictionaryController
								.instance()
								.getDic("phis.dictionary.doctor")
								.getText(
										String.valueOf(zljhlist.get(i).get(
												"YSDM"))));
				records.add(zljhlist.get(i));
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
		UserRoleToken user = UserRoleToken.getCurrent();
		response.put("TITLE", user.getManageUnitName() + "诊疗计划单");
		Long zyh = 0l;
		if (request.get("ZYH") != null) {
			zyh = Long.parseLong(request.get("ZYH") + "");
		}

		parameter.put("ZYH", zyh);
		String sql = "select ZYHM as ZYHM,BRXM as BRXM,BRXB as BRXB,RYNL as RYNL,JCZD as RYZD from JC_BRRY where zyh=:ZYH";
		try {

			Map<String, Object> headermap = dao.doLoad(sql, parameter);
			if (null != headermap.get("ZYHM")) {
				response.put("ZYHM", headermap.get("ZYHM") + "");
			}
			if (null != headermap.get("BRXM")) {
				response.put("BRXM", headermap.get("BRXM") + "");
			}
			if (null != headermap.get("RYNL")) {
//				String csny = String.valueOf(headermap.get("CSNY"));
//				int nl = EHRUtil.calculateAge(BSHISUtil.toDate(csny),
//						new Date());
//				response.put("RYNL", nl + "岁");
				response.put("RYNL", headermap.get("RYNL")+"岁");
			}
			if (null != headermap.get("BRXB")) {
				response.put(
						"BRXB",
						DictionaryController.instance()
								.getDic("phis.dictionary.gender")
								.getText(String.valueOf(headermap.get("BRXB"))));
			}
			if (null != headermap.get("RYZD")) {
				response.put("RYZD", headermap.get("RYZD") + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
