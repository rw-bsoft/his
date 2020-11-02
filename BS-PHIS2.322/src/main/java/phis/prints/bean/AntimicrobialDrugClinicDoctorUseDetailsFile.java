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
public class AntimicrobialDrugClinicDoctorUseDetailsFile implements IHandler {
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
			if ("2".equals(request.get("flag"))) {
				// 统计明细
				parameters.put("ksdm", request.get("ksdm") + "");
				parameters.put("ysdm", request.get("ysdm") + "");
				parameters.put("ksscfs", Long.parseLong(request.get("ksscfs") + ""));
				getParameters_MX(dao, parameters, response);
				Map<String,Object> ksmc_map = dao.doLoad(BSPHISEntryNames.SYS_Office, request.get("ksdm") + "");
				Map<String,Object> p=new HashMap<String, Object>();
				p.put("personid", request.get("ysdm") + "");
				Map<String,Object> ysmc_map=dao.doSqlLoad("select b.personname as PERSONNAME " +
						"from sys_personnel b where b.personid=:personid", p);
				response.put("TITLE", "门诊抗菌药物使用明细表("+ksmc_map.get("OFFICENAME")+"-"+ysmc_map.get("PERSONNAME")+")"); 
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
		String sql="select d.bmmc as KJYWFL,c.ypmc as YPMC ,c.kssdj as KJJB,b.yfgg as GG,b.yfdw as DW,sum(b.ypsl) as SL," +
				" sum(b.hjje) as ZJJE,round(sum(b.hjje)/:ksscfs,2) as CFJE from ms_cf01 a join ms_cf02 b on a.cfsb=b.cfsb " +
				" join yk_typk c on b.ypxh=c.ypxh " +
				" left join yk_bmzd d on c.ypdm=d.bmxh "+
				" where c.ksbz=1 and a.ksdm=:ksdm" +
				" and a.ysdm=:ysdm and to_char(a.kfrq,'yyyy-mm-dd') " +
				" between :dateFrom and :dateTo"+
				" and a.jgid=:jgid group by d.bmmc,c.ypmc,b.yfgg,b.yfdw,c.kssdj";
		
		ret_list = dao.doSqlQuery(sql, parameters);
//		response.put("YYS", ret_list.size() + "种");
		for (Map<String, Object> m : ret_list) {
			response.put("TOTALJE",add_Double(response.get("TOTALJE"), m.get("ZJJE")));
		}
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
