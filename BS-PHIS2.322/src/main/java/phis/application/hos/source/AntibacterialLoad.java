package phis.application.hos.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;

import ctd.service.core.Service;
import ctd.service.dao.SimpleLoad;
import ctd.util.context.Context;

public class AntibacterialLoad extends SimpleLoad {
	/**
	 * 
	 * 查询病人就诊信息
	 * 
	 */
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		int code = 200;
		String msg = "Success";
		Object pKey = null;
		if (req.containsKey(PKEY)) {
			pKey = req.get(PKEY);
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		String tableName = "ZY_BRRY";
		parameters.put("JZLX", 1);
		parameters.put("YSDM", "ZSYS");
		if (req.containsKey("openBy") && "fsb".equals(req.get("openBy"))) {
			tableName = "JC_BRRY";
			parameters.put("JZLX", 6);
			parameters.put("YSDM", "ZRYS");
		}

		parameters.put("SQDH", Long.parseLong(pKey.toString()));
		String sql = "select a.*,b.YPMC as YPMC,b.YFGG,b.YPJL,b.YFBZ,c.ZYHM,c.BRXB,c.BRXM,c.RYNL as AGE,:YSDM as ZSYS,c.RYZD as JBMC from AMQC_SYSQ01 a,YK_TYPK b,"
				+ tableName
				+ " c where a.JZLX=:JZLX and a.JZXH=c.ZYH and a.YPXH=b.YPXH and a.SQDH=:SQDH";
		BaseDAO dao = new BaseDAO(ctx);
		try {
			List<Map<String, Object>> rec = dao.doSqlQuery(sql, parameters);
			if (rec.size() > 0) {
				res.put("body", rec.get(0));
			}
		} catch (PersistentDataOperationException e) {
			code = 501;
			msg = "DataAccessException:" + e.getMessage();
			res.put(Service.RES_CODE, code);
			res.put(Service.RES_MESSAGE, msg);
		}
	}
}
