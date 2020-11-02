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
public abstract class ControlResult {

	// @@ 控制情况--优良。
	public static final String VERY_WELL = "1";
	// @@ 控制情况--尚可。
	public static final String JUST_SOSO = "2";
	// @@ 控制情况--不良。
	public static final String BAD = "3";
	// @@ 控制情况--新档案。
	public static final String NEW_DOC = "5";
	// @@ 控制情况--不定期转组。
	public static final String NON_FIX_DATE = "6";

}
