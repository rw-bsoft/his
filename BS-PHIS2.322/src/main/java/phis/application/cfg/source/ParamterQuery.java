package phis.application.cfg.source;

import java.util.List;
import java.util.Map;

import phis.source.BSPHISSystemArgument;

import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleQuery;
import ctd.util.context.Context;

public class ParamterQuery extends SimpleQuery {
	/**
	 * 对动态参数进行解析，便于用户识别
	 */
	@SuppressWarnings("unchecked")
	public void doAfterExec(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) res
				.get("body");
		for (Map<String, Object> p : body) {
			String csmc = p.get("CSMC").toString();
			if (csmc.matches("^\\w+\\d+$")) {
				String key = csmc.replaceAll("\\d", "");
				if (BSPHISSystemArgument.dynamicParamter.containsKey(key)) {
					try {
						p.put("MRZ",
								DictionaryController
										.instance()
										.get("phis.dictionary."
												+ BSPHISSystemArgument.dynamicParamter
														.get(key))
										.getText(csmc.replaceAll("[A-Z]", "")));
					} catch (ControllerException e) {
						e.printStackTrace();
					}

				}
			}
		}
		res.put("body", body);
	}

	public static void main(String[] args) {
		System.out.println("FSDDF1".matches("^\\w+\\d+$"));
		System.out.println("FSDDF1".replaceAll("\\d", ""));
		System.out.println("FSDDF1".replaceAll("[A-Z]", ""));
	}
}
