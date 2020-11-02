/*
 * @(#)PlanScheduleItem.java Created on 2012-1-13 上午11:26:59
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.schedule;

import java.io.Serializable;
import java.util.Date;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class PlanScheduleItem implements Serializable {

	private static final long serialVersionUID = -244398659123424252L;

	private Date beginDate;
	private Date endDate;
	private Date planDate;

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getPlanDate() {
		return planDate;
	}

	public void setPlanDate(Date planDate) {
		this.planDate = planDate;
	}
}
