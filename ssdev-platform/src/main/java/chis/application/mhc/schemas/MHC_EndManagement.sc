<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mhc.schemas.MHC_EndManagement" alias="终止妊娠">
  <item id="id" alias="序号" pkey="true" type="string" length="16"
    not-null="1" fixed="true" hidden="true" generator="assigned">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="empiId" alias="EMPIID" length="32" hidden="true" type="string"/>
  <item id="pregnantId" alias="孕妇档案编号" length="30" hidden="true" type="string"/>
  <item id="gestationMode" alias="终止妊娠方式" length="20" display="2"  not-null="1"  type="string" anchor="100%" colspan="2">
    <dic id="chis.dictionary.gestationMode" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="endDate" alias="终止妊娠时间" type="date" not-null="1" defaultValue="%server.date.today"  maxValue="%server.date.today"/>
  <item id="remark" alias="备注" xtype="textarea" length="200" display="2" colspan="3" />
  <item id="endUnit" alias="录入机构" length="20" type="string"
    width="180" defaultValue="%user.manageUnit.id" anchor="100%"
    fixed="true" colspan="2">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="endDoctor" alias="录入医生" length="20" type="string"
    fixed="true" defaultValue="%user.userId">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
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
  <item id="lastModifyDate" alias="最后修改日期"  type="datetime"  xtype="datefield"
    defaultValue="%server.date.today" display="1">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
</entry>
