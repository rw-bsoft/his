package platform.security.source;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import ctd.monitor.MonitorInfoUtil;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class SqlAndSerInfo implements Service{
	private void getSql(Map<String, Object> req, Map<String, Object> res,Context ctx) {
		List<Map<String, String>> body = new ArrayList<Map<String,String>>();
		List<Map<String, Object>> getSql=MonitorInfoUtil.getSQLInfo();
		for(Map<String, Object> m:getSql){
			Map<String, String> o=new HashMap<String, String>();
			o.put("ExecuteCount",m.get("ExecuteCount").toString());
			o.put("timespan", m.get("MaxTimespan").toString());
			o.put("content","SQL:"+ m.get("SQL").toString());
			body.add(o);
		}
		res.put("body", body);
	}
	private void getService(Map<String, Object> req, Map<String, Object> res,Context ctx){
		List<Map<String, Object>> serviceInfo=MonitorInfoUtil.getServiceInfo();
		res.put("body", serviceInfo);
	}
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		String op = (String) req.get("op");
		if(StringUtils.isEmpty(op)){
			res.put("body", "");
			return;
		}
		if("getSql".equals(op)){
			getSql(req, res, ctx);
		}
		if("getService".equals(op)){
			getService(req, res, ctx);
		}
	}

	
}
