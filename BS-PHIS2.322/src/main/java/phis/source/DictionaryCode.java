/**
 * @(#)DictionaryCode.java Created on 2012-1-9 下午04:31:08
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.source;

/**
 * @description 用于定义一些常用的字典数据项
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public abstract class DictionaryCode {

	public static final int VISIT_RESULT_SATISFIED = 1;// @@ 随访结果满意
	public static final int VISIT_RESULT_DISSATISFIED = 2;// @@ 随访结果不满意

	public static final String VISIT_RESULT_CURING = "1";// @@ 正在治疗。

	// @@ 以下是随访计划状态。
	public static final String VISIT_PLAN_NEED_VISIT = "0";// @@ 应访
	public static final String VISIT_PLAN_VISITED = "1";// @@ 已访
	public static final String VISIT_PLAN_LOST = "2";// @@ 失访
	public static final String VISIT_PLAN_NOT_VISIT = "3";// @@ 未访
	public static final String VISIT_PLAN_OVER = "4";// @@ 过访
	public static final String VISIT_PLAN_WRITEOFF = "9";// 注销

	public static final String OPTION_YES = "1"; // chb 标志:是
	public static final String OPTION_NO = "2"; // chb 标志：否

	// chb 健康档案状态
	public static final String HEALTH_STATUS_NOMAL = "0";// 正常
	public static final String HEALTH_STATUS_CANCEL = "1";// 已注销
	public static final String HEALTH_STATUS_AUDIT = "2";// 未审核

	// @@ 结案管理标志，已结案
	public static final String END_MANAGE_FLAG_YES = "1";
	// @@ 结案管理标志，未结案
	public static final String END_MANAGE_FLAG_NO = "0";

	// **既往史类别--疾病史**
	public static final String PASTHIS_TYPECODE_SCREEN = "05";
	// **既往史类别--残疾**
	public static final String PASTHIS_TYPECODE_DEFORMITY = "14";
	// **既往史类别--过敏物质**
	public static final String PASTHIS_TYPECODE_ALLERGIC = "03";

	// **既往史--疾病史--编码--其他疾病**
	public static final String PASTHIS_SCREEN_CODE = "0512";
	// chb既往史--残疾--编码--无残疾**
	public static final String PASTHIS_NOTDEFORMITY_CODE = "1401";
	// **既往史--残疾--编码--其他残疾**
	public static final String PASTHIS_DEFORMITY_CODE = "1412";
	// **既往史--过敏--编码--其他过敏物质**
	public static final String PASTHIS_ALLERGIC_CODE = "0305";

	// @@ 以下是随访计划类型。
	public static final String BUSINESS_TYPE_GXY = "1";
	public static final String BUSINESS_TYPE_TNB = "2";
	public static final String BUSINESS_TYPE_ZL = "3";
	public static final String BUSINESS_TYPE_LNR = "4";
	// 儿童询问
	public static final String BUSINESS_TYPE_CD_IQ = "5";
	// 体格检查
	public static final String BUSINESS_TYPE_CD_CU = "6";
	// @@ 体弱儿童随访计划类型。
	public static final String BUSINESS_TYPE_CD_DC = "7";
	// @@ 孕妇随访。
	public static final String BUSINESS_TYPE_MATERNAL = "8";
	// @@ 孕妇高危随访。
	public static final String BUSINESS_TYPE_PREGNANT_HIGH_RISK = "9";
	// @@ 精神病随访。
	public static final String BUSINESS_TYPE_PSYCHOSIS = "10";
	// @@ 高血压询问。
	public static final String BUSINESS_TYPE_HYPERINQUIRE = "11";

	// 儿童体格检查评价标准
	public static final String appraise_zhonggao = "1";
	public static final String appraise_zhongshang = "2";
	public static final String appraise_shang = "3";
	public static final String appraise_shangshang = "4";
	public static final String appraise_zhongdi = "-1";
	public static final String appraise_zhongxia = "-2";
	public static final String appraise_xia = "-3";
	public static final String appraise_xiaxia = "-4";
	public static final String appraise_nomatch = "0";

	// chb 网格地址相关
	// 是否是户节点
	public static final String AREA_ISFAMILYNODE = "1";
	public static final String AREA_NOTFAMILYNODE = "2";
	// 是否是最底层
	public static final String AREA_ISBOTTOMNODE = "1";
	public static final String AREA_NOTBOTTOMNODE = "2";

}
