<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_BRSQ" alias="家床申请">
	<item id="ID" alias="家床申请ID" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1"/>
		</key>
	</item>
	<item id="MZHM" alias="门诊号码" type="string" length="20" colspan="2"/>
	<item id="ZYHM" alias="住院号码" type="string" length="20" colspan="2"/>
	<item id="BRXM" alias="姓名" fixed="true" not-null="1" type="string" length="20" colspan="2"/>
	<item id="BRXB" alias="性别" type="long" colspan="2" fixed="true">
		<dic id="phis.dictionary.gender" autoLoad="true"/>
	</item>
	
	<item id="BRXZ" alias="病人性质" type="string" colspan="2" fixed="true">
		<dic id="phis.dictionary.patientProperties"  autoLoad="true"/>
	</item>
	<item id="SFZH" alias="身份证" type="string" fixed="true" colspan="2"/>
	<item id="LXDZ" alias="地址" type="string" colspan="3" fixed="true"/>
	<item id="BRNL" alias="年龄" type="string" length="3" fixed="true" colspan="1"/>
	<item id="LXR" alias="联系人" type="string" length="40" colspan="2"/>
	<item id="YHGX" alias="与患关系" type="string" colspan="2">
		<dic id="phis.dictionary.GB_T4761" />
	</item>
	<item id="LXDH" alias="联系电话" type="string" colspan="2" width="100"/>
	<item id="SQRQ" alias="申请日期" minValue="%server.date.date" not-null="1" defaultValue="%server.date.date" type="date" colspan="2"/>
	<item id="JCZD" alias="建床诊断" type="string" not-null="1" showRed="true" colspan="2" mode="remote"/>
	<item id="ICD10" alias="ICD码" type="string" not-null="1" fixed="true" colspan="2"/>

	<item id="ZDRQ" alias="诊断日期" not-null="1" defaultValue="%server.date.date" type="date" colspan="2"/>
	<item id="SQYS" alias="申请医生" type="string" display="2" not-null="1" length="20" defaultValue="%user.userId" colspan="2">
		<dic id="phis.dictionary.doctor" autoLoad="true" searchField="PYCODE" filter="['and',['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.LOGOFF'],['s','1']]]"/>
	</item>
	<item id="BQZY" alias="病情摘要" type="string" display="2" not-null="1" colspan="8" xtype="textarea" length="250"/>
	<item id="JCYJ" alias="收治指证和建床意见" type="string" display="2" not-null="1" colspan="8" xtype="textarea" length="250"/>
	
	<item id="SQZT" alias="状态" type="string" defaultValue="1">
		<dic id="phis.dictionary.fsb_sqzt"/>
	</item>
	<item id="JGID" alias="机构ID" length="20" defaultValue="%user.manageUnit.id" type="string"/>
	<item id="BRID" alias="病人ID" type="long"/>
	<item id="SQFS" alias="申请方式(住院、门诊)" type="long" defaultValue="3"/>
	<item id="CSNY" alias="出生年月" type="date"/>
</entry>
