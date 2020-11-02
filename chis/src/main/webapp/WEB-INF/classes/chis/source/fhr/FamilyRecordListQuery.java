package chis.source.fhr;

import java.util.List;
import java.util.Map;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleQuery;
import ctd.util.context.Context;

public class FamilyRecordListQuery extends SimpleQuery {
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		
		super.execute(req, res, ctx);
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> body =(List<Map<String,Object>>) res.get("body");
		for(int i=0;i<body.size();i++){
			Map<String,Object> rec =  body.get(i);
			rec.put("ownerName_text", rec.get("ownerName"));
		}
		
	}
}
