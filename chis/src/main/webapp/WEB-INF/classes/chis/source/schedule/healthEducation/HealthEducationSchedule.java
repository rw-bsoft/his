/**
 * @(#)HealthCheckSchedule.java Created on 2012-5-28 上午11:03:26
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.schedule.healthEducation;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.dic.WorkType;
import chis.source.schedule.AbstractJobSchedule;
import chis.source.worklist.WorkListModel;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class HealthEducationSchedule extends AbstractJobSchedule {

	@Override
	public void doJob(BaseDAO dao) throws ModelDataOperationException {
		WorkListModel wlModel = new WorkListModel(dao);
		List<Map<String, Object>> hesList = wlModel
				.getWorkSummaryList(WorkType.HEALTHEDUCATION);
		for (Map<String, Object> r : hesList) {
			Date endDate = (Date) r.get("endDate");
			Calendar c = Calendar.getInstance();
			Long endDateValue = endDate.getTime();
			Long now = c.getTimeInMillis();
			if (endDateValue < now) {
				wlModel.deleteWorkListByPkey((String) r.get("workId"));
			}
		}
	}

}
