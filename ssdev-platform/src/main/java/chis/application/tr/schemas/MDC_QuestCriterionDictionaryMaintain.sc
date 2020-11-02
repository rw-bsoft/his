<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tr.schemas.MDC_QuestCriterionDictionaryMaintain" tableName="MPM_DictionaryMaintain" alias="数值值域维护">
	<item id="dicId" alias="编号" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="fieldId" alias="字段名" type="string" length="16" display="0"/>
	<item id="keys" alias="值" type="string" length="10" not-null="1"/>
	<item id="text" alias="值含义" type="string" length="100" not-null="1" colspan="2"/>
</entry>