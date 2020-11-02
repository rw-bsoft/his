/**
 * @(#)PsyAnnualAssessmentSchedule.java Created on 2012-5-25 下午7:37:59
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.schedule.psychosis;

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
import chis.source.schedule.hypertension.HypertensionYearFixGroupSchedule;
import chis.source.worklist.WorkListModel;

/**
 * @description 精神病年度评估 慢病健康检查
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class PsyAnnualAssessmentSchedule extends AbstractJobSchedule {
	private static final Log logger = LogFactory
			.getLog(HypertensionYearFixGroupSchedule.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.schedule.AbstractJobSchedule#doJob(chis.source.
	 * BaseDAO)
	 */
	@Override
	public void doJob(BaseDAO dao) throws ModelDataOperationException {
		// 删去往年未完成的评估任务。
		WorkListModel wlModel = new WorkListModel(dao);
		wlModel.deleteLastYearTask(WorkType.PSYCHOSIS_ANNUAL_ASSESSMENT);

		// 统计本年度需评估档案 (精神病年度评估)* 创建此年度评估任务
		PsyAnnualAssessmentModel paamModel = new PsyAnnualAssessmentModel(dao);
		List<Map<String, Object>> rsList = paamModel
				.getPsyAnnualAssessmentRecord();
		Calendar c = Calendar.getInstance();
		int currentYear = c.get(Calendar.YEAR);
		for (Map<String, Object> r : rsList) {
			Date maxCreateDate = (Date) r.get("maxDate");
			if(maxCreateDate == null){//对去年未做评估的，重新生成任务提醒
			    r.put("workType", WorkType.PSYCHOSIS_ANNUAL_ASSESSMENT);
                //增加任务
                wlModel.insertWorkListRecord(r, false);
				continue;
			}
			c.setTime(maxCreateDate);
			int maxYear = c.get(Calendar.YEAR);
			if (currentYear > maxYear) {
				r.remove("maxDate");
				r.put("workType", WorkType.PSYCHOSIS_ANNUAL_ASSESSMENT);
				//增加任务
				wlModel.insertWorkListRecord(r, false);
			}
		}
		logger.info("Has added " + rsList.size() + " tasks.");
	}

}
