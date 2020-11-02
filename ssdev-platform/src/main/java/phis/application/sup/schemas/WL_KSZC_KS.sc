<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_KSZC_KS" tableName="WL_KSZC" alias="科室帐册(WL_KSZC)">
	<item id="JLXH" alias="记录序号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
	</item>
	<item id="KSDM" alias="科室代码" type="long" length="18" display="0"/>
	<item id="KSMC" alias="科室名称" length="18" virtual="true"/>
	<item id="PYDM" alias="拼音代码" length="18" queryable="true" virtual="true" />
</entry>
