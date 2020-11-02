<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BRRY" alias="病人入院">
	<item id="ZYH" alias="住院号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="ZYHM" alias="住院号码" length="10"  not-null="1" fixed="true"/>
	<item id="BRXM" alias="姓名" length="20" not-null="1" fixed="true"/>
	<item id="BRCH" alias="床号" length="12" fixed="true"/>
	<item id="BRXB" alias="性别" type="int" length="4" not-null="1" fixed="true">
		<dic id="phis.dictionary.gender" autoLoad="true"/>
	</item>
	<item id="GZDW" alias="工作单位" colspan="2" length="40" fixed="true"/>
	<item id="RYQK" alias="入院情况" type="int" length="4" fixed="true">
		<dic id="phis.dictionary.patientSituation" autoLoad="true"/>
	</item>
	<item id="BRKS" alias="科室" type="long" length="18" not-null="1" fixed="true">
		<dic id="phis.dictionary.department" autoLoad="true"/>
	</item>
	<item id="RYRQ" alias="入院日期"  not-null="1" fixed="true"/>
	<item id="CYRQ" alias="出院日期" fixed="true"/>
	<item id="DAYS" alias="天数" type="int" virtual="true" fixed="true"/>
	<item id="CYFS" alias="出院方式" type="int" length="1" fixed="true">
		<dic id="phis.dictionary.outcomeSituation" autoLoad="true"/>
	</item>
	<item id="BZXX" alias="建议" xtype="textarea" colspan="4" virtual="true" fixed="true"/>
	<item id="CYPB" alias="出院判别" type="int" display="0"/>
</entry>
