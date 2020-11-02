package phis.prints.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

/**
 * 抗菌药物使用强度
 * 
 * @author Bollen
 * 
 */
public class AntimicrobialDrugUseIntensityInformationFile implements IHandler {
	List<Map<String, Object>> ret_list = new ArrayList<Map<String, Object>>();

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		ret_list.clear();
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		String jgname = user.getManageUnit().getName();
		String dateFrom = request.get("dateFrom") + "";
		String dateTo = request.get("dateTo") + "";
		String TJSJFW = request
				.get("dateFrom")
				.toString()
				.substring(0, 10)
				.replaceFirst("-", ".")
				.replaceFirst("-", ".")
				.concat(" -- ")
				.concat(request.get("dateTo").toString().substring(0, 10)
						.replaceFirst("-", ".").replaceFirst("-", "."));
		response.put("JGMC", jgname);
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("dateFrom", dateFrom);
			parameters.put("dateTo", dateTo);
			parameters.put("jgid", jgid);
			if ("1".equals(request.get("flag"))) {
				// 按科室统计
				getParameters_KS(dao, parameters, response);
			} else {
				// 按统计
				parameters.put("ksdm", request.get("ksdm") + "");
				getParameters_MX(dao, parameters, response);
				Map<String,Object> ksmc_map = dao.doLoad(BSPHISEntryNames.SYS_Office, request.get("ksdm") + "");
				if(!"null".equals(ksmc_map+"")){
					response.put("TITLE", "住院抗菌药物使用强度表("+ksmc_map.get("OFFICENAME")+")"); 
				}else{
					response.put("TITLE", "住院抗菌药物使用强度表");
				}
			}

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		response.put("TJSJ", TJSJFW);
		response.put("ZB", user.getUserName());
		response.put("ZBRQ", BSHISUtil.toString(new Date()));
	}

	private void getParameters_MX(BaseDAO dao, Map<String, Object> parameters,
			Map<String, Object> response)
			throws PersistentDataOperationException {
		// 出院人数
		long CYRS = dao
				.doCount(
						"ZY_BRRY",
						"cypb not in(0,1,99) and to_char(cyrq,'yyyy-mm-dd') between :dateFrom and  :dateTo  and jgid =:jgid and brks=:ksdm",
						parameters);
		// 平均出院天数
		List<Map<String, Object>> PJZYTS_list = dao
				.doSqlQuery(
						"select sum(trunc(cyrq)-trunc(ryrq)+1) as ZYRTS,sum(trunc(cyrq)-trunc(ryrq)+1)/count(*) as PJZYTS from zy_brry where cypb not in(0,1,99) and to_char(cyrq,'yyyy-mm-dd') between :dateFrom and :dateTo and jgid =:jgid and brks=:ksdm group by brks",
						parameters);
		parameters.put("CYRS", CYRS);
		parameters.put("PJZYTS",
				PJZYTS_list.size() == 0 ? 0 : PJZYTS_list.get(0).get("PJZYTS"));
		// 出院病人
		String sql_cybr = "select zyh from zy_brry  WHERE  cypb not in(0,1,99) and to_char(cyrq,'yyyy-mm-dd') between :dateFrom and  :dateTo  and jgid =:jgid and brks=:ksdm ";
		String sql = "select  d.bmmc as kjywfl, a.fyxh as ypxh, b.ypmc as ypmc, b.kssdj as kjjb,b.ypgg as gg,b.YPDW as dw,b.ypjl as jl, sum(a.fysl) as sl, sum(round(a.zjje, 2)) as zjje,sum(round(a.zjje/:CYRS,2)) as rjje,   sum(round(b.ypjl * a.fysl*nvl(e.yfbz,b.zxbz) / b.dddz,2)) as yypd, sum(round(b.ypjl * a.fysl*nvl(e.yfbz,b.zxbz) * 100 / b.dddz /:PJZYTS,2)) as SYQD  from zy_fymx a left join yk_typk b on a.fyxh = b.ypxh left join zy_brry c on a.zyh = c.zyh   left join yk_bmzd d on b.ypdm=d.bmxh  left join zy_bqyz e on a.yzxh=e.jlxh  where b.ksbz = 1 and a.zyh in ("
				+ sql_cybr + " ) " +
						"group by d.bmmc, a.fyxh, b.ypmc , b.kssdj,b.ypgg, b.YPDW, b.ypjl";
		ret_list = dao.doSqlQuery(sql, parameters);
		response.put("ZYRTS", PJZYTS_list.size() == 0 ? 0 : PJZYTS_list.get(0)
				.get("ZYRTS") + "");// 住院人天数
		response.put("CYRS", CYRS + "");// 出院人数
		response.put("YYS", ret_list.size() + "种");
		for (Map<String, Object> m : ret_list) {
			response.put("YYPD_ALL",
					add_Double(response.get("YYPD_ALL"), m.get("YYPD")));
			response.put("SYQD_ALL",
					add_Double(response.get("SYQD_ALL"), m.get("SYQD")));
		}
	}

	// 按科室统计
	private void getParameters_KS(BaseDAO dao, Map<String, Object> parameters,
			Map<String, Object> response)
			throws PersistentDataOperationException {
		// 科室出院人数、科室平均住院天数
		String cYRS = "(select brks,count(1) as CYRS,sum(trunc(cyrq)-trunc(ryrq)+1)/count(1) as PJZYTS from zy_brry where cypb not in(0,1,99) and to_char(cyrq,'yyyy-mm-dd') between :dateFrom and :dateTo group by brks)";
		String sql = "select d.brks as KSDM,e.OFFICENAME as KSMC ,round(sum(a.ZJJE), 2) as YPZFY,round(sum(case b.ksbz when 1 then a.ZJJE else  0   end), 2) as KJYWZFY,round(sum(case b.ksbz when 1 then a.ZJJE else  0   end / c.CYRS),2) as KJYWRJFY,sum(round(nvl(f.yfbz,b.zxbz)*b.ypjl * a.fysl / b.dddz,2)) as LJKJYWXH,sum(round(nvl(f.yfbz,b.zxbz)*b.ypjl * a.fysl / b.dddz*100/c.PJZYTS,2)) as SYQD "
				+ "from (select * from zy_fymx where yplx <> 0) a "
				+ "left join yk_typk b on a.FYXH = b.ypxh    "
				+ "left join zy_brry d on a.zyh=d.zyh left join "
				+ cYRS
				+ " c on c.brks = d.brks   "
				+ "left join sys_office e on e.ID=d.brks   "
				+ "left join zy_bqyz f on a.yzxh=f.jlxh  "
				+ "where a.jgid=:jgid and d.cypb not in(0,1,99) and to_char(d.cyrq,'yyyy-mm-dd') between :dateFrom and :dateTo "
				+ "group by d.brks,e.OFFICENAME";
		ret_list = dao.doSqlQuery(sql, parameters);
		for (Map<String, Object> m : ret_list) {
			response.put("YPZFY_ALL",
					add_Double(response.get("YPZFY_ALL"), m.get("YPZFY")));
			response.put("KJYWZFY_ALL",
					add_Double(response.get("KJYWZFY_ALL"), m.get("KJYWZFY")));
//			response.put("KJYWRJFY_ALL",
//					add_Double(response.get("KJYWRJFY_ALL"), m.get("KJYWRJFY")));
			response.put("LJKJYWXH_ALL",
					add_Double(response.get("LJKJYWXH_ALL"), m.get("LJKJYWXH")));
			response.put("SYQD_ALL",
					add_Double(response.get("SYQD_ALL"), m.get("SYQD")));
		}
		//出院人数      
		Long cyrs_all = dao.doCount("ZY_BRRY", "cypb not in(0,1,99) and to_char(cyrq,'yyyy-mm-dd') between :dateFrom and :dateTo and jgid=:jgid", parameters);
		if(cyrs_all!=0){response.put("KJYWRJFY_ALL",  String.format("%.2f", parse_Double(response.get("KJYWZFY_ALL"))/cyrs_all));}   
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		records.addAll(ret_list);
	}

	// 用bigDecimal做浮点数加法计算
	private String add_Double(Object x, Object y) {
		if (x == null) {
			x = 0;
		}
		if (y == null) {
			y = 0;
		}
		double ret = new BigDecimal(x.toString()).add(
				new BigDecimal(y.toString())).doubleValue();
		return ret + "";
	}
	 
	private double parse_Double(Object o) {
		if (o == null) {
			o = 0;
		}
		return Double.parseDouble(o+"");
	}

}
