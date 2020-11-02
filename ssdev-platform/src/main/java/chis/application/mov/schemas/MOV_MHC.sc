<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mov.schemas.MOV_MHC"   alias="孕妇户籍地址迁移列表" sort="applyDate desc">
  <item id="archiveMoveId" pkey="true" alias="记录序号" type="string"
    width="160" length="16" not-null="1" hidden="true"
    generator="assigned">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="moveType" alias="迁移类别" type="string" length="1" 		display="0">
    <dic>
      <item key="1" text="申请迁入" />
      <item key="2" text="申请迁出" />
    </dic>
  </item>
  <item id="archiveId" alias="孕妇档案号" type="string" length="30"
    fixed="true" width="160" not-null="1" />
  <item id="phrId" alias="孕妇健康档案号" type="string" length="30"
    fixed="true"  width="160" not-null="1"  hidden="true"/>
  <item id="personName" alias="孕妇姓名" type="string" length="20"
    not-null="1" queryable="true"   fixed="true"/>
  <item id="status" alias="迁移状态" type="string" display="1">
    <dic id="chis.dictionary.archiveMoveStatus" />
  </item>
  <item id="affirmType" alias="确认处理" type="string" length="1"
    queryable="true" fixed="true" display="1">
    <dic>
      <item key="1" text="同意迁移" />
      <item key="2" text="退回" />
    </dic>
  </item>
  <item id="sourceOwnerArea" alias="原归属地" width="80" length="2"
    fixed="true" not-null="1">
    <dic id="chis.dictionary.pregnantOwnerArea" />
  </item>
  <item id="sourceHomeAddress" alias="原户籍地址" type="string" length="25"
    fixed="true" width="200" queryable="true" not-null="1" colspan="2">
    <dic id="chis.dictionary.areaGrid" minChars="4" includeParentMinLen="6" filterMin="10"
      filterMax="18" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="sourceHomeAddress_text" length="200" display="0"/>  
  <item id="sourceResidenceCode" alias="原居住证号码" width="80"
    length="20" type="string" fixed="true" />
  <item id="sourceRestRegion" alias="原产休地" type="string" length="25"
    fixed="true" width="250" queryable="true" not-null="1" colspan="2">
    <dic id="chis.dictionary.areaGrid" minChars="4" includeParentMinLen="6" filterMin="10"
      filterMax="18" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="sourceRestRegion_text" length="200" display="0"/>  
  <item id="sourceMhcDoctorId" alias="原妇保医生" type="string" length="20"
    not-null="1" fixed="true">
    <dic id="chis.dictionary.user03" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="sourceManaUnitId" alias="原管辖机构" type="string" length="20"
    fixed="true" width="250" queryable="true" not-null="1" colspan="2">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  lengthLimit="9"  render="Tree"  rootVisible = "true"
      parentKey="%user.manageUnit.id" />
  </item>
  <item id="targetOwnerArea" alias="现归属地" width="80" length="2"
    not-null="1" fixed="true">
    <dic id="chis.dictionary.pregnantOwnerArea" />
  </item>
  <item id="targetHomeAddress" alias="现户籍地址" type="string" length="25"
    width="200" not-null="1" queryable="true" colspan="2" fixed="true">
    <dic id="chis.dictionary.areaGrid" minChars="4" includeParentMinLen="6" filterMin="10"
      filterMax="18" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="targetHomeAddress_text" length="200" display="0"/>  
  <item id="targetResidenceCode" alias="现居住证号码" width="80"
    length="20" type="string" fixed="true"/>
  <item id="targetRestRegion" alias="现产休地" type="string" length="25"
    width="200" queryable="true" not-null="1" colspan="2" fixed="true">
    <dic id="chis.dictionary.areaGrid" minChars="4" includeParentMinLen="6" filterMin="10"
      filterMax="18" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="targetRestRegion_text" length="200" display="0"/>  
  <item id="targetMhcDoctorId" alias="现妇保医生" type="string" length="20" not-null="1">
    <dic id="chis.dictionary.user03" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="targetManaUnitId" alias="现管辖机构" type="string" length="20"
    fixed="true" width="250" not-null="true" queryable="true"
    colspan="3">
    <dic id="chis.@manageUnit" includeParentMinLen="6" onlySelectLeaf="true" lengthLimit="9" querySliceType="0" render="Tree" />
  </item>
  <item id="applyReason" alias="申请原因" type="string" length="500"
    display="2" colspan="3" fixed="true"/>
  <item id="applyUnit" alias="申请机构" type="string" length="20"
    fixed="true" width="250">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
      onlySelectLeaf="true" />
  </item>
  <item id="applyUser" alias="申请人" type="string" length="20"
    queryable="true" fixed="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="applyDate" alias="申请日期" type="date" queryable="true"
    fixed="true" />
  <item id="affirmView" alias="确认人意见" type="string" length="500"
    colspan="3"   display="2"/>
  <item id="affirmUnit" alias="确认机构" type="string" length="20"
    fixed="true" width="250"  defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" />
  </item>
  <item id="affirmUser" alias="确认人" type="string" length="20"
    queryable="true" fixed="true"  defaultValue="%user.userId">
    <dic id="chis.dictionary.user14" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="affirmDate" alias="确认日期" type="date" queryable="true"
    fixed="true" />
  <item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1" width="180" hidden="true" defaultValue="%user.manageUnit.id">
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