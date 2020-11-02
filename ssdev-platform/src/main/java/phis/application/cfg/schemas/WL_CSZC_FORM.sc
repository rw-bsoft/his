<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_CSZC_FORM" tableName="WL_CSZC" alias="初始帐册">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<value name="increaseId" type="increase" startPos="24"/>
		</key>
	</item>
	<item id="WZMC" alias="资产名称" mode="remote" virtual="true" length="20"/>
	<item id="WZGG" alias="规格型号" virtual="true" fixed="true" length="8"/>
	<item id="WZDW" alias="物资单位" virtual="true" fixed="true" length="8"/>
	<item id="KFXH" alias="库房名称" type="int" fixed="true" length="8">
		<dic id="phis.dictionary.treasury" autoLoad="true"/>
	</item>
	<item id="TZRQ" alias="添置日期" type="date" not-null="1" defaultValue="%server.date.date"/>
	<item id="ZCYZ" alias="资产原值" type="double" length="12" precision="2"/>
	<item id="WHYZ" alias="外汇原值" type="double" length="12" precision="2"/>
	<item id="HBDW" alias="货币单位" type="int" length="5">
		<dic id="phis.dictionary.currency" autoLoad="true"/>
	</item>
	<item id="CJMC" alias="生产厂家" virtual="true" fixed="true" length="8" colspan="2"/>
	<item id="DWMC" alias="供应商" virtual="true" not-null="1" mode="remotedw" length="12" colspan="2"/>	
</entry>
