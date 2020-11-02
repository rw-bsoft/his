<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mhc.schemas.MHC_PregnantWomanIndex" alias="孕妇指标" sort="indexCode">
  <item id="indexId" alias="孕妇指标编号" length="30" pkey="true"
    type="string" not-null="1" fixed="true" hidden="true"
    generator="assigned">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="pregnantId" alias="孕妇档案编号" length="30" 
    type="string" not-null="1" fixed="true" hidden="true"/>
  <item id="empiId" alias="EMPIID" type="string" length="32" hidden="true" />
  <item id="indexType" alias="指标类型" type="string" width="160" hidden="true"/>
  <item id="indexName" alias="指标名称" type="string" length="100" width="150" fixed="true"/>
  <item id="indexValue" alias="指标值"  type="string"  width="100" >
    <dic id="chis.dictionary.indexValue1"/>
  </item>
  <item id="ifException" alias="是否异常" type="string" width="100" defaultValue="1">
    <dic render="Simple">  
      <item key="1" text="未见异常"/>
      <item key="2" text="异常"/>
      <item key="3" text="未查"/>
    </dic>
  </item>
  <item id="exceptionDesc" alias="异常描述" type="string" length="200" width="400" colspan="0"/>
  <item id="referenceValue" alias="参考值" type="string" hidden="true"/>
  <item id="indexSourceId" alias="指标来源单号"  type="string" length="50" hidden="true"/>
  <item id="indexSourceType" alias="指标来源类型" type="string" hidden="true"/>
  <item id="indexCode" alias="指标代码" length="50" type="string" hidden="true"/>
  <item id="indexCreateDate" alias="指标产生日期" type="date" hidden="true" 
    defaultValue="%server.date.today" />
  <item id="createUnit" alias="产生机构" type="string" length="20" width="180" hidden="true"
    defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="indexUnit" alias="指标单位"  type="string" length="20" width="180" hidden="true"
    defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
  </item>
  <item id="visitId" alias="随访序号" length="30"  type="string"   hidden="true" />
  <item id="createUser" alias="录入人" type="string" length="20"  hidden="true"
    update="false" fixed="true" defaultValue="%user.userId">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>	
  </item>
  <item id="createDate" alias="录入时间" type="datetime"  xtype="datefield" update="false"  hidden="true"
    fixed="true" defaultValue="%server.date.today">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="20" display="0" width="180" hidden="true" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    defaultValue="%user.userId" hidden="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
    defaultValue="%server.date.today" hidden="true">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
</entry>
