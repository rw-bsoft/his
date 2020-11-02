package phis.prints.bean; 
import java.util.HashMap;
import java.util.List;
import java.util.Map;  

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;

import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context; 
public class StorehousePaymentDetailsFile implements IHandler {
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException { 
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 获取JGID 
		String dwxh = null;
		String sql = null;
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		long yksb = Long.parseLong(user.getProperty("storehouseId")+"");// 用户的药库识别
        parameters.put("YKSB", yksb); 
		parameters.put("JGID", jgid);
		parameters.put("DWXH", dwxh);
		if (request.get("dwxh") != null) {
			dwxh = request.get("dwxh") + "";
		}
		parameters.put("DWXH", Long.parseLong(dwxh));
		try {
			if (!"".equals(dwxh)) {
				if (!"0".equals(dwxh)) {
					sql = "SELECT c.DWMC as DWMC,b.DWXH as DWXH,a.YSDH as YSDH,b.PWD as PWD,SUM(a.JHHJ) as JHZE,SUM(a.PFJE) as PFZE,SUM(a.LSJE) as LSZE,sum(a.LSJE)-sum(a.JHHJ) as JXCE,d.RKFS as RKFS,d.FSMC as FSMC,a.YSRQ as YSRQ,a.FKRQ as FKRQ,a.FPHM as FPHM,b.RKDH as RKDH,b.RKBZ as RKBZ,to_char(b.RKRQ,'yyyy-mm-dd hh24:mi:ss') as RKRQ FROM YK_RK02 a,YK_RK01 b,YK_JHDW c,YK_RKFS d where b.XTSB = a.XTSB and b.RKFS = a.RKFS and b.RKDH = a.RKDH and b.DWXH = c.DWXH and b.DWXH = :DWXH and b.XTSB = :YKSB and b.XTSB = d.XTSB and b.RKFS = d.RKFS and a.FKRQ is null and a.YSRQ is not null and EXISTS (SELECT * FROM GY_XTPZ WHERE DMLB = 15000 and PZBH = 1 and d.DYFS = PZBH ) and b.JGID = a.JGID and b.JGID = :JGID GROUP BY c.DWMC,a.YSDH,b.PWD,b.DWXH,d.RKFS,d.FSMC,a.YSRQ,a.FKRQ,a.FPHM,b.RKDH,b.RKBZ,b.RKRQ order by a.FPHM";
				} else {
					sql = "SELECT c.DWMC as DWMC,b.DWXH as DWXH,a.YSDH as YSDH,b.PWD as PWD,SUM(a.JHHJ) as JHZE,SUM(a.PFJE) as PFZE,SUM(a.LSJE) as LSZE,sum(a.LSJE)-sum(a.JHHJ) as JXCE,d.RKFS as RKFS,d.FSMC as FSMC,a.YSRQ as YSRQ,a.FKRQ as FKRQ,a.FPHM as FPHM,b.RKDH as RKDH,b.RKBZ as RKBZ,to_char(b.RKRQ,'yyyy-mm-dd hh24:mi:ss') as RKRQ FROM YK_RK02 a,YK_RK01 b,YK_JHDW c,YK_RKFS d where b.XTSB = a.XTSB and b.RKFS = a.RKFS and b.RKDH = a.RKDH and b.DWXH = c.DWXH and b.DWXH = :DWXH and b.XTSB = :YKSB and b.XTSB = d.XTSB and b.RKFS = d.RKFS and a.FKRQ is null and a.YSRQ is not null and EXISTS (SELECT * FROM GY_XTPZ WHERE DMLB = 15000 and PZBH = 1 and d.DYFS = PZBH ) and b.JGID = a.JGID and b.JGID = :JGID GROUP BY c.DWMC,a.YSDH,b.PWD,b.DWXH,d.RKFS,d.FSMC,a.YSRQ,a.FKRQ,a.FPHM,b.RKDH,b.RKBZ,b.RKRQ order by a.FPHM";
				}
				List<Map<String, Object>> kcmxlist = dao.doSqlQuery(sql,
						parameters);
				for (int i = 0; i < kcmxlist.size(); i++) {
					try {
						kcmxlist.get(i).put(
								"RKFS",
								DictionaryController.instance().get("phis.dictionary.storeroomStorage")
										.getText(kcmxlist.get(i).get("RKFS") + ""));
					} catch (ControllerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					kcmxlist.get(i)
					.put("JHZE",
							String.format("%1$.2f", kcmxlist.get(i)
									.get("JHZE")));
			kcmxlist.get(i)
					.put("PFZE",
							String.format("%1$.2f", kcmxlist.get(i)
									.get("PFZE")));
			kcmxlist.get(i)
					.put("LSZE",
							String.format("%1$.2f", kcmxlist.get(i)
									.get("LSZE")));
			kcmxlist.get(i)
					.put("JXCE",
							String.format("%1$.2f", kcmxlist.get(i)
									.get("JXCE")));
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
		String jgid = user.getManageUnitId();// 用户的机构ID
		String dwxh = null;
		String sql = null;
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		long yksb = Long.parseLong(user.getProperty("storehouseId")+"");// 用户的药库识别
		parameters.put("YKSB", yksb);
		parameters.put("JGID", jgid);
		if (request.get("dwxh") != null) {
			dwxh = request.get("dwxh") + "";
		}
		parameters.put("DWXH", Long.parseLong(dwxh));
		try {
			if (!"".equals(dwxh)) {
				if (!"0".equals(dwxh)) {
					sql = "SELECT SUM(a.JHHJ) as JHZE,SUM(a.PFJE) as PFZE,SUM(a.LSJE) as LSZE,sum(a.LSJE)-sum(a.JHHJ) as JXCE FROM YK_RK02 a,YK_RK01 b,YK_JHDW c,YK_RKFS d where b.XTSB = a.XTSB and b.RKFS = a.RKFS and b.RKDH = a.RKDH and b.DWXH = c.DWXH and b.DWXH = :DWXH and b.XTSB = :YKSB and b.XTSB = d.XTSB and b.RKFS = d.RKFS and a.FKRQ is null and a.YSRQ is not null and EXISTS (SELECT * FROM GY_XTPZ WHERE DMLB = 15000 and PZBH = 1 and d.DYFS = PZBH ) and b.JGID = a.JGID and b.JGID = :JGID order by b.DWXH";
				} else {
					sql = "SELECT SUM(a.JHHJ) as JHZE,SUM(a.PFJE) as PFZE,SUM(a.LSJE) as LSZE,sum(a.LSJE)-sum(a.JHHJ) as JXCE FROM YK_RK02 a,YK_RK01 b,YK_JHDW c,YK_RKFS d where b.XTSB = a.XTSB and b.RKFS = a.RKFS and b.RKDH = a.RKDH and b.DWXH = c.DWXH and b.DWXH = :DWXH and b.XTSB = :YKSB and b.XTSB = d.XTSB and b.RKFS = d.RKFS and a.FKRQ is null and a.YSRQ is not null and EXISTS (SELECT * FROM GY_XTPZ WHERE DMLB = 15000 and PZBH = 1 and d.DYFS = PZBH ) and b.JGID = a.JGID and b.JGID = :JGID order by b.DWXH";
				}
				List<Map<String, Object>> kcmxlist = dao.doSqlQuery(sql,
						parameters);
				double jhze = 0.00;
				double lsze = 0.00;
				double jxce = 0.00;
				if (kcmxlist.size() > 0) {
					if (kcmxlist.get(0).get("JHZE") != null) {
						jhze = Double.parseDouble(kcmxlist.get(0).get("JHZE")
								+ "");
					}
					if (kcmxlist.get(0).get("JXCE") != null) {
						jxce = Double.parseDouble(kcmxlist.get(0).get("JXCE")
								+ "");
					}
					if (kcmxlist.get(0).get("LSZE") != null) {
						lsze = Double.parseDouble(kcmxlist.get(0).get("LSZE")
								+ "");
					}
				}
				response.put("SUMJHZE", String.format("%1$.2f", jhze));
				response.put("SUMJXCE", String.format("%1$.2f", jxce));
				response.put("SUMLSZE", String.format("%1$.2f", lsze));
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
