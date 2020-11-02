<?xml version="1.0" encoding="UTF-8"?>
<entry alias="糖尿病终止">
  <!-- <item id="terminalReason" alias="终止原因" type="string" length="1" not-null = "1" >
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
  <item id="terminalPerson" alias="终止人" type="string" length="20" defaultValue="%user.userId" fixed="true"  colspan="2" anchor="100%">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"       parentKey="%user.manageUnit.id" />
  </item>
  <item id="terminalDate" alias="终止日期"  type="datetime"  xtype="datefield" maxValue="%server.date.date" defaultValue="%server.date.today" fixed="true">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="terminalOrgan" alias="终止单位" type="string" length="20" fixed="true"  colspan="2" anchor="100%" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
  </item> -->
  <item id="visitEffect" alias="转归" type="string" length="1" defaultValue="9" colspan="3">
    <dic render="Radio">
      <item key="2" text="暂时失访" />
      <item key="9" text="终止管理" />
    </dic>
  </item>
  <item id="noVisitReason" alias="原因" type="string" length="100" colspan="3"/>
  <item id="terminalPerson" alias="终止人" type="string" length="20" defaultValue="%user.userId" fixed="true" anchor="100%">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"       parentKey="%user.manageUnit.id" />
  </item>
  <item id="terminalDate" alias="终止日期"  type="datetime"  xtype="datefield" maxValue="%server.date.date" defaultValue="%server.date.today" fixed="true">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="terminalOrgan" alias="终止单位" type="string" length="20" fixed="true" anchor="100%" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
  </item>
</entry>