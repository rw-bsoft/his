package phis.prints.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import phis.application.ivc.source.JacksonUtil;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.service.configure.SchemaConfig;
import ctd.util.context.Context;

public class HospitalPaymentQueriesFile implements IHandler {
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		Map<String, Object> config = new HashMap<String, Object>();
//		String sql = null;
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", jgid);
		if (request.get("config") != null) {
			try {
				config = (Map<String, Object>) JacksonUtil
						.jsonToBean(request.get("config")+"", HashMap.class);
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			config = request.get("config")+"";
		}
		Map<String,Object> requestData = (Map<String,Object>)config.get("requestData");
		Map<String,Object> res = new HashMap<String,Object>();
		List<Map<String,Object>> reList = new ArrayList<Map<String,Object>>();
		try {
			if("single".equals(config.get("page")+"")){
				res = dao.doList((List<Object>)requestData.get("cnd"), null, requestData.get("schema")+"", Integer.parseInt(requestData.get("pageNo")+""), Integer.parseInt(requestData.get("pageSize")+""), null);
				reList = (List<Map<String,Object>>)res.get("body");
			}else{
				reList = dao.doList((List<Object>)requestData.get("cnd"), null, requestData.get("schema")+"");
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Map<String, Object> map : reList) {
			map.put("JKRQ",map.get("JKRQ").toString().substring(0,19));
		}
		SchemaUtil.setDictionaryMassageForList(reList, requestData.get("schema")+"");
		records.addAll(reList);
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnitName();
		response.put("title", jgname);
	}
}
