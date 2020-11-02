package phis.prints.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class HospitalChargesSummary extends AbstractActionService implements
		DAOSupportable {
	private static HospitalChargesSummary cck = null;
	Date idt_hzrq = null;
	List<Map<String, Object>> li = new ArrayList<Map<String, Object>>();
	SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat sdfdatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private HospitalChargesSummary() {
	}

	public void doGetFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		for (int i = 0; i < li.size(); i++) {
			li.get(i).put(
					"CZGH",
					DictionaryController.instance().getDic("phis.dictionary.doctor")
							.getText(li.get(i).get("CZGH").toString()));
		}
		records.addAll(li);
	}

	public void doGetParameters(Map<String, Object> request,
			Map<String, Object> response, BaseDAO dao, Context ctx)
			throws PrintException {
		li.clear();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();
		String manaUnitId = user.getManageUnit().getId();
		response.put("title", jgname);
		Date id_dqrq = null;
		Date ldt_CurrentDateTime = null;
//		String QTYSFB="";
		Map<String, Object> idt_LastDate = new HashMap<String, Object>();
		try {
			if (request.get("summaryDate") != null) {
				ldt_CurrentDateTime = sdfdate.parse(sdfdate.format(new Date()));
				id_dqrq = sdfdate.parse(request.get("summaryDate") + "");
				if (id_dqrq.getTime() == ldt_CurrentDateTime.getTime()) {
					idt_hzrq = sdfdatetime
							.parse(sdfdatetime.format(new Date()));
				} else {
					idt_hzrq = sdfdatetime.parse(sdfdate.format(id_dqrq)
							+ " 23:59:59");
				}
			}
			int hzsign = BSPHISUtil.wf_IsGather(idt_hzrq, idt_LastDate, dao,
					ctx);
			if (hzsign == 1) {
				String[] lS_JZRQ = sdfdate.format(idt_hzrq).split("-| |:");
				String JZRQ = lS_JZRQ[0] + "年" + lS_JZRQ[1] + "月" + lS_JZRQ[2]
						+ "日(" + lS_JZRQ[3] + ":" + lS_JZRQ[4] + ")";
				response.put("HZRQ", "汇总日期:" + JZRQ);
			} else {
				String[] lS_JZRQ = sdfdate.format(idt_hzrq).split("-| ");
				String JZRQ = lS_JZRQ[0] + "年" + lS_JZRQ[1] + "月" + lS_JZRQ[2]
						+ "日";
				response.put("HZRQ", "汇总日期:" + JZRQ);
			}

			Map<String, Object> parametershzbd = new HashMap<String, Object>();
			parametershzbd.put("adt_jzrq",
					sdfdatetime.parse(sdfdatetime.format(idt_hzrq)));
			parametershzbd.put("al_jgid", manaUnitId);
			// 日结汇总汇总表单
			li = dao.doQuery(
					"SELECT a.CZGH as CZGH,sum(a.CYSR) as CYSR,sum(a.YJJE) as YJJE,sum(a.TPJE) as TPJE,sum(a.FPZS) as FPZS,sum(a.SJZS) as SJZS,sum(a.YSJE) as YSJE,sum(a.YSXJ) as YSXJ,sum(a.ZPZS) as ZPZS,sum(a.TYJJ) as TYJJ,sum(a.YSQT) as YSQT,sum(a.QTZS) as QTZS,sum(a.SRJE) as SRJE,sum(a.TCZC) as TCZC,sum(a.DBZC) as DBZC,sum(a.ZXJZFY) as ZXJZFY,sum(a.GRXJZF) as GRXJZF,sum(a.BCZHZF) as BCZHZF,sum(a.AZQGFY) as AZQGFY FROM ZY_JZXX a WHERE a.HZRQ IS NULL AND a.JZRQ<=:adt_jzrq AND a.JGID=:al_jgid group by a.CZGH order by a.CZGH",
					parametershzbd);
			for (int i = 0; i < li.size(); i++) {
				li.get(i).put("CYSR",
						String.format("%1$.2f", li.get(i).get("CYSR")));
				li.get(i).put("YJJE",
						String.format("%1$.2f", li.get(i).get("YJJE")));
				li.get(i).put("TPJE",
						String.format("%1$.2f", li.get(i).get("TPJE")));
				li.get(i).put("YSJE",
						String.format("%1$.2f", li.get(i).get("YSJE")));
				li.get(i).put("YSXJ",
						String.format("%1$.2f", li.get(i).get("YSXJ")));
				li.get(i).put("TYJJ",
						String.format("%1$.2f", li.get(i).get("TYJJ")));
				li.get(i).put("YSQT",
						String.format("%1$.2f", li.get(i).get("YSQT")));
				li.get(i).put("SRJE",
						String.format("%1$.2f", li.get(i).get("SRJE")));
				if (li.get(i).get("TCZC") != null) {
					li.get(i).put("TCZC",
							String.format("%1$.2f", li.get(i).get("TCZC")));
				}
				if (li.get(i).get("DBZC") != null) {
					li.get(i).put("DBZC",
							String.format("%1$.2f", li.get(i).get("DBZC")));
				}
				if (li.get(i).get("ZXJZFY") != null) {
					li.get(i).put("ZXJZFY",
							String.format("%1$.2f", li.get(i).get("ZXJZFY")));
				}
				if (li.get(i).get("GRXJZF") != null) {
					li.get(i).put("GRXJZF",
							String.format("%1$.2f", li.get(i).get("GRXJZF")));
				}
				if (li.get(i).get("BCZHZF") != null) {
					li.get(i).put("BCZHZF",
							String.format("%1$.2f", li.get(i).get("BCZHZF")));
				}
				if (li.get(i).get("AZQGFY") != null) {
					li.get(i).put("AZQGFY",
							String.format("%1$.2f", li.get(i).get("AZQGFY")));
				}
//				if (li.get(i).get("SZYB") != null) {
//					li.get(i).put("SZYB",
//							String.format("%1$.2f", li.get(i).get("SZYB")));
//				}
//				if (li.get(i).get("SYB") != null) {
//					li.get(i).put("SYB",
//							String.format("%1$.2f", li.get(i).get("SYB")));
//				}
//				if (li.get(i).get("YHYB") != null) {
//					li.get(i).put("YHYB",
//							String.format("%1$.2f", li.get(i).get("YHYB")));
//				}
//				if (li.get(i).get("SMK") != null) {
//					li.get(i).put("SMK",
//							String.format("%1$.2f", li.get(i).get("SMK")));
//				}
//				if (li.get(i).get("QZPJ") == null) {
//					li.get(i).put("QZPJ", "");
//				}
//				if (li.get(i).get("QZSJ") == null) {
//					li.get(i).put("QZSJ", "");
//				}
				String ids_fkfs_hql = "select d.FKFS as FKFS,sum(d.FKJE) as FKJE,e.FKMC as FKMC from ("+
						"select a.FKFS as FKFS, (-1*a.FKJE) as FKJE from ZY_FKXX a, ZY_JSZF b,ZY_JZXX c where a.ZYH = b.ZYH and a.JSCS = b.JSCS AND b.JZRQ = c.JZRQ and b.JGID = c.JGID and c.HZRQ IS NULL AND c.JZRQ<=:adt_jzrq AND c.JGID=:al_jgid and b.ZFGH = :czgh"+
						" union all "+
						"select a.FKFS as FKFS, a.FKJE as FKJE from ZY_FKXX a, ZY_ZYJS b,ZY_JZXX c where b.JSLX<>4 and a.ZYH = b.ZYH and a.JSCS = b.JSCS AND b.JZRQ = c.JZRQ and b.JGID = c.JGID and c.HZRQ IS NULL AND c.JZRQ<=:adt_jzrq AND c.JGID=:al_jgid and b.CZGH = :czgh"+
						" union all "+
						"SELECT a.JKFS as FKFS,a.JKJE as FKJE FROM ZY_TBKK a,ZY_JZXX b WHERE a.JZRQ = b.JZRQ and b.JGID = a.JGID and b.HZRQ IS NULL AND b.JZRQ<=:adt_jzrq AND b.JGID=:al_jgid and a.CZGH = :czgh"+
						" union all "+
						"SELECT a.JKFS as FKFS,(-1*a.JKJE) as FKJE FROM ZY_TBKK a ,ZY_JKZF b,ZY_JZXX c WHERE b.JKXH = a.JKXH AND b.JZRQ = c.JZRQ and b.JGID = c.JGID and c.HZRQ IS NULL AND c.JZRQ<=:adt_jzrq AND c.JGID=:al_jgid and b.ZFGH = :czgh"+
						") d left outer join GY_FKFS e on d.FKFS = e.FKFS group by d.FKFS,e.FKMC";
				String ids_brxz_hql = "select sum(c.FYHJ) as FYHJ,sum(c.ZFHJ) as ZFHJ,c.BRXZ as BRXZ,d.XZMC as XZMC,d.DBPB as DBPB from ("+
				"SELECT a.FYHJ as FYHJ,a.ZFHJ as ZFHJ,a.BRXZ as BRXZ FROM ZY_ZYJS a,ZY_JZXX b WHERE a.JSLX<>4 and a.FYHJ<>a.ZFHJ and a.JZRQ = b.JZRQ and b.JGID = a.JGID and b.HZRQ IS NULL AND b.JZRQ<=:adt_jzrq AND b.JGID=:al_jgid and a.CZGH = :czgh"+
				" union all "+
				"SELECT (-1*a.FYHJ) as FYHJ,(-1*a.ZFHJ) as ZFHJ,a.BRXZ as BRXZ FROM ZY_ZYJS a ,ZY_JSZF b,ZY_JZXX c WHERE a.JSLX<>4 and a.ZYH = b.ZYH AND a.JSCS = b.JSCS and a.FYHJ<>a.ZFHJ and b.JZRQ = c.JZRQ and b.JGID = c.JGID and c.HZRQ IS NULL AND c.JZRQ<=:adt_jzrq AND c.JGID=:al_jgid and b.ZFGH = :czgh"+
				") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("adt_jzrq",
						sdfdatetime.parse(sdfdatetime.format(idt_hzrq)));
				parameters.put("al_jgid", manaUnitId);
				parameters.put("czgh", li.get(i).get("CZGH")+"");
				List<Map<String, Object>> ids_brxz = dao.doSqlQuery(ids_brxz_hql,parameters);
				List<Map<String, Object>> ids_fkfs = dao.doSqlQuery(ids_fkfs_hql,parameters);
				String  qtysFb="";
				String jzjeSt="0.00";
				if (ids_fkfs  != null && ids_fkfs .size() != 0) {
					 for(int n=0;n<ids_fkfs.size();n++){
							 qtysFb = qtysFb +ids_fkfs.get(n).get("FKMC")+ ":"
									+ String.format("%1$.2f",ids_fkfs.get(n).get("FKJE"))
									+ " ";
					 }
				}
				if (ids_brxz  != null && ids_brxz .size() != 0) {
					 for(int n=0;n<ids_brxz.size();n++){
						 if(Integer.parseInt(ids_brxz.get(n).get("DBPB")+"")==0){
							 jzjeSt= String.format("%1$.2f",parseDouble(jzjeSt) +(parseDouble(ids_brxz.get(n).get("FYHJ")+ "")-parseDouble(ids_brxz.get(n).get("ZFHJ")+ "")));
						 }else{
							 qtysFb = qtysFb +ids_brxz.get(n).get("XZMC")+ ":"
									+ String.format("%1$.2f",(parseDouble(ids_brxz.get(n).get("FYHJ")+ "")-parseDouble(ids_brxz.get(n).get("ZFHJ")+ "")))
									+ " ";
						 }
					 }
					 qtysFb = qtysFb+" "+"记账 :"+jzjeSt+" ";
				}
				
//				if (li.get(i).get("QTYSFB") == null) {
					li.get(i).put("QTYSFB", qtysFb);
//				}
//				QTYSFB=li.get(i).get("QTYSFB")+" ";
			}
			String ids_fkfs_hql = "select d.FKFS as FKFS,sum(d.FKJE) as FKJE,e.FKMC as FKMC from ("+
					"select a.FKFS as FKFS, (-1*a.FKJE) as FKJE from ZY_FKXX a, ZY_JSZF b,ZY_JZXX c where a.ZYH = b.ZYH and a.JSCS = b.JSCS AND b.JZRQ = c.JZRQ and b.JGID = c.JGID and c.HZRQ IS NULL AND c.JZRQ<=:adt_jzrq AND c.JGID=:al_jgid"+
					" union all "+
					"select a.FKFS as FKFS, a.FKJE as FKJE from ZY_FKXX a, ZY_ZYJS b,ZY_JZXX c where b.JSLX<>4 and a.ZYH = b.ZYH and a.JSCS = b.JSCS AND b.JZRQ = c.JZRQ and b.JGID = c.JGID and c.HZRQ IS NULL AND c.JZRQ<=:adt_jzrq AND c.JGID=:al_jgid"+
					" union all "+
					"SELECT a.JKFS as FKFS,a.JKJE as FKJE FROM ZY_TBKK a,ZY_JZXX b WHERE a.JZRQ = b.JZRQ and b.JGID = a.JGID and b.HZRQ IS NULL AND b.JZRQ<=:adt_jzrq AND b.JGID=:al_jgid"+
					" union all "+
					"SELECT a.JKFS as FKFS,(-1*a.JKJE) as FKJE FROM ZY_TBKK a ,ZY_JKZF b,ZY_JZXX c WHERE b.JKXH = a.JKXH AND b.JZRQ = c.JZRQ and b.JGID = c.JGID and c.HZRQ IS NULL AND c.JZRQ<=:adt_jzrq AND c.JGID=:al_jgid"+
					") d left outer join GY_FKFS e on d.FKFS = e.FKFS group by d.FKFS,e.FKMC";
			String ids_brxz_hql = "select sum(c.FYHJ) as FYHJ,sum(c.ZFHJ) as ZFHJ,c.BRXZ as BRXZ,d.XZMC as XZMC,d.DBPB as DBPB from ("+
			"SELECT a.FYHJ as FYHJ,a.ZFHJ as ZFHJ,a.BRXZ as BRXZ FROM ZY_ZYJS a,ZY_JZXX b WHERE a.JSLX<>4 and a.FYHJ<>a.ZFHJ and a.JZRQ = b.JZRQ and b.JGID = a.JGID and b.HZRQ IS NULL AND b.JZRQ<=:adt_jzrq AND b.JGID=:al_jgid"+
			" union all "+
			"SELECT (-1*a.FYHJ) as FYHJ,(-1*a.ZFHJ) as ZFHJ,a.BRXZ as BRXZ FROM ZY_ZYJS a ,ZY_JSZF b,ZY_JZXX c WHERE a.JSLX<>4 and a.ZYH = b.ZYH AND a.JSCS = b.JSCS and a.FYHJ<>a.ZFHJ and b.JZRQ = c.JZRQ and b.JGID = c.JGID and c.HZRQ IS NULL AND c.JZRQ<=:adt_jzrq AND c.JGID=:al_jgid"+
			") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB";
			List<Map<String, Object>> ids_brxz = dao.doSqlQuery(ids_brxz_hql,parametershzbd);
			List<Map<String, Object>> ids_fkfs = dao.doSqlQuery(ids_fkfs_hql,parametershzbd);
			String  qtysFb="";
			String jzjeSt="0.00";
			if (ids_fkfs  != null && ids_fkfs .size() != 0) {
				 for(int i=0;i<ids_fkfs.size();i++){
						 qtysFb = qtysFb +ids_fkfs.get(i).get("FKMC")+ ":"
								+ String.format("%1$.2f",ids_fkfs.get(i).get("FKJE"))
								+ " ";
				 }
			}
			if (ids_brxz  != null && ids_brxz .size() != 0) {
				 for(int i=0;i<ids_brxz.size();i++){
					 if(Integer.parseInt(ids_brxz.get(i).get("DBPB")+"")==0){
						 jzjeSt= String.format("%1$.2f",parseDouble(jzjeSt) +(parseDouble(ids_brxz.get(i).get("FYHJ")+ "")-parseDouble(ids_brxz.get(i).get("ZFHJ")+ "")));
					 }else{
						 qtysFb = qtysFb +ids_brxz.get(i).get("XZMC")+ ":"
								+ String.format("%1$.2f",(parseDouble(ids_brxz.get(i).get("FYHJ")+ "")-parseDouble(ids_brxz.get(i).get("ZFHJ")+ "")))
								+ " ";
					 }
				 }
				 qtysFb = qtysFb+" "+"记账 :"+jzjeSt+" ";
			}
			response.put("qtysFb", qtysFb);
			Map<String, Object> parametershj = dao
					.doLoad("SELECT sum(ZY_JZXX.CYSR) as ZCYSR,sum(ZY_JZXX.YJJE) as ZYJJE,sum(ZY_JZXX.TPJE) as ZTPJE,sum(ZY_JZXX.FPZS) as ZFPZS,sum(ZY_JZXX.SJZS) as ZSJZS,sum(ZY_JZXX.YSJE) as ZYSJE,sum(ZY_JZXX.YSXJ) as ZYSXJ,sum(ZY_JZXX.ZPZS) as ZZPZS,sum(ZY_JZXX.TYJJ) as ZTYJJ,sum(ZY_JZXX.YSQT) as ZYSQT,sum(ZY_JZXX.QTZS) as ZQTZS,sum(ZY_JZXX.SRJE) as ZSRJE,sum(ZY_JZXX.TCZC) as TCZC,sum(ZY_JZXX.DBZC) as DBZC,sum(ZY_JZXX.ZXJZFY) as ZXJZFY,sum(ZY_JZXX.GRXJZF) as GRXJZF,sum(ZY_JZXX.BCZHZF) as BCZHZF,sum(ZY_JZXX.AZQGFY) as AZQGFY FROM ZY_JZXX ZY_JZXX WHERE ZY_JZXX.HZRQ IS NULL AND ZY_JZXX.JZRQ<=:adt_jzrq AND JGID=:al_jgid",
							parametershzbd);
			if (parametershj.get("ZCYSR") != null) {
				response.put("ZCYSR",
						String.format("%1$.2f", parametershj.get("ZCYSR")));
			} else {
				response.put("ZCYSR", "");
			}
			if (parametershj.get("ZYJJE") != null) {
				response.put("ZYJJE",
						String.format("%1$.2f", parametershj.get("ZYJJE")));
			} else {
				response.put("ZYJJE", "");
			}
			if (parametershj.get("ZTPJE") != null) {
				response.put("ZTPJE",
						String.format("%1$.2f", parametershj.get("ZTPJE")));
			} else {
				response.put("ZTPJE", "");
			}
			if (parametershj.get("ZFPZS") != null) {
				response.put("ZFPZS", parametershj.get("ZFPZS") + "");
			} else {
				response.put("ZFPZS", "");
			}
			if (parametershj.get("ZZPZS") != null) {
				response.put("ZZPZS", parametershj.get("ZZPZS") + "");
			} else {
				response.put("ZZPZS", "");
			}
			if (parametershj.get("ZSJZS") != null) {
				response.put("ZSJZS", parametershj.get("ZSJZS") + "");
			} else {
				response.put("ZSJZS", "");
			}
			if (parametershj.get("ZTYJJ") != null) {
				response.put("ZTYJJ",
						String.format("%1$.2f", parametershj.get("ZTYJJ")));
			} else {
				response.put("ZTYJJ", "");
			}
			if (parametershj.get("ZYSJE") != null) {
				response.put("ZYSJE",
						String.format("%1$.2f", parametershj.get("ZYSJE")));
			} else {
				response.put("ZYSJE", "");
			}
			if (parametershj.get("ZYSXJ") != null) {
				response.put("ZYSXJ",
						String.format("%1$.2f", parametershj.get("ZYSXJ")));
			} else {
				response.put("ZYSXJ", "");

			}
			if (parametershj.get("ZYSQT") != null) {
				response.put("ZYSQT",
						String.format("%1$.2f", parametershj.get("ZYSQT")));
			} else {
				response.put("ZYSQT", "");
			}
			if (parametershj.get("ZQTZS") != null) {
				response.put("ZQTZS", parametershj.get("ZQTZS") + "");
			} else {
				response.put("ZQTZS", "");
			}
			if (parametershj.get("ZSRJE") != null) {
				response.put("ZSRJE",
						String.format("%1$.2f", parametershj.get("ZSRJE")));
			} else {
				response.put("ZSRJE", "");
			}
			if (parametershj.get("TCZC") != null) {
				response.put("TCZCHJ",
						String.format("%1$.2f", parametershj.get("TCZC")));
			} else {
				response.put("TCZCHJ", "");
			}
			if (parametershj.get("DBZC") != null) {
				response.put("DBZCHJ",
						String.format("%1$.2f", parametershj.get("DBZC")));
			} else {
				response.put("DBZCHJ", "");
			}
			if (parametershj.get("ZXJZFY") != null) {
				response.put("ZXJZFYHJ",
						String.format("%1$.2f", parametershj.get("ZXJZFY")));
			} else {
				response.put("ZXJZFYHJ", "");
			}
			if (parametershj.get("GRXJZF") != null) {
				response.put("GRXJZFHJ",
						String.format("%1$.2f", parametershj.get("GRXJZF")));
			} else {
				response.put("GRXJZFHJ", "");
			}
			if (parametershj.get("BCZHZF") != null) {
				response.put("BCZHZFHJ",
						String.format("%1$.2f", parametershj.get("BCZHZF")));
			} else {
				response.put("BCZHZFHJ", "");
			}
			if (parametershj.get("AZQGFY") != null) {
				response.put("AZQGFYHJ",
						String.format("%1$.2f", parametershj.get("AZQGFY")));
			} else {
				response.put("AZQGFYHJ", "");
			}
//			if (parametershj.get("SZYB") != null) {
//				response.put("SZYBHJ",
//						String.format("%1$.2f", parametershj.get("SZYB")));
//			}
//			if (parametershj.get("SYB") != null) {
//				response.put("SYBHJ",
//						String.format("%1$.2f", parametershj.get("SYB")));
//			}
//			if (parametershj.get("YHYB") != null) {
//				response.put("YHYBHJ",
//						String.format("%1$.2f", parametershj.get("YHYB")));
//			}
//			if (parametershj.get("SMK") != null) {
//				response.put("SMKHJ",
//						String.format("%1$.2f", parametershj.get("SMK")));
//			}
			List<Map<String, Object>> zfzslist = new ArrayList<Map<String, Object>>();
			StringBuffer sbfp = new StringBuffer();
			StringBuffer sbsj = new StringBuffer();
			zfzslist = dao
					.doQuery(
							"SELECT ZY_ZFPJ.PJLB as PJLB,ZY_ZFPJ.PJHM as PJHM FROM ZY_ZFPJ ZY_ZFPJ,ZY_JZXX ZY_JZXX WHERE ( ZY_ZFPJ.JZRQ = ZY_JZXX.JZRQ ) AND ( ZY_ZFPJ.CZGH = ZY_JZXX.CZGH ) AND ZY_JZXX.HZRQ IS NULL AND ZY_JZXX.JZRQ <=:adt_jzrq and ZY_JZXX.JGID=:al_jgid ORDER BY ZY_ZFPJ.PJLB,ZY_ZFPJ.PJHM",
							parametershzbd);
			for (int i = 0; i < zfzslist.size(); i++) {
				if (Integer.parseInt(zfzslist.get(i).get("PJLB") + "") == 1) {
					if (zfzslist.get(i).get("PJHM") != null) {
						sbfp.append(zfzslist.get(i).get("PJHM") + " ");
					}
				} else {
					if (zfzslist.get(i).get("PJHM") != null) {
						sbsj.append(zfzslist.get(i).get("PJHM") + " ");
					}
				}
			}
			if (sbfp.toString() != null) {
				response.put("ZFFPHM", sbfp.toString());
			} else {
				response.put("ZFFPHM", "");
			}
			if (sbsj.toString() != null) {
				response.put("ZFSJHM", sbsj.toString());
			} else {
				response.put("ZFSJHM", "");
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ModelDataOperationException e1) {
			e1.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void doInquiry(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws PrintException {
		li.clear();
		try {
			BSPHISUtil.wf_Query(req, res, li, dao, ctx);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}

	public static HospitalChargesSummary getInstance() {
		if (cck == null) {
			cck = new HospitalChargesSummary();
		}
		return cck;
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
