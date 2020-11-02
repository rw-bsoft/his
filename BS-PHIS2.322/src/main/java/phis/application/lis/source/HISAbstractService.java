package phis.application.lis.source;

import java.util.List;
import java.util.Map;

import phis.application.lis.source.rpc.HISService;

public abstract class HISAbstractService implements HISService{
	protected boolean checkData(Map<String, Object> args, Map<String, Object> res)
	   {
	     List<String> list = getNotNullFields();
	     for (String key : list) {
	       if (args.get(key) == null) {
	         res.put("x-response-code", Integer.valueOf(4000));
	         res.put("x-response-msg", "Request [" + key + 
	           "] can't be empty");
	         return false;
	       }
	     }
	     return true;
	   }
	 
	   protected List<String> getNotNullFields() {
	     return null;
	   }
}
