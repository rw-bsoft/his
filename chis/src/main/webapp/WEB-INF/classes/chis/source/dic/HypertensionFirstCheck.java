/**
 * @(#)HypertensionFirstCheck.java Created on 2012-3-19 上午10:24:08
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.dic;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HypertensionFirstCheck {
	// 不需要做首诊测压也不用做高血压核实。
	public static final int DO_HYPERTENSION_NON = 0;
	// 需要做首诊测压
	public static final int DO_HYPERTENSION_FIRST = 1;
	// 需要做高血压核实
	public static final int DO_HYPERTENSION_CHECK = 2;
	// 需要建立高血压档案。
	public static final int DO_HYPERTENSION_CREATE = 3;
	
	//**********首府测压核实结果(diagnosisType) 字典 *****************\\
	//未确定
	public static final String DIAGNOSISTYPE_INDETERMINATE = "0";
	//确诊
	public static final String DIAGNOSISTYPE_CONFIRMED = "1";
	//疑似
	public static final String DIAGNOSISTYPE_SUSPECTED = "2";
	//排除
	public static final String DIAGNOSISTYPE_EVACUANT = "3";
	
	
	
}
