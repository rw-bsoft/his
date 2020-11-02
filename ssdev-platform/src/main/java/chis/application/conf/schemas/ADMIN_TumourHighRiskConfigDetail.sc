<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.conf.schemas.ADMIN_TumourHighRiskConfigDetail" alias="肿瘤高危人群随访参数设置">
	<item id="instanceType"  alias="方案类型" type="string" hidden="true" not-null="true" defaultValue="15" display="0"/>
	<item id="group" alias="随访分组 " type="string" not-null="true" width="150">
		<dic id="chis.dictionary.tumourManagerGroup"/>
	</item>
	<item id="tumourType" alias="高危类别" type="string" length="2" not-null="1">
		<dic id="chis.dictionary.tumourHighRiskType"/>
	</item>
	<item id="planTypeCode" alias="计划类型" type="string" length="2"
		width="200" not-null="true">
		<dic id="chis.dictionary.planTypeDic"/>
	</item>
	<item id="expression" alias="条件表达式"  type="string"  length="200" hidden="true"/>
</entry> 