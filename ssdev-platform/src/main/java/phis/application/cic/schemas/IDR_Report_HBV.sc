<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="IDR_Report_HIV"  tableName="IDR_Report" alias="传染病报告卡-艾滋病附卡" >
  <item id="RecordID" alias="传染病报告卡记录编号" type="string" length="16" pkey="true"  not-null="1" fixed="true" hidden="true" generator="assigned" display="0"> 
    <key> 
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
    </key> 
  </item>
  <item id="HBsAgPositiveTime" alias="HBsAg阳性时间" type="string" tag="radioGroup" length="1" colspan="2" not-null="1" >
    <dic>
      <item key="1" text="阴性" />
      <item key="2" text="阳性" />
      <item key="3" text="未测" />
    </dic>
  </item>
   <item id="HBsAgFirstAppearYear" alias="首次出现乙肝症状和体征时间-年" type="string" length="10" />
    <item id="HBsAgFirstAppearMonth" alias="首次出现乙肝症状和体征时间-月" type="string" length="10" />
    <item id="HBsAgFirstAppearUnknown" alias="首次出现乙肝症状和体征时间不详" type="string" length="10" />
    <item id="AlTCode" alias="本次ALT" type="string" length="10" />
  <item id="HBcIgM1TestConclusionCode" alias="抗-HBc IgM1:1000检测结果" type="string" tag="radioGroup" length="1" colspan="1" not-null="1">
    <dic>
        <item key="1" text="阴性" />
        <item key="2" text="阳性" />
        <item key="3" text="未测" />
    </dic>
  </item>
 
   <item id="LiverPunctureTestConclusionCode" alias="肝穿刺检测结果" type="string" tag="radioGroup" length="1" colspan="1" not-null="1">
    <dic>
        <item key="1" text="急性病变" />
        <item key="2" text="慢性病变" />
        <item key="3" text="未测" />
    </dic>
  </item>
   <item id="HBsAgToHBsTime" alias="恢复期血清HBsAg阴转，抗-HBs阳转" type="string" tag="radioGroup" length="1" colspan="1" not-null="1">
    <dic>
       <item  key="1" text="是" />
        <item  key="2" text="否" />
        <item  key="3" text="未测" />
    </dic>
  </item>
</entry>