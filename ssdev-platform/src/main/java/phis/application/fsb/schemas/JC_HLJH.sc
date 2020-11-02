<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_HLJH" tableName="JC_HLJH" alias="护理计划表" sort="JLBH asc">
	<item id="JLBH" alias="计划编号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
	</item>
  
	<item id="KSRQ" alias="开始日期" type="datetime"  defaultValue="%server.date.datetime" not-null="1"/>
	<item id="HLZD" alias="护理诊断" type="string" length="50" not-null="1" xtype="textarea" height="100"/>
	<item id="HLMB" alias="护理目标" type="string" length="100" xtype="textarea" height="100"/>
	<item id="HLCS" alias="护理措施" type="string" length="300" xtype="textarea" height="140"/>
	<item id="HLPJ" alias="护理评价" type="string" length="300" xtype="textarea" height="100"/>
	<item id="TZRQ" alias="停止日期" type="datetime"/>
	<item id="HLHS" alias="护士护士" type="string" display="0"/>
	<item id="ZYH" alias="住院号" type="long" display="0"/>
	<item id="JGID" alias="机构id" type="string" length="20" display="0" defaultValue="%user.manageUnit.id" not-null="1"/>
</entry>
