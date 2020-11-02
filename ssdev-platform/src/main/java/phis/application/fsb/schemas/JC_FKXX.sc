<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_FKXX" alias="住院结算付款信息">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="20" startPos="1" />
		</key>
	</item>
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1"/>
	<item id="JSCS" alias="结算次数" type="int" length="3" not-null="1"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
	<item id="FKFS" alias="付款方式" type="long" length="18" not-null="1"/>
	<item id="FKJE" alias="付款金额" type="double" length="12" precision="2" not-null="1"/>
	<item id="FKHM" alias="付款号码" length="40"/>
</entry>
