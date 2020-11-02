<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="IDR_Report_HIV"  tableName="IDR_Report" alias="传染病报告卡-艾滋病附卡" >
  <item id="RecordID" alias="传染病报告卡记录编号" type="string" length="16" pkey="true"  not-null="1" fixed="true" hidden="true" generator="assigned" display="0"> 
    <key> 
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
    </key> 
  </item>
  <item id="SpecimenSourceCode" alias="样本来源编码" type="string" tag="radioGroup" length="1" colspan="2" not-null="1" >
    <dic>
      <item key="1" text="术前检测" />
      <item key="2" text="受血(制品)前检测" />
      <item key="3" text="性病门诊" />
      <item key="4" text="其他就诊者检测" />
      <item key="5" text="婚前检查(含涉外婚姻)" />
      <item key="6" text="孕产期检查" />
      <item key="7" text="检测咨询" />
      <item key="8" text="阳性者配偶或性伴检测" />
      <item key="9" text="女性阳性者子女检测" />
      <item key="10" text="职业暴露检测" />
      <item key="11" text="娱乐场所人员体检" />
      <item key="12" text="有偿供血(浆)人员检测" />
      <item key="13" text="无偿献血人员检测" />
      <item key="14" text="出入境人员体检" />
      <item key="15" text="新兵体检" />
      <item key="16" text="强制/劳教戒毒人员检测" />
      <item key="17" text="妇教所/女劳收教人员检测" />
      <item key="18" text="其他羁押人员体检" />
      <item key="19" text="专题调查" />
      <item key="20" text="其他" />
    </dic>
  </item>
  <item id="PossibleInfectionRouteCode" alias="最有可能感染途径编码" type="string" tag="radioGroup" length="1" colspan="1" not-null="1">
    <dic>
        <item key="1" text="注射毒品" />
        <item key="2" text="异性传播" />
        <item key="3" text="同性传播" />
        <item key="4" text="性接触 + 注射毒品" />
        <item key="5" text="采血(浆)" />
        <item key="6" text="输血/血制品" />
        <item key="7" text="母婴传播" />
        <item key="8" text="职业暴露" />
        <item key="9" text="不详" />
        <item key="10" text="其他" />
    </dic>
  </item>
  <item id="OtherSampleSource" alias="样本来源其它" type="string" length="20" />
   <item id="ContactHistoryCode" alias="接触史编码" type="string" tag="radioGroup" length="1" colspan="1" not-null="1">
    <dic>
        <item key="1" text="注射毒品史" />
        <item key="2" text="非婚异性性接触史" />
        <item key="3" text="配偶/固定性伴阳性" />
        <item key="4" text="男男性行为史" />
        <item key="5" text="献血（浆）史" />
        <item key="6" text="输血/血制品史" />
        <item key="7" text="母亲阳性" />
        <item key="8" text="职业暴露史" />
        <item key="9" text="手术史" />
        <item key="11" text="其他" />
        <item key="10" text="不详" />
        <item key="12" text="非商业" />
        <item key="13" text="商业" />
    </dic>
  </item>
  <item id="OtherContactHistory" alias="接触史其它" type="string" length="20"  />
  <item id="OtherInfectionRoute" alias="感染途径其它" type="string" length="20"  />
  <item id="AIDSDiagnosisDate" alias="艾滋病诊断日期"   queryable="true"  type="date" not-null="1"/>
  <item id="InjectionTogetherNum" alias="注射毒品史与病人共用过注射器人数 " type="string" length="20"  />
  <item id="NonmaritalSexNum" alias="非婚异性性接触史 与病人有非婚性行为人数 " type="string" length="20" />
  <item id="HomosexualSexNum" alias="男男性行为史 发生同性性行为人数 " type="string" length="20" />
    <item id="OtherSampleSource" alias="样本来源其它" type="string" length="20" />
   <item id="LaborTestConclusionCode" alias="实验室检测结论编码" type="string" tag="radioGroup" length="1" colspan="1" not-null="1">
    <dic>
       <item  key="1" text="确认结果阳性" />
        <item  key="2" text="替代策略检测阳性" />
        <item  key="3" text="核酸检测阳性" />
    </dic>
  </item>
  <item id="ConfirmedTestPositiveDate" alias="确认（替代策略）检测阳性日期 "   queryable="true"  type="date" />
  <item id="ConfirmedTestPositiveOrgName" alias="确认（替代策略）检测单位" type="string" length="20" />
  <item id="VenerealHistoryCode" alias="性病史编码" type="string" tag="radioGroup" length="1" colspan="1" not-null="1">
    <dic>
         <item  key="1" text="有" />
         <item  key="2" text="无" />
         <item  key="9" text="不详" />
    </dic>
  </item>
   <item id="ChlamydialTrachomatisCode" alias="生殖道沙眼衣原体感染编码" type="string" tag="radioGroup" length="1" colspan="1">
    <dic>
        <item key="1" text="确诊病例" />
        <item key="2" text="无症状感染" />
    </dic>
  </item>
</entry>