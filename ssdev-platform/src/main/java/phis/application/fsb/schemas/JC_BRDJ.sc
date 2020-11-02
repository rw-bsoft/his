<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_BRDJ" alias="家床登记">
	<item id="ID" alias="家床申请ID" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="20" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0" defaultValue="%user.manageUnit.id"/>
	<!--<item id="BRID" alias="病人ID" type="long" length="18" not-null="1" display="0"/>-->
	<item id="virtual1" alias="" virtual="true" xtype="panel" layout="part1"/>
	<item id="virtual2" alias="" virtual="true" xtype="panel" layout="part1"/>
	<item id="JCHM" alias="家床号码" length="10" readOnly="true" not-null="1" layout="part1"/>
	
	<item id="JCBH" alias="家床编号" length="10" not-null="1" layout="part2"/>
	<item id="MZHM" alias="门诊号码" length="32" layout="part2"/>
	<item id="BRXZ" alias="性质" type="long" length="18" not-null="1" updates="true" layout="part2">
		<dic id="phis.dictionary.patientProperties_ZY" searchField="PYDM" autoLoad="true"/>
	</item>

	<item id="BRXM" alias="姓名" type="string" length="40" fixed="true" layout="part3"/>
	<item id="BRXB" alias="性别" type="string" fixed="true" layout="part3">
		<dic id="phis.dictionary.gender"/>
	</item>
	<item id="BRNL" alias="年龄" type="string" width="40" fixed="true" layout="part3"/>
	<item id="SFZ" alias="身份证" type="string" width="180" fixed="true" layout="part3"/>
	<item id="DZ" alias="地址" type="string" fixed="true" layout="part3" colspan="2" />
	<item id="LXR" alias="联系人" type="string" length="40" fixed="true" layout="part3"/>
	<item id="YHGX" alias="与患关系" type="string" fixed="true" layout="part3">
		<dic id="phis.dictionary.GB_T4761" />
	</item>
	<item id="LXDH" alias="联系电话" type="string" fixed="true" layout="part3"/>
	<item id="JCZD" alias="建床诊断" type="string" fixed="true" layout="part3"/>
	<item id="ICD" alias="ICD码" type="string" fixed="true" layout="part3"/>
	<item id="ZDRQ" alias="诊断日期" type="date" fixed="true" layout="part3"/>
	<item id="BQZY" alias="病情摘要" type="string" colspan="3" xtype="textarea" length="2000" fixed="true" layout="part3"/>
	<item id="JCYJ" alias="收治指征和建床意见" type="string" colspan="3" xtype="textarea" length="2000" fixed="true" layout="part3"/>
	
	<item id="JCLX" alias="家床类型" type="string" layout="part4" defaultValue="01">
		<dic> 
			<item key="01" text="治疗型"/>
			<item key="02" text="护理型"/>
		</dic>
	</item>
	<item id="KSRQ" alias="开始日期" type="date" layout="part4"/>
	<item id="ZZRQ" alias="终止日期" type="date" layout="part4"/>
	<item id="ZRYS" alias="责任医生" type="string" defaultValue="%user.userId" layout="part4">
		<dic id="phis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="ZRHS" alias="责任护士" type="string" length="10" defaultValue="%user.userId" layout="part4">
		<dic id="phis.dictionary.user06" filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	
	<item id="JKJE" alias="缴款金额" type="double" minValue="0.01" maxValue="99999999.99" length="11" precision="2" update="true" layout="part5" />
	<item id="JKFS" alias="缴款方式" type="int" length="6" not-null="1" update="true" layout="part5" >
		<dic id="phis.dictionary.payment"  filter="['and',['eq',['$','item.properties.SYLX'],['s',2]],['eq',['$','item.properties.ZFBZ'],['s',0]],['eq',['$','item.properties.HBWC'],['s',0]]]" autoLoad="false"/>
	</item> 
	<item id="ZPHM" alias="票(卡)号码" fixed="true" length="20" layout="part5" />
	<item id="SJHM" alias="收据号码" length="20" readOnly="true" not-null="1" queryable="true" layout="part5" />
</entry>
