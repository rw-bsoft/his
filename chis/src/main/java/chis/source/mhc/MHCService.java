/**
 * @(#)MHCService.java Created on 2012-4-28 下午2:06:11
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.mhc;

import java.util.Map;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.PlanMode;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.ApplicationUtil;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.PlanType;
import chis.source.visitplan.VisitPlanCreator;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class MHCService extends AbstractActionService implements DAOSupportable {

	private VisitPlanCreator visitPlanCreator;

	/**
	 * 根据本次随访的高危因素创建今后的高危随访计划
	 * 
	 * @param body
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws CreateVisitPlanException
	 * @throws ServiceException
	 */
	protected String makeNextHighVisitPlan(Map<String, Object> body, Context ctx)
			throws CreateVisitPlanException, ServiceException {
		String planMode;
		try {
			planMode = ApplicationUtil.getProperty(Constants.UTIL_APP_ID,
					BusinessType.MATERNAL + "_planMode");
			if (PlanMode.BY_RESERVED.equals(planMode)) {
				return "";
			}
			body.put("businessType", BusinessType.PREGNANT_HIGH_RISK);
			PlanType planType = getVisitPlanCreator().create(body, ctx);
			String planTypeCode = null;
			if (planType != null) {
				planTypeCode = planType.getPlanTypeCode();
			}
			return planTypeCode;
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 孕妇档案终止管理
	 * 
	 * @param record
	 * @param dao
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	protected void endManagePregnant(Map<String, Object> record, BaseDAO dao)
			throws ValidateException, ModelDataOperationException {
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		prm.saveEndManageRecord(record, "create");
		String pregnantId = (String) record.get("pregnantId");
		prm.updatePregnantRecordStatus(pregnantId,
				Constants.CODE_STATUS_END_MANAGE);
		prm.closePregnantVisitPlan(pregnantId);
	}

	/**
	 * 
	 * @return the visitPlanCreator
	 */
	public VisitPlanCreator getVisitPlanCreator() {
		return visitPlanCreator;
	}

	/**
	 * 
	 * @param visitPlanCreator
	 *            the visitPlanCreator to set
	 */
	public void setVisitPlanCreator(VisitPlanCreator visitPlanCreator) {
		this.visitPlanCreator = visitPlanCreator;
	}

}
