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
public abstract class IsFamily {

	public static final String FAMILY = "1";
	
	//不能作为底层使用的层次
	public static final String NOT_BOTTOM_CITY ="g";
	public static final String NOT_BOTTOM_COUNTRY ="f";
	//城市和农村的结尾标示
	public static final String END_TAG_CITY ="1";
	public static final String END_TAG_COUNTRY ="2";
	
	public static final String NOTFAMILY= "2"; 
	
}
