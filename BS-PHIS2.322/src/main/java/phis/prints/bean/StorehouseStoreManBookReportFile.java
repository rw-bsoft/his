package phis.prints.bean;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;

import ctd.util.context.Context;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;

public class StorehouseStoreManBookReportFile implements IHandler {
	@SuppressWarnings("unchecked")
	public void getParameters(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 获取JGID
		String jgName = user.getManageUnit().getName();// 用户的机构名称
		Long yksb = Long.parseLong(req.get("yksb") + "");
		String ldt_start = req.get("ksrq") + "";// 开始时间
		String ldt_end = req.get("jsrq") + " 23:59:59";// 结束时间
		String ldt_ypmc = "";
		String ldt_ypgg = "";
		String ldt_ypdw = "";
		String ldt_cdmc = "";
		try {
			ldt_ypmc = URLDecoder.decode(req.get("ypmc") + "", "UTF-8");// 药品名称
			ldt_ypgg = URLDecoder.decode(req.get("ypgg") + "", "UTF-8");// 药品规格
			ldt_ypdw = URLDecoder.decode(req.get("ypdw") + "", "UTF-8");// 药品单位
			ldt_cdmc = URLDecoder.decode(req.get("cdmc") + "", "UTF-8");// 产地名称

			String ypmc = ldt_ypmc + "/" + ldt_ypgg + "/" + ldt_ypdw;
			String ldt_ypxh = req.get("ypxh") + "";// 药品序号
			String ldt_ypcd = req.get("ypcd") + "";// 药品产地
			Map<String, Object> jzParameters = new HashMap<String, Object>();
			jzParameters.put("XTSB", yksb);
//			jzParameters.put("ZZSJ", BSHISUtil.toDate(ldt_start));
			jzParameters.put("ZZSJ", ldt_start);
			StringBuffer jzHql = new StringBuffer();
			jzHql.append("select t.XTSB as XTSB,t.CWYF as CWYF,t.QSSJ as QSSJ,t.ZZSJ as ZZSJ from (select a.XTSB as XTSB,a.CWYF as CWYF,a.QSSJ as QSSJ,to_char(a.ZZSJ,'yyyy-mm-dd') as ZZSJ from "
					+ " YK_JZJL a where a.XTSB=:XTSB and to_char(a.ZZSJ,'YYYY-MM-DD')<=:ZZSJ order by a.CWYF desc) t where rownum=1");
			Map<String, Object> jzmap=dao.doSqlLoad(jzHql.toString(), jzParameters);
			ldt_start=jzmap.get("ZZSJ")+"";
			String begin = ldt_start.split("-")[0] + "."+ ldt_start.split("-")[1]+ "."+ ldt_start.split("-")[2];
			String end = ldt_end.split("-")[0] + "." + ldt_end.split("-")[1] + "." + ldt_end.split("-")[2];
			Map<String, Object> hqlParameters = new HashMap<String, Object>();
			StringBuffer hql = new StringBuffer();
			hqlParameters.put("YKSB", yksb);
			hqlParameters.put("JGID", jgid);
			hql.append("select a.YKMC as YKMC from YK_YKLB a where a.YKSB=:YKSB and a.JGID=:JGID");
			String ykmc = "";
			Map<String, Object> ykmcMap;
			ykmcMap = dao.doLoad(hql.toString(), hqlParameters);
			if (ykmcMap.get("YKMC") != null) {
				ykmc = ykmcMap.get("YKMC") + "";
			}
				Double pfjg = 0.00;// 批发价格
				Double lsjg = 0.00;// 零售价格
				// YK_KCMX查询 批发价格 零售价格
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("JGID", jgid);
				parameters.put("YPXH", parseLong(ldt_ypxh));
				parameters.put("YPCD", parseLong(ldt_ypcd));
				Map<String, Object> ypjgMap = dao.doLoad("select a.JHJG as PFJG,a.LSJG as LSJG from YK_CDXX"
								+ " a where JGID=:JGID and YPXH=:YPXH and YPCD=:YPCD",parameters);
				if (ypjgMap != null) {
					if (ypjgMap.get("PFJG") == null) {
						pfjg = 0.00;
					} else {
						pfjg = parseDouble(ypjgMap.get("PFJG") + "");
					}
					lsjg = parseDouble(ypjgMap.get("LSJG") + "");
				}
				res.put("title", jgName + ykmc + "药品存货明细帐");
				res.put("ypmc", ypmc);
				res.put("ypcd", ldt_cdmc);
				res.put("pfjg", String.format("%1$.2f", pfjg));// 批发价格
				res.put("lsjg", String.format("%1$.2f", lsjg));// 零售价格
				res.put("cxrq", begin + "--" + end);
				res.put("year", ldt_end.split("-")[0] + "年");// 年(结束时间)
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getFields(Map<String, Object> req,
			List<Map<String, Object>> res, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 获取JGID
		BaseDAO dao = new BaseDAO(ctx);
		Long yksb = Long.parseLong(req.get("yksb") + "");
		String ldt_start = req.get("ksrq") + "";// 开始时间
		String ldt_end = req.get("jsrq") + "";// 结束时间
		String ldt_ypxh = req.get("ypxh") + "";// 药品序号
		String ldt_ypcd = req.get("ypcd") + "";// 药品产地

		List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
		Double ldc_jc = 0.00;// 结存数量
		Double ldc_rk = 0.00;// 入库数量
		Double ldc_ck = 0.00;// 出库数量
		Map<String, Object> jzParameters = new HashMap<String, Object>();
		jzParameters.put("XTSB", yksb);
		jzParameters.put("ZZSJ", BSHISUtil.toDate(ldt_start));
		StringBuffer jzHql = new StringBuffer();
		jzHql.append("select t.XTSB as XTSB,t.CWYF as CWYF,t.QSSJ as QSSJ,t.ZZSJ as ZZSJ from (select a.XTSB as XTSB,a.CWYF as CWYF,a.QSSJ as QSSJ,a.ZZSJ as ZZSJ from "
				+ " YK_JZJL a where a.XTSB=:XTSB and a.ZZSJ<=:ZZSJ order by a.CWYF desc) t where rownum=1");
		try {

			List<Map<String, Object>> jzList = dao.doSqlQuery(jzHql.toString(),jzParameters);// 结账
			if (jzList.size() > 0 ) {
				String xtsb = jzList.get(0).get("XTSB") + "";
				String ldt_cwyf = jzList.get(0).get("CWYF") + "";
				String ldt_zzsj = jzList.get(0).get("ZZSJ") + "";
				ldt_start = jzList.get(0).get("ZZSJ") + "";
				StringBuffer kcslHql = new StringBuffer();
				Map<String, Object> kcslParameters = new HashMap<String, Object>();
				kcslParameters.put("XTSB", parseLong(xtsb));
				kcslParameters.put("CWYF", ldt_cwyf);
				kcslParameters.put("JGID", jgid);
				kcslParameters.put("YPXH", parseLong(ldt_ypxh));
				kcslParameters.put("YPCD", parseLong(ldt_ypcd));
				kcslHql.append("select a.KCSL as LDC_JC from YK_SWYJ a where a.XTSB=:XTSB " +
						" and to_char(CWYF,'yyyy-mm-dd')=:CWYF and a.JGID=:JGID and a.YPXH=:YPXH and a.YPCD=:YPCD");
				List<Map<String, Object>> kcslList = dao.doSqlQuery(kcslHql.toString(), kcslParameters);
				if (kcslList.size() != 0) {
					ldc_jc = parseDouble(kcslList.get(0).get("LDC_JC") + "");
				}
				SimpleDateFormat cwyfsdf = new SimpleDateFormat("yyyy-MM-dd");
				String cwyfmon = cwyfsdf.format(BSHISUtil.toDate(ldt_cwyf)).split("-")[1];// 月份
				String cwyfday = cwyfsdf.format(BSHISUtil.toDate(ldt_cwyf)).split("-")[2];// 天数

				// 入库数量
				StringBuffer rkslHql = new StringBuffer();
				Map<String, Object> rkslParameters = new HashMap<String, Object>();
				rkslParameters.put("XTSB", parseLong(xtsb));
				rkslParameters.put("ldt_zzsj", BSHISUtil.toDate(ldt_zzsj));
				rkslParameters.put("ldt_start", BSHISUtil.toDate(ldt_start));
				rkslParameters.put("YPXH", parseLong(ldt_ypxh));
				rkslParameters.put("YPCD", parseLong(ldt_ypcd));
				rkslParameters.put("JGID", jgid);
				rkslHql.append("select sum(b.RKSL) as LDC_RK from  YK_RK01 a ,YK_RK02 b"
						+ " where b.XTSB = a.XTSB and b.RKFS = a.RKFS and b.RKDH = a.RKDH and a.XTSB=:XTSB"
						+ " and a.RKRQ >=:ldt_zzsj and a.RKRQ <=:ldt_start and a.RKPB = 1 and b.YPXH=:YPXH"
						+ " and b.YPCD=:YPCD and a.JGID=:JGID");
				List<Map<String, Object>> rkslList = dao.doSqlQuery(rkslHql.toString(), rkslParameters);
				if (rkslList.get(0).get("LDC_RK") != null) {
					ldc_rk = parseDouble(rkslList.get(0).get("LDC_RK") + "");
				}
				// 出库数量
				StringBuffer ckslHql = new StringBuffer();
				Map<String, Object> ckslParameters = new HashMap<String, Object>();
				ckslParameters.put("XTSB", parseLong(xtsb));
				ckslParameters.put("ldt_zzsj", BSHISUtil.toDate(ldt_zzsj));
				ckslParameters.put("ldt_start", BSHISUtil.toDate(ldt_start));
				ckslParameters.put("YPXH", parseLong(ldt_ypxh));
				ckslParameters.put("YPCD", parseLong(ldt_ypcd));
				ckslParameters.put("JGID", jgid);
				ckslHql.append("select sum(b.SFSL) as LDC_CK from  YK_RK01 a ,YK_RK02 b"
						+ " where b.XTSB = a.XTSB and b.CKFS = a.CKFS and b.CKDH = a.CKDH and a.XTSB=:XTSB"
						+ " and a.CKRQ >=:ldt_zzsj and a.CKRQ <=:ldt_start and a.CKPB = 1 and b.YPXH=:YPXH"
						+ " and b.YPCD=:YPCD and a.JGID=:JGID");
				List<Map<String, Object>> ckslList = dao.doSqlQuery(rkslHql.toString(), rkslParameters);
				if (ckslList.get(0).get("LDC_CK") != null) {
					ldc_ck = parseDouble(ckslList.get(0).get("LDC_CK") + "");
				}
				ldc_jc = ldc_jc + ldc_rk - ldc_ck;

				Double ldc_sum_rk = 0.00;//入库数量汇总
				Double ldc_sum_ck = 0.00;//出库数量汇总
				// 入库
				Map<String, Object> rkParameters = new HashMap<String, Object>();
				rkParameters.put("ldt_start", BSHISUtil.toDate(ldt_start));
				rkParameters.put("ldt_end",BSHISUtil.toDate(ldt_end + " 23:59:59"));
				rkParameters.put("YPXH", parseLong(ldt_ypxh));// 药品序号
				rkParameters.put("YPCD", parseLong(ldt_ypcd));// 药品产地
				rkParameters.put("XTSB", parseLong(xtsb));// 系统识别
				rkParameters.put("JGID", jgid);// 机构ID
				StringBuffer rkHql = new StringBuffer();
				rkHql.append("select a.DWXH as DWXH,a.RKRQ as RKRQ,a.RKFS as RKFS,b.RKDH as RKDH,b.RKSL as RKSL,"
						+ "b.PFJG as PFJG,b.FPHM as FPHM,b.LSJG as LSJG,a.CZGH as CZGH,a.CWPB as CWPB,b.SHHM as SHHM"
						+ " from YK_RK01 a,YK_RK02 b where a.RKRQ >=:ldt_start and a.RKRQ <=:ldt_end and b.YPXH=:YPXH"
						+ " and b.YPCD=:YPCD and a.RKFS = b.RKFS and a.RKDH = b.RKDH and a.XTSB = b.XTSB and a.RKPB = 1"
						+ " and b.XTSB=:XTSB and b.JGID=:JGID and a.JGID = b.JGID");
				List<Map<String, Object>> rkList = dao.doSqlQuery(rkHql.toString(), rkParameters);
				// 入库遍历
				if (rkList.size() > 0) {
					for (int i = 0; i < rkList.size(); i++) {
						Map<String, Object> rkmxMap = new HashMap<String, Object>();
						Long dwxh = null;
						if (rkList.get(i).get("DWXH") != null) {
							dwxh = Long.parseLong(rkList.get(i).get("DWXH")+ "");
						}
						Date rkrq = null;
						if (rkList.get(i).get("RKRQ") != null) {
							rkrq = BSHISUtil.toDate((rkList.get(i).get("RKRQ") + ""));
						}
						int rkfs = Integer.parseInt((rkList.get(i).get("RKFS") + ""));
						Long rkdh = Long.parseLong(rkList.get(i).get("RKDH")+ "");
						Double rksl = Double.parseDouble(rkList.get(i).get("RKSL")+ "");
						ldc_sum_rk += rksl;// 不清楚
//						Double pfjg = 0.00;
//						if (rkList.get(i).get("PFJG") != null) {
//							pfjg = Double.parseDouble(rkList.get(i).get("PFJG")+ "");
//						}
						String fphm = null;
						if (rkList.get(i).get("FPHM") != null) {
							fphm = rkList.get(i).get("FPHM") + "";
						}
//						Double lsjg = Double.parseDouble(rkList.get(i).get("LSJG")+ "");
						String czgh = null;
						if (rkList.get(i).get("CZGH") != null) {
							czgh = rkList.get(i).get("CZGH") + "";
						}
//						Integer cwpb = Integer.parseInt(rkList.get(i).get("CWPB")+ "");
						String shhm = null;
						if (rkList.get(i).get("SHHM") != null) {
							shhm = rkList.get(i).get("SHHM") + "";
						}

						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						String mon = sdf.format(rkrq).split("-")[1];// 月份
						String day = sdf.format(rkrq).split("-")[2];// 天数
						// 员工姓名
						Map<String, Object> ygxmParameters = new HashMap<String, Object>();
						ygxmParameters.put("YGDM", czgh);
						StringBuffer ygxmHql = new StringBuffer();
						ygxmHql.append("select a.PERSONNAME as YGXM from  SYS_Personnel a where a.PERSONID=:YGDM");
						Map<String, Object> ygxmMap = dao.doLoad(ygxmHql.toString(), ygxmParameters);
						String ygxm = null;
						if (ygxmMap != null) {
							ygxm = ygxmMap.get("YGXM") + "";
						}
						// 单位名称
						Map<String, Object> dwmcParameters = new HashMap<String, Object>();
						if(dwxh==null){
							dwxh=0L;
						}
						dwmcParameters.put("DWXH", dwxh);
						StringBuffer dwmcHql = new StringBuffer();
						dwmcHql.append("select a.DWMC as DWMC from YK_JHDW a where a.DWXH=:DWXH");
						Map<String, Object> dwmcMap = dao.doLoad(dwmcHql.toString(), dwmcParameters);
						String dwmc = null;
						if (dwmcMap != null) {
							dwmc = dwmcMap.get("DWMC") + "";
						}
						// 方式
						Map<String, Object> fsmcParameters = new HashMap<String, Object>();
						fsmcParameters.put("RKFS", rkfs);
						fsmcParameters.put("XTSB", parseLong(xtsb));
						fsmcParameters.put("JGID", jgid);
						StringBuffer fsmcHql = new StringBuffer();
						fsmcHql.append("select a.FSMC as FSMC from YK_RKFS a " +
								" where a.RKFS=:RKFS and a.XTSB=:XTSB and a.JGID=:JGID");
						Map<String, Object> fsmcMap = dao.doLoad(fsmcHql.toString(), fsmcParameters);
						String fsmc = fsmcMap.get("FSMC") + "";

						if (fphm == null || fphm == "") {
							fphm = "";
						} else {
							fphm = "(" + fphm + ")";
						}
						if (dwmc == null || dwmc == "") {
							dwmc = "";
						} else {
							dwmc = "(" + dwmc + ")";
						}
						if (shhm == null || shhm == "") {
							shhm = "";
						} else {
							shhm = "(h:" + shhm + ")";
						}
						String zy = fsmc + dwmc + fphm + shhm;// 摘要
						rkmxMap.put("rq", rkrq);// 按日期排序
						rkmxMap.put("mon", mon);
						rkmxMap.put("day", day);
						rkmxMap.put("pzhm", rkdh);
						rkmxMap.put("zy", zy);
						rkmxMap.put("srsl", String.format("%1$.2f", rksl));
						rkmxMap.put("fcsl", "");
						rkmxMap.put("czgh", ygxm);
						rkmxMap.put("fspb", 1);// 入库
						records.add(rkmxMap);
					}
				}

				// 出库
				Map<String, Object> ckParameters = new HashMap<String, Object>();
				StringBuffer ckHql = new StringBuffer();
				ckParameters.put("ldt_start", BSHISUtil.toDate(ldt_start));
				ckParameters.put("ldt_end",BSHISUtil.toDate(ldt_end + " 23:59:59"));
				ckParameters.put("YPXH", parseLong(ldt_ypxh));// 药品序号
				ckParameters.put("YPCD", parseLong(ldt_ypcd));// 药品产地
				ckParameters.put("XTSB", parseLong(xtsb));// 系统识别
				ckParameters.put("JGID", jgid);// 机构ID
				ckHql.append("select a.CKRQ as CKRQ,a.CKFS as CKFS,a.CKDH as CKDH,sum(b.SFSL) as SFSL,"
						+ "b.PFJG as PFJG,b.LSJG as LSJG,a.CZGH as CZGH,a.CKKS as CKKS"
						+ " from YK_CK01 a,YK_CK02 b"
						+ " where a.CKRQ >=:ldt_start and a.CKRQ<=:ldt_end and a.CKPB = 1 and b.YPXH=:YPXH"
						+ " and b.YPCD=:YPCD and a.CKFS = b.CKFS and  a.CKDH = b.CKDH and a.XTSB= b.XTSB"
						+ " and b.XTSB=:XTSB and a.JGID=:JGID group by a.CKDH,a.CKFS,a.CKRQ,b.PFJG,b.LSJG,a.CZGH,a.CKKS");
				List<Map<String, Object>> ckList = dao.doSqlQuery(
						ckHql.toString(), ckParameters);
				// 出库遍历
				if (ckList.size() > 0) {
					for (int i = 0; i < ckList.size(); i++) {
						Map<String, Object> ckmxMap = new HashMap<String, Object>();
						Date ckrq = null;
						if (ckList.get(i).get("CKRQ") != null) {
							ckrq = BSHISUtil.toDate((ckList.get(i).get("CKRQ") + ""));
						}
						int ckfs = Integer.parseInt((ckList.get(i).get("CKFS") + ""));
						Long ckdh = Long.parseLong(ckList.get(i).get("CKDH")+ "");
						Double sfsl = Double.parseDouble(ckList.get(i).get("SFSL")+ "");
						ldc_sum_ck += sfsl;// 不清楚
//						Double pfjg = 0.00;
//						if (ckList.get(i).get("PFJG") != null) {
//							pfjg = Double.parseDouble(ckList.get(i).get("PFJG")+ "");
//						}
//						Double lsjg = Double.parseDouble(ckList.get(i).get("LSJG")+ "");
						String czgh = null;
						if (ckList.get(i).get("CZGH") != null) {
							czgh = ckList.get(i).get("CZGH") + "";
						}
						Long ckks = null;
						if (ckList.get(i).get("CKKS") != null) {
							ckks = Long.parseLong(ckList.get(i).get("CKKS")+ "");// 出库科室
						}

						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						String mon = sdf.format(ckrq).split("-")[1];// 月份
						String day = sdf.format(ckrq).split("-")[2];// 天数
						// 员工姓名
						Map<String, Object> ygxmParameters = new HashMap<String, Object>();
						ygxmParameters.put("YGDM", czgh);
						StringBuffer ygxmHql = new StringBuffer();
						ygxmHql.append("select a.PERSONNAME as YGXM from SYS_Personnel a where a.PERSONID=:YGDM");
						Map<String, Object> ygxmMap = dao.doLoad(ygxmHql.toString(), ygxmParameters);
						String ygxm = null;
						if (ygxmMap != null) {
							ygxm = ygxmMap.get("YGXM") + "";
						}
						// 方式
						Map<String, Object> fsmcParameters = new HashMap<String, Object>();
						fsmcParameters.put("CKFS", ckfs);
						fsmcParameters.put("XTSB", parseLong(xtsb));
						fsmcParameters.put("JGID", jgid);
						StringBuffer fsmcHql = new StringBuffer();
						fsmcHql.append("select a.FSMC as FSMC from YK_CKFS a " +
								" where a.CKFS=:CKFS and a.XTSB=:XTSB and a.JGID=:JGID");
						Map<String, Object> fsmcMap = dao.doLoad(
								fsmcHql.toString(), fsmcParameters);
						String fsmc = null;
						if (fsmcMap != null) {
							fsmc = fsmcMap.get("FSMC") + "";
						}

						String zy = fsmc;// 摘要
						if (ckks != null && ckks > 0) {// 出库科室
							Map<String, Object> ckksParameters = new HashMap<String, Object>();
							StringBuffer ckksHql = new StringBuffer();
							ckksParameters.put("KSDM", ckks);
							ckksHql.append("select a.OFFICENAME as KSMC from SYS_Office a where a.ID=:KSDM");
							Map<String, Object> ckksMap = dao.doLoad(ckksHql.toString(), ckksParameters);
							String ksmc = ckksMap.get("KSMC") + "";
							zy += "(" + ksmc + ")";
						}
						ckmxMap.put("rq", ckrq);// 按日期排序
						ckmxMap.put("mon", mon);
						ckmxMap.put("day", day);
						ckmxMap.put("pzhm", ckdh);
						ckmxMap.put("zy", zy);
						ckmxMap.put("srsl", "");
						ckmxMap.put("fcsl", String.format("%1$.2f", sfsl));
						ckmxMap.put("czgh", ygxm);
						ckmxMap.put("fspb", 2);// 出库
						records.add(ckmxMap);
					}
				}

				// 期初结存----
				List<Map<String, Object>> sortRecords = new ArrayList<Map<String, Object>>();// 已排序
				Map<String, Object> cwyfhMap = new HashMap<String, Object>();
				cwyfhMap.put("mon", cwyfmon);
				cwyfhMap.put("day", cwyfday);
				cwyfhMap.put("pzhm", "");
				cwyfhMap.put("zy", "期初结存(财务月份)");
				cwyfhMap.put("srsl", "");
				cwyfhMap.put("fcsl", "");
				cwyfhMap.put("jcsl", String.format("%1$.2f", ldc_jc));
				cwyfhMap.put("czgh", "");
				cwyfhMap.put("fspb", 3);// 初期结存
				sortRecords.add(cwyfhMap);
				listSort(records);// 按出库入库日期排序 rq
				Double jc_sum = 0.00;// 结存
				Double sr_sum = 0.00;// 收入
				Double zc_sum = 0.00;// 支出
				for (int i = 0; i < records.size(); i++) {
					int fspb = Integer.parseInt((records.get(i).get("fspb") + ""));
					if (fspb == 1) {// 入库
						sr_sum = sr_sum+ parseDouble(records.get(i).get("srsl") + "");
						jc_sum = jc_sum+ parseDouble(records.get(i).get("srsl") + "");
						records.get(i).put("jcsl",String.format("%1$.2f", jc_sum + ldc_jc));
					} else {// 出库
						zc_sum = zc_sum+ parseDouble(records.get(i).get("fcsl") + "");
						jc_sum = jc_sum- parseDouble(records.get(i).get("fcsl") + "");
						records.get(i).put("jcsl",String.format("%1$.2f", jc_sum + ldc_jc));
					}
				}
				Map<String, Object> hzmap = new HashMap<String, Object>();
				hzmap.put("zy", "合计:");
				hzmap.put("srsl", ldc_sum_rk);
				hzmap.put("fcsl", ldc_sum_ck);
				records.add(hzmap);
				// 插入空2行
				for (int i = 0; i < 2; i++) {
					Map<String, Object> crkhMap = new HashMap<String, Object>();
					crkhMap.put("mon", "");
					crkhMap.put("day", "");
					crkhMap.put("pzhm", "");
					crkhMap.put("zy", "");
					crkhMap.put("srsl", "");
					crkhMap.put("fcsl", "");
					crkhMap.put("jcsl", "");
					crkhMap.put("czgh", "");// ldc_sum_rk
					crkhMap.put("fspb", 0);// 空行
					records.add(crkhMap);
				}
				
				sortRecords.addAll(records);
				res.addAll(sortRecords);// 显示
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-7-27
	 * @description 数据转换成long
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public long parseLong(Object o) {
		if (o == null) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-8-10
	 * @description 数据转换成double
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-8-10
	 * @description 数据转换成int
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public int parseInt(Object o) {
		if (o == null) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}

	public static void main(String args[]) {
		List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> aa = new HashMap<String, Object>();
		Map<String, Object> bb = new HashMap<String, Object>();
		Map<String, Object> cc = new HashMap<String, Object>();
		Map<String, Object> dd = new HashMap<String, Object>();
		Map<String, Object> ee = new HashMap<String, Object>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date datea = null;
		Date dateb = null;
		Date datec = null;
		Date dated = null;
		Date datee = null;
		try {
			datea = sdf.parse("2013-09-09");
			dateb = sdf.parse("2012-08-08");
			datec = sdf.parse("2013-07-07");
			dated = sdf.parse("2013-06-06");
			datee = sdf.parse("2013-09-09");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		aa.put("rq", datea);
		aa.put("key", "a");
		bb.put("rq", dateb);
		bb.put("key", "b");
		cc.put("rq", datec);
		cc.put("key", "c");
		dd.put("rq", dated);
		dd.put("key", "d");
		ee.put("rq", datee);
		ee.put("key", "e");
		list1.add(aa);
		list1.add(bb);
		list2.add(cc);
		list2.add(dd);
		list2.add(ee);
		list.addAll(list1);
		list.addAll(list2);
		try {
			listSort(list);

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(list);
	}

	public static void listSort(List<Map<String, Object>> resultList) {
		// resultList是需要排序的list，其内放的是Map
		// 返回的结果集
		Collections.sort(resultList, new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				// o1，o2是list中的Map，可以在其内取得值，按其排序，此例为升序
				Date a1 = (Date) o1.get("rq");
				Date a2 = (Date) o2.get("rq");
				if (a1.compareTo(a2) > 0) {
					// System.out.println(a1);
					return 1;
				} else {
					// System.out.println(a2);
					return -1;
				}
			}
		});
	}

}