<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="QualityData_Main" alias="数据质控总表">
  <item id="QualityDataId" alias="数据质控ID" type="int" not-null="1" generator="auto" pkey="true"/>
  <item id="RecordClassifying" alias="记录分类" type="string" length="32" not-null="1"/>
  <item id="Authororganization" alias="创建机构" type="string" length="32" not-null="1"/>
  <item id="StatDate" alias="统计日期" type="date" not-null="1"/>
  <item id="Stage1Success" alias="医院 - 前置机(成功数量)" type="int"/>
  <item id="Stage1Fail" alias="医院 - 前置机(错误数量)" type="int"/>
  <item id="Stage2Success" alias="前置机 - 临时库(成功数量)" type="int"/>
  <item id="Stage2Fail" alias="前置机 - 临时库(错误数量)" type="int"/>
  <item id="Stage3Success" alias="临时库 - 中心库(成功数量)" type="int"/>
  <item id="Stage3Fail" alias="临时库 - 中心库(错误数量)" type="int"/>
  <item id="Stage1UploadTime" alias="医院 - 前置机 - 上报时间" type="date"/>
  <item id="Stage2UploadTime" alias="前置机 - 临时库 - 上报时间" type="date"/>
  <item id="Stage3UploadTime" alias="临时库 - 中心库 - 上报时间" type="date"/>
  <item id="UnFinishCount" alias="未上传数量" type="int"/>
</entry>
