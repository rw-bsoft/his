<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mpm.schemas.MPM_FieldData" alias="字段数据表（子表）">
	<item id="dataId" alias="编号" length="16" not-null="1" generator="assigned" pkey="true" type="string" width="160"
		hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="recordId" alias="主表Id" length="16"/>
	<item id="fieldId" alias="字段编号" length="30"/>
	<item id="id" alias="字段代码" length="30"/>
	<item id="fieldValue" alias="字段值" length="4000"/>
</entry>
