<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mpm.schemas.MPM_FieldMasterRelation" alias="数据结构与模版的联系表">
	<item id="relationId" alias="编号" length="16" not-null="1" generator="assigned" pkey="true" type="string" width="160"
		hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="masterplateId" alias="模版编号" length="16"/>
	<item id="fieldId" alias="模版编号" length="16"/>
	<relations>
    	<relation type="parent" entryName="chis.application.mpm.schemas.MPM_FieldMaintain">
    	</relation>
    </relations>
</entry>