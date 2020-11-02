<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="GY_JBBM_DZ" alias="疾病编码对照" sort="JBXH,JBXH_TCM">
	<item id="JBXH" alias="疾病序号"  length="12" type="long" not-null="1" pkey="true" display="0"/>
	<item id="JBXH_TCM" alias="疾病序号（省中医馆）"  length="12" type="long" not-null="1" pkey="true" display="0"/>
  	<item id="ICD10" alias="ICD编码HIS" type="string" length="20" width="80"/>
  	<item id="JBMC" alias="疾病名称HIS" type="string" length="255" width="200"/>
  	<item id="ICD10_TCM" alias="ICD编码TCM" type="string" length="20" width="80"/>
  	<item id="JBMC_TCM" alias="疾病名称TCM" type="string" length="255" width="200"/>
  	<item id="PYDM" alias="拼音码HIS"  type="string" length="8" width="100"/>
  	<item id="PYDM_TCM" alias="拼音码TCM"  type="string" length="8" width="100"/>
</entry>

