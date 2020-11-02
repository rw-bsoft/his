<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="SYS_Personnel1" alias="员工代码" tableName="SYS_Personnel">
	<item id="PERSONID" alias="员工代码" display="0" type="string" length="10" not-null="1" generator="assigned" pkey="true"/>
	<item id="OFFICECODE" alias="科室代码" display="0" length="18" not-null="1" type="string"/>
	<item id="PERSONID" alias="员工编号" display="1"  type="string" length="10" not-null="1"/>
	<item id="PERSONNAME" alias="员工姓名" display="1" type="string"/>
	<item id="PYCODE" alias="拼音码" display="0" queryable="true" type="string"/>
</entry>
