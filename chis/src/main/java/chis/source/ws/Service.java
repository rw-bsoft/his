/**
 * @(#)Service.java Created on 2009-10-23 上午11:31:53
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ws;

/**
 * 
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public interface Service {

	public static final String RES_CODE = "x-response-code";
	public static final String RES_MESSAGE = "x-response-message";
	
	/**
	 * @param request
	 * @return
	 */
	public String execute(String request);
}
