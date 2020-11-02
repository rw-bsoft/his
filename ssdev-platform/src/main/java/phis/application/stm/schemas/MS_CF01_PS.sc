<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_CF01_PS" tableName="MS_CF01" alias="门诊处方01表">
	<item id="MZHM" alias="门诊号码" fixed="true" type="string" length="10" />
	<item id="BRXM" alias="姓名" fixed="true" type="string" length="10" />
	<item id="BRXB" alias="病人性别" length="4" fixed="true" type="string">
		<dic id="phis.dictionary.gender" />
	</item>
	<item id="NL" virtual="true" alias="年龄" fixed="true" type="string" />
	<item id="BRXZ" alias="病人性质" length="18" fixed="true" type="string">
		<dic id="phis.dictionary.patientProperties" />
	</item>
	<item id="KFRQ" alias="开方时间" type="string" width="140" not-null="1" fixed="true" />
	<item ref="b.HKDZ" alias="地址" fixed="true" />
	<item id="CFHM" alias="处方号码" fixed="true" type="string" />
	<item id="KSDM" alias="就诊科室" type="long" length="18" fixed="true">
		<dic id="phis.dictionary.department_leaf" searchField="PYCODE"
			autoLoad="true"
			filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" />
	</item>
	<item id="YSDM" alias="开方医生" length="10" fixed="true" type="string">
		<dic id="phis.dictionary.doctor_cfqx_kjg" searchField="PYCODE"
			autoLoad="true" />
	</item>
	<item id="BRID" alias="病人ID" display="0" type="long" length="18" />
</entry>
