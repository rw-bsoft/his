package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

public class NursePlanPrintFile implements IHandler {

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
			List<Map<String, Object>> hljhlist = dao.doQuery(
					"select JLBH as JLBH,KSRQ as KSRQ,HLZD as HLZD,HLMB as HLMB,HLCS as HLCS,HLPJ as HLPJ,TZRQ as TZRQ," +
					" to_char(KSRQ,'MM-DD') as KSRQ,to_char(KSRQ,'HH24:MI') as KSSJ," +
					" to_char(TZRQ,'MM-DD') as JSRQ,to_char(TZRQ,'HH24:MI') as JSSJ" +
					" from EMR_HLJH WHERE ZYH =:ZYH and JGID=:JGID order by KSRQ",
					parameter);
			
			// 每页显示多少行
			int culNum = 15;
			// 总页数
			int pagNum = hljhlist.size() / culNum;
			for (int i = 0; i < pagNum * culNum; i++) {
				records.add(hljhlist.get(i));
			}
			for (int i = pagNum * culNum; i < hljhlist.size(); i++) {
				records.add(hljhlist.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		Long zyh = 0l;
		if (request.get("zyh") != null) {
			zyh = Long.parseLong(request.get("zyh") + "");
		}

		parameter.put("ZYH", zyh);
		String sql = "select BRKS as KB,BRXM as XM,RYNL as NL,BRXB as XB ,BRCH as CH,BAHM as ZYBL,"
				+ " CSNY as CSNY, RYRQ as RYRQ,RYZD as ZD from ZY_BRRY where zyh=:ZYH";
		String jbmcSql = "select b.JBMC as JBMC from ZY_RYZD a , GY_JBBM b where a.ZYH = :ZYH and a.ZDXH = b.JBXH ";
		try {
			
			Map<String, Object> headermap = dao.doLoad(sql, parameter);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (null != headermap.get("KB")) {
				
				response.put("KB", DictionaryController.instance().getDic("phis.dictionary.department").getText(String.valueOf(headermap.get("KB"))));
//				response.put("KB", headermap.get("KB")+"");
			}
			if (null != headermap.get("XM")) {
				response.put("XM", headermap.get("XM")+"");
			}
			if (null != headermap.get("CSNY")) {
				String csny = String.valueOf(headermap.get("CSNY"));
				int nl = EHRUtil.calculateAge(BSHISUtil.toDate(csny), new Date());
				response.put("NL", nl + "");
//				response.put("NL", headermap.get("NL")+"");
			}
			if (null != headermap.get("XB")) {
				response.put("XB", DictionaryController.instance().getDic("phis.dictionary.gender").getText(String.valueOf(headermap.get("XB"))));
//				response.put("XB", headermap.get("XB")+"");
			}
			if (null != headermap.get("CH")) {
				response.put("CH", headermap.get("CH")+"");
			}
			if (null != headermap.get("ZYBL")) {
				response.put("ZYBL", headermap.get("ZYBL")+"");
			}
			if (null != headermap.get("RYRQ")) {
				response.put("RYRQ", sdf.format(BSHISUtil.toDate(headermap
						.get("RYRQ") + "")));
			}
			List<Map<String, Object>> list = dao.doSqlQuery(jbmcSql, parameter);
			if(list != null && list.size() > 0){
				Map<String, Object> map = null;
				StringBuilder zdBuilder = new StringBuilder();
				for(int i =0; i<list.size(); i++){
					map = list.get(i);
					zdBuilder.append(map.get("JBMC"));
					if(i< list.size()-1){
						zdBuilder.append("、");
					}
				}
				response.put("ZD", zdBuilder.toString());
			}
//			if (null != headermap.get("ZD")) {
//				response.put("ZD", headermap.get("ZD")+"");
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
