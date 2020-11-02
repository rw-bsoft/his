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
public abstract class VisitEffect {

	// @@ 继续随访。
	public static final String CONTINUE = "1";
	// @@ 暂时失访。
	public static final String LOST = "2";
	// @@ 暂时拒访。可规类为失访
	public static final String REPULSE = "3";
	// @@ 终止管理。
	public static final String END = "9";
	
}
