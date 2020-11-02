package phis.prints.bean;

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
import ctd.account.user.User;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

/**
 * 	抗菌药物使用信息
 * @author Bollen
 * 
 */
public class AntimicrobialDrugUseInformationFile implements IHandler {
	List<Map<String, Object>> details_mz = new ArrayList<Map<String, Object>>();
	List<Map<String, Object>> details_mz_sum = new ArrayList<Map<String, Object>>();
	Long  count = 0L;//抗菌药总数
	Long  count_mz = 0L;//抗菌药总数
	Long  count_zy = 0L;//抗菌药总数
	List<Map<String, Object>> details_zy = new ArrayList<Map<String, Object>>();
	List<Map<String, Object>> details_zy_sum = new ArrayList<Map<String, Object>>();

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		details_mz.clear();
		details_zy.clear();
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		String jgname = user.getManageUnit().getName();
		String dateFrom = request.get("dateFrom") + "";
		String dateTo = request.get("dateTo") + "";
		String TJSJFW = request.get("dateFrom")
				.toString()
				.substring(0, 10)
				.replaceFirst("-", ".")
				.replaceFirst("-", ".")
				.concat(" -- ")
				.concat(request.get("dateTo").toString().substring(0, 10)
						.replaceFirst("-", ".").replaceFirst("-", "."));
		response.put("JGMC", jgname);
		try {
			//查询抗菌药种类数量
			 count = Long.parseLong(dao.doSqlQuery("select count(1) as kjywzs from yk_ypxx k left join yk_typk k1 on k.ypxh=k1.ypxh where ksbz=1 and jgid='"+jgid+"'", null).get(0).get("KJYWZS")+"");
			//查询门诊各药房发药使用了的抗菌药种类数量
			String s = "select yfsb as yfsb,count(*) as count from (select a.yfsb as yfsb, a.ypxh as ypxh " +
					" from yf_mzfymx a left join yk_typk b on a.ypxh = b.ypxh   WHERE a.JGID ="+ jgid
					+ " and  to_char(a.FYRQ,'yyyy-mm-dd') between '"+ dateFrom + "' and '"+ dateTo 
					+"' and b.ksbz=1"
					+ " having sum(a.ypsl)<>0 group by a.yfsb,a.ypxh) group by yfsb";
			List<Map<String,Object>> l_mz = dao.doSqlQuery(s, null);
			
			//查询门诊所有药房发药共使用了的抗菌药种类数量
			 s = "select count(1) as count from (select a.jgid as jgid, a.ypxh as ypxh " +
					" from yf_mzfymx a left join yk_typk b on a.ypxh = b.ypxh   WHERE a.JGID ="+ jgid
					+ " and  to_char(a.FYRQ,'yyyy-mm-dd') between '"+ dateFrom + "' and '"+ dateTo 
					+"' and b.ksbz=1"
					+ " having sum(a.ypsl)<>0 group by a.jgid,a.ypxh)";
			List<Map<String,Object>> l_mz_sum = dao.doSqlQuery(s, null);
			count_mz = Long.parseLong(l_mz_sum.get(0).get("COUNT")+"");
			
			// 统计门诊数据
			String sql = "select a.yfsb as yfsb ,c.yfmc as YF,round(sum(a.ypsl*a.YFBZ/b.ZXBZ),2) as MZFYS,round(sum(case b.ksbz when 1 then ypsl*a.YFBZ/b.ZXBZ else 0 end),2) as KJYWS,"
					+ "round(sum(case b.ksbz when 1 then ypsl else 0 end)*100/sum(a.ypsl),2) || '%' as KJYBL," 
							+count+" as KJYZS,"
					+ "sum(HJJE) as MZFYJE, sum(case b.ksbz when 1 then HJJE else 0 end) as KJYZE,"
					+ "round(sum(case b.ksbz when 1 then HJJE else 0 end)*100/sum(HJJE),2) || '%' as JEZB "
					+ " from yf_mzfymx a left join yk_typk b on a.ypxh = b.ypxh left join yf_yflb c on a.yfsb= c.yfsb  WHERE a.JGID ="
					+ jgid
					+ " and  to_char(a.FYRQ,'yyyy-mm-dd') between '"
					+ dateFrom
					+ "' and '"
					+ dateTo
					+ "' having sum(a.ypsl)<>0 group by a.yfsb,c.yfmc";
			details_mz = dao.doSqlQuery(sql, null);
			// 门诊合计
			String mz_sum = "select '合计' as YF,sum(a.ypsl) as MZFYS,sum(case b.ksbz when 1 then ypsl else 0 end) as KJYWS,"
					+ "round(sum(case b.ksbz when 1 then ypsl else 0 end)*100/sum(a.ypsl),2) || '%' as KJYBL,"
					+count+" as KJYZS,"
					+ "sum(HJJE) as MZFYJE, sum(case b.ksbz when 1 then HJJE else 0 end) as KJYZE,"
					+ "round(sum(case b.ksbz when 1 then HJJE else 0 end)*100/sum(HJJE),2) || '%' as JEZB "
					+ " from yf_mzfymx a left join yk_typk b on a.ypxh = b.ypxh   WHERE a.JGID ="
					+ jgid
					+ " and  to_char(a.FYRQ,'yyyy-mm-dd') between '"
					+ dateFrom
					+ "' and '"
					+ dateTo
					+ "'  having sum(a.ypsl)<>0 group by a.jgid ";
			details_mz_sum = dao.doSqlQuery(mz_sum, null);
			//算门诊使用比例合计
			if(details_mz_sum.size()>0){
				details_mz_sum.get(0).put("SYBL",String.format("%.2f", Double.parseDouble(count_mz+"")*100/count)+"%");
			}
			//算门诊使用比例
			for(Map<String,Object> m : details_mz){
				for(Map<String,Object> map : l_mz){
					if((m.get("YFSB")+"").equals(map.get("YFSB")+"")){
						m.put("SYBL",String.format("%.2f", Double.parseDouble(map.get("COUNT")+"")*100/count)+"%" );
					}
				}
			}
			//查询住院各药房发药使用了的抗菌药种类数量
			String s_zy = "select yfsb as yfsb,count(*) as count from (select  a.yfsb as yfsb, a.ypxh as ypxh   " +
					" from yf_zyfymx a left join yk_typk b on a.ypxh = b.ypxh   WHERE a.JGID ="+ jgid
					+ " and  to_char(a.FYRQ,'yyyy-mm-dd') between '"+ dateFrom + "' and '"+ dateTo 
					+"' and b.ksbz=1"
					+ " having sum(a.ypsl)<>0 group by a.yfsb,a.ypxh) group by yfsb ";
			List<Map<String,Object>> l_zy = dao.doSqlQuery(s_zy, null);
			//查询住院所有药房发药共使用了的抗菌药种类数量
			 s_zy = "select count(*) as count from (select  a.JGID as JGID, a.ypxh as ypxh   " +
					" from yf_zyfymx a left join yk_typk b on a.ypxh = b.ypxh   WHERE a.JGID ="+ jgid
					+ " and  to_char(a.FYRQ,'yyyy-mm-dd') between '"+ dateFrom + "' and '"+ dateTo 
					+"' and b.ksbz=1"
					+ " having sum(a.ypsl)<>0 group by a.JGID,a.ypxh) ";
			List<Map<String,Object>> l_zy_sum = dao.doSqlQuery(s_zy, null);
			count_zy = Long.parseLong(l_zy_sum.get(0).get("COUNT")+"");
			// 统计住院数据
			sql = "select a.yfsb as yfsb ,c.yfmc as YF,round(sum(a.ypsl*a.YFBZ/b.ZXBZ),2) as MZFYS,round(sum(case b.ksbz when 1 then ypsl*a.YFBZ/b.ZXBZ else 0 end),2) as KJYWS,"
					+ "round(sum(case b.ksbz when 1 then ypsl else 0 end)*100/sum(a.ypsl),2) || '%' as KJYBL,"
					+count+" as KJYZS,"
					+ "sum(FYJE) as MZFYJE, sum(case b.ksbz when 1 then FYJE else 0 end) as KJYZE,"
					+ "round(sum(case b.ksbz when 1 then FYJE else 0 end)*100/sum(FYJE),2) || '%' as JEZB "
					+ " from yf_zyfymx a left join yk_typk b on a.ypxh = b.ypxh left join yf_yflb c on a.yfsb=c.yfsb   WHERE a.JGID ="
					+ jgid
					+ " and  to_char(a.FYRQ,'yyyy-mm-dd') between '"
					+ dateFrom
					+ "' and '"
					+ dateTo
					+ "' having sum(a.ypsl)<>0 group by a.yfsb,c.yfmc";
			details_zy = (dao.doSqlQuery(sql, null));
			//算住院使用比例
			for(Map<String,Object> m : details_zy){
				for(Map<String,Object> map : l_zy){
					if((m.get("YFSB")+"").equals(map.get("YFSB")+"")){
						m.put("SYBL",String.format("%.2f", Double.parseDouble(map.get("COUNT")+"")*100/count)+"%" );
					}
				}
			}
			
			// 住院合计
			String zy_sum = "select '合计' as YF,sum(a.ypsl) as MZFYS,sum(case b.ksbz when 1 then ypsl else 0 end) as KJYWS,"
					+ "round(sum(case b.ksbz when 1 then ypsl else 0 end)*100/sum(a.ypsl),2) || '%' as KJYBL,"
					+ "sum(FYJE) as MZFYJE, sum(case b.ksbz when 1 then FYJE else 0 end) as KJYZE,"
					+count+" as KJYZS,"
					+ "round(sum(case b.ksbz when 1 then FYJE else 0 end)*100/sum(FYJE),2) || '%' as JEZB "
					+ " from yf_zyfymx a left join yk_typk b on a.ypxh = b.ypxh   WHERE a.JGID ="
					+ jgid
					+ " and  to_char(a.FYRQ,'yyyy-mm-dd') between '"
					+ dateFrom
					+ "' and '"
					+ dateTo
					+ "'  having sum(a.ypsl)<>0 group by a.jgid ";
			details_zy_sum = dao.doSqlQuery(zy_sum, null);
			//算住院使用比例合计
			if(details_zy_sum.size()>0){
				details_zy_sum.get(0).put("SYBL",String.format("%.2f", Double.parseDouble(count_zy+"")*100/count)+"%");
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		response.put("TJSJFW", TJSJFW);
		response.put("ZB", user.getUserName());
		response.put("ZBRQ", BSHISUtil.toString(new Date()));
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		records.addAll(details_mz);
		// 加入门诊合计
		if(details_mz_sum.size()>0){
			records.add(details_mz_sum.get(0));
		}else{
			Map<String,Object> m = new HashMap<String, Object>();
			m.put("YF", "合计");
			records.add(m);
		}
		// 加入列名
		Map<String, Object> columnName = new HashMap<String, Object>();
		columnName.put("YF", "药房");
		columnName.put("MZFYS", "住院发药数");
		columnName.put("KJYWS", "抗菌药物数");
		columnName.put("KJYBL", "抗菌药比例");
		columnName.put("KJYZS", "抗菌药总数");
		columnName.put("SYBL", "使用比例");
		columnName.put("MZFYJE", "门诊发药总额");
		columnName.put("KJYZE", "抗菌药总额");
		columnName.put("JEZB", "金额占比");
		records.add(columnName);
		records.addAll(details_zy);
		// 加入住院合计
		if(details_zy_sum.size()>0){
			records.add(details_zy_sum.get(0));
		}else{
			Map<String,Object> m = new HashMap<String, Object>();
			m.put("YF", "合计");
			records.add(m);
		}
	}
}
