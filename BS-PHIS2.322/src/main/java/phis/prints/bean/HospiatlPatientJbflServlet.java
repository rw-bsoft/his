package phis.prints.bean;

import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class HospiatlPatientJbflServlet implements IHandler {

	private static String[] cyzk = { "ZY", "HZ", "WY", "SW", "QT" };
	int cyzrs = 0;
	int swzrs = 0;
	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		cyzrs = 0;
		swzrs = 0;
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		String name = user.getManageUnit().getName();
		String dateFrom = (String) request.get("dateFrom");
		String dateTo = (String) request.get("dateTo");
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("select e.CYZGDM as CYZGDM,count(e.JLXH) as CY_COUNT from EMR_ZYZDJL e,ZY_BRRY a where a.ZYH=e.JZXH and (a.CYPB=8 or a.CYPB=9) and e.ZDLB = 51 and e.ZZBZ = 1 and (e.ZFBZ = 0 or e.zfbz is null)");
			sql.append(" and e.jgid =").append(jgid);
			sql.append(" and e.ZDRQ between TO_DATE('" + dateFrom
					+ "', 'yyyy-mm-dd') and to_date('" + dateTo
					+ "', 'yyyy-mm-dd')");
			sql.append(" group by e.cyzgdm");
			sql.append(" order by e.cyzgdm asc");
			List<Map<String, Object>> list = dao.doSqlQuery(sql.toString(),
					null);
			for (int i = 0; i < cyzk.length; i++) {
				response.put(cyzk[i], 0);
			}
			for (Map<String, Object> m : list) {
				int cy = Integer.valueOf(m.get("CY_COUNT")+"");
				int CYZGDM = Integer.valueOf(m.get("CYZGDM")+"") - 1;
				cyzrs += cy;
				response.put(cyzk[CYZGDM], cy);
//				if (CYZGDM == 0)
//					zy = cy;
				if (CYZGDM == 3)
					swzrs = cy;
			}
			response.put("ZJ", (int) cyzrs);
			response.put("ZCYRSBFB",100);
			response.put("SWSZBFB",100);
			response.put("TITLE", name
					+ "住院病人疾病分类统计报表");
			response.put("datefromto", dateFrom + "至" + dateTo );
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		String dateFrom = (String) request.get("dateFrom");
		String dateTo = (String) request.get("dateTo");
		StringBuffer sql = new StringBuffer();
		sql.append("select COUNT as COUNT,MLBM as MLBM,FLMC as FLMC,QSBM as QSBM,JSBM as JSBM from(");
		sql.append("select count(a.JBBM) as COUNT,b.MLBM as MLBM,b.FLMC as FLMC,b.QSBM as QSBM,b.JSBM as JSBM from EMR_ZYZDJL a,GY_JBFL b,ZY_BRRY c where c.ZYH=a.JZXH and (c.CYPB=8 or c.CYPB=9) and (a.JBBM like substr(b.QSBM,0,1)||'%' or a.JBBM like substr(b.JSBM,0,1)||'%') and length(b.mlbm) <= 3 and a.ZXLB=1 group by b.MLBM,b.FLMC,b.QSBM,b.JSBM");
		sql.append(" union all ");
		sql.append("select count(a.JBBM) as COUNT,b.JBDM as MLBM,f.FLMC as FLMC,f.FLBM as QSBM,f.FLBM as JSBM from EMR_ZYZDJL a,EMR_ZYJB b,EMR_ZYFL d,ZY_BRRY c,EMR_ZYFL f where c.ZYH=a.JZXH and (c.CYPB=8 or c.CYPB=9) and a.JBBM = b.JBDM and a.JBXH=b.JBBS and b.ZYFL=d.ZYFL and d.FLBM like f.FLBM||'%' and length(f.FLBM) <= 3 and a.ZXLB=2 group by b.JBDM,f.FLMC,f.FLBM) order by COUNT desc");
		try {
			List<Map<String, Object>> list = dao.doSqlQuery(sql.toString(),
					null);
			int j = 1;
			for (Map<String, Object> m : list) {
				m.put("MLBM", j);
				j++;
				float zy = 0;
				float sw = 0;
				float tj = 0;
				for (int i = 0; i < cyzk.length; i++) {
					m.put(cyzk[i], 0);
				}
				m.put("ZJ", 0);
				m.put("ZCYRSBFB", 0);
				m.put("SWSZBFB", 0);
				sql = new StringBuffer();
				String qsbm = (String) m.get("QSBM");
				String jsbm = (String) m.get("JSBM");
				sql.append("select e.CYZGDM as CYZGDM,count(e.jlxh) as CY_COUNT from EMR_ZYZDJL e,ZY_BRRY a where e.ZXLB=1 and a.ZYH=e.JZXH and (a.CYPB=8 or a.CYPB=9) and ");
				sql.append(" e.jgid =").append(jgid);
				sql.append(" and e.ZDLB = 51 and e.ZZBZ = 1 and (e.ZFBZ = 0 or e.zfbz is null)");
				sql.append(" and (e.jbbm like '").append(qsbm.substring(0, 1))
						.append("%'");
				sql.append(" or e.jbbm like '").append(jsbm.substring(0, 1))
						.append("%')");
				sql.append(" and e.ZDRQ between TO_DATE('" + dateFrom
						+ "', 'yyyy-mm-dd') and to_date('" + dateTo
						+ "', 'yyyy-mm-dd')");
				sql.append(" group by e.cyzgdm");
				sql.append(" union all ");
				sql.append("select e.CYZGDM as CYZGDM,count(e.jlxh) as CY_COUNT from EMR_ZYZDJL e,EMR_ZYJB b,EMR_ZYFL d,ZY_BRRY a where e.ZXLB=2 and e.JBBM = b.JBDM and e.JBXH=b.JBBS  and b.ZYFL=d.ZYFL and a.ZYH=e.JZXH and (a.CYPB=8 or a.CYPB=9) and ");
				sql.append(" e.jgid =").append(jgid);
				sql.append(" and e.ZDLB = 51 and e.ZZBZ = 1 and (e.ZFBZ = 0 or e.zfbz is null)");
				sql.append(" and d.FLBM like '").append(qsbm)
						.append("%'");
				sql.append(" and e.ZDRQ between TO_DATE('" + dateFrom
						+ "', 'yyyy-mm-dd') and to_date('" + dateTo
						+ "', 'yyyy-mm-dd')");
				sql.append(" group by e.cyzgdm");
				List<Map<String, Object>> l = dao.doSqlQuery(sql.toString(),
						null);
				if (l.size() == 0)
					continue;
				for (Map<String, Object> n : l) {
					int cy = Integer.valueOf(n.get("CY_COUNT")+"");
					int CYZGDM = Integer.valueOf(n.get("CYZGDM")+"") - 1;
					tj += cy;
					m.put(cyzk[CYZGDM], cy);
					if (CYZGDM == 0)
						zy = cy;
					if (CYZGDM == 3){
						sw = cy;
					}
				}
				m.put("ZJ", (int) tj);
				m.put("ZCYRSBFB", BSPHISUtil.getDouble((tj / cyzrs) * 100,2));
				m.put("SWSZBFB", BSPHISUtil.getDouble((sw / swzrs) * 100,2));
			}
			records.addAll(list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

}
