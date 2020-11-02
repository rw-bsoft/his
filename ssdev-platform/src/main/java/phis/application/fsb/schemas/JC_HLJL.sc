<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_HLJL" tableName="JC_HLJL" alias="护理记录" sort="JLBH asc">
	<item id="JLBH" alias="计划编号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
	</item>
	<item id="HLRQ" alias="护理日期" type="date"  defaultValue="%server.date.date" not-null="1"/>
	<item id="RHSJ" alias="入户时间" type="datetime" length="50" not-null="1" />
	<item id="CHSJ" alias="出户时间" type="datetime" length="100"/>
	<item id="HLCS" alias="护理措施" type="string" length="300" xtype="textarea" height="340"/>
	<item id="ZYH" alias="住院号" type="long" display="0"/>
	<item id="HSGH" alias="护士护士" type="string" display="0"/>
	<item id="JGID" alias="机构id" type="string" length="20" display="0" defaultValue="%user.manageUnit.id" not-null="1"/>
</entry>
