<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cdh.schemas.CDH_DeadRegister_Module" tableName="CDH_DeadRegister" alias="儿童死亡登记表" sort="a.deadRegisterId desc">
  <item id="deadRegisterId" alias="死亡编号" pkey="true" type="string" length="16"
    not-null="1" fixed="true" generator="assigned"  width="160" display="0" >
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item ref="b.manaDoctorId" display="1" queryable="true" />
  <item id="deadNo" alias="死亡登记编号" type="string" length="8"
    not-null="1" width="110" />
  <item id="phrId" alias="档案编号"  type="string" length="30"
    fixed="true" generator="assigned" queryable="true" width="160" display="0">
  </item>
  <item id="idCard" alias="身份证号" type="string" length="20" width="160" fixed="true" queryable="true"
    />

  <item id="childName" alias="患儿姓名" type="string" length="20"
    queryable="true" not-null="1" update="fasle" />		
  <item id="childSex" alias="患儿性别" type="string" length="1" width="60"
    queryable="true" not-null="1" fixed="true">
    <dic id="chis.dictionary.gender" />
  </item>
  <item id="phoneNumber" alias="家庭电话" type="string" length="20"  queryable="true"/>
  <item id="childRegister" alias="患儿户籍" type="string" length="1"
    not-null="1" fixed="true">
    <dic id="chis.dictionary.registeredPermanent" />
  </item>	
  <item id="homeAddress" alias="户籍地址" length="25" anchor="100%"
    width="200" fixed="true" not-null="1">
    <dic id="chis.dictionary.areaGrid" includeParentMinLen="6" filterMin="10" minChars="4" filterMax="18" render="Tree" onlySelectLeaf="true" />
  </item>	
  <item id="homeAddress_text" length="200" display="0"/>
  <item id="cdhDoctorId" alias="儿保医生" type="string" length="20" 
    queryable="true" fixed="true">
    <dic id="chis.dictionary.user02" render="Tree" onlySelectLeaf="true" />
  </item>				
  <item id="empiId" alias="empiId" type="string" length="32"
    display="0" />
  <item id="manaUnitId" alias="管辖机构" type="string" length="20"
    fixed="true" width="180" queryable="true" display="1">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" lengthLimit="9" querySliceType="0"/>
  </item>
  <item id="oneYearLive" alias="居住一年以上" length="1" display="2"
    fixed="true">
    <dic id="chis.dictionary.yesOrNo" />
  </item>
  <item id="fatherName" alias="父亲姓名" type="string" length="20"
    display="2" fixed="true"/>
  <item id="motherName" alias="母亲姓名" type="string" length="20"
    display="2" fixed="true"/>
  <item id="fatherEmpiId" alias="父亲编号" type="string" length="32"
    display="0" />
  <item id="motherEmpiId" alias="母亲编号" type="string" length="32"
    display="0" />
  <item id="birthday" alias="出生日期" type="date" fixed="true"
    queryable="true" not-null="1" />
  <item id="birthWeight" alias="出生体重(kg)" type="bigDecimal" length="8"
    fixed="true" display="2" precision="2" not-null="1"/>
  <item id="measureMethod" alias="测量方法" type="string" length="1"
    not-null="1">
    <dic>
      <item key="1" text="测量"></item>
      <item key="2" text="估计"></item>
    </dic>
  </item>
  <item id="gestation" alias="出生孕周" type="int" display="2"
    fixed="true" not-null="1"/>
  <item id="birthAddress" alias="出生地点" type="string" length="2"
    not-null="1" display="2">
    <dic id="chis.dictionary.birthAddress" />
  </item>
  <item id="birthDefectsIn7" alias="7天内缺陷" type="string" length="7"
    display="0">
    <dic id="chis.dictionary.defectsType" />
  </item>
  <item id="birthDefectsOut7" alias="7天后缺陷" type="string" display="0"
    length="7">
    <dic id="chis.dictionary.defectsType" />
  </item>
  <item id="diagnoseUnit" alias="缺陷诊断机构" type="string" length="20"
    display="0">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="deathDate" alias="死亡日期" type="date" queryable="true"
    not-null="1" />
  <item id="detailAddress" alias="死亡详细地点" type="string" length="50"
    display="0" />
  <item id="diagnose1" alias="死亡诊断" type="string" length="500"
    not-null="1" display="2"  />
  <item id="deathYear" alias="死亡年龄" type="string" length="50"
    display="2" fixed="true" />
  <item id="deathReason" alias="死亡原因分类" type="string" length="2"
    not-null="1" display="2">
    <dic id="chis.dictionary.deathReason"></dic>
  </item>
  <item id="treatment" alias="死前治疗" type="string" length="1"
    not-null="1" display="2">
    <dic>
      <item key="1" text="住院"></item>
      <item key="2" text="门诊"></item>
      <item key="3" text="未治疗"></item>
    </dic>
  </item>
  <item id="diagnoseLevel" alias="死前诊断级别" type="string" length="2"
    not-null="1" display="2">
    <dic id="chis.dictionary.diagnoseLevel"></dic>
  </item>
  <item id="noTreatmentReason" alias="未处理原因" type="string" length="1"
    display="2" fixed="true">
    <dic>
      <item key="1" text="经济困难"></item>
      <item key="2" text="交通不便"></item>
      <item key="3" text="来不及送医院"></item>
      <item key="4" text="家长认为病情不严重"></item>
      <item key="5" text="风俗习惯"></item>
      <item key="6" text="其他"></item>
    </dic>
  </item>
  <item id="noTreatOtherReason" alias="其他原因" type="string"
    length="100" display="2" fixed="true" colspan="1" />
  <item id="reasonBasis" alias="死因诊断依据" type="string" length="1"
    not-null="1" display="2">
    <dic>
      <item key="1" text="病理尸检"></item>
      <item key="2" text="临床"></item>
      <item key="3" text="死后推断"></item>
    </dic>
  </item>
  <item id="deathAddress" alias="死亡地点" type="string" length="1"
    not-null="1" display="2">
    <dic>
      <item key="1" text="医院"></item>
      <item key="2" text="途中"></item>
      <item key="3" text="家中"></item>
    </dic>
  </item>
  <item id="inputDate" alias="填卡日期" type="datetime"  xtype="datefield" fixed="true" update="false"
    defaultValue="%server.date.today" >
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="inputUnit" alias="填卡单位" type="string" length="20"
    width="180" defaultValue="%user.manageUnit.id" fixed="true"  update="false"
    anchor="100%" colspan="2">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"/>
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="inputUser" alias="填卡人员" type="string" length="20"  update="false"
    fixed="true" defaultValue="%user.userId" queryable="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    defaultValue="%user.userId" display="1">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"/>
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
    defaultValue="%user.manageUnit.id"  display="1">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
    defaultValue="%server.date.today" display="1">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <relations>
    <relation type="parent" entryName="chis.application.hr.schemas.EHR_HealthRecord">
    </relation>
  </relations>
</entry>
