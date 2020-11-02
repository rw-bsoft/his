<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cdh.schemas.CDH_DefectRegister" alias="儿童缺陷登记" sort="a.inputDate desc">
  <item id="phrId" pkey="true" alias="档案编号" type="string" length="30"
    not-null="1" fixed="true" queryable="true" width="160" display="1" />

  <item ref="b.personName" display="1" queryable="true" />
  <item ref="b.sexCode" display="1" queryable="true" />
  <item ref="b.birthday" display="1" queryable="true" />
  <item ref="b.idCard" display="1" queryable="true" />
  <item ref="b.phoneNumber" display="1" queryable="true" />
  <item ref="c.regionCode" display="0" queryable="true"  width="300"/>
  <item ref="c.regionCode_text" display="0"  width="300"/>
  <item ref="d.homeAddress" display="1" alias="户籍地址" queryable="true"  width="300"/>
  <item  ref="d.homeAddress_text" display="0" alias="户籍地址"  width="300"/>
  <item ref="d.manaDoctorId" display="1" queryable="true" />
  <item ref="d.cdhDoctorId" display="1" queryable="true" />

  <item id="empiId" alias="empiId" type="string" length="32"
    not-null="1" display="0" />
  <item id="manaUnitId" alias="管辖机构" type="string" length="20"
    display="1" width="180" fixed="true" not-null="1" queryable="true">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" lengthLimit="9" querySliceType="0"/>
  </item>
  <!-- 缺陷儿母亲情况 -->
  <item id="motherEmpiId" alias="母亲编号" type="string" length="32"
    display="0" group="母亲信息"/>
  <item id="motherName" alias="母亲姓名" type="string" length="32"
    display="2" virtual="true" group="母亲信息" fixed="true"/>
  <item id="hospitalNo" alias="住院号" type="string" length="10"
    display="2"   group="母亲信息"/>
  <item id="pregnancyTimes" alias="孕次" type="int" display="2"  group="母亲信息" />
  <item id="birthTimes" alias="产次" type="int" display="2"   group="母亲信息"/>
  <item id="residenceType" alias="常住地址" type="string" length="1" 
    display="2"  group="母亲信息">
    <dic>
      <item key="1" text="城镇"></item>
      <item key="2" text="乡村"></item>
    </dic>
  </item>
  <item id="postalAddress" alias="通讯地址" type="string" length="25"
    display="2" colspan="2"    group="母亲信息"/>
  <item id="postCode" alias="邮编" type="string" length="6" display="2"
    hidden="true"   group="母亲信息"/>
  <item id="homeIncome" alias="家族年人均收入" type="string" length="1"
    display="2"  group="母亲信息">
    <dic id="chis.dictionary.aveIncome"/>
  </item>
  <item id="literacy" alias="文化程序" type="string" length="2"
    display="2"  group="母亲信息">
    <dic id="chis.dictionary.education" />
  </item>
  <item id="consanguineousMarriage" alias="近亲婚配" type="string"
    display="2" length="1" defaultValue="n"  group="母亲信息">
    <dic id="chis.dictionary.yesOrNo" />
  </item>
  <item id="consanguineousRelations" alias="近亲婚配关系" type="string"
    display="2" length="2" fixed="true"  group="母亲信息">
    <dic id="chis.dictionary.relaCode1" onlySelectLeaf="true"></dic>
  </item>

  <!--  缺陷儿情况	 -->
  <item id="gestation" alias="胎龄" type="int"  />
  <item id="birthWeight" alias="体重（克）" type="bigDecimal" length="8"
    display="2" precision="2"  />
  <item id="twins" alias="胎数" type="string" length="1" >
    <dic>
      <item key="1" text="单胎"></item>
      <item key="2" text="双胎"></item>
      <item key="3" text="多胎"></item>
    </dic>
  </item>
  <item id="twinsType" alias="双胞胎类别" type="string" length="1"
    display="2" fixed="true" >
    <dic>
      <item key="1" text="同卵"></item>
      <item key="2" text="异卵"></item>
    </dic>
  </item>
  <item id="vestingCode" alias="转归" type="string" length="1" >
    <dic>
      <item key="1" text="活产"></item>
      <item key="2" text="死胎"></item>
      <item key="3" text="死产"></item>
      <item key="4" text="七天内死亡"></item>
      <item key="9" text="其他"></item>
    </dic>
  </item>
  <item id="inducedAbortion" alias="缺陷治疗性引产" type="string" display="2"
    length="1" >
    <dic id="chis.dictionary.yesOrNo" />
  </item>
  <item id="defectName" alias="缺陷名称" type="string" length="40"
    display="2" fixed="true"  />
  <item id="birthAddress" alias="出生地点" type="string" length="2" >
    <dic id="chis.dictionary.birthAddress" />
  </item>
  <item id="diagnoseBasis" alias="诊断依据" type="string" length="1"
    display="2" >
    <dic>
      <item key="1" text="临床诊断"></item>
      <item key="2" text="超声检查"></item>
      <item key="3" text="尸体解剖"></item>
      <item key="4" text="生化检查"></item>
      <item key="5" text="染色体检测"></item>
      <item key="6" text="其他"></item>
    </dic>
  </item>
  <item id="otherBasis" alias="其它诊断依据" type="string" length="20"
    display="2" fixed="true"  />
  <item id="deformityConfirmDate" alias="畸形确诊时间" type="string"
    length="1" width="110" >
    <dic>
      <item key="1" text="产前"></item>
      <item key="2" text="产后七天内"></item>
      <item key="3" text="产后42天内"></item>
      <item key="4" text="儿童一周岁内 "></item>
      <item key="5" text="儿童一周岁后"></item>
    </dic>
  </item>
  <item id="antenatalWeeks" alias="产前(周)"   type="int"  />

  <!-- 出生缺陷诊断 -->
  <item id="defectDiagnose" alias="出生缺陷诊断" type="string" length="100" >
    <dic id="chis.dictionary.defectsType" render="LovCombo"></dic>
  </item>
  <item id="otherDiagnose" alias="其它缺陷诊断" type="string" length="20"
    display="2" fixed="true"  />
  <item id="illFlag" alias="孕早期患病" type="string" length="10" >
    <dic  render="LovCombo">
      <item key="1" text="发烧（＞38℃）"></item>
      <item key="2" text="病毒感染"></item>
      <item key="3" text="糖尿病"></item>
      <item key="9" text="其他"></item>
    </dic>
  </item>
  <item id="infection" alias="病毒类型" type="string" length="20"
    display="2" fixed="true"  />
  <item id="otherIllness" alias="其它患病描述" type="string" length="20"
    display="2" fixed="true"  />
  <item id="medicineFlag" alias="服药" type="string" length="1"
    defaultValue="n" >
    <dic id="chis.dictionary.yesOrNo" />
  </item>
  <item id="sulfa" alias="磺胺类" type="string" length="20" fixed="true"
    display="2"  />
  <item id="antibiotics" alias="抗生素" type="string" length="20"
    display="2" fixed="true" />
  <item id="contraceptives" alias="避孕药" type="string" length="20"
    display="2" fixed="true" />
  <item id="otherMedicine" alias="其它服药描述" type="string" length="20"
    display="2" fixed="true" />
  <item id="otherFactors" alias="接触其他有害因素" type="string" length="1" >
    <dic>
      <item key="1" text="饮酒"></item>
      <item key="2" text="农药"></item>
      <item key="3" text="射线"></item>
      <item key="4" text="化学制剂"></item>
      <item key="9" text="其他"></item>
    </dic>
  </item>
  <item id="drink" alias="饮酒量" type="string" length="20" fixed="true"
    display="2"  />
  <item id="pesticides" alias="农药名称" type="string" length="20"
    display="2" fixed="true" />
  <item id="ray" alias="射线名称" type="string" length="20" fixed="true"
    display="2"  />
  <item id="chemicals" alias="化学制剂名称" type="string" length="20"
    display="2" fixed="true" />
  <item id="others" alias="其他因素" type="string" length="20" fixed="true"
    display="2" />


  <!-- 家庭史 -->
  <item id="stillbirth" alias="死胎例数" type="int" display="2"  />
  <item id="miscarriage" alias="自然流产例数" type="int" display="2"  />
  <item id="defectiveChild" alias="缺陷儿例数" type="int" display="2"  />
  <item id="defect1" alias="缺陷名1" type="string" length="100"
    display="2" >
    <dic id="chis.dictionary.defectsType"  render="LovCombo"></dic>
  </item>
  <item id="defect2" alias="缺陷名2" type="string" length="100"
    display="2" >
    <dic id="chis.dictionary.defectsType"  render="LovCombo"></dic>
  </item>
  <item id="defect3" alias="缺陷名3" type="string" length="100"
    display="2"  >
    <dic id="chis.dictionary.defectsType"  render="LovCombo"></dic>
  </item>
  <item id="geneticDefect1" alias="遗传缺陷名1" type="string" display="2"
    length="20" >
    <dic id="chis.dictionary.defectsType"></dic>
  </item>
  <item id="relations1" alias="与缺陷儿关系1" type="string" length="2"
    display="2" fixed="true"  >
    <dic id="chis.dictionary.relaCode1" onlySelectLeaf="true"></dic>
  </item>
  <item id="geneticDefect2" alias="遗传缺陷名2" type="string" display="2"
    length="20"  >
    <dic id="chis.dictionary.defectsType"></dic>
  </item>
  <item id="relations2" alias="与缺陷儿关系2" type="string" length="2"
    display="2" fixed="true"  >
    <dic id="chis.dictionary.relaCode1" onlySelectLeaf="true"></dic>
  </item>
  <item id="geneticDefect3" alias="遗传缺陷名3" type="string" display="2"
    length="20"  >
    <dic id="chis.dictionary.defectsType"></dic>
  </item>
  <item id="relations3" alias="与缺陷儿关系3" type="string" length="2"
    display="2" fixed="true"  >
    <dic id="chis.dictionary.relaCode1" onlySelectLeaf="true"></dic>
  </item>
  <item id="inputUnit" alias="填卡单位" type="string" length="20"
    width="180" defaultValue="%user.manageUnit.id" fixed="true" update="false"
    hidden="true">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"/>
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="inputUser" alias="填卡人" type="string" length="20" update="false"
    hidden="true" fixed="true" defaultValue="%user.userId"
    queryable="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"  parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="inputDate" alias="填卡日期"  type="datetime"  xtype="datefield" fixed="true" update="false"
    hidden="true" defaultValue="%server.date.today" queryable="true" >
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    defaultValue="%user.userId" display="1">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
    defaultValue="%user.manageUnit.id"  display="1">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期"   type="datetime"  xtype="datefield"
    defaultValue="%server.date.today" display="1">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
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
