<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hr.schemas.EHR_LifeCycle" alias="生命周期" version="1332124044000" filename="E:\MyProject\BZWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\ehr/EHR_LifeCycle.xml">
  <item id="cycleId" alias="周期编号" type="string" length="2" not-null="1" generator="assigned" pkey="true"/>
  <item id="cycleName" alias="周期名称" type="string" length="20"/>
  <item id="startAge" alias="起始年龄" type="int"/>
  <item id="endAge" alias="终止年龄" type="int"/>
</entry>
