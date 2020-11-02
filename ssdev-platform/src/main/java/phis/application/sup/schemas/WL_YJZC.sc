<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_YJZC" alias="月结资产(WL_YJZC)">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20"/>
	<item id="JZXH" alias="结帐序号" type="long" length="18"/>
	<item id="KFXH" alias="库房序号" type="int" length="8"/>
	<item id="ZBLB" alias="帐薄类别" type="int" length="8"/>
	<item id="CWYF" alias="财务月份" length="6"/>
	<item id="ZBXH" alias="帐薄序号" type="long" length="18"/>
	<item id="WZXH" alias="物资序号" type="long" length="18"/>
	<item id="WZZT" alias="物资状态" type="int" length="2"/>
	<item id="ZYKS" alias="在用科室" type="long" length="18"/>
	<item id="CZYZ" alias="重置原值" type="double" length="18" precision="2"/>
</entry>
