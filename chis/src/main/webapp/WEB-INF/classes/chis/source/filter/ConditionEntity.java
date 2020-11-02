/**
 * @(#)ConditionEntity.java Created on 2013-12-11 下午6:22:40
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.filter;

import java.io.Serializable;
import java.util.Map;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class ConditionEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2307709354846101377L;

	private String nodeKey = "";//节点在EMRViewNav配置文件中的key值
	private String nodeShowName = "";//节点显示（中文）
	
	private String prerequisite = null;// 先决条件

	private String dependencies = null;// 依赖节点,只有依赖节点状态为read,才判断该节点的状态，否则为hide

	private String dependNodeStatus = "read";// 依赖节点状态

	private boolean selectUnnormal = false;// 是否正常，确定是否执行无状态记录过虑

	private String entryName = null;// 查询的表

	private boolean accessControl = false;// 权限过虑，如果该登陆医生没有操作该人员数据的create节点将置为hide,该节点可以不配置,该功能慎用
	private String acExpAction="create";//权限过虑引用action(取引用ActionControl.xml中配置的那个表达式),与accessControl一起用，如果只有accessControl默认到"create"表达式
	private String acRuleId = "";//权限过虑时用的表达式ID ActionControl.xml中配置的rule的ID

	private Map<String, Object> condtions;// 数据过虑，如果已经存在该员数据节点状态为read，否则为create

	private String createReadField="";//EHR_RecordInfo表中对应档案 建档状态 字段。档案档案（GRAD）,高血压档案（GAO）
	
	/**
	 * 获得nodeKey
	 * @return the nodeKey
	 */
	public String getNodeKey() {
		return nodeKey;
	}

	/**
	 * 设置nodeKey
	 * @param nodeKey the nodeKey to set
	 */
	public void setNodeKey(String nodeKey) {
		this.nodeKey = nodeKey;
	}

	/**
	 * 获得nodeShowName
	 * @return the nodeShowName
	 */
	public String getNodeShowName() {
		return nodeShowName;
	}

	/**
	 * 设置nodeShowName
	 * @param nodeShowName the nodeShowName to set
	 */
	public void setNodeShowName(String nodeShowName) {
		this.nodeShowName = nodeShowName;
	}

	/**
	 * 获得prerequisite
	 * 
	 * @return the prerequisite
	 */
	public String getPrerequisite() {
		return prerequisite;
	}

	/**
	 * 设置prerequisite
	 * 
	 * @param prerequisite
	 *            the prerequisite to set
	 */
	public void setPrerequisite(String prerequisite) {
		this.prerequisite = prerequisite;
	}

	/**
	 * 获得dependencies
	 * 
	 * @return the dependencies
	 */
	public String getDependencies() {
		return dependencies;
	}

	/**
	 * 获得dependNodeStatus
	 * 
	 * @return the dependNodeStatus
	 */
	public String getDependNodeStatus() {
		return dependNodeStatus;
	}

	/**
	 * 设置dependNodeStatus
	 * 
	 * @param dependNodeStatus
	 *            the dependNodeStatus to set
	 */
	public void setDependNodeStatus(String dependNodeStatus) {
		this.dependNodeStatus = dependNodeStatus;
	}

	/**
	 * 设置dependencies
	 * 
	 * @param dependencies
	 *            the dependencies to set
	 */
	public void setDependencies(String dependencies) {
		this.dependencies = dependencies;
	}

	/**
	 * 获得selectUnnormal
	 * 
	 * @return the selectUnnormal
	 */
	public boolean isSelectUnnormal() {
		return selectUnnormal;
	}

	/**
	 * 设置selectUnnormal
	 * 
	 * @param selectUnnormal
	 *            the selectUnnormal to set
	 */
	public void setSelectUnnormal(boolean selectUnnormal) {
		this.selectUnnormal = selectUnnormal;
	}

	/**
	 * 获得entryName
	 * 
	 * @return the entryName
	 */
	public String getEntryName() {
		return entryName;
	}

	/**
	 * 获得accessControl
	 * @return the accessControl
	 */
	public boolean isAccessControl() {
		return accessControl;
	}

	/**
	 * 设置accessControl
	 * @param accessControl the accessControl to set
	 */
	public void setAccessControl(boolean accessControl) {
		this.accessControl = accessControl;
	}

	/**
	 * 获得acExpAction
	 * @return the acExpAction
	 */
	public String getAcExpAction() {
		return acExpAction;
	}

	/**
	 * 设置acExpAction
	 * @param acExpAction the acExpAction to set
	 */
	public void setAcExpAction(String acExpAction) {
		this.acExpAction = acExpAction;
	}

	/**
	 * 获得acRuleId
	 * @return the acRuleId
	 */
	public String getAcRuleId() {
		return acRuleId;
	}

	/**
	 * 设置acRuleId
	 * @param acRuleId the acRuleId to set
	 */
	public void setAcRuleId(String acRuleId) {
		this.acRuleId = acRuleId;
	}

	/**
	 * 设置entryName
	 * 
	 * @param entryName
	 *            the entryName to set
	 */
	public void setEntryName(String entryName) {
		this.entryName = entryName;
	}

	public Object getCondtions(String name) {
		return condtions.get(name);
	}

	/**
	 * 获得condtions
	 * 
	 * @return the condtions
	 */
	public Map<String, Object> getCondtions() {
		return condtions;
	}

	public void setCondtions(Map<String, Object> condtions) {
		this.condtions = condtions;
	}

	public String getCreateReadField() {
		return createReadField;
	}

	public void setCreateReadField(String createReadField) {
		this.createReadField = createReadField;
	}
}
