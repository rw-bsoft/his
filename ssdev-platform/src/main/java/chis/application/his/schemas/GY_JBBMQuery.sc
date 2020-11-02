<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.his.schemas.GY_JBBMQuery" tableName="GY_JBBM" alias="疾病编码库">
  <item id="JBXH" alias="疾病序号" display="0" type="long" length="18" not-null="1" generator="assigned" pkey="true">
    <key>
      <rule name="increaseId" type="increase" length="12" startPos="22543"/>
    </key>
  </item>
  <item id="ICD10" alias="ICD编码" type="string" length="20" width="150" queryable="true"/>
  <item id="JBMC" alias="疾病名称" type="string" length="255" width="250" queryable="true"/>
  <item id="PYDM" alias="拼音码"  type="string" length="8" queryable="true" selected="true" width="120" target="JBMC" codeType="py"/> 	
  <item id="JBPB" alias="疾病判别" type="string" length="500" width="250"  colspan="2" queryable="true">
  	<dic id="phis.dictionary.diseaseKind" render="LovCombo" />
  </item>
  <item id="JBBGK" alias="疾病报告卡" type="string" length="2" width="200" queryable="true">
  	<dic id="phis.dictionary.diseaseReportType"/>
  </item>
</entry>