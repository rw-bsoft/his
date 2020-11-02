package phis.prints.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class HospitalBedManagementFile implements IHandler {
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		String brch = null;
		String sql = null;
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", jgid);
		if (request.get("brch") != null) {
			brch = request.get("brch") + "";
		}
		String[] brchArr = brch.split(",");
		StringBuffer brchStr = new StringBuffer();
		for(int i = 0 ; i < brchArr.length ; i ++){
			if(i!=0){
				brchStr.append(",'"+brchArr[i]+"'");
			}else{
				brchStr.append("'"+brchArr[i]+"'");
			}
		}
		brch = brchStr.toString();
		if (!"0".equals(brch)) {
			sql = "select cw.BRCH as BRCH,cw.FJHM as FJHM,cw.CWKS as CWKS,cw.KSDM as KSDM,(case when cw.CWXB='1' then '男' when cw.CWXB='2' then '女' when cw.CWXB='3' then '不限' end) as CWXB,cw.CWFY as CWFY,(case when cw.JCPB='0' then '普通' when cw.JCPB='1' then '加床' when cw.JCPB='2' then '虚床' end) as JCPB,br.ZYH as ZYH,br.ZYHM as ZYHM,br.BRXM as BRXM,br.BRXB as BRXB,br.BRXZ as BRXZ,br.BRKS as BRKS,br.BRBQ as BRBQ,br.RYRQ as RYRQ FROM ZY_CWSZ cw left join ZY_BRRY br on cw.ZYH = br.ZYH where cw.BRCH in ("
					+ brch + ") and cw.JGID=:JGID ORDER BY cw.BRCH";
		} else {
			sql = "select cw.BRCH as BRCH,cw.FJHM as FJHM,cw.CWKS as CWKS,cw.KSDM as KSDM,(case when cw.CWXB='1' then '男' when cw.CWXB='2' then '女' when cw.CWXB='3' then '不限' end) as CWXB,cw.CWFY as CWFY,(case when cw.JCPB='0' then '普通' when cw.JCPB='1' then '加床' when cw.JCPB='2' then '虚床' end) as JCPB,br.ZYH as ZYH,br.ZYHM as ZYHM,br.BRXM as BRXM,br.BRXB as BRXB,br.BRXZ as BRXZ,br.BRKS as BRKS,br.BRBQ as BRBQ,br.RYRQ as RYRQ FROM ZY_CWSZ cw left join ZY_BRRY br on cw.ZYH = br.ZYH where cw.JGID=:JGID ORDER BY cw.BRCH";
		}
		try {
			List<Map<String, Object>> kcmxlist = dao
					.doSqlQuery(sql, parameters);
			for (int i = 0; i < kcmxlist.size(); i++) {
				if (kcmxlist.get(i).get("CWKS") != null
						&& kcmxlist.get(i).get("CWKS") != "") {
					kcmxlist.get(i).put(
							"CWKS",
							DictionaryController.instance().get("phis.dictionary.department_zy")
									.getText(kcmxlist.get(i).get("CWKS") + ""));
				} else {
					kcmxlist.get(i).put("CWKS", "");
				}
				if (kcmxlist.get(i).get("KSDM") != null
						&& kcmxlist.get(i).get("KSDM") != "") {
					kcmxlist.get(i).put(
							"KSDM",
							DictionaryController.instance().get("phis.dictionary.department_bq")
									.getText(kcmxlist.get(i).get("KSDM") + ""));
				} else {
					kcmxlist.get(i).put("KSDM", "");
				}
				if (kcmxlist.get(i).get("BRXB") != null
						&& kcmxlist.get(i).get("BRXB") != "") {
					kcmxlist.get(i).put(
							"BRXB",
							DictionaryController.instance().get("phis.dictionary.gender")
									.getText(kcmxlist.get(i).get("BRXB") + ""));
				} else {
					kcmxlist.get(i).put("BRXB", "");
				}

				if (kcmxlist.get(i).get("BRXZ") != null
						&& kcmxlist.get(i).get("BRXZ") != "") {
					kcmxlist.get(i).put(
							"BRXZ",
							DictionaryController.instance().get("phis.dictionary.patientProperties_ZY")
									.getText(kcmxlist.get(i).get("BRXZ") + ""));
				} else {
					kcmxlist.get(i).put("BRXZ", "");
				}

				if (kcmxlist.get(i).get("BRKS") != null
						&& kcmxlist.get(i).get("BRKS") != "") {
					kcmxlist.get(i).put(
							"BRKS",
							DictionaryController.instance().get("phis.dictionary.department_zy")
									.getText(kcmxlist.get(i).get("BRKS") + ""));
				} else {
					kcmxlist.get(i).put("BRKS", "");
				}
				if (kcmxlist.get(i).get("ZYHM") != null) {
					kcmxlist.get(i).put("SFYR", "有");
				} else {
					kcmxlist.get(i).put("SFYR", "无");
				}
			}
			records.addAll(kcmxlist);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnitName();
		response.put("title", jgname);
	}
}
