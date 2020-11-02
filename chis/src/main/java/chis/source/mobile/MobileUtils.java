package chis.source.mobile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class MobileUtils {
	/**
	 * @param data
	 * @return
	 * @throws JSONException
	 */
	public static Map<String, Object> isNullForJSONObject(
			Map<String, Object> data) throws JSONException {
		Map<String, Object> dataNew = new HashMap<String, Object>();
		Iterator<String> iterator = data.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			Object value = data.get(key);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(key, value);
			if (jsonObject.isNull(key)) {
				dataNew.put(key, "");
			} else {
				dataNew.put(key, value);
			}
		}
		return dataNew;

	}
}
