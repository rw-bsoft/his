package chis.source.print.instance;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ctd.print.PrintException;
import ctd.dictionary.DictionaryController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import chis.source.PersistentDataOperationException;
import chis.source.gis.LayerDic;
import chis.source.gis.ServiceUtil;
import chis.source.print.base.PrintImpl;
import ctd.util.context.Context;

public class ReportFile extends PrintImpl {

	private static final Log logger = LogFactory.getLog(ReportFile.class);

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx)
			throws PrintException {
		String jrxml = (String) request.get("jrxml");
		String regionCode = "";
		String title = "";
		if (request.containsKey("areaGrid")) {
			regionCode = (String) request.get("areaGrid");
		}
		if (request.containsKey("name")) {
			title = (String) request.get("name");
		}
		try {
			response = getJrxml(jrxml, ctx, regionCode, title);
		} catch (PersistentDataOperationException e) {
			logger.error("", e);
		}
	}

	@SuppressWarnings("rawtypes")
	public Map<String, Object> getJrxml(String jrxml, Context ctx,
			String regionCode, String title)
			throws PersistentDataOperationException {
		Map<String, Object> returnJrxml = new HashMap<String, Object>();
		String appHome = this.getClass().getClassLoader().getResource("/")
				.getPath();
		if (appHome != null) {
			try {
				appHome = URLDecoder.decode(appHome, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("", e);
				return new HashMap<String, Object>();
			}
		}
		String where = "";
		String regDic = LayerDic.layerDic.get(String.valueOf(regionCode
				.length()));
		String path = appHome + "config/report/ehrMb/" + jrxml + ".xml";
		try {
			if (ServiceUtil.read(path) == null) {
				return returnJrxml;
			}
			Document xmlDoc = ServiceUtil.read(path);
			if (xmlDoc == null) {
			}
			List sqls = xmlDoc.selectNodes("//sql");
			Element sqlNode = (Element) xmlDoc.selectSingleNode("//sqls");

			HashMap<String,Object> req = null;
			for (int i = 0; i < sqls.size(); i++) {
				int funcVaule = 0;
				Element sqlItem = (Element) sqls.get(i);
				String hql = sqlItem.attributeValue("query");
				if (!"".equals(sqlItem.attributeValue("query"))) {
					if (!"".equals(where)) {
						hql += " and " + where;
					}
					if (!"".equals(regionCode)
							&& "true".equals(sqlNode.attributeValue("zq"))) {
						hql += " and " + regDic + "='" + regionCode + "' ";
					}
					if (sqlItem.attributeValue("group") != null) {
						hql += " group by " + sqlItem.attributeValue("group");
					}
					req = ServiceUtil.executeReportSql(hql, ctx);
				}
				String key[] = sqlItem.attributeValue("key").split(",");
				Object rs = null;
				for (int j = 0; j < key.length; j++) {
					String k[] = key[j].split("_");
					String ky = k[2];
					if (req.containsKey(ky)) {
						rs = req.get(ky);
					} else {
						rs = "0";
					}
					returnJrxml.put(key[j], rs.toString());
					if ("sum".equals(sqlItem.attributeValue("func"))) {
						funcVaule += Long.parseLong(rs.toString());
					}
				}
				returnJrxml.put(sqlItem.attributeValue("funcKey"),
						String.valueOf(funcVaule));
				returnJrxml.put(
						"Title",
						DictionaryController.instance().get("chis.dictionary.areaGrid")
								.getText(regionCode));
				java.util.Date date1 = new java.util.Date();
				DateFormat d = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
				String timeStr2 = d.format(date1);
				returnJrxml.put("date", timeStr2);
			}
			if (xmlDoc.selectNodes("//operation") != null) {
				List operations = xmlDoc.selectNodes("//operation");
				for (int z = 0; z < operations.size(); z++) {
					Element operation = (Element) operations.get(z);
					String operationkey = operation.attributeValue("key");
					String[] operationValues = operation
							.attributeValue("value").split(",");
					int value = 0;
					for (int k = 0; k < operationValues.length; k++) {
						value = value
								+ Integer.parseInt(String.valueOf(returnJrxml
										.get(operationValues[k])));
					}
					returnJrxml.put(operationkey, String.valueOf(value));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return returnJrxml;
	}

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		
	}

}
