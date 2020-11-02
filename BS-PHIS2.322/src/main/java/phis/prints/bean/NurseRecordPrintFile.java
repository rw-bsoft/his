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
import phis.source.utils.ParameterUtil;


import ctd.account.UserRoleToken;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class NurseRecordPrintFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
	//UserRoleToken user1 = UserRoleToken.getCurrent();
		//
		//String jgid = user1.getManageUnitId();// 用户的机构ID
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
	//	String jgname = user.getManageUnit().getName();
		String jgid = user.getManageUnitId();
		//Map<String, Object> parameters = new HashMap<String, Object>();
		//Map<String, Object> parametersjzhm = new HashMap<String, Object>();

		Long zyh = 0l;
		if (request.get("zyh") != null) {
			zyh = Long.parseLong(request.get("zyh") + "");
		}
		
		try {
			List<Map<String, Object>> jgbhlist = dao.doQuery(
					"select JGBH as JGBH from ENR_JG01 WHERE JGMC = '一般护理记录单'",
					parameter);
			parameter.put("ZYH", zyh);
			long JGBH = 0;
			if(jgbhlist.size()>0){
				JGBH = Long.parseLong(jgbhlist.get(0).get("JGBH") + "");
			}
			parameter.put("JGBH",JGBH);
			//parameter.put("jgname",jgname);
		
			String sql = "SELECT JLBH   as JLBH,ZYH    as ZYH ,to_char(JLSJ,'MM-DD') as JLRQ1,to_char(JLSJ,'HH24:MI') as JLSJ,SXHS as SXHS FROM ENR_JL01"
					+ " WHERE (ZYH =:ZYH)  AND (JGBH =:JGBH)  AND JLZT <> 9 order by JLRQ1,JLSJ ";
			List<Map<String, Object>> nrlist = dao.doQuery(sql, parameter);

			Dictionary sysUser = DictionaryController.instance().get("phis.dictionary.doctor");
			// 把明细放到nrlist里面去
			Map<String, Object> parameter_jl02 = new HashMap<String, Object>();
			parameter_jl02.put("JGID", jgid);
			for (int i = 0; i < nrlist.size(); i++) {
				Map<String, Object> nrmap = nrlist.get(i);
                if(i>0){
                	if(nrmap.get("JLRQ1").equals(nrlist.get(i-1).get("JLRQ1"))){
                		nrmap.put("JLRQ", "");
                	}else {
                		nrmap.put("JLRQ", nrmap.get("JLRQ1"));
					}
                }else {
                	nrmap.put("JLRQ", nrmap.get("JLRQ1"));
				}
				parameter_jl02.put("JLBH",
						Long.parseLong(nrmap.get("JLBH") + ""));
				String sql_jl02 = "SELECT MXBH   as MXBH, JLBH   as JLBH,   XMBH   as XMBH,"
						+ " XMMC as XMMC, XSMC as XSMC, XMQZ   as XMQZ, KSLH   as KSLH,"
						+ " JSLH as JSLH, HDBZ as HDBZ,  YMCLFS as YMCLFS, HHJG  as HHJG,"
						+ " XGBZ as XGBZ, 0  AS GXBZ FROM ENR_JL02 WHERE JLBH =:JLBH  AND (JGID =:JGID)";
				List<Map<String, Object>> jl02list = dao.doQuery(sql_jl02,
						parameter_jl02);
				for (Map<String, Object> jl02map : jl02list) {
					nrmap.put("COL" + jl02map.get("KSLH"), jl02map.get("XMQZ")
							+ "");
				}
				nrmap.put("JLBH", nrmap.get("JLBH")+"");
				nrmap.put("ZYH", nrmap.get("ZYH")+"");
//				System.out.println("----nrmap:"+nrmap.toString());

				int HSQM = Integer.parseInt(ParameterUtil.getParameter(jgid,
						"HSQMBZ", ctx));
				if(HSQM>0){
					nrmap.put("COL19", sysUser.getText(nrmap.get("SXHS")+ ""));
				}
			}
			// 每页显示多少行
			int culNum = 15;
			// 总页数
			int pagNum = nrlist.size() / culNum;
			for (int i = 0; i < pagNum * culNum; i++) {
				records.add(nrlist.get(i));
			}
			for (int i = pagNum * culNum; i < nrlist.size(); i++) {
				records.add(nrlist.get(i));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();
		Map<String, Object> parameter = new HashMap<String, Object>();
		Long zyh = 0l;
		if (request.get("zyh") != null) {
			zyh = Long.parseLong(request.get("zyh") + "");
		}

		parameter.put("ZYH", zyh);
		String sql = "select BRKS as KB,BRXM as XM,RYNL as NL,BRXB as XB ,BRCH as CH,BAHM as ZYBL,"
				+ " CSNY as CSNY, RYRQ as RYRQ,RYZD as ZD from ZY_BRRY where zyh=:ZYH";
		String jbmcSql = "select distinct b.JBMC as JBMC from ZY_RYZD a , GY_JBBM b where a.ZYH = :ZYH and a.ZDXH = b.JBXH and a.ZDLB='2' ";
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
			if (null != jgname) { //add by  zhj增加机构名称
				response.put("jgname", jgname);
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
