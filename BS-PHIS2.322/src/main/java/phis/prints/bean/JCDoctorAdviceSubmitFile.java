package phis.prints.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.sequence.KeyManager;
import ctd.util.context.Context;

public class JCDoctorAdviceSubmitFile implements IHandler {
	@SuppressWarnings("deprecation")
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		String brxm = null;
		try {
			List<Map<String, Object>> collardrugdetailslist = new ArrayList<Map<String, Object>>();
			Map<String, Object> collardrugdetailsparameters = new HashMap<String, Object>();
			StringBuffer sql = new StringBuffer(
					"SELECT b.BRXM as BRXM,a.YZMC as YZMC,a.MZCS as MZCS,str(a.KSSJ,'YYYY-MM-DD HH24:MI:SS') as KSSJ,str(a.TZSJ,'YYYY-MM-DD HH24:MI:SS') as TZSJ,a.YPYF as YPYF,a.MRCS as MRCS,a.YCSL as YCSL,a.YPDJ as YPDJ,a.SYBZ as SYBZ,str(a.QRSJ,'YYYY-MM-DD HH24:MI:SS') as QRSJ,a.YPXH as YPXH,a.YPLX as YPLX,a.JLXH as JLXH,a.FYFS as FYFS,a.SYPC as SYPC,a.ZYH as ZYH,a.BZXX as BZXX,a.XMLX as XMLX,a.YZZH as YZZH,a.FYSX as FYSX,a.LSYZ as LSYZ,a.YFSB as YFSB,0 as FYTS,0 as FYCS,0.00 as YPSL,c.YPSX as YPSX,a.YPCD as YPCD,a.YZPB as YZPB,a.YSTJ as YSTJ,a.YEPB as YEPB,a.SRCS as SRCS,a.ZXKS as ZXKS,a.YFGG as YFGG,a.YFDW as YFDW,a.YFBZ as YFBZ,a.YYTS as YYTS,a.YZZXSJ as YZZXSJ FROM JC_BRYZ a,JC_BRRY b,YK_TYPK c WHERE ( a.ZYH=b.ZYH and b.CYPB=0) and (a.YPXH=c.YPXH)  and (a.LSBZ=0) and (a.SYBZ = 0) and (a.XMLX<4) and (a.FYSX<>2) AND (a.JFBZ<2)   and ZFBZ=0 and ((a.PSPB IS NULL) or (a.PSPB=0) or (a.PSPB=1))");
			if (request.containsKey("jlxhs")) {
				sql.append(" and a.JLXH in(:jlxhs)");
				String j = MedicineUtils.parseString(request.get("jlxhs"));
				String[] s = j.split(",");
				List<Long> jlxhs = new ArrayList<Long>();
				for (String o : s) {
					jlxhs.add(MedicineUtils.parseLong(o));
				}
				collardrugdetailsparameters.put("jlxhs", jlxhs);
			}
			sql.append(" ORDER BY a.JLXH ASC");
			collardrugdetailslist = dao.doQuery(sql.toString(),
					collardrugdetailsparameters);
			for (int i = 0; i < collardrugdetailslist.size(); i++) {
				collardrugdetailslist.get(i).put(
						"JE",MedicineUtils.simpleMultiply(2, collardrugdetailslist.get(i).get("YCSL"), collardrugdetailslist.get(i).get("YPDJ")));
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
					brxm = collardrugdetailslist.get(i).get("BRXM") + "";
				} else {
					if (collardrugdetailslist.get(i).get("BRXM")
									.toString().equals(brxm)) {
						collardrugdetailslist.get(i).put("BRXM", "");
					} else {
						brxm = collardrugdetailslist.get(i).get("BRXM") + "";
					}
				}
				records.add(collardrugdetailslist.get(i));
			}

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} 
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnitName();
			response.put("title", jgname + "-家床领药单(明细)");
	}
}
