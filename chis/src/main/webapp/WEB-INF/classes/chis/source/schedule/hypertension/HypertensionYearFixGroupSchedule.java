/**
 * @(#)HypertensionYearFixGroupSchedule.java Created on 2012-5-25 上午9:26:55
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.schedule.hypertension;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.dic.PlanStatus;
import chis.source.dic.WorkType;
import chis.source.schedule.AbstractJobSchedule;
import chis.source.worklist.WorkListModel;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HypertensionYearFixGroupSchedule extends AbstractJobSchedule {

	private static final Log logger = LogFactory
			.getLog(HypertensionYearFixGroupSchedule.class);

	@Override
	public void doJob(BaseDAO dao) throws ModelDataOperationException {
		// 删去往年未完成的评估任务。
		WorkListModel wlModel = new WorkListModel(dao);
		wlModel.deleteLastYearTask(WorkType.HYPERTENSION_YEAR_FIXGROUP);

		// 将最后一条随访已访或者最后一条随访已超期的高血压档案记录到任务列表里。
		HyperYearFixGroupScheduleModel hyfgModel = new HyperYearFixGroupScheduleModel(
				dao);
		List<Map<String, Object>> rsList = hyfgModel
				.getHypertensionLastVisitRecord();
		
		Calendar c = Calendar.getInstance();
		int currentMonth = c.get(Calendar.MONTH);
		int currentYear = c.get(Calendar.YEAR);
		for (Map<String, Object> r : rsList) {
			Date maxPlanDate = (Date) r.get("maxPlanDate");
			if(maxPlanDate == null){
			    r.remove("maxPlanStatus");
                r.remove("maxPlanDate");
                r.put("workType", WorkType.HYPERTENSION_YEAR_FIXGROUP);
                
                wlModel.insertWorkListRecord(r, false);
				continue;
			}
			c.setTime(maxPlanDate);
			int mpdm = c.get(Calendar.MONTH);
			int mpdy = c.get(Calendar.YEAR);
			// @@ 最后一条随访已访或者最后一条随访已超期		
			if (r.get("maxPlanStatus").equals(PlanStatus.VISITED)
					|| (currentMonth > mpdm && currentYear >= mpdy)) {
				r.remove("maxPlanStatus");
				r.remove("maxPlanDate");
				r.put("workType", WorkType.HYPERTENSION_YEAR_FIXGROUP);
				
				wlModel.insertWorkListRecord(r, false);
			}
		}
		logger.info("Has added " + rsList.size() + " tasks.");
	}

}
