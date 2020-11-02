<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_YJJG" alias="月结结果(WL_YJJG)">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
	</item>
	<item id="JZXH" alias="结帐序号" type="long" length="18"/>
	<item id="ZBLB" alias="帐薄类别" type="int" length="8"/>
	<item id="WZXH" alias="物资序号" type="long" length="18"/>
	<item id="CJXH" alias="厂家序号" type="long" length="12"/>
	<item id="QCSL" alias="期初数量" type="double" length="18" precision="2"/>
	<item id="QCJE" alias="期初金额" type="double" length="18" precision="4"/>
	<item id="RKSL" alias="入库数量" type="double" length="18" precision="2"/>
	<item id="RKJE" alias="入库金额" type="double" length="18" precision="4"/>
	<item id="CKSL" alias="出库数量" type="double" length="18" precision="2"/>
	<item id="CKJE" alias="出库金额" type="double" length="18" precision="4"/>
	<item id="BSSL" alias="报损数量" type="double" length="18" precision="2"/>
	<item id="BSJE" alias="报损金额" type="double" length="18" precision="4"/>
	<item id="PYSL" alias="盘盈数量" type="double" length="18" precision="2"/>
	<item id="PYJE" alias="盘盈金额" type="double" length="18" precision="4"/>
	<item id="QMSL" alias="期末数量" type="double" length="18" precision="2"/>
	<item id="QMJE" alias="期末金额" type="double" length="18" precision="4"/>
	<item id="QCLSJE" alias="期初零售金额" type="double" length="18" precision="2"/>
	<item id="RKLSJE" alias="本期入库零售金额" type="double" length="18" precision="4"/>
	<item id="CKLSJE" alias="本期出库零售金额" type="double" length="18" precision="2"/>
	<item id="JCLSJE" alias="本期结存零售金额" type="double" length="18" precision="4"/>
</entry>
