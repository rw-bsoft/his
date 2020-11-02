package ctd.dictionary.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.dictionary.support.TableDictionary;
import ctd.util.S;

public class DepartmentDictionary extends TableDictionary {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DepartmentDictionary.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected DictionaryItem parseDicItem(Object[] r) {
		String key = String.valueOf(r[0]);
		String text = String.valueOf(r[1]);
		DictionaryItem dictionaryItem = new DictionaryItem(key, text);
		Map<Object, String> manageRef = new HashMap<Object, String>();
		try {
			List<DictionaryItem> dicItems = DictionaryController.instance()
					.get("phis.@manageUnit").itemsList();
			for (DictionaryItem d : dicItems) {
				manageRef.put(d.getProperty("ref"), d.getKey());
			}
		} catch (ControllerException e) {
			LOGGER.error("DepartmentDictionary parse error!");
		}
		if (propFields != null) {
			Set<String> ps = propFields.keySet();
			int i = 2;
			for (String p : ps) {
				if (r[i] != null) {
					// 如果是ORGANIZCODE，转成phis的JGID
					if ("ORGANIZCODE".equals(p)) {
						dictionaryItem.setProperty(
								S.isEmpty(propFields.get(p)) ? p : propFields
										.get(p), manageRef.get(r[i]));
					} else {
						dictionaryItem.setProperty(
								S.isEmpty(propFields.get(p)) ? p : propFields
										.get(p), r[i]);
					}
				}
				i++;
			}
		}
		if (!S.isEmpty(iconCls)) {
			dictionaryItem.setProperty("iconCls", iconCls);
		}
		if (codeRule != null) {
			String parentKey = codeRule.getParentKey(key);
			dictionaryItem.setProperty("parent", parentKey);
			if (codeRule.isLeaf(key)) {
				dictionaryItem.setLeaf(true);
			}
		} else {
			if (!S.isEmpty(parent)) {
				String parentKey = r[r.length - 1] == null ? null : String
						.valueOf(r[r.length - 1]);
				if (!S.isEmpty(parentKey)) {
					dictionaryItem.setProperty("parent", parentKey);
				}
			} else {
				dictionaryItem.setLeaf(true);
			}
		}
		return dictionaryItem;
	}

}