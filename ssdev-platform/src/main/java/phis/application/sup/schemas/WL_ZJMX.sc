<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_ZJMX" alias="折旧明细(WL_ZJMX)">
	<item id="JLXH" alias="记录序号" length="18" not-null="1" type="long" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" startPos="24" />
		</key>
	</item>
	<item id="ZJXH" alias="折旧序号" type="long" length="18"/>
	<item id="KFXH" alias="库房序号" type="int" length="8"/>
	<item id="ZBLB" alias="帐薄类别" type="long" length="8"/>
	<item id="WZXH" alias="物资序号" type="long" length="18"/>
	<item id="CJXH" alias="厂家序号" type="long" length="12"/>
	<item id="ZBXH" alias="帐薄序号" type="long" length="18"/>
	<item id="ZYKS" alias="在用科室" type="long" length="18"/>
	<item id="GZL" 	alias="工作量" type="int" length="8"/>
	<item id="ZJFF" alias="折旧方法" type="int" length="6"/>
	<item id="ZJJE" alias="折旧金额" type="double" length="18" precision="4"/>
	<item id="ZCXZ" alias="资产现值" type="double" length="18" precision="4"/>
	<item id="LJZJ" alias="累计折旧" type="double" length="18" precision="4"/>
	<item id="JLLX" alias="记录类型" type="int" length="1"/>
	<item id="LYXH" alias="资金来源" type="long" length="18"/>
	<item id="ZJRQ" alias="折旧日期" type="date"/>
	<item id="ZXRQ" alias="执行日期" type="date"/>
	<item id="ZJSM" alias="折旧说明" type="string" length="50"/>
</entry>
