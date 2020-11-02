<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="RES_DataStandard" alias="数据标准版本">
  <item id="DataStandardId" alias="数据标准标识" type="string" length="16" not-null="1" generator="assigned" pkey="true"/>
  <item id="DName" alias="别名" type="string" length="50" colspan="2" not-null="1"/>
  <item id="DVersion" alias="版本" type="string" length="16"/>
  <item id="PublishDate" alias="发布日期" type="date"/>
  <item id="Publisher" alias="发布者" type="string" length="32"/>
  <item id="Administrator" alias="管理者" type="string" length="32"/>
  <item id="Status" alias="状态" type="string" length="1" defaultValue="1">
  	<dic>
  		<item key="1" text="正常"/>
  		<item key="2" text="注销"/>
  	</dic>
  </item>
  <item id="DComment" alias="说明" type="string" length="200"/>
</entry>
