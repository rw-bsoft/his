package phis.source.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.utils.BSHISUtil;
import ctd.util.context.Context;

public class TemperatureSearchModule extends AbstractSearchModule {

	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		// String searchText = req.getParameter("query").toUpperCase();
		// Long zyh = Long.parseLong(req.getParameter("zyh"));
		String date = req.get("date") + "";//
		String zhy = req.get("zhy") + "";//
		BaseDAO dao = new BaseDAO(ctx);
		try {
			List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> list = dao
					.doSqlQuery(
							"select to_char(CJSJ,'yyyy-MM-dd HH24:mi:ss') as CJSJ,to_char(CJSJ,'HH24') as HOUR,TZNR,XMXB,XMH,CJH from BQ_SMTZ where ZYH="
									+ zhy
									+ " and to_char(CJSJ,'yyyy-MM-dd')='"
									+ date
									+ "' and XMH="
									+ 1
									+ " and FCBZ=0 order by CJSJ,CJH", null);

			Map<Integer, Map<String, Object>> retMap = new HashMap<Integer, Map<String, Object>>();
			for (Map<String, Object> tempOjb : list) {
				Integer hour = Integer.parseInt(tempOjb.get("HOUR").toString());
				Integer index = hour / 4;
				if (!retMap.containsKey(index)
						|| BSHISUtil.toDate(
								retMap.get(index).get("CJSJ").toString())
								.getTime() >= BSHISUtil.toDate(
								tempOjb.get("CJSJ").toString()).getTime()) {
					retMap.put(index, tempOjb);
				}
			}
			retList.addAll(retMap.values());
			res.put("count", retList.size());
			res.put("mds", retList);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
