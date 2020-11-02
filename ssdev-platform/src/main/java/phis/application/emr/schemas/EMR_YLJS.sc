<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_YLJS" alias="医疗角色表">
  <item id="JSXH" alias="医疗角色序号" length="18" type="long" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
  <item id="JSMC" alias="医疗角色名称" length="25" width="150" not-null="1"/>
  <item id="JSLX" alias="医疗角色类型" type="string" length="1" width="100" not-null="1">
  	<dic>
  		<item key="0" text="公用" />
		<item key="1" text="医生" />
		<item key="2" text="护士" />
		<item key="9" text="其它" />
  	</dic>
  </item>
  <item id="JSJB" alias="医疗角色级别" length="2" type="int" defaultValue="0" width="100" align="left"/>
  <item id="JSSM" alias="医疗角色说明" length="127" width="300" xtype="textarea" height="80"/>
</entry>