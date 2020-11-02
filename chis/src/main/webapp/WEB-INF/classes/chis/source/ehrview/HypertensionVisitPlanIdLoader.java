/**
 * @(#)HypertensionVisitPlanIdLoader.java Created on 2012-5-7 下午9:44:16
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.ehrview;

import java.util.HashMap;
import java.util.Map;

import chis.source.BaseDAO;
import chis.source.dic.BusinessType;
import chis.source.visitplan.VisitPlanModel;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HypertensionVisitPlanIdLoader extends AbstractIdLoader {

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#hasStatus()
	 */
	@Override
	public boolean hasStatus() {
		return false;
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#getEntryName()
	 */
	@Override
	public String getEntryName() {
		return "MDC_HypertensionVisit";
	}
	
	@Override
	public String getEntityName() {
		return MDC_HypertensionVisit;
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#getIdColumn()
	 */
	@Override
	public String getIdColumn() {
		return "planId";
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#getIdName()
	 */
	@Override
	public String getIdName() {
		return "PUB_VisitPlan.planId";
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.AbstractIdLoader#load(java.lang.String, ctd.util.context.Context)
	 */
	@Override
	public Map<String, Object> load(String loadBy, Context ctx)
			throws ServiceException {
		VisitPlanModel vpm = new VisitPlanModel(new BaseDAO());
		String hyperPlanId;
		try {
			hyperPlanId = vpm.getFirstPlanId(loadBy,
					BusinessType.GXY);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		Map<String, Object> idAndStatus = new HashMap<String, Object>();
		if (hyperPlanId != null) {
			idAndStatus.put(getIdColumn(), hyperPlanId);
		}
		return idAndStatus;
	}

	
}
