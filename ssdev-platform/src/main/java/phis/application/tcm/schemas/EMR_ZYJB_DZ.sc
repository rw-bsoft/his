<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_ZYJB_DZ" alias="中医疾病对照">
	<item id="JBBS" alias="疾病标识HIS"  length="12" type="long" not-null="1" pkey="true" display="0"/>
	<item id="JBBS_TCM" alias="疾病标识TCM"  length="12" type="long" not-null="1" pkey="true" display="0"/>	
    <item id="JBDM" alias="疾病代码HIS" type="string" length="20" not-null="1" />
    <item id="JBMC" alias="疾病名称HIS" type="string" length="60" not-null="1" width="130"/>
    <item id="JBDM_TCM" alias="疾病代码TCM" type="string" length="20" not-null="1" />
    <item id="JBMC_TCM" alias="疾病名称TCM" type="string" length="60" not-null="1" width="130"/>
    <item id="PYDM" alias="拼音码HIS" type="string" length="10"/>
    <item id="PYDM_TCM" alias="拼音码TCM" type="string" length="10"/>
</entry>
