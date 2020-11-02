/**
 * @(#)HealthCheckSchedule.java Created on 2012-5-23 上午14:11:02
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.schedule.healthCheck;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
public class HealthCheckSchedule extends AbstractJobSchedule {

	private static final Log logger = LogFactory
			.getLog(HealthCheckSchedule.class);

	public void doJob(BaseDAO dao) throws ModelDataOperationException {
		// 删除往年的任务提醒
		WorkListModel wlModel = new WorkListModel(dao);
		wlModel.deleteLastYearTask(WorkType.HEALTHCHECK_YEAR_WARN);
		// 增加新的任务提醒
		HealthCheckScheduleModule hsModule = new HealthCheckScheduleModule(dao);
		List<Map<String, Object>> hsList = hsModule.getHealthCheckRecord();
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.YEAR, 1);
		c.add(Calendar.DAY_OF_YEAR, -1);
		Date endDate = c.getTime();
//		int currentYear = c.get(Calendar.YEAR);
		for (Map<String, Object> r : hsList) {
			r.put("beginDate", new Date());
			r.put("endDate", endDate);
//			Date maxCreateDate = (Date) r.get("maxDate");
//			if (maxCreateDate != null) {
//				c.setTime(maxCreateDate);
//				int maxYear = c.get(Calendar.YEAR);
//				if (currentYear > maxYear) {
//					r.remove("maxDate");
//					r.put("workType", WorkType.HEALTHCHECK_YEAR_WARN);
//					// 增加任务
//					wlModel.insertWorkListRecord(r, false);
//				}
//			} else {
			r.remove("maxDate");
			r.put("workType", WorkType.HEALTHCHECK_YEAR_WARN);
			// 增加任务
			wlModel.insertWorkListRecord(r, false);
//			}
		}
		logger.info("Has added " + hsList.size() + " tasks.");
	}

}
