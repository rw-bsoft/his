/**
 * @(#)CreateVisitPlanException.java Created on Nov 25, 2009 10:05:25 AM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.visitplan;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class CreateVisitPlanException extends Exception {

	private static final long serialVersionUID = -7422712765465838099L;

	private int code;

	public CreateVisitPlanException() {
		super();
	}

	public CreateVisitPlanException(String message) {
		super(message);
	}

	public CreateVisitPlanException(int code, String message) {
		this(message);
		this.code = code;
	}

	public CreateVisitPlanException(String message, Throwable t) {
		super(message, t);
	}

	public CreateVisitPlanException(int code, String message, Throwable t) {
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
