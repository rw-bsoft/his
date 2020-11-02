<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="IDR_Report_AFP"  tableName="IDR_Report" alias="传染病报告卡-AFP附卡" >
  <item id="RecordID" alias="传染病报告卡记录编号" type="string" length="16" pkey="true"  not-null="1" fixed="true" hidden="true" generator="assigned" display="0"> 
    <key> 
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
    </key> 
  </item>
  <item id="PatientResidenceTypeCode" alias="病人所属地类型" type="string" tag="radioGroup" length="1" colspan="2" not-null="1" >
    <dic>
      <item key="0" text="本地" />
      <item key="1" text="异地" />
    </dic>
  </item>
  <item id="PalsyDate" alias="麻痹日期"   queryable="true"  type="date" not-null="1"/>
  <item id="TreatmentlandDate" alias="来现就诊地日期"   queryable="true"  type="date"/>
  <item id="TreatmentlandTypeCode" alias="现就诊地住址" type="string" tag="radioGroup" length="1" colspan="1" not-null="1">
    <dic>
        <item key="1" text="本县区" />
        <item key="2" text="本市其它县区" />
        <item key="3" text="本省其它设区" />
        <item key="4" text="其他省" />
        <item key="5" text="港澳台" />
        <item key="6" text="外籍" />
    </dic>
  </item>
   <item id="TreatmentlandZoneCode" alias="现就诊地住址" type="string" tag="radioGroup" length="1" colspan="1" >
    <dic>
        <item key="32012408" text="永阳" />
        <item key="32012401" text="白马" />
        <item key="32012402" text="东屏" />
        <item key="32012403" text="柘塘" />
        <item key="32012405" text="石湫" />
        <item key="32012404" text="洪蓝" />
        <item key="32012406" text="晶桥" />
        <item key="32012407" text="和凤" />
    </dic>
  </item>
  <item id="TreatmentlandLivingAddressDetails" alias="现就诊地详细住址" type="string" length="100" colspan="2"/>
  
  <item id="PalsySymptom" alias="麻痹症状" type="string" length="20"  />
</entry>