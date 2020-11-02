<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.conf.schemas.ADMIN_DebilityChildrenConfigDetail" alias="体弱儿模块参数设置">
	<item id="instanceType" alias="随访分类 " type="string" not-null="true" width="150" hidden="true"/>
	<item id="debilityReason" alias="体弱儿分类" type="string" length="64"
		  not-null="true" virtual="true" width="150">
		<dic id="chis.dictionary.debilityDiseaseType"  />
	</item>
	<item id="planTypeCode" alias="计划类型" type="string" length="2"
		width="200" not-null="true">
		<dic id="chis.dictionary.planTypeDic"  filter="['or',['eq',['$','item.properties.frequency'],['s','2']],['eq',['$','item.properties.frequency'],['s','3']]]"/>
	</item>
	<item id="expression" alias="条件表达式"  type="string"  length="200" hidden="true"/>
</entry>