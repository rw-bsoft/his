package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.ParameterUtil;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class InventoryLedgerEjFile implements IHandler {
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdftime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfyf = new SimpleDateFormat("yyyyMM");
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();// 用户的机构ID
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		try {
			if (KFXH == 0) {
				String YWLB = "6";
				List<Map<String, Object>> listKfxx = dao
						.doQuery(
								"select a.KSDM as KSDM,b.KFMC as KFMC,b.EJKF as EJKF,a.MRBZ as MRBZ,b.KFLB as KFLB,b.LBXH as LBXH,b.GLKF as GLKF,b.WXKF as WXKF,b.CKFS as CKFS,b.CSBZ as CSBZ,b.ZJBZ as ZJBZ,b.ZJYF as ZJYF,b.HZPD as HZPD,b.PDZT as PDZT,b.KFZT as KFZT,b.KFZB as KFZB from GY_QXKZ a,WL_KFXX b where a.KSDM=b.KFXH and b.KFZT<>0 and a.YGDM='"
										+ user.getUserId()
										+ "' and a.JGID='"
										+ JGID
										+ "' and a.YWLB='"
										+ YWLB
										+ "' and a.MRBZ=1", null);

				for (int i = 0; i < listKfxx.size(); i++) {
					if (listKfxx.get(i).get("KSDM") != null
							&& listKfxx.get(i).get("KSDM") != "") {
						KFXH = Integer.parseInt(listKfxx.get(i).get("KSDM")
								+ "");
					}
				}

			}
			Map<String, Object> parameter = new HashMap<String, Object>();
			parameter.put("KFXH", KFXH);
			Calendar a = Calendar.getInstance();
			int year = Integer.parseInt(request.get("date").toString()
					.substring(0, request.get("date").toString().indexOf("-")));
			int month = Integer
					.parseInt(request
							.get("date")
							.toString()
							.substring(
									request.get("date").toString().indexOf("-") + 1));
			a.set(Calendar.YEAR, year);
			a.set(Calendar.MONTH, month - 1);
			a.set(Calendar.DATE, 10);
			a.set(Calendar.HOUR_OF_DAY, 0);
			a.set(Calendar.MINUTE, 0);
			a.set(Calendar.SECOND, 0);
			String cwyf = sdfyf.format(a.getTime());
			a.set(Calendar.MONTH, month - 2);
			String sycwyf = sdfyf.format(a.getTime());
			List<String> list_qszzsj;

			list_qszzsj = dateQuery(request, dao, ctx);
			parameter.put("CWYF", cwyf);
			String sql = "select t.JZXH as JZXH from WL_YJJL t where t.CWYF =:CWYF and t.KFXH =:KFXH";
			List<Map<String, Object>> rklist = dao.doSqlQuery(sql, parameter);
			// 如果在WL_YJJL 中存在 则直接调用历史月结中的数据 否则要逐个查询。
			List<Map<String, Object>> inofList = new ArrayList<Map<String, Object>>();
			if (rklist.size() > 0) {
				parameter.clear();
				parameter.put("JGID", JGID);
				parameter.put("KFXH", KFXH);
				parameter.put("CWYF", cwyf);
				StringBuffer sql_list = new StringBuffer(
						"SELECT DISTINCT t2.WZMC as WZMC,t2.WZGG as WZGG,t1.CJMC as CJMC,t2.WZDW as WZDW,t.JZXH as JZXH,t.ZBLB as ZBLB,t.WZXH as WZXH, ");
				sql_list.append(" t.CJXH as CJXH,t.QCSL as QCSL,t.QCJE as QCJE,t.RKSL as RKSL, ");
				sql_list.append(" t.RKJE as RKJE,t.CKSL as CKSL,t.CKJE as CKJE,t.QMSL as QMSL,t.QMJE as QMJE,t2.HSLB as HSLB FROM");
				sql_list.append(" WL_YJJG t  LEFT OUTER JOIN WL_SCCJ t1  ON t.CJXH = t1.CJXH, WL_WZZD t2, WL_YJJL t3 ");
				sql_list.append(" WHERE (t.WZXH = t2.WZXH) AND t.JZXH = t3.JZXH AND t3.JGID =:JGID AND t3.KFXH =:KFXH AND t3.CWYF =:CWYF ");
				sql_list.append(" ORDER BY t2.WZMC ASC, t2.WZGG ASC ");
				inofList = dao.doSqlQuery(sql_list.toString(), parameter);
			} else {
				parameter.clear();
				parameter.put("al_Jgid", JGID);
				parameter.put("al_kfxh", KFXH);
				parameter.put("adt_qsrq", sdftime.parse(list_qszzsj.get(0)));
				parameter.put("adt_jsrq", sdftime.parse(list_qszzsj.get(1)));
				parameter.put("as_qsyf", sycwyf);
				/**
				 * Adt_ksrq 当前月份的开始日期 Adt_jsrq 当前月份的截止日期 as_qsyf 当前月份的上一个月份
				 **/
				StringBuffer sql_list = new StringBuffer(
						"SELECT DISTINCT WL_WZZD.WZMC as WZMC,WL_WZZD.WZGG as WZGG,WL_SCCJ.CJMC as CJMC,WL_WZZD.WZDW as WZDW, ");
				sql_list.append("  B.WZXH as WZXH ,B.CJXH as CJXH,B.QCSL as QCSL,B.QCJE as QCJE,B.RKSL as RKSL,B.RKJE as RKJE,B.CKSL as CKSL, ");
				sql_list.append("  B.CKJE as CKJE,WL_WZZD.HSLB,(B.QCSL + B.RKSL - B.CKSL) as  QMSL,(B.QCJE + B.RKJE - B.CKJE)  as QMJE   FROM WL_WZZD, ");
				sql_list.append("  (SELECT A.WZXH,A.CJXH,SUM(QCSL) QCSL,SUM(QCJE) QCJE,SUM(RKSL) RKSL,SUM(RKJE) RKJE,SUM(CKSL) CKSL,SUM(CKJE) CKJE ");
				sql_list.append("  FROM (select wl_yjjg.jlxh, wl_yjjg.wzxh,wl_yjjg.cjxh,wl_yjjg.zblb,wl_yjjg.qmsl qcsl, wl_yjjg.qmje qcje,0.0000 rksl,0.0000 rkje,0.0000 cksl,0.0000 ckje ");
				sql_list.append("  from wl_yjjg, wl_yjjl where wl_yjjg.jzxh = wl_yjjl.jzxh  and wl_yjjl.cwyf =:as_qsyf ");
				sql_list.append("  and wl_yjjl.kfxh =:al_kfxh and wl_yjjl.JGID =:al_Jgid union all  ");
				sql_list.append("  select mxxh,wzxh,cjxh, wl_zcmx.zblb,0.0000,0.0000,wzsl,wzje,0.0000, 0.0000  from wl_zcmx ");
				sql_list.append("  where FSRQ >=:adt_qsrq and FSRQ <=:adt_jsrq  and wl_zcmx.kfxh =:al_kfxh");
				sql_list.append("  and wl_zcmx.JGID =:al_Jgid and WL_ZCMX.YWLB = 1 and (wl_zcmx.djlx = 'RK' Or wl_zcmx.djlx = 'DR') ");
				sql_list.append("  AND WL_zcmx.kcxh > 0 union all  select mxxh,wzxh,cjxh, wl_zcmx.zblb, 0.0000,0.0000,0 - wzsl,0 - wzje,0.0000,0.0000 ");
				sql_list.append("  from wl_zcmx where FSRQ >= :adt_qsrq and FSRQ <= :adt_jsrq  and wl_zcmx.kfxh = :al_kfxh");
				sql_list.append("  and wl_zcmx.JGID = :al_Jgid and  (wl_zcmx.djlx = 'RK' OR wl_zcmx.djlx = 'DR') AND WL_ZCMX.YWLB = -1 and WL_zcmx.kcxh > 0");
				sql_list.append("  union all select mxxh, wzxh,cjxh,wl_zcmx.zblb, 0.0000, 0.0000,0.0000,0.0000,wzsl,wzje");
				sql_list.append("  from wl_zcmx where FSRQ >= :adt_qsrq and FSRQ <= :adt_jsrq  and wl_zcmx.kfxh = :al_kfxh");
				sql_list.append("  and wl_zcmx.JGID = :al_Jgid and (wl_zcmx.djlx = 'CK' or wl_zcmx.djlx = 'DB')");
				sql_list.append("  AND WL_ZCMX.YWLB = -1 and wl_zcmx.kcxh > 0 union all  select mxxh, wzxh, cjxh, wl_zcmx.zblb,  0.0000, ");
				sql_list.append("  0.0000, 0.0000, 0.0000, 0 - wzsl, 0 - wzje from wl_zcmx where FSRQ >= :adt_qsrq and FSRQ <= :adt_jsrq  and wl_zcmx.JGID = :al_Jgid ");
				sql_list.append("  and wl_zcmx.kfxh = :al_kfxh and (((wl_zcmx.djlx = 'CK' or wl_zcmx.djlx = 'DB') AND  WL_ZCMX.YWLB = 1) or wl_zcmx.djlx = 'TK') ");
				sql_list.append("  and Wl_zcmx.ywlb = 1  and kcxh > 0 union all select wl_bs02.jlxh,  wl_bs02.wzxh,  wl_bs02.cjxh, wl_bs01.zblb, 0.0000, 0.0000, 0.0000, 0.0000, ");
				sql_list.append("  wzsl, wzje from wl_bs01, wl_bs02 where jzRQ >= :adt_qsrq and jzRQ <= :adt_jsrq  and wl_bs01.kfxh = :al_kfxh");
				sql_list.append("  and wl_bs01.JGID = :al_Jgid and wl_bs01.djxh = wl_bs02.djxh and wl_bs01.bsfs = 0) A ");
				sql_list.append("  GROUP BY A.WZXH, A.CJXH) B LEFT OUTER JOIN WL_SCCJ ON B.CJXH = WL_SCCJ.CJXH WHERE B.WZXH = WL_WZZD.WZXH ");
				inofList = dao.doSqlQuery(sql_list.toString(), parameter);
			}
			for (int i = 0; i < inofList.size(); i++) {

				Double QCSL = 0.00;
				if (inofList.get(i).get("QCSL") != null) {
					QCSL = Double.parseDouble(inofList.get(i).get("QCSL") + "");
				}
				inofList.get(i).put("QCSL",
						String.format("%1$.2f", QCSL).toString());

				Double QCJE = 0.00;
				if (inofList.get(i).get("QCJE") != null) {
					QCJE = Double.parseDouble(inofList.get(i).get("QCJE") + "");
				}
				inofList.get(i).put("QCJE",
						String.format("%1$.4f", QCJE).toString());

				Double RKSL = 0.00;
				if (inofList.get(i).get("RKSL") != null) {
					RKSL = Double.parseDouble(inofList.get(i).get("RKSL") + "");
				}
				inofList.get(i).put("RKSL",
						String.format("%1$.2f", RKSL).toString());

				Double RKJE = 0.00;
				if (inofList.get(i).get("RKJE") != null) {
					RKJE = Double.parseDouble(inofList.get(i).get("RKJE") + "");
				}
				inofList.get(i).put("RKJE",
						String.format("%1$.4f", RKJE).toString());

				Double CKSL = 0.00;
				if (inofList.get(i).get("CKSL") != null) {
					CKSL = Double.parseDouble(inofList.get(i).get("CKSL") + "");
				}
				inofList.get(i).put("CKSL",
						String.format("%1$.2f", CKSL).toString());

				Double CKJE = 0.00;
				if (inofList.get(i).get("CKJE") != null) {
					CKJE = Double.parseDouble(inofList.get(i).get("CKJE") + "");
				}
				inofList.get(i).put("CKJE",
						String.format("%1$.4f", CKJE).toString());

				Double QMSL = 0.00;
				if (inofList.get(i).get("QMSL") != null) {
					QMSL = Double.parseDouble(inofList.get(i).get("QMSL") + "");
				}
				inofList.get(i).put("QMSL",
						String.format("%1$.2f", QMSL).toString());

				Double QMJE = 0.00;
				if (inofList.get(i).get("QMJE") != null) {
					QMJE = Double.parseDouble(inofList.get(i).get("QMJE") + "");
				}
				inofList.get(i).put("QMJE",
						String.format("%1$.4f", QMJE).toString());

				records.add(inofList.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGIDNAMW = user.getManageUnitName();// 用户的机构ID
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		String KFNAME = "";
		if (user.getProperty("treasuryName") != null) {
			KFNAME = user.getProperty("treasuryName") + "";
		}
		if (KFNAME == "") {
			String YWLB = "6";
			List<Map<String, Object>> listkfxx;
			try {
				listkfxx = dao
						.doQuery(
								"select a.KSDM as KSDM,b.KFMC as KFMC,b.EJKF as EJKF,a.MRBZ as MRBZ,b.KFLB as KFLB,b.LBXH as LBXH,b.GLKF as GLKF,b.WXKF as WXKF,b.CKFS as CKFS,b.CSBZ as CSBZ,b.ZJBZ as ZJBZ,b.ZJYF as ZJYF,b.HZPD as HZPD,b.PDZT as PDZT,b.KFZT as KFZT,b.KFZB as KFZB from GY_QXKZ a,WL_KFXX b where a.KSDM=b.KFXH and b.KFZT<>0 and a.YGDM='"
										+ user.getUserId()
										+ "' and a.JGID='"
										+ manaUnitId
										+ "' and a.YWLB='"
										+ YWLB
										+ "' and a.MRBZ=1", null);
				for (int i = 0; i < listkfxx.size(); i++) {
					if (listkfxx.get(i).get("KFMC") != null
							&& listkfxx.get(i).get("KFMC") != "") {
						KFNAME = listkfxx.get(i).get("KFMC") + "";
					}
				}
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		}
		String ZBR = user.getUserName();
		response.put("TITLE1", JGIDNAMW + KFNAME);
		response.put("DYRQ", new Date());
		response.put("ZBR", ZBR);
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-23
	 * @description 采购入库时间条件查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<String> dateQuery(Map<String, Object> body, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		List<String> l = new ArrayList<String>();
		String jgid = user.getManageUnitId();// 用户的机构ID
		int kfxh = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		try {
			if (kfxh == 0) {
				String YWLB = "6";
				List<Map<String, Object>> listKfxx = dao
						.doQuery(
								"select a.KSDM as KSDM,b.KFMC as KFMC,b.EJKF as EJKF,a.MRBZ as MRBZ,b.KFLB as KFLB,b.LBXH as LBXH,b.GLKF as GLKF,b.WXKF as WXKF,b.CKFS as CKFS,b.CSBZ as CSBZ,b.ZJBZ as ZJBZ,b.ZJYF as ZJYF,b.HZPD as HZPD,b.PDZT as PDZT,b.KFZT as KFZT,b.KFZB as KFZB from GY_QXKZ a,WL_KFXX b where a.KSDM=b.KFXH and b.KFZT<>0 and a.YGDM='"
										+ user.getUserId()
										+ "' and a.JGID='"
										+ jgid
										+ "' and a.YWLB='"
										+ YWLB
										+ "' and a.MRBZ=1", null);

				for (int i = 0; i < listKfxx.size(); i++) {
					if (listKfxx.get(i).get("KSDM") != null
							&& listKfxx.get(i).get("KSDM") != "") {
						kfxh = Integer.parseInt(listKfxx.get(i).get("KSDM")
								+ "");
					}
				}

			}
			int year = Integer.parseInt(body.get("date").toString()
					.substring(0, body.get("date").toString().indexOf("-")));
			int month = Integer.parseInt(body.get("date").toString()
					.substring(body.get("date").toString().indexOf("-") + 1));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdfcwyf = new SimpleDateFormat("yyyyMM");
			StringBuffer hql_qszz = new StringBuffer();
			hql_qszz.append("select distinct QSSJ as QSSJ,ZZSJ as ZZSJ from ")
					.append("WL_YJJL")
					.append(" where CWYF=:cwyf and KFXH=:kfxh and CSBZ=0");
			Calendar a = Calendar.getInstance();
			a.set(Calendar.YEAR, year);
			a.set(Calendar.MONTH, month - 1);
			a.set(Calendar.DATE, 10);
			a.set(Calendar.HOUR_OF_DAY, 0);
			a.set(Calendar.MINUTE, 0);
			a.set(Calendar.SECOND, 0);
			Map<String, Object> map_par_qszz = new HashMap<String, Object>();
			map_par_qszz.put("cwyf", sdfcwyf.format(a.getTime()));
			map_par_qszz.put("kfxh", kfxh);

			Map<String, Object> map_qszz = dao.doLoad(hql_qszz.toString(),
					map_par_qszz);
			if (map_qszz != null && map_qszz.get("QSSJ") != null
					&& map_qszz.get("ZZSJ") != null) {
				l.add(sdf.format((Date) map_qszz.get("QSSJ")));
				l.add(sdf.format((Date) map_qszz.get("ZZSJ")));
				return l;
			}
			a.set(Calendar.MONTH, month - 2);
			map_par_qszz.put("cwyf", sdfcwyf.format(a.getTime()));
			map_qszz = dao.doLoad(hql_qszz.toString(), map_par_qszz);
			if (map_qszz != null && map_qszz.get("QSSJ") != null) {
				l.add(sdf.format((Date) map_qszz.get("QSSJ")));
			}
			int yjDate = Integer.parseInt(ParameterUtil.getParameter(jgid,
					"YJSJ_KF" + kfxh, "32", "库房月结时间 对应一个月的31天  32为月底结 ", ctx));
			if (l.size() == 0) {
				int lastDay = a.getActualMaximum(Calendar.DAY_OF_MONTH);
				if (yjDate < lastDay) {
					a.set(Calendar.DATE, yjDate + 1);
				} else {
					a.set(Calendar.DATE, lastDay + 1);
				}
				a.set(Calendar.HOUR_OF_DAY, 0);
				a.set(Calendar.MINUTE, 0);
				a.set(Calendar.SECOND, 0);
				l.add(sdf.format(a.getTime()));
			}
			a.set(Calendar.MONTH, month - 1);
			int lastDay = a.getActualMaximum(Calendar.DAY_OF_MONTH);
			if (yjDate < lastDay) {
				a.set(Calendar.DATE, yjDate);
			} else {
				a.set(Calendar.DATE, lastDay);
			}
			a.set(Calendar.HOUR_OF_DAY, 23);
			a.set(Calendar.MINUTE, 59);
			a.set(Calendar.SECOND, 59);
			l.add(sdf.format(a.getTime()));
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "日期查询失败");
		}
		return l;
	}
}
