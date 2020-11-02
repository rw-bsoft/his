/**
 * @(#)CategorySaveModel.java Created on 2013-3-15 上午09:19:09
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.cvd;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BaseDAO;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.SendDictionaryReloadSynMsg;

import com.bsoft.mpi.model.ModelDataOperationException;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class CategorySave extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(CategorySave.class);

	@SuppressWarnings("unchecked")
	protected void doSaveCategory(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		CategorySaveModel csModel = new CategorySaveModel(dao);
		try {
		Map<String,Object> data = csModel.saveCategory(op, body);
		res.put("body", data);
		} catch (ModelDataOperationException e) {
			logger.error("save category is fail.", e);
			throw new ServiceException(e);
		}
		DictionaryController.instance().reload("chis.dictionary.category");
		SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.category");
	}

}
