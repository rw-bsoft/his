<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.pub.schemas.PUB_PlanType" alias="计划类型表" sort="planTypeCode">
  <item id="planTypeCode" alias="类型编号" type="string" length="2"
    width="160"  pkey="true" display="0" not-null="1" >
    <key>
      <rule name="increaseId" defaultFill="0" type="increase"  length="2"  startPos="12"/>
    </key>
  </item>
  <item id="planTypeName" alias="类型名称" type="string" length="20"  width="180" not-null="1"/>
  <item  id="cycle" alias="周期"  type="int"  width="100" not-null="1"/>
  <item id="frequency" alias="频率" type="string"  length="1" width="100" not-null="1">
    <dic>
      <item key="1" text="年"/>
      <item key="2" text="月"/>
      <item key="3" text="周"/>
      <item key="6" text="天"/>
    </dic>	
  </item>
  <item id="times" alias="发生次数"  type="int"  defaultValue="1" hidden="true" not-null="1" display="0"/>
  <item id="planTypeDesc" alias="描述" xtype="textarea" type="string"  length="50" colspan="3" anchor="100%" width="300" />
</entry>
