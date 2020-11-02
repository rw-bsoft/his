<?xml version="1.0" encoding="UTF-8"?>

<entry   alias="体弱儿童档案"  
  sort="a.phrId desc,a.createDate desc">
  <item id="recordId" alias="主键" type="string" length="16" width="160"
    pkey="true" display="0">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="phrId" alias="档案编号" type="string" length="30" width="160"
    queryable="true" display="0" fixed="true" />
  <item id="empiId" alias="empiid" type="string" length="32"
    fixed="true" notDefaultValue="true" display="0" />

  <item ref="b.personName" display="1" queryable="true" />
  <item ref="b.sexCode" display="1" queryable="true" />
  <item ref="b.birthday" display="1" queryable="true" />
  <item ref="b.idCard" display="1" queryable="true" />
  <item ref="b.phoneNumber" display="1" queryable="true" />
  <item ref="c.regionCode" display="0" queryable="true"  width="300"/>
  
  <item ref="d.homeAddress" display="1" alias="户籍地址" queryable="true"  width="300"/>
  <item ref="d.homeAddress_text" display="0" alias="户籍地址"  width="300"/>
  <item ref="d.cdhDoctorId"  queryable="true" not-null="1" fixed="true" defaultValue="%user.userId"  display="1" >
    <dic id="chis.dictionary.user02" render="Tree" onlySelectLeaf="true" />
  </item>
  <item ref="d.manaUnitId"  queryable="true" not-null="1"  fixed="true"/>
  <item ref="d.manaDoctorId" display="1" queryable="true" />
  <item id="phoneNo" alias="联系电话" type="string" length="16" />
  <item id="feedWay" alias="喂养方式" type="string" length="1">
    <dic id="chis.dictionary.feedWay" />
  </item>
  <item id="debilityReason" alias="体弱儿分类" type="string" length="64"
    queryable="true" not-null="true">
    <dic id="chis.dictionary.debilityDiseaseType" render="LovCombo" />
  </item>
  <item id="debilityOtherReason" alias="其他分类" type="string"
    length="64" fixed="true"/>
  <item id="deseaseReason" alias="病因" type="string" length="64"
    defaultValue="7" >
    <dic id="chis.dictionary.debilityChildrenDeseaseReason" render="LovCombo" />
  </item>
  <item id="deseaseOtherReason" alias="其他病因" type="string"
    length="64" />
  <item id="vestingCode" alias="转归" type="string" length="1">
    <dic id="chis.dictionary.vestingCode"  />
  </item>
  <item id="otherVesting" alias="其他转归" type="string" length="64" fixed="true"
    enableKeyEvents="true" />
  <item id="createUser" alias="建档医生" type="string" length="20"
    fixed="true" defaultValue="%user.userId" queryable="true" update="false">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"  parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="createDate" alias="建档日期" type="datetime"  xtype="datefield" fixed="true" update="false"
    defaultValue="%server.date.today" queryable="true" >
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="createUnit" alias="建档单位" type="string" length="20" 
    defaultValue="%user.manageUnit.id" >
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="closedUnit" alias="结案单位" type="string" length="20"
    fixed="true">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="closedDoctor" alias="结案医师" type="string" length="20"
    fixed="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"/>
  </item>
  <item id="closedDate" alias="结案日期" type="date" fixed="true" />
  <item id="closeFlag" alias="结案标识" type="string" length="1"
    display="0" defaultValue="n" queryable="true">
    <dic id="chis.dictionary.closeFlag" />
  </item>

  <item id="planTypeCode" alias="随访计划类型" type="string" length="2"
    display="0" />
  <item id="status" alias="档案状态" type="string" length="1"
    defaultValue="0" fixed="true" display="0">
    <dic>
      <item key="0" text="正常" />
      <item key="1" text="已注销" />
    </dic>
  </item>
  <item id="lastModifyUser" alias="修改人" type="string" length="20"
    hidden="true" defaultValue="%user.userId" display="1">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyDate" alias="修改日期"  type="datetime"  xtype="datefield"
    defaultValue="%server.date.today" display="1">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="lastModifyUnit" alias="修改单位" type="string" length="20"
    width="180" defaultValue="%user.manageUnit.id" display="1">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"/>
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="cancellationUser" alias="注销人" type="string" length="20"
    hidden="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="cancellationDate" alias="注销日期" type="date" hidden="true" />
  <item id="cancellationUnit" alias="注销单位" type="string" length="20"
    width="180" hidden="true">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="cancellationReason" alias="注销原因" type="string" length="1"
    hidden="true">
    <dic>
      <item key="1" text="死亡" />
      <item key="2" text="迁出" />
      <item key="3" text="失访" />
      <item key="4" text="拒绝" />
      <item key="6" text="作废" />
      <item key="9" text="其他" />
    </dic>
  </item>
  <item id="deadReason" alias="死亡原因" type="string" fixed="true" hidden="true"
    length="100" display="2" colspan="3" anchor="100%" />
  <item ref="c.regionCode_text" display="0" />
  <relations>
    <relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
    <relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
      <join parent="phrId" child="phrId" />
    </relation>
    <relation type="children" entryName="chis.application.cdh.schemas.CDH_HealthCard">
      <join parent="phrId" child="phrId" />
    </relation>
  </relations>
</entry>
