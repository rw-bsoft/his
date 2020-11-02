<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mhc.schemas.MHC_HighRiskVisitReason" alias="高危随访因素">
  <item id="id" alias="序号" length="16" pkey="true" type="string"
    not-null="1" fixed="true" hidden="true" generator="assigned">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="visitId" alias="随访序号" length="16" width="120" display="0"
    type="string" />
  <item id="highRiskReasonId" alias="高危因素" length="20" width="380"
    type="string">
    <dic id="chis.dictionary.highRiskScore" />
  </item>
  <item id="highRiskScore" alias="高危评分" type="int" width="70" />
  <item id="empiId" alias="EMPIID" length="32" width="120" display="0"
    type="string" />
  <item id="highRiskLevel" alias="因素分级" length="1" width="70"
    type="string" />
  <item id="frequence" alias="周期" type="int" width="60" display="0" />
  <item id="pregnantId" alias="孕妇档案编号" length="30" width="120"
    display="0" type="string" />
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
  <item id="createDate" alias="录入时间" type="datetime"  xtype="datefield" update="false" display="0"
    fixed="true" defaultValue="%server.date.today">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="20" display="0" width="180" hidden="true" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    defaultValue="%user.userId" display="0">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
    defaultValue="%server.date.today" display="0">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
</entry>
