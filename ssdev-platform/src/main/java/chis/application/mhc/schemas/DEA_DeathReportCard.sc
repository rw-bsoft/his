<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mhc.schemas.DEA_DeathReportCard" alias="孕产妇死亡报告卡">
  <item id="cardId" alias="编号" type="string" length="20" hidden="true"
    pkey="true" width="160" not-null="1" fixed="true"  generator="assigned" >
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="20" startPos="1"/>
    </key>
  </item>
  <item ref="b.personName" display="1" queryable="true"/>
  <item ref="b.sexCode" display="1" queryable="false"/>
  <item ref="b.birthday" display="1" queryable="true"/>
  <item ref="b.idCard" display="1" queryable="true"/>
  <item ref="c.manaUnitId" display="0"/>
  <item id="pregnantId" alias="孕妇档案号" type="string" length="16" display="0"/>
  <item id="empiId" alias="EMPIID" type="string" length="32" display="0" />
  <item id="register" alias="户口" type="string" length="1" not-null="1" queryable="true" fixed="true" >
    <dic id="chis.dictionary.registerType"/>
  </item>
  <item id="planInclusion" alias="计划内外" type="string" length="1" not-null="1" queryable="true">
    <dic>
      <item key="1" text="计划内"/>
      <item key="2" text="计划外"/>
    </dic>
  </item>
  <item id="age" alias="年龄" queryable="true" type="int" fixed="true"/>
  <item id="nationCode" alias="民族" type="string" length="2" defaultValue="01">
    <dic id="chis.dictionary.ethnic" />
  </item>
  <item id="educationCode" alias="文化程度" type="string" length="2">
    <dic id="chis.dictionary.education" render="Tree" onlySelectLeaf="true"/>
  </item>
  <item id="aveIncome" alias="家庭年人均收入(元)" type="string" length="1">
    <dic id="chis.dictionary.aveIncome"/>
  </item>
  <item id="domicileType" alias="居住地区" type="string" length="1" not-null="1">
    <dic>
      <item key="1" text="平原"/>
      <item key="2" text="山区"/>
      <item key="3" text="其他地区"/>
    </dic>
  </item>
  <item id="permanentRegionCode" alias="常住地址" length="25" not-null="1" queryable="true">
    <dic id="chis.dictionary.areaGrid" includeParentMinLen="6" filterMin="10" minChars="4" filterMax="18" render="Tree" />
  </item>
  <item id="permanentRegionCode_text" alias="常住地址文本" display="0"/>
  <item id="temporaryRegionCode" alias="暂住地址" length="25"  >
    <dic id="chis.dictionary.areaGrid" includeParentMinLen="6" filterMin="10" minChars="4" filterMax="18" render="Tree" />
  </item>
  <item id="temporaryRegionCode_text" alias="暂住地址文本" display="0"/>
  <item id="registerRegionCode" alias="户口地址" length="200" not-null="1" colspan="2" />
  <item id="registerRegionCode_Text" alias="户口地址" length="100"  hidden="true" fixed="true"/>
  <item id="gravidityTimes" type="int" alias="孕次" not-null="1" maxValue="99"/>
  <item id="deliverTimes" type="int" alias="产次" not-null="1" maxValue="99"/>
  <item id="abortTimes" type="int" alias="人工流产、引产次" maxValue="99"/>
  <item id="lastMenstrualPeriod" alias="末次月经" type="date" fixed="true" maxValue="%server.date.date"/>
  <item id="deadTime" alias="死亡时间" type="datetime"  not-null="1" maxValue="%server.date.datetime"/>
  <item id="deadPlace" alias="死亡地点" type="string" length="1" not-null="1">
    <dic>
      <item key="1" text="省(地、市)级医院"/>
      <item key="2" text="县(区)级医院"/>
      <item key="3" text="街道(乡镇)卫生院"/>
      <item key="4" text="村接生室"/>
      <item key="5" text="家中"/>
      <item key="6" text="途中"/>
      <item key="7" text="其他"/>
    </dic>
  </item>
  <item id="deliverPlace" alias="分娩地点" type="string" length="1">
    <dic>
      <item key="1" text="省(地、市)级医院"/>
      <item key="2" text="县(区)级医院"/>
      <item key="3" text="街道(乡镇)卫生院"/>
      <item key="4" text="村接生室"/>
      <item key="5" text="家中"/>
      <item key="6" text="途中"/>
      <item key="7" text="其他"/>
    </dic>
  </item>
  <item id="deliveryDate" alias="分娩时间" type="datetime"   maxValue="%server.date.datetime"/>
  <item id="deliverType" alias="分娩方式" type="string" length="1">
    <dic>
      <item key="0" text="未娩"/>
      <item key="1" text="自然产"/>
      <item key="2" text="阴道手术产"/>
      <item key="3" text="剖宫产"/>
    </dic>
  </item>
  <item id="newWayAccouche" alias="新法接生" type="string" length="1">
    <dic id="chis.dictionary.yesOrNo"/>
  </item>
  <item id="midwife" alias="接生者" type="string" length="1">
    <dic>
      <item key="1" text="医务人员"/>
      <item key="2" text="乡村医生"/>
      <item key="3" text="接生员"/>
      <item key="4" text="其他人员"/>
    </dic>
  </item>
  <item id="prenatalScreen" alias="产前检查" type="string" length="1">
    <dic id="chis.dictionary.yesOrNo"/>
  </item>
  <item id="firstCheckWeeks" alias="初检孕周" type="int" fixed="true" maxValue="99"/>
  <item id="checkingTimes" alias="产检次数" type="int" fixed="true" maxValue="99"/>
  <item id="deathDiagnosis1" alias="死因诊断1" length="50" not-null="1"/>
  <item id="deathDiagnosis2" alias="死因诊断2" length="50"/>
  <item id="deathDiagnosis3" alias="死因诊断3" length="50"/>
  <item id="deathDiagnosisAcc" alias="死因诊断依据" type="string" length="1" not-null="1">
    <dic>
      <item key="1" text="尸检"/>
      <item key="2" text="病理"/>
      <item key="3" text="临床"/>
      <item key="4" text="死后推断"/>
    </dic>
  </item>
  <item id="deadReasonType" alias="死因分类" type="string" length="2" not-null="1">
    <dic id="chis.dictionary.deadReasonCategory"/>
  </item>
  <item id="provinceAppraise" alias="省级评审结果" type="string" length="1">
    <dic minChars="1">
      <item key="y" text="可避免" mCode="1"/>
      <item key="n" text="不可避免" mCode="2"/>
    </dic>
  </item>
  <item id="proInfDeadFactor" alias="省级影响死亡因素" length="100" colspan="2">
    <dic id="chis.dictionary.deadFactor" render="LovCombo"/>
  </item>
  <item id="stateAppraise" alias="国家级评审结果" type="string" length="1">
    <dic minChars="1">
      <item key="y" text="可避免" mCode="1"/>
      <item key="n" text="不可避免" mCode="2"/>
    </dic>
  </item>
  <item id="staInfDeadFactor" alias="国家级影响死亡因素" length="100" colspan="2">
    <dic id="chis.dictionary.deadFactor" render="LovCombo"/>
  </item>
  <item id="deathSummay" alias="死亡病历摘要" length="1000" xtype="textarea" colspan="3"/>
  <item id="createPerson" alias="填卡人" length="20" queryable="true" not-null="1" defaultValue="%user.userId" update="false" >
    <dic id="chis.dictionary.user03" sliceType="4" render="Tree" onlySelectLeaf="true"  parentKey="%user.manageUnit.id" rootVisible="true"/>
  </item>
  <item id="createUnit" alias="填卡单位" length="22" fixed="true" queryable="true" not-null="1"  defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="9"  render="Tree"  
      parentKey="%user.manageUnit.id"  rootVisible="true" />	
  </item>
  <item id="status" alias="状态" type="string" length="1" defaultValue="0" display="0" fixed="true">
		<dic>
			<item key="0" text="正常" />
			<item key="1" text="已删除" />
		</dic>
  </item>
  <item id="createDate" alias="填卡日期" type="date" update="false" fixed="true" not-null="1" defaultValue="%server.date.date" queryable="true">
    <set type="exp">['$','%server.date.date']</set>
  </item>
  <item id="inputPerson" alias="录入人员" length="20" queryable="true" fixed="true" defaultValue="%user.userId">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"  parentKey="%user.manageUnit.id"/>
  </item>
  <item id="inputUnit" alias="录入机构" length="22" fixed="true" queryable="true" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />	
  </item>
  <item id="inputDate" alias="录入日期" type="date" update="false" fixed="true" defaultValue="%server.date.date" queryable="true">
    <set type="exp">['$','%server.date.date']</set>
  </item>
  <item id="administrativeId" alias="行政区划代码" type="string" length="20" update="false"
    display="0" defaultValue="%user.manageUnit.administrativeId">
    <set type="exp">['$','%user.manageUnit.administrativeId']</set>
  </item>
  <relations>
    <relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
     <relation type="parent" entryName="chis.application.mhc.schemas.MHC_PregnantRecord">
     	<join parent="pregnantId" child="pregnantId" />
     </relation>
  </relations>
</entry>
