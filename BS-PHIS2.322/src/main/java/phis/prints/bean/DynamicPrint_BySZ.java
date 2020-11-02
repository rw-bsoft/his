package phis.prints.bean;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import ctd.print.DynamicPrint;

public abstract class DynamicPrint_BySZ extends DynamicPrint {
	/**
	 * 得到项目归并规则
	 * 
	 * @author gaof
	 * @param uid
	 * @param ss
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getListXmgb(int bbbh, String uid,
			Session ss) {
		StringBuffer sql_xmgb = new StringBuffer();
		sql_xmgb.append(
				"select GY_XMGB.Sfxm,GY_XMGB.GBXM,GY_XMGB.Xmmc,GY_XMGB.Sxh from GY_XMGB"
						+ " where GY_XMGB.Bbbh = ").append(bbbh)
				.append(" and GY_XMGB.JGID = ").append(uid);
		List<Map<String, Object>> list_xmgb = ss
				.createSQLQuery(sql_xmgb.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		// 如果没有对应的，则使用默认规则
		if (list_xmgb.size() == 0 || list_xmgb == null) {
			StringBuffer sql_xmgb_default = new StringBuffer();
			sql_xmgb_default
					.append("SELECT SFXM,SFXM AS GBXM, SFMC AS XMMC FROM GY_SFXM order by length(PLSX),PLSX");
			List<Map<String, Object>> list_xmgb_default = ss
					.createSQLQuery(sql_xmgb_default.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			return list_xmgb_default;
		}
		return list_xmgb;
	}
	/**
	 * 得到报表列集合
	 * 
	 * @author gaof
	 * @param uid
	 * @param ss
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getListColumn(int bbbh, String uid,
			Session ss, List<Map<String, Object>> list_xmgb) {
		StringBuffer sql_column = new StringBuffer();
		sql_column
				.append("select GY_XMGB.GBXM,GY_XMGB.SXH from GY_XMGB"
						+ " where GY_XMGB.Bbbh = ").append(bbbh)
				.append(" and GY_XMGB.JGID = ").append(uid)
				.append(" group by GY_XMGB.GBXM,GY_XMGB.SXH order by SXH");
		List<Map<String, Object>> list_column = ss
				.createSQLQuery(sql_column.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		// 如果没有对应的，则使用默认规则
		if (list_column.size() == 0 || list_column == null) {
			StringBuffer sql_column_default = new StringBuffer();
			sql_column_default
					.append("SELECT SFXM AS GBXM, SFMC AS XMMC FROM GY_SFXM order by length(PLSX),PLSX");
			List<Map<String, Object>> list_column_default = ss
					.createSQLQuery(sql_column_default.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			return list_column_default;
		}
		// 把收费项目和归并名称的关系存到一个map中
		Map<BigDecimal, String> map_xmgb_relation = new HashMap<BigDecimal, String>();
		for (Map<String, Object> m_xmgb : list_xmgb) {
			map_xmgb_relation.put((BigDecimal) m_xmgb.get("SFXM"),
					(String) m_xmgb.get("XMMC"));
		}
		// 找到对应的在报表显示的项目名称
		for (Map<String, Object> m_column : list_column) {
			String xmmc = map_xmgb_relation.get(m_column.get("GBXM"));
			m_column.put("XMMC", xmmc);
		}
		return list_column;
	}
	/**
	 * 对象转换成double数据
	 * @param object
	 * @return
	 */
	public double Object2Double(Object object){
		if(object == null){
			return 0;
		}else{
			return Double.parseDouble(object + "");
		}
	}
}
