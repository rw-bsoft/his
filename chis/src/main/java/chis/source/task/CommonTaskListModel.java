/**
 * @(#)AbstractTaskListModel.java Created on 2012-3-19 上午11:23:05
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.task;
 
import java.util.Date;

import chis.source.BaseDAO;
import chis.source.util.BSCHISUtil;
import ctd.schema.Schema;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class CommonTaskListModel extends AbstractTaskListModel  {

	public CommonTaskListModel(BaseDAO dao) {
		super(dao);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 要查询的字段。
	 * 
	 * @param sc
	 * @return
	 */
	protected String buildQueryFields(Schema sc) {
		StringBuffer fields = new StringBuffer(super.buildQueryFields(sc));
		String currentDate = BSCHISUtil.toString(new Date(), null);
		// @@ 高血压跟糖尿病 的剩余天数使用计划日期来算，其他的用结束日期算。
		fields.append(",a.endDate-date('").append(currentDate).append("') as remainDays")
				.append(",a.endDate as lastDate")
				.append(",a.beginDate as earliestDate");
		
//		fields.append(",(case when a.businessType='1' or a.businessType='2' ")
//				.append("then ((a.planDate-date('")
//				.append(currentDate)
//				.append("'))+10) when a.businessType='10' then ((a.planDate-date('")
//				.append(currentDate).append("'))+7) else (a.endDate-date('")
//				.append(currentDate).append("')) end) as remainDays");
//		fields.append(",(case when a.businessType='1' or a.businessType='2' ")
//				.append("then sum_day(a.planDate+10) when a.businessType='10' then sum_day(a.planDate+7) else a.endDate end) as lastDate");
//		fields.append(",(case when a.businessType='1' or a.businessType='2' ")
//				.append("then sum_day(a.planDate-10) when a.businessType='10' then sum_day(a.planDate-7) else a.beginDate end) as earliestDate");
		return fields.toString();
	}
}
