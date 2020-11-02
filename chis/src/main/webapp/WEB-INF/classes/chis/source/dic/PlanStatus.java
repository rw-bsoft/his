/*
 * @(#)BusinessType.java Created on 2012-3-9 下午4:23:32
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.dic;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 * 
 */
public abstract class PlanStatus {

	// @@ 以下是随访计划状态。
	// @@ 应访
	public static final String NEED_VISIT = "0";
	// @@ 已访
	public static final String VISITED = "1";
	// @@ 失访
	public static final String LOST = "2";
	// @@ 未访
	public static final String NOT_VISIT = "3";
	// @@ 过访
	public static final String OVER = "4";
	// ** 结案
	public static final String CLOSE = "8";
	// 注销
	public static final String WRITEOFF = "9";

}
