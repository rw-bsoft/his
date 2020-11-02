/**
 * @(#)HealthRecipelManage.java Created on 2014-9-4 下午4:13:25
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.print.instance;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.PersistentDataOperationException;
import chis.source.her.HealthRecipelManageService;
import chis.source.print.base.BSCHISPrint;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.JSONUtils;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class HealthRecipelManage extends BSCHISPrint implements IHandler {

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {

	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		getDao(ctx);
		Map<String, Object> rCtx = HealthRecipelManageService.rCtx;
		String strConfig = (String) request.get("body");
		if (strConfig != null && rCtx.size() > 0) {
			try {
				strConfig = URLDecoder.decode(strConfig, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			HashMap<String, Object> body = JSONUtils.parse(strConfig,
					HashMap.class);
			if (body != null && body.size() > 0) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				for (Iterator it = body.keySet().iterator(); it.hasNext();) {
					String key = it.next() + "";
					String recordId = (String) body.get(key);
					list.add((Map<String, Object>) rCtx.get(recordId));
				}
				records.addAll(list);
			}
		} else {
			String id = (String) request.get("id");
			String guideWay = (String) request.get("guideWay");
			String wayId = (String) request.get("wayId");
			String cnd = "";
			if (id != null && !"".equals(id)) {
				cnd = "['eq',['$','a.id'],['s','" + id + "']]";
			} else {
				cnd = "['and',['eq',['$','a.wayId'],['s','" + wayId
						+ "']],['eq',['$','a.guideWay'],['s','" + guideWay
						+ "']]]";
			}
			String schemaId = BSCHISEntryNames.HER_HealthRecipeRecord;
			if ("06".equals(guideWay)) {
				schemaId = BSCHISEntryNames.HER_HealthRecipeRecord_JHZX;
			}
			List<Map<String, Object>> list = null;
			try {
				list = dao.doQuery(toListCnd(cnd), null, schemaId);
			} catch (PersistentDataOperationException e1) {
				e1.printStackTrace();
			}
			if (list != null && list.size() > 0) {
				records.addAll(list);
			}
		}
	}
}
