package phis.prints.bean; 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 

import phis.application.pha.source.PharmacyCheckInOutManageModel;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context; 


public class DrugStoreMonthlyReportFile implements IHandler {
	//public Map<String,Object> map_ret=null;
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		Map<String, Object> body = getRecord(request, ctx);
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String userName = user.getUserName();// 用户名
		String jgName = user.getManageUnit().getName();// 用户的机构名称
		String jgid = user.getManageUnit().getId();// 用户的机构ID 
		long yfsb = parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
			SimpleDateFormat sfm = new SimpleDateFormat("yyyy.MM.dd");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			StringBuffer hql = new StringBuffer();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("YFSB", yfsb);
			parameters.put("JGID", jgid);
			hql.append("select a.YFMC as YFMC from YF_YFLB  a where a.YFSB=:YFSB and a.JGID=:JGID");
			Map<String, Object> yfmcMap;
			try {
				yfmcMap = dao.doLoad(hql.toString(), parameters);
				if(yfmcMap != null){
					response.put("title", jgName+yfmcMap.get("YFMC")+"月报");//标题				6
				}
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
			response.put("ybrq",  sdf.format((Date)body.get("begin"))+"--"+sdf.format((Date)body.get("end")));//月报日期
			response.put("rkhj",  String.format("%1$.2f",parseDouble(body.get("rkhj"))));//入库合计
			response.put("ckhj",  String.format("%1$.2f",parseDouble(body.get("ckhj"))));//出库合计
			response.put("syjc",  String.format("%1$.2f",parseDouble(body.get("syjc"))));//上月结存             
			response.put("byjc",  String.format("%1$.2f",parseDouble(body.get("byjc"))));//本月结存
			response.put("mzhjje",String.format("%1$.2f",parseDouble(body.get("mzhj"))));//门诊划价金额
			response.put("zbrq", sfm.format(new Date()));//制表日期
			response.put("zbr", userName);//制表人
	}
	@SuppressWarnings("unchecked")
	//getFields
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		Map<String, Object> body = getRecord(request, ctx);
		Map<String, Object> rkmcMap = (HashMap<String, Object>) body
				.get("rkmcMap");// 入库名称
		Map<String, Object> rkjeMap = (HashMap<String, Object>) body
				.get("rkjeMap");// 入库金额
		Map<String, Object> ckmcMap = (HashMap<String, Object>) body
				.get("ckmcMap");// 出库名称
		Map<String, Object> ckjeMap = (HashMap<String, Object>) body
				.get("ckjeMap");// 出库金额
		int rks = rkmcMap.size();
		int cks = ckmcMap.size();
		int max=0;
		if(rks>cks){
			max=rks;
		}else{
			max=cks;
		}
		for(int i=0;i<max;i++){
			String rkmc="";
			double rkje=0;
			String ckmc="";
			double ckje=0;
			if(rks>=i){
				rkmc=parseString(rkmcMap.get("rkmc" + i));
				rkje=parseDouble(rkjeMap.get("rkje" + i));
			}
			if(cks>=i){
				ckmc=parseString(ckmcMap.get("ckmc" + i));
				ckje=parseDouble(ckjeMap.get("ckje" + i));
			}
			Map<String, Object> crfs = new HashMap<String, Object>();
			crfs.put("rkmc", rkmc);
			crfs.put("rkje", rkje);
			crfs.put("ckmc", ckmc);
			crfs.put("ckje", ckje);
			records.add(crfs);
		}
	}
	public long parseLong(Object o) {
		if (o == null||"".equals(o)) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}
	public double parseDouble(Object o) {
		if (o == null||"".equals(o)) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}
	public String parseString(Object o) {
		if (o == null) {
			return new String("");
		}
		return o + "";
	}
	public double formatDouble(int number, double data) {
		return BSPHISUtil.getDouble(data,number);
	}
	
	public Map<String,Object> getRecord(Map<String,Object> body,Context ctx)throws PrintException{
//		if(map_ret!=null&&map_ret.size()>0){
//			return map_ret;
//		}
		Map<String,Object> map_ret=new HashMap<String,Object>();
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID 
		long yfsb = parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		try {
			PharmacyCheckInOutManageModel pmm = new PharmacyCheckInOutManageModel(dao);
			List<String> date = pmm.dateQuery(body,ctx);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd");
			if(date==null||date.size()<0){
				return map_ret;
			}
			Date idt_begin=sdf.parse(date.get(0));//开始时间
			Date idt_end=sdf.parse(date.get(1));//结束时间
			Long yplx = parseLong(body.get("yplx"));//药品类型
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("idt_begin", idt_begin);
			map_par.put("idt_end", idt_end);
			StringBuffer lyrkHql = new StringBuffer();//领药入库Sql
			lyrkHql.append("select sum(LSJE) as MONEY from YK_CK01 a,YK_CK02 b,YK_CKFS c,YK_TYPK d  where b.YPXH = d.YPXH  and a.XTSB = b.XTSB and a.CKFS = b.CKFS  and a.CKDH = b.CKDH and c.CKFS = a.CKFS and a.LYPB = 1 and a.LYRQ >:idt_begin  and a.LYRQ <=:idt_end and a.YFSB =:YFSB and c.DYFS <> 6");
			StringBuffer tyHql = new StringBuffer();//统计退药
			tyHql.append("select sum(LSJE) as MONEY from YK_CK01 a,YK_CK02 b,YK_CKFS c,YK_TYPK d where b.YPXH = d.YPXH  and a.XTSB = b.XTSB and a.CKFS = b.CKFS and  a.CKDH = b.CKDH and c.CKFS = a.CKFS and a.LYPB = 1 and a.LYRQ >:idt_begin and a.LYRQ <=:idt_end  and a.YFSB=:YFSB and c.DYFS = 6");
			StringBuffer rkfsHql = new StringBuffer();//查询入库方式
			rkfsHql.append("select a.RKFS as RKFS,a.FSMC as FSMC from YF_RKFS  a where a.JGID=:JGID and a.YFSB=:YFSB");
			StringBuffer rkfstjhql = new StringBuffer();//入库方式对应的金额
			rkfstjhql.append("select sum(LSJE) as MONEY from YF_RK01 a,YF_RK02 b,YK_TYPK c where   b.YPXH = c.YPXH  and a.YFSB = b.YFSB and a.RKFS = b.RKFS and a.RKDH = b.RKDH  and a.RKPB = 1 and a.RKFS=:RKFS and a.YFSB=:YFSB and a.RKRQ >:idt_begin and a.RKRQ <=:idt_end");
			StringBuffer ckfshql = new StringBuffer();//查询出库方式
			ckfshql.append("select CKFS as CKFS,FSMC as FSMC from YF_CKFS where JGID=:JGID and YFSB=:YFSB");
			StringBuffer ckfstjhql = new StringBuffer();//查询出库方式对应的金额
			ckfstjhql.append("select sum(LSJE) as MONEY from YF_CK01 a,YF_CK02 b,YK_TYPK c where  b.YPXH = c.YPXH  and a.YFSB = b.YFSB and a.CKFS = b.CKFS and a.CKDH = b.CKDH and  a.CKPB = 1 and a.CKFS=:CKFS and a.YFSB=:YFSB and a.YFSB=:YFSB and a.CKRQ >:idt_begin and a.CKRQ <=:idt_end ");
			StringBuffer dbrkhql = new StringBuffer();//统计调拨入库金额
			dbrkhql.append("select sum(LSJE) as MONEY from  YF_DB01 a,YF_DB02 b,YK_TYPK c where  b.YPXH = c.YPXH  and a.SQYF = b.SQYF and a.SQDH = b.SQDH  and a.RKBZ = 1 and a.SQYF=:YFSB and a.RKRQ >:idt_begin and a.RKRQ <=:idt_end");
			StringBuffer mzfytjhql = new StringBuffer();//统计门诊发药金额
			mzfytjhql.append("select sum(LSJE) as MONEY,sum(HJJE) as IDC_HJJE from YF_MZFYMX a,YK_TYPK b  where a.YFSB=:YFSB and a.YPXH = b.YPXH  and a.FYRQ >:idt_begin and a.FYRQ <=:idt_end");
			StringBuffer bqfytjhql = new StringBuffer();//统计病区发药金额
			bqfytjhql.append("select sum(LSJE) as MONEY from YF_ZYFYMX a,YK_TYPK b where a.YFSB=:YFSB  and a.YPXH = b.YPXH  and a.JFRQ >:idt_begin and a.JFRQ <=:idt_end");
			StringBuffer jcfytjhql = new StringBuffer();//统计家床发药金额
			jcfytjhql.append("select sum(LSJE) as MONEY from YF_JCFYMX a,YK_TYPK b where a.YFSB=:YFSB  and a.YPXH = b.YPXH  and a.JFRQ >:idt_begin and a.JFRQ <=:idt_end");
			StringBuffer dbckhql = new StringBuffer();//统计调拨出库金额
			dbckhql.append("select sum(LSJE) as MONEY from  YF_DB01 a,YF_DB02 b,YK_TYPK c  where b.YPXH = c.YPXH  and a.SQYF = b.SQYF and a.SQDH = b.SQDH  and a.CKBZ = 1 and a.MBYF=:YFSB and a.CKRQ >:idt_begin and a.CKRQ <=:idt_end");
			StringBuffer pytjhql = new StringBuffer();//统计盘盈金额
			pytjhql.append("select sum(XLSE - YLSE) as MONEY from YF_YK01 a,YF_YK02 b,YK_TYPK c  where a.PDDH = b.PDDH and a.CKBH = b.CKBH and  a.YFSB = b.YFSB and a.YFSB=:YFSB and a.PDWC = 1 and b.SPSL > b.PQSL  and a.WCRQ >:idt_begin and a.WCRQ <=:idt_end and b.YPXH = c.YPXH ");
			StringBuffer pktjhql = new StringBuffer();//统计盘亏金额
			pktjhql.append("select sum(XLSE - YLSE) as MONEY from YF_YK01 a,YF_YK02 b,YK_TYPK c where  a.PDDH = b.PDDH and a.CKBH = b.CKBH and  a.YFSB = b.YFSB and a.YFSB=:YFSB and a.PDWC = 1 and b.SPSL < b.PQSL and  a.WCRQ >:idt_begin and a.WCRQ <=:idt_end and b.YPXH = c.YPXH ");
			StringBuffer tjzztjhql = new StringBuffer();//统计调价增值
			tjzztjhql.append("select sum(XLSE - YLSE) as MONEY from YF_TJJL a,YK_TYPK b where b.YPXH = a.YPXH  and a.YFSB=:YFSB and a.TJRQ >:idt_begin and a.TJRQ <=:idt_end  and a.XLSJ > a.YLSJ ");
			StringBuffer tjjztjhql = new StringBuffer();//统计调价减值
			tjjztjhql.append("select sum(XLSE - YLSE) as MONEY from YF_TJJL a,YK_TYPK b where  b.YPXH = a.YPXH  and a.YFSB=:YFSB and a.TJRQ >:idt_begin and a.TJRQ <=:idt_end and a.XLSJ < a.YLSJ");
			StringBuffer byHql = new StringBuffer();//取本次月结的期初财务月份
			byHql.append("select CWYF as IDT_FIRSTCWYF,QSSJ as LDT_QSSJ from YF_JZJL  where ZZSJ=:idt_begin ");
			StringBuffer syjctjhql = new StringBuffer();//统计上月结存数据
			syjctjhql.append("select sum(LSJE) as MONEY from YF_YJJG a,YK_TYPK b where  a.YFSB=:YFSB and a.CWYF=:idt_firstcwyf and a.YPXH = b.YPXH ");
			StringBuffer hql_csyf=new StringBuffer();//如果没月结,直接查询
			hql_csyf.append("select sum(LSJE) as MONEY from YF_YJJG a,YK_TYPK b where  a.YFSB=:YFSB  and a.YPXH = b.YPXH ");
			if(yplx!=0){//如果有选择药品类型
				map_par.put("YPLX", yplx);
				lyrkHql.append(" and d.TYPE =:YPLX");
				tyHql.append(" and d.TYPE =:YPLX");
				rkfstjhql.append(" and c.TYPE=:YPLX");
				ckfstjhql.append(" and c.TYPE=:YPLX");
				dbrkhql.append(" and c.TYPE=:YPLX");
				mzfytjhql.append(" and b.TYPE=:YPLX");
				bqfytjhql.append(" and b.TYPE=:YPLX");
				jcfytjhql.append(" and b.TYPE=:YPLX");
				dbckhql.append(" and c.TYPE=:YPLX");
				pytjhql.append(" and c.TYPE=:YPLX");
				pktjhql.append(" and c.TYPE=:YPLX");
				tjzztjhql.append(" and b.TYPE=:YPLX");
				tjjztjhql.append(" and b.TYPE=:YPLX");
				syjctjhql.append(" and b.TYPE=:YPLX");
			}
			Map<String,Object> rkmcMap = new HashMap<String,Object>();//入库名称		
			Map<String,Object> rkjeMap = new HashMap<String,Object>();//入库金额		
			Map<String,Object> ckmcMap = new HashMap<String,Object>();//出库名称		
			Map<String,Object> ckjeMap = new HashMap<String,Object>();//出库金额
			map_par.put("YFSB", yfsb);
			List<Map<String,Object>> lyrkList = dao.doSqlQuery(lyrkHql.toString(), map_par);
			double lyrk=getMoney(lyrkList,"MONEY");//领药入库
			List<Map<String,Object>> tyList = dao.doSqlQuery(tyHql.toString(), map_par);
			double ty = getMoney(tyList,"MONEY");//退药
			double lyrkzj = lyrk+ty;//领药入库总金额
			rkmcMap.put("rkmc0", "领药入库*");//统计领药入库
			rkjeMap.put("rkje0", lyrkzj);
			Map<String,Object> map_par_rkfs=new HashMap<String,Object>();
			map_par_rkfs.put("JGID", jgid);
			map_par_rkfs.put("YFSB", yfsb);
			List<Map<String,Object>> rkfsList = dao.doQuery(rkfsHql.toString(), map_par_rkfs);
			double rkfsje = 0;//入库方式合计金额
			for (int i = 0; i < rkfsList.size(); i++) {//统计各种入库方式
				map_par.put("RKFS", parseLong(rkfsList.get(i).get("RKFS")));
				List<Map<String,Object>> rkfstjList = dao.doSqlQuery(rkfstjhql.toString(), map_par);
				double id_money = getMoney(rkfstjList,"MONEY");
				rkfsje += id_money;
				rkmcMap.put("rkmc"+(i+1), rkfsList.get(i).get("FSMC")+"");//入库方式名称
				rkjeMap.put("rkje"+(i+1), id_money);//入库方式对应的金额
			}
			map_par.remove("RKFS");
			List<Map<String,Object>> ckfsList = dao.doQuery(ckfshql.toString(), map_par_rkfs);
			double ckfsje = 0;//出库方式金额
			for (int i = 0; i < ckfsList.size(); i++) { //统计各种出库方式
				map_par.put("CKFS", parseLong(ckfsList.get(i).get("CKFS")));
				List<Map<String,Object>> ckfstjList = dao.doSqlQuery(ckfstjhql.toString(), map_par);
				double id_money = getMoney(ckfstjList,"MONEY");
				ckfsje += id_money;
				ckmcMap.put("ckmc"+i, ckfsList.get(i).get("FSMC")+"");//入库方式名称
				ckjeMap.put("ckje"+i, id_money);//入库方式对应的金额
			}
			map_par.remove("CKFS");
			List<Map<String,Object>> dbrkList = dao.doSqlQuery(dbrkhql.toString(), map_par);
			double dbrk = getMoney(dbrkList,"MONEY");//调拨入库金额
 			rkmcMap.put("rkmc"+(rkmcMap.size()), "调拨入库*");//统计调拨入库
			rkjeMap.put("rkje"+(rkjeMap.size()),dbrk);
 			List<Map<String,Object>> mzfytjList = dao.doSqlQuery(mzfytjhql.toString(),map_par);
			//List<Map<String,Object>> mzfytjList=null;
			double mzfy = getMoney(mzfytjList,"MONEY");//门诊发药金额
			double mzhj=getMoney(mzfytjList,"IDC_HJJE");//门诊划价金额
			ckmcMap.put("ckmc"+(ckmcMap.size()), "门诊发药");//统计门诊发药
			ckjeMap.put("ckje"+(ckjeMap.size()), mzfy);
			List<Map<String, Object>> bqfytjList = dao.doSqlQuery(bqfytjhql.toString(), map_par);
			double bqfy = getMoney(bqfytjList,"MONEY");//病区发药金额
			ckmcMap.put("ckmc"+(ckmcMap.size()), "病区发药");//统计病区发药
			ckjeMap.put("ckje"+(ckjeMap.size()), bqfy);
			List<Map<String, Object>> jcfytjList = dao.doSqlQuery(jcfytjhql.toString(), map_par);
			double jcfy = getMoney(jcfytjList,"MONEY");//家床发药金额
			ckmcMap.put("ckmc"+(ckmcMap.size()), "家床发药");//统计家床发药
			ckjeMap.put("ckje"+(ckjeMap.size()), jcfy);
			List<Map<String,Object>> dbckList = dao.doSqlQuery(dbckhql.toString(), map_par);
			double dbck = getMoney(dbckList,"MONEY");//调拨出库金额
			ckmcMap.put("ckmc"+(ckmcMap.size()), "调拨出库*");//统计调拨出库
			ckjeMap.put("ckje"+(ckjeMap.size()), dbck);
			List<Map<String,Object>> pytjList = dao.doSqlQuery(pytjhql.toString(), map_par);
			double yppy = getMoney(pytjList,"MONEY");//药品盘盈金额
			rkmcMap.put("rkmc"+(rkmcMap.size()), "药品盘盈*");//统计药品盘盈
			rkjeMap.put("rkje"+(rkjeMap.size()), yppy);
			List<Map<String,Object>> pktjList = dao.doSqlQuery(pktjhql.toString(), map_par);
			double yppk = getMoney(pktjList,"MONEY");//药品盘亏金额
			ckmcMap.put("ckmc"+(ckmcMap.size()), "药品盘亏*");//统计药品盘亏
			ckjeMap.put("ckje"+(ckjeMap.size()), 0-yppk);
			List<Map<String,Object>> tjzztjList = dao.doSqlQuery(tjzztjhql.toString(), map_par);
			double tjzz = getMoney(tjzztjList,"MONEY");//调价增值金额
			rkmcMap.put("rkmc"+(rkmcMap.size()), "调价增值*");//统计调价增值
			rkjeMap.put("rkje"+(rkjeMap.size()), tjzz);
			List<Map<String,Object>> tjjztjList = dao.doSqlQuery(tjjztjhql.toString(), map_par);
			double tjjz = getMoney(tjjztjList,"MONEY");//调价减值金额
			ckmcMap.put("ckmc"+(ckmcMap.size()), "调价减值*");//统计调价减值
			ckjeMap.put("ckje"+(ckjeMap.size()), 0-tjjz);
	 		Map<String,Object> map_par_sycwyf=new HashMap<String,Object>();
			//map_par_sycwyf.put("idt_begin", sdfd.parse(body.get("prior_begin")+""));
			map_par_sycwyf.put("idt_begin", idt_begin);
			Date idt_firstcwyf = null;
			List<Map<String,Object>> byList = dao.doQuery(byHql.toString(), map_par_sycwyf);
			if(byList!=null&&byList.size()!=0&&byList.get(0)!=null&&byList.get(0).size()!=0){
				 idt_firstcwyf = (Date)byList.get(0).get("IDT_FIRSTCWYF");
			}
			Map<String,Object> map_par_syjc=new HashMap<String,Object>();
			map_par_syjc.put("YFSB", yfsb);
			map_par_syjc.put("idt_firstcwyf", idt_firstcwyf);
			if(yplx!=0){
				map_par_syjc.put("YPLX", yplx);
				syjctjhql.append(" and b.TYPE=:YPLX");
				hql_csyf.append(" and b.TYPE=:YPLX");
			}
			double syjc=0;
			if(idt_firstcwyf!=null){
			List<Map<String,Object>> syjctjList = dao.doSqlQuery(syjctjhql.toString(), map_par_syjc);
			//List<Map<String,Object>> syjctjList=null;
			syjc = getMoney(syjctjList,"MONEY");;//上月结存金额
			}else{//如果没有财务月份(没有月结过)
				map_par_syjc.remove("idt_firstcwyf");
				List<Map<String,Object>> syjctjList = dao.doSqlQuery(hql_csyf.toString(), map_par_syjc);
				syjc = getMoney(syjctjList,"MONEY");;//上月结存金额
			}
			double rkhj = 0.00;//入库合计
			double ckhj = 0.00;//出库合计
			double byjc = 0.00;//本月结存
			rkhj = lyrkzj + rkfsje + dbrk + yppy + tjzz;
			ckhj = ckfsje + dbck + mzfy +bqfy+jcfy -yppk - tjjz;
			byjc = syjc + rkhj-ckhj;
			map_ret.put("rkmcMap", rkmcMap);//入库的名称
			map_ret.put("rkjeMap", rkjeMap);//入库的金额
			map_ret.put("ckmcMap", ckmcMap);//出库的名称
			map_ret.put("ckjeMap", ckjeMap);//出库的金额
			map_ret.put("rkhj", rkhj);//入库合计
			map_ret.put("ckhj", ckhj);//出库合计
			map_ret.put("byjc", byjc);//本月结存
			map_ret.put("syjc", syjc);//上月结存
			map_ret.put("begin", idt_begin);//开始时间
			map_ret.put("end", idt_end);//结束时间
			map_ret.put("mzhj", mzhj);//门诊划价金额
		} catch (ModelDataOperationException e) {
			throw new PrintException(9000,e.getMessage());
		} catch (ParseException e) {
			throw new PrintException(9000,e.getMessage());
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000,e.getMessage());
		} 
		return map_ret;
	}
	
	public double getMoney(List<Map<String,Object>> list,String par){
		if(list!=null&&list.size()!=0&&list.get(0)!=null&&list.get(0).size()!=0){
			return parseDouble(list.get(0).get(par));
		}
		return 0;
	}
	
	}