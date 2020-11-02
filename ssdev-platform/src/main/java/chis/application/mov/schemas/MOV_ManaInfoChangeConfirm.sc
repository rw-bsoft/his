<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mov.schemas.MOV_ManaInfoChange"  alias="修改档案管理医生迁移确认记录" sort="applyDate desc">
  <item id="archiveMoveId" pkey="true" alias="记录序号" type="string"
    width="160" length="16" not-null="1" hidden="true"
    generator="assigned">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item ref="b.personName" queryable="true" not-null="1" length="20"  xtype="lookupfieldex" enableKeyEvents="true" />
  <item ref="b.idCard" queryable="true" length="20" width="160" display="1" />
  <item id="moveType" alias="迁移类别" type="string" length="1"
    display="0">
    <dic>
      <item key="1" text="申请迁入" />
      <item key="2" text="申请迁出" />
    </dic>
  </item>
  <item id="empiId" alias="empiid" type="string" length="32"    hidden="true" />
  <item id="status" alias="迁移状态" type="string" display="1" defaultValue="1">
    <dic id="chis.dictionary.archiveMoveStatus" />
  </item>
  <item id="applyUser" alias="申请人" type="string" length="20"
    queryable="true" fixed="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="applyDate" alias="申请日期" type="date" queryable="true" fixed="true" />

  <item id="applyReason" alias="申请原因" type="string" length="500"
    colspan="2" display="2" fixed="true" />
  <item id="applyUnit" alias="申请机构" type="string" length="20" fixed="true"  width="250" >
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
      onlySelectLeaf="true" />
  </item>
  <item id="affirmUnit" alias="确认机构" type="string" length="20" fixed="true"
    width="250"  defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
      onlySelectLeaf="true" />
  </item>
  <item id="affirmUser" alias="确认人" type="string" length="20"
    queryable="true" defaultValue="%user.userId"  fixed="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="affirmDate" alias="确认日期"  type="datetime"  xtype="datefield" queryable="true"  defaultValue="%server.date.today"  fixed="true">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="affirmView" alias="确认人意见" type="string" length="500"
    colspan="3" display="2"  />
  <item id="affirmType" alias="确认处理" type="string" length="1"
    queryable="true" display="1">
    <dic>
      <item key="1" text="同意迁移" />
      <item key="2" text="退回" />
    </dic>
  </item>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    display="1" defaultValue="%user.userId" >
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="1"  defaultValue="%server.date.today" >
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
    width="180" display="1" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
      onlySelectLeaf="true" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <relations>
    <relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
  </relations>
</entry>