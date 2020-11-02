<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_LRMX" alias="盘点录入明细(WL_LRMX)">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
	</item>
	<item id="LRXH" alias="录入单号" type="long" length="18"/>
	<item id="PDXH" alias="盘点单号" type="long" length="18"/>
	<item id="MXXH" alias="明细序号" type="long" length="18"/>
	<item id="WZXH" alias="物资序号" type="long" length="18"/>
	<item id="CJXH" alias="厂家序号" type="long" length="12"/>
	<item id="WZPH" alias="物资批号" length="20"/>
	<item id="SCRQ" alias="生产日期" type="date"/>
	<item id="SXRQ" alias="失效日期" type="date"/>
	<item id="KCXH" alias="库存序号" type="long" length="18"/>
	<item id="ZBXH" alias="帐薄序号" type="long" length="18"/>
	<item id="WZJG" alias="物资价格" type="double" length="18" precision="4"/>
	<item id="LSJG" alias="零售价格" type="double" length="18" precision="4"/>
	<item id="KCSL" alias="库存数量" type="double" length="18" precision="2"/>
	<item id="PCSL" alias="盘点数量" type="double" length="18" precision="2"/>
	<item id="PCJE" alias="盘存金额" type="double" length="18" precision="4"/>
	<item id="LSJE" alias="盘存金额" type="double" length="18" precision="4"/>
	<item id="KSDM" alias="科室代码" type="long" length="18"/>
	<item id="PDBZ" alias="盘点标志" type="int" length="1"/>
</entry>
