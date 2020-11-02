<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_HLZD" tableName="EMR_HLZD" alias="护理诊断表" sort="ZDXH asc">
	<item id="ZDXH" alias="诊断序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
	</item>
  
	<item id="ZDLB" alias="诊断类别" type="int" length="12" not-null="1" width="80" queryable="true" selected="true">
		<dic autoLoad="true">
			<item key="1" text="交换"/>
			<item key="2" text="沟通"/>
			<item key="3" text="关系"/>
			<item key="4" text="赋予价值"/>
			<item key="5" text="选择"/>
			<item key="6" text="活动"/>
			<item key="7" text="感知"/>
			<item key="8" text="认知"/>
			<item key="9" text="感觉"/>
		</dic>
	</item>
	<item id="ZDMC" alias="诊断名称" type="string" length="50" not-null="1" width="260" queryable="true"/>
	<item id="PYDM" alias="拼音代码" type="string" length="10" not-null="1" target="ZDMC" codeType="py" display="2" queryable="true"/>
	<item id="MS" alias="详细描述" type="string" length="200" width="300" xtype="textarea" height="60"/>
</entry>
