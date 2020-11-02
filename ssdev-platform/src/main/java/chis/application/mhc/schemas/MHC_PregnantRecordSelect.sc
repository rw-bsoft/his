<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mhc.schemas.MHC_PregnantRecordSelect" alias="孕妇基本信息"
  sort="createDate desc">
  <item id="status" alias="状态" length="1" display="1"  type="string" 
    defaultValue="0">
    <dic>
      <item key="0" text="正常" />
      <item key="1" text="注销" />
      <item key="2" text="(注销）待核实" />
      <item key="3" text="终止妊娠" />
    </dic>
  </item>
  <item id="pregnantId" alias="孕妇档案号" type="string" length="16"
    pkey="true" width="120" not-null="1" fixed="true" queryable="true"
    display="1" />
  <item id="pregnantBookId" alias="孕册号" length="50" width="80"  type="string" 
    not-null="1" anchor="100%" queryable="true"/>
  <item id="mhcDoctorId" alias="妇保医生" type="string" length="20"
    not-null="1" width="100" colspan="2" update="false">
    <dic id="chis.dictionary.user03" render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="lastMenstrualPeriod" alias="末次月经时间" type="date"
    width="80" not-null="1" anchor="100%" maxValue="%server.date.today" colspan="2"/>
  <item id="dateOfPrenatal" alias="预产期" type="date" not-null="1"
    width="80" fixed="true" />
  <item id="createUnit" alias="建册单位编号" type="string" length="20"
    display="1" width="140" defaultValue="%user.manageUnit.id"
    anchor="100%" fixed="true" colspan="2">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="createUser" alias="建册医生编号" type="string" length="20"
    display="1" fixed="true" defaultValue="%user.userId"   width="100"
    queryable="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="createDate" alias="建册日期" type="datetime"  xtype="datefield"  fixed="true"
    display="1" defaultValue="%server.date.today" queryable="true" >
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
</entry>
