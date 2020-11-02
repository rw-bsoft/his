/*
 * @(#)OldPeoplePrecondition.java Created on 2012-1-12 下午7:21:52
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.precondition;

import java.util.Date;
import java.util.Map;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.empi.EmpiModel;
import chis.source.util.BSCHISUtil;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.VisitPlanModel;
import chis.source.visitplan.adapter.BusinessAdapter;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class OldPeoplePrecondition extends AbstractPrecondition {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.precondition.AbstractPrecondition#doSatisfy
	 * (chis.source.visitplan.CreatationParams,
	 * chis.source.visitplan.adapter.BusinessAdapter,
	 * ctd.util.context.Context)
	 */
	public boolean doSatisfy(CreatationParams params, BusinessAdapter adapter,
			Context ctx) throws CreateVisitPlanException {
		VisitPlanModel vpm = new VisitPlanModel(getDAO(ctx));
		try {
			// @@ 如果当前还有计划未做完，不创建新一轮的计划。
			if (vpm.hasVisitPlan(params.getRecordId(),new Date(),
					BusinessType.LNR)) {
				return false;
			}
		} catch (ModelDataOperationException e) {
			throw new CreateVisitPlanException(e.getMessage(), e);
		}
		// @@ 计算年龄（周岁）。
		try {
			Map<String, Object> r = new EmpiModel(new BaseDAO())
					.getSexAndBirthday(params.getEmpiId());
			Date birthday = (Date) r.get("birthday");
			params.setDateValue("$birthday", birthday);
			int age = BSCHISUtil.calculateAge(birthday, new Date());
			params.setIntValue("$age", age);
		} catch (ModelDataOperationException e) {
			throw new CreateVisitPlanException("Failed to get birthday.", e);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.precondition.Precondition#getBusinessType()
	 */
	public String getBusinessType() {
		return BusinessType.LNR;
	}

}
