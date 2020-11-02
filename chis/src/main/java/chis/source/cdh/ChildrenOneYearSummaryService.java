/**
 * @(#)ChildrenOneYearSummaryService.java Created on 2012-2-6 下午3:50:48
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */

package chis.source.cdh;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.VisitPlanModel;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description 儿童三周岁小结
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */

public class ChildrenOneYearSummaryService extends AbstractActionService
		implements DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(ChildrenOneYearSummaryService.class);

	/**
	 * 初始化三周岁小结信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ValidateException
	 */
	@SuppressWarnings({ "unchecked" })
	protected void doInitChildrenOneYearSummaryRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ValidateException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		try {
			Map<String, Object> data = chm.getManageChildHealthCardByEmpiId(empiId);
			if (data == null || data.size() < 1) {
				return;
			}
			Map<String, Object> resBody = new HashMap<String, Object>();
			resBody.put("phrId", data.get("phrId"));
			resBody.put("manaUnitId", data.get("manaUnitId"));
			resBody.put("summaryDate", BSCHISUtil.toString(new Date()));
			res.put("body", SchemaUtil.setDictionaryMessageForForm(resBody,
					CDH_OneYearSummary));
		} catch (ModelDataOperationException e) {
			logger.error("init summary chilren one year information failed.");
			throw new ServiceException(e);
		}
	}

	/**
	 * 自动获取三周岁小结数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	protected void doAutoCreateSummaryRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ValidateException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		VisitPlanModel vpm = new VisitPlanModel(dao);
		try {
			String planId = vpm.getLastVisitedPlanId(phrId, BusinessType.CD_CU);
			if (planId == null) {
				return;
			}
			Map<String, Object> planData = vpm.getPlan(planId);
			if (planData == null) {
				return;
			}
			int monthAge = Integer.parseInt((String) planData.get("extend1"));
			String schemaName = null;
			if (monthAge < 1) {
				schemaName = BSCHISEntryNames.CDH_CheckupInOne;
			} else if (monthAge < 3) {
				schemaName = BSCHISEntryNames.CDH_CheckupOneToTwo;
			} else {
				schemaName = BSCHISEntryNames.CDH_CheckupThreeToSix;
			}
			String checkupId = (String) planData.get("visitId");
			ChildrenCheckupModel ccm = new ChildrenCheckupModel(dao);
			Map<String, Object> data = ccm.getCheckupRecord(schemaName, checkupId);
			res.put("body", SchemaUtil.setDictionaryMessageForForm(data,
					CDH_OneYearSummary));
		} catch (ModelDataOperationException e) {
			logger.error("init summary chilren one year information failed.");
			throw new ServiceException(e);
		}
	}

	/**
	 * 儿童三周岁小结
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveChildrenOneYearSummaryRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ValidateException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		String op = (String) req.get("op");
		try {
			ChildrenHealthModel chm = new ChildrenHealthModel(dao);
			chm.saveChildrenOneYearSummary(body, op, res);
			DebilityChildrenModel dcm = new DebilityChildrenModel(dao);
			dcm.updateVestingMassage(body);
			chm.endHealthCard(body);
			dcm.endDebilityRecord(body);
		} catch (ModelDataOperationException e) {
			logger.error("Summary chilren one year information failed.");
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(CDH_OneYearSummary, op, phrId, dao);
	}
}
