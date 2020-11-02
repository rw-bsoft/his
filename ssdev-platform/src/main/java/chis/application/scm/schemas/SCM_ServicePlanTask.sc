<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schemas.SCM_ServicePlanTask" alias="签约服务计划任务表">
	<item id="taskId" alias="任务编号" type="string" length="20" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="planId" alias="签约计划编号" type="string" length="20"/>
	<item id="empiId" alias="个人主索引" type="string" length="32"/>
	<item id="SCID" alias="签约记录编码" type="string" length="20"/>
	<item id="taskName" alias="任务名" type="string" length="100"/>
	<item id="moduleAppId" alias="任务模块路径" type="string" length="200">
		<dic id="chis.dictionary.moduleApp"/>
	</item>
	<item id="status" alias="任务状态" type="string" length="1" defaultValue="0">
		<dic>
			<item key="0" text="未完成"/>
			<item key="1" text="完成"/>
		</dic>
	</item>
	<item id="RVID" alias="档案记录或随访编号" type="string" length="50"/>
</entry>
