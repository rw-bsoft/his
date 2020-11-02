<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hr.schemas.ADMIN_RecordRevert" alias="档案恢复列表" >
  <item id="phrId" alias="个人健康档案号" type="string" length="30"
    width="160" not-null="1" virtual="true" hidden="true" />
  <item id="pregnantId" alias="孕妇档案号" type="string" length="16"
    width="160" virtual="true" hidden="true" />
  <item id="recordId" alias="体弱儿档案号" type="string" length="16"
    width="160" virtual="true" hidden="true" />
  <item id="THRID" alias="肿瘤高危档案号" type="string" length="16"
    width="160" virtual="true" hidden="true" />  
  <item id="TCID" alias="肿瘤确诊记录号" type="string" length="16"
    width="160" virtual="true" hidden="true" />  
  <item id="TPRCID" alias="肿瘤现患报卡号" type="string" length="16"
    width="160" virtual="true" hidden="true" />  
  <item id="recordId" alias="初筛记录号" type="string" length="16"
    width="160" virtual="true" hidden="true" />  
  <item id="gcId" alias="问卷记录录号" type="string" length="16"
    width="160" virtual="true" hidden="true" /> 
  <item id="personName" alias="姓名" type="string" length="20"
    not-null="1" virtual="true" hidden="true" />
  <item id="empiId" alias="empiId" type="string" length="32"
    fixed="true" virtual="true" hidden="true" />
  <item id="type" alias="档案类型" type="string" length="2" width="100" virtual="true">
    <dic>
      <item key="1" text="个人健康档案" />
      <item key="2" text="高血压档案" />
      <item key="3" text="糖尿病档案" />
      <item key="4" text="孕产妇档案" />
      <item key="5" text="儿童档案" />
      <item key="6" text="体弱儿档案" />
      <item key="7" text="精神病档案" />
      <item key="8" text="老年人档案" />
      <item key="9" text="肿瘤档案" />
      <item key="10" text="血吸虫档案" />
      <item key="11" text="狂犬病档案" />
      <item key="12" text="肢体残疾登记" />
      <item key="13" text="脑瘫残疾登记" />
      <item key="14" text="智力残疾登记" />
      <item key="15" text="肿瘤高危档案" />
      <item key="16" text="肿瘤患者报告卡" />
      <item key="17" text="离休干部档案" />
      <item key="18" text="肿瘤确诊记录" />
      <item key="19" text="肿瘤初筛记录" />
      <item key="20" text="肿瘤问卷记录" />
      <item key="21" text="高血压高危档案" />
    </dic>
  </item>
  <item id="status" alias="档案状态" type="string" length="1"
    defaultValue="0" virtual="true">
    <dic>
      <item key="0" text="正常" />
      <item key="1" text="已注销" />
      <item key="2" text="未审核" />
      <item key="3" text="终止妊娠" />
    </dic>
  </item>
  <item id="highRiskType" alias="高危类别" type="string" length="2" >
		<dic id="chis.dictionary.tumourHighRiskType"/>
  </item>
  <item id="manaUnitId" alias="管辖机构" type="string" length="20"
    colspan="2" anchor="100%" width="180" not-null="1" fixed="true"
    virtual="true">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
  </item>
  <item id="cancellationUnit" alias="注销单位" type="string" length="20"
    width="180" virtual="true">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
  </item>
  <item id="cancellationUser" alias="注销人" type="string" length="20"
    virtual="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
  </item>
  <item id="cancellationDate" alias="注销日期" type="date" virtual="true" />

  <item id="cancellationReason" alias="注销原因" type="string" length="1"
    virtual="true">
    <dic>
      <item key="1" text="死亡" />
      <item key="2" text="迁出" />
      <item key="3" text="失访" />
      <item key="4" text="拒绝" />
      <item key="5" text="终止妊娠" />
      <item key="6" text="作废" />
      <item key="9" text="其他" />
    </dic>
  </item>
  <item id="createUnit" alias="录入单位" type="string" length="20"
    width="180" fixed="true" update="false"
    defaultValue="%user.manageUnit.id" virtual="true">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="createUser" alias="录入人员" type="string" length="20"
    update="false" fixed="true" defaultValue="%user.userId"
    virtual="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="createDate" alias="录入日期" type="datetime"  xtype="datefield" update="false"
    fixed="true" defaultValue="%server.date.today" virtual="true" >
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    defaultValue="%user.userId" virtual="true" display="1">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
    defaultValue="%user.manageUnit.id" virtual="true" display="1">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
    defaultValue="%server.date.today" virtual="true" display="1">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
</entry>
