<?xml version="1.0" encoding="UTF-8"?>
<entry  entityName="chis.application.pub.schemas.PUB_VisitPlan" alias="儿童工作列表" sort="a.empiId,a.planDate">
  <item id="planId" pkey="true" alias="计划序号" type="string" length="16"
    not-null="1" fixed="true" hidden="true"/>	
  <item id="empiId" type="string" display="1"/>
  <item id="recordId" type="string" display="0"/>
	 
  <item ref="b.personName" display="0" queryable="true" />
  <item ref="b.sexCode" display="0" queryable="true" />
  <item ref="b.mobileNumber" display="0" alias ="联系电话"  queryable="true" />

  <item id="planDate" alias="计划日期" type="date" width="160"/>
  <item id="earliestDate" alias="最早执行日期" type="date" virtual="true" width="160"/>
  <item id="lastDate" alias="最晚执行日期" type="date" virtual="true"  width="160"/>
  <item id="businessType" alias="任务名称" type="string" length="1" queryable="true" width="160">
    <dic>
      <item key="5" text="儿童询问" />
      <item key="6" text="体格检查"/>
      <item key="7" text="体弱儿随访"/>
    </dic>
  </item>
  <item id="extend1" alias="月龄" type="int" display="0"/>
	
  <item id="remainDays" alias="剩余天数" type="string" virtual="true" display="0" width="160"/>
  <item id="age" alias="月龄" type="string" virtual="true" width="160"/>	
	
  <item ref="c.cdhDoctorId" display="0"/>
  <item ref="c.homeAddress" display="0"/>
  <item ref="c.homeAddress_text" display="0"/>
  <item ref="d.regionCode" display="0" queryable="false" />
  <item ref="d.regionCode_text" display="0"/>
  <item ref="b.registeredPermanent" display="0"/>
	
  <relations>
    <relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
    <relation type="children" entryName="chis.application.cdh.schemas.CDH_HealthCard">
      <join parent="empiId" child="empiId" />
    </relation>
    <relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
      <join parent="empiId" child="empiId"/>	
    </relation>
  </relations>
</entry>