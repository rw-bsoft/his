package platform.dataset.source;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;


import ctd.service.core.ServiceException;
import ctd.service.dao.DBService;
import ctd.util.S;
import ctd.util.context.Context;

public class SearchSet extends DBService{
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		String catalog=S.obj2String(req.get("catalog"));
		Session ss=(Session) ctx.get(Context.DB_SESSION);
		String path=S.obj2String(req.get("setName"));
		if(!StringUtils.isEmpty(catalog)){
			path=findParentCatalog(catalog, ss, path);
		}
		res.put("path", path);
	}
	private String findParentCatalog(String catalog,Session ss,String path){
			Query q=ss.createQuery("from RES_DataSetCatalog where CataLogId=?");
			q.setString(0, catalog);
			List<Map<String,Object>> li=q.list();
			if(li.size()>0){
				path=li.get(0).get("CatalogName")+"/"+path;
				String parentId=S.obj2String(li.get(0).get("ParentCatalogId"));
				if(!StringUtils.isEmpty(parentId)){
					path=findParentCatalog(parentId, ss, path);
				}
			}
			return path;
	}
}
