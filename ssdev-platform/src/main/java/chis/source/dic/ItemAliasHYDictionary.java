/**
 * @(#)ItemAliasDictionary.java Created on 2015-8-19 下午5:05:25
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.dic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.schema.DictionaryIndicator;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.service.dao.DBService;
import ctd.util.context.Context;
import ctd.util.xml.XMLHelper;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class ItemAliasHYDictionary extends DBService {

	public Document dic() {
		Document doc = XMLHelper.createDocument();
		String entryName = "chis.application.hy.schemas.MDC_HypertensionVisit";
		Element root = doc.addElement("dic").addAttribute("class",
				"XMLDictionary");
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(entryName);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		for (SchemaItem ctsi : sc.getItems()) {
			Integer display = ctsi.getDisplay();
			boolean hidden = ctsi.isHidden();
			if ((display != null && (display == 0 || display == 1))
					|| hidden == true) {
				continue;
			}
			root.addElement("item").addAttribute("key", ctsi.getId())
					.addAttribute("text", ctsi.getAlias());
		}
		return doc;
	}

	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		// TODO Auto-generated method stub

	}

}
