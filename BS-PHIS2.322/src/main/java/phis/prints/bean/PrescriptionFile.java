package phis.prints.bean;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.ivc.source.OrderCardsInjectionCard;
import phis.application.pub.source.PublicModel;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class PrescriptionFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		int a = 0;
		int ypzh = 0;
		Long cfsb = 0l;
		double je = 0.0;
		int cul = 10;// 每页处方10条（目前）
		if (request.get("cfsb") != null) {
			cfsb = Long.parseLong(request.get("cfsb") + "");
		}
		int max = 0;
		int n = 0;
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("CFSB", cfsb);
		//modify by zhaojian 2017-06-01 增加备注（皮试）的打印
		// String sql =
		// "select yt.PSPB as PSPB,cf.PSPB as PSPB2,yt.TSYP as TSYP,pc.BZXX as BZXX,cf.YPZH as YPZH,yt.YPMC as YPMC,yp.YFGG as YFGG,yt.YBFL as YBFL,cf.YCJL as YCJL,cf.YPSL as YPSL,cf.YFDW as YFDW,yt.JLDW as JLDW,cf.GYTJ as GYTJ,cf.YPYF as YPYF,cf.YYTS as YYTS,cf.HJJE as HJJE from YK_TYPK yt,YF_YPXX yp,MS_CF01 cf1,MS_CF02 cf left outer join GY_SYPC pc on cf.YPYF=pc.PCBM  where cf.ZFYP!=1 and cf1.CFSB=cf.CFSB and cf.YPXH=yp.YPXH and yp.YPXH=yt.YPXH and cf1.YFSB=yp.YFSB and cf.JGID=yp.JGID and cf.YPXH=yt.YPXH and cf.CFSB=:CFSB order by cf.YPZH";
		String sql = "select yt.PSPB as PSPB,cf.PSPB as PSPB2,yt.TSYP as TSYP,pc.BZXX as BZXX,cf.YPZH as YPZH,yt.YPMC as YPMC,yp.YFGG as YFGG,yt.YBFL as YBFL,cf.YCJL as YCJL,cf.YPSL as YPSL,cf.YFDW as YFDW,yt.JLDW as JLDW,cf.GYTJ as GYTJ,pc.PCMC as YPYF,cf.YYTS as YYTS,cf.HJJE as HJJE,cf.BZXX as BZ from YK_TYPK yt,YF_YPXX yp,MS_CF01 cf1,MS_CF02 cf left outer join GY_SYPC pc on cf.YPYF=pc.PCBM  where cf.ZFYP!=1 and cf1.CFSB=cf.CFSB and cf.YPXH=yp.YPXH and yp.YPXH=yt.YPXH and cf1.YFSB=yp.YFSB and cf.JGID=yp.JGID and cf.YPXH=yt.YPXH and cf.CFSB=:CFSB order by cf.YPZH,cf.SBXH";
		// String hql_zby=
		// "select yt.PSPB as PSPB,cf.PSPB as PSPB2,yt.TSYP as TSYP,pc.BZXX as BZXX,cf.YPZH as YPZH,'(自备)'||yt.YPMC as YPMC,yt.YFGG as YFGG,yt.YBFL as YBFL,cf.YCJL as YCJL,cf.YPSL as YPSL,cf.YFDW as YFDW,yt.JLDW as JLDW,cf.GYTJ as GYTJ,cf.YPYF as YPYF,cf.YYTS as YYTS,0 as HJJE from YK_TYPK yt,MS_CF01 cf1,MS_CF02 cf left outer join GY_SYPC pc on cf.YPYF=pc.PCBM  where cf.ZFYP=1 and cf1.CFSB=cf.CFSB and cf.YPXH=yt.YPXH  and cf.CFSB=:CFSB  order by cf.YPZH";
		String hql_zby = "select yt.PSPB as PSPB,cf.PSPB as PSPB2,yt.TSYP as TSYP,pc.BZXX as BZXX,cf.YPZH as YPZH,'(自备)'||yt.YPMC as YPMC,yt.YFGG as YFGG,yt.YBFL as YBFL,cf.YCJL as YCJL,cf.YPSL as YPSL,cf.YFDW as YFDW,yt.JLDW as JLDW,cf.GYTJ as GYTJ,pc.PCMC as YPYF,cf.YYTS as YYTS,0 as HJJE,cf.BZXX as BZ from YK_TYPK yt,MS_CF01 cf1,MS_CF02 cf left outer join GY_SYPC pc on cf.YPYF=pc.PCBM  where cf.ZFYP=1 and cf1.CFSB=cf.CFSB and cf.YPXH=yt.YPXH  and cf.CFSB=:CFSB  order by cf.YPZH,cf.SBXH";
		try {
			List<Map<String, Object>> cflist = dao.doSqlQuery(sql, parameters);
			List<Map<String, Object>> cflist_zby = dao.doSqlQuery(hql_zby,
					parameters);
			if (cflist == null || cflist.size() <= 0) {
				cflist = new ArrayList<Map<String, Object>>();
				// return;
			}
			cflist.addAll(cflist_zby);
			max = cflist.size();
			if (max == 0) {
				return;
			}
			for (int j = 0; j < cflist.size(); j++) {

				if (cflist.get(j).get("YPSL") != null
						&& cflist.get(j).get("YPSL") != "") {
					cflist.get(j).put(
							"YPSL",
							String.format("%1$.0f", cflist.get(j).get("YPSL"))
									+ ""
									+ (cflist.get(j).get("YFDW") == null ? ""
											: cflist.get(j).get("YFDW")));
				}
				/*
				 * if (cflist.get(j).get("YPYF") != null &&
				 * cflist.get(j).get("YPYF") != "") { cflist.get(j).put("YPYF",
				 * cflist.get(j).get("BZXX")); }
				 */
				if (cflist.get(j).get("GYTJ") != null
						&& cflist.get(j).get("GYTJ") != "") {
					cflist.get(j).put(
							"GYTJ",
							// "用法:"+
							DictionaryController.instance()
									.getDic("phis.dictionary.drugMode")
									.getText(cflist.get(j).get("GYTJ") + ""));
				}
				cflist.get(j).put(
						"YCJL",
						// "每次:"+
						(cflist.get(j).get("YCJL") == null ? "" : cflist.get(j)
								.get("YCJL"))
								+ ""
								+ (cflist.get(j).get("JLDW") == null ? ""
										: cflist.get(j).get("JLDW")));
				if (cflist.get(j).get("YPZH") != null
						&& cflist.get(j).get("YPZH") != "") {
					if (ypzh == Integer
							.parseInt(cflist.get(j).get("YPZH") + "")) {
						cflist.get(j).put("XH", "");
					} else {
						ypzh = Integer.parseInt(cflist.get(j).get("YPZH") + "");
						cflist.get(j).put("XH", this.getNum(n) + "）");
						n++;
					}
				}
				if (cflist.get(j).get("PSPB") != null
						&& cflist.get(j).get("PSPB") != "") {
					if (parseInt(cflist.get(j).get("PSPB")) == 1) {
						if (parseInt(cflist.get(j).get("PSPB2")) == 1) {
							cflist.get(j).put("PSPB", "(皮)▲");
						} else if (parseInt(cflist.get(j).get("PSPB2")) == 0) {
							cflist.get(j).put("PSPB", "(续)▲");
						}

					} else {
						cflist.get(j).put("PSPB", "");
					}
				}
				if (cflist.get(j).get("YPMC") != null
						&& cflist.get(j).get("YPMC") != ""
						&& cflist.get(j).get("YFGG") != null
						&& cflist.get(j).get("YFGG") != "") {
					cflist.get(j).put(
							"YPMC",
							(cflist.get(j).get("YPMC") + "")
									+ (cflist.get(j).get("YFGG") + ""));
				}
				je += Double.parseDouble(cflist.get(j).get("HJJE") + "");
				if (j + 1 == cflist.size()) {
					cflist.get(j).put("YPJE",
							"药品金额：" + String.format("%1$.2f", je));
				}
				if (cflist.get(j).get("BZ") != null) {
					cflist.get(j).get("BZ");
				} else {
					cflist.get(j).put("BZ", "");
				}
				records.add(cflist.get(j));
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		String realPath = OrderCardsInjectionCard.class.getResource("/")
				.getPath();
		System.out.println(realPath);
		realPath = realPath.substring(1, realPath.indexOf("BS-PHIS") + 7);
		Long cfsb = 0l;

		if (request.get("cfsb") != null) {
			cfsb = Long.parseLong(request.get("cfsb") + "");
		}
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();
		String jgid = user.getManageUnitId();// 用户的机构ID
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parametersjzhm = new HashMap<String, Object>();
		parameters.put("CFSB", cfsb);
		String sql = "select br.LXDZ as DZ,br.JTDH as DH,cf.CFSB as CFSB,cf.BRID as BRID,br.BRXM as BRXM,cf.CFHM as CFHM,br.BRXB as BRXB,"
				+ "br.CSNY as CSNY,cf.KFRQ as KFRQ,cf.KSDM as KSDM,br.JZKH as JZKH,br.MZHM as MZHM,"
				+ "br.BRXZ as BRXZ,"
				+ "dm.PERSONID as YGBH,cf.JZXH as JZXH,cf.YSDM as YSDM,cf.HJGH as HJGH,cf.PYGH as PYGH,"
				+ "cf.HDGH as HDGH,cf.FYGH as FYGH,yj.GHXH as SBXH from MS_CF01 cf "
				+ "left outer join YS_MZ_JZLS yj on cf.JZXH=yj.JZXH,MS_BRDA br,SYS_Personnel dm "
				+ "where cf.BRID=br.BRID and cf.YSDM=dm.PERSONID and cf.CFSB=:CFSB";
		String sql1 = "select JZHM as JZHM from MS_GHMX where SBXH=:SBXH";
		// 判断精神处方和总计金额
		String sql2 = "select cf.HJJE as HJJE,yt.TSYP as TSYP from MS_CF02 cf, YK_TYPK yt where cf.YPXH=yt.YPXH and cf.CFSB=:CFSB";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Map<String, Object> cfmap = dao.doSqlQuery(sql, parameters).get(0);
			if (cfmap.get("SBXH") != null) {
				parametersjzhm.put("SBXH",
						Long.parseLong(cfmap.get("SBXH") + ""));
			} else {
				parametersjzhm.put("SBXH", 0L);
			}
			// sql3只取精神 TSYP
			List<Map<String, Object>> cfList = dao.doQuery(sql2, parameters);
			double hjje = 0.00;//add by lizhi 2017-11-12 增加合计金额
			DecimalFormat df = new DecimalFormat("######0.00");
			for (Map<String, Object> map : cfList) {
				hjje += Double.parseDouble(map.get("HJJE")+"");
			}
			response.put("HJJE", df.format(hjje));
			int tsyp = 0;
			for (Map<String, Object> map : cfList) {
				if (parseInt(map.get("TSYP")) == 7
						|| parseInt(map.get("TSYP")) == 8
						|| parseInt(map.get("TSYP")) == 1) {
					tsyp = parseInt(map.get("TSYP"));
					break;
				}
			}

			Map<String, Object> cfmapjzhm = dao.doLoad(sql1, parametersjzhm);
			if (cfmapjzhm != null) {
				if (cfmapjzhm.get("JZHM") != null) {
					response.put("JZHM", cfmapjzhm.get("JZHM"));
				} else {
					response.put("JZHM", "");
				}
			}
			if (cfmap != null && cfmap.size() > 0) {
				String ksmc = "";
				if (cfmap.get("KSDM") != null && cfmap.get("KSDM") != "") {
					ksmc = DictionaryController.instance()
							.getDic("phis.dictionary.department_leaf")
							.getText(cfmap.get("KSDM") + "");
				}
				String xzmc = "";
				if (cfmap.get("BRXZ") != null && cfmap.get("BRXZ") != "") {
					xzmc = DictionaryController.instance()
							.getDic("phis.dictionary.patientProperties")
							.getText(cfmap.get("BRXZ") + "");
				}
				String ygxm = "";
				if (cfmap.get("YSDM") != null && cfmap.get("YSDM") != "") {
					ygxm = DictionaryController.instance()
							.getDic("phis.dictionary.doctor_cfqx")
							.getText(cfmap.get("YSDM") + "");
				}
				String brxb = "";
				if (cfmap.get("BRXB") != null && cfmap.get("BRXB") != "") {
					brxb = DictionaryController.instance()
							.getDic("phis.dictionary.gender")
							.getText(cfmap.get("BRXB") + "");
				}
				String sfy = "";
				if (cfmap.get("HJGH") != null && cfmap.get("HJGH") != "") {
					sfy = DictionaryController.instance()
							.getDic("phis.dictionary.doctor")
							.getText(cfmap.get("HJGH") + "");
				}
				String dp = "";
				if (cfmap.get("PYGH") != null && cfmap.get("PYGH") != "") {
					dp = DictionaryController.instance()
							.getDic("phis.dictionary.doctor")
							.getText(cfmap.get("PYGH") + "");
				}
				String hd = "";
				if (cfmap.get("HDGH") != null && cfmap.get("HDGH") != "") {
					hd = DictionaryController.instance()
							.getDic("phis.dictionary.doctor")
							.getText(cfmap.get("HDGH") + "");
				}
				String fy = "";
				if (cfmap.get("FYGH") != null && cfmap.get("FYGH") != "") {
					fy = DictionaryController.instance()
							.getDic("phis.dictionary.doctor")
							.getText(cfmap.get("FYGH") + "");
				}
				PublicModel pm = new PublicModel();
				String age = "";
				if (cfmap.get("CSNY") != null && cfmap.get("CSNY") != "") {
					pm.doPersonAge(BSHISUtil.toDate(cfmap.get("CSNY") + ""),
							request);
					age = request
							.get("body")
							.toString()
							.substring(
									request.get("body").toString()
											.lastIndexOf("=") + 1,
									request.get("body").toString().length() - 1);
				}

				String agePb = ParameterUtil.getParameter(jgid, "CFRKNLPB",
						"16", "年龄小于16为儿科处方", ctx);
				if (!"".equals(agePb) && agePb != null && !"0".equals(agePb)
						&& agePb != "0") {
					agePb = agePb;
				} else {
					agePb = 16 + "";
				}
				String year = age;
				if (tsyp == 1) {
					response.put("CFLB", "麻醉处方");
				} else if (tsyp == 7) {
					response.put("CFLB", "精一处方");
				} else if (tsyp == 8) {
					response.put("CFLB", "精二处方");
				} else if (year.indexOf("岁") == -1
						|| parseInt(year.substring(0, year.indexOf("岁"))) <= parseInt(agePb)) {
					response.put("CFLB", "儿科处方");
				} else {
					response.put("CFLB", "普通处方");
				}
//				response.put("title", jgname + "处方笺");
				response.put("title", jgname);
				response.put("CFBH", cfmap.get("CFHM"));
				response.put("BRXM", cfmap.get("BRXM"));
				response.put("BRXB", brxb);
				if (cfmap.get("KFRQ") != null && cfmap.get("KFRQ") != "") {
					response.put("KFRQ", sdf.format(BSHISUtil.toDate(cfmap
							.get("KFRQ") + "")));
				} else {
					response.put("KFRQ", "");
				}
				response.put("AGE", age);
				response.put("KSDM", ksmc);
				response.put("MZHM", cfmap.get("MZHM"));
				response.put("BRXZ", xzmc);
				//
				// if (cfmap.get("ADDRESS") != null
				// || cfmap.get("ADDRESS") != null) {
				// response.put(
				// "ADDRESS",
				// cfmap.get("ADDRESS") + "/"
				// + cfmap.get("MOBILENUMBER"));
				// }
				// 增加地址、电话
				// if (cfmap.get("HKDZ") != null || cfmap.get("HKDZ") != null) {
				// response.put("DZ", cfmap.get("HKDZ") + "");
				// }
				// if (cfmap.get("LXDH") != null || cfmap.get("LXDH") != null) {
				// response.put("DH", cfmap.get("LXDH") + "");
				// }

				if (cfmap.get("DZ") != null || cfmap.get("DZ") != null) {
					response.put("DZ", cfmap.get("DZ") + "");
				} else {
					response.put("DZ", "");
				}
				if (cfmap.get("DH") != null || cfmap.get("DH") != null) {
					response.put("DH", cfmap.get("DH") + "");
				}

				Map<String, Object> zdparameters = new HashMap<String, Object>();
				if (cfmap.get("JZXH") != null && cfmap.get("JZXH") != "") {
					zdparameters.put("JZXH",
							Long.parseLong(cfmap.get("JZXH") + ""));
				} else {
					zdparameters.put("JZXH", 0L);
				}
				StringBuffer sb = new StringBuffer("");
				String str = "";
				String zdsql = "select ZDMC as ZDMC, ZDBW as ZDBW, ZHMC as ZHMC from MS_BRZD a left join EMR_ZYZH b on a.ZDBW = b.ZHBS where JZXH=:JZXH";
				List<Map<String, Object>> zdlist = dao.doSqlQuery(zdsql,zdparameters);
				for (int i = 0; i < zdlist.size(); i++) {
					sb.append(zdlist.get(i).get("ZDMC"));
					if(zdlist.get(i).get("ZDBW") != null && zdlist.get(i).get("ZDBW") != ""){
						String strZDBW = "";
						if(zdlist.get(i).get("ZHMC") == null){
							int ZDBW = Integer.parseInt(zdlist.get(i).get("ZDBW").toString());							
							if (ZDBW == 1)
								strZDBW = "头部";
							if (ZDBW == 2)
								strZDBW = "手臂";
							if (ZDBW == 3)
								strZDBW = "脚踝";
							if (ZDBW == 4)
								strZDBW = "左臂";
							if (ZDBW == 5)
								strZDBW = "右臂";	
						}else{
							strZDBW = zdlist.get(i).get("ZHMC").toString();
						}
						if(!strZDBW.equals("")){
							sb.append("(");
							sb.append(strZDBW);
							sb.append(")");		
						}	
					}	
					if (i + 1 != zdlist.size()) {
						sb.append(" ");
					}
				}
				if (sb.length() > 35) {
					str = sb.substring(0, 35);
					str = str + "...";
				} else {
					str = sb.toString();
				}
				response.put("ZDMC", str);
				response.put("YSDM", ygxm);
				response.put("YSGH", cfmap.get("YGBH"));
				response.put("HJGH", sfy);
				response.put("PYGH", dp);
				response.put("HDGH", hd);
				response.put("FYGH", fy);
				response.put("DYSJ", BSHISUtil.getDateTime());
				response.put("SJH", "▲");
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public String getNum(int num) {
		String[] finalNum = new String[] { "1", "2", "3", "4", "5", "6", "7",
				"8", "9", "10" };
		return finalNum[num];
	}

	public int parseInt(Object o) {
		if (o == null) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}
}
