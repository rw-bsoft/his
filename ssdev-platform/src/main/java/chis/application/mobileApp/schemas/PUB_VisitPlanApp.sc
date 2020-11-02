<?xml version="1.0" encoding="UTF-8"?>

<entry  entityName="chis.application.mobileApp.schemas.PUB_VisitPlanApp" alias="随访计划" sort="beginDate" >
  <item id="planId" pkey="true" alias="计划识别" type="string" length="16" not-null="1" fixed="true" hidden="true">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item ref="b.personName" display="1" queryable="true"/>
  <item ref="b.sexCode" display="1" queryable="true"/>
  <item ref="b.idCard" display="1" queryable="true"/>
  <item ref="b.birthday" display="1" queryable="true"/>
  <item ref="b.mobileNumber" display="1" queryable="true"/>
  <item ref="c.regionCode_text" alias="网格地址" display="0"/>  
  <item id="beginDate" alias="开始日期" type="date" display="0"/>
  <item id="endDate" alias="结束日期" type="date" display="0"/>
  <item id="visitId" alias="随访记录ID" type="string" length="16" hidden="true"/>
  <item id="planDate" alias="计划日期" type="date"/>
  <item id="businessType" alias="计划类型" type="string" length="2" display="0">
    <dic id="chis.dictionary.planType"/>
  </item>
  <item id="empiId" alias="EMPIID" type="string" length="32" hidden="true"/>
  <relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="parent" entryName="chis.application.hr.schemas.EHR_HealthRecord"/>
	</relations>
</entry>
