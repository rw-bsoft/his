<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_BRSQ" alias="家床申请" sort="a.SQRQ DESC">
	<item id="ID" alias="家床申请ID" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0"/>
	<item id="MZHM" alias="门诊号码" type="string" length="32" queryable="true" width="80" selected="true"/>
	<item id="ZYHM" alias="住院号码" type="string" length="32" queryable="true" width="120" selected="true"/>
	<item id="BRXM" alias="姓名" type="string" length="40" queryable="true"/>
	<item id="BRXB" alias="性别" type="string" width="40">
		<dic id="phis.dictionary.gender"/>
	</item>
	<item id="CSNY" alias="出生年月" type="date"/>
	<item id="BRXZ" alias="病人性质" type="string">
		<dic id="phis.dictionary.patientProperties"/>
	</item>
	<item id="SFZH" alias="身份证" type="string" width="150" selected="true"/>
	<item id="LXDZ" alias="地址" type="string" width="150" selected="true"/>
	<item id="LXR" alias="联系人" type="string" length="40" selected="true"/>
	<item id="YHGX" alias="与患关系" type="string"> 
		<dic id="phis.dictionary.GB_T4761" />
	</item>
	<item id="LXDH" alias="联系电话" type="string"/>
	<item id="SQRQ" alias="申请日期" type="date"/>
	<item id="SQZT" alias="状态" type="string" display="1" defaultValue="2" queryable="true">
		<dic id="phis.dictionary.fsb_sqzt"/>
	</item>
	
	<item id="BRID" alias="病人ID" type="long" display="0"/>
	<item id="BRNL" alias="年龄" type="string" length="3" fixed="true" colspan="1" display="0"/>
	<item id="JCZD" alias="建床诊断" type="string" colspan="2" mode="remote" display="0"/>
	<item id="ICD10" alias="ICD码" type="string" colspan="2" display="0"/>
	<item id="ZDRQ" alias="诊断日期" defaultValue="%server.date.date" type="date" colspan="2" display="0"/>
	<item id="SQYS" alias="申请医生" type="string" length="20" defaultValue="%user.userId" colspan="2" display="0">
		<dic id="phis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.properties.manaUnitId" rootVisible="true" colspan="2"/>
	</item>
	<item id="BQZY" alias="病情摘要" type="string" colspan="8" xtype="textarea" length="2000" display="0"/>
	<item id="JCYJ" alias="收治指证和建床意见" type="string" colspan="8" xtype="textarea" length="2000" display="0"/>
	<item id="JGID" alias="机构ID" length="20" defaultValue="%user.manageUnit.id" type="string" display="0"/>
	<item id="SQFS" alias="申请方式(住院、门诊)" type="long" defaultValue="3" display="0"/>
</entry>
