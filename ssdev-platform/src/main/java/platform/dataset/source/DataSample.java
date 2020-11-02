package platform.dataset.source;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.dao.QueryResult;
import ctd.dao.SimpleDAO;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.util.JSONUtils;
import ctd.util.S;
import ctd.util.context.Context;
import ctd.util.xml.XMLHelper;

public class DataSample {
	private Dictionary groupDic = DictionaryController.instance().getDic("platform.dataset.dic.resDataGroup");
	private static final Logger LOGGER = LoggerFactory.getLogger(DataSample.class);

	public List getQuertCnd(String key, String value) throws Exception {
		String exp = "['eq',['$','" + key + "'],['i'," + value + "]]";
		List queryCnd = JSONUtils.parse(exp, List.class);
		return queryCnd;
	}

	public String parseName(String str) {
		str = str.replaceAll(" ", "");
		if (str.startsWith("G")) {
			str = str.split("-")[0].replaceAll("\\/", "\\.");
		} else if (str.startsWith("W")) {
			if (str.contains("CV"))
				str = str.substring(str.indexOf("C"), str.length());
		}
		return str;
	}

	// 返回最顶层组的名字和强制性标识符
	private String[] getTopGroup(String groupId) {
		String parentKey = S.obj2String(groupDic.getItem(groupId).getProperty("ParentGroupId"));
		if (StringUtils.isEmpty(parentKey)) {
			String[] arr = new String[2];
			String name = S.obj2String(groupDic.getItem(groupId).getProperty("GroupIdentify"));
			String frequencyNumber = S.obj2String(groupDic.getItem(groupId).getProperty("FrequencyNumber"));
			String forceIdentify = S.obj2String(groupDic.getItem(groupId).getProperty("ForceIdentify"));
			if (frequencyNumber.equals("2")) {
				name = name + "s";
			}
			arr[0] = name;
			arr[1] = forceIdentify;
			return arr;
		} else {
			return getTopGroup(parentKey);
		}
	}

	private List<String> getGroupNames(List<String> groups, String groupId) {
		String groupname = S.obj2String(groupDic.getItem(groupId).getProperty("GroupIdentify"));
		String frequencyNumber = S.obj2String(groupDic.getItem(groupId).getProperty("FrequencyNumber"));
		if (StringUtils.isEmpty(frequencyNumber) || frequencyNumber.equals("1")) {
			groups.add(0, groupname);
		} else if (frequencyNumber.equals("2")) {
			groups.add(0, groupname);
			groups.add(0, groupname + "s");
		}
		String parentKey = S.obj2String(groupDic.getItem(groupId).getProperty("ParentGroupId"));
		if (StringUtils.isEmpty(parentKey)) {
			return groups;
		} else {
			return getGroupNames(groups, parentKey);
		}
	}

	public void addElement(Element el, Map<String, Object> map) {
		Element e = el.addElement(String.valueOf(map.get("CustomIdentify"))).addAttribute("de",
				String.valueOf(map.get("StandardIdentify")));
		if (String.valueOf(map.get("CustomIdentify")).equals("null")) {
			LOGGER.warn("CustomIdentify is null");
		}
		String sampleValue = S.obj2String(map.get("SampleValue"));
		if (StringUtils.isEmpty(sampleValue)) {
			sampleValue = "";
		}
		e.addText(sampleValue);
		if (map.get("bCodeSystem") != null && !String.valueOf(map.get("CodeSystem")).equals("1")) {
			String dicName = parseName(String.valueOf(map.get("bCodeSystem")));
			String dataStandardId=S.obj2String(map.get("DataStandardId"));
			if(!StringUtils.isEmpty(dataStandardId)){
				dicName="platform.dataset.dic.res."+dataStandardId+"."+dicName;
			}
			Dictionary dic = DictionaryController.instance().getDic(dicName);
			String text;
			if (dic == null) {
				return;
			}
			if (sampleValue.equals("")) {
				text = "";
			} else {
				text = dic.getText(sampleValue);
			}
			e.addAttribute("display", text);

		}
	}

	public QueryResult find(String enrtyName, List queryCnd, Context ctx) throws Exception {
		Schema schema = SchemaController.instance().getSchema(enrtyName);
		SimpleDAO dao = new SimpleDAO(schema, ctx);
		QueryResult qr = dao.find(queryCnd);
		return qr;
	}

	public Document getDocument(String dataSetId, String name, String customIdentify, String standardIdentify,
			Context ctx) throws Exception {
		List queryCnd = getQuertCnd("DataSetId", dataSetId);
		List<Map<String, Object>> list = find("platform.dataset.schemas.RES_DataSetContent", queryCnd, ctx).getRecords();
		Document doc = XMLHelper.createDocument();
		Element root = doc.addElement(customIdentify);
		root.addAttribute("Name", name);
		for (Map<String, Object> map : list) {
			String groupId = S.obj2String(map.get("DataGroup"));
			if (!StringUtils.isEmpty(groupId)) {
				String groupname = S.obj2String(groupDic.getItem(groupId).getProperty("GroupIdentify"));
				if (groupname.equals("")) {
					LOGGER.error("could not find DataGroup:" + S.obj2String(map.get("DataGroup")));
				} else {
					Element groupNode = (Element) root.selectSingleNode("//" + groupname);
					if (groupNode == null) {
						List<String> groupNames = new ArrayList<String>();
						groupNames = getGroupNames(groupNames, groupId);
						Element g = root;
						for (int i = 0; i < groupNames.size(); i++) {
							Element e = (Element) root.selectSingleNode("//" + groupNames.get(i));
							if (e == null) {
								g = g.addElement(groupNames.get(i));
							} else {
								g = e;
								continue;
							}
						}
						groupNode = g;
					}
					addElement(groupNode, map);
				}
				continue;
			}
			addElement(root, map);
		}
		return doc;
	}
}
