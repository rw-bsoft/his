<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_ZJJL" alias="折旧记录(WL_ZJJL)">
	<item id="ZJXH" alias="折旧序号" length="18" type="long" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" startPos="24" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20"/>
	<item id="KFXH" alias="库房序号" type="int" length="8"/>
	<item id="CWYF" alias="折旧月份" type="string" length="8"/>
	<item id="ZJRQ" alias="折旧日期" type="date"/>
	<item id="ZXRQ" alias="执行日期" type="date"/>
	<item id="CZGH" alias="操作员工" type="string" length="10"/>
</entry>
