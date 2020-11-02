<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="QualityData_Error" alias="错误分类明细">
  <item id="QualityDataId" alias="数据质控ID" type="int" not-null="1" generator="auto" pkey="true"/>
  <item id="RecordClassifying" alias="记录分类" type="string" length="32" not-null="1"/>
  <item id="Authororganization" alias="创建机构" type="string" length="32" not-null="1"/>
  <item id="StatDate" alias="统计日期" type="date" not-null="1"/>
  <item id="StageType" alias="传输阶段" type="string" length="1"/>
  <item id="ErrorType" alias="错误类别" type="string" length="4"/>
  <item id="ErrorCount" alias="错误数量" type="int"/>
  <item id="UploadTime" alias="上报时间" type="date"/>
</entry>
