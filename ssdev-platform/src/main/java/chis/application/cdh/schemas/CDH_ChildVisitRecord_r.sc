<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mhc.schemas.MHC_BabyVisitRecord" alias="新生儿访视记录">
  <item id="visitId" alias="随访序号" type="string" length="16"
    not-null="1" generator="assigned" pkey="true" hidden="true">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="pregnantId" alias="孕妇档案编号" type="string" length="30"
    hidden="true" />
  <item id="babyId" alias="新生儿编号" type="string" length="16"
    hidden="true" />
  <item id="visitDate" alias="访视日期" type="date" update="false"
    fixed="true" defaultValue="%server.date.today">
  </item>
  <item id="visitDoctor" alias="随访医生" type="string" length="20"
    update="false" fixed="true" defaultValue="%user.userId">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
  </item>
  <item id="visitUnit" alias="随访单位" type="string" length="20"
    update="false" fixed="true" width="180" colspan="2" anchor="100%"
    defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
  </item>
  <item id="temperature" alias="体温(℃)" type="bigDecimal" length="4"
    precision="1" />
  <item id="weight" alias="目前体重(kg)" type="bigDecimal" length="5"
    precision="2" />
  <item id="respiratoryFrequency" alias="呼吸频率(次/分)" type="int"
    width="100" />
  <item id="pulse" alias="脉率(次/分)" type="int" width="100" />
  <item id="feedWay" alias="喂养方式" type="string" length="2"
    display="2">
    <dic>
      <item key="1" text="纯母乳"></item>
      <item key="2" text="混合"></item>
      <item key="3" text="人工"></item>
    </dic>
  </item>
  <item id="eatNum" alias="吃奶量(ml)" type="int" length="20" />
  <item id="eatCount" alias="吃奶次数(次/日)" type="int" length="5" />
  <item id="vomit" alias="呕吐" type="string" length="2" >
    <dic>
      <item key="1" text="有"></item>
      <item key="2" text="无"></item>
    </dic>
  </item>
  <item id="jaundice" alias="黄疸部位" type="string" length="1">
    <dic>
      <item key="1" text="面部" />
      <item key="2" text="躯干" />
      <item key="3" text="四肢" />
      <item key="4" text="手足" />
      <item key="5" text="巩膜" />
      <item key="9" text="其他" />
    </dic>
  </item>
  <item id="face" alias="面色" type="string" length="1">
    <dic id ="chis.dictionary.face" />
  </item>
  <item id="faceOther" alias="其它面色" type="string" length="30" />
  <item id="bregmaTransverse" alias="前囟纵径(cm)" type="bigDecimal"
    length="5" precision="2" width="120" />
  <item id="bregmaLongitudinal" alias="前囟横径(cm)" type="bigDecimal"
    length="5" precision="2" width="120" />
  <item id="bregmaStatus" alias="前囟状态" type="string" length="1">
    <dic>
      <item key="1" text="正常" />
      <item key="2" text="膨隆" />
      <item key="3" text="凹陷" />
      <item key="4" text="其他" />
    </dic>
  </item>
  <item id="otherStatus" alias="其它前囟状态" type="string" length="30" />
   
  <item id="eye" alias="眼" type="string" length="2">
    <dic>
      <item key="1" text="正常" />
      <item key="2" text="眼睑浮肿" />
      <item key="3" text="眼睑下垂" />
      <item key="4" text="眼球突出" />
      <item key="5" text="结膜充血" />
      <item key="6" text="眼分泌物多" />
      <item key="7" text="角膜浑浊" />
      <item key="8" text="斜视" />
      <item key="9" text="砂眼" />
      <item key="10" text="其他" />
    </dic>
  </item>
  <item id="eyeAbnormal" alias="眼其他异常" type="string" length="30" />
  <item id="ear" alias="耳" type="string" length="1">
    <dic>
      <item key="1" text="正常" />
      <item key="2" text="耳廓畸形" />
      <item key="3" text="附耳" />
      <item key="4" text="耳前窦道" />
      <item key="5" text="外耳（道）湿疹" />
      <item key="6" text="叮咛堵塞" />
      <item key="7" text="其他" />
    </dic>
  </item>
  <item id="earAbnormal" alias="耳其他异常" type="string" length="30" />
  <item id="nose" alias="鼻" type="string" length="1">
    <dic>
      <item key="1" text="未见异常" />
      <item key="2" text="异常" />
    </dic>
  </item>
  <item id="noseAbnormal" alias="鼻异常" type="string" length="30" />
  <item id="mouse" alias="口腔" type="string" length="2">
    <dic>
      <item key="1" text="正常" />
      <item key="2" text="口唇苍白" />
      <item key="3" text="口周青紫" />
      <item key="4" text="口角糜烂" />
      <item key="5" text="地图舌" />
      <item key="6" text="黏膜溃疡" />
      <item key="7" text="鹅口疮" />
      <item key="8" text="唇裂" />
      <item key="9" text="腭裂" />
      <item key="10" text="唇腭裂" />
      <item key="11" text="舌系带短" />
      <item key="12" text="咬颌异常" />
      <item key="13" text="出牙次序乱" />
      <item key="14" text="其他" />
    </dic>
  </item>
  <item id="mouseAbnormal" alias="口腔其他异常" type="string" length="30" />

  <item id="thrush" alias="鹅口疮" type="string" length="1">
    <dic id="chis.dictionary.haveOrNot" />
  </item>
  <item id="heartlung" alias="心肺听诊" type="string" length="1">
    <dic>
      <item key="1" text="未见异常" />
      <item key="2" text="异常" />
    </dic>
  </item>
  <item id="heartLungAbnormal" alias="心肺异常" type="string" length="30" />
  <item id="abdominal" alias="腹部" type="string" length="1">
    <dic>
      <item key="1" text="正常" />
      <item key="2" text="腹壁肌肉松弛" />
      <item key="3" text="膨隆" />
      <item key="4" text="凹陷" />
      <item key="5" text="其他" />
    </dic>
  </item>
  <item id="abdominalabnormal" alias="腹部其他异常" type="string" length="30" />

  <item id="limbs" alias="四肢活动" type="string" length="1">
    <dic>
      <item key="1" text="未见异常" />
      <item key="2" text="异常" />
    </dic>
  </item>
  <item id="limbsAbnormal" alias="四肢活动异常" type="string" length="30" />
  <item id="neck" alias="颈部包块" type="string" length="1">
    <dic id="chis.dictionary.haveOrNot" />
  </item>
  <item id="neck1" alias="颈部包块描述" type="string" length="30" />
  <item id="skin" alias="皮肤" type="string" length="2">
    <dic id="chis.dictionary.skin"/>
  </item>
  <item id="skinAbnormal" alias="皮肤其它症状" type="string" length="30" />
  <item id="anal" alias="肛门" type="string" length="1">
    <dic>
      <item key="1" text="未见异常" />
      <item key="2" text="异常" />
    </dic>
  </item>
  <item id="analAbnormal" alias="肛门异常" type="string" length="30" />
  <item id="genitalia" alias="外生殖器" type="string" length="1">
    <dic>
      <item key="1" text="未见异常" />
      <item key="2" text="异常" />
    </dic>
  </item>
  <item id="genitaliaAbnormal" alias="外生殖器异常" type="string"
    length="30" />
  <item id="spine" alias="脊柱" type="string" length="1">
    <dic>
      <item key="1" text="未见异常" />
      <item key="2" text="异常" />
    </dic>
  </item>
  <item id="spineAbnormal" alias="脊柱异常" type="string" length="30" />
  <item id="umbilical" alias="脐带" type="string" length="1">
    <dic id="chis.dictionary.umbilical"/>
  </item>
  <item id="umbilicalOther" alias="脐带其它" type="string" length="30" />
  <item id="stoolTimes" alias="大便次数" type="int" />
  <item id="stoolDates" alias="/天数" type="int" />
  <item id="stoolColor" alias="大便颜色" type="string" length="20">
    <dic>
      <item key="1" text="黄色"></item>
      <item key="2" text="灰白色"></item>
      <item key="3" text="其他"></item>
      <item key="4" text="不详"></item>
    </dic>
  </item>
  <item id="stoolStatus" alias="大便性状" type="string" length="20">
    <dic id="chis.dictionary.stoolStatus"/>
  </item>
  <item id="guide" alias="指导" type="string" length="64">
    <dic render="LovCombo">
      <item key="1" text="喂养指导" />
      <item key="2" text="发育指导" />
      <item key="3" text="防病指导" />
      <item key="4" text="预防伤害指导" />
      <item key="5" text="口腔保健指导" />
    </dic>
  </item>
  <item id="referral" alias="转诊" type="string" length="1">
    <dic id="chis.dictionary.haveOrNot" />
  </item>
  <item id="referralUnit" alias="转诊机构及科室" type="string" length="50" anchor="100%" fixed="true"
    width="120" />
  <item id="referralReason" alias="转诊原因" type="string" length="50" anchor="100%" fixed="true"/>
  <item id="nextVisitDate" alias="下次随访日期" type="date"  minValue="%server.date.today"/>
  <item id="nextVisitAddress" alias="下次随访地点" type="string"
    length="100" width="120" />
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
  <item id="createDate" alias="录入时间"  type="datetime"  xtype="datefield" update="false" display="0"
    fixed="true" defaultValue="%server.date.today">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="20" display="0" width="180" hidden="true" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    hidden="true" defaultValue="%user.userId">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期"  type="datetime"  xtype="datefield" hidden="true"
    defaultValue="%server.date.today">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
</entry>