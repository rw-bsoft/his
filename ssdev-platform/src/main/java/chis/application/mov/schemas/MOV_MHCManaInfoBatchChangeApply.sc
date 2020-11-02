<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mov.schemas.MOV_ManaInfoBatchChange"  alias="批量修改孕妇档案管理医生申请记录" sort="applyDate desc">
  <item id="archiveMoveId" pkey="true" alias="记录序号" type="string"
    width="160" length="16" not-null="1" hidden="true"
    generator="assigned">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="archiveType" alias="档案类别" type="string" queryable="true"
    display="1">
    <dic>
      <item key="1" text="个人健康档案" />
      <item key="5" text="儿童档案" />
      <item key="6" text="孕产妇档案" />
    </dic>
  </item>
  <item id="moveType" alias="迁移类别" type="string" length="1"
    display="0">
    <dic>
      <item key="1" text="申请迁入" />
      <item key="2" text="申请迁出" />
    </dic>
  </item>
  <item id="targetDoctor" alias="现妇保医生" type="string" length="20" not-null="1">
    <dic id="chis.dictionary.user03" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" keyNotUniquely="true"/>
  </item>
  <item id="targetUnit" alias="现管辖机构" type="string" length="20"
    width="320" not-null="true" colspan="2" queryable="true" fixed="true">
    <dic id="chis.@manageUnit" includeParentMinLen="6" onlySelectLeaf="true" lengthLimit="9" querySliceType="0" render="Tree" />
  </item>
  <item id="applyUser" alias="申请人" type="string" length="20"
    queryable="true" defaultValue="%user.userId" fixed="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="applyUnit" alias="申请机构" type="string" length="20" fixed="true" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
      onlySelectLeaf="true" />
  </item>
  <item id="applyDate" alias="申请日期" type="datetime"  xtype="datefield" queryable="true"
    defaultValue="%server.date.today" fixed="true" >
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="applyReason" alias="申请原因" type="string" length="500"
    colspan="3" display="2" />
  <item id="status" alias="迁移状态" type="string" display="1"  defaultValue="1">
    <dic id="chis.dictionary.archiveMoveStatus" />
  </item>
  <item id="affirmUser" alias="确认人" type="string" length="20"
    queryable="true" fixed="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="affirmUnit" alias="确认机构" type="string" length="20"
    width="180"  fixed="true">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
      onlySelectLeaf="true" />
  </item>
  <item id="affirmDate" alias="确认日期" type="date" queryable="true"
    fixed="true" />
  <item id="affirmType" alias="处理结果" type="string" length="1"
    fixed="true" queryable="true" display="1">
    <dic>
      <item key="1" text="同意迁移" />
      <item key="2" text="退回" />
    </dic>
  </item>
  <item id="affirmView" alias="确认人意见" type="string" length="500"
    colspan="3" display="3" fixed="true" />
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    display="1" defaultValue="%user.userId" >
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="1" defaultValue="%server.date.today" >
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
    width="180" display="1" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
      onlySelectLeaf="true" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
</entry>