package phis.prints.bean; 
import java.util.HashMap;
import java.util.List;
import java.util.Map; 

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context; 
public class PaymentProcessingFile implements IHandler {
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String ypxh = null;
		String sql = null;
		int ysdh = 0;
		int rkfs = 0;
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		long yksb = Long.parseLong(user.getProperty("storehouseId")+"");// 用户的药库识别
		parameters.put("XTSB", yksb);
		parameters.put("JGID", jgid);
		if (request.get("ypxh") != null) {
			ypxh = request.get("ypxh") + "";
		}
		if (request.get("ysdh") != null) {
			ysdh = Integer.parseInt(request.get("ysdh") + "");
			parameters.put("YSDH", ysdh);
		}
		if (request.get("rkfs") != null) {
			rkfs = Integer.parseInt(request.get("rkfs") + "");
			parameters.put("RKFS", rkfs);
		}
		try {
			if (!"".equals(ypxh)) {
				if (!"0".equals(ypxh)) {
					sql = "SELECT c.YPMC as YPMC,b.CDMC as CDMC,a.YSDH as YSDH,a.FKJE as FKJE,a.YFJE as YFJE,a.SBXH as SBXH,a.XTSB as XTSB,a.RKFS as RKFS,a.RKDH as RKDH,a.YPXH as YPXH,a.YPCD as YPCD,a.PZHM as PZHM,a.FKRQ as FKRQ,a.FKGH as FKGH,a.FKJE-a.YFJE as BCJE FROM YK_RK02 a,YK_CDDZ  b,YK_TYPK c WHERE a.YPCD = b.YPCD and a.YPXH = c.YPXH and a.YSDH = :YSDH and a.RKFS = :RKFS and a.JGID = :JGID and a.XTSB = :XTSB and a.YPXH in ("
							+ ypxh + ")  order by a.YPXH";
				} else {
					sql = "SELECT c.YPMC as YPMC,b.CDMC as CDMC,a.YSDH as YSDH,a.FKJE as FKJE,a.YFJE as YFJE,a.SBXH as SBXH,a.XTSB as XTSB,a.RKFS as RKFS,a.RKDH as RKDH,a.YPXH as YPXH,a.YPCD as YPCD,a.PZHM as PZHM,a.FKRQ as FKRQ,a.FKGH as FKGH,a.FKJE-a.YFJE as BCJE FROM YK_RK02 a,YK_CDDZ  b,YK_TYPK c WHERE a.YPCD = b.YPCD and a.YPXH = c.YPXH and a.YSDH = :YSDH and a.RKFS = :RKFS and a.JGID = :JGID and a.XTSB = :XTSB order by a.YPXH";
				}
				List<Map<String, Object>> kcmxlist = dao.doSqlQuery(sql,
						parameters);
				for (int i = 0; i < kcmxlist.size(); i++) {
					kcmxlist.get(i)
							.put("FKJE",
									String.format("%1$.2f", kcmxlist.get(i)
											.get("FKJE")));
					kcmxlist.get(i)
							.put("YFJE",
									String.format("%1$.2f", kcmxlist.get(i)
											.get("YFJE")));
					kcmxlist.get(i)
							.put("BCJE",
									String.format("%1$.2f", kcmxlist.get(i)
											.get("BCJE")));
				}
				records.addAll(kcmxlist);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnitName();
		response.put("TITLE", jgname);
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String ypxh = null;
		String sql = null;
		int ysdh = 0;
		int rkfs = 0;
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		long yksb = Long.parseLong(user.getProperty("storehouseId")+"");// 用户的药库识别
		parameters.put("XTSB", yksb);
		parameters.put("JGID", jgid);
		if (request.get("ypxh") != null) {
			ypxh = request.get("ypxh") + "";
		}
		if (request.get("ysdh") != null) {
			ysdh = Integer.parseInt(request.get("ysdh") + "");
			parameters.put("YSDH", ysdh);
		}
		if (request.get("rkfs") != null) {
			rkfs = Integer.parseInt(request.get("rkfs") + "");
			parameters.put("RKFS", rkfs);
		}
		try {
			if (!"".equals(ypxh)) {
				if (!"0".equals(ypxh)) {
					sql = "SELECT sum(a.FKJE) as FKJE,sum(a.YFJE) as YFJE,sum(a.FKJE)-sum(a.YFJE) as BCJE FROM YK_RK02 a,YK_CDDZ  b,YK_TYPK c WHERE a.YPCD = b.YPCD and a.YPXH = c.YPXH and a.YSDH = :YSDH and a.RKFS = :RKFS and a.JGID = :JGID and a.XTSB = :XTSB and a.YPXH in ("
							+ ypxh + ")";
				} else {
					sql = "SELECT sum(a.FKJE) as FKJE,sum(a.YFJE) as YFJE,sum(a.FKJE)-sum(a.YFJE) as BCJE FROM YK_RK02 a,YK_CDDZ  b,YK_TYPK c WHERE a.YPCD = b.YPCD and a.YPXH = c.YPXH and a.YSDH = :YSDH and a.RKFS = :RKFS and a.JGID = :JGID and a.XTSB = :XTSB";
				}
				List<Map<String, Object>> kcmxlist = dao.doSqlQuery(sql,
						parameters);
				double fkje = 0.00;
				double yfje = 0.00;
				double bcje = 0.00;
				if (kcmxlist.size() > 0) {
					if (kcmxlist.get(0).get("FKJE") != null) {
						fkje = Double.parseDouble(kcmxlist.get(0).get("FKJE")
								+ "");
					}
					if (kcmxlist.get(0).get("YFJE") != null) {
						yfje = Double.parseDouble(kcmxlist.get(0).get("YFJE")
								+ "");
					}
					if (kcmxlist.get(0).get("BCJE") != null) {
						bcje = Double.parseDouble(kcmxlist.get(0).get("BCJE")
								+ "");
					}
				}
				response.put("SUMFKJE", String.format("%1$.2f", fkje));
				response.put("SUMYFJE", String.format("%1$.2f", yfje));
				response.put("SUMBCJE", String.format("%1$.2f", bcje));
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
