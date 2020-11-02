<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="SYS_Personnel" alias="员工代码">
	<item id="PERSONID" alias="员工代码" display="2" type="string" length="10" not-null="1" generator="assigned" pkey="true"/>
	<item id="ORGANIZCODE" alias="所属机构" display="2" type="string" length="50" not-null="1" />
	<item id="OFFICECODE" alias="所属科室" fixed="true" type="string" length="18" not-null="1" pkey="true">
		<dic id="phis.dictionary.department"></dic>
	</item>	
	<item id="PERSONNAME" alias="医生姓名" type="string" length="50"/>
	<item id="GENDER" alias="员工性别" type="string" length="4" >
		<dic id="phis.dictionary.gender" />
	</item>
</entry>