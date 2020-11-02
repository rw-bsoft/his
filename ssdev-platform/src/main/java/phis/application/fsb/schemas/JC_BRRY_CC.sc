<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_BRRY" alias="病人入院">
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1"
		display="0"  pkey="true" />
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"
		display="0" />
	<item id="JCBH" alias="家床编号" length="10" not-null="1" fixed="true"
		layout="JCXX" />
	<item id="ZYHM" alias="家床号" length="10" not-null="1" fixed="true"
		layout="JCXX" />
	<item id="BRXZ" alias="性质" type="long" length="18" layout="JCXX" fixed="true">
		<dic id="phis.dictionary.patientProperties" autoLoad="true" />
	</item>
	<item id="BRXM" alias="姓名" length="20" not-null="1" fixed="true"
		layout="JBXX" />
	<item id="BRXB" alias="性别" type="int" length="4" not-null="1"
		fixed="true" layout="JBXX">
		<dic id="phis.dictionary.gender" autoLoad="true" />
	</item>
	<item id="RYNL" alias="年龄" layout="JBXX" fixed="true"/>
	<item id="SFZH" alias="身份证" length="10" not-null="1" fixed="true"
		layout="JBXX" />
	<item id="LXDZ" alias="地址" colspan="2" fixed="true" layout="JBXX" />
	<item id="LXRM" alias="联系人" layout="JBXX" fixed="true"/>
	<item id="LXGX" alias="与患关系" layout="JBXX" fixed="true">
		<dic id="phis.dictionary.GB_T4761" autoLoad="true" />
	</item>
	<item id="LXDH" alias="联系电话" layout="JBXX" fixed="true"/>
	<item id="JCLX" alias="家床类型" type="long" not-null="1" layout="JBXX" fixed="true">
		<dic id="phis.dictionary.famliySickbedType" autoLoad="true" />
	</item>
	<item id="KSRQ" alias="开始日期" type="date" layout="JBXX" fixed="true" />
	<item id="JSRQ" alias="终止日期" type="date" layout="JBXX" fixed="true" />
	<item id="CYRQ" alias="撤床日期" type="date" layout="CYXX" defaultValue="%server.date.date" />
	<item id="JCTS" alias="建床天数" virtual="true" layout="CYXX" fixed="true"/>
	<item id="CYFS" alias="转归" type="int" length="1" not-null="1"
		layout="CYXX">
		<dic id="phis.dictionary.outcomeSituation" autoLoad="true" />
	</item>
	<item id="JCZD" alias="建床诊断" type="tring" layout="JBXX" virtual="true" fixed="true"/>
	<item id="ICD10_JC" alias="ICD10码" type="string" layout="JBXX" fixed="true"
		virtual="true" />
	<item id="ZDRQ" alias="诊断日期" type="date" layout="JBXX" virtual="true" fixed="true"/>
	<item id="CCZD" alias="撤床诊断" type="tring" layout="JBXX" virtual="true" fixed="true"/>
	<item id="ICD10_CC" alias="ICD10码" type="string" layout="JBXX" fixed="true"
		virtual="true" />
	<item id="CCRQ" alias="诊断日期" type="date" layout="JBXX" virtual="true" fixed="true"/>
	<item id="CCQK" alias="经过治疗撤床时情况" xtype="textarea" colspan="3" length="255"
		layout="JBXX" />
	<item id="CYPB" type="int" alias="出院判别" display="0"/>
</entry>
