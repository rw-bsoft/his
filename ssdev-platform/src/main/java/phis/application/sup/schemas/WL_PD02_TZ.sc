<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_PD02_TZ" tableName="WL_PD02" alias="盘点明细(WL_PD02)">
	<item id="JLXH" alias="记录序号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
	<item id="DJXH" alias="单据序号" display="0" type="long" length="18"/>
	<item id="WZBH" alias="资产编号"  type="long" fixed="true" virtual="true" length="18" width="100"/>
	<item id="WZMC" alias="名称" virtual="true" fixed="true" length="12" width="150"/>
	<item id="WZGG" alias="规格" virtual="true" fixed="true" length="12"/>
	<item id="CJMC" alias="厂家" virtual="true" fixed="true" length="12" width="160"/>
	<item id="WZDW" alias="单位" virtual="true" fixed="true" length="12"/>
	<item id="WZXH" alias="物资序号" display="0" type="long" length="18"/>
	<item id="CJXH" alias="厂家序号" display="0" type="long" length="12"/>
	<item id="WZPH" alias="物资批号" display="0" length="20"/>
	<item id="SCRQ" alias="生产日期" display="0" type="date"/>
	<item id="SXRQ" alias="失效日期" display="0" type="date"/>
	<item id="KCXH" alias="库存序号" display="0" type="long" length="18"/>
	<item id="ZBXH" alias="帐薄序号" display="0" type="long" length="18"/>
	<item id="WZJG" alias="物资价格" display="0" type="double" length="18" precision="4"/>
	<item id="KCSL" alias="库存数量" display="0" type="double" length="18" precision="2"/>
	<item id="KCJE" alias="库存金额" display="0" type="double" length="18" precision="4"/>
	<item id="PCSL" alias="盘存数量" display="0" type="double" length="18" precision="2"/>
	<item id="PCJE" alias="盘存金额" display="0" type="double" length="18" precision="4"/>
	<item id="KSDM" alias="在用科室" type="long" fixed="true" length="8">
		<dic id="phis.dictionary.department" filter = "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true" searchField="PYCODE">
		</dic>
	</item>
	<item id="WZZT" alias="资产状态" virtual="true" type="long" fixed="true" length="8">
		<dic id="phis.dictionary.assetsta" autoLoad="true">
		</dic>
	</item>
	<item id="LRBZ" alias="录入标志" display="0" type="int" length="1"/>
	<item id="CZBZ" alias="是否存在" type="int" defaultValue="0" fixed="true" length="1">
		<dic id="phis.dictionary.confirm" autoLoad="true">
		</dic>
	</item>
</entry>
