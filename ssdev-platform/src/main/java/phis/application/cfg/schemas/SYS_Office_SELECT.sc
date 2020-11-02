<?xml version="1.0" encoding="UTF-8"?>
<entry id="SYS_Office_SELECT" tableName="SYS_Office" alias="科室代码_药房科室选择">
	<item id="ID" alias="科室代码"  length="15" width="65" display="0" generator="assigned" pkey="true"/>	
	<item id="OFFICENAME" alias="所有科室" not-null="true" width="100" type="string" length="50"/>
	<item id="MRZ" alias="默认值" virtual="true" type="int" display="0"/>
	<item id="PYCODE" alias="拼音代码" type="string" length="6" display="0" queryable="true" target="KSMC" codeType="py"/>
</entry>
