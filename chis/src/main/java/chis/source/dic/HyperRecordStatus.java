/**
 * @(#)HyperRecordStatus.java Created on 2012-3-15 下午5:00:05
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.dic;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public abstract class HyperRecordStatus {
	public static final String NORMAL = "0";// 正常

	public static final String WRITE_OFF = "1";// 已注销(核实注销)

	public static final String NOT_AUDIT = "2";// 注销(核实中)
	
}
