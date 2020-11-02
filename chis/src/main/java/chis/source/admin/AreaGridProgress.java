/**
 * @(#)AreaGridProgress.java Created on 2012-6-7 上午10:51:38
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.admin;

import java.util.HashMap;
import java.util.Map;

import chis.source.BaseDAO;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class AreaGridProgress extends AbstractActionService implements
		DAOSupportable {
	protected void doGetProgress(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> resBody = new HashMap<String, Object>();
		resBody.put("progress", AreaGridExcelFileUpload.getProgress());
		res.put("body", resBody);
	}
}
