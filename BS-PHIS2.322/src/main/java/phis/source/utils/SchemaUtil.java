/**
 * @(#)SchemaUtil.java Created on 2011-12-28 上午10:36:25
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */

package phis.source.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;

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
	public static Map<String, Object> setDictionaryMassageForForm(
			Map<String, Object> info, String schemaName) {
		if (null == info) {
			return null;
		}
		Schema schema = SchemaController.instance().getSchema(schemaName);
		List<SchemaItem> itemList = schema.getItems();
		for (Iterator<SchemaItem> iterator = itemList.iterator(); iterator
				.hasNext();) {
			SchemaItem item = iterator.next();
			if (item.isCodedValue()) {
				String schemaKey = item.getId();
				String key = StringUtils.trimToEmpty(info.get(schemaKey)==null?null:info.get(schemaKey)
						.toString());

				if (!key.equals("") && key != null) {
					Map<String, String> dicValue = new HashMap<String, String>();
					dicValue.put("key", key);
					dicValue.put("text", item.getDisplayValue(key));
					info.put(schemaKey, dicValue);
				}
			}
		}
		return info;
	}

	/**
	 * 根据schema设置字典文本数据
	 * 
	 * @param infoList
	 * @param schemaName
	 * @return
	 */
	public static List<Map<String, Object>> setDictionaryMassageForForm(
			List<Map<String, Object>> infoList, String schemaName) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < infoList.size(); i++) {
			list.add(setDictionaryMassageForForm(infoList.get(i), schemaName));
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
	public static Map<String, Object> setDictionaryMassageForList(
			Map<String, Object> info, String schemaName) {
		Schema schema = SchemaController.instance().getSchema(schemaName);
		List<SchemaItem> itemList = schema.getItems();
		for (Iterator<SchemaItem> iterator = itemList.iterator(); iterator
				.hasNext();) {
			SchemaItem item = iterator.next();
			if (item.isCodedValue()) {
				String schemaKey = item.getId();
				String key = StringUtils.trimToEmpty(info.get(schemaKey)==null?null:info.get(schemaKey)
						.toString());
				if (!key.equals("") && key != null) {
					info.put(schemaKey + "_text", item.getDisplayValue(key));
				}
			}
		}
		return info;
	}

	/**
	 * 根据schema设置字典文本数据
	 * 
	 * @param infoList
	 * @param schemaName
	 * @return
	 */
	public static List<Map<String, Object>> setDictionaryMassageForList(
			List<Map<String, Object>> infoList, String schemaName) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < infoList.size(); i++) {
			list.add(setDictionaryMassageForList(infoList.get(i), schemaName));
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
		Map<String, Object> formData = new HashMap<String, Object>();
		for (Iterator iterator = listData.keySet().iterator(); iterator
				.hasNext();) {
			String key = (String) iterator.next();
			if (key.indexOf("_text") > 0) {
				continue;
			}
			String keyValue = (String) listData.get(key);
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

}
