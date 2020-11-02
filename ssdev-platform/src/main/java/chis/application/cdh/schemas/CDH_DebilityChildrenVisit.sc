<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cdh.schemas.CDH_DebilityChildrenVisit" alias="体弱儿童档案随访表">
  <item id="visitId" alias="记录序号" type="string" length="16"
    not-null="1" generator="assigned" pkey="true" display="0">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="recordId" alias="档案编号" type="string" length="16" display="0" />
  <item id="empiId" alias="empiid" type="string" length="32"
    fixed="true" notDefaultValue="true" display="0" />
  <item id="visitDate" alias="随诊日期" type="date" not-null="1"
    defaultValue="%server.date.today" enableKeyEvents="true"  maxValue="%server.date.today"/>
  <item id="visitMonth" alias="随诊月龄" type="int" not-null="1" />
  <item id="nextVisitDate" alias="复诊日期" type="date" />
  <item id="length" alias="身长（cm）" minValue="0"     type="double" />
  <item id="weight" alias="体重（kg）" minValue="0"    type="double" />
  <item id="signs" alias="阳性体征" type="string" length="100" />
  <item id="symptoms" alias="主要症状" type="string" length="100"
    colspan="2" />
  <item id="feedWay" alias="喂养情况" type="string" length="20" colspan="1">
  </item>
  <item id="examination" alias="检查项目" type="string" length="100"
    colspan="3" />
  <item id="results" alias="检查结果" type="string" length="100" colspan="3"/>
  <item id="appraiseWY" alias="体重/年龄" type="string" length="20"
    fixed="true" display="2"  >
    <dic id="chis.dictionary.developmentCase"/>
  </item>
  <item id="appraiseHY" alias="身高/年龄" type="string" length="20"
    fixed="true" display="2" >
    <dic id="chis.dictionary.developmentCase"/>
  </item>
  <item id="appraiseWH" alias="体重/身高" type="string" length="20"
    fixed="true" display="2">
    <dic id="chis.dictionary.developmentCase"/>
  </item>
  <item id="guidance" alias="处理及指导" type="string" length="524288000"
    xtype="textarea" colspan="3" />	
  <item id="medicine" alias="服药情况" type="string" length="500"
    xtype="textarea" colspan="3" />
  <item id="examineUnit" alias="检查(测)机构" type="string" length="20" update="false"
    defaultValue="%user.manageUnit.id" fixed="true">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="examineDoctor" alias="检查(测)人员" type="string" fixed="true" update="false"
    length="20" defaultValue="%user.userId">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"/>
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="examineDate" alias="检查(测)日期" type="datetime"  xtype="datefield" display="2" update="false"
    fixed="true" defaultValue="%server.date.today" >
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
