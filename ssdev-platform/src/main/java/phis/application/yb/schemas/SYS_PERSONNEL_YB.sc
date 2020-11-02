<?xml version="1.0" encoding="UTF-8"?>

<entry alias="本地人员对照表" entityName="SYS_Personnel" >
	<item id="PERSONID" alias="人员编号" type="string" length="50" display="0"/>
	<item id="PERSONNAME" alias="人员姓名" type="string" length="50" fixed="true"/>
	<item id="GENDER" alias="性别" type="string" length="1" width="40" defaultValue="1" fixed="true">
		<dic id="platform.reg.dictionary.gender"/>
	</item>
	<item id="CARDNUM" alias="证件号码" type="string" length="25" width="150" fixed="true"/>
	<item id="PYCODE" alias="拼音码" type="string" length="50"  queryable="true" fixed="true"/>
	<item id="YBDM" alias="医保编码" type="string" length="20" width="150"/>
</entry>