/**
 * @(#)UserDictionary.java Created on 2012-5-28 上午10:23:34
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.dictionary.SliceTypes;
import ctd.dictionary.support.TableDictionary;

/**
 * @description
 * 
 * @author <a href="mailto:tianj@bsoft.com.cn">田军</a>
 */
public class UserDictionary extends TableDictionary {
	private static final long serialVersionUID = 7169815142767886249L;
	public static String PREFIX = "[";
	public static String SUFFIX = "]";
	public static String DICTIONARYID = "manageUnit";
	public static String SEARCHFIELD = "b.manaUnitId";
	public static String DISPLAYEDFIELD = "display";
	private Dictionary navDic;
	protected static final String SLICE_BY_COMPOSE = "$sliceByCompose";

	public List<DictionaryItem> getSlice(String parentKey, int sliceType,
			String query, int start, int limit) {
		if (!StringUtils.isEmpty(query)
				|| sliceType == SliceTypes.ALL_LEAF) {
			List<DictionaryItem> list = super.getSlice(parentKey, sliceType,query);
			Dictionary dic = null;
			try {
				dic = DictionaryController.instance().get(DICTIONARYID);
			} catch (ControllerException e) {
				e.printStackTrace();
			}
			List<DictionaryItem> processedList = new ArrayList<DictionaryItem>();
			for (DictionaryItem map : list) {
				String manageUnitKey = (String) map.getProperty(SEARCHFIELD);
				String manageUnitText = dic.getText(manageUnitKey);
				String key = map.getKey();
				String text = map.getText();
				String processedText = new StringBuilder(text).append(PREFIX)
						.append(manageUnitText).append(SUFFIX).toString();
				DictionaryItem dictionaryItem = new DictionaryItem(key,
						processedText);
				HashMap<String, Object> props = map.getProperties();
				Set<String> nms = props.keySet();
				for (String nm : nms) {
					if (nm.equals("text")) {
						continue;
					}
					String value = (String) props.get(nm);
					dictionaryItem.setProperty(nm, value);
				}
				dictionaryItem.setProperty(DISPLAYEDFIELD, text);
				processedList.add(dictionaryItem);
			}
			return processedList;
		}
		List<DictionaryItem> ls = getNavSlice(parentKey, sliceType, query);
		if (!StringUtils.isEmpty(parentKey)) {
			String qs = "=" + parentKey;
			ls.addAll(this.getSlice(null, sliceType, qs, start, limit));
			for (int i = 0; i < ls.size() - 1; i++) {
				for (int j = ls.size() - 1; j > i; j--) {
					if (ls.get(j).getKey().equals(ls.get(i).getKey())) {
						ls.remove(j);
					}
				}
			}
		}
		return ls;
	}
	
	protected List<DictionaryItem> getNavSlice(String parentKey, int sliceType,String query){
		List<DictionaryItem> items = navDic.getSlice(parentKey, sliceType, query);
		Iterator<DictionaryItem> it = items.iterator();
		while(it.hasNext()){
			DictionaryItem item = it.next();
			String navMaxLength = (String) getProperty("navMaxLength");
			if(!StringUtils.isEmpty(navMaxLength)){
				Integer max = Integer.valueOf(navMaxLength);
				if(item.getKey().length() > max){
					it.remove();
					continue;
				}
			}
			item.setProperty("folder", "true");
		}
		return items;
	}


}
