<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_SSDM_WH" tableName="GY_SSDM" alias="手术代码维护">
	<item id="SSNM" alias="手术序号" display="0" type="long" not-null="1" generator="assigned" pkey="true" >
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="SSDM" alias="手术代码" type="string" queryable="true" length="10" not-null="1"/>
	<item id="SSMC" alias="手术名称"  type="string" length="160"/>
	<item id="SSDJ" alias="手术单价"  type="double" />
	<item id="ZJDM" alias="拼音码"  type="string" length="100" queryable="true" selected="true" target="SSMC" codeType="py"/>
	<item id="FYXH" alias="费用序号" display="0"  type="long" />
</entry>
