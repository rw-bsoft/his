<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cdh.schemas.CDH_Inquire" alias="儿童询问记录">
  <item id="inquireId" alias="记录序号" type="string" length="16"
    width="160" not-null="1" generator="assigned" pkey="true"
    display="1">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="phrId" alias="档案编号" type="string" length="30" display="0" />
  <item id="manaUnitId" alias="管辖机构" fixed="true"  type="string" length="20"
    not-null="1"   queryable="true" colspan="3">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="inquireDate" alias="询问日期" type="date" width="100"  not-null="1"  />
  <item id="ageDate" alias="实足日龄" type="int" fixed="true" width="100" />
  <item id="feedWay" alias="喂养方式" type="string" length="1" width="100" queryable="true">
    <dic>
      <item key="1" text="纯母乳喂养"></item>
      <item key="2" text="主要母乳喂养"></item>
      <item key="3" text="辅助喂养"></item>
      <item key="4" text="人工喂养"></item>
    </dic>
  </item>
  <item id="breastMilkCount" alias="母乳量" type="string" length="1"
    defaultValue="9">
    <dic>
      <item key="1" text="充足"></item>
      <item key="2" text="不多"></item>
      <item key="3" text="无"></item>
      <item key="9" text="不祥"></item>
    </dic>
  </item>
  <item id="dryMilk" alias="奶粉ml/日" type="string" length="10"
    display="2" />
  <item id="milk" alias="牛奶kg/月" type="string" length="10"
    display="2" />
  <item id="otherFood" alias="其他kg/月" type="string" length="10"
    display="2" />
  <item id="weanFlag" alias="断奶标志" type="string" length="1"
    display="2">
    <dic id="chis.dictionary.yesOrNo" />
  </item>
  <item id="weanMonth" alias="断奶月龄" type="int" display="2"
    width="100" fixed="true"/>
  <item id="wheaten" alias="谷面次/日" type="string" length="10"
    display="2" />
  <item id="fruit" alias="果蔬次/日" type="string" length="10"
    display="2" />
  <item id="beanProducts" alias="豆制品次/日" type="string" length="10"
    display="2" />
  <item id="meatAndEgg" alias="肉蛋类次/日" type="string" length="10"
    display="2" />
  <item id="vitaminADFlage" alias="维生素D" type="string" length="2"
    display="2" defaultValue="3">
    <dic>
      <item key="1" text="已加"></item>
      <item key="2" text="未加"></item>
      <item key="3" text="不详"></item>
      <item key="9" text="其他"></item>
    </dic>
  </item>
  <item id="vitaminADName" alias="维生素D名称" type="string" length="1"
    fixed="true" display="2">
    <dic>
      <item key="1" text="贝特令"></item>
      <item key="2" text="伊可新"></item>
      <item key="3" text="小施尔康"></item>
      <item key="4" text="VitAD滴剂"></item>
    </dic>
  </item>
  <item id="vitaminAD" alias="用法IU/日" type="string" length="10"
    fixed="true" display="2" />
  <item id="calciumFlage" alias="钙片添加标志" type="string" length="1"
    display="2" defaultValue="3">
    <dic>
      <item key="1" text="已加"></item>
      <item key="2" text="未加"></item>
      <item key="3" text="不详"></item>
    </dic>
  </item>
  <item id="appetite" alias="食欲" type="string" length="1"
    queryable="true" defaultValue="4">
    <dic>
      <item key="1" text="很好"></item>
      <item key="2" text="一般"></item>
      <item key="3" text="差"></item>
      <item key="4" text="不祥"></item>
    </dic>
  </item>
  <item id="fecesState" alias="大便性状" type="string" length="1"
    display="2">
    <dic id="chis.dictionary.stoolStatus"/>
  </item>
  <item id="defecateTimes" alias="大便次数" type="int"/>
  <item id="defecateDates" alias="/天数" type="int"/>
  <item id="fecesColor" alias="大便颜色" type="string" length="1"
    display="2" defaultValue="4">
    <dic>
      <item key="1" text="黄色"></item>
      <item key="2" text="灰白色"></item>
      <item key="3" text="其他"></item>
      <item key="4" text="不详"></item>
    </dic>
  </item>
  <item id="otherColor" alias="其他大便颜色" type="string" length="10"
    display="2" fixed="true" />
  <item id="sleepQuality" alias="睡眠情况" type="string" length="1"
    queryable="true" defaultValue="9">
    <dic>
      <item key="1" text="安静"></item>
      <item key="2" text="汗多"></item>
      <item key="3" text="烦躁"></item>
      <item key="4" text="夜惊"></item>
    </dic>
  </item>
  <item id="ricketsSymptom" alias="可疑佝偻病症状" type="string"
    length="1">
    <dic>
      <item key="1" text="无"></item>
      <item key="2" text="夜惊"></item>
      <item key="3" text="多汗"></item>
      <item key="4" text="烦躁"></item>
      <item key="9" text="其他"></item>
    </dic>
  </item>
  <item id="ricketsSign" alias="可疑佝偻病体征" type="string"
    length="2">
    <dic>
      <item key="01" text="无"></item>
      <item key="02" text="颅骨软化"></item>
      <item key="03" text="方颅"></item>
      <item key="04" text="枕秃"></item>
      <item key="05" text="肋串珠"></item>
      <item key="06" text="肋外翻"></item>
      <item key="07" text="肋软骨沟"></item>
      <item key="08" text="鸡胸"></item>
      <item key="09" text="手镯征"></item>
      <item key="10" text="“0”型腿"></item>
      <item key="11" text="“X”型腿"></item>
      <item key="99" text="其他"></item>
    </dic>
  </item>
  <item id="illness" alias="访间患病情况" type="string" length="1"
    defaultValue="2">
    <dic>
      <item key="1" text="有"></item>
      <item key="2" text="无"></item>
      <item key="9" text="不详"></item>
    </dic>
  </item>
  <item id="illnessType" alias="患病类型" type="string" length="20"
    display="2" fixed="true">
    <dic render="LovCombo">
      <item key="1" text="肺炎"></item>
      <item key="2" text="腹泻"></item>
      <item key="3" text="外伤"></item>
      <item key="4" text="其他"></item>
    </dic>
  </item>
  <item id="pneumoniaCount" alias="肺炎次数" type="string" length="5"
    display="2" fixed="true"/>
  <item id="diarrheaCount" alias="腹泻次数" type="string" length="5"
    display="2" fixed="true"/>
  <item id="traumaCount" alias="外伤次数" type="string" length="5"
    display="2" fixed="true"/>
  <item id="otherCount" alias="其他次数" type="string" length="5"
    display="2" fixed="true"/>
  <item id="illnessName" alias="其他患病名称" type="string" length="50" 
    display="2" fixed="true"/>
  <item id="outdoorActivities" alias="户外活动" type="string"
    length="1">
    <dic>
      <item key="1" text=">=2小时/天"></item>
      <item key="2" text="1~2小时/天"></item>
      <item key="3" text="小于1小时/天"></item>
      <item key="4" text="基本不外出"></item>
    </dic>
  </item>
	
  <item id="inquireDoctor" alias="询问医生" type="string" length="20" update="false"
    fixed="true" defaultValue="%user.userId" queryable="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="inquireUnit" alias="询问单位" type="string" length="20" update="false"
    width="180" fixed="true" defaultValue="%user.manageUnit.id"  anchor="100%">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    defaultValue="%user.userId" display="1">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"/>
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"  display="1"
    width="180" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
    defaultValue="%server.date.today" display="1">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
</entry>
