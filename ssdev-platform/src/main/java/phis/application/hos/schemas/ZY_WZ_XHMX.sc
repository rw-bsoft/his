<?xml version="1.0" encoding="UTF-8"?>

<entry  entityName="WL_XHMX" alias="住院物资消耗明细(WL_XHMX)">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
	</item>
	<item id="BRXM" alias="病人姓名" length="30"/>
	<item id="BRID" alias="患者ID" type="long" display="0" length="18"/>
	<item id="XHRQ" alias="消耗日期" width="130" type="timestamp"/>
	<item id="KSDM" alias="科室" display="0" type="long" length="18"/>
	<item id="KSMC" alias="科室名称" width="130" virtual="true" length="10"/>
</entry>
