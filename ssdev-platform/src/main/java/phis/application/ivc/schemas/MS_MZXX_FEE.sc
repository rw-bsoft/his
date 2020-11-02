<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_MZXX" alias="门诊收费信息">
	<item id="MZXH" alias="门诊序号" length="18" not-null="1" generator="assigned" pkey="true" display="0"/>
	<item id="MZGL" alias="卡号" length="32" />
	<item id="BRXM" alias="病人姓名" type="string" length="40" fixed="true" />
	<item id="BRXZ" alias="病人性质" length="18" fixed="true">
		<dic id="phis.dictionary.patientProperties_MZ" autoLoad="true"/>
	</item>
	<item id="FPHM" alias="发票号码" type="string" fixed="true" virtual="true"/>
	<item id="BRXB" alias="病人性别" length="4" type="int" display="0" fixed="true">
		<dic id="phis.dictionary.gender" autoLoad="true"/>
	</item>
	<item id="AGE" alias="年龄" type="string" display="0" fixed="true"/>
	<item id="YSDM" alias="医生" type="string">
		<dic id="phis.dictionary.doctor_FEE" searchField="PYCODE" autoLoad="true" />
	</item>
	<item id="KSDM" alias="科室" type="string">
		<dic id="phis.dictionary.department_leaf" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="ZDXH" alias="诊断序号" type="long" display="0" length="18"/>
	<item id="JBMC" alias="诊断" type="string" length="40" virtual="true" mode="remote"  />
	<item id="ICD10" alias="ICD10" type="long" display="0" length="20"/>
	<item id="YBMC" alias="医保病种" type="string" defaultValue="20">
		<dic id="phis.dictionary.ybJbbm" searchField="PYDM" autoLoad="true" />
	</item>
	<item id="CFTS" alias="草药帖数" defaultValue="1" type="int" minValue="1" maxValue="99" not-null="1" />
</entry>
