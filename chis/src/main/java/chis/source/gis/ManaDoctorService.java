package chis.source.gis;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import chis.source.Constants;
import ctd.account.user.User;
import ctd.account.user.UserController;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.service.core.Service;
import ctd.util.context.Context;

public class ManaDoctorService implements Service {

	public void execute(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, Context ctx) {
		Map<String, Object> jsonBody = new HashMap<String, Object>();
		String id = (String) jsonReq.get("manaDoctorId");
		User user = null;
		try {
			user = UserController.instance().get("01_" + id);
			if (user == null) {
				user = UserController.instance().get("05_" + id);
			}

			if (user == null) {
				user = UserController.instance().get("02_" + id);
			}

			if (user == null) {
				jsonRes.put(RES_CODE, Constants.CODE_NOT_FOUND);
				jsonRes.put(RES_MESSAGE, "责任医生信息不完整");
				return;
			}
			String manageUnitText = getDisplayValue("manageUnit",
					StringUtils.trimToEmpty((String) jsonReq.get("manaUnitId")));
			if (manageUnitText.length() == 0)
				manageUnitText = (String) user.getProperty("manaUnitId_text");
			jsonBody.put("manaDoctorId_text", user.getName());
			jsonBody.put("manaUnitId_text", manageUnitText);
			jsonRes.put("body", jsonBody);
		} catch (ControllerException e) {
			e.printStackTrace();
		}

	}

	private String getDisplayValue(String id, String key)
			throws ControllerException {
		Dictionary d = DictionaryController.instance().get(id);
		if (d == null)
			return key;
		String text = "";
		if (key.indexOf(",") == -1) {
			text = d.getText(key);
			DictionaryItem di = d.getItem(key);
			if (di == null) {
				return text;
			}
		} else {
			String[] keys = key.split(",");
			StringBuffer sb = new StringBuffer();
			for (String s : keys) {
				sb.append(",").append(d.getText(s));
			}
			text = sb.substring(1);
		}
		return text;
	}
}