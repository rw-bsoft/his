package phis.prints.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils.Null;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class ChargesSummarySearchFile implements IHandler {
	List<Map<String, Object>> li = new ArrayList<Map<String, Object>>();

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
		String ksrqstr = String.valueOf(request.get("ksrq")).substring(0, 10);
		String jsrqstr = String.valueOf(request.get("jsrq")).substring(0, 10);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgName = user.getManageUnit().getName();
		String jgid = user.getManageUnit().getId();
		String userName = user.getUserName();// 用户名
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> fkfsparameters = new HashMap<String, Object>();
		double sfxjje = 0.0;// 收费现金金额
		double sfhbwc = 0.0;// 收费货币误差
		double ghxjje = 0.0;// 挂号现金金额
		double ghhbwc = 0.0;// 挂号货币误差
		// String fkxx="";
		// String fkxxAmount="";
		try {
			parameters.put("JGID", jgid);
			parameters.put("MZLB",
					Long.parseLong(BSPHISUtil.getMZLB(jgid, dao) + ""));
			parameters.put("KSRQ", sdf.parseObject(ksrqstr + " 00:00:00"));
			parameters.put("JSRQ", sdf.parseObject(jsrqstr + " 23:59:59"));
			fkfsparameters.put("SYLX", "1");
			fkfsparameters.put("ZFBZ", "0");
			//zhaojian 2019-05-12 增加挂号优惠金额字段GHYHHJ
			String hzrb_sql = "select CZGH as YGDM,0 as GHJE,sum(ZJJE) as ZJJE,sum(XJJE) as XJJE,sum(QTYS) as QTYS,sum(HBWC) as HBWC,0 as GHRC,sum(FPZS) as FPZS,0 as THSL,sum(ZFZS) as ZFZS,sum(ZFZF) as ZFZF,0 as YZJM,0 as GHYHHJ from MS_HZRB where JGID =:JGID and MZLB =:MZLB and HZRQ >=:KSRQ and HZRQ <=:JSRQ group by CZGH";
			String ghrb_sql = "select CZGH as YGDM,sum(ZJJE) as GHJE,0 as ZJJE,sum(XJJE) as XJJE,sum(QTYS) as QTYS,sum(HBWC) as HBWC,sum(FPZS) as GHRC,0 as FPZS,sum(THSL) as THSL,0 as ZFZS,sum(ZHJE) as ZFZF,sum(YZJM) as YZJM,sum(YHJE) as GHYHHJ from MS_GHRB where JGID =:JGID and MZLB =:MZLB and HZRQ >=:KSRQ and HZRQ <=:JSRQ  group by CZGH";
			String hzghrb_sql = "select YGDM as YGDM,sum(GHJE) as GHJE,sum(ZJJE) as ZJJE,sum(XJJE) as XJJE,sum(QTYS) as QTYS,sum(HBWC) as HBWC,sum(GHRC) as GHRC,sum(FPZS) as FPZS,sum(THSL) as THSL,sum(ZFZS) as ZFZS,sum(ZFZF) as ZFZF,sum(YZJM) as YZJM,sum(GHYHHJ) as GHYHHJ from ("
					+ hzrb_sql + " union all " + ghrb_sql + ") group by YGDM";
			List<Map<String, Object>> hzghrb_list = dao.doSqlQuery(hzghrb_sql,
					parameters);// 收费日报数据
			// String sfrb_fkmx_sql =
			// "select MS_HZRB.CZGH as YGDM,MS_SFRB_FKMX.FKFS as FKFS,sum(MS_SFRB_FKMX.FKJE) as FKJE from MS_SFRB_FKMX MS_SFRB_FKMX,MS_HZRB MS_HZRB where MS_HZRB.CZGH = MS_SFRB_FKMX.CZGH and MS_HZRB.JZRQ = MS_SFRB_FKMX.JZRQ and MS_HZRB.JGID =:JGID and MS_HZRB.MZLB =:MZLB and MS_HZRB.HZRQ >=:KSRQ and MS_HZRB.HZRQ <=:JSRQ group by MS_HZRB.CZGH,MS_SFRB_FKMX.FKFS";
			// String ghrb_fkmx_sql =
			// "select MS_GHRB.CZGH as YGDM,MS_GHRB_FKMX.FKFS as FKFS,sum(MS_GHRB_FKMX.FKJE) as FKJE from MS_GHRB_FKMX MS_GHRB_FKMX,MS_GHRB MS_GHRB where MS_GHRB.CZGH = MS_GHRB_FKMX.CZGH and MS_GHRB.JZRQ = MS_GHRB_FKMX.JZRQ and MS_GHRB.JGID =:JGID and MS_GHRB.MZLB =:MZLB and MS_GHRB.HZRQ >=:KSRQ and MS_GHRB.HZRQ <=:JSRQ group by MS_GHRB.CZGH,MS_GHRB_FKMX.FKFS";
			// String fkfs_sql =
			// "SELECT FKFS as FKFS,FKMC as FKMC,FKLB as FKLB FROM GY_FKFS WHERE SYLX =:SYLX AND ZFBZ =:ZFBZ";
			// List<Map<String, Object>> hzrb_list = dao.doQuery(hzrb_sql,
			// parameters);// 收费日报数据
			// List<Map<String, Object>> ghrb_list = dao.doQuery(ghrb_sql,
			// parameters);// 挂号日报数据
			// List<Map<String, Object>> sfrb_fkmx_list = dao.doQuery(
			// sfrb_fkmx_sql, parameters);// 已结帐未汇总收费付款数据
			// List<Map<String, Object>> ghrb_fkmx_list = dao.doQuery(
			// ghrb_fkmx_sql, parameters);// 已结帐未汇总挂号付款数据
			// List<Map<String, Object>> fkfs_list = dao.doQuery(fkfs_sql,
			// fkfsparameters);// 付款方式数据
			// 收费现金金额和货币误差赋值

			// //性质不为“0”时的按收费性质统计的QTYS
			//
			// List<Map<String, Object>> map_xztj = new ArrayList<Map<String,
			// Object>>();
			// StringBuffer sql_xztj = new StringBuffer();
			// sql_xztj.append(" select c.XZDM as XZDM,d.XZMC as XZMC,c.QTYS as QTYS from (select  b.BRXZ as XZDM,sum(a.QTYS) as QTYS ");
			// sql_xztj.append(" from MS_MZXX a left join GY_BRXZ b  on a.brxz = b.brxz ");
			// sql_xztj.append(" where a.JGID=:JGID and a.HZRQ>=:KSRQ and a.HZRQ <=:JSRQ and a.MZLB =:MZLB and b.DBPB!='0' ");
			// sql_xztj.append("  group by  b.BRXZ) c left join GY_BRXZ d on c.XZDM=d.brxz ");
			// map_xztj = dao.doSqlQuery(sql_xztj.toString(), parameters);
			// if (map_xztj != null && map_xztj.size() != 0) {
			// for(int i=0;i<map_xztj.size();i++){
			// fkxx = fkxx +map_xztj.get(i).get("XZMC")+ ":"
			// + String.format("%1$.2f",map_xztj.get(i).get("QTYS"))
			// + " ";
			// fkxxAmount = fkxxAmount +map_xztj.get(i).get("XZMC")+ ":"
			// + String.format("%1$.2f",map_xztj.get(i).get("QTYS"))
			// + " ";
			// }
			// }
			// //性质为“0”时的统计QTYS
			//
			// List<Map<String, Object>> map_qtys = new ArrayList<Map<String,
			// Object>>();
			// StringBuffer sql_qtys = new StringBuffer();
			// sql_qtys.append(" select a.CZGH,b.DBPB as DBPB,sum(a.QTYS) as QTYS ");
			// sql_qtys.append("from MS_MZXX a left join GY_BRXZ b on a.brxz=b.brxz ");
			// sql_qtys.append(" where a.JGID=:JGID and a.HZRQ>=:KSRQ and a.HZRQ <=:JSRQ and a.MZLB =:MZLB and b.DBPB='0' ");
			// sql_qtys.append(" group by b.DBPB , a.CZGH ");
			// map_qtys = dao.doSqlQuery(sql_qtys.toString(), parameters);
			// if (map_qtys != null && map_qtys.size() != 0) {
			// fkxx = fkxx + "记账 :"
			// + String.format("%1$.2f",map_qtys.get(0).get("QTYS"))
			// + " ";
			// fkxxAmount = fkxxAmount + "记账 :"
			// + String.format("%1$.2f",map_qtys.get(0).get("QTYS"))
			// + " ";
			// }

			for (int i = 0; i < hzghrb_list.size(); i++) {
				Map<String, Object> xj_hz_map = new HashMap<String, Object>();
				// for (int j = 0; j < sfrb_fkmx_list.size(); j++) {
				// if (hzrb_list.get(i).get("YGDM")
				// .equals(sfrb_fkmx_list.get(j).get("YGDM"))) {//
				// 如果相等就放到xj_hz_map
				// for (int k = 0; k < fkfs_list.size(); k++) {
				// if (sfrb_fkmx_list
				// .get(j)
				// .get("FKFS")
				// .toString()
				// .equals(fkfs_list.get(k).get("FKFS")
				// .toString())) {
				// if ("1".equals(fkfs_list.get(k).get("FKLB"))) {
				// sfxjje = parseDouble(sfrb_fkmx_list.get(j)
				// .get("FKJE"));
				// xj_hz_map.put("XJJE", sfxjje);
				//
				// } else if("7".equals(fkfs_list.get(k).get("FKLB"))){
				// sfxjje = parseDouble(sfrb_fkmx_list.get(j)
				// .get("FKJE"));
				// // xj_hz_map.put("SMKJE", sfxjje);
				// } else if("2".equals(fkfs_list.get(k).get("FKLB"))){
				// sfxjje = parseDouble(sfrb_fkmx_list.get(j)
				// .get("FKJE"));
				// // xj_hz_map.put("ZPJE", sfxjje);
				// }else {
				// sfhbwc = parseDouble(sfrb_fkmx_list.get(j)
				// .get("FKJE"));
				// xj_hz_map.put("HBWC", sfhbwc);
				//
				// }
				// }
				// }
				// }
				// }
				hzghrb_list.get(i).put(
						"CZGH",
						DictionaryController
								.instance()
								.getDic("phis.dictionary.doctor")
								.getText(
										hzghrb_list.get(i).get("YGDM")
												.toString()));
				hzghrb_list.get(i).putAll(xj_hz_map);// 接着放到List 接着对比
			}
			// 挂号现金金额和货币误差赋值
			// for (int i = 0; i < ghrb_list.size(); i++) {
			// Map<String, Object> xj_gh_map = new HashMap<String, Object>();
			// for (int j = 0; j < ghrb_fkmx_list.size(); j++) {
			// if (ghrb_list.get(i).get("YGDM")
			// .equals(ghrb_fkmx_list.get(j).get("YGDM"))) {// 如果相等就放到xj_hz_map
			// for (int k = 0; k < fkfs_list.size(); k++) {
			// if (ghrb_fkmx_list
			// .get(j)
			// .get("FKFS")
			// .toString()
			// .equals(fkfs_list.get(k).get("FKFS")
			// .toString())) {
			// if ("1".equals(fkfs_list.get(k).get("FKLB"))) {
			// ghxjje = parseDouble(ghrb_fkmx_list.get(j)
			// .get("FKJE"));
			// xj_gh_map.put("XJJE", ghxjje);
			//
			// // } else if("7".equals(fkfs_list.get(k).get("FKLB"))){
			// // ghxjje = parseDouble(ghrb_fkmx_list.get(j)
			// // .get("FKJE"));
			// //// xj_gh_map.put("SMKJE", ghxjje);
			// // } else if("2".equals(fkfs_list.get(k).get("FKLB"))){
			// // ghxjje = parseDouble(ghrb_fkmx_list.get(j)
			// // .get("FKJE"));
			// //// xj_gh_map.put("ZPJE", ghxjje);
			// } else {
			// ghhbwc = parseDouble(ghrb_fkmx_list.get(j)
			// .get("FKJE"));
			// xj_gh_map.put("HBWC", ghhbwc);
			//
			// }
			// }
			// }
			// }
			// }
			// ghrb_list.get(i)
			// .put("CZGH",
			// DictionaryController
			// .instance()
			// .getDic("phis.dictionary.doctor")
			// .getText(
			// ghrb_list.get(i).get("YGDM")
			// .toString()));
			// ghrb_list.get(i).putAll(xj_gh_map);// 接着放到List 接着对比
			// }
			// // 合并挂号和收费
			// StringBuffer str = new StringBuffer();
			// for (int i = 0; i < hzrb_list.size(); i++) {
			// if (i + 1 == hzrb_list.size()) {
			// str.append(hzrb_list.get(i).get("YGDM") + "");
			// } else {
			// str.append(hzrb_list.get(i).get("YGDM") + ",");
			// }
			// }
			// for (int i = 0; i < ghrb_list.size(); i++) {
			// if (i + 1 == ghrb_list.size()) {
			// if (str.toString().indexOf(
			// ghrb_list.get(i).get("YGDM") + "") < 0) {
			// if (str.length() > 0) {
			// str.append(",");
			// }
			// str.append(ghrb_list.get(i).get("YGDM") + "");
			// }
			// } else {
			// if (str.toString().indexOf(
			// ghrb_list.get(i).get("YGDM") + "") < 0) {
			// if (str.length() > 0) {
			// str.append(",");
			// }
			// str.append(ghrb_list.get(i).get("YGDM") + ",");
			// }
			// }
			// }
			// String[] strs = str.toString().split(",");
			int ghcount = 0;// 人次合计
			double ghAmount = 0.0;// 挂号金额合计
			int thcount = 0;// 退号合计
			int sfcount = 0;// 发票张数合计
			double sfamount = 0.0;// 收费金额合计
			int zfcount = 0;// 发票作废张数
			double totals = 0.0;// 总的合计
			double xjAmount = 0.0;// 现金合计
			double qtysAmount = 0.0;// 其他应收
			// double smkAmount = 0.0;//市民卡
			// double zpAmount = 0.0;//支票
			double hbwcAmount = 0.0;// 货币误差
			// double jjzfjeAmount = 0.0;// 门诊统筹
			// double tczfhj = 0.0;// 统筹支付
			// double dbtchj = 0.0;// 大病统筹
			// double zxjzzfhj = 0.0;// 专项救助支付
			// double grzhzfhj = 0.0;// 个人账户支付
			double zfzfAmount = 0.0;// 账户支付合计
			// for (int i = 0; i < strs.length; i++) {
			double yzjmAmount = 0.0;//义诊减免合计 zhaojian 2019-05-12
			double ghyhhjAmount = 0.0;//挂号优惠金额合计 zhaojian 2019-05-12

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
				zfzfAmount += parseDouble(hzghrb_list.get(j).get("ZFZF"));
				yzjmAmount = BSHISUtil.doublesum(yzjmAmount,parseDouble(hzghrb_list.get(j).get("YZJM")));
				ghyhhjAmount = BSHISUtil.doublesum(ghyhhjAmount,parseDouble(hzghrb_list.get(j).get("GHYHHJ")));
				m.put("CZGH", hzghrb_list.get(j).get("CZGH") + "");
				m.put("GHJE",
						String.format("%1$.2f", hzghrb_list.get(j).get("GHJE")));
				m.put("GHRC", hzghrb_list.get(j).get("GHRC") + "");
				m.put("THSL", hzghrb_list.get(j).get("THSL") + "");
				m.put("ZJJE",
						String.format("%1$.2f", hzghrb_list.get(j).get("ZJJE")));
				m.put("FPZS", hzghrb_list.get(j).get("FPZS") + "");
				m.put("ZFZS", hzghrb_list.get(j).get("ZFZS") + "");
				m.put("QTYS",
						String.format("%1$.2f", hzghrb_list.get(j).get("QTYS")));
				m.put("HBWC",
						String.format("%1$.2f", hzghrb_list.get(j).get("HBWC")));
				m.put("XJJE",
						String.format("%1$.2f", hzghrb_list.get(j).get("XJJE")));
				m.put("TOTALAMOUNT", String.format("%1$.2f", BSPHISUtil
						.getDouble(parseDouble(hzghrb_list.get(j).get("ZJJE"))
								+ parseDouble(hzghrb_list.get(j).get("GHJE")),
								2)));
				m.put("YZJM", String.format("%1$.2f", hzghrb_list.get(j).get("YZJM")));//zhaojian 2019-05-12 增加义诊减免
				m.put("GHYHHJ", String.format("%1$.2f", hzghrb_list.get(j).get("GHYHHJ")));//zhaojian 2019-05-12 增加挂号优惠金额

				// fkxx="货币误差:"+String.format("%1$.2f",
				// hzghrb_list.get(j).get("HBWC"))+fkxx+"";
				// fkxx="现金:"+String.format("%1$.2f",
				// hzghrb_list.get(j).get("XJJE"))+fkxx+"";
				// m.put("fkxx", fkxx);
				// MS_HZRB where JGID =:JGID and MZLB =:MZLB and HZRQ >=:KSRQ
				// and HZRQ <=:JSRQ group by CZGH";
				// MS_GHRB where JGID =:JGID and MZLB =:MZLB and HZRQ >=:KSRQ
				// and HZRQ <=:JSRQ group by CZGH";" +
				String sql_fkfs = "select c.FKFS as FKFS,sum(c.FKJE) as FKJE,d.FKMC as FKMC from ("
						+ "select a.FKFS as FKFS,a.FKJE as FKJE from MS_FKXX a,MS_MZXX b where a.MZXH = b.MZXH and b.JGID = :jgid and b.HZRQ >=:ksrq and b.HZRQ <=:jsrq and b.MZLB = :mzlb and b.CZGH = :czgh"
						+ " union all "
						+ "select a.FKFS as FKFS,(-1*a.FKJE) as FKJE from MS_FKXX a,MS_ZFFP b where a.MZXH = b.MZXH and b.JGID = :jgid and b.HZRQ >=:ksrq and b.HZRQ <=:jsrq and b.MZLB = :mzlb and b.CZGH = :czgh"
						+ " union all "
						+ "select a.FKFS as FKFS,a.FKJE as FKJE from MS_GH_FKXX a,MS_GHMX b where a.SBXH = b.SBXH and b.JGID = :jgid and b.HZRQ >=:ksrq and b.HZRQ <=:jsrq and b.MZLB = :mzlb and b.CZGH = :czgh"
						+ " union all "
						+ "select a.FKFS as FKFS,(-1*a.FKJE) as FKJE from MS_GH_FKXX a,MS_THMX b where a.SBXH = b.SBXH and b.JGID = :jgid and b.HZRQ >=:ksrq and b.HZRQ <=:jsrq and b.MZLB = :mzlb and b.CZGH = :czgh"
						+ ") c left outer join GY_FKFS d on c.FKFS = d.FKFS group by c.FKFS,d.FKMC order by c.FKFS";
				String sql_brxz = "select sum(c.QTYS) as QTYS,c.BRXZ as BRXZ,d.XZMC as XZMC,d.DBPB as DBPB from ("
						+ "select a.BRXZ as BRXZ,a.QTYS as QTYS from MS_MZXX a where a.JGID=:jgid and a.HZRQ >=:ksrq and a.HZRQ <=:jsrq and a.MZLB = :mzlb and a.CZGH = :czgh"
						+ " union all "
						+ "select a.BRXZ as BRXZ,(-1*a.QTYS) as QTYS from MS_MZXX a,MS_ZFFP b where a.MZXH = b.MZXH and b.JGID=:jgid and b.HZRQ >=:ksrq and b.HZRQ <=:jsrq and b.MZLB = :mzlb and b.CZGH = :czgh"
						+ " union all "
						+ "select a.BRXZ as BRXZ,a.QTYS as QTYS from MS_GHMX a where a.JGID=:jgid and a.HZRQ >=:ksrq and a.HZRQ <=:jsrq and a.MZLB = :mzlb and a.CZGH = :czgh"
						+ " union all "
						+ "select a.BRXZ as BRXZ,(-1*a.QTYS) as QTYS from MS_GHMX a,MS_THMX b where a.SBXH = b.SBXH and b.JGID=:jgid and b.HZRQ >=:ksrq and b.HZRQ <=:jsrq and b.MZLB = :mzlb and b.CZGH = :czgh"
						+ ") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB";
				Map<String, Object> parameters2 = new HashMap<String, Object>();
				parameters2.put("jgid", jgid);
				parameters2.put("mzlb",
						Long.parseLong(BSPHISUtil.getMZLB(jgid, dao) + ""));
				parameters2.put("czgh", hzghrb_list.get(j).get("YGDM") + "");
				// parameters2.put("hzrq", cdate.getTime());
				parameters2.put("ksrq", sdf.parseObject(ksrqstr + " 00:00:00"));
				parameters2.put("jsrq", sdf.parseObject(jsrqstr + " 23:59:59"));
				List<Map<String, Object>> ids_fkfs = dao.doSqlQuery(sql_fkfs,
						parameters2);
				List<Map<String, Object>> ids_brxz = dao.doSqlQuery(sql_brxz,
						parameters2);
				String qtysFb = "";
				String jzjeSt = "0.00";
				if (ids_fkfs != null && ids_fkfs.size() != 0) {
					for (int n = 0; n < ids_fkfs.size(); n++) {
						qtysFb = qtysFb
								+ ids_fkfs.get(n).get("FKMC")
								+ ":"
								+ String.format("%1$.2f",
										ids_fkfs.get(n).get("FKJE")) + " ";
					}
				}
				qtysFb = qtysFb + "挂号减免:" + String.format("%1$.2f", BSHISUtil.doublesum(parseDouble(hzghrb_list.get(j).get("YZJM")),parseDouble(hzghrb_list.get(j).get("GHYHHJ")))) + " ";//zhaojian 2019-05-12 增加挂号优惠金额
				if (ids_brxz != null && ids_brxz.size() != 0) {
					for (int n = 0; n < ids_brxz.size(); n++) {
						if (ids_brxz.get(n).get("DBPB") != null
								&& Integer.parseInt(ids_brxz.get(n).get("DBPB")
										+ "") == 0) {
							jzjeSt = String.format(
									"%1$.2f",
									parseDouble(jzjeSt)
											+ parseDouble(ids_brxz.get(n).get(
													"QTYS")
													+ ""));
						} else {
							qtysFb = qtysFb
									+ ids_brxz.get(n).get("XZMC")
									+ ":"
									+ String.format(
											"%1$.2f",
											parseDouble(ids_brxz.get(n).get(
													"QTYS")
													+ "")) + " ";
						}
					}
					qtysFb = qtysFb + "账户:" +hzghrb_list.get(j).get("ZFZF") + " 记账 :" + jzjeSt + " ";
				}
				m.put("fkxx", qtysFb);
				li.add(m);
			}
			// }
			// response.put("smkAmount", String.format("%1$.2f", smkAmount));
			// response.put("zpAmount", String.format("%1$.2f", zpAmount));
			response.put("startSummaryDate", ksrqstr);
			response.put("preparedby", jgName);
			response.put("summaryDate", jsrqstr);
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
			// response.put("jjzfjeAmount", String.format("%1$.2f",
			// jjzfjeAmount));
			// response.put("TCZFHJ", String.format("%1$.2f", tczfhj));
			// response.put("DBTCHJ", String.format("%1$.2f", dbtchj));
			// response.put("ZXJZZFHJ", String.format("%1$.2f", zxjzzfhj));
			// response.put("GRZHZFHJ", String.format("%1$.2f", grzhzfhj));
			// fkxxAmount="货币误差:"+hbwcAmount+fkxxAmount+"";
			// fkxxAmount="现金:"+xjAmount+fkxxAmount+"";
			String sql_fkfs = "select c.FKFS as FKFS,sum(c.FKJE) as FKJE,d.FKMC as FKMC from ("
					+ "select a.FKFS as FKFS,a.FKJE as FKJE from MS_FKXX a,MS_MZXX b,MS_HZRB c where c.JZRQ = b.JZRQ and c.CZGH = b.CZGH and a.MZXH = b.MZXH and b.JGID = :jgid and c.HZRQ >=:ksrq and c.HZRQ <=:jsrq and b.MZLB = :mzlb"
					+ " union all "
					+ "select a.FKFS as FKFS,(-1*a.FKJE) as FKJE from MS_FKXX a,MS_ZFFP b,MS_HZRB c where c.JZRQ = b.JZRQ and c.CZGH = b.CZGH and a.MZXH = b.MZXH and b.JGID = :jgid and c.HZRQ >=:ksrq and c.HZRQ <=:jsrq and b.MZLB = :mzlb"
					+ " union all "
					+ "select a.FKFS as FKFS,a.FKJE as FKJE from MS_GH_FKXX a,MS_GHMX b,MS_GHRB c where c.JZRQ = b.JZRQ and c.CZGH = b.CZGH and a.SBXH = b.SBXH and b.JGID = :jgid and c.HZRQ >=:ksrq and c.HZRQ <=:jsrq and b.MZLB = :mzlb"
					+ " union all "
					+ "select a.FKFS as FKFS,(-1*a.FKJE) as FKJE from MS_GH_FKXX a,MS_THMX b,MS_GHRB c where c.JZRQ = b.JZRQ and c.CZGH = b.CZGH and a.SBXH = b.SBXH and b.JGID = :jgid and c.HZRQ >=:ksrq and c.HZRQ <=:jsrq and b.MZLB = :mzlb"
					+ ") c left outer join GY_FKFS d on c.FKFS = d.FKFS group by c.FKFS,d.FKMC order by c.FKFS";
			String sql_brxz = "select sum(c.QTYS) as QTYS,c.BRXZ as BRXZ,d.XZMC as XZMC,d.DBPB as DBPB from ("
					+ "select a.BRXZ as BRXZ,a.QTYS as QTYS from MS_MZXX a,MS_HZRB c where c.JZRQ = a.JZRQ and c.CZGH = a.CZGH and a.JGID=:jgid and c.HZRQ >=:ksrq and c.HZRQ <=:jsrq and a.MZLB = :mzlb"
					+ " union all "
					+ "select a.BRXZ as BRXZ,(-1*a.QTYS) as QTYS from MS_MZXX a,MS_ZFFP b,MS_HZRB c where c.JZRQ = b.JZRQ and c.CZGH = b.CZGH and a.MZXH = b.MZXH and b.JGID=:jgid and c.HZRQ >=:ksrq and c.HZRQ <=:jsrq and b.MZLB = :mzlb"
					+ " union all "
					+ "select a.BRXZ as BRXZ,a.QTYS as QTYS from MS_GHMX a,MS_GHRB c where c.JZRQ = a.JZRQ and c.CZGH = a.CZGH and a.JGID=:jgid and c.HZRQ >=:ksrq and c.HZRQ <=:jsrq and a.MZLB = :mzlb"
					+ " union all "
					+ "select a.BRXZ as BRXZ,(-1*a.QTYS) as QTYS from MS_GHMX a,MS_THMX b,MS_GHRB c where c.JZRQ = b.JZRQ and c.CZGH = b.CZGH and a.SBXH = b.SBXH and b.JGID=:jgid and c.HZRQ >=:ksrq and c.HZRQ <=:jsrq and b.MZLB = :mzlb"
					+ ") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB";
			Map<String, Object> parameters2 = new HashMap<String, Object>();
			parameters2.put("jgid", jgid);
			parameters2.put("mzlb",
					Long.parseLong(BSPHISUtil.getMZLB(jgid, dao) + ""));
			// parameters2.put("hzrq", cdate.getTime());
			parameters2.put("ksrq", sdf.parseObject(ksrqstr + " 00:00:00"));
			parameters2.put("jsrq", sdf.parseObject(jsrqstr + " 23:59:59"));
			List<Map<String, Object>> ids_fkfs = dao.doSqlQuery(sql_fkfs,
					parameters2);
			List<Map<String, Object>> ids_brxz = dao.doSqlQuery(sql_brxz,
					parameters2);
			String qtysFb = "";
			String jzjeSt = "0.00";
			if (ids_fkfs != null && ids_fkfs.size() != 0) {
				for (int n = 0; n < ids_fkfs.size(); n++) {
					qtysFb = qtysFb
							+ ids_fkfs.get(n).get("FKMC")
							+ ":"
							+ String.format("%1$.2f",
									ids_fkfs.get(n).get("FKJE")) + " ";
				}
			}
			qtysFb = qtysFb + "挂号减免:" + BSHISUtil.doublesum(yzjmAmount,ghyhhjAmount) + " ";//zhaojian 2019-05-12 增加挂号优惠金额
			if (ids_brxz != null && ids_brxz.size() != 0) {
				for (int n = 0; n < ids_brxz.size(); n++) {
					if (ids_brxz.get(n).get("DBPB") != null
							&& Integer.parseInt(ids_brxz.get(n).get("DBPB")
									+ "") == 0) {
						jzjeSt = String
								.format("%1$.2f",
										parseDouble(jzjeSt)
												+ parseDouble(ids_brxz.get(n)
														.get("QTYS") + ""));
					} else {
						qtysFb = qtysFb
								+ ids_brxz.get(n).get("XZMC")
								+ ":"
								+ String.format("%1$.2f", parseDouble(ids_brxz
										.get(n).get("QTYS") + "")) + " ";
					}
				}
				qtysFb = qtysFb + "账户:" + zfzfAmount + " 记账 :" + jzjeSt + " ";
			}
			response.put("fkxxAmount", qtysFb);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ModelDataOperationException e1) {
			e1.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
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
