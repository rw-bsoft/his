/*
 * @(#)HypertensionInquireAdapter.java Created on 2012-2-2 下午4:33:07
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import chis.source.ModelDataOperationException;
import chis.source.util.ManageYearUtil;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.PlanType;
import chis.source.visitplan.VisitPlanModel;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class HypertensionInquireAdapter extends AbstractBusinessAdapter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.adapter.BusinessAdapter#getOnsetDate(com.bsoft
	 * .bschis.source.visitplan.CreatationParams, chis.source.visitplan.PlanType,
	 * ctd.util.context.Context)
	 */
	@Override
	public Date getOnsetDate(CreatationParams params, PlanType planType,
			Context ctx) throws CreateVisitPlanException {
		Calendar c = Calendar.getInstance();
		c.setTime(params.getDateValue("visitDate"));
		c.add(planType.getFrequency(), planType.getCycle());
		return c.getTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.adapter.BusinessAdapter#getCutoutDate(com.
	 * bsoft.bschis.source.visitplan.CreatationParams,
	 * chis.source.visitplan.PlanType, ctd.util.context.Context)
	 */
	@Override
	public Date getCutoutDate(CreatationParams params, PlanType planType,
			Context ctx) throws CreateVisitPlanException, ServiceException {
		if (params.getDateValue("nextPlanDate") != null) {
			return params.getDateValue("nextPlanDate");
		}
		String nextPlanId = (String) params.getStringValue("nextPlanId");
		if (nextPlanId.length() != 0) {
			VisitPlanModel vpm = new VisitPlanModel(getDAO(ctx));

			Map<String, Object> map;
			try {
				map = vpm.getPlan(nextPlanId);
			} catch (ModelDataOperationException e) {
				throw new CreateVisitPlanException("", e);
			}
			if (map != null) {
				return (Date) map.get("beginDate");
			}
		}
		// @@ 如果没有下一次计划，说明是该年度 的最后一次随访计划，询问计划排到年度最后一天。
		ManageYearUtil util = new ManageYearUtil(new Date());
		return util.getHypertensionCurYearEndDate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.adapter.AbstractBusinessAdapter#runExpression
	 * (chis.source.visitplan.CreatationParams, java.lang.String,
	 * ctd.util.context.Context)
	 */
	@Override
	protected boolean runExpression(CreatationParams params, String expression,
			Context ctx) throws CreateVisitPlanException {
		expression = expression.replace("cycle",
				params.getStringValue("$cycle"));
		try {
			return (Boolean) ExpRunner.run(expression, ctx);
		} catch (ExpException e) {
			throw new CreateVisitPlanException(
					"Cannot run plan instance exception.", e);
		}
	}
}
