<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YJ_MZFORM">
	<item id="JZKH" alias="卡号" type="long" update="false"/>
	<item id="MZHM" alias="门诊号码" type="long" display="0"/>
	<item id="BRXM" alias="病人姓名" type="string" fixed="true"/>
	<item id="BRXZ" alias="病人性质" type="string" fixed="true">
		<dic id="phis.dictionary.patientProperties_GY" autoLoad="true"/>
	</item>
	<item id="ZXYS" alias="检查医生" type="string">
		<dic id="phis.dictionary.user_YJJCYS" autoLoad="true"  />
	</item>
	<item  id="YSDM" alias="申检医生" type="string">
		<dic id="phis.dictionary.user_YJSJYS" autoLoad="true" />
	</item>
	<item id="KSDM" alias="申检科室" type="string">
		<dic id="phis.dictionary.department_leaf" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" defaultValue = "%user.manageUnit.id" searchField="PYCODE"/>
	</item>
	<item id="ZXRQ" alias="检查日期" type="date" defaultValue = "%server.date.date"/>
</entry>
