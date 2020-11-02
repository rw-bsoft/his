<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mov.schemas.MOV_Changeareagrid" alias="根据网格地址批量修改责任医生">
  <item id="Id" pkey="true" alias="记录序号" type="string"
    width="160" length="16" not-null="1" display="1"
    generator="assigned">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="status" alias="迁移状态" type="string" display="1" defaultValue="1">
    <dic id="chis.dictionary.archiveMoveStatus" />
  </item>
  <item id="affirmType" alias="确认处理" type="string" length="1"
    fixed="true" queryable="true" display="1">
    <dic>
      <item key="1" text="同意迁移" />
      <item key="2" text="退回" />
    </dic>
  </item>
  <item id="lastareagrid" alias="迁移网格地址" type="string" length="25" not-null="1" width="200" colspan="2" anchor="100%"  queryable="true">
	<dic id="chis.dictionary.areaGrid" includeParentMinLen="6" filterMin="10" minChars="4" filterMax="18" render="Tree" onlySelectLeaf="false"
		 parentKey="%user.regionCode" />
  </item>
  <item id="targetDoctor" alias="现医生" type="string" length="20" 
    not-null="1">
    <dic id="chis.dictionary.user04" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
  </item>
  <item id="nowupperareagrid" alias="现上级网格地址" type="string" length="25" not-null="1" width="200" colspan="2" anchor="100%"  queryable="true">
	<dic id="chis.dictionary.areaGrid" includeParentMinLen="6" filterMin="10" minChars="4" filterMax="18" render="Tree" onlySelectLeaf="false"
		 parentKey="%user.regionCode" />
  </item>
  <item id="targetUnit" alias="现管辖机构" type="string" length="20" fixed="true"
    width="320" not-null="true" colspan="2" queryable="true">
    <dic id="chis.@manageUnit" includeParentMinLen="6" onlySelectLeaf="true" lengthLimit="9" querySliceType="0" render="Tree" rootVisible = "true" parentKey="%user.manageUnit.id"/>
  </item>
  <item id="applyUser" alias="申请人" type="string" length="20"
    queryable="true"  fixed="true">
    <dic id="chis.dictionary.user05" render="Tree" onlySelectLeaf="true"  parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="applyUnit" alias="申请机构" type="string" length="20" width="250"  fixed="true">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
      onlySelectLeaf="true" />
      <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="applyDate" alias="申请日期" type="date" queryable="true"  fixed="true" >
  	<set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="applyReason" alias="申请原因" type="string" length="500"
    colspan="3" display="2"  fixed="true" />
  <item id="affirmUser" alias="确认人" type="string" length="20" fixed="true"
    queryable="true" >
    <dic id="chis.dictionary.user14" render="Tree" onlySelectLeaf="true"  parentKey="%user.manageUnit.id" />
  </item>
  <item id="affirmUnit" alias="确认机构" type="string" length="20" fixed="true"
    width="250" display="2" >
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
      onlySelectLeaf="true" />
  </item>
  <item id="affirmDate" alias="确认日期" type="date" queryable="true" fixed="true" />
  <item id="affirmView" alias="确认人意见" type="string" length="500" not-null="true"
    colspan="2" display="2"  />
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    display="1" defaultValue="%user.userId" >
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"  display="1" defaultValue="%server.date.today" >
     <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
    width="180" display="1" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
      onlySelectLeaf="true" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
</entry>