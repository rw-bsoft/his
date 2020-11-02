/**
 * @(#)AllocationEntity.java Created on 2014-6-3 上午10:28:37
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.log;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class AllocationEntity {
	/**
	 * 日志类型
	 */
	private String logType="OTH";
	/**
	 * 日志所存储的数据表名
	 */
	private String logStoreTable="chis.application.log.schemas.LOG_OTH_VindicateLog";

	/**
	 * 获得logType
	 * @return the logType
	 */
	public String getLogType() {
		return logType;
	}

	/**
	 * 设置logType
	 * @param logType the logType to set
	 */
	public void setLogType(String logType) {
		this.logType = logType;
	}

	/**
	 * 获得logStoreTable
	 * @return the logStoreTable
	 */
	public String getLogStoreTable() {
		return logStoreTable;
	}

	/**
	 * 设置logStoreTable
	 * @param logStoreTable the logStoreTable to set
	 */
	public void setLogStoreTable(String logStoreTable) {
		this.logStoreTable = logStoreTable;
	}
	
}
