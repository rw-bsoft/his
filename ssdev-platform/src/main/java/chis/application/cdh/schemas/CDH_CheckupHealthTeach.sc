<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cdh.schemas.CDH_CheckupHealthTeach" alias="儿童体检健康教育">
  <item id="recordId" alias="记录序号" type="string" length="16"
    not-null="1" generator="assigned" pkey="true" hidden="true">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="phrId" alias="档案编号" type="string" length="30" hidden="true"/>
  <item id="checkupId" alias="随访标识" type="string" length="16" hidden="true"/>
  <item id="checkupType" alias="体检类型" type="string" length="1" hidden="true">
    <dic>
      <item key="1" text="1岁以内"/>
      <item key="2" text="1～2岁"/>
      <item key="3" text="3～6岁"/>
    </dic>
  </item>
  <item id="healthTeach" alias="健康教育描述" type="string" length="2000" xtype="textarea"/>
  <item id="inputUnit" alias="录入单位" type="string" update="false" length="20" fixed="true" defaultValue="%user.manageUnit.id" width="150">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="inputDate" alias="录入日期" type="datetime"  xtype="datefield" fixed="true" update="false"
    defaultValue="%server.date.today">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="inputUser" alias="录入员工" type="string" length="20" update="false" fixed="true" defaultValue="%user.userId">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    defaultValue="%user.userId" display="1">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
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
