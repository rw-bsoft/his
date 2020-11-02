/**
 * @(#)ChineseMedicineManage.java Created on 2014-6-26 上午10:24:44
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.print.instance;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.print.base.BSCHISPrint;

import ctd.controller.exception.ControllerException;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleQuery;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class ChineseMedicineManage extends BSCHISPrint implements IHandler {
	private String empiId;
	private String phrId;
	private String id;
	private Context ctx;

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		empiId = (String) request.get("empiId");
		phrId = (String) request.get("phrId");
		this.ctx = ctx;
		id = (String) request.get("id");
		Map<String, Object> personInfo = null;
		try {
			personInfo = this.getFirstRecord(this.getBaseInfo(
					MPI_DemographicInfo, request, ctx));
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		response.put("personName", personInfo.get("personName"));
		response.put("phrId", phrId);
		Map<String, Object> jsonReq = new HashMap<String, Object>();
		Map<String, Object> res = new HashMap<String, Object>();
		jsonReq.put("schema", "chis.application.ohr.schemas.MDC_ChineseMedicineManage");
		String cnd = "['eq',['$','id'],['s','" + id + "']]";
		jsonReq.put("cnd", toListCnd(cnd));
		try {
			new SimpleQuery().execute(jsonReq, res, ctx);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) res
				.get("body");
		Map<String, Object> manageInfo = this.getFirstRecord(list);
		Schema schema = null;
		try {
			schema = SchemaController.instance().get(
					"chis.application.ohr.schemas.MDC_ChineseMedicineManage");
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.putAll(manageInfo);
		List<SchemaItem> items = schema.getItems();
		for (SchemaItem it : items) {
			String itemId = it.getId();
			if (manageInfo.get(itemId) == null) {
				response.put(itemId, "");
				continue;
			}
			String boxType = it.getProperty("boxType")+"";
			if ("radio".equals(boxType)||"radio2".equals(boxType)) {
				String itemValue = (String) manageInfo.get(itemId);
				response.put(itemId + itemValue, "√");
			} else if ("date".equals(boxType)) {
				Date itemValue = (Date) manageInfo.get(itemId);
				response.put(itemId, new SimpleDateFormat("yyyy-MM-dd").format(itemValue));
			} else if ("check".equals(boxType)) {
				String itemValue = (String) manageInfo.get(itemId);
				if (itemValue != null && itemValue.length() > 0) {
					String[] values = itemValue.split(",");
					for (int i = 0; i < values.length; i++) {
						String val = values[i];
						response.put(itemId + val, "√");
					}
				}
			} else {
				response.put(itemId, manageInfo.get(itemId));
			}
		}
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}

}
