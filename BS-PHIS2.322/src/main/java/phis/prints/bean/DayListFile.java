package phis.prints.bean;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class DayListFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		if (request.get("ZYH") == null) {
			throw new RuntimeException("查询病人失败！");
		}
		String sqlwhere = "";
		int YPLX = Integer.parseInt(request.get("YPLX") + "");
		if (YPLX == 2) {
			sqlwhere = "and t.YPLX =0 ";
		}
		if (YPLX == 3) {
			sqlwhere = "and t.YPLX in (1,2,3) ";
		}
		int JSLX =-1; 
		if(!(request.get("JSLX")+"").equals("null")&&!"undefined".equals(request.get("JSLX"))){
			JSLX=Integer.parseInt(request.get("JSLX").toString());
		}
		int JSCS =-1; 
		if(!(request.get("JSCS")+"").equals("null")&&!"undefined".equals(request.get("JSCS"))){
			JSCS=Integer.parseInt(request.get("JSCS").toString());
		}
		if (JSLX == 10&&JSCS !=-1) {
			sqlwhere += "and t.JSCS ='"+JSCS+"' ";
		}else if(JSLX == 9){
			sqlwhere += "and t.JSCS !='0' ";
		}else if(JSLX != -1&&JSCS !=-1){
			sqlwhere += "and t.JSCS ='0' ";
		}
				
		parameter.put("ZYH", Long.parseLong(request.get("ZYH") + ""));
		// String sql
		// ="select t.ZYH as ZYH,t.FYRQ as FYRQ,t.YPLX as YPLX,t.FYXH as FYXH,t.FYMC as FYMC,t.FYSL as FYSL,t.FYDJ as FYDJ,t.ZJJE as ZJJE,t.ZFJE as ZFJE,t.ZLJE as ZLJE,t.ZFBL as ZFBL,t.FYKS as FYKS,(select k.YGXM as YGXM from SYS_Personnel k where k.YGDM =t.SRGH)as SRGH,(select t1.KSMC from SYS_Office t1 where t1.KSDM=t.FYKS) as KSMC from "
		// +"ZY_FYMX t WHERE t.ZYH =:ZYH "+sqlwhere +" ORDER BY t.FYRQ DESC ";
		String sqlsum = "select t.FYXM as FYXM,'【'||s.SFMC||'】:￥' as SFMC,sum(t.ZJJE) as ZJJE from "
				+ "ZY_FYMX t,GY_SFXM s WHERE t.FYXM=s.SFXM and t.ZYH =:ZYH "
				+ sqlwhere + " group BY t.FYXM,s.SFMC ORDER BY t.FYXM";
		String sqllist = "select t.FYXH as FYXH,t.ZYH as ZYH,t.YPLX as YPLX,t.FYXM as FYXM,t.FYMC as FYMC,sum(t.FYSL) as FYSL,t.FYDJ as FYDJ,sum(t.ZJJE) as FYJE,sum(t.ZFJE) as ZFJE,sum(t.ZLJE) as ZLJE,t.ZFBL as ZFBL from "
				+ "ZY_FYMX t,GY_SFXM s WHERE t.FYXM=s.SFXM and t.ZYH =:ZYH "
				+ sqlwhere
				+ " group by t.FYXH,t.ZYH,t.YPLX,t.FYXM,t.FYMC,t.FYDJ,t.ZFBL ORDER BY t.FYXH DESC ";
		try {
			List<Map<String, Object>> rksum = dao.doQuery(sqlsum, parameter);
			List<Map<String, Object>> rklist = dao.doSqlQuery(sqllist,
					parameter);
			for (int i = 0; i < rksum.size(); i++) {
				rksum.get(i).put(
						"SFMC",
						rksum.get(i).get("SFMC") + ""
								+ rksum.get(i).get("ZJJE"));
				Map<String, Object> map = rksum.get(i);
				records.add(map);
				for (int j = 0; j < rklist.size(); j++) {
					Map<String, Object> detailMap = rklist.get(j);
					if (detailMap.get("FYSL") != null// 过滤为0的明细
							&& Double.parseDouble(detailMap.get("FYSL")
									.toString()) <= 0) {
						rklist.remove(detailMap);
						j--;
						continue;
					}
					String fymc = detailMap.get("FYMC") + "";
					String mc = "";
					if (fymc.indexOf("/") > 0) {
						mc = fymc.substring(0, fymc.indexOf("/"));
					}
					String dw = "";
					String gg = "";
					if (fymc.split("/").length == 3) {
						gg = fymc.substring(fymc.indexOf("/") + 1,
								fymc.lastIndexOf("/"));
						dw = fymc.substring(fymc.lastIndexOf("/") + 1);
					} else if (fymc.split("/").length == 2) {
						dw = fymc.substring(fymc.indexOf("/") + 1);
					}
					if (dw != "") {
						detailMap.put("DW", dw.trim());
					}
					if (gg != "") {
						detailMap.put("GG", gg);
					}
					if (mc != "") {
						detailMap.put("FYMC", mc);
					}
					if (map.get("FYXM").toString()
							.equals(detailMap.get("FYXM").toString())) {
						records.add(detailMap);
					}
				}
			}

			String sqlSumDetails = "select '合计:' as FYMC,sum(t.FYSL) as FYSL,sum(t.ZJJE) as FYJE,sum(t.ZFJE) as ZFJE,sum(t.ZLJE) as ZLJE from "
					+ "ZY_FYMX t WHERE t.ZYH =:ZYH " + sqlwhere;
			Map<String, Object> sum = dao.doLoad(sqlSumDetails, parameter);
			records.add(sum);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String TITLE = user.getManageUnit().getName();
		response.put("title", TITLE + "病人住院费用清单(明细)");
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZYH", Long.parseLong(request.get("ZYH") + ""));
		try {
			if (request.get("ZYHM") != null) {
				response.put("ZYHM", request.get("ZYHM"));
			}
			if (request.get("BRXM") != null) {
				String str = new String(request.get("BRXM").toString()
						.getBytes("iso8859_1"), "UTF-8");
				response.put("BRXM", str);
			}
			if (request.get("BRCH") != null) {
				response.put("BRCH", request.get("BRCH"));
			}
			if (request.get("BRXZ") != null) {
				String str = new String(request.get("BRXZ").toString()
						.getBytes("iso8859_1"), "UTF-8");
				response.put("BRXZ", str);
			}
			if (request.get("BRKS") != null) {
				String str = new String(request.get("BRKS").toString()
						.getBytes("iso8859_1"), "UTF-8");
				response.put("BRKS", str);
			}
			if (request.get("RYRQ") != null) {
				response.put("RYRQ", request.get("RYRQ"));
			}
			if (request.get("CYRQ") != null) {
				response.put("CYRQ", request.get("CYRQ"));
			}
			if (!request.get("DAYS").equals("null")) {
				response.put("DAYS", request.get("DAYS"));
			}
			if (request.get("ZFHJ") != null) {
				response.put("ZFHJ", request.get("ZFHJ"));
			}
			if (request.get("FYHJ") != null) {
				response.put("FYZE", request.get("FYHJ"));
			}
			if (!"undefined".equals(request.get("JKHJ"))) {
				response.put("JKHJ", request.get("JKHJ"));
			}
			double jkhj = 0.00;
			if (!"undefined".equals(request.get("JKHJ"))
					&& !"null".equals(request.get("JKHJ")+"")) {
				jkhj = parseDouble(request.get("JKHJ"));
			}
			double zfhj = 0.00;
			if (!"undefined".equals(request.get("ZFHJ"))
					&& !"null".equals(request.get("ZFHJ")+"")) {
				zfhj = parseDouble(request.get("ZFHJ"));
			}
			double jsje = 0.00;
			if (!"undefined".equals(request.get("JSJE"))
					&& !"null".equals(request.get("JSJE")+"")) {
				jsje = parseDouble(request.get("JSJE"));
			}
			double syxj = jkhj - zfhj;
			if(syxj<=0){
				syxj+=jsje;
			}
			response.put("SYXJ", String.format("%1$.2f", syxj));
			Map<String, Object> csnyMap = dao
					.doLoad("select RYNL as RYNL,BRXB as BRXB,ZSYS as ZSYS from ZY_BRRY WHERE ZYH=:ZYH",
							parameters);
			response.put("NL", csnyMap.get("RYNL"));
			response.put("XB", DictionaryController.instance().getDic("phis.dictionary.gender")
					.getText(csnyMap.get("BRXB") + ""));
			String zzys = "";
			if (csnyMap.get("ZSYS") != null) {
				zzys = DictionaryController.instance().getDic("phis.dictionary.user")
						.getText(csnyMap.get("ZSYS") + "");
			}
			response.put("ZZYS", zzys);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	public int parseInt(Object o) {
		if (o == null) {
			return new Integer(0);
		}
		return Integer.parseInt(o + "");
	}
}
