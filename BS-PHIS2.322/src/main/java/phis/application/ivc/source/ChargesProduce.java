package phis.application.ivc.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import phis.source.utils.BSHISUtil;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.print.PrintException;

public class ChargesProduce extends AbstractActionService implements
		DAOSupportable {
	private static ChargesProduce cck = null;

	List<Map<String, Object>> li = new ArrayList<Map<String, Object>>();
	Date hzrq = new Date();
	private ChargesProduce() {
	}

	public void doGetFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		records.addAll(li);
	}

	public void doGetParameters(Map<String, Object> request,
			Map<String, Object> response, BaseDAO dao, Context ctx)
			throws PrintException {
		li.clear();
		UserRoleToken user = UserRoleToken.getCurrent();
		String userName = user.getUserName();// 用户名
		String jgName = user.getManageUnitName();// 用户的机构名称
		String jgid = user.getManageUnitId();// 用户的机构名称
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> fkfsparameters = new HashMap<String, Object>();
		try {
			parameters.put("JGID", jgid);
			parameters.put("MZLB",
					Long.parseLong(BSPHISUtil.getMZLB(jgid, dao) + ""));
			Calendar cdate = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = sdf.parse(request.get("hzrq")+"");
			hzrq = date;
			cdate.setTime(date);
			cdate.set(Calendar.MILLISECOND, 0);
			parameters.put("hzrq", cdate.getTime());
			fkfsparameters.put("SYLX", "1");
			fkfsparameters.put("ZFBZ", "0");

			String hzrb_sql = "select CZGH as YGDM,0 as GHJE,sum(ZJJE) as ZJJE,sum(XJJE) as XJJE," +
					" sum(QTYS) as QTYS,sum(HBWC) as HBWC,0 as GHRC,sum(FPZS) as FPZS,0 as THSL," +
					" sum(ZFZS) as ZFZS,sum(ZFFSHJ) as ZFFSHJ,sum(NHZFHJ) as NHZFHJ,sum(YBZFHJ) as YBZFHJ," +
					" sum(NHJZ) as NHJZ,sum(YBJZ) as YBJZ,sum(JZJEST) as JZJEST,sum(JZZE+ZFZF) as JZZE," +
					" sum(ZFZF) as ZFZF,sum(WXHJ) as WXHJ,sum(ZFBHJ) as ZFBHJ,(sum(WXHJ)+sum(ZFBHJ)) as SMFHJ," +
					"sum(APPWXHJ) as APPWXHJ,sum(APPZFBHJ) as APPZFBHJ,(sum(APPWXHJ)+sum(APPZFBHJ)) as APPHJ,0 as GHYHHJ" +
					" from MS_HZRB where JGID =:JGID and MZLB =:MZLB and HZRQ is null " +
					" and JZRQ<=:hzrq group by CZGH";
			String ghrb_sql = "select CZGH as YGDM,sum(ZJJE-YZJM-YHJE) as GHJE,0 as ZJJE,sum(XJJE) as XJJE," +
					" sum(QTYS) as QTYS,sum(HBWC) as HBWC,sum(FPZS) as GHRC,0 as FPZS," +
					" sum(THSL) as THSL,0 as ZFZS,sum(ZFFSHJ) as ZFFSHJ,0 as NHZFHJ,sum(YBZFHJ) as YBZFHJ," +
					" 0 as NHJZ,sum(QTYS) as YBJZ,0 as JZJEST,sum(QTYS+ZHJE) as JZZE,sum(ZHJE) as ZFZF,sum(WXHJ) as WXHJ,sum(ZFBHJ) as ZFBHJ,(sum(WXHJ)+sum(ZFBHJ)) as SMFHJ,0 as APPWXHJ,0 as APPZFBHJ,0 as APPHJ,sum(YHJE) as GHYHHJ " +
					" from MS_GHRB where JGID =:JGID and MZLB =:MZLB" +
					" and HZRQ is null and JZRQ<=:hzrq group by CZGH";
			String hzghrb_sql = "select YGDM as YGDM,sum(GHJE) as GHJE,sum(ZJJE) as ZJJE,sum(XJJE) as XJJE," +
					" sum(QTYS) as QTYS,sum(HBWC) as HBWC,sum(GHRC) as GHRC,sum(FPZS) as FPZS," +
					" sum(THSL) as THSL,sum(ZFZS) as ZFZS,sum(ZFFSHJ) as ZFFSHJ,sum(NHZFHJ) as NHZFHJ,sum(YBZFHJ) as YBZFHJ," +
					" sum(NHJZ) as NHJZ,sum(YBJZ) as YBJZ,sum(JZJEST) as JZJEST,sum(JZZE) as JZZE," +
					" sum(ZFZF) as ZFZF,sum(WXHJ) as WXHJ,sum(ZFBHJ) as ZFBHJ,(sum(WXHJ)+sum(ZFBHJ)) as SMFHJ," +
					"sum(APPWXHJ) as APPWXHJ,sum(APPZFBHJ) as APPZFBHJ,(sum(APPWXHJ)+sum(APPZFBHJ)) as APPHJ,sum(GHYHHJ) as GHYHHJ from ("
					+ hzrb_sql + " union all " + ghrb_sql + ") group by YGDM";
			List<Map<String, Object>> hzghrb_list = dao.doSqlQuery(hzghrb_sql,
					parameters);// 收费日报数据
			String fkxx="";//收费明细字符串
			// 合并挂号和收费
			int ghcount = 0;// 人次合计
			double ghAmount = 0.0;// 挂号金额合计
			int thcount = 0;// 退号合计
			int sfcount = 0;// 发票张数合计
			double sfamount = 0.0;// 收费金额合计
			int zfcount = 0;// 发票作废张数
			double totals = 0.0;// 总的合计
			double xjAmount = 0.0;// 现金合计
			double qtysAmount = 0.0;// 其他应收
			double hbwcAmount = 0.0;// 货币误差
            double ZFFSHJAmount=0.0;//自费金额合计
            double NHZFHJAmount=0.0;//农合自费金额合计
            double YBZFHJAmount=0.0;//医保自费金额合计
            double XJJEAmount=0.0;//现金金额合计
            double ZFZFAmount=0.0;//账户支付合计
            double NHJZAmount=0.0;//农合记账合计
            double YBJZAmount=0.0;//医保记账
            double JZZEAmount=0.0;//记账总额合计
            double WXAmount=0.0;//微信金额合计
            double ZFBAmount=0.0;//支付宝金额合计
            double APPWXAmount=0.0;//app微信金额合计
            double APPZFBAmount=0.0;//app支付宝金额合计
			double GHYHHJAmount=0.0;//挂号优惠总额合计 zhaojian 2019-05-12
			for (int j = 0; j < hzghrb_list.size(); j++) {
				Map<String, Object> m = new HashMap<String, Object>();
				ghcount += parseInt(hzghrb_list.get(j).get("GHRC"));
				thcount += parseInt(hzghrb_list.get(j).get("THSL"));
				ghAmount += parseDouble(hzghrb_list.get(j).get("GHJE"));
				sfcount += parseInt(hzghrb_list.get(j).get("FPZS"));
				zfcount += parseInt(hzghrb_list.get(j).get("ZFZS"));
				sfamount += parseDouble(hzghrb_list.get(j).get("ZJJE"));
				totals += BSPHISUtil.getDouble(
						parseDouble(hzghrb_list.get(j).get("ZJJE"))
						+ parseDouble(hzghrb_list.get(j).get("GHJE")),2);
				xjAmount += parseDouble(hzghrb_list.get(j).get("XJJE"));
				qtysAmount += parseDouble(hzghrb_list.get(j).get("QTYS"));
				hbwcAmount += parseDouble(hzghrb_list.get(j).get("HBWC"));
				
				ZFFSHJAmount += parseDouble(hzghrb_list.get(j).get("ZFFSHJ"));
				NHZFHJAmount += parseDouble(hzghrb_list.get(j).get("NHZFHJ"));
				YBZFHJAmount += parseDouble(hzghrb_list.get(j).get("YBZFHJ"));
				XJJEAmount += parseDouble(hzghrb_list.get(j).get("XJJE"));
				ZFZFAmount += parseDouble(hzghrb_list.get(j).get("ZFZF"));
				NHJZAmount += parseDouble(hzghrb_list.get(j).get("NHJZ"));
				YBJZAmount += parseDouble(hzghrb_list.get(j).get("YBJZ"));
				JZZEAmount += parseDouble(hzghrb_list.get(j).get("JZZE"));
				WXAmount += parseDouble(hzghrb_list.get(j).get("WXHJ"));//微信
				ZFBAmount += parseDouble(hzghrb_list.get(j).get("ZFBHJ"));//支付宝
				APPWXAmount += parseDouble(hzghrb_list.get(j).get("APPWXHJ"));//app微信
				APPZFBAmount += parseDouble(hzghrb_list.get(j).get("APPZFBHJ"));//app支付宝
				//挂号优惠合计 zhaojian 2019-05-12
				GHYHHJAmount = BSHISUtil.doublesum(GHYHHJAmount,parseDouble(hzghrb_list.get(j).get("GHYHHJ")));
				Map<String, Object> para = new HashMap<String, Object>();
				para.put("PERSONID", hzghrb_list.get(j).get("YGDM") + "");
				List<Map<String,Object>> personList = dao.doQuery("select PERSONNAME as PERSONNAME from SYS_Personnel" +
						" where PERSONID=:PERSONID", para);
				String ygxm = hzghrb_list.get(j).get("YGDM") + "";
				if(personList.size()>0){
					ygxm = personList.get(0).get("PERSONNAME")+"";
				}
				m.put("ZFFSHJ", String.format("%1$.2f",hzghrb_list.get(j).get("ZFFSHJ")));
				m.put("NHZFHJ", String.format("%1$.2f",hzghrb_list.get(j).get("NHZFHJ")));
				m.put("YBZFHJ", String.format("%1$.2f",hzghrb_list.get(j).get("YBZFHJ")));
				m.put("ZFZF", String.format("%1$.2f",hzghrb_list.get(j).get("ZFZF")));
				m.put("NHJZ", String.format("%1$.2f",hzghrb_list.get(j).get("NHJZ")));
				m.put("YBJZ", String.format("%1$.2f",hzghrb_list.get(j).get("YBJZ")));
				m.put("JZZE", String.format("%1$.2f",hzghrb_list.get(j).get("JZZE")));
				
				m.put("CZGH", ygxm);
				m.put("ZJJE",String.format("%1$.2f", hzghrb_list.get(j).get("ZJJE")));
				m.put("FPZS", hzghrb_list.get(j).get("FPZS") + "");
				m.put("ZFZS", hzghrb_list.get(j).get("ZFZS") + "");
				m.put("QTYS",String.format("%1$.2f", hzghrb_list.get(j).get("QTYS")));
				m.put("HBWC",String.format("%1$.2f", hzghrb_list.get(j).get("HBWC")));
				m.put("XJJE",String.format("%1$.2f", parseDouble(hzghrb_list.get(j).get("XJJE"))));
				m.put("TOTALAMOUNT",
						String.format("%1$.2f", BSPHISUtil.getDouble(
						parseDouble(hzghrb_list.get(j).get("ZJJE"))
								+ parseDouble(hzghrb_list.get(j).get("GHJE")),
						2)));
				m.put("GHJE",String.format("%1$.2f", hzghrb_list.get(j).get("GHJE")));
				m.put("GHRC", hzghrb_list.get(j).get("GHRC")+"");
				m.put("THSL", hzghrb_list.get(j).get("THSL")+"");
				
				m.put("WXHJ", String.format("%1$.2f",hzghrb_list.get(j).get("WXHJ")));//微信
				m.put("ZFBHJ", String.format("%1$.2f",hzghrb_list.get(j).get("ZFBHJ")));//支付宝
				m.put("SMFHJ", String.format("%1$.2f",hzghrb_list.get(j).get("SMFHJ")));//扫码付
				m.put("APPWXHJ", String.format("%1$.2f",hzghrb_list.get(j).get("APPWXHJ")));//app微信
				m.put("APPZFBHJ", String.format("%1$.2f",hzghrb_list.get(j).get("APPZFBHJ")));//app支付宝
				m.put("APPHJ", String.format("%1$.2f",hzghrb_list.get(j).get("APPHJ")));//app
				//挂号优惠总额合计 zhaojian 2019-05-12
				m.put("GHYHHJ", (hzghrb_list.get(j).get("GHYHHJ")+"").equals("0")?"0":("-"+(hzghrb_list.get(j).get("GHYHHJ")+"")));
//				String sql_fkfs = "select c.FKFS as FKFS,sum(c.FKJE) as FKJE,d.FKMC as FKMC from ("+
//						"select a.FKFS as FKFS,a.FKJE as FKJE from MS_FKXX a,MS_MZXX b where a.MZXH = b.MZXH and b.JGID = :jgid and b.HZRQ is null and b.MZLB = :mzlb and b.CZGH = :czgh and b.JZRQ<=:hzrq"+
//						" union all "+
//						"select a.FKFS as FKFS,(-1*a.FKJE) as FKJE from MS_FKXX a,MS_ZFFP b where a.MZXH = b.MZXH and b.JGID = :jgid and b.HZRQ is null and b.MZLB = :mzlb and b.CZGH = :czgh and b.JZRQ<=:hzrq"+
//						" union all "+
//						"select a.FKFS as FKFS,a.FKJE as FKJE from MS_GH_FKXX a,MS_GHMX b where a.SBXH = b.SBXH and b.JGID = :jgid and b.HZRQ is null and b.MZLB = :mzlb and b.CZGH = :czgh and b.JZRQ<=:hzrq"+
//						" union all "+
//						"select a.FKFS as FKFS,(-1*a.FKJE) as FKJE from MS_GH_FKXX a,MS_THMX b where a.SBXH = b.SBXH and b.JGID = :jgid and b.HZRQ is null and b.MZLB = :mzlb and b.CZGH = :czgh and b.JZRQ<=:hzrq"+
//						") c left outer join GY_FKFS d on c.FKFS = d.FKFS group by c.FKFS,d.FKMC order by c.FKFS";
//				String sql_brxz = "select sum(c.QTYS) as QTYS,c.BRXZ as BRXZ,d.XZMC as XZMC,nvl(d.DBPB,0) as DBPB from ("+
//						"select a.BRXZ as BRXZ,a.QTYS as QTYS from MS_MZXX a where a.JGID=:jgid and a.HZRQ IS NULL and a.MZLB = :mzlb and a.CZGH = :czgh and a.JZRQ<=:hzrq"+
//						" union all "+
//						"select a.BRXZ as BRXZ,(-1*a.QTYS) as QTYS from MS_MZXX a,MS_ZFFP b where a.MZXH = b.MZXH and b.JGID=:jgid and b.HZRQ IS NULL and b.MZLB = :mzlb and b.CZGH = :czgh and b.JZRQ<=:hzrq" +
//						" union all "+
//						"select a.BRXZ as BRXZ,a.QTYS as QTYS from MS_GHMX a where a.JGID=:jgid and a.HZRQ IS NULL and a.MZLB = :mzlb and a.CZGH = :czgh and a.JZRQ<=:hzrq"+
//						" union all "+
//						"select a.BRXZ as BRXZ,(-1*a.QTYS) as QTYS from MS_GHMX a,MS_THMX b where a.SBXH = b.SBXH and b.JGID=:jgid and b.HZRQ IS NULL and b.MZLB = :mzlb and b.CZGH = :czgh and b.JZRQ<=:hzrq" +
//						") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB";
//				Map<String, Object> parameters2 = new HashMap<String, Object>();
//				parameters2.put("jgid", jgid);
//				parameters2.put("mzlb",Long.parseLong(BSPHISUtil.getMZLB(jgid, dao) + ""));
//				parameters2.put("czgh",hzghrb_list.get(j).get("YGDM") + "");
//				parameters2.put("hzrq", cdate.getTime());
//				List<Map<String, Object>> ids_fkfs = dao.doSqlQuery(sql_fkfs,parameters2);
//				List<Map<String, Object>> ids_brxz = dao.doSqlQuery(sql_brxz,parameters2);
//				String  qtysFb="";
//				String jzjeSt="0.00";
//				if (ids_fkfs  != null && ids_fkfs .size() != 0) {
//					 for(int n=0;n<ids_fkfs.size();n++){
//							 qtysFb = qtysFb +ids_fkfs.get(n).get("FKMC")+ ":"
//									+ String.format("%1$.2f",ids_fkfs.get(n).get("FKJE"))
//									+ " ";
//					 }
//				}
//				if (ids_brxz  != null && ids_brxz .size() != 0) {
//					 for(int n=0;n<ids_brxz.size();n++){
//						 if(Integer.parseInt(ids_brxz.get(n).get("DBPB")+"")==0){
//							 jzjeSt= String.format("%1$.2f",parseDouble(jzjeSt) +parseDouble(ids_brxz.get(n).get("QTYS")+ ""));
//						 }else{
//							 qtysFb = qtysFb +ids_brxz.get(n).get("XZMC")+ ":"
//									+ String.format("%1$.2f",parseDouble(ids_brxz.get(n).get("QTYS")+ ""))
//									+ " ";
//						 }
//					 }
//					 qtysFb = qtysFb+" "+"记账 :"+jzjeSt+" ";
//				}
//				m.put("fkxx", qtysFb);
				li.add(m);
			}
			Map<String, Object> jzrq = new HashMap<String, Object>();
			jzrq.put("JGID", jgid);
			// 查询上次结账时间
			Map<String, Object> hzrqmapsf = dao.doLoad(
					"select max(HZRQ) as HZRQ from  MS_HZRB where JGID=:JGID",
					jzrq);
			Map<String, Object> hzrqmapgh = dao.doLoad(
					"select max(HZRQ) as HZRQ from  MS_GHRB where JGID=:JGID",
					jzrq);
			if (hzrqmapsf.get("HZRQ") != null && hzrqmapgh.get("HZRQ") != null) {
				Date hzrqsf = sdf.parse(hzrqmapsf.get("HZRQ") + "");
				Date hzrqgh = sdf.parse(hzrqmapgh.get("HZRQ") + "");
				if (hzrqsf.getTime() > hzrqgh.getTime()) {
					response.put("startSummaryDate",
							BSHISUtil.toString(hzrqsf, "yyyy-MM-dd HH:mm:ss"));
				} else {
					response.put("startSummaryDate",
							BSHISUtil.toString(hzrqgh, "yyyy-MM-dd HH:mm:ss"));
				}
			} else if (hzrqmapsf.get("HZRQ") != null) {
				Date hzrqsf = sdf.parse(hzrqmapsf.get("HZRQ") + "");
				response.put("startSummaryDate",
						BSHISUtil.toString(hzrqsf, "yyyy-MM-dd HH:mm:ss"));
			} else if (hzrqmapgh.get("HZRQ") != null) {
				Date hzrqgh = sdf.parse(hzrqmapgh.get("HZRQ") + "");
				response.put("startSummaryDate",
						BSHISUtil.toString(hzrqgh, "yyyy-MM-dd HH:mm:ss"));
			} else {
				Map<String, Object> sfrqorghrq = new HashMap<String, Object>();
				sfrqorghrq.put("JGID", jgid);
				// 查询上次结账时间
				Map<String, Object> jzrqmapsf = dao
						.doLoad("select min(JZRQ) as JZRQ from  MS_HZRB where JGID=:JGID",
								sfrqorghrq);
				Map<String, Object> jzrqmapgh = dao
						.doLoad("select min(JZRQ) as JZRQ from  MS_GHRB where JGID=:JGID",
								sfrqorghrq);
				if (jzrqmapsf.get("JZRQ") != null
						&& jzrqmapgh.get("JZRQ") != null) {
					Date jzrqsf = sdf.parse(jzrqmapsf.get("JZRQ") + "");
					Date jzrqgh = sdf.parse(jzrqmapgh.get("JZRQ") + "");
					if (jzrqsf.getTime() < jzrqgh.getTime()) {
						response.put("startSummaryDate", BSHISUtil.toString(
								jzrqsf, "yyyy-MM-dd HH:mm:ss"));
					} else {
						response.put("startSummaryDate", BSHISUtil.toString(
								jzrqgh, "yyyy-MM-dd HH:mm:ss"));
					}
				} else if (jzrqmapsf.get("JZRQ") != null) {
					Date jzrqsf = sdf.parse(jzrqmapsf.get("JZRQ") + "");
					response.put("startSummaryDate",
							BSHISUtil.toString(jzrqsf, "yyyy-MM-dd HH:mm:ss"));
				} else if (jzrqmapgh.get("JZRQ") != null) {
					Date jzrqgh = sdf.parse(jzrqmapgh.get("JZRQ") + "");
					response.put("startSummaryDate",
							BSHISUtil.toString(jzrqgh, "yyyy-MM-dd HH:mm:ss"));
				}
			}
			response.put("Reviewedby","");
			response.put("fkxx", fkxx);
			response.put("preparedby", jgName);
			response.put("summaryDate", request.get("hzrq")+"");
			response.put("Lister", userName);
			response.put("DateTabling", BSHISUtil.getDate());
			response.put("totals", String.format("%1$.2f", totals));
			response.put("ghcount", ghcount + "");
			response.put("thcount", thcount + "");
			response.put("ghAmount", String.format("%1$.2f", ghAmount));
			response.put("sfcount", sfcount + "");
			response.put("zfcount", zfcount + "");
			response.put("sfamount", String.format("%1$.2f", sfamount));
			response.put("xjAmount", String.format("%1$.2f", xjAmount));
			response.put("qtysAmount", String.format("%1$.2f", qtysAmount));
			response.put("hbwcAmount", String.format("%1$.2f", hbwcAmount));
			response.put("ZFFSHJAmount", String.format("%1$.2f", ZFFSHJAmount));
			response.put("NHZFHJAmount", String.format("%1$.2f", NHZFHJAmount));
			response.put("YBZFHJAmount", String.format("%1$.2f", YBZFHJAmount));
			response.put("XJJEAmount", String.format("%1$.2f", XJJEAmount));
			response.put("NHJZAmount", String.format("%1$.2f", NHJZAmount));
			response.put("YBJZAmount", String.format("%1$.2f", YBJZAmount));
			response.put("ZFZFAmount", String.format("%1$.2f", ZFZFAmount));
			response.put("JZZEAmount", String.format("%1$.2f", YBJZAmount+ZFZFAmount+NHJZAmount));
			response.put("WXAmount", String.format("%1$.2f", WXAmount));//微信
			response.put("ZFBAmount", String.format("%1$.2f", ZFBAmount));//支付宝
			response.put("SMFAmount", String.format("%1$.2f", WXAmount+ZFBAmount));//扫码付
			response.put("APPWXAmount", String.format("%1$.2f", APPWXAmount));//app微信
			response.put("APPZFBAmount", String.format("%1$.2f", APPZFBAmount));//app支付宝
			response.put("APPAmount", String.format("%1$.2f", APPWXAmount+APPZFBAmount));//app
			//挂号优惠总额合计 zhaojian 2019-05-12
			response.put("GHYHHJAmount", String.format("%1$.2f", GHYHHJAmount).equals("0.00")?"0":("-"+String.format("%1$.2f", GHYHHJAmount)));
			
//			String sql_fkfs = "select c.FKFS as FKFS,sum(c.FKJE) as FKJE,d.FKMC as FKMC from ("+
//					"select a.FKFS as FKFS,a.FKJE as FKJE from MS_FKXX a,MS_MZXX b,MS_HZRB c where c.JZRQ = b.JZRQ and c.CZGH = b.CZGH and a.MZXH = b.MZXH and b.JGID = :jgid and c.HZRQ is null and b.MZLB = :mzlb and c.JZRQ<=:hzrq"+
//					" union all "+
//					"select a.FKFS as FKFS,(-1*a.FKJE) as FKJE from MS_FKXX a,MS_ZFFP b,MS_HZRB c where c.JZRQ = b.JZRQ and c.CZGH = b.CZGH and a.MZXH = b.MZXH and b.JGID = :jgid and c.HZRQ is null and b.MZLB = :mzlb and c.JZRQ<=:hzrq"+
//					" union all "+
//					"select a.FKFS as FKFS,a.FKJE as FKJE from MS_GH_FKXX a,MS_GHMX b,MS_GHRB c where c.JZRQ = b.JZRQ and c.CZGH = b.CZGH and a.SBXH = b.SBXH and b.JGID = :jgid and c.HZRQ is null and b.MZLB = :mzlb and c.JZRQ<=:hzrq"+
//					" union all "+
//					"select a.FKFS as FKFS,(-1*a.FKJE) as FKJE from MS_GH_FKXX a,MS_THMX b,MS_GHRB c where c.JZRQ = b.JZRQ and c.CZGH = b.CZGH and a.SBXH = b.SBXH and b.JGID = :jgid and c.HZRQ is null and b.MZLB = :mzlb and c.JZRQ<=:hzrq"+
//					") c left outer join GY_FKFS d on c.FKFS = d.FKFS group by c.FKFS,d.FKMC order by c.FKFS";
//			String sql_brxz = "select sum(c.QTYS) as QTYS,c.BRXZ as BRXZ,d.XZMC as XZMC,nvl(d.DBPB,0) as DBPB from ("+
//					"select a.BRXZ as BRXZ,a.QTYS as QTYS from MS_MZXX a,MS_HZRB c where c.JZRQ = a.JZRQ and c.CZGH = a.CZGH and a.JGID=:jgid and c.HZRQ IS NULL and a.MZLB = :mzlb and c.JZRQ<=:hzrq"+
//					" union all "+
//					"select a.BRXZ as BRXZ,(-1*a.QTYS) as QTYS from MS_MZXX a,MS_ZFFP b,MS_HZRB c where c.JZRQ = b.JZRQ and c.CZGH = b.CZGH and a.MZXH = b.MZXH and b.JGID=:jgid and c.HZRQ IS NULL and b.MZLB = :mzlb and c.JZRQ<=:hzrq" +
//					" union all "+
//					"select a.BRXZ as BRXZ,a.QTYS as QTYS from MS_GHMX a,MS_GHRB c where c.JZRQ = a.JZRQ and c.CZGH = a.CZGH and a.JGID=:jgid and c.HZRQ IS NULL and a.MZLB = :mzlb and c.JZRQ<=:hzrq"+
//					" union all "+
//					"select a.BRXZ as BRXZ,(-1*a.QTYS) as QTYS from MS_GHMX a,MS_THMX b,MS_GHRB c where c.JZRQ = b.JZRQ and c.CZGH = b.CZGH and a.SBXH = b.SBXH and b.JGID=:jgid and c.HZRQ IS NULL and b.MZLB = :mzlb and c.JZRQ<=:hzrq" +
//					") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB";
//			Map<String, Object> parameters2 = new HashMap<String, Object>();
//			parameters2.put("jgid", jgid);
//			parameters2.put("mzlb",Long.parseLong(BSPHISUtil.getMZLB(jgid, dao) + ""));
//			parameters2.put("hzrq", cdate.getTime());
//			List<Map<String, Object>> ids_fkfs = dao.doSqlQuery(sql_fkfs,parameters2);
//			List<Map<String, Object>> ids_brxz = dao.doSqlQuery(sql_brxz,parameters2);
//			String  qtysFb="";
//			String jzjeSt="0.00";
//			if (ids_fkfs  != null && ids_fkfs .size() != 0) {
//				 for(int n=0;n<ids_fkfs.size();n++){
//						 qtysFb = qtysFb +ids_fkfs.get(n).get("FKMC")+ ":"
//								+ String.format("%1$.2f",ids_fkfs.get(n).get("FKJE"))
//								+ " ";
//				 }
//			}
//			if (ids_brxz  != null && ids_brxz .size() != 0) {
//				 for(int n=0;n<ids_brxz.size();n++){
//					 if(Integer.parseInt(ids_brxz.get(n).get("DBPB")+"")==0){
//						 jzjeSt= String.format("%1$.2f",parseDouble(jzjeSt) +parseDouble(ids_brxz.get(n).get("QTYS")+ ""));
//					 }else{
//						 qtysFb = qtysFb +ids_brxz.get(n).get("XZMC")+ ":"
//								+ String.format("%1$.2f",parseDouble(ids_brxz.get(n).get("QTYS")+ ""))
//								+ " ";
//					 }
//				 }
//				 qtysFb = qtysFb+" "+"记账 :"+jzjeSt+" ";
//			}
//			response.put("fkxxAmount", qtysFb);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ModelDataOperationException e1) {
			e1.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void doCheckout(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException,
			PersistentDataOperationException {
		Calendar cdate = Calendar.getInstance();
		cdate.setTime((Date) req.get("hzrq"));
		cdate.set(Calendar.MILLISECOND, 0);
		hzrq = cdate.getTime();
//		Date nowdate = new Date();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		Calendar startc = Calendar.getInstance();
		startc.setTime(hzrq);
		startc.set(Calendar.HOUR_OF_DAY, 0);
		startc.set(Calendar.MINUTE, 0);
		startc.set(Calendar.SECOND, 0);
		startc.set(Calendar.MILLISECOND, 0);
		Date ldt_begin = startc.getTime();
		startc.set(Calendar.HOUR_OF_DAY, 23);
		startc.set(Calendar.MINUTE, 59);
		startc.set(Calendar.SECOND, 59);
		startc.set(Calendar.MILLISECOND, 999);
		Date ldt_end = startc.getTime();
		try {
			ss.beginTransaction();
//			Map<String, Object> hzrbjzrqparameters = new HashMap<String, Object>();
//			hzrbjzrqparameters.put("JGID", jgid);
//			hzrbjzrqparameters.put("JZRQ", hzrq);
//			hzrbjzrqparameters.put("MZLB",
//					Long.parseLong(BSPHISUtil.getMZLB(jgid, dao) + ""));
//			String hzrbjzrqsql = "select CZGH as CZGH from MS_HZRB where JGID =:JGID and MZLB =:MZLB and JZRQ>:JZRQ";
//			List<Map<String, Object>> hzrbjzrqlist = dao.doQuery(hzrbjzrqsql,
//					hzrbjzrqparameters);
//			if (hzrbjzrqlist.size() > 0)// 判断在idt_jzrq后是否有新的结帐发生
//				return;
			Map<String, Object> hzrbhzrqparameters = new HashMap<String, Object>();
			hzrbhzrqparameters.put("JGID", jgid);
			hzrbhzrqparameters.put("ldt_begin", ldt_begin);
			hzrbhzrqparameters.put("ldt_end", ldt_end);
			hzrbhzrqparameters.put("MZLB",
					Long.parseLong(BSPHISUtil.getMZLB(jgid, dao) + ""));
			String hzrbhzrqql = "select CZGH as CZGH from MS_HZRB where JGID =:JGID and HZRQ >=:ldt_begin and HZRQ <=:ldt_end and MZLB =:MZLB";
			List<Map<String, Object>> hzrbhzrqlist = dao.doQuery(hzrbhzrqql,
					hzrbhzrqparameters);
			if (hzrbhzrqlist.size() > 0)// 判断在指定的汇总日期是否已有汇总结帐,如果已有则不能再结帐
				return;
//			Map<String, Object> ghrbjzrqparameters = new HashMap<String, Object>();
//			ghrbjzrqparameters.put("JGID", jgid);
//			ghrbjzrqparameters.put("JZRQ", hzrq);
//			ghrbjzrqparameters.put("MZLB",
//					Long.parseLong(BSPHISUtil.getMZLB(jgid, dao) + ""));
//			String ghrbjzrqsql = "select CZGH as CZGH from MS_GHRB where JGID =:JGID and MZLB =:MZLB and JZRQ>:JZRQ";
//			List<Map<String, Object>> ghrbjzrqlist = dao.doQuery(ghrbjzrqsql,
//					ghrbjzrqparameters);
//			if (ghrbjzrqlist.size() > 0)
//				return;
			Map<String, Object> ghrbhzrqparameters = new HashMap<String, Object>();
			ghrbhzrqparameters.put("JGID", jgid);
			ghrbhzrqparameters.put("ldt_begin", ldt_begin);
			ghrbhzrqparameters.put("ldt_end", ldt_end);
			ghrbhzrqparameters.put("MZLB",
					Long.parseLong(BSPHISUtil.getMZLB(jgid, dao) + ""));
			String ghrbhzrqql = "select CZGH as CZGH from MS_HZRB where JGID =:JGID and HZRQ >=:ldt_begin and HZRQ <=:ldt_end and MZLB =:MZLB";
			List<Map<String, Object>> ghrbhzrqlist = dao.doQuery(ghrbhzrqql,
					ghrbhzrqparameters);
			if (ghrbhzrqlist.size() > 0)
				return;
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("gl_jgid", jgid);
			parameters.put("hzrq", hzrq);
			parameters.put("MZLB",
					Long.parseLong(BSPHISUtil.getMZLB(jgid, dao) + ""));
			// 根据指定门诊类别填门诊信息中未汇总记录的汇总日期
			dao.doUpdate(
					"update MS_MZXX set HZRQ =:hzrq where JGID =:gl_jgid and MZLB =:MZLB and JZRQ<=:hzrq and HZRQ Is Null",
					parameters);
			// 根据指定门诊类别填作废发票中未汇总记录的汇总日期
			dao.doUpdate(
					"update MS_ZFFP set HZRQ =:hzrq where JGID =:gl_jgid and MZLB =:MZLB and JZRQ<=:hzrq and HZRQ Is Null",
					parameters);
			// 更新门诊收费日报中未汇总记录的汇总时间为取得的汇总日期
			dao.doUpdate(
					"update MS_HZRB set HZRQ =:hzrq where JGID =:gl_jgid and MZLB =:MZLB and JZRQ<=:hzrq and HZRQ Is Null",
					parameters);
			// 根据指定门诊类别填挂号信息中未汇总记录的汇总日期
			dao.doUpdate(
					"update MS_GHMX set HZRQ =:hzrq where JGID =:gl_jgid and MZLB =:MZLB and JZRQ<=:hzrq and HZRQ Is Null",
					parameters);
			// 根据指定门诊类别填退号明细中未汇总记录的汇总日期
			dao.doUpdate(
					"update MS_THMX set HZRQ =:hzrq where JGID =:gl_jgid and MZLB =:MZLB and JZRQ<=:hzrq and HZRQ Is Null",
					parameters);
			// 更新门诊收费日报中未汇总记录的汇总时间为取得的汇总日期
			dao.doUpdate(
					"update MS_GHRB set HZRQ =:hzrq where JGID =:gl_jgid and MZLB =:MZLB and JZRQ<=:hzrq and HZRQ Is Null",
					parameters);
			ss.getTransaction().commit();
			res.put("hzrq", BSHISUtil.toString(hzrq, "yyyy-MM-dd HH:mm:ss"));
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			ss.getTransaction().rollback();
		} catch (ModelDataOperationException e1) {
			e1.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void doQuerySQLList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hzhql = new StringBuffer();
		try {
			parameters.put("jgid", jgid);
			parameters.put("MZLB",
					Long.parseLong(BSPHISUtil.getMZLB(jgid, dao) + ""));
			if (req.containsKey("cnd")) {
				List<Object> listCND = (List<Object>) req.get("cnd");
				ExpressionProcessor exp = new ExpressionProcessor();
				String cnd = exp.toString(listCND);
				cnd = cnd.replaceAll("str", "to_char");
				hzhql.append(
						" SELECT distinct HZRQ as HZRQ from (SELECT to_char(HZRQ,'yyyy-mm-dd hh24:mi:ss') as HZRQ FROM MS_HZRB WHERE JGID =:jgid and ")
						.append("MZLB=:MZLB and ").append(cnd)
						.append(" group by HZRQ union all SELECT to_char(HZRQ,'yyyy-mm-dd hh24:mi:ss') as HZRQ FROM MS_GHRB WHERE JGID =:jgid and ")
						.append("MZLB=:MZLB and ").append(cnd)
						.append(" group by HZRQ) order by HZRQ desc");
			}
			List<Map<String, Object>> hzListSQL = dao.doSqlQuery(hzhql.toString(),
					parameters);
			res.put("body", hzListSQL);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ModelDataOperationException e1) {
			e1.printStackTrace();
		} catch (ExpException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doQuerySQLZYJSList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hzhql = new StringBuffer();
		try {
			parameters.put("jgid", jgid);
			if (req.containsKey("cnd")) {
				List<Object> listCND = (List<Object>) req.get("cnd");
				ExpressionProcessor exp = new ExpressionProcessor();
				String cnd = exp.toString(listCND);
				cnd = cnd.replaceAll("str", "to_char");
				hzhql.append(
						"SELECT to_char(HZRQ,'yyyy-mm-dd hh24:mi:ss') as HZRQ FROM ZY_JZHZ WHERE JGID =:jgid and ")
						.append(cnd)
						.append(" group by HZRQ order by HZRQ desc");
			}
			List<Map<String, Object>> hzListSQL = dao.doSqlQuery(hzhql.toString(),
					parameters);
			res.put("body", hzListSQL);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ExpException e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	public void doQuerySQLJCJSList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hzhql = new StringBuffer();
		try {
			parameters.put("jgid", jgid);
			if (req.containsKey("cnd")) {
				List<Object> listCND = (List<Object>) req.get("cnd");
				ExpressionProcessor exp = new ExpressionProcessor();
				String cnd = exp.toString(listCND);
				cnd = cnd.replaceAll("str", "to_char");
				hzhql.append(
						"SELECT to_char(HZRQ,'yyyy-mm-dd hh24:mi:ss') as HZRQ FROM JC_JZHZ WHERE JGID =:jgid and ")
						.append(cnd)
						.append(" group by HZRQ order by HZRQ desc");
			}
			List<Map<String, Object>> hzListSQL = dao.doSqlQuery(hzhql.toString(),
					parameters);
			res.put("body", hzListSQL);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ExpException e) {
			e.printStackTrace();
		}
	}

	public void doInquiry(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws PrintException {
		li.clear();
		UserRoleToken user = UserRoleToken.getCurrent();
		String userName = user.getUserName();// 用户名
		String jgName = user.getManageUnitName();// 用户的机构名称
		String jgid = user.getManageUnitId();// 用户的机构名称
		SimpleDateFormat sdftime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> fkfsparameters = new HashMap<String, Object>();
		try {
			parameters.put("JGID", jgid);
			parameters.put("MZLB",
					Long.parseLong(BSPHISUtil.getMZLB(jgid, dao) + ""));
			Calendar cdate = Calendar.getInstance();
			cdate.setTime((Date) req.get("hzrq"));
			cdate.set(Calendar.MILLISECOND, 0);
			parameters.put("HZRQ", cdate.getTime());
			fkfsparameters.put("SYLX", "1");
			fkfsparameters.put("ZFBZ", "0");
			String hzrb_sql = "select CZGH as YGDM,0 as GHJE,sum(ZJJE) as ZJJE,sum(XJJE) as XJJE," +
					" sum(QTYS) as QTYS,sum(HBWC) as HBWC,0 as GHRC,sum(FPZS) as FPZS,0 as THSL," +
					" sum(ZFZS) as ZFZS,sum(ZFFSHJ) as ZFFSHJ,sum(NHZFHJ) as NHZFHJ,sum(YBZFHJ) as YBZFHJ," +
					" sum(NHJZ) as NHJZ,sum(YBJZ) as YBJZ,sum(JZJEST) as JZJEST,(sum(YBJZ)+sum(ZFZF)) as JZZE," +
					" sum(ZFZF) as ZFZF,sum(WXHJ) as WXHJ,sum(ZFBHJ) as ZFBHJ,(sum(WXHJ)+sum(ZFBHJ)) as SMFHJ," +
					"sum(APPWXHJ) as APPWXHJ,sum(APPZFBHJ) as APPZFBHJ,(sum(APPWXHJ)+sum(APPZFBHJ)) as APPHJ,0 as GHYHHJ" +
					" from MS_HZRB where JGID =:JGID and MZLB =:MZLB" +
					" and HZRQ=:HZRQ group by CZGH";
			String ghrb_sql = "select CZGH as YGDM,sum(ZJJE-YZJM-YHJE) as GHJE,0 as ZJJE,sum(XJJE) as XJJE," +
					" sum(QTYS) as QTYS,sum(HBWC) as HBWC,sum(FPZS) as GHRC,0 as FPZS," +
					" sum(THSL) as THSL,0 as ZFZS,0 as ZFFSHJ,0 as NHZFHJ,0 as YBZFHJ," +
					" 0 as NHJZ,sum(QTYS)  as YBJZ,0 as JZJEST,(sum(QTYS)+sum(ZHJE))  as JZZE,sum(ZHJE) as ZFZF,sum(WXHJ) as WXHJ,sum(ZFBHJ) as ZFBHJ,(sum(WXHJ)+sum(ZFBHJ)) as SMFHJ,0 as APPWXHJ,0 as APPZFBHJ,0 as APPHJ,sum(YHJE) as GHYHHJ " +
					" from MS_GHRB where JGID =:JGID and MZLB =:MZLB" +
					" and HZRQ=:HZRQ group by CZGH";
			String hzghrb_sql = "select YGDM as YGDM,sum(GHJE) as GHJE,sum(ZJJE) as ZJJE,sum(XJJE) as XJJE," +
					" sum(QTYS) as QTYS,sum(HBWC) as HBWC,sum(GHRC) as GHRC,sum(FPZS) as FPZS," +
					" sum(THSL) as THSL,sum(ZFZS) as ZFZS,sum(ZFFSHJ) as ZFFSHJ,sum(NHZFHJ) as NHZFHJ,sum(YBZFHJ) as YBZFHJ," +
					" sum(NHJZ) as NHJZ,sum(YBJZ) as YBJZ,sum(JZJEST) as JZJEST,sum(JZZE) as JZZE," +
					" sum(ZFZF) as ZFZF,sum(WXHJ) as WXHJ,sum(ZFBHJ) as ZFBHJ,(sum(WXHJ)+sum(ZFBHJ)) as SMFHJ," +
					"sum(APPWXHJ) as APPWXHJ,sum(APPZFBHJ) as APPZFBHJ,(sum(APPWXHJ)+sum(APPZFBHJ)) as APPHJ,sum(GHYHHJ) as GHYHHJ from ("
					+ hzrb_sql + " union all " + ghrb_sql + ") group by YGDM";
			List<Map<String, Object>> hzghrb_list = dao.doSqlQuery(hzghrb_sql,
					parameters);// 收费日报数据
			// 收费现金金额和货币误差赋值
			for (int i = 0; i < hzghrb_list.size(); i++) {
				Map<String, Object> xj_hz_map = new HashMap<String, Object>();
				hzghrb_list.get(i).put(
						"CZGH",
						DictionaryController
								.instance()
								.get("phis.dictionary.doctor")
								.getText(
										hzghrb_list.get(i).get("YGDM")
												.toString()));
				if (xj_hz_map != null && xj_hz_map.size() > 0) {
					hzghrb_list.get(i).putAll(xj_hz_map);// 接着放到List 接着对比
				}
			}
			int ghcount = 0;// 人次合计
			Double ghAmount = 0.0;// 挂号金额合计
			int thcount = 0;// 退号合计
			int sfcount = 0;// 发票张数合计
			Double sfamount = 0.0;// 收费金额合计
			int zfcount = 0;// 发票作废合计
			Double totals = 0.0;// 总的合计
			Double xjAmount = 0.0;// 现金合计
			Double qtysAmount = 0.0;// 其他应收
			Double hbwcAmount = 0.0;// 货币误差	
            double ZFFSHJAmount=0.0;//自费金额合计
            double NHZFHJAmount=0.0;//农合自费金额合计
            double YBZFHJAmount=0.0;//医保自费金额合计
            double XJJEAmount=0.0;//现金金额合计
            double ZFZFAmount=0.0;//账户支付合计
            double NHJZAmount=0.0;//农合记账合计
            double YBJZAmount=0.0;//医保记账
            double JZZEAmount=0.0;//记账总额合计
            double WXAmount=0.0;//微信金额合计
            double ZFBAmount=0.0;//支付宝金额合计
            double APPWXAmount=0.0;//app微信金额合计
            double APPZFBAmount=0.0;//app支付宝金额合计
            double GHYHHJAmount=0.0;//挂号优惠总额合计 zhaojian 2019-05-12
			for (int j = 0; j < hzghrb_list.size(); j++) {
				Map<String, Object> m = new HashMap<String, Object>();
				ghcount += parseInt(hzghrb_list.get(j).get("GHRC"));
				thcount += parseInt(hzghrb_list.get(j).get("THSL"));
				ghAmount += parseDouble(hzghrb_list.get(j).get("GHJE"));
				sfcount += parseInt(hzghrb_list.get(j).get("FPZS"));
				zfcount += parseInt(hzghrb_list.get(j).get("ZFZS"));
				sfamount += parseDouble(hzghrb_list.get(j).get("ZJJE"));
				totals += BSPHISUtil.getDouble(
						parseDouble(hzghrb_list.get(j).get("ZJJE"))
								+ parseDouble(hzghrb_list.get(j).get("GHJE")),
						2);
				xjAmount += parseDouble(hzghrb_list.get(j).get("XJJE"));
				qtysAmount += parseDouble(hzghrb_list.get(j).get("QTYS"));
				hbwcAmount += parseDouble(hzghrb_list.get(j).get("HBWC"));
				
				ZFFSHJAmount += parseDouble(hzghrb_list.get(j).get("ZFFSHJ"));
				NHZFHJAmount += parseDouble(hzghrb_list.get(j).get("NHZFHJ"));
				YBZFHJAmount += parseDouble(hzghrb_list.get(j).get("YBZFHJ"));
				XJJEAmount += parseDouble(hzghrb_list.get(j).get("XJJE"));
				ZFZFAmount += parseDouble(hzghrb_list.get(j).get("ZFZF"));
				NHJZAmount += parseDouble(hzghrb_list.get(j).get("NHJZ"));
				YBJZAmount += parseDouble(hzghrb_list.get(j).get("YBJZ"));
				JZZEAmount += parseDouble(hzghrb_list.get(j).get("JZZE"));
				WXAmount += parseDouble(hzghrb_list.get(j).get("WXHJ"));//微信
				ZFBAmount += parseDouble(hzghrb_list.get(j).get("ZFBHJ"));//支付宝
				APPWXAmount += parseDouble(hzghrb_list.get(j).get("APPWXHJ"));//app微信
				APPZFBAmount += parseDouble(hzghrb_list.get(j).get("APPZFBHJ"));//app支付宝
				GHYHHJAmount = BSHISUtil.doublesum(GHYHHJAmount,parseDouble(hzghrb_list.get(j).get("GHYHHJ")));//挂号优惠总额合计 zhaojian 2019-05-12
				
				m.put("ZFFSHJ", hzghrb_list.get(j).get("ZFFSHJ")+"");
				//优惠总额合计 zhaojian 2019-05-12
				m.put("GHYHHJ", (hzghrb_list.get(j).get("GHYHHJ")+"").equals("0")?"0":("-"+(hzghrb_list.get(j).get("GHYHHJ")+"")));
				m.put("NHZFHJ", hzghrb_list.get(j).get("NHZFHJ")+"");
				m.put("YBZFHJ", hzghrb_list.get(j).get("YBZFHJ")+"");
				m.put("ZFZF", hzghrb_list.get(j).get("ZFZF")+"");
				m.put("NHJZ", hzghrb_list.get(j).get("NHJZ")+"");
				m.put("YBJZ", hzghrb_list.get(j).get("YBJZ")+"");
				m.put("JZZE", hzghrb_list.get(j).get("JZZE")+"");
				m.put("GHJE",String.format("%1$.2f", hzghrb_list.get(j).get("GHJE")));
				m.put("GHRC", hzghrb_list.get(j).get("GHRC") + "");
				m.put("THSL", hzghrb_list.get(j).get("THSL") + "");
				m.put("CZGH", hzghrb_list.get(j).get("CZGH") + "");
				m.put("ZJJE",String.format("%1$.2f", hzghrb_list.get(j).get("ZJJE")));
				m.put("FPZS", hzghrb_list.get(j).get("FPZS") + "");
				m.put("ZFZS", hzghrb_list.get(j).get("ZFZS") + "");
				m.put("QTYS",String.format("%1$.2f", hzghrb_list.get(j).get("QTYS")));
				m.put("HBWC",String.format("%1$.2f", hzghrb_list.get(j).get("HBWC")));

				m.put("XJJE",String.format("%1$.2f", parseDouble(hzghrb_list.get(j).get("XJJE"))));
				m.put("TOTALAMOUNT", String.format("%1$.2f", BSPHISUtil
						.getDouble(parseDouble(hzghrb_list.get(j).get("ZJJE"))
								+ parseDouble(hzghrb_list.get(j).get("GHJE")),
								2)));
				m.put("WXHJ", hzghrb_list.get(j).get("WXHJ")+"");//微信
				m.put("ZFBHJ", hzghrb_list.get(j).get("ZFBHJ")+"");//支付宝
				m.put("SMFHJ", hzghrb_list.get(j).get("SMFHJ")+"");//扫码付
				m.put("APPWXHJ", hzghrb_list.get(j).get("APPWXHJ")+"");//app微信
				m.put("APPZFBHJ", hzghrb_list.get(j).get("APPZFBHJ")+"");//app支付宝
				m.put("APPHJ", hzghrb_list.get(j).get("APPHJ")+"");//app
//				String sql_fkfs = "select c.FKFS as FKFS,sum(c.FKJE) as FKJE,d.FKMC as FKMC from ("+
//						"select a.FKFS as FKFS,a.FKJE as FKJE from MS_FKXX a,MS_MZXX b where a.MZXH = b.MZXH and b.JGID = :jgid and b.HZRQ = :hzrq and b.MZLB = :mzlb and b.CZGH = :czgh"+
//						" union all "+
//						"select a.FKFS as FKFS,(-1*a.FKJE) as FKJE from MS_FKXX a,MS_ZFFP b where a.MZXH = b.MZXH and b.JGID = :jgid and b.HZRQ = :hzrq and b.MZLB = :mzlb and b.CZGH = :czgh"+
//						" union all "+
//						"select a.FKFS as FKFS,a.FKJE as FKJE from MS_GH_FKXX a,MS_GHMX b where a.SBXH = b.SBXH and b.JGID = :jgid and b.HZRQ = :hzrq and b.MZLB = :mzlb and b.CZGH = :czgh"+
//						" union all "+
//						"select a.FKFS as FKFS,(-1*a.FKJE) as FKJE from MS_GH_FKXX a,MS_THMX b where a.SBXH = b.SBXH and b.JGID = :jgid and b.HZRQ = :hzrq and b.MZLB = :mzlb and b.CZGH = :czgh"+
//						") c left outer join GY_FKFS d on c.FKFS = d.FKFS group by c.FKFS,d.FKMC order by c.FKFS";
//				String sql_brxz = "select sum(c.QTYS) as QTYS,c.BRXZ as BRXZ,d.XZMC as XZMC,nvl(d.DBPB,0) as DBPB from ("+
//						"select a.BRXZ as BRXZ,a.QTYS as QTYS from MS_MZXX a where a.JGID=:jgid and a.HZRQ = :hzrq and a.MZLB = :mzlb and a.CZGH = :czgh"+
//						" union all "+
//						"select a.BRXZ as BRXZ,(-1*a.QTYS) as QTYS from MS_MZXX a,MS_ZFFP b where a.MZXH = b.MZXH and b.JGID=:jgid and b.HZRQ = :hzrq and b.MZLB = :mzlb and b.CZGH = :czgh" +
//						" union all "+
//						"select a.BRXZ as BRXZ,a.QTYS as QTYS from MS_GHMX a where a.JGID=:jgid and a.HZRQ = :hzrq and a.MZLB = :mzlb and a.CZGH = :czgh"+
//						" union all "+
//						"select a.BRXZ as BRXZ,(-1*a.QTYS) as QTYS from MS_GHMX a,MS_THMX b where a.SBXH = b.SBXH and b.JGID=:jgid and b.HZRQ = :hzrq and b.MZLB = :mzlb and b.CZGH = :czgh" +
//						") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB";
//				Map<String, Object> parameters2 = new HashMap<String, Object>();
//				parameters2.put("jgid", jgid);
//				parameters2.put("mzlb",Long.parseLong(BSPHISUtil.getMZLB(jgid, dao) + ""));
//				parameters2.put("czgh",hzghrb_list.get(j).get("YGDM") + "");
//				parameters2.put("hzrq", cdate.getTime());
//				List<Map<String, Object>> ids_fkfs = dao.doSqlQuery(sql_fkfs,parameters2);
//				List<Map<String, Object>> ids_brxz = dao.doSqlQuery(sql_brxz,parameters2);
//				String  qtysFb="";
//				String jzjeSt="0.00";
//				if (ids_fkfs  != null && ids_fkfs .size() != 0) {
//					 for(int n=0;n<ids_fkfs.size();n++){
//							 qtysFb = qtysFb +ids_fkfs.get(n).get("FKMC")+ ":"
//									+ String.format("%1$.2f",ids_fkfs.get(n).get("FKJE"))
//									+ " ";
//					 }
//				}
//				if (ids_brxz  != null && ids_brxz .size() != 0) {
//					 for(int n=0;n<ids_brxz.size();n++){
//						 if(Integer.parseInt(ids_brxz.get(n).get("DBPB")+"")==0){
//							 jzjeSt= String.format("%1$.2f",parseDouble(jzjeSt) +parseDouble(ids_brxz.get(n).get("QTYS")+ ""));
//						 }else{
//							 qtysFb = qtysFb +ids_brxz.get(n).get("XZMC")+ ":"
//									+ String.format("%1$.2f",parseDouble(ids_brxz.get(n).get("QTYS")+ ""))
//									+ " ";
//						 }
//					 }
//					 qtysFb = qtysFb+" "+"记账 :"+jzjeSt+" ";
//				}
//				m.put("fkxx", qtysFb);
			    li.add(m);
			}
			Map<String, Object> hzrq = new HashMap<String, Object>();
			hzrq.put("JGID", jgid);
			// 查询上次结账时间
			Map<String, Object> hzrqmapsf = dao
					.doLoad("select max(HZRQ) as HZRQ from  MS_HZRB where JGID=:JGID and str(HZRQ,'YYYY-MM-DD HH24:MI:SS')<'"
							+ sdftime.format(req.get("hzrq")) + "'", hzrq);
			Map<String, Object> hzrqmapgh = dao
					.doLoad("select max(HZRQ) as HZRQ from  MS_GHRB where JGID=:JGID and str(HZRQ,'YYYY-MM-DD HH24:MI:SS')<'"
							+ sdftime.format(req.get("hzrq")) + "'", hzrq);
			if (hzrqmapsf.get("HZRQ") != null && hzrqmapgh.get("HZRQ") != null) {
				Date hzrqsf = sdftime.parse(hzrqmapsf.get("HZRQ") + "");
				Date hzrqgh = sdftime.parse(hzrqmapgh.get("HZRQ") + "");
				if (hzrqsf.getTime() > hzrqgh.getTime()) {
					res.put("startSummaryDate",
							BSHISUtil.toString(hzrqsf, "yyyy-MM-dd HH:mm:ss"));
				} else {
					res.put("startSummaryDate",
							BSHISUtil.toString(hzrqgh, "yyyy-MM-dd HH:mm:ss"));
				}
			} else if (hzrqmapsf.get("HZRQ") != null) {
				Date hzrqsf = sdftime.parse(hzrqmapsf.get("HZRQ") + "");
				res.put("startSummaryDate",
						BSHISUtil.toString(hzrqsf, "yyyy-MM-dd HH:mm:ss"));
			} else if (hzrqmapgh.get("HZRQ") != null) {
				Date hzrqgh = sdftime.parse(hzrqmapgh.get("HZRQ") + "");
				res.put("startSummaryDate",
						BSHISUtil.toString(hzrqgh, "yyyy-MM-dd HH:mm:ss"));
			} else {

				Map<String, Object> sfrqorghrq = new HashMap<String, Object>();
				sfrqorghrq.put("JGID", jgid);
				// 查询上次结账时间
				Map<String, Object> jzrqmapsf = dao
						.doLoad("select min(JZRQ) as JZRQ from  MS_HZRB where JGID=:JGID",
								sfrqorghrq);
				Map<String, Object> jzrqmapgh = dao
						.doLoad("select min(JZRQ) as JZRQ from  MS_GHRB where JGID=:JGID",
								sfrqorghrq);
				if (jzrqmapsf.get("JZRQ") != null
						&& jzrqmapgh.get("JZRQ") != null) {
					Date jzrqsf = sdftime.parse(jzrqmapsf.get("JZRQ") + "");
					Date jzrqgh = sdftime.parse(jzrqmapgh.get("JZRQ") + "");
					if (jzrqsf.getTime() < jzrqgh.getTime()) {
						res.put("startSummaryDate", BSHISUtil.toString(jzrqsf,
								"yyyy-MM-dd HH:mm:ss"));
					} else {
						res.put("startSummaryDate", BSHISUtil.toString(jzrqgh,
								"yyyy-MM-dd HH:mm:ss"));
					}
				} else if (jzrqmapsf.get("JZRQ") != null) {
					Date jzrqsf = sdftime.parse(jzrqmapsf.get("JZRQ") + "");
					res.put("startSummaryDate",
							BSHISUtil.toString(jzrqsf, "yyyy-MM-dd HH:mm:ss"));
				} else if (jzrqmapgh.get("JZRQ") != null) {
					Date jzrqgh = sdftime.parse(jzrqmapgh.get("JZRQ") + "");
					res.put("startSummaryDate",
							BSHISUtil.toString(jzrqgh, "yyyy-MM-dd HH:mm:ss"));
				}
			}
			res.put("Reviewedby", "");
			res.put("preparedby", jgName);
			res.put("summaryDate", sdftime.format(req.get("hzrq")));
			res.put("Lister", userName);
			res.put("DateTabling", BSHISUtil.getDate());
			res.put("totals", String.format("%1$.2f", totals));
			res.put("ghcount", ghcount + "");
			res.put("thcount", thcount + "");
			res.put("ghAmount", String.format("%1$.2f", ghAmount));
			res.put("sfcount", sfcount + "");
			res.put("zfcount", zfcount + "");
			res.put("sfamount", String.format("%1$.2f", sfamount));
			res.put("xjAmount", String.format("%1$.2f", xjAmount));
			res.put("qtysAmount", String.format("%1$.2f", qtysAmount));
			res.put("hbwcAmount", String.format("%1$.2f", hbwcAmount));
			res.put("ZFFSHJAmount", String.format("%1$.2f", ZFFSHJAmount));
			//挂号优惠总额合计 zhaojian 2019-05-12
			res.put("GHYHHJAmount", String.format("%1$.2f", GHYHHJAmount).equals("0.00")?"0":("-"+String.format("%1$.2f", GHYHHJAmount)));
			res.put("NHZFHJAmount", String.format("%1$.2f", NHZFHJAmount));
			res.put("YBZFHJAmount", String.format("%1$.2f", YBZFHJAmount));
			res.put("XJJEAmount", String.format("%1$.2f", XJJEAmount));
			res.put("NHJZAmount", String.format("%1$.2f", NHJZAmount));
			res.put("ZFZFAmount", String.format("%1$.2f", ZFZFAmount));
			res.put("JZZEAmount", String.format("%1$.2f", YBJZAmount+ZFZFAmount+NHJZAmount));
			res.put("YBJZAmount", String.format("%1$.2f", YBJZAmount));
			res.put("WXAmount", String.format("%1$.2f", WXAmount));//微信
			res.put("ZFBAmount", String.format("%1$.2f", ZFBAmount));//支付宝
			res.put("SMFAmount", String.format("%1$.2f", WXAmount+ZFBAmount));//扫码付
			res.put("APPWXAmount", String.format("%1$.2f", APPWXAmount));//app微信
			res.put("APPZFBAmount", String.format("%1$.2f", APPZFBAmount));//app支付宝
			res.put("APPAmount", String.format("%1$.2f", APPWXAmount+APPZFBAmount));//app
//			String sql_fkfs = "select c.FKFS as FKFS,sum(c.FKJE) as FKJE,d.FKMC as FKMC from ("+
//					"select a.FKFS as FKFS,a.FKJE as FKJE from MS_FKXX a,MS_MZXX b,MS_HZRB c where c.JZRQ = b.JZRQ and c.CZGH = b.CZGH and a.MZXH = b.MZXH and b.JGID = :jgid and c.HZRQ = :hzrq and b.MZLB = :mzlb"+
//					" union all "+
//					"select a.FKFS as FKFS,(-1*a.FKJE) as FKJE from MS_FKXX a,MS_ZFFP b,MS_HZRB c where c.JZRQ = b.JZRQ and c.CZGH = b.CZGH and a.MZXH = b.MZXH and b.JGID = :jgid and c.HZRQ = :hzrq and b.MZLB = :mzlb"+
//					" union all "+
//					"select a.FKFS as FKFS,a.FKJE as FKJE from MS_GH_FKXX a,MS_GHMX b,MS_GHRB c where c.JZRQ = b.JZRQ and c.CZGH = b.CZGH and a.SBXH = b.SBXH and b.JGID = :jgid and c.HZRQ = :hzrq and b.MZLB = :mzlb"+
//					" union all "+
//					"select a.FKFS as FKFS,(-1*a.FKJE) as FKJE from MS_GH_FKXX a,MS_THMX b,MS_GHRB c where c.JZRQ = b.JZRQ and c.CZGH = b.CZGH and a.SBXH = b.SBXH and b.JGID = :jgid and c.HZRQ = :hzrq and b.MZLB = :mzlb"+
//					") c left outer join GY_FKFS d on c.FKFS = d.FKFS group by c.FKFS,d.FKMC order by c.FKFS";
//			String sql_brxz = "select sum(c.QTYS) as QTYS,c.BRXZ as BRXZ,d.XZMC as XZMC,nvl(d.DBPB,0) as DBPB from ("+
//					"select a.BRXZ as BRXZ,a.QTYS as QTYS from MS_MZXX a,MS_HZRB c where c.JZRQ = a.JZRQ and c.CZGH = a.CZGH and a.JGID=:jgid and c.HZRQ = :hzrq and a.MZLB = :mzlb"+
//					" union all "+
//					"select a.BRXZ as BRXZ,(-1*a.QTYS) as QTYS from MS_MZXX a,MS_ZFFP b,MS_HZRB c where c.JZRQ = b.JZRQ and c.CZGH = b.CZGH and a.MZXH = b.MZXH and b.JGID=:jgid and c.HZRQ = :hzrq and b.MZLB = :mzlb" +
//					" union all "+
//					"select a.BRXZ as BRXZ,a.QTYS as QTYS from MS_GHMX a,MS_GHRB c where c.JZRQ = a.JZRQ and c.CZGH = a.CZGH and a.JGID=:jgid and c.HZRQ = :hzrq and a.MZLB = :mzlb"+
//					" union all "+
//					"select a.BRXZ as BRXZ,(-1*a.QTYS) as QTYS from MS_GHMX a,MS_THMX b,MS_GHRB c where c.JZRQ = b.JZRQ and c.CZGH = b.CZGH and a.SBXH = b.SBXH and b.JGID=:jgid and c.HZRQ = :hzrq and b.MZLB = :mzlb" +
//					") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB";
//			Map<String, Object> parameters2 = new HashMap<String, Object>();
//			parameters2.put("jgid", jgid);
//			parameters2.put("mzlb",Long.parseLong(BSPHISUtil.getMZLB(jgid, dao) + ""));
//			parameters2.put("hzrq", cdate.getTime());
//			List<Map<String, Object>> ids_fkfs = dao.doSqlQuery(sql_fkfs,parameters2);
//			List<Map<String, Object>> ids_brxz = dao.doSqlQuery(sql_brxz,parameters2);
//			String  qtysFb="";
//			String jzjeSt="0.00";
//			if (ids_fkfs  != null && ids_fkfs .size() != 0) {
//				 for(int n=0;n<ids_fkfs.size();n++){
//						 qtysFb = qtysFb +ids_fkfs.get(n).get("FKMC")+ ":"
//								+ String.format("%1$.2f",ids_fkfs.get(n).get("FKJE"))
//								+ " ";
//				 }
//			}
//			if (ids_brxz  != null && ids_brxz .size() != 0) {
//				 for(int n=0;n<ids_brxz.size();n++){
//					 if(Integer.parseInt(ids_brxz.get(n).get("DBPB")+"")==0){
//						 jzjeSt= String.format("%1$.2f",parseDouble(jzjeSt) +parseDouble(ids_brxz.get(n).get("QTYS")+ ""));
//					 }else{
//						 qtysFb = qtysFb +ids_brxz.get(n).get("XZMC")+ ":"
//								+ String.format("%1$.2f",parseDouble(ids_brxz.get(n).get("QTYS")+ ""))
//								+ " ";
//					 }
//				 }
//				 qtysFb = qtysFb+" "+"记账 :"+jzjeSt+" ";
//			}
//			res.put("fkxxAmount", qtysFb);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ModelDataOperationException e1) {
			e1.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
	}

	public static ChargesProduce getInstance() {
		if (cck == null) {
			cck = new ChargesProduce();
		}
		return cck;
	}

	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	public int parseInt(Object o) {
		if (o == null) {
			return new Integer(0);
		}
		return Integer.parseInt(o + "");
	}

}
