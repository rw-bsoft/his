/**
 * @(#)ManageYearUtil.java Created on Sep 17, 2009 4:36:53 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.util;

import java.util.Calendar;
import java.util.Date;

import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;

import chis.source.Constants;
import chis.source.dic.BusinessType;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class ManageYearUtil {
	// ** 高血压管理------年度起始、结束月份
	public static final String HYPERTENSION_START_MONTH = "hypertensionStartMonth";
	public static final String HYPERTENSION_END_MONTH = "hypertensionEndMonth";
	// ** 糖尿病管理年度起始月份
	public static final String DIABETES_START_MONTH = "diabetesStartMonth";
	public static final String DIABETES_END_MONTH = "diabetesEndMonth";
	// ** 精神病档案参数设置配置信息*年度起始月份
	public static final String PSYCHOSIS_START_MONTH = "psychosisStartMonth";
	public static final String PSYCHOSIS_END_MONTH = "psychosisEndMonth";
	// ** 老年人档案随访 年度起始月份
	public static final String OLDPEOPLE_START_MONTH = "oldPeopleStartMonth";
	public static final String OLDPEOPLE_END_MONTH = "oldPeopleEndMonth";

	private Date currentDate = null;

	public ManageYearUtil(Date currentDate) {
		setCurrentDate(currentDate);
	}

	/**
	 * 获取系统设置的开始月份<br>
	 * 如果未找到系统设置的起始月份 则 －－返回自然年份的起始月份 1(月)
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public int getSystemSetupStartMonth(String businessType)
			throws ServiceException {
		String startMonthPropertyName = "";
		if (BusinessType.GXY.equals(businessType)) {
			startMonthPropertyName = HYPERTENSION_START_MONTH;
		} else if (BusinessType.TNB.equals(businessType)) {
			startMonthPropertyName = DIABETES_START_MONTH;
		} else if (BusinessType.PSYCHOSIS.equals(businessType)) {
			startMonthPropertyName = PSYCHOSIS_START_MONTH;
		} else if (BusinessType.LNR.equals(businessType)) {
			startMonthPropertyName = OLDPEOPLE_START_MONTH;
		}
		// TODO--如有其他在此补充
		// 如果没有返回自然年开始月份
		if ("".equals(startMonthPropertyName)) {
			return 1;
		}
		try {
			return Integer.parseInt(ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, startMonthPropertyName));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取系统设置的结束月份<br>
	 * 如果未找到系统设置的 截止月份 则 －－返回自然年份的截止月份12(月)
	 * 
	 * @param businessType
	 * @return
	 * @throws ControllerException
	 * @throws NumberFormatException
	 */
	public int getSystemSetupEndMonth(String businessType)
			throws NumberFormatException, ControllerException {
		String endMonthPropertyName = "";
		if (BusinessType.GXY.equals(businessType)) {
			endMonthPropertyName = HYPERTENSION_END_MONTH;
		} else if (BusinessType.TNB.equals(businessType)) {
			endMonthPropertyName = DIABETES_END_MONTH;
		} else if (BusinessType.PSYCHOSIS.equals(businessType)) {
			endMonthPropertyName = PSYCHOSIS_END_MONTH;
		} else if (BusinessType.LNR.equals(businessType)) {
			endMonthPropertyName = OLDPEOPLE_END_MONTH;
		}
		// TODO--如有其他在此补充
		// 如果没有返回自然年结束月份
		if ("".equals(endMonthPropertyName)) {
			return 12;
		}
		return Integer.parseInt(ApplicationUtil.getProperty(
				Constants.UTIL_APP_ID, endMonthPropertyName));
	}

	// -----------------随访计划上一个年度开始结束日期－－－－－－－－－－－－－－
	/**
	 * 获取上一个随访计划年度的开始日期
	 * 
	 * @param businessType
	 * @return
	 * @throws ServiceException
	 */
	public Date getVisitPlanPreYearStartDate(String businessType)
			throws ServiceException {
		try {
			return getPreYearStartDate(getSystemSetupStartMonth(businessType),
					getSystemSetupEndMonth(businessType));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取上一个随访计划年度的结束日期
	 * 
	 * @param businessType
	 * @return
	 * @throws ServiceException
	 */
	public Date getVisitPlanPreYearEndDate(String businessType)
			throws ServiceException {
		try {
			return getPreYearEndDate(
					getVisitPlanPreYearStartDate(businessType),
					getSystemSetupEndMonth(businessType));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}

	// -----------------随访计划本一个年度开始结束日期－－－－－－－－－－－－－－
	/**
	 * 获取本个随访计划年度的开始日期
	 * 
	 * @param businessType
	 * @return
	 * @throws ServiceException
	 */
	public Date getVisitPlanCurYearStartDate(String businessType)
			throws ServiceException {
		try {
			return getCurYearStartDate(getSystemSetupStartMonth(businessType),
					getSystemSetupEndMonth(businessType));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取本个随访计划年度的结束日期
	 * 
	 * @param businessType
	 * @return
	 * @throws ServiceException
	 */
	public Date getVisitPlanCurYearEndDate(String businessType)
			throws ServiceException {
		try {
			return getCurYearEndDate(
					getVisitPlanCurYearStartDate(businessType),
					getSystemSetupEndMonth(businessType));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}

	// -----------------随访计划本一个年度开始结束日期－－－－－－－－－－－－－－
	/**
	 * 获取本个随访计划年度的开始日期
	 * 
	 * @param businessType
	 * @return
	 * @throws ServiceException
	 */
	public Date getVisitPlanNextYearStartDate(String businessType)
			throws ServiceException {
		try {
			return getNextYearStartDate(getSystemSetupStartMonth(businessType),
					getSystemSetupEndMonth(businessType));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取下一个随访计划年度的结束日期
	 * 
	 * @param businessType
	 * @return
	 * @throws ServiceException
	 */
	public Date getVisitPlanNextYearEndDate(String businessType)
			throws ServiceException {
		try {
			return getNextYearEndDate(
					getVisitPlanNextYearStartDate(businessType),
					getSystemSetupEndMonth(businessType));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * 获取高血压管理年度的开始月份。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public int getHypertensionStartMonth() throws ServiceException {
		try {
			return Integer.parseInt(ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, "hypertensionStartMonth"));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取高血压管理年度的结束月份。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public int getHypertensionEndMonth() throws ServiceException {
		try {
			return Integer.parseInt(ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, "hypertensionEndMonth"));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取老年人管理年度的开始月份。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public int getOldPeopleStartMonth() throws ServiceException {
		try {
			return Integer.parseInt(ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, "oldPeopleStartMonth"));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取老年人管理年度的结束月份。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public int getOldPeopleEndMonth() throws ServiceException {
		try {
			return Integer.parseInt(ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, "oldPeopleEndMonth"));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取糖尿病管理年度的开始月份。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public int getDiabetesStartMonth() throws ServiceException {
		try {
			return Integer.parseInt(ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, "diabetesStartMonth"));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取糖尿病管理年度的结束月份。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public int getDiabetesEndMonth() throws ServiceException {
		try {
			return Integer.parseInt(ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, "diabetesEndMonth"));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取肿瘤管理年度的开始月份。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public int getTumourStartMonth() throws ServiceException {
		try {
			return Integer.parseInt(ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, "TumourStartMonth"));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取肿瘤管理年度的结束月份。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public int getTumourEndMonth() throws ServiceException {
		try {
			return Integer.parseInt(ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, "TumourEndMonth"));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取体弱儿童管理年度的开始月份。
	 * 
	 * @return
	 */
	public int getDebilityChildrenStartMonth() {
		return 1;
	}

	/**
	 * 获取健康年度检查管理年度的开始月份。
	 * 
	 * @return
	 */
	public int getHealthCheckStartMonth() {
		return 1;
	}

	/**
	 * 获取体弱儿童管理年度的结束月份。
	 * 
	 * @return
	 */
	public int getDebilityChildrenEndMonth() {
		return 12;
	}

	/**
	 * 获取健康年度检查管理年度的结束月份。
	 * 
	 * @return
	 */
	public int getHealthCheckEndMonth() {
		return 12;
	}

	/**
	 * 获取下一个高血压管理年度的开始日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getHypertensionNextYearStartDate() throws ServiceException {
		return getNextYearStartDate(getHypertensionStartMonth(),
				getHypertensionEndMonth());
	}

	/**
	 * 获取下一个高血压管理年度的结束日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getHypertensionNextYearEndDate() throws ServiceException {
		return getNextYearEndDate(getHypertensionNextYearStartDate(),
				getHypertensionEndMonth());
	}

	/**
	 * 获取本个高血压管理年度的开始日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getHypertensionCurYearStartDate() throws ServiceException {
		return getCurYearStartDate(getHypertensionStartMonth(),
				getHypertensionEndMonth());
	}

	/**
	 * 获取本个高血压管理年度的结束日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getHypertensionCurYearEndDate() throws ServiceException {
		return getCurYearEndDate(getHypertensionCurYearStartDate(),
				getHypertensionEndMonth());
	}

	/**
	 * 获取上一个高血压管理年度的开始日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getHypertensionnPreYearStartDate() throws ServiceException {
		return getPreYearStartDate(getHypertensionStartMonth(),
				getHypertensionEndMonth());
	}

	/**
	 * 获取上一个高血压管理年度的结束日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getHypertensionPreYearEndDate() throws ServiceException {
		return getPreYearEndDate(getHypertensionnPreYearStartDate(),
				getHypertensionEndMonth());
	}

	/**
	 * 获取上一个老年人管理年度的开始日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getOldPeoplePreYearStartDate() throws ServiceException {
		return getPreYearStartDate(getOldPeopleStartMonth(),
				getOldPeopleEndMonth());
	}

	/**
	 * 获取上一个老年人管理年度的结束日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getOldPeoplePreYearEndDate() throws ServiceException {
		return getPreYearEndDate(getOldPeoplePreYearStartDate(),
				getOldPeopleEndMonth());
	}

	/**
	 * 获取本年度老年人管理年度的开始日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getOldPeopleCurYearStartDate() throws ServiceException {
		return getCurYearStartDate(getOldPeopleStartMonth(),
				getOldPeopleEndMonth());
	}

	/**
	 * 获取本年度老年人管理年度的结束日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getOldPeopleCurYearEndDate() throws ServiceException {
		return getCurYearEndDate(getOldPeopleCurYearStartDate(),
				getOldPeopleEndMonth());
	}

	/**
	 * 获取下一个老年人管理年度的开始日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getOldPeopleNextYearStartDate() throws ServiceException {
		return getNextYearStartDate(getOldPeopleStartMonth(),
				getOldPeopleEndMonth());
	}

	/**
	 * 获取下一个老年人管理年度的结束日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getOldPeopleNextYearEndDate() throws ServiceException {
		return getNextYearEndDate(getOldPeopleNextYearStartDate(),
				getOldPeopleEndMonth());
	}

	/**
	 * 获取前一个糖尿病管理年度的开始日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getDiabetesPreYearStartDate() throws ServiceException {
		return getPreYearStartDate(getDiabetesStartMonth(),
				getDiabetesEndMonth());
	}

	/**
	 * 获取前一个糖尿病管理年度的结束日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getDiabetesPreYearEndDate() throws ServiceException {
		return getPreYearEndDate(getDiabetesPreYearStartDate(),
				getDiabetesEndMonth());
	}

	/**
	 * 获取当前糖尿病管理年度的开始日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getDiabetesCurYearStartDate() throws ServiceException {
		return getCurYearStartDate(getDiabetesStartMonth(),
				getDiabetesEndMonth());
	}

	/**
	 * 获取当前糖尿病管理年度的结束日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getDiabetesCurYearEndDate() throws ServiceException {
		return getCurYearEndDate(getDiabetesCurYearStartDate(),
				getDiabetesEndMonth());
	}

	/**
	 * 获取下一个糖尿病管理年度的开始日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getDiabetesNextYearStartDate() throws ServiceException {
		return getNextYearStartDate(getDiabetesStartMonth(),
				getDiabetesEndMonth());
	}

	/**
	 * 获取下一个糖尿病管理年度的结束日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getDiabetesNextYearEndDate() throws ServiceException {
		return getNextYearEndDate(getDiabetesNextYearStartDate(),
				getDiabetesEndMonth());
	}

	/**
	 * 获取上一个肿瘤管理年度的开始日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getTumourPreYearStartDate() throws ServiceException {
		return getPreYearStartDate(getTumourStartMonth(), getTumourEndMonth());
	}

	/**
	 * 获取上一个肿瘤管理年度的结束日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getTumourPreYearEndDate() throws ServiceException {
		return getPreYearEndDate(getTumourPreYearStartDate(),
				getTumourEndMonth());
	}

	/**
	 * 获取本个肿瘤管理年度的开始日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getTumourCurYearStartDate() throws ServiceException {
		return getCurYearStartDate(getTumourStartMonth(), getTumourEndMonth());
	}

	/**
	 * 获取本个肿瘤管理年度的结束日期。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Date getTumourCurYearEndDate() throws ServiceException {
		return getCurYearEndDate(getTumourCurYearStartDate(),
				getTumourEndMonth());
	}

	/**
	 * 获取体弱儿童前一个管理年度的开始日期。
	 * 
	 * @return
	 */
	public Date getDebilityChildrenPreYearStartDate() {
		return getPreYearStartDate(getDebilityChildrenStartMonth(),
				getDebilityChildrenEndMonth());
	}

	/**
	 * 获取体弱儿童前一个管理年度的结束日期。
	 * 
	 * @return
	 */
	public Date getDebilityChildrenPreYearEndDate() {
		return getPreYearEndDate(getDebilityChildrenPreYearStartDate(),
				getDebilityChildrenEndMonth());
	}

	/**
	 * 获取体弱儿童本个管理年度的开始日期。
	 * 
	 * @return
	 */
	public Date getDebilityChildrenCurYearStartDate() {
		return getCurYearStartDate(getDebilityChildrenStartMonth(),
				getDebilityChildrenEndMonth());
	}

	/**
	 * 获取健康年度检查管理年度的开始日期。
	 * 
	 * @return
	 */
	public Date getHealthCheckCurYearStartDate() {
		return getCurYearStartDate(getHealthCheckStartMonth(),
				getHealthCheckEndMonth());
	}

	/**
	 * 获取体弱儿童本个管理年度的结束日期。
	 * 
	 * @return
	 */
	public Date getDebilityChildrenCurYearEndDate() {
		return getCurYearEndDate(getDebilityChildrenCurYearStartDate(),
				getDebilityChildrenEndMonth());
	}

	/**
	 * 获取健康年度检查管理年度的结束日期。
	 * 
	 * @return
	 */
	public Date getHealthCheckCurYearEndDate() {
		return getCurYearEndDate(getHealthCheckCurYearStartDate(),
				getHealthCheckEndMonth());
	}

	/**
	 * 获取体弱儿童下一个管理年度的开始日期。
	 * 
	 * @return
	 */
	public Date getDebilityChildrenNextYearStartDate() {
		return getNextYearStartDate(getDebilityChildrenStartMonth(),
				getDebilityChildrenEndMonth());
	}

	/**
	 * 获取体弱儿童下一个管理年度的结束日期。
	 * 
	 * @return
	 */
	public Date getDebilityChildrenNextYearEndDate() {
		return getNextYearEndDate(getDebilityChildrenNextYearStartDate(),
				getDebilityChildrenEndMonth());
	}

	/**
	 * 获取下一个肿瘤管理年度的开始日期。
	 * 
	 * @return
	 * @throws ServiceException 
	 */
	public Date getTumourNextYearStartMonth() throws ServiceException {
		return getNextYearStartDate(getTumourStartMonth(), getTumourEndMonth());
	}

	/**
	 * 获取下一个肿瘤管理年度的结束日期。
	 * 
	 * @return
	 * @throws ServiceException 
	 */
	public Date getTumourNextYearEndMonth() throws ServiceException {
		return getNextYearEndDate(getTumourNextYearStartMonth(),
				getTumourEndMonth());
	}

	/**
	 * 获取上一年度的开始日期。
	 * 
	 * @param startMonth
	 * @return
	 */
	private Date getPreYearStartDate(int startMonth, int endMonth) {
		Calendar c = getStartDate(startMonth);
		if (c.get(Calendar.MONTH) <= endMonth - 1 && startMonth > endMonth) {
			c.add(Calendar.YEAR, -2);
		} else {
			c.add(Calendar.YEAR, -1);
		}
		return c.getTime();
	}

	/**
	 * 获取上一年度的结束日期。
	 * 
	 * @param preYearStartDate
	 * @param endMonth
	 * @return
	 */
	private Date getPreYearEndDate(Date preYearStartDate, int endMonth) {
		return getEndDate(preYearStartDate, endMonth);
	}

	/**
	 * 获取本年度的开始日期。
	 * 
	 * @param startMonth
	 * @return
	 */
	private Date getCurYearStartDate(int startMonth, int endMonth) {
		Calendar c = getStartDate(startMonth);
		if (c.get(Calendar.MONTH) <= endMonth - 1 && startMonth > endMonth) {
			c.add(Calendar.YEAR, -1);
		}
		return c.getTime();
	}

	/**
	 * 获取本年度的结束日期。
	 * 
	 * @param curYearStartDate
	 * @param endMonth
	 * @return
	 */
	private Date getCurYearEndDate(Date curYearStartDate, int endMonth) {
		return getEndDate(curYearStartDate, endMonth);
	}

	/**
	 * 获取下一年度的开始日期。
	 * 
	 * @param startMonth
	 * @param endMonth
	 * @return
	 */
	private Date getNextYearStartDate(int startMonth, int endMonth) {
		Calendar c = getStartDate(startMonth);
		if (c.get(Calendar.MONTH) <= endMonth - 1 && startMonth > endMonth) {
			return c.getTime();
		}
		c.add(Calendar.YEAR, 1);
		return c.getTime();
	}

	/**
	 * 获取下一年度的结束日期。
	 * 
	 * @param nextYearStartDate
	 * @param endMonth
	 * @return
	 */
	private Date getNextYearEndDate(Date nextYearStartDate, int endMonth) {
		return getEndDate(nextYearStartDate, endMonth);
	}

	/**
	 * 获取前一个糖尿病管理年度的开始日期。
	 * 
	 * @return
	 * @throws ServiceException 
	 */
	public Date getPsychosisPreYearStartDate() throws ServiceException {
		return getPreYearStartDate(getPsychosisStartMonth(),
				getPsychosisEndMonth());
	}

	/**
	 * 获取前一个糖尿病管理年度的结束日期。
	 * 
	 * @return
	 * @throws ServiceException 
	 */
	public Date getPsychosisPreYearEndDate() throws ServiceException {
		return getPreYearEndDate(getPsychosisPreYearStartDate(),
				getPsychosisEndMonth());
	}

	/**
	 * 获取当前糖尿病管理年度的开始日期。
	 * 
	 * @return
	 * @throws ServiceException 
	 */
	public Date getPsychosisCurYearStartDate() throws ServiceException {
		return getCurYearStartDate(getPsychosisStartMonth(),
				getPsychosisEndMonth());
	}

	/**
	 * 获取当前糖尿病管理年度的结束日期。
	 * 
	 * @return
	 * @throws ServiceException 
	 */
	public Date getPsychosisCurYearEndDate() throws ServiceException {
		return getCurYearEndDate(getPsychosisCurYearStartDate(),
				getPsychosisEndMonth());
	}

	/**
	 * 获取下一个糖尿病管理年度的开始日期。
	 * 
	 * @return
	 * @throws ServiceException 
	 */
	public Date getPsychosisNextYearStartMonth() throws ServiceException {
		return getNextYearStartDate(getPsychosisStartMonth(),
				getPsychosisEndMonth());
	}

	/**
	 * 获取下一个糖尿病管理年度的结束日期。
	 * 
	 * @return
	 * @throws ServiceException 
	 */
	public Date getPsychosisNextYearEndMonth() throws ServiceException {
		return getNextYearEndDate(getPsychosisNextYearStartMonth(),
				getPsychosisEndMonth());
	}

	/**
	 * 获取糖尿病管理年度的开始月份。
	 * 
	 * @return
	 * @throws ServiceException 
	 */
	public int getPsychosisStartMonth() throws ServiceException {
		try {
			return Integer.parseInt(ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, "psychosisStartMonth"));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取糖尿病管理年度的结束月份。
	 * 
	 * @return
	 * @throws ServiceException 
	 */
	public int getPsychosisEndMonth() throws ServiceException {
		try {
			return Integer.parseInt(ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, "psychosisEndMonth"));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取当前自然年的管理年度开始日期。
	 * 
	 * @param startMonth
	 * @return
	 */
	private Calendar getStartDate(int startMonth) {
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		if (c.get(Calendar.MONTH) + 1 < startMonth) {
			c.add(Calendar.YEAR, -1);
		}
		c.set(Calendar.MONTH, startMonth - 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c;
	}

	/**
	 * 获取结束日期。
	 * 
	 * @param startDate
	 * @param endMonth
	 * @return
	 */
	private Date getEndDate(Date startDate, int endMonth) {
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		if (c.get(Calendar.MONTH) > endMonth - 1) {
			c.add(Calendar.YEAR, 1);
		}
		c.set(Calendar.MONTH, endMonth - 1);
		// @@ 获取月末日期。
		int day0 = c.get(Calendar.DAY_OF_MONTH);
		c.roll(Calendar.DAY_OF_MONTH, 31);
		int day1 = c.get(Calendar.DAY_OF_MONTH);
		c.set(Calendar.DAY_OF_MONTH, day0 + 31 - day1);
		return c.getTime();
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date curDate) {
		this.currentDate = curDate;
	}
}
