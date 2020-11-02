/**
 * @(#)DemographicController.java Created on 2013-6-26 上午9:08:27
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.controller;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import chis.source.BSCHISEntryNames;
import ctd.mvc.controller.JSONOutputMVCConroller;
import ctd.util.AppContextHolder;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
@Controller("demographicController")
public class DemographicController extends JSONOutputMVCConroller {

	private static final Logger logger = LoggerFactory
			.getLogger(DemographicController.class);

	private static final String chartStart = "<?xml version='1.0' encoding='UTF-8'?><chart xAxisMinValue='0' xAxisMaxValue='100' yAxisMinValue='0' yAxisMaxValue='100' is3D='1' numDivLines='0' showFormBtn='0'	baseFontSize='14' enableLink='1'><trendlines><line startValue='20' color='ff0000' thickness='2' displayValue='孙子辈' showOnTop='0'/></trendlines><trendlines><line startValue='40' color='ff0000' thickness='2' displayValue='子辈' showOnTop='0' /></trendlines><trendlines><line startValue='60' color='ff0000' thickness='2' displayValue='父辈' showOnTop='0' /></trendlines><trendlines><line startValue='80' color='ff0000' thickness='2' displayValue='祖辈' showOnTop='0' /></trendlines>";

	private static final String chartEnd = "<styles><definition><style name='fontsize' type='font' size='12' /></definition><application><apply toObject='DATALABELS' styles='fontsize' /><apply toObject='TOOLTIP' styles='myHTMLFont' /></application></styles></chart>";

	public static final String LineColor = "988c85";

	public static final String NAME = "name";

	public static final int grandsonGap = 6;

	private ArrayList<HashMap<String, Object>> jsonArr = null;

	private String imagePath = "";

	private String chartId = "";

	private String healthyDes = "";

	@RequestMapping(value = "*.graphic", method = RequestMethod.GET)
	public void doDemographic(HttpServletRequest request,
			HttpServletResponse response) {
		Session ss = null;
		try {
			SessionFactory sf = AppContextHolder.getBean(
					AppContextHolder.DEFAULT_SESSION_FACTORY,
					SessionFactory.class);
			ss = sf.openSession();
			jsonArr = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> query = parseQueryString(request
					.getQueryString());
			imagePath = query.get("path").toString();
			chartId = query.get("chartId").toString();
			executeFindAll(query.get("empiId").toString(), ss);

			HashMap<String, Object> jsonRes = new HashMap<String, Object>();
			jsonRes.put("body", createFlashString(jsonArr));
			writeToResponse(response, jsonRes);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			if (ss != null && ss.isOpen()) {
				ss.close();
			}
		}
	}

	private HashMap<String, Object> parseQueryString(String q) {
		HashMap<String, Object> query = new HashMap<String, Object>();
		query.put("chartId", "");
		q = q.substring(2);
		if (q != null) {
			int p = q.indexOf("@");
			if (p > -1) {
				String[] qus = q.split("@");
				int size = qus.length;
				for (int i = 0; i < size; i++) {
					String[] kv = qus[i].split(";");
					if (kv.length == 2) {
						try {
							String key = URLDecoder.decode(kv[0], "utf-8");
							String value = URLDecoder.decode(kv[1], "utf-8");
							query.put(key, value);
						} catch (UnsupportedEncodingException e) {
							logger.error("parseQueryStringFailed:"
									+ e.getMessage());
						}
					}
				}
			} else {
				String[] kv = q.split(";");
				if (kv.length == 2) {
					try {
						String key = URLDecoder.decode(kv[0], "utf-8");
						String value = URLDecoder.decode(kv[1], "utf-8");
						query.put(key, value);
					} catch (UnsupportedEncodingException e) {
						logger.error("parseQueryStringFailed:" + e.getMessage());
					}
				}
			}
		}
		return query;
	}

	private List<?> executeSql(String hql, Session ss) {
		Query q = ss.createQuery(hql);
		List<?> records = q.list();
		return records;
	}

	private String getValue(Object obj) {
		if (obj == null) {
			return "";
		} else {
			return obj.toString();
		}
	}

	@SuppressWarnings({ "rawtypes" })
	private void executeFindAll(String empiid, Session ss) {
		String hql = "from " + BSCHISEntryNames.EHR_HealthRecord
				+ " where empiid='" + empiid + "'";
		List records = executeSql(hql, ss);
		int rowCount = records.size();
		if (rowCount == 0) {
			logger.info("No person find with empiid=[" + empiid + "]");
		} else {
			Object object = (Object) records.get(0);
			HashMap map = (HashMap) object;
			String fatherEmpiId = getValue(map.get("fatherId"));
			String wifeEmpiId = getValue(map.get("partnerId"));
			String motherEmpiId = getValue(map.get("motherId"));
			jsonArr.add(getBasicInfo("me", empiid, ss));
			jsonArr.add(getBasicInfo("father", fatherEmpiId, ss));
			executeFindGrandParent(fatherEmpiId, "0", ss);
			jsonArr.add(getBasicInfo("mother", motherEmpiId, ss));
			executeFindGrandParent(motherEmpiId, "1", ss);
			jsonArr.add(getBasicInfo("partner", wifeEmpiId, ss));
			executeFindChild(ss);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void executeFindChild(Session ss) {
		HashMap<String, Object> me = (HashMap<String, Object>) jsonArr.get(0);
		String type = (String) me.get("type");
		String hql = "";
		me.put("childnumber", "0");
		if (type.startsWith("M")) {
			hql = "from " + BSCHISEntryNames.EHR_HealthRecord
					+ " where fatherid='" + me.get("empiid") + "'";
		} else if (type.startsWith("F")) {
			hql = "from " + BSCHISEntryNames.EHR_HealthRecord
					+ " where motherid='" + me.get("empiid") + "'";
		}
		List childRecords = executeSql(hql, ss);
		int childCount = childRecords.size();
		if (childCount != 0) {
			me.put("childnumber", "" + childCount);
			HashMap<String, Object> childAll = new HashMap<String, Object>();
			ArrayList childAllArrayList = new ArrayList();
			childAll.put("id", "child");
			for (int i = 0; i < childCount; i++) {
				Object childObject = (Object) childRecords.get(i);
				HashMap childMap = (HashMap) childObject;
				String childEmpiid = getValue(childMap.get("empiId"));
				HashMap<String, Object> child = getBasicInfo("child" + (i + 1),
						childEmpiid, ss);
				childAllArrayList.add(child);
				String childType = (String) child.get("type");
				String childHql = "";
				if (childType.startsWith("M")) {
					childHql = "from " + BSCHISEntryNames.EHR_HealthRecord
							+ " where fatherid='" + childEmpiid + "'";
				} else if (childType.startsWith("F")) {
					childHql = "from " + BSCHISEntryNames.EHR_HealthRecord
							+ " where motherid='" + childEmpiid + "'";
				} else {
					break;
				}
				List grandRecords = executeSql(childHql, ss);
				int grandCount = grandRecords.size();
				if (grandCount != 0) {
					ArrayList grandAllArrayList = new ArrayList();
					child.put("child", grandAllArrayList);
					for (int j = 0; j < grandCount; j++) {
						Object grandObject = (Object) grandRecords.get(j);
						HashMap grandMap = (HashMap) grandObject;
						String grandEmpiid = getValue(grandMap.get("empiId"));
						HashMap<String, Object> grandChild = getBasicInfo(
								"child" + (i + 1) + "grandchild" + (j + 1),
								grandEmpiid, ss);
						grandAllArrayList.add(grandChild);
					}
				}
			}
			childAll.put("child", childAllArrayList);
			jsonArr.add(childAll);
		}
	}

	@SuppressWarnings({ "rawtypes"})
	private void executeFindGrandParent(String empiid, String type, Session ss) {
		String fName = "";
		String mName = "";
		if (type.equals("0")) {
			mName = "granddad";
			fName = "grandma";
		} else {
			mName = "grandfather";
			fName = "grandmother";
		}
		String hql = "from " + BSCHISEntryNames.EHR_HealthRecord
				+ " where empiid='" + empiid + "'";
		List records = executeSql(hql, ss);
		int rowCount = records.size();
		if (rowCount == 0) {
			jsonArr.add(getBasicInfo(mName, "", ss));
			jsonArr.add(getBasicInfo(fName, "", ss));
		} else {
			Object object = (Object) records.get(0);
			HashMap map = (HashMap) object;
			String fatherEmpiId = getValue(map.get("fatherId"));
			String motherEmpiId = getValue(map.get("motherId"));
			if (type.equals("0")) {
				jsonArr.add(getBasicInfo(mName, fatherEmpiId, ss));
				jsonArr.add(getBasicInfo(fName, motherEmpiId, ss));
			} else {
				jsonArr.add(getBasicInfo(mName, fatherEmpiId, ss));
				jsonArr.add(getBasicInfo(fName, motherEmpiId, ss));
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private HashMap<String, Object> getBasicInfo(String id, String empiId,
			Session ss) {
		String hql = "from " + BSCHISEntryNames.MPI_DemographicInfo
				+ " where empiId='" + empiId + "'";
		List records = executeSql(hql, ss);
		int rowCount = records.size();
		String sex = "";
		String name = "";
		String type = "";
		HashMap<String, Object> json = new HashMap<String, Object>();
		json.put("id", id);
		json.put("empiid", empiId);
		if (rowCount != 0) {
			Object object = (Object) records.get(0);
			HashMap map = (HashMap) object;
			sex = getValue(map.get("sexCode"));
			if (sex.equals("1")) {
				type = getType("M", empiId, ss);
			} else {
				type = getType("F", empiId, ss);
			}
			name = getValue(map.get("personName"));
			json.put("name", name);
			json.put("type", type);
			json.put("path", imagePath + type);
			json.put("healthyDes", healthyDes);
		} else {
			json.put("name", "未注册");
			json.put("type", "0.png");
			json.put("path", imagePath + "0.png");
			json.put("healthyDes", "未知");
		}
		return json;
	}

	@SuppressWarnings("rawtypes")
	private String getType(String sex, String empiId, Session ss) {
		healthyDes = "";
		int count = 0;
		String type = "";
		String hql = "from " + BSCHISEntryNames.MDC_DiabetesRecord
				+ " where empiId='" + empiId + "'";
		List records = executeSql(hql, ss);
		int rowCount = records.size();
		if (rowCount != 0) {
			type = sex + "_4.png";
			count = count + 1;
			healthyDes = "[糖尿病]";
		}

		hql = "from " + BSCHISEntryNames.MDC_HypertensionRecord
				+ " where empiId='" + empiId + "'";
		records = executeSql(hql, ss);
		rowCount = records.size();
		if (rowCount != 0) {
			type = sex + "_5.png";
			count = count + 1;
			healthyDes = healthyDes + "[高血压]";
		}
		if (count == 0) {
			healthyDes = "[健康]";
			type = sex + "_1.png";
		} else if (count >= 2) {
			type = sex + "_6.png";
		}
		return type;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String createFlashString(List jsonArr) {
		String set = "";
		String connector = "";
		String sameSet = "width='50' height='80' imageNode='1' labelAlign='top' alpha='0' imageAlign='bottom' imageWidth='48' imageHeight='65'/>";
		String sameConnector = "<connector color='" + LineColor
				+ "' arrowAtStart='0' arrowAtEnd='0' ";
		connector = connector + sameConnector
				+ "from='granddad' to='father' Label='父子'/>";
		connector = connector + sameConnector
				+ "from='grandma' to='father' Label='母子'/>";
		connector = connector + sameConnector
				+ "from='grandfather' to='mother' Label='父女'/>";
		connector = connector + sameConnector
				+ "from='grandmother' to='mother' Label='母女'/>";
		int childnumber = 0;
		double sonX = 0;
		double step = 0;
		for (int z = 0; z < jsonArr.size(); z++) {
			HashMap<String, Object> childArrJson = (HashMap<String, Object>) jsonArr
					.get(z);
			String key = (String) childArrJson.get("id");
			if (key.equals("granddad")) {
				set = set + "<set x='10' y='90' name='"
						+ childArrJson.get(NAME)
						+ "' link='javascript:FC_Click(&quot;" + chartId
						+ "&quot;,&quot;" + childArrJson.get("empiid")
						+ "&quot;)' imageurl='" + childArrJson.get("path")
						+ "' toolText='" + childArrJson.get("healthyDes")
						+ "' id='" + key + "' " + sameSet;
			}
			if (key.equals("grandma")) {
				set = set + "<set x='40' y='90' name='"
						+ childArrJson.get(NAME)
						+ "' link='javascript:FC_Click(&quot;" + chartId
						+ "&quot;,&quot;" + childArrJson.get("empiid")
						+ "&quot;)' imageurl='" + childArrJson.get("path")
						+ "' toolText='" + childArrJson.get("healthyDes")
						+ "' id='" + key + "' " + sameSet;
			}
			if (key.equals("grandfather")) {
				set = set + "<set x='60' y='90' name='"
						+ childArrJson.get(NAME)
						+ "' link='javascript:FC_Click(&quot;" + chartId
						+ "&quot;,&quot;" + childArrJson.get("empiid")
						+ "&quot;)' imageurl='" + childArrJson.get("path")
						+ "' toolText='" + childArrJson.get("healthyDes")
						+ "' id='" + key + "' " + sameSet;
			}
			if (key.equals("grandmother")) {
				set = set + "<set x='90' y='90' name='"
						+ childArrJson.get(NAME)
						+ "' link='javascript:FC_Click(&quot;" + chartId
						+ "&quot;,&quot;" + childArrJson.get("empiid")
						+ "&quot;)' imageurl='" + childArrJson.get("path")
						+ "' toolText='" + childArrJson.get("healthyDes")
						+ "' id='" + key + "' " + sameSet;
			}
			if (key.equals("father")) {
				set = set + "<set x='25' y='70' name='"
						+ childArrJson.get(NAME)
						+ "' link='javascript:FC_Click(&quot;" + chartId
						+ "&quot;,&quot;" + childArrJson.get("empiid")
						+ "&quot;)' imageurl='" + childArrJson.get("path")
						+ "' toolText='" + childArrJson.get("healthyDes")
						+ "' id='" + key + "' " + sameSet;
			}
			if (key.equals("mother")) {
				set = set + "<set x='75' y='70' name='"
						+ childArrJson.get(NAME)
						+ "' link='javascript:FC_Click(&quot;" + chartId
						+ "&quot;,&quot;" + childArrJson.get("empiid")
						+ "&quot;)' imageurl='" + childArrJson.get("path")
						+ "' toolText='" + childArrJson.get("healthyDes")
						+ "' id='" + key + "' " + sameSet;
			}
			if (key.equals("me")) {
				if (((String) childArrJson.get("type")).startsWith("M")) {
					connector = connector + sameConnector
							+ "from='father' to='me' Label='父子'/>";
					connector = connector + sameConnector
							+ "from='mother' to='me' Label='母子'/>";
				} else {
					connector = connector + sameConnector
							+ "from='father' to='me' Label='父女'/>";
					connector = connector + sameConnector
							+ "from='mother' to='me' Label='母女'/>";
				}

				childnumber = Integer.parseInt(childArrJson.get("childnumber")
						.toString());
				if (childnumber == 1) {
					sonX = 50;
					step = 0;
				} else if (childnumber == 2) {
					sonX = 27.5;
					step = 50;
				} else if (childnumber == 3) {
					sonX = 20;
					step = 30;
				} else if (childnumber == 4) {
					sonX = 15;
					step = 25;
				} else if (childnumber == 5) {
					sonX = 10;
					step = 20;
				}
				set = set + "<set x='50' y='50' name='"
						+ childArrJson.get(NAME)
						+ "' link='javascript:FC_Click(&quot;" + chartId
						+ "&quot;,&quot;" + childArrJson.get("empiid")
						+ "&quot;)' imageurl='" + childArrJson.get("path")
						+ "' toolText='" + childArrJson.get("healthyDes")
						+ "' id='" + key + "' " + sameSet;
			}
			if (key.equals("partner")) {
				if (childArrJson.get("empiid") != null
						&& !childArrJson.get("empiid").equals("")) {
					set = set
							+ "<set x='90' y='50' link='javascript:FC_Click(&quot;"
							+ chartId + "&quot;,&quot;"
							+ childArrJson.get("empiid")
							+ "&quot;)' imageurl='" + childArrJson.get("path")
							+ "' toolText='" + childArrJson.get("healthyDes")
							+ "' name='" + childArrJson.get(NAME) + "' id='"
							+ key + "' " + sameSet;
					connector = connector + sameConnector
							+ "from='me' to='partner' Label='配偶'/>";
				}
			}
			if (key.equals("child")) {
				ArrayList childArr = (ArrayList) childArrJson.get("child");
				int length = childArr.size();
				String mulSonSet = "";
				String mulGrandSet = "";
				HashMap<String, Object> meJson = (HashMap<String, Object>) jsonArr
						.get(0);
				for (int i = 0; i < length; i++) {
					HashMap<String, Object> childJson = (HashMap<String, Object>) childArr
							.get(i);
					key = (String) childJson.get("id");
					String relationShip = "";
					relationShip = getRelationShip((String) meJson.get("type"),
							(String) childJson.get("type"));
					connector = connector + sameConnector + "from='me' to='"
							+ key + "' Label='" + relationShip + "'/>";
					mulSonSet = mulSonSet + "<set x='" + sonX
							+ "' y='30' name='" + childJson.get(NAME)
							+ "' link='javascript:FC_Click(&quot;" + chartId
							+ "&quot;,&quot;" + childJson.get("empiid")
							+ "&quot;)' imageurl='" + childJson.get("path")
							+ "' toolText='" + childJson.get("healthyDes")
							+ "' id='" + key + "' " + sameSet;
					if (childJson.containsKey("child")) {
						double grandsonX = 0;
						List<HashMap<String, Object>> grandChildArr = (List<HashMap<String, Object>>) childJson
								.get("child");
						String grandKey = "";
						int grandChildLength = grandChildArr.size();
						for (int j = 0; j < grandChildLength; j++) {
							HashMap<String, Object> grandChildJson = (HashMap<String, Object>) grandChildArr
									.get(j);
							grandKey = (String) grandChildJson.get("id");
							String grandRelationShip = "";
							grandRelationShip = getRelationShip(
									(String) childJson.get("type"),
									(String) grandChildJson.get("type"));
							connector = connector + sameConnector + "from='"
									+ key + "' to='" + grandKey + "' Label='"
									+ grandRelationShip + "'/>";
							grandsonX = sonX + (j - 1) * grandsonGap;
							mulGrandSet = mulGrandSet + "<set x='" + grandsonX
									+ "' y='8' name='"
									+ grandChildJson.get(NAME)
									+ "' link='javascript:FC_Click(&quot;"
									+ chartId + "&quot;,&quot;"
									+ grandChildJson.get("empiid")
									+ "&quot;)' imageurl='"
									+ grandChildJson.get("path")
									+ "' toolText='"
									+ childJson.get("healthyDes") + "' id='"
									+ grandKey + "' " + sameSet;
						}
					}
					sonX = sonX + step;
				}
				set = set + mulSonSet + mulGrandSet;
			}
		}
		connector = "<connectors strength='1' color='000000' stdThickness='4'>"
				+ connector + "</connectors>";
		set = "<dataset plotborderAlpha='2' showformbtn='0'>" + set
				+ "</dataset>";
		return chartStart + connector + set + chartEnd;
	}

	private String getRelationShip(String meType, String otherType) {
		String ship = "未知";
		if (meType.startsWith("M") && otherType.startsWith("M")) {
			ship = "父子";
		} else if (meType.startsWith("M") && otherType.startsWith("F")) {
			ship = "父女";
		} else if (meType.startsWith("F") && otherType.startsWith("M")) {
			ship = "母子";
		} else if (meType.startsWith("F") && otherType.startsWith("F")) {
			ship = "母女";
		}
		return ship;
	}

	public void writeToResponse(HttpServletResponse response,
			HashMap<String, Object> json) throws IOException {
		response.addHeader("content-type", "text/xml;charset=UTF-8");
		OutputStreamWriter out = new OutputStreamWriter(
				response.getOutputStream(), "gbk");
		try {
			out.write((String) json.get("body"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

}
