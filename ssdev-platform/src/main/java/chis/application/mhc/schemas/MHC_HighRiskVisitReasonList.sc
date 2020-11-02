<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mhc.schemas.MHC_HighRiskVisitReasonList" alias="高危随访因素一览表" sort="a.pregnantId desc">
  <item id="recordID" alias="序号" length="16" pkey="true" type="string"
    not-null="1" fixed="true" hidden="true" generator="assigned">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item ref="b.personName" display="1" queryable="true" />
  <item ref="b.idCard" display="1" queryable="true" />
  <item ref="b.birthday" display="1" queryable="true" />
  <item ref="c.manaDoctorId" display="1" queryable="true" />
  <item ref="c.manaUnitId" display="1" queryable="true" width="200"/>
  <item ref="c.ownerArea" display="1" queryable="true"/>
  <item ref="c.homeAddress" display="1" queryable="true" width="300"/>
  <item ref="c.homeAddress_text"  display="0"/>
  <item ref="c.lastMenstrualPeriod" display="1" queryable="true"/>
  <item id="pregnantId" alias="孕妇档案编号" length="30" width="120"
    display="0" type="string" />
  <item id="visitId" alias="随访序号" length="16" width="120" display="0"
    type="string" />
  <item id="highRiskReasonId" alias="高危因素" length="1000" width="600"
    type="string">
    <dic id="chis.dictionary.highRiskScore" />
  </item>
  <item id="highRiskScore" alias="高危评分" type="int" width="70" />
  <item id="empiId" alias="EMPIID" length="32" width="120" display="0"
    type="string" />
  <item id="highRiskLevel" alias="高危等级" length="1" width="70"
    type="string" />
  <item id="frequence" alias="周期" type="int" width="60" display="0" />
  <item id="visitDate" alias="筛查日期" type="date" display="1" queryable="true"/>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    defaultValue="%user.userId" display="0">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
    defaultValue="%server.date.today" display="0">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <relations>
    <relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
    <relation type="children" entryName="chis.application.mhc.schemas.MHC_PregnantRecord">
      <join parent="pregnantId" child="pregnantId" />
    </relation>
  </relations>
</entry>
