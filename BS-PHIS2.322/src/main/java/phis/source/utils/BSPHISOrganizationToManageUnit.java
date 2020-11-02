package phis.source.utils;

//文件类
import java.io.File;

//文件输出类
import java.io.FileOutputStream;

//负责解析的类
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
//映射类
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

//xml映射输入和输出类
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//节点类
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * @author 2048 电子病历模版维护机构转换工具类 将新框架的机构配置文件转换成旧的机构配置文件 供CS版电子病历模块编辑工具使用
 * 
 */

public class BSPHISOrganizationToManageUnit {

	public static void queryXml() {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
			Document doc = dbBuilder
					.parse("/Workspaces/MyEclipse 10/SSDEV-PLATFORM-MINI/src/main/java/phis/4404.org");
			Document document = dbBuilder.newDocument();
			document.setXmlVersion("1.0");
			Element root = document.createElement("dic");
			root.setAttribute("manageRule", "4,2,3,3");
			root.setAttribute("searchKey", "pyCode");
			root.setAttribute("queryOnly", "true");
			root.setAttribute("lastModi", "Thu May 10 08:59:54 CST 2012");
			root.setAttribute("class", "XMLDictionary");
			root.setAttribute(
					"filetext",
					"D:\\workspace\\CTDS-BSPHIS2.0\\root\\WEB-INF\\com\\bsoft\\phis\\config\\dictionary\\manageUnit.xml");
			root.setAttribute("version", "1374821557718");
			root.setAttribute("id", "CTRL_CFG_orgCfg");
			root.setAttribute("filename",
					"E:\\jcyl\\PHIS\\WEB-INF\\com\\bsoft\\phis\\config\\dictionary\\manageUnit.xml");
			document.appendChild(root);
			NodeList unitList = doc.getElementsByTagName("unit");
			Element itemElement1 = null;
			Element itemElement2 = null;
			Element itemElement3 = null;
			for (int i = 0; i < unitList.getLength(); i++) {
				Element node = (Element) unitList.item(i);
				if (node.getAttribute("id").length() == 6) {
					itemElement1 = document.createElement("item");
					itemElement1.setAttribute("pyCode",
							node.getAttribute("pyCode"));
					itemElement1
							.setAttribute("text", node.getAttribute("name"));
					itemElement1
							.setAttribute("type", node.getAttribute("type"));
					itemElement1.setAttribute("key", node.getAttribute("id"));
					if (i % 4 == 0) {
						root.appendChild(itemElement1);
					}
				} else if (node.getAttribute("id").length() == 9) {
					itemElement2 = document.createElement("item");
					itemElement2.setAttribute("pyCode",
							node.getAttribute("pyCode"));
					itemElement2
							.setAttribute("text", node.getAttribute("name"));
					itemElement2
							.setAttribute("type", node.getAttribute("type"));
					itemElement2.setAttribute("key", node.getAttribute("id"));
					itemElement1.appendChild(itemElement2);
				} else if (node.getAttribute("id").length() == 12) {
					itemElement3 = document.createElement("item");
					itemElement3.setAttribute("pyCode",
							node.getAttribute("pyCode"));
					itemElement3
							.setAttribute("text", node.getAttribute("name"));
					itemElement3
							.setAttribute("type", node.getAttribute("type"));
					itemElement3.setAttribute("key", node.getAttribute("id"));
					itemElement2.appendChild(itemElement3);
				}
			}
			root.appendChild(itemElement1);
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transFormer = transFactory.newTransformer();
			transFormer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource domSource = new DOMSource(document);
			File file = new File(
					"/Workspaces/MyEclipse 10/SSDEV-PLATFORM-MINI/src/main/java/phis/manageUnit.xml");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream out = new FileOutputStream(file);
			StreamResult xmlResult = new StreamResult(out);
			transFormer.transform(domSource, xmlResult);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		BSPHISOrganizationToManageUnit.queryXml();
	}

}
