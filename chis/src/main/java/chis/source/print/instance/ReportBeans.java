package chis.source.print.instance;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import chis.source.gis.LayerDic;
import chis.source.gis.ServiceUtil;
import chis.source.print.base.PrintImpl;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class ReportBeans extends PrintImpl {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map<String, Object>> getJrxml(String jrxml, Context ctx,
			String code, String codeFlag, String date) {
		List<Map<String, Object>> beans = new ArrayList();
		try {
			String appHome = this.getClass().getClassLoader().getResource("")
					.getPath();
			if (appHome != null) {
				try {
					appHome = URLDecoder.decode(appHome, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			String listHql = "";
			List codeList;
			if (codeFlag.equals("regionCode")) {
				String relen = String.valueOf(LayerDic.layerMapping.get(String
						.valueOf(code.length())));
				listHql = "from EHR_AreaGridChild a where length(a.regionCode)="
						+ relen
						+ " and a.regionCode like '"
						+ code
						+ "%' order by a.regionCode";
				codeList = ServiceUtil.executeSql(listHql, ctx);
			} else if (codeFlag.equals("manageUnit")) {
				// String relen =
				// String.valueOf(LayerDic.getManaUnitNextLayerLength(code.length()));
				codeList = DictionaryController.instance()
						.get("chis.@manageUni").getSlice(code, 3, "");
				if (code.length() > 6) {
					DictionaryItem d = new DictionaryItem(code,
							DictionaryController.instance()
									.get("chis.@manageUnit").getText(code));
					codeList.add(d);
				}
			} else {
				// String relen =
				// String.valueOf(LayerDic.getManaUnitNextLayerLength(code.length()));
				String usql = "select distinct(b.logonName) from SystemUsersProfile b where substring (b.manaUnitId,1,"
						+ code.length() + ")='" + code + "'";
				codeList = ServiceUtil.executeSql(usql, ctx);
			}

			LinkedHashMap<String, List> rows = new LinkedHashMap<String, List>();
			for (int x = 0; x < codeList.size(); x++) {
				List xc = new ArrayList();
				if (codeFlag.equals("regionCode")) {
					HashMap rcode = (HashMap) codeList.get(x);
					rows.put((String) rcode.get("regionCode"), xc);
				} else if (codeFlag.equals("manageUnit")) {
					DictionaryItem rcode = (DictionaryItem) codeList.get(x);
					rows.put(rcode.getKey(), xc);
				} else if (codeFlag.equals("user04")) {
					String rcode = (String) codeList.get(x);
					rows.put(rcode, xc);
				}
			}
			// DecimalFormat df = new DecimalFormat("0.00%");
			String path = appHome + "/config/report/ehrMb/" + jrxml + ".xml";
			// String regDic = String.valueOf(LayerDic.layerMapping.get(String
			// .valueOf(code.length())));
			if (ServiceUtil.read(path) == null) {
				return beans;
			}
			Document xmlDoc = ServiceUtil.read(path);
			List sqls = xmlDoc.selectNodes("//sql");
			HashMap<String, String> valueType = new HashMap<String, String>();
			HashMap<String, String> dics = new HashMap<String, String>();
			for (int i = 0; i < sqls.size(); i++) {
				// int funcVaule = 0;
				Map<String, Object> req = null;
				Element sqlItem = (Element) sqls.get(i);
				String hql = createSql(code, sqlItem, codeFlag, date);
				String key = sqlItem.attributeValue("key");
				// String perFlag = sqlItem.attributeValue("per");
				valueType.put(key, sqlItem.attributeValue("valueType"));
				dics.put(key, sqlItem.attributeValue("dic"));
				if (hql != "") {
					if (hql == "mdic") {
						List reqlist = DictionaryController.instance()
								.get("chis.@manageUnit").getSlice(code, 3, "");
						if (code.length() > 6) {
							DictionaryItem d = new DictionaryItem(code,
									DictionaryController.instance()
											.get("chis.@manageUnit")
											.getText(code));
							reqlist.add(d);
						}

						req = new HashMap<String, Object>();
						for (int x = 0; x < reqlist.size(); x++) {
							DictionaryItem rcode = (DictionaryItem) codeList
									.get(x);
							req.put(rcode.getKey(), DictionaryController
									.instance().get("chis.@manageUnit")
									.getText(rcode.getKey()));
						}
					} else if (hql == "udic") {
						req = new HashMap<String, Object>();
						for (int x = 0; x < codeList.size(); x++) {
							String rcode = (String) codeList.get(x);
							req.put(rcode, rcode);

						}
					} else {
						req = ServiceUtil.executeSqlObj(hql, ctx);
					}
				}
				Iterator itRs = rows.keySet().iterator();
				for (; itRs.hasNext();) {
					String row = (String) itRs.next();
					List rowBean = rows.get(row);

					HashMap<String, String> value = new HashMap<String, String>();
					if (req != null && req.containsKey(row)) {
						value.put(key, (String) req.get(row));
					} else {
						value.put(key, "0");
					}
					rowBean.add(value);
				}

			}
			Iterator itRs = rows.keySet().iterator();
			for (; itRs.hasNext();) {
				HashMap map = new HashMap();
				String row = (String) itRs.next();
				List rowBean = rows.get(row);
				for (int z = 0; z < rowBean.size(); z++) {
					HashMap<String, String> rs = (HashMap<String, String>) rowBean
							.get(z);
					String key = rs.keySet().iterator().next();
					if (valueType.get(key) != null
							&& !valueType.get(key).equals("String")) {
						String n = rs.get(key);
						if (n.indexOf(".") >= 0) {
							n = n.substring(0, n.indexOf("."));
						}
						int k = Integer.parseInt(n);
						map.put(key, k);
					} else {
						if ("add".equals(key) && "user04".equals(codeFlag)) {
							Dictionary d = DictionaryController.instance().get(
									"chis.dictionary.user");
							String u = d.getText(rs.get(key));
							map.put(key, u);
						} else {
							map.put(key, rs.get(key));
						}

					}
				}
				List funicton = xmlDoc.selectNodes("//funicton");
				for (int i = 0; i < funicton.size(); i++) {
					Element funcItem = (Element) funicton.get(i);
					map = (HashMap) createFunc(funcItem, map);
				}
				beans.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return beans;
	}

	public Map<String, Object> createFunc(Element funcItem,
			Map<String, Object> map) {
		if (!"".equals(funcItem.attributeValue("sum"))
				&& funcItem.attributeValue("sum") != null) {
			String[] funcs = funcItem.attributeValue("sum").split(",");
			int x = 0;
			for (int i = 0; i < funcs.length; i++) {
				String fun = funcs[i];
				x += Integer.parseInt(map.get(fun).toString());
			}
			String key = funcItem.attributeValue("key");
			map.put(key, x);
		}
		if (!"".equals(funcItem.attributeValue("per"))
				&& funcItem.attributeValue("per") != null) {
			String[] funcs = funcItem.attributeValue("per").split(",");
			int p1 = Integer.parseInt(map.get(funcs[0]).toString());
			int p2 = Integer.parseInt(map.get(funcs[1]).toString());
			String p = "";
			if (p2 == 0) {
				p = "0%";
			} else {
				int per = p1 / p2 * 100;
				p = per + "%";
			}

			String key = funcItem.attributeValue("key");
			map.put(key, p);
		}
		return map;
	}

	public String createSql(String code, Element sqlItem, String codeFlag,
			String date) {
		String hql = "select ";
		String groupby = "";
		if (!"".equals(sqlItem.attributeValue("select"))
				&& sqlItem.attributeValue("select") != null) {
			String[] selects = sqlItem.attributeValue("select").split(",");
			for (int j = 0; j < selects.length; j++) {
				if (selects[j].equals("mdic")) {
					return hql = "mdic";
				} else if (selects[j].equals("udic")) {
					return hql = "udic";
				} else if (selects[j].equals("mudic")) {
					return hql = "mudic";
				}
				if (selects[j].equals("dynamicCodeNum")) {
					String codeNum = LayerDic.layerDic.get(String
							.valueOf(LayerDic.layerMapping.get(String
									.valueOf(code.length()))));
					hql += "a." + codeNum + ",";
					groupby = "a." + codeNum;
				} else if (selects[j].equals("dynamicCode")) {
					String codeSub = "substring (a.regionCode,1,"
							+ String.valueOf(LayerDic.layerMapping.get(String
									.valueOf(code.length()))) + ")";
					hql += codeSub + ",";
					groupby = codeSub;
				} else if (selects[j].equals("dynamicCodeManaUnit")) {
					String codeSub = "substring (a.manaUnit,1,"
							+ String.valueOf(LayerDic
									.getManaUnitNextLayerLength(code.length()))
							+ ")";
					hql += codeSub + ",";
					groupby = codeSub;
				} else if (selects[j].equals("dynamicCodemanaUnitId")) {
					String codeSub = "substring (a.manageUnit,1,"
							+ String.valueOf(LayerDic
									.getManaUnitNextLayerLength(code.length()))
							+ ")";
					hql += codeSub + ",";
					groupby = codeSub;
				} else if (!selects[j].equals("dynamicCodeNum")
						&& !selects[j].equals("dynamicCode")) {
					hql += selects[j];
					if (j < selects.length - 1) {
						hql += ",";
					}
				}
			}
		} else {
			return hql = "";
		}
		if (!"".equals(sqlItem.attributeValue("from"))) {
			hql += " from " + sqlItem.attributeValue("from");
		}
		if (!"".equals(sqlItem.attributeValue("where"))
				&& sqlItem.attributeValue("where") != null) {
			String where[] = sqlItem.attributeValue("where").split(";");
			if (!"".equals(code)) {
				if (codeFlag.equals("regionCode")) {
					hql += " where a.regionCode like '" + code + "%' and ";
				} else if ("user04".equals(codeFlag)) {
					hql += " where ";
				} else {
					hql += " where a.manageUnit like '" + code + "%' and ";
				}
			}
			for (int k = 0; k < where.length; k++) {
				String dynamicLength = "";
				if ("dynamicCodeLength".equals(where[k])) {
					dynamicLength = "length(a.regionCode)="
							+ LayerDic.layerMapping.get(String.valueOf(code
									.length()));
					hql += dynamicLength;
				} else if ("dynamicManaUnitLength".equals(where[k])) {
					dynamicLength = "length(a.manageUnit)="
							+ LayerDic
									.getManaUnitNextLayerLength(code.length());
					hql += dynamicLength;
				} else {
					if (!"".equals(sqlItem.attributeValue("GLFlag"))
							&& sqlItem.attributeValue("GLFlag") != null) {
						String[] d = date.split("and");
						String d1 = " and " + d[1];
						if ("=".equals(sqlItem.attributeValue("GLFlag"))) {
							d1 = " and " + d[1].replace("<=", "=");
						}
						date = d1;
					}
					hql += where[k];
				}
			}
			hql += " " + date;
		}

		if (sqlItem.attributeValue("group") != null
				&& !"".equals(sqlItem.attributeValue("group"))) {
			if ("dynamicCodeNum".equals(sqlItem.attributeValue("group"))
					|| "dynamicCodeManaUnit".equals(sqlItem
							.attributeValue("group"))
					|| "dynamicCode".equals(sqlItem.attributeValue("group"))
					|| "dynamicCodemanaUnitId".equals(sqlItem
							.attributeValue("group"))) {
				hql += " group by " + groupby;
			} else {
				hql += " group by " + sqlItem.attributeValue("group");
			}

		}

		if (sqlItem.attributeValue("orderby") != null
				&& !"".equals(sqlItem.attributeValue("orderby"))) {
			hql += " order by " + sqlItem.attributeValue("orderby");
		}
		return hql;
	}

	public void getFields(Map<String, Object> requestData,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		String jrxml = (String) requestData.get("jrxml");
		String code = "";
		String codeFlag = "";
		String date = "";
		if (requestData.containsKey("areaGrid")) {
			codeFlag = "regionCode";
			code = (String) requestData.get("areaGrid");
		}
		if (requestData.containsKey("manageUnit")) {
			codeFlag = "manageUnit";
			code = (String) requestData.get("manageUnit");
		}
		if (requestData.containsKey("user04")) {
			codeFlag = "user04";
			code = (String) requestData.get("user04");
		}
		if (requestData.containsKey("beginDate")
				&& requestData.containsKey("endDate")) {
			String bd[] = requestData.get("beginDate").toString().split("-");
			String byear = bd[0];
			String bmm = bd[1];
			String ed[] = requestData.get("endDate").toString().split("-");
			String eyear = ed[0];
			String emm = ed[1];
			date = " and (year<='" + eyear + "' and month<='" + emm
					+ "') and (year>='" + byear + "' and month>='" + bmm
					+ "') ";
		} else if (requestData.containsKey("beginDateDD")
				&& requestData.containsKey("endDateDD")) {
			date = " and str(a.statDate,'yyyy-mm-dd')<='"
					+ requestData.get("endDateDD")
					+ "' and str(a.statDate,'yyyy-mm-dd')>='"
					+ requestData.get("beginDateDD") + "'";
		} else {
			date = "";
		}
		if (requestData.containsKey("sex")) {
			String v = (String) requestData.get("sex");
			if (!v.equals("0")) {
				jrxml = jrxml + v;
			}
		}
		// if ("GZLEhr".equals(jrxml) && code.length() < 7) {
		//
		// }
		records = getJrxml(jrxml, ctx, code, codeFlag, date);
	}

	public void getParameters(Map<String, Object> requestData,
			Map<String, Object> response, Context ctx) throws PrintException {
		try {
			java.util.Date date1 = new java.util.Date();
			if (requestData.containsKey("areaGrid")) {
				String regionCode = (String) requestData.get("areaGrid");
				response.put(
						"Title",
						DictionaryController.instance()
								.get("chis.dictionary.areaGrid")
								.getText(regionCode));
			} else if (requestData.containsKey("manageUnit")) {
				String manageUnit = (String) requestData.get("manageUnit");
				response.put("Title",
						DictionaryController.instance().get("chis.@manageUnit")
								.getText(manageUnit));
			}
			DateFormat d = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
			String timeStr2 = d.format(date1);
			response.put("date", timeStr2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
