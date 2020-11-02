<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_ZLFA02" tableName="GY_ZLFA" alias="诊疗方案明细">
	<item id="ZLXH" alias="诊疗序号" length="12" type="int" hidden="true" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="8869" />
		</key>
	</item>
	<item id="ZLMC" alias="诊疗方案名称" type="string" length="30" display="0" width="160"/>
	<item id="PYDM" alias="拼音代码" type="string" display="0" length="10"/>
	<item id="QYBZ" alias="启用" length="1" type="int" display="0"/>
	<item id="SSLB" alias="所属类别" length="2" type="int" hidden="true" display="0"/>
	<item id="YGDM" alias="员工代码" type="string" hidden="true" length="10" display="0"/>
	<item id="KSDM" alias="科室代码" type="int" hidden="true" length="10" display="0"/>
	<item id="JBXH" alias="常用诊断" length="18" type="long">
		<dic id="phis.dictionary.commonlyUsedDiagnosis" filter="['and',['and',['eq',['$','item.properties.SSLB'],['i','1']],['eq',['$','item.properties.YGDM'],['$','%user.userId']]],['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']]]" searchField="PYDM"></dic>
		<!--
		<dic id="phis.dictionary.commonlyUsedDiagnosis" filter="['eq',['$','item.properties.SSLB'],['i','1']]" searchField="PYDM"></dic>
		-->
	</item>
	<item id="CFZTBH" alias="处方组套" length="18" type="long">
		<dic id="phis.dictionary.personalCombo" filter="['and',['and',['and',['and',['ne',['$','item.properties.ZTLB'],['i','4']],['eq',['$','item.properties.SFQY'],['i','1']]],['eq',['$','item.properties.SSLB'],['i','1']]],['eq',['$','item.properties.YGDM'],['$','%user.userId']]],['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']]]" searchField="PYDM"></dic>
	</item>
	<item id="XMZTBH" alias="项目组套" length="18" type="long">
		<dic id="phis.dictionary.personalCombo" filter="['and',['and',['and',['and',['eq',['$','item.properties.ZTLB'],['i','4']],['eq',['$','item.properties.SFQY'],['i','1']]],['eq',['$','item.properties.SSLB'],['i','1']]],['eq',['$','item.properties.YGDM'],['$','%user.userId']]],['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']]]" searchField="PYDM"></dic>
	</item>
	<item id="BLMBBH" alias="病历模版" length="18" type="long">
		<dic id="phis.dictionary.medicalRecordTemplates" filter="['and',['and',['and',['eq',['$','item.properties.SSLB'],['i','1']],['eq',['$','item.properties.QYBZ'],['i','1']]],['eq',['$','item.properties.YGDM'],['$','%user.userId']]],['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']]]" searchField="PYDM"></dic>
	</item>
</entry>
