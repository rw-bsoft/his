<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_CWTJ" alias="床位统计">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="12" startPos="27"/>
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
	<item id="CZRQ" alias="操作日期" type="date"/>
	<item id="CZLX" alias="操作类型" type="int" length="1"/>
	<item id="ZYH" alias="住院号" type="long" length="18"/>
	<item id="BRKS" alias="病人科室" type="long" length="18"/>
	<item id="YSYS" alias="原使用数" type="int" length="6"/>
	<item id="XSYS" alias="现使用数" type="int" length="6"/>
	<item id="BQPB" alias="病区判别" type="int" length="1"/>
</entry>
