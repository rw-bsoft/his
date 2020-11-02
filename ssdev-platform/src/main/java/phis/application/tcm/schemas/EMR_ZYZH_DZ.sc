<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_ZYZH_DZ" alias="中医证侯对照">
	<item id="ZHBS" alias="证侯标识HIS"  length="12" type="long" not-null="1" pkey="true" display="0"/>
	<item id="ZHBS_TCM" alias="证侯标识TCM"  length="12" type="long" not-null="1" pkey="true" display="0"/>
	<item id="ZHDM" alias="证侯代码HIS" type="string" length="20" not-null="1"/>
	<item id="ZHMC" alias="证侯名称HIS" type="string" length="60" not-null="1" width="130"/>
	<item id="ZHDM_TCM" alias="证侯代码TCM" type="string" length="20" not-null="1"/>
	<item id="ZHMC_TCM" alias="证侯名称TCM" type="string" length="60" not-null="1" width="130"/>
	<item id="PYDM" alias="拼音码HIS" type="string" length="10"/>
	<item id="PYDM_TCM" alias="拼音码TCM" type="string" length="10"/>
</entry>
