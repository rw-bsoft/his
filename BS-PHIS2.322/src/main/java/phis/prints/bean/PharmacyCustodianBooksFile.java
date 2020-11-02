package phis.prints.bean;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.ParameterUtil;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class PharmacyCustodianBooksFile implements IHandler{

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		BaseDAO dao = new BaseDAO(ctx);
		try {
			Map<String,Object> map_yf=dao.doLoad("phis.application.pha.schemas.YF_YFLB", MedicineUtils.parseLong(user.getProperty("pharmacyId")));
			response.put("YFMC", user.getManageUnit().getName()+map_yf.get("YFMC"));
			response.put("SJ", request.get("dateFrom")+"--"+request.get("dateTo"));
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000,"查询药房失败"+e.getMessage());
		}
		
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateFrom=MedicineUtils.parseString(request.get("dateFrom"));
		String dateTo=MedicineUtils.parseString(request.get("dateTo"));
		try {
			//Date cwyf=sdf.parse(dateFrom+"-10");
			String cwyf=dateFrom+"-10";
			Map<String,Object> map_par_cwyf=new HashMap<String,Object>();
			map_par_cwyf.put("cwyf", cwyf);
			map_par_cwyf.put("yfsb", yfsb);
			StringBuffer hql=new StringBuffer();
			hql.append("select ZZSJ as ZZSJ from YF_JZJL where YFSB=:yfsb and to_char(CWYF,'yyyy-mm-dd')=:cwyf");
			Map<String,Object> map_zzsj=dao.doLoad(hql.toString(), map_par_cwyf);
			if(map_zzsj==null||map_zzsj.size()==0||map_zzsj.get("ZZSJ")==null){
				return;
			}
			Date kssj=(Date)map_zzsj.get("ZZSJ");//本次查询开始的时间(月结的终止时间)
			int yjDate = Integer.parseInt(ParameterUtil.getParameter(jgid,
					"YJSJ_YF" + yfsb,
					BSPHISSystemArgument.defaultValue.get("YJSJ_YF"),
					BSPHISSystemArgument.defaultAlias.get("YJSJ_YF"),BSPHISSystemArgument.defaultCategory.get("YJSJ_YF"), ctx));// 月结日
			String [] sj=dateTo.split("-");
			Calendar a = Calendar.getInstance();
			a.set(Calendar.YEAR, MedicineUtils.parseInt(sj[0]));
			a.set(Calendar.MONTH, MedicineUtils.parseInt(sj[1])-1);
			a.set(Calendar.DATE, 1);// 把日期设置为当月第一天
			a.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
			int lastDate = a.get(Calendar.DATE);// 上次月结月的最后一天
			int yj_day = yjDate;
			if (yjDate > lastDate) {
				yj_day = lastDate;
			}
			a.set(Calendar.DATE, yj_day);
			a.set(Calendar.HOUR_OF_DAY, 23);
			a.set(Calendar.MINUTE, 59);
			a.set(Calendar.SECOND, 59);
			Date zzsj = a.getTime();//本次查询的结束时间
			if(kssj.getTime()>=zzsj.getTime()){
				return;
			}
			DecimalFormat df_two = new DecimalFormat("###.00");
			StringBuffer hql_yfyp = new StringBuffer();// 统计药房药品
			StringBuffer hql_syjc = new StringBuffer();// 统计上月结存
			StringBuffer hql_rksl = new StringBuffer();// 统计入库数量
			StringBuffer hql_cksl = new StringBuffer();// 统计出库数量
			StringBuffer hql_fysl = new StringBuffer();// 统计发药数量
			//StringBuffer hql_tjje = new StringBuffer();// 统计调价金额
			StringBuffer hql_yk = new StringBuffer();// 统计盈盈亏数据
			StringBuffer hql_bqfy = new StringBuffer();// 统计病区发药
			StringBuffer hql_jcfy = new StringBuffer();// 统计家床发药
			StringBuffer hql_dbrk = new StringBuffer();// 统计调拨入库
			StringBuffer hql_dbck = new StringBuffer();// 统计调拨出库
			StringBuffer hql_ypsl = new StringBuffer();// 统计药品申领和退药
			//..家床
			hql_yfyp.append("select jg.YPCD as YPCD,jg.YPXH as YPXH,yf.YFBZ as YFBZ,yp.ZXBZ as ZXBZ,yp.YPGG as YFGG,yf.YFDW as YFDW,(jg.LSJG/yp.ZXBZ)*yf.YFBZ as LSJG,dz.CDMC as CDMC,yp.YPMC as YPMC  from YK_TYPK yp,YF_YPXX yf,YK_CDXX jg,YK_CDDZ dz,YK_YPBM bm where bm.YPXH=yp.YPXH and dz.YPCD=jg.YPCD and yp.YPXH=yf.YPXH and jg.YPXH=yf.YPXH and yf.JGID=jg.JGID and yf.YFSB=:yfsb");
			hql_syjc.append("select a.KCSL as YPSL,a.LSJG as LSJG,a.YFBZ as YFBZ,a.LSJE as LSJE,a.YPXH as YPXH,a.YPCD as YPCD from YF_YJJG a,YF_JZJL b where a.YFSB=b.YFSB and a.CWYF=b.CWYF and a.YFSB=:yfsb  and b.ZZSJ=:zzsj");
			hql_rksl.append("select sum(b.RKSL) as YPSL,sum(b.LSJE) as LSJE,b.YFBZ as YFBZ,b.YPXH as YPXH,b.YPCD as YPCD from YF_RK01 a,YF_RK02 b  where a.YFSB =b.YFSB and a.RKFS =b.RKFS  and a.RKDH =b.RKDH and a.RKPB =1 and a.RKRQ >:begin and a.RKRQ <=:end and a.YFSB=:yfsb  group by b.YFBZ,b.YPXH,b.YPCD");
			hql_cksl.append("select sum(b.CKSL) as YPSL,sum(b.LSJE) as LSJE,b.YFBZ as YFBZ,b.YPXH as YPXH,b.YPCD as YPCD from YF_CK01 a,YF_CK02 b  where a.YFSB =b.YFSB and a.CKFS =b.CKFS  and a.CKDH =b.CKDH and a.CKPB =1 and a.CKRQ >:begin and a.CKRQ <=:end and a.YFSB=:yfsb  group by b.YFBZ,b.YPXH,b.YPCD");
			hql_fysl.append("select sum(YPSL) as YPSL,YFBZ as YFBZ,sum(LSJE) as LSJE,YPXH as YPXH,YPCD as YPCD  from YF_MZFYMX  where YFSB=:yfsb and FYRQ >:begin and FYRQ <=:end  group by YFBZ,YPXH,YPCD");
			//hql_tjje.append("select XLSE-YLSE as LSJE from YF_TJJL  where  YFSB=:yfsb and TJRQ>:begin and TJRQ<=:end");
			hql_yk.append("select sum(SPSL-PQSL) as YPSL,sum(XLSE-YLSE) as LSJE,sum(XPFE-YPFE) as PFJE,sum(XJHE-YJHE) as JHJE,b.YFBZ as YFBZ,b.YPXH as YPXH,b.YPCD as YPCD from YF_YK01 a,YF_YK02 b where a.YFSB=b.YFSB and a.CKBH=b.CKBH and a.PDDH=b.PDDH and a.PDWC=1 and a.WCRQ >:begin and a.WCRQ <=:end  and a.YFSB=:yfsb  group by b.YFBZ,b.YPXH,b.YPCD");
			hql_bqfy.append("select sum(YPSL) as YPSL,YFBZ as YFBZ,sum(LSJE) as LSJE,YPXH as YPXH,YPCD as YPCD  from YF_ZYFYMX where YFSB=:yfsb and JFRQ >:begin and JFRQ <=:end  group by LSJG,YFBZ,YPXH,YPCD");
			hql_jcfy.append("select sum(YPSL) as YPSL,YFBZ as YFBZ,sum(LSJE) as LSJE,YPXH as YPXH,YPCD as YPCD  from YF_JCFYMX where YFSB=:yfsb and JFRQ >:begin and JFRQ <=:end  group by LSJG,YFBZ,YPXH,YPCD");
			hql_dbrk.append("select sum(b.QRSL) as YPSL,sum(b.LSJE) as LSJE,b.YFBZ as YFBZ,b.YPXH as YPXH,b.YPCD as YPCD from YF_DB01  a,YF_DB02  b  where  a.SQYF = b.SQYF and a.SQDH = b.SQDH  and a.RKBZ = 1 and a.SQYF=:yfsb and a.RKRQ >:begin and a.RKRQ <=:end  group by b.YFBZ,b.YPXH,b.YPCD");
			hql_dbck.append("select sum(b.QRSL) as YPSL,sum(b.LSJE) as LSJE,b.YFBZ as YFBZ,b.YPXH as YPXH,b.YPCD as YPCD from YF_DB01 a,YF_DB02 b  where  a.SQYF = b.SQYF and a.SQDH = b.SQDH  and a.CKBZ = 1 and a.MBYF=:yfsb and a.CKRQ >:begin and a.CKRQ <=:end  group by b.YFBZ,b.YPXH,b.YPCD");
			hql_ypsl.append("select sum(b.SFSL) as YPSL,sum(b.LSJE) as LSJE,c.ZXBZ as ZXBZ,b.YPXH as YPXH,b.YPCD as YPCD from YK_CK01 a,YK_CK02 b,YK_TYPK  c  where b.YPXH=c.YPXH  and a.XTSB = b.XTSB and a.CKFS = b.CKFS  and a.CKDH = b.CKDH and a.LYPB = 1 and a.LYRQ >:begin  and a.LYRQ <=:end and a.YFSB =:yfsb  group by c.ZXBZ,b.YPXH,b.YPCD");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yfsb", yfsb);
			if(request.containsKey("yplx")){
				hql_yfyp.append(" and yp.TYPE=:yplx");
				map_par.put("yplx", MedicineUtils.parseInt(request.get("yplx")));
			}
			if(request.containsKey("pydm")){
				hql_yfyp.append(" and bm.PYDM like :pydm");
				map_par.put("pydm", request.get("pydm")+"%");
			}
			List<Map<String,Object>> list_ypxx=dao.doQuery(hql_yfyp.toString(), map_par);//查询当前药房所有药品
			if(list_ypxx==null||list_ypxx.size()==0){
				return;
			}
			if(map_par.containsKey("yplx")){
				map_par.remove("yplx");
			}
			if(map_par.containsKey("pydm")){
				map_par.remove("pydm");
			}
			map_par.put("zzsj", kssj);
			List<Map<String,Object>> list_syjc=dao.doQuery(hql_syjc.toString(), map_par);//开始时间的月的结存数据
			map_par.remove("zzsj");
			map_par.put("begin", kssj);
			map_par.put("end", zzsj);
			List<Map<String,Object>> list_rkjl=dao.doSqlQuery(hql_rksl.toString(), map_par);//入库记录
			List<Map<String,Object>> list_ckjl=dao.doSqlQuery(hql_cksl.toString(), map_par);//出库记录
			List<Map<String,Object>> list_fyjl=dao.doSqlQuery(hql_fysl.toString(), map_par);//发药记录
			//List<Map<String,Object>> list_tjjl=dao.doSqlQuery(hql_tjje.toString(), map_par);//调价记录
			List<Map<String,Object>> list_ykjl=dao.doSqlQuery(hql_yk.toString(), map_par);//盈亏记录
			List<Map<String,Object>> list_bqfyjl=dao.doSqlQuery(hql_bqfy.toString(), map_par);//病区发药记录
			List<Map<String,Object>> list_jcfyjl=dao.doSqlQuery(hql_jcfy.toString(), map_par);//病区发药记录
			List<Map<String,Object>> list_dbrkjl=dao.doSqlQuery(hql_dbrk.toString(), map_par);//调拨入库记录
			List<Map<String,Object>> list_dbckjl=dao.doSqlQuery(hql_dbck.toString(), map_par);//调拨出库库记录
			List<Map<String,Object>> list_ypsljl=dao.doSqlQuery(hql_ypsl.toString(), map_par);//药品申领记录
			
			for (Map<String, Object> map_yp : list_ypxx) {
				map_yp.put("KSSJ", sdf.format(kssj));
				map_yp.put("ZZSJ", sdf.format(zzsj));
				double qcsl = 0;
				double qcje = 0;
				double rksl = 0;
				double rkje = 0;
				double cksl = 0;
				double ckje = 0;
				double qmsl = 0;
				double qmje = 0;
				for(Map<String,Object> map_syjc:list_syjc){
					if(MedicineUtils.compareMaps(map_yp, new String[]{"YPXH","YPCD"}, map_syjc, new String[]{"YPXH","YPCD"})){
						qcsl=MedicineUtils.parseDouble(df_two
								.format(MedicineUtils.parseDouble(map_syjc
										.get("YPSL"))
										* MedicineUtils.parseDouble(map_syjc
												.get("YFBZ"))
										/ MedicineUtils.parseDouble(map_yp
												.get("YFBZ"))));
						qcje=MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_syjc.get("LSJE")));
						break;
						}
				}
				
				for(Map<String,Object> map_rksl:list_rkjl){
					if(MedicineUtils.compareMaps(map_yp, new String[]{"YPXH","YPCD"}, map_rksl, new String[]{"YPXH","YPCD"})){
						rksl = rksl
								+ MedicineUtils.parseDouble(df_two
										.format(MedicineUtils
												.parseDouble(map_rksl
														.get("YPSL"))
												* MedicineUtils
														.parseDouble(map_rksl
																.get("YFBZ"))
												/ MedicineUtils
														.parseDouble(map_yp
																.get("YFBZ"))));
						rkje =MedicineUtils.formatDouble(4, rkje
								+ MedicineUtils.parseDouble(map_rksl
										.get("LSJE"))) ;
					}
					}
				
				for(Map<String,Object> map_cksl:list_ckjl){
					if(MedicineUtils.compareMaps(map_yp, new String[]{"YPXH","YPCD"}, map_cksl, new String[]{"YPXH","YPCD"})){
						cksl = cksl
								+ MedicineUtils.parseDouble(df_two
										.format(MedicineUtils
												.parseDouble(map_cksl
														.get("YPSL"))
												* MedicineUtils
														.parseDouble(map_cksl
																.get("YFBZ"))
												/ MedicineUtils
														.parseDouble(map_yp
																.get("YFBZ"))));
						ckje=MedicineUtils.formatDouble(4, ckje+MedicineUtils.parseDouble(map_cksl
								.get("LSJE")));
						
					}
					}
				for(Map<String,Object> map_fysl:list_fyjl){
					if(MedicineUtils.compareMaps(map_yp, new String[]{"YPXH","YPCD"}, map_fysl, new String[]{"YPXH","YPCD"})){
						cksl = cksl
								+ MedicineUtils.parseDouble(df_two
										.format(MedicineUtils
												.parseDouble(map_fysl
														.get("YPSL"))
												* MedicineUtils
														.parseDouble(map_fysl
																.get("YFBZ"))
												/ MedicineUtils
														.parseDouble(map_yp
																.get("YFBZ"))));
						ckje=MedicineUtils.formatDouble(4, ckje+MedicineUtils.parseDouble(map_fysl
								.get("LSJE")));	
					}
					}
				for(Map<String,Object> map_yk:list_ykjl){
					if(MedicineUtils.compareMaps(map_yp, new String[]{"YPXH","YPCD"}, map_yk, new String[]{"YPXH","YPCD"})){
						double yksl=MedicineUtils.parseDouble(df_two
								.format(MedicineUtils
										.parseDouble(map_yk
												.get("YPSL"))
										* MedicineUtils
												.parseDouble(map_yk
														.get("YFBZ"))
										/ MedicineUtils
												.parseDouble(map_yp
														.get("YFBZ"))));
						if(yksl<0){
							cksl = cksl-yksl ;
							ckje=MedicineUtils.formatDouble(4, ckje-MedicineUtils.parseDouble(map_yk
									.get("LSJE")));
						}else{
							rksl = rksl+yksl ;
							rkje=MedicineUtils.formatDouble(4, rkje+MedicineUtils.parseDouble(map_yk
									.get("LSJE")));	
						}
						
					}
					}
				for(Map<String,Object> map_fysl:list_bqfyjl){
					if(MedicineUtils.compareMaps(map_yp, new String[]{"YPXH","YPCD"}, map_fysl, new String[]{"YPXH","YPCD"})){
						cksl = cksl
								+ MedicineUtils.parseDouble(df_two
										.format(MedicineUtils
												.parseDouble(map_fysl
														.get("YPSL"))
												* MedicineUtils
														.parseDouble(map_fysl
																.get("YFBZ"))
												/ MedicineUtils
														.parseDouble(map_yp
																.get("YFBZ"))));
						ckje=MedicineUtils.formatDouble(4, ckje+MedicineUtils.parseDouble(map_fysl
								.get("LSJE")));	
					}
					}
				for(Map<String,Object> map_fysl:list_jcfyjl){
					if(MedicineUtils.compareMaps(map_yp, new String[]{"YPXH","YPCD"}, map_fysl, new String[]{"YPXH","YPCD"})){
						cksl = cksl
								+ MedicineUtils.parseDouble(df_two
										.format(MedicineUtils
												.parseDouble(map_fysl
														.get("YPSL"))
												* MedicineUtils
														.parseDouble(map_fysl
																.get("YFBZ"))
												/ MedicineUtils
														.parseDouble(map_yp
																.get("YFBZ"))));
						ckje=MedicineUtils.formatDouble(4, ckje+MedicineUtils.parseDouble(map_fysl
								.get("LSJE")));	
					}
					}
				for(Map<String,Object> map_rksl:list_dbrkjl){
					if(MedicineUtils.compareMaps(map_yp, new String[]{"YPXH","YPCD"}, map_rksl, new String[]{"YPXH","YPCD"})){
						rksl = rksl
								+ MedicineUtils.parseDouble(df_two
										.format(MedicineUtils
												.parseDouble(map_rksl
														.get("YPSL"))
												* MedicineUtils
														.parseDouble(map_rksl
																.get("YFBZ"))
												/ MedicineUtils
														.parseDouble(map_yp
																.get("YFBZ"))));
						rkje=MedicineUtils.formatDouble(4, rkje+MedicineUtils.parseDouble(map_rksl
								.get("LSJE")));	
					}
					}
				for(Map<String,Object> map_cksl:list_dbckjl){
					if(MedicineUtils.compareMaps(map_yp, new String[]{"YPXH","YPCD"}, map_cksl, new String[]{"YPXH","YPCD"})){
						cksl = cksl
								+ MedicineUtils.parseDouble(df_two
										.format(MedicineUtils
												.parseDouble(map_cksl
														.get("YPSL"))
												* MedicineUtils
														.parseDouble(map_cksl
																.get("YFBZ"))
												/ MedicineUtils
														.parseDouble(map_yp
																.get("YFBZ"))));
						ckje=MedicineUtils.formatDouble(4, ckje+MedicineUtils.parseDouble(map_cksl
								.get("LSJE")));	
					}
					}
				
				for(Map<String,Object> map_rksl:list_ypsljl){
					if(MedicineUtils.compareMaps(map_yp, new String[]{"YPXH","YPCD"}, map_rksl, new String[]{"YPXH","YPCD"})){
						rksl = rksl
								+ MedicineUtils.parseDouble(df_two
										.format(MedicineUtils
												.parseDouble(map_rksl
														.get("YPSL"))
												* MedicineUtils
														.parseDouble(map_rksl
																.get("ZXBZ"))
												/ MedicineUtils
														.parseDouble(map_yp
																.get("YFBZ"))));
						rkje=MedicineUtils.formatDouble(4, rkje+MedicineUtils.parseDouble(map_rksl
								.get("LSJE")));	
					}
					}
				qmsl=MedicineUtils.formatDouble(2, qcsl+rksl-cksl);
				qmje=MedicineUtils.formatDouble(4, qcje+rkje-ckje);
				if(qcsl==0&&rksl==0&&cksl==0&&qmsl==0){
					continue;
				}
				map_yp.put("QCSL", String.format("%1$.2f", qcsl));
				map_yp.put("QCJE", String.format("%1$.2f", qcje));
				map_yp.put("RKSL", String.format("%1$.2f", rksl));
				map_yp.put("RKJE", String.format("%1$.2f", rkje));
				map_yp.put("CKSL", String.format("%1$.2f", cksl));
				map_yp.put("CKJE", String.format("%1$.2f", ckje));
				map_yp.put("QMSL", String.format("%1$.2f", qmsl));
				map_yp.put("QMJE", String.format("%1$.2f", qmje));
				records.add(map_yp);
			}
		} catch (Exception e) {
			throw new PrintException(9000,"查询保管员账簿明细失败"+e.getMessage());
		}
	}

}
