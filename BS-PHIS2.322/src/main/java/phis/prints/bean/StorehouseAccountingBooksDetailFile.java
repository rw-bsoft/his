package phis.prints.bean;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.application.sto.source.StorehouseAccountingBooksModel;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class StorehouseAccountingBooksDetailFile implements IHandler{

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		response.put("JGMC", user.getManageUnitName());
		StringBuffer hql=new StringBuffer();
		hql.append("select a.YPMC as YPMC,c.CDMC as CDMC from YK_TYPK a,YK_CDXX b,YK_CDDZ c where a.YPXH=b.YPXH and b.YPCD=c.YPCD and b.YPXH=:ypxh and b.YPCD=:ypcd and b.JGID=:jgid");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("ypxh", MedicineUtils.parseLong(request.get("ypxh")));
		map_par.put("ypcd", MedicineUtils.parseLong(request.get("ypcd")));
		map_par.put("jgid", user.getManageUnitId());
		try {
			Map<String,Object> map_ypxx=dao.doLoad(hql.toString(), map_par);
			response.putAll(map_ypxx);
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000,"查询会计账簿明细失败"+e.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		String dateFrom=MedicineUtils.parseString(request.get("dateFrom"));//开始时间
		String dateTo=MedicineUtils.parseString(request.get("dateTo"));//结束时间
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, MedicineUtils.parseInt(dateFrom.split("-")[0]));
		a.set(Calendar.MONTH, MedicineUtils.parseInt(dateFrom.split("-")[1]) - 1);
		a.set(Calendar.DATE, 10);
		a.set(Calendar.HOUR_OF_DAY, 0);
		a.set(Calendar.MINUTE, 0);
		a.set(Calendar.SECOND, 0);
		a.set(Calendar.MILLISECOND, 0);
		Date cwyf=a.getTime();
		StorehouseAccountingBooksModel model=new StorehouseAccountingBooksModel(dao);
		try {
			Date jssj=model.getYjsjByMonth(MedicineUtils.parseInt(dateTo.split("-")[0]),MedicineUtils.parseInt(dateTo.split("-")[1]),yksb,user.getManageUnitId(),ctx);
			StringBuffer hql_qc=new StringBuffer();//统计期初数据
			StringBuffer hql_rk=new StringBuffer();//统计入库数据
			StringBuffer hql_ck=new StringBuffer();//统计出库数据
			StringBuffer hql_tj=new StringBuffer();//统计调价数据
			StringBuffer hql_pz=new StringBuffer();//统计平账数据
			hql_qc.append("select a.KCSL as JCSL,a.LSJE as JCJE,a.LSJG as LSJG,b.ZZSJ as RQ,'期初结存' as ZY,'' as PZH from YK_YJJG a,YK_JZJL b where a.XTSB=b.XTSB and a.CWYF=b.CWYF and a.YPXH=:ypxh and a.YPCD=:ypcd and a.XTSB=:xtsb and a.CWYF=:cwyf");
			hql_rk.append("select 'rk' as TAG,a.RKSL as RKSL,a.LSJE as RKJE,a.LSJG as LSJG,a.YSRQ as RQ, b.FSMC as ZY,a.RKDH as PZH,1 as BZ,a.XTSB as XTSB,a.RKFS as FS,a.YSDH as YSDH,c.DWXH as DWXH from YK_RK02 a,YK_RKFS b,YK_RK01 c where a.XTSB=c.XTSB and a.RKFS=c.RKFS and a.RKDH=c.RKDH and a.RKFS=b.RKFS and a.XTSB=:xtsb and a.YSRQ>=:begin and a.YSRQ<=:end and a.YPXH=:ypxh and a.YPCD=:ypcd");
			hql_ck.append("select 'ck' as TAG,b.SFSL as CKSL,b.LSJE as CKJE,b.LSJG as LSJG,a.CKRQ as RQ, c.FSMC as ZY,a.CKDH as PZH,2 as BZ,a.XTSB as XTSB,a.CKFS as FS,c.KSPB as KSPB from YK_CK01 a,YK_CK02 b,YK_CKFS c where a.CKFS=c.CKFS and  b.XTSB=:xtsb and a.CKRQ>=:begin and a.CKRQ<=:end and a.XTSB=b.XTSB and a.CKDH=b.CKDH and a.CKFS=b.CKFS and a.CKPB=1 and b.YPXH=:ypxh and b.YPCD=:ypcd");
			hql_tj.append("select 'tj' as TAG,b.LSZZ-b.LSJZ as LSJE,b.XLSJ as LSJG,(case a.TJFS when 1 then '国家调价' when 2 then '企业调价' else '进货调价' end) as ZY,a.TJDH as PZH,a.ZXRQ as RQ,a.TJFS as FS,a.JGID as JGID,a.XTSB as XTSB,3 as BZ  from YK_TJ01 a,YK_TJ02 b where  b.XTSB=:xtsb and a.ZXRQ>=:begin and a.ZXRQ<=:end and a.XTSB=b.XTSB and a.TJDH=b.TJDH and a.TJFS=b.TJFS and a.ZYPB=1 and b.YPXH=:ypxh and b.YPCD=:ypcd");
			hql_pz.append("select 'pz' as TAG,b.XLSE-b.YLSE as LSJE,b.XLSJ as LSJG,a.PZYY as ZY,a.RCDH as PZH,0 as BZ,a.PZRQ as RQ from YK_PZ01 a,YK_PZ02 b where a.PZID=b.PZID and a.XTSB=:xtsb and a.PZRQ>=:begin and a.PZRQ<=:end and b.YPXH=:ypxh and b.YPCD=:ypcd ");
			double JCJE=0;
			double JCSL=0;
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("cwyf", cwyf);
			map_par.put("ypxh", MedicineUtils.parseLong(request.get("ypxh")));
			map_par.put("ypcd", MedicineUtils.parseLong(request.get("ypcd")));
			map_par.put("xtsb", yksb);
			Map<String,Object> map_qc=dao.doLoad(hql_qc.toString(), map_par);
			JCJE=MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_qc.get("JCJE")));
			JCSL=MedicineUtils.formatDouble(2, MedicineUtils.parseDouble(map_qc.get("JCSL")));
			Date kssj=(Date)map_qc.get("RQ");//月结的终止时间作为查询的开始时间
			map_qc.put("RQ", MedicineUtils.parseString(map_qc.get("RQ")).substring(0, 10));
			records.add(map_qc);
			map_par.remove("cwyf");
			map_par.put("begin", kssj);
			map_par.put("end", jssj);
			List<Map<String,Object>> list_rkjl=dao.doQuery(hql_rk.toString(), map_par);
			records.addAll(list_rkjl);
			List<Map<String,Object>> list_ckjl=dao.doQuery(hql_ck.toString(), map_par);
			records.addAll(list_ckjl);
			List<Map<String,Object>> list_tjjl=dao.doQuery(hql_tj.toString(), map_par);
			records.addAll(list_tjjl);
			List<Map<String,Object>> list_pzjl=dao.doQuery(hql_pz.toString(), map_par);
			records.addAll(list_pzjl);
			//按日期排序
			Collections.sort(records,new Comparator() {
			      @Override
			      public int compare(Object o1, Object o2) {
			    	  int d1=((((Map<String,Object>)o1).get("RQ")+"").substring(0, 10)).hashCode();
			    	  int d2=((((Map<String,Object>)o2).get("RQ")+"").substring(0, 10)).hashCode();
			    	  return d1-d2;
			      }});
			for(Map<String,Object> map:records){
				if("rk".equals(map.get("TAG"))){
					JCJE=MedicineUtils.formatDouble(4, JCJE+MedicineUtils.parseDouble(map.get("RKJE")));
					JCSL=MedicineUtils.formatDouble(4, JCSL+MedicineUtils.parseDouble(map.get("RKSL")));
				}else if("ck".equals(map.get("TAG"))){
					JCJE=MedicineUtils.formatDouble(4, JCJE-MedicineUtils.parseDouble(map.get("CKJE")));
					JCSL=MedicineUtils.formatDouble(4, JCSL-MedicineUtils.parseDouble(map.get("CKSL")));
				}else if("tj".equals(map.get("TAG"))){
					JCJE=MedicineUtils.formatDouble(4, JCJE+MedicineUtils.parseDouble(map.get("LSJE")));
				}else if("pz".equals(map.get("TAG"))){
					JCJE=MedicineUtils.formatDouble(4, JCJE+MedicineUtils.parseDouble(map.get("LSJE")));
				}
				map.put("JCJE", JCJE);
				map.put("JCSL", JCSL);
			}
//			for(Map<String,Object> map_rkjl:list_rkjl){
//				JCJE=MedicineUtils.formatDouble(4, JCJE+MedicineUtils.parseDouble(map_rkjl.get("RKJE")));
//				JCSL=MedicineUtils.formatDouble(4, JCSL+MedicineUtils.parseDouble(map_rkjl.get("RKSL")));
//				map_rkjl.put("JCJE", JCJE);
//				map_rkjl.put("JCSL", JCSL);
//				records.add(map_rkjl);
//			}
//			List<Map<String,Object>> list_ckjl=dao.doQuery(hql_ck.toString(), map_par);
//			for(Map<String,Object> map_ckjl:list_ckjl){
//				JCJE=MedicineUtils.formatDouble(4, JCJE-MedicineUtils.parseDouble(map_ckjl.get("CKJE")));
//				JCSL=MedicineUtils.formatDouble(4, JCSL-MedicineUtils.parseDouble(map_ckjl.get("CKSL")));
//				map_ckjl.put("JCJE", JCJE);
//				map_ckjl.put("JCSL", JCSL);
//				records.add(map_ckjl);
//			}
//			List<Map<String,Object>> list_tjjl=dao.doQuery(hql_tj.toString(), map_par);
//			for(Map<String,Object> map_tjjl:list_tjjl){
//				JCJE=MedicineUtils.formatDouble(4, JCJE+MedicineUtils.parseDouble(map_tjjl.get("LSJE")));
//				map_tjjl.put("JCJE", JCJE);
//				map_tjjl.put("JCSL", JCSL);
//				records.add(map_tjjl);
//			}
//			List<Map<String,Object>> list_pzjl=dao.doQuery(hql_pz.toString(), map_par);
//			for(Map<String,Object> map_pzjl:list_pzjl){
//				JCJE=MedicineUtils.formatDouble(4, JCJE+MedicineUtils.parseDouble(map_pzjl.get("LSJE")));
//				map_pzjl.put("JCJE", JCJE);
//				map_pzjl.put("JCSL", JCSL);
//				records.add(map_pzjl);
//			}
//			Collections.sort(records,new Comparator() {
//			      @Override
//			      public int compare(Object o1, Object o2) {
//			    	  int d1=((((Map<String,Object>)o1).get("RQ")+"").substring(0, 10)).hashCode();
//			    	  int d2=((((Map<String,Object>)o2).get("RQ")+"").substring(0, 10)).hashCode();
//			    	  return d1-d2;
//			      }});
		} catch (ModelDataOperationException e) {
			throw new PrintException(9000,"查询会计账簿明细失败:"+e.getMessage());
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000,"查询会计账簿明细失败:"+e.getMessage());
		}
		
	}

}
