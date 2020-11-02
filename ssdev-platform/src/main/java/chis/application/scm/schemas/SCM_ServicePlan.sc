<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schemas.SCM_ServicePlan" alias="签约服务计划">
	<item id="planId" alias="计划编号" type="string" length="20" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="个人主索引" type="string" length="32"/>
	<item id="SCID" alias="签约记录编码" type="string" length="20"/>
	<item id="beginDate" alias="计划开始日期" type="date"/>
	<item id="planDate" alias="计划日期" type="date"/>
	<item id="endDate" alias="计划结束日期" type="date"/>
	<item id="planStatus" alias="计划状态" type="string" length="1" default="0">
		<dic>
			<item key="0" text="未做"/>
			<item key="1" text="已做"/>
			<item key="2" text="失访(未联系上签约人)"/>
			<item key="3" text="未访"/>
			<item key="4" text="过访(过期未处理)"/>
			<item key="8" text="解约"/>
			<item key="9" text="档案全注销"/>
		</dic>
	</item>
</entry>
