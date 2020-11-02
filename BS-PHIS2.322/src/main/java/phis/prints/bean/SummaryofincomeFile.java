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
import ctd.account.user.User;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class SummaryofincomeFile implements IHandler {
	List<Map<String, Object>> li = new ArrayList<Map<String, Object>>();
	SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		records.addAll(li);
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		li.clear();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();
		String manaUnitId = user.getManageUnit().getId();
		response.put("title", jgname);
		Map<String, Object> parameters = new HashMap<String, Object>();
		Date adt_hzrq_start = null;
		Date adt_hzrq_end = null;
		try {
			if (request.get("beginDate") != null) {
				adt_hzrq_start = sdfdate.parse(request.get("beginDate") + "");
			}
			if (request.get("endDate") != null) {
				adt_hzrq_end = sdfdate.parse(request.get("endDate") + "");
			}
			String[] lS_ksrq = (request.get("beginDate") + "").split("-| ");
			String ksrq = lS_ksrq[0] + "年" + lS_ksrq[1] + "月" + lS_ksrq[2]
					+ "日";
			String[] lS_jsrq = (request.get("endDate") + "").split("-| ");
			String jsrq = lS_jsrq[0] + "年" + lS_jsrq[1] + "月" + lS_jsrq[2]
					+ "日";
			response.put("HZRQ", "汇总日期:" + ksrq + " 至 " + jsrq);
			parameters.put("adt_hzrq_b", adt_hzrq_start);
			parameters.put("adt_hzrq_e", adt_hzrq_end);
			parameters.put("al_jgid", manaUnitId);
			li = dao.doQuery(
					"SELECT ZY_JZHZ.XMBH as XMBH,ZY_JZHZ.SQJC as SQJC,ZY_JZHZ.QFJE as QFJE,ZY_JZHZ.BQFS as BQFS,ZY_JZHZ.BQJS as BQJS,ZY_JZHZ.XJZP as XJZP,ZY_JZHZ.CBJE as CBJE,ZY_JZHZ.QTJE as QTJE,ZY_JZHZ.BQYE as BQYE FROM ZY_JZHZ ZY_JZHZ WHERE ZY_JZHZ.HZRQ>=:adt_hzrq_b and ZY_JZHZ.HZRQ<=:adt_hzrq_e and ZY_JZHZ.JGID=:al_jgid order by ZY_JZHZ.XMBH asc,ZY_JZHZ.HZRQ desc",
					parameters);
			
			for (int i = 0; i < li.size(); i++) {
//				String qtysFb="";
				if (Integer.parseInt(li.get(i).get("XMBH") + "") == 1) {
					li.get(i).put("XMBH", "在院病人结算");
					li.get(i).put("CBJE",
							String.format("%1$.2f", li.get(i).get("CBJE")));
					li.get(i).put("QTJE",
							String.format("%1$.2f", li.get(i).get("QTJE")));
					li.get(i).put("QFJE","--");
					li.get(i).put("BQFS",String.format("%1$.2f", li.get(i).get("BQFS")));
					String ids_zyjs_hql = "select d.FKFS as FKFS,sum(d.FKJE) as FKJE,e.FKMC as FKMC from ("+
							"select a.FKFS as FKFS, (-1*a.FKJE) as FKJE from ZY_FKXX a, ZY_JSZF b where a.ZYH = b.ZYH and a.JSCS = b.JSCS and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid"+
							" union all "+
							"select a.FKFS as FKFS, a.FKJE as FKJE from ZY_FKXX a, ZY_ZYJS b where b.JSLX<>4 and a.ZYH = b.ZYH and a.JSCS = b.JSCS and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid"+
							" union all "+
							"select a.JKFS as FKFS, a.JKJE as FKJE from ZY_TBKK a, ZY_ZYJS b where b.JSLX<>4 and a.ZYH = b.ZYH and a.JSCS = b.JSCS and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid"+
							" union all "+
							"select a.JKFS as FKFS, (-1*a.JKJE) as FKJE from ZY_TBKK a, ZY_JKZF b,ZY_ZYJS c where b.JKXH = a.JKXH and c.JSLX<>4 and a.ZYH = c.ZYH and a.JSCS = c.JSCS and c.HZRQ>=:adt_hzrq_b AND c.HZRQ<=:adt_hzrq_e AND c.JGID=:al_jgid"+
							") d left outer join GY_FKFS e on d.FKFS = e.FKFS group by d.FKFS,e.FKMC";
					String ids_brxz_hql = "select sum(c.FYHJ) as FYHJ,sum(c.ZFHJ) as ZFHJ,c.BRXZ as BRXZ,d.XZMC as XZMC,d.DBPB as DBPB from ("+
							"SELECT a.FYHJ as FYHJ,a.ZFHJ as ZFHJ,a.BRXZ as BRXZ FROM ZY_ZYJS a WHERE a.JSLX<>4 and a.FYHJ<>a.ZFHJ and a.HZRQ>=:adt_hzrq_b AND a.HZRQ<=:adt_hzrq_e AND a.JGID=:al_jgid"+
							" union all "+
							"SELECT (-1*a.FYHJ) as FYHJ,(-1*a.ZFHJ) as ZFHJ,a.BRXZ as BRXZ FROM ZY_ZYJS a ,ZY_JSZF b WHERE a.JSLX<>4 and a.ZYH = b.ZYH AND a.JSCS = b.JSCS and a.FYHJ<>a.ZFHJ and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid"+
							") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB";
					List<Map<String, Object>> ids_zyjs = dao.doSqlQuery(ids_zyjs_hql,parameters);
					List<Map<String, Object>> ids_brxz = dao.doSqlQuery(ids_brxz_hql,parameters);
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
//					System.out.println("@@@"+qtysFb);
					li.get(i).put("qtysFb",qtysFb);
				} else if(Integer.parseInt(li.get(i).get("XMBH") + "") == 2){
					li.get(i).put("XMBH", "预缴金");
					li.get(i).put("CBJE", "-");
					li.get(i).put("QTJE", "-");
					li.get(i).put("QFJE", li.get(i).get("QFJE"));
					li.get(i).put("BQFS",String.format("%1$.2f", li.get(i).get("BQFS")));
					String ids_yjk_hql = "select d.FKFS as FKFS,sum(d.FKJE) as FKJE,e.FKMC as FKMC from ("+
//							"SELECT a.JKFS as FKFS,a.JKJE as FKJE FROM ZY_TBKK a WHERE a.HZRQ>=:adt_hzrq_b AND a.HZRQ<=:adt_hzrq_e AND a.JGID=:al_jgid"+
//							" union all "+
//							"SELECT a.JKFS as FKFS,(-1*a.JKJE) as FKJE FROM ZY_TBKK a ,ZY_JKZF b WHERE b.JKXH = a.JKXH and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid"+
//							" union all "+
							"select a.JKFS as FKFS, a.JKJE as FKJE from ZY_TBKK a, ZY_ZYJS b where b.JSLX<>4 and a.ZYH = b.ZYH and a.JSCS = b.JSCS and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid "+//and (a.HZRQ<:adt_hzrq_b or a.HZRQ>:adt_hzrq_e or a.HZRQ is null)
							" union all "+
							"SELECT a.JKFS as FKFS,(-1*a.JKJE) as FKJE FROM ZY_TBKK a ,ZY_JKZF b,ZY_ZYJS c WHERE c.JSLX<>4 and a.ZYH = c.ZYH and a.JSCS = c.JSCS AND b.JKXH = a.JKXH and c.HZRQ>=:adt_hzrq_b AND c.HZRQ<=:adt_hzrq_e AND c.JGID=:al_jgid "+// and (b.HZRQ<:adt_hzrq_b or b.HZRQ>:adt_hzrq_e or b.HZRQ is null)
							") d left outer join GY_FKFS e on d.FKFS = e.FKFS group by d.FKFS,e.FKMC";
					List<Map<String, Object>> ids_yjk = dao.doSqlQuery(ids_yjk_hql,parameters);
					String  qtysFb="";
					if (ids_yjk  != null && ids_yjk .size() != 0) {
						 for(int j=0;j<ids_yjk.size();j++){
								 qtysFb = qtysFb +ids_yjk.get(j).get("FKMC")+ ":"
										+ String.format("%1$.2f",ids_yjk.get(j).get("FKJE"))
										+ " ";
						 }
					}
					li.get(i).put("qtysFb",qtysFb);
				}else{
					li.get(i).put("XMBH", "出院终结");
					li.get(i).put("CBJE", "-");
					li.get(i).put("QTJE", "-");
					li.get(i).put("QFJE", li.get(i).get("QFJE"));
					li.get(i).put("BQFS","--");
					String ids_zyjs_hql = "select d.FKFS as FKFS,sum(d.FKJE) as FKJE,e.FKMC as FKMC from ("+
							"select a.FKFS as FKFS, a.FKJE as FKJE from ZY_FKXX a, ZY_ZYJS b where b.JSLX=4 and a.ZYH = b.ZYH and a.JSCS = b.JSCS and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid"+
							" union all "+
							"select a.JKFS as FKFS, a.JKJE as FKJE from ZY_TBKK a, ZY_ZYJS b where b.JSLX=4 and a.ZYH = b.ZYH and a.JSCS = b.JSCS and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid"+
							") d left outer join GY_FKFS e on d.FKFS = e.FKFS group by d.FKFS,e.FKMC";
					String ids_brxz_hql = "select sum(c.FYHJ) as FYHJ,sum(c.ZFHJ) as ZFHJ,c.BRXZ as BRXZ,d.XZMC as XZMC,d.DBPB as DBPB from ("+
							"SELECT a.FYHJ as FYHJ,a.ZFHJ as ZFHJ,a.BRXZ as BRXZ FROM ZY_ZYJS a WHERE a.JSLX=4 and a.FYHJ<>a.ZFHJ and a.HZRQ>=:adt_hzrq_b AND a.HZRQ<=:adt_hzrq_e AND a.JGID=:al_jgid"+
							") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB";
					List<Map<String, Object>> ids_zyjs = dao.doSqlQuery(ids_zyjs_hql,parameters);
					List<Map<String, Object>> ids_brxz = dao.doSqlQuery(ids_brxz_hql,parameters);
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
					li.get(i).put("qtysFb",qtysFb);
				}
				li.get(i).put("SQJC",
						String.format("%1$.2f", li.get(i).get("SQJC")));
				
				li.get(i).put("BQJS",
						String.format("%1$.2f", li.get(i).get("BQJS")));
				li.get(i).put("XJZP",
						String.format("%1$.2f", li.get(i).get("XJZP")));
				li.get(i).put("BQYE",
						String.format("%1$.2f", li.get(i).get("BQYE")));
//				if(){
//					String ids_fkfs_hql = "select d.FKFS as FKFS,sum(d.FKJE) as FKJE,e.FKMC as FKMC from ("+
//							"select a.FKFS as FKFS, a.FKJE as FKJE from ZY_FKXX a, ZY_JSZF b,ZY_JZXX c where a.ZYH = b.ZYH and a.JSCS = b.JSCS AND b.JZRQ = c.JZRQ and b.JGID = c.JGID and c.HZRQ>=:adt_hzrq_b AND c.HZRQ<=:adt_hzrq_e AND c.JGID=:al_jgid"+
//							" union all "+
//							"select a.FKFS as FKFS, a.FKJE as FKJE from ZY_FKXX a, ZY_ZYJS b,ZY_JZXX c where b.JSLX<>4 and a.ZYH = b.ZYH and a.JSCS = b.JSCS AND b.JZRQ = c.JZRQ and b.JGID = c.JGID and c.HZRQ>=:adt_hzrq_b AND c.HZRQ<=:adt_hzrq_e AND c.JGID=:al_jgid"+
//							" union all "+
//							"SELECT a.JKFS as FKFS,a.JKJE as FKJE FROM ZY_TBKK a,ZY_JZXX b WHERE a.JZRQ = b.JZRQ and b.JGID = a.JGID and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid"+
//							" union all "+
//							"SELECT (-1*a.JKJE) as FKJE ,a.JKFS as FKFS FROM ZY_TBKK a ,ZY_JKZF b,ZY_JZXX c WHERE b.JKXH = a.JKXH AND b.JZRQ = c.JZRQ and b.JGID = c.JGID and c.HZRQ>=:adt_hzrq_b AND c.HZRQ<=:adt_hzrq_e AND c.JGID=:al_jgid"+
//							") d left outer join GY_FKFS e on d.FKFS = e.FKFS group by d.FKFS,e.FKMC";
//					String ids_brxz_hql = "select sum(c.FYHJ) as FYHJ,sum(c.ZFHJ) as ZFHJ,c.BRXZ as BRXZ,d.XZMC as XZMC,d.DBPB as DBPB from ("+
//					"SELECT a.FYHJ as FYHJ,a.ZFHJ as ZFHJ,a.BRXZ as BRXZ FROM ZY_ZYJS a,ZY_JZXX b WHERE a.JSLX<>4 and a.FYHJ<>a.ZFHJ and a.JZRQ = b.JZRQ and b.JGID = a.JGID and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid"+
//					" union all "+
//					"SELECT (-1*a.FYHJ) as FYHJ,(-1*a.ZFHJ) as ZFHJ,a.BRXZ as BRXZ FROM ZY_ZYJS a ,ZY_JSZF b,ZY_JZXX c WHERE a.JSLX<>4 and a.ZYH = b.ZYH AND a.JSCS = b.JSCS and a.FYHJ<>a.ZFHJ and b.JZRQ = c.JZRQ and b.JGID = c.JGID and c.HZRQ>=:adt_hzrq_b AND c.HZRQ<=:adt_hzrq_e AND c.JGID=:al_jgid"+
//					") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB";
//				}
//				qtysFb=" "+"结算支付:"+ li.get(i).get("XJZP")+" ";
//				qtysFb=qtysFb+"欠费:"+li.get(i).get("QFJE");
//				qtysFb=qtysFb+"参保:"+ li.get(i).get("CBJE")+" ";
//				qtysFb=qtysFb+"记账:"+li.get(i).get("QTJE")+" ";
//				li.get(i).put("qtysFb",qtysFb);
			}
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
