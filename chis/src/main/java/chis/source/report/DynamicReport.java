package chis.source.report;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ctd.dictionary.DictionaryController;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.web.context.WebApplicationContext;

import chis.source.gis.ReportSQLUtil;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryItem;
import ctd.util.context.Context;

public class DynamicReport {
//
//	private static final String DEFAULT_SCHEMAID = "MHC/District_region";
//
//	private static final String DEFAULT_SQL_DEFINE = "default";
//
//	private static final String BASE_SQL_DEFINE = "base";
//
//	@SuppressWarnings("rawtypes")
//	private Map<String, Map> hash_base = new HashMap<String, Map>();
//
//	private Document doc = null;
//
//	@SuppressWarnings("rawtypes")
//	private List subjectsPercent = null;
//
//	@SuppressWarnings("rawtypes")
//	private List subjectsSub = null;
//
//	private ReportSchema dschema = null;
//
//	private Map<String,Object> schemaJSON = null;
//
//	private Map<String,Object> jsonReq = null;
//
//	private Context context = null;
//
//	public DynamicReport(Context ctx, Map<String,Object> request){
//		context = ctx;
//		jsonReq = request;
//		init();
//	}
//
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public void init(){
//
//		String schemaId = StringUtils.trimToEmpty((String)jsonReq.get("entryName"));
//		if (schemaId == null || schemaId.length() == 0)
//			schemaId = DEFAULT_SCHEMAID;
////		if (jsonReq.optList("subjects").length() == 1) {
////			String tid = jsonReq.optList("subjects").optString(0);
////			dschema = ReportSchemaController.instance().getSchema(tid);
////		}
//		if (dschema != null) {
//			schemaJSON = dschema.json(context);
//		} else {
//			ReportSchema schema = ReportSchemaController.instance().getSchema(
//					schemaId);
//			schemaJSON = schema.json(context);
//			ArrayList items = new ArrayList();
//			ArrayList defineItems = (ArrayList) schemaJSON.get("items");
//			Map<String,Object> dics = (Map<String, Object>) jsonReq.get("dics");
//			String field = "";
//			for (int i = 0; i < defineItems.size(); i++) {
//				Map map = (Map)defineItems.get(i);
//				if (StringUtils.trimToEmpty((String)map.get("func")).length() == 0) {
//					items.add(map);
//					if (field.length() > 0) {
//						field += ",";
//					}
//					field += StringUtils.trimToEmpty((String)map.get("id"));
//
//				}
//			}
//			schemaJSON.put("field", field);
//			Iterator<String> it = dics.keySet().iterator();
//			for (; it.hasNext();) {
//				String key = (String) it.next();
//				if (key == null || key.length() == 0)
//					continue;
//				Map<String,Object> item = new HashMap<String,Object>();
//				item.put("id", key);
//				item.put("alias", dics.get(key));
//				if (ReportSQLUtil.isPercent(key)) {
//					item.put("renderer", "percentRender");
//				}
//				item.put("type", "double");
//				items.add(item);
//			}
//			schemaJSON.put("items", items);
//			initSQLMode();
//			initBaseSql(field);
//		}
//	}
//
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private void initSQLMode() {
//		ArrayList subjects = (ArrayList) jsonReq.get("subjects");
//		subjectsSub = new ArrayList();
//		subjectsPercent = new ArrayList();
//		doc = ReportSchemaController.instance().getConfigDoc("MHC/Report_SQL");
//		for (int i = 0; i < subjects.size(); i++) {
//			String item = (String) subjects.get(i);
//			if (ReportSQLUtil.isPercent(item)) {
//				subjectsPercent.add(item);
//				String subId = getDefineCode(item, "id", "sub");
//				if (subId != null)
//					subjectsSub.add(subId);
//			} else {
//				subjectsSub.add(item);
//			}
//		}
//	}
//
//	private String getDefineCode(String id, String prop, String attrName) {
//		Element ele = getDefine(id, prop);
//		if (ele == null)
//			return id;
//		return ele.attributeValue(attrName);
//	}
//
//	private Element getDefine(String id, String prop) {
//		Element ele = (Element) doc.selectSingleNode("//item[@" + prop + "='"
//				+ id + "']");
//		return ele;
//	}
//
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	private void initBaseSql(String field){
//		if (subjectsPercent.size() == 0)
//			return;
//		Session session = null;
//		try {
//			WebApplicationContext wac = (WebApplicationContext) context
//					.get("_applicationContext");
//			SessionFactory fac = (SessionFactory) wac
//					.getBean("mySessionFactory");
//			session = fac.openSession();
//			Map<String,Object> sqlList = new HashMap<String,Object>();
//			sqlList.put(DEFAULT_SQL_DEFINE, new ArrayList());
//			for (int i = 0; i < subjectsPercent.size(); i++) {
//				String id = (String) subjectsPercent.get(i);
//				Element ele = getDefine(id, "id");
//				if (ele == null)
//					continue;
//				String mode = ele.attributeValue("mode");
//				if (!sqlList.containsKey(mode))
//					sqlList.put(mode, new ArrayList());
//				if (mode.equals(DEFAULT_SQL_DEFINE)) {
//					((ArrayList)sqlList.get(mode)).add(ele.attributeValue("sub"));
//					((ArrayList)sqlList.get(mode)).add(ele.attributeValue("base"));
//				} else if (mode.equals(BASE_SQL_DEFINE)) {
//					((ArrayList)sqlList.get(DEFAULT_SQL_DEFINE)).add(
//							ele.attributeValue("sub"));
//					((ArrayList)sqlList.get(mode)).add(ele.attributeValue("base"));
//				}
//			}
//			Iterator<String> it = sqlList.keySet().iterator();
//			for (; it.hasNext();) {
//				String key = it.next();
//				String sql = null;
//				if (((ArrayList)sqlList.get(key)).size() == 0)
//					continue;
//				if (key.equals(DEFAULT_SQL_DEFINE)) {
//					sql = ReportSQLUtil.getDefaultSQL(
//							(ArrayList)sqlList.get(key), jsonReq, field);
//				} else if (key.equals(BASE_SQL_DEFINE)) {
//					sql = ReportSQLUtil.getBaseSQL((ArrayList)sqlList.get(key),
//							jsonReq, field);
//				}
//				Query q = session.createQuery(sql);
//				Map<String, Map<String,Object>> hash = new HashMap<String, Map<String,Object>>();
//				Map<String, Double> hash_sum = new HashMap<String, Double>();
//				String[] fs = field.split(",");
//				List<Object[]> list = q.list();
//				for (Object[] r : list) {
//					Map<String,Object> o = null;
//					String pkey = "";
//					for (int j = 0; j < fs.length; j++) {
//						pkey += r[j + 1];
//					}
//					if (hash.containsKey(pkey)) {
//						o = hash.get(pkey);
//					} else {
//						o = new HashMap<String,Object>();
//						for (int i = 0; i < fs.length; i++) {
//							o.put(fs[i], r[i + 1]);
//							o.put(fs[i] + "_text",
//									getDisplayValue(fs[i],
//											String.valueOf(r[i + 1])));
//						}
//						ArrayList lt = (ArrayList)sqlList.get(key);
//						for (int i = 0; i < lt.size(); i++) {
//							o.put((String) lt.get(i), 0.0);
//						}
//						hash.put(pkey, o);
//					}
//
//					if (!hash_sum.containsKey(String.valueOf(r[0]))) {
//						hash_sum.put(String.valueOf(r[0]),
//								(Double) r[fs.length + 1]);
//					} else {
//						hash_sum.put(String.valueOf(r[0]),
//								hash_sum.get(String.valueOf(r[0]))
//										+ (Double) r[fs.length + 1]);
//					}
//
//					o.put(String.valueOf(r[0]), r[fs.length + 1]);
//				}
//
//				Map<String,Object> total = new HashMap<String,Object>();
//				total.put(fs[0], "sum");
//				total.put(fs[0] + "_text", "合计");
//				Iterator<String> unitKeys = hash_sum.keySet().iterator();
//				for (; unitKeys.hasNext();) {
//					String ukey = unitKeys.next();
//					total.put(ukey, hash_sum.get(ukey));
//				}
//				hash.put("sum", total);
//				hash_base.put(key, hash);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (session != null && session.isOpen())
//				session.close();
//		}
//	}
//
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private List executeSql(String hql, String field){
//		Session session = null;
//		List body = null;
//		try {
//			WebApplicationContext wac = (WebApplicationContext) context
//					.get("_applicationContext");
//			SessionFactory fac = (SessionFactory) wac
//					.getBean("mySessionFactory");
//			session = fac.openSession();
//			Query q = session.createQuery(hql);
//			Map<String, Map<String,Object>> hash = new HashMap<String, Map<String,Object>>();
//			Map<String, Double> hash_sum = new HashMap<String, Double>();
//			ArrayList subjects = (ArrayList)jsonReq.get("subjects");
//			String[] fs = field.split(",");
//			List<Object[]> list = q.list();
//			body = new ArrayList();
//			for (Object[] r : list) {
//				Map<String,Object> o = null;
//				String pkey = "";
//				String kpicode = String.valueOf(r[0]);
//				for (int j = 0; j < fs.length; j++) {
//					pkey += r[j + 1];
//				}
//				if (hash.containsKey(pkey)) {
//					o = hash.get(pkey);
//				} else {
//					o = new HashMap<String,Object>();
//					for (int i = 0; i < fs.length; i++) {
//						o.put(fs[i], r[i + 1]);
//						o.put(fs[i] + "_text",
//								getDisplayValue(fs[i], String.valueOf(r[i + 1])));
//					}
//					for (int i = 0; i < subjects.size(); i++) {
//						o.put((String) subjects.get(i), 0.0);
//					}
//					hash.put(pkey, o);
//					body.add(o);
//				}
//
//				if (!hash_sum.containsKey(kpicode)) {
//					hash_sum.put(kpicode, (Double) r[fs.length + 1]);
//				} else {
//					hash_sum.put(kpicode, hash_sum.get(kpicode)
//							+ (Double) r[fs.length + 1]);
//				}
//
//				o.put(kpicode, r[fs.length + 1]);
//			}
//
//			Map<String,Object> total = new HashMap<String,Object>();
//			total.put(fs[0], "sum");
//			total.put(fs[0] + "_text", "合计");
//			Iterator<String> unitKeys = hash_sum.keySet().iterator();
//			for (; unitKeys.hasNext();) {
//				String key = unitKeys.next();
//				total.put(key, doubleFormat(hash_sum.get(key)));
//			}
//			body.add(total);
//			hash.put("sum", total);
//
//			if (subjectsPercent.size() > 0) {
//				Iterator<String> allkeys = hash.keySet().iterator();
//				for (; allkeys.hasNext();) {
//					String k = allkeys.next();
//					for (int j = 0; j < subjectsPercent.size(); j++) {
//						Element ele = this.getDefine(
//								(String)subjectsPercent.get(j), "id");
//						if (ele == null
//								|| hash_base.get(DEFAULT_SQL_DEFINE) == null
//								|| hash_base.get(ele.attributeValue("mode")) == null) {
//							continue;
//						}
//						Map<String,Object> subData = (Map<String,Object>) hash_base.get(
//								DEFAULT_SQL_DEFINE).get(k);
//						Map<String,Object> baseData = (Map<String,Object>) hash_base.get(
//								ele.attributeValue("mode")).get(k);
//						if (subData == null || baseData == null)
//							continue;
//						Double subNumber = (Double) subData.get(ele
//								.attributeValue("sub"));
//						Double baseNumber = (Double) baseData.get(ele
//								.attributeValue("base"));
//						Double percentNumber = 0.0;
//						if (subNumber > 0 && baseNumber > 0) {
//							percentNumber = doubleFormat(subNumber / baseNumber
//									* 100);
//						}
//						((Map<String,Object>) hash.get(k)).put(
//								(String)subjectsPercent.get(j), percentNumber);
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (session != null && session.isOpen())
//				session.close();
//		}
//
//		return body;
//	}
//
//	public Double doubleFormat(double d) {
//		DecimalFormat df = new DecimalFormat("0.## ");
//		return Double.parseDouble(df.format(d));
//	}
//
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public List getData(){
//		List body = new ArrayList();
//		if (dschema != null) {
//			List<HashMap> data = dschema.run(context);
//			for (Map r : data) {
//				String kpicode = (String) r.get("KPICode");
//				if (kpicode != null && kpicode.indexOf("_") > -1) {
//					kpicode = kpicode.split("_")[1];
//					r.put("KPICode", kpicode);
//					if (r.containsKey("KPICode_text")) {
//						r.put("KPICode_text", getDisplayValue("icd10", kpicode));
//					}
//				}
//				body.add(r);
//			}
//		} else {
//			String field = StringUtils.trimToEmpty((String)this.getSchema().get("field"));
//			String sql = ReportSQLUtil.getDefaultSQL(subjectsSub, jsonReq,
//					field);
//			body = executeSql(sql, field);
//		}
//		return body;
//	}
//
//	private String getDisplayValue(String id, String key) {
//		Dictionary d = DictionaryController.instance().getDic(id);
//		if (d == null)
//			return key;
//		String text = "";
//		key = key.trim();
//		if (key.indexOf(",") == -1) {
//			text = d.getText(key);
//			DictionaryItem di = d.getItem(key);
//			if (di == null) {
//				return text;
//			}
//		} else {
//			String[] keys = key.split(",");
//			StringBuffer sb = new StringBuffer();
//			for (String s : keys) {
//				sb.append(",").append(d.getText(s));
//			}
//			text = sb.substring(1);
//		}
//		return text;
//	}
//
//	public Map<String,Object> getSchema() {
//		return schemaJSON;
//	}
//
//	public ReportSchema getReportSchema() {
//		return this.dschema;
//	}

}
