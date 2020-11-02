<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_XTCS" alias="系统参数" sort="a.JGID, a.CSMC">
	<item id="BRXZ" alias="费用性质" virtual="true" length="18" defaultValue = "1"  not-null="1">
		<dic id="phis.dictionary.patientProperties_MZ" autoLoad="true" searchField="PYDM" />
	</item>
	<item id="WORKPLACE" alias="工作单位" type="string" length="50" />
   <item id="KSDM" alias="挂号科室" type="string">
		<dic id="phis.dictionary.department_leaf_TJ" searchField="PYDM" autoLoad="true"/>
	</item>
</entry>
