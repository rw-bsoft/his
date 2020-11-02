/**
 * @(#)PelpleHealthTeachService.java Created on 2015-3-6 下午4:41:36
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.pub;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class PelpleHealthTeachService extends AbstractActionService implements
		DAOSupportable {
	public static Logger logger = LoggerFactory
			.getLogger(PelpleHealthTeachService.class);

	public void doSaveSelectRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PelpleHealthTeachModel pht = new PelpleHealthTeachModel(dao);
		Map<String, Object> diagnoses = (Map<String, Object>) req
				.get("records");
		String recordId = (String) req.get("recordId");
		try {
			for (Iterator it = diagnoses.keySet().iterator(); it.hasNext();) {
				Object key = (Object) it.next();
				Map<String, Object> diagnose=(Map<String, Object>) diagnoses.get(key);
				pht.saveSelectRecords(diagnose);
				
			}
		} catch (ModelDataOperationException e) {
			logger.error("save Select Records failed.", e);
			throw new ServiceException(e);
		}
	}

	public void doClearSelectRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PelpleHealthTeachModel pht = new PelpleHealthTeachModel(dao);
		List<Map<String, Object>> diagnoses = (List<Map<String, Object>>) req
				.get("diagnoses");
		String recordId = (String) req.get("recordId");
		try {
			pht.clearSelectRecords(recordId);
		} catch (ModelDataOperationException e) {
			logger.error("save Select Records failed.", e);
			throw new ServiceException(e);
		}
	}
}
