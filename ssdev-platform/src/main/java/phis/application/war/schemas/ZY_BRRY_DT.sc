<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BRRY" alias="病人入院">
	<item id="ZYH" alias="住院号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="JGID" alias="机构ID" type="string" display="0" length="20" not-null="1"/>
	<item id="BRID" alias="病人ID" type="long" display="0" length="18" not-null="1"/>
	<item id="ZYHM" alias="住院号码" length="10"  not-null="1" />
	<item id="BRXM" alias="病人姓名" length="20" not-null="1" fixed="true"/>
	<item id="BRXB" alias="病人性别" type="string" length="4" not-null="1" fixed="true">
		<dic id="phis.dictionary.gender" autoLoad="true"/>
	</item>
	<item id="AGE" alias="病人年龄" type="string" virtual="true" fixed="true"/>
	<item id="BRXZ" alias="病人性质" type="long" length="18" not-null="1" fixed="true">
		<dic id="phis.dictionary.patientProperties_ZY" autoLoad="true"/>
	</item>
	<item id="BRKS" alias="病人科室" type="long" length="18" not-null="1" fixed="true">
		<dic id="phis.dictionary.department_zy" autoLoad="true"/>
	</item>
	<item id="RYRQ" alias="入院日期" not-null="1" fixed="true"/>
	<item id="BRQK" alias="病人情况" length="40" not-null="1" fixed="true">
		<dic id="phis.dictionary.patientSituation" autoLoad="true"/>
	</item>
	<item id="HLJB" alias="护理级别" type="string" length="4" fixed="true">
		<dic id="phis.dictionary.careLevel" autoLoad="true"/>
	</item>
	<item id="YSDM" alias="饮食情况" type="int" length="4" fixed="true">
		<dic id="phis.dictionary.diet" autoLoad="true"/>
	</item>
	<item id="GMYW" alias="过敏药物" length="200"  xtype="textarea" fixed="true"/>
	<item id="ZSYS" alias="主任医师" length="10" fixed="true">
		<dic id="phis.dictionary.doctor" autoLoad="true"/>
	</item>
	<item id="CYPB" alias="病人状态" type="string" length="2" not-null="1" fixed="true">
		<dic id="phis.dictionary.patientStatus" autoLoad="true"/>
	</item>
	<item id="JBMC" alias="入院诊断" length="200" xtype="textarea" fixed="true"/>
</entry>
