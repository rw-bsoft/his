/**
 * @(#)UserDataBoxService.java Created on 2013-5-13 下午2:33:50
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.emr.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.CNDHelper;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class UserDataBoxService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory.getLogger(UserDataBoxService.class);

	@SuppressWarnings("unchecked")
	public void doListUserDataByCnd(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		List<Object> cnd = (List<Object>) req.get("cnd");
		List<Object> queryCnd = (List<Object>) req.get("queryCnd");
		if (queryCnd != null) {
			cnd = CNDHelper.createArrayCnd("and", cnd, queryCnd);
		}
		String queryCndsType = null;
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String sortInfo = "KSSJ";
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 1;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		UserDataBoxModel udb = new UserDataBoxModel(dao);
		Map<String, Object> result = udb.listUserDataByCnd(cnd, queryCndsType,
				sortInfo, pageSize, pageNo);
		res.putAll(result);
	}

	public void doGetTemporaryData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		List<Object> cnd = (List<Object>) req.get("cnd");
		UserDataBoxModel udb = new UserDataBoxModel(dao);
		List<Map<String, Object>> body = udb.getTemporaryData(cnd);
		if (body != null
				&& (cnd == null || (cnd != null && cnd.get(2) != null && ((List<Object>) cnd
						.get(2)).get(1).equals("特殊符号")))) {
			Map<String, Object> date1 = new HashMap<String, Object>();
			date1.put("XMMC", "特殊符号");
			date1.put("XMQZ", "日期1");
			body.add(date1);
			Map<String, Object> date2 = new HashMap<String, Object>();
			date2.put("XMMC", "特殊符号");
			date2.put("XMQZ", "日期2");
			body.add(date2);
			Map<String, Object> date3 = new HashMap<String, Object>();
			date3.put("XMMC", "特殊符号");
			date3.put("XMQZ", "日期3");
			body.add(date3);
			Map<String, Object> date4 = new HashMap<String, Object>();
			date4.put("XMMC", "特殊符号");
			date4.put("XMQZ", "日期4");
			body.add(date4);
			Map<String, Object> userName = new HashMap<String, Object>();
			userName.put("XMMC", "特殊符号");
			userName.put("XMQZ", "用户");
			body.add(userName);
		}
		res.put("body", body);
	}

	public void doListUserNormalDic(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		HashMap<String, DictionaryItem> json = new HashMap<String, DictionaryItem>();
		try {
			json = DictionaryController.instance()
					.get("platform.reg.dictionary.dicConfig").getItems();
		} catch (ControllerException e) {
			throw new ModelDataOperationException("获取字典失败", e);
		}
		// List<Map<String, Object>> itemList = (List<Map<String, Object>>) json
		// .get("items");
		List<Map<String, Object>> body = new ArrayList<Map<String, Object>>();
		for (String key : json.keySet()) {
			Map<String, Object> map = new HashMap<String, Object>();
			DictionaryItem data = json.get(key);
			if ("phis".equals(data.getProperty("domain"))
					&& !data.getKey().equals("phis")) {
				map.put("ZDFL", data.getText());
				map.put("BZBM", data.getProperty("codeRule"));
				map.put("ZDID", data.getKey());
				body.add(map);
			}
		}
		res.put("body", body);
		res.put("totalCount", body.size());
	}

	public void doListUserDicItem(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		String ZDID = (String) req.get("ZDID");
		Dictionary dic = null;
		try {
			dic = DictionaryController.instance().get(ZDID);
		} catch (ControllerException e) {
			throw new ModelDataOperationException("获取字典出错", e);
		}
		List<Map<String, Object>> body = new ArrayList<Map<String, Object>>();
		HashMap<String, DictionaryItem> ls = dic.getItems();
		for (String dicId : ls.keySet()) {
			Map<String, Object> map = new HashMap<String, Object>();
			DictionaryItem data = ls.get(dicId);
			map.put("XMMC", data.getText());
			map.put("BZBM", data.getKey());
			body.add(map);
		}
		res.put("body", body);
		res.put("totalCount", body.size());
	}
	
	/**
	 * 获取辅助检查列表
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doGetFzjcList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
					throws ModelDataOperationException {
		UserDataBoxModel udb = new UserDataBoxModel(dao);
		List<Map<String, Object>> result = udb.doGetFzjcList(req,res, dao, ctx);
		res.put("body", result);
	}
}
