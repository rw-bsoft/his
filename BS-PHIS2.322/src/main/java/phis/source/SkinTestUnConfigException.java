/**
 * @(#)PersistentDataOperationException.java Created on Dec 1, 2009 10:32:29 AM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.source;

/**
 * @description 皮试收费对应项目没有设置。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class SkinTestUnConfigException extends Exception {

	private static final long serialVersionUID = 1576230103368228659L;

	public SkinTestUnConfigException() {
		super();
	}

	public SkinTestUnConfigException(String message) {
		super(message);
	}

	public SkinTestUnConfigException(String message, Throwable t) {
		super(message, t);
	}

	public SkinTestUnConfigException(Throwable t) {
		super(t);
	}
}
