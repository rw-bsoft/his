<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.pd.schemas.CDH_DictCorrection" alias="矫治记录" sort="age">
  <item id="recordId" alias="记录序号" type="string" length="16"
    not-null="1" pkey="true" display="0" generator="assigned">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="diseaseType" alias="疾病类型" type="string" length="1" display="2"/>
  <item id="age" alias="年龄（月）" type="string" length="2">
    <dic id="chis.dictionary.childrenAge" />
  </item>
  <item id="suggestion" alias="指导意见" type="string" length="524288000" width="3000" xtype="htmleditor" colspan = "2"/>
  <item id="createUser" alias="录入人" type="string" length="20" fixed="true" update="false"
    display="1" defaultValue="%user.userId">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="createUnit" alias="录入机构" type="string" length="20" update="false"
    fixed="true" display="1" defaultValue="%user.manageUnit.id" width="450">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
      onlySelectLeaf="true" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="createDate" alias="录入时间" type="datetime"  xtype="datefield" fixed="true" update="false"
    display="1" defaultValue="%server.date.today" >
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
