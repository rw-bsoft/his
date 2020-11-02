<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.pub.schemas.PUB_PlanInstance" alias="计划方案表" >
	<item id="instanceId" alias="方案编号" type="string" length="16"
		width="160" generator="assigned" pkey="true" display="0" not-null="1">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="25"/>
		</key>
	</item>
	<item id="instanceName" alias="方案名称"  type="string" length="60"  width="180" />
	<item id="instanceType" alias="方案类型"  type="string" length="2"  width="100" not-null="1"/>
	<item id="expression" alias="条件表达式"  type="string"  length="200" width="100" not-null="1"/>
	<item id="planTypeCode" alias="计划类型"  type="string"  length="2" colspan="2" width="300" />
</entry>
