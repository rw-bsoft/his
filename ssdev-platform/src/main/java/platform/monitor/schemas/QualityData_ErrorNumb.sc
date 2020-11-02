<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="QualityData_ErrorNumb" alias="错误记录产明细">
  <item id="QualityDataId" alias="数据质控ID" type="int" not-null="1" generator="auto" pkey="true" display="0"/>
  <item id="RecordClassifying" alias="记录分类" type="string" length="32" not-null="1" display="0"/>
  <item id="Authororganization" alias="创建机构" type="string" length="32" not-null="1" display="0"/>
  <item id="StatDate" alias="统计日期" type="date" not-null="1" display="0"/>
  <item id="StageType" alias="传输阶段" type="string" length="1" display="0"/>
  <item id="ErrorCategory" alias="错误记录" type="string" length="200" width="400"/>
  <item id="ErrorCount" alias="记录产生次数" type="int" width="100"/>
</entry>
