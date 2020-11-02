/**
 * @(#)PlanType.java Created on Nov 25, 2009 10:19:48 AM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.visitplan;

import java.io.Serializable;
import java.util.Calendar;

/**
 * @description：计划类型。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class PlanType implements Serializable {

	private static final long serialVersionUID = -1496748376883930657L;

	public static final int FREQUENCY_WEEK = Calendar.WEEK_OF_YEAR;// @@ 3
	public static final int FREQUENCY_MONTH = Calendar.MONTH;// @@ 2
	public static final int FREQUENCY_YEAR = Calendar.YEAR;// @@ 1
	public static final int FREQUENCY_DAY = Calendar.DAY_OF_YEAR;// @@ 6

	// @@ 计划类型编号。
	private String planTypeCode;
	// @@ 3.周 2.月 1.年 6.天
	private int frequency = -1;
	// @@ 表示每几（周，月，年，天）一次
	private int cycle = 0;
	// @@ 可提前的天数占比（%）。
	private int precedeDays;
	// @@ 可延后的天数占比（%）。
	private int delayDays;
	
	/**
	 * 计划类型编号。
	 * 
	 * @return
	 */
	public String getPlanTypeCode() {
		return planTypeCode;
	}

	/**
	 * 计划类型编号。
	 * 
	 * @param planTypeCode
	 */
	public void setPlanTypeCode(String planTypeCode) {
		this.planTypeCode = planTypeCode;
	}

	/**
	 * 3.周 2.月 1.年 6.天
	 * 
	 * @return
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * 3.周 2.月 1.年 6.天
	 * 
	 * @param frequency
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * 表示每几（周，月，年，天）一次
	 * 
	 * @return
	 */
	public int getCycle() {
		return cycle;
	}

	/**
	 * 表示每几（周，月，年，天）一次
	 * 
	 * @param cycle
	 */
	public void setCycle(int cycle) {
		this.cycle = cycle;
	}

	/**
	 * 可提前的天数占比（%）。
	 * 
	 * @return
	 */
	public int getPrecedeDays() {
		return precedeDays;
	}

	/**
	 * 可提前的天数占比（%）。
	 * 
	 * @param precedeDays
	 */
	public void setPrecedeDays(int precedeDays) {
		this.precedeDays = precedeDays;
	}

	/**
	 * 可延后的天数占比（%）。
	 * 
	 * @return
	 */
	public int getDelayDays() {
		return delayDays;
	}

	/**
	 * 可延后的天数占比（%）。
	 * 
	 * @param delayDays
	 */
	public void setDelayDays(int delayDays) {
		this.delayDays = delayDays;
	}
}
