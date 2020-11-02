<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_KSMBDY" alias="科室模板订阅">
	<item id="ID" alias="科室模板订阅ID" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" startPos="24"/>
		</key>
	</item>
	<item id="SYS_OFFICE_ID" alias="机构科室ID" type="long" length="18" display="0"/>
	<item id="OFFICENAME" alias="科室" type="string" length="8"/>
	<item id="ORGANIZCODE" alias="机构" type="string" display="0"/>
	<item id="T_TABLE_NAME" alias="模板表名" type="string" display="0"/>
	<item id="TEMPLATE_ID" alias="模板ID" type="long" display="0"/>
</entry>
