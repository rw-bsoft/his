package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class SuppliesxhmxFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		long ksdm = 0L;
		Date xhrq = null;
		Long brid = 0L;
		try {
			if (request.get("BRID") != null) {
				brid = Long.parseLong(request.get("BRID") + "");
			}
			if (request.get("KSDM") != null) {
				ksdm = Long.parseLong(request.get("KSDM") + "");
			}
			if (request.get("XHRQ") != null) {
				xhrq = sdf.parse(request.get("XHRQ") + "");
			}
			parameter.put("KSDM", ksdm);
			parameter.put("XHRQ", xhrq);
			parameter.put("BRID", brid);
			String sql = "select b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,a.WZSL as WZSL,a.XMJE as WZJE from WL_XHMX a,WL_WZZD b where a.WZXH=b.WZXH and a.BRID=:BRID AND a.XHRQ=:XHRQ and a.KSDM=:KSDM";
			List<Map<String, Object>> rklist = dao.doQuery(sql, parameter);
			// 先放记录集
			double DYJE = 0.0;
			// 每页显示多少行
			int culNum = 15;
			// 总页数
			int pagNum = rklist.size() / culNum;
			for (int i = 0; i < pagNum * culNum; i++) {
				rklist.get(i).put("WZSL",
						String.format("%1$.2f", rklist.get(i).get("WZSL")));
				rklist.get(i).put("WZJE",
						String.format("%1$.2f", rklist.get(i).get("WZJE")));
				DYJE += Double.parseDouble(rklist.get(i).get("WZJE") + "");
				if (rklist.size() > culNum) {
					if ((i + 1) % culNum == 0) {
						rklist.get(i).put("DYJE",
								"合计金额：" + String.format("%1$.2f", DYJE));
						DYJE = 0.0;
					}
				}
				records.add(rklist.get(i));
			}
			for (int i = pagNum * culNum; i < rklist.size(); i++) {
				rklist.get(i).put("WZSL",
						String.format("%1$.2f", rklist.get(i).get("WZSL")));
				rklist.get(i).put("WZJE",
						String.format("%1$.2f", rklist.get(i).get("WZJE")));
				DYJE += Double.parseDouble(rklist.get(i).get("WZJE") + "");
				if (i == (rklist.size() - 1)) {
					rklist.get(i).put("DYJE",
							"合计金额：" + String.format("%1$.2f", DYJE));
					DYJE = 0.0;
				}
				records.add(rklist.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameters1 = new HashMap<String, Object>();
		String username = user.getUserName();
		String brid = null;
		String ksmc = "";
		String xhrq = null;
		Long ksdm = 0L;
		try {
			if (request.get("XHRQ") != null) {
				xhrq = request.get("XHRQ") + "";

			}
			if (request.get("BRID") != null) {
				brid = request.get("BRID") + "";
			}
			if (request.get("KSDM") != null) {
				ksdm = Long.parseLong(request.get("KSDM") + "");
			}
			parameters.put("BRID", brid);
			Map<String, Object> maoBRXM = dao.doLoad(
					"select BRXM as BRXM from MS_BRDA where BRID=:BRID",
					parameters);
			parameters1.put("KSDM", ksdm);

			Map<String, Object> maoksmc = dao.doLoad(
					"select KSMC as KSMC from SYS_Office where KSDM=:KSDM",
					parameters1);
			if (maoksmc.get("KSMC") != null) {
				ksmc = maoksmc.get("KSMC") + "";
			}
			response.put("BRXM", maoBRXM.get("BRXM") + "");
			response.put("KSMC", ksmc);
			response.put("XHRQ", xhrq + "");
			response.put("ZDR", username);
			response.put("JBR", username);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
