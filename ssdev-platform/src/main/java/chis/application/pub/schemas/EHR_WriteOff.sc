<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hr.schemas.EHR_HealthRecord"  alias="档案注销">
  <item id="cancellationReason" alias="注销原因" type="string" length="1" not-null = "1" >
    <dic>
      <item key="1" text="死亡" />
      <item key="2" text="迁出" />
      <item key="3" text="失访" />
      <item key="4" text="拒绝" />
      <item key="9" text="其他" />
    </dic>
  </item>
  <item id="deadReason" alias="死亡原因" type="string" fixed="true"     length="100" display="2"  colspan="2" anchor="100%"/>
  <item id="deadDate" alias="死亡日期" type="date" fixed="true"
    display="2" maxValue="%server.date.date"/>
  <item id="cancellationUser" alias="注销人" type="string" length="20" defaultValue="%user.userId" fixed="true"  colspan="2" anchor="100%">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"       parentKey="%user.manageUnit.id" />
  </item>
  <item id="cancellationDate" alias="注销日期"  type="datetime"  xtype="datefield" maxValue="%server.date.date" defaultValue="%server.date.today" fixed="true">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="cancellationUnit" alias="注销单位" type="string" length="20" fixed="true"  colspan="2" anchor="100%" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
  </item>
</entry>