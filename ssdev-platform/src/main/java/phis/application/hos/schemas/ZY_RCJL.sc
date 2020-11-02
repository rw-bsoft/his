<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_RCJL" alias="住院临床病人入出记录">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="12" startPos="27"/>
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
	<item id="CZRQ" alias="操作日期" type="date" not-null="1"/>
	<item id="LCRQ" alias="临床日期" type="date" not-null="1"/>
	<item id="CZLX" alias="操作类型" type="int" length="2" not-null="1"/>
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1"/>
	<item id="BRKS" alias="病人科室" type="long" length="18" not-null="1"/>
	<item id="YJZYRS" alias="月结在院人数" type="int" length="6"/>
	<item id="BQPB" alias="病区判别" type="int" length="1" not-null="1"/>
	<item id="CYFS" alias="出院方式" type="int" length="4"/>
	<item id="BZXX" alias="备注信息" length="255"/>
</entry>
