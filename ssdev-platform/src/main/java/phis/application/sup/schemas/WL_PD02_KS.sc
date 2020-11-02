<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_PD02_KS" tableName="WL_PD02" alias="盘点明细(WL_PD02)">
	<item id="JLXH" alias="记录序号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
	<item id="DJXH" alias="单据序号" type="long" display="0" length="18"/>
	<item id="WZXH" alias="物资序号" type="long" display="0" length="18"/>
	<item id="CJXH" alias="厂家序号" type="long" display="0" length="12"/>
	<item id="KSDM" alias="在用科室" type="long" fixed="true" length="8">
		<dic id="phis.dictionary.department" filter = "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true" searchField="PYCODE">
		</dic>
	</item>
	<item id="HSLB" alias="核算类别" type="long" fixed="true" virtual="true" length="12">
		<dic id="phis.dictionary.WL_HSLB_SJHS"  autoLoad="true"/>
	</item>
	<item id="WZMC" alias="名称" virtual="true" fixed="true" length="12" width="150"/>
	<item id="WZGG" alias="规格" virtual="true" fixed="true" length="12"/>
	<item id="WZDW" alias="单位" virtual="true" fixed="true" length="12"/>
	<item id="WZJG" alias="价格" type="double" fixed="true" length="18" precision="4"/>
	<item id="SCRQ" alias="生产日期" type="date" display="0"/>
	<item id="SXRQ" alias="失效日期" type="date" display="0"/>
	<item id="KCXH" alias="库存序号" type="long" length="18" display="0"/>
	<item id="ZBXH" alias="帐薄序号" type="long" length="18" display="0"/>
	<item id="KCSL" alias="库存数量" type="double" fixed="true" length="18" precision="2"/>
	<item id="KCJE" alias="库存金额" type="double" display="0" length="18" precision="4"/>
	<item id="PCSL" alias="盘存数量" type="double" fixed="true" length="18" precision="2"/>
	<item id="PCJE" alias="盘存金额" type="double" fixed="true" length="18" precision="4"/>
	<item id="GLFS" alias="管理方式" type="int" fixed="true" virtual="true" length="1" not-null="true">
		<dic id="phis.dictionary.wzglfs"  autoLoad="true"/>
	</item>
	<item id="CJMC" alias="生产厂家" virtual="true" fixed="true" length="1" width="160" not-null="true"/>
	<item id="WZPH" alias="物资批号" length="20" fixed="true"/>
	<item id="LRBZ" alias="录入标志" type="int" display="0" length="1"/>
</entry>
