/*
 * 
 */
package chis.source.conf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import chis.source.util.BSCHISUtil;

import ctd.util.xml.XMLHelper;

/**
 * 
 * @description
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class BizColumnConfig {

	private static final String CONF_PATH = "update/";
	public static final String MOV_EHR = "EHRMove";
	public static final String MOV_EHR_SUB = "EHRMoveSubAll";
	public static final String MOV_CDH = "CDHMove";
	public static final String MOV_CDH_BATCH = "CDHMoveBatch";
	public static final String MOV_MHC = "MHCMove";
	public static final String MOV_MHC_BATCH = "MHCMoveBatch";
	public static final String UPDATE_REGION_TEXT = "UpdateRegionText";
	public static final String CHECK_MANADOC_IN_USE = "checkManaDoctorInUser";

	private static Map<String, Config> confs = null;

	/**
	 * 
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 */
	@SuppressWarnings("unchecked")
	private static void parse() throws DocumentException, IOException {
		String home = BSCHISUtil.getConfigPath(CONF_PATH);
		File homeDir = new File(home);
		String[] files = homeDir.list();
		confs = new HashMap<String, Config>();
		for (int i = 0; i < files.length; i++) {
			File f = new File(home + files[i]);
			Document doc = XMLHelper.getDocument(f);
			Element root = doc.getRootElement();
			Config c = new Config();
			c.id = root.attributeValue("id");
			// 解析tables
			List<Element> tableElements = root.elements();
			for (int j = 0; j < tableElements.size(); j++) {
				Table t = new Table();
				Element tableElement = tableElements.get(j);
				t.name = tableElement.attributeValue("name");
				t.queryFields = tableElement.attributeValue("queryField")
						.split(",");
				t.queryParams = tableElement.attributeValue("queryParam")
						.split(",");
				// 解析columns
				List<Element> columnElements = tableElement.elements();
				for (int k = 0; k < columnElements.size(); k++) {
					Element columnElement = columnElements.get(k);
					Column cl = new Column();
					cl.name = columnElement.attributeValue("name");
					cl.param = columnElement.attributeValue("param");
					t.columns.add(cl);
				}
				c.tables.put(t.name, t);
			}
			confs.put(c.id, c);
		}
	}

	/**
	 * 
	 * @param configName
	 * @return
	 */
	private static Config getConfig(String configName, List<String> tableNames) {
		// if(confs ==null ){
		try {
			parse();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }
		Config conf = null;
		conf = confs.get(configName);
		if (tableNames == null || tableNames.size() == 0) {
			return conf;
		}

		Config copy = new Config();
		copy.id = conf.id;
		for (String tableName : tableNames) {
			tableName = tableName.substring(tableName.lastIndexOf(".") + 1);
			Table t = conf.tables.get(tableName);
			if (t == null)
				continue;
			copy.tables.put(t.name, t);
		}
		return copy;
	}

	/**
	 * 
	 * @param configName
	 * @param params
	 * @return
	 */
	public static List<String> getQuerySql(String configName,
			Map<String, Object> params) {
		ArrayList<String> sqlList = new ArrayList<String>();
		Config conf = getConfig(configName, null);
		for (Table t : conf.tables.values()) {
			StringBuffer sb = new StringBuffer("select ");
			for (Column cl : t.columns) {
				sb.append(cl.name).append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append(" from ").append(t.name).append(" where ");
			for (int i = 0; i < t.queryFields.length; i++) {
				sb.append(t.queryFields[i]).append("=:")
						.append(params.get(t.queryParams[i])).append(" and ");
			}
			sb.delete(sb.length() - 5, sb.length() - 1);

			sqlList.add(sb.toString());
		}
		return sqlList;
	}

	/**
	 * 
	 * @param configName
	 * @param tableNames
	 * @return
	 */
	public static List<String> getUpdateSql(String configName,
			Map<String, Object> params, List<String> tableNames) {
		ArrayList<String> sqlList = new ArrayList<String>();
		Config conf = getConfig(configName, tableNames);

		for (Table t : conf.tables.values()) {
			StringBuffer sb = new StringBuffer("update ").append(t.name)
					.append(" set ");
			for (Column cl : t.columns) {
				if (params.containsKey(cl.param)) {
					sb.append(cl.name).append("=:").append(cl.param)
							.append(",");
				}
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append(" where ");
			for (int i = 0; i < t.queryFields.length; i++) {
				sb.append(t.queryFields[i]).append("=:")
						.append(t.queryParams[i]).append(" and ");
			}
			sb.delete(sb.length() - 5, sb.length() - 1);
			sqlList.add(sb.toString());
		}
		return sqlList;
	}

	/**
	 * 
	 * @param configName
	 * @return
	 */
	public static List<String> getUpdateSql(String configName,
			Map<String, Object> prarms) {
		return getUpdateSql(configName, prarms, null);
	}

	/**
	 * 
	 * @Description:获取全部fullName
	 * @param configName
	 * @return
	 * @author ChenXianRui 2014-6-27 上午11:22:08
	 * @throws IOException
	 * @throws DocumentException
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getUpdateTable(String configName)
			throws IOException, DocumentException {
		List<String> utList = new ArrayList<String>();
		String home = BSCHISUtil.getConfigPath(CONF_PATH);
		File f = new File(home + configName + ".xml");
		Document doc = XMLHelper.getDocument(f);
		Element root = doc.getRootElement();
		List<Element> tableElements = root.elements();
		for (int j = 0; j < tableElements.size(); j++) {
			Element tableElement = tableElements.get(j);
			utList.add(tableElement.attributeValue("fullName"));
		}
		return utList;
	}

	public static void main(String args[]) {
		Map<String, Object> p = new HashMap();
		p.put("phrId", "123");
		p.put("phrId", "123");
		p.put("phrId", "123");
		System.out.println(getUpdateSql("EHRMove", p));
		try {
			System.out.println(getUpdateTable("EHRMove.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 
	 */
	static class Config {
		String id = "";
		Map<String, Table> tables = new HashMap<String, Table>();
	}

	/*
	 * 
	 */
	static class Table {
		String name = "";
		String queryFields[];
		String queryParams[];
		ArrayList<Column> columns = new ArrayList<Column>();
	}

	/*
	 * 
	 */
	static class Column {
		String name = "";
		String param = "";
	}
}
