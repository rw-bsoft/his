<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="RES_DataElement" alias="数据元">
  <item id="DataElementId" alias="数据元ID" type="int" not-null="1" generator="auto" pkey="true" display="0"/>
  <item id="DataStandardId" alias="数据标准ID" type="string" length="16" display="0">
  	<dic id="platform.dataset.dic.resDataStandard"/>
  </item>
  <item id="StandardIdentify" alias="卫生部标识符" type="string" length="50" not-null="1"/>
  <item id="CustomIdentify" alias="自定义标识符" type="string" length="50"/>
  <item id="DName" alias="名称" type="string" length="50" not-null="1"/>
  <item id="DataType" alias="数据类型" type="string" length="2" not-null="1">
  	<dic id="platform.dataset.dic.resdataType"/>
  </item>
  <item id="DataLength" alias="数据长度" type="string" length="15" />
  <item id="CodeSystem" alias="字典" type="string" length="50"/>
  <item id="Maximum" alias="最大值" type="string" length="16"/>
  <item id="Minimum" alias="最小值" type="string" length="16"/>
  <item id="StandardRule" alias="标准依据" type="string" length="100"/>
  <item id="LastModifytime" alias="最后修改时间" type="timestamp">
  	<set type="exp" run="server">['$','server.date.datetime']</set>
  </item>
  <item id="Category" alias="分类" type="int" not-null="1" colspan="2">
  	<dic id="platform.dataset.dic.resDataElementCata"/>
  </item>
  <item id="DComment" alias="说明" type="string" xtype="textarea" length="500" colspan="2"/>
</entry>
