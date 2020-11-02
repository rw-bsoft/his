/**
 * @(#)ServiceProxy.java Created on 2010-9-6 下午03:17:51
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.interceptor;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;

/**
 * @description <ActionProxy/>的生成工厂。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class ActionProxyFactoryBean implements FactoryBean {

	private ActionProxy actionProxy = null;

	public ActionProxy getActionProxy() {
		return actionProxy;
	}

	public void setActionProxy(ActionProxy actionProxy) {
		this.actionProxy = actionProxy;
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	public Object getObject() throws Exception {
		Class<?> cls = actionProxy.getTarget().getClass();
		return Proxy.newProxyInstance(cls.getClassLoader(),
				cls.getInterfaces(), actionProxy);
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@SuppressWarnings("rawtypes")
	public Class getObjectType() {
		return actionProxy.getTarget().getClass();
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	public boolean isSingleton() {
		return false;
	}
}
