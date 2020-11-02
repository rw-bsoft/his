/**
 * @(#)BizSimpleSave.java Created on 2012-1-16 下午04:15:50
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleSave;
import ctd.util.context.Context;

/**
 * @description 替代框架的SimpleSave
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class BizSimpleSave extends SimpleSave {

	Logger log = LoggerFactory.getLogger(BizSimpleSave.class);
	/* (non-Javadoc)
	 * @see ctd.service.core.Service#execute(java.util.Map, java.util.Map, ctd.util.context.Context)
	 */
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
//		BaseDAO dao = new BaseDAO();
//		try {
//			dao.doSave(req, res);
//		} catch (PersistentDataOperationException e) {
//			log.error("save failed for schema ",req.get("entryName"));
//			e.printStackTrace();
//			throw new ServiceException("数据保存失败.",e);
//		}
		super.execute(req, res, ctx);
	}
}
