package phis.prints.bean;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;
import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 门诊患者基本药物处方占比统计
 * @author renwei 2020-08-07
 */
public class BasicDrugUseStatistics_MZCFFile implements IHandler {
	List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();

	@Override
	public void getParameters(Map<String, Object> request, Map<String, Object> response, Context ctx) throws PrintException {
		ret.clear();
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String jgname = user.getManageUnit().getName();
		//long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		String TJSJFW = request.get("dateFrom").toString().substring(0, 10).replaceFirst("-", ".").replaceFirst("-", ".").concat(" 00:00:00 ").concat(" -- ")
				.concat(request.get("dateTo").toString().substring(0, 10).replaceFirst("-", ".").replaceFirst("-", "."))
				.concat(" 23:59:59 ");
		try {
			StringBuffer hql;
			hql = getSqlAndParameters_Doctor(request, JGID);
			if("noResult".equals(hql.toString())){
				response.put("TITLE", jgname + "(当前时间段内无病人就诊)");
			}else {
				response.put("TITLE", jgname + "门诊患者基本药物处方占比");
				ret = dao.doSqlQuery(hql.toString(), null);
			}
			response.put("JGMC", jgname);
			response.put("TJSJFW", TJSJFW);
			response.put("ZB", user.getUserName());
			response.put("ZBRQ", BSHISUtil.toString(new Date()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private StringBuffer getSqlAndParameters_Doctor(Map<String, Object> request, String jgid) throws PersistentDataOperationException {
		String dateFrom=request.get("dateFrom")+" 00:00:00";
		String dateTo=request.get("dateTo")+" 23:59:59";
		//String YFSB=yfsb+"";
		//判断时间段内是否有人就诊，否则会造成除数为0
		String isJZ="select count(*) as MZZRS from ms_ghmx where jgid='"+jgid+"' and to_char(ghsj,'yyyy-mm-dd hh24:mi:ss')>='"+dateFrom+"' " +
				" and to_char(ghsj,'yyyy-mm-dd hh24:mi:ss')<='"+dateTo+"' and jzzt=9 ";
		BaseDAO dao = new BaseDAO();
		Map<String,Object> brjz = dao.doSqlLoad(isJZ, null);
		if(brjz.get("MZZRS")!="0") {
			//就诊总人数
			String sql_1 = "("+isJZ+") m,";
			//基本药物使用人数
			String sql_2 = "(select count(distinct(a.brid)) AS JYMZRS FROM  MS_CF02 b,MS_CF01 a,YK_TYPK c where c.YPXH = b.YPXH and a.CFSB = b.CFSB and a.ZFPB = 0 " +
					" and b.ZFYP != 1 and to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss') >= '"+dateFrom+"' and to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss') <= '"+dateTo+"' " +
					" and a.JGID ='"+jgid+"' and c.jylx=2 ) n";

			StringBuffer hql = new StringBuffer("select m.mzzrs as MZZRS,n.jymzrs as JYMZRS,nvl(round(n.jymzrs*100/m.mzzrs,2),'0.00')||'%' as MZJYBL from ")
			.append(sql_1)
			.append(sql_2);
			//.append(sql_3);
			return hql;
		}else{
			StringBuffer result = new StringBuffer("noResult");
			return result;
		}
	}


	//用bigDecimal做浮点数加法计算
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		records.addAll(ret);
	}
	private double pDouble(Object o){
		if(o==null){
			return 0;
		}
		return Double.parseDouble(o+"");
	}

}
