package phis.source.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.utils.SchemaUtil;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.util.context.Context;

public class MedicalDiagnosisZhlrSearchModule extends AbstractSearchModule {
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
			if ("1".equals(ZXLB)) {
				List<Map<String, Object>> Diseasesbw = new ArrayList<Map<String, Object>>();
				Map<String, DictionaryItem> bwinfo = DictionaryController
						.instance().getDic("phis.dictionary.position")
						.getItems();
				for (int i = 1; i <= bwinfo.size(); i++) {
					String a = DictionaryController.instance()
							.getDic("phis.dictionary.position").getText(i + "");
					Map<String, Object> bwMap = new HashMap<String, Object>();
					bwMap.put("ZHBS", i);
					bwMap.put("ZHMC", a);
					Diseasesbw.add(bwMap);
				}
				for (int i = 0; i < Diseasesbw.size(); i++) {
					Diseasesbw.get(i).put("numKey", (i + 1 == 10) ? 0 : i + 1);
					if (i >= 9)
						break;
				}
				SchemaUtil.setDictionaryMassageForList(Diseasesbw,
						"phis.application.cic.schemas.MS_BRZD_CIC");
				res.put("count", Diseasesbw.size());
				res.put("diseasezh", Diseasesbw);
			} else {
				String tableName = "EMR_ZYZH";
				String hqlWhere = "(PYDM LIKE :PYDM or ZHMC LIKE :ZHMC)";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("PYDM", searchText + "%");
				parameters.put("ZHMC", searchText + "%");
				String hql = "select a.ZHBS as ZHBS,a.ZHMC as ZHMC from EMR_ZYZH a where (a.PYDM LIKE :PYDM or a.ZHMC LIKE :ZHMC) order by length(a.ZHMC),a.ZHBS";
				Long count = dao.doCount(tableName, hqlWhere, parameters);
				parameters.put("first", Integer.parseInt(strStart));
				parameters.put("max", Integer.parseInt(strLimit));
				List<Map<String, Object>> Diseaseszh = new ArrayList<Map<String, Object>>();
				if (hql != "") {
					Diseaseszh = dao.doQuery(hql, parameters);
				}
				for (int i = 0; i < Diseaseszh.size(); i++) {
					Diseaseszh.get(i).put("numKey", (i + 1 == 10) ? 0 : i + 1);
					if (i >= 9)
						break;
				}
				SchemaUtil.setDictionaryMassageForList(Diseaseszh,
						"phis.application.cic.schemas.MS_BRZD_CIC");
				res.put("count", count);
				res.put("diseasezh", Diseaseszh);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
