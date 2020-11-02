package phis.source.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PHISExpSymbols;
import phis.source.PersistentDataOperationException;
import phis.source.utils.CNDHelper;


import ctd.util.context.Context;

public class CharacterEncodingController implements BSPHISEntryNames {
	private static final Logger logger = LoggerFactory
			.getLogger(CharacterEncodingController.class);
	private static final int MAX_LENGTH = 10;
	private static HashMap<Object, Map<String, Object>> CEHM = new HashMap<Object, Map<String, Object>>();// 对应的汉字编码表

	public static String getCode(String s, String type, Context ctx) {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isEmpty(s)) {
			return "";
		}
		s = s.replaceAll("[^a-zA-Z0-9\u4E00-\u9FA5]", "");
		char[] array = s.toCharArray();
		for (int i = 0; i < array.length; i++) {
			if (sb.length() >= MAX_LENGTH)
				break;
			char c = array[i];
			if (c <= 128) {
				sb.append(c);
				continue;
			}
			sb.append(getCEHM(c, type, ctx));
		}
		return sb.length() > MAX_LENGTH ? sb.substring(0, MAX_LENGTH)
				.toUpperCase() : sb.toString().toUpperCase();
	}

	private static String getCEHM(char c, String type, Context ctx) {
		BaseDAO dao = new BaseDAO();
		if (!CEHM.containsKey(c)) {
			try {
				Map<String, Object> hm = null;
				List<Map<String, Object>> list = dao.doList(
						CNDHelper.createSimpleCnd("eq", "hzCode", "s", c),
						"CODEID", PUB_CharacterEncoding);
				if (list.size() > 0) {
					hm = list.get(0);
				} else {
					return "";
				}
				CEHM.put(c, hm);
			} catch (PersistentDataOperationException e) {
				logger.error(
						"failed to get CharacterEncodingController message.", e);
			}
		}
		String ch = CEHM.get(c).get(type).toString();
		if (type.equals("pyCode") ||type.equals(PHISExpSymbols.WB) || type.equals(PHISExpSymbols.BH)) {
			return ch.substring(0, 1).toUpperCase();// 五笔码或笔画码取对应汉字编码的前一位
		} else {
			return ch.substring(0, 2);// 其他码取对应汉字其他码的前两位
		}
	}
}
