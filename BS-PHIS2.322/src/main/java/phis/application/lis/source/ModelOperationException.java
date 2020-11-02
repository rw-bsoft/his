package phis.application.lis.source;

/**
 * @description Model异常类
 * @author <a href="mailto:chengzx@bsoft.com.cn">chzhxiang</a>
 */
public class ModelOperationException extends Exception {
	private static final long serialVersionUID = 1L;
	private int code = 0;

	public ModelOperationException() {
	}

	public ModelOperationException(String message) {
		super(message);
	}

	public ModelOperationException(Throwable t) {
		super(t);
	}

	public ModelOperationException(String message, Throwable t) {
		super(message, t);
	}

	public ModelOperationException(int code, String message) {
		this(message);
		this.code = code;
	}

	public ModelOperationException(int code, String message, Throwable t) {
		this(message, t);
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
