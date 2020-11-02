<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="IDR_Report_MD"  tableName="IDR_Report" alias="传染病报告卡-梅毒病附卡" >
  <item id="RecordID" alias="传染病报告卡记录编号" type="string" length="16" pkey="true"  not-null="1" fixed="true" hidden="true" generator="assigned" display="0"> 
    <key> 
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
    </key> 
  </item>
  <item id="SyphilisTestConclusionCode" alias="检验结果（含滴度）" type="string" length="20" colspan="3"/>
  <item id="SyphiliClinicalManifestation" alias="特征性临床表现" type="string" length="20" colspan="3" />
  <item id="SyphiliReportingDepartment" alias="报告科室" type="string" length="20" colspan="3" />
</entry>