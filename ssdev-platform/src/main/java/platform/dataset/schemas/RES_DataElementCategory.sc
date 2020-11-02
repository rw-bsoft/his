<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="RES_DataElementCategory" alias="数据元类别">
  <item id="DataElementCategoryId" alias="数据元类别ID" type="int" not-null="1" generator="auto" pkey="true" display="0"/>
  <item id="DataStandardId" alias="数据标准ID" type="string" length="16" not-null="1" display="0">
  	<dic id="platform.dataset.dic.resDataStandard"/>
  </item>
  <item id="StandardIdentify" alias="卫生部标识符" type="string" length="50"/>
  <item id="CustomIdentify" alias="自定义标识符" type="string" length="50" not-null="1"/>
  <item id="DName" alias="名称" type="string" length="50" not-null="1"/>
  <item id="DeType" alias="数据元类型" type="string" length="1" not-null="1" display="0">
  	<dic id="platform.dataset.dic.resDEType" />
  </item>
</entry>
