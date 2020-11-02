<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tr.schemas.MDC_QuestCriterionFieldMaintain " tableName="MPM_FieldMaintain" alias="数据结构维护" sort="a.fieldId asc">
	<item id="fieldId" alias="字段编号" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="alias" alias="字段名称" type="string" length="100"  width="300" not-null="1" colspan="3"/>
	<item id="useType" alias="适用类别" type="string" length="2" not-null="1">
		<dic id="chis.dictionary.PHQUseType" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item ref="b.masterplateId" display="0"/>
	<relations>
		<relation type="parent" entryName="chis.application.mpm.schemas.MPM_FieldMasterRelation">
			<join parent = "fieldId" child = "fieldId" />
		</relation>
	</relations>
</entry>