package phis.prints.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class FsbSummarycategoryHospitalForFile implements IHandler {
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		UserRoleToken user = UserRoleToken.getCurrent();
		String Gl_jgid = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_jgid", Gl_jgid);
		try {
			Date stardate = sdfdate.parse(request.get("beginDate") + "");
			Date enddate = sdfdate.parse(request.get("endDate") + "");
			parameters.put("adt_hzrq", stardate);
			parameters.put("adt_hzrq_end", enddate);
			List<Map<String, Object>> xmflAll = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> xmflList = dao
					.doSqlQuery(
							"select s.ZYGB as ZYGB,s.SFMC as SFMC,sum(s.ZJJE) as ZJJE "
									+ "from (SELECT b.ZYGB as ZYGB,b.SFMC as SFMC,c.ZJJE as ZJJE FROM JC_JCJS a,GY_SFXM b,JC_FYMX_js c "
									+ "WHERE ( c.FYXM = b.SFXM ) AND ( a.ZYH = c.ZYH ) AND ( a.JSCS = c.JSCS ) AND ( a.JGID = c.JGID ) "
									+ "AND ( a.HZRQ >=:adt_hzrq ) and ( a.HZRQ <=:adt_hzrq_end ) AND a.JGID =:al_jgid "
									+ "union all "
									+ "select b.ZYGB as ZYGB,b.SFMC as SFMC,-(c.ZJJE) as ZJJE FROM JC_JCJS a,GY_SFXM b,JC_FYMX_js c,JC_JSZF d "
									+ "WHERE ( c.FYXM = b.SFXM ) AND ( a.ZYH = c.ZYH ) AND ( a.JSCS = c.JSCS ) AND ( a.JGID = c.JGID ) "
									+ "AND ( a.ZYH = d.ZYH ) AND ( a.JSCS = d.JSCS ) AND ( a.JGID = d.JGID ) AND ( d.hzrq >=:adt_hzrq )  "
									+ "and ( d.hzrq <=:adt_hzrq_end ) AND a.JGID =:al_jgid) s "
									+ "group by s.ZYGB,s.SFMC", parameters);
			// 追加西药基药/中药基药
			List<Map<String, Object>> xmflList2 = dao
					.doSqlQuery(
							"select s.ZYGB as ZYGB,(case when s.ZYGB = 2 then '西药基药' when s.ZYGB = 1 then '中成药基药' end) as SFMC,sum(s.ZJJE) as ZJJE "
									+ "from (SELECT b.ZYGB as ZYGB,b.SFMC as SFMC,c.ZJJE as ZJJE FROM JC_JCJS a,GY_SFXM b,JC_FYMX_js c ,YK_TYPK d "
									+ "WHERE ( c.FYXM = b.SFXM ) AND ( a.ZYH = c.ZYH ) AND ( a.JSCS = c.JSCS ) AND ( a.JGID = c.JGID ) AND c.YPLX IN(1,2)"
									+ // 关联药库表
									"AND ( c.FYXH = d.YPXH) AND d.JYLX > 1 "
									+ "AND ( a.HZRQ >=:adt_hzrq ) and ( a.HZRQ <=:adt_hzrq_end ) AND a.JGID =:al_jgid "
									+ "union all "
									+ "select b.ZYGB as ZYGB,b.SFMC as SFMC,-(c.ZJJE) as ZJJE "
									+ "FROM JC_JCJS a,GY_SFXM b,JC_FYMX_js c,JC_JSZF d,YK_TYPK f "
									+ "WHERE ( c.FYXM = b.SFXM ) AND ( a.ZYH = c.ZYH ) AND ( a.JSCS = c.JSCS ) AND ( a.JGID = c.JGID ) "
									+ "AND ( a.ZYH = d.ZYH ) AND ( a.JSCS = d.JSCS ) AND ( a.JGID = d.JGID ) AND ( c.FYXH = f.YPXH) "
									+ "AND c.YPLX IN(1,2) AND f.JYLX > 1 "
									+ "AND ( d.hzrq >=:adt_hzrq ) and ( d.hzrq <=:adt_hzrq_end ) AND a.JGID =:al_jgid) s "
									+ "group by s.ZYGB,s.SFMC", parameters);
			xmflAll.addAll(xmflList);
			xmflAll.addAll(xmflList2);
			for (int i = 0; i < xmflAll.size(); i = i + 3) {
				Map<String, Object> cf = new HashMap<String, Object>();
				cf.put("XM1", xmflAll.get(i).get("SFMC"));
				cf.put("JE1",
						String.format("%1$.2f", xmflAll.get(i).get("ZJJE")));
				if (i + 1 < xmflAll.size()) {
					cf.put("XM2", xmflAll.get(i + 1).get("SFMC"));
					cf.put("JE2",
							String.format("%1$.2f",
									xmflAll.get(i + 1).get("ZJJE")));
				}
				if (i + 2 < xmflAll.size()) {
					cf.put("XM3", xmflAll.get(i + 2).get("SFMC"));
					cf.put("JE3",
							String.format("%1$.2f",
									xmflAll.get(i + 2).get("ZJJE")));
				}
				records.add(cf);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		UserRoleToken user = UserRoleToken.getCurrent();
		String Gl_jgid = user.getManageUnit().getId();
		String jgname = user.getManageUnit().getName();
		String czy = user.getUserName();
		response.put("title", jgname);
		response.put("CZY", czy);
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("al_jgid", Gl_jgid);
		try {
			double zjje = 0.00;
			double tczf = 0.00;
			double bczhzf = 0.00;
			double srje = 0.00;
			Date stardate = sdfdate.parse(request.get("beginDate") + "");
			Date enddate = sdfdate.parse(request.get("endDate") + "");
			String[] lS_ksrq = (request.get("beginDate") + "").split("-| ");
			String ksrq = lS_ksrq[0] + "年" + lS_ksrq[1] + "月" + lS_ksrq[2]
					+ "日";
			String[] lS_jsrq = (request.get("endDate") + "").split("-| ");
			String jsrq = lS_jsrq[0] + "年" + lS_jsrq[1] + "月" + lS_jsrq[2]
					+ "日";
			response.put("HZRQ", ksrq + " 至 " + jsrq);
			parameter.put("adt_hzrq", stardate);
			parameter.put("adt_hzrq_end", new Date(enddate.getTime()+(1000*60*60*24)));
			List<Map<String, Object>> xmflList = dao
					.doSqlQuery(
							"select sum(s.ZJJE) as ZJJE from (SELECT c.ZJJE as ZJJE FROM JC_JCJS a,GY_SFXM b,JC_FYMX_js c WHERE ( c.FYXM = b.SFXM ) AND ( a.ZYH = c.ZYH ) AND ( a.JSCS = c.JSCS ) AND ( a.JGID = c.JGID ) AND ( a.HZRQ >=:adt_hzrq ) and ( a.HZRQ <=:adt_hzrq_end ) AND a.JGID =:al_jgid union all select -(c.ZJJE) as ZJJE FROM JC_JCJS a,GY_SFXM b,JC_FYMX_js c,JC_JSZF d WHERE ( c.FYXM = b.SFXM ) AND ( a.ZYH = c.ZYH ) AND ( a.JSCS = c.JSCS ) AND ( a.JGID = c.JGID ) AND ( a.ZYH = d.ZYH ) AND ( a.JSCS = d.JSCS ) AND ( a.JGID = d.JGID ) AND ( d.hzrq >=:adt_hzrq )  and ( d.hzrq <:adt_hzrq_end ) AND a.JGID =:al_jgid) s",
							parameter);
			if (xmflList != null && xmflList.size() > 0) {
				if (xmflList.get(0).get("ZJJE") != null) {
					response.put("HJJE", String.format("%1$.2f", xmflList
							.get(0).get("ZJJE")));
				} else {
					response.put("HJJE", String.format("%1$.2f", 0.00));
				}
				if (xmflList.get(0).get("ZJJE") != null) {
					zjje = Double.parseDouble(xmflList.get(0).get("ZJJE") + "");
				}
			}

			Map<String, Object> xmflMap2 = dao
					.doLoad("SELECT sum(TCZC) as TCZF,sum(SRJE) AS SRJE, sum(BCZHZF) as ZHZF FROM JC_JZXX WHERE ( HZRQ >= :adt_hzrq ) and ( HZRQ < :adt_hzrq_end ) AND JGID = :al_jgid",
							parameter);
			if (xmflMap2 != null && xmflMap2.size() > 0) {
				if (xmflMap2.get("TCZF") != null) {
					tczf = Double.parseDouble(xmflMap2.get("TCZF") + "");
				}
				if (xmflMap2.get("ZHZF") != null) {
					bczhzf = Double.parseDouble(xmflMap2.get("ZHZF") + "");
				}
				if (xmflMap2.get("SRJE") != null) {
					srje = Double.parseDouble(xmflMap2.get("SRJE") + "");
				}
			}
			Map<String, Object> xmflMap3 = dao
					.doLoad("SELECT COUNT(BRID) AS ZJJE FROM JC_BRRY where (ryrq >=:adt_hzrq ) and (ryrq <:adt_hzrq_end ) AND JGID =:al_jgid ",
							parameter);
			// 出院人数
			Map<String, Object> xmflMap4 = dao
					.doLoad("SELECT COUNT(BRID) AS ZJJE FROM JC_BRRY where CYPB=8 and (cyrq >=:adt_hzrq ) and (cyrq <:adt_hzrq_end ) AND JGID =:al_jgid",
							parameter);
			//其中
//			String qtysFb="";
//			List<Map<String, Object>> qtflList = dao
//					.doSqlQuery(
//							"select t.ZJJE,g.XZMC from (select sum(s.ZJJE) as ZJJE,BRXZ   from (  SELECT sum(c.ZJJE) as ZJJE,a.BRXZ as BRXZ FROM JC_JCJS a, GY_SFXM b, JC_FYMX_js c,gy_brxz d WHERE ( c.FYXM = b.SFXM ) AND ( a.ZYH = c.ZYH ) AND ( a.JSCS = c.JSCS ) AND ( a.JGID = c.JGID ) AND ( a.HZRQ >=:adt_hzrq ) and ( a.HZRQ <=:adt_hzrq_end ) AND a.JGID =:al_jgid  AND a.brxz=d.brxz  group by a.brxz " +
//							" union all   " +
//							"select sum(- (c.ZJJE)) as ZJJE,a.BRXZ as BRXZ   FROM JC_JCJS a, GY_SFXM b, JC_FYMX_js c, JC_JSZF d,gy_brxz f  WHERE ( c.FYXM = b.SFXM ) AND ( a.ZYH = c.ZYH ) AND ( a.JSCS = c.JSCS ) AND ( a.JGID = c.JGID ) AND ( a.ZYH = d.ZYH ) AND ( a.JSCS = d.JSCS ) AND ( a.JGID = d.JGID ) AND ( d.hzrq >=:adt_hzrq )  and ( d.hzrq <:adt_hzrq_end ) AND a.JGID =:al_jgid    AND a.brxz=f.brxz  group by a.brxz  ) s group by BRXZ ) t left join gy_brxz g on t.BRXZ=g.BRXZ ",
//							parameter);
//			if (qtflList != null && qtflList.size() != 0 && qtflList.get(0).get("ZJJE")!=null) {
//				 for(int i=0;i<qtflList.size();i++){
//					 qtysFb = qtysFb +qtflList.get(i).get("XZMC")+ ":"
//								+ String.format("%1$.2f",qtflList.get(i).get("ZJJE"))
//								+ " ";
//				 }
//			}else{
//				qtysFb="0";
//			}
//			response.put("qtysFb", qtysFb);// 其中:
			String ids_zyjs_hql = "select d.FKFS as FKFS,sum(d.FKJE) as FKJE,e.FKMC as FKMC from ("+
					"select a.FKFS as FKFS, (-1*a.FKJE) as FKJE from JC_FKXX a, JC_JSZF b where a.ZYH = b.ZYH and a.JSCS = b.JSCS and b.HZRQ>=:adt_hzrq AND b.HZRQ<=:adt_hzrq_end AND b.JGID=:al_jgid"+
					" union all "+
					"select a.FKFS as FKFS, a.FKJE as FKJE from JC_FKXX a, JC_JCJS b where a.ZYH = b.ZYH and a.JSCS = b.JSCS and b.HZRQ>=:adt_hzrq AND b.HZRQ<=:adt_hzrq_end AND b.JGID=:al_jgid"+
					" union all "+
					"select a.JKFS as FKFS, a.JKJE as FKJE from JC_TBKK a, JC_JCJS b where a.ZYH = b.ZYH and a.JSCS = b.JSCS and b.HZRQ>=:adt_hzrq AND b.HZRQ<=:adt_hzrq_end AND b.JGID=:al_jgid"+
					" union all "+
					"select a.JKFS as FKFS, (-1*a.JKJE) as FKJE from JC_TBKK a, JC_JKZF b,JC_JCJS c where b.JKXH = a.JKXH and a.ZYH = c.ZYH and a.JSCS = c.JSCS and c.HZRQ>=:adt_hzrq AND c.HZRQ<=:adt_hzrq_end AND c.JGID=:al_jgid"+
					") d left outer join GY_FKFS e on d.FKFS = e.FKFS group by d.FKFS,e.FKMC";
			String ids_brxz_hql = "select sum(c.FYHJ) as FYHJ,sum(c.ZFHJ) as ZFHJ,c.BRXZ as BRXZ,d.XZMC as XZMC,d.DBPB as DBPB from ("+
					"SELECT a.FYHJ as FYHJ,a.ZFHJ as ZFHJ,a.BRXZ as BRXZ FROM JC_JCJS a WHERE a.FYHJ<>a.ZFHJ and a.HZRQ>=:adt_hzrq AND a.HZRQ<=:adt_hzrq_end AND a.JGID=:al_jgid"+
					" union all "+
					"SELECT (-1*a.FYHJ) as FYHJ,(-1*a.ZFHJ) as ZFHJ,a.BRXZ as BRXZ FROM JC_JCJS a ,JC_JSZF b WHERE a.ZYH = b.ZYH AND a.JSCS = b.JSCS and a.FYHJ<>a.ZFHJ and b.HZRQ>=:adt_hzrq AND b.HZRQ<=:adt_hzrq_end AND b.JGID=:al_jgid"+
					") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB";
			List<Map<String, Object>> ids_zyjs = dao.doSqlQuery(ids_zyjs_hql,parameter);
			List<Map<String, Object>> ids_brxz = dao.doSqlQuery(ids_brxz_hql,parameter);
			String  qtysFb="";
			String jzjeSt="0.00";
			if (ids_zyjs  != null && ids_zyjs .size() != 0) {
				 for(int j=0;j<ids_zyjs.size();j++){
						 qtysFb = qtysFb +ids_zyjs.get(j).get("FKMC")+ ":"
								+ String.format("%1$.2f",ids_zyjs.get(j).get("FKJE"))
								+ " ";
				 }
			}
			if (ids_brxz  != null && ids_brxz .size() != 0) {
				 for(int j=0;j<ids_brxz.size();j++){
					 if(Integer.parseInt(ids_brxz.get(j).get("DBPB")+"")==0){
						 jzjeSt= String.format("%1$.2f",parseDouble(jzjeSt) +(parseDouble(ids_brxz.get(j).get("FYHJ")+ "")-parseDouble(ids_brxz.get(j).get("ZFHJ")+ "")));
					 }else{
						 qtysFb = qtysFb +ids_brxz.get(j).get("XZMC")+ ":"
								+ String.format("%1$.2f",(parseDouble(ids_brxz.get(j).get("FYHJ")+ "")-parseDouble(ids_brxz.get(j).get("ZFHJ")+ "")))
								+ " ";
					 }
				 }
				 qtysFb = qtysFb+" "+"记账 :"+jzjeSt+" ";
			}
//			System.out.println("@@@"+qtysFb);
			response.put("qtysFb",qtysFb);
			response.put("CYRS", String.valueOf(xmflMap4.get("ZJJE")));// 出院人数:
			response.put("RYRS", String.valueOf(xmflMap3.get("ZJJE")));// 入院人数:
			response.put("XJJE", String.format("%1$.2f", zjje - tczf - bczhzf));// 现金金额:
			response.put("TCZF", String.format("%1$.2f", tczf));// 统筹支付:
			response.put("ZHZF", String.format("%1$.2f", bczhzf));// 账户支付:
			response.put("JZJE", String.format("%1$.2f", 0.00));// 记账金额:
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-21
	 * @description 数据转换成double
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public double parseDouble(Object o) {
		if (o == null || "null".equals(o)) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}
}
