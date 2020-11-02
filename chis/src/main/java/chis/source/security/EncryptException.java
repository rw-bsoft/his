/*
 * @(#)EncryptException.java Created on 2011-7-15 下午4:09:51
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.security;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class EncryptException extends Exception {

	private static final long serialVersionUID = 8776250375937956683L;

	public EncryptException() {
		super();
	}

	public EncryptException(String msg) {
		super(msg);
	}

	public EncryptException(String msg, Throwable t) {
		super(msg, t);
	}

	public EncryptException(Throwable t) {
		super(t);
	}
}
