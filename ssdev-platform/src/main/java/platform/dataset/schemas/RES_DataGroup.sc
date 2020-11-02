<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="RES_DataGroup" alias="数据组">
  <item id="DataGroupId" alias="数据组ID" type="int" not-null="1" generator="auto" pkey="true" display="0"/>
  <item id="DataStandardId" alias="数据标准ID" type="string" length="16" not-null="1" width="150" display="0">
  	<dic id="platform.dataset.dic.resDataStandard"/>
  </item>
  <item id="GroupIdentify" alias="标识符" type="string" length="50" width="220" not-null="1"/>
  <item id="DName" alias="名称" type="string" length="50" width="220" not-null="1"/>
  <item id="ForceIdentify" alias="强制性标识" type="string" length="1" not-null="1" defaultValue="0">
  	<dic id="platform.dataset.dic.resForceIdentify"/>
  </item>
  <item id="FrequencyNumber" alias="频率" type="int" defaultValue="1">
  	<dic>
		<item key="1" text="1次" />
		<item key="2" text="多次" />
	</dic>
  </item>
  <item id="ParentGroupId" alias="所属数据组" type="int">
  	<dic id="platform.dataset.dic.resDataGroup"/>
  </item>
</entry>
