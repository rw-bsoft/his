<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mhc.schemas.MHC_PregnantRecord"   alias="批量修改管理医生孕妇档案查询页面"
  sort="a.createDate desc">
  <item id="pregnantId" alias="孕妇档案号" type="string" length="16"  width="160"  queryable="true"  generator="assigned" >
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item ref="b.personName" display="1" queryable="true" />
  <item ref="b.sexCode" display="1" queryable="true" />
  <item ref="b.birthday" display="1" queryable="true" not-null="0"/>
  <item ref="b.idCard" display="1" queryable="true" />
  <item ref="b.mobileNumber" display="1" queryable="true" />
  <item ref="c.regionCode" display="0" queryable="true" />
  <item id="empiId" alias="EMPIID" length="32" not-null="1" display="0" />
  <item id="phrId" alias="健康档案编号" length="30" width="160" display="1"/>
  <item id="pregnantBookId" alias="孕册号" length="50" width="160" queryable="true"/>
  <item id="mhcDoctorId" alias="妇保医生" type="string" length="20"
    width="160"  update="false" queryable="true">
    <dic id="chis.dictionary.user03" render="Tree" onlySelectLeaf="true"  parentKey="%user.manageUnit.id"/>
  </item>
  <item id="manaUnitId" alias="管辖机构" type="string" length="20"  queryable="true" width="180" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"   onlySelectLeaf="true" lengthLimit="9" querySliceType="0"  render="Tree"  parentKey="%user.manageUnit.id" rootVisible="false"/>
  </item>
  <item id="restRegionCode" alias="产休地" length="25" display="2"
    colspan="2"  update="false" width="200">
    <dic id="chis.dictionary.areaGrid" minChars="4" includeParentMinLen="6" filterMin="10" filterMax="18" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="restRegionCode_text" type="string" length="200" display="0"/>
  <item id="ownerArea" alias="孕妇归属地" width="160" length="16"  queryable="true"  update="false" >  
    <dic id="chis.dictionary.pregnantOwnerArea" />
  </item>
  <item id="homeAddress" alias="户籍地址" length="25" 
    colspan="2" update="false"  width="200">
    <dic id="chis.dictionary.areaGrid" minChars="4" includeParentMinLen="6" filterMin="10" filterMax="18" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="homeAddress_text" type="string" length="200" display="0"/>
  <item id="manaDoctorId" alias="责任医生" type="string" length="20" width="160"  not-null="1" update="false"  fixed="true">
    <dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="realRegionCode" alias="居(暂)住地址" length="21" colspan="2"
    update="false" hidden="true" width="200">
    <dic id="chis.dictionary.areaGrid" minChars="4" render="Tree" includeParentMinLen="6" filterMin="10" filterMax="18"
      parentKey="%user.role.regionCode" onlySelectLeaf="true" />
  </item>
  <item id="realRegionCode_text" type="string" length="200" display="0"/>
  <item id="residenceCode" alias="居(暂)住证号" width="160" length="20"
    type="string"  queryable="true"/>
  <item id="residencePermit" alias="办居住证日期" width="160" type="date"
    display="2" fixed="true" />
  <item id="menarcheAge" alias="初潮年龄" not-null="1" type="int"
    width="160" />
  <item id="menstrualPeriod" alias="经期(天)" display="2" not-null="1"
    width="160" type="int" />
  <item id="cycle" alias="周期" display="2" not-null="1" type="int"
    width="160" />
  <item id="menstrualBlood" alias="月经量" type="string" length="1"
    width="160" not-null="1" display="2">
    <dic>
      <item key="1" text="多" />
      <item key="2" text="中" />
      <item key="3" text="少" />
    </dic>
  </item>
  <item id="dysmenorrhea" alias="痛经" type="string" length="1"
    width="160" not-null="1" display="2">
    <dic>
      <item key="1" text="无" />
      <item key="2" text="轻" />
      <item key="3" text="重" />
      <item key="4" text="中" />
    </dic>
  </item>
  <item id="lastMenstrualPeriod" alias="末次月经时间" type="date"
    width="160" not-null="1" anchor="100%" maxValue="%server.date.today" colspan="2"/>
  <item id="dateOfPrenatal" alias="预产期" type="date" not-null="1"
    width="160" fixed="true" />
  <item id="gravidity" alias="孕次" not-null="1" type="int" />
  <item id="vaginalDelivery" alias="阴道分娩次数" not-null="1" type="int" />
  <item id="abdominalDelivery" alias="剖宫产次数" not-null="1" type="int" />
  <item id="trafficFlow" alias="人工流产次数" display="2" not-null="1"
    type="int" defaultValue="0" />
  <item id="naturalAbortion" alias="自然流产次数" display="2" not-null="1"
    type="int" defaultValue="0" />
  <item id="qweTimes" alias="药物流产次数" display="2" not-null="1"
    type="int" defaultValue="0" />
  <item id="odinopoeia" alias="中期引产次数" display="2" not-null="1"
    type="int" defaultValue="0" />
  <item id="preterm" alias="早产次数" display="2" not-null="1" type="int"
    defaultValue="0" />
  <item id="dystocia" alias="难产次数" display="2" not-null="1" type="int"
    defaultValue="0" />
  <item id="dyingFetus" alias="死胎次数" display="2" not-null="1" type="int"
    defaultValue="0" />
  <item id="stillBirth" alias="死产次数" display="2" not-null="1" type="int"
    defaultValue="0" />
  <item id="abnormality" alias="畸形儿次数" display="2" not-null="1"
    type="int" defaultValue="0" />
  <item id="newbronDied" alias="死亡儿次数" display="2" not-null="1"
    type="int" defaultValue="0" />
  <item id="ectopicpregnancy" alias="宫外孕次数" display="2" not-null="1"
    type="int" defaultValue="0" />
  <item id="vesicularMole" alias="葡萄胎次数" display="2" not-null="1"
    type="int" defaultValue="0" />
  <item id="preGestationDate" alias="前妊娠终止日" type="date" display="2" />
  <item id="preGestationMode" alias="终止方式" length="20" display="2">
    <dic id="chis.dictionary.gestationMode" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="preDeliveryDate" alias="前次分娩日期" type="date" display="2" />
  <item id="preDeliveryMode" alias="分娩方式" length="20" display="2">
    <dic id="chis.dictionary.deliveryType" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="weight" alias="基础体重(kg)" length="8" precision="2"
    type="double" display="2" not-null = "1"/>
  <item id="height" alias="身高(cm)" display="2" type="double" not-null = "1"/>
  <item id="sbp" alias="收缩压(mmHg)" display="2" type="int" minValue="10"
    maxValue="500" enableKeyEvents="true" validationEvent="false" not-null = "1" />
  <item id="dbp" alias="舒张压(mmHg)" display="2" type="int" minValue="10"
    maxValue="500" enableKeyEvents="true" validationEvent="false" not-null = "1" />
  <item id="gestationNeuropathy" alias="妊娠并发症史" length="20"
    display="2" colspan="2" />
  <item id="pastHistory" alias="既往病史" length="20" display="2" >
    <dic render="LovCombo">
      <item key="1" text="无" />
      <item key="2" text="心脏病" />
      <item key="3" text="肾脏疾病" />
      <item key="4" text="肝脏疾病" />
      <item key="5" text="高血压" />
      <item key="6" text="贫血" />
      <item key="7" text="糖尿病" />
      <item key="8" text="其他" />
    </dic>
  </item>
  <item id="otherPastHistory" alias="其他既往病史" length="100" display="2"
    colspan="2" fixed="true" />
  <item id="unusualBone" alias="异常孕产史" length="100" display="2"
    colspan="2" not-null="1" defaultValue="无" />
  <item id="familyHistory" alias="家族史" length="10" display="2">
    <dic render="LovCombo">
      <item key="1" text="遗传性疾病史" />
      <item key="2" text="精神疾病史" />
      <item key="3" text="其他" />
    </dic>
  </item>
  <item id="otherFamilyHistory" alias="其他家族史" length="200" display="2"
    colspan="2" fixed="true" />
  <item id="operationHistory" alias="手术史" length="100" display="2"
    colspan="2" fixed="true" />
  <item id="personHistory" alias="个人史" length="20" display="2">
    <dic render="LovCombo">
      <item key="1" text="吸烟" />
      <item key="2" text="饮酒" />
      <item key="3" text="服用药物" />
      <item key="4" text="接触有毒有害物质" />
      <item key="5" text="接触放射线" />
      <item key="6" text="其他" />
    </dic>
  </item>
  <item id="otherPersonHistory" alias="其他个人史" length="200" display="2"
    colspan="2" fixed="true" />
  <item id="gynecologyOPS" alias="妇科手术史" length="100" display="2"
    colspan="2" />
  <item id="allergicHistory" alias="过敏史" length="100" display="2"
    fixed="true" colspan="2" />
  <item id="husbandEmpiId" alias="丈夫EMPIID" length="32"  display="0"/>
  <item id="husbandPhrId" alias="丈夫健康档案" length="30" hidden="true" />
  <item id="husbandFamilyHistory" alias="丈夫家族史" length="100"
    fixed="true" display="2" colspan="2" />
  <item id="gestationalWeeks" alias="建册孕周" fixed="true" not-null="1"
    type="int" width="160" />
  <item id="createUnit" alias="建册单位" type="string" length="20" update="false"    width="180"      >
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="createUser" alias="建册医生" type="string" length="20"   queryable="true">
    <dic id="chis.dictionary.user03" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
  </item>
  <item id="createDate" alias="建册日期" type="date" queryable="true"  />
  <item id="status" alias="状态" length="1" hidden="true"
    defaultValue="0">
    <dic>
      <item key="0" text="正常" />
      <item key="1" text="已注销" />
      <item key="3" text="终止妊娠" />
    </dic>
  </item>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    defaultValue="%user.userId" display="1">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期" type="date"
    defaultValue="%server.date.today" display="1">
    <set type="exp">['$','%server.date.today']</set>
  </item>
  <item id="lastModifyUnit" alias="修改单位" type="string" length="20"
    width="180" hidden="true" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  
  <item id="cancellationUser" alias="注销人" type="string" length="20"
    hidden="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
  </item>
  <item id="cancellationDate" alias="注销日期" type="date" hidden="true" />
  <item id="cancellationUnit" alias="注销单位" type="string" length="20"
    width="180" hidden="true">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
  </item>
  <item id="cancellationReason" alias="注销原因" type="string" length="1"
    hidden="true">
    <dic>
      <item key="1" text="死亡" />
      <item key="2" text="迁出" />
      <item key="3" text="失访" />
      <item key="4" text="拒绝" />
      <item key="9" text="其他" />
    </dic>
  </item>
  <item id="deadReason" alias="死亡原因" type="string" fixed="true" hidden="true"
    length="100" display="2" colspan="3" anchor="100%" />
  <item ref="c.regionCode_text" display="0"/>
  
  <relations>
    <relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
    <relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
      <join parent="phrId" child="phrId" />
    </relation>
  </relations>

</entry>
