<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_BRRY" alias="家床病人入院">
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1"
		display="0" generator="assigned" pkey="true" />
	<item id="ZYHM" alias="家床号" length="10" not-null="1" queryable="true"
		selected="true" fixed="true"/>
	<item id="BRXM" alias="姓名" length="40" not-null="1" queryable="true"
		fixed="true" />
	<item id="BRXB" alias="性别" type="int" length="4" width="50"
		not-null="1" fixed="true">
		<dic id="phis.dictionary.gender" />
	</item>
	<item id="RYNL" alias="年龄" width="50" fixed="true" />
	<item id="BRXZ" alias="性质" type="long" length="18" fixed="true">
		<dic id="phis.dictionary.patientProperties" />
	</item>
	<item id="JCLX" alias="家床类型" type="long" not-null="1" fixed="true">
		<dic id="phis.dictionary.famliySickbedType" />
	</item>
	<item id="KSRQ" alias="开始日期" type="date" fixed="true" />
	<item id="JSRQ" alias="终止日期" type="date" fixed="true" />
	<item id="ZRYS" alias="责任医生" length="10" fixed="true">
		<dic id="phis.dictionary.doctor" />
	</item>
	<item id="JCZD" alias="诊断" length="10" fixed="true" />

</entry>
