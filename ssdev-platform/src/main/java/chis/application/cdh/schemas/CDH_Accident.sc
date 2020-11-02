<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cdh.schemas.CDH_Accident" alias="儿童意外情况">
  <item id="accidentId" alias="记录序号" type="string" length="16"
    width="160" not-null="1" generator="assigned" pkey="true"
    display="1">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="phrId" alias="档案编号" type="string" length="30" display="0" />
  <item id="manaUnitId" alias="管辖机构" type="string" length="20"
    width="180" not-null="1" fixed="true" queryable="true"
    anchor="100%">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="oneYearLive" alias="居住一年以上" length="1" display="2"
    fixed="true">
    <dic id="chis.dictionary.yesOrNo" />
  </item>
  <item id="accidentDate" alias="发生日期" type="date" not-null="1"
    width="90" defaultValue="%server.date.today" maxValue="%server.date.today"/>
  <item id="accidentPlace" alias="发生场所" type="string" length="1"
    not-null="1" queryable="true">
    <dic>
      <item key="1" text="幼儿园"></item>
      <item key="2" text="途中"></item>
      <item key="3" text="家中"></item>
      <item key="4" text="其他"></item>
    </dic>
  </item>
  <item id="otherPlace" alias="其它场所描述" type="string" length="20"
    fixed="true" display="2" />
  <item id="inOutRoom" alias="室内外" type="string" length="1">
    <dic>
      <item key="1" text="室内"></item>
      <item key="2" text="室外"></item>
    </dic>
  </item>
  <item id="presentGuardian" alias="在场监护人" type="string" length="1">
    <dic>
      <item key="1" text="父"></item>
      <item key="2" text="母"></item>
      <item key="3" text="祖（外祖）父母"></item>
      <item key="4" text="保姆"></item>
      <item key="5" text="保育员"></item>
      <item key="6" text="老师"></item>
      <item key="7" text="其它亲属"></item>
      <item key="8" text="其他人员"></item>
    </dic>
  </item>
  <item id="otherDescribe" alias="其他人员描述" type="string" length="20"
    fixed="true" display="2" />
  <item id="trainFlag" alias="在场人员培训" type="string" length="1">
    <dic id="chis.dictionary.haveOrNot" />
  </item>
  <item id="accidentDiagnosis" alias="意外诊断" type="string"
    length="100" />
  <item id="accidentType" alias="意外分类" type="string" length="3"
    queryable="true" width="148">
    <dic id="chis.dictionary.accidentType" render="Tree" onlySelectLeaf="true"></dic>
  </item>
  <item id="firstManageArea" alias="第一处理场所" type="string"
    queryable="true" length="1">
    <dic>
      <item key="1" text="家"></item>
      <item key="2" text="幼儿园"></item>
      <item key="3" text="医院"></item>
      <item key="4" text="其他"></item>
    </dic>
  </item>
  <item id="firstHandler" alias="第一处理人" type="string" length="1">
    <dic>
      <item key="1" text="家长"></item>
      <item key="2" text="保健老师"></item>
      <item key="3" text="班老师"></item>
      <item key="4" text="医生"></item>
      <item key="5" text="其他"></item>
    </dic>
  </item>
  <item id="otherHandler" alias="其他处理人" type="string" length="20"
    fixed="true" display="2" />
  <item id="otherManageArea" alias="其他处理场所" type="string" fixed="true"
    display="2" length="20" />
  <item id="treatHospital" alias="治疗医院" type="string" length="50" />
  <item id="hospitalLevel" alias="医院级别" type="string" length="1">
    <dic>
      <item key="1" text="省（市）"></item>
      <item key="2" text="区县"></item>
      <item key="3" text="街道（乡镇）"></item>
      <item key="4" text="村、社区服务站"></item>
      <item key="5" text="未就医"></item>
    </dic>
  </item>
  <item id="levelResult" alias="意外结局" type="string" length="1"
    queryable="true">
    <dic>
      <item key="1" text="治愈"></item>
      <item key="2" text="残疾"></item>
      <item key="3" text="死亡"></item>
    </dic>
  </item>
  <item id="inputUnit" alias="录入单位" type="string" length="20"
    fixed="true" update="false" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"/>
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="inputUser" alias="录入人员" type="string" length="20"
    fixed="true" update="false" defaultValue="%user.userId">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"/>
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="inputDate" alias="录入日期" type="datetime" fixed="true" xtype="datefield"   update="false"
    defaultValue="%server.date.today"  >
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
  <item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
    defaultValue="%server.date.today" display="1">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
</entry>
