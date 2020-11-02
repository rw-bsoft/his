<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_SSDM_YR" tableName="GY_SSDM_YR" alias="手术代码引入">
	<item id="SSDM" alias="手术代码" type="string" length="10" not-null="1" pkey="true" />
	<item id="SSMC" alias="手术名称"  type="string" queryable="true" length="160" width="200"/>
	<item id="ZJDM" alias="拼音码"  type="string" length="100" queryable="true" selected="true" target="SSMC" codeType="py"/>
</entry>
