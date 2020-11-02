<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_BRRY" alias="家床病人入院" sort="JSRQ">
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1"
		display="0" generator="assigned" pkey="true" />
	<item id="ZYHM" alias="家床号" length="10" not-null="1" queryable="true"
		selected="true" />
	<item id="BRID" alias="病人id"  display="0" />
	<item id="BRXM" alias="姓名" length="40" not-null="1" queryable="true" />
	<item id="BRXB" alias="性别" type="int" length="4" width="50"
		not-null="1">
		<dic id="phis.dictionary.gender" />
	</item>
	<item id="RYNL" alias="年龄" width="50" />
	<item id="BRXZ" alias="性质" type="long" length="18" not-null="1"
		queryable="true">
		<dic id="phis.dictionary.patientProperties" />
	</item>
	<item id="JCZD" alias="诊断" width="140" />
	<item id="JCLX" alias="家床类型" type="long" not-null="1">
		<dic id="phis.dictionary.famliySickbedType" />
	</item>
	<item id="JCTS" alias="家床天数" virtual="true" width="80"  type="int" renderer="jctsRender"/>
	<item id="KSRQ" alias="开始日期" type="date" />
	<item id="JSRQ" alias="终止日期" type="date" />
	<item id="ZRYS" alias="责任医生" length="10">
		<dic id="phis.dictionary.doctor" />
	</item>
	<item id="ZRHS" alias="责任护士" length="10">
		<dic id="phis.dictionary.doctor" />
	</item>
	<item id="CYPB" alias="状态" type="int" length="10">
		<dic id="phis.dictionary.famliySickbedStatus" />
	</item>
	<item id="LXDZ" alias="联系地址" type="tring" display="0"/>
	<item id="LXDH" alias="联系电话" type="tring" display="0"/>
</entry>
