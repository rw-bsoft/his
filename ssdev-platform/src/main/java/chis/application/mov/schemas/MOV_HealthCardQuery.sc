<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.cdh.schemas.CDH_HealthCard"   alias="批量修改管理医生儿童保健卡查询页面" sort="a.createDate desc">
  <item id="phrId"  alias="档案编号" type="string" length="30"  width="160"  queryable="true"  />
  <item ref="b.personName" display="1" queryable="true" />
  <item ref="b.sexCode" display="1" queryable="true" />
  <item ref="b.birthday" display="1" queryable="true"  not-null="0"/>
  <item ref="b.idCard" display="1" queryable="true" />
  <item ref="b.mobileNumber" display="1" queryable="true" />
  <item ref="b.contactPhone" display="1" queryable="true" />
  <item ref="b.registeredPermanent" display="0"/>
	
  <item id="empiId" alias="empiId" type="string" length="32"
     display="0" />
  <item id="ownerArea" alias="归属地" width="160" length="2"  queryable="true"  update="false">
    <dic>
      <item key="11" text="本市"></item>
      <item key="22" text="本省外市"></item>
      <item key="23" text="外省"></item>
    </dic>
  </item>
  <item id="homeAddress" alias="户籍地址" length="25"  width="200" update="false"   queryable="true" >
    <dic id="chis.dictionary.areaGrid" minChars="4" includeParentMinLen="6" filterMin="10" filterMax="18" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="homeAddress_text" alias="户籍地址" type="string"  length="100"  />
  <item id="cdhDoctorId" alias="儿保医生" type="string" length="8"  update="false" queryable="true">
    <dic id="chis.dictionary.user02" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
  </item>
  <item id="oneYearLive" alias="居住一年以上" length="1" display="2">
    <dic id="chis.dictionary.yesOrNo" />
  </item>
  <item id="manaUnitId" alias="管辖机构" type="string" length="20"  queryable="true"  defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  onlySelectLeaf="true" lengthLimit="9" querySliceType="0" render="Tree" parentKey="%user.manageUnit.id" rootVisible="false"/>
  </item>
  <item id="manaDoctorId" alias="责任医生" type="string" length="20"
     update="false"  fixed="true">
    <dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="healthNo" alias="保健号" type="string" length="20"  queryable="true"   />
  <item id="fatherEmpiId" alias="父亲编号" type="string" length="32"
    display="0" />
  <item id="motherEmpiId" alias="母亲编号" type="string" length="32"
    display="0" />
  <item id="boneCondition" alias="出生情况" type="string" length="1"
     display="2">
    <dic>
      <item key="1" text="顺产"></item>
      <item key="2" text="头吸"></item>
      <item key="3" text="产钳"></item>
      <item key="4" text="剖宫"></item>
      <item key="5" text="双多胎"></item>
      <item key="6" text="臀位"></item>
      <item key="7" text="其他"></item>
    </dic>
  </item>
  <item id="otherBone" alias="其他出生情况" type="string" length="100"
    fixed="true" display="2" colspan="1" anchor="100%" width="180">
  </item>
  <item id="litters" alias="胎数" type="string" length="1" 
    display="2">
    <dic>
      <item key="1" text="单胎"></item>
      <item key="2" text="双胎"></item>
      <item key="3" text="多胎"></item>
    </dic>
  </item>
  <item id="gestation" alias="出生孕周" type="int" display="2"/>
  <item id="conception" alias="第几胎" type="int" display="2"   />
  <item id="birth" alias="第几产" type="int" display="2"  />
  <item id="birthWeight" alias="体重(kg)" type="bigDecimal" length="8"
     display="2" precision="2" />
  <item id="birthHeight" alias="身长(cm)" type="bigDecimal" length="8"
    display="2" precision="2" />
  <item id="headSize" alias="头围(cm)" type="bigDecimal" length="8"
    display="2" precision="2" />
  <item id="circumference" alias="胸围(cm)" type="bigDecimal" length="8"
    display="2" precision="2" />
  <item id="deliveryConditions" alias="产时情况" type="string" display="2"
     length="1" defaultValue="1">
    <dic>
      <item key="1" text="正常"></item>
      <item key="2" text="青紫"></item>
      <item key="3" text="窒息"></item>
      <item key="4" text="产伤"></item>
      <item key="5" text="抢救"></item>
    </dic>
  </item>
  <item id="apgar1" alias="Apgar评分1" type="string" length="10"
    display="2" />
  <item id="birthDefects" alias="出生缺陷标志" type="string" length="1"
    defaultValue="n" display="2">
    <dic id="chis.dictionary.haveOrNot" />
  </item>
  <item id="defectsType" alias="出生缺陷类型" type="string" length="20"
    width="180" fixed="true" display="2" colspan="2" anchor="100%">
    <dic id="chis.dictionary.defectsType" render="LovCombo" />
  </item>
  <item id="apgar5" alias="Apgar评分5" type="string" length="10"
    display="2" />
  <item id="motherAbnormal" alias="母孕期异常" type="string" length="1"
    defaultValue="n" display="2">
    <dic id="chis.dictionary.haveOrNot" />
  </item>
  <item id="abnormal" alias="异常情况" type="string" length="100"
    display="2" colspan="2" fixed="true">
  </item>
  <item id="apgar10" alias="Apgar评分10" type="string" length="10"
    display="2" />
  <item id="screenFlage" alias="新生儿疾病" type="string" length="1"
    defaultValue="n" display="2">
    <dic id="chis.dictionary.haveOrNot" />
  </item>
  <item id="screen" alias="疾病类型" type="string" length="100"
    width="180" fixed="true" display="2" colspan="2" anchor="100%">
    <dic render="LovCombo">
      <item key="01" text="新生儿窒息" />
      <item key="02" text="新生儿黄疸" />
      <item key="03" text="新生儿缺血缺氧性脑病" />
      <item key="04" text="新生儿颅内出血" />
      <item key="05" text="呼吸窘迫综合征" />
      <item key="06" text="新生儿肺炎" />
      <item key="07" text="新生儿败血症" />
      <item key="08" text="新生儿化脓性脑膜炎" />
      <item key="09" text="新生儿出血症" />
      <item key="10" text="新生儿梅毒" />
      <item key="11" text="新生儿溶血症" />
      <item key="12" text="新生儿低血糖" />
      <item key="13" text="新生儿高血糖" />
      <item key="14" text="新生儿低钙血症" />
      <item key="15" text="头颅血肿" />
      <item key="16" text="锁骨骨折" />
      <item key="17" text="面神经麻痹" />
      <item key="18" text="先天性甲状腺功能低下" />
      <item key="19" text="苯丙酮尿症" />
      <item key="20" text="其他氨基酸代谢病" />
      <item key="21" text="其他" />
    </dic>
  </item>
  <item id="otherScreen" alias="其他疾病" type="string" length="100"
    fixed="true" display="2">
  </item>
  <item id="screenRecord" alias="新生儿疾筛" type="string" length="2"
     display="2" defaultValue="1">
    <dic>
      <item key="1" text="正常"></item>
      <item key="2" text="甲低"></item>
      <item key="3" text="苯丙酮尿症"></item>
      <item key="4" text="其他代谢性疾病"></item>
      <item key="5" text="其他"></item>
    </dic>
  </item>
  <item id="otherScreenResult" alias="其他疾筛结果" type="string"
    length="100" fixed="true" display="2">
  </item>
  <item id="heardScreen" alias="新生儿听筛" type="string" length="2"
     display="2" defaultValue="1">
    <dic>
      <item key="1" text="正常"></item>
      <item key="2" text="可疑(左耳)"></item>
      <item key="3" text="可疑(右耳)"></item>
      <item key="4" text="可疑(两耳)"></item>
      <item key="5" text="未查"></item>
    </dic>
  </item>
  <item id="deformity" alias="残疾情况" type="string" length="100"
    display="2" colspan="2" defaultValue="1401">
    <dic render="LovCombo">
      <item key="1401" text="无残疾" />
      <item key="1402" text="听力残" />
      <item key="1403" text="言语残" />
      <item key="1404" text="肢体残(上肢)" />
      <item key="1408" text="肢体残(下肢)" />
      <item key="1405" text="智力残" />
      <item key="1406" text="视力残" />
      <item key="1407" text="精神残" />
      <item key="1409" text="孤独症儿童" />
      <item key="1410" text="脑瘫儿童" />
      <item key="1412" text="其他残疾" />
    </dic>
  </item>
  <item id="otherDeformity" alias="其他残疾" type="string" length="100"
    fixed="true" display="2" colspan="2">
  </item>
  <item id="familyHistory" alias="遗传家族史" type="string" length="20"
    display="2" colspan="2" defaultValue="1">
    <dic render="LovCombo">
      <item key="1" text="无"></item>
      <item key="2" text="盲"></item>
      <item key="3" text="聋"></item>
      <item key="4" text="哑"></item>
      <item key="5" text="精神病"></item>
      <item key="6" text="先天性智力低下"></item>
      <item key="7" text="先天性心脏病"></item>
      <item key="8" text="血友病"></item>
      <item key="9" text="糖尿病"></item>
      <item key="10" text="其他"></item>
    </dic>
  </item>
  <item id="otherFamily" alias="其它家族史" type="string" length="100"
    fixed="true" display="2" />
  <item id="relationship" alias="与儿童关系" type="string" length="10"
    fixed="true" display="2">
    <dic render="LovCombo">
      <item key="0" text="本人"></item>
      <item key="5" text="父母"></item>
      <item key="6" text="祖父母,外祖父母"></item>
      <item key="7" text="兄弟姐妹"></item>
      <item key="8" text="其他"></item>
    </dic>
  </item>
  <item id="allergicHistory" alias="药物过敏史" type="string" length="100"
    display="2" colspan="2" defaultValue="0301">
    <dic render="LovCombo">
      <item key="0301" text="无药物过敏史" />
      <item key="0302" text="青霉素" />
      <item key="0303" text="磺胺" />
      <item key="0304" text="链霉素" />
      <item key="0305" text="其他" />
    </dic>
  </item>
  <item id="otherAllergyDrug" alias="其他过敏药物" type="string"
    fixed="true" display="2" length="100" />

  <item id="isHighRisk" alias="是否高危儿" type="string" length="1">
    <dic id="chis.dictionary.yesOrNo" />
  </item>
  <item id="highRiskType" alias="高危儿分类" type="string" length="100"
    fixed="true" display="2" colspan="2">
    <dic render="LovCombo" id="highRiskType" />
  </item>
  <item id="otherType" alias="其他分类" type="string" length="100"
    fixed="true" display="2" colspan="2">
  </item>
  <item id="endManageFlag" alias="结案标志" type="string" length="1" defaultValue="n"   fixed="true">
    <dic id="chis.dictionary.closeFlag" />
  </item>
  <item id="endManageUnit" alias="结案单位" type="string" length="20"
    fixed="true" display="2" colspan="2" anchor="100%">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="endManageDoctor" alias="结案医生" type="string" length="20"
    fixed="true" display="2">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="endManageDate" alias="结案时间" type="date" fixed="true"
    display="2" />
  <item id="createUnit" alias="建档机构" type="string" length="20"    width="180"  anchor="100%"     fixed="true">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="createUser" alias="建档人员" type="string" length="20"  queryable="true">
    <dic id="chis.dictionary.user02" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="createDate" alias="建档日期" type="date"  queryable="true" maxValue="%server.date.today"/>
  <item id="cancellationReason" alias="档案注销原因" type="string"
    length="1" display="0">
    <dic>
      <item key="1" text="死亡" />
      <item key="2" text="迁出" />
      <item key="3" text="失访" />
      <item key="4" text="拒绝" />
      <item key="9" text="其他" />
    </dic>
  </item>
  <item id="cancellationDate" alias="档案注销日期" type="date" display="0"
    defaultValue="%server.date.today" />
  <item id="cancellationUser" alias="注销人" type="string" length="20"
    hidden="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"/>
  </item>
  <item id="deadReason" alias="死亡原因" type="string" fixed="true"
    length="100" hidden="true" colspan="3" anchor="100%" />
  <item id="cancellationCheckUnit" alias="注销复核机构" type="string"
    width="180" display="0" length="8" />
  <item id="cancellationCheckUser" alias="注销复核人员" type="string"
    length="20" display="0" />
  <item id="cancellationCheckDate" alias="注销复核时间" type="date"
    display="0" />
  <item id="status" alias="档案状态" type="string" length="1"
    defaultValue="0" display="0">
    <dic id="chis.dictionary.docStatu"/>
  </item>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    defaultValue="%user.userId" display="1">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"/>
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期" type="date"
    defaultValue="%server.date.today" display="1">
    <set type="exp">['$','%server.date.today']</set>
  </item>
  <!-- add by yyd -->
	
  <item id="lastModifyUnit" alias="修改单位" type="string" length="20"
    width="180" hidden="true" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>

  <item id="cancellationDate" alias="注销日期" type="date" hidden="true" />
  <item id="cancellationUnit" alias="注销单位" type="string" length="20"
    width="180" hidden="true">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"/>
  </item>
  <item ref="c.regionCode_text" display="0" />
	
  <relations>
    <relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
    <relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
      <join parent="phrId" child="phrId" />
    </relation>
  </relations>
</entry>