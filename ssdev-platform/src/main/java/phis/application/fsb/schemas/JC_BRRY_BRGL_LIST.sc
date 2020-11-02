<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_BRRY_RYDJ" tableName="JC_BRRY" alias="病人入院">
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="20" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0" defaultValue="%user.manageUnit.id"/>
	<item id="BRID" alias="病人ID" type="long" length="18" not-null="1" display="0"/>
	<item id="virtual1" alias="" virtual="true" xtype="panel" layout="part1" display="0"/>
	<item id="virtual2" alias="" virtual="true" xtype="panel" layout="part1" display="0"/>
	<item id="ZYHM" alias="家床号" length="10" layout="part2" queryable="true" updates="false" selected="true"/>
	<item id="JCBH" alias="家床编号" type="string" length="32" not-null="1"/>
	<item id="MZHM" alias="门诊号码" length="32" layout="part2" display="0" updates="false"/>
	<item id="BRXM" alias="姓名" type="string" length="40" fixed="true" layout="part3" updates="false" queryable="true"/>
	<item id="BRXB" alias="性别" type="int" fixed="true" width="40" layout="part3" queryable="true" updates="false">
		<dic id="phis.dictionary.gender"  autoLoad="true"/>
	</item>
	<item id="BRXZ" alias="性质" type="long" length="18" not-null="1" layout="part2" updates="false" queryable="true">
		<dic id="phis.dictionary.patientProperties_ZY" searchField="PYDM" autoLoad="true"/>
	</item>
	<item id="RYNL" alias="年龄" virtual="true" type="string" width="40" fixed="true" updates="false" layout="part3"/>
	<item id="SFZH" alias="身份证" type="string" width="180" fixed="true" layout="part3" updates="false" display="0"/>
	<item id="LXDZ" alias="地址" type="string" fixed="true" layout="part3" colspan="2" display="0" updates="false"/>
	<item id="LXRM" alias="联系人" type="string" length="40" fixed="true" layout="part3" display="0" updates="true"/>
	<item id="LXGX" alias="与患关系" type="int" fixed="true" layout="part3" display="0" updates="true">
		<dic id="phis.dictionary.GB_T4761"  autoLoad="true"/>
	</item>
	<item id="LXDH" alias="联系电话" type="string" fixed="true" layout="part3" display="0" updates="true"/>
	<item id="JCZD" alias="建床诊断" type="string" fixed="true" layout="part3" updates="auto"/>
	<item id="ICD10" alias="ICD码" type="string" fixed="true" layout="part3" display="0"/>
	<item id="ZDRQ" alias="诊断日期" type="date" fixed="true" layout="part3" display="0" updates="auto"/>
	<item id="BQZY" alias="病情摘要" type="string" colspan="3" xtype="textarea" length="2000" fixed="true" updates="auto" layout="part3" display="0"/>
	<item id="JCYJ" alias="收治指征和建床意见" type="string" colspan="3" xtype="textarea" length="2000" fixed="true" updates="auto" layout="part3" display="0"/>
	
	<item id="JCLX" alias="家床类型" type="int" layout="part4" defaultValue="01" updates="true">
		<dic> 
			<item key="1" text="治疗型"/>
			<item key="2" text="康复型"/>
			<item key="3" text="舒缓照顾型"/>
		</dic>
	</item>
	<item id="KSRQ" alias="开始日期" type="date" layout="part4" updates="true"/>
	<item id="JSRQ" alias="终止日期" type="date" layout="part4" updates="true"/>
	<item id="JCTS" alias="家床天数" type="int"/>
	<item id="ZRYS" alias="责任医生" type="string" defaultValue="%user.userId" layout="part4" updates="true">
		<dic id="phis.dictionary.doctor" autoLoad="true" searchField="PYCODE" filter="['and',['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.LOGOFF'],['s','1']]]"/>
	</item>
	<item id="ZRHS" alias="责任护士" type="string" length="10" defaultValue="%user.userId" layout="part4" updates="true">
		<dic id="phis.dictionary.doctor" autoLoad="true" searchField="PYCODE" filter="['and',['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.LOGOFF'],['s','1']]]"/>
	</item>
	<item id="RYRQ" alias="入院日期" type="date" display="0"/>
	<item id="CYPB" alias="状态" type="int" display="1" length="2" not-null="1" defaultValue="0" renderer="cypbRender">
		<dic id="phis.dictionary.famliySickbedStatus" autoLoad="true" />
	</item>
	<item id="CZGH" alias="操作工号" length="10" display="0" defaultValue="%user.userId"/>
	<item id="JSCS" alias="结算次数" type="int" display="0" length="3" not-null="1" defaultValue="0"/>
	<item id="LJTS" alias="临近天数" display="0"/>
	<item id="XCTS" alias="相差天数" display="0"/>
	<item id="SQFS" alias="申请方式" display="0"/>
</entry>
