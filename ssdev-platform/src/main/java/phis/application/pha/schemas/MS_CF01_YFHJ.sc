<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_CF01_YFHJ" tableName="MS_CF01" alias="药房划价_处方01表">
	<item id="CFSB" alias="处方识别" display="1" type="long" length="18"
		not-null="1" isGenerate="false" hidden="true" generator="assigned"
		pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="10" startPos="1000" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" display="0" type="string" length="20" />
	<item id="JZKH" alias="卡号" length="32" />
	<item id="BRXM" alias="病人姓名" type="string" length="40" fixed="true" />
	<item id="BRXZ" alias="病人性质" length="18" fixed="true">
		<dic id="phis.dictionary.patientProperties_MZ" autoLoad="true" />
	</item>
	<item id="CFHM" alias="处方号码" fixed="true" virtual="true" />
	<item id="BRXB" alias="病人性别" length="4" type="int" fixed="true">
		<dic id="phis.dictionary.gender" autoLoad="true" />
	</item>
	<item id="CFLX" alias="处方类型" type="int" length="1" not-null="1"
		defaultValue="1" fixed="true">
		<dic id="phis.dictionary.prescriptionType" autoLoad="true" />
	</item>
	<item id="YSDM" alias="开方医生" length="10" type="string">
		<dic id="phis.dictionary.doctor_FEE" searchField="PYCODE"
			autoLoad="true" />
	</item>
	<item id="KSDM" alias="就诊科室" type="long" length="18">
		<dic id="phis.dictionary.department_leaf" searchField="PYCODE"
			autoLoad="true"
			filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" />
	</item>
	<item id="CFTS" alias="草药帖数" defaultValue="1" type="int" minValue="1"
		maxValue="99" not-null="1" display="2" />
	<item id="JZXH" alias="就诊序号" display="0" type="long" length="18" />
	<item id="BRID" alias="病人ID" display="0" type="long" length="18" />
	<item id="DJLY" alias="单据来源" display="0" type="int" length="8" />
</entry>
