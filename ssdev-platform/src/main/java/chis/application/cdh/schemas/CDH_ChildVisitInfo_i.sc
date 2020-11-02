<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mhc.schemas.MHC_BabyVisitInfo" alias="新生儿访视基本信息">
  <item id="babyId" alias="新生儿编号" type="string" length="16"
    not-null="1" generator="assigned" pkey="true" hidden="true">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="pregnantId" alias="孕妇档案编号" type="string" length="30"
    hidden="true" />
  <item id="empiId" type="string" length="32" display="0"/>	
  <item id="babyName" alias="婴儿姓名" type="string" length="30" />
  <item id="babyNo" alias="婴儿编号" type="string" length="20"
    display="0" />
  <item id="babySex" alias="婴儿性别" type="string" length="1" not-null="1">
    <dic id="chis.dictionary.gender" />
  </item>
  <item id="babyBirth" alias="婴儿出生日期" type="date" width="150" not-null="1"  maxValue="%server.date.today"/>
  <item id="babyIdCard" alias="婴儿身份证号"  type="string" length="20" width="160"   vtype="idCard" enableKeyEvents="true"/>
  <item id="certificateNo" alias="婴儿出生证号" type="string" length="10"
    width="150" />
  <item id="babyAddress" alias="婴儿家庭住址" type="string" length="100"
    width="250" />
  <item id="fatherEmpiId" type="string" display="0"/>
  <item id="fatherName" alias="父亲姓名" type="string" length="30"
    xtype="lookupfieldex" />
  <item id="fatherJob" alias="父亲职业" type="string" length="30"
    display="2">
    <dic id="chis.dictionary.jobtitle" onlySelectLeaf="true" />
  </item>
  <item id="fatherPhone" alias="父亲联系电话" type="string" length="30"
    display="2" />
  <item id="fatherBirth" alias="父亲出生日期" type="date" display="2"  maxValue="%server.date.today" />
  <item id="motherName" alias="母亲姓名" type="string" length="30" fixed="true"/>
  <item id="motherJob" alias="母亲职业" type="string" length="30"
    display="2" fixed="true">
    <dic id="chis.dictionary.jobtitle" onlySelectLeaf="true" />
  </item>
  <item id="motherPhone" alias="母亲联系电话" type="string" length="30"
    display="2" fixed="true" />
  <item id="motherBirth" alias="母亲出生日期" type="date" display="2"
    fixed="true"  maxValue="%server.date.today"/>
  <item id="motherCardNo" alias="母亲身份证号" type="string" length="18"
    display="2" fixed="true" />
  <item id="gestation" alias="出生孕周" type="int" />
  <item id="pregnancyDisease" alias="妊娠期疾病" type="string" length="2"
    display="2">
    <dic>
      <item key="1" text="糖尿病" />
      <item key="2" text="妊娠期高血压疾病" />
      <item key="3" text="其他" />
    </dic>
  </item>
  <item id="otherDisease" alias="其它疾病" type="string" length="30"
    display="2" />
  <item id="deliveryUnit" alias="助产机构名称" type="string" length="70"
    width="150" />
  <item id="birthStatus" alias="出生情况" type="string" length="6">
    <dic>
      <item key="1" text="顺产" />
      <item key="2" text="头吸" />
      <item key="3" text="产钳" />
      <item key="4" text="剖宫" />
      <item key="5" text="双多胎" />
      <item key="6" text="臀位" />
      <item key="7" text="其他" />
    </dic>
  </item>
  <item id="otherStatus" alias="其它出生情况" type="string" length="30"
    display="2" />
  <item id="asphyxia" alias="新生儿窒息" type="string" length="2"
    display="2">
    <dic id="chis.dictionary.haveOrNot" />
  </item>
  <item id="malforMation" alias="是否有畸型" type="string" length="2"
    display="2">
    <dic id="chis.dictionary.haveOrNot" />
  </item>
  <item id="malforMationDescription" alias="畸形描述" type="string"
    length="30" display="2" />
  <item id="hearingTest" alias="新生儿听力筛查" type="string" length="2"
    display="2">
    <dic>
      <item key="1" text="通过"></item>
      <item key="2" text="未通过"></item>
      <item key="3" text="未筛查"></item>
      <item key="4" text="不详"></item>
    </dic>
  </item>
  <item id="illnessScreening" alias="新生儿疾病筛查" type="string" length="2">
    <dic>
      <item key="1" text="甲低"></item>
      <item key="2" text="苯丙酮尿症"></item>
      <item key="3" text="其他遗传代谢病"></item>
    </dic>
  </item>
  <item id="otherIllness" alias="其他遗传代谢病" type="string" length="50" width="120"/>
  <item id="weight" alias="出生体重(kg)" type="bigDecimal" length="5"
    precision="2" width="120"/>
  <item id="length" alias="出生身长(cm)" type="bigDecimal" length="5"
    precision="2" width="120"/>
  <item id="inputUnit" alias="录入单位" type="string" length="20"
    update="false" fixed="true" width="120"
    defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="inputUser" alias="录入员工" type="string" length="20"
    update="false" fixed="true" defaultValue="%user.userId">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="inputDate" alias="录入日期"  type="datetime"  xtype="datefield" update="false"
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
  <item id="lastModifyDate" alias="最后修改日期"  type="datetime"  xtype="datefield"
    defaultValue="%server.date.today" display="1">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
</entry>
