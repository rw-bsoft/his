<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.log.schemas.LOG_HC_VindicateLog" alias="健康检查维护日志">
	<item id="logId" alias="日志编号" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="recordId" alias="记录编号" type="string" length="32"/>
	<item id="empiId" alias="empiId" type="string" length="32"/>
	<item id="logType" alias="日志类型" type="string" length="10"/>
	<item id="operateType" alias="操作类型" type="string" length="1">
		<dic id="chis.dictionary.operateType"/>
	</item>
	<item id="updateTable" alias="操作主表" type="string" length="200"/>
	<item id="createDate" alias="记录时间" type="datetime" update="false" fixed="true" queryable="true"/>
	<item id="createUser" alias="记录人" type="string" length="20" fixed="true" update="false" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="记录单位" type="string" length="20" update="false" width="180" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
</entry>
