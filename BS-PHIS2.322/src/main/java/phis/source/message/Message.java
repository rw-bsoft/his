package phis.source.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6196645039949728808L;
	/**
	 * 业务类型 1.对应字典文件，1000指定为系统消息
	 */
	private String businessId;
	
	private String title;
	/**
	 * 消息内容
	 */
	private String body;
	/**
	 * 提醒方式 1.右下角提醒(类似QQ业务的提醒) 2.顶部提醒(目前PHIS系统中提醒方式)
	 * 3.调用JS方法(主动刷新数据等操作,具体功能有JS模块提供,消息系统调用统一接口) 9.系统消息
	 */
	private String remindMode;
	private String moduleId;
	private Map<String, Object> keywords = new HashMap<String, Object>();// 业务关键字，用于消息过滤
	private Map<String,Object> exProp = new HashMap<String,Object>(); //扩展属性
	public static final String MSG_BOTTOM = "1";
	public static final String MSG_TOP = "2";
	public static final String MSG_SYSTEM = "9";

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}
	
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String message) {
		this.body = message;
	}

	public String getRemindMode() {
		return remindMode;
	}

	public void setRemindMode(String remindMode) {
		this.remindMode = remindMode;
	}

	public Map<String, Object> getKeywords() {
		return keywords;
	}

	public void setKeywords(Map<String, Object> keywords) {
		this.keywords = keywords;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public Map<String, Object> getExProp() {
		return exProp;
	}

	public void setExProp(Map<String, Object> exProp) {
		this.exProp = exProp;
	}

	
}
