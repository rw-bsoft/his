package phis.source.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import ctd.util.context.Context;

public class MedicalDiagnosisSearchModule extends AbstractSearchModule {
	/**
	 * 疾病查询功能
	 */
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String searchText = MATCH_TYPE
				+ req.get("query").toString().toUpperCase();
		String ZXLB = req.get("ZXLB") + "";// 药品类别
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		BaseDAO dao = new BaseDAO();
		try {
			String hql = "";
			String tableName = "GY_JBBM";
			String hqlWhere = "PYDM LIKE :PYDM";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("PYDM", searchText + "%");
			if ("1".equals(ZXLB)) {
				hql = "select a.JBXH as JBXH,a.JBMC as MSZD,a.ICD10 as JBBM from GY_JBBM a where a.PYDM LIKE :PYDM order by length(a.JBMC),a.JBXH";
			} else if ("2".equals(ZXLB)) {
				hql = "select a.JBBS as JBXH,a.JBMC as MSZD,a.JBDM as JBBM from EMR_ZYJB a where a.PYDM LIKE :PYDM order by length(a.JBMC),a.JBBS";
				tableName = "EMR_ZYJB";
			}
			Long count = dao.doCount(tableName, hqlWhere, parameters);
			parameters.put("first", Integer.parseInt(strStart));
			parameters.put("max", Integer.parseInt(strLimit));
			List<Map<String, Object>> Diseases = dao.doQuery(hql, parameters);
			for (int i = 0; i < Diseases.size(); i++) {
				Diseases.get(i).put("numKey", (i + 1 == 10) ? 0 : i + 1);
				if (i >= 9)
					break;
			}
			res.put("count", count);
			res.put("disease", Diseases);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
