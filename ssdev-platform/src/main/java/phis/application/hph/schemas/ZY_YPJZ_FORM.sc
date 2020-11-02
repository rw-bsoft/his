<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_YPJZ_FORM" tableName="ZY_BRRY" alias="住院记账form">
	<item id="ZYH" alias="住院号" length="12" not-null="1" type="long" display="0"/>
	<item id="BRCH" alias="床号" length="12" not-null="1"/>
	<item id="ZYHM" alias="住院号" length="10"  not-null="1"/>
	<item id="BRXM" alias="姓名" length="20"  fixed="true" not-null="1"/>
	<item id="BRKS" alias="科室" type="long" length="18" not-null="1" fixed="true">
		<dic id="phis.dictionary.department" autoLoad="true"/>
	</item>
	<item id="BRXB" alias="性别" type="int" length="4" not-null="1" fixed="true">
		<dic id="phis.dictionary.gender"/>
	</item>
	<item id="BRXZ" alias="性质" type="long" length="18" not-null="1" fixed="true">
		<dic id="phis.dictionary.patientProperties_ZY" />
	</item>
	<item id="YSDM" alias="开嘱医生" length="10" type="string" not-null="1"  nextFocus="fire_YSEntry">
		<dic id="phis.dictionary.doctor_FEE" searchField="PYCODE" autoLoad="true" />
	</item>
	<item id="CFTS" alias="帖数" defaultValue="1" type="int" minValue="1" maxValue="99" not-null="1" nextFocus="fire_TSEntry"/>
	<item id="BRBQ" alias="病人病区" type="long" length="18" display="0"/>
</entry>
