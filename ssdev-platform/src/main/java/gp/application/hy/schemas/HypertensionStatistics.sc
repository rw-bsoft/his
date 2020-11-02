<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="gp.application.hy.schemas.HypertensionStatistics" alias="高血压统计虚拟有">
	<item id="dataType" alias="数据类型" type="string" length="1">
		<dic>
			<item key="1" text="月"/>
			<item key="2" text="委"/>
			<item key="3" text="年"/>
		</dic>
	</item>
	<item id="time" alias="时间" type="string"/>
	<item id="recordRealityTarget" alias="管理人数实际目标值" type="string"/>
	<item id="recordMonthRatio" alias="管理人数月完成率" type="string"/>
	<item id="recordYearRatio" alias="管理人数年实际完成率" type="string"/>
	
	<item id="visitRealityTarget" alias="随访人次实际目标值" type="string"/>
	<item id="visitMonthRatio" alias="随访人次月完成率" type="string"/>
	<item id="visitYearRatio" alias="随访人次年实际完成率" type="string"/>
	<item id="visitPlanRatio" alias="随访人次年计划完成率" type="string"/>	
</entry>