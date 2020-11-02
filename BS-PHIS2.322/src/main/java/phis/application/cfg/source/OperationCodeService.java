/**
 * @(#)OperationCodeService.java Created on 2020-9-21 中午11:10:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.cfg.source;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import java.util.List;
import java.util.Map;

/**
 * @description 手术内码维护Service
 * 
 * @author yanghe 2020.09.21
 */
public class OperationCodeService extends AbstractActionService implements
		DAOSupportable {

	/**
	 * @author yanghe
	 * @createDate 2020/9/21
	 * @description 手术内码导入功能
	 * @updateInfo
	 *
	 */
	public void doSaveSsnm(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException, ExpException {
		List<Map<String,Object>> body = (List<Map<String,Object>>) req.get("body");
		try {
			OperationCodeModel model=new OperationCodeModel(dao);
			model.saveSsnm(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
}
