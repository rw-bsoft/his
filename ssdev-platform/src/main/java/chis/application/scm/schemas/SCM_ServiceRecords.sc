<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schemas.SCM_ServiceRecords" alias="签约服务记录">
	<item id="SRID" alias="服务记录编号" type="string" length="20" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="itemCode" alias="服务项目编码" type="string" length="20"/>
	<item id="empiId" alias="个人主索引" type="string" length="32"/>
	<item id="SCID" alias="签约记录编码" type="string" length="20"/>
	<item id="planId" alias="签约计划编码" type="string" length="20"/>
	<item id="taskId" alias="签约计划任务编号" type="string" length="20"/>
</entry>
