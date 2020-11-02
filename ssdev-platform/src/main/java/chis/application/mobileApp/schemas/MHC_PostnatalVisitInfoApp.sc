<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mobileApp.schemas.MHC_PostnatalVisitInfoApp" alias="产后访视信息"  sort="visitDate,visitId">
  <item id="visitId" alias="检查单号" length="16" pkey="true"
    type="string" not-null="1" fixed="true" hidden="true"
    generator="assigned">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="empiId" alias="EMPIID" length="32" not-null="1"
    display="0" type="string" />	
  <item ref="b.personName" display="1" queryable="true"/>
  <item ref="b.idCard" display="1" queryable="true"/>
  <item ref="b.birthday" display="1" queryable="true"/>
  <item ref="b.mobileNumber" display="1" queryable="true"/>
  <item id="pregnantId" alias="孕妇档案编号" length="16" display="2"
    type="string" hidden="true" fixed="true" />
  <item id="visitDate" alias="访视日期" not-null="1" type="date"
    defaultValue="%server.date.today" maxValue="%server.date.today" />
  <item id="birthDay" alias="出生日期" not-null="1" type="date"
    enableKeyEvents="true" maxValue="%server.date.today"   display="2"/>
  <item id="postnatalDays" alias="产后天数" length="2" not-null="1"
    type="int" fixed="true" />
  <item id="checkDoctor" alias="随访医生" type="string" length="20"
    fixed="true" defaultValue="%user.userId" display="2">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
  </item>
  <item id="nextVisitDate" alias="下次随访日期" type="date"  minValue="%server.date.today"/>
  <item id="checkManaUnit" alias="管辖单位" type="string" not-null="1" length="20" hidden="true"  fixed="true" display="2">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"/>
  </item>
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
    defaultValue="%user.userId" display="1">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
    defaultValue="%server.date.today" display="1">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
</entry>
