<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_BRRY" alias="病人入院">
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true"/>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0" not-null="1"/>
	<item id="BRID" alias="病人ID" type="long" length="18" display="0" not-null="1"/>
	<item id="ZYHM" alias="住院号码" length="10" not-null="1"/>
	<item id="BRXM" alias="病人姓名" length="20" not-null="1"/>
	<item id="BRXZ" alias="病人性质" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.patientProperties_ZY" autoLoad="true"/>
	</item>
	<item id="KSRQ" alias="开始日期" type="date" width="130" not-null="1"/>
	<item id="JSRQ" alias="终止日期" type="date" width="130"/>
	<item id="CCJSRQ" alias="结算日期" type="date" display="0"/>
	<item id="JSCS" alias="结算次数" type="int" length="3" not-null="1" display="0"/>
	<item id="CYPB" alias="备注" type="int" length="2" not-null="1">
		<dic id="phis.dictionary.famliySickbedStatus" autoLoad="true" />
	</item>
</entry>
