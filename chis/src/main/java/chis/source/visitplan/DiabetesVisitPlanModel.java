/*
 * @(#)VisitPlanModel.java Created on 2011-12-27 下午3:26:51
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.visitplan;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import ctd.service.core.ServiceException;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.util.ManageYearUtil;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class DiabetesVisitPlanModel extends VisitPlanModel implements
		BSCHISEntryNames {

	public DiabetesVisitPlanModel(BaseDAO dao) {
		super(dao);
		// TODO Auto-generated constructor stub
	}

	public List<Map<String, Object>> getVisitPlan(int yearType, String empiId,
			int current, String instanceType)
			throws ModelDataOperationException, ServiceException {
		Map<String, Object> reqMap = new HashMap<String, Object>();
		if (StringUtils.isEmpty(empiId)) {
			throw new ModelDataOperationException(
					Constants.CODE_BUSINESS_DATA_NULL, "The empiId is null!");
		}
		reqMap.put("empiId", empiId);

		Date startDate = null;
		Date endDate = null;
		Calendar c = Calendar.getInstance();
		if (yearType == 1) {
			c.add(Calendar.YEAR, current + 1);
			ManageYearUtil util = new ManageYearUtil(c.getTime());
			startDate = util.getDiabetesPreYearStartDate();
			endDate = util.getDiabetesPreYearEndDate();
		} else if (yearType == 2) {
			ManageYearUtil util = new ManageYearUtil(c.getTime());
			startDate = util.getDiabetesCurYearStartDate();
			endDate = util.getDiabetesCurYearEndDate();
		} else if (yearType == 3) {
			c.add(Calendar.YEAR, current - 1);
			ManageYearUtil util = new ManageYearUtil(c.getTime());
			startDate = util.getDiabetesNextYearStartDate();
			endDate = util.getDiabetesNextYearEndDate();
		}
		

		reqMap.put("startDate", startDate);
		reqMap.put("endDate", endDate);
		reqMap.put("instanceType", instanceType);

		return this.getVisitPlan(reqMap);
	}
}
