<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mhc.schemas.MHC_WomanRecord" alias="妇女基本信息">
  <item id="phrId" alias="健康档案号" length="30" pkey="true" type="string"
    not-null="1" fixed="true" hidden="true" generator="assigned" queryable="true" />
  <item id="empiId" alias="EMPIID" length="32" />
  <item id="manaUnitId" alias="管辖机构" type="string" length="20"
    width="150" queryable="true">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
  </item>
  <item id="ownerArea" alias="孕妇归属地" width="160" length="16"
    defaultValue="1" not-null="1" queryable="true">
    <dic id="chis.dictionary.pregnantOwnerArea" />
  </item>
  <item id="manaDoctorId" alias="责任医生" type="string" length="20"
    width="160" not-null="1" queryable="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
  </item>
  <item id="mhcDoctorId" alias="妇保医生" type="string" length="20"
    not-null="1" width="160" queryable="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
  </item>
  <item id="menarcheAge" alias="初潮年龄" type="int"/>
  <item id="menstrualPeriod" alias="经期(天)"  type="int"/>
  <item id="cycle" alias="周期"  type="int"/>
  <item id="menstrualBlood" alias="月经量" type="string" length="1">
    <dic>
      <item key="1" text="多" />
      <item key="2" text="中" />
      <item key="3" text="少" />
    </dic>
  </item>
  <item id="dysmenorrhea" alias="痛经" type="string" length="1">
    <dic>
      <item key="1" text="无" />
      <item key="2" text="轻" />
      <item key="3" text="重" />
      <item key="4" text="中" />
    </dic>
  </item>
  <item id="gravidity" alias="孕次"  type="int" />
  <item id="vaginalDelivery" alias="阴道分娩次数" not-null="1" type="int" />
  <item id="abdominalDelivery" alias="剖宫产次数" not-null="1" type="int" />
  <item id="trafficFlow" alias="人工流产次数"  type="int"/>
  <item id="naturalAbortion" alias="自然流产次数"  type="int"/>
  <item id="odinopoeia" alias="中期引产次数"  type="int"/>
  <item id="preterm" alias="早产次数"  type="int"/>
  <item id="dystocia" alias="难产次数"  type="int"/>
  <item id="dyingFetus" alias="死胎次数"   type="int" />
  <item id="stillBirth" alias="死产次数"  type="int"/>
  <item id="abnormality" alias="生育畸形儿次数"  type="int"/>
  <item id="newbronDied" alias="新生儿死亡次数"  type="int"/>
  <item id="qweTimes" alias="药物流产次数"  type="int"/>
  <item id="ectopicpregnancy" alias="宫外孕次数"  type="int"/>
  <item id="vesicularMole" alias="葡萄胎次数"  type="int"/>
  <item id="gynecologyOPS" alias="妇科手术史" />
  <item id="modifyDate" alias="更新日期" type="date" />
  <item id="modifyRecordId" alias="更新档案号" type="string" length="20" />
  <item id="modifyUnit" alias="更新机构" length="16">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
  </item>
  <item id="createUnit" alias="录入机构" type="string" length="20"
    width="180" fixed="true" update="false" display="0"
    defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="createUser" alias="录入人" type="string" length="20" display="0"
    update="false" fixed="true" defaultValue="%user.userId">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="createDate" alias="录入时间" type="datetime"  xtype="datefield" update="false" display="0"
    fixed="true" defaultValue="%server.date.today">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="20" display="0" width="180" hidden="true" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    defaultValue="%user.userId" display="1">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
    defaultValue="%server.date.today" display="1">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
</entry>
