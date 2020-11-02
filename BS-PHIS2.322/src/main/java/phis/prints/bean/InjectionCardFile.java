package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.pub.source.PublicModel;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class InjectionCardFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		Long cfsb = 0l;
		if (request.get("cfsb") != null) {
			cfsb = Long.parseLong(request.get("cfsb") + "");
		}
		int ypzh = 0;
		int n = 0;
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("CFSB", cfsb);
		String sql = "select cf.ZFYP as ZFYP,cf.YPZH as YPZH,yt.YPMC as YPMZ,yt.YFGG as YFGG,cf.YCJL as CFJL,yt.YPJL as YPJL,yt.JLDW as JLDW,cf.YPSL as YPSL,cf.YFDW as YFDW,cf.YPYF as YPYF,cf.YYTS as YYTS,cf.GYTJ as GYTJ,yt.PSPB as PSPB,cf.PSPB as PSPB2 from MS_CF02 cf,YK_TYPK yt,ZY_YPYF zy where cf.YPXH=yt.YPXH and cf.GYTJ=zy.YPYF and cf.CFSB=:CFSB and zy.KPDY=1 order by cf.SBXH";
		try {
			List<Map<String, Object>> cflist = dao.doQuery(sql, parameters);
			StringBuffer sbf = new StringBuffer();
			for (int i = 0; i < cflist.size(); i++) {
				if(Integer.parseInt(cflist.get(i).get("ZFYP")+"")==1){
					cflist.get(i).put("YPMZ", "(自备)"+cflist.get(i).get("YPMZ"));
				}
				List<Map<String, Object>> ypyf = dao.doQuery(sql, parameters);
				ypyf.add(cflist.get(i));// 先把药品用法记录起来
				cflist.get(i).put("YPYF", "");// 然后每行都清空
				String pspb = "";
				if (Integer.parseInt(cflist.get(i).get("PSPB") + "") == 1) {
					//pspb = "(皮)";
					if(parseInt(cflist.get(i).get("PSPB2")) == 1){
						cflist.get(i).put("PSPB","(皮)▲");
					} else if(parseInt(cflist.get(i).get("PSPB2")) == 0){
						cflist.get(i).put("PSPB","(续)▲");
					}
				} else {
					cflist.get(i).put("PSPB", "");
				}
				cflist.get(i).put(
						"YPMC",
						cflist.get(i).get("YPMZ")
								+ "/"
								+ (cflist.get(i).get("YFGG") == null ? ""
										: cflist.get(i).get("YFGG")));
				cflist.get(i).put(
						"YCJL",String.format("%1$.0f", cflist.get(i).get("YPSL"))
								+ (cflist.get(i).get("YFDW") == null ? ""
										: cflist.get(i).get("YFDW")));
				cflist.get(i).put(
						"CFJLDW",
						cflist.get(i).get("CFJL")
								+ ""
								+ (cflist.get(i).get("JLDW") == null ? ""
										: cflist.get(i).get("JLDW")));
				if (cflist.size() == 1) {// 只有一行的时候
					cflist.get(0).put("XH", this.getNum(0));
					cflist.get(i).put("YF", "用法:");
					cflist.get(i).put(
							"YPYF",
							DictionaryController.instance().getDic("phis.dictionary.drugMode")
									.getText(cflist.get(i).get("GYTJ") + "")
									+ " "
									+ DictionaryController
											.instance()
											.getDic("phis.dictionary.useRate")
											.getText(
													ypyf.get(i).get("YPYF")
															+ "")
									+ "×"
									+ cflist.get(i).get("YYTS") + "天");

					cflist.get(i).put("JYRY", "加药:______________");
					cflist.get(i).put("SJ", "时 间:");
					cflist.get(i).put("DS", "滴数/分:");
					cflist.get(i).put("QM", "签 名:");
				} else {
					if (i == 0) {// 等于0放入第一个
						cflist.get(0).put("XH", this.getNum(0));
						ypzh = Integer.parseInt(cflist.get(0).get("YPZH") + "");
						sbf.append(cflist.get(0).get("CFJL")
								+ ""
								+ (cflist.get(i).get("JLDW") == null ? ""
										: cflist.get(i).get("JLDW")) + " ,");
					} else {
						if (Integer.parseInt(cflist.get(i).get("YPZH") + "") == ypzh) {// 判断下一个是否和上一个相等
							sbf.append(cflist.get(i).get("CFJL")
									+ ""
									+ (cflist.get(i).get("JLDW") == null ? ""
											: cflist.get(i).get("JLDW")) + " ,");
							cflist.get(i).put("XH", "");// 如果相等 就不要赋序号了
							if ((i + 1) == cflist.size()) {// 等于最后一行的时候
															// 设置下cflist
								String sbfd = sbf.toString().replaceAll(",",
										"\r");
								sbf.delete(0, sbf.length());
								cflist.get(i).put("YF", "用法:");
								cflist.get(i)
										// 如果相等的是最后一行的时候
										.put("YPYF",
												DictionaryController
														.instance()
														.getDic("phis.dictionary.drugMode")
														.getText(
																cflist.get(i)
																		.get("GYTJ")
																		+ "")
														+ " "
														+ DictionaryController
																.instance()
																.getDic("phis.dictionary.useRate")
																.getText(
																		ypyf.get(
																				i)
																				.get("YPYF")
																				+ "")
														+ "×"
														+ cflist.get(i).get(
																"YYTS") + "天");

								cflist.get(i).put("JYRY", "加药:______________");
								cflist.get(i).put("SJ", "时 间:");
								cflist.get(i).put("DS", "滴数/分:");
								cflist.get(i).put("QM", "签 名:");
							}
						} else {
							if ((i + 1) == cflist.size()) {// 不想等且是最后一行的时候
															// 设置下cflist
								StringBuffer sbfzh = new StringBuffer();
								sbfzh.append(cflist.get(i).get("CFJL")
										+ ""
										+ (cflist.get(i).get("JLDW") == null ? ""
												: cflist.get(i).get("JLDW"))
										+ " ,");
								String sbfd = sbfzh.toString().replaceAll(",",
										"\r");
								cflist.get(i).put("YF", "用法:");
								cflist.get(i)
										.put("YPYF",
												DictionaryController
														.instance()
														.getDic("phis.dictionary.drugMode")
														.getText(
																cflist.get(i)
																		.get("GYTJ")
																		+ "")
														+ " "
														+ DictionaryController
																.instance()
																.getDic("phis.dictionary.useRate")
																.getText(
																		ypyf.get(
																				i)
																				.get("YPYF")
																				+ "")
														+ "×"
														+ cflist.get(i).get(
																"YYTS") + "天");

								cflist.get(i).put("JYRY", "加药:______________");
								cflist.get(i).put("SJ", "时 间:");
								cflist.get(i).put("DS", "滴数/分:");
								cflist.get(i).put("QM", "签 名:");
							}
							String sbfd = sbf.toString().replaceAll(",", "\r");
							sbf.delete(0, sbf.length());
							sbf.append(cflist.get(i).get("CFJL")
									+ ""
									+ (cflist.get(i).get("JLDW") == null ? ""
											: cflist.get(i).get("JLDW")) + " ,");
							records.get(i - 1).put("YF", "用法:");
							records.get(i - 1)
									.put(// 不想等且不是最后一行就设置上一次的ypyf
									"YPYF",
											DictionaryController
													.instance()
													.getDic("phis.dictionary.drugMode")
													.getText(
															cflist.get(i - 1)
																	.get("GYTJ")
																	+ "")
													+ " "
													+ DictionaryController
															.instance()
															.getDic("phis.dictionary.useRate")
															.getText(
																	ypyf.get(
																			i - 1)
																			.get("YPYF")
																			+ "")
													+ "×"
													+ cflist.get(i - 1).get(
															"YYTS") + "天");
							records.get(i - 1).put("JYRY", "加药:______________");
							records.get(i - 1).put("SJ", "时 间:");
							records.get(i - 1).put("DS", "滴数/分:");
							records.get(i - 1).put("QM", "签 名:");
							ypzh = Integer.parseInt(cflist.get(i).get("YPZH")
									+ "");// 如果不等就重新获取ypzh
							n++;// 去下一序号
							cflist.get(i).put("XH", this.getNum(n));// 并赋值
						}
					}
				}
				records.add(cflist.get(i));
			}
			System.out.println("===");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		Long cfsb = 0l;
		if (request.get("cfsb") != null) {
			cfsb = Long.parseLong(request.get("cfsb") + "");
		}
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parametersjzhm = new HashMap<String, Object>();

		parameters.put("CFSB", cfsb);
		String sql = "select br.LXDZ as DZ,br.JTDH as DH ,cf.CFSB as CFSB,cf.BRID as BRID,br.BRXM as BRXM,cf.CFHM as CFHM,br.BRXB as BRXB,br.CSNY as CSNY,cf.KFRQ as KFRQ,cf.KSDM as KSDM,br.JZKH as JZKH,br.MZHM as MZHM,br.BRXZ as BRXZ,md.address||'/'||md.mobileNumber as ADDRESS,cf.YSDM as YSDM,cf.JZXH as JZXH,dm.PERSONID as YGBH,cf.HJGH as HJGH,cf.PYGH as PYGH,cf.HDGH as HDGH,cf.FYGH as FYGH,yj.GHXH as SBXH from MS_CF01 cf left outer join YS_MZ_JZLS yj on cf.JZXH=yj.JZXH,MS_BRDA br,SYS_Personnel dm,MPI_DemographicInfo md where cf.BRID=br.BRID and cf.YSDM=dm.PERSONID and br.EMPIID=md.empiId and cf.CFSB=:CFSB";
		//String sql = "select br.LXDZ as DZ,br.JTDH as DH ,cf.CFSB as CFSB,cf.BRID as BRID,br.BRXM as BRXM,cf.CFHM as CFHM,br.BRXB as BRXB,br.CSNY as CSNY,cf.KFRQ as KFRQ,cf.KSDM as KSDM,br.JZKH as JZKH,br.MZHM as MZHM,br.BRXZ as BRXZ,cf.YSDM as YSDM,cf.JZXH as JZXH,dm.PERSONID as YGBH,cf.HJGH as HJGH,cf.PYGH as PYGH,cf.HDGH as HDGH,cf.FYGH as FYGH,yj.GHXH as SBXH from MS_CF01 cf left outer join YS_MZ_JZLS yj on cf.JZXH=yj.JZXH,MS_BRDA br,SYS_Personnel dm where cf.BRID=br.BRID and cf.YSDM=dm.YGDM and cf.CFSB=:CFSB";

		String sql1 = "select JZHM as JZHM from MS_GHMX where SBXH=:SBXH";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<Map<String, Object>> cfmaplist =  dao.doSqlQuery(sql, parameters);
			Map<String, Object> cfmap = null;
			if(cfmaplist.size()>0){
				cfmap=cfmaplist.get(0);
			}
			if (cfmap != null && cfmap.get("SBXH") != null) {
				parametersjzhm.put("SBXH",
						Long.parseLong(cfmap.get("SBXH") + ""));
			} else {
				parametersjzhm.put("SBXH", 0L);
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
				String ksmc = DictionaryController.instance().getDic("phis.dictionary.department_leaf")
						.getText(cfmap.get("KSDM") + "");
				String xzmc = DictionaryController.instance()
						.getDic("phis.dictionary.patientProperties")
						.getText(cfmap.get("BRXZ") + "");
				String brxb = DictionaryController.instance().getDic("phis.dictionary.gender")
						.getText(cfmap.get("BRXB") + "");
				String sfy = DictionaryController.instance().getDic("phis.dictionary.doctor")
						.getText(cfmap.get("HJGH") + "");
				String hd = DictionaryController.instance().getDic("phis.dictionary.doctor")
						.getText(cfmap.get("HDGH") + "");
				String fy = DictionaryController.instance().getDic("phis.dictionary.doctor")
						.getText(cfmap.get("FYGH") + "");
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
				response.put("title", jgname+"注射单");
				response.put("CFBH", cfmap.get("CFHM"));
				response.put("BRXM", cfmap.get("BRXM"));
				response.put("BRXB", brxb);
				response.put("KFRQ",
						sdf.format(BSHISUtil.toDate(cfmap.get("KFRQ") + "")));
				response.put("DYRQ", sdf.format(new Date()));
				response.put("AGE", age);
				response.put("KSDM", ksmc);
				response.put("MZHM", cfmap.get("MZHM"));
				response.put("BRXZ", xzmc);
//				response.put("ADDRESS", cfmap.get("ADDRESS"));
				// 增加地址、电话
//				if (cfmap.get("HKDZ") != null || cfmap.get("HKDZ") != null) {
//					response.put("DZ", cfmap.get("HKDZ") + "");
//				}
//				if (cfmap.get("LXDH") != null || cfmap.get("LXDH") != null) {
//					response.put("DH", cfmap.get("LXDH") + "");
//				}
				if (cfmap.get("DZ") != null || cfmap.get("DZ") != null) {
					response.put("DZ", cfmap.get("DZ") + "");
				}
				if (cfmap.get("DH") != null || cfmap.get("DH") != null) {
					response.put("DH", cfmap.get("DH") + "");
				}
				//增加条码
				response.put("CFSB", cfsb+"");
				
				Map<String, Object> zdparameters = new HashMap<String, Object>();
				if (cfmap.get("JZXH") != null && cfmap.get("JZXH") != "") {
					zdparameters.put("JZXH",
							Long.parseLong(cfmap.get("JZXH") + ""));
				} else {
					zdparameters.put("JZXH", 0L);
				}
				StringBuffer sb = new StringBuffer("");
				String str = "";
				String zdsql = "select ZDMC as ZDMC from MS_BRZD where JZXH=:JZXH";
				List<Map<String, Object>> zdlist = dao.doQuery(zdsql,
						zdparameters);
				for (int i = 0; i < zdlist.size(); i++) {
					sb.append(zdlist.get(i).get("ZDMC"));
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
				if (cfmap.get("YSDM") != null) {
					response.put("YSDM",
							DictionaryController.instance().getDic("phis.dictionary.doctor_cfqx")
									.getText(cfmap.get("YSDM") + ""));
				}
				response.put("HJGH", sfy);
				response.put("HDGH", hd);
				response.put("FYGH", fy);
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
