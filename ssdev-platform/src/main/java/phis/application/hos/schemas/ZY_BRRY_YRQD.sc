<?xml version="1.0" encoding="UTF-8"?>

<entry  entityName="ZY_BRRY" alias="病人入院">
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0" defaultValue="%user.manageUnit.id"/>
	<item id="BRID" alias="病人ID" type="long" length="18" not-null="1" display="0"/>
	<item id="ZYHM" alias="住院号码" length="10" readOnly="true" layout="part1"/>
	<item id="BRCH" alias="床号" length="12" width="45" updates="false" layout="part7"/>
	<item id="BRXM" alias="姓名" length="20" not-null="1" layout="part3"/>
	<item id="BRXB" alias="性别" type="int" length="4" width="40" defaultValue="1" layout="part3">
		<dic id="phis.dictionary.gender" autoLoad="true"/>
	</item>
	<item id="BRKS" alias="科室" type="long" width="135" length="18" not-null="1" layout="part7">
		<dic id="phis.dictionary.department_zy" autoLoad="true" searchField="PYCODE" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="BRBQ" alias="病区" type="long" width="135" length="18">
		<dic id="phis.dictionary.department_bq" searchField="PYCODE" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="BRXZ" alias="性质" type="long" length="18" not-null="1" display="0">
		<dic id="phis.dictionary.patientProperties_ZY" autoLoad="true"/>
	</item>
	<item id="RYRQ" alias="入院日期" type="date" width="130" not-null="1" display="0"/>
	<item id="CYRQ" alias="出院日期" type="date" width="130" display="0"/>
</entry>
