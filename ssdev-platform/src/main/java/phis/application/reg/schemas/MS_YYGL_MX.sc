<?xml version="1.0" encoding="UTF-8"?>

<entry id="MS_YYGL_MX" tableName="MS_YYGH" alias="预约管理明细">
	<item id="KSDM" alias="科室名称" fixed="true" type="long" width="210" length="18" not-null="1" pkey="true">
		<dic id="phis.dictionary.department" autoLoad="true"></dic>
	</item>
	<item id="YSDM" alias="医生姓名" fixed="true" type="string" width="220" length="10" not-null="1" pkey="true">
		<dic id="phis.dictionary.doctor" autoLoad="true"></dic>
	</item>
	<item id="MZHM" alias="门诊号码" length="10" type="String" display="0" virtual="true"/>
	<item id="YYXH" alias="预约序号" length="10" type="long" display="0" virtual="true"/>
	<item id="JZXH" alias="就诊序号" length="10" type="int" display="0" virtual="true"/>
</entry>