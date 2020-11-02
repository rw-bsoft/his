<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.conf.schemas.ADMIN_PsychosisConfigDetail" alias="精神病模块参数设置">
	<item id="instanceType" alias="随访分类 " type="string" not-null="true" width="150">
		<dic>
			<item key="3" text="稳定" />
			<item key="2" text="基本稳定" />
			<item key="1" text="不稳定" />
		</dic>
	</item>
	<item id="planTypeCode" alias="计划类型" type="string" length="2"
		width="200" not-null="true">
		<dic id="chis.dictionary.planTypeDic"  filter="['or',['eq',['$','item.properties.frequency'],['s','2']],['eq',['$','item.properties.frequency'],['s','3']]]"/>
	</item>
	<item id="expression" alias="条件表达式"  type="string"  length="200" hidden="true"/>
</entry> 