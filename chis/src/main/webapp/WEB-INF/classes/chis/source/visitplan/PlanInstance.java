/*
 * @(#)PlanInstance.java Created on 2012-2-10 下午2:44:27
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class PlanInstance {

	private String planTypeCode;
	private String expression;
	private String instanceType;

	public String getPlanTypeCode() {
		return planTypeCode;
	}

	public void setPlanTypeCode(String planTypeCode) {
		this.planTypeCode = planTypeCode;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}

}
