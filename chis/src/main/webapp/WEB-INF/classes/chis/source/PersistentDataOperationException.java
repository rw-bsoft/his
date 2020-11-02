/**
 * @(#)PersistentDataOperationException.java Created on Dec 1, 2009 10:32:29 AM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source;

/**
 * @description 持久数据操作异常。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class PersistentDataOperationException extends Exception {

	private static final long serialVersionUID = 1576230103398228659L;

	public PersistentDataOperationException() {
		super();
	}

	public PersistentDataOperationException(String message) {
		super(message);
	}

	public PersistentDataOperationException(String message, Throwable t) {
		super(message, t);
	}

	public PersistentDataOperationException(Throwable t) {
		super(t);
	}
}
