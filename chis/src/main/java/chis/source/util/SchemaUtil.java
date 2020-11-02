/**
 * @(#)SchemaUtil.java Created on 2011-12-28 上午10:36:25
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */

package chis.source.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.validator.ValidateException;

/**
 * @description Schema操作工具类
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */

public class SchemaUtil {
	/**
	 * 根据schema设置字典文本数据
	 * 
	 * @param info
	 * @param schemaName
	 * @return
	 */
	public static Map<String, Object> setDictionaryMessageForForm(
			Map<String, Object> info, String schemaName) {
		if (null == info || info.size() < 1) {
			return null;
		}
		Map<String, Object> data = new HashMap<String, Object>(info);
		Schema schema = SchemaController.instance().getSchema(schemaName);
		List<SchemaItem> itemList = schema.getItems();
		for (Iterator<SchemaItem> iterator = itemList.iterator(); iterator
				.hasNext();) {
			SchemaItem item = iterator.next();
			if (item.isCodedValue()) {
				String schemaKey = item.getId();
				Object keyValue = info.get(schemaKey);
				if (keyValue ==  null) {
					continue;
				}
				if(keyValue instanceof Map){
					continue;
				}
				Map<Object, Object> dicValue = new HashMap<Object, Object>();
				dicValue.put("key", keyValue);
				dicValue.put("text", item.toDisplayValue(keyValue));
				data.put(schemaKey, dicValue);
			} 
		}
		return data;
	}

	/**
	 * 根据schema设置字典文本数据
	 * 
	 * @param infoList
	 * @param schemaName
	 * @return
	 */
	public static List<Map<String, Object>> setDictionaryMessageForForm(
			List<Map<String, Object>> infoList, String schemaName) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < infoList.size(); i++) {
			list.add(setDictionaryMessageForForm(infoList.get(i), schemaName));
		}
		return list;
	}

	/**
	 * 根据schema设置字典文本数据
	 * 
	 * @param info
	 * @param schemaName
	 * @return
	 */
	public static Map<String, Object> setDictionaryMessageForList(
			Map<String, Object> info, String schemaName) {
		if (info == null || info.size() < 1) {
			return null;
		}
		Map<String, Object> data = new HashMap<String, Object>(info);
		Schema schema = SchemaController.instance().getSchema(schemaName);
		List<SchemaItem> itemList = schema.getItems();
		for (Iterator<SchemaItem> iterator = itemList.iterator(); iterator
				.hasNext();) {
			SchemaItem item = iterator.next();
			String itemKey = item.getId();
			Object keyValue =  info.get(itemKey);
			if ( keyValue == null) {
				continue;
			}
			if (item.isCodedValue()) {
				//如果已经包含了_text字段，不处理。
				if(info.containsKey(itemKey + "_text"))
					continue ;
				data.put(itemKey + "_text", item.getDisplayValue(keyValue));
			}
		}
		return data;
	}

	/**
	 * 根据schema设置字典文本数据
	 * 
	 * @param infoList
	 * @param schemaName
	 * @return
	 */
	public static List<Map<String, Object>> setDictionaryMessageForList(
			List<Map<String, Object>> infoList, String schemaName) {
		if(infoList == null){
			return null;
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < infoList.size(); i++) {
			list.add(setDictionaryMessageForList(infoList.get(i), schemaName));
		}
		return list;
	}
	
	/**
	 * 根据schema设置字典文本数据,且该数据用sqlQuery查询得到
	 * 
	 * @param info
	 * @param schemaName
	 * @return
	 */
	public static Map<String, Object> setDictionaryMessageForListFromSQL(
			Map<String, Object> info, String schemaName) {
		if (info == null || info.size() < 1) {
			return null;
		}
		Map<String, Object> data = new HashMap<String, Object>();
		Schema schema = SchemaController.instance().getSchema(schemaName);
		List<SchemaItem> itemList = schema.getItems();
		for (Iterator<SchemaItem> iterator = itemList.iterator(); iterator
				.hasNext();) {
			SchemaItem item = iterator.next();
			String itemKey = item.getId();
			Object keyValue =  info.get(itemKey.toUpperCase());
			if ( keyValue == null) {
				continue;
			}
			data.put(itemKey, keyValue);
			if (item.isCodedValue()) {
				//如果已经包含了_text字段，不处理。
				if(info.containsKey(itemKey.toUpperCase() + "_TEXT"))
					continue ;
				data.put(itemKey + "_text", item.getDisplayValue(keyValue));
			}
			
		}
		return data;
	}

	/**
	 * 根据schema设置字典文本数据,且该数据用sqlQuery查询得到
	 * 
	 * @param infoList
	 * @param schemaName
	 * @return
	 */
	public static List<Map<String, Object>> setDictionaryMessageForListFromSQL(
			List<Map<String, Object>> infoList, String schemaName) {
		if(infoList == null){
			return null;
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < infoList.size(); i++) {
			list.add(setDictionaryMessageForListFromSQL(infoList.get(i), schemaName));
		}
		return list;
	}

	/**
	 * 将Form数据转换为List数据
	 * 
	 * @param formData
	 *            form格式数据
	 * @return list格式数据
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Object> getDataFormToList(
			Map<String, Object> formData) {
		if (formData == null) {
			return null;
		}
		Map<String, Object> listData = new HashMap<String, Object>();
		for (Iterator iterator = formData.keySet().iterator(); iterator
				.hasNext();) {
			String key = (String) iterator.next();
			Object value = formData.get(key);
			if (value instanceof Map) {
				Map<String, Object> dicData = (Map<String, Object>) value;
				listData.put(key, dicData.get("key"));
				listData.put(key + "_text", dicData.get("text"));
			} else {
				listData.put(key, value);
			}
		}
		return listData;
	}

	/**
	 * 将List数据转换为Form数据
	 * 
	 * @param listData
	 *            list格式数据
	 * @return form格式数据
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> getDataListToForm(
			Map<String, Object> listData) {
		if (listData == null) {
			return null;
		}
		Map<String, Object> formData = new HashMap<String, Object>();
		for (Iterator iterator = listData.keySet().iterator(); iterator
				.hasNext();) {
			String key = (String) iterator.next();
			if (key.indexOf("_text") > 0) {
				continue;
			}
			Object keyValue = (Object) listData.get(key);
			Object textValue = listData.get(key + "_text");
			if (textValue != null) {
				Map<String, Object> dicData = new HashMap<String, Object>();
				dicData.put("key", keyValue);
				dicData.put("text", textValue);
				formData.put(key, dicData);
			} else {
				formData.put(key, keyValue);
			}
		}
		return formData;
	}
	
	/**
	 * 将前台过来的值转换成schema里面配置的类型。
	 * @param schema
	 * @param itemId
	 * @param v
	 * @return
	 * @throws ValidateException 
	 */
	public static Object getValue(String schema,String itemId,Object v) throws ValidateException{
		Schema sc = SchemaController.instance().getSchema(schema);
		if(sc!=null){
			SchemaItem si = sc.getItem(itemId);
			return  si.getTypeValue(v);
		}
		return null;
	}

}
