package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class RegisterCheckReqOrder implements IHandler {

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		//String quhaomm = String.valueOf(request.get("quhaomm"));
		String zhuanzhendh = String.valueOf(request.get("zhuanzhendh"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			//parameters.put("QUHAOMM", quhaomm);
			parameters.put("JIANCHASQDH", zhuanzhendh);
			String sql = "select a.MZHM as MZHM,a.JIANCHASQDH as JIANCHASQDH,a.BINGRENXM as BINGRENXM,a.SONGJIANKSMC as SONGJIANKSMC,a.SONGJIANYS as SONGJIANYS,a.JIANCHAXMMC as JIANCHAXMMC,a.SONGJIANRQ as SONGJIANRQ,a.ZHENDUAN as ZHENDUAN from DR_CLINICCHECKHISTORY a  where a.JIANCHASQDH=:JIANCHASQDH";
			List<Map<String, Object>> list = dao.doQuery(sql, parameters);
			String bt = "检查预约单";
			if (list.size() > 0) {
				Map<String, Object> map = list.get(0);
				response.putAll(map);
			}
			response.put("BIAOTI", bt);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

}
