package ctd.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import ctd.controller.exception.ControllerException;
import ctd.controller.support.AbstractConfigurableLoader;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.dictionary.support.XMLDictionary;
import ctd.util.BeanUtils;
import ctd.util.JSONUtils;
import ctd.util.S;
import ctd.util.converter.ConversionUtils;

public class SchemaLocalLoader extends AbstractConfigurableLoader<Schema> {
	private static String[] relAlias = { "b", "c", "d", "e", "f", "g", "h", "i" };

	public SchemaLocalLoader() {
		postfix = ".sc";
	}

	@Override
	@SuppressWarnings("unchecked")
	public Schema createInstanceFormDoc(String id, Document doc, long lastModi)
			throws ControllerException {
		Element root = doc.getRootElement();
		if (root == null) {
			throw new ControllerException(ControllerException.PARSE_ERROR,
					"root element missing.");
		}
		try {
			Schema sc = ConversionUtils.convert(root, Schema.class);
			sc.setId(id);
			sc.setLastModify(lastModi);
			List<Element> els = root.selectNodes("properies/p");
			for (Element el : els) {
				sc.setProperty(el.attributeValue("name"), el.getTextTrim());
			}
			els = root.selectNodes("relations/relation");
			int count = 0;
			for (Element rel : els) {
				String type = rel.attributeValue("type", "children");
				String joinWay = rel.attributeValue("joinWay", "inner");
				String entryName = rel.attributeValue("entryName");
				String alias = relAlias[count];
				Schema relationSchema = SchemaController.instance().get(
						entryName);
				SchemaRelation sr = new SchemaRelation(type, entryName, alias,
						relationSchema.getEntityName(), relationSchema);
				sr.setJoinWay(joinWay);
				if (type.equals("parent")) {
					sc.addParentRelation(sr);
					Schema psc = SchemaController.instance().get(entryName);
					String fk = psc.getKey();
					if (rel.element("join") == null) {
						sr.addJoinCondition(fk, fk);
					}
				} else {
					sc.addChildrenRelation(sr);
					String key = sc.getKey();
					if (rel.element("join") == null) {
						sr.addJoinCondition(key, key);
					}
				}
				els = rel.elements("update");
				for (Element update : els) {
					sr.addUpdate(update.attributeValue("from"),
							update.attributeValue("to"),
							update.attributeValue("func", ""));
				}
				els = rel.elements("join");
				for (Element join : els) {
					sr.addJoinCondition(join.attributeValue("parent"),
							join.attributeValue("child"));
				}
				els = rel.elements("condition");
				for (Element condition : els) {
					sr.addCondition(condition.attributeValue("exp"));
				}
				count++;
			}
			els = root.selectNodes("item");
			List<String> pkeys = new ArrayList<String>();
			for (Element el : els) {
				String ref = el.attributeValue("ref");
				if (S.isEmpty(ref)) {
					SchemaItem si = ConversionUtils.convert(el,
							SchemaItem.class);
					si.setSchemaId(id);
					si.setEntityName(sc.getEntityName());
					String notNull = (String) si.getProperty("not-null");
					if ("1".equals(notNull)) {
						notNull = "true";
					}
					if (!S.isEmpty(notNull)) {
						si.setRequired(Boolean.parseBoolean(notNull));
					}
					if (!S.isEmpty(el.attributeValue("pkey"))) {
						pkeys.add(si.getId());
					}
					Element dicEl = el.element("dic");
					if (dicEl != null) {
						List<Element> items = dicEl.elements("item");
						if (items.size() == 0) {
							if (S.isEmpty(dicEl.attributeValue("id"))) {
								dicEl.addAttribute("id",
										el.attributeValue("id"));
							}
							DictionaryIndicator dic = ConversionUtils.convert(
									dicEl, DictionaryIndicator.class);
							si.setDic(dic);
						} else {
							String dicId = S.join(id, ".", si.getId());
							XMLDictionary innerDic = ConversionUtils.convert(
									dicEl, XMLDictionary.class);
							innerDic.setId(dicId);
							innerDic.setLastModify(lastModi);
							Document define = DocumentHelper
									.createDocument(dicEl.createCopy());
							items = define.selectNodes("//item");
							for (Element eli : items) {
								DictionaryItem item = ConversionUtils.convert(
										eli, DictionaryItem.class);
								if (eli.elements("item").size() == 0) {
									item.setLeaf(true);
								}
								innerDic.addItem(item);
							}
							innerDic.setDefineDoc(define);
							DictionaryController.instance().add(innerDic);
							DictionaryIndicator dic = ConversionUtils.convert(
									dicEl, DictionaryIndicator.class);
							dic.setId(dicId);
							dic.setInternal(true);
							si.setDic(dic);
						}
					}
					Element setEl = el.element("set");
					if (setEl != null) {
						String assignType = setEl.attributeValue("type", "exp");
						if (assignType.equals("exp")) {
							List<Object> exp = JSONUtils.parse(setEl.getText(),
									List.class);
							si.setExp(exp);
						}
					}
					Element keyEl = el.element("key");
					if (keyEl != null) {
						List<Element> rs = keyEl.elements();
						for (Element r : rs) {
							HashMap<String, String> m = new HashMap<String, String>();
							List<Attribute> attrs = r.attributes();
							for (Attribute attr : attrs) {
								m.put(attr.getName(), attr.getValue());
							}
							String text = r.getText();
							if (!"".equals(text) && text != null) {
								m.put("text", text.trim());
							}
							si.addKeyRules(m);
						}
					}
					sc.addItem(si);
				} else {
					int n = ref.indexOf(".");
					String rEntryAlias = "b";
					String rItemId = ref;
					if (ref.indexOf(".") > -1) {
						rEntryAlias = ref.substring(0, n);
						rItemId = ref.substring(n + 1);
					}
					SchemaRelation sr = sc.getRelationByAlias(rEntryAlias);
					if (sr != null) {
						SchemaItem item = sr.getEntrySchema().getItem(rItemId);
						if (item == null) {
							throw new ControllerException(
									ControllerException.PARSE_ERROR, "item["
											+ rItemId + "] in schema["
											+ sr.getEntryName() + "] not found");
						}
						SchemaItem si = item.createCopy();
						List<Attribute> attrs = el.attributes();
						for (Attribute attr : attrs) {
							try {
								BeanUtils.setProperty(si, attr.getName(),
										attr.getValue());
							} catch (Exception e) {
								try {
									BeanUtils.setPropertyInMap(si,
											attr.getName(), attr.getValue());
								} catch (Exception e2) {
								}
							}
						}
						si.setSchemaId(id);
						si.setProperty("refAlias", rEntryAlias);
						si.setProperty("refItemId", rItemId);
						sc.putItem(ref, si);
					}
				}
			}
			if (pkeys.size() == 1) {
				sc.setKey(pkeys.get(0));
				sc.setKeyGenerator(sc.getItem(pkeys.get(0)).getKeyGenerator());
			}
			if (pkeys.size() > 1) {
				for (String key : pkeys) {
					sc.addCompositeKey(key);
				}
			}
			return sc;
		} catch (Exception e) {
			throw new ControllerException(e, ControllerException.PARSE_ERROR,
					"schema[" + id + "] init unknow error:" + e.getMessage());
		}
	}
}
