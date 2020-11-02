<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mhc.schemas.MHC_PregnantScreen" alias="孕妇产前筛查">
  <item id="pregnantId" alias="孕妇档案号" type="string" length="16"
    pkey="true" width="160" not-null="1" fixed="true" queryable="true"
    display="1" generator="assigned">
  </item>
  <item id="empiId" alias="EMPIID" length="32" not-null="1"
    display="0" />
  <item id="diagnoseDate" alias="产前诊断日期" type="date"  maxValue="%server.date.today" />
  <item id="diagnoseResult" alias="产前诊断结果" type="string" length="1">
    <dic>
      <item key="1" text="阳性" />
      <item key="2" text="阴性" />
    </dic>
  </item>
  <item id="screeningDate" alias="产前筛查日期" type="date"  maxValue="%server.date.today"/>
  <item id="screening" alias="产前筛查结果" type="string" length="1">
    <dic>
      <item key="1" text="低风险" />
      <item key="2" text="高风险" />
      <item key="3" text="未查" />
    </dic>
  </item>
  <item id="inputDate" alias="录入日期" type="datetime"  xtype="datefield" fixed="true" update="false"
    defaultValue="%server.date.today">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="inputUser" alias="录入员工" type="string" update="false" length="20" fixed="true" defaultValue="%user.userId">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="inputUnit" alias="录入单位" type="string" update="false" length="20" colspan="2" fixed="true" defaultValue="%user.manageUnit.id" width="150">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.manageUnit.id']</set>
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
