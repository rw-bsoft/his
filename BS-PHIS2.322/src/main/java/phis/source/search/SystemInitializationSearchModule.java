package phis.source.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;

public class SystemInitializationSearchModule extends AbstractSearchModule {
	/**
	 * 实现药品查询功能
	 */
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		BaseDAO dao = new BaseDAO();
		String id = req.get("node_id") + "";
		String parent_id = req.get("parent_id") + "";
		String[] parent_ids = parent_id.split("/");
		try {
			List<Map<String, Object>> JSONObjects = new ArrayList<Map<String, Object>>();
			if (parent_ids.length == 2) {
				String hql = "SELECT GROUPID as GROUPID,GROUPNAME as GROUPNAME FROM GY_GNJ WHERE SYBZ = 1 ORDER BY GROUPID";
				List<Map<String, Object>> GY_GNJ = dao.doQuery(hql, null);
				for (int i = 0; i < GY_GNJ.size(); i++) {
					Map<String, Object> JsonMap = new HashMap<String, Object>();
					JsonMap.put("id", GY_GNJ.get(i).get("GROUPID"));
					JsonMap.put("text", GY_GNJ.get(i).get("GROUPNAME"));
					String GROUPID = GY_GNJ.get(i).get("GROUPID") + "";
					if ("1".equals(GROUPID) || "4".equals(GROUPID)
							|| "5".equals(GROUPID) || "6".equals(GROUPID)) {
						JsonMap.put("leaf", true);
						long count = dao.doCount("GY_CSH",
								"JGID = '" + manaUnitId + "' and GROUPID = "
										+ GY_GNJ.get(i).get("GROUPID")
										+ " and OFFICEID = 0 and INIT = 1",
								null);
						if (count > 0) {
							JsonMap.put("checked", true);
							JsonMap.put("disabled", true);
						} else {
							JsonMap.put("checked", false);
						}
					} else if ("2".equals(GROUPID) || "3".equals(GROUPID)) {
						JsonMap.put("leaf", false);
						JsonMap.put("expanded", true);
					} else if ("3".equals(GY_GNJ.get(i).get("GROUPID"))) {
						JsonMap.put("leaf", false);
						JsonMap.put("expanded", true);
					}
					JSONObjects.add(JsonMap);
				}
			} else {
				if ("2".equals(id)) {
					String hql = "SELECT YKMC as YKMC,YKSB as YKSB FROM YK_YKLB WHERE JGID = '"
							+ manaUnitId + "' ORDER BY YKSB";
					List<Map<String, Object>> GY_GNJ = dao.doQuery(hql, null);
					for (int i = 0; i < GY_GNJ.size(); i++) {
						long count = dao.doCount("GY_CSH", "JGID = '"
								+ manaUnitId + "' and GROUPID = " + id
								+ " and OFFICEID = "
								+ GY_GNJ.get(i).get("YKSB") + " and INIT = 1",
								null);
						Map<String, Object> JsonMap = new HashMap<String, Object>();
						if (count > 0) {
							JsonMap.put("checked", true);
							JsonMap.put("disabled", true);
						} else {
							JsonMap.put("checked", false);
							;
						}
						JsonMap.put("id", GY_GNJ.get(i).get("YKSB").toString());
						JsonMap.put("text", GY_GNJ.get(i).get("YKMC"));
						JsonMap.put("leaf", true);
						JSONObjects.add(JsonMap);
					}
				}
				if ("3".equals(id)) {
					String hql = "SELECT YFSB as YFSB,YFMC as YFMC FROM YF_YFLB WHERE JGID = '"
							+ manaUnitId + "' ORDER BY YFSB";
					List<Map<String, Object>> GY_GNJ = dao.doQuery(hql, null);
					for (int i = 0; i < GY_GNJ.size(); i++) {
						long count = dao.doCount("GY_CSH", "JGID = '"
								+ manaUnitId + "' and GROUPID = " + id
								+ " and OFFICEID = "
								+ GY_GNJ.get(i).get("YFSB") + " and INIT = 1",
								null);
						Map<String, Object> JsonMap = new HashMap<String, Object>();
						if (count > 0) {
							JsonMap.put("checked", true);
							JsonMap.put("disabled", true);
						} else {
							JsonMap.put("checked", false);
							;
						}
						JsonMap.put("id", GY_GNJ.get(i).get("YFSB").toString());
						JsonMap.put("text", GY_GNJ.get(i).get("YFMC"));
						JsonMap.put("leaf", true);
						JSONObjects.add(JsonMap);
					}
				}
			}
			res.put("body", JSONObjects);
//			String retStr = JSONUtil.toJson(JSONObjects);
//			res.setCharacterEncoding("utf-8");
//			PrintWriter out = res.getWriter();
//			out.println(retStr);
//			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
