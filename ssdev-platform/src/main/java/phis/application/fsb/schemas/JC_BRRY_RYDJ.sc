<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_BRRY_RYDJ" tableName="JC_BRRY" alias="病人入院">
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="20" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0" defaultValue="%user.manageUnit.id"/>
	<item id="BRID" alias="病人ID" type="long" length="18" not-null="1" display="0"/>
	<item id="virtual1" alias="" virtual="true" xtype="panel" layout="part1"/>
	<item id="virtual2" alias="" virtual="true" xtype="panel" layout="part1"/>
	<item id="ZYHM" alias="家床号" type="string" length="10"  readOnly="true" not-null="1" layout="part1"/>
	<item id="JCBH" alias="家床编号" type="string" length="32" not-null="1" layout="part2"/>
	<item id="MZHM" alias="门诊号码" type="string" length="32" layout="part2"/>
	<item id="BRXZ" alias="性质" type="long" length="18" not-null="1" updates="true" layout="part2">
		<dic id="phis.dictionary.patientProperties_ZY" searchField="PYDM" autoLoad="true"/>
	</item>

	<item id="BRXM" alias="姓名" type="string" length="40" fixed="true" not-null="1" layout="part3"/>
	<item id="BRXB" alias="性别" type="int" fixed="true" layout="part3">
		<dic id="phis.dictionary.gender"  autoLoad="true"/>
	</item>
	<item id="RYNL" alias="年龄"  type="string" width="40" fixed="true" layout="part3"/>
	<item id="SFZH" alias="身份证" type="string" width="180" fixed="true" layout="part3"/>
	<item id="LXDZ" alias="地址" type="string" fixed="true" layout="part3" colspan="2" />
	<item id="LXRM" alias="联系人" type="string" length="40" fixed="true" layout="part3"/>
	<item id="LXGX" alias="与患关系" type="int" fixed="true" layout="part3">
		<dic id="phis.dictionary.GB_T4761"  autoLoad="true"/>
	</item>
	<item id="LXDH" alias="联系电话" type="string" fixed="true" layout="part3"/>
	<item id="JCZD" alias="建床诊断" type="string" fixed="true" layout="part3"/>
	<item id="ICD10" alias="ICD码" type="string" fixed="true" layout="part3"/>
	<item id="ZDRQ" alias="诊断日期" type="date" fixed="true" layout="part3"/>
	<item id="BQZY" alias="病情摘要" type="string" colspan="3" xtype="textarea" length="2000" fixed="true" layout="part3"/>
	<item id="JCYJ" alias="收治指征和建床意见" type="string" colspan="3" xtype="textarea" length="2000" fixed="true" layout="part3"/>
	
	<item id="JCLX" alias="家床类型" type="int" layout="part4" not-null="1" defaultValue="1">
		<dic> 
			<item key="1" text="治疗型" />
			<item key="2" text="康复型"/>
			<item key="3" text="舒缓照顾型"/>
		</dic>
	</item>
	<item id="KSRQ" alias="开始日期" type="date" defaultValue="%server.date.date" not-null="1" layout="part4"/>
	<item id="JSRQ" alias="结束日期" type="date" not-null="1" layout="part4"/>
	<item id="ZRYS" alias="责任医生" type="string" defaultValue="%user.userId" not-null="1" layout="part4">
		<dic id="phis.dictionary.doctor" autoLoad="true" searchField="PYCODE" filter="['and',['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.LOGOFF'],['s','1']]]"/>
	</item>
	<item id="ZRHS" alias="责任护士" type="string" length="10" defaultValue="%user.userId" layout="part4">
		<dic id="phis.dictionary.doctor" autoLoad="true" searchField="PYCODE" filter="['and',['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.LOGOFF'],['s','1']]]"/>
	</item>
	<!--
	<item id="RYZD" alias="入院诊断" type="string" not-null="1" layout="part7" virtual="true" mode="remote"/>
	-->
	<item ref="b.JKJE" type="double" emptyText="" length="10" layout="part5"/>
	<item ref="b.JKFS" type="int" length="6" not-null="0" showRed="false" layout="part5"/>
	<item ref="b.ZPHM" length="20" not-null="0" showRed="false" layout="part5"/>
	<item ref="b.SJHM" length="20" not-null="0" showRed="false" layout="part5"/>
	
	
	
	<item id="DJRQ" alias="登记日期" type="timestamp" display="0"/>
	<item id="CYPB" alias="出院判别" type="int" display="0" length="2" not-null="1" defaultValue="0"/>
	<item id="CZGH" alias="操作工号" length="10" display="0" defaultValue="%user.userId"/>
	<item id="JSCS" alias="结算次数" type="int" display="0" length="3" not-null="1" defaultValue="0"/>
	<item id="CSNY" alias="出身年月" type="date" display="0"/>
<!--	<item id="SRKH" alias="输入的卡号(杭州市医保用)"   length="40"  display="0"/>
	<item id="ZYLSH" alias="住院流水号(杭州市医保用)"   length="18"  display="0"/>
	<item id="YYLSH" alias="住院交易流水号(杭州市医保用)"  type="string"  length="20"  display="0"/>
	<item id="GRBH" alias="个人编号(杭州市医保用)" length="20" display="0"/>
	
	<item id="BARGAININGID" alias="省医保交易流水号" type="long" length="18" display="0"/>
	<item id="ICMW" alias="省医保IC卡明文"  type="string"  length="500"  display="0"/>
	-->
	<relations>
		<relation type="child" entryName="phis.application.fsb.schemas.JC_TBKK_JKFORM" >
			<join parent="ZYH" child="ZYH"></join>
		</relation>
	</relations>
</entry>
