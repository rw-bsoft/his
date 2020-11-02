package phis.prints.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class DoctorAdviceSubmitFile implements IHandler {
	@SuppressWarnings("unchecked")
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		String brch = null;
		String brxm = null;
		try {
			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
			UserRoleToken user = UserRoleToken.getCurrent();
			String manaUnitId = user.getManageUnitId();// 用户的机构ID
			Long al_hsql = 0L;
			if (user.getProperty("wardId") != null
					&& user.getProperty("wardId") != "") {
				al_hsql = Long.parseLong(user.getProperty("wardId") + "");
			}
			Long al_zyh = 0L;
			Long ldt_fyfs = 0L;
			int ldt_lsyz = 0;
			Long ldt_yfsb = 0L;
			Date ldt_server = sdfdate.parse(sdfdate.format(new Date()));
			if (request.get("al_zyh") != null) {
				al_zyh = Long.parseLong(request.get("al_zyh") + "");
			}
			if (request.get("ldt_lyrq") != null) {
				ldt_server = sdfdate.parse(request.get("ldt_lyrq") + "");
			}
			if (request.get("fyfs") != null) {
				ldt_fyfs = Long.parseLong(request.get("fyfs") + "");
			}
			if (request.get("lsyz") != null) {
				ldt_lsyz = Integer.parseInt(request.get("lsyz") + "");
			}
			if (request.get("yfsb") != null) {
				ldt_yfsb = Long.parseLong(request.get("yfsb") + "");
			}
			List<Map<String, Object>> collardrugdetailslist = new ArrayList<Map<String, Object>>();
			// List<Date> qrsjlist = new ArrayList<Date>();
			Map<Long,List<Date>> qrsjMap = new HashMap<Long,List<Date>>();
			Map<String, Object> collardrugdetailsparameters = new HashMap<String, Object>();
			StringBuffer sql = new StringBuffer(
					"SELECT ZY_BRRY.BRCH as BRCH,ZY_BRRY.BRXM as BRXM,ZY_BQYZ.YZMC as YZMC,ZY_BQYZ.MZCS as MZCS,str(ZY_BQYZ.KSSJ,'YYYY-MM-DD HH24:MI:SS') as KSSJ,str(ZY_BQYZ.TZSJ,'YYYY-MM-DD HH24:MI:SS') as TZSJ,ZY_BQYZ.YPYF as YPYF,ZY_BQYZ.MRCS as MRCS,ZY_BQYZ.YCSL as YCSL,ZY_BQYZ.YPDJ as YPDJ,ZY_BQYZ.SYBZ as SYBZ,ZY_BQYZ.SRKS as SRKS,str(ZY_BQYZ.QRSJ,'YYYY-MM-DD HH24:MI:SS') as QRSJ,ZY_BQYZ.YPXH as YPXH,ZY_BQYZ.YPLX as YPLX,ZY_BQYZ.JLXH as JLXH,ZY_BQYZ.FYFS as FYFS,ZY_BQYZ.SYPC as SYPC,ZY_BQYZ.ZYH as ZYH,ZY_BQYZ.BZXX as BZXX,ZY_BQYZ.XMLX as XMLX,ZY_BQYZ.YZZH as YZZH,ZY_BQYZ.FYSX as FYSX,ZY_BQYZ.LSYZ as LSYZ,ZY_BQYZ.YFSB as YFSB,0 as FYTS,0 as FYCS,0.00 as YPSL,YK_TYPK.YPSX as YPSX,ZY_BQYZ.YPCD as YPCD,ZY_BRRY.DJID as DJID,ZY_BQYZ.YZPB as YZPB,ZY_BQYZ.YSTJ as YSTJ,ZY_BQYZ.YEPB as YEPB,ZY_BQYZ.SRCS as SRCS,ZY_BQYZ.ZXKS as ZXKS,ZY_BQYZ.YFGG as YFGG,ZY_BQYZ.YFDW as YFDW,ZY_BQYZ.YFBZ as YFBZ,ZY_BQYZ.YYTS as YYTS,ZY_BQYZ.BRKS as BRKS,ZY_BQYZ.BRBQ as BRBQ,ZY_BQYZ.YZZXSJ as YZZXSJ FROM ZY_BQYZ ZY_BQYZ,ZY_BRRY ZY_BRRY,YK_TYPK YK_TYPK WHERE ( ZY_BQYZ.ZYH=ZY_BRRY.ZYH and ZY_BRRY.CYPB=0) and (ZY_BQYZ.YPXH=YK_TYPK.YPXH) and (ZY_BQYZ.SRKS=:SRKS) and (ZY_BQYZ.LSBZ=0) and (ZY_BQYZ.SYBZ = 0) and (ZY_BQYZ.XMLX<4) and (ZY_BQYZ.FYSX<>2) AND (ZY_BQYZ.JFBZ<2) and ( ZY_BQYZ.JGID=:JGID) and (ZY_BQYZ.YSBZ=0 or (ZY_BQYZ.YSBZ=1 AND ZY_BQYZ.YSTJ=1)) and ZFBZ=0 and ((ZY_BQYZ.PSPB IS NULL) or (ZY_BQYZ.PSPB=0) or (ZY_BQYZ.PSPB=1))");
			if (request.containsKey("jlxhs")) {
				sql.append(" and ZY_BQYZ.JLXH in(:jlxhs)");
				String j = MedicineUtils.parseString(request.get("jlxhs"));
				String[] s = j.split(",");
				List<Long> jlxhs = new ArrayList<Long>();
				for (String o : s) {
					jlxhs.add(MedicineUtils.parseLong(o));
				}
				collardrugdetailsparameters.put("jlxhs", jlxhs);
			}
			if (al_zyh != 0L) {
				sql.append(" and (ZY_BQYZ.ZYH=:ZYH or ZY_BQYZ.ZYH=0)");
				collardrugdetailsparameters.put("ZYH", al_zyh);
			}
			if (ldt_yfsb != 0L) {
				sql.append(" and ZY_BQYZ.YFSB=:YFSB");
				collardrugdetailsparameters.put("YFSB", ldt_yfsb);
			}
			if (ldt_lsyz == 0 || ldt_lsyz == 1) {
				sql.append(" and ZY_BQYZ.LSYZ=:LSYZ");
				collardrugdetailsparameters.put("LSYZ", ldt_lsyz);
			} else if (ldt_lsyz == 3) {
				sql.append(" and ZY_BQYZ.XMLX=2");
			} else if (ldt_lsyz == 4) {
				sql.append(" and ZY_BQYZ.XMLX=3");
			}
			if (ldt_fyfs != 0L) {
				sql.append(" and ZY_BQYZ.FYFS=:FYFS");
				collardrugdetailsparameters.put("FYFS", ldt_fyfs);
			}
			String XYF = ParameterUtil.getParameter(manaUnitId,
					BSPHISSystemArgument.FHYZHJF, ctx);
			if ("1".equals(XYF)) {
				sql.append(" and ZY_BQYZ.FHBZ=1");
			}
			sql.append(" ORDER BY ZY_BQYZ.BRCH ASC,ZY_BQYZ.JLXH ASC");
			collardrugdetailsparameters.put("JGID", manaUnitId);
			collardrugdetailsparameters.put("SRKS", al_hsql);
			collardrugdetailslist = dao.doQuery(sql.toString(),
					collardrugdetailsparameters);
			// 处理领药明细表单数据
			for (int i = 0; i < collardrugdetailslist.size(); i++) {
				Date ldt_qrsj = null;
				Date ldt_kssj = null;
				if (collardrugdetailslist.get(i).get("QRSJ") != null) {
					ldt_qrsj = sdfdate.parse(collardrugdetailslist.get(i).get(
							"QRSJ")
							+ "");
				}
				if (collardrugdetailslist.get(i).get("KSSJ") != null) {
					ldt_kssj = sdfdate.parse(collardrugdetailslist.get(i).get(
							"KSSJ")
							+ "");
				}
				if (ldt_qrsj == null) {
					continue;
				}
				if (ldt_server.getTime() < ldt_qrsj.getTime()
						|| ldt_server.getTime() < ldt_kssj.getTime()) {// 如果领药日期小于医嘱确认日期
					collardrugdetailslist.remove(i);
					i--;
				}
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("firstrow", 1);
			parameters.put("lastrow", collardrugdetailslist.size());
			parameters.put("ldt_server", ldt_server);
			BSPHISUtil.uf_yztj(collardrugdetailslist, qrsjMap, parameters,
					dao, ctx);
			for (int i = 0; i < collardrugdetailslist.size(); i++) {
				int ll_fycs_total = Integer.parseInt(collardrugdetailslist.get(
						i).get("FYCS")
						+ "");
				if (ll_fycs_total == 0) {
					collardrugdetailslist.remove(i);
					i--;
					continue;
				}
				collardrugdetailslist.get(i).put(
						"YCSL",
						String.format("%1$.3f", collardrugdetailslist.get(i)
								.get("YCSL")));
				collardrugdetailslist.get(i).put(
						"YPYF",
						DictionaryController
								.instance()
								.getDic("phis.dictionary.drugMode")
								.getText(
										collardrugdetailslist.get(i)
												.get("YPYF") + ""));
				collardrugdetailslist.get(i).put(
						"SYPC",
						DictionaryController
								.instance()
								.getDic("phis.dictionary.useRate_yztj")
								.getText(
										collardrugdetailslist.get(i)
												.get("SYPC") + ""));
				collardrugdetailslist.get(i).put(
						"YFSB",
						DictionaryController
								.instance()
								.getDic("phis.dictionary.pharmacy_bqtj")
								.getText(
										collardrugdetailslist.get(i)
												.get("YFSB") + ""));
				if (i == 0) {
					brch = collardrugdetailslist.get(i).get("BRCH") + "";
					brxm = collardrugdetailslist.get(i).get("BRXM") + "";
				} else {
					if (collardrugdetailslist.get(i).get("BRCH") != null
							&& collardrugdetailslist.get(i).get("BRCH")
									.toString().equals(brch)
							&& collardrugdetailslist.get(i).get("BRXM")
									.toString().equals(brxm)) {
						collardrugdetailslist.get(i).put("BRCH", "");
						collardrugdetailslist.get(i).put("BRXM", "");
					} else {
						brch = collardrugdetailslist.get(i).get("BRCH") + "";
						brxm = collardrugdetailslist.get(i).get("BRXM") + "";
					}
				}
				records.add(collardrugdetailslist.get(i));
			}

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnitName();
		int ldt_lsyzpar = 0;
		if (request.get("lsyz") != null) {
			ldt_lsyzpar = Integer.parseInt(request.get("lsyz") + "");
		}
		if (ldt_lsyzpar == 3) {
			response.put("title", jgname + "-病区急诊用药领药单(明细)");
		} else if (ldt_lsyzpar == 4) {
			response.put("title", jgname + "-病区出院带药领药单(明细)");
		} else {
			response.put("title", jgname + "-病区领药单(明细)");
		}
	}
}
