package chis.source.gis;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;

import ctd.dictionary.Dictionary;
import ctd.util.context.Context;

public class BuildDefine {

//	public static ReportSchema getCreateSchema(String id, HashMap<String,Object> codeList,
//			HashMap<String, Object> squery){
//
//		ReportSchema schema = null;
//		BuildDefine buildDefine = new BuildDefine();
//		String appHome = buildDefine.getClass().getClassLoader().getResource(
//				"/").getPath();
//		String Path = appHome + "config/report/" + id + ".xml";
//		try {
//			Document xmlDoc = ServiceUtil.read(Path);
//			Element query = (Element) xmlDoc.selectSingleNode("//query");
//			if (query == null) {
//				schema = createSchemaCode(id, squery);
//			} else {
//				if ("single".equals(query.attributeValue("id"))) {
//					schema = createSchemaCode(id, squery);
//				}
//				if ("sum".equals(query.attributeValue("id"))) {
//					schema = createSchemaRkzs(id, squery);
//				}
//				if ("all".equals(query.attributeValue("id"))) {
//					schema = createSchema(id, codeList, squery);
//				}
//				if ("no".equals(query.attributeValue("id"))) {
//					return ReportSchemaController.instance().getSchema(id);
//				}
//				if ("date".equals(query.attributeValue("id"))) {
//					schema = createSchemaDate(id, squery);
//				}
//				if ("Ylzyfx".equals(query.attributeValue("id"))) {
//					schema = createSchemaYlzyfx(id, codeList, squery);
//				}
//				
//			}
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		}
//		return schema;
//	}
//
//	@SuppressWarnings({ "rawtypes", "unused" })
//	public static ReportSchema createSchemaRkzs(String id,
//			HashMap<String, Object> squery) {
//		ReportSchema schema = null;
//		BuildDefine buildDefine = new BuildDefine();
//		String code = String.valueOf(squery.get("regionCode"));
//		String ages = String.valueOf(squery.get("ages"));
//		String appHome = buildDefine.getClass().getClassLoader().getResource(
//				"/").getPath();
//		String Path = appHome + "config/gis/IntegratedCfg.xml";
//		String codeDic = LayerDic.layerDic.get(String.valueOf(code.length()));
//		String nextCodeDic = LayerDic.layerDic.get(String
//				.valueOf(LayerDic.layerMapping.get(String
//						.valueOf(code.length()))));
//		try {
//			Document xmlDoc = ServiceUtil.read(Path);
//			Node defineNode = xmlDoc.selectSingleNode("//report[@id='" + id
//					+ "']");
//			Node tableNode = defineNode.selectSingleNode("table");
//			Node itemNode = defineNode.selectSingleNode("item");
//			List fieldNode = defineNode.selectNodes("field");
//			Element item = (Element) itemNode;
//			item.addAttribute("expr", "a." + nextCodeDic);
//			String tableStr = tableNode.getText();
//			String[] table = tableStr.split(",");
//			Document doc = ReportSchemaController.instance().getConfigDoc(id);
//			Element root = doc.getRootElement();
//			String conText = new String();
//			Element codeDefine = root.addElement("define");
//			setDefineNode(codeDefine, defineNode, code);
//			Element src = codeDefine.addElement("src");
//			for (int j = 0; j < table.length; j++) {
//				Element entrytable = src.addElement("entry");
//				entrytable.addAttribute("name", table[j]);
//			}
//			Element join = src.addElement("join");
//			for (int f = 0; f < fieldNode.size(); f++) {
//				Element field = join.addElement("field");
//				field.appendAttributes((Element) fieldNode.get(f));
//			}
//			Element condition = codeDefine.addElement("condition");
//			// conText.append("['and',");
//			// conText.append(conditionNode.getText() + ",");
//			conText = "['and',['eq',['$','a." + codeDic + "'],['s','" + code
//					+ "']],['eq',['$','a.status'],['s','0']]]";
//			condition.addText(conText);
//			schema = new ReportSchema();
//			schema.setDefineDoc(root.getDocument());
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		}
//		return schema;
//	}
//
//	@SuppressWarnings({ "deprecation", "rawtypes" })
//	public static ReportSchema createSchemaDate(String id,
//			HashMap<String, Object> squery) {
//		ReportSchema schema = null;
//		BuildDefine buildDefine = new BuildDefine();
//		String appHome = buildDefine.getClass().getClassLoader().getResource(
//				"/").getPath();
//		String code = String.valueOf(squery.get("regionCode"));
//		String codeDic = LayerDic.layerDic.get(String.valueOf(code.length()));
//		String Path = appHome + "config/report/" + id + ".xml";
//		try {
//			Document xmlDoc = ServiceUtil.read(Path);
//			List itemNode = xmlDoc.selectNodes("//item");
//			List conditionNode = xmlDoc.selectNodes("//condition");
//			for (int i = 0; i < conditionNode.size(); i++) {
//				Element condition = (Element) conditionNode.get(i);
//				String reText = ",['eq',['$',a." + codeDic
//						+ "],['$','%q.regionCode']]]";
//				condition.addText(reText);
//			}
//			for (int i = 0; i < itemNode.size(); i++) {
//				Element item = (Element) itemNode.get(i);
//				item.setAttributeValue("expr", "a." + codeDic);
//			}
//			schema = new ReportSchema();
//			schema.setDefineDoc(xmlDoc.getDocument());
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		}
//
//		return schema;
//	}
//
//	@SuppressWarnings({ "rawtypes", "unused" })
//	public static ReportSchema createSchemaYlzyfx(String id,
//			HashMap<String,Object> codeList, HashMap<String, Object> squery){
//		ReportSchema schema = null;
//		BuildDefine buildDefine = new BuildDefine();
//		String appHome = buildDefine.getClass().getClassLoader().getResource(
//				"/").getPath();
//		String Path = appHome + "config/gis/IntegratedCfg.xml";
//		try {
//			Document xmlDoc = ServiceUtil.read(Path);
//			Node defineNode = xmlDoc.selectSingleNode("//report[@id='" + id
//					+ "']");
//			Node conditionNode = defineNode.selectSingleNode("condition");
//			Node tableNode = defineNode.selectSingleNode("table");
//			List fieldNode = defineNode.selectNodes("field");
//			String tableStr = tableNode.getText();
//			String[] table = tableStr.split(",");
//			Document doc = ReportSchemaController.instance().getConfigDoc(id);
//			Element root = doc.getRootElement();
//			Iterator codeIt = codeList.entrySet().iterator();
//			for (; codeIt.hasNext();) {
//				StringBuffer conText = new StringBuffer();
//				String code = (String) codeIt.next();
//				String codeDic = LayerDic.layerDic.get(String.valueOf(code
//						.length()));
//				if (codeDic == null) {
//					codeDic = "regionCode";
//				}
//				Element codeDefine = root.addElement("define");
//				Element returnEl = null;
//				Dictionary dic = DictionaryController.instance().get("areaGrid");
//				String codeName = dic.getText(code);
//				Node exprNode = defineNode.selectSingleNode("exp");
//				List itemList = defineNode.selectNodes("item");
//				// Node itemNode = defineNode.selectSingleNode("item");
//				Element header = codeDefine.addElement("headers");
//				for (int i = 0; i < itemList.size(); i++) {
//					Element Item1 = header.addElement("item");
//					Item1.appendAttributes((Element) itemList.get(i));
//				}
//				// Element Item1 = header.addElement("item");
//				// Item1.appendAttributes((Element) itemNode);
//				Element Item2 = header.addElement("item");
//				Item2.addAttribute("renderIndex", "2");
//				Item2.addAttribute("id", code);
//				Item2.addAttribute("alias", codeName);
//				Item2.addAttribute("expr", "resourceNumber");
//				Item2.addAttribute("type", "int");
//				Item2.addAttribute("func", "sum");
//				Element src = codeDefine.addElement("src");
//				for (int j = 0; j < table.length; j++) {
//					Element entrytable = src.addElement("entry");
//					entrytable.addAttribute("name", table[j]);
//				}
//				Element join = src.addElement("join");
//				for (int f = 0; f < fieldNode.size(); f++) {
//					Element field = join.addElement("field");
//					field.appendAttributes((Element) fieldNode.get(f));
//				}
//				Element condition = codeDefine.addElement("condition");
//				conText.append("['and',");
//				conText.append("['eq',['$','regionCode'],['s','"+code+"']]," );
//				conText.append("['eq',['len',['$',regionCode]],['s','");
//				conText.append(String.valueOf(code.length()));
//				conText.append("']]]");
//				condition.addText(conText.toString());
//			}
//			schema = new ReportSchema();
//			schema.setDefineDoc(root.getDocument());
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		}
//		return schema;
//	}
//
//	@SuppressWarnings({ "rawtypes", "unused" })
//	public static ReportSchema createSchema(String id, HashMap<String,Object> codeList,
//			HashMap<String, Object> squery){
//		ReportSchema schema = null;
//		BuildDefine buildDefine = new BuildDefine();
//		String appHome = buildDefine.getClass().getClassLoader().getResource(
//				"/").getPath();
//		String Path = appHome + "config/gis/IntegratedCfg.xml";
//		try {
//			Document xmlDoc = ServiceUtil.read(Path);
//			Node defineNode = xmlDoc.selectSingleNode("//report[@id='" + id
//					+ "']");
//			Node conditionNode = defineNode.selectSingleNode("condition");
//			Node tableNode = defineNode.selectSingleNode("table");
//			List fieldNode = defineNode.selectNodes("field");
//			String tableStr = tableNode.getText();
//			String[] table = tableStr.split(",");
//			Document doc = ReportSchemaController.instance().getConfigDoc(id);
//			Element root = doc.getRootElement();
//			Iterator codeIt = codeList.entrySet().iterator();
//			schema = new ReportSchema();
//			schema.setDefineDoc(doc);
//			for (; codeIt.hasNext();) {
//				StringBuffer conText = new StringBuffer();
//				String code = (String) codeIt.next();
//				String codeDic = LayerDic.layerDic.get(String.valueOf(code
//						.length()));
//				if (codeDic == null) {
//					codeDic = "regionCode";
//				}
//				Element codeDefine = root.addElement("define");
//				setDefineNode(codeDefine, defineNode, code);
//				Element src = codeDefine.addElement("src");
//				for (int j = 0; j < table.length; j++) {
//					Element entrytable = src.addElement("entry");
//					entrytable.addAttribute("name", table[j]);
//				}
//				Element join = src.addElement("join");
//				for (int f = 0; f < fieldNode.size(); f++) {
//					Element field = join.addElement("field");
//					field.appendAttributes((Element) fieldNode.get(f));
//				}
//				Element condition = codeDefine.addElement("condition");
//				// conText.append("['and',");
//				// conText.append(conditionNode.getText() + ",");
//				conText.append("['and',['eq',['$','a." + codeDic + "'],['s','"
//						+ code + "']],['eq',['$','a.status'],['s','0']]]");
//				condition.addText(conText.toString());
//			}
//			schema = new ReportSchema();
//			schema.setDefineDoc(root.getDocument());
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		}
//		return schema;
//	}
//
//	@SuppressWarnings({ "rawtypes", "unused" })
//	public static ReportSchema createSchemaCode(String id,
//			HashMap<String, Object> squery){
//		ReportSchema schema = null;
//		BuildDefine buildDefine = new BuildDefine();
//		String code = String.valueOf(squery.get("regionCode"));
//		String ages = String.valueOf(squery.get("ages"));
//		String appHome = buildDefine.getClass().getClassLoader().getResource(
//				"/").getPath();
//		String Path = appHome + "config/gis/IntegratedCfg.xml";
//		String codeDic = LayerDic.layerDic.get(String.valueOf(code.length()));
//		try {
//			Document xmlDoc = ServiceUtil.read(Path);
//			Node defineNode = xmlDoc.selectSingleNode("//report[@id='" + id
//					+ "']");
//			Node conditionNode = defineNode.selectSingleNode("condition");
//			Node tableNode = defineNode.selectSingleNode("table");
//			List fieldNode = defineNode.selectNodes("field");
//			String tableStr = tableNode.getText();
//			String[] table = tableStr.split(",");
//			Document doc = ReportSchemaController.instance().getConfigDoc(id);
//			Element root = doc.getRootElement();
//			String conText = new String();
//			Element codeDefine = root.addElement("define");
//			setDefineNode(codeDefine, defineNode, code);
//			Element src = codeDefine.addElement("src");
//			for (int j = 0; j < table.length; j++) {
//				Element entrytable = src.addElement("entry");
//				entrytable.addAttribute("name", table[j]);
//			}
//			Element join = src.addElement("join");
//			for (int f = 0; f < fieldNode.size(); f++) {
//				Element field = join.addElement("field");
//				field.appendAttributes((Element) fieldNode.get(f));
//			}
//			Element condition = codeDefine.addElement("condition");
//			// conText.append("['and',");
//			// conText.append(conditionNode.getText() + ",");
//			conText = "['and',['eq',['$','a." + codeDic + "'],['s','" + code
//					+ "']],['eq',['$','a.status'],['s','0']]]";
//			condition.addText(conText);
//			schema = new ReportSchema();
//			schema.setDefineDoc(doc);
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		}
//		return schema;
//	}
//
//	@SuppressWarnings({ "unused", "rawtypes" })
//	public static Element setDefineNode(Element subRoot, Node defineNode,
//			String code) throws ControllerException {
//		Element returnEl = null;
//		Dictionary dic = DictionaryController.instance().get("areaGrid");
//		String codeName = dic.getText(code);
//		Node exprNode = defineNode.selectSingleNode("exp");
//		List itemList = defineNode.selectNodes("item");
//		// Node itemNode = defineNode.selectSingleNode("item");
//		Element header = subRoot.addElement("headers");
//		for (int i = 0; i < itemList.size(); i++) {
//			Element Item1 = header.addElement("item");
//			Item1.appendAttributes((Element) itemList.get(i));
//		}
//		// Element Item1 = header.addElement("item");
//		// Item1.appendAttributes((Element) itemNode);
//		Element Item2 = header.addElement("item");
//		Item2.addAttribute("renderIndex", "2");
//		Item2.addAttribute("id", code);
//		Item2.addAttribute("alias", codeName);
//		Item2.addAttribute("expr", "a.regionCode");
//		Item2.addAttribute("type", "int");
//		Item2.addAttribute("func", "count");
//		return returnEl;
//	}
//
//	@SuppressWarnings({ "unused", "rawtypes" })
//	public static ReportSchema createGridSchema(String id, String regionCode) {
//		ReportSchema schema = null;
//		BuildDefine buildDefine = new BuildDefine();
//		Document doc = ReportSchemaController.instance().getConfigDoc(id);
//		Element root = doc.getRootElement();
//		List defineList = root.selectNodes("define");
//		String topLayer = String.valueOf(LayerDic.layerMapping.get(String
//				.valueOf(regionCode.length())));
//		String topReCode = LayerDic.layerDic.get(topLayer);
//		for (int i = 0; i < defineList.size(); i++) {
//			Element define = (Element) defineList.get(i);
//			Element header = (Element) define.selectSingleNode("headers");
//			Element Item = (Element) header
//					.selectSingleNode("item[@renderIndex='1']");
//			Item.addAttribute("expr", "a." + topReCode + "");
//		}
//		String reCode = LayerDic.layerDic.get(String.valueOf(regionCode
//				.length()));
//		Element condition = root.addElement("condition");
//		String conText = "['and',['eq',['$','a." + reCode + "'],['s','"
//				+ regionCode + "']],['eq',['$','a.status'],['s','0']]]";
//		condition.addText(conText.toString());
//		Document rootdoc = root.getDocument();
//		schema = new ReportSchema();
//		schema.setDefineDoc(rootdoc);
//		return schema;
//	}
//
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public static List getGridIntegratedCfg(HashMap<String,Object> jsonReq,
//			HashMap<String,Object> codeList, Context ctx) {
//		ReportSchema schema = createGridSchema((String)jsonReq.get("schema"),
//				(String)jsonReq.get("regionCode"));
//		List<HashMap> data = schema.run(ctx);
//		List<HashMap<String,Object>> body = new ArrayList<HashMap<String,Object>>();
//		for (HashMap r : data) {
//			body.add(r);
//		}
//		return body;
//	}
//
//	@SuppressWarnings("rawtypes")
//	public static List getIntegratedCfg(HashMap<String,Object> jsonReq,
//			HashMap<String,Object> codeList, Context ctx, HashMap<String, Object> query){
////		ReportSchema schema = null;
////		if ("Analys_rkn".equals(jsonReq.getString("schema"))
////				|| "Analys_gxynl".equals(jsonReq.getString("schema"))
////				|| "Analys_tnbnl".equals(jsonReq.getString("schema"))
////				|| "Analys_jtnl".equals(jsonReq.getString("schema"))
////				|| "Analys_zlbnl".equals(jsonReq.getString("schema"))) {
////			// Iterator codesIt=codeList.keys();
////			// for(;codesIt.hasNext();)
////			// {
////			// String code=(String)codesIt.next();
////			// System.out.println(code);
////			// }
////		} else {
////			if(jsonReq.getString("schema").equals("Ylzyfx"))
////			{
////				codeList.remove(jsonReq.getString("regionCode"));
////				schema = createSchemaYlzyfx(jsonReq.getString("schema"), codeList, query);
////			}else{
////			schema = createSchema(jsonReq.getString("schema"), codeList, query);
////			}
////		}
////		List<HashMap> data=new ArrayList<HashMap>();
////		try{
////			data = schema.run(ctx);
////		}catch(Exception e){
////			
////		}
//		
//		List body = new ArrayList();
//		return body;
//	}
}
