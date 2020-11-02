<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schemas.SCM_ServiceItemsQuery" alias="签约服务项目表">
	<item id="itemName" alias="服务项目名" type="string" length="50" queryable="true"/>
	<item id="intendedPopulation" alias="适应人群" type="string" length="1" queryable="true">
		<dic id="chis.dictionary.intendedPopulation"/>
	</item>
	<item id="itemType" alias="项目类型" type="string" length="1" defaultValue="4" fixed="true" queryable="true">
		<dic>
			<item key="1" text="业务分类"/>
			<item key="2" text="项目分类"/>
			<item key="3" text="服务分类"/>
			<item key="4" text="服务项目"/>
		</dic>
	</item>
	<item id="isBottom" alias="是否服务项目" type="string" length="1" defaultValue="y" fixed="true" queryable="true">
		<dic id="chis.dictionary.yesOrNo" />
	</item>
<!--
	<item id="pyCode" alias="拼音编码" type="string" length="50" />
	<item id="serviceTimes" alias="服务次数" type="int" length="3"/>
	<item id="price" alias="价格" type="double" length="6" precision="2"/>
	<item id="realPrice" alias="实际价格" type="double" length="6" precision="2"/>
	<item id="startUsingDate" alias="启用时间" type="date" queryable="true"/>
	<item id="intro" alias="简介" type="string" length="2000"/>
	<item id="itemNature" alias="项目性质" type="string" length="1" queryable="true">
		<dic>
			<item key="1" text="公卫"/>
			<item key="2" text="医疗"/>
		</dic>
	</item>
	<item id="serviceTable" alias="业务表" type="string" length="200"/>
	<item id="serviceFields" alias="业务字段集" type="string" length="2000"/>
	<item id="moduleAppId" alias="任务模块路径" type="string" length="200"/>
	-->
</entry>
