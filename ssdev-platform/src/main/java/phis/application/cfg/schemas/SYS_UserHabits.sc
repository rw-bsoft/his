<?xml version="1.0" encoding="UTF-8"?>
<entry id="SYS_UserHabits" alias="用户使用习惯">
	<item id="searchType" alias="默认查询方式" type="string"  virtual="true">
		<dic autoLoad="true">
			<item key="PYDM" text="拼音码"/>
			<item key="WBDM" text="五笔码"/>
			<item key="JXDM" text="角形码"/>
			<item key="BHDM" text="笔画码"/>
			<item key="QTDM" text="其它码"/>
		</dic>
	</item>
	<item id="matchType" alias="默认匹配方式" type="string" defaultValue="ALL"  virtual="true">
		<dic autoLoad="true">
			<item key="LEFT" text="左匹配"/>
			<item key="ALL" text="模糊匹配"/>
		</dic>
	</item>
</entry>