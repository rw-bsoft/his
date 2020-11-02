/**
 * @(#)DictionaryUtil.java Created on 2012-11-21 下午03:04:47
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.PersistentDataOperationException;
import chis.source.util.CNDHelper;
import ctd.service.core.ServiceException;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class DictionaryUtil {
	public static final String MPM_Dic = "dic";
	public static final String MPM_Text = "text";
	public static final String MPM_Keys = "keys";
	public static final String MPM_IsAnswer = "isAnswer";
	public static final String MPM_Option = "option";
	public static final String MPM_Score = "score";

	@SuppressWarnings({ "rawtypes" })
	public static Map<String, Object> setDictionaryMessageForForm(
			Map<String, Object> map, BaseDAO dao) throws ServiceException {
		if (map == null) {
			return null;
		}
		Map<String, Object> data = new HashMap<String, Object>();
		Set set = map.entrySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Entry b = (Entry) it.next();
			String fieldName = (String) b.getKey();
			String fieldValue = (String) b.getValue();
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "fieldId", "s",
					fieldName);
			try {
				Map<String, Object> fieldAttribute = dao.doLoad(cnd,
						BSCHISEntryNames.MPM_FieldMaintain);
				if (fieldAttribute != null) {
					String dicRender = (String) fieldAttribute.get("dicRender");
					if ("1".equals(dicRender)) {
						String fieldId = (String) fieldAttribute.get("fieldId");
						List<?> cnd1 = CNDHelper.createSimpleCnd("eq",
								"fieldId", "s", fieldId);
						List<Map<String, Object>> dicFields = dao.doList(cnd1,
								null, BSCHISEntryNames.MPM_DictionaryMaintain);
						for (Map<String, Object> dicField : dicFields) {
							String dicValue = (String) dicField.get("keys");
							if (dicValue.equals(fieldValue)) {
								Map<String, Object> dicData = new HashMap<String, Object>();
								dicData.put("key", fieldValue);
								dicData.put("text", dicField.get("text"));
								dicData.put("alias",
										fieldAttribute.get("alias"));
								data.put(fieldName, dicData);
								break;
							}
						}
					} else if ("2".equals(dicRender)) {
						String fieldId = (String) fieldAttribute.get("fieldId");
						List<?> cnd1 = CNDHelper.createSimpleCnd("eq",
								"fieldId", "s", fieldId);
						List<Map<String, Object>> dicFields = dao.doList(cnd1,
								null, BSCHISEntryNames.MPM_DictionaryMaintain);
						Map<String, Object> dicData = new HashMap<String, Object>();
						for (Map<String, Object> dicField : dicFields) {
							String dicValue = (String) dicField.get("keys");
							if (fieldValue != null
									&& fieldValue.indexOf(dicValue) != -1) {
								if (dicData.get("key") == null) {
									dicData.put("key", fieldValue);
								}
								if (dicData.get("text") == null) {
									dicData.put("text", dicField.get("text"));
								} else {
									dicData.put("text", dicData.get("text")
											+ ";" + dicField.get("text"));
								}
								if (dicData.get("alias") == null) {
									dicData.put("alias",
											fieldAttribute.get("alias"));
								}
							}
						}
						data.put(fieldName, dicData);
					} else {
						Map<String, Object> genData = new HashMap<String, Object>();
						genData.put(fieldName, fieldValue);
						genData.put("alias", fieldAttribute.get("alias"));
						genData.put("text", fieldValue);
						data.put(fieldName, genData);
					}
				}
			} catch (PersistentDataOperationException e) {
				throw new ServiceException("获取字段属性信息失败", e);
			}
		}
		return data;
	}

	public static List<Map<String, Object>> setDictionaryMessageForForm(
			List<Map<String, Object>> list, BaseDAO dao)
			throws ServiceException {
		List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> r : list) {
			Map<String, Object> record = setDictionaryMessageForForm(r, dao);
			if (record != null) {
				records.add(record);
			}
		}
		return records;
	}

	@SuppressWarnings("rawtypes")
	public static Map<String, Object> setDictionaryMessageForList(
			Map<String, Object> map, BaseDAO dao) throws ServiceException {
		if (map == null) {
			return null;
		}
		Map<String, Object> data = new HashMap<String, Object>();
		Set set = map.entrySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Entry b = (Entry) it.next();
			String fieldName = (String) b.getKey();
			String fieldValue = (String) b.getValue();
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "fieldId", "s",
					fieldName);
			try {
				Map<String, Object> fieldAttribute = dao.doLoad(cnd,
						BSCHISEntryNames.MPM_FieldMaintain);
				if (fieldAttribute != null) {
					String dicRender = (String) fieldAttribute.get("dicRender");
					if (!"0".equals(dicRender)) {
						String fieldId = (String) fieldAttribute.get("fieldId");
						List<?> cnd1 = CNDHelper.createSimpleCnd("eq",
								"fieldId", "s", fieldId);
						List<Map<String, Object>> dicFields = dao.doList(cnd1,
								null, BSCHISEntryNames.MPM_DictionaryMaintain);
						for (Map<String, Object> dicField : dicFields) {
							String dicValue = (String) dicField.get("keys");
							if (dicValue.equals(fieldValue)) {
								data.put(fieldName, fieldValue);
								data.put(fieldName + "_text",
										dicField.get("text"));
								break;
							}
						}
					} else {
						data.put(fieldName, fieldValue);
					}
				}
			} catch (PersistentDataOperationException e) {
				throw new ServiceException("获取字段属性信息失败", e);
			}
		}
		return data;
	}

	public static List<Map<String, Object>> setDictionaryMessageForList(
			List<Map<String, Object>> list, BaseDAO dao)
			throws ServiceException {
		List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> r : list) {
			Map<String, Object> record = setDictionaryMessageForList(r, dao);
			if (record != null) {
				records.add(record);
			}
		}
		return records;
	}

	/**
	 * 具体某一个服务记录模板所有字段解析
	 * 
	 * @param map
	 * @param dao
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings({ "unchecked" })
	public static List<Map<String, Object>> setDictionaryOnePlate(
			Map<String, Object> map, BaseDAO dao) throws ServiceException {
		if (map == null || map.size() == 0) {
			return null;
		}
		List<Map<String, Object>> body = (List<Map<String, Object>>) map
				.get("body");
		if (body == null || body.size() == 0) {
			return null;
		}
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> filelds = null;
		for (Map<String, Object> fieldAttribute : body) {
			String dicRender = (String) fieldAttribute.get("dicRender");
			if ("0".equals(dicRender)) {
				data.add(fieldAttribute);
			} else if ("1".equals(dicRender)) {
				filelds = doListFilelds(fieldAttribute, dao);
				data.add(recombinationField(filelds, fieldAttribute, dicRender));
			} else if ("2".equals(dicRender)) {
				filelds = doListFilelds(fieldAttribute, dao);
				data.add(recombinationField(filelds, fieldAttribute, dicRender));
			}

		}
		return data;
	}

	/**
	 * 具体某一个健康问卷模板所有字段解析
	 * 
	 * @param map
	 * @param dao
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings({ "unchecked" })
	public static List<Map<String, Object>> setDictionarySurveryOnePlate(
			Map<String, Object> map, BaseDAO dao) throws ServiceException {
		if (map == null || map.size() == 0) {
			return null;
		}
		List<Map<String, Object>> body = (List<Map<String, Object>>) map
				.get("body");
		if (body == null || body.size() == 0) {
			return null;
		}
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> filelds = null;
		for (Map<String, Object> fieldAttribute : body) {
			String dicRender = (String) fieldAttribute.get("dicRender");
			if ("0".equals(dicRender)) {
				data.add(fieldAttribute);
			} else if ("1".equals(dicRender)) {
				filelds = doListFilelds(fieldAttribute, dao);
				data.add(recombinationSurveyField(filelds, fieldAttribute,
						dicRender));
			} else if ("2".equals(dicRender)) {
				filelds = doListFilelds(fieldAttribute, dao);
				data.add(recombinationSurveyField(filelds, fieldAttribute,
						dicRender));
			}

		}
		return data;
	}

	/**
	 * 根据fieldId查询某一个字段的全部信息
	 * 
	 * @param fieldAttribute
	 * @param dao
	 * @return
	 * @throws ServiceException
	 */
	private static List<Map<String, Object>> doListFilelds(
			Map<String, Object> fieldAttribute, BaseDAO dao)
			throws ServiceException {
		String fieldId = (String) fieldAttribute.get("fieldId");
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "fieldId", "s", fieldId);
		List<Map<String, Object>> dicFields = null;
		try {
			dicFields = dao.doList(cnd1, null, BSCHISEntryNames.MPM_DictionaryMaintain);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException("获取字段属性信息失败", e);
		}
		return dicFields;
	}

	/**
	 * 重组某一个字典(服务记录),满足Android端需求
	 * 
	 * @param filelds
	 * @param fieldAttribute
	 * @return
	 */
	private static Map<String, Object> recombinationField(
			List<Map<String, Object>> filelds,
			Map<String, Object> fieldAttribute, String dicRender) {
		Map<String, Object> rFilelds = new HashMap<String, Object>();
		Map<String, Object> rFieldDic = new HashMap<String, Object>();
		List<Map<String, Object>> dic = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> data : filelds) {
			Object text = data.get(MPM_Text);
			Object keys = data.get(MPM_Keys);
			if (text != null && keys != null) {
				rFieldDic.put(text.toString(), data.get(MPM_Keys));
				rFilelds.put(keys.toString(), data);
			}
		}
		dic.add(rFieldDic);
		rFilelds.putAll(fieldAttribute);
		rFilelds.put(MPM_Dic, dic);
		return rFilelds;
	}

	/**
	 * 重组某一个字典(健康问卷),满足Android端需求
	 * 
	 * @param filelds
	 * @param fieldAttribute
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map<String, Object> recombinationSurveyField(
			List<Map<String, Object>> filelds,
			Map<String, Object> fieldAttribute, String dicRender) {
		Map<String, Object> rFilelds = new HashMap<String, Object>();

		List dic = new ArrayList();
		for (Map<String, Object> data : filelds) {
			Map<String, Object> fileld = new HashMap<String, Object>();
			Object text = data.get(MPM_Text);
			Object keys = data.get(MPM_Keys);
			Object isAnswer = data.get(MPM_IsAnswer);
			Object score = data.get(MPM_Score);
			if (text != null && keys != null) {
				fileld.put(MPM_Option, keys.toString() + text.toString());
				fileld.put(MPM_IsAnswer, isAnswer);
				fileld.put(MPM_Keys, keys.toString());
				fileld.put(MPM_Score, score);
				dic.add(fileld);
			}
		}
		rFilelds.putAll(fieldAttribute);
		rFilelds.put(MPM_Dic, dic);
		return rFilelds;
	}
}
