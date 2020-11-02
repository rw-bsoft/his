<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_CSKC_KSZY_FORM" tableName="WL_CSKC" alias="初始库存">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<value name="increaseId" type="increase" startPos="24"/>
		</key>
	</item>
	<item id="WZMC" alias="物资名称" mode="remote" virtual="true" length="20"/>
	<item id="WZGG" alias="规格型号" virtual="true" fixed="true" length="8"/>
	<item id="WZDW" alias="物资单位" virtual="true" fixed="true" length="8"/>
	<item id="CJMC" alias="生产厂家" virtual="true" fixed="true" length="8"/>
	<item id="KFXH" alias="库房名称" type="int" fixed="true" length="8">
		<dic id="phis.dictionary.treasury" autoLoad="true"/>
	</item>
</entry>
