/**
 * @(#)ModelDataOperationException.java Created on 2011-12-28 下午12:36:39
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */


package chis.source;

/**
 * @description Model层数据操作异常。
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */

public class ModelDataOperationException extends Exception{
	
	private static final long serialVersionUID = -4877607502563318481L;
	private int code = 0;
	
	public ModelDataOperationException() {
		super();
	}
	
	public ModelDataOperationException(String message) {
		super(message);
	}
	
	public ModelDataOperationException(Throwable t) {
		super(t);
	}
	
	public ModelDataOperationException(String message, Throwable t) {
		super(message, t);
	}
	
	public ModelDataOperationException(int code, String message) {
		this(message);
		this.code = code;
	}
	
	public ModelDataOperationException(int code, String message, Throwable t) {
		this(message, t);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
