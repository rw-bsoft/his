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

public class RegisterReqOrder implements IHandler {

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		String quhaomm = String.valueOf(request.get("quhaomm"));
		String zhuanzhendh = String.valueOf(request.get("zhuanzhendh"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("QUHAOMM", quhaomm);
			parameters.put("ZHUANZHENDH", zhuanzhendh);
			String sql = "select a.MZHM as MZHM,b.BINGRENXM as BINGRENXM,a.GUAHAOXH as GUAHAOXH,a.JIUZHENSJ as JIUZHENSJ,a.GUAHAOBC as GUAHAOBC,a.QUHAOMM as QUHAOMM,a.YUYUERQ as YUYUERQ,a.ZHUANRUKSMC as ZHUANRUKSMC,a.YISHENGXM as YISHENGXM,b.SHENQINGJGMC as SHENQINGJGMC,c.PERSONNAME as SHENQINGYS,b.SHENQINGYSDH as SHENQINGYSDH,a.ZHUANRUYYMC as ZHUANRUYYMC from DR_CLINICZZRECORDHISTORY a,DR_CLINICRECORDLHISTORY b,SYS_Personnel c where a.ZHUANZHENDH =:ZHUANZHENDH and a.QUHAOMM =:QUHAOMM and a.ZHUANZHENDH = b.ZHUANZHENDH and b.SHENQINGYS = c.PERSONID";
			List<Map<String, Object>> list = dao.doQuery(sql, parameters);
			String bt = "挂号预约单（双向转诊）";
			if (list.size() > 0) {
				Map<String, Object> map = list.get(0);
				response.putAll(map);
				if (map.containsKey("GUAHAOBC")
						&& map.get("GUAHAOBC") != "null"
						&& Integer.parseInt(map.get("GUAHAOBC") + "") == 1) {
					response.put("GUAHAOBC", "上午");
				} else if (map.containsKey("GUAHAOBC")	
						&& map.get("GUAHAOBC") != "null"
						&& Integer.parseInt(map.get("GUAHAOBC") + "") == 2) {
					response.put("GUAHAOBC", "下午");
				}
				if ("470032824".equals(map.get("ZHUANRUYYDM"))) {
					bt = "挂号预约单";
				}
			}
			response.put("BIAOTI", bt);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

}
