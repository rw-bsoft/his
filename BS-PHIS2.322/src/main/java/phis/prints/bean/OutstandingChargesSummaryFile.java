package phis.prints.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.ivc.source.ChargesProduce;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class OutstandingChargesSummaryFile implements IHandler {
	ChargesProduce cck = ChargesProduce.getInstance();

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		String userid = user.getUserId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("jgid", jgid);
		Map<String, Object> parametersUser = new HashMap<String, Object>();
		parametersUser.put("jgid", jgid);

		// 界面表单获取数据SQL
		// String sql_jmbd =
		// " SELECT CZGH as CZGH,0 as GHRC,0.00 as GHJE,sum(FPZS) as FPZS,"
		// +
		// " sum(ZJJE) as SFJE,0.00 as XJJE,0.00 as ZPJE,sum(ZFZF) as ZFZF,sum(QTYS) as QTYS,"
		// +
		// " 0.00 as HBWC,0.00 as KHJE,0.00 AS JKJE,0.00 AS TKJE,0.00 as YHKJE,0.00 as YHJE,"
		// +
		// " 0.00 as QTZF  FROM MS_HZRB WHERE (HZRQ is NULL ) and JGID =:jgid "
		// + "  GROUP BY CZGH";
		// 收费日报表单获取数据SQL语句如下（参数:机构ID、用户ID）：
		//增加挂号优惠合计字段 zhaojian 2019-05-12
		String sql_sfrb = "SELECT CZGH as CZGH,0 as GHRC,sum(FPZS) as FPZS,0 as GHJE,sum(ZJJE) as ZJJE,0 as GHYHHJ from (SELECT CZGH as CZGH,count(*) as FPZS,sum(ZJJE) as ZJJE from MS_MZXX where JGID =:jgid"
				+ " and JZRQ is null  "
				+ " and (ZFPB is null or ZFPB=0)"
				+ " group by CZGH union all SELECT CZGH as CZGH,count(*) as FPZS,0 as ZJJE from MS_MZXX where JGID =:jgid"
				+ " and JZRQ is null  "
				+ " and ZFPB=1"
				+ " group by CZGH) group by CZGH";
		// 挂号日报表单获取数据SQL语句如下（参数: 机构ID、用户ID）：
		//增加挂号优惠合计字段 zhaojian 2019-05-12
		String sql_ghrb = "SELECT CZGH as CZGH,sum(GHRC) as GHRC,0 as FPZS,sum(GHJE) as GHJE,0 as ZJJE,sum(GHYHHJ) as GHYHHJ from (SELECT CZGH as CZGH,count(*) as GHRC,sum(GHJE + ZLJE + ZJFY + BLJE) as GHJE,sum(YHJE) as GHYHHJ "
				+ " from MS_GHMX where JGID =:jgid and JZRQ is null  "
				+ " and (THBZ is null or THBZ=0)"
				+ " group by CZGH union all SELECT CZGH as CZGH,count(*) as GHRC,0 as GHJE,0 as GHYHHJ "
				+ " from MS_GHMX where JGID =:jgid and JZRQ is null  "
				+ " and THBZ=1" + " group by CZGH) group by CZGH";
		//增加挂号优惠合计字段 zhaojian 2019-05-12
		String sql_sfghrb = "SELECT CZGH as CZGH,sum(GHRC) as GHRC,sum(FPZS) as FPZS,sum(GHJE) as GHJE,sum(ZJJE) as ZJJE,sum(GHYHHJ) as GHYHHJ from ("
				+ sql_sfrb + " union all " + sql_ghrb + ") group by CZGH";
		// 已结帐未汇总收费表单获取数据SQL语句如下（参数:机构ID）：
		String sql_yjzsf = "select a.CZGH as CZGH,sum(b.FKJE) as FKJE,c.FKLB as FKLB,c.FKMC as FKMC,sum(a.QTYS) as QTYS "
				+ " from MS_MZXX a,MS_FKXX b left outer join GY_FKFS c on b.FKFS=c.FKFS where a.MZXH = b.MZXH AND a.JGID = b.JGID "
				+ " AND a.JGID =:jgid  and a.JZRQ is null "
				+ " and a.ZFPB = 0 group by a.CZGH,c.FKLB,c.FKMC";
		// 已结帐未汇总挂号表单获取数据SQL语句如下（参数:机构ID）：
		String sql_yjzgh = "select a.CZGH as CZGH,sum(b.FKJE) as FKJE,c.FKLB as FKLB,c.FKMC as FKMC,sum(a.QTYS) as QTYS "
				+ " from MS_GHMX a,MS_GH_FKXX b left outer join GY_FKFS c on b.FKFS=c.FKFS where a.SBXH = b.SBXH "
				+ " AND a.JGID = :jgid  and a.JZRQ is null"
				+ " and a.THBZ = 0 group by a.CZGH,c.FKLB,c.FKMC";
		String sql_yjzghsf = "select CZGH as CZGH,sum(FKJE) as FKJE,FKLB as FKLB,FKMC as FKMC,sum(QTYS) as QTYS from ("
				+ sql_yjzsf
				+ " union all "
				+ sql_yjzgh
				+ ") group by CZGH,FKLB,FKMC order by FKLB";
		// 付款方式表单获取数据SQL语句如下（参数:机构ID）：
		// String sql_fkfs =
		// "SELECT FKFS as FKFS,FKMC as FKMC,FKLB as FKLB FROM GY_FKFS WHERE SYLX = 1 AND ZFBZ = 0";

		try {
			// Map<String, Object> jmbd_list = dao.doLoad(sql_jmbd,
			// parameters);
			List<Map<String, Object>> sfghrb_list = dao.doSqlQuery(sql_sfghrb,
					parametersUser);
			// List<Map<String, Object>> ghrb_list = dao.doSqlQuery(sql_ghrb,
			// parametersUser);
			// List<Map<String, Object>> yjzsf_list = dao.doQuery(sql_yjzsf,
			// parameters);
			// List<Map<String, Object>> yjzgh_list = dao.doQuery(sql_yjzgh,
			// parameters);
			List<Map<String, Object>> yjzghsf_list = dao.doSqlQuery(
					sql_yjzghsf, parameters);
			List<Map<String, Object>> reList = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < sfghrb_list.size(); i++) {
				Map<String, Object> reMap = sfghrb_list.get(i);
				reMap.put("XJJE", "0.00");
				reMap.put("ZPJE", "0.00");
				reMap.put("HBWC", "0.00");
				reMap.put("YHJE", "0.00");
				reMap.put("YHKJE", "0.00");
				reMap.put("QTZF", "0.00");
				reMap.put("QTYS", "0.00");
				reMap.put("GRZH", "0.00");
				StringBuffer sbfkxx = new StringBuffer();
				double qtys = 0;
				for (int j = 0; j < yjzghsf_list.size(); j++) {
					if ((reMap.get("CZGH") + "").equals(yjzghsf_list.get(j)
							.get("CZGH") + "")) {
						// reMap.putAll(yjzghsf_list.get(j));
						Map<String, Object> yjzghsf_map = yjzghsf_list.get(j);
						// String ll_fkfs = yjzsf_map.get("FKFS") + "";
						// Double ld_fkje =
						// getDoubleValue(yjzghsf_map.get("FKJE"));
						// Double ld_qtys =
						// getDoubleValue(yjzghsf_map.get("QTYS"));
						// if ("1".equals(yjzghsf_map.get("FKLB") + "")) {
						// reMap.put("XJJE", String.format("%1$.2f", ld_fkje));
						// } else if ("2".equals(yjzghsf_map.get("FKLB") + ""))
						// {
						// reMap.put("ZPJE", String.format("%1$.2f", ld_fkje));
						// } else if ("4".equals(yjzghsf_map.get("FKLB") + ""))
						// {
						// reMap.put("HBWC", String.format("%1$.2f", ld_fkje));
						// } else if ("5".equals(yjzghsf_map.get("FKLB") + ""))
						// {
						// reMap.put("YHJE", String.format("%1$.2f", ld_fkje));
						// } else if ("6".equals(yjzghsf_map.get("FKLB") + ""))
						// {
						// reMap.put("YHKJE", String.format("%1$.2f", ld_fkje));
						// } else {
						// reMap.put("QTZF", String.format("%1$.2f", ld_fkje
						// + getDoubleValue(reMap.get("QTZF"))));
						// }
						// if (!"4".equals(yjzghsf_map.get("FKLB") + "")) {
						// reMap.put("QTYS", String.format("%1$.2f",
						// BSPHISUtil.getDouble(
						// ld_qtys
						// + getDoubleValue(reMap
						// .get("QTYS")), 2)));
						// }
						if (getDoubleValue(yjzghsf_map.get("FKJE")) != 0) {
							if(sbfkxx.length()>0){
								sbfkxx.append(",");
							}
							sbfkxx.append(yjzghsf_map.get("FKMC"))
									.append(":")
									.append(String.format("%1$.2f",
											yjzghsf_map.get("FKJE")));
						}
						if (getDoubleValue(yjzghsf_map.get("QTYS")) != 0) {
							qtys += Double.parseDouble(yjzghsf_map.get("QTYS")+"");
						}
					}
				}
				//增加挂号优惠合计 zhaojian 2019-05-12
				sbfkxx.append(",优惠:").append(String.format("%1$.2f",(reMap.get("GHYHHJ"))));
				if(qtys>0){
					if(sbfkxx.length()>0){
						sbfkxx.append(",");
					}
					sbfkxx.append("其它应收:").append(
							String.format("%1$.2f",qtys));
				}
//				if (sbfkxx.toString().lastIndexOf(",") > 0) {
//					reMap.put(
//							"fkxx",
//							sbfkxx.toString().substring(0,
//									sbfkxx.toString().lastIndexOf(",")));
//				} else {
					reMap.put("fkxx", sbfkxx.toString());
//				}
				reMap.put(
						"CZGH",
						DictionaryController.instance()
								.get("phis.dictionary.doctor")
								.getText(reMap.get("CZGH") + ""));
				reMap.put("GHJE", String.format("%1$.2f", reMap.get("GHJE")));
				reMap.put("SFJE", String.format("%1$.2f", reMap.get("ZJJE")));
				reMap.put(
						"TOTALAMOUNT",
						String.format(
								"%1$.2f",
								BSPHISUtil.getDouble(
										getDoubleValue(reMap.get("GHJE"))
												+ getDoubleValue(reMap
														.get("SFJE")), 2)));
				reList.add(reMap);
			}
			records.addAll(reList);
			// parameters.clear();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		// List<Map<String, Object>> fkfs_list = dao.doQuery(sql_fkfs,
		// parameters);
		// 填收费日报信息
		// 遍历收费日报表单
		// Double ll_zffp = 0D;
		// for (int i = 0; i < sfrb_list.size(); i++) {
		// Map<String, Object> sfrb_map = sfrb_list.get(i);
		// String ls_ygdm = sfrb_map.get("CZGH") + "";
		// String ll_find = "";
		// for (int j = 0; j < jmbd_list.size(); j++) {
		// Map<String, Object> jmbd_map = jmbd_list.get(j);
		// if (ls_ygdm.equals(jmbd_map.get("CZGH")+"")) {
		// ll_find = jmbd_map.get("CZGH") + "";
		// break;
		// }
		// }
		// if (ll_find.endsWith("")) {
		// Map<String, Object> jmbd_map = new HashMap<String, Object>();
		// jmbd_map.put("CZGH", ls_ygdm);
		// jmbd_map.put("GHRC", 0);
		// jmbd_map.put("GHJE", 0D);
		// jmbd_map.put("FPZS", 0);
		// jmbd_map.put("SFJE", 0D);
		// jmbd_map.put("XJJE", 0D);
		// jmbd_map.put("ZPJE", 0D);
		// jmbd_map.put("YHJE", 0D);
		// jmbd_map.put("YHKJE", 0D);
		// jmbd_map.put("QTZF", 0D);
		// jmbd_map.put("ZFZF", 0D);
		// jmbd_map.put("QTYS", 0D);
		// jmbd_map.put("HBWC", 0D);
		// jmbd_map.put("KHJE", 0D);
		// jmbd_map.put("JKJE", 0D);
		// jmbd_map.put("TKJE", 0D);
		//
		// jmbd_map.put("GRZH", 0D);
		// jmbd_map.put("ZJJE", 0D);
		//
		// // 遍历已结帐未汇总收费表单
		// for (int j = 0; j < yjzsf_list.size(); j++) {
		// Map<String, Object> yjzsf_map = yjzsf_list.get(j);
		// // String ll_fkfs = yjzsf_map.get("FKFS") + "";
		// Double ld_fkje = getDoubleValue(yjzsf_map
		// .get("FKJE"));
		// if (yjzsf_map.get("CZGH").equals(jmbd_map.get("CZGH"))) {
		// if (yjzsf_map.get("FKLB").equals("1")) {
		// jmbd_map.put(
		// "XJJE",
		// getDoubleValue(jmbd_map.get("XJJE"))
		// + ld_fkje);
		// } else if (yjzsf_map.get("FKLB").equals("2")) {
		// jmbd_map.put(
		// "ZPJE",
		// getDoubleValue(jmbd_map.get("ZPJE"))
		// + ld_fkje);
		// } else if (yjzsf_map.get("FKLB").equals("4")) {
		// jmbd_map.put(
		// "HBWC",
		// getDoubleValue(jmbd_map.get("HBWC"))
		// + ld_fkje);
		// } else if (yjzsf_map.get("FKLB").equals("5")) {
		// jmbd_map.put(
		// "YHJE",
		// getDoubleValue(jmbd_map.get("YHJE"))
		// + ld_fkje);
		// } else if (yjzsf_map.get("FKLB").equals("6")) {
		// jmbd_map.put(
		// "YHKJE",
		// getDoubleValue(jmbd_map
		// .get("YHKJE"))
		// + ld_fkje);
		// } else {
		// jmbd_map.put(
		// "QTZF",
		// getDoubleValue(jmbd_map.get("QTZF"))
		// + ld_fkje);
		// }
		// // yjzsf_map.putAll(jmbd_map);
		// }
		//
		// // boolean isFind=false;
		// // for (Map<String, Object> fkfs_map: fkfs_list) {
		// // if (fkfs_map.get("FKFS").equals(ll_fkfs)) {
		// // isFind=true;
		// // if(fkfs_map.get("FKLB").equals("1")){
		// // jmbd_map.put("XJJE",
		// // Double.parseDouble(jmbd_map.get("XJJE")+"")+ld_fkje);
		// // }
		// // else if(fkfs_map.get("FKLB").equals("2")){
		// // jmbd_map.put("ZPJE",
		// // Double.parseDouble(jmbd_map.get("ZPJE")+"")+ld_fkje);
		// // }
		// // else if(fkfs_map.get("FKLB").equals("4")){
		// // jmbd_map.put("HBWC",
		// // Double.parseDouble(jmbd_map.get("HBWC")+"")+ld_fkje);
		// // }
		// // else if(fkfs_map.get("FKLB").equals("5")){
		// // jmbd_map.put("YHJE",
		// // Double.parseDouble(jmbd_map.get("YHJE")+"")+ld_fkje);
		// // }
		// // else if(fkfs_map.get("FKLB").equals("6")){
		// // jmbd_map.put("YHKJE",
		// // Double.parseDouble(jmbd_map.get("YHKJE")+"")+ld_fkje);
		// // }else{
		// // jmbd_map.put("QTZF",
		// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
		// // }
		// // }
		// // }
		// // if(isFind==false){
		// // jmbd_map.put("QTZF",
		// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
		// // }
		// }
		//
		// // 遍历已结帐未汇总挂号表单
		// for (int j = 0; j < yjzgh_list.size(); j++) {
		// Map<String, Object> yjzgh_map = yjzgh_list.get(j);
		// String ll_fkfs = yjzgh_map.get("FKFS") + "";
		// Double ld_fkje = getDoubleValue(yjzgh_map
		// .get("FKJE"));
		// if (yjzgh_map.get("CZGH").equals(jmbd_map.get("CZGH"))) {
		// if (yjzgh_map.get("FKLB").equals("1")) {
		// jmbd_map.put(
		// "XJJE",
		// getDoubleValue(jmbd_map.get("XJJE"))
		// + ld_fkje);
		// } else if (yjzgh_map.get("FKLB").equals("2")) {
		// jmbd_map.put(
		// "ZPJE",
		// getDoubleValue(jmbd_map.get("ZPJE"))
		// + ld_fkje);
		// } else if (yjzgh_map.get("FKLB").equals("4")) {
		// jmbd_map.put(
		// "HBWC",
		// getDoubleValue(jmbd_map.get("HBWC"))
		// + ld_fkje);
		// } else if (yjzgh_map.get("FKLB").equals("5")) {
		// jmbd_map.put(
		// "YHJE",
		// getDoubleValue(jmbd_map.get("YHJE"))
		// + ld_fkje);
		// } else if (yjzgh_map.get("FKLB").equals("6")) {
		// jmbd_map.put(
		// "YHKJE",
		// getDoubleValue(jmbd_map
		// .get("YHKJE"))
		// + ld_fkje);
		// } else {
		// jmbd_map.put(
		// "QTZF",
		// getDoubleValue(jmbd_map.get("QTZF"))
		// + ld_fkje);
		// }
		// // yjzgh_map.putAll(jmbd_map);
		// }
		// // boolean isFind=false;
		// // for (int k = 0; k < fkfs_list.size(); k++) {
		// // Map<String, Object> fkfs_map = fkfs_list.get(k);
		// // if (fkfs_map.get("FKFS").equals(ll_fkfs)) {
		// // isFind=true;
		// // if(fkfs_map.get("FKLB").equals("1")){
		// // jmbd_map.put("XJJE",
		// // Double.parseDouble(jmbd_map.get("XJJE")+"")+ld_fkje);
		// // }
		// // else if(fkfs_map.get("FKLB").equals("2")){
		// // jmbd_map.put("ZPJE",
		// // Double.parseDouble(jmbd_map.get("ZPJE")+"")+ld_fkje);
		// // }
		// // else if(fkfs_map.get("FKLB").equals("4")){
		// // jmbd_map.put("HBWC",
		// // Double.parseDouble(jmbd_map.get("HBWC")+"")+ld_fkje);
		// // }
		// // else if(fkfs_map.get("FKLB").equals("5")){
		// // jmbd_map.put("YHJE",
		// // Double.parseDouble(jmbd_map.get("YHJE")+"")+ld_fkje);
		// // }
		// // else if(fkfs_map.get("FKLB").equals("6")){
		// // jmbd_map.put("YHKJE",
		// // Double.parseDouble(jmbd_map.get("YHKJE")+"")+ld_fkje);
		// // }else{
		// // jmbd_map.put("QTZF",
		// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
		// // }
		// // }
		// // }
		// // if(isFind==false){
		// // jmbd_map.put("QTZF",
		// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
		// // }
		// }
		// jmbd_map.put(
		// "FPZS",
		// getIntValue(jmbd_map.get("FPZS"))
		// + getIntValue(sfrb_map.get("FPZS")));
		// jmbd_map.put(
		// "SFJE",
		// getDoubleValue(jmbd_map.get("SFJE"))
		// + getDoubleValue(sfrb_map.get("ZJJE")));
		// if (sfrb_map.get("ZFZF") != null
		// && sfrb_map.get("ZFZF") != "") {
		// jmbd_map.put(
		// "ZFZF",
		// Double.parseDouble(jmbd_map.get("ZFZF") + "")
		// + Double.parseDouble(sfrb_map
		// .get("ZFZF") + ""));
		// }
		// if (sfrb_map.get("QTYS") != null
		// && sfrb_map.get("QTYS") != "") {
		// jmbd_map.put(
		// "QTYS",
		// Double.parseDouble(jmbd_map.get("QTYS") + "")
		// + Double.parseDouble(sfrb_map
		// .get("QTYS") + ""));
		// }
		// // jmbd_list.get(0).putAll(jmbd_map);
		// jmbd_list.add(jmbd_map);
		// }
		// // if (sfrb_map.get("ZFSZ") != null && sfrb_map.get("ZFSZ") != "") {
		// // ll_zffp += Double.parseDouble(sfrb_map.get("ZFSZ") + "");
		// // }
		//
		// }
		//
		// // 填挂号日报信息
		// // 遍历挂号日报表单
		// // Double ll_ghth = 0D;
		// for (int i = 0; i < ghrb_list.size(); i++) {
		// Map<String, Object> ghrb_map = ghrb_list.get(i);
		// String ls_ygdm = ghrb_map.get("CZGH") + "";
		// String ll_find = "";
		// for (int j = 0; j < jmbd_list.size(); j++) {
		// Map<String, Object> jmbd_map = jmbd_list.get(j);
		// if (ls_ygdm.equals(jmbd_map.get("CZGH"))) {
		// ll_find = jmbd_map.get("CZGH") + "";
		// jmbd_map.put("GHRC",
		// getIntValue(ghrb_map.get("FPZS")));
		// jmbd_map.put("GHJE",
		// getDoubleValue(ghrb_map.get("ZJJE")));
		// break;
		// }
		// }
		// // if (ll_find.endsWith("")) {
		// // Map<String, Object> jmbd_map = new HashMap<String, Object>();
		// // jmbd_map.put("CZGH", ls_ygdm);
		// // jmbd_map.put("GHRC", 0D);
		// // jmbd_map.put("GHJE", 0D);
		// // jmbd_map.put("FPZS", 0D);
		// // jmbd_map.put("SFJE", 0D);
		// // jmbd_map.put("XJJE", 0D);
		// // jmbd_map.put("ZPJE", 0D);
		// // jmbd_map.put("YHJE", 0D);
		// // jmbd_map.put("YHKJE", 0D);
		// // jmbd_map.put("QTZF", 0D);
		// // jmbd_map.put("ZFZF", 0D);
		// // jmbd_map.put("QTYS", 0D);
		// // jmbd_map.put("HBWC", 0D);
		// // jmbd_map.put("KHJE", 0D);
		// // jmbd_map.put("JKJE", 0D);
		// // jmbd_map.put("TKJE", 0D);
		// //
		// // jmbd_map.put("GRZH", 0D);
		// // jmbd_map.put("ZJJE", 0D);
		// //
		// // //遍历已结帐未汇总收费表单
		// // for (int j = 0; j < yjzsf_list.size(); j++) {
		// // Map<String, Object> yjzsf_map = yjzsf_list.get(j);
		// // String ll_fkfs = yjzsf_map.get("FKFS") + "";
		// // Double ld_fkje = Double.parseDouble(yjzsf_map
		// // .get("FKJE") + "");
		// // if(yjzsf_map.get("CZGH").equals(jmbd_map.get("CZGH"))){
		// // if(yjzsf_map.get("FKLB").equals("1")){
		// // jmbd_map.put("XJJE",
		// // Double.parseDouble(jmbd_map.get("XJJE")+"")+ld_fkje);
		// // }else if(yjzsf_map.get("FKLB").equals("2")){
		// // jmbd_map.put("ZPJE",
		// // Double.parseDouble(jmbd_map.get("ZPJE")+"")+ld_fkje);
		// // }
		// // else if(yjzsf_map.get("FKLB").equals("4")){
		// // jmbd_map.put("HBWC",
		// // Double.parseDouble(jmbd_map.get("HBWC")+"")+ld_fkje);
		// // }
		// // else if(yjzsf_map.get("FKLB").equals("5")){
		// // jmbd_map.put("YHJE",
		// // Double.parseDouble(jmbd_map.get("YHJE")+"")+ld_fkje);
		// // }
		// // else if(yjzsf_map.get("FKLB").equals("6")){
		// // jmbd_map.put("YHKJE",
		// // Double.parseDouble(jmbd_map.get("YHKJE")+"")+ld_fkje);
		// // }else{
		// // jmbd_map.put("QTZF",
		// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
		// // }}
		// // // boolean isFind=false;
		// // // for (int k = 0; k < fkfs_list.size(); k++) {
		// // // Map<String, Object> fkfs_map = fkfs_list.get(k);
		// // // if (fkfs_map.get("FKFS").equals(ll_fkfs)) {
		// // // isFind=true;
		// // // if(fkfs_map.get("FKLB").equals("1")){
		// // // jmbd_map.put("XJJE",
		// // Double.parseDouble(jmbd_map.get("XJJE")+"")+ld_fkje);
		// // // }
		// // // else if(fkfs_map.get("FKLB").equals("2")){
		// // // jmbd_map.put("ZPJE",
		// // Double.parseDouble(jmbd_map.get("ZPJE")+"")+ld_fkje);
		// // // }
		// // // else if(fkfs_map.get("FKLB").equals("4")){
		// // // jmbd_map.put("HBWC",
		// // Double.parseDouble(jmbd_map.get("HBWC")+"")+ld_fkje);
		// // // }
		// // // else if(fkfs_map.get("FKLB").equals("5")){
		// // // jmbd_map.put("YHJE",
		// // Double.parseDouble(jmbd_map.get("YHJE")+"")+ld_fkje);
		// // // }
		// // // else if(fkfs_map.get("FKLB").equals("6")){
		// // // jmbd_map.put("YHKJE",
		// // Double.parseDouble(jmbd_map.get("YHKJE")+"")+ld_fkje);
		// // // }else{
		// // // jmbd_map.put("QTZF",
		// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
		// // // }
		// // // }
		// // // }
		// // // if(isFind==false){
		// // // jmbd_map.put("QTZF",
		// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
		// // // }
		// // }
		// //
		// // //遍历已结帐未汇总挂号表单
		// // for (int j = 0; j < yjzgh_list.size(); j++) {
		// // Map<String, Object> yjzgh_map = yjzgh_list.get(j);
		// // String ll_fkfs = yjzgh_map.get("FKFS") + "";
		// // Double ld_fkje = Double.parseDouble(yjzgh_map
		// // .get("FKJE") + "");
		// // if(yjzgh_map.get("CZGH").equals(jmbd_map.get("CZGH"))){
		// // if(yjzgh_map.get("FKLB").equals("1")){
		// // jmbd_map.put("XJJE",
		// // Double.parseDouble(jmbd_map.get("XJJE")+"")+ld_fkje);
		// // }else if(yjzgh_map.get("FKLB").equals("2")){
		// // jmbd_map.put("ZPJE",
		// // Double.parseDouble(jmbd_map.get("ZPJE")+"")+ld_fkje);
		// // }
		// // else if(yjzgh_map.get("FKLB").equals("4")){
		// // jmbd_map.put("HBWC",
		// // Double.parseDouble(jmbd_map.get("HBWC")+"")+ld_fkje);
		// // }
		// // else if(yjzgh_map.get("FKLB").equals("5")){
		// // jmbd_map.put("YHJE",
		// // Double.parseDouble(jmbd_map.get("YHJE")+"")+ld_fkje);
		// // }
		// // else if(yjzgh_map.get("FKLB").equals("6")){
		// // jmbd_map.put("YHKJE",
		// // Double.parseDouble(jmbd_map.get("YHKJE")+"")+ld_fkje);
		// // }else{
		// // jmbd_map.put("QTZF",
		// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
		// // }}
		// // // boolean isFind=false;
		// // // for (int k = 0; k < fkfs_list.size(); k++) {
		// // // Map<String, Object> fkfs_map = fkfs_list.get(k);
		// // // if (fkfs_map.get("FKFS").equals(ll_fkfs)) {
		// // // isFind=true;
		// // // if(fkfs_map.get("FKLB").equals("1")){
		// // // jmbd_map.put("XJJE",
		// // Double.parseDouble(jmbd_map.get("XJJE")+"")+ld_fkje);
		// // // }
		// // // else if(fkfs_map.get("FKLB").equals("2")){
		// // // jmbd_map.put("ZPJE",
		// // Double.parseDouble(jmbd_map.get("ZPJE")+"")+ld_fkje);
		// // // }
		// // // else if(fkfs_map.get("FKLB").equals("4")){
		// // // jmbd_map.put("HBWC",
		// // Double.parseDouble(jmbd_map.get("HBWC")+"")+ld_fkje);
		// // // }
		// // // else if(fkfs_map.get("FKLB").equals("5")){
		// // // jmbd_map.put("YHJE",
		// // Double.parseDouble(jmbd_map.get("YHJE")+"")+ld_fkje);
		// // // }
		// // // else if(fkfs_map.get("FKLB").equals("6")){
		// // // jmbd_map.put("YHKJE",
		// // Double.parseDouble(jmbd_map.get("YHKJE")+"")+ld_fkje);
		// // // }else{
		// // // jmbd_map.put("QTZF",
		// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
		// // // }
		// // // }
		// // // }
		// // // if(isFind==false){
		// // // jmbd_map.put("QTZF",
		// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
		// // // }
		// // }
		// // jmbd_map.put("GHRC",
		// //
		// Double.parseDouble(jmbd_map.get("GHRC")+"")+Double.parseDouble(ghrb_map.get("FPZS")+""));
		// // jmbd_map.put("GHJE",
		// //
		// Double.parseDouble(jmbd_map.get("GHJE")+"")+Double.parseDouble(ghrb_map.get("ZJJE")+""));
		// // if(ghrb_map.get("ZFZF")!=null&&ghrb_map.get("ZFZF")!=""){
		// // jmbd_map.put("ZFZF",
		// //
		// Double.parseDouble(jmbd_map.get("ZFZF")+"")+Double.parseDouble(ghrb_map.get("ZFZF")+""));
		// // }
		// // if(ghrb_map.get("QTYS")!=null&&ghrb_map.get("QTYS")!=""){
		// // jmbd_map.put("QTYS",
		// //
		// Double.parseDouble(jmbd_map.get("QTYS")+"")+Double.parseDouble(ghrb_map.get("QTYS")+""));
		// // }
		// // jmbd_list.add(jmbd_map);
		// // }
		// // if(ghrb_map.get("ZFSZ")!=null&&ghrb_map.get("ZFSZ")!=""){
		// // ll_ghth+=Double.parseDouble(ghrb_map.get("THSL")+"");
		// // }
		//
		// }
		// for (int j = 0; j < jmbd_list.size(); j++) {
		// Map<String, Object> jmbd_map = jmbd_list.get(j);
		// jmbd_map.put(
		// "TOTALAMOUNT",
		// getDoubleValue(jmbd_map.get("GHJE"))
		// + getDoubleValue(jmbd_map.get("SFJE")));
		// jmbd_map.put("HJ", 0D);
		// jmbd_map.put("CZGH",
		// DictionaryController.instance().get("phis.dictionary.doctor")
		// .getText(jmbd_map.get("CZGH") + ""));
		// // System.out.println(jmbd_map.toString());
		// }
		// records.addAll(jmbd_list);
		//

	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		String userid = user.getUserId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("jgid", jgid);
		Map<String, Object> parametersUser = new HashMap<String, Object>();
		parametersUser.put("jgid", jgid);

		// 界面表单获取数据SQL
		// String sql_jmbd =
		// " SELECT CZGH as CZGH,0 as GHRC,0.00 as GHJE,sum(FPZS) as FPZS,"
		// +
		// " sum(ZJJE) as SFJE,0.00 as XJJE,0.00 as ZPJE,sum(ZFZF) as ZFZF,sum(QTYS) as QTYS,"
		// +
		// " 0.00 as HBWC,0.00 as KHJE,0.00 AS JKJE,0.00 AS TKJE,0.00 as YHKJE,0.00 as YHJE,"
		// +
		// " 0.00 as QTZF  FROM MS_HZRB WHERE (HZRQ is NULL ) and JGID =:jgid "
		// + "  GROUP BY CZGH";
		// 收费日报表单获取数据SQL语句如下（参数:机构ID、用户ID）：
		//增加挂号优惠合计字段 zhaojian 2019-05-12
		String sql_sfrb = "select sum(FPZS) as FPZS,sum(ZJJE) as ZJJE,0 as GHYHHJ from (SELECT count(*) as FPZS,sum(ZJJE) as ZJJE from MS_MZXX where JGID =:jgid"
				+ " and JZRQ is null  "
				+ " and ZFPB = 0"
				+ " union all SELECT count(*) as FPZS,0 as ZJJE from MS_MZXX where JGID =:jgid"
				+ " and JZRQ is null  " + " and ZFPB = 1 )";
		// 挂号日报表单获取数据SQL语句如下（参数: 机构ID、用户ID）：
		String sql_ghrb = "select sum(FPZS) as FPZS,sum(ZJJE) as ZJJE,sum(GHYHHJ) as GHYHHJ from (SELECT count(*) as FPZS,sum(GHJE + ZLJE + ZJFY + BLJE) as ZJJE,sum(YHJE) as GHYHHJ "
				+ " from MS_GHMX where JGID =:jgid and JZRQ is null  "
				+ " and THBZ = 0 union all SELECT count(*) as FPZS,0 as ZJJE,0 as GHYHHJ "
				+ " from MS_GHMX where JGID =:jgid and JZRQ is null  "
				+ " and THBZ = 1)";
		// 已结帐未汇总收费表单获取数据SQL语句如下（参数:机构ID）：
		String sql_yjzsf = "select sum(b.FKJE) as FKJE,c.FKLB as FKLB,c.FKMC as FKMC,sum(a.QTYS) as QTYS "
				+ " from MS_MZXX a,MS_FKXX b left outer join GY_FKFS c on b.FKFS=c.FKFS where a.MZXH = b.MZXH AND a.JGID = b.JGID "
				+ " AND a.JGID =:jgid  and a.JZRQ is null "
				+ " and a.ZFPB = 0 group by c.FKLB,c.FKMC";
		// 已结帐未汇总挂号表单获取数据SQL语句如下（参数:机构ID）：
		String sql_yjzgh = "select sum(b.FKJE) as FKJE,c.FKLB as FKLB,c.FKMC as FKMC,sum(a.QTYS) as QTYS "
				+ " from MS_GHMX a,MS_GH_FKXX b left outer join GY_FKFS c on b.FKFS=c.FKFS where a.SBXH = b.SBXH "
				+ " AND a.JGID = :jgid  and a.JZRQ is null"
				+ " and a.THBZ = 0 group by c.FKLB,c.FKMC";
		String sql_yjzghsf = "select sum(FKJE) as FKJE,FKLB as FKLB,FKMC as FKMC,sum(QTYS) as QTYS from ("
				+ sql_yjzsf
				+ " union all "
				+ sql_yjzgh
				+ ") group by FKLB,FKMC order by FKLB";
		// 付款方式表单获取数据SQL语句如下（参数:机构ID）：
		// String sql_fkfs =
		// "SELECT FKFS as FKFS,FKMC as FKMC,FKLB as FKLB FROM GY_FKFS WHERE SYLX = 1 AND ZFBZ = 0";

		try {
			// Map<String, Object> jmbd_list = dao.doLoad(sql_jmbd,
			// parameters);
			List<Map<String, Object>> sfrb_list = dao.doSqlQuery(sql_sfrb,
					parametersUser);
			List<Map<String, Object>> ghrb_list = dao.doSqlQuery(sql_ghrb,
					parametersUser);
			if (sfrb_list == null && ghrb_list == null) {
				return;
			}
			// List<Map<String, Object>> yjzsf_list = dao.doQuery(sql_yjzsf,
			// parameters);
			// List<Map<String, Object>> yjzgh_list = dao.doQuery(sql_yjzgh,
			// parameters);
			List<Map<String, Object>> yjzghsf_list = dao.doSqlQuery(
					sql_yjzghsf, parameters);
			// List<Map<String, Object>> reList = new ArrayList<Map<String,
			// Object>>();
			// Map<String, Object> reMap = new HashMap<String,Object>();
			// response.put("CZGH",
			// DictionaryController.instance().get("phis.dictionary.doctor")
			// .getText(sfrb_map.get("CZGH") + ""));
			Integer ghcount = 0;
			Double ghAmount = 0D;
			Integer sfcount = 0;
			Double sfAmount = 0D;
			Double totals = 0D;
			Double xjAmount = 0D;
			Double zpAmount = 0D;
			Double yhkAmount = 0D;
			Double yhAmount = 0D;
			Double qtAmount = 0D;
			Double grAmount = 0D;
			Double qtysAmount = 0D;
			Double hbwcAmount = 0D;
			Double khAmount = 0D;
			Double jkAmount = 0D;
			Double tkAmount = 0D;
			Double finalAmount = 0D;
			Double ghyhhjAmount = 0D;//增加挂号优惠合计 zhaojian 2019-05-12
			if (ghrb_list != null && ghrb_list.size() > 0) {
				Map<String, Object> ghrb_map = ghrb_list.get(0);
				if (ghrb_map != null) {
					ghcount = getIntValue(ghrb_map.get("FPZS"));
					ghAmount = getDoubleValue(ghrb_map.get("ZJJE"));
					ghyhhjAmount = getDoubleValue(ghrb_map.get("GHYHHJ"));//增加挂号优惠合计 zhaojian 2019-05-12
				} else {
					ghcount = 0;
					ghAmount = 0D;
				}
			}
			if (sfrb_list != null && sfrb_list.size() > 0) {
				Map<String, Object> sfrb_map = sfrb_list.get(0);
				if (sfrb_map != null) {
					sfcount = getIntValue(sfrb_map.get("FPZS"));
					sfAmount = getDoubleValue(sfrb_map.get("ZJJE"));
				} else {
					sfcount = 0;
					sfAmount = 0D;
				}
			}
			totals = ghAmount + sfAmount;

			StringBuffer sbfkxxAmount = new StringBuffer();
			double qtys = 0;
			for (int i = 0; i < yjzghsf_list.size(); i++) {
				Map<String, Object> yjzghsf_map = yjzghsf_list.get(i);
				// String ll_fkfs = yjzsf_map.get("FKFS") + "";
				// Double ld_fkje = getDoubleValue(yjzghsf_map.get("FKJE"));
				// Double ld_qtys = getDoubleValue(yjzghsf_map.get("QTYS"));
				// if ("1".equals(yjzghsf_map.get("FKLB") + "")) {
				// xjAmount = ld_fkje;
				// } else if ("2".equals(yjzghsf_map.get("FKLB") + "")) {
				// zpAmount = ld_fkje;
				// } else if ("4".equals(yjzghsf_map.get("FKLB") + "")) {
				// hbwcAmount = ld_fkje;
				// } else if ("5".equals(yjzghsf_map.get("FKLB") + "")) {
				// yhAmount = ld_fkje;
				// } else if ("6".equals(yjzghsf_map.get("FKLB") + "")) {
				// yhkAmount = ld_fkje;
				// response.put("yhkAmount", ld_fkje);
				// } else {
				// qtAmount += ld_fkje;
				// }
				// if (!"4".equals(yjzghsf_map.get("FKLB") + "")) {
				// qtysAmount += ld_qtys;
				// }
				if (getDoubleValue(yjzghsf_map.get("FKJE")) != 0) {
					if(sbfkxxAmount.length()>0){
						sbfkxxAmount.append(",");
					}
					sbfkxxAmount
							.append(yjzghsf_map.get("FKMC"))
							.append(":")
							.append(String.format("%1$.2f",
									yjzghsf_map.get("FKJE")));
				}
				if (getDoubleValue(yjzghsf_map.get("QTYS")) != 0) {
					qtys += Double.parseDouble(yjzghsf_map.get("QTYS")+"");
				}
			}
			//增加挂号优惠合计 zhaojian 2019-05-12
			sbfkxxAmount.append(",优惠:").append(String.format("%1$.2f",ghyhhjAmount));
			if (qtys > 0) {
				if(sbfkxxAmount.length()>0){
					sbfkxxAmount.append(",");
				}
				sbfkxxAmount.append("其它应收:").append(
						String.format("%1$.2f", qtys));
			}
//			if (sbfkxxAmount.toString().lastIndexOf(",") > 0) {
//				response.put(
//						"fkxxAmount",
//						sbfkxxAmount.toString().substring(0,
//								sbfkxxAmount.toString().lastIndexOf(",")));
//			} else {
				response.put("fkxxAmount", sbfkxxAmount.toString());
//			}
			response.put("ghcount", ghcount.toString());
			response.put("ghAmount", String.format("%1$.2f", ghAmount));
			response.put("sfcount", sfcount.toString());
			response.put("sfAmount", String.format("%1$.2f", sfAmount));
			response.put("totals", String.format("%1$.2f", totals));
			response.put("xjAmount", String.format("%1$.2f", xjAmount));
			response.put("zpAmount", String.format("%1$.2f", zpAmount));
			response.put("yhkAmount", String.format("%1$.2f", yhkAmount));
			response.put("yhAmount", String.format("%1$.2f", yhAmount));
			response.put("qtAmount", String.format("%1$.2f", qtAmount));
			response.put("grAmount", String.format("%1$.2f", grAmount));
			response.put("qtysAmount", String.format("%1$.2f", qtysAmount));
			response.put("hbwcAmount", String.format("%1$.2f", hbwcAmount));
			response.put("khAmount", String.format("%1$.2f", khAmount));
			response.put("jkAmount", String.format("%1$.2f", jkAmount));
			response.put("tkAmount", String.format("%1$.2f", tkAmount));
			response.put("finalAmount", String.format("%1$.2f", finalAmount));
			String userName = user.getUserName();// 用户名
			String jgName = user.getManageUnit().getName();// 用户的机构名称
			response.put("preparedby", jgName);
			response.put("Lister", userName);
			response.put("DateTabling", BSHISUtil.getDate());
			long ll_ghth = dao.doCount("MS_GHMX",
					"JGID =:jgid and JZRQ is null and THBZ=1", parametersUser);
			response.put("GHTH", ll_ghth + "");
			List<Map<String, Object>> ll_zffpObj = dao
					.doQuery(
							"select FPHM as FPHM from MS_MZXX where JGID =:jgid and JZRQ is null and ZFPB=1",
							parametersUser);
			int ll_zffp = ll_zffpObj.size();
			response.put("ZFFP", ll_zffp + "");
			String zffpstr = "";
			for (int i = 0; i < ll_zffpObj.size(); i++) {
				Map<String, Object> map = ll_zffpObj.get(i);
				if (i == 0) {
					zffpstr = map.get("FPHM") + "";
				} else {
					zffpstr += "," + map.get("FPHM");
				}
			}
			response.put("ZFFPSTR", zffpstr);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		// List<Map<String, Object>> fkfs_list = dao.doQuery(sql_fkfs,
		// parameters);
		// 填收费日报信息
		// 遍历收费日报表单
		// Double ll_zffp = 0D;
		// for (int i = 0; i < sfrb_list.size(); i++) {
		// Map<String, Object> sfrb_map = sfrb_list.get(i);
		// String ls_ygdm = sfrb_map.get("CZGH") + "";
		// String ll_find = "";
		// for (int j = 0; j < jmbd_list.size(); j++) {
		// Map<String, Object> jmbd_map = jmbd_list.get(j);
		// if (ls_ygdm.equals(jmbd_map.get("CZGH")+"")) {
		// ll_find = jmbd_map.get("CZGH") + "";
		// break;
		// }
		// }
		// if (ll_find.endsWith("")) {
		// Map<String, Object> jmbd_map = new HashMap<String, Object>();
		// jmbd_map.put("CZGH", ls_ygdm);
		// jmbd_map.put("GHRC", 0);
		// jmbd_map.put("GHJE", 0D);
		// jmbd_map.put("FPZS", 0);
		// jmbd_map.put("SFJE", 0D);
		// jmbd_map.put("XJJE", 0D);
		// jmbd_map.put("ZPJE", 0D);
		// jmbd_map.put("YHJE", 0D);
		// jmbd_map.put("YHKJE", 0D);
		// jmbd_map.put("QTZF", 0D);
		// jmbd_map.put("ZFZF", 0D);
		// jmbd_map.put("QTYS", 0D);
		// jmbd_map.put("HBWC", 0D);
		// jmbd_map.put("KHJE", 0D);
		// jmbd_map.put("JKJE", 0D);
		// jmbd_map.put("TKJE", 0D);
		//
		// jmbd_map.put("GRZH", 0D);
		// jmbd_map.put("ZJJE", 0D);
		//
		// // 遍历已结帐未汇总收费表单
		// for (int j = 0; j < yjzsf_list.size(); j++) {
		// Map<String, Object> yjzsf_map = yjzsf_list.get(j);
		// // String ll_fkfs = yjzsf_map.get("FKFS") + "";
		// Double ld_fkje = getDoubleValue(yjzsf_map
		// .get("FKJE"));
		// if (yjzsf_map.get("CZGH").equals(jmbd_map.get("CZGH"))) {
		// if (yjzsf_map.get("FKLB").equals("1")) {
		// jmbd_map.put(
		// "XJJE",
		// getDoubleValue(jmbd_map.get("XJJE"))
		// + ld_fkje);
		// } else if (yjzsf_map.get("FKLB").equals("2")) {
		// jmbd_map.put(
		// "ZPJE",
		// getDoubleValue(jmbd_map.get("ZPJE"))
		// + ld_fkje);
		// } else if (yjzsf_map.get("FKLB").equals("4")) {
		// jmbd_map.put(
		// "HBWC",
		// getDoubleValue(jmbd_map.get("HBWC"))
		// + ld_fkje);
		// } else if (yjzsf_map.get("FKLB").equals("5")) {
		// jmbd_map.put(
		// "YHJE",
		// getDoubleValue(jmbd_map.get("YHJE"))
		// + ld_fkje);
		// } else if (yjzsf_map.get("FKLB").equals("6")) {
		// jmbd_map.put(
		// "YHKJE",
		// getDoubleValue(jmbd_map
		// .get("YHKJE"))
		// + ld_fkje);
		// } else {
		// jmbd_map.put(
		// "QTZF",
		// getDoubleValue(jmbd_map.get("QTZF"))
		// + ld_fkje);
		// }
		// // yjzsf_map.putAll(jmbd_map);
		// }
		//
		// // boolean isFind=false;
		// // for (Map<String, Object> fkfs_map: fkfs_list) {
		// // if (fkfs_map.get("FKFS").equals(ll_fkfs)) {
		// // isFind=true;
		// // if(fkfs_map.get("FKLB").equals("1")){
		// // jmbd_map.put("XJJE",
		// // Double.parseDouble(jmbd_map.get("XJJE")+"")+ld_fkje);
		// // }
		// // else if(fkfs_map.get("FKLB").equals("2")){
		// // jmbd_map.put("ZPJE",
		// // Double.parseDouble(jmbd_map.get("ZPJE")+"")+ld_fkje);
		// // }
		// // else if(fkfs_map.get("FKLB").equals("4")){
		// // jmbd_map.put("HBWC",
		// // Double.parseDouble(jmbd_map.get("HBWC")+"")+ld_fkje);
		// // }
		// // else if(fkfs_map.get("FKLB").equals("5")){
		// // jmbd_map.put("YHJE",
		// // Double.parseDouble(jmbd_map.get("YHJE")+"")+ld_fkje);
		// // }
		// // else if(fkfs_map.get("FKLB").equals("6")){
		// // jmbd_map.put("YHKJE",
		// // Double.parseDouble(jmbd_map.get("YHKJE")+"")+ld_fkje);
		// // }else{
		// // jmbd_map.put("QTZF",
		// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
		// // }
		// // }
		// // }
		// // if(isFind==false){
		// // jmbd_map.put("QTZF",
		// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
		// // }
		// }
		//
		// // 遍历已结帐未汇总挂号表单
		// for (int j = 0; j < yjzgh_list.size(); j++) {
		// Map<String, Object> yjzgh_map = yjzgh_list.get(j);
		// String ll_fkfs = yjzgh_map.get("FKFS") + "";
		// Double ld_fkje = getDoubleValue(yjzgh_map
		// .get("FKJE"));
		// if (yjzgh_map.get("CZGH").equals(jmbd_map.get("CZGH"))) {
		// if (yjzgh_map.get("FKLB").equals("1")) {
		// jmbd_map.put(
		// "XJJE",
		// getDoubleValue(jmbd_map.get("XJJE"))
		// + ld_fkje);
		// } else if (yjzgh_map.get("FKLB").equals("2")) {
		// jmbd_map.put(
		// "ZPJE",
		// getDoubleValue(jmbd_map.get("ZPJE"))
		// + ld_fkje);
		// } else if (yjzgh_map.get("FKLB").equals("4")) {
		// jmbd_map.put(
		// "HBWC",
		// getDoubleValue(jmbd_map.get("HBWC"))
		// + ld_fkje);
		// } else if (yjzgh_map.get("FKLB").equals("5")) {
		// jmbd_map.put(
		// "YHJE",
		// getDoubleValue(jmbd_map.get("YHJE"))
		// + ld_fkje);
		// } else if (yjzgh_map.get("FKLB").equals("6")) {
		// jmbd_map.put(
		// "YHKJE",
		// getDoubleValue(jmbd_map
		// .get("YHKJE"))
		// + ld_fkje);
		// } else {
		// jmbd_map.put(
		// "QTZF",
		// getDoubleValue(jmbd_map.get("QTZF"))
		// + ld_fkje);
		// }
		// // yjzgh_map.putAll(jmbd_map);
		// }
		// // boolean isFind=false;
		// // for (int k = 0; k < fkfs_list.size(); k++) {
		// // Map<String, Object> fkfs_map = fkfs_list.get(k);
		// // if (fkfs_map.get("FKFS").equals(ll_fkfs)) {
		// // isFind=true;
		// // if(fkfs_map.get("FKLB").equals("1")){
		// // jmbd_map.put("XJJE",
		// // Double.parseDouble(jmbd_map.get("XJJE")+"")+ld_fkje);
		// // }
		// // else if(fkfs_map.get("FKLB").equals("2")){
		// // jmbd_map.put("ZPJE",
		// // Double.parseDouble(jmbd_map.get("ZPJE")+"")+ld_fkje);
		// // }
		// // else if(fkfs_map.get("FKLB").equals("4")){
		// // jmbd_map.put("HBWC",
		// // Double.parseDouble(jmbd_map.get("HBWC")+"")+ld_fkje);
		// // }
		// // else if(fkfs_map.get("FKLB").equals("5")){
		// // jmbd_map.put("YHJE",
		// // Double.parseDouble(jmbd_map.get("YHJE")+"")+ld_fkje);
		// // }
		// // else if(fkfs_map.get("FKLB").equals("6")){
		// // jmbd_map.put("YHKJE",
		// // Double.parseDouble(jmbd_map.get("YHKJE")+"")+ld_fkje);
		// // }else{
		// // jmbd_map.put("QTZF",
		// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
		// // }
		// // }
		// // }
		// // if(isFind==false){
		// // jmbd_map.put("QTZF",
		// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
		// // }
		// }
		// jmbd_map.put(
		// "FPZS",
		// getIntValue(jmbd_map.get("FPZS"))
		// + getIntValue(sfrb_map.get("FPZS")));
		// jmbd_map.put(
		// "SFJE",
		// getDoubleValue(jmbd_map.get("SFJE"))
		// + getDoubleValue(sfrb_map.get("ZJJE")));
		// if (sfrb_map.get("ZFZF") != null
		// && sfrb_map.get("ZFZF") != "") {
		// jmbd_map.put(
		// "ZFZF",
		// Double.parseDouble(jmbd_map.get("ZFZF") + "")
		// + Double.parseDouble(sfrb_map
		// .get("ZFZF") + ""));
		// }
		// if (sfrb_map.get("QTYS") != null
		// && sfrb_map.get("QTYS") != "") {
		// jmbd_map.put(
		// "QTYS",
		// Double.parseDouble(jmbd_map.get("QTYS") + "")
		// + Double.parseDouble(sfrb_map
		// .get("QTYS") + ""));
		// }
		// // jmbd_list.get(0).putAll(jmbd_map);
		// jmbd_list.add(jmbd_map);
		// }
		// // if (sfrb_map.get("ZFSZ") != null && sfrb_map.get("ZFSZ") != "") {
		// // ll_zffp += Double.parseDouble(sfrb_map.get("ZFSZ") + "");
		// // }
		//
		// }
		//
		// // 填挂号日报信息
		// // 遍历挂号日报表单
		// // Double ll_ghth = 0D;
		// for (int i = 0; i < ghrb_list.size(); i++) {
		// Map<String, Object> ghrb_map = ghrb_list.get(i);
		// String ls_ygdm = ghrb_map.get("CZGH") + "";
		// String ll_find = "";
		// for (int j = 0; j < jmbd_list.size(); j++) {
		// Map<String, Object> jmbd_map = jmbd_list.get(j);
		// if (ls_ygdm.equals(jmbd_map.get("CZGH"))) {
		// ll_find = jmbd_map.get("CZGH") + "";
		// jmbd_map.put("GHRC",
		// getIntValue(ghrb_map.get("FPZS")));
		// jmbd_map.put("GHJE",
		// getDoubleValue(ghrb_map.get("ZJJE")));
		// break;
		// }
		// }
		// // if (ll_find.endsWith("")) {
		// // Map<String, Object> jmbd_map = new HashMap<String, Object>();
		// // jmbd_map.put("CZGH", ls_ygdm);
		// // jmbd_map.put("GHRC", 0D);
		// // jmbd_map.put("GHJE", 0D);
		// // jmbd_map.put("FPZS", 0D);
		// // jmbd_map.put("SFJE", 0D);
		// // jmbd_map.put("XJJE", 0D);
		// // jmbd_map.put("ZPJE", 0D);
		// // jmbd_map.put("YHJE", 0D);
		// // jmbd_map.put("YHKJE", 0D);
		// // jmbd_map.put("QTZF", 0D);
		// // jmbd_map.put("ZFZF", 0D);
		// // jmbd_map.put("QTYS", 0D);
		// // jmbd_map.put("HBWC", 0D);
		// // jmbd_map.put("KHJE", 0D);
		// // jmbd_map.put("JKJE", 0D);
		// // jmbd_map.put("TKJE", 0D);
		// //
		// // jmbd_map.put("GRZH", 0D);
		// // jmbd_map.put("ZJJE", 0D);
		// //
		// // //遍历已结帐未汇总收费表单
		// // for (int j = 0; j < yjzsf_list.size(); j++) {
		// // Map<String, Object> yjzsf_map = yjzsf_list.get(j);
		// // String ll_fkfs = yjzsf_map.get("FKFS") + "";
		// // Double ld_fkje = Double.parseDouble(yjzsf_map
		// // .get("FKJE") + "");
		// // if(yjzsf_map.get("CZGH").equals(jmbd_map.get("CZGH"))){
		// // if(yjzsf_map.get("FKLB").equals("1")){
		// // jmbd_map.put("XJJE",
		// // Double.parseDouble(jmbd_map.get("XJJE")+"")+ld_fkje);
		// // }else if(yjzsf_map.get("FKLB").equals("2")){
		// // jmbd_map.put("ZPJE",
		// // Double.parseDouble(jmbd_map.get("ZPJE")+"")+ld_fkje);
		// // }
		// // else if(yjzsf_map.get("FKLB").equals("4")){
		// // jmbd_map.put("HBWC",
		// // Double.parseDouble(jmbd_map.get("HBWC")+"")+ld_fkje);
		// // }
		// // else if(yjzsf_map.get("FKLB").equals("5")){
		// // jmbd_map.put("YHJE",
		// // Double.parseDouble(jmbd_map.get("YHJE")+"")+ld_fkje);
		// // }
		// // else if(yjzsf_map.get("FKLB").equals("6")){
		// // jmbd_map.put("YHKJE",
		// // Double.parseDouble(jmbd_map.get("YHKJE")+"")+ld_fkje);
		// // }else{
		// // jmbd_map.put("QTZF",
		// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
		// // }}
		// // // boolean isFind=false;
		// // // for (int k = 0; k < fkfs_list.size(); k++) {
		// // // Map<String, Object> fkfs_map = fkfs_list.get(k);
		// // // if (fkfs_map.get("FKFS").equals(ll_fkfs)) {
		// // // isFind=true;
		// // // if(fkfs_map.get("FKLB").equals("1")){
		// // // jmbd_map.put("XJJE",
		// // Double.parseDouble(jmbd_map.get("XJJE")+"")+ld_fkje);
		// // // }
		// // // else if(fkfs_map.get("FKLB").equals("2")){
		// // // jmbd_map.put("ZPJE",
		// // Double.parseDouble(jmbd_map.get("ZPJE")+"")+ld_fkje);
		// // // }
		// // // else if(fkfs_map.get("FKLB").equals("4")){
		// // // jmbd_map.put("HBWC",
		// // Double.parseDouble(jmbd_map.get("HBWC")+"")+ld_fkje);
		// // // }
		// // // else if(fkfs_map.get("FKLB").equals("5")){
		// // // jmbd_map.put("YHJE",
		// // Double.parseDouble(jmbd_map.get("YHJE")+"")+ld_fkje);
		// // // }
		// // // else if(fkfs_map.get("FKLB").equals("6")){
		// // // jmbd_map.put("YHKJE",
		// // Double.parseDouble(jmbd_map.get("YHKJE")+"")+ld_fkje);
		// // // }else{
		// // // jmbd_map.put("QTZF",
		// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
		// // // }
		// // // }
		// // // }
		// // // if(isFind==false){
		// // // jmbd_map.put("QTZF",
		// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
		// // // }
		// // }
		// //
		// // //遍历已结帐未汇总挂号表单
		// // for (int j = 0; j < yjzgh_list.size(); j++) {
		// // Map<String, Object> yjzgh_map = yjzgh_list.get(j);
		// // String ll_fkfs = yjzgh_map.get("FKFS") + "";
		// // Double ld_fkje = Double.parseDouble(yjzgh_map
		// // .get("FKJE") + "");
		// // if(yjzgh_map.get("CZGH").equals(jmbd_map.get("CZGH"))){
		// // if(yjzgh_map.get("FKLB").equals("1")){
		// // jmbd_map.put("XJJE",
		// // Double.parseDouble(jmbd_map.get("XJJE")+"")+ld_fkje);
		// // }else if(yjzgh_map.get("FKLB").equals("2")){
		// // jmbd_map.put("ZPJE",
		// // Double.parseDouble(jmbd_map.get("ZPJE")+"")+ld_fkje);
		// // }
		// // else if(yjzgh_map.get("FKLB").equals("4")){
		// // jmbd_map.put("HBWC",
		// // Double.parseDouble(jmbd_map.get("HBWC")+"")+ld_fkje);
		// // }
		// // else if(yjzgh_map.get("FKLB").equals("5")){
		// // jmbd_map.put("YHJE",
		// // Double.parseDouble(jmbd_map.get("YHJE")+"")+ld_fkje);
		// // }
		// // else if(yjzgh_map.get("FKLB").equals("6")){
		// // jmbd_map.put("YHKJE",
		// // Double.parseDouble(jmbd_map.get("YHKJE")+"")+ld_fkje);
		// // }else{
		// // jmbd_map.put("QTZF",
		// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
		// // }}
		// // // boolean isFind=false;
		// // // for (int k = 0; k < fkfs_list.size(); k++) {
		// // // Map<String, Object> fkfs_map = fkfs_list.get(k);
		// // // if (fkfs_map.get("FKFS").equals(ll_fkfs)) {
		// // // isFind=true;
		// // // if(fkfs_map.get("FKLB").equals("1")){
		// // // jmbd_map.put("XJJE",
		// // Double.parseDouble(jmbd_map.get("XJJE")+"")+ld_fkje);
		// // // }
		// // // else if(fkfs_map.get("FKLB").equals("2")){
		// // // jmbd_map.put("ZPJE",
		// // Double.parseDouble(jmbd_map.get("ZPJE")+"")+ld_fkje);
		// // // }
		// // // else if(fkfs_map.get("FKLB").equals("4")){
		// // // jmbd_map.put("HBWC",
		// // Double.parseDouble(jmbd_map.get("HBWC")+"")+ld_fkje);
		// // // }
		// // // else if(fkfs_map.get("FKLB").equals("5")){
		// // // jmbd_map.put("YHJE",
		// // Double.parseDouble(jmbd_map.get("YHJE")+"")+ld_fkje);
		// // // }
		// // // else if(fkfs_map.get("FKLB").equals("6")){
		// // // jmbd_map.put("YHKJE",
		// // Double.parseDouble(jmbd_map.get("YHKJE")+"")+ld_fkje);
		// // // }else{
		// // // jmbd_map.put("QTZF",
		// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
		// // // }
		// // // }
		// // // }
		// // // if(isFind==false){
		// // // jmbd_map.put("QTZF",
		// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
		// // // }
		// // }
		// // jmbd_map.put("GHRC",
		// //
		// Double.parseDouble(jmbd_map.get("GHRC")+"")+Double.parseDouble(ghrb_map.get("FPZS")+""));
		// // jmbd_map.put("GHJE",
		// //
		// Double.parseDouble(jmbd_map.get("GHJE")+"")+Double.parseDouble(ghrb_map.get("ZJJE")+""));
		// // if(ghrb_map.get("ZFZF")!=null&&ghrb_map.get("ZFZF")!=""){
		// // jmbd_map.put("ZFZF",
		// //
		// Double.parseDouble(jmbd_map.get("ZFZF")+"")+Double.parseDouble(ghrb_map.get("ZFZF")+""));
		// // }
		// // if(ghrb_map.get("QTYS")!=null&&ghrb_map.get("QTYS")!=""){
		// // jmbd_map.put("QTYS",
		// //
		// Double.parseDouble(jmbd_map.get("QTYS")+"")+Double.parseDouble(ghrb_map.get("QTYS")+""));
		// // }
		// // jmbd_list.add(jmbd_map);
		// // }
		// // if(ghrb_map.get("ZFSZ")!=null&&ghrb_map.get("ZFSZ")!=""){
		// // ll_ghth+=Double.parseDouble(ghrb_map.get("THSL")+"");
		// // }
		//
		// }
		// for (int j = 0; j < jmbd_list.size(); j++) {
		// Map<String, Object> jmbd_map = jmbd_list.get(j);
		// jmbd_map.put(
		// "TOTALAMOUNT",
		// getDoubleValue(jmbd_map.get("GHJE"))
		// + getDoubleValue(jmbd_map.get("SFJE")));
		// jmbd_map.put("HJ", 0D);
		// jmbd_map.put("CZGH",
		// DictionaryController.instance().get("phis.dictionary.doctor")
		// .getText(jmbd_map.get("CZGH") + ""));
		// // System.out.println(jmbd_map.toString());
		// }
		// records.addAll(jmbd_list);
		//

	}

	// BaseDAO dao = new BaseDAO(ctx);
	// UserRoleToken user = UserRoleToken.getCurrent();
	// String jgid = user.getManageUnit().getId();
	// String userid = user.getUserId();
	// Map<String, Object> parameters = new HashMap<String, Object>();
	// parameters.put("jgid", jgid);
	// Map<String, Object> parametersUser = new HashMap<String, Object>();
	// parametersUser.put("jgid", jgid);
	// parametersUser.put("userid", userid);
	//
	// // 界面表单获取数据SQL
	// String sql_jmbd =
	// " SELECT CZGH as CZGH,0 as GHRC,0.00 as GHJE,sum(FPZS) as FPZS,"
	// +
	// " sum(ZJJE) as SFJE,0.00 as XJJE,0.00 as ZPJE,sum(ZFZF) as ZFZF,sum(QTYS) as QTYS,"
	// +
	// " 0.00 as HBWC,0.00 as KHJE,0.00 AS JKJE,0.00 AS TKJE,0.00 as YHKJE,0.00 as YHJE,"
	// + " 0.00 as QTZF  FROM MS_HZRB WHERE (HZRQ is NULL ) and JGID =:jgid "
	// + "  GROUP BY CZGH";
	// // 收费日报表单获取数据SQL语句如下（参数:机构ID、用户ID）：
	// String sql_sfrb =
	// "SELECT CZGH as CZGH,count(*) as FPZS,sum(ZJJE) as ZJJE,sum(XJJE) as XJJE,sum(ZPJE) as ZPJE,"
	// +
	// " sum(ZHJE) as ZHJE,sum(QTYS) as QTYS,sum(HBWC) as HBWC from MS_MZXX where JGID =:jgid"
	// + " and JZRQ is null  "
	// + " and CZGH =:userid "
	// + " group by CZGH ";
	// // 挂号日报表单获取数据SQL语句如下（参数: 机构ID、用户ID）：
	// String sql_ghrb =
	// "SELECT CZGH as CZGH,count(*) as FPZS,sum(GHJE + ZLJE + ZJFY + BLJE) as ZJJE,sum(XJJE) as XJJE,"
	// +
	// " sum(ZPJE) as ZPJE,sum(ZHJE) as ZHJE,sum(QTYS) as QTYS,sum(HBWC) as HBWC "
	// + " from MS_GHMX where JGID =:jgid and JZRQ is null  "
	// + " and CZGH =:userid " + " group by CZGH";
	// // 已结帐未汇总收费表单获取数据SQL语句如下（参数:机构ID）：
	// String sql_yjzsf =
	// "select a.CZGH as CZGH,b.FKFS as FKFS,sum(b.FKJE) as FKJE,c.FKLB as FKLB "
	// +
	// " from MS_MZXX a,MS_FKXX b,GY_FKFS c where a.MZXH = b.MZXH AND a.JGID = b.JGID "
	// + " AND a.JGID =:jgid  and a.JZRQ is null "
	// + " and c.SYLX=1 and c.ZFBZ=0 and b.FKFS=c.FKFS"
	// + " and a.ZFPB = 0 group by a.CZGH,b.FKFS,c.FKLB";
	// // 已结帐未汇总挂号表单获取数据SQL语句如下（参数:机构ID）：
	// String sql_yjzgh =
	// "select a.CZGH as CZGH,b.FKFS as FKFS,sum(b.FKJE) as FKJE,c.FKLB as FKLB "
	// + " from MS_GHMX a,MS_GH_FKXX b,GY_FKFS c where a.SBXH = b.SBXH "
	// + " AND a.JGID = :jgid  and a.JZRQ is null"
	// + " and c.SYLX=1 and c.ZFBZ=0 and b.FKFS=c.FKFS"
	// + " and a.THBZ = 0 group by a.CZGH,b.FKFS,c.FKLB";
	// // 付款方式表单获取数据SQL语句如下（参数:机构ID）：
	// String sql_fkfs =
	// "SELECT FKFS as FKFS,FKMC as FKMC,FKLB as FKLB FROM GY_FKFS WHERE SYLX = 1 AND ZFBZ = 0";
	//
	// try {
	// List<Map<String, Object>> jmbd_list = dao.doQuery(sql_jmbd,
	// parameters);
	// List<Map<String, Object>> sfrb_list = dao.doQuery(sql_sfrb,
	// parametersUser);
	// List<Map<String, Object>> ghrb_list = dao.doQuery(sql_ghrb,
	// parametersUser);
	// List<Map<String, Object>> yjzsf_list = dao.doQuery(sql_yjzsf,
	// parameters);
	// List<Map<String, Object>> yjzgh_list = dao.doQuery(sql_yjzgh,
	// parameters);
	// parameters.clear();
	// List<Map<String, Object>> fkfs_list = dao.doQuery(sql_fkfs,
	// parameters);
	// // 填收费日报信息
	// // 遍历收费日报表单
	// // Double ll_zffp = 0D;
	// for (int i = 0; i < sfrb_list.size(); i++) {
	// Map<String, Object> sfrb_map = sfrb_list.get(i);
	// String ls_ygdm = sfrb_map.get("CZGH") + "";
	// String ll_find = "";
	// for (int j = 0; j < jmbd_list.size(); j++) {
	// Map<String, Object> jmbd_map = jmbd_list.get(j);
	// if (ls_ygdm.equals(jmbd_map.get("CZGH")+"")) {
	// ll_find = jmbd_map.get("CZGH") + "";
	// break;
	// }
	// }
	// if (ll_find.endsWith("")) {
	// Map<String, Object> jmbd_map = new HashMap<String, Object>();
	// jmbd_map.put("CZGH", ls_ygdm);
	// jmbd_map.put("GHRC", 0);
	// jmbd_map.put("GHJE", 0D);
	// jmbd_map.put("FPZS", 0);
	// jmbd_map.put("SFJE", 0D);
	// jmbd_map.put("XJJE", 0D);
	// jmbd_map.put("ZPJE", 0D);
	// jmbd_map.put("YHJE", 0D);
	// jmbd_map.put("YHKJE", 0D);
	// jmbd_map.put("QTZF", 0D);
	// jmbd_map.put("ZFZF", 0D);
	// jmbd_map.put("QTYS", 0D);
	// jmbd_map.put("HBWC", 0D);
	// jmbd_map.put("KHJE", 0D);
	// jmbd_map.put("JKJE", 0D);
	// jmbd_map.put("TKJE", 0D);
	//
	// jmbd_map.put("GRZH", 0D);
	// jmbd_map.put("ZJJE", 0D);
	//
	// // 遍历已结帐未汇总收费表单
	// for (int j = 0; j < yjzsf_list.size(); j++) {
	// Map<String, Object> yjzsf_map = yjzsf_list.get(j);
	// String ll_fkfs = yjzsf_map.get("FKFS") + "";
	// Double ld_fkje = getDoubleValue(yjzsf_map
	// .get("FKJE"));
	// if (yjzsf_map.get("CZGH").equals(jmbd_map.get("CZGH"))) {
	// if (yjzsf_map.get("FKLB").equals("1")) {
	// jmbd_map.put(
	// "XJJE",
	// getDoubleValue(jmbd_map.get("XJJE"))
	// + ld_fkje);
	// } else if (yjzsf_map.get("FKLB").equals("2")) {
	// jmbd_map.put(
	// "ZPJE",
	// getDoubleValue(jmbd_map.get("ZPJE"))
	// + ld_fkje);
	// } else if (yjzsf_map.get("FKLB").equals("4")) {
	// jmbd_map.put(
	// "HBWC",
	// getDoubleValue(jmbd_map.get("HBWC"))
	// + ld_fkje);
	// } else if (yjzsf_map.get("FKLB").equals("5")) {
	// jmbd_map.put(
	// "YHJE",
	// getDoubleValue(jmbd_map.get("YHJE"))
	// + ld_fkje);
	// } else if (yjzsf_map.get("FKLB").equals("6")) {
	// jmbd_map.put(
	// "YHKJE",
	// getDoubleValue(jmbd_map
	// .get("YHKJE"))
	// + ld_fkje);
	// } else {
	// jmbd_map.put(
	// "QTZF",
	// getDoubleValue(jmbd_map.get("QTZF"))
	// + ld_fkje);
	// }
	// }
	//
	// // boolean isFind=false;
	// // for (Map<String, Object> fkfs_map: fkfs_list) {
	// // if (fkfs_map.get("FKFS").equals(ll_fkfs)) {
	// // isFind=true;
	// // if(fkfs_map.get("FKLB").equals("1")){
	// // jmbd_map.put("XJJE",
	// // Double.parseDouble(jmbd_map.get("XJJE")+"")+ld_fkje);
	// // }
	// // else if(fkfs_map.get("FKLB").equals("2")){
	// // jmbd_map.put("ZPJE",
	// // Double.parseDouble(jmbd_map.get("ZPJE")+"")+ld_fkje);
	// // }
	// // else if(fkfs_map.get("FKLB").equals("4")){
	// // jmbd_map.put("HBWC",
	// // Double.parseDouble(jmbd_map.get("HBWC")+"")+ld_fkje);
	// // }
	// // else if(fkfs_map.get("FKLB").equals("5")){
	// // jmbd_map.put("YHJE",
	// // Double.parseDouble(jmbd_map.get("YHJE")+"")+ld_fkje);
	// // }
	// // else if(fkfs_map.get("FKLB").equals("6")){
	// // jmbd_map.put("YHKJE",
	// // Double.parseDouble(jmbd_map.get("YHKJE")+"")+ld_fkje);
	// // }else{
	// // jmbd_map.put("QTZF",
	// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
	// // }
	// // }
	// // }
	// // if(isFind==false){
	// // jmbd_map.put("QTZF",
	// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
	// // }
	// }
	//
	// // 遍历已结帐未汇总挂号表单
	// for (int j = 0; j < yjzgh_list.size(); j++) {
	// Map<String, Object> yjzgh_map = yjzgh_list.get(j);
	// String ll_fkfs = yjzgh_map.get("FKFS") + "";
	// Double ld_fkje = getDoubleValue(yjzgh_map
	// .get("FKJE"));
	// if (yjzgh_map.get("CZGH").equals(jmbd_map.get("CZGH"))) {
	// if (yjzgh_map.get("FKLB").equals("1")) {
	// jmbd_map.put(
	// "XJJE",
	// getDoubleValue(jmbd_map.get("XJJE"))
	// + ld_fkje);
	// } else if (yjzgh_map.get("FKLB").equals("2")) {
	// jmbd_map.put(
	// "ZPJE",
	// getDoubleValue(jmbd_map.get("ZPJE"))
	// + ld_fkje);
	// } else if (yjzgh_map.get("FKLB").equals("4")) {
	// jmbd_map.put(
	// "HBWC",
	// getDoubleValue(jmbd_map.get("HBWC"))
	// + ld_fkje);
	// } else if (yjzgh_map.get("FKLB").equals("5")) {
	// jmbd_map.put(
	// "YHJE",
	// getDoubleValue(jmbd_map.get("YHJE"))
	// + ld_fkje);
	// } else if (yjzgh_map.get("FKLB").equals("6")) {
	// jmbd_map.put(
	// "YHKJE",
	// getDoubleValue(jmbd_map
	// .get("YHKJE"))
	// + ld_fkje);
	// } else {
	// jmbd_map.put(
	// "QTZF",
	// getDoubleValue(jmbd_map.get("QTZF"))
	// + ld_fkje);
	// }
	// }
	// // boolean isFind=false;
	// // for (int k = 0; k < fkfs_list.size(); k++) {
	// // Map<String, Object> fkfs_map = fkfs_list.get(k);
	// // if (fkfs_map.get("FKFS").equals(ll_fkfs)) {
	// // isFind=true;
	// // if(fkfs_map.get("FKLB").equals("1")){
	// // jmbd_map.put("XJJE",
	// // Double.parseDouble(jmbd_map.get("XJJE")+"")+ld_fkje);
	// // }
	// // else if(fkfs_map.get("FKLB").equals("2")){
	// // jmbd_map.put("ZPJE",
	// // Double.parseDouble(jmbd_map.get("ZPJE")+"")+ld_fkje);
	// // }
	// // else if(fkfs_map.get("FKLB").equals("4")){
	// // jmbd_map.put("HBWC",
	// // Double.parseDouble(jmbd_map.get("HBWC")+"")+ld_fkje);
	// // }
	// // else if(fkfs_map.get("FKLB").equals("5")){
	// // jmbd_map.put("YHJE",
	// // Double.parseDouble(jmbd_map.get("YHJE")+"")+ld_fkje);
	// // }
	// // else if(fkfs_map.get("FKLB").equals("6")){
	// // jmbd_map.put("YHKJE",
	// // Double.parseDouble(jmbd_map.get("YHKJE")+"")+ld_fkje);
	// // }else{
	// // jmbd_map.put("QTZF",
	// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
	// // }
	// // }
	// // }
	// // if(isFind==false){
	// // jmbd_map.put("QTZF",
	// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
	// // }
	// }
	// jmbd_map.put(
	// "FPZS",
	// getIntValue(jmbd_map.get("FPZS"))
	// + getIntValue(sfrb_map.get("FPZS")));
	// jmbd_map.put(
	// "SFJE",
	// getDoubleValue(jmbd_map.get("SFJE"))
	// + getDoubleValue(sfrb_map.get("ZJJE")));
	// if (sfrb_map.get("ZFZF") != null
	// && sfrb_map.get("ZFZF") != "") {
	// jmbd_map.put(
	// "ZFZF",
	// Double.parseDouble(jmbd_map.get("ZFZF") + "")
	// + Double.parseDouble(sfrb_map
	// .get("ZFZF") + ""));
	// }
	// if (sfrb_map.get("QTYS") != null
	// && sfrb_map.get("QTYS") != "") {
	// jmbd_map.put(
	// "QTYS",
	// Double.parseDouble(jmbd_map.get("QTYS") + "")
	// + Double.parseDouble(sfrb_map
	// .get("QTYS") + ""));
	// }
	// jmbd_list.add(jmbd_map);
	// }
	// // if (sfrb_map.get("ZFSZ") != null && sfrb_map.get("ZFSZ") != "") {
	// // ll_zffp += Double.parseDouble(sfrb_map.get("ZFSZ") + "");
	// // }
	//
	// }
	//
	// // 填挂号日报信息
	// // 遍历挂号日报表单
	// // Double ll_ghth = 0D;
	// for (int i = 0; i < ghrb_list.size(); i++) {
	// Map<String, Object> ghrb_map = ghrb_list.get(i);
	// String ls_ygdm = ghrb_map.get("CZGH") + "";
	// String ll_find = "";
	// for (int j = 0; j < jmbd_list.size(); j++) {
	// Map<String, Object> jmbd_map = jmbd_list.get(j);
	// if (ls_ygdm.equals(jmbd_map.get("CZGH")+"")) {
	// ll_find = jmbd_map.get("CZGH") + "";
	// jmbd_map.put("GHRC", getIntValue(ghrb_map.get("FPZS")));
	// jmbd_map.put("GHJE",
	// getDoubleValue(ghrb_map.get("ZJJE")));
	// break;
	// }
	// }
	// // if (ll_find.endsWith("")) {
	// // Map<String, Object> jmbd_map = new HashMap<String, Object>();
	// // jmbd_map.put("CZGH", ls_ygdm);
	// // jmbd_map.put("GHRC", 0D);
	// // jmbd_map.put("GHJE", 0D);
	// // jmbd_map.put("FPZS", 0D);
	// // jmbd_map.put("SFJE", 0D);
	// // jmbd_map.put("XJJE", 0D);
	// // jmbd_map.put("ZPJE", 0D);
	// // jmbd_map.put("YHJE", 0D);
	// // jmbd_map.put("YHKJE", 0D);
	// // jmbd_map.put("QTZF", 0D);
	// // jmbd_map.put("ZFZF", 0D);
	// // jmbd_map.put("QTYS", 0D);
	// // jmbd_map.put("HBWC", 0D);
	// // jmbd_map.put("KHJE", 0D);
	// // jmbd_map.put("JKJE", 0D);
	// // jmbd_map.put("TKJE", 0D);
	// //
	// // jmbd_map.put("GRZH", 0D);
	// // jmbd_map.put("ZJJE", 0D);
	// //
	// // //遍历已结帐未汇总收费表单
	// // for (int j = 0; j < yjzsf_list.size(); j++) {
	// // Map<String, Object> yjzsf_map = yjzsf_list.get(j);
	// // String ll_fkfs = yjzsf_map.get("FKFS") + "";
	// // Double ld_fkje = Double.parseDouble(yjzsf_map
	// // .get("FKJE") + "");
	// // if(yjzsf_map.get("CZGH").equals(jmbd_map.get("CZGH"))){
	// // if(yjzsf_map.get("FKLB").equals("1")){
	// // jmbd_map.put("XJJE",
	// // Double.parseDouble(jmbd_map.get("XJJE")+"")+ld_fkje);
	// // }else if(yjzsf_map.get("FKLB").equals("2")){
	// // jmbd_map.put("ZPJE",
	// // Double.parseDouble(jmbd_map.get("ZPJE")+"")+ld_fkje);
	// // }
	// // else if(yjzsf_map.get("FKLB").equals("4")){
	// // jmbd_map.put("HBWC",
	// // Double.parseDouble(jmbd_map.get("HBWC")+"")+ld_fkje);
	// // }
	// // else if(yjzsf_map.get("FKLB").equals("5")){
	// // jmbd_map.put("YHJE",
	// // Double.parseDouble(jmbd_map.get("YHJE")+"")+ld_fkje);
	// // }
	// // else if(yjzsf_map.get("FKLB").equals("6")){
	// // jmbd_map.put("YHKJE",
	// // Double.parseDouble(jmbd_map.get("YHKJE")+"")+ld_fkje);
	// // }else{
	// // jmbd_map.put("QTZF",
	// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
	// // }}
	// // // boolean isFind=false;
	// // // for (int k = 0; k < fkfs_list.size(); k++) {
	// // // Map<String, Object> fkfs_map = fkfs_list.get(k);
	// // // if (fkfs_map.get("FKFS").equals(ll_fkfs)) {
	// // // isFind=true;
	// // // if(fkfs_map.get("FKLB").equals("1")){
	// // // jmbd_map.put("XJJE",
	// // Double.parseDouble(jmbd_map.get("XJJE")+"")+ld_fkje);
	// // // }
	// // // else if(fkfs_map.get("FKLB").equals("2")){
	// // // jmbd_map.put("ZPJE",
	// // Double.parseDouble(jmbd_map.get("ZPJE")+"")+ld_fkje);
	// // // }
	// // // else if(fkfs_map.get("FKLB").equals("4")){
	// // // jmbd_map.put("HBWC",
	// // Double.parseDouble(jmbd_map.get("HBWC")+"")+ld_fkje);
	// // // }
	// // // else if(fkfs_map.get("FKLB").equals("5")){
	// // // jmbd_map.put("YHJE",
	// // Double.parseDouble(jmbd_map.get("YHJE")+"")+ld_fkje);
	// // // }
	// // // else if(fkfs_map.get("FKLB").equals("6")){
	// // // jmbd_map.put("YHKJE",
	// // Double.parseDouble(jmbd_map.get("YHKJE")+"")+ld_fkje);
	// // // }else{
	// // // jmbd_map.put("QTZF",
	// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
	// // // }
	// // // }
	// // // }
	// // // if(isFind==false){
	// // // jmbd_map.put("QTZF",
	// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
	// // // }
	// // }
	// //
	// // //遍历已结帐未汇总挂号表单
	// // for (int j = 0; j < yjzgh_list.size(); j++) {
	// // Map<String, Object> yjzgh_map = yjzgh_list.get(j);
	// // String ll_fkfs = yjzgh_map.get("FKFS") + "";
	// // Double ld_fkje = Double.parseDouble(yjzgh_map
	// // .get("FKJE") + "");
	// // if(yjzgh_map.get("CZGH").equals(jmbd_map.get("CZGH"))){
	// // if(yjzgh_map.get("FKLB").equals("1")){
	// // jmbd_map.put("XJJE",
	// // Double.parseDouble(jmbd_map.get("XJJE")+"")+ld_fkje);
	// // }else if(yjzgh_map.get("FKLB").equals("2")){
	// // jmbd_map.put("ZPJE",
	// // Double.parseDouble(jmbd_map.get("ZPJE")+"")+ld_fkje);
	// // }
	// // else if(yjzgh_map.get("FKLB").equals("4")){
	// // jmbd_map.put("HBWC",
	// // Double.parseDouble(jmbd_map.get("HBWC")+"")+ld_fkje);
	// // }
	// // else if(yjzgh_map.get("FKLB").equals("5")){
	// // jmbd_map.put("YHJE",
	// // Double.parseDouble(jmbd_map.get("YHJE")+"")+ld_fkje);
	// // }
	// // else if(yjzgh_map.get("FKLB").equals("6")){
	// // jmbd_map.put("YHKJE",
	// // Double.parseDouble(jmbd_map.get("YHKJE")+"")+ld_fkje);
	// // }else{
	// // jmbd_map.put("QTZF",
	// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
	// // }}
	// // // boolean isFind=false;
	// // // for (int k = 0; k < fkfs_list.size(); k++) {
	// // // Map<String, Object> fkfs_map = fkfs_list.get(k);
	// // // if (fkfs_map.get("FKFS").equals(ll_fkfs)) {
	// // // isFind=true;
	// // // if(fkfs_map.get("FKLB").equals("1")){
	// // // jmbd_map.put("XJJE",
	// // Double.parseDouble(jmbd_map.get("XJJE")+"")+ld_fkje);
	// // // }
	// // // else if(fkfs_map.get("FKLB").equals("2")){
	// // // jmbd_map.put("ZPJE",
	// // Double.parseDouble(jmbd_map.get("ZPJE")+"")+ld_fkje);
	// // // }
	// // // else if(fkfs_map.get("FKLB").equals("4")){
	// // // jmbd_map.put("HBWC",
	// // Double.parseDouble(jmbd_map.get("HBWC")+"")+ld_fkje);
	// // // }
	// // // else if(fkfs_map.get("FKLB").equals("5")){
	// // // jmbd_map.put("YHJE",
	// // Double.parseDouble(jmbd_map.get("YHJE")+"")+ld_fkje);
	// // // }
	// // // else if(fkfs_map.get("FKLB").equals("6")){
	// // // jmbd_map.put("YHKJE",
	// // Double.parseDouble(jmbd_map.get("YHKJE")+"")+ld_fkje);
	// // // }else{
	// // // jmbd_map.put("QTZF",
	// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
	// // // }
	// // // }
	// // // }
	// // // if(isFind==false){
	// // // jmbd_map.put("QTZF",
	// // Double.parseDouble(jmbd_map.get("QTZF")+"")+ld_fkje);
	// // // }
	// // }
	// // jmbd_map.put("GHRC",
	// //
	// Double.parseDouble(jmbd_map.get("GHRC")+"")+Double.parseDouble(ghrb_map.get("FPZS")+""));
	// // jmbd_map.put("GHJE",
	// //
	// Double.parseDouble(jmbd_map.get("GHJE")+"")+Double.parseDouble(ghrb_map.get("ZJJE")+""));
	// // if(ghrb_map.get("ZFZF")!=null&&ghrb_map.get("ZFZF")!=""){
	// // jmbd_map.put("ZFZF",
	// //
	// Double.parseDouble(jmbd_map.get("ZFZF")+"")+Double.parseDouble(ghrb_map.get("ZFZF")+""));
	// // }
	// // if(ghrb_map.get("QTYS")!=null&&ghrb_map.get("QTYS")!=""){
	// // jmbd_map.put("QTYS",
	// //
	// Double.parseDouble(jmbd_map.get("QTYS")+"")+Double.parseDouble(ghrb_map.get("QTYS")+""));
	// // }
	// // jmbd_list.add(jmbd_map);
	// // }
	// // if (ghrb_map.get("ZFSZ") != null && ghrb_map.get("ZFSZ") != "") {
	// // ll_ghth += Double.parseDouble(ghrb_map.get("THSL") + "");
	// // }
	//
	// }
	//
	// for (int j = 0; j < jmbd_list.size(); j++) {
	// Map<String, Object> jmbd_map = jmbd_list.get(j);
	// jmbd_map.put("TOTALAMOUNT",
	// getDoubleValue(jmbd_map.get("GHJE"))
	// + getDoubleValue(jmbd_map.get("SFJE")));
	// jmbd_map.put("HJ", 0D);
	// jmbd_map.put("CZGH",
	// DictionaryController.instance().get("phis.dictionary.doctor")
	// .getText(jmbd_map.get("CZGH") + ""));
	// // System.out.println(jmbd_map.toString());
	// }
	// Integer ghcount = 0;
	// Double ghAmount = 0D;
	// Integer sfcount = 0;
	// Double sfAmount = 0D;
	// Double totals = 0D;
	// Double xjAmount = 0D;
	// Double zpAmount = 0D;
	// Double yhkAmount = 0D;
	// Double yhAmount = 0D;
	// Double qtAmount = 0D;
	// Double grAmount = 0D;
	// Double qtysAmount = 0D;
	// Double hbwcAmount = 0D;
	// Double khAmount = 0D;
	// Double jkAmount = 0D;
	// Double tkAmount = 0D;
	// Double finalAmount = 0D;
	//
	// for (int j = 0; j < jmbd_list.size(); j++) {
	// Map<String, Object> jmbd_map = jmbd_list.get(j);
	//
	// ghcount += getIntValue(jmbd_map.get("GHRC"));
	// ghAmount += getDoubleValue(jmbd_map.get("GHJE"));
	// sfcount += getIntValue(jmbd_map.get("FPZS"));
	// sfAmount += getDoubleValue(jmbd_map.get("SFJE"));
	// totals += getDoubleValue(jmbd_map.get("TOTALAMOUNT"));
	// xjAmount += getDoubleValue(jmbd_map.get("XJJE"));
	// zpAmount += getDoubleValue(jmbd_map.get("ZPJE"));
	// yhkAmount += getDoubleValue(jmbd_map.get("YHKJE"));
	// yhAmount += getDoubleValue(jmbd_map.get("YHJE"));
	// qtAmount += getDoubleValue(jmbd_map.get("QTZF"));
	// grAmount += getDoubleValue(jmbd_map.get("GRZH"));
	// qtysAmount += getDoubleValue(jmbd_map.get("QTYS"));
	// hbwcAmount += getDoubleValue(jmbd_map.get("HBWC"));
	// khAmount += getDoubleValue(jmbd_map.get("KHJE"));
	// jkAmount += getDoubleValue(jmbd_map.get("JKJE"));
	// tkAmount += getDoubleValue(jmbd_map.get("TKJE"));
	// finalAmount += getDoubleValue(jmbd_map.get("HJ"));
	//
	// }
	//
	// response.put("ghcount", ghcount.toString());
	// response.put("ghAmount", String.format("%1$.2f", ghAmount));
	// response.put("sfcount", sfcount.toString());
	// response.put("sfAmount", String.format("%1$.2f", sfAmount));
	// response.put("totals", String.format("%1$.2f", totals));
	// response.put("xjAmount", String.format("%1$.2f", xjAmount));
	// response.put("zpAmount", String.format("%1$.2f", zpAmount));
	// response.put("yhkAmount", String.format("%1$.2f", yhkAmount));
	// response.put("yhAmount", String.format("%1$.2f", yhAmount));
	// response.put("qtAmount", String.format("%1$.2f", qtAmount));
	// response.put("grAmount", String.format("%1$.2f", grAmount));
	// response.put("qtysAmount", String.format("%1$.2f", qtysAmount));
	// response.put("hbwcAmount", String.format("%1$.2f", hbwcAmount));
	// response.put("khAmount", String.format("%1$.2f", khAmount));
	// response.put("jkAmount", String.format("%1$.2f", jkAmount));
	// response.put("tkAmount", String.format("%1$.2f", tkAmount));
	// response.put("finalAmount", String.format("%1$.2f", finalAmount));
	//
	//
	// } catch (PersistentDataOperationException e) {
	// e.printStackTrace();
	// } catch (ControllerException e) {
	// e.printStackTrace();
	// }

	// }

	private int getIntValue(Object object) {
		if (object == null) {
			return 0;
		}
		return Integer.parseInt(object + "");
	}

	private double getDoubleValue(Object object) {
		if (object == null) {
			return 0;
		}
		return Double.parseDouble(object + "");
	}
}
