<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BRRY" alias="病人入院">
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0"/>
	<item id="ZYH" alias="住院号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="ZYHM" alias="住院号码" length="10"  not-null="1" fixed="true" layout="JBXX"/>
	<item id="BRXM" alias="姓名" length="20" not-null="1" fixed="true" layout="JBXX"/>
	<item id="BRCH" alias="床号" length="12" fixed="true" layout="JBXX"/>
	<item id="BRXB" alias="性别" type="int" length="4" not-null="1" fixed="true" layout="JBXX">
		<dic id="phis.dictionary.gender" autoLoad="true"/>
	</item>
	<item id="GZDW" alias="工作单位" colspan="2" length="40"  fixed="true" layout="JBXX"/>
	<item id="RYQK" alias="入院情况" type="int" length="4"  fixed="true" layout="JBXX">
		<dic id="phis.dictionary.patientSituation" autoLoad="true"/>
	</item>
	<item id="BRKS" alias="科室" type="long" length="18" not-null="1" fixed="true" layout="JBXX">
		<dic id="phis.dictionary.department" autoLoad="true"/>
	</item>
	<item id="RYRQ" alias="入院时间" not-null="1" colspan="2" fixed="true" layout="JBXX"/>
	<item id="CYRQ" alias="原出院时间" colspan="2" not-null="1" fixed="true" layout="JBXX"/>
	<item id="BRXZ" alias="性质" type="long" length="18" updates="false" fixed="true" layout="JBXX">
		<dic id="phis.dictionary.patientProperties_ZY" searchField="PYDM" autoLoad="true"/>
	</item>
	<item id="XCYRQ" alias="出院时间" type="datetime" not-null="1" colspan="2" layout="CYXX"/>
	<item id="DAYS" alias="天数" type="int" virtual="true" fixed="true" layout="CYXX"/>
	<item id="CYFS" alias="出院方式" type="int" length="1" not-null="1" layout="CYXX">
		<dic id="phis.dictionary.outcomeSituation" autoLoad="true"/>
	</item>
	<item id="BZXX" alias="建议" xtype="textarea" colspan="4" layout="CYXX" length="127"/>
	<item id="YBZY" alias="医保转院" colspan="2" type="string" updates="true" layout="CYXX" >
		<dic id="phis.dictionary.ybzy" autoLoad="true" />
	</item>
</entry>