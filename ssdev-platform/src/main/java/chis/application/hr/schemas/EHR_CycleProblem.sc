<?xml version="1.0" encoding="UTF-8"?>

<entry entityName ="chis.application.hr.schemas.EHR_CycleProblem" alias="生命周期主要问题">
  <item id="cycleProblemId" alias="记录序号" type="string" length="16" hidden="1" not-null="1" generator="assigned" pkey="true"/>
  <item id="cycleId" alias="周期编号" type="string" length="2" hidden="1"/>
  <item id="problemName" alias="主要问题名称" type="string" length="80"/>
  <item id="description" alias="问题描述" type="string" length="200"/>
</entry>
