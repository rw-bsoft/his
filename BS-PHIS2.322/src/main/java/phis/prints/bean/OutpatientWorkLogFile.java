package phis.prints.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class OutpatientWorkLogFile implements IHandler {
	@Override
	public void getFields(Map<String, Object> request,
						  List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		System.out.println("jgid----->"+jgid);
		String jzxh = null;
		String sql = null;
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", jgid);
		if (request.get("jzxh") != null) {
			jzxh = request.get("jzxh") + "";
		}
		String gltj = "";
		if (request.get("gltj") != null) {
			gltj = request.get("gltj") + "";
		}
		// SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// try {
		// Date kssj = sdf.parse(request.get("kssj") + "");
		// Date jssj = sdf.parse(request.get("jssj") + "");
		// } catch (ParseException e1) {
		// e1.printStackTrace();
		// }
		try {
			if (!"".equals(jzxh)) {
				// 20131212在框架升级中MS_BCJL.ISCRB as
				// ISCRB,为之后创建的字段，在原有的数据库中不存在，所以在本次升级中去除。如果需要在以下每条sql中加入
				if (!"0".equals(jzxh)) {
					sql = "select MS_BCJL.BRQX as BRQX,MS_BCJL.JKJY as JKJY, to_char(YS_MZ_JZLS.KSSJ,'YYYY-MM-DD hh24:mi:ss') as JZRQ,MS_BRDA.BRXM as YGXM,MS_BRDA.CSNY as YGNL,MS_BRDA.BRXB as YGXB,MS_BRDA.SFZH as SFZH,MS_BRDA.ZYDM as YGZY,MS_BRDA.LXDZ as XXDZ,MS_BRDA.JTDH as LXDH,MS_BRDA.DWMC as DWHXX,MS_BRZD.FZBZ as CZPB,to_char(MS_BRZD.FBRQ,'YYYY-MM-DD') as FBRQ,MS_BCJL.T as T,MS_BCJL.KS as KS,MS_BCJL.SZY as SZY,MS_BCJL.SSY as SSY,MS_BCJL.YT as YT,MS_BCJL.HXKN as HXKN,MS_BCJL.OT as OT,MS_BCJL.FT as FT,MS_BCJL.FX as FX,MS_BCJL.PZ as PZ,MS_BCJL.QT as QT,MS_BCJL.FZJC as FZJG,MS_BCJL.CLCS as ZLCZ,MS_BRZD.ZDMC as ZD from YS_MZ_JZLS YS_MZ_JZLS left outer join MS_BRZD MS_BRZD on YS_MZ_JZLS.JZXH=MS_BRZD.JZXH and MS_BRZD.ZZBZ=1 left outer join MS_BCJL MS_BCJL on YS_MZ_JZLS.JZXH=MS_BCJL.JZXH left outer join MS_GHMX MS_GHMX on YS_MZ_JZLS.GHXH=MS_GHMX.SBXH,MS_BRDA MS_BRDA where YS_MZ_JZLS.BRBH=MS_BRDA.BRID and YS_MZ_JZLS.JZXH in ("
							+ jzxh + ") and YS_MZ_JZLS.JGID=:JGID";
				} else if (gltj.equals("1")) {
					sql = "select MS_BCJL.BRQX as BRQX,MS_BCJL.JKJY as JKJY, to_char(YS_MZ_JZLS.KSSJ,'YYYY-MM-DD hh24:mi:ss') as JZRQ,MS_BRDA.BRXM as YGXM,MS_BRDA.CSNY as YGNL,MS_BRDA.BRXB as YGXB,MS_BRDA.SFZH as SFZH,MS_BRDA.ZYDM as YGZY,MS_BRDA.LXDZ as XXDZ,MS_BRDA.JTDH as LXDH,MS_BRDA.DWMC as DWHXX,MS_BRZD.FZBZ as CZPB,to_char(MS_BRZD.FBRQ,'YYYY-MM-DD') as FBRQ,MS_BCJL.T as T,MS_BCJL.KS as KS,MS_BCJL.SZY as SZY,MS_BCJL.SSY as SSY,MS_BCJL.YT as YT,MS_BCJL.HXKN as HXKN,MS_BCJL.OT as OT,MS_BCJL.FT as FT,MS_BCJL.FX as FX,MS_BCJL.PZ as PZ,MS_BCJL.QT as QT,MS_BCJL.FZJC as FZJG,MS_BCJL.CLCS as ZLCZ,MS_BRZD.ZDMC as ZD from YS_MZ_JZLS YS_MZ_JZLS left outer join MS_BRZD MS_BRZD on YS_MZ_JZLS.JZXH=MS_BRZD.JZXH and MS_BRZD.ZZBZ=1 left outer join MS_BCJL MS_BCJL on YS_MZ_JZLS.JZXH=MS_BCJL.JZXH left outer join MS_GHMX MS_GHMX on YS_MZ_JZLS.GHXH=MS_GHMX.SBXH,MS_BRDA MS_BRDA where YS_MZ_JZLS.BRBH=MS_BRDA.BRID and YS_MZ_JZLS.JGID=:JGID";
					sql += " and YS_MZ_JZLS.KSSJ>=to_date('"
							+ (request.get("kssj") + "")
							+ "','yyyy-mm-dd') and (YS_MZ_JZLS.JSSJ<=to_date('"
							+ (request.get("jssj") + " 23:59:59")
							+ "','yyyy-mm-dd HH24:mi:ss') or YS_MZ_JZLS.JSSJ is null)";
				} else if (gltj.equals("2")) {
					String departmentId = "";
					if (request.get("departmentId") != null) {
						departmentId = request.get("departmentId") + "";
					}
					sql = "select MS_BCJL.BRQX as BRQX,MS_BCJL.JKJY as JKJY, to_char(YS_MZ_JZLS.KSSJ,'YYYY-MM-DD hh24:mi:ss') as JZRQ,MS_BRDA.BRXM as YGXM,MS_BRDA.CSNY as YGNL,MS_BRDA.BRXB as YGXB,MS_BRDA.SFZH as SFZH,MS_BRDA.ZYDM as YGZY,MS_BRDA.LXDZ as XXDZ,MS_BRDA.JTDH as LXDH,MS_BRDA.DWMC as DWHXX,MS_BRZD.FZBZ as CZPB,to_char(MS_BRZD.FBRQ,'YYYY-MM-DD') as FBRQ,MS_BCJL.T as T,MS_BCJL.KS as KS,MS_BCJL.SZY as SZY,MS_BCJL.SSY as SSY,MS_BCJL.YT as YT,MS_BCJL.HXKN as HXKN,MS_BCJL.OT as OT,MS_BCJL.FT as FT,MS_BCJL.FX as FX,MS_BCJL.PZ as PZ,MS_BCJL.QT as QT,MS_BCJL.FZJC as FZJG,MS_BCJL.CLCS as ZLCZ,MS_BRZD.ZDMC as ZD from YS_MZ_JZLS YS_MZ_JZLS left outer join MS_BRZD MS_BRZD on YS_MZ_JZLS.JZXH=MS_BRZD.JZXH and MS_BRZD.ZZBZ=1 left outer join MS_BCJL MS_BCJL on YS_MZ_JZLS.JZXH=MS_BCJL.JZXH left outer join MS_GHMX MS_GHMX on YS_MZ_JZLS.GHXH=MS_GHMX.SBXH,MS_BRDA MS_BRDA where YS_MZ_JZLS.BRBH=MS_BRDA.BRID and YS_MZ_JZLS.JGID=:JGID and YS_MZ_JZLS.KSDM="
							+ departmentId;
					sql += " and YS_MZ_JZLS.KSSJ>=to_date('"
							+ (request.get("kssj") + "")
							+ "','yyyy-mm-dd') and (YS_MZ_JZLS.JSSJ<=to_date('"
							+ (request.get("jssj") + " 23:59:59")
							+ "','yyyy-mm-dd HH24:mi:ss')or YS_MZ_JZLS.JSSJ is null)";
				} else if (gltj.equals("3")) {
					String uid = "";
					if (request.get("uid") != null) {
						uid = request.get("uid")+"";
					}
					System.out.println("uid----->"+uid);
					sql = "select MS_BCJL.BRQX as BRQX,MS_BCJL.JKJY as JKJY, to_char(YS_MZ_JZLS.KSSJ,'YYYY-MM-DD hh24:mi:ss') as JZRQ,MS_BRDA.BRXM as YGXM,MS_BRDA.CSNY as YGNL,MS_BRDA.BRXB as YGXB,MS_BRDA.SFZH as SFZH,MS_BRDA.ZYDM as YGZY,MS_BRDA.LXDZ as XXDZ,MS_BRDA.JTDH as LXDH,MS_BRDA.DWMC as DWHXX,MS_BRZD.FZBZ as CZPB,to_char(MS_BRZD.FBRQ,'YYYY-MM-DD') as FBRQ,MS_BCJL.T as T,MS_BCJL.KS as KS,MS_BCJL.SZY as SZY,MS_BCJL.SSY as SSY,MS_BCJL.YT as YT,MS_BCJL.HXKN as HXKN,MS_BCJL.OT as OT,MS_BCJL.FT as FT,MS_BCJL.FX as FX,MS_BCJL.PZ as PZ,MS_BCJL.QT as QT,MS_BCJL.FZJC as FZJG,MS_BCJL.CLCS as ZLCZ,MS_BRZD.ZDMC as ZD from YS_MZ_JZLS YS_MZ_JZLS left outer join MS_BRZD MS_BRZD on YS_MZ_JZLS.JZXH=MS_BRZD.JZXH and MS_BRZD.ZZBZ=1 left outer join MS_BCJL MS_BCJL on YS_MZ_JZLS.JZXH=MS_BCJL.JZXH left outer join MS_GHMX MS_GHMX on YS_MZ_JZLS.GHXH=MS_GHMX.SBXH,MS_BRDA MS_BRDA where YS_MZ_JZLS.BRBH=MS_BRDA.BRID" +
							" and YS_MZ_JZLS.JGID=:JGID and YS_MZ_JZLS.YSDM='"+uid+"'";
					sql += " and YS_MZ_JZLS.KSSJ>=to_date('"
							+ (request.get("kssj") + "")
							+ "','yyyy-mm-dd') and (YS_MZ_JZLS.JSSJ<=to_date('"
							+ (request.get("jssj") + " 23:59:59")
							+ "','yyyy-mm-dd HH24:mi:ss') or YS_MZ_JZLS.JSSJ is null)";
				}
				sql += " order by YS_MZ_JZLS.JZXH desc";
				List<Map<String, Object>> kcmxlist = dao.doSqlQuery(sql,
						parameters);
				for (int i = 0; i < kcmxlist.size(); i++) {
					if (kcmxlist.get(i).get("YGNL") != null) {
						Map<String, Object> agemap = BSPHISUtil.getPersonAge(
								sdf.parse(kcmxlist.get(i).get("YGNL") + ""),
								new Date());
						if (kcmxlist.get(i).get("YGNL") != null
								&& kcmxlist.get(i).get("YGNL") != "") {
							kcmxlist.get(i).put("YGNL", agemap.get("age"));
						}
					}
					if (kcmxlist.get(i).get("YGXB") != null
							&& kcmxlist.get(i).get("YGXB") != "") {
						kcmxlist.get(i).put(
								"YGXB",
								DictionaryController
										.instance()
										.getDic("phis.dictionary.gender")
										.getText(
												kcmxlist.get(i).get("YGXB")
														+ ""));
					}
					if (kcmxlist.get(i).get("YGZY") != null
							&& kcmxlist.get(i).get("YGZY") != "") {
						kcmxlist.get(i).put(
								"YGZY",
								DictionaryController
										.instance()
										.getDic("phis.dictionary.jobtitle")
										.getText(
												kcmxlist.get(i).get("YGZY")
														+ ""));
					}
					if (kcmxlist.get(i).get("CZPB") != null) {
						if (kcmxlist.get(i).get("CZPB").toString().equals("1")) {
							kcmxlist.get(i).put("CZPD", "复诊");
						} else {
							kcmxlist.get(i).put("CZPD", "初诊");
						}
					}
//					if (kcmxlist.get(i).get("ISCRB") != null) {
//						if (kcmxlist.get(i).get("ISCRB").toString().equals("1")) {
//							kcmxlist.get(i).put("ISCRB", "是");
//						} else {
//							kcmxlist.get(i).put("ISCRB", "否");
//						}
//					}
					if (kcmxlist.get(i).get("BRQX") != null) {
						if (kcmxlist.get(i).get("BRQX").toString().equals("1")) {
							kcmxlist.get(i).put("BRQX", "离院");
						} else if (kcmxlist.get(i).get("BRQX").toString()
								.equals("2")) {
							kcmxlist.get(i).put("BRQX", "留院");
						} else if (kcmxlist.get(i).get("BRQX").toString()
								.equals("2")) {
							kcmxlist.get(i).put("BRQX", "住院");
						} else if (kcmxlist.get(i).get("BRQX").toString()
								.equals("2")) {
							kcmxlist.get(i).put("BRQX", "死亡");
						} else {
							kcmxlist.get(i).put("BRQX", " ");
						}
					}

					// if (kcmxlist.get(i).get("KS").toString().equals("1")) {
					// kcmxlist.get(i).put("KS", "√");
					// } else {
					// kcmxlist.get(i).put("KS", "");
					// }
					// if (kcmxlist.get(i).get("YT").toString().equals("1")) {
					// kcmxlist.get(i).put("YT", "√");
					// } else {
					// kcmxlist.get(i).put("YT", "");
					// }
					// if (kcmxlist.get(i).get("HXKN").toString().equals("1")) {
					// kcmxlist.get(i).put("HXKN", "√");
					// } else {
					// kcmxlist.get(i).put("HXKN", "");
					// }
					// if (kcmxlist.get(i).get("OT").toString().equals("1")) {
					// kcmxlist.get(i).put("OT", "√");
					// } else {
					// kcmxlist.get(i).put("OT", "");
					// }
					// if (kcmxlist.get(i).get("FT").toString().equals("1")) {
					// kcmxlist.get(i).put("FT", "√");
					// } else {
					// kcmxlist.get(i).put("FT", "");
					// }
					// if (kcmxlist.get(i).get("FX").toString().equals("1")) {
					// kcmxlist.get(i).put("FX", "√");
					// } else {
					// kcmxlist.get(i).put("FX", "");
					// }
					// if (kcmxlist.get(i).get("PZ").toString().equals("1")) {
					// kcmxlist.get(i).put("PZ", "√");
					// } else {
					// kcmxlist.get(i).put("PZ", "");
					// }
					// if (kcmxlist.get(i).get("QT").toString().equals("1")) {
					// kcmxlist.get(i).put("QT", "√");
					// } else {
					// kcmxlist.get(i).put("QT", "");
					// }
					kcmxlist.get(i).put("XH", i + 1);
					if (kcmxlist.get(i).get("JKJY") == null
							|| kcmxlist.get(i).get("JKJY") == ""
							|| (kcmxlist.get(i).get("JKJY") + "")
							.equals("null")) {
						kcmxlist.get(i).put("JKJY", "");
					}
				}
				records.addAll(kcmxlist);
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
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();
		response.put("title", jgname + "门诊工作日志");
	}
}
