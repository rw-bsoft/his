/**
 * @(#)ActionEntity.java Created on Nov 4, 2009 3:33:13 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ws;

import java.io.Serializable;
import java.util.Map;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class ActionEntity implements Serializable {

	private static final long serialVersionUID = 5123043997919513908L;

	private String actionId = null;

	private String actionName = null;

	private String cls = null;

	private Map<String, Object> properties;

	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	/**
	 * @return the properties
	 */
	public Map<String, Object> getProperties() {
		return properties;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public Object getProperty(String name) {
		return properties.get(name);
	}
}
