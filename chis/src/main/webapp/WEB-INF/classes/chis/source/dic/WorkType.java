/**
 * @(#)WorkType.java Created on 2012-5-25 下午7:19:25
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.dic;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class WorkType {
	public static final String PERSON_PREVIOUS_HISTORY = "01";// 完善个人既往史信息
	public static final String HYPERTENSION_YEAR_FIXGROUP = "02";// 高血压年度评估

	public static final String MOV_EHR_CONFIRM = "03"; // ** 确认档案迁移
	public static final String MOV_CDH_CONFIRM = "04"; // ** 确认儿童户籍地址迁移
	public static final String MOV_MHC_CONFIRM = "05"; // ** 确认孕妇户籍地址迁移
	public static final String MOV_BATCH_CONFIRM = "06"; // ** 确认批量迁移
	public static final String MOV_MANAGEINFO_CONFIRM = "07"; // ** 确认修改档案医生

	public static final String PSYCHOSIS_ANNUAL_ASSESSMENT = "08";// 精神病年度评估
	public static final String HEALTHCHECK_YEAR_WARN = "17";// 健康检查年度任务提醒
	public static final String HEALTHEDUCATION = "16";// 健康教育
	public static final String DEF_LIMB_EVALUATE = "10";// 残疾人评估提醒
	public static final String DEF_BRAIN_EVALUATE = "11";// 残疾人评估提醒
	public static final String DEF_INTELLECT_EVALUATE = "12";// 残疾人评估提醒
	public static final String DEF_LIMB_PLAN = "13";// 残疾人训练计划提醒
	public static final String DEF_BRAIN_PLAN = "14";// 残疾人训练计划提醒
	public static final String DEF_INTELLECT_PLAN = "15";// 残疾人训练计划提醒
	
	public static final String MDC_DIABETESRECORD = "18";// 糖尿病建档
	public static final String MDC_DIABETESRISKCONFIRM = "19";// 糖尿病高危核实///已经不再使用
	public static final String MDC_DIABETESRISKASSESSMENT = "20";// 糖尿病高危评估///已经不再使用
	
	public static final String MDC_HYPERTENSIONRECORD = "21";// 高血压建档
	public static final String MDC_HYPERTENSIONRISKCONFIRM = "22";// 高血压高危核实///已经不再使用
	public static final String MDC_HYPERTENSIONRISKASSESSMENT = "23";// 高血压高危评估///已经不再使用
	public static final String MDC_HYPERTENSIONFIXGROUP = "24";// 高血压分组评估

}
