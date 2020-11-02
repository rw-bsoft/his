/**
 * @(#)BizSimpleRemove.java Created on 2012-1-16 下午04:15:28
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleRemove;
import ctd.util.context.Context;

/**
 * @description 替代框架的SimpleRemove
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class BizSimpleRemove extends SimpleRemove {

	Logger log = LoggerFactory.getLogger(BizSimpleRemove.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see ctd.service.core.Service#execute(java.util.Map, java.util.Map,
	 * ctd.util.context.Context)
	 */
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		// BaseDAO dao = new BaseDAO();
		// String pk=(String)req.get("pkey");
		// String entryName =(String) req.get("entryName");
		// try {
		// dao.doRemove(pk, entryName);
		// } catch (PersistentDataOperationException e) {
		// log.error("save failed for schema ",req.get("entryName"));
		// e.printStackTrace();
		// throw new ServiceException("数据保存失败.",e);
		// }
		@SuppressWarnings("unchecked")
		String pkey = (String) ((Map<String,Object>) req.get("body")).get("pkey");
		req.put("pkey", pkey);
		super.execute(req, res, ctx);
	}

}
