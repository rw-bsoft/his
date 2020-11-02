package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;

import ctd.util.context.Context;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;

//DynamicPrint
public class DepartmentConsumptionDetailsFile implements IHandler {

	public void getFields(Map<String, Object> req,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		Long yksb = MedicineUtils.parseLong(UserRoleToken.getCurrent()
				.getProperty("storehouseId"));
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", jgid);
		parameters.put("yksb", yksb);
		long ksdm = parseLong(req.get("ksdm"));
		parameters.put("CKKS", ksdm);
		StringBuffer sql = new StringBuffer();
		sql.append("select d.YPMC as YPMC,d.YPGG as YPGG,d.YPDW as YPDW,e.CDMC as CDMC,b.SFSL as SFSL,b.JHJE as JHJE,b.LSJE as LSJE ");
		sql.append("from YK_CK01 a, YK_CK02 b,YK_CKFS c ");
		sql.append(" ,YK_TYPK d,YK_CDDZ e where d.YPXH = b.YPXH and b.YPCD=e.YPCD");
		sql.append(" and b.XTSB = a.XTSB and b.CKFS = a.CKFS and b.CKDH = a.CKDH and c.JGID = :JGID and c.XTSB=:yksb ");
		sql.append(" and a.CKPB = 1 and a.XTSB = c.XTSB and a.CKFS = c.CKFS and c.KSPB = 1");
		if (req.get("dateFrom") != null) {
			sql.append(" and to_char(a.CKRQ,'yyyy-mm-dd hh24:mi:ss')>=:dateFrom ");
			parameters.put("dateFrom", req.get("dateFrom") + " 00:00:00");
		}
		if (req.get("dateTo") != null) {
			sql.append(" and to_char(a.CKRQ,'yyyy-mm-dd hh24:mi:ss')<=:dateTo ");
			parameters.put("dateTo", req.get("dateTo") + " 23:59:59");
		}
		if (req.get("zblb") != null && !req.get("zblb").equals("")) {
			sql.append(" and d.TYPE=:zblb ");
			parameters.put("zblb", req.get("zblb") + "");
		}
		sql.append(" and a.CKKS=:CKKS and a.CKKS is not null ORDER BY a.CKKS ASC");
		try {
			List<Map<String, Object>> cklsit = dao.doSqlQuery(sql.toString(),
					parameters);
			// 先放记录集
			double JHHJ = 0.0;
			double LSHJ = 0.0;
			// 每页显示多少行
			int culNum = 16;
			if (cklsit.size() % culNum != 0) {
				for (int i = 0; i < culNum; i++) {
					Map<String, Object> ckMAP = new HashMap<String, Object>();
					ckMAP.put("YPMC", "");
					ckMAP.put("YPGG", "");
					ckMAP.put("YPDW", "");
					ckMAP.put("CDMC", "");
					ckMAP.put("SFSL", "");
					ckMAP.put("JHJE", "");
					ckMAP.put("LSJE", "");
					cklsit.add(ckMAP);
					if (cklsit.size() % culNum == 0) {
						break;
					}
				}
			}
			// 总页数
			int pagNum = cklsit.size() / culNum;
			for (int i = 0; i < pagNum * culNum; i++) {
				if (cklsit.get(i).get("SFSL") != null
						&& cklsit.get(i).get("SFSL") != "") {
					cklsit.get(i).put("SFSL",
							String.format("%1$.0f", cklsit.get(i).get("SFSL")));
				}
				if (cklsit.get(i).get("JHJE") != null
						&& cklsit.get(i).get("JHJE") != "") {
					cklsit.get(i).put("JHJE",
							String.format("%1$.2f", cklsit.get(i).get("JHJE")));
					JHHJ += Double.parseDouble(cklsit.get(i).get("JHJE") + "");
				}
				if (cklsit.get(i).get("LSJE") != null
						&& cklsit.get(i).get("LSJE") != "") {
					cklsit.get(i).put("LSJE",
							String.format("%1$.2f", cklsit.get(i).get("LSJE")));
					LSHJ += Double.parseDouble(cklsit.get(i).get("LSJE") + "");
				}
				if ((i + 1) % culNum == 0) {
					cklsit.get(i).put("BYHJ", "本页合计");
					cklsit.get(i).put("BYJHHJ", String.format("%1$.2f", JHHJ));
					cklsit.get(i).put("BYLSHJ", String.format("%1$.2f", LSHJ));
					JHHJ = 0.0;
					LSHJ = 0.0;
				}
				records.add(cklsit.get(i));
			}
			for (int i = pagNum * culNum; i < cklsit.size(); i++) {
				if (cklsit.get(i).get("SFSL") != null
						&& cklsit.get(i).get("SFSL") != "") {
					cklsit.get(i).put("SFSL",
							String.format("%1$.0f", cklsit.get(i).get("SFSL")));
				}
				if (cklsit.get(i).get("JHJE") != null
						&& cklsit.get(i).get("JHJE") != "") {
					cklsit.get(i).put("JHJE",
							String.format("%1$.2f", cklsit.get(i).get("JHJE")));
					JHHJ += Double.parseDouble(cklsit.get(i).get("JHJE") + "");
				}
				if (cklsit.get(i).get("LSJE") != null
						&& cklsit.get(i).get("LSJE") != "") {
					cklsit.get(i).put("LSJE",
							String.format("%1$.2f", cklsit.get(i).get("LSJE")));
					LSHJ += Double.parseDouble(cklsit.get(i).get("LSJE") + "");
				}
				if (i == (cklsit.size() - 1)) {
					cklsit.get(i).put("BYHJ", "本页合计");
					cklsit.get(i).put("BYJHHJ", String.format("%1$.2f", JHHJ));
					cklsit.get(i).put("BYLSHJ", String.format("%1$.2f", LSHJ));
					JHHJ = 0.0;
					LSHJ = 0.0;

				}
				records.add(cklsit.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getParameters(Map<String, Object> req,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Long yksb = MedicineUtils.parseLong(UserRoleToken.getCurrent()
				.getProperty("storehouseId"));
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", jgid);
		parameters.put("yksb", yksb);
		long ksdm = parseLong(req.get("ksdm"));
		parameters.put("CKKS", ksdm);
		String ksdmstr = req.get("ksdm") + "";
		StringBuffer sql = new StringBuffer();
		sql.append("select sum(b.JHJE) as JHJE,sum(b.LSJE) as LSJE ");
		sql.append("from YK_CK01 a, YK_CK02 b,YK_CKFS c ");
		sql.append(" ,YK_TYPK d,YK_CDDZ e where d.YPXH = b.YPXH and b.YPCD=e.YPCD");
		sql.append(" and b.XTSB = a.XTSB and b.CKFS = a.CKFS and b.CKDH = a.CKDH and c.JGID = :JGID and c.XTSB=:yksb ");
		sql.append(" and a.CKPB = 1 and a.XTSB = c.XTSB and a.CKFS = c.CKFS and c.KSPB = 1");
		if (req.get("dateFrom") != null) {
			sql.append(" and to_char(a.CKRQ,'yyyy-mm-dd hh24:mi:ss')>=:dateFrom ");
			parameters.put("dateFrom", req.get("dateFrom") + " 00:00:00");
		}
		if (req.get("dateTo") != null) {
			sql.append(" and to_char(a.CKRQ,'yyyy-mm-dd hh24:mi:ss')<=:dateTo ");
			parameters.put("dateTo", req.get("dateTo") + " 23:59:59");
		}
		if (req.get("zblb") != null && !req.get("zblb").equals("")) {
			sql.append(" and d.TYPE=:zblb ");
			parameters.put("zblb", req.get("zblb") + "");
		}
		sql.append(" and a.CKKS=:CKKS and a.CKKS is not null ORDER BY a.CKKS ASC");
		try {
			List<Map<String, Object>> cklsit = dao.doSqlQuery(sql.toString(),
					parameters);
			if (cklsit.size() > 0) {
				response.put("JHJESUM",
						String.format("%1$.2f", cklsit.get(0).get("JHJE")));
				response.put("LSJESUM",
						String.format("%1$.2f", cklsit.get(0).get("LSJE")));
			}
			response.put(
					"CKKS",
					DictionaryController.instance()
							.getDic("phis.dictionary.department")
							.getItem(ksdmstr).getText());
			response.put("KSRQ", req.get("dateFrom") + " 00:00:00");
			response.put("JSRQ", req.get("dateTo") + " 23:59:59");
			response.put("ZDR", sdf.format(new Date()));
			response.put("LLR", user.getUserName());
			response.put("TITLE", user.getManageUnit().getName() + "科室消耗药品明细");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public long parseLong(Object o) {
		if (o == null) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}
}
