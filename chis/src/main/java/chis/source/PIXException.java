/**
 * 
 */
package chis.source;
/**
 * 
 */
import ctd.service.core.ServiceException;

/**
 * 
 * @description PIX调用产生的异常
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class PIXException extends ServiceException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PIXException(String msg, Throwable t) {
		super(msg, t);
	}
}
