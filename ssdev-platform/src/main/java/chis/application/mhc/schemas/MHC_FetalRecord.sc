<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mhc.schemas.MHC_FetalRecord" alias="胎儿信息">
  <item id="fetalId" alias="胎儿编号" length="30" pkey="true"
    type="string" not-null="1" fixed="true" hidden="true"
    generator="assigned">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="pregnantId" alias="孕妇档案编号" length="30" hidden="true" />
  <item id="empiId" alias="EMPIID" length="32" hidden="true" />
  <item id="visitId" alias="随访序号" length="30"  type="string"   hidden="true" />
  <item id="fetalPosition" alias="胎方位" length="3" width="180">
    <dic id="chis.dictionary.CV5105_01" defaultValue="99" />
  </item>
  <item id="fetalPositionFlag" alias="胎位异常" type="string" width="110" length="1">
    <dic id="chis.dictionary.haveOrNot" />
  </item>
  <item id="fetalHeartRate" alias="胎心率(次/分）" type="int" width="180"/>
  <item id="fetalHeartFlag" alias="胎心异常" type="string" length="1" width="110">
    <dic id="chis.dictionary.haveOrNot" />
  </item>
  <item id="saveFlag" alias="保存标志" type="string" length="1" hidden="true">
    <dic id="chis.dictionary.yesOrNo" />
  </item>
  <item id="createUnit" alias="录入机构" type="string" length="20"
    width="180" fixed="true" update="false" hidden="true"
    defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="createUser" alias="录入人" type="string" length="20" hidden="true"
    update="false" fixed="true" defaultValue="%user.userId">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="createDate" alias="录入时间" type="datetime"  xtype="datefield" update="false" hidden="true"
    fixed="true" defaultValue="%server.date.today">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="20" display="0" width="180" hidden="true" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    defaultValue="%user.userId" hidden="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"  />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期"  type="datetime"  xtype="datefield"
    defaultValue="%server.date.today" hidden="true">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
</entry>
