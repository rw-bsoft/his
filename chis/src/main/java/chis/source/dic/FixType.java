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
public abstract class FixType {

	// @@ 初次定级
	public static final String CREATE = "1";
	// @@ 维持原定级
	public static final String NO_CHANGE = "2";
	// @@ 不定期转级
	public static final String NON_FIX_DATE = "4";
	
}
