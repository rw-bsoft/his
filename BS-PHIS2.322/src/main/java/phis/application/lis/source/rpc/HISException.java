package phis.application.lis.source.rpc;

/**
 * @description
 * @author <a href="mailto:chengzx@bsoft.com.cn">chzhxiang</a>
 */
public class HISException extends Exception {

	private static final long serialVersionUID = 1L;

	public HISException() {
	}

	public HISException(String message) {
		super(message);
	}

	public HISException(String message, Throwable t) {
		super(message, t);
	}

	public HISException(Throwable t) {
		super(t);
	}
}
