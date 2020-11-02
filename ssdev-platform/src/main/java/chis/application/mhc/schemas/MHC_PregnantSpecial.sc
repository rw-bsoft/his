<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mhc.schemas.MHC_PregnantSpecial" alias="孕妇特殊情况">
  <item id="specialId" alias="记录序号" type="string" length="16"
    width="160" not-null="1" generator="assigned" pkey="true"
    hidden="true" display="1">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="empiId" alias="EMPIID" length="32" not-null="1"
    hidden="true" display="0" />
  <item id="phrId" alias="健康档案编号" length="30" width="160" display="0"
    hidden="true" not-null="1" />
  <item id="pregnantId" alias="孕妇档案编号" length="30" hidden="true" />
  <item id="occurDate" alias="发生日期" type="date" not-null="1" display="2" enableKeyEvents="true" maxValue="%server.date.today" />
  <item id="gestationalWeeks" alias="孕周" not-null="1" width="60" fixed="true"/>
  <item id="transactDate" alias="处理日期" type="date" width="90" enableKeyEvents="true" maxValue="%server.date.today" />
  <item id="problems" alias="主要问题" type="string" length="200" display="2" xtype="textarea" colspan="3" anchor="100%"/>
  <item id="transactDesc" alias="处理情况" type="string" length="100"
    display="2" xtype="textarea" colspan="3" anchor="100%" />

  <item id="inputUnit" alias="录入单位" type="string" length="20"
    update="false" width="180" fixed="true"
    defaultValue="%user.manageUnit.id" display="2">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="inputUser" alias="录入人员" type="string" length="20"
    update="false" display="2" fixed="true" defaultValue="%user.userId">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="inputDate" alias="录入日期"  type="datetime"  xtype="datefield"  fixed="true"
    update="false" display="2" defaultValue="%server.date.today" >
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
