package phis.prints.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class FamilySickBedDispensingDetailsFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		StringBuffer hql = new StringBuffer();
		hql.append("select to_char(d.KSSJ,'yyyy-MM-dd hh24:mi:ss') as KSSJ,c.BRXM as BRXM,b.YPMC as YPMC,a.YPGG as YPGG,a.YFDW as YPDW,d.YCJL as JL,e.PCMC as YF,sum(a.LSJE) as JE,sum(a.YPSL) as ZSL from YF_JCFYMX a,YK_TYPK b,JC_BRRY c,JC_BRYZ d,GY_SYPC e where a.YPXH=b.YPXH and a.ZYH=c.ZYH and a.YZXH=d.JLXH and a.JLID=:jlid and d.SYPC=e.PCBM group by a.YPXH,a.YPCD,c.BRXM ,b.YPMC ,a.YPGG ,a.YFDW ,d.YCJL ,e.PCMC,d.KSSJ");
		StringBuffer hql_jyjz = new StringBuffer();
		hql_jyjz.append("select to_char(a.FYRQ,'yyyy-MM-dd hh24:mi:ss') as KSSJ,c.BRXM as BRXM,b.YPMC as YPMC,a.YPGG as YPGG,a.YFDW as YPDW,'' as JL,'' as YF,sum(a.LSJE) as JE,sum(a.YPSL) as ZSL from YF_JCFYMX a,YK_TYPK b,JC_BRRY c where a.YPXH=b.YPXH and a.ZYH=c.ZYH  and a.JLID=:jlid  group by a.YPXH,a.YPCD, c.BRCH ,c.BRXM ,b.YPMC ,a.YPGG ,a.YFDW,a.FYRQ ");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("jlid", MedicineUtils.parseLong(request.get("JLID")));
		try {
			List<Map<String, Object>> list_fymx = dao.doSqlQuery(
					hql.toString(), map_par);
			if (list_fymx == null || list_fymx.size() == 0) {
				list_fymx = dao.doSqlQuery(hql_jyjz.toString(), map_par);
				if (list_fymx == null || list_fymx.size() == 0) {
					return;
				}
			}
			// 先放记录集
			double je_sum = 0.00;
			double sl_sum = 0.00;
			int culNum = 20;// 每页显示多少行
			int pagNum = list_fymx.size() / culNum;// 总页数
			for (int i = 0; i < pagNum * culNum; i++) {
				Map<String, Object> map_fymx = list_fymx.get(i);
				if (map_fymx.get("YPDW") == null
						|| "null".equals(map_fymx.get("YPDW") + "")) {
					map_fymx.put("YPDW", "");
				}
				if (map_fymx.get("YPGG") == null
						|| "null".equals(map_fymx.get("YPGG") + "")) {
					map_fymx.put("YPGG", "");
				}
				Map<String, Object> map_temp = new HashMap<String, Object>();
				if (map_fymx.get("JE") != "" && map_fymx.get("JE") != null) {
					je_sum += Double.parseDouble(map_fymx.get("JE") + "");
					// map_fymx.put("JE",
					// String.format("%1$.4f", map_fymx.get("JE")));
				} // else {
					// map_fymx.put("JE", "");
				// }
				if (map_fymx.get("ZSL") != "" && map_fymx.get("ZSL") != null) {
					sl_sum += Double.parseDouble(map_fymx.get("ZSL") + "");
					// map_fymx.put("ZSL",
					// String.format("%1$.4f", map_fymx.get("ZSL")));
				} // else {
					// map_fymx.put("ZSL", "");
				// }
				map_temp.putAll(map_fymx);
//				if (i != 0) {
//					if (MedicineUtils.parseString(map_fymx.get("BRXM")).equals(
//							MedicineUtils.parseString(list_fymx.get(i - 1).get(
//									"BRXM")))
//							&& MedicineUtils.parseString(map_fymx.get("BRCH"))
//									.equals(MedicineUtils.parseString(list_fymx
//											.get(i - 1).get("BRCH")))) {
//						map_temp.put("BRCH", "");
//						map_temp.put("BRXM", "");
//					}
//				}
				if (list_fymx.size() > culNum) {
					if ((i + 1) % culNum == 0) {
						map_temp.put("SUM_JE", String.format("%1$.4f", je_sum));
						map_temp.put("SUM_SL", String.format("%1$.4f", sl_sum));
						je_sum = 0.00;
						sl_sum = 0.00;
					}
				}
				records.add(map_temp);
			}
			for (int i = pagNum * culNum; i < list_fymx.size(); i++) {
				Map<String, Object> map_fymx = list_fymx.get(i);
				if (map_fymx.get("YPDW") == null
						|| "null".equals(map_fymx.get("YPDW") + "")) {
					map_fymx.put("YPDW", "");
				}
				if (map_fymx.get("YPGG") == null
						|| "null".equals(map_fymx.get("YPGG") + "")) {
					map_fymx.put("YPGG", "");
				}
				Map<String, Object> map_temp = new HashMap<String, Object>();
				if (map_fymx.get("JE") != "" && map_fymx.get("JE") != null) {
					je_sum += Double.parseDouble(map_fymx.get("JE") + "");
					// map_fymx.put("JE",
					// String.format("%1$.4f", map_fymx.get("JE")));
				} // else {
					// map_fymx.put("JE", "");
					// }
				if (map_fymx.get("ZSL") != "" && map_fymx.get("ZSL") != null) {
					sl_sum += Double.parseDouble(map_fymx.get("ZSL") + "");
					// map_fymx.put("ZSL",
					// String.format("%1$.4f", map_fymx.get("ZSL")));
				} // else {
					// map_fymx.put("ZSL", "");
					// }
				map_temp.putAll(map_fymx);
				if (i != 0) {
					if (MedicineUtils.parseString(map_fymx.get("BRXM")).equals(
							MedicineUtils.parseString(list_fymx.get(i - 1).get(
									"BRXM")))
							&& MedicineUtils.parseString(map_fymx.get("BRCH"))
									.equals(MedicineUtils.parseString(list_fymx
											.get(i - 1).get("BRCH")))) {
						map_temp.put("BRXM", "");
					}
				}
				if (i == (list_fymx.size() - 1)) {
					map_temp.put("SUM_JE", String.format("%1$.4f", je_sum));
					map_temp.put("SUM_SL", String.format("%1$.4f", sl_sum));
					je_sum = 0.00;
					sl_sum = 0.00;
				}
				records.add(map_temp);
			}
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000, "打印数据查询失败"+e.getMessage());
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		response.put("TITLE", UserRoleToken.getCurrent()
				.getManageUnitName() + "家床发药明细清单");
//		BaseDAO dao = new BaseDAO(ctx);
//		StringBuffer hql = new StringBuffer();
//		hql.append("select to_char(a.FYSJ, 'yyyy-MM-dd hh24:mi:ss') as FYSJ,a.fylx,(select fsmc from ZY_FYFS where fyfs = a.fyfs) as FSMC from JC_FYJL a  where a.jlid =:jlid");
//		try {
//			Map<String, Object> map_par = new HashMap<String, Object>();
//			map_par.put("jlid", MedicineUtils.parseLong(request.get("JLID")));
//			List<Map<String, Object>> list_fyjl = dao.doSqlQuery(
//					hql.toString(), map_par);
//			Map<String, Object> map_fyjl = list_fyjl.get(0);
//			int fylxint = 1;
//			if (map_fyjl.get("FYLX") != null) {
//				fylxint = Integer.parseInt(map_fyjl.get("FYLX") + "");
//			}
//			response.put("SJ", map_fyjl.get("FYSJ"));
//			if (fylxint == 1) {
//				response.put("TITLE", UserRoleToken.getCurrent()
//						.getManageUnitName()
//						+ "发药明细清单("
//						+ map_fyjl.get("FSMC")
//						+ ")");
//			} else if (fylxint == 2) {
//				response.put("TITLE", UserRoleToken.getCurrent()
//						.getManageUnitName() + "发药明细清单(急诊用药)");
//			} else if (fylxint == 3) {
//				response.put("TITLE", UserRoleToken.getCurrent()
//						.getManageUnitName() + "发药明细清单(出院带药)");
//			} else if (fylxint == 5) {
//				response.put("TITLE", UserRoleToken.getCurrent()
//						.getManageUnitName() + "退药明细清单(病区退药)");
//			}
//			response.put("MYHS", 20);// 每页打印行数
//		} catch (Exception e) {
//			throw new PrintException(9000, "打印数据查询失败"+e.getMessage());
//		}
	}
}
