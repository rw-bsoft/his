<?xml version="1.0" encoding="UTF-8" ?>
<entry alias="单病种目录" entityName="NH_BSOFT_JBBM_DRGS" sort="">
<item alias="单病种编码" id="CODE" not-null="true" pkey="true" type="long"/>
<item alias="单病种名称" id="NAME" length="40" type="string" queryable="true" />
<item alias="拼音编码" id="PYDM" length="8" type="string" queryable="true"/>
<item alias="ICD10" id="ICD10" length="10" type="string"/>
<item alias="" id="BCJEFD" type="double"/>
<item alias="机构类别" id="JGLB" length="10" not-null="true" type="string"/>
<item alias="" id="MZFS" not-null="true" type="double"/>
<item alias="" id="JGID" not-null="true" type="string"/>
</entry>