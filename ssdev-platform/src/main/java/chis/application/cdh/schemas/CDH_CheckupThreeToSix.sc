<entry entityName="chis.application.cdh.schemas.CDH_CheckupThreeToSix" alias="3～6岁儿童健康检查记录">
  <item id="checkupId" alias="体检序列号" type="string" length="16"
    not-null="1" generator="assigned" pkey="true" hidden="true">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="phrId" alias="档案编号" type="string" length="30" fixed="true"
    colspan="2"  anchor="100%"  display="1" />
  <item id="manaUnitId" alias="管辖机构" type="string" length="20"
    defaultValue="%user.manageUnit.id"  colspan="2"   anchor="100%" fixed="true"
    display="1">
    <dic id="chis.@manageUnit" includeParentMinLen="6"   render="Tree" />
  </item>
  <item id="checkupDate" alias="随访日期" type="date" anchor="100%"  maxValue="%server.date.today"  not-null="1" />
  <item id="nextCheckupDate" alias="下次随访日期" type="date" anchor="100%"/> 
  <item id="checkupStage" alias="体检阶段" type="string" length="2" fixed="true"   not-null="1" anchor="100%">
    <dic id="chis.dictionary.childrenAge"  filter="['ge',['$map',['s','key']],['s','36']]"/>
  </item>
  <item id="weight" alias="体重(kg)" type="double" length="5"
    precision="1" not-null="1"  />
  <item id="weightDevelopment" alias="体重发育情况" type="string" length="1" fixed="true">
    <dic id="chis.dictionary.developmentCase"/>
  </item>
  <item id="height" alias="身长(cm)" type="double" length="6"
    precision="1" not-null="1"  />
  <item id="heightDevelopment" alias="身长发育情况" type="string" length="1" fixed="true">
    <dic id="chis.dictionary.developmentCase"/>
  </item>
  <item id="devEvaluation" alias="体格发育评价" type="string" length="20" fixed="true"   defaultValue="1">
    <dic render="LovCombo">
      <item key="1" text="正常" />
      <item key="2" text="低体重" />
      <item key="3" text="消瘦" />
      <item key="4" text="发育迟缓" />
      <item key="5" text="超重" />
    </dic>
  </item>
  <item id="hearing" alias="听力" type="string" length="2"     defaultValue="1" anchor="100%">
    <dic>
      <item key="1" text="通过" />
      <item key="2" text="未通过" />
    </dic>
  </item>
  <item id="eyesight" alias="视力" type="string" length="10" anchor="100%"/>
  <item id="decayedTooth" alias="出牙数" type="int"  length="3"   not-null="1" />
  <item id="dentalCaries" alias="龋齿数" type="int" length="3"/>
  <item id="heartLung" alias="心肺" type="string" length="1"    defaultValue="1">
    <dic id="chis.dictionary.hasAbnormal"/>
  </item>
  <item id="abdomen" alias="腹部" type="string" length="1"     defaultValue="1" >
    <dic id="chis.dictionary.hasAbnormal"/>
  </item>
  <item id="hgb" alias="血红蛋白值(g/L)"  type="double" length="6" precision="2"/>
  <item id="other" alias="其他" type="string" length="64" colspan="3"/>
  <item id="guide" alias="指导" length="10"  display="2"> 
    <dic render="LovCombo"> 
      <item key="1" text="合理膳食"/>  
      <item key="2" text="生长发育"/>  
      <item key="3" text="疾病预防"/>  
      <item key="4" text="预防意外伤害"/>  
      <item key="5" text="其他"/> 
    </dic> 
  </item>  
  <item id="otherGuide" alias="其他指导描述" length="500" colspan="2" fixed="true" display="2"/> 
  <item id="referral" alias="转诊建议" type="string" length="1">
    <dic id="chis.dictionary.haveOrNot" />
  </item>
  <item id="referralUnit" alias="转诊机构及科室" type="string" colspan="2" length="50" anchor="100%"   fixed="true" 
    width="120" />
  <item id="referralReason" alias="转诊原因" type="string" length="50"  colspan="3" anchor="100%"   fixed="true" />
  <item id="checkDoctor" alias="随访医生" type="string" length="20" update="false"
    defaultValue="%user.userId" fixed="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"/>
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="checkUnit" alias="随访单位" type="string" length="20" update="false"
    defaultValue="%user.manageUnit.id" fixed="true" colspan="2"  anchor="100%">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    defaultValue="%user.userId" display="0">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
    defaultValue="%user.manageUnit.id"  display="0">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
    defaultValue="%server.date.today" display="0">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
</entry>