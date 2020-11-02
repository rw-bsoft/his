<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_JBBM_TCM" alias="疾病编码库(省中医馆)">
  <item id="JBXH" alias="疾病序号" display="0" type="long" length="18" not-null="1" generator="assigned" pkey="true">
    <key>
      <rule name="increaseId" type="increase" length="12" startPos="22543"/>
    </key>
  </item>
  <item id="ICD10" alias="ICD编码" type="string" length="20" width="80" />
  <item id="JBMC" alias="疾病名称" type="string" length="255" width="200" />
  <item id="PYDM" alias="拼音码"  type="string" length="8" width="100" /> 
</entry>
