<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="IDR_Report_Verify" alias="传染病报告卡审核">
  <item id="RecordID" alias="传染病报告卡记录编号" type="string" length="16" pkey="true"  not-null="1" fixed="true" hidden="true" generator="assigned" display="0"> 
    <key> 
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
    </key> 
  </item>
  <item id="finishStatus" alias="审核结果" type="string" tag="radioGroup" length="1" colspan="3">
    <dic>
      <item key="1" text="同意" />
      <item key="2" text="不同意" />
    </dic>
  </item>
  <item id="finishReason" alias="审核意见" type="string" length="200" xtype="textarea" colspan="3"/>
</entry>