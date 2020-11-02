<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_YJJL" alias="月结记录(WL_YJJL)">
	<item id="JZXH" alias="结帐序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" display="0" length="20"/>
	<item id="KFXH" alias="库房序号" type="int" display="0" length="8"/>
	<item id="ZBLB" alias="帐薄类别" type="int" display="0" length="8"/>
	<item id="CWYF" alias="财务月份" length="6"/>
	<item id="QSSJ" alias="起始时间" type="date"/>
	<item id="ZZSJ" alias="终止时间" type="date"/>
	<item id="JZSJ" alias="结帐时间" display="0" type="date"/>
	<item id="CSBZ" alias="初始标志" display="0" type="int"/>
</entry>
