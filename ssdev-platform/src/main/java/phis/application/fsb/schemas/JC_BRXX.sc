<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_BRXX" tableName="JC_BRRY" alias="病人入院">
	<item id="ZYH" alias="家床号" type="long" length="18" not-null="1"
		display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="20" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"
		display="0" defaultValue="%user.manageUnit.id" />
	<item id="BRID" alias="病人ID" type="long" length="18" not-null="1"
		display="0" />
	<item id="ZYHM" alias="家床号" length="10" fixed="true" />
	<item id="BRXM" alias="姓名" type="string" length="40" fixed="true"
		layout="part3" />
	<item id="BRXB" alias="性别" type="int" fixed="true" >
		<dic id="phis.dictionary.gender" autoLoad="true" />
	</item>
	<item id="RYNL" alias="年龄" type="string" width="40" fixed="true"
		/>

	<item id="BRXZ" alias="性质" type="long" length="18" not-null="1"
		updates="true" fixed="true">
		<dic id="phis.dictionary.patientProperties_ZY" searchField="PYDM"
			autoLoad="true" />
	</item>
	<item id="SFZH" alias="身份证" type="string" width="180" fixed="true" />
	<item id="LXDZ" alias="地址" type="string" fixed="true" 
		colspan="2" />

	<item id="LXRM" alias="联系人" type="string" length="40" fixed="true"
		/>
	<item id="LXGX" alias="与患关系" type="int" fixed="true" >
		<dic id="phis.dictionary.GB_T4761" autoLoad="true" />
	</item>
	<item id="LXDH" alias="联系电话" type="string" fixed="true" />
	<item id="DJRQ" alias="申请日期" type="date" fixed="true" />

	<item id="JCZD" alias="建床诊断" type="string" fixed="true" />
	<item id="ICD10" alias="ICD码" type="string" fixed="true" />
	<item id="ZDRQ" alias="诊断日期" type="date" fixed="true" />
	<item id="CZGH" alias="申请医生" type="string" defaultValue="%user.userId" fixed="true">
		<dic id="phis.dictionary.user" />
	</item>

	<item id="BQZY" alias="病情摘要" type="string" colspan="4" xtype="textarea"
		length="2000" fixed="true" />
	<item id="JCYJ" alias="收治指征和建床意见" type="string" colspan="4" xtype="textarea"
		length="2000" fixed="true" />

	<item id="JCLX" alias="家床类型" type="int" fixed="true" >
		<dic id="phis.dictionary.famliySickbedType" />
	</item>
	<item id="KSRQ" alias="开始日期" type="date" fixed="true" />
	<item id="JSRQ" alias="终止日期" type="date" fixed="true" />
	<item id="ZRYS" alias="责任医生" type="string" defaultValue="%user.userId" fixed="true">
		<dic id="phis.dictionary.user" render="Tree" onlySelectLeaf="true"
			keyNotUniquely="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="ZRHS" alias="责任护士" type="string" length="10"
		defaultValue="%user.userId" fixed="true">
		<dic id="phis.dictionary.user06"
			filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]" />
	</item>
	<item id="BRQK" alias="病人情况" type="int" length="4" renderer="brqkRender">
		<dic id="phis.dictionary.patientSituation" autoLoad="true"/>
	</item>
	<item id="CYPB" alias="病人状态" type="int" length="2" not-null="1" display="0"/>
	<item id="HLJB" alias="护理级别" type="int" length="4">
		<dic id="phis.dictionary.careLevel" autoLoad="true" />
	</item>
	<item id="YSDM" alias="饮食情况" type="int" length="4">
		<dic id="phis.dictionary.diet" autoLoad="true" />
	</item>
</entry>
