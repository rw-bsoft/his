/**

 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.sto.source;

import java.text.ParseException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description 医生排班维护
 * 
 * @author shiwy 2012.08.28
 */
public class EssentialGraphService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(EssentialGraphService.class);

	/**
	 * 挂号分类统计
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ParseException
	 * @throws ValidateException
	 */
	public void doQueryEssentialGraph(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ParseException,
			ValidateException {
		EssentialGraphModule egm = new EssentialGraphModule(dao);
		egm.doQueryEssentialGraph(req, res, ctx);
	}
}
