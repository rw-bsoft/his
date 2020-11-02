<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BRRY" alias="病人入院">
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
	<item id="BRID" alias="病人ID" type="long" length="18" not-null="1"/>
	<item id="ZYHM" alias="住院号" length="10" not-null="1" fixed="true" layout="JBXX"/>
	<item id="BRXM" alias="病人姓名" length="20" not-null="1" fixed="true" layout="JBXX"/>
	<item id="BRXB" alias="性别" type="int" length="4" not-null="1" fixed="true" layout="JBXX">
		<dic id="phis.dictionary.gender" autoLoad="true"/>
	</item>
	<item id="BRXZ" alias="性质" type="long" length="18" not-null="1" fixed="true" layout="JBXX">
		<dic id="phis.dictionary.patientProperties_ZY" autoLoad="true"/>
	</item>
	<item id="RYQK" alias="入院情况" type="int" length="4" fixed="true" layout="JBXX">
		<dic id="phis.dictionary.patientSituation" autoLoad="true"/>
	</item>
	<item id="BRKS" alias="科室" type="long" length="18" fixed="true" not-null="1" layout="JBXX">
		<dic id="phis.dictionary.department_zy" autoLoad="true"/>
	</item>
	<item id="BAHM" alias="病案号" length="10" fixed="true" layout="ZYXX"/>
	<item id="BRQK" alias="病人情况" type="int" length="4" layout="ZYXX">
		<dic id="phis.dictionary.patientSituation" autoLoad="true"/>
	</item>
	<item id="HLJB" alias="护理级别" type="string" length="4" layout="ZYXX">
		<dic id="phis.dictionary.careLevel" autoLoad="true"/>
	</item>
	<item id="YSDM" alias="饮食情况" type="int" length="4" layout="ZYXX">
		<dic id="phis.dictionary.diet" autoLoad="true"/>
	</item>
	<item id="BRXX" alias="病人血型" type="int" fixed="true" length="4" layout="ZYXX">
		<dic id="phis.dictionary.blood" autoLoad="true"/>
	</item>
	<item id="ZSYS" alias="主任医师" length="10" layout="BRYS">
		<dic id="phis.dictionary.doctor_cfqx" searchField="PYCODE" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true"/>
	</item>
	<item id="MZYS" alias="门诊医师" length="10" layout="BRYS">
		<dic id="phis.dictionary.doctor_cfqx" searchField="PYCODE" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true"/>
	</item>
	<item id="ZYYS" alias="住院医师" length="10" layout="BRYS">
		<dic id="phis.dictionary.doctor_cfqx" searchField="PYCODE" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true"/>
	</item>
	<item id="ZZYS" alias="主治医师" length="10" layout="BRYS">
		<dic id="phis.dictionary.doctor_cfqx" searchField="PYCODE" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true"/>
	</item>
	
</entry>
