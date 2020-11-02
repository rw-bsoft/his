package chis.source.util;

import java.util.List;

import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryItem;

public class AreaGridDicUtil {

	public static final String DIC = "areaGrid";
	private static final Logger logger = LoggerFactory
			.getLogger(AreaGridDicUtil.class);
	private static byte[] lock = new byte[0];

	/**
	 * 网格地址字典中增加一字典项
	 * 
	 * @param key
	 *            字典键值
	 * @param text
	 *            文本值
	 * @param parent
	 *            父节点
	 * @param isBottom
	 *            是否最底层
	 * @param isFamily
	 *            是否为户
	 * @param isFloder
	 *            是否文件夹
	 * @throws ControllerException 
	 */
	public static void insert(String key, String text, String parent,
			String isBottom, String isFamily, Boolean isFloder) throws ControllerException {
		Dictionary areaGrid = DictionaryController.instance().get(DIC);
		DictionaryItem it = new DictionaryItem(key, text);
		it.setProperty("parent", parent);
		it.setProperty("isBottom", isBottom);
		it.setProperty("isFamily", isFamily);
		if (isFloder) {
			it.setProperty("folder", "true");
		}
		synchronized (lock) {
			DictionaryItem item = new DictionaryItem(key, text);
			areaGrid.addItem(item);
		}
		logger.info("add areagrid item  [" + key + " " + text + "]");
	}

	public static void update(DictionaryItem item, String newText,
			String isBottom, String isFamily, Boolean isFolder) throws ControllerException {
		Dictionary areaGrid = DictionaryController.instance().get(DIC);
		DictionaryItem newItem = null;
		if (newText == null || newText.equals("")) {
			newText = item.getText();
		}
		newItem = new DictionaryItem(item.getKey(), newText);
		newItem.setProperty("mapSign", item.getProperty("mapSign"));
		newItem.setProperty("parent", item.getProperty("parent"));
		if (isBottom == null || isBottom.equals("")) {
			isBottom = (String) item.getProperty("isBottom");
		}
		newItem.setProperty("isBottom", isBottom);
		if (isFamily == null || isFamily.equals("")) {
			isFamily = (String) item.getProperty("isFamily");
		}
		newItem.setProperty("isFamily", isFamily);
		if (isFolder == null) {
			newItem.setProperty("folder", item.getProperty("folder"));
		} else {
			if (isFolder) {
				newItem.setProperty("folder", "true");
			}
		}
		synchronized (lock) {
			areaGrid.removeItem(item.getKey());
			areaGrid.addItem(newItem);
		}
		logger.info("update areagrid item   [" + item.getKey() + " " + newText
				+ "]");
	}

	/**
	 * 修改网格地址中的某个字典项
	 * 
	 * @param key
	 *            字典键值
	 * @param text
	 *            文本值
	 * @param isBottom
	 *            是否最底层
	 * @param isFamily
	 *            是否为户
	 * @param isFloder
	 *            是否文件夹
	 * @throws ControllerException 
	 */
	public static void update(String key, String text, String isBottom,
			String isFamily, Boolean isFolder) throws ControllerException {
		if (key == null || key.equals("")) {
			logger.error("key : " + key + " is null .");
			return;
		}
		Dictionary areaGird = DictionaryController.instance().get(DIC);
		DictionaryItem it = areaGird.getItem(key);
		if (it == null) {
			logger.error("can not found areagrid id " + key + " from ram .");
			return;
		}
		update(it, text, isBottom, isFamily, isFolder);
	}

	public static void delete(List<String> keyList) throws ControllerException {
		Dictionary areaGrid = DictionaryController.instance().get(DIC);
		synchronized (lock) {
			for (int i = 0; i < keyList.size(); i++) {
				areaGrid.removeItem(keyList.get(i));
				logger.info("delete areagrid item   [" + keyList.get(i) + "]");
			}
		}
	}
}
