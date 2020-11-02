<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="IDR_Report_HFMD"  tableName="IDR_Report" alias="传染病报告卡-手足口病附卡" >
  <item id="RecordID" alias="传染病报告卡记录编号" type="string" length="16" pkey="true"  not-null="1" fixed="true" hidden="true" generator="assigned" display="0"> 
    <key> 
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
    </key> 
  </item>
  <item id="IntensivePatientCode" alias="患者本人是否是重症患者" type="string" tag="radioGroup" length="1" colspan="3"  not-null="1">
    <dic>
      <item key="1" text="是" />
      <item key="0" text="否" />
    </dic>
  </item>
  <item id="LaborTestResultCode" alias="患者本人实验室诊断结果" type="string" tag="radioGroup" length="1" colspan="3">
    <dic>
      <item key="1" text="EV71" />
      <item key="2" text="Cox A16" />
	  <item key="3" text="其他肠道病毒" />
    </dic>
  </item>
</entry>