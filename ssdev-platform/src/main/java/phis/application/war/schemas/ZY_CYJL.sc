<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_CYJL" alias="住院病人出院记录">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18"
				startPos="1000" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1"/>
	<item id="CYSJ" alias="出院时间" type="date"/>
	<item id="CYFS" alias="出院方式" type="int" length="4" not-null="1"/>
	<item id="CZLX" alias="操作类型" type="int" length="2" not-null="1"/>
	<item id="CZR" alias="操作人" length="10" not-null="1"/>
	<item id="CZRQ" alias="操作日期" type="date"/>
</entry>
