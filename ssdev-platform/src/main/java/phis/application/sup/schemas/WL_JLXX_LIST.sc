<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_JLXX_LIST" tableName="WL_JLXX" alias="计量信息(WL_JLXX)">
	<item id="JLXH" alias="计量序号" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<value name="increaseId" defaultFill="0" type="increase" startPos="24"/>
		</key>
	</item>
	<item id="WZXH" alias="物资序号" type="long" length="18" virtual="true" display="0"/>
	<item id="CJXH" alias="生产厂家" type="long" length="18" virtual="true" display="0"/>
	<item id="WZMC" alias="物资名称" type="string" virtual="true" length="12"/>
	<item id="WZGG" alias="物资规格" type="string" virtual="true" length="12"/>
	<item id="WZDW" alias="物资单位" type="string" virtual="true" length="12"/>
	<item id="CJMC" alias="厂家名称" type="string" virtual="true" length="12" width="160"/>
	<item id="SL" alias="物资数量" type="int" virtual="true" length="12"/>
	<item id="WZJG" alias="物资价格" type="double" precision="4" virtual="true" length="12"/>
	<item id="KSDM" alias="科室名称" type="long" virtual="true" length="12">
		<dic id="phis.dictionary.department"/>
	</item>
</entry>
