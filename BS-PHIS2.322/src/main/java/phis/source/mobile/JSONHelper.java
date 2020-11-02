package phis.source.mobile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class JSONHelper {

	
	@SuppressWarnings("unchecked")
	public static JSONObject map2JSONObject(Map<String, Object> map)
			throws JSONException {
		JSONObject json = new JSONObject();
		for (String key : map.keySet()) {
			Object obj = map.get(key);
			if (obj instanceof Map) {
				json.put(key, map2JSONObject((Map<String, Object>) obj));
			} else if (obj instanceof List) {
				json.put(key, list2JSONArray((List<Object>) obj));
			} else {
				json.put(key, obj);
			}
		}
		
		return json;
	}

	/**
	 * @param list
	 * @return
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public static JSONArray list2JSONArray(List<Object> list)
			throws JSONException {
		JSONArray array = new JSONArray();
		for (Object obj : list) {
			if (obj instanceof Map) {
				array.put(map2JSONObject((Map<String, Object>) obj));
			} else if (obj instanceof List) {
				array.put(list2JSONArray((List<Object>) obj));
			} else {
				array.put(obj);
			}
		}
		return array;
	}

	/**
	 * @param json
	 * @param map
	 * @return ������map��null���򷵻�һ���µ�map���󣬷����Ծ��Ǵ�����Ǹ�����
	 * @throws JSONException
	 */
	public static Map<String, Object> jsonObject2Map(JSONObject json,
			Map<String, Object> map) throws JSONException {
		map = map == null ? new HashMap<String, Object>() : map;
		for (@SuppressWarnings("unchecked")
		Iterator<String> it = json.keys(); it.hasNext();) {
			String key = it.next();
			Object obj = json.get(key);
			if (obj instanceof JSONObject) {
				map.put(key, jsonObject2Map((JSONObject) obj, null));
			} else if (obj instanceof JSONArray) {
				map.put(key, jsonArray2List((JSONArray) obj, null));
			}else{
				map.put(key, obj);
			}
		}
		return map;
	}

	/**
	 * @param array
	 * @return
	 * @throws JSONException
	 */
	public static List<Object> jsonArray2List(JSONArray array, List<Object> list)
			throws JSONException {
		list = list == null ? new ArrayList<Object>() : list;
		for (int i = 0; i < array.length(); i++) {
			Object obj = array.get(i);
			if (obj instanceof JSONObject) {
				list.add(jsonObject2Map((JSONObject) obj, null));
			} else if (obj instanceof JSONArray) {
				list.add(jsonArray2List((JSONArray) obj, null));
			} else {
				list.add(obj);
			}
		}
		return list;
	}
	
	public static JSONArray createJSONArray(Object key,Object value){
		JSONArray array = new JSONArray();
		array.put(key);
		array.put(value);
		return array ;
	}
	
	public static JSONArray createSimpleCnd(String exp , String columnName , String columnType  , String value){
		JSONArray colCnd = JSONHelper.createJSONArray("$", columnName); 
		JSONArray valCnd = JSONHelper.createJSONArray(columnType, value); 
		return createJSONArray(exp , colCnd , valCnd);
	} 
	
	public static JSONArray createJSONArray(Object obj1 ,Object obj2,Object obj3){
		JSONArray array = new JSONArray();
		array.put(obj1);
		array.put(obj2);
		array.put(obj3);
		return array ;
	}
	/**
	 * json��ʽ�ַ�ת����map
	 * @param jsonStr
	 * @return
	 * @throws JSONException
	 */
	public static Map<String, Object> jsonStr2Map(String jsonStr) throws JSONException{
		JSONObject json=new JSONObject(jsonStr);
		Map<String, Object> map=jsonObject2Map(json,null);
		return map;
	}
}
